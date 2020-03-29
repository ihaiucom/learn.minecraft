package net.minecraftforge.fluids;

import javax.annotation.Nonnull;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class DispenseFluidContainer extends DefaultDispenseItemBehavior {
   private static final DispenseFluidContainer INSTANCE = new DispenseFluidContainer();
   private final DefaultDispenseItemBehavior dispenseBehavior = new DefaultDispenseItemBehavior();

   public static DispenseFluidContainer getInstance() {
      return INSTANCE;
   }

   private DispenseFluidContainer() {
   }

   @Nonnull
   public ItemStack dispenseStack(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
      return FluidUtil.getFluidContained(stack).isPresent() ? this.dumpContainer(source, stack) : this.fillContainer(source, stack);
   }

   @Nonnull
   private ItemStack fillContainer(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
      World world = source.getWorld();
      Direction dispenserFacing = (Direction)source.getBlockState().get(DispenserBlock.FACING);
      BlockPos blockpos = source.getBlockPos().offset(dispenserFacing);
      FluidActionResult actionResult = FluidUtil.tryPickUpFluid(stack, (PlayerEntity)null, world, blockpos, dispenserFacing.getOpposite());
      ItemStack resultStack = actionResult.getResult();
      if (actionResult.isSuccess() && !resultStack.isEmpty()) {
         if (stack.getCount() == 1) {
            return resultStack;
         } else {
            if (((DispenserTileEntity)source.getBlockTileEntity()).addItemStack(resultStack) < 0) {
               this.dispenseBehavior.dispense(source, resultStack);
            }

            ItemStack stackCopy = stack.copy();
            stackCopy.shrink(1);
            return stackCopy;
         }
      } else {
         return super.dispenseStack(source, stack);
      }
   }

   @Nonnull
   private ItemStack dumpContainer(IBlockSource source, @Nonnull ItemStack stack) {
      ItemStack singleStack = stack.copy();
      singleStack.setCount(1);
      IFluidHandlerItem fluidHandler = (IFluidHandlerItem)FluidUtil.getFluidHandler(singleStack).orElse((Object)null);
      if (fluidHandler == null) {
         return super.dispenseStack(source, stack);
      } else {
         FluidStack fluidStack = fluidHandler.drain(1000, IFluidHandler.FluidAction.EXECUTE);
         Direction dispenserFacing = (Direction)source.getBlockState().get(DispenserBlock.FACING);
         BlockPos blockpos = source.getBlockPos().offset(dispenserFacing);
         FluidActionResult result = FluidUtil.tryPlaceFluid((PlayerEntity)null, source.getWorld(), Hand.MAIN_HAND, blockpos, (ItemStack)stack, fluidStack);
         if (result.isSuccess()) {
            ItemStack drainedStack = result.getResult();
            if (drainedStack.getCount() == 1) {
               return drainedStack;
            } else {
               if (!drainedStack.isEmpty() && ((DispenserTileEntity)source.getBlockTileEntity()).addItemStack(drainedStack) < 0) {
                  this.dispenseBehavior.dispense(source, drainedStack);
               }

               ItemStack stackCopy = drainedStack.copy();
               stackCopy.shrink(1);
               return stackCopy;
            }
         } else {
            return this.dispenseBehavior.dispense(source, stack);
         }
      }
   }
}
