package net.minecraft.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DispenseBoatBehavior extends DefaultDispenseItemBehavior {
   private final DefaultDispenseItemBehavior field_218402_b = new DefaultDispenseItemBehavior();
   private final BoatEntity.Type field_218403_c;

   public DispenseBoatBehavior(BoatEntity.Type p_i50793_1_) {
      this.field_218403_c = p_i50793_1_;
   }

   public ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
      Direction lvt_3_1_ = (Direction)p_82487_1_.getBlockState().get(DispenserBlock.FACING);
      World lvt_4_1_ = p_82487_1_.getWorld();
      double lvt_5_1_ = p_82487_1_.getX() + (double)((float)lvt_3_1_.getXOffset() * 1.125F);
      double lvt_7_1_ = p_82487_1_.getY() + (double)((float)lvt_3_1_.getYOffset() * 1.125F);
      double lvt_9_1_ = p_82487_1_.getZ() + (double)((float)lvt_3_1_.getZOffset() * 1.125F);
      BlockPos lvt_11_1_ = p_82487_1_.getBlockPos().offset(lvt_3_1_);
      double lvt_12_3_;
      if (lvt_4_1_.getFluidState(lvt_11_1_).isTagged(FluidTags.WATER)) {
         lvt_12_3_ = 1.0D;
      } else {
         if (!lvt_4_1_.getBlockState(lvt_11_1_).isAir() || !lvt_4_1_.getFluidState(lvt_11_1_.down()).isTagged(FluidTags.WATER)) {
            return this.field_218402_b.dispense(p_82487_1_, p_82487_2_);
         }

         lvt_12_3_ = 0.0D;
      }

      BoatEntity lvt_14_1_ = new BoatEntity(lvt_4_1_, lvt_5_1_, lvt_7_1_ + lvt_12_3_, lvt_9_1_);
      lvt_14_1_.setBoatType(this.field_218403_c);
      lvt_14_1_.rotationYaw = lvt_3_1_.getHorizontalAngle();
      lvt_4_1_.addEntity(lvt_14_1_);
      p_82487_2_.shrink(1);
      return p_82487_2_;
   }

   protected void playDispenseSound(IBlockSource p_82485_1_) {
      p_82485_1_.getWorld().playEvent(1000, p_82485_1_.getBlockPos(), 0);
   }
}
