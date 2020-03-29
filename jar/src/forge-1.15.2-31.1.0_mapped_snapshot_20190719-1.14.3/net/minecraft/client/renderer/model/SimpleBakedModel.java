package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelConfiguration;

@OnlyIn(Dist.CLIENT)
public class SimpleBakedModel implements IBakedModel {
   protected final List<BakedQuad> generalQuads;
   protected final Map<Direction, List<BakedQuad>> faceQuads;
   protected final boolean ambientOcclusion;
   protected final boolean gui3d;
   protected final boolean field_230186_e_;
   protected final TextureAtlasSprite texture;
   protected final ItemCameraTransforms cameraTransforms;
   protected final ItemOverrideList itemOverrideList;

   public SimpleBakedModel(List<BakedQuad> p_i230059_1_, Map<Direction, List<BakedQuad>> p_i230059_2_, boolean p_i230059_3_, boolean p_i230059_4_, boolean p_i230059_5_, TextureAtlasSprite p_i230059_6_, ItemCameraTransforms p_i230059_7_, ItemOverrideList p_i230059_8_) {
      this.generalQuads = p_i230059_1_;
      this.faceQuads = p_i230059_2_;
      this.ambientOcclusion = p_i230059_3_;
      this.gui3d = p_i230059_5_;
      this.field_230186_e_ = p_i230059_4_;
      this.texture = p_i230059_6_;
      this.cameraTransforms = p_i230059_7_;
      this.itemOverrideList = p_i230059_8_;
   }

   public List<BakedQuad> getQuads(@Nullable BlockState p_200117_1_, @Nullable Direction p_200117_2_, Random p_200117_3_) {
      return p_200117_2_ == null ? this.generalQuads : (List)this.faceQuads.get(p_200117_2_);
   }

   public boolean isAmbientOcclusion() {
      return this.ambientOcclusion;
   }

   public boolean isGui3d() {
      return this.gui3d;
   }

   public boolean func_230044_c_() {
      return this.field_230186_e_;
   }

   public boolean isBuiltInRenderer() {
      return false;
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.texture;
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.cameraTransforms;
   }

   public ItemOverrideList getOverrides() {
      return this.itemOverrideList;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<BakedQuad> builderGeneralQuads;
      private final Map<Direction, List<BakedQuad>> builderFaceQuads;
      private final ItemOverrideList builderItemOverrideList;
      private final boolean builderAmbientOcclusion;
      private TextureAtlasSprite builderTexture;
      private final boolean field_230187_f_;
      private final boolean builderGui3d;
      private final ItemCameraTransforms builderCameraTransforms;

      public Builder(IModelConfiguration p_i230080_1_, ItemOverrideList p_i230080_2_) {
         this(p_i230080_1_.useSmoothLighting(), p_i230080_1_.isShadedInGui(), p_i230080_1_.isSideLit(), p_i230080_1_.getCameraTransforms(), p_i230080_2_);
      }

      public Builder(BlockModel p_i230060_1_, ItemOverrideList p_i230060_2_, boolean p_i230060_3_) {
         this(p_i230060_1_.isAmbientOcclusion(), p_i230060_1_.func_230176_c_().func_230178_a_(), p_i230060_3_, p_i230060_1_.getAllTransforms(), p_i230060_2_);
      }

      private Builder(boolean p_i230061_1_, boolean p_i230061_2_, boolean p_i230061_3_, ItemCameraTransforms p_i230061_4_, ItemOverrideList p_i230061_5_) {
         this.builderGeneralQuads = Lists.newArrayList();
         this.builderFaceQuads = Maps.newEnumMap(Direction.class);
         Direction[] var6 = Direction.values();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Direction direction = var6[var8];
            this.builderFaceQuads.put(direction, Lists.newArrayList());
         }

         this.builderItemOverrideList = p_i230061_5_;
         this.builderAmbientOcclusion = p_i230061_1_;
         this.field_230187_f_ = p_i230061_2_;
         this.builderGui3d = p_i230061_3_;
         this.builderCameraTransforms = p_i230061_4_;
      }

      public SimpleBakedModel.Builder addFaceQuad(Direction p_177650_1_, BakedQuad p_177650_2_) {
         ((List)this.builderFaceQuads.get(p_177650_1_)).add(p_177650_2_);
         return this;
      }

      public SimpleBakedModel.Builder addGeneralQuad(BakedQuad p_177648_1_) {
         this.builderGeneralQuads.add(p_177648_1_);
         return this;
      }

      public SimpleBakedModel.Builder setTexture(TextureAtlasSprite p_177646_1_) {
         this.builderTexture = p_177646_1_;
         return this;
      }

      public IBakedModel build() {
         if (this.builderTexture == null) {
            throw new RuntimeException("Missing particle!");
         } else {
            return new SimpleBakedModel(this.builderGeneralQuads, this.builderFaceQuads, this.builderAmbientOcclusion, this.field_230187_f_, this.builderGui3d, this.builderTexture, this.builderCameraTransforms, this.builderItemOverrideList);
         }
      }
   }
}
