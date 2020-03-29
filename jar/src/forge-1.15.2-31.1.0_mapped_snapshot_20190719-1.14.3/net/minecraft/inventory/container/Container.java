package net.minecraft.inventory.container;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Container {
   private final NonNullList<ItemStack> inventoryItemStacks = NonNullList.create();
   public final List<Slot> inventorySlots = Lists.newArrayList();
   private final List<IntReferenceHolder> trackedIntReferences = Lists.newArrayList();
   @Nullable
   private final ContainerType<?> containerType;
   public final int windowId;
   @OnlyIn(Dist.CLIENT)
   private short transactionID;
   private int dragMode = -1;
   private int dragEvent;
   private final Set<Slot> dragSlots = Sets.newHashSet();
   private final List<IContainerListener> listeners = Lists.newArrayList();
   private final Set<PlayerEntity> playerList = Sets.newHashSet();

   protected Container(@Nullable ContainerType<?> p_i50105_1_, int p_i50105_2_) {
      this.containerType = p_i50105_1_;
      this.windowId = p_i50105_2_;
   }

   protected static boolean isWithinUsableDistance(IWorldPosCallable p_216963_0_, PlayerEntity p_216963_1_, Block p_216963_2_) {
      return (Boolean)p_216963_0_.applyOrElse((p_lambda$isWithinUsableDistance$0_2_, p_lambda$isWithinUsableDistance$0_3_) -> {
         return p_lambda$isWithinUsableDistance$0_2_.getBlockState(p_lambda$isWithinUsableDistance$0_3_).getBlock() != p_216963_2_ ? false : p_216963_1_.getDistanceSq((double)p_lambda$isWithinUsableDistance$0_3_.getX() + 0.5D, (double)p_lambda$isWithinUsableDistance$0_3_.getY() + 0.5D, (double)p_lambda$isWithinUsableDistance$0_3_.getZ() + 0.5D) <= 64.0D;
      }, true);
   }

   public ContainerType<?> getType() {
      if (this.containerType == null) {
         throw new UnsupportedOperationException("Unable to construct this menu by type");
      } else {
         return this.containerType;
      }
   }

   protected static void assertInventorySize(IInventory p_216962_0_, int p_216962_1_) {
      int i = p_216962_0_.getSizeInventory();
      if (i < p_216962_1_) {
         throw new IllegalArgumentException("Container size " + i + " is smaller than expected " + p_216962_1_);
      }
   }

   protected static void assertIntArraySize(IIntArray p_216959_0_, int p_216959_1_) {
      int i = p_216959_0_.size();
      if (i < p_216959_1_) {
         throw new IllegalArgumentException("Container data count " + i + " is smaller than expected " + p_216959_1_);
      }
   }

   protected Slot addSlot(Slot p_75146_1_) {
      p_75146_1_.slotNumber = this.inventorySlots.size();
      this.inventorySlots.add(p_75146_1_);
      this.inventoryItemStacks.add(ItemStack.EMPTY);
      return p_75146_1_;
   }

   protected IntReferenceHolder trackInt(IntReferenceHolder p_216958_1_) {
      this.trackedIntReferences.add(p_216958_1_);
      return p_216958_1_;
   }

   protected void trackIntArray(IIntArray p_216961_1_) {
      for(int i = 0; i < p_216961_1_.size(); ++i) {
         this.trackInt(IntReferenceHolder.create(p_216961_1_, i));
      }

   }

   public void addListener(IContainerListener p_75132_1_) {
      if (!this.listeners.contains(p_75132_1_)) {
         this.listeners.add(p_75132_1_);
         p_75132_1_.sendAllContents(this, this.getInventory());
         this.detectAndSendChanges();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void removeListener(IContainerListener p_82847_1_) {
      this.listeners.remove(p_82847_1_);
   }

   public NonNullList<ItemStack> getInventory() {
      NonNullList<ItemStack> nonnulllist = NonNullList.create();

      for(int i = 0; i < this.inventorySlots.size(); ++i) {
         nonnulllist.add(((Slot)this.inventorySlots.get(i)).getStack());
      }

      return nonnulllist;
   }

   public void detectAndSendChanges() {
      int j;
      for(j = 0; j < this.inventorySlots.size(); ++j) {
         ItemStack itemstack = ((Slot)this.inventorySlots.get(j)).getStack();
         ItemStack itemstack1 = (ItemStack)this.inventoryItemStacks.get(j);
         if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
            boolean clientStackChanged = !itemstack1.equals(itemstack, true);
            itemstack1 = itemstack.copy();
            this.inventoryItemStacks.set(j, itemstack1);
            if (clientStackChanged) {
               Iterator var5 = this.listeners.iterator();

               while(var5.hasNext()) {
                  IContainerListener icontainerlistener = (IContainerListener)var5.next();
                  icontainerlistener.sendSlotContents(this, j, itemstack1);
               }
            }
         }
      }

      for(j = 0; j < this.trackedIntReferences.size(); ++j) {
         IntReferenceHolder intreferenceholder = (IntReferenceHolder)this.trackedIntReferences.get(j);
         if (intreferenceholder.isDirty()) {
            Iterator var8 = this.listeners.iterator();

            while(var8.hasNext()) {
               IContainerListener icontainerlistener1 = (IContainerListener)var8.next();
               icontainerlistener1.sendWindowProperty(this, j, intreferenceholder.get());
            }
         }
      }

   }

   public boolean enchantItem(PlayerEntity p_75140_1_, int p_75140_2_) {
      return false;
   }

   public Slot getSlot(int p_75139_1_) {
      return (Slot)this.inventorySlots.get(p_75139_1_);
   }

   public ItemStack transferStackInSlot(PlayerEntity p_82846_1_, int p_82846_2_) {
      Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);
      return slot != null ? slot.getStack() : ItemStack.EMPTY;
   }

   public ItemStack slotClick(int p_184996_1_, int p_184996_2_, ClickType p_184996_3_, PlayerEntity p_184996_4_) {
      ItemStack itemstack = ItemStack.EMPTY;
      PlayerInventory playerinventory = p_184996_4_.inventory;
      ItemStack itemstack8;
      ItemStack itemstack11;
      int k3;
      int k1;
      if (p_184996_3_ == ClickType.QUICK_CRAFT) {
         int j1 = this.dragEvent;
         this.dragEvent = getDragEvent(p_184996_2_);
         if ((j1 != 1 || this.dragEvent != 2) && j1 != this.dragEvent) {
            this.resetDrag();
         } else if (playerinventory.getItemStack().isEmpty()) {
            this.resetDrag();
         } else if (this.dragEvent == 0) {
            this.dragMode = extractDragMode(p_184996_2_);
            if (isValidDragMode(this.dragMode, p_184996_4_)) {
               this.dragEvent = 1;
               this.dragSlots.clear();
            } else {
               this.resetDrag();
            }
         } else if (this.dragEvent == 1) {
            Slot slot7 = (Slot)this.inventorySlots.get(p_184996_1_);
            itemstack11 = playerinventory.getItemStack();
            if (slot7 != null && canAddItemToSlot(slot7, itemstack11, true) && slot7.isItemValid(itemstack11) && (this.dragMode == 2 || itemstack11.getCount() > this.dragSlots.size()) && this.canDragIntoSlot(slot7)) {
               this.dragSlots.add(slot7);
            }
         } else if (this.dragEvent == 2) {
            if (!this.dragSlots.isEmpty()) {
               itemstack8 = playerinventory.getItemStack().copy();
               k1 = playerinventory.getItemStack().getCount();
               Iterator var23 = this.dragSlots.iterator();

               label340:
               while(true) {
                  Slot slot8;
                  ItemStack itemstack13;
                  do {
                     do {
                        do {
                           do {
                              if (!var23.hasNext()) {
                                 itemstack8.setCount(k1);
                                 playerinventory.setItemStack(itemstack8);
                                 break label340;
                              }

                              slot8 = (Slot)var23.next();
                              itemstack13 = playerinventory.getItemStack();
                           } while(slot8 == null);
                        } while(!canAddItemToSlot(slot8, itemstack13, true));
                     } while(!slot8.isItemValid(itemstack13));
                  } while(this.dragMode != 2 && itemstack13.getCount() < this.dragSlots.size());

                  if (this.canDragIntoSlot(slot8)) {
                     ItemStack itemstack14 = itemstack8.copy();
                     int j3 = slot8.getHasStack() ? slot8.getStack().getCount() : 0;
                     computeStackSize(this.dragSlots, this.dragMode, itemstack14, j3);
                     k3 = Math.min(itemstack14.getMaxStackSize(), slot8.getItemStackLimit(itemstack14));
                     if (itemstack14.getCount() > k3) {
                        itemstack14.setCount(k3);
                     }

                     k1 -= itemstack14.getCount() - j3;
                     slot8.putStack(itemstack14);
                  }
               }
            }

            this.resetDrag();
         } else {
            this.resetDrag();
         }
      } else if (this.dragEvent != 0) {
         this.resetDrag();
      } else {
         Slot slot6;
         int l2;
         if (p_184996_3_ != ClickType.PICKUP && p_184996_3_ != ClickType.QUICK_MOVE || p_184996_2_ != 0 && p_184996_2_ != 1) {
            if (p_184996_3_ == ClickType.SWAP && p_184996_2_ >= 0 && p_184996_2_ < 9) {
               slot6 = (Slot)this.inventorySlots.get(p_184996_1_);
               itemstack8 = playerinventory.getStackInSlot(p_184996_2_);
               itemstack11 = slot6.getStack();
               if (!itemstack8.isEmpty() || !itemstack11.isEmpty()) {
                  if (itemstack8.isEmpty()) {
                     if (slot6.canTakeStack(p_184996_4_)) {
                        playerinventory.setInventorySlotContents(p_184996_2_, itemstack11);
                        slot6.onSwapCraft(itemstack11.getCount());
                        slot6.putStack(ItemStack.EMPTY);
                        slot6.onTake(p_184996_4_, itemstack11);
                     }
                  } else if (itemstack11.isEmpty()) {
                     if (slot6.isItemValid(itemstack8)) {
                        l2 = slot6.getItemStackLimit(itemstack8);
                        if (itemstack8.getCount() > l2) {
                           slot6.putStack(itemstack8.split(l2));
                        } else {
                           slot6.putStack(itemstack8);
                           playerinventory.setInventorySlotContents(p_184996_2_, ItemStack.EMPTY);
                        }
                     }
                  } else if (slot6.canTakeStack(p_184996_4_) && slot6.isItemValid(itemstack8)) {
                     l2 = slot6.getItemStackLimit(itemstack8);
                     if (itemstack8.getCount() > l2) {
                        slot6.putStack(itemstack8.split(l2));
                        slot6.onTake(p_184996_4_, itemstack11);
                        if (!playerinventory.addItemStackToInventory(itemstack11)) {
                           p_184996_4_.dropItem(itemstack11, true);
                        }
                     } else {
                        slot6.putStack(itemstack8);
                        playerinventory.setInventorySlotContents(p_184996_2_, itemstack11);
                        slot6.onTake(p_184996_4_, itemstack11);
                     }
                  }
               }
            } else if (p_184996_3_ == ClickType.CLONE && p_184996_4_.abilities.isCreativeMode && playerinventory.getItemStack().isEmpty() && p_184996_1_ >= 0) {
               slot6 = (Slot)this.inventorySlots.get(p_184996_1_);
               if (slot6 != null && slot6.getHasStack()) {
                  itemstack8 = slot6.getStack().copy();
                  itemstack8.setCount(itemstack8.getMaxStackSize());
                  playerinventory.setItemStack(itemstack8);
               }
            } else if (p_184996_3_ == ClickType.THROW && playerinventory.getItemStack().isEmpty() && p_184996_1_ >= 0) {
               slot6 = (Slot)this.inventorySlots.get(p_184996_1_);
               if (slot6 != null && slot6.getHasStack() && slot6.canTakeStack(p_184996_4_)) {
                  itemstack8 = slot6.decrStackSize(p_184996_2_ == 0 ? 1 : slot6.getStack().getCount());
                  slot6.onTake(p_184996_4_, itemstack8);
                  p_184996_4_.dropItem(itemstack8, true);
               }
            } else if (p_184996_3_ == ClickType.PICKUP_ALL && p_184996_1_ >= 0) {
               slot6 = (Slot)this.inventorySlots.get(p_184996_1_);
               itemstack8 = playerinventory.getItemStack();
               if (!itemstack8.isEmpty() && (slot6 == null || !slot6.getHasStack() || !slot6.canTakeStack(p_184996_4_))) {
                  k1 = p_184996_2_ == 0 ? 0 : this.inventorySlots.size() - 1;
                  l2 = p_184996_2_ == 0 ? 1 : -1;

                  for(int k = 0; k < 2; ++k) {
                     for(int l = k1; l >= 0 && l < this.inventorySlots.size() && itemstack8.getCount() < itemstack8.getMaxStackSize(); l += l2) {
                        Slot slot1 = (Slot)this.inventorySlots.get(l);
                        if (slot1.getHasStack() && canAddItemToSlot(slot1, itemstack8, true) && slot1.canTakeStack(p_184996_4_) && this.canMergeSlot(itemstack8, slot1)) {
                           ItemStack itemstack2 = slot1.getStack();
                           if (k != 0 || itemstack2.getCount() != itemstack2.getMaxStackSize()) {
                              k3 = Math.min(itemstack8.getMaxStackSize() - itemstack8.getCount(), itemstack2.getCount());
                              ItemStack itemstack3 = slot1.decrStackSize(k3);
                              itemstack8.grow(k3);
                              if (itemstack3.isEmpty()) {
                                 slot1.putStack(ItemStack.EMPTY);
                              }

                              slot1.onTake(p_184996_4_, itemstack3);
                           }
                        }
                     }
                  }
               }

               this.detectAndSendChanges();
            }
         } else if (p_184996_1_ == -999) {
            if (!playerinventory.getItemStack().isEmpty()) {
               if (p_184996_2_ == 0) {
                  p_184996_4_.dropItem(playerinventory.getItemStack(), true);
                  playerinventory.setItemStack(ItemStack.EMPTY);
               }

               if (p_184996_2_ == 1) {
                  p_184996_4_.dropItem(playerinventory.getItemStack().split(1), true);
               }
            }
         } else if (p_184996_3_ == ClickType.QUICK_MOVE) {
            if (p_184996_1_ < 0) {
               return ItemStack.EMPTY;
            }

            slot6 = (Slot)this.inventorySlots.get(p_184996_1_);
            if (slot6 == null || !slot6.canTakeStack(p_184996_4_)) {
               return ItemStack.EMPTY;
            }

            for(itemstack8 = this.transferStackInSlot(p_184996_4_, p_184996_1_); !itemstack8.isEmpty() && ItemStack.areItemsEqual(slot6.getStack(), itemstack8); itemstack8 = this.transferStackInSlot(p_184996_4_, p_184996_1_)) {
               itemstack = itemstack8.copy();
            }
         } else {
            if (p_184996_1_ < 0) {
               return ItemStack.EMPTY;
            }

            slot6 = (Slot)this.inventorySlots.get(p_184996_1_);
            if (slot6 != null) {
               itemstack8 = slot6.getStack();
               itemstack11 = playerinventory.getItemStack();
               if (!itemstack8.isEmpty()) {
                  itemstack = itemstack8.copy();
               }

               if (itemstack8.isEmpty()) {
                  if (!itemstack11.isEmpty() && slot6.isItemValid(itemstack11)) {
                     l2 = p_184996_2_ == 0 ? itemstack11.getCount() : 1;
                     if (l2 > slot6.getItemStackLimit(itemstack11)) {
                        l2 = slot6.getItemStackLimit(itemstack11);
                     }

                     slot6.putStack(itemstack11.split(l2));
                  }
               } else if (slot6.canTakeStack(p_184996_4_)) {
                  if (itemstack11.isEmpty()) {
                     if (itemstack8.isEmpty()) {
                        slot6.putStack(ItemStack.EMPTY);
                        playerinventory.setItemStack(ItemStack.EMPTY);
                     } else {
                        l2 = p_184996_2_ == 0 ? itemstack8.getCount() : (itemstack8.getCount() + 1) / 2;
                        playerinventory.setItemStack(slot6.decrStackSize(l2));
                        if (itemstack8.isEmpty()) {
                           slot6.putStack(ItemStack.EMPTY);
                        }

                        slot6.onTake(p_184996_4_, playerinventory.getItemStack());
                     }
                  } else if (slot6.isItemValid(itemstack11)) {
                     if (areItemsAndTagsEqual(itemstack8, itemstack11)) {
                        l2 = p_184996_2_ == 0 ? itemstack11.getCount() : 1;
                        if (l2 > slot6.getItemStackLimit(itemstack11) - itemstack8.getCount()) {
                           l2 = slot6.getItemStackLimit(itemstack11) - itemstack8.getCount();
                        }

                        if (l2 > itemstack11.getMaxStackSize() - itemstack8.getCount()) {
                           l2 = itemstack11.getMaxStackSize() - itemstack8.getCount();
                        }

                        itemstack11.shrink(l2);
                        itemstack8.grow(l2);
                     } else if (itemstack11.getCount() <= slot6.getItemStackLimit(itemstack11)) {
                        slot6.putStack(itemstack11);
                        playerinventory.setItemStack(itemstack8);
                     }
                  } else if (itemstack11.getMaxStackSize() > 1 && areItemsAndTagsEqual(itemstack8, itemstack11) && !itemstack8.isEmpty()) {
                     l2 = itemstack8.getCount();
                     if (l2 + itemstack11.getCount() <= itemstack11.getMaxStackSize()) {
                        itemstack11.grow(l2);
                        itemstack8 = slot6.decrStackSize(l2);
                        if (itemstack8.isEmpty()) {
                           slot6.putStack(ItemStack.EMPTY);
                        }

                        slot6.onTake(p_184996_4_, playerinventory.getItemStack());
                     }
                  }
               }

               slot6.onSlotChanged();
            }
         }
      }

      return itemstack;
   }

   public static boolean areItemsAndTagsEqual(ItemStack p_195929_0_, ItemStack p_195929_1_) {
      return p_195929_0_.getItem() == p_195929_1_.getItem() && ItemStack.areItemStackTagsEqual(p_195929_0_, p_195929_1_);
   }

   public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
      return true;
   }

   public void onContainerClosed(PlayerEntity p_75134_1_) {
      PlayerInventory playerinventory = p_75134_1_.inventory;
      if (!playerinventory.getItemStack().isEmpty()) {
         p_75134_1_.dropItem(playerinventory.getItemStack(), false);
         playerinventory.setItemStack(ItemStack.EMPTY);
      }

   }

   protected void clearContainer(PlayerEntity p_193327_1_, World p_193327_2_, IInventory p_193327_3_) {
      int i;
      if (!p_193327_1_.isAlive() || p_193327_1_ instanceof ServerPlayerEntity && ((ServerPlayerEntity)p_193327_1_).hasDisconnected()) {
         for(i = 0; i < p_193327_3_.getSizeInventory(); ++i) {
            p_193327_1_.dropItem(p_193327_3_.removeStackFromSlot(i), false);
         }
      } else {
         for(i = 0; i < p_193327_3_.getSizeInventory(); ++i) {
            p_193327_1_.inventory.placeItemBackInInventory(p_193327_2_, p_193327_3_.removeStackFromSlot(i));
         }
      }

   }

   public void onCraftMatrixChanged(IInventory p_75130_1_) {
      this.detectAndSendChanges();
   }

   public void putStackInSlot(int p_75141_1_, ItemStack p_75141_2_) {
      this.getSlot(p_75141_1_).putStack(p_75141_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setAll(List<ItemStack> p_190896_1_) {
      for(int i = 0; i < p_190896_1_.size(); ++i) {
         this.getSlot(i).putStack((ItemStack)p_190896_1_.get(i));
      }

   }

   public void updateProgressBar(int p_75137_1_, int p_75137_2_) {
      ((IntReferenceHolder)this.trackedIntReferences.get(p_75137_1_)).set(p_75137_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public short getNextTransactionID(PlayerInventory p_75136_1_) {
      ++this.transactionID;
      return this.transactionID;
   }

   public boolean getCanCraft(PlayerEntity p_75129_1_) {
      return !this.playerList.contains(p_75129_1_);
   }

   public void setCanCraft(PlayerEntity p_75128_1_, boolean p_75128_2_) {
      if (p_75128_2_) {
         this.playerList.remove(p_75128_1_);
      } else {
         this.playerList.add(p_75128_1_);
      }

   }

   public abstract boolean canInteractWith(PlayerEntity var1);

   protected boolean mergeItemStack(ItemStack p_75135_1_, int p_75135_2_, int p_75135_3_, boolean p_75135_4_) {
      boolean flag = false;
      int i = p_75135_2_;
      if (p_75135_4_) {
         i = p_75135_3_ - 1;
      }

      Slot slot1;
      ItemStack itemstack;
      if (p_75135_1_.isStackable()) {
         while(!p_75135_1_.isEmpty()) {
            if (p_75135_4_) {
               if (i < p_75135_2_) {
                  break;
               }
            } else if (i >= p_75135_3_) {
               break;
            }

            slot1 = (Slot)this.inventorySlots.get(i);
            itemstack = slot1.getStack();
            if (!itemstack.isEmpty() && areItemsAndTagsEqual(p_75135_1_, itemstack)) {
               int j = itemstack.getCount() + p_75135_1_.getCount();
               int maxSize = Math.min(slot1.getSlotStackLimit(), p_75135_1_.getMaxStackSize());
               if (j <= maxSize) {
                  p_75135_1_.setCount(0);
                  itemstack.setCount(j);
                  slot1.onSlotChanged();
                  flag = true;
               } else if (itemstack.getCount() < maxSize) {
                  p_75135_1_.shrink(maxSize - itemstack.getCount());
                  itemstack.setCount(maxSize);
                  slot1.onSlotChanged();
                  flag = true;
               }
            }

            if (p_75135_4_) {
               --i;
            } else {
               ++i;
            }
         }
      }

      if (!p_75135_1_.isEmpty()) {
         if (p_75135_4_) {
            i = p_75135_3_ - 1;
         } else {
            i = p_75135_2_;
         }

         while(true) {
            if (p_75135_4_) {
               if (i < p_75135_2_) {
                  break;
               }
            } else if (i >= p_75135_3_) {
               break;
            }

            slot1 = (Slot)this.inventorySlots.get(i);
            itemstack = slot1.getStack();
            if (itemstack.isEmpty() && slot1.isItemValid(p_75135_1_)) {
               if (p_75135_1_.getCount() > slot1.getSlotStackLimit()) {
                  slot1.putStack(p_75135_1_.split(slot1.getSlotStackLimit()));
               } else {
                  slot1.putStack(p_75135_1_.split(p_75135_1_.getCount()));
               }

               slot1.onSlotChanged();
               flag = true;
               break;
            }

            if (p_75135_4_) {
               --i;
            } else {
               ++i;
            }
         }
      }

      return flag;
   }

   public static int extractDragMode(int p_94529_0_) {
      return p_94529_0_ >> 2 & 3;
   }

   public static int getDragEvent(int p_94532_0_) {
      return p_94532_0_ & 3;
   }

   @OnlyIn(Dist.CLIENT)
   public static int getQuickcraftMask(int p_94534_0_, int p_94534_1_) {
      return p_94534_0_ & 3 | (p_94534_1_ & 3) << 2;
   }

   public static boolean isValidDragMode(int p_180610_0_, PlayerEntity p_180610_1_) {
      if (p_180610_0_ == 0) {
         return true;
      } else if (p_180610_0_ == 1) {
         return true;
      } else {
         return p_180610_0_ == 2 && p_180610_1_.abilities.isCreativeMode;
      }
   }

   protected void resetDrag() {
      this.dragEvent = 0;
      this.dragSlots.clear();
   }

   public static boolean canAddItemToSlot(@Nullable Slot p_94527_0_, ItemStack p_94527_1_, boolean p_94527_2_) {
      boolean flag = p_94527_0_ == null || !p_94527_0_.getHasStack();
      if (!flag && p_94527_1_.isItemEqual(p_94527_0_.getStack()) && ItemStack.areItemStackTagsEqual(p_94527_0_.getStack(), p_94527_1_)) {
         return p_94527_0_.getStack().getCount() + (p_94527_2_ ? 0 : p_94527_1_.getCount()) <= p_94527_1_.getMaxStackSize();
      } else {
         return flag;
      }
   }

   public static void computeStackSize(Set<Slot> p_94525_0_, int p_94525_1_, ItemStack p_94525_2_, int p_94525_3_) {
      switch(p_94525_1_) {
      case 0:
         p_94525_2_.setCount(MathHelper.floor((float)p_94525_2_.getCount() / (float)p_94525_0_.size()));
         break;
      case 1:
         p_94525_2_.setCount(1);
         break;
      case 2:
         p_94525_2_.setCount(p_94525_2_.getMaxStackSize());
      }

      p_94525_2_.grow(p_94525_3_);
   }

   public boolean canDragIntoSlot(Slot p_94531_1_) {
      return true;
   }

   public static int calcRedstone(@Nullable TileEntity p_178144_0_) {
      return p_178144_0_ instanceof IInventory ? calcRedstoneFromInventory((IInventory)p_178144_0_) : 0;
   }

   public static int calcRedstoneFromInventory(@Nullable IInventory p_94526_0_) {
      if (p_94526_0_ == null) {
         return 0;
      } else {
         int i = 0;
         float f = 0.0F;

         for(int j = 0; j < p_94526_0_.getSizeInventory(); ++j) {
            ItemStack itemstack = p_94526_0_.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
               f += (float)itemstack.getCount() / (float)Math.min(p_94526_0_.getInventoryStackLimit(), itemstack.getMaxStackSize());
               ++i;
            }
         }

         f /= (float)p_94526_0_.getSizeInventory();
         return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
      }
   }
}
