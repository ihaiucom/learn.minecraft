package net.minecraftforge.energy;

public class EnergyStorage implements IEnergyStorage {
   protected int energy;
   protected int capacity;
   protected int maxReceive;
   protected int maxExtract;

   public EnergyStorage(int capacity) {
      this(capacity, capacity, capacity, 0);
   }

   public EnergyStorage(int capacity, int maxTransfer) {
      this(capacity, maxTransfer, maxTransfer, 0);
   }

   public EnergyStorage(int capacity, int maxReceive, int maxExtract) {
      this(capacity, maxReceive, maxExtract, 0);
   }

   public EnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
      this.capacity = capacity;
      this.maxReceive = maxReceive;
      this.maxExtract = maxExtract;
      this.energy = Math.max(0, Math.min(capacity, energy));
   }

   public int receiveEnergy(int maxReceive, boolean simulate) {
      if (!this.canReceive()) {
         return 0;
      } else {
         int energyReceived = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, maxReceive));
         if (!simulate) {
            this.energy += energyReceived;
         }

         return energyReceived;
      }
   }

   public int extractEnergy(int maxExtract, boolean simulate) {
      if (!this.canExtract()) {
         return 0;
      } else {
         int energyExtracted = Math.min(this.energy, Math.min(this.maxExtract, maxExtract));
         if (!simulate) {
            this.energy -= energyExtracted;
         }

         return energyExtracted;
      }
   }

   public int getEnergyStored() {
      return this.energy;
   }

   public int getMaxEnergyStored() {
      return this.capacity;
   }

   public boolean canExtract() {
      return this.maxExtract > 0;
   }

   public boolean canReceive() {
      return this.maxReceive > 0;
   }
}
