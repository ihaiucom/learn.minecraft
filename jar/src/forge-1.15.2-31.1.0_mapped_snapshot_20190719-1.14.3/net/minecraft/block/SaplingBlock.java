package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.trees.Tree;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;

public class SaplingBlock extends BushBlock implements IGrowable {
   public static final IntegerProperty STAGE;
   protected static final VoxelShape SHAPE;
   private final Tree tree;

   protected SaplingBlock(Tree p_i48337_1_, Block.Properties p_i48337_2_) {
      super(p_i48337_2_);
      this.tree = p_i48337_1_;
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(STAGE, 0));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      super.func_225534_a_(p_225534_1_, p_225534_2_, p_225534_3_, p_225534_4_);
      if (p_225534_2_.isAreaLoaded(p_225534_3_, 1)) {
         if (p_225534_2_.getLight(p_225534_3_.up()) >= 9 && p_225534_4_.nextInt(7) == 0) {
            this.func_226942_a_(p_225534_2_, p_225534_3_, p_225534_1_, p_225534_4_);
         }

      }
   }

   public void func_226942_a_(ServerWorld p_226942_1_, BlockPos p_226942_2_, BlockState p_226942_3_, Random p_226942_4_) {
      if ((Integer)p_226942_3_.get(STAGE) == 0) {
         p_226942_1_.setBlockState(p_226942_2_, (BlockState)p_226942_3_.cycle(STAGE), 4);
      } else {
         if (!ForgeEventFactory.saplingGrowTree(p_226942_1_, p_226942_4_, p_226942_2_)) {
            return;
         }

         this.tree.func_225545_a_(p_226942_1_, p_226942_1_.getChunkProvider().getChunkGenerator(), p_226942_2_, p_226942_3_, p_226942_4_);
      }

   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      return true;
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return (double)p_180670_1_.rand.nextFloat() < 0.45D;
   }

   public void func_225535_a_(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      this.func_226942_a_(p_225535_1_, p_225535_3_, p_225535_4_, p_225535_2_);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(STAGE);
   }

   static {
      STAGE = BlockStateProperties.STAGE_0_1;
      SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
   }
}
