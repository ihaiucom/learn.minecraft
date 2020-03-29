package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

@OnlyIn(Dist.CLIENT)
public class MultipartBakedModel implements IBakedModel {
   private final List<Pair<Predicate<BlockState>, IBakedModel>> selectors;
   protected final boolean ambientOcclusion;
   protected final boolean gui3D;
   protected final boolean field_230185_c_;
   protected final TextureAtlasSprite particleTexture;
   protected final ItemCameraTransforms cameraTransforms;
   protected final ItemOverrideList overrides;
   private final Map<BlockState, BitSet> field_210277_g = new Object2ObjectOpenCustomHashMap(Util.identityHashStrategy());

   public MultipartBakedModel(List<Pair<Predicate<BlockState>, IBakedModel>> p_i48273_1_) {
      this.selectors = p_i48273_1_;
      IBakedModel lvt_2_1_ = (IBakedModel)((Pair)p_i48273_1_.iterator().next()).getRight();
      this.ambientOcclusion = lvt_2_1_.isAmbientOcclusion();
      this.gui3D = lvt_2_1_.isGui3d();
      this.field_230185_c_ = lvt_2_1_.func_230044_c_();
      this.particleTexture = lvt_2_1_.getParticleTexture();
      this.cameraTransforms = lvt_2_1_.getItemCameraTransforms();
      this.overrides = lvt_2_1_.getOverrides();
   }

   public List<BakedQuad> getQuads(@Nullable BlockState p_200117_1_, @Nullable Direction p_200117_2_, Random p_200117_3_) {
      if (p_200117_1_ == null) {
         return Collections.emptyList();
      } else {
         BitSet lvt_4_1_ = (BitSet)this.field_210277_g.get(p_200117_1_);
         if (lvt_4_1_ == null) {
            lvt_4_1_ = new BitSet();

            for(int lvt_5_1_ = 0; lvt_5_1_ < this.selectors.size(); ++lvt_5_1_) {
               Pair<Predicate<BlockState>, IBakedModel> lvt_6_1_ = (Pair)this.selectors.get(lvt_5_1_);
               if (((Predicate)lvt_6_1_.getLeft()).test(p_200117_1_)) {
                  lvt_4_1_.set(lvt_5_1_);
               }
            }

            this.field_210277_g.put(p_200117_1_, lvt_4_1_);
         }

         List<BakedQuad> lvt_5_2_ = Lists.newArrayList();
         long lvt_6_2_ = p_200117_3_.nextLong();

         for(int lvt_8_1_ = 0; lvt_8_1_ < lvt_4_1_.length(); ++lvt_8_1_) {
            if (lvt_4_1_.get(lvt_8_1_)) {
               lvt_5_2_.addAll(((IBakedModel)((Pair)this.selectors.get(lvt_8_1_)).getRight()).getQuads(p_200117_1_, p_200117_2_, new Random(lvt_6_2_)));
            }
         }

         return lvt_5_2_;
      }
   }

   public boolean isAmbientOcclusion() {
      return this.ambientOcclusion;
   }

   public boolean isGui3d() {
      return this.gui3D;
   }

   public boolean func_230044_c_() {
      return this.field_230185_c_;
   }

   public boolean isBuiltInRenderer() {
      return false;
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.particleTexture;
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.cameraTransforms;
   }

   public ItemOverrideList getOverrides() {
      return this.overrides;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<Pair<Predicate<BlockState>, IBakedModel>> selectors = Lists.newArrayList();

      public void putModel(Predicate<BlockState> p_188648_1_, IBakedModel p_188648_2_) {
         this.selectors.add(Pair.of(p_188648_1_, p_188648_2_));
      }

      public IBakedModel build() {
         return new MultipartBakedModel(this.selectors);
      }
   }
}
