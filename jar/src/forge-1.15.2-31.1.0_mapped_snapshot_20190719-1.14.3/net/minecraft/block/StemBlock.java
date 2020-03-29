package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.PlantType;

public class StemBlock extends BushBlock implements IGrowable {
   public static final IntegerProperty AGE;
   protected static final VoxelShape[] SHAPES;
   private final StemGrownBlock crop;

   protected StemBlock(StemGrownBlock p_i48318_1_, Block.Properties p_i48318_2_) {
      super(p_i48318_2_);
      this.crop = p_i48318_1_;
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(AGE, 0));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPES[(Integer)p_220053_1_.get(AGE)];
   }

   protected boolean isValidGround(BlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return p_200014_1_.getBlock() == Blocks.FARMLAND;
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      super.func_225534_a_(p_225534_1_, p_225534_2_, p_225534_3_, p_225534_4_);
      if (p_225534_2_.isAreaLoaded(p_225534_3_, 1)) {
         if (p_225534_2_.func_226659_b_(p_225534_3_, 0) >= 9) {
            float f = CropsBlock.getGrowthChance(this, p_225534_2_, p_225534_3_);
            if (ForgeHooks.onCropsGrowPre(p_225534_2_, p_225534_3_, p_225534_1_, p_225534_4_.nextInt((int)(25.0F / f) + 1) == 0)) {
               int i = (Integer)p_225534_1_.get(AGE);
               if (i < 7) {
                  p_225534_2_.setBlockState(p_225534_3_, (BlockState)p_225534_1_.with(AGE, i + 1), 2);
               } else {
                  Direction direction = Direction.Plane.HORIZONTAL.random(p_225534_4_);
                  BlockPos blockpos = p_225534_3_.offset(direction);
                  BlockState soil = p_225534_2_.getBlockState(blockpos.down());
                  Block block = soil.getBlock();
                  if (p_225534_2_.isAirBlock(blockpos) && (soil.canSustainPlant(p_225534_2_, blockpos.down(), Direction.UP, this) || block == Blocks.FARMLAND || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.GRASS_BLOCK)) {
                     p_225534_2_.setBlockState(blockpos, this.crop.getDefaultState());
                     p_225534_2_.setBlockState(p_225534_3_, (BlockState)this.crop.getAttachedStem().getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, direction));
                  }
               }

               ForgeHooks.onCropsGrowPost(p_225534_2_, p_225534_3_, p_225534_1_);
            }
         }

      }
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   protected Item getSeedItem() {
      if (this.crop == Blocks.PUMPKIN) {
         return Items.PUMPKIN_SEEDS;
      } else {
         return this.crop == Blocks.MELON ? Items.MELON_SEEDS : null;
      }
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      Item item = this.getSeedItem();
      return item == null ? ItemStack.EMPTY : new ItemStack(item);
   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      return (Integer)p_176473_3_.get(AGE) != 7;
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return true;
   }

   public void func_225535_a_(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      int i = Math.min(7, (Integer)p_225535_4_.get(AGE) + MathHelper.nextInt(p_225535_1_.rand, 2, 5));
      BlockState blockstate = (BlockState)p_225535_4_.with(AGE, i);
      p_225535_1_.setBlockState(p_225535_3_, blockstate, 2);
      if (i == 7) {
         blockstate.func_227033_a_(p_225535_1_, p_225535_3_, p_225535_1_.rand);
      }

   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(AGE);
   }

   public StemGrownBlock getCrop() {
      return this.crop;
   }

   public PlantType getPlantType(IBlockReader p_getPlantType_1_, BlockPos p_getPlantType_2_) {
      return PlantType.Crop;
   }

   static {
      AGE = BlockStateProperties.AGE_0_7;
      SHAPES = new VoxelShape[]{Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 4.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 8.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 12.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D)};
   }
}
