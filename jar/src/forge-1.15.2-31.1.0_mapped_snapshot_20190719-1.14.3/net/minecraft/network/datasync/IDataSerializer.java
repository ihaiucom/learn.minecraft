package net.minecraft.network.datasync;

import net.minecraft.network.PacketBuffer;

public interface IDataSerializer<T> {
   void write(PacketBuffer var1, T var2);

   T read(PacketBuffer var1);

   default DataParameter<T> createKey(int p_187161_1_) {
      return new DataParameter(p_187161_1_, this);
   }

   T copyValue(T var1);
}
