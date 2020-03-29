package net.minecraftforge.common.model.animation;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.animation.Event;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.model.TransformationHelper;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Clips {
   private static final Logger LOGGER = LogManager.getLogger();

   @OnlyIn(Dist.CLIENT)
   public static IClip getModelClipNode(ResourceLocation modelLocation, String clipName) {
      IUnbakedModel model = (IUnbakedModel)ModelLoader.defaultModelGetter().apply(modelLocation);
      Optional<? extends IClip> clip = model.getClip(clipName);
      if (clip.isPresent()) {
         return new Clips.ModelClip((IClip)clip.get(), modelLocation, clipName);
      } else {
         LOGGER.error("Unable to find clip {} in the model {}", clipName, modelLocation);
         return new Clips.ModelClip(Clips.IdentityClip.INSTANCE, modelLocation, clipName);
      }
   }

   private static IJointClip blendClips(IJoint joint, final IJointClip fromClip, final IJointClip toClip, final ITimeValue input, final ITimeValue progress) {
      return new IJointClip() {
         public TransformationMatrix apply(float time) {
            float clipTime = input.apply(time);
            return TransformationHelper.slerp(fromClip.apply(clipTime), toClip.apply(clipTime), MathHelper.clamp(progress.apply(time), 0.0F, 1.0F));
         }
      };
   }

   public static Pair<IModelTransform, Iterable<Event>> apply(final IClip clip, float lastPollTime, final float time) {
      return Pair.of(new IModelTransform() {
         public TransformationMatrix func_225615_b_() {
            return TransformationMatrix.func_227983_a_();
         }

         public TransformationMatrix getPartTransformation(Object part) {
            if (!(part instanceof IJoint)) {
               return TransformationMatrix.func_227983_a_();
            } else {
               IJoint joint = (IJoint)part;
               TransformationMatrix jointTransform = clip.apply(joint).apply(time).compose(joint.getInvBindPose());

               for(Optional parent = joint.getParent(); parent.isPresent(); parent = ((IJoint)parent.get()).getParent()) {
                  TransformationMatrix parentTransform = clip.apply((IJoint)parent.get()).apply(time);
                  jointTransform = parentTransform.compose(jointTransform);
               }

               return jointTransform;
            }
         }
      }, clip.pastEvents(lastPollTime, time));
   }

   public static enum CommonClipTypeAdapterFactory implements TypeAdapterFactory {
      INSTANCE;

      private final ThreadLocal<Function<String, IClip>> clipResolver = new ThreadLocal();

      public void setClipResolver(@Nullable Function<String, IClip> clipResolver) {
         this.clipResolver.set(clipResolver);
      }

      @Nullable
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
         if (type.getRawType() != IClip.class) {
            return null;
         } else {
            final TypeAdapter<ITimeValue> parameterAdapter = gson.getAdapter(ITimeValue.class);
            return new TypeAdapter<IClip>() {
               public void write(JsonWriter out, IClip clip) throws IOException {
                  if (clip instanceof IStringSerializable) {
                     out.value("#" + ((IStringSerializable)clip).getName());
                  } else if (clip instanceof Clips.TimeClip) {
                     out.beginArray();
                     out.value("apply");
                     Clips.TimeClip timeClip = (Clips.TimeClip)clip;
                     this.write(out, timeClip.childClip);
                     parameterAdapter.write(out, timeClip.time);
                     out.endArray();
                  } else if (clip instanceof Clips.SlerpClip) {
                     out.beginArray();
                     out.value("slerp");
                     Clips.SlerpClip slerpClip = (Clips.SlerpClip)clip;
                     this.write(out, slerpClip.from);
                     this.write(out, slerpClip.to);
                     parameterAdapter.write(out, slerpClip.input);
                     parameterAdapter.write(out, slerpClip.progress);
                     out.endArray();
                  } else if (clip instanceof Clips.TriggerClip) {
                     out.beginArray();
                     out.value("trigger_positive");
                     Clips.TriggerClip triggerClip = (Clips.TriggerClip)clip;
                     this.write(out, triggerClip.clip);
                     parameterAdapter.write(out, triggerClip.parameter);
                     out.value(triggerClip.event);
                     out.endArray();
                  } else if (clip instanceof Clips.ModelClip) {
                     Clips.ModelClip modelClip = (Clips.ModelClip)clip;
                     out.value(modelClip.modelLocation + "@" + modelClip.clipName);
                  } else {
                     throw new NotImplementedException("unknown Clip to json: " + clip);
                  }
               }

               public IClip read(JsonReader in) throws IOException {
                  switch(in.peek()) {
                  case BEGIN_ARRAY:
                     in.beginArray();
                     String type = in.nextString();
                     Object clip;
                     if ("apply".equals(type)) {
                        clip = new Clips.TimeClip(this.read(in), (ITimeValue)parameterAdapter.read(in));
                     } else if ("slerp".equals(type)) {
                        clip = new Clips.SlerpClip(this.read(in), this.read(in), (ITimeValue)parameterAdapter.read(in), (ITimeValue)parameterAdapter.read(in));
                     } else {
                        if (!"trigger_positive".equals(type)) {
                           throw new IOException("Unknown Clip type \"" + type + "\"");
                        }

                        clip = new Clips.TriggerClip(this.read(in), (ITimeValue)parameterAdapter.read(in), in.nextString());
                     }

                     in.endArray();
                     return (IClip)clip;
                  case STRING:
                     String string = in.nextString();
                     if (string.equals("#identity")) {
                        return Clips.IdentityClip.INSTANCE;
                     } else {
                        if (string.startsWith("#")) {
                           return new Clips.ClipReference(string.substring(1), (Function)CommonClipTypeAdapterFactory.this.clipResolver.get());
                        }

                        int at = string.lastIndexOf(64);
                        String location = string.substring(0, at);
                        String clipName = string.substring(at + 1, string.length());
                        Object model;
                        if (location.indexOf(35) != -1) {
                           model = new ModelResourceLocation(location);
                        } else {
                           model = new ResourceLocation(location);
                        }

                        return Clips.getModelClipNode((ResourceLocation)model, clipName);
                     }
                  default:
                     throw new IOException("expected Clip, got " + in.peek());
                  }
               }
            };
         }
      }
   }

   public static final class ClipReference implements IClip, IStringSerializable {
      private final String clipName;
      private final Function<String, IClip> clipResolver;
      private IClip clip;

      public ClipReference(String clipName, Function<String, IClip> clipResolver) {
         this.clipName = clipName;
         this.clipResolver = clipResolver;
      }

      private void resolve() {
         if (this.clip == null) {
            if (this.clipResolver != null) {
               this.clip = (IClip)this.clipResolver.apply(this.clipName);
            }

            if (this.clip == null) {
               throw new IllegalArgumentException("Couldn't resolve clip " + this.clipName);
            }
         }

      }

      public IJointClip apply(IJoint joint) {
         this.resolve();
         return this.clip.apply(joint);
      }

      public Iterable<Event> pastEvents(float lastPollTime, float time) {
         this.resolve();
         return this.clip.pastEvents(lastPollTime, time);
      }

      public String getName() {
         return this.clipName;
      }

      public int hashCode() {
         this.resolve();
         return this.clip.hashCode();
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            Clips.ClipReference other = (Clips.ClipReference)obj;
            this.resolve();
            other.resolve();
            return Objects.equal(this.clip, other.clip);
         }
      }
   }

   public static final class TriggerClip implements IClip {
      private final IClip clip;
      private final ITimeValue parameter;
      private final String event;

      public TriggerClip(IClip clip, ITimeValue parameter, String event) {
         this.clip = clip;
         this.parameter = parameter;
         this.event = event;
      }

      public IJointClip apply(IJoint joint) {
         return this.clip.apply(joint);
      }

      public Iterable<Event> pastEvents(float lastPollTime, float time) {
         return this.parameter.apply(lastPollTime) < 0.0F && this.parameter.apply(time) >= 0.0F ? Iterables.mergeSorted(ImmutableSet.of(this.clip.pastEvents(lastPollTime, time), ImmutableSet.of(new Event(this.event, 0.0F))), Ordering.natural()) : this.clip.pastEvents(lastPollTime, time);
      }
   }

   public static final class SlerpClip implements IClip {
      private final IClip from;
      private final IClip to;
      private final ITimeValue input;
      private final ITimeValue progress;

      public SlerpClip(IClip from, IClip to, ITimeValue input, ITimeValue progress) {
         this.from = from;
         this.to = to;
         this.input = input;
         this.progress = progress;
      }

      public IJointClip apply(IJoint joint) {
         IJointClip fromClip = this.from.apply(joint);
         IJointClip toClip = this.to.apply(joint);
         return Clips.blendClips(joint, fromClip, toClip, this.input, this.progress);
      }

      public Iterable<Event> pastEvents(float lastPollTime, float time) {
         float clipLastPollTime = this.input.apply(lastPollTime);
         float clipTime = this.input.apply(time);
         return Iterables.mergeSorted(ImmutableSet.of(this.from.pastEvents(clipLastPollTime, clipTime), this.to.pastEvents(clipLastPollTime, clipTime)), Ordering.natural());
      }

      public int hashCode() {
         return Objects.hashCode(new Object[]{this.from, this.to, this.input, this.progress});
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            Clips.SlerpClip other = (Clips.SlerpClip)obj;
            return Objects.equal(this.from, other.from) && Objects.equal(this.to, other.to) && Objects.equal(this.input, other.input) && Objects.equal(this.progress, other.progress);
         }
      }
   }

   public static final class TimeClip implements IClip {
      private final IClip childClip;
      private final ITimeValue time;

      public TimeClip(IClip childClip, ITimeValue time) {
         this.childClip = childClip;
         this.time = time;
      }

      public IJointClip apply(final IJoint joint) {
         return new IJointClip() {
            private final IJointClip parent;

            {
               this.parent = TimeClip.this.childClip.apply(joint);
            }

            public TransformationMatrix apply(float time) {
               return this.parent.apply(TimeClip.this.time.apply(time));
            }
         };
      }

      public Iterable<Event> pastEvents(float lastPollTime, float time) {
         return this.childClip.pastEvents(this.time.apply(lastPollTime), this.time.apply(time));
      }

      public int hashCode() {
         return Objects.hashCode(new Object[]{this.childClip, this.time});
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            Clips.TimeClip other = (Clips.TimeClip)obj;
            return Objects.equal(this.childClip, other.childClip) && Objects.equal(this.time, other.time);
         }
      }
   }

   public static final class ModelClip implements IClip {
      private final IClip childClip;
      private final ResourceLocation modelLocation;
      private final String clipName;

      public ModelClip(IClip childClip, ResourceLocation modelLocation, String clipName) {
         this.childClip = childClip;
         this.modelLocation = modelLocation;
         this.clipName = clipName;
      }

      public IJointClip apply(IJoint joint) {
         return this.childClip.apply(joint);
      }

      public Iterable<Event> pastEvents(float lastPollTime, float time) {
         return this.childClip.pastEvents(lastPollTime, time);
      }

      public int hashCode() {
         return Objects.hashCode(new Object[]{this.modelLocation, this.clipName});
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            Clips.ModelClip other = (Clips.ModelClip)obj;
            return Objects.equal(this.modelLocation, other.modelLocation) && Objects.equal(this.clipName, other.clipName);
         }
      }
   }

   public static enum IdentityClip implements IClip, IStringSerializable {
      INSTANCE;

      public IJointClip apply(IJoint joint) {
         return JointClips.IdentityJointClip.INSTANCE;
      }

      public Iterable<Event> pastEvents(float lastPollTime, float time) {
         return ImmutableSet.of();
      }

      public String getName() {
         return "identity";
      }
   }
}
