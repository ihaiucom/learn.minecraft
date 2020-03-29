package net.minecraftforge.client.model.obj;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import joptsimple.internal.Strings;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.IMultipartModelGeometry;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import org.apache.commons.lang3.tuple.Pair;

public class OBJModel implements IMultipartModelGeometry<OBJModel> {
   private static Vector4f COLOR_WHITE = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   private static Vec2f[] DEFAULT_COORDS = new Vec2f[]{new Vec2f(0.0F, 0.0F), new Vec2f(0.0F, 1.0F), new Vec2f(1.0F, 1.0F), new Vec2f(1.0F, 0.0F)};
   private final Map<String, OBJModel.ModelGroup> parts = Maps.newHashMap();
   private final List<Vector3f> positions = Lists.newArrayList();
   private final List<Vec2f> texCoords = Lists.newArrayList();
   private final List<Vector3f> normals = Lists.newArrayList();
   private final List<Vector4f> colors = Lists.newArrayList();
   public final boolean detectCullableFaces;
   public final boolean diffuseLighting;
   public final boolean flipV;
   public final boolean ambientToFullbright;
   public final ResourceLocation modelLocation;
   @Nullable
   public final String materialLibraryOverrideLocation;

   OBJModel(LineReader reader, OBJModel.ModelSettings settings) throws IOException {
      this.modelLocation = settings.modelLocation;
      this.detectCullableFaces = settings.detectCullableFaces;
      this.diffuseLighting = settings.diffuseLighting;
      this.flipV = settings.flipV;
      this.ambientToFullbright = settings.ambientToFullbright;
      this.materialLibraryOverrideLocation = settings.materialLibraryOverrideLocation;
      String modelDomain = this.modelLocation.getNamespace();
      String modelPath = this.modelLocation.getPath();
      int lastSlash = modelPath.lastIndexOf(47);
      if (lastSlash >= 0) {
         modelPath = modelPath.substring(0, lastSlash + 1);
      } else {
         modelPath = "";
      }

      MaterialLibrary mtllib = MaterialLibrary.EMPTY;
      MaterialLibrary.Material currentMat = null;
      String currentSmoothingGroup = null;
      OBJModel.ModelGroup currentGroup = null;
      OBJModel.ModelObject currentObject = null;
      OBJModel.ModelMesh currentMesh = null;
      boolean objAboveGroup = false;
      if (this.materialLibraryOverrideLocation != null) {
         String lib = this.materialLibraryOverrideLocation;
         if (lib.contains(":")) {
            mtllib = OBJLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(lib));
         } else {
            mtllib = OBJLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(modelDomain, modelPath + lib));
         }
      }

      while(true) {
         String[] line;
         while((line = reader.readAndSplitLine(true)) != null) {
            String var14 = line[0];
            byte var15 = -1;
            switch(var14.hashCode()) {
            case -1063936832:
               if (var14.equals("mtllib")) {
                  var15 = 0;
               }
               break;
            case -836034370:
               if (var14.equals("usemtl")) {
                  var15 = 1;
               }
               break;
            case 102:
               if (var14.equals("f")) {
                  var15 = 6;
               }
               break;
            case 103:
               if (var14.equals("g")) {
                  var15 = 8;
               }
               break;
            case 111:
               if (var14.equals("o")) {
                  var15 = 9;
               }
               break;
            case 115:
               if (var14.equals("s")) {
                  var15 = 7;
               }
               break;
            case 118:
               if (var14.equals("v")) {
                  var15 = 2;
               }
               break;
            case 3757:
               if (var14.equals("vc")) {
                  var15 = 5;
               }
               break;
            case 3768:
               if (var14.equals("vn")) {
                  var15 = 4;
               }
               break;
            case 3774:
               if (var14.equals("vt")) {
                  var15 = 3;
               }
            }

            String smoothingGroup;
            switch(var15) {
            case 0:
               if (this.materialLibraryOverrideLocation == null) {
                  smoothingGroup = line[1];
                  if (smoothingGroup.contains(":")) {
                     mtllib = OBJLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(smoothingGroup));
                  } else {
                     mtllib = OBJLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(modelDomain, modelPath + smoothingGroup));
                  }
               }
               break;
            case 1:
               smoothingGroup = Strings.join((String[])Arrays.copyOfRange(line, 1, line.length), " ");
               MaterialLibrary.Material newMat = mtllib.getMaterial(smoothingGroup);
               if (Objects.equals(newMat, currentMat)) {
                  break;
               }

               currentMat = newMat;
               if (currentMesh != null && currentMesh.mat == null && currentMesh.faces.size() == 0) {
                  currentMesh.mat = newMat;
                  break;
               }

               currentMesh = null;
               break;
            case 2:
               this.positions.add(parseVector4To3(line));
               break;
            case 3:
               this.texCoords.add(parseVector2(line));
               break;
            case 4:
               this.normals.add(parseVector3(line));
               break;
            case 5:
               this.colors.add(parseVector4(line));
               break;
            case 6:
               if (currentMesh == null) {
                  currentMesh = new OBJModel.ModelMesh(currentMat, currentSmoothingGroup);
                  if (currentObject != null) {
                     currentObject.meshes.add(currentMesh);
                  } else {
                     if (currentGroup == null) {
                        currentGroup = new OBJModel.ModelGroup("");
                        this.parts.put("", currentGroup);
                     }

                     currentGroup.meshes.add(currentMesh);
                  }
               }

               int[][] vertices = new int[line.length - 1][];

               for(int i = 0; i < vertices.length; ++i) {
                  String vertexData = line[i + 1];
                  String[] vertexParts = vertexData.split("/");
                  int[] vertex = Arrays.stream(vertexParts).mapToInt((num) -> {
                     return Strings.isNullOrEmpty(num) ? 0 : Integer.parseInt(num);
                  }).toArray();
                  int var10002;
                  if (vertex[0] < 0) {
                     vertex[0] += this.positions.size();
                  } else {
                     var10002 = vertex[0]--;
                  }

                  if (vertex.length > 1) {
                     if (vertex[1] < 0) {
                        vertex[1] += this.texCoords.size();
                     } else {
                        var10002 = vertex[1]--;
                     }

                     if (vertex.length > 2) {
                        if (vertex[2] < 0) {
                           vertex[2] += this.normals.size();
                        } else {
                           var10002 = vertex[2]--;
                        }

                        if (vertex.length > 3) {
                           if (vertex[3] < 0) {
                              vertex[3] += this.colors.size();
                           } else {
                              var10002 = vertex[3]--;
                           }
                        }
                     }
                  }

                  vertices[i] = vertex;
               }

               currentMesh.faces.add(vertices);
               break;
            case 7:
               smoothingGroup = "off".equals(line[1]) ? null : line[1];
               if (Objects.equals(currentSmoothingGroup, smoothingGroup)) {
                  break;
               }

               currentSmoothingGroup = smoothingGroup;
               if (currentMesh != null && currentMesh.smoothingGroup == null && currentMesh.faces.size() == 0) {
                  currentMesh.smoothingGroup = smoothingGroup;
                  break;
               }

               currentMesh = null;
               break;
            case 8:
               smoothingGroup = line[1];
               if (objAboveGroup) {
                  currentObject = new OBJModel.ModelObject(currentGroup.name() + "/" + smoothingGroup);
                  currentGroup.parts.put(smoothingGroup, currentObject);
               } else {
                  currentGroup = new OBJModel.ModelGroup(smoothingGroup);
                  this.parts.put(smoothingGroup, currentGroup);
                  currentObject = null;
               }

               currentMesh = null;
               break;
            case 9:
               smoothingGroup = line[1];
               if (!objAboveGroup && currentGroup != null) {
                  currentObject = new OBJModel.ModelObject(currentGroup.name() + "/" + smoothingGroup);
                  currentGroup.parts.put(smoothingGroup, currentObject);
               } else {
                  objAboveGroup = true;
                  currentGroup = new OBJModel.ModelGroup(smoothingGroup);
                  this.parts.put(smoothingGroup, currentGroup);
                  currentObject = null;
               }

               currentMesh = null;
            }
         }

         return;
      }
   }

   public static Vector3f parseVector4To3(String[] line) {
      switch(line.length) {
      case 1:
         return new Vector3f(0.0F, 0.0F, 0.0F);
      case 2:
         return new Vector3f(Float.parseFloat(line[1]), 0.0F, 0.0F);
      case 3:
         return new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), 0.0F);
      case 4:
         return new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]));
      default:
         Vector4f vec4 = parseVector4(line);
         return new Vector3f(vec4.getX() / vec4.getW(), vec4.getY() / vec4.getW(), vec4.getZ() / vec4.getW());
      }
   }

   public static Vec2f parseVector2(String[] line) {
      switch(line.length) {
      case 1:
         return new Vec2f(0.0F, 0.0F);
      case 2:
         return new Vec2f(Float.parseFloat(line[1]), 0.0F);
      default:
         return new Vec2f(Float.parseFloat(line[1]), Float.parseFloat(line[2]));
      }
   }

   public static Vector3f parseVector3(String[] line) {
      switch(line.length) {
      case 1:
         return new Vector3f(0.0F, 0.0F, 0.0F);
      case 2:
         return new Vector3f(Float.parseFloat(line[1]), 0.0F, 0.0F);
      case 3:
         return new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), 0.0F);
      default:
         return new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]));
      }
   }

   public static Vector4f parseVector4(String[] line) {
      switch(line.length) {
      case 1:
         return new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
      case 2:
         return new Vector4f(Float.parseFloat(line[1]), 0.0F, 0.0F, 1.0F);
      case 3:
         return new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), 0.0F, 1.0F);
      case 4:
         return new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]), 1.0F);
      default:
         return new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]), Float.parseFloat(line[4]));
      }
   }

   public Collection<? extends IModelGeometryPart> getParts() {
      return this.parts.values();
   }

   public Optional<? extends IModelGeometryPart> getPart(String name) {
      return Optional.ofNullable(this.parts.get(name));
   }

   private Pair<BakedQuad, Direction> makeQuad(int[][] indices, int tintIndex, Vector4f colorTint, Vector4f ambientColor, TextureAtlasSprite texture, TransformationMatrix transform) {
      boolean needsNormalRecalculation = false;
      int[][] var8 = indices;
      int var9 = indices.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         int[] ints = var8[var10];
         needsNormalRecalculation |= ints.length < 3;
      }

      Vector3f faceNormal = new Vector3f(0.0F, 0.0F, 0.0F);
      if (needsNormalRecalculation) {
         Vector3f a = (Vector3f)this.positions.get(indices[0][0]);
         Vector3f ab = (Vector3f)this.positions.get(indices[1][0]);
         Vector3f ac = (Vector3f)this.positions.get(indices[2][0]);
         Vector3f abs = ab.func_229195_e_();
         abs.sub(a);
         Vector3f acs = ac.func_229195_e_();
         acs.sub(a);
         abs.cross(acs);
         abs.func_229194_d_();
         faceNormal = abs;
      }

      Vector4f[] pos = new Vector4f[4];
      Vector3f[] norm = new Vector3f[4];
      BakedQuadBuilder builder = new BakedQuadBuilder(texture);
      builder.setQuadTint(tintIndex);
      Vec2f uv2 = new Vec2f(0.0F, 0.0F);
      if (this.ambientToFullbright) {
         int fakeLight = (int)((ambientColor.getX() + ambientColor.getY() + ambientColor.getZ()) * 15.0F / 3.0F);
         uv2 = new Vec2f((float)(fakeLight << 4) / 32767.0F, (float)(fakeLight << 4) / 32767.0F);
         builder.setApplyDiffuseLighting(fakeLight == 0);
      }

      boolean hasTransform = !transform.isIdentity();
      TransformationMatrix transformation = hasTransform ? transform.blockCenterToCorner() : transform;

      for(int i = 0; i < 4; ++i) {
         int[] index = indices[Math.min(i, indices.length - 1)];
         Vector3f pos0 = (Vector3f)this.positions.get(index[0]);
         Vector4f position = new Vector4f(pos0);
         Vec2f texCoord = index.length >= 2 && this.texCoords.size() > 0 ? (Vec2f)this.texCoords.get(index[1]) : DEFAULT_COORDS[i];
         Vector3f norm0 = !needsNormalRecalculation && index.length >= 3 && this.normals.size() > 0 ? (Vector3f)this.normals.get(index[2]) : faceNormal;
         Vector3f normal = norm0;
         Vector4f color = index.length >= 4 && this.colors.size() > 0 ? (Vector4f)this.colors.get(index[3]) : COLOR_WHITE;
         if (hasTransform) {
            normal = norm0.func_229195_e_();
            transformation.transformPosition(position);
            transformation.transformNormal(normal);
         }

         Vector4f tintedColor = new Vector4f(color.getX() * colorTint.getX(), color.getY() * colorTint.getY(), color.getZ() * colorTint.getZ(), color.getW() * colorTint.getW());
         this.putVertexData(builder, position, texCoord, normal, tintedColor, uv2, texture);
         pos[i] = position;
         norm[i] = normal;
      }

      builder.setQuadOrientation(Direction.getFacingFromVector(norm[0].getX(), norm[0].getY(), norm[0].getZ()));
      Direction cull = null;
      if (this.detectCullableFaces) {
         if (MathHelper.epsilonEquals(pos[0].getX(), 0.0F) && MathHelper.epsilonEquals(pos[1].getX(), 0.0F) && MathHelper.epsilonEquals(pos[2].getX(), 0.0F) && MathHelper.epsilonEquals(pos[3].getX(), 0.0F) && norm[0].getX() < 0.0F) {
            cull = Direction.WEST;
         } else if (MathHelper.epsilonEquals(pos[0].getX(), 1.0F) && MathHelper.epsilonEquals(pos[1].getX(), 1.0F) && MathHelper.epsilonEquals(pos[2].getX(), 1.0F) && MathHelper.epsilonEquals(pos[3].getX(), 1.0F) && norm[0].getX() > 0.0F) {
            cull = Direction.EAST;
         } else if (MathHelper.epsilonEquals(pos[0].getZ(), 0.0F) && MathHelper.epsilonEquals(pos[1].getZ(), 0.0F) && MathHelper.epsilonEquals(pos[2].getZ(), 0.0F) && MathHelper.epsilonEquals(pos[3].getZ(), 0.0F) && norm[0].getZ() < 0.0F) {
            cull = Direction.NORTH;
         } else if (MathHelper.epsilonEquals(pos[0].getZ(), 1.0F) && MathHelper.epsilonEquals(pos[1].getZ(), 1.0F) && MathHelper.epsilonEquals(pos[2].getZ(), 1.0F) && MathHelper.epsilonEquals(pos[3].getZ(), 1.0F) && norm[0].getZ() > 0.0F) {
            cull = Direction.SOUTH;
         } else if (MathHelper.epsilonEquals(pos[0].getY(), 0.0F) && MathHelper.epsilonEquals(pos[1].getY(), 0.0F) && MathHelper.epsilonEquals(pos[2].getY(), 0.0F) && MathHelper.epsilonEquals(pos[3].getY(), 0.0F) && norm[0].getY() < 0.0F) {
            cull = Direction.DOWN;
         } else if (MathHelper.epsilonEquals(pos[0].getY(), 1.0F) && MathHelper.epsilonEquals(pos[1].getY(), 1.0F) && MathHelper.epsilonEquals(pos[2].getY(), 1.0F) && MathHelper.epsilonEquals(pos[3].getY(), 1.0F) && norm[0].getY() > 0.0F) {
            cull = Direction.UP;
         }
      }

      return Pair.of(builder.build(), cull);
   }

   private void putVertexData(IVertexConsumer consumer, Vector4f position0, Vec2f texCoord0, Vector3f normal0, Vector4f color0, Vec2f uv2, TextureAtlasSprite texture) {
      ImmutableList<VertexFormatElement> elements = consumer.getVertexFormat().func_227894_c_();

      for(int j = 0; j < elements.size(); ++j) {
         VertexFormatElement e = (VertexFormatElement)elements.get(j);
         switch(e.getUsage()) {
         case POSITION:
            consumer.put(j, position0.getX(), position0.getY(), position0.getZ(), position0.getW());
            break;
         case COLOR:
            consumer.put(j, color0.getX(), color0.getY(), color0.getZ(), color0.getW());
            break;
         case UV:
            switch(e.getIndex()) {
            case 0:
               consumer.put(j, texture.getInterpolatedU((double)(texCoord0.x * 16.0F)), texture.getInterpolatedV((double)((this.flipV ? 1.0F - texCoord0.y : texCoord0.y) * 16.0F)));
               continue;
            case 2:
               consumer.put(j, uv2.x, uv2.y);
               continue;
            default:
               consumer.put(j);
               continue;
            }
         case NORMAL:
            consumer.put(j, normal0.getX(), normal0.getY(), normal0.getZ());
            break;
         default:
            consumer.put(j);
         }
      }

   }

   public static class ModelSettings {
      @Nonnull
      public final ResourceLocation modelLocation;
      public final boolean detectCullableFaces;
      public final boolean diffuseLighting;
      public final boolean flipV;
      public final boolean ambientToFullbright;
      @Nullable
      public final String materialLibraryOverrideLocation;

      public ModelSettings(@Nonnull ResourceLocation modelLocation, boolean detectCullableFaces, boolean diffuseLighting, boolean flipV, boolean ambientToFullbright, @Nullable String materialLibraryOverrideLocation) {
         this.modelLocation = modelLocation;
         this.detectCullableFaces = detectCullableFaces;
         this.diffuseLighting = diffuseLighting;
         this.flipV = flipV;
         this.ambientToFullbright = ambientToFullbright;
         this.materialLibraryOverrideLocation = materialLibraryOverrideLocation;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            OBJModel.ModelSettings that = (OBJModel.ModelSettings)o;
            return this.equals(that);
         } else {
            return false;
         }
      }

      public boolean equals(@Nonnull OBJModel.ModelSettings that) {
         return this.detectCullableFaces == that.detectCullableFaces && this.diffuseLighting == that.diffuseLighting && this.flipV == that.flipV && this.ambientToFullbright == that.ambientToFullbright && this.modelLocation.equals(that.modelLocation) && Objects.equals(this.materialLibraryOverrideLocation, that.materialLibraryOverrideLocation);
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.modelLocation, this.detectCullableFaces, this.diffuseLighting, this.flipV, this.ambientToFullbright, this.materialLibraryOverrideLocation});
      }
   }

   private class ModelMesh {
      @Nullable
      public MaterialLibrary.Material mat;
      @Nullable
      public String smoothingGroup;
      public final List<int[][]> faces = Lists.newArrayList();

      public ModelMesh(@Nullable MaterialLibrary.Material currentMat, @Nullable String currentSmoothingGroup) {
         this.mat = currentMat;
         this.smoothingGroup = currentSmoothingGroup;
      }
   }

   public class ModelGroup extends OBJModel.ModelObject {
      final Map<String, OBJModel.ModelObject> parts = Maps.newHashMap();

      ModelGroup(String name) {
         super(name);
      }

      public Collection<? extends IModelGeometryPart> getParts() {
         return this.parts.values();
      }

      public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation) {
         super.addQuads(owner, modelBuilder, bakery, spriteGetter, modelTransform, modelLocation);
         this.getParts().stream().filter((part) -> {
            return owner.getPartVisibility(part);
         }).forEach((part) -> {
            part.addQuads(owner, modelBuilder, bakery, spriteGetter, modelTransform, modelLocation);
         });
      }

      public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<com.mojang.datafixers.util.Pair<String, String>> missingTextureErrors) {
         Set<Material> combined = Sets.newHashSet();
         combined.addAll(super.getTextures(owner, modelGetter, missingTextureErrors));
         Iterator var5 = this.getParts().iterator();

         while(var5.hasNext()) {
            IModelGeometryPart part = (IModelGeometryPart)var5.next();
            combined.addAll(part.getTextures(owner, modelGetter, missingTextureErrors));
         }

         return combined;
      }
   }

   public class ModelObject implements IModelGeometryPart {
      public final String name;
      List<OBJModel.ModelMesh> meshes = Lists.newArrayList();

      ModelObject(String name) {
         this.name = name;
      }

      public String name() {
         return this.name;
      }

      public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation) {
         Iterator var7 = this.meshes.iterator();

         while(true) {
            OBJModel.ModelMesh mesh;
            MaterialLibrary.Material mat;
            do {
               if (!var7.hasNext()) {
                  return;
               }

               mesh = (OBJModel.ModelMesh)var7.next();
               mat = mesh.mat;
            } while(mat == null);

            TextureAtlasSprite texture = (TextureAtlasSprite)spriteGetter.apply(ModelLoaderRegistry.resolveTexture(mat.diffuseColorMap, owner));
            int tintIndex = mat.diffuseTintIndex;
            Vector4f colorTint = mat.diffuseColor;
            Iterator var13 = mesh.faces.iterator();

            while(var13.hasNext()) {
               int[][] face = (int[][])var13.next();
               Pair<BakedQuad, Direction> quad = OBJModel.this.makeQuad(face, tintIndex, colorTint, mat.ambientColor, texture, modelTransform.func_225615_b_());
               if (quad.getRight() == null) {
                  modelBuilder.addGeneralQuad((BakedQuad)quad.getLeft());
               } else {
                  modelBuilder.addFaceQuad((Direction)quad.getRight(), (BakedQuad)quad.getLeft());
               }
            }
         }
      }

      public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<com.mojang.datafixers.util.Pair<String, String>> missingTextureErrors) {
         return (Collection)this.meshes.stream().map((mesh) -> {
            return ModelLoaderRegistry.resolveTexture(mesh.mat.diffuseColorMap, owner);
         }).collect(Collectors.toSet());
      }
   }
}
