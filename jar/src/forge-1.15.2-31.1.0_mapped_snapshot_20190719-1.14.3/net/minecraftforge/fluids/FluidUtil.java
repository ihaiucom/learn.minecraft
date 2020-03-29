package net.minecraftforge.fluids;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class FluidUtil {
   private FluidUtil() {
   }

   public static boolean interactWithFluidHandler(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull World world, @Nonnull BlockPos pos, @Nullable Direction side) {
      Preconditions.checkNotNull(world);
      Preconditions.checkNotNull(pos);
      return (Boolean)getFluidHandler(world, pos, side).map((handler) -> {
         return interactWithFluidHandler(player, hand, handler);
      }).orElse(false);
   }

   public static boolean interactWithFluidHandler(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull IFluidHandler handler) {
      Preconditions.checkNotNull(player);
      Preconditions.checkNotNull(hand);
      Preconditions.checkNotNull(handler);
      ItemStack heldItem = player.getHeldItem(hand);
      return !heldItem.isEmpty() ? (Boolean)player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map((playerInventory) -> {
         FluidActionResult fluidActionResult = tryFillContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
         if (!fluidActionResult.isSuccess()) {
            fluidActionResult = tryEmptyContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
         }

         if (fluidActionResult.isSuccess()) {
            player.setHeldItem(hand, fluidActionResult.getResult());
            return true;
         } else {
            return false;
         }
      }).orElse(false) : false;
   }

   @Nonnull
   public static FluidActionResult tryFillContainer(@Nonnull ItemStack container, IFluidHandler fluidSource, int maxAmount, @Nullable PlayerEntity player, boolean doFill) {
      ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1);
      return (FluidActionResult)getFluidHandler(containerCopy).map((containerFluidHandler) -> {
         FluidStack simulatedTransfer = tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, false);
         if (!simulatedTransfer.isEmpty()) {
            if (doFill) {
               tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, true);
               if (player != null) {
                  SoundEvent soundevent = simulatedTransfer.getFluid().getAttributes().getFillSound(simulatedTransfer);
                  player.world.playSound((PlayerEntity)null, player.func_226277_ct_(), player.func_226278_cu_() + 0.5D, player.func_226281_cx_(), soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
               }
            } else {
               containerFluidHandler.fill(simulatedTransfer, IFluidHandler.FluidAction.SIMULATE);
            }

            ItemStack resultContainer = containerFluidHandler.getContainer();
            return new FluidActionResult(resultContainer);
         } else {
            return FluidActionResult.FAILURE;
         }
      }).orElse(FluidActionResult.FAILURE);
   }

   @Nonnull
   public static FluidActionResult tryEmptyContainer(@Nonnull ItemStack container, IFluidHandler fluidDestination, int maxAmount, @Nullable PlayerEntity player, boolean doDrain) {
      ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1);
      return (FluidActionResult)getFluidHandler(containerCopy).map((containerFluidHandler) -> {
         FluidStack transfer = tryFluidTransfer(fluidDestination, containerFluidHandler, maxAmount, true);
         if (transfer.isEmpty()) {
            return FluidActionResult.FAILURE;
         } else {
            if (doDrain && player != null) {
               SoundEvent soundevent = transfer.getFluid().getAttributes().getEmptySound(transfer);
               player.world.playSound((PlayerEntity)null, player.func_226277_ct_(), player.func_226278_cu_() + 0.5D, player.func_226281_cx_(), soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            ItemStack resultContainer = containerFluidHandler.getContainer();
            return new FluidActionResult(resultContainer);
         }
      }).orElse(FluidActionResult.FAILURE);
   }

   @Nonnull
   public static FluidActionResult tryFillContainerAndStow(@Nonnull ItemStack container, IFluidHandler fluidSource, IItemHandler inventory, int maxAmount, @Nullable PlayerEntity player, boolean doFill) {
      if (container.isEmpty()) {
         return FluidActionResult.FAILURE;
      } else {
         FluidActionResult filledReal;
         if (player != null && player.abilities.isCreativeMode) {
            filledReal = tryFillContainer(container, fluidSource, maxAmount, player, doFill);
            if (filledReal.isSuccess()) {
               return new FluidActionResult(container);
            }
         } else if (container.getCount() == 1) {
            filledReal = tryFillContainer(container, fluidSource, maxAmount, player, doFill);
            if (filledReal.isSuccess()) {
               return filledReal;
            }
         } else {
            filledReal = tryFillContainer(container, fluidSource, maxAmount, player, false);
            if (filledReal.isSuccess()) {
               ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, filledReal.getResult(), true);
               if (remainder.isEmpty() || player != null) {
                  FluidActionResult filledReal = tryFillContainer(container, fluidSource, maxAmount, player, doFill);
                  remainder = ItemHandlerHelper.insertItemStacked(inventory, filledReal.getResult(), !doFill);
                  if (!remainder.isEmpty() && player != null && doFill) {
                     ItemHandlerHelper.giveItemToPlayer(player, remainder);
                  }

                  ItemStack containerCopy = container.copy();
                  containerCopy.shrink(1);
                  return new FluidActionResult(containerCopy);
               }
            }
         }

         return FluidActionResult.FAILURE;
      }
   }

   @Nonnull
   public static FluidActionResult tryEmptyContainerAndStow(@Nonnull ItemStack container, IFluidHandler fluidDestination, IItemHandler inventory, int maxAmount, @Nullable PlayerEntity player, boolean doDrain) {
      if (container.isEmpty()) {
         return FluidActionResult.FAILURE;
      } else {
         FluidActionResult emptiedReal;
         if (player != null && player.abilities.isCreativeMode) {
            emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, doDrain);
            if (emptiedReal.isSuccess()) {
               return new FluidActionResult(container);
            }
         } else if (container.getCount() == 1) {
            emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, doDrain);
            if (emptiedReal.isSuccess()) {
               return emptiedReal;
            }
         } else {
            emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, false);
            if (emptiedReal.isSuccess()) {
               ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedReal.getResult(), true);
               if (remainder.isEmpty() || player != null) {
                  FluidActionResult emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, doDrain);
                  remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedReal.getResult(), !doDrain);
                  if (!remainder.isEmpty() && player != null && doDrain) {
                     ItemHandlerHelper.giveItemToPlayer(player, remainder);
                  }

                  ItemStack containerCopy = container.copy();
                  containerCopy.shrink(1);
                  return new FluidActionResult(containerCopy);
               }
            }
         }

         return FluidActionResult.FAILURE;
      }
   }

   @Nonnull
   public static FluidStack tryFluidTransfer(IFluidHandler fluidDestination, IFluidHandler fluidSource, int maxAmount, boolean doTransfer) {
      FluidStack drainable = fluidSource.drain(maxAmount, IFluidHandler.FluidAction.SIMULATE);
      return !drainable.isEmpty() ? tryFluidTransfer_Internal(fluidDestination, fluidSource, drainable, doTransfer) : FluidStack.EMPTY;
   }

   @Nonnull
   public static FluidStack tryFluidTransfer(IFluidHandler fluidDestination, IFluidHandler fluidSource, FluidStack resource, boolean doTransfer) {
      FluidStack drainable = fluidSource.drain(resource, IFluidHandler.FluidAction.SIMULATE);
      return !drainable.isEmpty() && resource.isFluidEqual(drainable) ? tryFluidTransfer_Internal(fluidDestination, fluidSource, drainable, doTransfer) : FluidStack.EMPTY;
   }

   @Nonnull
   private static FluidStack tryFluidTransfer_Internal(IFluidHandler fluidDestination, IFluidHandler fluidSource, FluidStack drainable, boolean doTransfer) {
      int fillableAmount = fluidDestination.fill(drainable, IFluidHandler.FluidAction.SIMULATE);
      if (fillableAmount > 0) {
         if (!doTransfer) {
            drainable.setAmount(fillableAmount);
            return drainable;
         }

         FluidStack drained = fluidSource.drain(fillableAmount, IFluidHandler.FluidAction.EXECUTE);
         if (!drained.isEmpty()) {
            drained.setAmount(fluidDestination.fill(drained, IFluidHandler.FluidAction.EXECUTE));
            return drained;
         }
      }

      return FluidStack.EMPTY;
   }

   public static LazyOptional<IFluidHandlerItem> getFluidHandler(@Nonnull ItemStack itemStack) {
      return itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
   }

   public static LazyOptional<FluidStack> getFluidContained(@Nonnull ItemStack container) {
      if (!container.isEmpty()) {
         container = ItemHandlerHelper.copyStackWithSize(container, 1);
         return getFluidHandler(container).map((handler) -> {
            return handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
         });
      } else {
         return LazyOptional.empty();
      }
   }

   public static LazyOptional<IFluidHandler> getFluidHandler(World world, BlockPos blockPos, @Nullable Direction side) {
      BlockState state = world.getBlockState(blockPos);
      Block block = state.getBlock();
      if (block.hasTileEntity(state)) {
         TileEntity tileEntity = world.getTileEntity(blockPos);
         if (tileEntity != null) {
            return tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
         }
      }

      return LazyOptional.empty();
   }

   @Nonnull
   public static FluidActionResult tryPickUpFluid(@Nonnull ItemStack emptyContainer, @Nullable PlayerEntity playerIn, World worldIn, BlockPos pos, Direction side) {
      if (!emptyContainer.isEmpty() && worldIn != null && pos != null) {
         BlockState state = worldIn.getBlockState(pos);
         Block block = state.getBlock();
         if (block instanceof IFluidBlock) {
            IFluidHandler targetFluidHandler = (IFluidHandler)getFluidHandler(worldIn, pos, side).orElse((Object)null);
            if (targetFluidHandler != null) {
               return tryFillContainer(emptyContainer, targetFluidHandler, Integer.MAX_VALUE, playerIn, true);
            }
         }

         return FluidActionResult.FAILURE;
      } else {
         return FluidActionResult.FAILURE;
      }
   }

   @Nonnull
   public static FluidActionResult tryPlaceFluid(@Nullable PlayerEntity player, World world, Hand hand, BlockPos pos, @Nonnull ItemStack container, FluidStack resource) {
      ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1);
      return (FluidActionResult)getFluidHandler(containerCopy).filter((handler) -> {
         return tryPlaceFluid(player, world, hand, pos, (IFluidHandler)handler, resource);
      }).map(IFluidHandlerItem::getContainer).map(FluidActionResult::new).orElse(FluidActionResult.FAILURE);
   }

   public static boolean tryPlaceFluid(@Nullable PlayerEntity player, World world, Hand hand, BlockPos pos, IFluidHandler fluidSource, FluidStack resource) {
      if (world != null && pos != null) {
         Fluid fluid = resource.getFluid();
         if (fluid != null && fluid.getAttributes().canBePlacedInWorld(world, pos, (FluidStack)resource)) {
            if (fluidSource.drain(resource, IFluidHandler.FluidAction.SIMULATE).isEmpty()) {
               return false;
            } else {
               BlockItemUseContext context = new BlockItemUseContext(new ItemUseContext(player, hand, new BlockRayTraceResult(Vec3d.ZERO, Direction.UP, pos, false)));
               BlockState destBlockState = world.getBlockState(pos);
               Material destMaterial = destBlockState.getMaterial();
               boolean isDestNonSolid = !destMaterial.isSolid();
               boolean isDestReplaceable = destBlockState.isReplaceable(context);
               if (!world.isAirBlock(pos) && !isDestNonSolid && !isDestReplaceable) {
                  return false;
               } else {
                  if (world.dimension.doesWaterVaporize() && fluid.getAttributes().doesVaporize(world, pos, resource)) {
                     FluidStack result = fluidSource.drain(resource, IFluidHandler.FluidAction.EXECUTE);
                     if (!result.isEmpty()) {
                        result.getFluid().getAttributes().vaporize(player, world, pos, result);
                        return true;
                     }
                  } else {
                     IFluidHandler handler = getFluidBlockHandler(fluid, world, pos);
                     FluidStack result = tryFluidTransfer(handler, fluidSource, resource, true);
                     if (!result.isEmpty()) {
                        SoundEvent soundevent = resource.getFluid().getAttributes().getEmptySound(resource);
                        world.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        return true;
                     }
                  }

                  return false;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static IFluidHandler getFluidBlockHandler(Fluid fluid, World world, BlockPos pos) {
      BlockState state = fluid.getAttributes().getBlock(world, pos, fluid.getDefaultState());
      return new BlockWrapper(state, world, pos);
   }

   public static void destroyBlockOnFluidPlacement(World world, BlockPos pos) {
      if (!world.isRemote) {
         BlockState destBlockState = world.getBlockState(pos);
         Material destMaterial = destBlockState.getMaterial();
         boolean isDestNonSolid = !destMaterial.isSolid();
         boolean isDestReplaceable = false;
         if ((isDestNonSolid || isDestReplaceable) && !destMaterial.isLiquid()) {
            world.destroyBlock(pos, true);
         }
      }

   }

   @Nonnull
   public static ItemStack getFilledBucket(@Nonnull FluidStack fluidStack) {
      Fluid fluid = fluidStack.getFluid();
      if (!fluidStack.hasTag() || fluidStack.getTag().isEmpty()) {
         if (fluid == Fluids.WATER) {
            return new ItemStack(Items.WATER_BUCKET);
         }

         if (fluid == Fluids.LAVA) {
            return new ItemStack(Items.LAVA_BUCKET);
         }

         if (fluid.getRegistryName().equals(new ResourceLocation("milk"))) {
            return new ItemStack(Items.MILK_BUCKET);
         }
      }

      return fluid.getAttributes().getBucket(fluidStack);
   }
}
