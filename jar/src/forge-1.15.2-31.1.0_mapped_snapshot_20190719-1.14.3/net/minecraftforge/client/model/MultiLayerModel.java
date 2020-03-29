package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.TransformationMatrix;
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
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class MultiLayerModel implements IModelGeometry<MultiLayerModel> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ImmutableMap<RenderType, IUnbakedModel> models;

   public MultiLayerModel(ImmutableMap<RenderType, IUnbakedModel> models) {
      this.models = models;
   }

   public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
      Set<Material> materials = Sets.newHashSet();
      materials.add(owner.resolveTexture("particle"));
      UnmodifiableIterator var5 = this.models.values().iterator();

      while(var5.hasNext()) {
         IUnbakedModel m = (IUnbakedModel)var5.next();
         materials.addAll(m.func_225614_a_(modelGetter, missingTextureErrors));
      }

      return materials;
   }

   private static ImmutableMap<RenderType, IBakedModel> buildModels(ImmutableMap<RenderType, IUnbakedModel> models, IModelTransform modelTransform, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ResourceLocation modelLocation) {
      Builder<RenderType, IBakedModel> builder = ImmutableMap.builder();
      UnmodifiableIterator var6 = models.entrySet().iterator();

      while(var6.hasNext()) {
         Entry<RenderType, IUnbakedModel> entry = (Entry)var6.next();
         builder.put(entry.getKey(), ((IUnbakedModel)entry.getValue()).func_225613_a_(bakery, spriteGetter, modelTransform, modelLocation));
      }

      return builder.build();
   }

   public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
      IUnbakedModel missing = ModelLoader.instance().getMissingModel();
      return new MultiLayerModel.MultiLayerBakedModel(owner.useSmoothLighting(), owner.isShadedInGui(), owner.isSideLit(), (TextureAtlasSprite)spriteGetter.apply(owner.resolveTexture("particle")), overrides, buildModels(this.models, modelTransform, bakery, spriteGetter, modelLocation), missing.func_225613_a_(bakery, spriteGetter, modelTransform, modelLocation), PerspectiveMapWrapper.getTransforms((IModelTransform)(new ModelTransformComposition(owner.getCombinedTransform(), modelTransform))));
   }

   public static final class Loader implements IModelLoader<MultiLayerModel> {
      public static final MultiLayerModel.Loader INSTANCE = new MultiLayerModel.Loader();

      private Loader() {
      }

      public void onResourceManagerReload(IResourceManager resourceManager) {
      }

      public MultiLayerModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
         Builder<RenderType, IUnbakedModel> builder = ImmutableMap.builder();
         JsonObject layersObject = JSONUtils.getJsonObject(modelContents, "layers");
         Iterator var5 = RenderType.func_228661_n_().iterator();

         while(var5.hasNext()) {
            RenderType layer = (RenderType)var5.next();
            String layerName = layer.toString();
            if (layersObject.has(layerName)) {
               builder.put(layer, deserializationContext.deserialize(JSONUtils.getJsonObject(layersObject, layerName), BlockModel.class));
            }
         }

         ImmutableMap<RenderType, IUnbakedModel> models = builder.build();
         return new MultiLayerModel(models);
      }
   }

   private static final class MultiLayerBakedModel implements IBakedModel {
      private final ImmutableMap<RenderType, IBakedModel> models;
      private final ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> cameraTransforms;
      protected final boolean ambientOcclusion;
      protected final boolean gui3d;
      protected final boolean isSideLit;
      protected final TextureAtlasSprite particle;
      protected final ItemOverrideList overrides;
      private final IBakedModel missing;

      public MultiLayerBakedModel(boolean ambientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrideList overrides, ImmutableMap<RenderType, IBakedModel> models, IBakedModel missing, ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> cameraTransforms) {
         this.isSideLit = isSideLit;
         this.models = models;
         this.cameraTransforms = cameraTransforms;
         this.missing = missing;
         this.ambientOcclusion = ambientOcclusion;
         this.gui3d = isGui3d;
         this.particle = particle;
         this.overrides = overrides;
      }

      public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
         return this.getQuads(state, side, rand, EmptyModelData.INSTANCE);
      }

      @Nonnull
      public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
         RenderType layer = MinecraftForgeClient.getRenderLayer();
         if (layer != null) {
            return ((IBakedModel)this.models.getOrDefault(layer, this.missing)).getQuads(state, side, rand, extraData);
         } else {
            com.google.common.collect.ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
            UnmodifiableIterator var7 = this.models.values().iterator();

            while(var7.hasNext()) {
               IBakedModel model = (IBakedModel)var7.next();
               builder.addAll(model.getQuads(state, side, rand));
            }

            return builder.build();
         }
      }

      public boolean isAmbientOcclusion() {
         return this.ambientOcclusion;
      }

      public boolean isAmbientOcclusion(BlockState state) {
         return this.ambientOcclusion;
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
         return this.particle;
      }

      public boolean doesHandlePerspectives() {
         return true;
      }

      public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
         return PerspectiveMapWrapper.handlePerspective(this, (ImmutableMap)this.cameraTransforms, cameraTransformType, mat);
      }

      public ItemOverrideList getOverrides() {
         return ItemOverrideList.EMPTY;
      }
   }
}
