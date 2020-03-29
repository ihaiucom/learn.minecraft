package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class Phase implements IPhase {
   protected final EnderDragonEntity dragon;

   public Phase(EnderDragonEntity p_i46795_1_) {
      this.dragon = p_i46795_1_;
   }

   public boolean getIsStationary() {
      return false;
   }

   public void clientTick() {
   }

   public void serverTick() {
   }

   public void onCrystalDestroyed(EnderCrystalEntity p_188655_1_, BlockPos p_188655_2_, DamageSource p_188655_3_, @Nullable PlayerEntity p_188655_4_) {
   }

   public void initPhase() {
   }

   public void removeAreaEffect() {
   }

   public float getMaxRiseOrFall() {
      return 0.6F;
   }

   @Nullable
   public Vec3d getTargetLocation() {
      return null;
   }

   public float func_221113_a(DamageSource p_221113_1_, float p_221113_2_) {
      return p_221113_2_;
   }

   public float getYawFactor() {
      float lvt_1_1_ = MathHelper.sqrt(Entity.func_213296_b(this.dragon.getMotion())) + 1.0F;
      float lvt_2_1_ = Math.min(lvt_1_1_, 40.0F);
      return 0.7F / lvt_2_1_ / lvt_1_1_;
   }
}
