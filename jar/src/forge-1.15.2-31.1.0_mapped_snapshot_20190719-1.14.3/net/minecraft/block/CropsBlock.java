package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.ForgeEventFactory;

public class CropsBlock extends BushBlock implements IGrowable {
   public static final IntegerProperty AGE;
   private static final VoxelShape[] SHAPE_BY_AGE;

   protected CropsBlock(Block.Properties p_i48421_1_) {
      super(p_i48421_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(this.getAgeProperty(), 0));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE_BY_AGE[(Integer)p_220053_1_.get(this.getAgeProperty())];
   }

   protected boolean isValidGround(BlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return p_200014_1_.getBlock() == Blocks.FARMLAND;
   }

   public IntegerProperty getAgeProperty() {
      return AGE;
   }

   public int getMaxAge() {
      return 7;
   }

   protected int getAge(BlockState p_185527_1_) {
      return (Integer)p_185527_1_.get(this.getAgeProperty());
   }

   public BlockState withAge(int p_185528_1_) {
      return (BlockState)this.getDefaultState().with(this.getAgeProperty(), p_185528_1_);
   }

   public boolean isMaxAge(BlockState p_185525_1_) {
      return (Integer)p_185525_1_.get(this.getAgeProperty()) >= this.getMaxAge();
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      super.func_225534_a_(p_225534_1_, p_225534_2_, p_225534_3_, p_225534_4_);
      if (p_225534_2_.isAreaLoaded(p_225534_3_, 1)) {
         if (p_225534_2_.func_226659_b_(p_225534_3_, 0) >= 9) {
            int i = this.getAge(p_225534_1_);
            if (i < this.getMaxAge()) {
               float f = getGrowthChance(this, p_225534_2_, p_225534_3_);
               if (ForgeHooks.onCropsGrowPre(p_225534_2_, p_225534_3_, p_225534_1_, p_225534_4_.nextInt((int)(25.0F / f) + 1) == 0)) {
                  p_225534_2_.setBlockState(p_225534_3_, this.withAge(i + 1), 2);
                  ForgeHooks.onCropsGrowPost(p_225534_2_, p_225534_3_, p_225534_1_);
               }
            }
         }

      }
   }

   public void grow(World p_176487_1_, BlockPos p_176487_2_, BlockState p_176487_3_) {
      int i = this.getAge(p_176487_3_) + this.getBonemealAgeIncrease(p_176487_1_);
      int j = this.getMaxAge();
      if (i > j) {
         i = j;
      }

      p_176487_1_.setBlockState(p_176487_2_, this.withAge(i), 2);
   }

   protected int getBonemealAgeIncrease(World p_185529_1_) {
      return MathHelper.nextInt(p_185529_1_.rand, 2, 5);
   }

   protected static float getGrowthChance(Block p_180672_0_, IBlockReader p_180672_1_, BlockPos p_180672_2_) {
      float f = 1.0F;
      BlockPos blockpos = p_180672_2_.down();

      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            float f1 = 0.0F;
            BlockState blockstate = p_180672_1_.getBlockState(blockpos.add(i, 0, j));
            if (blockstate.canSustainPlant(p_180672_1_, blockpos.add(i, 0, j), Direction.UP, (IPlantable)p_180672_0_)) {
               f1 = 1.0F;
               if (blockstate.isFertile(p_180672_1_, blockpos.add(i, 0, j))) {
                  f1 = 3.0F;
               }
            }

            if (i != 0 || j != 0) {
               f1 /= 4.0F;
            }

            f += f1;
         }
      }

      BlockPos blockpos1 = p_180672_2_.north();
      BlockPos blockpos2 = p_180672_2_.south();
      BlockPos blockpos3 = p_180672_2_.west();
      BlockPos blockpos4 = p_180672_2_.east();
      boolean flag = p_180672_0_ == p_180672_1_.getBlockState(blockpos3).getBlock() || p_180672_0_ == p_180672_1_.getBlockState(blockpos4).getBlock();
      boolean flag1 = p_180672_0_ == p_180672_1_.getBlockState(blockpos1).getBlock() || p_180672_0_ == p_180672_1_.getBlockState(blockpos2).getBlock();
      if (flag && flag1) {
         f /= 2.0F;
      } else {
         boolean flag2 = p_180672_0_ == p_180672_1_.getBlockState(blockpos3.north()).getBlock() || p_180672_0_ == p_180672_1_.getBlockState(blockpos4.north()).getBlock() || p_180672_0_ == p_180672_1_.getBlockState(blockpos4.south()).getBlock() || p_180672_0_ == p_180672_1_.getBlockState(blockpos3.south()).getBlock();
         if (flag2) {
            f /= 2.0F;
         }
      }

      return f;
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return (p_196260_2_.func_226659_b_(p_196260_3_, 0) >= 8 || p_196260_2_.func_226660_f_(p_196260_3_)) && super.isValidPosition(p_196260_1_, p_196260_2_, p_196260_3_);
   }

   public void onEntityCollision(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (p_196262_4_ instanceof RavagerEntity && ForgeEventFactory.getMobGriefingEvent(p_196262_2_, p_196262_4_)) {
         p_196262_2_.func_225521_a_(p_196262_3_, true, p_196262_4_);
      }

      super.onEntityCollision(p_196262_1_, p_196262_2_, p_196262_3_, p_196262_4_);
   }

   protected IItemProvider getSeedsItem() {
      return Items.WHEAT_SEEDS;
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return new ItemStack(this.getSeedsItem());
   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      return !this.isMaxAge(p_176473_3_);
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return true;
   }

   public void func_225535_a_(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      this.grow(p_225535_1_, p_225535_3_, p_225535_4_);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   static {
      AGE = BlockStateProperties.AGE_0_7;
      SHAPE_BY_AGE = new VoxelShape[]{Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};
   }
}
