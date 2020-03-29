package net.minecraft.util.math;

import net.minecraft.util.Direction;

public class BlockRayTraceResult extends RayTraceResult {
   private final Direction face;
   private final BlockPos pos;
   private final boolean isMiss;
   private final boolean inside;

   public static BlockRayTraceResult createMiss(Vec3d p_216352_0_, Direction p_216352_1_, BlockPos p_216352_2_) {
      return new BlockRayTraceResult(true, p_216352_0_, p_216352_1_, p_216352_2_, false);
   }

   public BlockRayTraceResult(Vec3d p_i51186_1_, Direction p_i51186_2_, BlockPos p_i51186_3_, boolean p_i51186_4_) {
      this(false, p_i51186_1_, p_i51186_2_, p_i51186_3_, p_i51186_4_);
   }

   private BlockRayTraceResult(boolean p_i51187_1_, Vec3d p_i51187_2_, Direction p_i51187_3_, BlockPos p_i51187_4_, boolean p_i51187_5_) {
      super(p_i51187_2_);
      this.isMiss = p_i51187_1_;
      this.face = p_i51187_3_;
      this.pos = p_i51187_4_;
      this.inside = p_i51187_5_;
   }

   public BlockRayTraceResult withFace(Direction p_216351_1_) {
      return new BlockRayTraceResult(this.isMiss, this.hitResult, p_216351_1_, this.pos, this.inside);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Direction getFace() {
      return this.face;
   }

   public RayTraceResult.Type getType() {
      return this.isMiss ? RayTraceResult.Type.MISS : RayTraceResult.Type.BLOCK;
   }

   public boolean isInside() {
      return this.inside;
   }
}
