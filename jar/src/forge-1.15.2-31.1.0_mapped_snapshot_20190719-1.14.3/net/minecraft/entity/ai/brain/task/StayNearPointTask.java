package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class StayNearPointTask extends Task<VillagerEntity> {
   private final MemoryModuleType<GlobalPos> field_220548_a;
   private final float field_220549_b;
   private final int field_220550_c;
   private final int field_220551_d;
   private final int field_223018_e;

   public StayNearPointTask(MemoryModuleType<GlobalPos> p_i51501_1_, float p_i51501_2_, int p_i51501_3_, int p_i51501_4_, int p_i51501_5_) {
      super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, p_i51501_1_, MemoryModuleStatus.VALUE_PRESENT));
      this.field_220548_a = p_i51501_1_;
      this.field_220549_b = p_i51501_2_;
      this.field_220550_c = p_i51501_3_;
      this.field_220551_d = p_i51501_4_;
      this.field_223018_e = p_i51501_5_;
   }

   private void func_225457_a(VillagerEntity p_225457_1_, long p_225457_2_) {
      Brain<?> lvt_4_1_ = p_225457_1_.getBrain();
      p_225457_1_.func_213742_a(this.field_220548_a);
      lvt_4_1_.removeMemory(this.field_220548_a);
      lvt_4_1_.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object)p_225457_2_);
   }

   protected void startExecuting(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      Brain<?> lvt_5_1_ = p_212831_2_.getBrain();
      lvt_5_1_.getMemory(this.field_220548_a).ifPresent((p_220545_6_) -> {
         if (this.func_223017_a(p_212831_1_, p_212831_2_)) {
            this.func_225457_a(p_212831_2_, p_212831_3_);
         } else if (this.func_220546_a(p_212831_1_, p_212831_2_, p_220545_6_)) {
            Vec3d lvt_7_1_ = null;
            int lvt_8_1_ = 0;

            for(boolean var9 = true; lvt_8_1_ < 1000 && (lvt_7_1_ == null || this.func_220546_a(p_212831_1_, p_212831_2_, GlobalPos.of(p_212831_2_.dimension, new BlockPos(lvt_7_1_)))); ++lvt_8_1_) {
               lvt_7_1_ = RandomPositionGenerator.findRandomTargetBlockTowards(p_212831_2_, 15, 7, new Vec3d(p_220545_6_.getPos()));
            }

            if (lvt_8_1_ == 1000) {
               this.func_225457_a(p_212831_2_, p_212831_3_);
               return;
            }

            lvt_5_1_.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(lvt_7_1_, this.field_220549_b, this.field_220550_c)));
         } else if (!this.func_220547_b(p_212831_1_, p_212831_2_, p_220545_6_)) {
            lvt_5_1_.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(p_220545_6_.getPos(), this.field_220549_b, this.field_220550_c)));
         }

      });
   }

   private boolean func_223017_a(ServerWorld p_223017_1_, VillagerEntity p_223017_2_) {
      Optional<Long> lvt_3_1_ = p_223017_2_.getBrain().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      if (lvt_3_1_.isPresent()) {
         return p_223017_1_.getGameTime() - (Long)lvt_3_1_.get() > (long)this.field_223018_e;
      } else {
         return false;
      }
   }

   private boolean func_220546_a(ServerWorld p_220546_1_, VillagerEntity p_220546_2_, GlobalPos p_220546_3_) {
      return p_220546_3_.getDimension() != p_220546_1_.getDimension().getType() || p_220546_3_.getPos().manhattanDistance(new BlockPos(p_220546_2_)) > this.field_220551_d;
   }

   private boolean func_220547_b(ServerWorld p_220547_1_, VillagerEntity p_220547_2_, GlobalPos p_220547_3_) {
      return p_220547_3_.getDimension() == p_220547_1_.getDimension().getType() && p_220547_3_.getPos().manhattanDistance(new BlockPos(p_220547_2_)) <= this.field_220550_c;
   }
}
