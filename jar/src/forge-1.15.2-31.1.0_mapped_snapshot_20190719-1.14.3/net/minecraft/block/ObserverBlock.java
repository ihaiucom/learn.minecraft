package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ObserverBlock extends DirectionalBlock {
   public static final BooleanProperty POWERED;

   public ObserverBlock(Block.Properties p_i48358_1_) {
      super(p_i48358_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.SOUTH)).with(POWERED, false));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, POWERED);
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(FACING)));
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if ((Boolean)p_225534_1_.get(POWERED)) {
         p_225534_2_.setBlockState(p_225534_3_, (BlockState)p_225534_1_.with(POWERED, false), 2);
      } else {
         p_225534_2_.setBlockState(p_225534_3_, (BlockState)p_225534_1_.with(POWERED, true), 2);
         p_225534_2_.getPendingBlockTicks().scheduleTick(p_225534_3_, this, 2);
      }

      this.updateNeighborsInFront(p_225534_2_, p_225534_3_, p_225534_1_);
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_1_.get(FACING) == p_196271_2_ && !(Boolean)p_196271_1_.get(POWERED)) {
         this.startSignal(p_196271_4_, p_196271_5_);
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   private void startSignal(IWorld p_203420_1_, BlockPos p_203420_2_) {
      if (!p_203420_1_.isRemote() && !p_203420_1_.getPendingBlockTicks().isTickScheduled(p_203420_2_, this)) {
         p_203420_1_.getPendingBlockTicks().scheduleTick(p_203420_2_, this, 2);
      }

   }

   protected void updateNeighborsInFront(World p_190961_1_, BlockPos p_190961_2_, BlockState p_190961_3_) {
      Direction lvt_4_1_ = (Direction)p_190961_3_.get(FACING);
      BlockPos lvt_5_1_ = p_190961_2_.offset(lvt_4_1_.getOpposite());
      p_190961_1_.neighborChanged(lvt_5_1_, this, p_190961_2_);
      p_190961_1_.notifyNeighborsOfStateExcept(lvt_5_1_, this, lvt_4_1_);
   }

   public boolean canProvidePower(BlockState p_149744_1_) {
      return true;
   }

   public int getStrongPower(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      return p_176211_1_.getWeakPower(p_176211_2_, p_176211_3_, p_176211_4_);
   }

   public int getWeakPower(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return (Boolean)p_180656_1_.get(POWERED) && p_180656_1_.get(FACING) == p_180656_4_ ? 15 : 0;
   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (p_220082_1_.getBlock() != p_220082_4_.getBlock()) {
         if (!p_220082_2_.isRemote() && (Boolean)p_220082_1_.get(POWERED) && !p_220082_2_.getPendingBlockTicks().isTickScheduled(p_220082_3_, this)) {
            BlockState lvt_6_1_ = (BlockState)p_220082_1_.with(POWERED, false);
            p_220082_2_.setBlockState(p_220082_3_, lvt_6_1_, 18);
            this.updateNeighborsInFront(p_220082_2_, p_220082_3_, lvt_6_1_);
         }

      }
   }

   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         if (!p_196243_2_.isRemote && (Boolean)p_196243_1_.get(POWERED) && p_196243_2_.getPendingBlockTicks().isTickScheduled(p_196243_3_, this)) {
            this.updateNeighborsInFront(p_196243_2_, p_196243_3_, (BlockState)p_196243_1_.with(POWERED, false));
         }

      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(FACING, p_196258_1_.getNearestLookingDirection().getOpposite().getOpposite());
   }

   static {
      POWERED = BlockStateProperties.POWERED;
   }
}
