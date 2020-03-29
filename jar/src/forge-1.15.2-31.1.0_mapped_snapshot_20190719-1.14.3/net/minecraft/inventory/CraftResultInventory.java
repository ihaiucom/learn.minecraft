package net.minecraft.inventory;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;

public class CraftResultInventory implements IInventory, IRecipeHolder {
   private final NonNullList<ItemStack> stackResult;
   private IRecipe<?> recipeUsed;

   public CraftResultInventory() {
      this.stackResult = NonNullList.withSize(1, ItemStack.EMPTY);
   }

   public int getSizeInventory() {
      return 1;
   }

   public boolean isEmpty() {
      Iterator var1 = this.stackResult.iterator();

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
      return (ItemStack)this.stackResult.get(0);
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      return ItemStackHelper.getAndRemove(this.stackResult, 0);
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      return ItemStackHelper.getAndRemove(this.stackResult, 0);
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      this.stackResult.set(0, p_70299_2_);
   }

   public void markDirty() {
   }

   public boolean isUsableByPlayer(PlayerEntity p_70300_1_) {
      return true;
   }

   public void clear() {
      this.stackResult.clear();
   }

   public void setRecipeUsed(@Nullable IRecipe<?> p_193056_1_) {
      this.recipeUsed = p_193056_1_;
   }

   @Nullable
   public IRecipe<?> getRecipeUsed() {
      return this.recipeUsed;
   }
}
