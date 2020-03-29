package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PressurePlateBlock extends AbstractPressurePlateBlock {
   public static final BooleanProperty POWERED;
   private final PressurePlateBlock.Sensitivity sensitivity;

   protected PressurePlateBlock(PressurePlateBlock.Sensitivity p_i48348_1_, Block.Properties p_i48348_2_) {
      super(p_i48348_2_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(POWERED, false));
      this.sensitivity = p_i48348_1_;
   }

   protected int getRedstoneStrength(BlockState p_176576_1_) {
      return (Boolean)p_176576_1_.get(POWERED) ? 15 : 0;
   }

   protected BlockState setRedstoneStrength(BlockState p_176575_1_, int p_176575_2_) {
      return (BlockState)p_176575_1_.with(POWERED, p_176575_2_ > 0);
   }

   protected void playClickOnSound(IWorld p_185507_1_, BlockPos p_185507_2_) {
      if (this.material == Material.WOOD) {
         p_185507_1_.playSound((PlayerEntity)null, p_185507_2_, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
      } else {
         p_185507_1_.playSound((PlayerEntity)null, p_185507_2_, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
      }

   }

   protected void playClickOffSound(IWorld p_185508_1_, BlockPos p_185508_2_) {
      if (this.material == Material.WOOD) {
         p_185508_1_.playSound((PlayerEntity)null, p_185508_2_, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.7F);
      } else {
         p_185508_1_.playSound((PlayerEntity)null, p_185508_2_, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
      }

   }

   protected int computeRedstoneStrength(World p_180669_1_, BlockPos p_180669_2_) {
      AxisAlignedBB lvt_3_1_ = PRESSURE_AABB.offset(p_180669_2_);
      List lvt_4_3_;
      switch(this.sensitivity) {
      case EVERYTHING:
         lvt_4_3_ = p_180669_1_.getEntitiesWithinAABBExcludingEntity((Entity)null, lvt_3_1_);
         break;
      case MOBS:
         lvt_4_3_ = p_180669_1_.getEntitiesWithinAABB(LivingEntity.class, lvt_3_1_);
         break;
      default:
         return 0;
      }

      if (!lvt_4_3_.isEmpty()) {
         Iterator var5 = lvt_4_3_.iterator();

         while(var5.hasNext()) {
            Entity lvt_6_1_ = (Entity)var5.next();
            if (!lvt_6_1_.doesEntityNotTriggerPressurePlate()) {
               return 15;
            }
         }
      }

      return 0;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(POWERED);
   }

   static {
      POWERED = BlockStateProperties.POWERED;
   }

   public static enum Sensitivity {
      EVERYTHING,
      MOBS;
   }
}
