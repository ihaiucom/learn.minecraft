package net.minecraft.pathfinding;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Region;
import net.minecraft.world.World;

public abstract class PathNavigator {
   protected final MobEntity entity;
   protected final World world;
   @Nullable
   protected Path currentPath;
   protected double speed;
   private final IAttributeInstance field_226333_p_;
   protected int totalTicks;
   protected int ticksAtLastPos;
   protected Vec3d lastPosCheck;
   protected Vec3d timeoutCachedNode;
   protected long timeoutTimer;
   protected long lastTimeoutCheck;
   protected double timeoutLimit;
   protected float maxDistanceToWaypoint;
   protected boolean tryUpdatePath;
   protected long lastTimeUpdated;
   protected NodeProcessor nodeProcessor;
   private BlockPos targetPos;
   private int field_225468_r;
   private float field_226334_s_;
   private final PathFinder pathFinder;

   public PathNavigator(MobEntity p_i1671_1_, World p_i1671_2_) {
      this.lastPosCheck = Vec3d.ZERO;
      this.timeoutCachedNode = Vec3d.ZERO;
      this.maxDistanceToWaypoint = 0.5F;
      this.field_226334_s_ = 1.0F;
      this.entity = p_i1671_1_;
      this.world = p_i1671_2_;
      this.field_226333_p_ = p_i1671_1_.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
      int i = MathHelper.floor(this.field_226333_p_.getValue() * 16.0D);
      this.pathFinder = this.getPathFinder(i);
   }

   public void func_226336_g_() {
      this.field_226334_s_ = 1.0F;
   }

   public void func_226335_a_(float p_226335_1_) {
      this.field_226334_s_ = p_226335_1_;
   }

   public BlockPos getTargetPos() {
      return this.targetPos;
   }

   protected abstract PathFinder getPathFinder(int var1);

   public void setSpeed(double p_75489_1_) {
      this.speed = p_75489_1_;
   }

   public boolean canUpdatePathOnTimeout() {
      return this.tryUpdatePath;
   }

   public void updatePath() {
      if (this.world.getGameTime() - this.lastTimeUpdated > 20L) {
         if (this.targetPos != null) {
            this.currentPath = null;
            this.currentPath = this.getPathToPos(this.targetPos, this.field_225468_r);
            this.lastTimeUpdated = this.world.getGameTime();
            this.tryUpdatePath = false;
         }
      } else {
         this.tryUpdatePath = true;
      }

   }

   @Nullable
   public final Path func_225466_a(double p_225466_1_, double p_225466_3_, double p_225466_5_, int p_225466_7_) {
      return this.getPathToPos(new BlockPos(p_225466_1_, p_225466_3_, p_225466_5_), p_225466_7_);
   }

   @Nullable
   public Path func_225463_a(Stream<BlockPos> p_225463_1_, int p_225463_2_) {
      return this.func_225464_a((Set)p_225463_1_.collect(Collectors.toSet()), 8, false, p_225463_2_);
   }

   @Nullable
   public Path getPathToPos(BlockPos p_179680_1_, int p_179680_2_) {
      return this.func_225464_a(ImmutableSet.of(p_179680_1_), 8, false, p_179680_2_);
   }

   @Nullable
   public Path getPathToEntityLiving(Entity p_75494_1_, int p_75494_2_) {
      return this.func_225464_a(ImmutableSet.of(new BlockPos(p_75494_1_)), 16, true, p_75494_2_);
   }

   @Nullable
   protected Path func_225464_a(Set<BlockPos> p_225464_1_, int p_225464_2_, boolean p_225464_3_, int p_225464_4_) {
      if (p_225464_1_.isEmpty()) {
         return null;
      } else if (this.entity.func_226278_cu_() < 0.0D) {
         return null;
      } else if (!this.canNavigate()) {
         return null;
      } else if (this.currentPath != null && !this.currentPath.isFinished() && p_225464_1_.contains(this.targetPos)) {
         return this.currentPath;
      } else {
         this.world.getProfiler().startSection("pathfind");
         float f = (float)this.field_226333_p_.getValue();
         BlockPos blockpos = p_225464_3_ ? (new BlockPos(this.entity)).up() : new BlockPos(this.entity);
         int i = (int)(f + (float)p_225464_2_);
         Region region = new Region(this.world, blockpos.add(-i, -i, -i), blockpos.add(i, i, i));
         Path path = this.pathFinder.func_227478_a_(region, this.entity, p_225464_1_, f, p_225464_4_, this.field_226334_s_);
         this.world.getProfiler().endSection();
         if (path != null && path.func_224770_k() != null) {
            this.targetPos = path.func_224770_k();
            this.field_225468_r = p_225464_4_;
         }

         return path;
      }
   }

   public boolean tryMoveToXYZ(double p_75492_1_, double p_75492_3_, double p_75492_5_, double p_75492_7_) {
      return this.setPath(this.func_225466_a(p_75492_1_, p_75492_3_, p_75492_5_, 1), p_75492_7_);
   }

   public boolean tryMoveToEntityLiving(Entity p_75497_1_, double p_75497_2_) {
      Path path = this.getPathToEntityLiving(p_75497_1_, 1);
      return path != null && this.setPath(path, p_75497_2_);
   }

   public boolean setPath(@Nullable Path p_75484_1_, double p_75484_2_) {
      if (p_75484_1_ == null) {
         this.currentPath = null;
         return false;
      } else {
         if (!p_75484_1_.isSamePath(this.currentPath)) {
            this.currentPath = p_75484_1_;
         }

         if (this.noPath()) {
            return false;
         } else {
            this.trimPath();
            if (this.currentPath.getCurrentPathLength() <= 0) {
               return false;
            } else {
               this.speed = p_75484_2_;
               Vec3d vec3d = this.getEntityPosition();
               this.ticksAtLastPos = this.totalTicks;
               this.lastPosCheck = vec3d;
               return true;
            }
         }
      }
   }

   @Nullable
   public Path getPath() {
      return this.currentPath;
   }

   public void tick() {
      ++this.totalTicks;
      if (this.tryUpdatePath) {
         this.updatePath();
      }

      if (!this.noPath()) {
         Vec3d vec3d2;
         if (this.canNavigate()) {
            this.pathFollow();
         } else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength()) {
            vec3d2 = this.getEntityPosition();
            Vec3d vec3d1 = this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex());
            if (vec3d2.y > vec3d1.y && !this.entity.onGround && MathHelper.floor(vec3d2.x) == MathHelper.floor(vec3d1.x) && MathHelper.floor(vec3d2.z) == MathHelper.floor(vec3d1.z)) {
               this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
            }
         }

         DebugPacketSender.func_218803_a(this.world, this.entity, this.currentPath, this.maxDistanceToWaypoint);
         if (!this.noPath()) {
            vec3d2 = this.currentPath.getPosition(this.entity);
            BlockPos blockpos = new BlockPos(vec3d2);
            this.entity.getMoveHelper().setMoveTo(vec3d2.x, this.world.getBlockState(blockpos.down()).isAir() ? vec3d2.y : WalkNodeProcessor.getGroundY(this.world, blockpos), vec3d2.z, this.speed);
         }
      }

   }

   protected void pathFollow() {
      Vec3d vec3d = this.getEntityPosition();
      this.maxDistanceToWaypoint = this.entity.getWidth() > 0.75F ? this.entity.getWidth() / 2.0F : 0.75F - this.entity.getWidth() / 2.0F;
      Vec3d vec3d1 = this.currentPath.getCurrentPos();
      if (Math.abs(this.entity.func_226277_ct_() - (vec3d1.x + (double)((int)(this.entity.getWidth() + 1.0F)) / 2.0D)) < (double)this.maxDistanceToWaypoint && Math.abs(this.entity.func_226281_cx_() - (vec3d1.z + (double)((int)(this.entity.getWidth() + 1.0F)) / 2.0D)) < (double)this.maxDistanceToWaypoint && Math.abs(this.entity.func_226278_cu_() - vec3d1.y) < 1.0D) {
         this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
      }

      this.checkForStuck(vec3d);
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
            this.timeoutLimit = this.entity.getAIMoveSpeed() > 0.0F ? d0 / (double)this.entity.getAIMoveSpeed() * 1000.0D : 0.0D;
         }

         if (this.timeoutLimit > 0.0D && (double)this.timeoutTimer > this.timeoutLimit * 3.0D) {
            this.timeoutCachedNode = Vec3d.ZERO;
            this.timeoutTimer = 0L;
            this.timeoutLimit = 0.0D;
            this.clearPath();
         }

         this.lastTimeoutCheck = Util.milliTime();
      }

   }

   public boolean noPath() {
      return this.currentPath == null || this.currentPath.isFinished();
   }

   public boolean func_226337_n_() {
      return !this.noPath();
   }

   public void clearPath() {
      this.currentPath = null;
   }

   protected abstract Vec3d getEntityPosition();

   protected abstract boolean canNavigate();

   protected boolean isInLiquid() {
      return this.entity.isInWaterOrBubbleColumn() || this.entity.isInLava();
   }

   protected void trimPath() {
      if (this.currentPath != null) {
         for(int i = 0; i < this.currentPath.getCurrentPathLength(); ++i) {
            PathPoint pathpoint = this.currentPath.getPathPointFromIndex(i);
            PathPoint pathpoint1 = i + 1 < this.currentPath.getCurrentPathLength() ? this.currentPath.getPathPointFromIndex(i + 1) : null;
            BlockState blockstate = this.world.getBlockState(new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z));
            Block block = blockstate.getBlock();
            if (block == Blocks.CAULDRON) {
               this.currentPath.setPoint(i, pathpoint.cloneMove(pathpoint.x, pathpoint.y + 1, pathpoint.z));
               if (pathpoint1 != null && pathpoint.y >= pathpoint1.y) {
                  this.currentPath.setPoint(i + 1, pathpoint1.cloneMove(pathpoint1.x, pathpoint.y + 1, pathpoint1.z));
               }
            }
         }
      }

   }

   protected abstract boolean isDirectPathBetweenPoints(Vec3d var1, Vec3d var2, int var3, int var4, int var5);

   public boolean canEntityStandOnPos(BlockPos p_188555_1_) {
      BlockPos blockpos = p_188555_1_.down();
      return this.world.getBlockState(blockpos).isOpaqueCube(this.world, blockpos);
   }

   public NodeProcessor getNodeProcessor() {
      return this.nodeProcessor;
   }

   public void setCanSwim(boolean p_212239_1_) {
      this.nodeProcessor.setCanSwim(p_212239_1_);
   }

   public boolean getCanSwim() {
      return this.nodeProcessor.getCanSwim();
   }

   public void func_220970_c(BlockPos p_220970_1_) {
      if (this.currentPath != null && !this.currentPath.isFinished() && this.currentPath.getCurrentPathLength() != 0) {
         PathPoint pathpoint = this.currentPath.getFinalPathPoint();
         Vec3d vec3d = new Vec3d(((double)pathpoint.x + this.entity.func_226277_ct_()) / 2.0D, ((double)pathpoint.y + this.entity.func_226278_cu_()) / 2.0D, ((double)pathpoint.z + this.entity.func_226281_cx_()) / 2.0D);
         if (p_220970_1_.withinDistance(vec3d, (double)(this.currentPath.getCurrentPathLength() - this.currentPath.getCurrentPathIndex()))) {
            this.updatePath();
         }
      }

   }
}
