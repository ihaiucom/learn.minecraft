package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

public class BeginRaidTask extends Task<LivingEntity> {
   public BeginRaidTask() {
      super(ImmutableMap.of());
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      return p_212832_1_.rand.nextInt(20) == 0;
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Brain<?> lvt_5_1_ = p_212831_2_.getBrain();
      Raid lvt_6_1_ = p_212831_1_.findRaid(new BlockPos(p_212831_2_));
      if (lvt_6_1_ != null) {
         if (lvt_6_1_.func_221297_c() && !lvt_6_1_.func_221334_b()) {
            lvt_5_1_.setFallbackActivity(Activity.RAID);
            lvt_5_1_.switchTo(Activity.RAID);
         } else {
            lvt_5_1_.setFallbackActivity(Activity.PRE_RAID);
            lvt_5_1_.switchTo(Activity.PRE_RAID);
         }
      }

   }
}
