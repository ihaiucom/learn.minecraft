package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class ExpireHidingTask extends Task<LivingEntity> {
   private final int field_220537_a;
   private final int field_220538_b;
   private int field_220539_c;

   public ExpireHidingTask(int p_i50349_1_, int p_i50349_2_) {
      super(ImmutableMap.of(MemoryModuleType.HIDING_PLACE, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleStatus.VALUE_PRESENT));
      this.field_220538_b = p_i50349_1_ * 20;
      this.field_220539_c = 0;
      this.field_220537_a = p_i50349_2_;
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Brain<?> lvt_5_1_ = p_212831_2_.getBrain();
      Optional<Long> lvt_6_1_ = lvt_5_1_.getMemory(MemoryModuleType.HEARD_BELL_TIME);
      boolean lvt_7_1_ = (Long)lvt_6_1_.get() + 300L <= p_212831_3_;
      if (this.field_220539_c <= this.field_220538_b && !lvt_7_1_) {
         BlockPos lvt_8_1_ = ((GlobalPos)lvt_5_1_.getMemory(MemoryModuleType.HIDING_PLACE).get()).getPos();
         if (lvt_8_1_.withinDistance(new BlockPos(p_212831_2_), (double)(this.field_220537_a + 1))) {
            ++this.field_220539_c;
         }

      } else {
         lvt_5_1_.removeMemory(MemoryModuleType.HEARD_BELL_TIME);
         lvt_5_1_.removeMemory(MemoryModuleType.HIDING_PLACE);
         lvt_5_1_.updateActivity(p_212831_1_.getDayTime(), p_212831_1_.getGameTime());
         this.field_220539_c = 0;
      }
   }
}
