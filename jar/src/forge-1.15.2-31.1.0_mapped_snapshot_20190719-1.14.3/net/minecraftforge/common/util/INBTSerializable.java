package net.minecraftforge.common.util;

import net.minecraft.nbt.INBT;

public interface INBTSerializable<T extends INBT> {
   T serializeNBT();

   void deserializeNBT(T var1);
}
