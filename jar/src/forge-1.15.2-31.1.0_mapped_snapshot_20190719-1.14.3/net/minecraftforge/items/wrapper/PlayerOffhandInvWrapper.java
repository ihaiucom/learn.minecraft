package net.minecraftforge.items.wrapper;

import net.minecraft.entity.player.PlayerInventory;

public class PlayerOffhandInvWrapper extends RangedWrapper {
   public PlayerOffhandInvWrapper(PlayerInventory inv) {
      super(new InvWrapper(inv), inv.mainInventory.size() + inv.armorInventory.size(), inv.mainInventory.size() + inv.armorInventory.size() + inv.offHandInventory.size());
   }
}
