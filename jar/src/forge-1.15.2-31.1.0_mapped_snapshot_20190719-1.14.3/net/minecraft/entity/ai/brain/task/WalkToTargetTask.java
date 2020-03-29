package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class WalkToTargetTask extends Task<MobEntity> {
   @Nullable
   private Path field_220488_a;
   @Nullable
   private BlockPos field_220489_b;
   private float field_220490_c;
   private int field_220491_d;

   public WalkToTargetTask(int p_i50356_1_) {
      super(ImmutableMap.of(MemoryModuleType.PATH, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_PRESENT), p_i50356_1_);
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, MobEntity p_212832_2_) {
      Brain<?> lvt_3_1_ = p_212832_2_.getBrain();
      WalkTarget lvt_4_1_ = (WalkTarget)lvt_3_1_.getMemory(MemoryModuleType.WALK_TARGET).get();
      if (!this.hasReachedTarget(p_212832_2_, lvt_4_1_) && this.func_220487_a(p_212832_2_, lvt_4_1_, p_212832_1_.getGameTime())) {
         this.field_220489_b = lvt_4_1_.getTarget().getBlockPos();
         return true;
      } else {
         lvt_3_1_.removeMemory(MemoryModuleType.WALK_TARGET);
         return false;
      }
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, MobEntity p_212834_2_, long p_212834_3_) {
      if (this.field_220488_a != null && this.field_220489_b != null) {
         Optional<WalkTarget> lvt_5_1_ = p_212834_2_.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
         PathNavigator lvt_6_1_ = p_212834_2_.getNavigator();
         return !lvt_6_1_.noPath() && lvt_5_1_.isPresent() && !this.hasReachedTarget(p_212834_2_, (WalkTarget)lvt_5_1_.get());
      } else {
         return false;
      }
   }

   protected void resetTask(ServerWorld p_212835_1_, MobEntity p_212835_2_, long p_212835_3_) {
      p_212835_2_.getNavigator().clearPath();
      p_212835_2_.getBrain().removeMemory(MemoryModuleType.WALK_TARGET);
      p_212835_2_.getBrain().removeMemory(MemoryModuleType.PATH);
      this.field_220488_a = null;
   }

   protected void startExecuting(ServerWorld p_212831_1_, MobEntity p_212831_2_, long p_212831_3_) {
      p_212831_2_.getBrain().setMemory(MemoryModuleType.PATH, (Object)this.field_220488_a);
      p_212831_2_.getNavigator().setPath(this.field_220488_a, (double)this.field_220490_c);
      this.field_220491_d = p_212831_1_.getRandom().nextInt(10);
   }

   protected void updateTask(ServerWorld p_212833_1_, MobEntity p_212833_2_, long p_212833_3_) {
      --this.field_220491_d;
      if (this.field_220491_d <= 0) {
         Path lvt_5_1_ = p_212833_2_.getNavigator().getPath();
         Brain<?> lvt_6_1_ = p_212833_2_.getBrain();
         if (this.field_220488_a != lvt_5_1_) {
            this.field_220488_a = lvt_5_1_;
            lvt_6_1_.setMemory(MemoryModuleType.PATH, (Object)lvt_5_1_);
         }

         if (lvt_5_1_ != null && this.field_220489_b != null) {
            WalkTarget lvt_7_1_ = (WalkTarget)lvt_6_1_.getMemory(MemoryModuleType.WALK_TARGET).get();
            if (lvt_7_1_.getTarget().getBlockPos().distanceSq(this.field_220489_b) > 4.0D && this.func_220487_a(p_212833_2_, lvt_7_1_, p_212833_1_.getGameTime())) {
               this.field_220489_b = lvt_7_1_.getTarget().getBlockPos();
               this.startExecuting(p_212833_1_, p_212833_2_, p_212833_3_);
            }

         }
      }
   }

   private boolean func_220487_a(MobEntity p_220487_1_, WalkTarget p_220487_2_, long p_220487_3_) {
      BlockPos lvt_5_1_ = p_220487_2_.getTarget().getBlockPos();
      this.field_220488_a = p_220487_1_.getNavigator().getPathToPos(lvt_5_1_, 0);
      this.field_220490_c = p_220487_2_.getSpeed();
      if (!this.hasReachedTarget(p_220487_1_, p_220487_2_)) {
         Brain<?> lvt_6_1_ = p_220487_1_.getBrain();
         boolean lvt_7_1_ = this.field_220488_a != null && this.field_220488_a.func_224771_h();
         if (lvt_7_1_) {
            lvt_6_1_.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, Optional.empty());
         } else if (!lvt_6_1_.hasMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
            lvt_6_1_.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object)p_220487_3_);
         }

         if (this.field_220488_a != null) {
            return true;
         }

         Vec3d lvt_8_1_ = RandomPositionGenerator.findRandomTargetBlockTowards((CreatureEntity)p_220487_1_, 10, 7, new Vec3d(lvt_5_1_));
         if (lvt_8_1_ != null) {
            this.field_220488_a = p_220487_1_.getNavigator().func_225466_a(lvt_8_1_.x, lvt_8_1_.y, lvt_8_1_.z, 0);
            return this.field_220488_a != null;
         }
      }

      return false;
   }

   private boolean hasReachedTarget(MobEntity p_220486_1_, WalkTarget p_220486_2_) {
      return p_220486_2_.getTarget().getBlockPos().manhattanDistance(new BlockPos(p_220486_1_)) <= p_220486_2_.getDistance();
   }

   // $FF: synthetic method
   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, LivingEntity p_212834_2_, long p_212834_3_) {
      return this.shouldContinueExecuting(p_212834_1_, (MobEntity)p_212834_2_, p_212834_3_);
   }

   // $FF: synthetic method
   protected void resetTask(ServerWorld p_212835_1_, LivingEntity p_212835_2_, long p_212835_3_) {
      this.resetTask(p_212835_1_, (MobEntity)p_212835_2_, p_212835_3_);
   }

   // $FF: synthetic method
   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      this.startExecuting(p_212831_1_, (MobEntity)p_212831_2_, p_212831_3_);
   }
}
