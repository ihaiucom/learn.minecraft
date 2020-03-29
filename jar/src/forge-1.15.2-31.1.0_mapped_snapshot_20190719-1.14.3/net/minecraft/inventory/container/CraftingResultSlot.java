package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.hooks.BasicEventHooks;

public class CraftingResultSlot extends Slot {
   private final CraftingInventory field_75239_a;
   private final PlayerEntity player;
   private int amountCrafted;

   public CraftingResultSlot(PlayerEntity p_i45790_1_, CraftingInventory p_i45790_2_, IInventory p_i45790_3_, int p_i45790_4_, int p_i45790_5_, int p_i45790_6_) {
      super(p_i45790_3_, p_i45790_4_, p_i45790_5_, p_i45790_6_);
      this.player = p_i45790_1_;
      this.field_75239_a = p_i45790_2_;
   }

   public boolean isItemValid(ItemStack p_75214_1_) {
      return false;
   }

   public ItemStack decrStackSize(int p_75209_1_) {
      if (this.getHasStack()) {
         this.amountCrafted += Math.min(p_75209_1_, this.getStack().getCount());
      }

      return super.decrStackSize(p_75209_1_);
   }

   protected void onCrafting(ItemStack p_75210_1_, int p_75210_2_) {
      this.amountCrafted += p_75210_2_;
      this.onCrafting(p_75210_1_);
   }

   protected void onSwapCraft(int p_190900_1_) {
      this.amountCrafted += p_190900_1_;
   }

   protected void onCrafting(ItemStack p_75208_1_) {
      if (this.amountCrafted > 0) {
         p_75208_1_.onCrafting(this.player.world, this.player, this.amountCrafted);
         BasicEventHooks.firePlayerCraftingEvent(this.player, p_75208_1_, this.field_75239_a);
      }

      if (this.inventory instanceof IRecipeHolder) {
         ((IRecipeHolder)this.inventory).onCrafting(this.player);
      }

      this.amountCrafted = 0;
   }

   public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
      this.onCrafting(p_190901_2_);
      ForgeHooks.setCraftingPlayer(p_190901_1_);
      NonNullList<ItemStack> nonnulllist = p_190901_1_.world.getRecipeManager().getRecipeNonNull(IRecipeType.CRAFTING, this.field_75239_a, p_190901_1_.world);
      ForgeHooks.setCraftingPlayer((PlayerEntity)null);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack itemstack = this.field_75239_a.getStackInSlot(i);
         ItemStack itemstack1 = (ItemStack)nonnulllist.get(i);
         if (!itemstack.isEmpty()) {
            this.field_75239_a.decrStackSize(i, 1);
            itemstack = this.field_75239_a.getStackInSlot(i);
         }

         if (!itemstack1.isEmpty()) {
            if (itemstack.isEmpty()) {
               this.field_75239_a.setInventorySlotContents(i, itemstack1);
            } else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1)) {
               itemstack1.grow(itemstack.getCount());
               this.field_75239_a.setInventorySlotContents(i, itemstack1);
            } else if (!this.player.inventory.addItemStackToInventory(itemstack1)) {
               this.player.dropItem(itemstack1, false);
            }
         }
      }

      return p_190901_2_;
   }
}
