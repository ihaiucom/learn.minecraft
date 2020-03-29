package net.minecraft.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FoodStats {
   private int foodLevel = 20;
   private float foodSaturationLevel = 5.0F;
   private float foodExhaustionLevel;
   private int foodTimer;
   private int prevFoodLevel = 20;

   public void addStats(int p_75122_1_, float p_75122_2_) {
      this.foodLevel = Math.min(p_75122_1_ + this.foodLevel, 20);
      this.foodSaturationLevel = Math.min(this.foodSaturationLevel + (float)p_75122_1_ * p_75122_2_ * 2.0F, (float)this.foodLevel);
   }

   public void consume(Item p_221410_1_, ItemStack p_221410_2_) {
      if (p_221410_1_.isFood()) {
         Food lvt_3_1_ = p_221410_1_.getFood();
         this.addStats(lvt_3_1_.getHealing(), lvt_3_1_.getSaturation());
      }

   }

   public void tick(PlayerEntity p_75118_1_) {
      Difficulty lvt_2_1_ = p_75118_1_.world.getDifficulty();
      this.prevFoodLevel = this.foodLevel;
      if (this.foodExhaustionLevel > 4.0F) {
         this.foodExhaustionLevel -= 4.0F;
         if (this.foodSaturationLevel > 0.0F) {
            this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
         } else if (lvt_2_1_ != Difficulty.PEACEFUL) {
            this.foodLevel = Math.max(this.foodLevel - 1, 0);
         }
      }

      boolean lvt_3_1_ = p_75118_1_.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
      if (lvt_3_1_ && this.foodSaturationLevel > 0.0F && p_75118_1_.shouldHeal() && this.foodLevel >= 20) {
         ++this.foodTimer;
         if (this.foodTimer >= 10) {
            float lvt_4_1_ = Math.min(this.foodSaturationLevel, 6.0F);
            p_75118_1_.heal(lvt_4_1_ / 6.0F);
            this.addExhaustion(lvt_4_1_);
            this.foodTimer = 0;
         }
      } else if (lvt_3_1_ && this.foodLevel >= 18 && p_75118_1_.shouldHeal()) {
         ++this.foodTimer;
         if (this.foodTimer >= 80) {
            p_75118_1_.heal(1.0F);
            this.addExhaustion(6.0F);
            this.foodTimer = 0;
         }
      } else if (this.foodLevel <= 0) {
         ++this.foodTimer;
         if (this.foodTimer >= 80) {
            if (p_75118_1_.getHealth() > 10.0F || lvt_2_1_ == Difficulty.HARD || p_75118_1_.getHealth() > 1.0F && lvt_2_1_ == Difficulty.NORMAL) {
               p_75118_1_.attackEntityFrom(DamageSource.STARVE, 1.0F);
            }

            this.foodTimer = 0;
         }
      } else {
         this.foodTimer = 0;
      }

   }

   public void read(CompoundNBT p_75112_1_) {
      if (p_75112_1_.contains("foodLevel", 99)) {
         this.foodLevel = p_75112_1_.getInt("foodLevel");
         this.foodTimer = p_75112_1_.getInt("foodTickTimer");
         this.foodSaturationLevel = p_75112_1_.getFloat("foodSaturationLevel");
         this.foodExhaustionLevel = p_75112_1_.getFloat("foodExhaustionLevel");
      }

   }

   public void write(CompoundNBT p_75117_1_) {
      p_75117_1_.putInt("foodLevel", this.foodLevel);
      p_75117_1_.putInt("foodTickTimer", this.foodTimer);
      p_75117_1_.putFloat("foodSaturationLevel", this.foodSaturationLevel);
      p_75117_1_.putFloat("foodExhaustionLevel", this.foodExhaustionLevel);
   }

   public int getFoodLevel() {
      return this.foodLevel;
   }

   public boolean needFood() {
      return this.foodLevel < 20;
   }

   public void addExhaustion(float p_75113_1_) {
      this.foodExhaustionLevel = Math.min(this.foodExhaustionLevel + p_75113_1_, 40.0F);
   }

   public float getSaturationLevel() {
      return this.foodSaturationLevel;
   }

   public void setFoodLevel(int p_75114_1_) {
      this.foodLevel = p_75114_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public void setFoodSaturationLevel(float p_75119_1_) {
      this.foodSaturationLevel = p_75119_1_;
   }
}
