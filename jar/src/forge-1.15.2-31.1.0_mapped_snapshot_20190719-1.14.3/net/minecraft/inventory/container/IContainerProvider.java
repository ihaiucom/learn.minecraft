package net.minecraft.inventory.container;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

@FunctionalInterface
public interface IContainerProvider {
   @Nullable
   Container createMenu(int var1, PlayerInventory var2, PlayerEntity var3);
}
