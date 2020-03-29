package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.world.server.ServerWorld;

public class ChangeJobTask extends Task<VillagerEntity> {
   public ChangeJobTask() {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_ABSENT));
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, VillagerEntity p_212832_2_) {
      VillagerData lvt_3_1_ = p_212832_2_.getVillagerData();
      return lvt_3_1_.getProfession() != VillagerProfession.NONE && lvt_3_1_.getProfession() != VillagerProfession.NITWIT && p_212832_2_.getXp() == 0 && lvt_3_1_.getLevel() <= 1;
   }

   protected void startExecuting(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      p_212831_2_.setVillagerData(p_212831_2_.getVillagerData().withProfession(VillagerProfession.NONE));
      p_212831_2_.resetBrain(p_212831_1_);
   }
}
