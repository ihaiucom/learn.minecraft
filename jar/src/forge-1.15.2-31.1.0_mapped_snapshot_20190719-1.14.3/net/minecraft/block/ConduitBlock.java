package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.ConduitTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ConduitBlock extends ContainerBlock implements IWaterLoggable {
   public static final BooleanProperty WATERLOGGED;
   protected static final VoxelShape SHAPE;

   public ConduitBlock(Block.Properties p_i48930_1_) {
      super(p_i48930_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(WATERLOGGED, true));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(WATERLOGGED);
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new ConduitTileEntity();
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public IFluidState getFluidState(BlockState p_204507_1_) {
      return (Boolean)p_204507_1_.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_204507_1_);
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if ((Boolean)p_196271_1_.get(WATERLOGGED)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasDisplayName()) {
         TileEntity lvt_6_1_ = p_180633_1_.getTileEntity(p_180633_2_);
         if (lvt_6_1_ instanceof BeaconTileEntity) {
            ((BeaconTileEntity)lvt_6_1_).setCustomName(p_180633_5_.getDisplayName());
         }
      }

   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IFluidState lvt_2_1_ = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      return (BlockState)this.getDefaultState().with(WATERLOGGED, lvt_2_1_.isTagged(FluidTags.WATER) && lvt_2_1_.getLevel() == 8);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE = Block.makeCuboidShape(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);
   }
}
