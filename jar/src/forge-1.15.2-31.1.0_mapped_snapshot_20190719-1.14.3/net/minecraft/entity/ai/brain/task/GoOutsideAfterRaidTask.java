package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

public class GoOutsideAfterRaidTask extends MoveToSkylightTask {
   public GoOutsideAfterRaidTask(float p_i50365_1_) {
      super(p_i50365_1_);
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      Raid lvt_3_1_ = p_212832_1_.findRaid(new BlockPos(p_212832_2_));
      return lvt_3_1_ != null && lvt_3_1_.isVictory() && super.shouldExecute(p_212832_1_, p_212832_2_);
   }
}
