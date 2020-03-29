package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.LongSerializable;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class SpawnGolemTask extends Task<VillagerEntity> {
   private long field_225461_a;

   public SpawnGolemTask() {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED));
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, VillagerEntity p_212832_2_) {
      if (p_212832_1_.getGameTime() - this.field_225461_a < 300L) {
         return false;
      } else if (p_212832_1_.rand.nextInt(2) != 0) {
         return false;
      } else {
         this.field_225461_a = p_212832_1_.getGameTime();
         GlobalPos lvt_3_1_ = (GlobalPos)p_212832_2_.getBrain().getMemory(MemoryModuleType.JOB_SITE).get();
         return Objects.equals(lvt_3_1_.getDimension(), p_212832_1_.getDimension().getType()) && lvt_3_1_.getPos().withinDistance(p_212832_2_.getPositionVec(), 1.73D);
      }
   }

   protected void startExecuting(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      Brain<VillagerEntity> lvt_5_1_ = p_212831_2_.getBrain();
      lvt_5_1_.setMemory(MemoryModuleType.LAST_WORKED_AT_POI, (Object)LongSerializable.of(p_212831_3_));
      lvt_5_1_.getMemory(MemoryModuleType.JOB_SITE).ifPresent((p_225460_1_) -> {
         lvt_5_1_.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new BlockPosWrapper(p_225460_1_.getPos())));
      });
      p_212831_2_.playWorkstationSound();
      if (p_212831_2_.func_223721_ek()) {
         p_212831_2_.func_213766_ei();
      }

   }
}
