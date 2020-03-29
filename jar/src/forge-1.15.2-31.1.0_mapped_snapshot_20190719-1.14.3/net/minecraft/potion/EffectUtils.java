package net.minecraft.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class EffectUtils {
   @OnlyIn(Dist.CLIENT)
   public static String getPotionDurationString(EffectInstance p_188410_0_, float p_188410_1_) {
      if (p_188410_0_.getIsPotionDurationMax()) {
         return "**:**";
      } else {
         int lvt_2_1_ = MathHelper.floor((float)p_188410_0_.getDuration() * p_188410_1_);
         return StringUtils.ticksToElapsedTime(lvt_2_1_);
      }
   }

   public static boolean hasMiningSpeedup(LivingEntity p_205135_0_) {
      return p_205135_0_.isPotionActive(Effects.HASTE) || p_205135_0_.isPotionActive(Effects.CONDUIT_POWER);
   }

   public static int getMiningSpeedup(LivingEntity p_205134_0_) {
      int lvt_1_1_ = 0;
      int lvt_2_1_ = 0;
      if (p_205134_0_.isPotionActive(Effects.HASTE)) {
         lvt_1_1_ = p_205134_0_.getActivePotionEffect(Effects.HASTE).getAmplifier();
      }

      if (p_205134_0_.isPotionActive(Effects.CONDUIT_POWER)) {
         lvt_2_1_ = p_205134_0_.getActivePotionEffect(Effects.CONDUIT_POWER).getAmplifier();
      }

      return Math.max(lvt_1_1_, lvt_2_1_);
   }

   public static boolean canBreatheUnderwater(LivingEntity p_205133_0_) {
      return p_205133_0_.isPotionActive(Effects.WATER_BREATHING) || p_205133_0_.isPotionActive(Effects.CONDUIT_POWER);
   }
}
