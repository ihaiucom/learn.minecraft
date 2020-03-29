package net.minecraft.inventory.container;

import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WorkbenchContainer extends RecipeBookContainer<CraftingInventory> {
   private final CraftingInventory field_75162_e;
   private final CraftResultInventory field_75160_f;
   private final IWorldPosCallable field_217070_e;
   private final PlayerEntity player;

   public WorkbenchContainer(int p_i50089_1_, PlayerInventory p_i50089_2_) {
      this(p_i50089_1_, p_i50089_2_, IWorldPosCallable.DUMMY);
   }

   public WorkbenchContainer(int p_i50090_1_, PlayerInventory p_i50090_2_, IWorldPosCallable p_i50090_3_) {
      super(ContainerType.CRAFTING, p_i50090_1_);
      this.field_75162_e = new CraftingInventory(this, 3, 3);
      this.field_75160_f = new CraftResultInventory();
      this.field_217070_e = p_i50090_3_;
      this.player = p_i50090_2_.player;
      this.addSlot(new CraftingResultSlot(p_i50090_2_.player, this.field_75162_e, this.field_75160_f, 0, 124, 35));

      int lvt_4_3_;
      int lvt_5_2_;
      for(lvt_4_3_ = 0; lvt_4_3_ < 3; ++lvt_4_3_) {
         for(lvt_5_2_ = 0; lvt_5_2_ < 3; ++lvt_5_2_) {
            this.addSlot(new Slot(this.field_75162_e, lvt_5_2_ + lvt_4_3_ * 3, 30 + lvt_5_2_ * 18, 17 + lvt_4_3_ * 18));
         }
      }

      for(lvt_4_3_ = 0; lvt_4_3_ < 3; ++lvt_4_3_) {
         for(lvt_5_2_ = 0; lvt_5_2_ < 9; ++lvt_5_2_) {
            this.addSlot(new Slot(p_i50090_2_, lvt_5_2_ + lvt_4_3_ * 9 + 9, 8 + lvt_5_2_ * 18, 84 + lvt_4_3_ * 18));
         }
      }

      for(lvt_4_3_ = 0; lvt_4_3_ < 9; ++lvt_4_3_) {
         this.addSlot(new Slot(p_i50090_2_, lvt_4_3_, 8 + lvt_4_3_ * 18, 142));
      }

   }

   protected static void func_217066_a(int p_217066_0_, World p_217066_1_, PlayerEntity p_217066_2_, CraftingInventory p_217066_3_, CraftResultInventory p_217066_4_) {
      if (!p_217066_1_.isRemote) {
         ServerPlayerEntity lvt_5_1_ = (ServerPlayerEntity)p_217066_2_;
         ItemStack lvt_6_1_ = ItemStack.EMPTY;
         Optional<ICraftingRecipe> lvt_7_1_ = p_217066_1_.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, p_217066_3_, p_217066_1_);
         if (lvt_7_1_.isPresent()) {
            ICraftingRecipe lvt_8_1_ = (ICraftingRecipe)lvt_7_1_.get();
            if (p_217066_4_.canUseRecipe(p_217066_1_, lvt_5_1_, lvt_8_1_)) {
               lvt_6_1_ = lvt_8_1_.getCraftingResult(p_217066_3_);
            }
         }

         p_217066_4_.setInventorySlotContents(0, lvt_6_1_);
         lvt_5_1_.connection.sendPacket(new SSetSlotPacket(p_217066_0_, 0, lvt_6_1_));
      }
   }

   public void onCraftMatrixChanged(IInventory p_75130_1_) {
      this.field_217070_e.consume((p_217069_1_, p_217069_2_) -> {
         func_217066_a(this.windowId, p_217069_1_, this.player, this.field_75162_e, this.field_75160_f);
      });
   }

   public void func_201771_a(RecipeItemHelper p_201771_1_) {
      this.field_75162_e.fillStackedContents(p_201771_1_);
   }

   public void clear() {
      this.field_75162_e.clear();
      this.field_75160_f.clear();
   }

   public boolean matches(IRecipe<? super CraftingInventory> p_201769_1_) {
      return p_201769_1_.matches(this.field_75162_e, this.player.world);
   }

   public void onContainerClosed(PlayerEntity p_75134_1_) {
      super.onContainerClosed(p_75134_1_);
      this.field_217070_e.consume((p_217068_2_, p_217068_3_) -> {
         this.clearContainer(p_75134_1_, p_217068_2_, this.field_75162_e);
      });
   }

   public boolean canInteractWith(PlayerEntity p_75145_1_) {
      return isWithinUsableDistance(this.field_217070_e, p_75145_1_, Blocks.CRAFTING_TABLE);
   }

   public ItemStack transferStackInSlot(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack lvt_3_1_ = ItemStack.EMPTY;
      Slot lvt_4_1_ = (Slot)this.inventorySlots.get(p_82846_2_);
      if (lvt_4_1_ != null && lvt_4_1_.getHasStack()) {
         ItemStack lvt_5_1_ = lvt_4_1_.getStack();
         lvt_3_1_ = lvt_5_1_.copy();
         if (p_82846_2_ == 0) {
            this.field_217070_e.consume((p_217067_2_, p_217067_3_) -> {
               lvt_5_1_.getItem().onCreated(lvt_5_1_, p_217067_2_, p_82846_1_);
            });
            if (!this.mergeItemStack(lvt_5_1_, 10, 46, true)) {
               return ItemStack.EMPTY;
            }

            lvt_4_1_.onSlotChange(lvt_5_1_, lvt_3_1_);
         } else if (p_82846_2_ >= 10 && p_82846_2_ < 46) {
            if (!this.mergeItemStack(lvt_5_1_, 1, 10, false)) {
               if (p_82846_2_ < 37) {
                  if (!this.mergeItemStack(lvt_5_1_, 37, 46, false)) {
                     return ItemStack.EMPTY;
                  }
               } else if (!this.mergeItemStack(lvt_5_1_, 10, 37, false)) {
                  return ItemStack.EMPTY;
               }
            }
         } else if (!this.mergeItemStack(lvt_5_1_, 10, 46, false)) {
            return ItemStack.EMPTY;
         }

         if (lvt_5_1_.isEmpty()) {
            lvt_4_1_.putStack(ItemStack.EMPTY);
         } else {
            lvt_4_1_.onSlotChanged();
         }

         if (lvt_5_1_.getCount() == lvt_3_1_.getCount()) {
            return ItemStack.EMPTY;
         }

         ItemStack lvt_6_1_ = lvt_4_1_.onTake(p_82846_1_, lvt_5_1_);
         if (p_82846_2_ == 0) {
            p_82846_1_.dropItem(lvt_6_1_, false);
         }
      }

      return lvt_3_1_;
   }

   public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
      return p_94530_2_.inventory != this.field_75160_f && super.canMergeSlot(p_94530_1_, p_94530_2_);
   }

   public int getOutputSlot() {
      return 0;
   }

   public int getWidth() {
      return this.field_75162_e.getWidth();
   }

   public int getHeight() {
      return this.field_75162_e.getHeight();
   }

   @OnlyIn(Dist.CLIENT)
   public int getSize() {
      return 10;
   }
}
