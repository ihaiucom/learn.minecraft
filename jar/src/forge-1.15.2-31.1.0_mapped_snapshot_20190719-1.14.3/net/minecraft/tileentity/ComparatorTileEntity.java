package net.minecraft.tileentity;

import net.minecraft.nbt.CompoundNBT;

public class ComparatorTileEntity extends TileEntity {
   private int outputSignal;

   public ComparatorTileEntity() {
      super(TileEntityType.COMPARATOR);
   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      p_189515_1_.putInt("OutputSignal", this.outputSignal);
      return p_189515_1_;
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      this.outputSignal = p_145839_1_.getInt("OutputSignal");
   }

   public int getOutputSignal() {
      return this.outputSignal;
   }

   public void setOutputSignal(int p_145995_1_) {
      this.outputSignal = p_145995_1_;
   }
}
