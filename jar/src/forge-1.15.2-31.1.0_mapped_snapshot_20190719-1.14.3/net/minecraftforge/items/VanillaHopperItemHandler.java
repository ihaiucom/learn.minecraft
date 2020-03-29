package net.minecraftforge.items;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraftforge.items.wrapper.InvWrapper;

public class VanillaHopperItemHandler extends InvWrapper {
   private final HopperTileEntity hopper;

   public VanillaHopperItemHandler(HopperTileEntity hopper) {
      super(hopper);
      this.hopper = hopper;
   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      if (simulate) {
         return super.insertItem(slot, stack, simulate);
      } else {
         boolean wasEmpty = this.getInv().isEmpty();
         int originalStackSize = stack.getCount();
         stack = super.insertItem(slot, stack, simulate);
         if (wasEmpty && originalStackSize > stack.getCount() && !this.hopper.mayTransfer()) {
            this.hopper.setTransferCooldown(8);
         }

         return stack;
      }
   }
}
