package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.server.ServerWorld;

public class SwimTask extends Task<MobEntity> {
   private final float field_220589_a;
   private final float field_220590_b;

   public SwimTask(float p_i50339_1_, float p_i50339_2_) {
      super(ImmutableMap.of());
      this.field_220589_a = p_i50339_1_;
      this.field_220590_b = p_i50339_2_;
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, MobEntity p_212832_2_) {
      return p_212832_2_.isInWater() && p_212832_2_.getSubmergedHeight() > (double)this.field_220589_a || p_212832_2_.isInLava();
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, MobEntity p_212834_2_, long p_212834_3_) {
      return this.shouldExecute(p_212834_1_, p_212834_2_);
   }

   protected void updateTask(ServerWorld p_212833_1_, MobEntity p_212833_2_, long p_212833_3_) {
      if (p_212833_2_.getRNG().nextFloat() < this.field_220590_b) {
         p_212833_2_.getJumpController().setJumping();
      }

   }

   // $FF: synthetic method
   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, LivingEntity p_212834_2_, long p_212834_3_) {
      return this.shouldContinueExecuting(p_212834_1_, (MobEntity)p_212834_2_, p_212834_3_);
   }

   // $FF: synthetic method
   protected void updateTask(ServerWorld p_212833_1_, LivingEntity p_212833_2_, long p_212833_3_) {
      this.updateTask(p_212833_1_, (MobEntity)p_212833_2_, p_212833_3_);
   }
}
