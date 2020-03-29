package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.server.ServerWorld;

public class WalkToPOITask extends Task<VillagerEntity> {
   private final float field_225445_a;
   private final int field_225446_b;

   public WalkToPOITask(float p_i51557_1_, int p_i51557_2_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
      this.field_225445_a = p_i51557_1_;
      this.field_225446_b = p_i51557_2_;
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, VillagerEntity p_212832_2_) {
      return !p_212832_1_.func_217483_b_(new BlockPos(p_212832_2_));
   }

   protected void startExecuting(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      PointOfInterestManager lvt_5_1_ = p_212831_1_.func_217443_B();
      int lvt_6_1_ = lvt_5_1_.func_219150_a(SectionPos.from(new BlockPos(p_212831_2_)));
      Vec3d lvt_7_1_ = null;

      for(int lvt_8_1_ = 0; lvt_8_1_ < 5; ++lvt_8_1_) {
         Vec3d lvt_9_1_ = RandomPositionGenerator.func_221024_a(p_212831_2_, 15, 7, (p_225444_1_) -> {
            return (double)(-p_212831_1_.func_217486_a(SectionPos.from(p_225444_1_)));
         });
         if (lvt_9_1_ != null) {
            int lvt_10_1_ = lvt_5_1_.func_219150_a(SectionPos.from(new BlockPos(lvt_9_1_)));
            if (lvt_10_1_ < lvt_6_1_) {
               lvt_7_1_ = lvt_9_1_;
               break;
            }

            if (lvt_10_1_ == lvt_6_1_) {
               lvt_7_1_ = lvt_9_1_;
            }
         }
      }

      if (lvt_7_1_ != null) {
         p_212831_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(lvt_7_1_, this.field_225445_a, this.field_225446_b)));
      }

   }
}
