package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.util.math.MathHelper;

@Immutable
public class DifficultyInstance {
   private final Difficulty worldDifficulty;
   private final float additionalDifficulty;

   public DifficultyInstance(Difficulty p_i45904_1_, long p_i45904_2_, long p_i45904_4_, float p_i45904_6_) {
      this.worldDifficulty = p_i45904_1_;
      this.additionalDifficulty = this.calculateAdditionalDifficulty(p_i45904_1_, p_i45904_2_, p_i45904_4_, p_i45904_6_);
   }

   public Difficulty getDifficulty() {
      return this.worldDifficulty;
   }

   public float getAdditionalDifficulty() {
      return this.additionalDifficulty;
   }

   public boolean isHarderThan(float p_193845_1_) {
      return this.additionalDifficulty > p_193845_1_;
   }

   public float getClampedAdditionalDifficulty() {
      if (this.additionalDifficulty < 2.0F) {
         return 0.0F;
      } else {
         return this.additionalDifficulty > 4.0F ? 1.0F : (this.additionalDifficulty - 2.0F) / 2.0F;
      }
   }

   private float calculateAdditionalDifficulty(Difficulty p_180169_1_, long p_180169_2_, long p_180169_4_, float p_180169_6_) {
      if (p_180169_1_ == Difficulty.PEACEFUL) {
         return 0.0F;
      } else {
         boolean lvt_7_1_ = p_180169_1_ == Difficulty.HARD;
         float lvt_8_1_ = 0.75F;
         float lvt_9_1_ = MathHelper.clamp(((float)p_180169_2_ + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;
         lvt_8_1_ += lvt_9_1_;
         float lvt_10_1_ = 0.0F;
         lvt_10_1_ += MathHelper.clamp((float)p_180169_4_ / 3600000.0F, 0.0F, 1.0F) * (lvt_7_1_ ? 1.0F : 0.75F);
         lvt_10_1_ += MathHelper.clamp(p_180169_6_ * 0.25F, 0.0F, lvt_9_1_);
         if (p_180169_1_ == Difficulty.EASY) {
            lvt_10_1_ *= 0.5F;
         }

         lvt_8_1_ += lvt_10_1_;
         return (float)p_180169_1_.getId() * lvt_8_1_;
      }
   }
}
