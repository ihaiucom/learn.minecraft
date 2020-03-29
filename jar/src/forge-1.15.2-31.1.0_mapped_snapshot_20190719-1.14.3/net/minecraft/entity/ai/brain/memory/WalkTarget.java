package net.minecraft.entity.ai.brain.memory;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.util.math.IPosWrapper;
import net.minecraft.util.math.Vec3d;

public class WalkTarget {
   private final IPosWrapper target;
   private final float speed;
   private final int distance;

   public WalkTarget(BlockPos p_i50302_1_, float p_i50302_2_, int p_i50302_3_) {
      this((IPosWrapper)(new BlockPosWrapper(p_i50302_1_)), p_i50302_2_, p_i50302_3_);
   }

   public WalkTarget(Vec3d p_i50303_1_, float p_i50303_2_, int p_i50303_3_) {
      this((IPosWrapper)(new BlockPosWrapper(new BlockPos(p_i50303_1_))), p_i50303_2_, p_i50303_3_);
   }

   public WalkTarget(IPosWrapper p_i50304_1_, float p_i50304_2_, int p_i50304_3_) {
      this.target = p_i50304_1_;
      this.speed = p_i50304_2_;
      this.distance = p_i50304_3_;
   }

   public IPosWrapper getTarget() {
      return this.target;
   }

   public float getSpeed() {
      return this.speed;
   }

   public int getDistance() {
      return this.distance;
   }
}
