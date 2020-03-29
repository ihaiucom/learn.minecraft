package net.minecraftforge.energy;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityEnergy {
   @CapabilityInject(IEnergyStorage.class)
   public static Capability<IEnergyStorage> ENERGY = null;

   public static void register() {
      CapabilityManager.INSTANCE.register(IEnergyStorage.class, new Capability.IStorage<IEnergyStorage>() {
         public INBT writeNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance, Direction side) {
            return IntNBT.func_229692_a_(instance.getEnergyStored());
         }

         public void readNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance, Direction side, INBT nbt) {
            if (!(instance instanceof EnergyStorage)) {
               throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
            } else {
               ((EnergyStorage)instance).energy = ((IntNBT)nbt).getInt();
            }
         }
      }, () -> {
         return new EnergyStorage(1000);
      });
   }
}
