package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AttachedStemBlock extends BushBlock {
   public static final DirectionProperty FACING;
   private final StemGrownBlock grownFruit;
   private static final Map<Direction, VoxelShape> SHAPES;

   protected AttachedStemBlock(StemGrownBlock p_i48449_1_, Block.Properties p_i48449_2_) {
      super(p_i48449_2_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH));
      this.grownFruit = p_i48449_1_;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return (VoxelShape)SHAPES.get(p_220053_1_.get(FACING));
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_3_.getBlock() != this.grownFruit && p_196271_2_ == p_196271_1_.get(FACING) ? (BlockState)this.grownFruit.getStem().getDefaultState().with(StemBlock.AGE, 7) : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   protected boolean isValidGround(BlockState p_200014_1_, IBlockReader p_200014_2_, BlockPos p_200014_3_) {
      return p_200014_1_.getBlock() == Blocks.FARMLAND;
   }

   @OnlyIn(Dist.CLIENT)
   protected Item getSeeds() {
      if (this.grownFruit == Blocks.PUMPKIN) {
         return Items.PUMPKIN_SEEDS;
      } else {
         return this.grownFruit == Blocks.MELON ? Items.MELON_SEEDS : Items.AIR;
      }
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return new ItemStack(this.getSeeds());
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING);
   }

   static {
      FACING = HorizontalBlock.HORIZONTAL_FACING;
      SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.SOUTH, Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 16.0D), Direction.WEST, Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D), Direction.NORTH, Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 10.0D, 10.0D), Direction.EAST, Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 16.0D, 10.0D, 10.0D)));
   }
}
