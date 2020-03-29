package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class InteractableDoorsSensor extends Sensor<LivingEntity> {
   protected void update(ServerWorld p_212872_1_, LivingEntity p_212872_2_) {
      DimensionType lvt_3_1_ = p_212872_1_.getDimension().getType();
      BlockPos lvt_4_1_ = new BlockPos(p_212872_2_);
      List<GlobalPos> lvt_5_1_ = Lists.newArrayList();

      for(int lvt_6_1_ = -1; lvt_6_1_ <= 1; ++lvt_6_1_) {
         for(int lvt_7_1_ = -1; lvt_7_1_ <= 1; ++lvt_7_1_) {
            for(int lvt_8_1_ = -1; lvt_8_1_ <= 1; ++lvt_8_1_) {
               BlockPos lvt_9_1_ = lvt_4_1_.add(lvt_6_1_, lvt_7_1_, lvt_8_1_);
               if (p_212872_1_.getBlockState(lvt_9_1_).isIn(BlockTags.WOODEN_DOORS)) {
                  lvt_5_1_.add(GlobalPos.of(lvt_3_1_, lvt_9_1_));
               }
            }
         }
      }

      Brain<?> lvt_6_2_ = p_212872_2_.getBrain();
      if (!lvt_5_1_.isEmpty()) {
         lvt_6_2_.setMemory(MemoryModuleType.INTERACTABLE_DOORS, (Object)lvt_5_1_);
      } else {
         lvt_6_2_.removeMemory(MemoryModuleType.INTERACTABLE_DOORS);
      }

   }

   public Set<MemoryModuleType<?>> getUsedMemories() {
      return ImmutableSet.of(MemoryModuleType.INTERACTABLE_DOORS);
   }
}
