package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class WalkRandomlyTask extends Task<CreatureEntity> {
   private final float field_220431_a;

   public WalkRandomlyTask(float p_i50364_1_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
      this.field_220431_a = p_i50364_1_;
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, CreatureEntity p_212832_2_) {
      return !p_212832_1_.func_226660_f_(new BlockPos(p_212832_2_));
   }

   protected void startExecuting(ServerWorld p_212831_1_, CreatureEntity p_212831_2_, long p_212831_3_) {
      BlockPos lvt_5_1_ = new BlockPos(p_212831_2_);
      List<BlockPos> lvt_6_1_ = (List)BlockPos.getAllInBox(lvt_5_1_.add(-1, -1, -1), lvt_5_1_.add(1, 1, 1)).map(BlockPos::toImmutable).collect(Collectors.toList());
      Collections.shuffle(lvt_6_1_);
      Optional<BlockPos> lvt_7_1_ = lvt_6_1_.stream().filter((p_220428_1_) -> {
         return !p_212831_1_.func_226660_f_(p_220428_1_);
      }).filter((p_220427_2_) -> {
         return p_212831_1_.func_217400_a(p_220427_2_, p_212831_2_);
      }).filter((p_220429_2_) -> {
         return p_212831_1_.func_226669_j_(p_212831_2_);
      }).findFirst();
      lvt_7_1_.ifPresent((p_220430_2_) -> {
         p_212831_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(p_220430_2_, this.field_220431_a, 0)));
      });
   }
}
