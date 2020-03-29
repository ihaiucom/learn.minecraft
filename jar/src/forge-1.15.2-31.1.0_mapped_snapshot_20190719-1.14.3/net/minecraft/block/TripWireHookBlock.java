package net.minecraft.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TripWireHookBlock extends Block {
   public static final DirectionProperty FACING;
   public static final BooleanProperty POWERED;
   public static final BooleanProperty ATTACHED;
   protected static final VoxelShape HOOK_NORTH_AABB;
   protected static final VoxelShape HOOK_SOUTH_AABB;
   protected static final VoxelShape HOOK_WEST_AABB;
   protected static final VoxelShape HOOK_EAST_AABB;

   public TripWireHookBlock(Block.Properties p_i48304_1_) {
      super(p_i48304_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(POWERED, false)).with(ATTACHED, false));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      switch((Direction)p_220053_1_.get(FACING)) {
      case EAST:
      default:
         return HOOK_EAST_AABB;
      case WEST:
         return HOOK_WEST_AABB;
      case SOUTH:
         return HOOK_SOUTH_AABB;
      case NORTH:
         return HOOK_NORTH_AABB;
      }
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      Direction lvt_4_1_ = (Direction)p_196260_1_.get(FACING);
      BlockPos lvt_5_1_ = p_196260_3_.offset(lvt_4_1_.getOpposite());
      BlockState lvt_6_1_ = p_196260_2_.getBlockState(lvt_5_1_);
      return lvt_4_1_.getAxis().isHorizontal() && lvt_6_1_.func_224755_d(p_196260_2_, lvt_5_1_, lvt_4_1_) && !lvt_6_1_.canProvidePower();
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_.getOpposite() == p_196271_1_.get(FACING) && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState lvt_2_1_ = (BlockState)((BlockState)this.getDefaultState().with(POWERED, false)).with(ATTACHED, false);
      IWorldReader lvt_3_1_ = p_196258_1_.getWorld();
      BlockPos lvt_4_1_ = p_196258_1_.getPos();
      Direction[] lvt_5_1_ = p_196258_1_.getNearestLookingDirections();
      Direction[] var6 = lvt_5_1_;
      int var7 = lvt_5_1_.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Direction lvt_9_1_ = var6[var8];
         if (lvt_9_1_.getAxis().isHorizontal()) {
            Direction lvt_10_1_ = lvt_9_1_.getOpposite();
            lvt_2_1_ = (BlockState)lvt_2_1_.with(FACING, lvt_10_1_);
            if (lvt_2_1_.isValidPosition(lvt_3_1_, lvt_4_1_)) {
               return lvt_2_1_;
            }
         }
      }

      return null;
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      this.calculateState(p_180633_1_, p_180633_2_, p_180633_3_, false, false, -1, (BlockState)null);
   }

   public void calculateState(World p_176260_1_, BlockPos p_176260_2_, BlockState p_176260_3_, boolean p_176260_4_, boolean p_176260_5_, int p_176260_6_, @Nullable BlockState p_176260_7_) {
      Direction lvt_8_1_ = (Direction)p_176260_3_.get(FACING);
      boolean lvt_9_1_ = (Boolean)p_176260_3_.get(ATTACHED);
      boolean lvt_10_1_ = (Boolean)p_176260_3_.get(POWERED);
      boolean lvt_11_1_ = !p_176260_4_;
      boolean lvt_12_1_ = false;
      int lvt_13_1_ = 0;
      BlockState[] lvt_14_1_ = new BlockState[42];

      BlockPos lvt_16_2_;
      for(int lvt_15_1_ = 1; lvt_15_1_ < 42; ++lvt_15_1_) {
         lvt_16_2_ = p_176260_2_.offset(lvt_8_1_, lvt_15_1_);
         BlockState lvt_17_1_ = p_176260_1_.getBlockState(lvt_16_2_);
         if (lvt_17_1_.getBlock() == Blocks.TRIPWIRE_HOOK) {
            if (lvt_17_1_.get(FACING) == lvt_8_1_.getOpposite()) {
               lvt_13_1_ = lvt_15_1_;
            }
            break;
         }

         if (lvt_17_1_.getBlock() != Blocks.TRIPWIRE && lvt_15_1_ != p_176260_6_) {
            lvt_14_1_[lvt_15_1_] = null;
            lvt_11_1_ = false;
         } else {
            if (lvt_15_1_ == p_176260_6_) {
               lvt_17_1_ = (BlockState)MoreObjects.firstNonNull(p_176260_7_, lvt_17_1_);
            }

            boolean lvt_18_1_ = !(Boolean)lvt_17_1_.get(TripWireBlock.DISARMED);
            boolean lvt_19_1_ = (Boolean)lvt_17_1_.get(TripWireBlock.POWERED);
            lvt_12_1_ |= lvt_18_1_ && lvt_19_1_;
            lvt_14_1_[lvt_15_1_] = lvt_17_1_;
            if (lvt_15_1_ == p_176260_6_) {
               p_176260_1_.getPendingBlockTicks().scheduleTick(p_176260_2_, this, this.tickRate(p_176260_1_));
               lvt_11_1_ &= lvt_18_1_;
            }
         }
      }

      lvt_11_1_ &= lvt_13_1_ > 1;
      lvt_12_1_ &= lvt_11_1_;
      BlockState lvt_15_2_ = (BlockState)((BlockState)this.getDefaultState().with(ATTACHED, lvt_11_1_)).with(POWERED, lvt_12_1_);
      if (lvt_13_1_ > 0) {
         lvt_16_2_ = p_176260_2_.offset(lvt_8_1_, lvt_13_1_);
         Direction lvt_17_2_ = lvt_8_1_.getOpposite();
         p_176260_1_.setBlockState(lvt_16_2_, (BlockState)lvt_15_2_.with(FACING, lvt_17_2_), 3);
         this.notifyNeighbors(p_176260_1_, lvt_16_2_, lvt_17_2_);
         this.playSound(p_176260_1_, lvt_16_2_, lvt_11_1_, lvt_12_1_, lvt_9_1_, lvt_10_1_);
      }

      this.playSound(p_176260_1_, p_176260_2_, lvt_11_1_, lvt_12_1_, lvt_9_1_, lvt_10_1_);
      if (!p_176260_4_) {
         p_176260_1_.setBlockState(p_176260_2_, (BlockState)lvt_15_2_.with(FACING, lvt_8_1_), 3);
         if (p_176260_5_) {
            this.notifyNeighbors(p_176260_1_, p_176260_2_, lvt_8_1_);
         }
      }

      if (lvt_9_1_ != lvt_11_1_) {
         for(int lvt_16_3_ = 1; lvt_16_3_ < lvt_13_1_; ++lvt_16_3_) {
            BlockPos lvt_17_3_ = p_176260_2_.offset(lvt_8_1_, lvt_16_3_);
            BlockState lvt_18_2_ = lvt_14_1_[lvt_16_3_];
            if (lvt_18_2_ != null) {
               p_176260_1_.setBlockState(lvt_17_3_, (BlockState)lvt_18_2_.with(ATTACHED, lvt_11_1_), 3);
               if (!p_176260_1_.getBlockState(lvt_17_3_).isAir()) {
               }
            }
         }
      }

   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      this.calculateState(p_225534_2_, p_225534_3_, p_225534_1_, false, true, -1, (BlockState)null);
   }

   private void playSound(World p_180694_1_, BlockPos p_180694_2_, boolean p_180694_3_, boolean p_180694_4_, boolean p_180694_5_, boolean p_180694_6_) {
      if (p_180694_4_ && !p_180694_6_) {
         p_180694_1_.playSound((PlayerEntity)null, p_180694_2_, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.4F, 0.6F);
      } else if (!p_180694_4_ && p_180694_6_) {
         p_180694_1_.playSound((PlayerEntity)null, p_180694_2_, SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 0.4F, 0.5F);
      } else if (p_180694_3_ && !p_180694_5_) {
         p_180694_1_.playSound((PlayerEntity)null, p_180694_2_, SoundEvents.BLOCK_TRIPWIRE_ATTACH, SoundCategory.BLOCKS, 0.4F, 0.7F);
      } else if (!p_180694_3_ && p_180694_5_) {
         p_180694_1_.playSound((PlayerEntity)null, p_180694_2_, SoundEvents.BLOCK_TRIPWIRE_DETACH, SoundCategory.BLOCKS, 0.4F, 1.2F / (p_180694_1_.rand.nextFloat() * 0.2F + 0.9F));
      }

   }

   private void notifyNeighbors(World p_176262_1_, BlockPos p_176262_2_, Direction p_176262_3_) {
      p_176262_1_.notifyNeighborsOfStateChange(p_176262_2_, this);
      p_176262_1_.notifyNeighborsOfStateChange(p_176262_2_.offset(p_176262_3_.getOpposite()), this);
   }

   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         boolean lvt_6_1_ = (Boolean)p_196243_1_.get(ATTACHED);
         boolean lvt_7_1_ = (Boolean)p_196243_1_.get(POWERED);
         if (lvt_6_1_ || lvt_7_1_) {
            this.calculateState(p_196243_2_, p_196243_3_, p_196243_1_, true, false, -1, (BlockState)null);
         }

         if (lvt_7_1_) {
            p_196243_2_.notifyNeighborsOfStateChange(p_196243_3_, this);
            p_196243_2_.notifyNeighborsOfStateChange(p_196243_3_.offset(((Direction)p_196243_1_.get(FACING)).getOpposite()), this);
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public int getWeakPower(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return (Boolean)p_180656_1_.get(POWERED) ? 15 : 0;
   }

   public int getStrongPower(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
      if (!(Boolean)p_176211_1_.get(POWERED)) {
         return 0;
      } else {
         return p_176211_1_.get(FACING) == p_176211_4_ ? 15 : 0;
      }
   }

   public boolean canProvidePower(BlockState p_149744_1_) {
      return true;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, POWERED, ATTACHED);
   }

   static {
      FACING = HorizontalBlock.HORIZONTAL_FACING;
      POWERED = BlockStateProperties.POWERED;
      ATTACHED = BlockStateProperties.ATTACHED;
      HOOK_NORTH_AABB = Block.makeCuboidShape(5.0D, 0.0D, 10.0D, 11.0D, 10.0D, 16.0D);
      HOOK_SOUTH_AABB = Block.makeCuboidShape(5.0D, 0.0D, 0.0D, 11.0D, 10.0D, 6.0D);
      HOOK_WEST_AABB = Block.makeCuboidShape(10.0D, 0.0D, 5.0D, 16.0D, 10.0D, 11.0D);
      HOOK_EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 5.0D, 6.0D, 10.0D, 11.0D);
   }
}
