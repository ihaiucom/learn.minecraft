package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

public class FindHidingPlaceDuringRaidTask extends FindHidingPlaceTask {
   public FindHidingPlaceDuringRaidTask(int p_i50360_1_, float p_i50360_2_) {
      super(p_i50360_1_, p_i50360_2_, 1);
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      Raid lvt_3_1_ = p_212832_1_.findRaid(new BlockPos(p_212832_2_));
      return super.shouldExecute(p_212832_1_, p_212832_2_) && lvt_3_1_ != null && lvt_3_1_.isActive() && !lvt_3_1_.isVictory() && !lvt_3_1_.isLoss();
   }
}
