package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class HopperBlock extends ContainerBlock {
   public static final DirectionProperty FACING;
   public static final BooleanProperty ENABLED;
   private static final VoxelShape INPUT_SHAPE;
   private static final VoxelShape MIDDLE_SHAPE;
   private static final VoxelShape INPUT_MIDDLE_SHAPE;
   private static final VoxelShape field_196326_A;
   private static final VoxelShape DOWN_SHAPE;
   private static final VoxelShape EAST_SHAPE;
   private static final VoxelShape NORTH_SHAPE;
   private static final VoxelShape SOUTH_SHAPE;
   private static final VoxelShape WEST_SHAPE;
   private static final VoxelShape DOWN_RAYTRACE_SHAPE;
   private static final VoxelShape EAST_RAYTRACE_SHAPE;
   private static final VoxelShape NORTH_RAYTRACE_SHAPE;
   private static final VoxelShape SOUTH_RAYTRACE_SHAPE;
   private static final VoxelShape WEST_RAYTRACE_SHAPE;

   public HopperBlock(Block.Properties p_i48378_1_) {
      super(p_i48378_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.DOWN)).with(ENABLED, true));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      switch((Direction)p_220053_1_.get(FACING)) {
      case DOWN:
         return DOWN_SHAPE;
      case NORTH:
         return NORTH_SHAPE;
      case SOUTH:
         return SOUTH_SHAPE;
      case WEST:
         return WEST_SHAPE;
      case EAST:
         return EAST_SHAPE;
      default:
         return field_196326_A;
      }
   }

   public VoxelShape getRaytraceShape(BlockState p_199600_1_, IBlockReader p_199600_2_, BlockPos p_199600_3_) {
      switch((Direction)p_199600_1_.get(FACING)) {
      case DOWN:
         return DOWN_RAYTRACE_SHAPE;
      case NORTH:
         return NORTH_RAYTRACE_SHAPE;
      case SOUTH:
         return SOUTH_RAYTRACE_SHAPE;
      case WEST:
         return WEST_RAYTRACE_SHAPE;
      case EAST:
         return EAST_RAYTRACE_SHAPE;
      default:
         return IHopper.INSIDE_BOWL_SHAPE;
      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      Direction lvt_2_1_ = p_196258_1_.getFace().getOpposite();
      return (BlockState)((BlockState)this.getDefaultState().with(FACING, lvt_2_1_.getAxis() == Direction.Axis.Y ? Direction.DOWN : lvt_2_1_)).with(ENABLED, true);
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new HopperTileEntity();
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (p_180633_5_.hasDisplayName()) {
         TileEntity lvt_6_1_ = p_180633_1_.getTileEntity(p_180633_2_);
         if (lvt_6_1_ instanceof HopperTileEntity) {
            ((HopperTileEntity)lvt_6_1_).setCustomName(p_180633_5_.getDisplayName());
         }
      }

   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (p_220082_4_.getBlock() != p_220082_1_.getBlock()) {
         this.updateState(p_220082_2_, p_220082_3_, p_220082_1_);
      }
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         TileEntity lvt_7_1_ = p_225533_2_.getTileEntity(p_225533_3_);
         if (lvt_7_1_ instanceof HopperTileEntity) {
            p_225533_4_.openContainer((HopperTileEntity)lvt_7_1_);
            p_225533_4_.addStat(Stats.INSPECT_HOPPER);
         }

         return ActionResultType.SUCCESS;
      }
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      this.updateState(p_220069_2_, p_220069_3_, p_220069_1_);
   }

   private void updateState(World p_176427_1_, BlockPos p_176427_2_, BlockState p_176427_3_) {
      boolean lvt_4_1_ = !p_176427_1_.isBlockPowered(p_176427_2_);
      if (lvt_4_1_ != (Boolean)p_176427_3_.get(ENABLED)) {
         p_176427_1_.setBlockState(p_176427_2_, (BlockState)p_176427_3_.with(ENABLED, lvt_4_1_), 4);
      }

   }

   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         TileEntity lvt_6_1_ = p_196243_2_.getTileEntity(p_196243_3_);
         if (lvt_6_1_ instanceof HopperTileEntity) {
            InventoryHelper.dropInventoryItems(p_196243_2_, (BlockPos)p_196243_3_, (HopperTileEntity)lvt_6_1_);
            p_196243_2_.updateComparatorOutputLevel(p_196243_3_, this);
         }

         super.onReplaced(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
      }
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public boolean hasComparatorInputOverride(BlockState p_149740_1_) {
      return true;
   }

   public int getComparatorInputOverride(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
      return Container.calcRedstone(p_180641_2_.getTileEntity(p_180641_3_));
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, ENABLED);
   }

   public void onEntityCollision(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity p_196262_4_) {
      TileEntity lvt_5_1_ = p_196262_2_.getTileEntity(p_196262_3_);
      if (lvt_5_1_ instanceof HopperTileEntity) {
         ((HopperTileEntity)lvt_5_1_).onEntityCollision(p_196262_4_);
      }

   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   static {
      FACING = BlockStateProperties.FACING_EXCEPT_UP;
      ENABLED = BlockStateProperties.ENABLED;
      INPUT_SHAPE = Block.makeCuboidShape(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      MIDDLE_SHAPE = Block.makeCuboidShape(4.0D, 4.0D, 4.0D, 12.0D, 10.0D, 12.0D);
      INPUT_MIDDLE_SHAPE = VoxelShapes.or(MIDDLE_SHAPE, INPUT_SHAPE);
      field_196326_A = VoxelShapes.combineAndSimplify(INPUT_MIDDLE_SHAPE, IHopper.INSIDE_BOWL_SHAPE, IBooleanFunction.ONLY_FIRST);
      DOWN_SHAPE = VoxelShapes.or(field_196326_A, Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D));
      EAST_SHAPE = VoxelShapes.or(field_196326_A, Block.makeCuboidShape(12.0D, 4.0D, 6.0D, 16.0D, 8.0D, 10.0D));
      NORTH_SHAPE = VoxelShapes.or(field_196326_A, Block.makeCuboidShape(6.0D, 4.0D, 0.0D, 10.0D, 8.0D, 4.0D));
      SOUTH_SHAPE = VoxelShapes.or(field_196326_A, Block.makeCuboidShape(6.0D, 4.0D, 12.0D, 10.0D, 8.0D, 16.0D));
      WEST_SHAPE = VoxelShapes.or(field_196326_A, Block.makeCuboidShape(0.0D, 4.0D, 6.0D, 4.0D, 8.0D, 10.0D));
      DOWN_RAYTRACE_SHAPE = IHopper.INSIDE_BOWL_SHAPE;
      EAST_RAYTRACE_SHAPE = VoxelShapes.or(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(12.0D, 8.0D, 6.0D, 16.0D, 10.0D, 10.0D));
      NORTH_RAYTRACE_SHAPE = VoxelShapes.or(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(6.0D, 8.0D, 0.0D, 10.0D, 10.0D, 4.0D));
      SOUTH_RAYTRACE_SHAPE = VoxelShapes.or(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(6.0D, 8.0D, 12.0D, 10.0D, 10.0D, 16.0D));
      WEST_RAYTRACE_SHAPE = VoxelShapes.or(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(0.0D, 8.0D, 6.0D, 4.0D, 10.0D, 10.0D));
   }
}
