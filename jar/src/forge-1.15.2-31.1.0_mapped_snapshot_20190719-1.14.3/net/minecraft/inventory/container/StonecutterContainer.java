package net.minecraft.inventory.container;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.StonecuttingRecipe;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StonecutterContainer extends Container {
   private final IWorldPosCallable field_217088_g;
   private final IntReferenceHolder field_217089_h;
   private final World field_217090_i;
   private List<StonecuttingRecipe> recipes;
   private ItemStack field_217092_k;
   private long field_217093_l;
   final Slot field_217085_d;
   final Slot field_217086_e;
   private Runnable inventoryUpdateListener;
   public final IInventory field_217087_f;
   private final CraftResultInventory inventory;

   public StonecutterContainer(int p_i50059_1_, PlayerInventory p_i50059_2_) {
      this(p_i50059_1_, p_i50059_2_, IWorldPosCallable.DUMMY);
   }

   public StonecutterContainer(int p_i50060_1_, PlayerInventory p_i50060_2_, final IWorldPosCallable p_i50060_3_) {
      super(ContainerType.STONECUTTER, p_i50060_1_);
      this.field_217089_h = IntReferenceHolder.single();
      this.recipes = Lists.newArrayList();
      this.field_217092_k = ItemStack.EMPTY;
      this.inventoryUpdateListener = () -> {
      };
      this.field_217087_f = new Inventory(1) {
         public void markDirty() {
            super.markDirty();
            StonecutterContainer.this.onCraftMatrixChanged(this);
            StonecutterContainer.this.inventoryUpdateListener.run();
         }
      };
      this.inventory = new CraftResultInventory();
      this.field_217088_g = p_i50060_3_;
      this.field_217090_i = p_i50060_2_.player.world;
      this.field_217085_d = this.addSlot(new Slot(this.field_217087_f, 0, 20, 33));
      this.field_217086_e = this.addSlot(new Slot(this.inventory, 1, 143, 33) {
         public boolean isItemValid(ItemStack p_75214_1_) {
            return false;
         }

         public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
            ItemStack lvt_3_1_ = StonecutterContainer.this.field_217085_d.decrStackSize(1);
            if (!lvt_3_1_.isEmpty()) {
               StonecutterContainer.this.func_217082_i();
            }

            p_190901_2_.getItem().onCreated(p_190901_2_, p_190901_1_.world, p_190901_1_);
            p_i50060_3_.consume((p_216954_1_, p_216954_2_) -> {
               long lvt_3_1_ = p_216954_1_.getGameTime();
               if (StonecutterContainer.this.field_217093_l != lvt_3_1_) {
                  p_216954_1_.playSound((PlayerEntity)null, p_216954_2_, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  StonecutterContainer.this.field_217093_l = lvt_3_1_;
               }

            });
            return super.onTake(p_190901_1_, p_190901_2_);
         }
      });

      int lvt_4_2_;
      for(lvt_4_2_ = 0; lvt_4_2_ < 3; ++lvt_4_2_) {
         for(int lvt_5_1_ = 0; lvt_5_1_ < 9; ++lvt_5_1_) {
            this.addSlot(new Slot(p_i50060_2_, lvt_5_1_ + lvt_4_2_ * 9 + 9, 8 + lvt_5_1_ * 18, 84 + lvt_4_2_ * 18));
         }
      }

      for(lvt_4_2_ = 0; lvt_4_2_ < 9; ++lvt_4_2_) {
         this.addSlot(new Slot(p_i50060_2_, lvt_4_2_, 8 + lvt_4_2_ * 18, 142));
      }

      this.trackInt(this.field_217089_h);
   }

   @OnlyIn(Dist.CLIENT)
   public int func_217073_e() {
      return this.field_217089_h.get();
   }

   @OnlyIn(Dist.CLIENT)
   public List<StonecuttingRecipe> getRecipeList() {
      return this.recipes;
   }

   @OnlyIn(Dist.CLIENT)
   public int getRecipeListSize() {
      return this.recipes.size();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_217083_h() {
      return this.field_217085_d.getHasStack() && !this.recipes.isEmpty();
   }

   public boolean canInteractWith(PlayerEntity p_75145_1_) {
      return isWithinUsableDistance(this.field_217088_g, p_75145_1_, Blocks.STONECUTTER);
   }

   public boolean enchantItem(PlayerEntity p_75140_1_, int p_75140_2_) {
      if (p_75140_2_ >= 0 && p_75140_2_ < this.recipes.size()) {
         this.field_217089_h.set(p_75140_2_);
         this.func_217082_i();
      }

      return true;
   }

   public void onCraftMatrixChanged(IInventory p_75130_1_) {
      ItemStack lvt_2_1_ = this.field_217085_d.getStack();
      if (lvt_2_1_.getItem() != this.field_217092_k.getItem()) {
         this.field_217092_k = lvt_2_1_.copy();
         this.updateAvailableRecipes(p_75130_1_, lvt_2_1_);
      }

   }

   private void updateAvailableRecipes(IInventory p_217074_1_, ItemStack p_217074_2_) {
      this.recipes.clear();
      this.field_217089_h.set(-1);
      this.field_217086_e.putStack(ItemStack.EMPTY);
      if (!p_217074_2_.isEmpty()) {
         this.recipes = this.field_217090_i.getRecipeManager().getRecipes(IRecipeType.STONECUTTING, p_217074_1_, this.field_217090_i);
      }

   }

   private void func_217082_i() {
      if (!this.recipes.isEmpty()) {
         StonecuttingRecipe lvt_1_1_ = (StonecuttingRecipe)this.recipes.get(this.field_217089_h.get());
         this.field_217086_e.putStack(lvt_1_1_.getCraftingResult(this.field_217087_f));
      } else {
         this.field_217086_e.putStack(ItemStack.EMPTY);
      }

      this.detectAndSendChanges();
   }

   public ContainerType<?> getType() {
      return ContainerType.STONECUTTER;
   }

   @OnlyIn(Dist.CLIENT)
   public void setInventoryUpdateListener(Runnable p_217071_1_) {
      this.inventoryUpdateListener = p_217071_1_;
   }

   public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
      return p_94530_2_.inventory != this.inventory && super.canMergeSlot(p_94530_1_, p_94530_2_);
   }

   public ItemStack transferStackInSlot(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack lvt_3_1_ = ItemStack.EMPTY;
      Slot lvt_4_1_ = (Slot)this.inventorySlots.get(p_82846_2_);
      if (lvt_4_1_ != null && lvt_4_1_.getHasStack()) {
         ItemStack lvt_5_1_ = lvt_4_1_.getStack();
         Item lvt_6_1_ = lvt_5_1_.getItem();
         lvt_3_1_ = lvt_5_1_.copy();
         if (p_82846_2_ == 1) {
            lvt_6_1_.onCreated(lvt_5_1_, p_82846_1_.world, p_82846_1_);
            if (!this.mergeItemStack(lvt_5_1_, 2, 38, true)) {
               return ItemStack.EMPTY;
            }

            lvt_4_1_.onSlotChange(lvt_5_1_, lvt_3_1_);
         } else if (p_82846_2_ == 0) {
            if (!this.mergeItemStack(lvt_5_1_, 2, 38, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.field_217090_i.getRecipeManager().getRecipe(IRecipeType.STONECUTTING, new Inventory(new ItemStack[]{lvt_5_1_}), this.field_217090_i).isPresent()) {
            if (!this.mergeItemStack(lvt_5_1_, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (p_82846_2_ >= 2 && p_82846_2_ < 29) {
            if (!this.mergeItemStack(lvt_5_1_, 29, 38, false)) {
               return ItemStack.EMPTY;
            }
         } else if (p_82846_2_ >= 29 && p_82846_2_ < 38 && !this.mergeItemStack(lvt_5_1_, 2, 29, false)) {
            return ItemStack.EMPTY;
         }

         if (lvt_5_1_.isEmpty()) {
            lvt_4_1_.putStack(ItemStack.EMPTY);
         }

         lvt_4_1_.onSlotChanged();
         if (lvt_5_1_.getCount() == lvt_3_1_.getCount()) {
            return ItemStack.EMPTY;
         }

         lvt_4_1_.onTake(p_82846_1_, lvt_5_1_);
         this.detectAndSendChanges();
      }

      return lvt_3_1_;
   }

   public void onContainerClosed(PlayerEntity p_75134_1_) {
      super.onContainerClosed(p_75134_1_);
      this.inventory.removeStackFromSlot(1);
      this.field_217088_g.consume((p_217079_2_, p_217079_3_) -> {
         this.clearContainer(p_75134_1_, p_75134_1_.world, this.field_217087_f);
      });
   }
}
