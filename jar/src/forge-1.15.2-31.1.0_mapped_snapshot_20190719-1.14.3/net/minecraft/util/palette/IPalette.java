package net.minecraft.util.palette;

import javax.annotation.Nullable;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IPalette<T> {
   int idFor(T var1);

   boolean contains(T var1);

   @Nullable
   T get(int var1);

   @OnlyIn(Dist.CLIENT)
   void read(PacketBuffer var1);

   void write(PacketBuffer var1);

   int getSerializedSize();

   void read(ListNBT var1);
}
