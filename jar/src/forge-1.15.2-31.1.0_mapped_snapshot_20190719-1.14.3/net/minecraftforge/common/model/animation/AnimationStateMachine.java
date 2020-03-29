package net.minecraftforge.common.model.animation;

import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.animation.Event;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.animation.TimeValues;
import net.minecraftforge.common.util.JsonUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AnimationStateMachine implements IAnimationStateMachine {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ImmutableMap<String, ITimeValue> parameters;
   private final ImmutableMap<String, IClip> clips;
   private final ImmutableList<String> states;
   private final ImmutableMultimap<String, String> transitions;
   @SerializedName("start_state")
   private final String startState;
   private transient boolean shouldHandleSpecialEvents;
   private transient String currentStateName;
   private transient IClip currentState;
   private transient float lastPollTime;
   private static final LoadingCache<Triple<? extends IClip, Float, Float>, Pair<IModelTransform, Iterable<Event>>> clipCache;
   private static final AnimationStateMachine missing;
   private static final Gson asmGson;

   public AnimationStateMachine(ImmutableMap<String, ITimeValue> parameters, ImmutableMap<String, IClip> clips, ImmutableList<String> states, ImmutableMultimap<String, String> transitions, String startState) {
      this.parameters = parameters;
      this.clips = clips;
      this.states = states;
      this.transitions = transitions;
      this.startState = startState;
   }

   void initialize() {
      if (this.parameters == null) {
         throw new JsonParseException("Animation State Machine should contain \"parameters\" key.");
      } else if (this.clips == null) {
         throw new JsonParseException("Animation State Machine should contain \"clips\" key.");
      } else if (this.states == null) {
         throw new JsonParseException("Animation State Machine should contain \"states\" key.");
      } else if (this.transitions == null) {
         throw new JsonParseException("Animation State Machine should contain \"transitions\" key.");
      } else {
         this.shouldHandleSpecialEvents = true;
         this.lastPollTime = Float.NEGATIVE_INFINITY;
         IClip state = (IClip)this.clips.get(this.startState);
         if (this.clips.containsKey(this.startState) && this.states.contains(this.startState)) {
            this.currentStateName = this.startState;
            this.currentState = state;
         } else {
            throw new IllegalStateException("unknown state: " + this.startState);
         }
      }
   }

   public Pair<IModelTransform, Iterable<Event>> apply(float time) {
      if (this.lastPollTime == Float.NEGATIVE_INFINITY) {
         this.lastPollTime = time;
      }

      Pair<IModelTransform, Iterable<Event>> pair = (Pair)clipCache.getUnchecked(Triple.of(this.currentState, this.lastPollTime, time));
      this.lastPollTime = time;
      boolean shouldFilter = false;
      if (this.shouldHandleSpecialEvents) {
         UnmodifiableIterator var4 = ImmutableList.copyOf((Iterable)pair.getRight()).reverse().iterator();

         while(var4.hasNext()) {
            Event event = (Event)var4.next();
            if (event.event().startsWith("!")) {
               shouldFilter = true;
               if (event.event().startsWith("!transition:")) {
                  String newState = event.event().substring("!transition:".length());
                  this.transition(newState);
               } else {
                  LOGGER.error("Unknown special event \"{}\", ignoring.", event.event());
               }
            }
         }
      }

      return !shouldFilter ? pair : Pair.of(pair.getLeft(), Iterables.filter((Iterable)pair.getRight(), new Predicate<Event>() {
         public boolean apply(Event event) {
            return !event.event().startsWith("!");
         }
      }));
   }

   public void transition(String newState) {
      IClip nc = (IClip)this.clips.get(newState);
      if (this.clips.containsKey(newState) && this.states.contains(newState)) {
         if (!this.transitions.containsEntry(this.currentStateName, newState)) {
            throw new IllegalArgumentException("no transition from current clip \"" + this.currentStateName + "\" to the clip \"" + newState + "\" found.");
         } else {
            this.currentStateName = newState;
            this.currentState = nc;
         }
      } else {
         throw new IllegalStateException("unknown state: " + newState);
      }
   }

   public String currentState() {
      return this.currentStateName;
   }

   public void shouldHandleSpecialEvents(boolean value) {
      this.shouldHandleSpecialEvents = true;
   }

   @OnlyIn(Dist.CLIENT)
   public static IAnimationStateMachine load(IResourceManager manager, ResourceLocation location, ImmutableMap<String, ITimeValue> customParameters) {
      AnimationStateMachine var4;
      try {
         IResource resource = manager.getResource(location);
         Throwable var30 = null;

         try {
            AnimationStateMachine.ClipResolver clipResolver = new AnimationStateMachine.ClipResolver();
            AnimationStateMachine.ParameterResolver parameterResolver = new AnimationStateMachine.ParameterResolver(customParameters);
            Clips.CommonClipTypeAdapterFactory.INSTANCE.setClipResolver(clipResolver);
            TimeValues.CommonTimeValueTypeAdapterFactory.INSTANCE.setValueResolver(parameterResolver);
            AnimationStateMachine asm = (AnimationStateMachine)asmGson.fromJson(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8), AnimationStateMachine.class);
            clipResolver.asm = asm;
            parameterResolver.asm = asm;
            asm.initialize();
            AnimationStateMachine var8 = asm;
            return var8;
         } catch (Throwable var26) {
            var30 = var26;
            throw var26;
         } finally {
            if (resource != null) {
               if (var30 != null) {
                  try {
                     resource.close();
                  } catch (Throwable var25) {
                     var30.addSuppressed(var25);
                  }
               } else {
                  resource.close();
               }
            }

         }
      } catch (JsonParseException | IOException var28) {
         LOGGER.error("Exception loading Animation State Machine {}, skipping", location, var28);
         var4 = missing;
      } finally {
         Clips.CommonClipTypeAdapterFactory.INSTANCE.setClipResolver((Function)null);
         TimeValues.CommonTimeValueTypeAdapterFactory.INSTANCE.setValueResolver((Function)null);
      }

      return var4;
   }

   public static AnimationStateMachine getMissing() {
      return missing;
   }

   static {
      clipCache = CacheBuilder.newBuilder().maximumSize(100L).expireAfterWrite(100L, TimeUnit.MILLISECONDS).build(new CacheLoader<Triple<? extends IClip, Float, Float>, Pair<IModelTransform, Iterable<Event>>>() {
         public Pair<IModelTransform, Iterable<Event>> load(Triple<? extends IClip, Float, Float> key) throws Exception {
            return Clips.apply((IClip)key.getLeft(), (Float)key.getMiddle(), (Float)key.getRight());
         }
      });
      missing = new AnimationStateMachine(ImmutableMap.of(), ImmutableMap.of("missingno", Clips.IdentityClip.INSTANCE), ImmutableList.of("missingno"), ImmutableMultimap.of(), "missingno");
      missing.initialize();
      asmGson = (new GsonBuilder()).registerTypeAdapter(ImmutableList.class, JsonUtils.ImmutableListTypeAdapter.INSTANCE).registerTypeAdapter(ImmutableMap.class, JsonUtils.ImmutableMapTypeAdapter.INSTANCE).registerTypeAdapterFactory(Clips.CommonClipTypeAdapterFactory.INSTANCE).registerTypeAdapterFactory(TimeValues.CommonTimeValueTypeAdapterFactory.INSTANCE).registerTypeAdapterFactory(AnimationStateMachine.TransitionsAdapterFactory.INSTANCE).setPrettyPrinting().enableComplexMapKeySerialization().disableHtmlEscaping().create();
   }

   private static enum TransitionsAdapterFactory implements TypeAdapterFactory {
      INSTANCE;

      @Nullable
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
         if (type.getRawType() == ImmutableMultimap.class && type.getType() instanceof ParameterizedType) {
            Type[] typeArguments = ((ParameterizedType)type.getType()).getActualTypeArguments();
            if (typeArguments.length == 2 && typeArguments[0] == String.class && typeArguments[1] == String.class) {
               final TypeAdapter<Map<String, Collection<String>>> mapAdapter = gson.getAdapter(new TypeToken<Map<String, Collection<String>>>() {
               });
               final TypeAdapter<Collection<String>> collectionAdapter = gson.getAdapter(new TypeToken<Collection<String>>() {
               });
               return new TypeAdapter<ImmutableMultimap<String, String>>() {
                  public void write(JsonWriter out, ImmutableMultimap<String, String> value) throws IOException {
                     mapAdapter.write(out, value.asMap());
                  }

                  public ImmutableMultimap<String, String> read(JsonReader in) throws IOException {
                     Builder<String, String> builder = ImmutableMultimap.builder();
                     in.beginObject();

                     while(in.hasNext()) {
                        String key = in.nextName();
                        switch(in.peek()) {
                        case STRING:
                           builder.put(key, in.nextString());
                           break;
                        case BEGIN_ARRAY:
                           builder.putAll(key, (Iterable)collectionAdapter.read(in));
                           break;
                        default:
                           throw new JsonParseException("Expected String or Array, got " + in.peek());
                        }
                     }

                     in.endObject();
                     return builder.build();
                  }
               };
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   private static final class ParameterResolver implements Function<String, ITimeValue> {
      private final ImmutableMap<String, ITimeValue> customParameters;
      private AnimationStateMachine asm;

      public ParameterResolver(ImmutableMap<String, ITimeValue> customParameters) {
         this.customParameters = customParameters;
      }

      public ITimeValue apply(String name) {
         return this.asm.parameters.containsKey(name) ? (ITimeValue)this.asm.parameters.get(name) : (ITimeValue)this.customParameters.get(name);
      }
   }

   private static final class ClipResolver implements Function<String, IClip> {
      private AnimationStateMachine asm;

      private ClipResolver() {
      }

      public IClip apply(String name) {
         return (IClip)this.asm.clips.get(name);
      }

      // $FF: synthetic method
      ClipResolver(Object x0) {
         this();
      }
   }
}
