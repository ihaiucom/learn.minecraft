package net.minecraft.inventory.container;

import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BeaconContainer extends Container {
   private final IInventory tileBeacon;
   private final BeaconContainer.BeaconSlot beaconSlot;
   private final IWorldPosCallable field_216971_e;
   private final IIntArray field_216972_f;

   public BeaconContainer(int p_i50099_1_, IInventory p_i50099_2_) {
      this(p_i50099_1_, p_i50099_2_, new IntArray(3), IWorldPosCallable.DUMMY);
   }

   public BeaconContainer(int p_i50100_1_, IInventory p_i50100_2_, IIntArray p_i50100_3_, IWorldPosCallable p_i50100_4_) {
      super(ContainerType.BEACON, p_i50100_1_);
      this.tileBeacon = new Inventory(1) {
         public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
            return p_94041_2_.isBeaconPayment();
         }

         public int getInventoryStackLimit() {
            return 1;
         }
      };
      assertIntArraySize(p_i50100_3_, 3);
      this.field_216972_f = p_i50100_3_;
      this.field_216971_e = p_i50100_4_;
      this.beaconSlot = new BeaconContainer.BeaconSlot(this.tileBeacon, 0, 136, 110);
      this.addSlot(this.beaconSlot);
      this.trackIntArray(p_i50100_3_);
      int i = true;
      int j = true;

      int i1;
      for(i1 = 0; i1 < 3; ++i1) {
         for(int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(p_i50100_2_, l + i1 * 9 + 9, 36 + l * 18, 137 + i1 * 18));
         }
      }

      for(i1 = 0; i1 < 9; ++i1) {
         this.addSlot(new Slot(p_i50100_2_, i1, 36 + i1 * 18, 195));
      }

   }

   public void onContainerClosed(PlayerEntity p_75134_1_) {
      super.onContainerClosed(p_75134_1_);
      if (!p_75134_1_.world.isRemote) {
         ItemStack itemstack = this.beaconSlot.decrStackSize(this.beaconSlot.getSlotStackLimit());
         if (!itemstack.isEmpty()) {
            p_75134_1_.dropItem(itemstack, false);
         }
      }

   }

   public boolean canInteractWith(PlayerEntity p_75145_1_) {
      return isWithinUsableDistance(this.field_216971_e, p_75145_1_, Blocks.BEACON);
   }

   public void updateProgressBar(int p_75137_1_, int p_75137_2_) {
      super.updateProgressBar(p_75137_1_, p_75137_2_);
      this.detectAndSendChanges();
   }

   public ItemStack transferStackInSlot(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (p_82846_2_ == 0) {
            if (!this.mergeItemStack(itemstack1, 1, 37, true)) {
               return ItemStack.EMPTY;
            }

            slot.onSlotChange(itemstack1, itemstack);
         } else {
            if (this.mergeItemStack(itemstack1, 0, 1, false)) {
               return ItemStack.EMPTY;
            }

            if (p_82846_2_ >= 1 && p_82846_2_ < 28) {
               if (!this.mergeItemStack(itemstack1, 28, 37, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_82846_2_ >= 28 && p_82846_2_ < 37) {
               if (!this.mergeItemStack(itemstack1, 1, 28, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.mergeItemStack(itemstack1, 1, 37, false)) {
               return ItemStack.EMPTY;
            }
         }

         if (itemstack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(p_82846_1_, itemstack1);
      }

      return itemstack;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_216969_e() {
      return this.field_216972_f.get(0);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Effect func_216967_f() {
      return Effect.get(this.field_216972_f.get(1));
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Effect func_216968_g() {
      return Effect.get(this.field_216972_f.get(2));
   }

   public void func_216966_c(int p_216966_1_, int p_216966_2_) {
      if (this.beaconSlot.getHasStack()) {
         this.field_216972_f.set(1, p_216966_1_);
         this.field_216972_f.set(2, p_216966_2_);
         this.beaconSlot.decrStackSize(1);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_216970_h() {
      return !this.tileBeacon.getStackInSlot(0).isEmpty();
   }

   class BeaconSlot extends Slot {
      public BeaconSlot(IInventory p_i1801_2_, int p_i1801_3_, int p_i1801_4_, int p_i1801_5_) {
         super(p_i1801_2_, p_i1801_3_, p_i1801_4_, p_i1801_5_);
      }

      public boolean isItemValid(ItemStack p_75214_1_) {
         return p_75214_1_.isBeaconPayment();
      }

      public int getSlotStackLimit() {
         return 1;
      }
   }
}
