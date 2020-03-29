package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

public class ChorusPlantBlock extends SixWayBlock {
   protected ChorusPlantBlock(Block.Properties p_i48428_1_) {
      super(0.3125F, p_i48428_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(UP, false)).with(DOWN, false));
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.makeConnections(p_196258_1_.getWorld(), p_196258_1_.getPos());
   }

   public BlockState makeConnections(IBlockReader p_196497_1_, BlockPos p_196497_2_) {
      Block lvt_3_1_ = p_196497_1_.getBlockState(p_196497_2_.down()).getBlock();
      Block lvt_4_1_ = p_196497_1_.getBlockState(p_196497_2_.up()).getBlock();
      Block lvt_5_1_ = p_196497_1_.getBlockState(p_196497_2_.north()).getBlock();
      Block lvt_6_1_ = p_196497_1_.getBlockState(p_196497_2_.east()).getBlock();
      Block lvt_7_1_ = p_196497_1_.getBlockState(p_196497_2_.south()).getBlock();
      Block lvt_8_1_ = p_196497_1_.getBlockState(p_196497_2_.west()).getBlock();
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(DOWN, lvt_3_1_ == this || lvt_3_1_ == Blocks.CHORUS_FLOWER || lvt_3_1_ == Blocks.END_STONE)).with(UP, lvt_4_1_ == this || lvt_4_1_ == Blocks.CHORUS_FLOWER)).with(NORTH, lvt_5_1_ == this || lvt_5_1_ == Blocks.CHORUS_FLOWER)).with(EAST, lvt_6_1_ == this || lvt_6_1_ == Blocks.CHORUS_FLOWER)).with(SOUTH, lvt_7_1_ == this || lvt_7_1_ == Blocks.CHORUS_FLOWER)).with(WEST, lvt_8_1_ == this || lvt_8_1_ == Blocks.CHORUS_FLOWER);
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, 1);
         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      } else {
         Block lvt_7_1_ = p_196271_3_.getBlock();
         boolean lvt_8_1_ = lvt_7_1_ == this || lvt_7_1_ == Blocks.CHORUS_FLOWER || p_196271_2_ == Direction.DOWN && lvt_7_1_ == Blocks.END_STONE;
         return (BlockState)p_196271_1_.with((IProperty)FACING_TO_PROPERTY_MAP.get(p_196271_2_), lvt_8_1_);
      }
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (!p_225534_1_.isValidPosition(p_225534_2_, p_225534_3_)) {
         p_225534_2_.destroyBlock(p_225534_3_, true);
      }

   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockState lvt_4_1_ = p_196260_2_.getBlockState(p_196260_3_.down());
      boolean lvt_5_1_ = !p_196260_2_.getBlockState(p_196260_3_.up()).isAir() && !lvt_4_1_.isAir();
      Iterator var6 = Direction.Plane.HORIZONTAL.iterator();

      Block lvt_10_1_;
      do {
         BlockPos lvt_8_1_;
         Block lvt_9_1_;
         do {
            if (!var6.hasNext()) {
               Block lvt_6_1_ = lvt_4_1_.getBlock();
               return lvt_6_1_ == this || lvt_6_1_ == Blocks.END_STONE;
            }

            Direction lvt_7_1_ = (Direction)var6.next();
            lvt_8_1_ = p_196260_3_.offset(lvt_7_1_);
            lvt_9_1_ = p_196260_2_.getBlockState(lvt_8_1_).getBlock();
         } while(lvt_9_1_ != this);

         if (lvt_5_1_) {
            return false;
         }

         lvt_10_1_ = p_196260_2_.getBlockState(lvt_8_1_.down()).getBlock();
      } while(lvt_10_1_ != this && lvt_10_1_ != Blocks.END_STONE);

      return true;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }
}
