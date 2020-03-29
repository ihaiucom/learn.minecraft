package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public class LookTask extends Task<MobEntity> {
   public LookTask(int p_i50358_1_, int p_i50358_2_) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_PRESENT), p_i50358_1_, p_i50358_2_);
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, MobEntity p_212834_2_, long p_212834_3_) {
      return p_212834_2_.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).filter((p_220485_1_) -> {
         return p_220485_1_.isVisibleTo(p_212834_2_);
      }).isPresent();
   }

   protected void resetTask(ServerWorld p_212835_1_, MobEntity p_212835_2_, long p_212835_3_) {
      p_212835_2_.getBrain().removeMemory(MemoryModuleType.LOOK_TARGET);
   }

   protected void updateTask(ServerWorld p_212833_1_, MobEntity p_212833_2_, long p_212833_3_) {
      p_212833_2_.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).ifPresent((p_220484_1_) -> {
         p_212833_2_.getLookController().func_220674_a(p_220484_1_.getPos());
      });
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
   protected void updateTask(ServerWorld p_212833_1_, LivingEntity p_212833_2_, long p_212833_3_) {
      this.updateTask(p_212833_1_, (MobEntity)p_212833_2_, p_212833_3_);
   }
}
