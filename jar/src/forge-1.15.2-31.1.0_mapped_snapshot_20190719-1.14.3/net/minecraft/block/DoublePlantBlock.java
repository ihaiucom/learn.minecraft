package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DoublePlantBlock extends BushBlock {
   public static final EnumProperty<DoubleBlockHalf> HALF;

   public DoublePlantBlock(Block.Properties p_i48412_1_) {
      super(p_i48412_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(HALF, DoubleBlockHalf.LOWER));
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      DoubleBlockHalf doubleblockhalf = (DoubleBlockHalf)p_196271_1_.get(HALF);
      if (p_196271_2_.getAxis() != Direction.Axis.Y || doubleblockhalf == DoubleBlockHalf.LOWER != (p_196271_2_ == Direction.UP) || p_196271_3_.getBlock() == this && p_196271_3_.get(HALF) != doubleblockhalf) {
         return doubleblockhalf == DoubleBlockHalf.LOWER && p_196271_2_ == Direction.DOWN && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      } else {
         return Blocks.AIR.getDefaultState();
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockPos blockpos = p_196258_1_.getPos();
      return blockpos.getY() < p_196258_1_.getWorld().getDimension().getHeight() - 1 && p_196258_1_.getWorld().getBlockState(blockpos.up()).isReplaceable(p_196258_1_) ? super.getStateForPlacement(p_196258_1_) : null;
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      p_180633_1_.setBlockState(p_180633_2_.up(), (BlockState)this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER), 3);
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      if (p_196260_1_.get(HALF) != DoubleBlockHalf.UPPER) {
         return super.isValidPosition(p_196260_1_, p_196260_2_, p_196260_3_);
      } else {
         BlockState blockstate = p_196260_2_.getBlockState(p_196260_3_.down());
         if (p_196260_1_.getBlock() != this) {
            return super.isValidPosition(p_196260_1_, p_196260_2_, p_196260_3_);
         } else {
            return blockstate.getBlock() == this && blockstate.get(HALF) == DoubleBlockHalf.LOWER;
         }
      }
   }

   public void placeAt(IWorld p_196390_1_, BlockPos p_196390_2_, int p_196390_3_) {
      p_196390_1_.setBlockState(p_196390_2_, (BlockState)this.getDefaultState().with(HALF, DoubleBlockHalf.LOWER), p_196390_3_);
      p_196390_1_.setBlockState(p_196390_2_.up(), (BlockState)this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER), p_196390_3_);
   }

   public void harvestBlock(World p_180657_1_, PlayerEntity p_180657_2_, BlockPos p_180657_3_, BlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      super.harvestBlock(p_180657_1_, p_180657_2_, p_180657_3_, Blocks.AIR.getDefaultState(), p_180657_5_, p_180657_6_);
   }

   public void onBlockHarvested(World p_176208_1_, BlockPos p_176208_2_, BlockState p_176208_3_, PlayerEntity p_176208_4_) {
      DoubleBlockHalf doubleblockhalf = (DoubleBlockHalf)p_176208_3_.get(HALF);
      BlockPos blockpos = doubleblockhalf == DoubleBlockHalf.LOWER ? p_176208_2_.up() : p_176208_2_.down();
      BlockState blockstate = p_176208_1_.getBlockState(blockpos);
      if (blockstate.getBlock() == this && blockstate.get(HALF) != doubleblockhalf) {
         p_176208_1_.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
         p_176208_1_.playEvent(p_176208_4_, 2001, blockpos, Block.getStateId(blockstate));
         if (!p_176208_1_.isRemote && !p_176208_4_.isCreative()) {
            spawnDrops(p_176208_3_, p_176208_1_, p_176208_2_, (TileEntity)null, p_176208_4_, p_176208_4_.getHeldItemMainhand());
            spawnDrops(blockstate, p_176208_1_, blockpos, (TileEntity)null, p_176208_4_, p_176208_4_.getHeldItemMainhand());
         }
      }

      super.onBlockHarvested(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HALF);
   }

   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.XZ;
   }

   @OnlyIn(Dist.CLIENT)
   public long getPositionRandom(BlockState p_209900_1_, BlockPos p_209900_2_) {
      return MathHelper.getCoordinateRandom(p_209900_2_.getX(), p_209900_2_.down(p_209900_1_.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), p_209900_2_.getZ());
   }

   static {
      HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   }
}
