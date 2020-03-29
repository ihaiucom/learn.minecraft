package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WeightedBakedModel implements IBakedModel {
   private final int totalWeight;
   private final List<WeightedBakedModel.WeightedModel> models;
   private final IBakedModel baseModel;

   public WeightedBakedModel(List<WeightedBakedModel.WeightedModel> p_i46073_1_) {
      this.models = p_i46073_1_;
      this.totalWeight = WeightedRandom.getTotalWeight(p_i46073_1_);
      this.baseModel = ((WeightedBakedModel.WeightedModel)p_i46073_1_.get(0)).model;
   }

   public List<BakedQuad> getQuads(@Nullable BlockState p_200117_1_, @Nullable Direction p_200117_2_, Random p_200117_3_) {
      return ((WeightedBakedModel.WeightedModel)WeightedRandom.getRandomItem(this.models, Math.abs((int)p_200117_3_.nextLong()) % this.totalWeight)).model.getQuads(p_200117_1_, p_200117_2_, p_200117_3_);
   }

   public boolean isAmbientOcclusion() {
      return this.baseModel.isAmbientOcclusion();
   }

   public boolean isAmbientOcclusion(BlockState p_isAmbientOcclusion_1_) {
      return this.baseModel.isAmbientOcclusion(p_isAmbientOcclusion_1_);
   }

   public boolean isGui3d() {
      return this.baseModel.isGui3d();
   }

   public boolean func_230044_c_() {
      return this.baseModel.func_230044_c_();
   }

   public boolean isBuiltInRenderer() {
      return this.baseModel.isBuiltInRenderer();
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.baseModel.getParticleTexture();
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.baseModel.getItemCameraTransforms();
   }

   public ItemOverrideList getOverrides() {
      return this.baseModel.getOverrides();
   }

   @OnlyIn(Dist.CLIENT)
   static class WeightedModel extends WeightedRandom.Item {
      protected final IBakedModel model;

      public WeightedModel(IBakedModel p_i46763_1_, int p_i46763_2_) {
         super(p_i46763_2_);
         this.model = p_i46763_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<WeightedBakedModel.WeightedModel> listItems = Lists.newArrayList();

      public WeightedBakedModel.Builder add(@Nullable IBakedModel p_177677_1_, int p_177677_2_) {
         if (p_177677_1_ != null) {
            this.listItems.add(new WeightedBakedModel.WeightedModel(p_177677_1_, p_177677_2_));
         }

         return this;
      }

      @Nullable
      public IBakedModel build() {
         if (this.listItems.isEmpty()) {
            return null;
         } else {
            return (IBakedModel)(this.listItems.size() == 1 ? ((WeightedBakedModel.WeightedModel)this.listItems.get(0)).model : new WeightedBakedModel(this.listItems));
         }
      }
   }
}
