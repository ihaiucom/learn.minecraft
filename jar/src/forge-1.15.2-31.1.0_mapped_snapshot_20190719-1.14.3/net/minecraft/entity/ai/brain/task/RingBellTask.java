package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class RingBellTask extends Task<LivingEntity> {
   public RingBellTask() {
      super(ImmutableMap.of(MemoryModuleType.MEETING_POINT, MemoryModuleStatus.VALUE_PRESENT));
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      return p_212832_1_.rand.nextFloat() > 0.95F;
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Brain<?> lvt_5_1_ = p_212831_2_.getBrain();
      BlockPos lvt_6_1_ = ((GlobalPos)lvt_5_1_.getMemory(MemoryModuleType.MEETING_POINT).get()).getPos();
      if (lvt_6_1_.withinDistance(new BlockPos(p_212831_2_), 3.0D)) {
         BlockState lvt_7_1_ = p_212831_1_.getBlockState(lvt_6_1_);
         if (lvt_7_1_.getBlock() == Blocks.BELL) {
            BellBlock lvt_8_1_ = (BellBlock)lvt_7_1_.getBlock();
            lvt_8_1_.func_226885_a_(p_212831_1_, lvt_6_1_, (Direction)null);
         }
      }

   }
}
