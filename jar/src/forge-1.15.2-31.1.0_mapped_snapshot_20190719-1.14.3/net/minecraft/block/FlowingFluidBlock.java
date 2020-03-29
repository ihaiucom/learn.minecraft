package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

public class FlowingFluidBlock extends Block implements IBucketPickupHandler {
   public static final IntegerProperty LEVEL;
   private final FlowingFluid fluid;
   private final List<IFluidState> field_212565_c;
   private final Supplier<? extends Fluid> supplier;
   private boolean fluidStateCacheInitialized = false;

   /** @deprecated */
   @Deprecated
   protected FlowingFluidBlock(FlowingFluid p_i49014_1_, Block.Properties p_i49014_2_) {
      super(p_i49014_2_);
      this.fluid = p_i49014_1_;
      this.field_212565_c = Lists.newArrayList();
      this.field_212565_c.add(p_i49014_1_.getStillFluidState(false));

      for(int i = 1; i < 8; ++i) {
         this.field_212565_c.add(p_i49014_1_.getFlowingFluidState(8 - i, false));
      }

      this.field_212565_c.add(p_i49014_1_.getFlowingFluidState(8, true));
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(LEVEL, 0));
      this.fluidStateCacheInitialized = true;
      this.supplier = p_i49014_1_.delegate;
   }

   public FlowingFluidBlock(Supplier<? extends FlowingFluid> p_i230066_1_, Block.Properties p_i230066_2_) {
      super(p_i230066_2_);
      this.fluid = null;
      this.field_212565_c = Lists.newArrayList();
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(LEVEL, 0));
      this.supplier = p_i230066_1_;
   }

   public void func_225542_b_(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
      p_225542_2_.getFluidState(p_225542_3_).randomTick(p_225542_2_, p_225542_3_, p_225542_4_);
   }

   public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return false;
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return !this.fluid.isIn(FluidTags.LAVA);
   }

   public IFluidState getFluidState(BlockState p_204507_1_) {
      int i = (Integer)p_204507_1_.get(LEVEL);
      if (!this.fluidStateCacheInitialized) {
         this.initFluidStateCache();
      }

      return (IFluidState)this.field_212565_c.get(Math.min(i, 8));
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSideInvisible(BlockState p_200122_1_, BlockState p_200122_2_, Direction p_200122_3_) {
      return p_200122_2_.getFluidState().getFluid().isEquivalentTo(this.fluid);
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.INVISIBLE;
   }

   public List<ItemStack> getDrops(BlockState p_220076_1_, LootContext.Builder p_220076_2_) {
      return Collections.emptyList();
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return VoxelShapes.empty();
   }

   public int tickRate(IWorldReader p_149738_1_) {
      return this.fluid.getTickRate(p_149738_1_);
   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (this.reactWithNeighbors(p_220082_2_, p_220082_3_, p_220082_1_)) {
         p_220082_2_.getPendingFluidTicks().scheduleTick(p_220082_3_, p_220082_1_.getFluidState().getFluid(), this.tickRate(p_220082_2_));
      }

   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_1_.getFluidState().isSource() || p_196271_3_.getFluidState().isSource()) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, p_196271_1_.getFluidState().getFluid(), this.tickRate(p_196271_4_));
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (this.reactWithNeighbors(p_220069_2_, p_220069_3_, p_220069_1_)) {
         p_220069_2_.getPendingFluidTicks().scheduleTick(p_220069_3_, p_220069_1_.getFluidState().getFluid(), this.tickRate(p_220069_2_));
      }

   }

   public boolean reactWithNeighbors(World p_204515_1_, BlockPos p_204515_2_, BlockState p_204515_3_) {
      if (this.fluid.isIn(FluidTags.LAVA)) {
         boolean flag = false;
         Direction[] var5 = Direction.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Direction direction = var5[var7];
            if (direction != Direction.DOWN && p_204515_1_.getFluidState(p_204515_2_.offset(direction)).isTagged(FluidTags.WATER)) {
               flag = true;
               break;
            }
         }

         if (flag) {
            IFluidState ifluidstate = p_204515_1_.getFluidState(p_204515_2_);
            if (ifluidstate.isSource()) {
               p_204515_1_.setBlockState(p_204515_2_, ForgeEventFactory.fireFluidPlaceBlockEvent(p_204515_1_, p_204515_2_, p_204515_2_, Blocks.OBSIDIAN.getDefaultState()));
               this.triggerMixEffects(p_204515_1_, p_204515_2_);
               return false;
            }

            if (ifluidstate.func_215679_a(p_204515_1_, p_204515_2_) >= 0.44444445F) {
               p_204515_1_.setBlockState(p_204515_2_, ForgeEventFactory.fireFluidPlaceBlockEvent(p_204515_1_, p_204515_2_, p_204515_2_, Blocks.COBBLESTONE.getDefaultState()));
               this.triggerMixEffects(p_204515_1_, p_204515_2_);
               return false;
            }
         }
      }

      return true;
   }

   private void triggerMixEffects(IWorld p_180688_1_, BlockPos p_180688_2_) {
      p_180688_1_.playEvent(1501, p_180688_2_, 0);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(LEVEL);
   }

   public Fluid pickupFluid(IWorld p_204508_1_, BlockPos p_204508_2_, BlockState p_204508_3_) {
      if ((Integer)p_204508_3_.get(LEVEL) == 0) {
         p_204508_1_.setBlockState(p_204508_2_, Blocks.AIR.getDefaultState(), 11);
         return this.fluid;
      } else {
         return Fluids.EMPTY;
      }
   }

   public void onEntityCollision(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (this.fluid.isIn(FluidTags.LAVA)) {
         p_196262_4_.setInLava();
      }

   }

   public FlowingFluid getFluid() {
      return (FlowingFluid)this.supplier.get();
   }

   protected synchronized void initFluidStateCache() {
      if (!this.fluidStateCacheInitialized) {
         this.field_212565_c.add(this.getFluid().getStillFluidState(false));

         for(int i = 1; i < 8; ++i) {
            this.field_212565_c.add(this.getFluid().getFlowingFluidState(8 - i, false));
         }

         this.field_212565_c.add(this.getFluid().getFlowingFluidState(8, true));
         this.fluidStateCacheInitialized = true;
      }

   }

   static {
      LEVEL = BlockStateProperties.LEVEL_0_15;
   }
}
