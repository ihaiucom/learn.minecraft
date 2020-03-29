package net.minecraftforge.common.util;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.WorldSavedData;

public class WorldCapabilityData extends WorldSavedData {
   public static final String ID = "capabilities";
   private INBTSerializable<CompoundNBT> serializable;
   private CompoundNBT capNBT = null;

   public WorldCapabilityData(String name) {
      super(name);
   }

   public WorldCapabilityData(@Nullable INBTSerializable<CompoundNBT> serializable) {
      super("capabilities");
      this.serializable = serializable;
   }

   public void read(CompoundNBT nbt) {
      this.capNBT = nbt;
      if (this.serializable != null) {
         this.serializable.deserializeNBT(this.capNBT);
         this.capNBT = null;
      }

   }

   public CompoundNBT write(CompoundNBT nbt) {
      if (this.serializable != null) {
         nbt = (CompoundNBT)this.serializable.serializeNBT();
      }

      return nbt;
   }

   public boolean isDirty() {
      return true;
   }

   public void setCapabilities(Dimension provider, INBTSerializable<CompoundNBT> capabilities) {
      this.serializable = capabilities;
      if (this.capNBT != null && this.serializable != null) {
         this.serializable.deserializeNBT(this.capNBT);
         this.capNBT = null;
      }

   }
}
