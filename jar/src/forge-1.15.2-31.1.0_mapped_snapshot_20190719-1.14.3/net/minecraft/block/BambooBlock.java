package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.SwordItem;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BambooLeaves;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BambooBlock extends Block implements IGrowable {
   protected static final VoxelShape SHAPE_NORMAL = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D);
   protected static final VoxelShape SHAPE_LARGE_LEAVES = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
   protected static final VoxelShape SHAPE_COLLISION = Block.makeCuboidShape(6.5D, 0.0D, 6.5D, 9.5D, 16.0D, 9.5D);
   public static final IntegerProperty PROPERTY_AGE;
   public static final EnumProperty<BambooLeaves> PROPERTY_BAMBOO_LEAVES;
   public static final IntegerProperty PROPERTY_STAGE;

   public BambooBlock(Block.Properties p_i49998_1_) {
      super(p_i49998_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(PROPERTY_AGE, 0)).with(PROPERTY_BAMBOO_LEAVES, BambooLeaves.NONE)).with(PROPERTY_STAGE, 0));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(PROPERTY_AGE, PROPERTY_BAMBOO_LEAVES, PROPERTY_STAGE);
   }

   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.XZ;
   }

   public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return true;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      VoxelShape lvt_5_1_ = p_220053_1_.get(PROPERTY_BAMBOO_LEAVES) == BambooLeaves.LARGE ? SHAPE_LARGE_LEAVES : SHAPE_NORMAL;
      Vec3d lvt_6_1_ = p_220053_1_.getOffset(p_220053_2_, p_220053_3_);
      return lvt_5_1_.withOffset(lvt_6_1_.x, lvt_6_1_.y, lvt_6_1_.z);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      Vec3d lvt_5_1_ = p_220071_1_.getOffset(p_220071_2_, p_220071_3_);
      return SHAPE_COLLISION.withOffset(lvt_5_1_.x, lvt_5_1_.y, lvt_5_1_.z);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IFluidState lvt_2_1_ = p_196258_1_.getWorld().getFluidState(p_196258_1_.getPos());
      if (!lvt_2_1_.isEmpty()) {
         return null;
      } else {
         BlockState lvt_3_1_ = p_196258_1_.getWorld().getBlockState(p_196258_1_.getPos().down());
         if (lvt_3_1_.isIn(BlockTags.BAMBOO_PLANTABLE_ON)) {
            Block lvt_4_1_ = lvt_3_1_.getBlock();
            if (lvt_4_1_ == Blocks.BAMBOO_SAPLING) {
               return (BlockState)this.getDefaultState().with(PROPERTY_AGE, 0);
            } else if (lvt_4_1_ == Blocks.BAMBOO) {
               int lvt_5_1_ = (Integer)lvt_3_1_.get(PROPERTY_AGE) > 0 ? 1 : 0;
               return (BlockState)this.getDefaultState().with(PROPERTY_AGE, lvt_5_1_);
            } else {
               return Blocks.BAMBOO_SAPLING.getDefaultState();
            }
         } else {
            return null;
         }
      }
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if (!p_225534_1_.isValidPosition(p_225534_2_, p_225534_3_)) {
         p_225534_2_.destroyBlock(p_225534_3_, true);
      } else if ((Integer)p_225534_1_.get(PROPERTY_STAGE) == 0) {
         if (p_225534_4_.nextInt(3) == 0 && p_225534_2_.isAirBlock(p_225534_3_.up()) && p_225534_2_.func_226659_b_(p_225534_3_.up(), 0) >= 9) {
            int lvt_5_1_ = this.func_220260_b(p_225534_2_, p_225534_3_) + 1;
            if (lvt_5_1_ < 16) {
               this.func_220258_a(p_225534_1_, p_225534_2_, p_225534_3_, p_225534_4_, lvt_5_1_);
            }
         }

      }
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return p_196260_2_.getBlockState(p_196260_3_.down()).isIn(BlockTags.BAMBOO_PLANTABLE_ON);
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (!p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_)) {
         p_196271_4_.getPendingBlockTicks().scheduleTick(p_196271_5_, this, 1);
      }

      if (p_196271_2_ == Direction.UP && p_196271_3_.getBlock() == Blocks.BAMBOO && (Integer)p_196271_3_.get(PROPERTY_AGE) > (Integer)p_196271_1_.get(PROPERTY_AGE)) {
         p_196271_4_.setBlockState(p_196271_5_, (BlockState)p_196271_1_.cycle(PROPERTY_AGE), 2);
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public boolean canGrow(IBlockReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_) {
      int lvt_5_1_ = this.func_220259_a(p_176473_1_, p_176473_2_);
      int lvt_6_1_ = this.func_220260_b(p_176473_1_, p_176473_2_);
      return lvt_5_1_ + lvt_6_1_ + 1 < 16 && (Integer)p_176473_1_.getBlockState(p_176473_2_.up(lvt_5_1_)).get(PROPERTY_STAGE) != 1;
   }

   public boolean canUseBonemeal(World p_180670_1_, Random p_180670_2_, BlockPos p_180670_3_, BlockState p_180670_4_) {
      return true;
   }

   public void func_225535_a_(ServerWorld p_225535_1_, Random p_225535_2_, BlockPos p_225535_3_, BlockState p_225535_4_) {
      int lvt_5_1_ = this.func_220259_a(p_225535_1_, p_225535_3_);
      int lvt_6_1_ = this.func_220260_b(p_225535_1_, p_225535_3_);
      int lvt_7_1_ = lvt_5_1_ + lvt_6_1_ + 1;
      int lvt_8_1_ = 1 + p_225535_2_.nextInt(2);

      for(int lvt_9_1_ = 0; lvt_9_1_ < lvt_8_1_; ++lvt_9_1_) {
         BlockPos lvt_10_1_ = p_225535_3_.up(lvt_5_1_);
         BlockState lvt_11_1_ = p_225535_1_.getBlockState(lvt_10_1_);
         if (lvt_7_1_ >= 16 || (Integer)lvt_11_1_.get(PROPERTY_STAGE) == 1 || !p_225535_1_.isAirBlock(lvt_10_1_.up())) {
            return;
         }

         this.func_220258_a(lvt_11_1_, p_225535_1_, lvt_10_1_, p_225535_2_, lvt_7_1_);
         ++lvt_5_1_;
         ++lvt_7_1_;
      }

   }

   public float getPlayerRelativeBlockHardness(BlockState p_180647_1_, PlayerEntity p_180647_2_, IBlockReader p_180647_3_, BlockPos p_180647_4_) {
      return p_180647_2_.getHeldItemMainhand().getItem() instanceof SwordItem ? 1.0F : super.getPlayerRelativeBlockHardness(p_180647_1_, p_180647_2_, p_180647_3_, p_180647_4_);
   }

   protected void func_220258_a(BlockState p_220258_1_, World p_220258_2_, BlockPos p_220258_3_, Random p_220258_4_, int p_220258_5_) {
      BlockState lvt_6_1_ = p_220258_2_.getBlockState(p_220258_3_.down());
      BlockPos lvt_7_1_ = p_220258_3_.down(2);
      BlockState lvt_8_1_ = p_220258_2_.getBlockState(lvt_7_1_);
      BambooLeaves lvt_9_1_ = BambooLeaves.NONE;
      if (p_220258_5_ >= 1) {
         if (lvt_6_1_.getBlock() == Blocks.BAMBOO && lvt_6_1_.get(PROPERTY_BAMBOO_LEAVES) != BambooLeaves.NONE) {
            if (lvt_6_1_.getBlock() == Blocks.BAMBOO && lvt_6_1_.get(PROPERTY_BAMBOO_LEAVES) != BambooLeaves.NONE) {
               lvt_9_1_ = BambooLeaves.LARGE;
               if (lvt_8_1_.getBlock() == Blocks.BAMBOO) {
                  p_220258_2_.setBlockState(p_220258_3_.down(), (BlockState)lvt_6_1_.with(PROPERTY_BAMBOO_LEAVES, BambooLeaves.SMALL), 3);
                  p_220258_2_.setBlockState(lvt_7_1_, (BlockState)lvt_8_1_.with(PROPERTY_BAMBOO_LEAVES, BambooLeaves.NONE), 3);
               }
            }
         } else {
            lvt_9_1_ = BambooLeaves.SMALL;
         }
      }

      int lvt_10_1_ = (Integer)p_220258_1_.get(PROPERTY_AGE) != 1 && lvt_8_1_.getBlock() != Blocks.BAMBOO ? 0 : 1;
      int lvt_11_1_ = (p_220258_5_ < 11 || p_220258_4_.nextFloat() >= 0.25F) && p_220258_5_ != 15 ? 0 : 1;
      p_220258_2_.setBlockState(p_220258_3_.up(), (BlockState)((BlockState)((BlockState)this.getDefaultState().with(PROPERTY_AGE, lvt_10_1_)).with(PROPERTY_BAMBOO_LEAVES, lvt_9_1_)).with(PROPERTY_STAGE, lvt_11_1_), 3);
   }

   protected int func_220259_a(IBlockReader p_220259_1_, BlockPos p_220259_2_) {
      int lvt_3_1_;
      for(lvt_3_1_ = 0; lvt_3_1_ < 16 && p_220259_1_.getBlockState(p_220259_2_.up(lvt_3_1_ + 1)).getBlock() == Blocks.BAMBOO; ++lvt_3_1_) {
      }

      return lvt_3_1_;
   }

   protected int func_220260_b(IBlockReader p_220260_1_, BlockPos p_220260_2_) {
      int lvt_3_1_;
      for(lvt_3_1_ = 0; lvt_3_1_ < 16 && p_220260_1_.getBlockState(p_220260_2_.down(lvt_3_1_ + 1)).getBlock() == Blocks.BAMBOO; ++lvt_3_1_) {
      }

      return lvt_3_1_;
   }

   static {
      PROPERTY_AGE = BlockStateProperties.AGE_0_1;
      PROPERTY_BAMBOO_LEAVES = BlockStateProperties.BAMBOO_LEAVES;
      PROPERTY_STAGE = BlockStateProperties.STAGE_0_1;
   }
}
