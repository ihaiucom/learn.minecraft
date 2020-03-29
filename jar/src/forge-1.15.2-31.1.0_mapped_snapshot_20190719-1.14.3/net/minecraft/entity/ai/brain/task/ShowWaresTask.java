package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class ShowWaresTask extends Task<VillagerEntity> {
   @Nullable
   private ItemStack field_220559_a;
   private final List<ItemStack> field_220560_b = Lists.newArrayList();
   private int field_220561_c;
   private int field_220562_d;
   private int field_220563_e;

   public ShowWaresTask(int p_i50343_1_, int p_i50343_2_) {
      super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryModuleStatus.VALUE_PRESENT), p_i50343_1_, p_i50343_2_);
   }

   public boolean shouldExecute(ServerWorld p_212832_1_, VillagerEntity p_212832_2_) {
      Brain<?> lvt_3_1_ = p_212832_2_.getBrain();
      if (!lvt_3_1_.getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent()) {
         return false;
      } else {
         LivingEntity lvt_4_1_ = (LivingEntity)lvt_3_1_.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
         return lvt_4_1_.getType() == EntityType.PLAYER && p_212832_2_.isAlive() && lvt_4_1_.isAlive() && !p_212832_2_.isChild() && p_212832_2_.getDistanceSq(lvt_4_1_) <= 17.0D;
      }
   }

   public boolean shouldContinueExecuting(ServerWorld p_212834_1_, VillagerEntity p_212834_2_, long p_212834_3_) {
      return this.shouldExecute(p_212834_1_, p_212834_2_) && this.field_220563_e > 0 && p_212834_2_.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
   }

   public void startExecuting(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      super.startExecuting(p_212831_1_, p_212831_2_, p_212831_3_);
      this.func_220557_c(p_212831_2_);
      this.field_220561_c = 0;
      this.field_220562_d = 0;
      this.field_220563_e = 40;
   }

   public void updateTask(ServerWorld p_212833_1_, VillagerEntity p_212833_2_, long p_212833_3_) {
      LivingEntity lvt_5_1_ = this.func_220557_c(p_212833_2_);
      this.func_220556_a(lvt_5_1_, p_212833_2_);
      if (!this.field_220560_b.isEmpty()) {
         this.func_220553_d(p_212833_2_);
      } else {
         p_212833_2_.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
         this.field_220563_e = Math.min(this.field_220563_e, 40);
      }

      --this.field_220563_e;
   }

   public void resetTask(ServerWorld p_212835_1_, VillagerEntity p_212835_2_, long p_212835_3_) {
      super.resetTask(p_212835_1_, p_212835_2_, p_212835_3_);
      p_212835_2_.getBrain().removeMemory(MemoryModuleType.INTERACTION_TARGET);
      p_212835_2_.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
      this.field_220559_a = null;
   }

   private void func_220556_a(LivingEntity p_220556_1_, VillagerEntity p_220556_2_) {
      boolean lvt_3_1_ = false;
      ItemStack lvt_4_1_ = p_220556_1_.getHeldItemMainhand();
      if (this.field_220559_a == null || !ItemStack.areItemsEqual(this.field_220559_a, lvt_4_1_)) {
         this.field_220559_a = lvt_4_1_;
         lvt_3_1_ = true;
         this.field_220560_b.clear();
      }

      if (lvt_3_1_ && !this.field_220559_a.isEmpty()) {
         this.func_220555_b(p_220556_2_);
         if (!this.field_220560_b.isEmpty()) {
            this.field_220563_e = 900;
            this.func_220558_a(p_220556_2_);
         }
      }

   }

   private void func_220558_a(VillagerEntity p_220558_1_) {
      p_220558_1_.setItemStackToSlot(EquipmentSlotType.MAINHAND, (ItemStack)this.field_220560_b.get(0));
   }

   private void func_220555_b(VillagerEntity p_220555_1_) {
      Iterator var2 = p_220555_1_.getOffers().iterator();

      while(var2.hasNext()) {
         MerchantOffer lvt_3_1_ = (MerchantOffer)var2.next();
         if (!lvt_3_1_.func_222217_o() && this.func_220554_a(lvt_3_1_)) {
            this.field_220560_b.add(lvt_3_1_.func_222200_d());
         }
      }

   }

   private boolean func_220554_a(MerchantOffer p_220554_1_) {
      return ItemStack.areItemsEqual(this.field_220559_a, p_220554_1_.func_222205_b()) || ItemStack.areItemsEqual(this.field_220559_a, p_220554_1_.func_222202_c());
   }

   private LivingEntity func_220557_c(VillagerEntity p_220557_1_) {
      Brain<?> lvt_2_1_ = p_220557_1_.getBrain();
      LivingEntity lvt_3_1_ = (LivingEntity)lvt_2_1_.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
      lvt_2_1_.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityPosWrapper(lvt_3_1_)));
      return lvt_3_1_;
   }

   private void func_220553_d(VillagerEntity p_220553_1_) {
      if (this.field_220560_b.size() >= 2 && ++this.field_220561_c >= 40) {
         ++this.field_220562_d;
         this.field_220561_c = 0;
         if (this.field_220562_d > this.field_220560_b.size() - 1) {
            this.field_220562_d = 0;
         }

         p_220553_1_.setItemStackToSlot(EquipmentSlotType.MAINHAND, (ItemStack)this.field_220560_b.get(this.field_220562_d));
      }

   }

   // $FF: synthetic method
   public boolean shouldContinueExecuting(ServerWorld p_212834_1_, LivingEntity p_212834_2_, long p_212834_3_) {
      return this.shouldContinueExecuting(p_212834_1_, (VillagerEntity)p_212834_2_, p_212834_3_);
   }

   // $FF: synthetic method
   public void resetTask(ServerWorld p_212835_1_, LivingEntity p_212835_2_, long p_212835_3_) {
      this.resetTask(p_212835_1_, (VillagerEntity)p_212835_2_, p_212835_3_);
   }

   // $FF: synthetic method
   public void updateTask(ServerWorld p_212833_1_, LivingEntity p_212833_2_, long p_212833_3_) {
      this.updateTask(p_212833_1_, (VillagerEntity)p_212833_2_, p_212833_3_);
   }

   // $FF: synthetic method
   public void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      this.startExecuting(p_212831_1_, (VillagerEntity)p_212831_2_, p_212831_3_);
   }
}
