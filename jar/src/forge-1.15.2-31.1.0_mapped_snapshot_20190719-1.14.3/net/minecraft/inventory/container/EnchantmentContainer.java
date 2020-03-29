package net.minecraft.inventory.container;

import java.util.List;
import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ForgeEventFactory;

public class EnchantmentContainer extends Container {
   private final IInventory tableInventory;
   private final IWorldPosCallable field_217006_g;
   private final Random rand;
   private final IntReferenceHolder xpSeed;
   public final int[] enchantLevels;
   public final int[] enchantClue;
   public final int[] worldClue;

   public EnchantmentContainer(int p_i50085_1_, PlayerInventory p_i50085_2_) {
      this(p_i50085_1_, p_i50085_2_, IWorldPosCallable.DUMMY);
   }

   public EnchantmentContainer(int p_i50086_1_, PlayerInventory p_i50086_2_, IWorldPosCallable p_i50086_3_) {
      super(ContainerType.ENCHANTMENT, p_i50086_1_);
      this.tableInventory = new Inventory(2) {
         public void markDirty() {
            super.markDirty();
            EnchantmentContainer.this.onCraftMatrixChanged(this);
         }
      };
      this.rand = new Random();
      this.xpSeed = IntReferenceHolder.single();
      this.enchantLevels = new int[3];
      this.enchantClue = new int[]{-1, -1, -1};
      this.worldClue = new int[]{-1, -1, -1};
      this.field_217006_g = p_i50086_3_;
      this.addSlot(new Slot(this.tableInventory, 0, 15, 47) {
         public boolean isItemValid(ItemStack p_75214_1_) {
            return true;
         }

         public int getSlotStackLimit() {
            return 1;
         }
      });
      this.addSlot(new Slot(this.tableInventory, 1, 35, 47) {
         public boolean isItemValid(ItemStack p_75214_1_) {
            return Tags.Items.GEMS_LAPIS.contains(p_75214_1_.getItem());
         }
      });

      int k;
      for(k = 0; k < 3; ++k) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_i50086_2_, j + k * 9 + 9, 8 + j * 18, 84 + k * 18));
         }
      }

      for(k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_i50086_2_, k, 8 + k * 18, 142));
      }

      this.trackInt(IntReferenceHolder.create((int[])this.enchantLevels, 0));
      this.trackInt(IntReferenceHolder.create((int[])this.enchantLevels, 1));
      this.trackInt(IntReferenceHolder.create((int[])this.enchantLevels, 2));
      this.trackInt(this.xpSeed).set(p_i50086_2_.player.getXPSeed());
      this.trackInt(IntReferenceHolder.create((int[])this.enchantClue, 0));
      this.trackInt(IntReferenceHolder.create((int[])this.enchantClue, 1));
      this.trackInt(IntReferenceHolder.create((int[])this.enchantClue, 2));
      this.trackInt(IntReferenceHolder.create((int[])this.worldClue, 0));
      this.trackInt(IntReferenceHolder.create((int[])this.worldClue, 1));
      this.trackInt(IntReferenceHolder.create((int[])this.worldClue, 2));
   }

   private float getPower(World p_getPower_1_, BlockPos p_getPower_2_) {
      return p_getPower_1_.getBlockState(p_getPower_2_).getEnchantPowerBonus(p_getPower_1_, p_getPower_2_);
   }

   public void onCraftMatrixChanged(IInventory p_75130_1_) {
      if (p_75130_1_ == this.tableInventory) {
         ItemStack itemstack = p_75130_1_.getStackInSlot(0);
         if (!itemstack.isEmpty() && itemstack.isEnchantable()) {
            this.field_217006_g.consume((p_lambda$onCraftMatrixChanged$0_2_, p_lambda$onCraftMatrixChanged$0_3_) -> {
               float power = 0.0F;

               int j1;
               for(j1 = -1; j1 <= 1; ++j1) {
                  for(int l = -1; l <= 1; ++l) {
                     if ((j1 != 0 || l != 0) && p_lambda$onCraftMatrixChanged$0_2_.isAirBlock(p_lambda$onCraftMatrixChanged$0_3_.add(l, 0, j1)) && p_lambda$onCraftMatrixChanged$0_2_.isAirBlock(p_lambda$onCraftMatrixChanged$0_3_.add(l, 1, j1))) {
                        power += this.getPower(p_lambda$onCraftMatrixChanged$0_2_, p_lambda$onCraftMatrixChanged$0_3_.add(l * 2, 0, j1 * 2));
                        power += this.getPower(p_lambda$onCraftMatrixChanged$0_2_, p_lambda$onCraftMatrixChanged$0_3_.add(l * 2, 1, j1 * 2));
                        if (l != 0 && j1 != 0) {
                           power += this.getPower(p_lambda$onCraftMatrixChanged$0_2_, p_lambda$onCraftMatrixChanged$0_3_.add(l * 2, 0, j1));
                           power += this.getPower(p_lambda$onCraftMatrixChanged$0_2_, p_lambda$onCraftMatrixChanged$0_3_.add(l * 2, 1, j1));
                           power += this.getPower(p_lambda$onCraftMatrixChanged$0_2_, p_lambda$onCraftMatrixChanged$0_3_.add(l, 0, j1 * 2));
                           power += this.getPower(p_lambda$onCraftMatrixChanged$0_2_, p_lambda$onCraftMatrixChanged$0_3_.add(l, 1, j1 * 2));
                        }
                     }
                  }
               }

               this.rand.setSeed((long)this.xpSeed.get());

               for(j1 = 0; j1 < 3; ++j1) {
                  this.enchantLevels[j1] = EnchantmentHelper.calcItemStackEnchantability(this.rand, j1, (int)power, itemstack);
                  this.enchantClue[j1] = -1;
                  this.worldClue[j1] = -1;
                  if (this.enchantLevels[j1] < j1 + 1) {
                     this.enchantLevels[j1] = 0;
                  }

                  this.enchantLevels[j1] = ForgeEventFactory.onEnchantmentLevelSet(p_lambda$onCraftMatrixChanged$0_2_, p_lambda$onCraftMatrixChanged$0_3_, j1, (int)power, itemstack, this.enchantLevels[j1]);
               }

               for(j1 = 0; j1 < 3; ++j1) {
                  if (this.enchantLevels[j1] > 0) {
                     List<EnchantmentData> list = this.getEnchantmentList(itemstack, j1, this.enchantLevels[j1]);
                     if (list != null && !list.isEmpty()) {
                        EnchantmentData enchantmentdata = (EnchantmentData)list.get(this.rand.nextInt(list.size()));
                        this.enchantClue[j1] = Registry.ENCHANTMENT.getId(enchantmentdata.enchantment);
                        this.worldClue[j1] = enchantmentdata.enchantmentLevel;
                     }
                  }
               }

               this.detectAndSendChanges();
            });
         } else {
            for(int i = 0; i < 3; ++i) {
               this.enchantLevels[i] = 0;
               this.enchantClue[i] = -1;
               this.worldClue[i] = -1;
            }
         }
      }

   }

   public boolean enchantItem(PlayerEntity p_75140_1_, int p_75140_2_) {
      ItemStack itemstack = this.tableInventory.getStackInSlot(0);
      ItemStack itemstack1 = this.tableInventory.getStackInSlot(1);
      int i = p_75140_2_ + 1;
      if ((itemstack1.isEmpty() || itemstack1.getCount() < i) && !p_75140_1_.abilities.isCreativeMode) {
         return false;
      } else if (this.enchantLevels[p_75140_2_] > 0 && !itemstack.isEmpty() && (p_75140_1_.experienceLevel >= i && p_75140_1_.experienceLevel >= this.enchantLevels[p_75140_2_] || p_75140_1_.abilities.isCreativeMode)) {
         this.field_217006_g.consume((p_lambda$enchantItem$1_6_, p_lambda$enchantItem$1_7_) -> {
            ItemStack itemstack2 = itemstack;
            List<EnchantmentData> list = this.getEnchantmentList(itemstack, p_75140_2_, this.enchantLevels[p_75140_2_]);
            if (!list.isEmpty()) {
               p_75140_1_.onEnchant(itemstack, i);
               boolean flag = itemstack.getItem() == Items.BOOK;
               if (flag) {
                  itemstack2 = new ItemStack(Items.ENCHANTED_BOOK);
                  this.tableInventory.setInventorySlotContents(0, itemstack2);
               }

               for(int j = 0; j < list.size(); ++j) {
                  EnchantmentData enchantmentdata = (EnchantmentData)list.get(j);
                  if (flag) {
                     EnchantedBookItem.addEnchantment(itemstack2, enchantmentdata);
                  } else {
                     itemstack2.addEnchantment(enchantmentdata.enchantment, enchantmentdata.enchantmentLevel);
                  }
               }

               if (!p_75140_1_.abilities.isCreativeMode) {
                  itemstack1.shrink(i);
                  if (itemstack1.isEmpty()) {
                     this.tableInventory.setInventorySlotContents(1, ItemStack.EMPTY);
                  }
               }

               p_75140_1_.addStat(Stats.ENCHANT_ITEM);
               if (p_75140_1_ instanceof ServerPlayerEntity) {
                  CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayerEntity)p_75140_1_, itemstack2, i);
               }

               this.tableInventory.markDirty();
               this.xpSeed.set(p_75140_1_.getXPSeed());
               this.onCraftMatrixChanged(this.tableInventory);
               p_lambda$enchantItem$1_6_.playSound((PlayerEntity)null, p_lambda$enchantItem$1_7_, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, p_lambda$enchantItem$1_6_.rand.nextFloat() * 0.1F + 0.9F);
            }

         });
         return true;
      } else {
         return false;
      }
   }

   private List<EnchantmentData> getEnchantmentList(ItemStack p_178148_1_, int p_178148_2_, int p_178148_3_) {
      this.rand.setSeed((long)(this.xpSeed.get() + p_178148_2_));
      List<EnchantmentData> list = EnchantmentHelper.buildEnchantmentList(this.rand, p_178148_1_, p_178148_3_, false);
      if (p_178148_1_.getItem() == Items.BOOK && list.size() > 1) {
         list.remove(this.rand.nextInt(list.size()));
      }

      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public int getLapisAmount() {
      ItemStack itemstack = this.tableInventory.getStackInSlot(1);
      return itemstack.isEmpty() ? 0 : itemstack.getCount();
   }

   @OnlyIn(Dist.CLIENT)
   public int func_217005_f() {
      return this.xpSeed.get();
   }

   public void onContainerClosed(PlayerEntity p_75134_1_) {
      super.onContainerClosed(p_75134_1_);
      this.field_217006_g.consume((p_lambda$onContainerClosed$2_2_, p_lambda$onContainerClosed$2_3_) -> {
         this.clearContainer(p_75134_1_, p_75134_1_.world, this.tableInventory);
      });
   }

   public boolean canInteractWith(PlayerEntity p_75145_1_) {
      return isWithinUsableDistance(this.field_217006_g, p_75145_1_, Blocks.ENCHANTING_TABLE);
   }

   public ItemStack transferStackInSlot(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (p_82846_2_ == 0) {
            if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
               return ItemStack.EMPTY;
            }
         } else if (p_82846_2_ == 1) {
            if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
               return ItemStack.EMPTY;
            }
         } else if (itemstack1.getItem() == Items.LAPIS_LAZULI) {
            if (!this.mergeItemStack(itemstack1, 1, 2, true)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (((Slot)this.inventorySlots.get(0)).getHasStack() || !((Slot)this.inventorySlots.get(0)).isItemValid(itemstack1)) {
               return ItemStack.EMPTY;
            }

            if (itemstack1.hasTag()) {
               ((Slot)this.inventorySlots.get(0)).putStack(itemstack1.split(1));
            } else if (!itemstack1.isEmpty()) {
               ((Slot)this.inventorySlots.get(0)).putStack(new ItemStack(itemstack1.getItem()));
               itemstack1.shrink(1);
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
}
