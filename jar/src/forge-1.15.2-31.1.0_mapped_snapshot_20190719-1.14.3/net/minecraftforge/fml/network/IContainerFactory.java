package net.minecraftforge.fml.network;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;

public interface IContainerFactory<T extends Container> extends ContainerType.IFactory<T> {
   T create(int var1, PlayerInventory var2, PacketBuffer var3);

   default T create(int p_create_1_, PlayerInventory p_create_2_) {
      return this.create(p_create_1_, p_create_2_, (PacketBuffer)null);
   }
}
