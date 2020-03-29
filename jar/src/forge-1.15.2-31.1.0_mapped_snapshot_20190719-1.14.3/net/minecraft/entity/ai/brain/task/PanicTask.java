package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.world.server.ServerWorld;

public class PanicTask extends Task<VillagerEntity> {
   public PanicTask() {
      super(ImmutableMap.of());
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, VillagerEntity p_212834_2_, long p_212834_3_) {
      return func_220512_b(p_212834_2_) || func_220513_a(p_212834_2_);
   }

   protected void startExecuting(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      if (func_220512_b(p_212831_2_) || func_220513_a(p_212831_2_)) {
         Brain<?> lvt_5_1_ = p_212831_2_.getBrain();
         if (!lvt_5_1_.hasActivity(Activity.PANIC)) {
            lvt_5_1_.removeMemory(MemoryModuleType.PATH);
            lvt_5_1_.removeMemory(MemoryModuleType.WALK_TARGET);
            lvt_5_1_.removeMemory(MemoryModuleType.LOOK_TARGET);
            lvt_5_1_.removeMemory(MemoryModuleType.BREED_TARGET);
            lvt_5_1_.removeMemory(MemoryModuleType.INTERACTION_TARGET);
         }

         lvt_5_1_.switchTo(Activity.PANIC);
      }

   }

   protected void updateTask(ServerWorld p_212833_1_, VillagerEntity p_212833_2_, long p_212833_3_) {
      if (p_212833_3_ % 100L == 0L) {
         p_212833_2_.func_223358_a(p_212833_3_, 3);
      }

   }

   public static boolean func_220513_a(LivingEntity p_220513_0_) {
      return p_220513_0_.getBrain().hasMemory(MemoryModuleType.NEAREST_HOSTILE);
   }

   public static boolean func_220512_b(LivingEntity p_220512_0_) {
      return p_220512_0_.getBrain().hasMemory(MemoryModuleType.HURT_BY);
   }

   // $FF: synthetic method
   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, LivingEntity p_212834_2_, long p_212834_3_) {
      return this.shouldContinueExecuting(p_212834_1_, (VillagerEntity)p_212834_2_, p_212834_3_);
   }

   // $FF: synthetic method
   protected void updateTask(ServerWorld p_212833_1_, LivingEntity p_212833_2_, long p_212833_3_) {
      this.updateTask(p_212833_1_, (VillagerEntity)p_212833_2_, p_212833_3_);
   }

   // $FF: synthetic method
   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      this.startExecuting(p_212831_1_, (VillagerEntity)p_212831_2_, p_212831_3_);
   }
}
