package net.minecraftforge.client.model.generators;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.BlockPartRotation;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class ModelBuilder<T extends ModelBuilder<T>> extends ModelFile {
   @Nullable
   protected ModelFile parent;
   protected final Map<String, String> textures = new LinkedHashMap();
   protected final ModelBuilder<T>.TransformsBuilder transforms = new ModelBuilder.TransformsBuilder();
   protected final ExistingFileHelper existingFileHelper;
   protected boolean ambientOcclusion = true;
   protected boolean gui3d = false;
   protected final List<ModelBuilder<T>.ElementBuilder> elements = new ArrayList();

   protected ModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
      super(outputLocation);
      this.existingFileHelper = existingFileHelper;
   }

   private T self() {
      return this;
   }

   protected boolean exists() {
      return true;
   }

   public T parent(ModelFile parent) {
      Preconditions.checkNotNull(parent, "Parent must not be null");
      parent.assertExistence();
      this.parent = parent;
      return this.self();
   }

   public T texture(String key, String texture) {
      Preconditions.checkNotNull(key, "Key must not be null");
      Preconditions.checkNotNull(texture, "Texture must not be null");
      if (texture.charAt(0) == '#') {
         this.textures.put(key, texture);
         return this.self();
      } else {
         ResourceLocation asLoc;
         if (texture.contains(":")) {
            asLoc = new ResourceLocation(texture);
         } else {
            asLoc = new ResourceLocation(this.getLocation().getNamespace(), texture);
         }

         return this.texture(key, asLoc);
      }
   }

   public T texture(String key, ResourceLocation texture) {
      Preconditions.checkNotNull(key, "Key must not be null");
      Preconditions.checkNotNull(texture, "Texture must not be null");
      Preconditions.checkArgument(this.existingFileHelper.exists(texture, ResourcePackType.CLIENT_RESOURCES, ".png", "textures"), "Texture %s does not exist in any known resource pack", texture);
      this.textures.put(key, texture.toString());
      return this.self();
   }

   public ModelBuilder<T>.TransformsBuilder transforms() {
      return this.transforms;
   }

   public T ao(boolean ao) {
      this.ambientOcclusion = ao;
      return this.self();
   }

   public T gui3d(boolean gui3d) {
      this.gui3d = gui3d;
      return this.self();
   }

   public ModelBuilder<T>.ElementBuilder element() {
      ModelBuilder<T>.ElementBuilder ret = new ModelBuilder.ElementBuilder();
      this.elements.add(ret);
      return ret;
   }

   public ModelBuilder<T>.ElementBuilder element(int index) {
      Preconditions.checkElementIndex(index, this.elements.size(), "Element index");
      return (ModelBuilder.ElementBuilder)this.elements.get(index);
   }

   @VisibleForTesting
   public JsonObject toJson() {
      JsonObject root = new JsonObject();
      if (this.parent != null) {
         root.addProperty("parent", this.serializeLoc(this.parent.getLocation()));
      }

      if (!this.ambientOcclusion) {
         root.addProperty("ambientocclusion", this.ambientOcclusion);
      }

      Map<ModelBuilder.Perspective, ItemTransformVec3f> transforms = this.transforms.build();
      JsonObject display;
      Iterator var4;
      Entry e;
      if (!transforms.isEmpty()) {
         display = new JsonObject();
         var4 = transforms.entrySet().iterator();

         while(var4.hasNext()) {
            e = (Entry)var4.next();
            JsonObject transform = new JsonObject();
            ItemTransformVec3f vec = (ItemTransformVec3f)e.getValue();
            if (!vec.equals(ItemTransformVec3f.DEFAULT)) {
               if (!vec.rotation.equals(ItemTransformVec3f.Deserializer.ROTATION_DEFAULT)) {
                  transform.add("rotation", this.serializeVector3f(vec.rotation));
               }

               if (!vec.translation.equals(ItemTransformVec3f.Deserializer.TRANSLATION_DEFAULT)) {
                  transform.add("translation", this.serializeVector3f(((ItemTransformVec3f)e.getValue()).translation));
               }

               if (!vec.scale.equals(ItemTransformVec3f.Deserializer.SCALE_DEFAULT)) {
                  transform.add("scale", this.serializeVector3f(((ItemTransformVec3f)e.getValue()).scale));
               }

               display.add(((ModelBuilder.Perspective)e.getKey()).name, transform);
            }
         }

         root.add("display", display);
      }

      if (!this.textures.isEmpty()) {
         display = new JsonObject();
         var4 = this.textures.entrySet().iterator();

         while(var4.hasNext()) {
            e = (Entry)var4.next();
            display.addProperty((String)e.getKey(), this.serializeLocOrKey((String)e.getValue()));
         }

         root.add("textures", display);
      }

      if (!this.elements.isEmpty()) {
         JsonArray elements = new JsonArray();
         this.elements.stream().map(ModelBuilder.ElementBuilder::build).forEach((part) -> {
            JsonObject partObj = new JsonObject();
            partObj.add("from", this.serializeVector3f(part.positionFrom));
            partObj.add("to", this.serializeVector3f(part.positionTo));
            JsonObject faces;
            if (part.partRotation != null) {
               faces = new JsonObject();
               faces.add("origin", this.serializeVector3f(part.partRotation.origin));
               faces.addProperty("axis", part.partRotation.axis.getName());
               faces.addProperty("angle", part.partRotation.angle);
               if (part.partRotation.rescale) {
                  faces.addProperty("rescale", part.partRotation.rescale);
               }

               partObj.add("rotation", faces);
            }

            if (!part.shade) {
               partObj.addProperty("shade", part.shade);
            }

            faces = new JsonObject();
            Direction[] var5 = Direction.values();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Direction dir = var5[var7];
               BlockPartFace face = (BlockPartFace)part.mapFaces.get(dir);
               if (face != null) {
                  JsonObject faceObj = new JsonObject();
                  faceObj.addProperty("texture", this.serializeLocOrKey(face.texture));
                  if (!Arrays.equals(face.blockFaceUV.uvs, part.getFaceUvs(dir))) {
                     faceObj.add("uv", (new Gson()).toJsonTree(face.blockFaceUV.uvs));
                  }

                  if (face.cullFace != null) {
                     faceObj.addProperty("cullface", face.cullFace.getName());
                  }

                  if (face.blockFaceUV.rotation != 0) {
                     faceObj.addProperty("rotation", face.blockFaceUV.rotation);
                  }

                  if (face.tintIndex != -1) {
                     faceObj.addProperty("tintindex", face.tintIndex);
                  }

                  faces.add(dir.getName(), faceObj);
               }
            }

            if (!part.mapFaces.isEmpty()) {
               partObj.add("faces", faces);
            }

            elements.add(partObj);
         });
         root.add("elements", elements);
      }

      return root;
   }

   private String serializeLocOrKey(String tex) {
      return tex.charAt(0) == '#' ? tex : this.serializeLoc(new ResourceLocation(tex));
   }

   String serializeLoc(ResourceLocation loc) {
      return loc.getNamespace().equals("minecraft") ? loc.getPath() : loc.toString();
   }

   private JsonArray serializeVector3f(Vector3f vec) {
      JsonArray ret = new JsonArray();
      ret.add(this.serializeFloat(vec.getX()));
      ret.add(this.serializeFloat(vec.getY()));
      ret.add(this.serializeFloat(vec.getZ()));
      return ret;
   }

   private Number serializeFloat(float f) {
      return (Number)((float)((int)f) == f ? (int)f : f);
   }

   public class TransformsBuilder {
      private final Map<ModelBuilder.Perspective, ModelBuilder<T>.TransformsBuilder.TransformVecBuilder> transforms = new LinkedHashMap();

      public ModelBuilder<T>.TransformsBuilder.TransformVecBuilder transform(ModelBuilder.Perspective type) {
         Preconditions.checkNotNull(type, "Perspective cannot be null");
         return (ModelBuilder.TransformsBuilder.TransformVecBuilder)this.transforms.computeIfAbsent(type, (x$0) -> {
            return new ModelBuilder.TransformsBuilder.TransformVecBuilder(x$0);
         });
      }

      Map<ModelBuilder.Perspective, ItemTransformVec3f> build() {
         return (Map)this.transforms.entrySet().stream().collect(Collectors.toMap(Entry::getKey, (e) -> {
            return ((ModelBuilder.TransformsBuilder.TransformVecBuilder)e.getValue()).build();
         }, (k1, k2) -> {
            throw new IllegalArgumentException();
         }, LinkedHashMap::new));
      }

      public T end() {
         return ModelBuilder.this.self();
      }

      public class TransformVecBuilder {
         private Vector3f rotation = new Vector3f();
         private Vector3f translation = new Vector3f();
         private Vector3f scale = new Vector3f();

         TransformVecBuilder(ModelBuilder.Perspective type) {
         }

         public ModelBuilder<T>.TransformsBuilder.TransformVecBuilder rotation(float x, float y, float z) {
            this.rotation = new Vector3f(x, y, z);
            return this;
         }

         public ModelBuilder<T>.TransformsBuilder.TransformVecBuilder translation(float x, float y, float z) {
            this.translation = new Vector3f(x, y, z);
            return this;
         }

         public ModelBuilder<T>.TransformsBuilder.TransformVecBuilder scale(float sc) {
            return this.scale(sc, sc, sc);
         }

         public ModelBuilder<T>.TransformsBuilder.TransformVecBuilder scale(float x, float y, float z) {
            this.scale = new Vector3f(x, y, z);
            return this;
         }

         ItemTransformVec3f build() {
            return new ItemTransformVec3f(this.rotation, this.translation, this.scale);
         }

         public ModelBuilder<T>.TransformsBuilder end() {
            return TransformsBuilder.this;
         }
      }
   }

   public static enum Perspective {
      THIRDPERSON_RIGHT(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, "thirdperson_righthand"),
      THIRDPERSON_LEFT(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, "thirdperson_lefthand"),
      FIRSTPERSON_RIGHT(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, "firstperson_righthand"),
      FIRSTPERSON_LEFT(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, "firstperson_lefthand"),
      HEAD(ItemCameraTransforms.TransformType.HEAD, "head"),
      GUI(ItemCameraTransforms.TransformType.GUI, "gui"),
      GROUND(ItemCameraTransforms.TransformType.GROUND, "ground"),
      FIXED(ItemCameraTransforms.TransformType.FIXED, "fixed");

      public final ItemCameraTransforms.TransformType vanillaType;
      final String name;

      private Perspective(ItemCameraTransforms.TransformType vanillaType, String name) {
         this.vanillaType = vanillaType;
         this.name = name;
      }
   }

   public static enum FaceRotation {
      ZERO(0),
      CLOCKWISE_90(90),
      UPSIDE_DOWN(180),
      COUNTERCLOCKWISE_90(270);

      final int rotation;

      private FaceRotation(int rotation) {
         this.rotation = rotation;
      }
   }

   public class ElementBuilder {
      private Vector3f from = new Vector3f();
      private Vector3f to = new Vector3f(16.0F, 16.0F, 16.0F);
      private final Map<Direction, ModelBuilder<T>.ElementBuilder.FaceBuilder> faces = new LinkedHashMap();
      private ModelBuilder<T>.ElementBuilder.RotationBuilder rotation;
      private boolean shade = true;

      private void validateCoordinate(float coord, char name) {
         Preconditions.checkArgument(coord >= -16.0F && coord <= 32.0F, "Position " + name + " out of range, must be within [-16, 32]. Found: %d", coord);
      }

      private void validatePosition(Vector3f pos) {
         this.validateCoordinate(pos.getX(), 'x');
         this.validateCoordinate(pos.getY(), 'y');
         this.validateCoordinate(pos.getZ(), 'z');
      }

      public ModelBuilder<T>.ElementBuilder from(float x, float y, float z) {
         this.from = new Vector3f(x, y, z);
         this.validatePosition(this.from);
         return this;
      }

      public ModelBuilder<T>.ElementBuilder to(float x, float y, float z) {
         this.to = new Vector3f(x, y, z);
         this.validatePosition(this.to);
         return this;
      }

      public ModelBuilder<T>.ElementBuilder.FaceBuilder face(Direction dir) {
         Preconditions.checkNotNull(dir, "Direction must not be null");
         return (ModelBuilder.ElementBuilder.FaceBuilder)this.faces.computeIfAbsent(dir, (x$0) -> {
            return new ModelBuilder.ElementBuilder.FaceBuilder(x$0);
         });
      }

      public ModelBuilder<T>.ElementBuilder.RotationBuilder rotation() {
         if (this.rotation == null) {
            this.rotation = new ModelBuilder.ElementBuilder.RotationBuilder();
         }

         return this.rotation;
      }

      public ModelBuilder<T>.ElementBuilder shade(boolean shade) {
         this.shade = shade;
         return this;
      }

      public ModelBuilder<T>.ElementBuilder allFaces(BiConsumer<Direction, ModelBuilder<T>.ElementBuilder.FaceBuilder> action) {
         Arrays.stream(Direction.values()).forEach((d) -> {
            action.accept(d, this.face(d));
         });
         return this;
      }

      public ModelBuilder<T>.ElementBuilder faces(BiConsumer<Direction, ModelBuilder<T>.ElementBuilder.FaceBuilder> action) {
         this.faces.entrySet().stream().forEach((e) -> {
            action.accept(e.getKey(), e.getValue());
         });
         return this;
      }

      public ModelBuilder<T>.ElementBuilder textureAll(String texture) {
         return this.allFaces(this.addTexture(texture));
      }

      public ModelBuilder<T>.ElementBuilder texture(String texture) {
         return this.faces(this.addTexture(texture));
      }

      public ModelBuilder<T>.ElementBuilder cube(String texture) {
         return this.allFaces(this.addTexture(texture).andThen((dir, f) -> {
            f.cullface(dir);
         }));
      }

      private BiConsumer<Direction, ModelBuilder<T>.ElementBuilder.FaceBuilder> addTexture(String texture) {
         return ($, f) -> {
            f.texture(texture);
         };
      }

      BlockPart build() {
         Map<Direction, BlockPartFace> faces = (Map)this.faces.entrySet().stream().collect(Collectors.toMap(Entry::getKey, (e) -> {
            return ((ModelBuilder.ElementBuilder.FaceBuilder)e.getValue()).build();
         }, (k1, k2) -> {
            throw new IllegalArgumentException();
         }, LinkedHashMap::new));
         return new BlockPart(this.from, this.to, faces, this.rotation == null ? null : this.rotation.build(), this.shade);
      }

      public T end() {
         return ModelBuilder.this.self();
      }

      public class RotationBuilder {
         private Vector3f origin;
         private Direction.Axis axis;
         private float angle;
         private boolean rescale;

         public ModelBuilder<T>.ElementBuilder.RotationBuilder origin(float x, float y, float z) {
            this.origin = new Vector3f(x, y, z);
            return this;
         }

         public ModelBuilder<T>.ElementBuilder.RotationBuilder axis(Direction.Axis axis) {
            Preconditions.checkNotNull(axis, "Axis must not be null");
            this.axis = axis;
            return this;
         }

         public ModelBuilder<T>.ElementBuilder.RotationBuilder angle(float angle) {
            Preconditions.checkArgument(angle == 0.0F || MathHelper.abs(angle) == 22.5F || MathHelper.abs(angle) == 45.0F, "Invalid rotation %f found, only -45/-22.5/0/22.5/45 allowed", angle);
            this.angle = angle;
            return this;
         }

         public ModelBuilder<T>.ElementBuilder.RotationBuilder rescale(boolean rescale) {
            this.rescale = rescale;
            return this;
         }

         BlockPartRotation build() {
            return new BlockPartRotation(this.origin, this.axis, this.angle, this.rescale);
         }

         public ModelBuilder<T>.ElementBuilder end() {
            return ElementBuilder.this;
         }
      }

      public class FaceBuilder {
         private Direction cullface;
         private int tintindex = -1;
         private String texture = MissingTextureSprite.getLocation().toString();
         private float[] uvs;
         private ModelBuilder.FaceRotation rotation;

         FaceBuilder(Direction dir) {
            this.rotation = ModelBuilder.FaceRotation.ZERO;
         }

         public ModelBuilder<T>.ElementBuilder.FaceBuilder cullface(@Nullable Direction dir) {
            this.cullface = dir;
            return this;
         }

         public ModelBuilder<T>.ElementBuilder.FaceBuilder tintindex(int index) {
            this.tintindex = index;
            return this;
         }

         public ModelBuilder<T>.ElementBuilder.FaceBuilder texture(String texture) {
            Preconditions.checkNotNull(texture, "Texture must not be null");
            this.texture = texture;
            return this;
         }

         public ModelBuilder<T>.ElementBuilder.FaceBuilder uvs(float u1, float v1, float u2, float v2) {
            this.uvs = new float[]{u1, v1, u2, v2};
            return this;
         }

         public ModelBuilder<T>.ElementBuilder.FaceBuilder rotation(ModelBuilder.FaceRotation rot) {
            Preconditions.checkNotNull(rot, "Rotation must not be null");
            this.rotation = rot;
            return this;
         }

         BlockPartFace build() {
            if (this.texture == null) {
               throw new IllegalStateException("A model face must have a texture");
            } else {
               return new BlockPartFace(this.cullface, this.tintindex, this.texture, new BlockFaceUV(this.uvs, this.rotation.rotation));
            }
         }

         public ModelBuilder<T>.ElementBuilder end() {
            return ElementBuilder.this;
         }
      }
   }
}
