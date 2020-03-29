package net.minecraftforge.fml.common.registry;

import net.minecraft.network.PacketBuffer;

public interface IEntityAdditionalSpawnData {
   void writeSpawnData(PacketBuffer var1);

   void readSpawnData(PacketBuffer var1);
}
