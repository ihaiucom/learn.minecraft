package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

public class ForgetRaidTask extends Task<LivingEntity> {
   public ForgetRaidTask() {
      super(ImmutableMap.of());
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      return p_212832_1_.rand.nextInt(20) == 0;
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Brain<?> lvt_5_1_ = p_212831_2_.getBrain();
      Raid lvt_6_1_ = p_212831_1_.findRaid(new BlockPos(p_212831_2_));
      if (lvt_6_1_ == null || lvt_6_1_.isStopped() || lvt_6_1_.isLoss()) {
         lvt_5_1_.setFallbackActivity(Activity.IDLE);
         lvt_5_1_.updateActivity(p_212831_1_.getDayTime(), p_212831_1_.getGameTime());
      }

   }
}
