package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.DaylightDetectorTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class DaylightDetectorBlock extends ContainerBlock {
   public static final IntegerProperty POWER;
   public static final BooleanProperty INVERTED;
   protected static final VoxelShape SHAPE;

   public DaylightDetectorBlock(Block.Properties p_i48419_1_) {
      super(p_i48419_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(POWER, 0)).with(INVERTED, false));
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public boolean func_220074_n(BlockState p_220074_1_) {
      return true;
   }

   public int getWeakPower(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return (Integer)p_180656_1_.get(POWER);
   }

   public static void updatePower(BlockState p_196319_0_, World p_196319_1_, BlockPos p_196319_2_) {
      if (p_196319_1_.dimension.hasSkyLight()) {
         int lvt_3_1_ = p_196319_1_.func_226658_a_(LightType.SKY, p_196319_2_) - p_196319_1_.getSkylightSubtracted();
         float lvt_4_1_ = p_196319_1_.getCelestialAngleRadians(1.0F);
         boolean lvt_5_1_ = (Boolean)p_196319_0_.get(INVERTED);
         if (lvt_5_1_) {
            lvt_3_1_ = 15 - lvt_3_1_;
         } else if (lvt_3_1_ > 0) {
            float lvt_6_1_ = lvt_4_1_ < 3.1415927F ? 0.0F : 6.2831855F;
            lvt_4_1_ += (lvt_6_1_ - lvt_4_1_) * 0.2F;
            lvt_3_1_ = Math.round((float)lvt_3_1_ * MathHelper.cos(lvt_4_1_));
         }

         lvt_3_1_ = MathHelper.clamp(lvt_3_1_, 0, 15);
         if ((Integer)p_196319_0_.get(POWER) != lvt_3_1_) {
            p_196319_1_.setBlockState(p_196319_2_, (BlockState)p_196319_0_.with(POWER, lvt_3_1_), 3);
         }

      }
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_4_.isAllowEdit()) {
         if (p_225533_2_.isRemote) {
            return ActionResultType.SUCCESS;
         } else {
            BlockState lvt_7_1_ = (BlockState)p_225533_1_.cycle(INVERTED);
            p_225533_2_.setBlockState(p_225533_3_, lvt_7_1_, 4);
            updatePower(lvt_7_1_, p_225533_2_, p_225533_3_);
            return ActionResultType.SUCCESS;
         }
      } else {
         return super.func_225533_a_(p_225533_1_, p_225533_2_, p_225533_3_, p_225533_4_, p_225533_5_, p_225533_6_);
      }
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public boolean canProvidePower(BlockState p_149744_1_) {
      return true;
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new DaylightDetectorTileEntity();
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(POWER, INVERTED);
   }

   static {
      POWER = BlockStateProperties.POWER_0_15;
      INVERTED = BlockStateProperties.INVERTED;
      SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);
   }
}
