package net.minecraft.inventory;

import java.util.Iterator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.NonNullList;

public class CraftingInventory implements IInventory, IRecipeHelperPopulator {
   private final NonNullList<ItemStack> stackList;
   private final int width;
   private final int height;
   private final Container field_70465_c;

   public CraftingInventory(Container p_i1807_1_, int p_i1807_2_, int p_i1807_3_) {
      this.stackList = NonNullList.withSize(p_i1807_2_ * p_i1807_3_, ItemStack.EMPTY);
      this.field_70465_c = p_i1807_1_;
      this.width = p_i1807_2_;
      this.height = p_i1807_3_;
   }

   public int getSizeInventory() {
      return this.stackList.size();
   }

   public boolean isEmpty() {
      Iterator var1 = this.stackList.iterator();

      ItemStack lvt_2_1_;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         lvt_2_1_ = (ItemStack)var1.next();
      } while(lvt_2_1_.isEmpty());

      return false;
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      return p_70301_1_ >= this.getSizeInventory() ? ItemStack.EMPTY : (ItemStack)this.stackList.get(p_70301_1_);
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      return ItemStackHelper.getAndRemove(this.stackList, p_70304_1_);
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      ItemStack lvt_3_1_ = ItemStackHelper.getAndSplit(this.stackList, p_70298_1_, p_70298_2_);
      if (!lvt_3_1_.isEmpty()) {
         this.field_70465_c.onCraftMatrixChanged(this);
      }

      return lvt_3_1_;
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      this.stackList.set(p_70299_1_, p_70299_2_);
      this.field_70465_c.onCraftMatrixChanged(this);
   }

   public void markDirty() {
   }

   public boolean isUsableByPlayer(PlayerEntity p_70300_1_) {
      return true;
   }

   public void clear() {
      this.stackList.clear();
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public void fillStackedContents(RecipeItemHelper p_194018_1_) {
      Iterator var2 = this.stackList.iterator();

      while(var2.hasNext()) {
         ItemStack lvt_3_1_ = (ItemStack)var2.next();
         p_194018_1_.accountPlainStack(lvt_3_1_);
      }

   }
}
