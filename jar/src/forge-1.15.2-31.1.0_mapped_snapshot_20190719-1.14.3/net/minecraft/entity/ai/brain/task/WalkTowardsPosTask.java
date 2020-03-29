package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class WalkTowardsPosTask extends Task<CreatureEntity> {
   private final MemoryModuleType<GlobalPos> field_220581_a;
   private final int field_220582_b;
   private final int field_220583_c;
   private long field_220584_d;

   public WalkTowardsPosTask(MemoryModuleType<GlobalPos> p_i50341_1_, int p_i50341_2_, int p_i50341_3_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, p_i50341_1_, MemoryModuleStatus.VALUE_PRESENT));
      this.field_220581_a = p_i50341_1_;
      this.field_220582_b = p_i50341_2_;
      this.field_220583_c = p_i50341_3_;
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, CreatureEntity p_212832_2_) {
      Optional<GlobalPos> lvt_3_1_ = p_212832_2_.getBrain().getMemory(this.field_220581_a);
      return lvt_3_1_.isPresent() && Objects.equals(p_212832_1_.getDimension().getType(), ((GlobalPos)lvt_3_1_.get()).getDimension()) && ((GlobalPos)lvt_3_1_.get()).getPos().withinDistance(p_212832_2_.getPositionVec(), (double)this.field_220583_c);
   }

   protected void startExecuting(ServerWorld p_212831_1_, CreatureEntity p_212831_2_, long p_212831_3_) {
      if (p_212831_3_ > this.field_220584_d) {
         Brain<?> lvt_5_1_ = p_212831_2_.getBrain();
         Optional<GlobalPos> lvt_6_1_ = lvt_5_1_.getMemory(this.field_220581_a);
         lvt_6_1_.ifPresent((p_220580_2_) -> {
            lvt_5_1_.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(p_220580_2_.getPos(), 0.4F, this.field_220582_b)));
         });
         this.field_220584_d = p_212831_3_ + 80L;
      }

   }
}
