package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class CongregateTask extends Task<LivingEntity> {
   public CongregateTask() {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.MEETING_POINT, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.INTERACTION_TARGET, MemoryModuleStatus.VALUE_ABSENT));
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      Brain<?> lvt_3_1_ = p_212832_2_.getBrain();
      Optional<GlobalPos> lvt_4_1_ = lvt_3_1_.getMemory(MemoryModuleType.MEETING_POINT);
      return p_212832_1_.getRandom().nextInt(100) == 0 && lvt_4_1_.isPresent() && Objects.equals(p_212832_1_.getDimension().getType(), ((GlobalPos)lvt_4_1_.get()).getDimension()) && ((GlobalPos)lvt_4_1_.get()).getPos().withinDistance(p_212832_2_.getPositionVec(), 4.0D) && ((List)lvt_3_1_.getMemory(MemoryModuleType.VISIBLE_MOBS).get()).stream().anyMatch((p_220570_0_) -> {
         return EntityType.VILLAGER.equals(p_220570_0_.getType());
      });
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Brain<?> lvt_5_1_ = p_212831_2_.getBrain();
      lvt_5_1_.getMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent((p_220568_2_) -> {
         p_220568_2_.stream().filter((p_220572_0_) -> {
            return EntityType.VILLAGER.equals(p_220572_0_.getType());
         }).filter((p_220571_1_) -> {
            return p_220571_1_.getDistanceSq(p_212831_2_) <= 32.0D;
         }).findFirst().ifPresent((p_220569_1_) -> {
            lvt_5_1_.setMemory(MemoryModuleType.INTERACTION_TARGET, (Object)p_220569_1_);
            lvt_5_1_.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityPosWrapper(p_220569_1_)));
            lvt_5_1_.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(new EntityPosWrapper(p_220569_1_), 0.3F, 1)));
         });
      });
   }
}
