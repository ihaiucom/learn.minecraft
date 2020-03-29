package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LeadItem;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FenceBlock extends FourWayBlock {
   private final VoxelShape[] renderShapes;

   public FenceBlock(Block.Properties p_i48399_1_) {
      super(2.0F, 2.0F, 16.0F, 16.0F, 24.0F, p_i48399_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(WATERLOGGED, false));
      this.renderShapes = this.makeShapes(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
   }

   public VoxelShape getRenderShape(BlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_) {
      return this.renderShapes[this.getIndex(p_196247_1_)];
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   public boolean func_220111_a(BlockState p_220111_1_, boolean p_220111_2_, Direction p_220111_3_) {
      Block lvt_4_1_ = p_220111_1_.getBlock();
      boolean lvt_5_1_ = lvt_4_1_.isIn(BlockTags.FENCES) && p_220111_1_.getMaterial() == this.material;
      boolean lvt_6_1_ = lvt_4_1_ instanceof FenceGateBlock && FenceGateBlock.isParallel(p_220111_1_, p_220111_3_);
      return !cannotAttach(lvt_4_1_) && p_220111_2_ || lvt_5_1_ || lvt_6_1_;
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isRemote) {
         ItemStack lvt_7_1_ = p_225533_4_.getHeldItem(p_225533_5_);
         return lvt_7_1_.getItem() == Items.LEAD ? ActionResultType.SUCCESS : ActionResultType.PASS;
      } else {
         return LeadItem.func_226641_a_(p_225533_4_, p_225533_2_, p_225533_3_);
      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockReader lvt_2_1_ = p_196258_1_.getWorld();
      BlockPos lvt_3_1_ = p_196258_1_.getPos();
      IFluidState lvt_4_1_ = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      BlockPos lvt_5_1_ = lvt_3_1_.north();
      BlockPos lvt_6_1_ = lvt_3_1_.east();
      BlockPos lvt_7_1_ = lvt_3_1_.south();
      BlockPos lvt_8_1_ = lvt_3_1_.west();
      BlockState lvt_9_1_ = lvt_2_1_.getBlockState(lvt_5_1_);
      BlockState lvt_10_1_ = lvt_2_1_.getBlockState(lvt_6_1_);
      BlockState lvt_11_1_ = lvt_2_1_.getBlockState(lvt_7_1_);
      BlockState lvt_12_1_ = lvt_2_1_.getBlockState(lvt_8_1_);
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)super.getStateForPlacement(p_196258_1_).with(NORTH, this.func_220111_a(lvt_9_1_, lvt_9_1_.func_224755_d(lvt_2_1_, lvt_5_1_, Direction.SOUTH), Direction.SOUTH))).with(EAST, this.func_220111_a(lvt_10_1_, lvt_10_1_.func_224755_d(lvt_2_1_, lvt_6_1_, Direction.WEST), Direction.WEST))).with(SOUTH, this.func_220111_a(lvt_11_1_, lvt_11_1_.func_224755_d(lvt_2_1_, lvt_7_1_, Direction.NORTH), Direction.NORTH))).with(WEST, this.func_220111_a(lvt_12_1_, lvt_12_1_.func_224755_d(lvt_2_1_, lvt_8_1_, Direction.EAST), Direction.EAST))).with(WATERLOGGED, lvt_4_1_.getFluid() == Fluids.WATER);
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if ((Boolean)p_196271_1_.get(WATERLOGGED)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      return p_196271_2_.getAxis().getPlane() == Direction.Plane.HORIZONTAL ? (BlockState)p_196271_1_.with((IProperty)FACING_TO_PROPERTY_MAP.get(p_196271_2_), this.func_220111_a(p_196271_3_, p_196271_3_.func_224755_d(p_196271_4_, p_196271_6_, p_196271_2_.getOpposite()), p_196271_2_.getOpposite())) : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
   }
}
