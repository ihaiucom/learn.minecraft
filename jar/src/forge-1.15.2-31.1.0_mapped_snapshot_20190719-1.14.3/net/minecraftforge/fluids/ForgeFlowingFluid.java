package net.minecraftforge.fluids;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public abstract class ForgeFlowingFluid extends FlowingFluid {
   private final Supplier<? extends Fluid> flowing;
   private final Supplier<? extends Fluid> still;
   @Nullable
   private final Supplier<? extends Item> bucket;
   @Nullable
   private final Supplier<? extends FlowingFluidBlock> block;
   private final FluidAttributes.Builder builder;
   private final boolean canMultiply;
   private final int slopeFindDistance;
   private final int levelDecreasePerBlock;
   private final float explosionResistance;
   private final int tickRate;

   protected ForgeFlowingFluid(ForgeFlowingFluid.Properties properties) {
      this.flowing = properties.flowing;
      this.still = properties.still;
      this.builder = properties.attributes;
      this.canMultiply = properties.canMultiply;
      this.bucket = properties.bucket;
      this.block = properties.block;
      this.slopeFindDistance = properties.slopeFindDistance;
      this.levelDecreasePerBlock = properties.levelDecreasePerBlock;
      this.explosionResistance = properties.explosionResistance;
      this.tickRate = properties.tickRate;
   }

   public Fluid getFlowingFluid() {
      return (Fluid)this.flowing.get();
   }

   public Fluid getStillFluid() {
      return (Fluid)this.still.get();
   }

   protected boolean canSourcesMultiply() {
      return this.canMultiply;
   }

   protected void beforeReplacingBlock(IWorld worldIn, BlockPos pos, BlockState state) {
      TileEntity tileentity = state.getBlock().hasTileEntity() ? worldIn.getTileEntity(pos) : null;
      Block.spawnDrops(state, worldIn.getWorld(), pos, tileentity);
   }

   protected int getSlopeFindDistance(IWorldReader worldIn) {
      return this.slopeFindDistance;
   }

   protected int getLevelDecreasePerBlock(IWorldReader worldIn) {
      return this.levelDecreasePerBlock;
   }

   public Item getFilledBucket() {
      return this.bucket != null ? (Item)this.bucket.get() : Items.AIR;
   }

   protected boolean func_215665_a(IFluidState state, IBlockReader world, BlockPos pos, Fluid fluidIn, Direction direction) {
      return direction == Direction.DOWN && !this.isEquivalentTo(fluidIn);
   }

   public int getTickRate(IWorldReader world) {
      return this.tickRate;
   }

   protected float getExplosionResistance() {
      return this.explosionResistance;
   }

   protected BlockState getBlockState(IFluidState state) {
      return this.block != null ? (BlockState)((FlowingFluidBlock)this.block.get()).getDefaultState().with(FlowingFluidBlock.LEVEL, getLevelFromState(state)) : Blocks.AIR.getDefaultState();
   }

   public boolean isEquivalentTo(Fluid fluidIn) {
      return fluidIn == this.still.get() || fluidIn == this.flowing.get();
   }

   protected FluidAttributes createAttributes() {
      return this.builder.build(this);
   }

   public static class Properties {
      private Supplier<? extends Fluid> still;
      private Supplier<? extends Fluid> flowing;
      private FluidAttributes.Builder attributes;
      private boolean canMultiply;
      private Supplier<? extends Item> bucket;
      private Supplier<? extends FlowingFluidBlock> block;
      private int slopeFindDistance = 4;
      private int levelDecreasePerBlock = 1;
      private float explosionResistance = 1.0F;
      private int tickRate = 5;

      public Properties(Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing, FluidAttributes.Builder attributes) {
         this.still = still;
         this.flowing = flowing;
         this.attributes = attributes;
      }

      public ForgeFlowingFluid.Properties canMultiply() {
         this.canMultiply = true;
         return this;
      }

      public ForgeFlowingFluid.Properties bucket(Supplier<? extends Item> bucket) {
         this.bucket = bucket;
         return this;
      }

      public ForgeFlowingFluid.Properties block(Supplier<? extends FlowingFluidBlock> block) {
         this.block = block;
         return this;
      }

      public ForgeFlowingFluid.Properties slopeFindDistance(int slopeFindDistance) {
         this.slopeFindDistance = slopeFindDistance;
         return this;
      }

      public ForgeFlowingFluid.Properties levelDecreasePerBlock(int levelDecreasePerBlock) {
         this.levelDecreasePerBlock = levelDecreasePerBlock;
         return this;
      }

      public ForgeFlowingFluid.Properties explosionResistance(float explosionResistance) {
         this.explosionResistance = explosionResistance;
         return this;
      }
   }

   public static class Source extends ForgeFlowingFluid {
      public Source(ForgeFlowingFluid.Properties properties) {
         super(properties);
      }

      public int getLevel(IFluidState state) {
         return 8;
      }

      public boolean isSource(IFluidState state) {
         return true;
      }
   }

   public static class Flowing extends ForgeFlowingFluid {
      public Flowing(ForgeFlowingFluid.Properties properties) {
         super(properties);
         this.setDefaultState((IFluidState)((IFluidState)this.getStateContainer().getBaseState()).with(LEVEL_1_8, 7));
      }

      protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder) {
         super.fillStateContainer(builder);
         builder.add(LEVEL_1_8);
      }

      public int getLevel(IFluidState state) {
         return (Integer)state.get(LEVEL_1_8);
      }

      public boolean isSource(IFluidState state) {
         return false;
      }
   }
}
