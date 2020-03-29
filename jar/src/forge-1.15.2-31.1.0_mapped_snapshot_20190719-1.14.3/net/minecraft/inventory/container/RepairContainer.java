package net.minecraft.inventory.container;

import java.util.Iterator;
import java.util.Map;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RepairContainer extends Container {
   private static final Logger LOGGER = LogManager.getLogger();
   private final IInventory outputSlot;
   private final IInventory inputSlots;
   private final IntReferenceHolder maximumCost;
   private final IWorldPosCallable field_216980_g;
   public int materialCost;
   private String repairedItemName;
   private final PlayerEntity player;

   public RepairContainer(int p_i50101_1_, PlayerInventory p_i50101_2_) {
      this(p_i50101_1_, p_i50101_2_, IWorldPosCallable.DUMMY);
   }

   public RepairContainer(int p_i50102_1_, PlayerInventory p_i50102_2_, final IWorldPosCallable p_i50102_3_) {
      super(ContainerType.ANVIL, p_i50102_1_);
      this.outputSlot = new CraftResultInventory();
      this.inputSlots = new Inventory(2) {
         public void markDirty() {
            super.markDirty();
            RepairContainer.this.onCraftMatrixChanged(this);
         }
      };
      this.maximumCost = IntReferenceHolder.single();
      this.field_216980_g = p_i50102_3_;
      this.player = p_i50102_2_.player;
      this.trackInt(this.maximumCost);
      this.addSlot(new Slot(this.inputSlots, 0, 27, 47));
      this.addSlot(new Slot(this.inputSlots, 1, 76, 47));
      this.addSlot(new Slot(this.outputSlot, 2, 134, 47) {
         public boolean isItemValid(ItemStack p_75214_1_) {
            return false;
         }

         public boolean canTakeStack(PlayerEntity p_82869_1_) {
            return (p_82869_1_.abilities.isCreativeMode || p_82869_1_.experienceLevel >= RepairContainer.this.maximumCost.get()) && RepairContainer.this.maximumCost.get() > 0 && this.getHasStack();
         }

         public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
            if (!p_190901_1_.abilities.isCreativeMode) {
               p_190901_1_.addExperienceLevel(-RepairContainer.this.maximumCost.get());
            }

            float breakChance = ForgeHooks.onAnvilRepair(p_190901_1_, p_190901_2_, RepairContainer.this.inputSlots.getStackInSlot(0), RepairContainer.this.inputSlots.getStackInSlot(1));
            RepairContainer.this.inputSlots.setInventorySlotContents(0, ItemStack.EMPTY);
            if (RepairContainer.this.materialCost > 0) {
               ItemStack itemstack = RepairContainer.this.inputSlots.getStackInSlot(1);
               if (!itemstack.isEmpty() && itemstack.getCount() > RepairContainer.this.materialCost) {
                  itemstack.shrink(RepairContainer.this.materialCost);
                  RepairContainer.this.inputSlots.setInventorySlotContents(1, itemstack);
               } else {
                  RepairContainer.this.inputSlots.setInventorySlotContents(1, ItemStack.EMPTY);
               }
            } else {
               RepairContainer.this.inputSlots.setInventorySlotContents(1, ItemStack.EMPTY);
            }

            RepairContainer.this.maximumCost.set(0);
            p_i50102_3_.consume((p_lambda$onTake$0_2_, p_lambda$onTake$0_3_) -> {
               BlockState blockstate = p_lambda$onTake$0_2_.getBlockState(p_lambda$onTake$0_3_);
               if (!p_190901_1_.abilities.isCreativeMode && blockstate.isIn(BlockTags.ANVIL) && p_190901_1_.getRNG().nextFloat() < breakChance) {
                  BlockState blockstate1 = AnvilBlock.damage(blockstate);
                  if (blockstate1 == null) {
                     p_lambda$onTake$0_2_.removeBlock(p_lambda$onTake$0_3_, false);
                     p_lambda$onTake$0_2_.playEvent(1029, p_lambda$onTake$0_3_, 0);
                  } else {
                     p_lambda$onTake$0_2_.setBlockState(p_lambda$onTake$0_3_, blockstate1, 2);
                     p_lambda$onTake$0_2_.playEvent(1030, p_lambda$onTake$0_3_, 0);
                  }
               } else {
                  p_lambda$onTake$0_2_.playEvent(1030, p_lambda$onTake$0_3_, 0);
               }

            });
            return p_190901_2_;
         }
      });

      int k;
      for(k = 0; k < 3; ++k) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_i50102_2_, j + k * 9 + 9, 8 + j * 18, 84 + k * 18));
         }
      }

      for(k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_i50102_2_, k, 8 + k * 18, 142));
      }

   }

   public void onCraftMatrixChanged(IInventory p_75130_1_) {
      super.onCraftMatrixChanged(p_75130_1_);
      if (p_75130_1_ == this.inputSlots) {
         this.updateRepairOutput();
      }

   }

   public void updateRepairOutput() {
      ItemStack itemstack = this.inputSlots.getStackInSlot(0);
      this.maximumCost.set(1);
      int i = 0;
      int j = 0;
      int k = 0;
      if (itemstack.isEmpty()) {
         this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
         this.maximumCost.set(0);
      } else {
         ItemStack itemstack1 = itemstack.copy();
         ItemStack itemstack2 = this.inputSlots.getStackInSlot(1);
         Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
         int j = j + itemstack.getRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getRepairCost());
         this.materialCost = 0;
         boolean flag = false;
         int k2;
         if (!itemstack2.isEmpty()) {
            if (!ForgeHooks.onAnvilChange(this, itemstack, itemstack2, this.outputSlot, this.repairedItemName, j)) {
               return;
            }

            flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(itemstack2).isEmpty();
            int i1;
            int j1;
            if (itemstack1.isDamageable() && itemstack1.getItem().getIsRepairable(itemstack, itemstack2)) {
               k2 = Math.min(itemstack1.getDamage(), itemstack1.getMaxDamage() / 4);
               if (k2 <= 0) {
                  this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
                  this.maximumCost.set(0);
                  return;
               }

               for(i1 = 0; k2 > 0 && i1 < itemstack2.getCount(); ++i1) {
                  j1 = itemstack1.getDamage() - k2;
                  itemstack1.setDamage(j1);
                  ++i;
                  k2 = Math.min(itemstack1.getDamage(), itemstack1.getMaxDamage() / 4);
               }

               this.materialCost = i1;
            } else {
               if (!flag && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.isDamageable())) {
                  this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
                  this.maximumCost.set(0);
                  return;
               }

               if (itemstack1.isDamageable() && !flag) {
                  k2 = itemstack.getMaxDamage() - itemstack.getDamage();
                  i1 = itemstack2.getMaxDamage() - itemstack2.getDamage();
                  j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
                  int k1 = k2 + j1;
                  int l1 = itemstack1.getMaxDamage() - k1;
                  if (l1 < 0) {
                     l1 = 0;
                  }

                  if (l1 < itemstack1.getDamage()) {
                     itemstack1.setDamage(l1);
                     i += 2;
                  }
               }

               Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
               boolean flag2 = false;
               boolean flag3 = false;
               Iterator var23 = map1.keySet().iterator();

               label177:
               while(true) {
                  Enchantment enchantment1;
                  do {
                     if (!var23.hasNext()) {
                        if (flag3 && !flag2) {
                           this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
                           this.maximumCost.set(0);
                           return;
                        }
                        break label177;
                     }

                     enchantment1 = (Enchantment)var23.next();
                  } while(enchantment1 == null);

                  int i2 = map.containsKey(enchantment1) ? (Integer)map.get(enchantment1) : 0;
                  int j2 = (Integer)map1.get(enchantment1);
                  j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                  boolean flag1 = enchantment1.canApply(itemstack);
                  if (this.player.abilities.isCreativeMode || itemstack.getItem() == Items.ENCHANTED_BOOK) {
                     flag1 = true;
                  }

                  Iterator var17 = map.keySet().iterator();

                  while(var17.hasNext()) {
                     Enchantment enchantment = (Enchantment)var17.next();
                     if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
                        flag1 = false;
                        ++i;
                     }
                  }

                  if (!flag1) {
                     flag3 = true;
                  } else {
                     flag2 = true;
                     if (j2 > enchantment1.getMaxLevel()) {
                        j2 = enchantment1.getMaxLevel();
                     }

                     map.put(enchantment1, j2);
                     int k3 = 0;
                     switch(enchantment1.getRarity()) {
                     case COMMON:
                        k3 = 1;
                        break;
                     case UNCOMMON:
                        k3 = 2;
                        break;
                     case RARE:
                        k3 = 4;
                        break;
                     case VERY_RARE:
                        k3 = 8;
                     }

                     if (flag) {
                        k3 = Math.max(1, k3 / 2);
                     }

                     i += k3 * j2;
                     if (itemstack.getCount() > 1) {
                        i = 40;
                     }
                  }
               }
            }
         }

         if (StringUtils.isBlank(this.repairedItemName)) {
            if (itemstack.hasDisplayName()) {
               k = 1;
               i += k;
               itemstack1.clearCustomName();
            }
         } else if (!this.repairedItemName.equals(itemstack.getDisplayName().getString())) {
            k = 1;
            i += k;
            itemstack1.setDisplayName(new StringTextComponent(this.repairedItemName));
         }

         if (flag && !itemstack1.isBookEnchantable(itemstack2)) {
            itemstack1 = ItemStack.EMPTY;
         }

         this.maximumCost.set(j + i);
         if (i <= 0) {
            itemstack1 = ItemStack.EMPTY;
         }

         if (k == i && k > 0 && this.maximumCost.get() >= 40) {
            this.maximumCost.set(39);
         }

         if (this.maximumCost.get() >= 40 && !this.player.abilities.isCreativeMode) {
            itemstack1 = ItemStack.EMPTY;
         }

         if (!itemstack1.isEmpty()) {
            k2 = itemstack1.getRepairCost();
            if (!itemstack2.isEmpty() && k2 < itemstack2.getRepairCost()) {
               k2 = itemstack2.getRepairCost();
            }

            if (k != i || k == 0) {
               k2 = func_216977_d(k2);
            }

            itemstack1.setRepairCost(k2);
            EnchantmentHelper.setEnchantments(map, itemstack1);
         }

         this.outputSlot.setInventorySlotContents(0, itemstack1);
         this.detectAndSendChanges();
      }

   }

   public static int func_216977_d(int p_216977_0_) {
      return p_216977_0_ * 2 + 1;
   }

   public void onContainerClosed(PlayerEntity p_75134_1_) {
      super.onContainerClosed(p_75134_1_);
      this.field_216980_g.consume((p_lambda$onContainerClosed$0_2_, p_lambda$onContainerClosed$0_3_) -> {
         this.clearContainer(p_75134_1_, p_lambda$onContainerClosed$0_2_, this.inputSlots);
      });
   }

   public boolean canInteractWith(PlayerEntity p_75145_1_) {
      return (Boolean)this.field_216980_g.applyOrElse((p_lambda$canInteractWith$1_1_, p_lambda$canInteractWith$1_2_) -> {
         return !p_lambda$canInteractWith$1_1_.getBlockState(p_lambda$canInteractWith$1_2_).isIn(BlockTags.ANVIL) ? false : p_75145_1_.getDistanceSq((double)p_lambda$canInteractWith$1_2_.getX() + 0.5D, (double)p_lambda$canInteractWith$1_2_.getY() + 0.5D, (double)p_lambda$canInteractWith$1_2_.getZ() + 0.5D) <= 64.0D;
      }, true);
   }

   public ItemStack transferStackInSlot(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (p_82846_2_ == 2) {
            if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            slot.onSlotChange(itemstack1, itemstack);
         } else if (p_82846_2_ != 0 && p_82846_2_ != 1) {
            if (p_82846_2_ >= 3 && p_82846_2_ < 39 && !this.mergeItemStack(itemstack1, 0, 2, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
            return ItemStack.EMPTY;
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

   public void updateItemName(String p_82850_1_) {
      this.repairedItemName = p_82850_1_;
      if (this.getSlot(2).getHasStack()) {
         ItemStack itemstack = this.getSlot(2).getStack();
         if (StringUtils.isBlank(p_82850_1_)) {
            itemstack.clearCustomName();
         } else {
            itemstack.setDisplayName(new StringTextComponent(this.repairedItemName));
         }
      }

      this.updateRepairOutput();
   }

   @OnlyIn(Dist.CLIENT)
   public int func_216976_f() {
      return this.maximumCost.get();
   }

   public void setMaximumCost(int p_setMaximumCost_1_) {
      this.maximumCost.set(p_setMaximumCost_1_);
   }
}
