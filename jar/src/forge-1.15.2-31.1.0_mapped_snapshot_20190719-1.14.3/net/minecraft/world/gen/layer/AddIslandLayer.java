package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IBishopTransformer;

public enum AddIslandLayer implements IBishopTransformer {
   INSTANCE;

   public int apply(INoiseRandom p_202792_1_, int p_202792_2_, int p_202792_3_, int p_202792_4_, int p_202792_5_, int p_202792_6_) {
      if (!LayerUtil.isShallowOcean(p_202792_6_) || LayerUtil.isShallowOcean(p_202792_5_) && LayerUtil.isShallowOcean(p_202792_4_) && LayerUtil.isShallowOcean(p_202792_2_) && LayerUtil.isShallowOcean(p_202792_3_)) {
         if (!LayerUtil.isShallowOcean(p_202792_6_) && (LayerUtil.isShallowOcean(p_202792_5_) || LayerUtil.isShallowOcean(p_202792_2_) || LayerUtil.isShallowOcean(p_202792_4_) || LayerUtil.isShallowOcean(p_202792_3_)) && p_202792_1_.random(5) == 0) {
            if (LayerUtil.isShallowOcean(p_202792_5_)) {
               return p_202792_6_ == 4 ? 4 : p_202792_5_;
            }

            if (LayerUtil.isShallowOcean(p_202792_2_)) {
               return p_202792_6_ == 4 ? 4 : p_202792_2_;
            }

            if (LayerUtil.isShallowOcean(p_202792_4_)) {
               return p_202792_6_ == 4 ? 4 : p_202792_4_;
            }

            if (LayerUtil.isShallowOcean(p_202792_3_)) {
               return p_202792_6_ == 4 ? 4 : p_202792_3_;
            }
         }

         return p_202792_6_;
      } else {
         int lvt_7_1_ = 1;
         int lvt_8_1_ = 1;
         if (!LayerUtil.isShallowOcean(p_202792_5_) && p_202792_1_.random(lvt_7_1_++) == 0) {
            lvt_8_1_ = p_202792_5_;
         }

         if (!LayerUtil.isShallowOcean(p_202792_4_) && p_202792_1_.random(lvt_7_1_++) == 0) {
            lvt_8_1_ = p_202792_4_;
         }

         if (!LayerUtil.isShallowOcean(p_202792_2_) && p_202792_1_.random(lvt_7_1_++) == 0) {
            lvt_8_1_ = p_202792_2_;
         }

         if (!LayerUtil.isShallowOcean(p_202792_3_) && p_202792_1_.random(lvt_7_1_++) == 0) {
            lvt_8_1_ = p_202792_3_;
         }

         if (p_202792_1_.random(3) == 0) {
            return lvt_8_1_;
         } else {
            return lvt_8_1_ == 4 ? 4 : p_202792_6_;
         }
      }
   }
}
