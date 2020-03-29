package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;

public class NetherWartBlock extends BushBlock {
   public static final IntegerProperty AGE;
   private static final VoxelShape[] SHAPES;

   protected NetherWartBlock(Block.Properties p_i48361_1_) {
      super(p_i48361_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(AGE, 0));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPES[(Integer)p_220053_1_.get(AGE)];
   }

   protected boolean isValidGround(BlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return p_200014_1_.getBlock() == Blocks.SOUL_SAND;
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      int i = (Integer)p_225534_1_.get(AGE);
      if (i < 3 && ForgeHooks.onCropsGrowPre(p_225534_2_, p_225534_3_, p_225534_1_, p_225534_4_.nextInt(10) == 0)) {
         p_225534_1_ = (BlockState)p_225534_1_.with(AGE, i + 1);
         p_225534_2_.setBlockState(p_225534_3_, p_225534_1_, 2);
         ForgeHooks.onCropsGrowPost(p_225534_2_, p_225534_3_, p_225534_1_);
      }

      super.func_225534_a_(p_225534_1_, p_225534_2_, p_225534_3_, p_225534_4_);
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return new ItemStack(Items.NETHER_WART);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   static {
      AGE = BlockStateProperties.AGE_0_3;
      SHAPES = new VoxelShape[]{Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 11.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D)};
   }
}
