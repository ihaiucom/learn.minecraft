package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class FourWayBlock extends Block implements IWaterLoggable {
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   public static final BooleanProperty WATERLOGGED;
   protected static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP;
   protected final VoxelShape[] collisionShapes;
   protected final VoxelShape[] shapes;
   private final Object2IntMap<BlockState> field_223008_i = new Object2IntOpenHashMap();

   protected FourWayBlock(float p_i48420_1_, float p_i48420_2_, float p_i48420_3_, float p_i48420_4_, float p_i48420_5_, Block.Properties p_i48420_6_) {
      super(p_i48420_6_);
      this.collisionShapes = this.makeShapes(p_i48420_1_, p_i48420_2_, p_i48420_5_, 0.0F, p_i48420_5_);
      this.shapes = this.makeShapes(p_i48420_1_, p_i48420_2_, p_i48420_3_, 0.0F, p_i48420_4_);
   }

   protected VoxelShape[] makeShapes(float p_196408_1_, float p_196408_2_, float p_196408_3_, float p_196408_4_, float p_196408_5_) {
      float lvt_6_1_ = 8.0F - p_196408_1_;
      float lvt_7_1_ = 8.0F + p_196408_1_;
      float lvt_8_1_ = 8.0F - p_196408_2_;
      float lvt_9_1_ = 8.0F + p_196408_2_;
      VoxelShape lvt_10_1_ = Block.makeCuboidShape((double)lvt_6_1_, 0.0D, (double)lvt_6_1_, (double)lvt_7_1_, (double)p_196408_3_, (double)lvt_7_1_);
      VoxelShape lvt_11_1_ = Block.makeCuboidShape((double)lvt_8_1_, (double)p_196408_4_, 0.0D, (double)lvt_9_1_, (double)p_196408_5_, (double)lvt_9_1_);
      VoxelShape lvt_12_1_ = Block.makeCuboidShape((double)lvt_8_1_, (double)p_196408_4_, (double)lvt_8_1_, (double)lvt_9_1_, (double)p_196408_5_, 16.0D);
      VoxelShape lvt_13_1_ = Block.makeCuboidShape(0.0D, (double)p_196408_4_, (double)lvt_8_1_, (double)lvt_9_1_, (double)p_196408_5_, (double)lvt_9_1_);
      VoxelShape lvt_14_1_ = Block.makeCuboidShape((double)lvt_8_1_, (double)p_196408_4_, (double)lvt_8_1_, 16.0D, (double)p_196408_5_, (double)lvt_9_1_);
      VoxelShape lvt_15_1_ = VoxelShapes.or(lvt_11_1_, lvt_14_1_);
      VoxelShape lvt_16_1_ = VoxelShapes.or(lvt_12_1_, lvt_13_1_);
      VoxelShape[] lvt_17_1_ = new VoxelShape[]{VoxelShapes.empty(), lvt_12_1_, lvt_13_1_, lvt_16_1_, lvt_11_1_, VoxelShapes.or(lvt_12_1_, lvt_11_1_), VoxelShapes.or(lvt_13_1_, lvt_11_1_), VoxelShapes.or(lvt_16_1_, lvt_11_1_), lvt_14_1_, VoxelShapes.or(lvt_12_1_, lvt_14_1_), VoxelShapes.or(lvt_13_1_, lvt_14_1_), VoxelShapes.or(lvt_16_1_, lvt_14_1_), lvt_15_1_, VoxelShapes.or(lvt_12_1_, lvt_15_1_), VoxelShapes.or(lvt_13_1_, lvt_15_1_), VoxelShapes.or(lvt_16_1_, lvt_15_1_)};

      for(int lvt_18_1_ = 0; lvt_18_1_ < 16; ++lvt_18_1_) {
         lvt_17_1_[lvt_18_1_] = VoxelShapes.or(lvt_10_1_, lvt_17_1_[lvt_18_1_]);
      }

      return lvt_17_1_;
   }

   public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return !(Boolean)p_200123_1_.get(WATERLOGGED);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return this.shapes[this.getIndex(p_220053_1_)];
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return this.collisionShapes[this.getIndex(p_220071_1_)];
   }

   private static int getMask(Direction p_196407_0_) {
      return 1 << p_196407_0_.getHorizontalIndex();
   }

   protected int getIndex(BlockState p_196406_1_) {
      return this.field_223008_i.computeIntIfAbsent(p_196406_1_, (p_223007_0_) -> {
         int lvt_1_1_ = 0;
         if ((Boolean)p_223007_0_.get(NORTH)) {
            lvt_1_1_ |= getMask(Direction.NORTH);
         }

         if ((Boolean)p_223007_0_.get(EAST)) {
            lvt_1_1_ |= getMask(Direction.EAST);
         }

         if ((Boolean)p_223007_0_.get(SOUTH)) {
            lvt_1_1_ |= getMask(Direction.SOUTH);
         }

         if ((Boolean)p_223007_0_.get(WEST)) {
            lvt_1_1_ |= getMask(Direction.WEST);
         }

         return lvt_1_1_;
      });
   }

   public IFluidState getFluidState(BlockState p_204507_1_) {
      return (Boolean)p_204507_1_.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_204507_1_);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
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

   static {
      NORTH = SixWayBlock.NORTH;
      EAST = SixWayBlock.EAST;
      SOUTH = SixWayBlock.SOUTH;
      WEST = SixWayBlock.WEST;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      FACING_TO_PROPERTY_MAP = (Map)SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().stream().filter((p_199775_0_) -> {
         return ((Direction)p_199775_0_.getKey()).getAxis().isHorizontal();
      }).collect(Util.toMapCollector());
   }
}
