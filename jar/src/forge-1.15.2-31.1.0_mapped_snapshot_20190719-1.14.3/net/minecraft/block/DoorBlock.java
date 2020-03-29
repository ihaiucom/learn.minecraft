package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DoorBlock extends Block {
   public static final DirectionProperty FACING;
   public static final BooleanProperty OPEN;
   public static final EnumProperty<DoorHingeSide> HINGE;
   public static final BooleanProperty POWERED;
   public static final EnumProperty<DoubleBlockHalf> HALF;
   protected static final VoxelShape SOUTH_AABB;
   protected static final VoxelShape NORTH_AABB;
   protected static final VoxelShape WEST_AABB;
   protected static final VoxelShape EAST_AABB;

   protected DoorBlock(Block.Properties p_i48413_1_) {
      super(p_i48413_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(OPEN, false)).with(HINGE, DoorHingeSide.LEFT)).with(POWERED, false)).with(HALF, DoubleBlockHalf.LOWER));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      Direction lvt_5_1_ = (Direction)p_220053_1_.get(FACING);
      boolean lvt_6_1_ = !(Boolean)p_220053_1_.get(OPEN);
      boolean lvt_7_1_ = p_220053_1_.get(HINGE) == DoorHingeSide.RIGHT;
      switch(lvt_5_1_) {
      case EAST:
      default:
         return lvt_6_1_ ? EAST_AABB : (lvt_7_1_ ? NORTH_AABB : SOUTH_AABB);
      case SOUTH:
         return lvt_6_1_ ? SOUTH_AABB : (lvt_7_1_ ? EAST_AABB : WEST_AABB);
      case WEST:
         return lvt_6_1_ ? WEST_AABB : (lvt_7_1_ ? SOUTH_AABB : NORTH_AABB);
      case NORTH:
         return lvt_6_1_ ? NORTH_AABB : (lvt_7_1_ ? WEST_AABB : EAST_AABB);
      }
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      DoubleBlockHalf lvt_7_1_ = (DoubleBlockHalf)p_196271_1_.get(HALF);
      if (p_196271_2_.getAxis() == Direction.Axis.Y && lvt_7_1_ == DoubleBlockHalf.LOWER == (p_196271_2_ == Direction.UP)) {
         return p_196271_3_.getBlock() == this && p_196271_3_.get(HALF) != lvt_7_1_ ? (BlockState)((BlockState)((BlockState)((BlockState)p_196271_1_.with(FACING, p_196271_3_.get(FACING))).with(OPEN, p_196271_3_.get(OPEN))).with(HINGE, p_196271_3_.get(HINGE))).with(POWERED, p_196271_3_.get(POWERED)) : Blocks.AIR.getDefaultState();
      } else {
         return lvt_7_1_ == DoubleBlockHalf.LOWER && p_196271_2_ == Direction.DOWN && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   public void harvestBlock(World p_180657_1_, PlayerEntity p_180657_2_, BlockPos p_180657_3_, BlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      super.harvestBlock(p_180657_1_, p_180657_2_, p_180657_3_, Blocks.AIR.getDefaultState(), p_180657_5_, p_180657_6_);
   }

   public void onBlockHarvested(World p_176208_1_, BlockPos p_176208_2_, BlockState p_176208_3_, PlayerEntity p_176208_4_) {
      DoubleBlockHalf lvt_5_1_ = (DoubleBlockHalf)p_176208_3_.get(HALF);
      BlockPos lvt_6_1_ = lvt_5_1_ == DoubleBlockHalf.LOWER ? p_176208_2_.up() : p_176208_2_.down();
      BlockState lvt_7_1_ = p_176208_1_.getBlockState(lvt_6_1_);
      if (lvt_7_1_.getBlock() == this && lvt_7_1_.get(HALF) != lvt_5_1_) {
         p_176208_1_.setBlockState(lvt_6_1_, Blocks.AIR.getDefaultState(), 35);
         p_176208_1_.playEvent(p_176208_4_, 2001, lvt_6_1_, Block.getStateId(lvt_7_1_));
         ItemStack lvt_8_1_ = p_176208_4_.getHeldItemMainhand();
         if (!p_176208_1_.isRemote && !p_176208_4_.isCreative() && p_176208_4_.canHarvestBlock(lvt_7_1_)) {
            Block.spawnDrops(p_176208_3_, p_176208_1_, p_176208_2_, (TileEntity)null, p_176208_4_, lvt_8_1_);
            Block.spawnDrops(lvt_7_1_, p_176208_1_, lvt_6_1_, (TileEntity)null, p_176208_4_, lvt_8_1_);
         }
      }

      super.onBlockHarvested(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      switch(p_196266_4_) {
      case LAND:
         return (Boolean)p_196266_1_.get(OPEN);
      case WATER:
         return false;
      case AIR:
         return (Boolean)p_196266_1_.get(OPEN);
      default:
         return false;
      }
   }

   private int getCloseSound() {
      return this.material == Material.IRON ? 1011 : 1012;
   }

   private int getOpenSound() {
      return this.material == Material.IRON ? 1005 : 1006;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockPos lvt_2_1_ = p_196258_1_.getPos();
      if (lvt_2_1_.getY() < 255 && p_196258_1_.getWorld().getBlockState(lvt_2_1_.up()).isReplaceable(p_196258_1_)) {
         World lvt_3_1_ = p_196258_1_.getWorld();
         boolean lvt_4_1_ = lvt_3_1_.isBlockPowered(lvt_2_1_) || lvt_3_1_.isBlockPowered(lvt_2_1_.up());
         return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(FACING, p_196258_1_.getPlacementHorizontalFacing())).with(HINGE, this.getHingeSide(p_196258_1_))).with(POWERED, lvt_4_1_)).with(OPEN, lvt_4_1_)).with(HALF, DoubleBlockHalf.LOWER);
      } else {
         return null;
      }
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      p_180633_1_.setBlockState(p_180633_2_.up(), (BlockState)p_180633_3_.with(HALF, DoubleBlockHalf.UPPER), 3);
   }

   private DoorHingeSide getHingeSide(BlockItemUseContext p_208073_1_) {
      IBlockReader lvt_2_1_ = p_208073_1_.getWorld();
      BlockPos lvt_3_1_ = p_208073_1_.getPos();
      Direction lvt_4_1_ = p_208073_1_.getPlacementHorizontalFacing();
      BlockPos lvt_5_1_ = lvt_3_1_.up();
      Direction lvt_6_1_ = lvt_4_1_.rotateYCCW();
      BlockPos lvt_7_1_ = lvt_3_1_.offset(lvt_6_1_);
      BlockState lvt_8_1_ = lvt_2_1_.getBlockState(lvt_7_1_);
      BlockPos lvt_9_1_ = lvt_5_1_.offset(lvt_6_1_);
      BlockState lvt_10_1_ = lvt_2_1_.getBlockState(lvt_9_1_);
      Direction lvt_11_1_ = lvt_4_1_.rotateY();
      BlockPos lvt_12_1_ = lvt_3_1_.offset(lvt_11_1_);
      BlockState lvt_13_1_ = lvt_2_1_.getBlockState(lvt_12_1_);
      BlockPos lvt_14_1_ = lvt_5_1_.offset(lvt_11_1_);
      BlockState lvt_15_1_ = lvt_2_1_.getBlockState(lvt_14_1_);
      int lvt_16_1_ = (lvt_8_1_.func_224756_o(lvt_2_1_, lvt_7_1_) ? -1 : 0) + (lvt_10_1_.func_224756_o(lvt_2_1_, lvt_9_1_) ? -1 : 0) + (lvt_13_1_.func_224756_o(lvt_2_1_, lvt_12_1_) ? 1 : 0) + (lvt_15_1_.func_224756_o(lvt_2_1_, lvt_14_1_) ? 1 : 0);
      boolean lvt_17_1_ = lvt_8_1_.getBlock() == this && lvt_8_1_.get(HALF) == DoubleBlockHalf.LOWER;
      boolean lvt_18_1_ = lvt_13_1_.getBlock() == this && lvt_13_1_.get(HALF) == DoubleBlockHalf.LOWER;
      if ((!lvt_17_1_ || lvt_18_1_) && lvt_16_1_ <= 0) {
         if ((!lvt_18_1_ || lvt_17_1_) && lvt_16_1_ >= 0) {
            int lvt_19_1_ = lvt_4_1_.getXOffset();
            int lvt_20_1_ = lvt_4_1_.getZOffset();
            Vec3d lvt_21_1_ = p_208073_1_.getHitVec();
            double lvt_22_1_ = lvt_21_1_.x - (double)lvt_3_1_.getX();
            double lvt_24_1_ = lvt_21_1_.z - (double)lvt_3_1_.getZ();
            return (lvt_19_1_ >= 0 || lvt_24_1_ >= 0.5D) && (lvt_19_1_ <= 0 || lvt_24_1_ <= 0.5D) && (lvt_20_1_ >= 0 || lvt_22_1_ <= 0.5D) && (lvt_20_1_ <= 0 || lvt_22_1_ >= 0.5D) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
         } else {
            return DoorHingeSide.LEFT;
         }
      } else {
         return DoorHingeSide.RIGHT;
      }
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (this.material == Material.IRON) {
         return ActionResultType.PASS;
      } else {
         p_225533_1_ = (BlockState)p_225533_1_.cycle(OPEN);
         p_225533_2_.setBlockState(p_225533_3_, p_225533_1_, 10);
         p_225533_2_.playEvent(p_225533_4_, (Boolean)p_225533_1_.get(OPEN) ? this.getOpenSound() : this.getCloseSound(), p_225533_3_, 0);
         return ActionResultType.SUCCESS;
      }
   }

   public void toggleDoor(World p_176512_1_, BlockPos p_176512_2_, boolean p_176512_3_) {
      BlockState lvt_4_1_ = p_176512_1_.getBlockState(p_176512_2_);
      if (lvt_4_1_.getBlock() == this && (Boolean)lvt_4_1_.get(OPEN) != p_176512_3_) {
         p_176512_1_.setBlockState(p_176512_2_, (BlockState)lvt_4_1_.with(OPEN, p_176512_3_), 10);
         this.playSound(p_176512_1_, p_176512_2_, p_176512_3_);
      }
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      boolean lvt_7_1_ = p_220069_2_.isBlockPowered(p_220069_3_) || p_220069_2_.isBlockPowered(p_220069_3_.offset(p_220069_1_.get(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
      if (p_220069_4_ != this && lvt_7_1_ != (Boolean)p_220069_1_.get(POWERED)) {
         if (lvt_7_1_ != (Boolean)p_220069_1_.get(OPEN)) {
            this.playSound(p_220069_2_, p_220069_3_, lvt_7_1_);
         }

         p_220069_2_.setBlockState(p_220069_3_, (BlockState)((BlockState)p_220069_1_.with(POWERED, lvt_7_1_)).with(OPEN, lvt_7_1_), 2);
      }

   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      BlockPos lvt_4_1_ = p_196260_3_.down();
      BlockState lvt_5_1_ = p_196260_2_.getBlockState(lvt_4_1_);
      if (p_196260_1_.get(HALF) == DoubleBlockHalf.LOWER) {
         return lvt_5_1_.func_224755_d(p_196260_2_, lvt_4_1_, Direction.UP);
      } else {
         return lvt_5_1_.getBlock() == this;
      }
   }

   private void playSound(World p_196426_1_, BlockPos p_196426_2_, boolean p_196426_3_) {
      p_196426_1_.playEvent((PlayerEntity)null, p_196426_3_ ? this.getOpenSound() : this.getCloseSound(), p_196426_2_, 0);
   }

   public PushReaction getPushReaction(BlockState p_149656_1_) {
      return PushReaction.DESTROY;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_2_ == Mirror.NONE ? p_185471_1_ : (BlockState)p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(FACING))).cycle(HINGE);
   }

   @OnlyIn(Dist.CLIENT)
   public long getPositionRandom(BlockState p_209900_1_, BlockPos p_209900_2_) {
      return MathHelper.getCoordinateRandom(p_209900_2_.getX(), p_209900_2_.down(p_209900_1_.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), p_209900_2_.getZ());
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HALF, FACING, OPEN, HINGE, POWERED);
   }

   static {
      FACING = HorizontalBlock.HORIZONTAL_FACING;
      OPEN = BlockStateProperties.OPEN;
      HINGE = BlockStateProperties.DOOR_HINGE;
      POWERED = BlockStateProperties.POWERED;
      HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
      SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
      NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
      WEST_AABB = Block.makeCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
   }
}
