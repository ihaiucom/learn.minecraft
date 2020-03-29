package net.minecraftforge.items.wrapper;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class PlayerArmorInvWrapper extends RangedWrapper {
   private final PlayerInventory inventoryPlayer;

   public PlayerArmorInvWrapper(PlayerInventory inv) {
      super(new InvWrapper(inv), inv.mainInventory.size(), inv.mainInventory.size() + inv.armorInventory.size());
      this.inventoryPlayer = inv;
   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      EquipmentSlotType equ = null;
      EquipmentSlotType[] var5 = EquipmentSlotType.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         EquipmentSlotType s = var5[var7];
         if (s.getSlotType() == EquipmentSlotType.Group.ARMOR && s.getIndex() == slot) {
            equ = s;
            break;
         }
      }

      return equ != null && slot < 4 && !stack.isEmpty() && stack.canEquip(equ, this.getInventoryPlayer().player) ? super.insertItem(slot, stack, simulate) : stack;
   }

   public PlayerInventory getInventoryPlayer() {
      return this.inventoryPlayer;
   }
}
