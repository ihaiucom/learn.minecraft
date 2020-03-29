package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class CreateBabyVillagerTask extends Task<VillagerEntity> {
   private long field_220483_a;

   public CreateBabyVillagerTask() {
      super(ImmutableMap.of(MemoryModuleType.BREED_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT), 350, 350);
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, VillagerEntity p_212832_2_) {
      return this.func_220478_b(p_212832_2_);
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, VillagerEntity p_212834_2_, long p_212834_3_) {
      return p_212834_3_ <= this.field_220483_a && this.func_220478_b(p_212834_2_);
   }

   protected void startExecuting(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      VillagerEntity lvt_5_1_ = this.func_220482_a(p_212831_2_);
      BrainUtil.func_220618_a(p_212831_2_, lvt_5_1_);
      p_212831_1_.setEntityState(lvt_5_1_, (byte)18);
      p_212831_1_.setEntityState(p_212831_2_, (byte)18);
      int lvt_6_1_ = 275 + p_212831_2_.getRNG().nextInt(50);
      this.field_220483_a = p_212831_3_ + (long)lvt_6_1_;
   }

   protected void updateTask(ServerWorld p_212833_1_, VillagerEntity p_212833_2_, long p_212833_3_) {
      VillagerEntity lvt_5_1_ = this.func_220482_a(p_212833_2_);
      if (p_212833_2_.getDistanceSq(lvt_5_1_) <= 5.0D) {
         BrainUtil.func_220618_a(p_212833_2_, lvt_5_1_);
         if (p_212833_3_ >= this.field_220483_a) {
            p_212833_2_.func_223346_ep();
            lvt_5_1_.func_223346_ep();
            this.func_223521_a(p_212833_1_, p_212833_2_, lvt_5_1_);
         } else if (p_212833_2_.getRNG().nextInt(35) == 0) {
            p_212833_1_.setEntityState(lvt_5_1_, (byte)12);
            p_212833_1_.setEntityState(p_212833_2_, (byte)12);
         }

      }
   }

   private void func_223521_a(ServerWorld p_223521_1_, VillagerEntity p_223521_2_, VillagerEntity p_223521_3_) {
      Optional<BlockPos> lvt_4_1_ = this.func_220479_b(p_223521_1_, p_223521_2_);
      if (!lvt_4_1_.isPresent()) {
         p_223521_1_.setEntityState(p_223521_3_, (byte)13);
         p_223521_1_.setEntityState(p_223521_2_, (byte)13);
      } else {
         Optional<VillagerEntity> lvt_5_1_ = this.func_220480_a(p_223521_2_, p_223521_3_);
         if (lvt_5_1_.isPresent()) {
            this.func_220477_a(p_223521_1_, (VillagerEntity)lvt_5_1_.get(), (BlockPos)lvt_4_1_.get());
         } else {
            p_223521_1_.func_217443_B().func_219142_b((BlockPos)lvt_4_1_.get());
            DebugPacketSender.func_218801_c(p_223521_1_, (BlockPos)lvt_4_1_.get());
         }
      }

   }

   protected void resetTask(ServerWorld p_212835_1_, VillagerEntity p_212835_2_, long p_212835_3_) {
      p_212835_2_.getBrain().removeMemory(MemoryModuleType.BREED_TARGET);
   }

   private VillagerEntity func_220482_a(VillagerEntity p_220482_1_) {
      return (VillagerEntity)p_220482_1_.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
   }

   private boolean func_220478_b(VillagerEntity p_220478_1_) {
      Brain<VillagerEntity> lvt_2_1_ = p_220478_1_.getBrain();
      if (!lvt_2_1_.getMemory(MemoryModuleType.BREED_TARGET).isPresent()) {
         return false;
      } else {
         VillagerEntity lvt_3_1_ = this.func_220482_a(p_220478_1_);
         return BrainUtil.isCorrectVisibleType(lvt_2_1_, MemoryModuleType.BREED_TARGET, EntityType.VILLAGER) && p_220478_1_.canBreed() && lvt_3_1_.canBreed();
      }
   }

   private Optional<BlockPos> func_220479_b(ServerWorld p_220479_1_, VillagerEntity p_220479_2_) {
      return p_220479_1_.func_217443_B().func_219157_a(PointOfInterestType.HOME.func_221045_c(), (p_220481_2_) -> {
         return this.func_223520_a(p_220479_2_, p_220481_2_);
      }, new BlockPos(p_220479_2_), 48);
   }

   private boolean func_223520_a(VillagerEntity p_223520_1_, BlockPos p_223520_2_) {
      Path lvt_3_1_ = p_223520_1_.getNavigator().getPathToPos(p_223520_2_, PointOfInterestType.HOME.func_225478_d());
      return lvt_3_1_ != null && lvt_3_1_.func_224771_h();
   }

   private Optional<VillagerEntity> func_220480_a(VillagerEntity p_220480_1_, VillagerEntity p_220480_2_) {
      VillagerEntity lvt_3_1_ = p_220480_1_.createChild(p_220480_2_);
      if (lvt_3_1_ == null) {
         return Optional.empty();
      } else {
         p_220480_1_.setGrowingAge(6000);
         p_220480_2_.setGrowingAge(6000);
         lvt_3_1_.setGrowingAge(-24000);
         lvt_3_1_.setLocationAndAngles(p_220480_1_.func_226277_ct_(), p_220480_1_.func_226278_cu_(), p_220480_1_.func_226281_cx_(), 0.0F, 0.0F);
         p_220480_1_.world.addEntity(lvt_3_1_);
         p_220480_1_.world.setEntityState(lvt_3_1_, (byte)12);
         return Optional.of(lvt_3_1_);
      }
   }

   private void func_220477_a(ServerWorld p_220477_1_, VillagerEntity p_220477_2_, BlockPos p_220477_3_) {
      GlobalPos lvt_4_1_ = GlobalPos.of(p_220477_1_.getDimension().getType(), p_220477_3_);
      p_220477_2_.getBrain().setMemory(MemoryModuleType.HOME, (Object)lvt_4_1_);
   }

   // $FF: synthetic method
   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, LivingEntity p_212834_2_, long p_212834_3_) {
      return this.shouldContinueExecuting(p_212834_1_, (VillagerEntity)p_212834_2_, p_212834_3_);
   }

   // $FF: synthetic method
   protected void resetTask(ServerWorld p_212835_1_, LivingEntity p_212835_2_, long p_212835_3_) {
      this.resetTask(p_212835_1_, (VillagerEntity)p_212835_2_, p_212835_3_);
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
