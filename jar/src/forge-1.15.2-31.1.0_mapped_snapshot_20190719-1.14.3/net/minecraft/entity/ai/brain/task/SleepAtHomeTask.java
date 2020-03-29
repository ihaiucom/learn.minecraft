package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.LongSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class SleepAtHomeTask extends Task<LivingEntity> {
   private long field_220552_a;

   public SleepAtHomeTask() {
      super(ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.field_226332_A_, MemoryModuleStatus.REGISTERED));
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      if (p_212832_2_.isPassenger()) {
         return false;
      } else {
         Brain<?> lvt_3_1_ = p_212832_2_.getBrain();
         GlobalPos lvt_4_1_ = (GlobalPos)lvt_3_1_.getMemory(MemoryModuleType.HOME).get();
         if (!Objects.equals(p_212832_1_.getDimension().getType(), lvt_4_1_.getDimension())) {
            return false;
         } else {
            Optional<LongSerializable> lvt_5_1_ = lvt_3_1_.getMemory(MemoryModuleType.field_226332_A_);
            if (lvt_5_1_.isPresent() && p_212832_1_.getGameTime() - ((LongSerializable)lvt_5_1_.get()).func_223461_a() < 100L) {
               return false;
            } else {
               BlockState lvt_6_1_ = p_212832_1_.getBlockState(lvt_4_1_.getPos());
               return lvt_4_1_.getPos().withinDistance(p_212832_2_.getPositionVec(), 2.0D) && lvt_6_1_.getBlock().isIn(BlockTags.BEDS) && !(Boolean)lvt_6_1_.get(BedBlock.OCCUPIED);
            }
         }
      }
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, LivingEntity p_212834_2_, long p_212834_3_) {
      Optional<GlobalPos> lvt_5_1_ = p_212834_2_.getBrain().getMemory(MemoryModuleType.HOME);
      if (!lvt_5_1_.isPresent()) {
         return false;
      } else {
         BlockPos lvt_6_1_ = ((GlobalPos)lvt_5_1_.get()).getPos();
         return p_212834_2_.getBrain().hasActivity(Activity.REST) && p_212834_2_.func_226278_cu_() > (double)lvt_6_1_.getY() + 0.4D && lvt_6_1_.withinDistance(p_212834_2_.getPositionVec(), 1.14D);
      }
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      if (p_212831_3_ > this.field_220552_a) {
         p_212831_2_.getBrain().getMemory(MemoryModuleType.field_225462_q).ifPresent((p_225459_2_) -> {
            InteractWithDoorTask.func_225449_a(p_212831_1_, ImmutableList.of(), 0, p_212831_2_, p_212831_2_.getBrain());
         });
         p_212831_2_.startSleeping(((GlobalPos)p_212831_2_.getBrain().getMemory(MemoryModuleType.HOME).get()).getPos());
      }

   }

   protected boolean isTimedOut(long p_220383_1_) {
      return false;
   }

   protected void resetTask(ServerWorld p_212835_1_, LivingEntity p_212835_2_, long p_212835_3_) {
      if (p_212835_2_.isSleeping()) {
         p_212835_2_.wakeUp();
         this.field_220552_a = p_212835_3_ + 40L;
      }

   }
}
