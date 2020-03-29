package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TripWireBlock extends Block {
   public static final BooleanProperty POWERED;
   public static final BooleanProperty ATTACHED;
   public static final BooleanProperty DISARMED;
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   private static final Map<Direction, BooleanProperty> field_196537_E;
   protected static final VoxelShape AABB;
   protected static final VoxelShape TRIP_WRITE_ATTACHED_AABB;
   private final TripWireHookBlock field_196538_F;

   public TripWireBlock(TripWireHookBlock p_i48305_1_, Block.Properties p_i48305_2_) {
      super(p_i48305_2_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(POWERED, false)).with(ATTACHED, false)).with(DISARMED, false)).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false));
      this.field_196538_F = p_i48305_1_;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return (Boolean)p_220053_1_.get(ATTACHED) ? AABB : TRIP_WRITE_ATTACHED_AABB;
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      IBlockReader lvt_2_1_ = p_196258_1_.getWorld();
      BlockPos lvt_3_1_ = p_196258_1_.getPos();
      return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(NORTH, this.shouldConnectTo(lvt_2_1_.getBlockState(lvt_3_1_.north()), Direction.NORTH))).with(EAST, this.shouldConnectTo(lvt_2_1_.getBlockState(lvt_3_1_.east()), Direction.EAST))).with(SOUTH, this.shouldConnectTo(lvt_2_1_.getBlockState(lvt_3_1_.south()), Direction.SOUTH))).with(WEST, this.shouldConnectTo(lvt_2_1_.getBlockState(lvt_3_1_.west()), Direction.WEST));
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return p_196271_2_.getAxis().isHorizontal() ? (BlockState)p_196271_1_.with((IProperty)field_196537_E.get(p_196271_2_), this.shouldConnectTo(p_196271_3_, p_196271_2_)) : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (p_220082_4_.getBlock() != p_220082_1_.getBlock()) {
         this.notifyHook(p_220082_2_, p_220082_3_, p_220082_1_);
      }
   }

   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (!p_196243_5_ && p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         this.notifyHook(p_196243_2_, p_196243_3_, (BlockState)p_196243_1_.with(POWERED, true));
      }
   }

   public void onBlockHarvested(World p_176208_1_, BlockPos p_176208_2_, BlockState p_176208_3_, PlayerEntity p_176208_4_) {
      if (!p_176208_1_.isRemote && !p_176208_4_.getHeldItemMainhand().isEmpty() && p_176208_4_.getHeldItemMainhand().getItem() == Items.SHEARS) {
         p_176208_1_.setBlockState(p_176208_2_, (BlockState)p_176208_3_.with(DISARMED, true), 4);
      }

      super.onBlockHarvested(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

   private void notifyHook(World p_176286_1_, BlockPos p_176286_2_, BlockState p_176286_3_) {
      Direction[] var4 = new Direction[]{Direction.SOUTH, Direction.WEST};
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction lvt_7_1_ = var4[var6];

         for(int lvt_8_1_ = 1; lvt_8_1_ < 42; ++lvt_8_1_) {
            BlockPos lvt_9_1_ = p_176286_2_.offset(lvt_7_1_, lvt_8_1_);
            BlockState lvt_10_1_ = p_176286_1_.getBlockState(lvt_9_1_);
            if (lvt_10_1_.getBlock() == this.field_196538_F) {
               if (lvt_10_1_.get(TripWireHookBlock.FACING) == lvt_7_1_.getOpposite()) {
                  this.field_196538_F.calculateState(p_176286_1_, lvt_9_1_, lvt_10_1_, false, true, lvt_8_1_, p_176286_3_);
               }
               break;
            }

            if (lvt_10_1_.getBlock() != this) {
               break;
            }
         }
      }

   }

   public void onEntityCollision(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      if (!p_196262_2_.isRemote) {
         if (!(Boolean)p_196262_1_.get(POWERED)) {
            this.updateState(p_196262_2_, p_196262_3_);
         }
      }
   }

   public void func_225534_a_(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
      if ((Boolean)p_225534_2_.getBlockState(p_225534_3_).get(POWERED)) {
         this.updateState(p_225534_2_, p_225534_3_);
      }
   }

   private void updateState(World p_176288_1_, BlockPos p_176288_2_) {
      BlockState lvt_3_1_ = p_176288_1_.getBlockState(p_176288_2_);
      boolean lvt_4_1_ = (Boolean)lvt_3_1_.get(POWERED);
      boolean lvt_5_1_ = false;
      List<? extends Entity> lvt_6_1_ = p_176288_1_.getEntitiesWithinAABBExcludingEntity((Entity)null, lvt_3_1_.getShape(p_176288_1_, p_176288_2_).getBoundingBox().offset(p_176288_2_));
      if (!lvt_6_1_.isEmpty()) {
         Iterator var7 = lvt_6_1_.iterator();

         while(var7.hasNext()) {
            Entity lvt_8_1_ = (Entity)var7.next();
            if (!lvt_8_1_.doesEntityNotTriggerPressurePlate()) {
               lvt_5_1_ = true;
               break;
            }
         }
      }

      if (lvt_5_1_ != lvt_4_1_) {
         lvt_3_1_ = (BlockState)lvt_3_1_.with(POWERED, lvt_5_1_);
         p_176288_1_.setBlockState(p_176288_2_, lvt_3_1_, 3);
         this.notifyHook(p_176288_1_, p_176288_2_, lvt_3_1_);
      }

      if (lvt_5_1_) {
         p_176288_1_.getPendingBlockTicks().scheduleTick(new BlockPos(p_176288_2_), this, this.tickRate(p_176288_1_));
      }

   }

   public boolean shouldConnectTo(BlockState p_196536_1_, Direction p_196536_2_) {
      Block lvt_3_1_ = p_196536_1_.getBlock();
      if (lvt_3_1_ == this.field_196538_F) {
         return p_196536_1_.get(TripWireHookBlock.FACING) == p_196536_2_.getOpposite();
      } else {
         return lvt_3_1_ == this;
      }
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      switch(p_185499_2_) {
      case CLOCKWISE_180:
         return (BlockState)((BlockState)((BlockState)((BlockState)p_185499_1_.with(NORTH, p_185499_1_.get(SOUTH))).with(EAST, p_185499_1_.get(WEST))).with(SOUTH, p_185499_1_.get(NORTH))).with(WEST, p_185499_1_.get(EAST));
      case COUNTERCLOCKWISE_90:
         return (BlockState)((BlockState)((BlockState)((BlockState)p_185499_1_.with(NORTH, p_185499_1_.get(EAST))).with(EAST, p_185499_1_.get(SOUTH))).with(SOUTH, p_185499_1_.get(WEST))).with(WEST, p_185499_1_.get(NORTH));
      case CLOCKWISE_90:
         return (BlockState)((BlockState)((BlockState)((BlockState)p_185499_1_.with(NORTH, p_185499_1_.get(WEST))).with(EAST, p_185499_1_.get(NORTH))).with(SOUTH, p_185499_1_.get(EAST))).with(WEST, p_185499_1_.get(SOUTH));
      default:
         return p_185499_1_;
      }
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      switch(p_185471_2_) {
      case LEFT_RIGHT:
         return (BlockState)((BlockState)p_185471_1_.with(NORTH, p_185471_1_.get(SOUTH))).with(SOUTH, p_185471_1_.get(NORTH));
      case FRONT_BACK:
         return (BlockState)((BlockState)p_185471_1_.with(EAST, p_185471_1_.get(WEST))).with(WEST, p_185471_1_.get(EAST));
      default:
         return super.mirror(p_185471_1_, p_185471_2_);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
   }

   static {
      POWERED = BlockStateProperties.POWERED;
      ATTACHED = BlockStateProperties.ATTACHED;
      DISARMED = BlockStateProperties.DISARMED;
      NORTH = SixWayBlock.NORTH;
      EAST = SixWayBlock.EAST;
      SOUTH = SixWayBlock.SOUTH;
      WEST = SixWayBlock.WEST;
      field_196537_E = FourWayBlock.FACING_TO_PROPERTY_MAP;
      AABB = Block.makeCuboidShape(0.0D, 1.0D, 0.0D, 16.0D, 2.5D, 16.0D);
      TRIP_WRITE_ATTACHED_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   }
}
