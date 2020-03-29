package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class SecondaryPositionSensor extends Sensor<VillagerEntity> {
   public SecondaryPositionSensor() {
      super(40);
   }

   protected void update(ServerWorld p_212872_1_, VillagerEntity p_212872_2_) {
      DimensionType lvt_3_1_ = p_212872_1_.getDimension().getType();
      BlockPos lvt_4_1_ = new BlockPos(p_212872_2_);
      List<GlobalPos> lvt_5_1_ = Lists.newArrayList();
      int lvt_6_1_ = true;

      for(int lvt_7_1_ = -4; lvt_7_1_ <= 4; ++lvt_7_1_) {
         for(int lvt_8_1_ = -2; lvt_8_1_ <= 2; ++lvt_8_1_) {
            for(int lvt_9_1_ = -4; lvt_9_1_ <= 4; ++lvt_9_1_) {
               BlockPos lvt_10_1_ = lvt_4_1_.add(lvt_7_1_, lvt_8_1_, lvt_9_1_);
               if (p_212872_2_.getVillagerData().getProfession().func_221150_d().contains(p_212872_1_.getBlockState(lvt_10_1_).getBlock())) {
                  lvt_5_1_.add(GlobalPos.of(lvt_3_1_, lvt_10_1_));
               }
            }
         }
      }

      Brain<?> lvt_7_2_ = p_212872_2_.getBrain();
      if (!lvt_5_1_.isEmpty()) {
         lvt_7_2_.setMemory(MemoryModuleType.SECONDARY_JOB_SITE, (Object)lvt_5_1_);
      } else {
         lvt_7_2_.removeMemory(MemoryModuleType.SECONDARY_JOB_SITE);
      }

   }

   public Set<MemoryModuleType<?>> getUsedMemories() {
      return ImmutableSet.of(MemoryModuleType.SECONDARY_JOB_SITE);
   }
}
