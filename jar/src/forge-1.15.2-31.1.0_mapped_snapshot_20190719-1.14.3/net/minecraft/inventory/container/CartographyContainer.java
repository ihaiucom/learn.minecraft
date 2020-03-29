package net.minecraft.inventory.container;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.storage.MapData;

public class CartographyContainer extends Container {
   private final IWorldPosCallable field_216999_d;
   private boolean field_217000_e;
   private long field_226605_f_;
   public final IInventory field_216998_c;
   private final CraftResultInventory field_217001_f;

   public CartographyContainer(int p_i50093_1_, PlayerInventory p_i50093_2_) {
      this(p_i50093_1_, p_i50093_2_, IWorldPosCallable.DUMMY);
   }

   public CartographyContainer(int p_i50094_1_, PlayerInventory p_i50094_2_, final IWorldPosCallable p_i50094_3_) {
      super(ContainerType.field_226625_v_, p_i50094_1_);
      this.field_216998_c = new Inventory(2) {
         public void markDirty() {
            CartographyContainer.this.onCraftMatrixChanged(this);
            super.markDirty();
         }
      };
      this.field_217001_f = new CraftResultInventory() {
         public void markDirty() {
            CartographyContainer.this.onCraftMatrixChanged(this);
            super.markDirty();
         }
      };
      this.field_216999_d = p_i50094_3_;
      this.addSlot(new Slot(this.field_216998_c, 0, 15, 15) {
         public boolean isItemValid(ItemStack p_75214_1_) {
            return p_75214_1_.getItem() == Items.FILLED_MAP;
         }
      });
      this.addSlot(new Slot(this.field_216998_c, 1, 15, 52) {
         public boolean isItemValid(ItemStack p_75214_1_) {
            Item lvt_2_1_ = p_75214_1_.getItem();
            return lvt_2_1_ == Items.PAPER || lvt_2_1_ == Items.MAP || lvt_2_1_ == Items.GLASS_PANE;
         }
      });
      this.addSlot(new Slot(this.field_217001_f, 2, 145, 39) {
         public boolean isItemValid(ItemStack p_75214_1_) {
            return false;
         }

         public ItemStack decrStackSize(int p_75209_1_) {
            ItemStack lvt_2_1_ = super.decrStackSize(p_75209_1_);
            ItemStack lvt_3_1_ = (ItemStack)p_i50094_3_.apply((p_216936_2_, p_216936_3_) -> {
               if (!CartographyContainer.this.field_217000_e && CartographyContainer.this.field_216998_c.getStackInSlot(1).getItem() == Items.GLASS_PANE) {
                  ItemStack lvt_4_1_ = FilledMapItem.func_219992_b(p_216936_2_, CartographyContainer.this.field_216998_c.getStackInSlot(0));
                  if (lvt_4_1_ != null) {
                     lvt_4_1_.setCount(1);
                     return lvt_4_1_;
                  }
               }

               return lvt_2_1_;
            }).orElse(lvt_2_1_);
            CartographyContainer.this.field_216998_c.decrStackSize(0, 1);
            CartographyContainer.this.field_216998_c.decrStackSize(1, 1);
            return lvt_3_1_;
         }

         protected void onCrafting(ItemStack p_75210_1_, int p_75210_2_) {
            this.decrStackSize(p_75210_2_);
            super.onCrafting(p_75210_1_, p_75210_2_);
         }

         public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
            p_190901_2_.getItem().onCreated(p_190901_2_, p_190901_1_.world, p_190901_1_);
            p_i50094_3_.consume((p_216935_1_, p_216935_2_) -> {
               long lvt_3_1_ = p_216935_1_.getGameTime();
               if (CartographyContainer.this.field_226605_f_ != lvt_3_1_) {
                  p_216935_1_.playSound((PlayerEntity)null, p_216935_2_, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  CartographyContainer.this.field_226605_f_ = lvt_3_1_;
               }

            });
            return super.onTake(p_190901_1_, p_190901_2_);
         }
      });

      int lvt_4_2_;
      for(lvt_4_2_ = 0; lvt_4_2_ < 3; ++lvt_4_2_) {
         for(int lvt_5_1_ = 0; lvt_5_1_ < 9; ++lvt_5_1_) {
            this.addSlot(new Slot(p_i50094_2_, lvt_5_1_ + lvt_4_2_ * 9 + 9, 8 + lvt_5_1_ * 18, 84 + lvt_4_2_ * 18));
         }
      }

      for(lvt_4_2_ = 0; lvt_4_2_ < 9; ++lvt_4_2_) {
         this.addSlot(new Slot(p_i50094_2_, lvt_4_2_, 8 + lvt_4_2_ * 18, 142));
      }

   }

   public boolean canInteractWith(PlayerEntity p_75145_1_) {
      return isWithinUsableDistance(this.field_216999_d, p_75145_1_, Blocks.CARTOGRAPHY_TABLE);
   }

   public void onCraftMatrixChanged(IInventory p_75130_1_) {
      ItemStack lvt_2_1_ = this.field_216998_c.getStackInSlot(0);
      ItemStack lvt_3_1_ = this.field_216998_c.getStackInSlot(1);
      ItemStack lvt_4_1_ = this.field_217001_f.getStackInSlot(2);
      if (lvt_4_1_.isEmpty() || !lvt_2_1_.isEmpty() && !lvt_3_1_.isEmpty()) {
         if (!lvt_2_1_.isEmpty() && !lvt_3_1_.isEmpty()) {
            this.func_216993_a(lvt_2_1_, lvt_3_1_, lvt_4_1_);
         }
      } else {
         this.field_217001_f.removeStackFromSlot(2);
      }

   }

   private void func_216993_a(ItemStack p_216993_1_, ItemStack p_216993_2_, ItemStack p_216993_3_) {
      this.field_216999_d.consume((p_216996_4_, p_216996_5_) -> {
         Item lvt_6_1_ = p_216993_2_.getItem();
         MapData lvt_7_1_ = FilledMapItem.func_219994_a(p_216993_1_, p_216996_4_);
         if (lvt_7_1_ != null) {
            ItemStack lvt_8_3_;
            if (lvt_6_1_ == Items.PAPER && !lvt_7_1_.locked && lvt_7_1_.scale < 4) {
               lvt_8_3_ = p_216993_1_.copy();
               lvt_8_3_.setCount(1);
               lvt_8_3_.getOrCreateTag().putInt("map_scale_direction", 1);
               this.detectAndSendChanges();
            } else if (lvt_6_1_ == Items.GLASS_PANE && !lvt_7_1_.locked) {
               lvt_8_3_ = p_216993_1_.copy();
               lvt_8_3_.setCount(1);
               this.detectAndSendChanges();
            } else {
               if (lvt_6_1_ != Items.MAP) {
                  this.field_217001_f.removeStackFromSlot(2);
                  this.detectAndSendChanges();
                  return;
               }

               lvt_8_3_ = p_216993_1_.copy();
               lvt_8_3_.setCount(2);
               this.detectAndSendChanges();
            }

            if (!ItemStack.areItemStacksEqual(lvt_8_3_, p_216993_3_)) {
               this.field_217001_f.setInventorySlotContents(2, lvt_8_3_);
               this.detectAndSendChanges();
            }

         }
      });
   }

   public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
      return p_94530_2_.inventory != this.field_217001_f && super.canMergeSlot(p_94530_1_, p_94530_2_);
   }

   public ItemStack transferStackInSlot(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack lvt_3_1_ = ItemStack.EMPTY;
      Slot lvt_4_1_ = (Slot)this.inventorySlots.get(p_82846_2_);
      if (lvt_4_1_ != null && lvt_4_1_.getHasStack()) {
         ItemStack lvt_5_1_ = lvt_4_1_.getStack();
         ItemStack lvt_6_1_ = lvt_5_1_;
         Item lvt_7_1_ = lvt_5_1_.getItem();
         lvt_3_1_ = lvt_5_1_.copy();
         if (p_82846_2_ == 2) {
            if (this.field_216998_c.getStackInSlot(1).getItem() == Items.GLASS_PANE) {
               lvt_6_1_ = (ItemStack)this.field_216999_d.apply((p_216997_2_, p_216997_3_) -> {
                  ItemStack lvt_4_1_ = FilledMapItem.func_219992_b(p_216997_2_, this.field_216998_c.getStackInSlot(0));
                  if (lvt_4_1_ != null) {
                     lvt_4_1_.setCount(1);
                     return lvt_4_1_;
                  } else {
                     return lvt_5_1_;
                  }
               }).orElse(lvt_5_1_);
            }

            lvt_7_1_.onCreated(lvt_6_1_, p_82846_1_.world, p_82846_1_);
            if (!this.mergeItemStack(lvt_6_1_, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            lvt_4_1_.onSlotChange(lvt_6_1_, lvt_3_1_);
         } else if (p_82846_2_ != 1 && p_82846_2_ != 0) {
            if (lvt_7_1_ == Items.FILLED_MAP) {
               if (!this.mergeItemStack(lvt_5_1_, 0, 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (lvt_7_1_ != Items.PAPER && lvt_7_1_ != Items.MAP && lvt_7_1_ != Items.GLASS_PANE) {
               if (p_82846_2_ >= 3 && p_82846_2_ < 30) {
                  if (!this.mergeItemStack(lvt_5_1_, 30, 39, false)) {
                     return ItemStack.EMPTY;
                  }
               } else if (p_82846_2_ >= 30 && p_82846_2_ < 39 && !this.mergeItemStack(lvt_5_1_, 3, 30, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.mergeItemStack(lvt_5_1_, 1, 2, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(lvt_5_1_, 3, 39, false)) {
            return ItemStack.EMPTY;
         }

         if (lvt_6_1_.isEmpty()) {
            lvt_4_1_.putStack(ItemStack.EMPTY);
         }

         lvt_4_1_.onSlotChanged();
         if (lvt_6_1_.getCount() == lvt_3_1_.getCount()) {
            return ItemStack.EMPTY;
         }

         this.field_217000_e = true;
         lvt_4_1_.onTake(p_82846_1_, lvt_6_1_);
         this.field_217000_e = false;
         this.detectAndSendChanges();
      }

      return lvt_3_1_;
   }

   public void onContainerClosed(PlayerEntity p_75134_1_) {
      super.onContainerClosed(p_75134_1_);
      this.field_217001_f.removeStackFromSlot(2);
      this.field_216999_d.consume((p_216995_2_, p_216995_3_) -> {
         this.clearContainer(p_75134_1_, p_75134_1_.world, this.field_216998_c);
      });
   }
}
