package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LecternContainer extends Container {
   private final IInventory field_217018_c;
   private final IIntArray field_217019_d;

   public LecternContainer(int p_i50075_1_) {
      this(p_i50075_1_, new Inventory(1), new IntArray(1));
   }

   public LecternContainer(int p_i50076_1_, IInventory p_i50076_2_, IIntArray p_i50076_3_) {
      super(ContainerType.LECTERN, p_i50076_1_);
      assertInventorySize(p_i50076_2_, 1);
      assertIntArraySize(p_i50076_3_, 1);
      this.field_217018_c = p_i50076_2_;
      this.field_217019_d = p_i50076_3_;
      this.addSlot(new Slot(p_i50076_2_, 0, 0, 0) {
         public void onSlotChanged() {
            super.onSlotChanged();
            LecternContainer.this.onCraftMatrixChanged(this.inventory);
         }
      });
      this.trackIntArray(p_i50076_3_);
   }

   public boolean enchantItem(PlayerEntity p_75140_1_, int p_75140_2_) {
      int lvt_3_2_;
      if (p_75140_2_ >= 100) {
         lvt_3_2_ = p_75140_2_ - 100;
         this.updateProgressBar(0, lvt_3_2_);
         return true;
      } else {
         switch(p_75140_2_) {
         case 1:
            lvt_3_2_ = this.field_217019_d.get(0);
            this.updateProgressBar(0, lvt_3_2_ - 1);
            return true;
         case 2:
            lvt_3_2_ = this.field_217019_d.get(0);
            this.updateProgressBar(0, lvt_3_2_ + 1);
            return true;
         case 3:
            if (!p_75140_1_.isAllowEdit()) {
               return false;
            }

            ItemStack lvt_3_4_ = this.field_217018_c.removeStackFromSlot(0);
            this.field_217018_c.markDirty();
            if (!p_75140_1_.inventory.addItemStackToInventory(lvt_3_4_)) {
               p_75140_1_.dropItem(lvt_3_4_, false);
            }

            return true;
         default:
            return false;
         }
      }
   }

   public void updateProgressBar(int p_75137_1_, int p_75137_2_) {
      super.updateProgressBar(p_75137_1_, p_75137_2_);
      this.detectAndSendChanges();
   }

   public boolean canInteractWith(PlayerEntity p_75145_1_) {
      return this.field_217018_c.isUsableByPlayer(p_75145_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getBook() {
      return this.field_217018_c.getStackInSlot(0);
   }

   @OnlyIn(Dist.CLIENT)
   public int getPage() {
      return this.field_217019_d.get(0);
   }
}
