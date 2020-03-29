package net.minecraftforge.client.model;

import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;

public class BlockModelConfiguration implements IModelConfiguration {
   public final BlockModel owner;
   public final BlockModelConfiguration.VisibilityData visibilityData = new BlockModelConfiguration.VisibilityData();
   @Nullable
   private IModelGeometry<?> customGeometry;
   @Nullable
   private IModelTransform customModelState;

   public BlockModelConfiguration(BlockModel owner) {
      this.owner = owner;
   }

   @Nullable
   public IUnbakedModel getOwnerModel() {
      return this.owner;
   }

   public String getModelName() {
      return this.owner.name;
   }

   public boolean hasCustomGeometry() {
      return this.getCustomGeometry() != null;
   }

   @Nullable
   public IModelGeometry<?> getCustomGeometry() {
      return this.owner.parent != null && this.customGeometry == null ? this.owner.parent.customData.getCustomGeometry() : this.customGeometry;
   }

   public void setCustomGeometry(IModelGeometry<?> geometry) {
      this.customGeometry = geometry;
   }

   @Nullable
   public IModelTransform getCustomModelState() {
      return this.owner.parent != null && this.customModelState == null ? this.owner.parent.customData.getCustomModelState() : this.customModelState;
   }

   public void setCustomModelState(IModelTransform modelState) {
      this.customModelState = modelState;
   }

   public boolean getPartVisibility(IModelGeometryPart part, boolean fallback) {
      return this.owner.parent != null && !this.visibilityData.hasCustomVisibility(part) ? this.owner.parent.customData.getPartVisibility(part, fallback) : this.visibilityData.isVisible(part, fallback);
   }

   public boolean isTexturePresent(String name) {
      return this.owner.isTexturePresent(name);
   }

   public Material resolveTexture(String name) {
      return this.owner.func_228816_c_(name);
   }

   public boolean isShadedInGui() {
      return true;
   }

   public boolean isSideLit() {
      return this.owner.func_230176_c_().func_230178_a_();
   }

   public boolean useSmoothLighting() {
      return this.owner.isAmbientOcclusion();
   }

   public ItemCameraTransforms getCameraTransforms() {
      return this.owner.getAllTransforms();
   }

   public IModelTransform getCombinedTransform() {
      IModelTransform state = this.getCustomModelState();
      return state != null ? new SimpleModelTransform(PerspectiveMapWrapper.getTransformsWithFallback(state, this.getCameraTransforms()), state.func_225615_b_()) : new SimpleModelTransform(PerspectiveMapWrapper.getTransforms(this.getCameraTransforms()));
   }

   public void copyFrom(BlockModelConfiguration other) {
      this.customGeometry = other.customGeometry;
      this.customModelState = other.customModelState;
      this.visibilityData.copyFrom(other.visibilityData);
   }

   public Collection<Material> getTextureDependencies(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
      IModelGeometry<?> geometry = this.getCustomGeometry();
      return (Collection)(geometry == null ? Collections.emptySet() : geometry.getTextures(this, modelGetter, missingTextureErrors));
   }

   public IBakedModel bake(ModelBakery bakery, Function<Material, TextureAtlasSprite> bakedTextureGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
      IModelGeometry<?> geometry = this.getCustomGeometry();
      if (geometry == null) {
         throw new IllegalStateException("Can not use custom baking without custom geometry");
      } else {
         return geometry.bake(this, bakery, bakedTextureGetter, modelTransform, overrides, modelLocation);
      }
   }

   public static class VisibilityData {
      private final Map<String, Boolean> data = new HashMap();

      public boolean hasCustomVisibility(IModelGeometryPart part) {
         return this.data.containsKey(part.name());
      }

      public boolean isVisible(IModelGeometryPart part, boolean fallback) {
         return (Boolean)this.data.getOrDefault(part.name(), fallback);
      }

      public void setVisibilityState(String partName, boolean type) {
         this.data.put(partName, type);
      }

      public void copyFrom(BlockModelConfiguration.VisibilityData visibilityData) {
         this.data.clear();
         this.data.putAll(visibilityData.data);
      }
   }
}
