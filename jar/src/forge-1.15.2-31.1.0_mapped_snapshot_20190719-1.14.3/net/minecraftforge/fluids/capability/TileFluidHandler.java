package net.minecraftforge.fluids.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class TileFluidHandler extends TileEntity {
   protected FluidTank tank = new FluidTank(1000);
   private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> {
      return this.tank;
   });

   public TileFluidHandler(@Nonnull TileEntityType<?> tileEntityTypeIn) {
      super(tileEntityTypeIn);
   }

   public void read(CompoundNBT tag) {
      super.read(tag);
      this.tank.readFromNBT(tag);
   }

   public CompoundNBT write(CompoundNBT tag) {
      tag = super.write(tag);
      this.tank.writeToNBT(tag);
      return tag;
   }

   @Nonnull
   public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
      return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? this.holder.cast() : super.getCapability(capability, facing);
   }
}
