package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class FindHidingPlaceTask extends Task<LivingEntity> {
   private final float field_220457_a;
   private final int field_220458_b;
   private final int field_220459_c;
   private Optional<BlockPos> field_220460_d = Optional.empty();

   public FindHidingPlaceTask(int p_i50361_1_, float p_i50361_2_, int p_i50361_3_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.HOME, MemoryModuleStatus.REGISTERED, MemoryModuleType.HIDING_PLACE, MemoryModuleStatus.REGISTERED));
      this.field_220458_b = p_i50361_1_;
      this.field_220457_a = p_i50361_2_;
      this.field_220459_c = p_i50361_3_;
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, LivingEntity p_212832_2_) {
      Optional<BlockPos> lvt_3_1_ = p_212832_1_.func_217443_B().func_219127_a((p_220454_0_) -> {
         return p_220454_0_ == PointOfInterestType.HOME;
      }, (p_220456_0_) -> {
         return true;
      }, new BlockPos(p_212832_2_), this.field_220459_c + 1, PointOfInterestManager.Status.ANY);
      if (lvt_3_1_.isPresent() && ((BlockPos)lvt_3_1_.get()).withinDistance(p_212832_2_.getPositionVec(), (double)this.field_220459_c)) {
         this.field_220460_d = lvt_3_1_;
      } else {
         this.field_220460_d = Optional.empty();
      }

      return true;
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Brain<?> lvt_5_1_ = p_212831_2_.getBrain();
      Optional<BlockPos> lvt_6_1_ = this.field_220460_d;
      if (!lvt_6_1_.isPresent()) {
         lvt_6_1_ = p_212831_1_.func_217443_B().func_219163_a((p_220453_0_) -> {
            return p_220453_0_ == PointOfInterestType.HOME;
         }, (p_220455_0_) -> {
            return true;
         }, PointOfInterestManager.Status.ANY, new BlockPos(p_212831_2_), this.field_220458_b, p_212831_2_.getRNG());
         if (!lvt_6_1_.isPresent()) {
            Optional<GlobalPos> lvt_7_1_ = lvt_5_1_.getMemory(MemoryModuleType.HOME);
            if (lvt_7_1_.isPresent()) {
               lvt_6_1_ = Optional.of(((GlobalPos)lvt_7_1_.get()).getPos());
            }
         }
      }

      if (lvt_6_1_.isPresent()) {
         lvt_5_1_.removeMemory(MemoryModuleType.PATH);
         lvt_5_1_.removeMemory(MemoryModuleType.LOOK_TARGET);
         lvt_5_1_.removeMemory(MemoryModuleType.BREED_TARGET);
         lvt_5_1_.removeMemory(MemoryModuleType.INTERACTION_TARGET);
         lvt_5_1_.setMemory(MemoryModuleType.HIDING_PLACE, (Object)GlobalPos.of(p_212831_1_.getDimension().getType(), (BlockPos)lvt_6_1_.get()));
         if (!((BlockPos)lvt_6_1_.get()).withinDistance(p_212831_2_.getPositionVec(), (double)this.field_220459_c)) {
            lvt_5_1_.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget((BlockPos)lvt_6_1_.get(), this.field_220457_a, this.field_220459_c)));
         }
      }

   }
}
