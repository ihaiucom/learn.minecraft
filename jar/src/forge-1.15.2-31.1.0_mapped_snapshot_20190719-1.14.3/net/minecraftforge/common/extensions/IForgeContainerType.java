package net.minecraftforge.common.extensions;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.IContainerFactory;

public interface IForgeContainerType<T> {
   static <T extends Container> ContainerType<T> create(IContainerFactory<T> factory) {
      return new ContainerType(factory);
   }

   T create(int var1, PlayerInventory var2, PacketBuffer var3);
}
