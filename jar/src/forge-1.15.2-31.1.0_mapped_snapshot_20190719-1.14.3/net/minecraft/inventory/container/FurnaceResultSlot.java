package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraftforge.fml.hooks.BasicEventHooks;

public class FurnaceResultSlot extends Slot {
   private final PlayerEntity player;
   private int removeCount;

   public FurnaceResultSlot(PlayerEntity p_i45793_1_, IInventory p_i45793_2_, int p_i45793_3_, int p_i45793_4_, int p_i45793_5_) {
      super(p_i45793_2_, p_i45793_3_, p_i45793_4_, p_i45793_5_);
      this.player = p_i45793_1_;
   }

   public boolean isItemValid(ItemStack p_75214_1_) {
      return false;
   }

   public ItemStack decrStackSize(int p_75209_1_) {
      if (this.getHasStack()) {
         this.removeCount += Math.min(p_75209_1_, this.getStack().getCount());
      }

      return super.decrStackSize(p_75209_1_);
   }

   public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
      this.onCrafting(p_190901_2_);
      super.onTake(p_190901_1_, p_190901_2_);
      return p_190901_2_;
   }

   protected void onCrafting(ItemStack p_75210_1_, int p_75210_2_) {
      this.removeCount += p_75210_2_;
      this.onCrafting(p_75210_1_);
   }

   protected void onCrafting(ItemStack p_75208_1_) {
      p_75208_1_.onCrafting(this.player.world, this.player, this.removeCount);
      if (!this.player.world.isRemote && this.inventory instanceof AbstractFurnaceTileEntity) {
         ((AbstractFurnaceTileEntity)this.inventory).func_213995_d(this.player);
      }

      this.removeCount = 0;
      BasicEventHooks.firePlayerSmeltedEvent(this.player, p_75208_1_);
   }
}
