package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class DolphinJumpGoal extends JumpGoal {
   private static final int[] field_220710_a = new int[]{0, 1, 4, 5, 6, 7};
   private final DolphinEntity field_220711_b;
   private final int field_220712_c;
   private boolean field_220713_d;

   public DolphinJumpGoal(DolphinEntity p_i50329_1_, int p_i50329_2_) {
      this.field_220711_b = p_i50329_1_;
      this.field_220712_c = p_i50329_2_;
   }

   public boolean shouldExecute() {
      if (this.field_220711_b.getRNG().nextInt(this.field_220712_c) != 0) {
         return false;
      } else {
         Direction lvt_1_1_ = this.field_220711_b.getAdjustedHorizontalFacing();
         int lvt_2_1_ = lvt_1_1_.getXOffset();
         int lvt_3_1_ = lvt_1_1_.getZOffset();
         BlockPos lvt_4_1_ = new BlockPos(this.field_220711_b);
         int[] var5 = field_220710_a;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            int lvt_8_1_ = var5[var7];
            if (!this.func_220709_a(lvt_4_1_, lvt_2_1_, lvt_3_1_, lvt_8_1_) || !this.func_220708_b(lvt_4_1_, lvt_2_1_, lvt_3_1_, lvt_8_1_)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean func_220709_a(BlockPos p_220709_1_, int p_220709_2_, int p_220709_3_, int p_220709_4_) {
      BlockPos lvt_5_1_ = p_220709_1_.add(p_220709_2_ * p_220709_4_, 0, p_220709_3_ * p_220709_4_);
      return this.field_220711_b.world.getFluidState(lvt_5_1_).isTagged(FluidTags.WATER) && !this.field_220711_b.world.getBlockState(lvt_5_1_).getMaterial().blocksMovement();
   }

   private boolean func_220708_b(BlockPos p_220708_1_, int p_220708_2_, int p_220708_3_, int p_220708_4_) {
      return this.field_220711_b.world.getBlockState(p_220708_1_.add(p_220708_2_ * p_220708_4_, 1, p_220708_3_ * p_220708_4_)).isAir() && this.field_220711_b.world.getBlockState(p_220708_1_.add(p_220708_2_ * p_220708_4_, 2, p_220708_3_ * p_220708_4_)).isAir();
   }

   public boolean shouldContinueExecuting() {
      double lvt_1_1_ = this.field_220711_b.getMotion().y;
      return (lvt_1_1_ * lvt_1_1_ >= 0.029999999329447746D || this.field_220711_b.rotationPitch == 0.0F || Math.abs(this.field_220711_b.rotationPitch) >= 10.0F || !this.field_220711_b.isInWater()) && !this.field_220711_b.onGround;
   }

   public boolean isPreemptible() {
      return false;
   }

   public void startExecuting() {
      Direction lvt_1_1_ = this.field_220711_b.getAdjustedHorizontalFacing();
      this.field_220711_b.setMotion(this.field_220711_b.getMotion().add((double)lvt_1_1_.getXOffset() * 0.6D, 0.7D, (double)lvt_1_1_.getZOffset() * 0.6D));
      this.field_220711_b.getNavigator().clearPath();
   }

   public void resetTask() {
      this.field_220711_b.rotationPitch = 0.0F;
   }

   public void tick() {
      boolean lvt_1_1_ = this.field_220713_d;
      if (!lvt_1_1_) {
         IFluidState lvt_2_1_ = this.field_220711_b.world.getFluidState(new BlockPos(this.field_220711_b));
         this.field_220713_d = lvt_2_1_.isTagged(FluidTags.WATER);
      }

      if (this.field_220713_d && !lvt_1_1_) {
         this.field_220711_b.playSound(SoundEvents.ENTITY_DOLPHIN_JUMP, 1.0F, 1.0F);
      }

      Vec3d lvt_2_2_ = this.field_220711_b.getMotion();
      if (lvt_2_2_.y * lvt_2_2_.y < 0.029999999329447746D && this.field_220711_b.rotationPitch != 0.0F) {
         this.field_220711_b.rotationPitch = MathHelper.func_226167_j_(this.field_220711_b.rotationPitch, 0.0F, 0.2F);
      } else {
         double lvt_3_1_ = Math.sqrt(Entity.func_213296_b(lvt_2_2_));
         double lvt_5_1_ = Math.signum(-lvt_2_2_.y) * Math.acos(lvt_3_1_ / lvt_2_2_.length()) * 57.2957763671875D;
         this.field_220711_b.rotationPitch = (float)lvt_5_1_;
      }

   }
}
