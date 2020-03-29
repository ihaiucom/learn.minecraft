package net.minecraftforge.client.model.animation;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeMap;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.animation.Event;
import net.minecraftforge.common.model.animation.IClip;
import net.minecraftforge.common.model.animation.IJoint;
import net.minecraftforge.common.model.animation.IJointClip;
import net.minecraftforge.common.model.animation.JointClips;
import net.minecraftforge.common.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelBlockAnimation {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ImmutableMap<String, ImmutableMap<String, float[]>> joints;
   private final ImmutableMap<String, ModelBlockAnimation.MBClip> clips;
   private transient ImmutableMultimap<Integer, ModelBlockAnimation.MBJointWeight> jointIndexMap;
   private static final Gson mbaGson;
   private static final ModelBlockAnimation defaultModelBlockAnimation;

   public ModelBlockAnimation(ImmutableMap<String, ImmutableMap<String, float[]>> joints, ImmutableMap<String, ModelBlockAnimation.MBClip> clips) {
      this.joints = joints;
      this.clips = clips;
   }

   public ImmutableMap<String, ? extends IClip> getClips() {
      return this.clips;
   }

   public ImmutableCollection<ModelBlockAnimation.MBJointWeight> getJoint(int i) {
      if (this.jointIndexMap == null) {
         Builder<Integer, ModelBlockAnimation.MBJointWeight> builder = ImmutableMultimap.builder();
         UnmodifiableIterator var3 = this.joints.entrySet().iterator();

         while(var3.hasNext()) {
            Entry<String, ImmutableMap<String, float[]>> info = (Entry)var3.next();
            com.google.common.collect.ImmutableMap.Builder<Integer, float[]> weightBuilder = ImmutableMap.builder();
            UnmodifiableIterator var6 = ((ImmutableMap)info.getValue()).entrySet().iterator();

            while(var6.hasNext()) {
               Entry<String, float[]> e = (Entry)var6.next();
               weightBuilder.put(Integer.parseInt((String)e.getKey()), e.getValue());
            }

            ImmutableMap<Integer, float[]> weightMap = weightBuilder.build();
            UnmodifiableIterator var10 = weightMap.entrySet().iterator();

            while(var10.hasNext()) {
               Entry<Integer, float[]> e = (Entry)var10.next();
               builder.put(e.getKey(), new ModelBlockAnimation.MBJointWeight((String)info.getKey(), weightMap));
            }
         }

         this.jointIndexMap = builder.build();
      }

      return this.jointIndexMap.get(i);
   }

   @Nullable
   public TransformationMatrix getPartTransform(IModelTransform state, BlockPart part, int i) {
      return this.getPartTransform(state, i);
   }

   @Nullable
   public TransformationMatrix getPartTransform(IModelTransform state, int i) {
      ImmutableCollection<ModelBlockAnimation.MBJointWeight> infos = this.getJoint(i);
      if (!infos.isEmpty()) {
         Matrix4f m = new Matrix4f();
         float weight = 0.0F;
         UnmodifiableIterator var7 = infos.iterator();

         while(var7.hasNext()) {
            ModelBlockAnimation.MBJointWeight info = (ModelBlockAnimation.MBJointWeight)var7.next();
            if (info.getWeights().containsKey(i)) {
               ModelBlockAnimation.MBJoint joint = new ModelBlockAnimation.MBJoint(info.getName());
               TransformationMatrix trOp = state.getPartTransformation(joint);
               if (!trOp.isIdentity()) {
                  float w = ((float[])info.getWeights().get(i))[0];
                  Matrix4f tmp = trOp.func_227988_c_();
                  tmp.func_226592_a_(w);
                  m.add(tmp);
                  weight += w;
               }
            }
         }

         if ((double)weight > 1.0E-5D) {
            m.func_226592_a_(1.0F / weight);
            return new TransformationMatrix(m);
         }
      }

      return null;
   }

   public static ModelBlockAnimation loadVanillaAnimation(IResourceManager manager, ResourceLocation armatureLocation) {
      try {
         try {
            IResource resource = manager.getResource(armatureLocation);
            Throwable var3 = null;

            ModelBlockAnimation var5;
            try {
               ModelBlockAnimation mba = (ModelBlockAnimation)mbaGson.fromJson(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8), ModelBlockAnimation.class);
               var5 = mba;
            } catch (Throwable var16) {
               var3 = var16;
               throw var16;
            } finally {
               if (resource != null) {
                  if (var3 != null) {
                     try {
                        resource.close();
                     } catch (Throwable var15) {
                        var3.addSuppressed(var15);
                     }
                  } else {
                     resource.close();
                  }
               }

            }

            return var5;
         } catch (FileNotFoundException var18) {
            return defaultModelBlockAnimation;
         }
      } catch (JsonParseException | IOException var19) {
         LOGGER.error("Exception loading vanilla model animation {}, skipping", armatureLocation, var19);
         return defaultModelBlockAnimation;
      }
   }

   static {
      mbaGson = (new GsonBuilder()).registerTypeAdapter(ImmutableList.class, JsonUtils.ImmutableListTypeAdapter.INSTANCE).registerTypeAdapter(ImmutableMap.class, JsonUtils.ImmutableMapTypeAdapter.INSTANCE).setPrettyPrinting().enableComplexMapKeySerialization().disableHtmlEscaping().create();
      defaultModelBlockAnimation = new ModelBlockAnimation(ImmutableMap.of(), ImmutableMap.of());
   }

   protected static class Parameter {
      public static enum Interpolation {
         @SerializedName("linear")
         LINEAR,
         @SerializedName("nearest")
         NEAREST;
      }

      public static enum Type {
         @SerializedName("uniform")
         UNIFORM;
      }

      public static enum Variable {
         @SerializedName("offset_x")
         X,
         @SerializedName("offset_y")
         Y,
         @SerializedName("offset_z")
         Z,
         @SerializedName("axis_x")
         XROT,
         @SerializedName("axis_y")
         YROT,
         @SerializedName("axis_z")
         ZROT,
         @SerializedName("angle")
         ANGLE,
         @SerializedName("scale")
         SCALE,
         @SerializedName("scale_x")
         XS,
         @SerializedName("scale_y")
         YS,
         @SerializedName("scale_z")
         ZS,
         @SerializedName("origin_x")
         XORIGIN,
         @SerializedName("origin_y")
         YORIGIN,
         @SerializedName("origin_z")
         ZORIGIN;
      }
   }

   protected static class MBJointWeight {
      private final String name;
      private final ImmutableMap<Integer, float[]> weights;

      public MBJointWeight(String name, ImmutableMap<Integer, float[]> weights) {
         this.name = name;
         this.weights = weights;
      }

      public String getName() {
         return this.name;
      }

      public ImmutableMap<Integer, float[]> getWeights() {
         return this.weights;
      }
   }

   protected static class MBJoint implements IJoint {
      private final String name;

      public MBJoint(String name) {
         this.name = name;
      }

      public TransformationMatrix getInvBindPose() {
         return TransformationMatrix.func_227983_a_();
      }

      public Optional<? extends IJoint> getParent() {
         return Optional.empty();
      }

      public String getName() {
         return this.name;
      }
   }

   protected static class MBClip implements IClip {
      private final boolean loop;
      @SerializedName("joint_clips")
      private final ImmutableMap<String, ImmutableList<ModelBlockAnimation.MBVariableClip>> jointClipsFlat;
      private transient ImmutableMap<String, ModelBlockAnimation.MBClip.MBJointClip> jointClips;
      @SerializedName("events")
      private final ImmutableMap<String, String> eventsRaw;
      private transient TreeMap<Float, Event> events;

      public MBClip(boolean loop, ImmutableMap<String, ImmutableList<ModelBlockAnimation.MBVariableClip>> clips, ImmutableMap<String, String> events) {
         this.loop = loop;
         this.jointClipsFlat = clips;
         this.eventsRaw = events;
      }

      private void initialize() {
         if (this.jointClips == null) {
            com.google.common.collect.ImmutableMap.Builder<String, ModelBlockAnimation.MBClip.MBJointClip> builder = ImmutableMap.builder();
            UnmodifiableIterator var2 = this.jointClipsFlat.entrySet().iterator();

            while(var2.hasNext()) {
               Entry<String, ImmutableList<ModelBlockAnimation.MBVariableClip>> e = (Entry)var2.next();
               builder.put(e.getKey(), new ModelBlockAnimation.MBClip.MBJointClip(this.loop, (ImmutableList)e.getValue()));
            }

            this.jointClips = builder.build();
            this.events = Maps.newTreeMap();
            if (!this.eventsRaw.isEmpty()) {
               TreeMap<Float, String> times = Maps.newTreeMap();
               UnmodifiableIterator var9 = this.eventsRaw.keySet().iterator();

               while(var9.hasNext()) {
                  String time = (String)var9.next();
                  times.put(Float.parseFloat(time), time);
               }

               float lastTime = Float.POSITIVE_INFINITY;
               if (this.loop) {
                  lastTime = (Float)times.firstKey();
               }

               Entry entry;
               float time;
               float offset;
               for(Iterator var11 = times.descendingMap().entrySet().iterator(); var11.hasNext(); this.events.put(time, new Event((String)this.eventsRaw.get(entry.getValue()), offset))) {
                  entry = (Entry)var11.next();
                  time = (Float)entry.getKey();
                  offset = lastTime - time;
                  if (this.loop) {
                     offset = 1.0F - (1.0F - offset) % 1.0F;
                  }
               }
            }
         }

      }

      public IJointClip apply(IJoint joint) {
         this.initialize();
         if (joint instanceof ModelBlockAnimation.MBJoint) {
            ModelBlockAnimation.MBJoint mbJoint = (ModelBlockAnimation.MBJoint)joint;
            ModelBlockAnimation.MBClip.MBJointClip clip = (ModelBlockAnimation.MBClip.MBJointClip)this.jointClips.get(mbJoint.getName());
            if (clip != null) {
               return clip;
            }
         }

         return JointClips.IdentityJointClip.INSTANCE;
      }

      public Iterable<Event> pastEvents(final float lastPollTime, final float time) {
         this.initialize();
         return new Iterable<Event>() {
            public Iterator<Event> iterator() {
               return new UnmodifiableIterator<Event>() {
                  private Float curKey;
                  private Event firstEvent;
                  private float stopTime;

                  {
                     if (lastPollTime >= time) {
                        this.curKey = null;
                     } else {
                        float fractTime = time - (float)Math.floor((double)time);
                        float fractLastTime = lastPollTime - (float)Math.floor((double)lastPollTime);
                        float checkCurTime;
                        if (fractLastTime > fractTime) {
                           checkCurTime = fractTime;
                           fractTime = fractLastTime;
                           fractLastTime = checkCurTime;
                        }

                        if (fractTime - fractLastTime > 0.5F) {
                           checkCurTime = fractTime;
                           fractTime = fractLastTime;
                           fractLastTime = checkCurTime;
                        }

                        this.stopTime = fractLastTime;
                        this.curKey = (Float)MBClip.this.events.floorKey(fractTime);
                        if (this.curKey == null && MBClip.this.loop && !MBClip.this.events.isEmpty()) {
                           this.curKey = (Float)MBClip.this.events.lastKey();
                        }

                        if (this.curKey != null) {
                           checkCurTime = this.curKey;
                           float checkStopTime = this.stopTime;
                           if (checkCurTime >= fractTime) {
                              --checkCurTime;
                           }

                           if (checkStopTime >= fractTime) {
                              --checkStopTime;
                           }

                           float offset = fractTime - checkCurTime;
                           Event event = (Event)MBClip.this.events.get(this.curKey);
                           if (checkCurTime < checkStopTime) {
                              this.curKey = null;
                           } else if (offset != event.offset()) {
                              this.firstEvent = new Event(event.event(), offset);
                           }
                        }
                     }

                  }

                  public boolean hasNext() {
                     return this.curKey != null;
                  }

                  public Event next() {
                     if (this.curKey == null) {
                        throw new NoSuchElementException();
                     } else {
                        Event event;
                        if (this.firstEvent == null) {
                           event = (Event)MBClip.this.events.get(this.curKey);
                        } else {
                           event = this.firstEvent;
                           this.firstEvent = null;
                        }

                        this.curKey = (Float)MBClip.this.events.lowerKey(this.curKey);
                        if (this.curKey == null && MBClip.this.loop) {
                           this.curKey = (Float)MBClip.this.events.lastKey();
                        }

                        if (this.curKey != null) {
                           float checkStopTime;
                           for(checkStopTime = this.stopTime; this.curKey + ((Event)MBClip.this.events.get(this.curKey)).offset() < checkStopTime; --checkStopTime) {
                           }

                           while(this.curKey + ((Event)MBClip.this.events.get(this.curKey)).offset() >= checkStopTime + 1.0F) {
                              ++checkStopTime;
                           }

                           if (this.curKey <= checkStopTime) {
                              this.curKey = null;
                           }
                        }

                        return event;
                     }
                  }
               };
            }
         };
      }

      protected static class MBJointClip implements IJointClip {
         private final boolean loop;
         private final ImmutableList<ModelBlockAnimation.MBVariableClip> variables;

         public MBJointClip(boolean loop, ImmutableList<ModelBlockAnimation.MBVariableClip> variables) {
            this.loop = loop;
            this.variables = variables;
            EnumSet<ModelBlockAnimation.Parameter.Variable> hadVar = Sets.newEnumSet(Collections.emptyList(), ModelBlockAnimation.Parameter.Variable.class);
            UnmodifiableIterator var4 = variables.iterator();

            while(var4.hasNext()) {
               ModelBlockAnimation.MBVariableClip var = (ModelBlockAnimation.MBVariableClip)var4.next();
               if (hadVar.contains(var.variable)) {
                  throw new IllegalArgumentException("duplicate variable: " + var);
               }

               hadVar.add(var.variable);
            }

         }

         public TransformationMatrix apply(float time) {
            time = (float)((double)time - Math.floor((double)time));
            Vector3f translation = new Vector3f(0.0F, 0.0F, 0.0F);
            Vector3f scale = new Vector3f(1.0F, 1.0F, 1.0F);
            Vector3f origin = new Vector3f(0.0F, 0.0F, 0.0F);
            Vector3f rotation_axis = new Vector3f(0.0F, 0.0F, 0.0F);
            float rotation_angle = 0.0F;
            UnmodifiableIterator var7 = this.variables.iterator();

            while(var7.hasNext()) {
               ModelBlockAnimation.MBVariableClip var = (ModelBlockAnimation.MBVariableClip)var7.next();
               int length = this.loop ? var.samples.length : var.samples.length - 1;
               float timeScaled = time * (float)length;
               int s1 = MathHelper.clamp((int)Math.round(Math.floor((double)timeScaled)), 0, length - 1);
               float progress = timeScaled - (float)s1;
               int s2 = s1 + 1;
               if (s2 == length && this.loop) {
                  s2 = 0;
               }

               float value = 0.0F;
               switch(var.interpolation) {
               case LINEAR:
                  if (var.variable == ModelBlockAnimation.Parameter.Variable.ANGLE) {
                     float v1 = var.samples[s1];
                     float v2 = var.samples[s2];
                     float diff = ((v2 - v1) % 360.0F + 540.0F) % 360.0F - 180.0F;
                     value = v1 + diff * progress;
                  } else {
                     value = var.samples[s1] * (1.0F - progress) + var.samples[s2] * progress;
                  }
                  break;
               case NEAREST:
                  value = var.samples[progress < 0.5F ? s1 : s2];
               }

               switch(var.variable) {
               case X:
                  translation.setX(value);
                  break;
               case Y:
                  translation.setY(value);
                  break;
               case Z:
                  translation.setZ(value);
                  break;
               case XROT:
                  rotation_axis.setX(value);
                  break;
               case YROT:
                  rotation_axis.setY(value);
                  break;
               case ZROT:
                  rotation_axis.setZ(value);
                  break;
               case ANGLE:
                  rotation_angle = (float)Math.toRadians((double)value);
                  break;
               case SCALE:
                  scale.set(value, value, value);
                  break;
               case XS:
                  scale.setX(value);
                  break;
               case YS:
                  scale.setY(value);
                  break;
               case ZS:
                  scale.setX(value);
                  break;
               case XORIGIN:
                  origin.setX(value - 0.5F);
                  break;
               case YORIGIN:
                  origin.setY(value - 0.5F);
                  break;
               case ZORIGIN:
                  origin.setX(value - 0.5F);
               }
            }

            Quaternion rot = new Quaternion(rotation_axis, rotation_angle, false);
            TransformationMatrix base = new TransformationMatrix(translation, rot, scale, (Quaternion)null);
            Vector3f negOrigin = origin.func_229195_e_();
            negOrigin.func_229192_b_(-1.0F, -1.0F, -1.0F);
            base = (new TransformationMatrix(origin, (Quaternion)null, (Vector3f)null, (Quaternion)null)).compose(base).compose(new TransformationMatrix(negOrigin, (Quaternion)null, (Vector3f)null, (Quaternion)null));
            return base.blockCenterToCorner();
         }
      }
   }

   protected static class MBVariableClip {
      private final ModelBlockAnimation.Parameter.Variable variable;
      private final ModelBlockAnimation.Parameter.Type type;
      private final ModelBlockAnimation.Parameter.Interpolation interpolation;
      private final float[] samples;

      public MBVariableClip(ModelBlockAnimation.Parameter.Variable variable, ModelBlockAnimation.Parameter.Type type, ModelBlockAnimation.Parameter.Interpolation interpolation, float[] samples) {
         this.variable = variable;
         this.type = type;
         this.interpolation = interpolation;
         this.samples = samples;
      }
   }
}
