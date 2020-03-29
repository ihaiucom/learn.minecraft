package net.minecraft.pathfinding;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SwimmerPathNavigator extends PathNavigator {
   private boolean field_205155_i;

   public SwimmerPathNavigator(MobEntity p_i45873_1_, World p_i45873_2_) {
      super(p_i45873_1_, p_i45873_2_);
   }

   protected PathFinder getPathFinder(int p_179679_1_) {
      this.field_205155_i = this.entity instanceof DolphinEntity;
      this.nodeProcessor = new SwimNodeProcessor(this.field_205155_i);
      return new PathFinder(this.nodeProcessor, p_179679_1_);
   }

   protected boolean canNavigate() {
      return this.field_205155_i || this.isInLiquid();
   }

   protected Vec3d getEntityPosition() {
      return new Vec3d(this.entity.func_226277_ct_(), this.entity.func_226283_e_(0.5D), this.entity.func_226281_cx_());
   }

   public void tick() {
      ++this.totalTicks;
      if (this.tryUpdatePath) {
         this.updatePath();
      }

      if (!this.noPath()) {
         Vec3d vec3d1;
         if (this.canNavigate()) {
            this.pathFollow();
         } else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength()) {
            vec3d1 = this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex());
            if (MathHelper.floor(this.entity.func_226277_ct_()) == MathHelper.floor(vec3d1.x) && MathHelper.floor(this.entity.func_226278_cu_()) == MathHelper.floor(vec3d1.y) && MathHelper.floor(this.entity.func_226281_cx_()) == MathHelper.floor(vec3d1.z)) {
               this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
            }
         }

         DebugPacketSender.func_218803_a(this.world, this.entity, this.currentPath, this.maxDistanceToWaypoint);
         if (!this.noPath()) {
            vec3d1 = this.currentPath.getPosition(this.entity);
            this.entity.getMoveHelper().setMoveTo(vec3d1.x, vec3d1.y, vec3d1.z, this.speed);
         }
      }

   }

   protected void pathFollow() {
      if (this.currentPath != null) {
         Vec3d vec3d = this.getEntityPosition();
         float f = this.entity.getWidth();
         float f1 = f > 0.75F ? f / 2.0F : 0.75F - f / 2.0F;
         Vec3d vec3d1 = this.entity.getMotion();
         if (Math.abs(vec3d1.x) > 0.2D || Math.abs(vec3d1.z) > 0.2D) {
            f1 = (float)((double)f1 * vec3d1.length() * 6.0D);
         }

         int i = true;
         Vec3d vec3d2 = this.currentPath.getCurrentPos();
         if (Math.abs(this.entity.func_226277_ct_() - (vec3d2.x + (double)((int)(this.entity.getWidth() + 1.0F)) / 2.0D)) < (double)f1 && Math.abs(this.entity.func_226281_cx_() - (vec3d2.z + (double)((int)(this.entity.getWidth() + 1.0F)) / 2.0D)) < (double)f1 && Math.abs(this.entity.func_226278_cu_() - vec3d2.y) < (double)(f1 * 2.0F)) {
            this.currentPath.incrementPathIndex();
         }

         for(int j = Math.min(this.currentPath.getCurrentPathIndex() + 6, this.currentPath.getCurrentPathLength() - 1); j > this.currentPath.getCurrentPathIndex(); --j) {
            vec3d2 = this.currentPath.getVectorFromIndex(this.entity, j);
            if (vec3d2.squareDistanceTo(vec3d) <= 36.0D && this.isDirectPathBetweenPoints(vec3d, vec3d2, 0, 0, 0)) {
               this.currentPath.setCurrentPathIndex(j);
               break;
            }
         }

         this.checkForStuck(vec3d);
      }

   }

   protected void checkForStuck(Vec3d p_179677_1_) {
      if (this.totalTicks - this.ticksAtLastPos > 100) {
         if (p_179677_1_.squareDistanceTo(this.lastPosCheck) < 2.25D) {
            this.clearPath();
         }

         this.ticksAtLastPos = this.totalTicks;
         this.lastPosCheck = p_179677_1_;
      }

      if (this.currentPath != null && !this.currentPath.isFinished()) {
         Vec3d vec3d = this.currentPath.getCurrentPos();
         if (vec3d.equals(this.timeoutCachedNode)) {
            this.timeoutTimer += Util.milliTime() - this.lastTimeoutCheck;
         } else {
            this.timeoutCachedNode = vec3d;
            double d0 = p_179677_1_.distanceTo(this.timeoutCachedNode);
            this.timeoutLimit = this.entity.getAIMoveSpeed() > 0.0F ? d0 / (double)this.entity.getAIMoveSpeed() * 100.0D : 0.0D;
         }

         if (this.timeoutLimit > 0.0D && (double)this.timeoutTimer > this.timeoutLimit * 2.0D) {
            this.timeoutCachedNode = Vec3d.ZERO;
            this.timeoutTimer = 0L;
            this.timeoutLimit = 0.0D;
            this.clearPath();
         }

         this.lastTimeoutCheck = Util.milliTime();
      }

   }

   protected boolean isDirectPathBetweenPoints(Vec3d p_75493_1_, Vec3d p_75493_2_, int p_75493_3_, int p_75493_4_, int p_75493_5_) {
      Vec3d vec3d = new Vec3d(p_75493_2_.x, p_75493_2_.y + (double)this.entity.getHeight() * 0.5D, p_75493_2_.z);
      return this.world.rayTraceBlocks(new RayTraceContext(p_75493_1_, vec3d, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this.entity)).getType() == RayTraceResult.Type.MISS;
   }

   public boolean canEntityStandOnPos(BlockPos p_188555_1_) {
      return !this.world.getBlockState(p_188555_1_).isOpaqueCube(this.world, p_188555_1_);
   }

   public void setCanSwim(boolean p_212239_1_) {
   }
}
