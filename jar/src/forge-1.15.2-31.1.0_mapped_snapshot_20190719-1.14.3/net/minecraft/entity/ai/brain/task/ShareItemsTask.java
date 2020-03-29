package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;

public class ShareItemsTask extends Task<VillagerEntity> {
   private Set<Item> field_220588_a = ImmutableSet.of();

   public ShareItemsTask() {
      super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT));
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, VillagerEntity p_212832_2_) {
      return BrainUtil.isCorrectVisibleType(p_212832_2_.getBrain(), MemoryModuleType.INTERACTION_TARGET, EntityType.VILLAGER);
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, VillagerEntity p_212834_2_, long p_212834_3_) {
      return this.shouldExecute(p_212834_1_, p_212834_2_);
   }

   protected void startExecuting(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      VillagerEntity lvt_5_1_ = (VillagerEntity)p_212831_2_.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
      BrainUtil.func_220618_a(p_212831_2_, lvt_5_1_);
      this.field_220588_a = func_220585_a(p_212831_2_, lvt_5_1_);
   }

   protected void updateTask(ServerWorld p_212833_1_, VillagerEntity p_212833_2_, long p_212833_3_) {
      VillagerEntity lvt_5_1_ = (VillagerEntity)p_212833_2_.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
      if (p_212833_2_.getDistanceSq(lvt_5_1_) <= 5.0D) {
         BrainUtil.func_220618_a(p_212833_2_, lvt_5_1_);
         p_212833_2_.func_213746_a(lvt_5_1_, p_212833_3_);
         if (p_212833_2_.canAbondonItems() && (p_212833_2_.getVillagerData().getProfession() == VillagerProfession.FARMER || lvt_5_1_.wantsMoreFood())) {
            func_220586_a(p_212833_2_, VillagerEntity.field_213788_bA.keySet(), lvt_5_1_);
         }

         if (!this.field_220588_a.isEmpty() && p_212833_2_.func_213715_ed().hasAny(this.field_220588_a)) {
            func_220586_a(p_212833_2_, this.field_220588_a, lvt_5_1_);
         }

      }
   }

   protected void resetTask(ServerWorld p_212835_1_, VillagerEntity p_212835_2_, long p_212835_3_) {
      p_212835_2_.getBrain().removeMemory(MemoryModuleType.INTERACTION_TARGET);
   }

   private static Set<Item> func_220585_a(VillagerEntity p_220585_0_, VillagerEntity p_220585_1_) {
      ImmutableSet<Item> lvt_2_1_ = p_220585_1_.getVillagerData().getProfession().func_221146_c();
      ImmutableSet<Item> lvt_3_1_ = p_220585_0_.getVillagerData().getProfession().func_221146_c();
      return (Set)lvt_2_1_.stream().filter((p_220587_1_) -> {
         return !lvt_3_1_.contains(p_220587_1_);
      }).collect(Collectors.toSet());
   }

   private static void func_220586_a(VillagerEntity p_220586_0_, Set<Item> p_220586_1_, LivingEntity p_220586_2_) {
      Inventory lvt_3_1_ = p_220586_0_.func_213715_ed();
      ItemStack lvt_4_1_ = ItemStack.EMPTY;
      int lvt_5_1_ = 0;

      while(lvt_5_1_ < lvt_3_1_.getSizeInventory()) {
         ItemStack lvt_6_1_;
         Item lvt_7_1_;
         int lvt_8_2_;
         label28: {
            lvt_6_1_ = lvt_3_1_.getStackInSlot(lvt_5_1_);
            if (!lvt_6_1_.isEmpty()) {
               lvt_7_1_ = lvt_6_1_.getItem();
               if (p_220586_1_.contains(lvt_7_1_)) {
                  if (lvt_6_1_.getCount() > lvt_6_1_.getMaxStackSize() / 2) {
                     lvt_8_2_ = lvt_6_1_.getCount() / 2;
                     break label28;
                  }

                  if (lvt_6_1_.getCount() > 24) {
                     lvt_8_2_ = lvt_6_1_.getCount() - 24;
                     break label28;
                  }
               }
            }

            ++lvt_5_1_;
            continue;
         }

         lvt_6_1_.shrink(lvt_8_2_);
         lvt_4_1_ = new ItemStack(lvt_7_1_, lvt_8_2_);
         break;
      }

      if (!lvt_4_1_.isEmpty()) {
         BrainUtil.throwItemAt(p_220586_0_, lvt_4_1_, p_220586_2_);
      }

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
