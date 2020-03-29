package net.minecraftforge.fluids.capability.wrappers;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.VoidFluidHandler;

public class BlockWrapper extends VoidFluidHandler {
   protected final BlockState state;
   protected final World world;
   protected final BlockPos blockPos;

   public BlockWrapper(BlockState state, World world, BlockPos blockPos) {
      this.state = state;
      this.world = world;
      this.blockPos = blockPos;
   }

   public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
      if (resource.getAmount() < 1000) {
         return 0;
      } else {
         if (action.execute()) {
            FluidUtil.destroyBlockOnFluidPlacement(this.world, this.blockPos);
            this.world.setBlockState(this.blockPos, this.state, 11);
         }

         return 1000;
      }
   }
}
