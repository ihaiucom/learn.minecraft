package net.minecraftforge.client.model.b3d;

import com.google.common.base.Objects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoadingException;
import net.minecraftforge.client.model.ModelTransformComposition;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TransformationHelper;
import net.minecraftforge.common.model.animation.IClip;
import net.minecraftforge.common.model.animation.IJoint;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum B3DLoader implements ISelectiveResourceReloadListener {
   INSTANCE;

   private static final Logger LOGGER = LogManager.getLogger();
   private IResourceManager manager;
   private final Set<String> enabledDomains = new HashSet();
   private final Map<ResourceLocation, B3DModel> cache = new HashMap();

   public void onResourceManagerReload(IResourceManager manager, Predicate<IResourceType> resourcePredicate) {
      this.manager = manager;
      this.cache.clear();
   }

   public IUnbakedModel loadModel(ResourceLocation modelLocation) throws Exception {
      ResourceLocation file = new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath());
      if (!this.cache.containsKey(file)) {
         IResource resource = null;

         try {
            try {
               resource = this.manager.getResource(file);
            } catch (FileNotFoundException var10) {
               if (modelLocation.getPath().startsWith("models/block/")) {
                  resource = this.manager.getResource(new ResourceLocation(file.getNamespace(), "models/item/" + file.getPath().substring("models/block/".length())));
               } else {
                  if (!modelLocation.getPath().startsWith("models/item/")) {
                     throw var10;
                  }

                  resource = this.manager.getResource(new ResourceLocation(file.getNamespace(), "models/block/" + file.getPath().substring("models/item/".length())));
               }
            }

            B3DModel.Parser parser = new B3DModel.Parser(resource.getInputStream());
            B3DModel model = parser.parse();
            this.cache.put(file, model);
         } catch (IOException var11) {
            this.cache.put(file, (Object)null);
            throw var11;
         } finally {
            IOUtils.closeQuietly(resource);
         }
      }

      B3DModel model = (B3DModel)this.cache.get(file);
      if (model == null) {
         throw new ModelLoadingException("Error loading model previously: " + file);
      } else {
         return !(model.getRoot().getKind() instanceof B3DModel.Mesh) ? new B3DLoader.ModelWrapper(modelLocation, model, ImmutableSet.of(), true, true, true, 1) : new B3DLoader.ModelWrapper(modelLocation, model, ImmutableSet.of(model.getRoot().getName()), true, true, true, 1);
      }
   }

   private static final class BakedWrapper implements IDynamicBakedModel {
      private final B3DModel.Node<?> node;
      private final IModelTransform state;
      private final boolean smooth;
      private final boolean gui3d;
      private final boolean isSideLit;
      private final ImmutableSet<String> meshes;
      private final ImmutableMap<String, TextureAtlasSprite> textures;
      private final LoadingCache<Integer, B3DLoader.B3DState> cache;
      private ImmutableList<BakedQuad> quads;

      public BakedWrapper(final B3DModel.Node<?> node, final IModelTransform state, boolean smooth, boolean gui3d, boolean isSideLit, ImmutableSet<String> meshes, ImmutableMap<String, TextureAtlasSprite> textures) {
         this(node, state, smooth, gui3d, isSideLit, meshes, textures, CacheBuilder.newBuilder().maximumSize(128L).expireAfterAccess(2L, TimeUnit.MINUTES).build(new CacheLoader<Integer, B3DLoader.B3DState>() {
            public B3DLoader.B3DState load(Integer frame) throws Exception {
               IModelTransform parent = state;
               B3DModel.Animation newAnimation = node.getAnimation();
               if (parent instanceof B3DLoader.B3DState) {
                  B3DLoader.B3DState ps = (B3DLoader.B3DState)parent;
                  parent = ps.getParent();
               }

               return new B3DLoader.B3DState(newAnimation, frame, frame, 0.0F, parent);
            }
         }));
      }

      public BakedWrapper(B3DModel.Node<?> node, IModelTransform state, boolean smooth, boolean gui3d, boolean isSideLit, ImmutableSet<String> meshes, ImmutableMap<String, TextureAtlasSprite> textures, LoadingCache<Integer, B3DLoader.B3DState> cache) {
         this.node = node;
         this.state = state;
         this.smooth = smooth;
         this.gui3d = gui3d;
         this.isSideLit = isSideLit;
         this.meshes = meshes;
         this.textures = textures;
         this.cache = cache;
      }

      public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData data) {
         if (side != null) {
            return ImmutableList.of();
         } else {
            IModelTransform modelState = this.state;
            IModelTransform newState = (IModelTransform)data.getData(Properties.AnimationProperty);
            if (newState != null) {
               IModelTransform parent = this.state;
               if (parent instanceof B3DLoader.B3DState) {
                  B3DLoader.B3DState ps = (B3DLoader.B3DState)parent;
                  parent = ps.getParent();
               }

               if (parent == null) {
                  modelState = newState;
               } else {
                  modelState = new ModelTransformComposition(parent, newState);
               }
            }

            Builder builder;
            if (this.quads == null) {
               builder = ImmutableList.builder();
               this.generateQuads(builder, this.node, this.state, ImmutableList.of());
               this.quads = builder.build();
            }

            if (this.state != modelState) {
               builder = ImmutableList.builder();
               this.generateQuads(builder, this.node, (IModelTransform)modelState, ImmutableList.of());
               return builder.build();
            } else {
               return this.quads;
            }
         }
      }

      private void generateQuads(Builder<BakedQuad> builder, B3DModel.Node<?> node, final IModelTransform state, ImmutableList<String> path) {
         Builder<String> pathBuilder = ImmutableList.builder();
         pathBuilder.addAll(path);
         pathBuilder.add(node.getName());
         ImmutableList<String> newPath = pathBuilder.build();
         UnmodifiableIterator var7 = node.getNodes().values().iterator();

         while(var7.hasNext()) {
            B3DModel.Node<?> child = (B3DModel.Node)var7.next();
            this.generateQuads(builder, child, state, newPath);
         }

         if (node.getKind() instanceof B3DModel.Mesh && this.meshes.contains(node.getName()) && state.getPartTransformation(Models.getHiddenModelPart(newPath)).isIdentity()) {
            B3DModel.Mesh mesh = (B3DModel.Mesh)node.getKind();
            Collection<B3DModel.Face> faces = mesh.bake(new Function<B3DModel.Node<?>, Matrix4f>() {
               private final TransformationMatrix global = state.func_225615_b_();
               private final LoadingCache<B3DModel.Node<?>, TransformationMatrix> localCache = CacheBuilder.newBuilder().maximumSize(32L).build(new CacheLoader<B3DModel.Node<?>, TransformationMatrix>() {
                  public TransformationMatrix load(B3DModel.Node<?> node) throws Exception {
                     return state.getPartTransformation(new B3DLoader.NodeJoint(node));
                  }
               });

               public Matrix4f apply(B3DModel.Node<?> node) {
                  return this.global.compose((TransformationMatrix)this.localCache.getUnchecked(node)).func_227988_c_();
               }
            });
            Iterator var9 = faces.iterator();

            while(var9.hasNext()) {
               B3DModel.Face f = (B3DModel.Face)var9.next();
               List<B3DModel.Texture> textures = null;
               if (f.getBrush() != null) {
                  textures = f.getBrush().getTextures();
               }

               TextureAtlasSprite sprite;
               if (textures != null && !textures.isEmpty()) {
                  if (textures.get(0) == B3DModel.Texture.White) {
                     sprite = ModelLoader.White.instance();
                  } else {
                     sprite = (TextureAtlasSprite)this.textures.get(((B3DModel.Texture)textures.get(0)).getPath());
                  }
               } else {
                  sprite = (TextureAtlasSprite)this.textures.get("missingno");
               }

               BakedQuadBuilder quadBuilder = new BakedQuadBuilder(sprite);
               quadBuilder.setContractUVs(true);
               quadBuilder.setQuadOrientation(Direction.getFacingFromVector(f.getNormal().getX(), f.getNormal().getY(), f.getNormal().getZ()));
               this.putVertexData(quadBuilder, f.getV1(), f.getNormal(), sprite);
               this.putVertexData(quadBuilder, f.getV2(), f.getNormal(), sprite);
               this.putVertexData(quadBuilder, f.getV3(), f.getNormal(), sprite);
               this.putVertexData(quadBuilder, f.getV3(), f.getNormal(), sprite);
               builder.add(quadBuilder.build());
            }
         }

      }

      private final void putVertexData(IVertexConsumer consumer, B3DModel.Vertex v, Vector3f faceNormal, TextureAtlasSprite sprite) {
         ImmutableList<VertexFormatElement> vertexFormatElements = consumer.getVertexFormat().func_227894_c_();

         for(int e = 0; e < vertexFormatElements.size(); ++e) {
            switch(((VertexFormatElement)vertexFormatElements.get(e)).getUsage()) {
            case POSITION:
               consumer.put(e, v.getPos().getX(), v.getPos().getY(), v.getPos().getZ(), 1.0F);
               break;
            case COLOR:
               if (v.getColor() != null) {
                  consumer.put(e, v.getColor().getX(), v.getColor().getY(), v.getColor().getZ(), v.getColor().getW());
               } else {
                  consumer.put(e, 1.0F, 1.0F, 1.0F, 1.0F);
               }
               break;
            case UV:
               if (((VertexFormatElement)vertexFormatElements.get(e)).getIndex() < v.getTexCoords().length) {
                  consumer.put(e, sprite.getInterpolatedU((double)(v.getTexCoords()[0].getX() * 16.0F)), sprite.getInterpolatedV((double)(v.getTexCoords()[0].getY() * 16.0F)), 0.0F, 1.0F);
               } else {
                  consumer.put(e, 0.0F, 0.0F, 0.0F, 1.0F);
               }
               break;
            case NORMAL:
               if (v.getNormal() != null) {
                  consumer.put(e, v.getNormal().getX(), v.getNormal().getY(), v.getNormal().getZ(), 0.0F);
               } else {
                  consumer.put(e, faceNormal.getX(), faceNormal.getY(), faceNormal.getZ(), 0.0F);
               }
               break;
            default:
               consumer.put(e);
            }
         }

      }

      public boolean isAmbientOcclusion() {
         return this.smooth;
      }

      public boolean isGui3d() {
         return this.gui3d;
      }

      public boolean func_230044_c_() {
         return this.isSideLit;
      }

      public boolean isBuiltInRenderer() {
         return false;
      }

      public TextureAtlasSprite getParticleTexture() {
         return (TextureAtlasSprite)this.textures.values().asList().get(0);
      }

      public boolean doesHandlePerspectives() {
         return true;
      }

      public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
         return PerspectiveMapWrapper.handlePerspective(this, (IModelTransform)this.state, cameraTransformType, mat);
      }

      public ItemOverrideList getOverrides() {
         return ItemOverrideList.EMPTY;
      }
   }

   private static final class ModelWrapper implements IUnbakedModel {
      private final ResourceLocation modelLocation;
      private final B3DModel model;
      private final ImmutableSet<String> meshes;
      private final ImmutableMap<String, String> textures;
      private final boolean smooth;
      private final boolean gui3d;
      private final boolean isSideLit;
      private final int defaultKey;

      public ModelWrapper(ResourceLocation modelLocation, B3DModel model, ImmutableSet<String> meshes, boolean smooth, boolean gui3d, boolean isSideLit, int defaultKey) {
         this(modelLocation, model, meshes, smooth, gui3d, isSideLit, defaultKey, buildTextures(model.getTextures()));
      }

      public ModelWrapper(ResourceLocation modelLocation, B3DModel model, ImmutableSet<String> meshes, boolean smooth, boolean gui3d, boolean isSideLit, int defaultKey, ImmutableMap<String, String> textures) {
         this.modelLocation = modelLocation;
         this.model = model;
         this.meshes = meshes;
         this.isSideLit = isSideLit;
         this.textures = textures;
         this.smooth = smooth;
         this.gui3d = gui3d;
         this.defaultKey = defaultKey;
      }

      private static ImmutableMap<String, String> buildTextures(List<B3DModel.Texture> textures) {
         com.google.common.collect.ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

         String path;
         String location;
         for(Iterator var2 = textures.iterator(); var2.hasNext(); builder.put(path, location)) {
            B3DModel.Texture t = (B3DModel.Texture)var2.next();
            path = t.getPath();
            location = getLocation(path);
            if (!location.startsWith("#")) {
               location = "#" + location;
            }
         }

         return builder.build();
      }

      private static String getLocation(String path) {
         if (path.endsWith(".png")) {
            path = path.substring(0, path.length() - ".png".length());
         }

         return path;
      }

      public Collection<Material> func_225614_a_(Function<ResourceLocation, IUnbakedModel> p_225614_1_, Set<Pair<String, String>> p_225614_2_) {
         return (Collection)this.textures.values().stream().filter((loc) -> {
            return !loc.startsWith("#");
         }).map((t) -> {
            return new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(t));
         }).collect(Collectors.toList());
      }

      public Collection<ResourceLocation> getDependencies() {
         return Collections.emptyList();
      }

      @Nullable
      public IBakedModel func_225613_a_(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation) {
         com.google.common.collect.ImmutableMap.Builder<String, TextureAtlasSprite> builder = ImmutableMap.builder();
         TextureAtlasSprite missing = (TextureAtlasSprite)spriteGetter.apply(new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, MissingTextureSprite.getLocation()));
         UnmodifiableIterator var7 = this.textures.entrySet().iterator();

         while(var7.hasNext()) {
            Entry<String, String> e = (Entry)var7.next();
            if (((String)e.getValue()).startsWith("#")) {
               B3DLoader.LOGGER.fatal("unresolved texture '{}' for b3d model '{}'", e.getValue(), this.modelLocation);
               builder.put(e.getKey(), missing);
            } else {
               builder.put(e.getKey(), spriteGetter.apply(new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation((String)e.getValue()))));
            }
         }

         builder.put("missingno", missing);
         return new B3DLoader.BakedWrapper(this.model.getRoot(), modelTransform, this.smooth, this.gui3d, this.isSideLit, this.meshes, builder.build());
      }

      public B3DLoader.ModelWrapper retexture(ImmutableMap<String, String> textures) {
         com.google.common.collect.ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
         UnmodifiableIterator var3 = this.textures.entrySet().iterator();

         while(true) {
            while(var3.hasNext()) {
               Entry<String, String> e = (Entry)var3.next();
               String path = (String)e.getKey();
               String loc = getLocation(path);
               if (loc.startsWith("#") && (textures.containsKey(loc) || textures.containsKey(loc.substring(1)))) {
                  String alt = loc.substring(1);
                  String newLoc = (String)textures.get(loc);
                  if (newLoc == null) {
                     newLoc = (String)textures.get(alt);
                  }

                  if (newLoc == null) {
                     newLoc = path.substring(1);
                  }

                  builder.put(e.getKey(), newLoc);
               } else {
                  builder.put(e);
               }
            }

            return new B3DLoader.ModelWrapper(this.modelLocation, this.model, this.meshes, this.smooth, this.gui3d, this.isSideLit, this.defaultKey, builder.build());
         }
      }

      public B3DLoader.ModelWrapper process(ImmutableMap<String, String> data) {
         ImmutableSet<String> newMeshes = this.meshes;
         int newDefaultKey = this.defaultKey;
         boolean hasChanged = false;
         JsonElement e;
         if (data.containsKey("mesh")) {
            e = (new JsonParser()).parse((String)data.get("mesh"));
            if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()) {
               return new B3DLoader.ModelWrapper(this.modelLocation, this.model, ImmutableSet.of(e.getAsString()), this.smooth, this.gui3d, this.isSideLit, this.defaultKey, this.textures);
            }

            if (!e.isJsonArray()) {
               B3DLoader.LOGGER.fatal("unknown mesh definition '{}' for b3d model '{}'", e.toString(), this.modelLocation);
               return this;
            }

            com.google.common.collect.ImmutableSet.Builder<String> builder = ImmutableSet.builder();
            Iterator var7 = e.getAsJsonArray().iterator();

            while(true) {
               if (!var7.hasNext()) {
                  newMeshes = builder.build();
                  hasChanged = true;
                  break;
               }

               JsonElement s = (JsonElement)var7.next();
               if (!s.isJsonPrimitive() || !s.getAsJsonPrimitive().isString()) {
                  B3DLoader.LOGGER.fatal("unknown mesh definition '{}' in array for b3d model '{}'", s.toString(), this.modelLocation);
                  return this;
               }

               builder.add(s.getAsString());
            }
         }

         if (data.containsKey("key")) {
            e = (new JsonParser()).parse((String)data.get("key"));
            if (!e.isJsonPrimitive() || !e.getAsJsonPrimitive().isNumber()) {
               B3DLoader.LOGGER.fatal("unknown keyframe definition '{}' for b3d model '{}'", e.toString(), this.modelLocation);
               return this;
            }

            newDefaultKey = e.getAsNumber().intValue();
            hasChanged = true;
         }

         return hasChanged ? new B3DLoader.ModelWrapper(this.modelLocation, this.model, newMeshes, this.smooth, this.gui3d, this.isSideLit, newDefaultKey, this.textures) : this;
      }

      public Optional<IClip> getClip(String name) {
         return name.equals("main") ? Optional.of(B3DClip.INSTANCE) : Optional.empty();
      }

      public IModelTransform getDefaultState() {
         return new B3DLoader.B3DState(this.model.getRoot().getAnimation(), this.defaultKey, this.defaultKey, 0.0F);
      }

      public B3DLoader.ModelWrapper smoothLighting(boolean value) {
         return value == this.smooth ? this : new B3DLoader.ModelWrapper(this.modelLocation, this.model, this.meshes, value, this.gui3d, this.isSideLit, this.defaultKey, this.textures);
      }

      public B3DLoader.ModelWrapper gui3d(boolean value) {
         return value == this.gui3d ? this : new B3DLoader.ModelWrapper(this.modelLocation, this.model, this.meshes, this.smooth, value, this.isSideLit, this.defaultKey, this.textures);
      }
   }

   static final class NodeJoint implements IJoint {
      private final B3DModel.Node<?> node;

      public NodeJoint(B3DModel.Node<?> node) {
         this.node = node;
      }

      public TransformationMatrix getInvBindPose() {
         Matrix4f m = (new TransformationMatrix(this.node.getPos(), this.node.getRot(), this.node.getScale(), (Quaternion)null)).func_227988_c_();
         m.func_226600_c_();
         TransformationMatrix pose = new TransformationMatrix(m);
         if (this.node.getParent() != null) {
            TransformationMatrix parent = (new B3DLoader.NodeJoint(this.node.getParent())).getInvBindPose();
            pose = pose.compose(parent);
         }

         return pose;
      }

      public Optional<B3DLoader.NodeJoint> getParent() {
         return this.node.getParent() == null ? Optional.empty() : Optional.of(new B3DLoader.NodeJoint(this.node.getParent()));
      }

      public B3DModel.Node<?> getNode() {
         return this.node;
      }

      public int hashCode() {
         return this.node.hashCode();
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (!super.equals(obj)) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            B3DLoader.NodeJoint other = (B3DLoader.NodeJoint)obj;
            return Objects.equal(this.node, other.node);
         }
      }
   }

   public static final class B3DState implements IModelTransform {
      @Nullable
      private final B3DModel.Animation animation;
      private final int frame;
      private final int nextFrame;
      private final float progress;
      @Nullable
      private final IModelTransform parent;
      private static LoadingCache<Triple<B3DModel.Animation, B3DModel.Node<?>, Integer>, TransformationMatrix> cache;

      public B3DState(@Nullable B3DModel.Animation animation, int frame) {
         this(animation, frame, frame, 0.0F);
      }

      public B3DState(@Nullable B3DModel.Animation animation, int frame, IModelTransform parent) {
         this(animation, frame, frame, 0.0F, parent);
      }

      public B3DState(@Nullable B3DModel.Animation animation, int frame, int nextFrame, float progress) {
         this(animation, frame, nextFrame, progress, (IModelTransform)null);
      }

      public B3DState(@Nullable B3DModel.Animation animation, int frame, int nextFrame, float progress, @Nullable IModelTransform parent) {
         this.animation = animation;
         this.frame = frame;
         this.nextFrame = nextFrame;
         this.progress = MathHelper.clamp(progress, 0.0F, 1.0F);
         this.parent = this.getParent(parent);
      }

      @Nullable
      private IModelTransform getParent(@Nullable IModelTransform parent) {
         if (parent == null) {
            return null;
         } else {
            return parent instanceof B3DLoader.B3DState ? ((B3DLoader.B3DState)parent).parent : parent;
         }
      }

      @Nullable
      public B3DModel.Animation getAnimation() {
         return this.animation;
      }

      public int getFrame() {
         return this.frame;
      }

      public int getNextFrame() {
         return this.nextFrame;
      }

      public float getProgress() {
         return this.progress;
      }

      @Nullable
      public IModelTransform getParent() {
         return this.parent;
      }

      public TransformationMatrix func_225615_b_() {
         return this.parent != null ? this.parent.func_225615_b_() : TransformationMatrix.func_227983_a_();
      }

      public TransformationMatrix getPartTransformation(Object part) {
         if (!(part instanceof B3DLoader.NodeJoint)) {
            return TransformationMatrix.func_227983_a_();
         } else {
            B3DModel.Node<?> node = ((B3DLoader.NodeJoint)part).getNode();
            TransformationMatrix nodeTransform;
            if ((double)this.progress >= 1.0E-5D && this.frame != this.nextFrame) {
               if ((double)this.progress > 0.99999D) {
                  nodeTransform = this.getNodeMatrix(node, this.nextFrame);
               } else {
                  nodeTransform = this.getNodeMatrix(node, this.frame);
                  nodeTransform = TransformationHelper.slerp(nodeTransform, this.getNodeMatrix(node, this.nextFrame), this.progress);
               }
            } else {
               nodeTransform = this.getNodeMatrix(node, this.frame);
            }

            return this.parent != null && node.getParent() == null ? this.parent.getPartTransformation(part).compose(nodeTransform) : nodeTransform;
         }
      }

      public TransformationMatrix getNodeMatrix(B3DModel.Node<?> node) {
         return this.getNodeMatrix(node, this.frame);
      }

      public TransformationMatrix getNodeMatrix(B3DModel.Node<?> node, int frame) {
         return (TransformationMatrix)cache.getUnchecked(Triple.of(this.animation, node, frame));
      }

      public static TransformationMatrix getNodeMatrix(@Nullable B3DModel.Animation animation, B3DModel.Node<?> node, int frame) {
         TransformationMatrix ret = TransformationMatrix.func_227983_a_();
         B3DModel.Key key = null;
         if (animation != null) {
            key = (B3DModel.Key)animation.getKeys().get(frame, node);
         } else if (node.getAnimation() != null) {
            key = (B3DModel.Key)node.getAnimation().getKeys().get(frame, node);
         }

         B3DModel.Node parent;
         TransformationMatrix invBind;
         if (key != null) {
            parent = node.getParent();
            if (parent != null) {
               invBind = (TransformationMatrix)cache.getUnchecked(Triple.of(animation, node.getParent(), frame));
               ret = ret.compose(invBind);
               ret = ret.compose(new TransformationMatrix(parent.getPos(), parent.getRot(), parent.getScale(), (Quaternion)null));
            }

            ret = ret.compose(new TransformationMatrix(key.getPos(), key.getRot(), key.getScale(), (Quaternion)null));
            invBind = (new B3DLoader.NodeJoint(node)).getInvBindPose();
            ret = ret.compose(invBind);
         } else {
            parent = node.getParent();
            if (parent != null) {
               invBind = (TransformationMatrix)cache.getUnchecked(Triple.of(animation, node.getParent(), frame));
               ret = ret.compose(invBind);
               ret = ret.compose(new TransformationMatrix(parent.getPos(), parent.getRot(), parent.getScale(), (Quaternion)null));
            }

            ret = ret.compose(new TransformationMatrix(node.getPos(), node.getRot(), node.getScale(), (Quaternion)null));
            invBind = (new B3DLoader.NodeJoint(node)).getInvBindPose();
            ret = ret.compose(invBind);
         }

         return ret;
      }

      static {
         cache = CacheBuilder.newBuilder().maximumSize(16384L).expireAfterAccess(2L, TimeUnit.MINUTES).build(new CacheLoader<Triple<B3DModel.Animation, B3DModel.Node<?>, Integer>, TransformationMatrix>() {
            public TransformationMatrix load(Triple<B3DModel.Animation, B3DModel.Node<?>, Integer> key) throws Exception {
               return B3DLoader.B3DState.getNodeMatrix((B3DModel.Animation)key.getLeft(), (B3DModel.Node)key.getMiddle(), (Integer)key.getRight());
            }
         });
      }
   }
}
