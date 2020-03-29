package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FenceGateBlock extends HorizontalBlock {
   public static final BooleanProperty OPEN;
   public static final BooleanProperty POWERED;
   public static final BooleanProperty IN_WALL;
   protected static final VoxelShape AABB_HITBOX_ZAXIS;
   protected static final VoxelShape AABB_HITBOX_XAXIS;
   protected static final VoxelShape AABB_HITBOX_ZAXIS_INWALL;
   protected static final VoxelShape AABB_HITBOX_XAXIS_INWALL;
   protected static final VoxelShape field_208068_x;
   protected static final VoxelShape AABB_COLLISION_BOX_XAXIS;
   protected static final VoxelShape field_208069_z;
   protected static final VoxelShape AABB_COLLISION_BOX_ZAXIS;
   protected static final VoxelShape field_208066_B;
   protected static final VoxelShape field_208067_C;

   public FenceGateBlock(Block.Properties p_i48398_1_) {
      super(p_i48398_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(OPEN, false)).with(POWERED, false)).with(IN_WALL, false));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      if ((Boolean)p_220053_1_.get(IN_WALL)) {
         return ((Direction)p_220053_1_.get(HORIZONTAL_FACING)).getAxis() == Direction.Axis.X ? AABB_HITBOX_XAXIS_INWALL : AABB_HITBOX_ZAXIS_INWALL;
      } else {
         return ((Direction)p_220053_1_.get(HORIZONTAL_FACING)).getAxis() == Direction.Axis.X ? AABB_HITBOX_XAXIS : AABB_HITBOX_ZAXIS;
      }
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      Direction.Axis lvt_7_1_ = p_196271_2_.getAxis();
      if (((Direction)p_196271_1_.get(HORIZONTAL_FACING)).rotateY().getAxis() != lvt_7_1_) {
         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      } else {
         boolean lvt_8_1_ = this.isWall(p_196271_3_) || this.isWall(p_196271_4_.getBlockState(p_196271_5_.offset(p_196271_2_.getOpposite())));
         return (BlockState)p_196271_1_.with(IN_WALL, lvt_8_1_);
      }
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      if ((Boolean)p_220071_1_.get(OPEN)) {
         return VoxelShapes.empty();
      } else {
         return ((Direction)p_220071_1_.get(HORIZONTAL_FACING)).getAxis() == Direction.Axis.Z ? field_208068_x : AABB_COLLISION_BOX_XAXIS;
      }
   }

   public VoxelShape getRenderShape(BlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_) {
      if ((Boolean)p_196247_1_.get(IN_WALL)) {
         return ((Direction)p_196247_1_.get(HORIZONTAL_FACING)).getAxis() == Direction.Axis.X ? field_208067_C : field_208066_B;
      } else {
         return ((Direction)p_196247_1_.get(HORIZONTAL_FACING)).getAxis() == Direction.Axis.X ? AABB_COLLISION_BOX_ZAXIS : field_208069_z;
      }
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

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      World lvt_2_1_ = p_196258_1_.getWorld();
      BlockPos lvt_3_1_ = p_196258_1_.getPos();
      boolean lvt_4_1_ = lvt_2_1_.isBlockPowered(lvt_3_1_);
      Direction lvt_5_1_ = p_196258_1_.getPlacementHorizontalFacing();
      Direction.Axis lvt_6_1_ = lvt_5_1_.getAxis();
      boolean lvt_7_1_ = lvt_6_1_ == Direction.Axis.Z && (this.isWall(lvt_2_1_.getBlockState(lvt_3_1_.west())) || this.isWall(lvt_2_1_.getBlockState(lvt_3_1_.east()))) || lvt_6_1_ == Direction.Axis.X && (this.isWall(lvt_2_1_.getBlockState(lvt_3_1_.north())) || this.isWall(lvt_2_1_.getBlockState(lvt_3_1_.south())));
      return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(HORIZONTAL_FACING, lvt_5_1_)).with(OPEN, lvt_4_1_)).with(POWERED, lvt_4_1_)).with(IN_WALL, lvt_7_1_);
   }

   private boolean isWall(BlockState p_196380_1_) {
      return p_196380_1_.getBlock().isIn(BlockTags.WALLS);
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if ((Boolean)p_225533_1_.get(OPEN)) {
         p_225533_1_ = (BlockState)p_225533_1_.with(OPEN, false);
         p_225533_2_.setBlockState(p_225533_3_, p_225533_1_, 10);
      } else {
         Direction lvt_7_1_ = p_225533_4_.getHorizontalFacing();
         if (p_225533_1_.get(HORIZONTAL_FACING) == lvt_7_1_.getOpposite()) {
            p_225533_1_ = (BlockState)p_225533_1_.with(HORIZONTAL_FACING, lvt_7_1_);
         }

         p_225533_1_ = (BlockState)p_225533_1_.with(OPEN, true);
         p_225533_2_.setBlockState(p_225533_3_, p_225533_1_, 10);
      }

      p_225533_2_.playEvent(p_225533_4_, (Boolean)p_225533_1_.get(OPEN) ? 1008 : 1014, p_225533_3_, 0);
      return ActionResultType.SUCCESS;
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (!p_220069_2_.isRemote) {
         boolean lvt_7_1_ = p_220069_2_.isBlockPowered(p_220069_3_);
         if ((Boolean)p_220069_1_.get(POWERED) != lvt_7_1_) {
            p_220069_2_.setBlockState(p_220069_3_, (BlockState)((BlockState)p_220069_1_.with(POWERED, lvt_7_1_)).with(OPEN, lvt_7_1_), 2);
            if ((Boolean)p_220069_1_.get(OPEN) != lvt_7_1_) {
               p_220069_2_.playEvent((PlayerEntity)null, lvt_7_1_ ? 1008 : 1014, p_220069_3_, 0);
            }
         }

      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HORIZONTAL_FACING, OPEN, POWERED, IN_WALL);
   }

   public static boolean isParallel(BlockState p_220253_0_, Direction p_220253_1_) {
      return ((Direction)p_220253_0_.get(HORIZONTAL_FACING)).getAxis() == p_220253_1_.rotateY().getAxis();
   }

   static {
      OPEN = BlockStateProperties.OPEN;
      POWERED = BlockStateProperties.POWERED;
      IN_WALL = BlockStateProperties.IN_WALL;
      AABB_HITBOX_ZAXIS = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
      AABB_HITBOX_XAXIS = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
      AABB_HITBOX_ZAXIS_INWALL = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 13.0D, 10.0D);
      AABB_HITBOX_XAXIS_INWALL = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 13.0D, 16.0D);
      field_208068_x = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 24.0D, 10.0D);
      AABB_COLLISION_BOX_XAXIS = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 24.0D, 16.0D);
      field_208069_z = VoxelShapes.or(Block.makeCuboidShape(0.0D, 5.0D, 7.0D, 2.0D, 16.0D, 9.0D), Block.makeCuboidShape(14.0D, 5.0D, 7.0D, 16.0D, 16.0D, 9.0D));
      AABB_COLLISION_BOX_ZAXIS = VoxelShapes.or(Block.makeCuboidShape(7.0D, 5.0D, 0.0D, 9.0D, 16.0D, 2.0D), Block.makeCuboidShape(7.0D, 5.0D, 14.0D, 9.0D, 16.0D, 16.0D));
      field_208066_B = VoxelShapes.or(Block.makeCuboidShape(0.0D, 2.0D, 7.0D, 2.0D, 13.0D, 9.0D), Block.makeCuboidShape(14.0D, 2.0D, 7.0D, 16.0D, 13.0D, 9.0D));
      field_208067_C = VoxelShapes.or(Block.makeCuboidShape(7.0D, 2.0D, 0.0D, 9.0D, 13.0D, 2.0D), Block.makeCuboidShape(7.0D, 2.0D, 14.0D, 9.0D, 13.0D, 16.0D));
   }
}
