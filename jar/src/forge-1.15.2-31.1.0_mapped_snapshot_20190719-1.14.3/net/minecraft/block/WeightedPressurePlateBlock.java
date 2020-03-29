package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class WeightedPressurePlateBlock extends AbstractPressurePlateBlock {
   public static final IntegerProperty POWER;
   private final int maxWeight;

   protected WeightedPressurePlateBlock(int p_i48295_1_, Block.Properties p_i48295_2_) {
      super(p_i48295_2_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(POWER, 0));
      this.maxWeight = p_i48295_1_;
   }

   protected int computeRedstoneStrength(World p_180669_1_, BlockPos p_180669_2_) {
      int lvt_3_1_ = Math.min(p_180669_1_.getEntitiesWithinAABB(Entity.class, PRESSURE_AABB.offset(p_180669_2_)).size(), this.maxWeight);
      if (lvt_3_1_ > 0) {
         float lvt_4_1_ = (float)Math.min(this.maxWeight, lvt_3_1_) / (float)this.maxWeight;
         return MathHelper.ceil(lvt_4_1_ * 15.0F);
      } else {
         return 0;
      }
   }

   protected void playClickOnSound(IWorld p_185507_1_, BlockPos p_185507_2_) {
      p_185507_1_.playSound((PlayerEntity)null, p_185507_2_, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.90000004F);
   }

   protected void playClickOffSound(IWorld p_185508_1_, BlockPos p_185508_2_) {
      p_185508_1_.playSound((PlayerEntity)null, p_185508_2_, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.75F);
   }

   protected int getRedstoneStrength(BlockState p_176576_1_) {
      return (Integer)p_176576_1_.get(POWER);
   }

   protected BlockState setRedstoneStrength(BlockState p_176575_1_, int p_176575_2_) {
      return (BlockState)p_176575_1_.with(POWER, p_176575_2_);
   }

   public int tickRate(IWorldReader p_149738_1_) {
      return 10;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(POWER);
   }

   static {
      POWER = BlockStateProperties.POWER_0_15;
   }
}
