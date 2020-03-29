package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.IMultipartModelGeometry;

public class CompositeModel implements IDynamicBakedModel {
   public static final ModelProperty<CompositeModel.SubmodelModelData> SUBMODEL_DATA = new ModelProperty();
   private final ImmutableMap<String, IBakedModel> bakedParts;
   private final boolean isAmbientOcclusion;
   private final boolean isGui3d;
   private final TextureAtlasSprite particle;
   private final ItemOverrideList overrides;
   private final IModelTransform transforms;

   public CompositeModel(boolean isGui3d, boolean isAmbientOcclusion, TextureAtlasSprite particle, ImmutableMap<String, IBakedModel> bakedParts, IModelTransform combinedTransform, ItemOverrideList overrides) {
      this.bakedParts = bakedParts;
      this.isAmbientOcclusion = isAmbientOcclusion;
      this.isGui3d = isGui3d;
      this.particle = particle;
      this.overrides = overrides;
      this.transforms = combinedTransform;
   }

   @Nonnull
   public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
      List<BakedQuad> quads = new ArrayList();
      UnmodifiableIterator var6 = this.bakedParts.entrySet().iterator();

      while(var6.hasNext()) {
         Entry<String, IBakedModel> entry = (Entry)var6.next();
         quads.addAll(((IBakedModel)entry.getValue()).getQuads(state, side, rand, this.getSubmodelData(extraData, (String)entry.getKey())));
      }

      return quads;
   }

   public boolean isAmbientOcclusion() {
      return this.isAmbientOcclusion;
   }

   public boolean isGui3d() {
      return this.isGui3d;
   }

   public boolean func_230044_c_() {
      return false;
   }

   public boolean isBuiltInRenderer() {
      return false;
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.particle;
   }

   public ItemOverrideList getOverrides() {
      return this.overrides;
   }

   public boolean doesHandlePerspectives() {
      return true;
   }

   public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
      return PerspectiveMapWrapper.handlePerspective(this, (IModelTransform)this.transforms, cameraTransformType, mat);
   }

   @Nullable
   public IBakedModel getPart(String name) {
      return (IBakedModel)this.bakedParts.get(name);
   }

   private IModelData getSubmodelData(IModelData extraData, String name) {
      CompositeModel.SubmodelModelData data = (CompositeModel.SubmodelModelData)extraData.getData(SUBMODEL_DATA);
      return (IModelData)(data == null ? EmptyModelData.INSTANCE : data.getSubmodelData(name));
   }

   public static class Loader implements IModelLoader<CompositeModel.Geometry> {
      public static final CompositeModel.Loader INSTANCE = new CompositeModel.Loader();

      private Loader() {
      }

      public void onResourceManagerReload(IResourceManager resourceManager) {
      }

      public CompositeModel.Geometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
         if (!modelContents.has("parts")) {
            throw new RuntimeException("Composite model requires a \"parts\" element.");
         } else {
            Builder<String, CompositeModel.Submodel> parts = ImmutableMap.builder();
            Iterator var4 = modelContents.get("parts").getAsJsonObject().entrySet().iterator();

            while(var4.hasNext()) {
               Entry<String, JsonElement> part = (Entry)var4.next();
               IModelTransform modelTransform = SimpleModelTransform.IDENTITY;
               parts.put(part.getKey(), new CompositeModel.Submodel((String)part.getKey(), (BlockModel)deserializationContext.deserialize((JsonElement)part.getValue(), BlockModel.class), modelTransform));
            }

            return new CompositeModel.Geometry(parts.build());
         }
      }
   }

   public static class Geometry implements IMultipartModelGeometry<CompositeModel.Geometry> {
      private final ImmutableMap<String, CompositeModel.Submodel> parts;

      Geometry(ImmutableMap<String, CompositeModel.Submodel> parts) {
         this.parts = parts;
      }

      public Collection<? extends IModelGeometryPart> getParts() {
         return this.parts.values();
      }

      public Optional<? extends IModelGeometryPart> getPart(String name) {
         return Optional.ofNullable(this.parts.get(name));
      }

      public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
         Material particleLocation = owner.resolveTexture("particle");
         TextureAtlasSprite particle = (TextureAtlasSprite)spriteGetter.apply(particleLocation);
         Builder<String, IBakedModel> bakedParts = ImmutableMap.builder();
         UnmodifiableIterator var10 = this.parts.entrySet().iterator();

         while(var10.hasNext()) {
            Entry<String, CompositeModel.Submodel> part = (Entry)var10.next();
            CompositeModel.Submodel submodel = (CompositeModel.Submodel)part.getValue();
            if (owner.getPartVisibility(submodel)) {
               bakedParts.put(part.getKey(), submodel.func_225613_a_(bakery, spriteGetter, modelTransform, modelLocation));
            }
         }

         return new CompositeModel(owner.isShadedInGui(), owner.useSmoothLighting(), particle, bakedParts.build(), owner.getCombinedTransform(), overrides);
      }

      public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
         Set<Material> textures = new HashSet();
         UnmodifiableIterator var5 = this.parts.values().iterator();

         while(var5.hasNext()) {
            CompositeModel.Submodel part = (CompositeModel.Submodel)var5.next();
            textures.addAll(part.getTextures(owner, modelGetter, missingTextureErrors));
         }

         return textures;
      }
   }

   private static class Submodel implements IModelGeometryPart {
      private final String name;
      private final BlockModel model;
      private final IModelTransform modelTransform;

      private Submodel(String name, BlockModel model, IModelTransform modelTransform) {
         this.name = name;
         this.model = model;
         this.modelTransform = modelTransform;
      }

      public String name() {
         return this.name;
      }

      public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation) {
         throw new UnsupportedOperationException("Attempted to call adQuads on a Submodel instance. Please don't.");
      }

      public IBakedModel func_225613_a_(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation) {
         return this.model.func_225613_a_(bakery, spriteGetter, new ModelTransformComposition(this.modelTransform, modelTransform, this.modelTransform.isUvLock() || modelTransform.isUvLock()), modelLocation);
      }

      public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
         return this.model.func_225614_a_(modelGetter, missingTextureErrors);
      }

      // $FF: synthetic method
      Submodel(String x0, BlockModel x1, IModelTransform x2, Object x3) {
         this(x0, x1, x2);
      }
   }

   public static class SubmodelModelData {
      private final Map<String, IModelData> parts = new HashMap();

      public IModelData getSubmodelData(String name) {
         return (IModelData)this.parts.getOrDefault(name, EmptyModelData.INSTANCE);
      }

      public void putSubmodelData(String name, IModelData data) {
         this.parts.put(name, data);
      }
   }
}
