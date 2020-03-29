package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class TradeTask extends Task<VillagerEntity> {
   private final float field_220476_a;

   public TradeTask(float p_i50359_1_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED), Integer.MAX_VALUE);
      this.field_220476_a = p_i50359_1_;
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, VillagerEntity p_212832_2_) {
      PlayerEntity lvt_3_1_ = p_212832_2_.getCustomer();
      return p_212832_2_.isAlive() && lvt_3_1_ != null && !p_212832_2_.isInWater() && !p_212832_2_.velocityChanged && p_212832_2_.getDistanceSq(lvt_3_1_) <= 16.0D && lvt_3_1_.openContainer != null;
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, VillagerEntity p_212834_2_, long p_212834_3_) {
      return this.shouldExecute(p_212834_1_, p_212834_2_);
   }

   protected void startExecuting(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      this.func_220475_a(p_212831_2_);
   }

   protected void resetTask(ServerWorld p_212835_1_, VillagerEntity p_212835_2_, long p_212835_3_) {
      Brain<?> lvt_5_1_ = p_212835_2_.getBrain();
      lvt_5_1_.removeMemory(MemoryModuleType.WALK_TARGET);
      lvt_5_1_.removeMemory(MemoryModuleType.LOOK_TARGET);
   }

   protected void updateTask(ServerWorld p_212833_1_, VillagerEntity p_212833_2_, long p_212833_3_) {
      this.func_220475_a(p_212833_2_);
   }

   protected boolean isTimedOut(long p_220383_1_) {
      return false;
   }

   private void func_220475_a(VillagerEntity p_220475_1_) {
      EntityPosWrapper lvt_2_1_ = new EntityPosWrapper(p_220475_1_.getCustomer());
      Brain<?> lvt_3_1_ = p_220475_1_.getBrain();
      lvt_3_1_.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(lvt_2_1_, this.field_220476_a, 2)));
      lvt_3_1_.setMemory(MemoryModuleType.LOOK_TARGET, (Object)lvt_2_1_);
   }

   // $FF: synthetic method
   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, LivingEntity p_212834_2_, long p_212834_3_) {
      return this.shouldContinueExecuting(p_212834_1_, (VillagerEntity)p_212834_2_, p_212834_3_);
   }

   // $FF: synthetic method
   protected void resetTask(ServerWorld p_212835_1_, LivingEntity p_212835_2_, long p_212835_3_) {
      this.resetTask(p_212835_1_, (VillagerEntity)p_212835_2_, p_212835_3_);
   }

   // $FF: synthetic method
   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      this.startExecuting(p_212831_1_, (VillagerEntity)p_212831_2_, p_212831_3_);
   }
}
