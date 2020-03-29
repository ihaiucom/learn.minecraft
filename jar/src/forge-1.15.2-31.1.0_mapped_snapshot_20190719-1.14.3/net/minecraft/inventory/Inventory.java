package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.NonNullList;

public class Inventory implements IInventory, IRecipeHelperPopulator {
   private final int slotsCount;
   private final NonNullList<ItemStack> inventoryContents;
   private List<IInventoryChangedListener> listeners;

   public Inventory(int p_i50397_1_) {
      this.slotsCount = p_i50397_1_;
      this.inventoryContents = NonNullList.withSize(p_i50397_1_, ItemStack.EMPTY);
   }

   public Inventory(ItemStack... p_i50398_1_) {
      this.slotsCount = p_i50398_1_.length;
      this.inventoryContents = NonNullList.from(ItemStack.EMPTY, p_i50398_1_);
   }

   public void addListener(IInventoryChangedListener p_110134_1_) {
      if (this.listeners == null) {
         this.listeners = Lists.newArrayList();
      }

      this.listeners.add(p_110134_1_);
   }

   public void removeListener(IInventoryChangedListener p_110132_1_) {
      this.listeners.remove(p_110132_1_);
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      return p_70301_1_ >= 0 && p_70301_1_ < this.inventoryContents.size() ? (ItemStack)this.inventoryContents.get(p_70301_1_) : ItemStack.EMPTY;
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      ItemStack lvt_3_1_ = ItemStackHelper.getAndSplit(this.inventoryContents, p_70298_1_, p_70298_2_);
      if (!lvt_3_1_.isEmpty()) {
         this.markDirty();
      }

      return lvt_3_1_;
   }

   public ItemStack func_223374_a(Item p_223374_1_, int p_223374_2_) {
      ItemStack lvt_3_1_ = new ItemStack(p_223374_1_, 0);

      for(int lvt_4_1_ = this.slotsCount - 1; lvt_4_1_ >= 0; --lvt_4_1_) {
         ItemStack lvt_5_1_ = this.getStackInSlot(lvt_4_1_);
         if (lvt_5_1_.getItem().equals(p_223374_1_)) {
            int lvt_6_1_ = p_223374_2_ - lvt_3_1_.getCount();
            ItemStack lvt_7_1_ = lvt_5_1_.split(lvt_6_1_);
            lvt_3_1_.grow(lvt_7_1_.getCount());
            if (lvt_3_1_.getCount() == p_223374_2_) {
               break;
            }
         }
      }

      if (!lvt_3_1_.isEmpty()) {
         this.markDirty();
      }

      return lvt_3_1_;
   }

   public ItemStack addItem(ItemStack p_174894_1_) {
      ItemStack lvt_2_1_ = p_174894_1_.copy();
      this.func_223372_c(lvt_2_1_);
      if (lvt_2_1_.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.func_223375_b(lvt_2_1_);
         return lvt_2_1_.isEmpty() ? ItemStack.EMPTY : lvt_2_1_;
      }
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      ItemStack lvt_2_1_ = (ItemStack)this.inventoryContents.get(p_70304_1_);
      if (lvt_2_1_.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.inventoryContents.set(p_70304_1_, ItemStack.EMPTY);
         return lvt_2_1_;
      }
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      this.inventoryContents.set(p_70299_1_, p_70299_2_);
      if (!p_70299_2_.isEmpty() && p_70299_2_.getCount() > this.getInventoryStackLimit()) {
         p_70299_2_.setCount(this.getInventoryStackLimit());
      }

      this.markDirty();
   }

   public int getSizeInventory() {
      return this.slotsCount;
   }

   public boolean isEmpty() {
      Iterator var1 = this.inventoryContents.iterator();

      ItemStack lvt_2_1_;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         lvt_2_1_ = (ItemStack)var1.next();
      } while(lvt_2_1_.isEmpty());

      return false;
   }

   public void markDirty() {
      if (this.listeners != null) {
         Iterator var1 = this.listeners.iterator();

         while(var1.hasNext()) {
            IInventoryChangedListener lvt_2_1_ = (IInventoryChangedListener)var1.next();
            lvt_2_1_.onInventoryChanged(this);
         }
      }

   }

   public boolean isUsableByPlayer(PlayerEntity p_70300_1_) {
      return true;
   }

   public void clear() {
      this.inventoryContents.clear();
      this.markDirty();
   }

   public void fillStackedContents(RecipeItemHelper p_194018_1_) {
      Iterator var2 = this.inventoryContents.iterator();

      while(var2.hasNext()) {
         ItemStack lvt_3_1_ = (ItemStack)var2.next();
         p_194018_1_.accountStack(lvt_3_1_);
      }

   }

   public String toString() {
      return ((List)this.inventoryContents.stream().filter((p_223371_0_) -> {
         return !p_223371_0_.isEmpty();
      }).collect(Collectors.toList())).toString();
   }

   private void func_223375_b(ItemStack p_223375_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < this.slotsCount; ++lvt_2_1_) {
         ItemStack lvt_3_1_ = this.getStackInSlot(lvt_2_1_);
         if (lvt_3_1_.isEmpty()) {
            this.setInventorySlotContents(lvt_2_1_, p_223375_1_.copy());
            p_223375_1_.setCount(0);
            return;
         }
      }

   }

   private void func_223372_c(ItemStack p_223372_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < this.slotsCount; ++lvt_2_1_) {
         ItemStack lvt_3_1_ = this.getStackInSlot(lvt_2_1_);
         if (ItemStack.areItemsEqual(lvt_3_1_, p_223372_1_)) {
            this.func_223373_a(p_223372_1_, lvt_3_1_);
            if (p_223372_1_.isEmpty()) {
               return;
            }
         }
      }

   }

   private void func_223373_a(ItemStack p_223373_1_, ItemStack p_223373_2_) {
      int lvt_3_1_ = Math.min(this.getInventoryStackLimit(), p_223373_2_.getMaxStackSize());
      int lvt_4_1_ = Math.min(p_223373_1_.getCount(), lvt_3_1_ - p_223373_2_.getCount());
      if (lvt_4_1_ > 0) {
         p_223373_2_.grow(lvt_4_1_);
         p_223373_1_.shrink(lvt_4_1_);
         this.markDirty();
      }

   }
}
