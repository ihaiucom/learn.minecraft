package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class SixWayBlock extends Block {
   private static final Direction[] FACING_VALUES = Direction.values();
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   public static final BooleanProperty UP;
   public static final BooleanProperty DOWN;
   public static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP;
   protected final VoxelShape[] shapes;

   protected SixWayBlock(float p_i48355_1_, Block.Properties p_i48355_2_) {
      super(p_i48355_2_);
      this.shapes = this.makeShapes(p_i48355_1_);
   }

   private VoxelShape[] makeShapes(float p_196487_1_) {
      float lvt_2_1_ = 0.5F - p_196487_1_;
      float lvt_3_1_ = 0.5F + p_196487_1_;
      VoxelShape lvt_4_1_ = Block.makeCuboidShape((double)(lvt_2_1_ * 16.0F), (double)(lvt_2_1_ * 16.0F), (double)(lvt_2_1_ * 16.0F), (double)(lvt_3_1_ * 16.0F), (double)(lvt_3_1_ * 16.0F), (double)(lvt_3_1_ * 16.0F));
      VoxelShape[] lvt_5_1_ = new VoxelShape[FACING_VALUES.length];

      for(int lvt_6_1_ = 0; lvt_6_1_ < FACING_VALUES.length; ++lvt_6_1_) {
         Direction lvt_7_1_ = FACING_VALUES[lvt_6_1_];
         lvt_5_1_[lvt_6_1_] = VoxelShapes.create(0.5D + Math.min((double)(-p_196487_1_), (double)lvt_7_1_.getXOffset() * 0.5D), 0.5D + Math.min((double)(-p_196487_1_), (double)lvt_7_1_.getYOffset() * 0.5D), 0.5D + Math.min((double)(-p_196487_1_), (double)lvt_7_1_.getZOffset() * 0.5D), 0.5D + Math.max((double)p_196487_1_, (double)lvt_7_1_.getXOffset() * 0.5D), 0.5D + Math.max((double)p_196487_1_, (double)lvt_7_1_.getYOffset() * 0.5D), 0.5D + Math.max((double)p_196487_1_, (double)lvt_7_1_.getZOffset() * 0.5D));
      }

      VoxelShape[] lvt_6_2_ = new VoxelShape[64];

      for(int lvt_7_2_ = 0; lvt_7_2_ < 64; ++lvt_7_2_) {
         VoxelShape lvt_8_1_ = lvt_4_1_;

         for(int lvt_9_1_ = 0; lvt_9_1_ < FACING_VALUES.length; ++lvt_9_1_) {
            if ((lvt_7_2_ & 1 << lvt_9_1_) != 0) {
               lvt_8_1_ = VoxelShapes.or(lvt_8_1_, lvt_5_1_[lvt_9_1_]);
            }
         }

         lvt_6_2_[lvt_7_2_] = lvt_8_1_;
      }

      return lvt_6_2_;
   }

   public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
      return false;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return this.shapes[this.getShapeIndex(p_220053_1_)];
   }

   protected int getShapeIndex(BlockState p_196486_1_) {
      int lvt_2_1_ = 0;

      for(int lvt_3_1_ = 0; lvt_3_1_ < FACING_VALUES.length; ++lvt_3_1_) {
         if ((Boolean)p_196486_1_.get((IProperty)FACING_TO_PROPERTY_MAP.get(FACING_VALUES[lvt_3_1_]))) {
            lvt_2_1_ |= 1 << lvt_3_1_;
         }
      }

      return lvt_2_1_;
   }

   static {
      NORTH = BlockStateProperties.NORTH;
      EAST = BlockStateProperties.EAST;
      SOUTH = BlockStateProperties.SOUTH;
      WEST = BlockStateProperties.WEST;
      UP = BlockStateProperties.UP;
      DOWN = BlockStateProperties.DOWN;
      FACING_TO_PROPERTY_MAP = (Map)Util.make(Maps.newEnumMap(Direction.class), (p_203421_0_) -> {
         p_203421_0_.put(Direction.NORTH, NORTH);
         p_203421_0_.put(Direction.EAST, EAST);
         p_203421_0_.put(Direction.SOUTH, SOUTH);
         p_203421_0_.put(Direction.WEST, WEST);
         p_203421_0_.put(Direction.UP, UP);
         p_203421_0_.put(Direction.DOWN, DOWN);
      });
   }
}
