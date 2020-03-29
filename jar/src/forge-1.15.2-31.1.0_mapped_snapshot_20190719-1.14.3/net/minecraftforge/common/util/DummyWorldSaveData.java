package net.minecraftforge.common.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

public class DummyWorldSaveData extends WorldSavedData {
   public static final DummyWorldSaveData DUMMY = new DummyWorldSaveData();

   private DummyWorldSaveData() {
      super("DUMMYDUMMY \ud83d\udc4c\ud83d\udc4c\ud83d\udc4c");
   }

   public void read(CompoundNBT nbt) {
   }

   public CompoundNBT write(CompoundNBT compound) {
      return null;
   }
}
