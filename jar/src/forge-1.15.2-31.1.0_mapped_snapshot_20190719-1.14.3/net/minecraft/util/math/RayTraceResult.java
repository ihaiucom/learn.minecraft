package net.minecraft.util.math;

public abstract class RayTraceResult {
   protected final Vec3d hitResult;
   public int subHit = -1;
   public Object hitInfo = null;

   protected RayTraceResult(Vec3d p_i51183_1_) {
      this.hitResult = p_i51183_1_;
   }

   public abstract RayTraceResult.Type getType();

   public Vec3d getHitVec() {
      return this.hitResult;
   }

   public static enum Type {
      MISS,
      BLOCK,
      ENTITY;
   }
}
