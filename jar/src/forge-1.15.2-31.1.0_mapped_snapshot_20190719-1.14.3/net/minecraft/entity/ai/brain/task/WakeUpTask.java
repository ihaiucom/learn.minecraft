package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.world.server.ServerWorld;

public class WakeUpTask extends Task<LivingEntity> {
   public WakeUpTask() {
      super(ImmutableMap.of());
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      return !p_212832_2_.getBrain().hasActivity(Activity.REST) && p_212832_2_.isSleeping();
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      p_212831_2_.wakeUp();
   }
}
