package net.minecraftforge.items.wrapper;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

public class PlayerMainInvWrapper extends RangedWrapper {
   private final PlayerInventory inventoryPlayer;

   public PlayerMainInvWrapper(PlayerInventory inv) {
      super(new InvWrapper(inv), 0, inv.mainInventory.size());
      this.inventoryPlayer = inv;
   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      ItemStack rest = super.insertItem(slot, stack, simulate);
      if (rest.getCount() != stack.getCount()) {
         ItemStack inSlot = this.getStackInSlot(slot);
         if (!inSlot.isEmpty()) {
            if (this.getInventoryPlayer().player.world.isRemote) {
               inSlot.setAnimationsToGo(5);
            } else if (this.getInventoryPlayer().player instanceof ServerPlayerEntity) {
               this.getInventoryPlayer().player.openContainer.detectAndSendChanges();
            }
         }
      }

      return rest;
   }

   public PlayerInventory getInventoryPlayer() {
      return this.inventoryPlayer;
   }
}
