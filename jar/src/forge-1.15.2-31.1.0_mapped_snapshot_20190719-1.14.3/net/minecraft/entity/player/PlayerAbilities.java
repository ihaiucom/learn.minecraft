package net.minecraft.entity.player;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PlayerAbilities {
   public boolean disableDamage;
   public boolean isFlying;
   public boolean allowFlying;
   public boolean isCreativeMode;
   public boolean allowEdit = true;
   private float flySpeed = 0.05F;
   private float walkSpeed = 0.1F;

   public void write(CompoundNBT p_75091_1_) {
      CompoundNBT lvt_2_1_ = new CompoundNBT();
      lvt_2_1_.putBoolean("invulnerable", this.disableDamage);
      lvt_2_1_.putBoolean("flying", this.isFlying);
      lvt_2_1_.putBoolean("mayfly", this.allowFlying);
      lvt_2_1_.putBoolean("instabuild", this.isCreativeMode);
      lvt_2_1_.putBoolean("mayBuild", this.allowEdit);
      lvt_2_1_.putFloat("flySpeed", this.flySpeed);
      lvt_2_1_.putFloat("walkSpeed", this.walkSpeed);
      p_75091_1_.put("abilities", lvt_2_1_);
   }

   public void read(CompoundNBT p_75095_1_) {
      if (p_75095_1_.contains("abilities", 10)) {
         CompoundNBT lvt_2_1_ = p_75095_1_.getCompound("abilities");
         this.disableDamage = lvt_2_1_.getBoolean("invulnerable");
         this.isFlying = lvt_2_1_.getBoolean("flying");
         this.allowFlying = lvt_2_1_.getBoolean("mayfly");
         this.isCreativeMode = lvt_2_1_.getBoolean("instabuild");
         if (lvt_2_1_.contains("flySpeed", 99)) {
            this.flySpeed = lvt_2_1_.getFloat("flySpeed");
            this.walkSpeed = lvt_2_1_.getFloat("walkSpeed");
         }

         if (lvt_2_1_.contains("mayBuild", 1)) {
            this.allowEdit = lvt_2_1_.getBoolean("mayBuild");
         }
      }

   }

   public float getFlySpeed() {
      return this.flySpeed;
   }

   @OnlyIn(Dist.CLIENT)
   public void setFlySpeed(float p_195931_1_) {
      this.flySpeed = p_195931_1_;
   }

   public float getWalkSpeed() {
      return this.walkSpeed;
   }

   @OnlyIn(Dist.CLIENT)
   public void setWalkSpeed(float p_82877_1_) {
      this.walkSpeed = p_82877_1_;
   }
}
