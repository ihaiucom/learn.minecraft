package net.minecraft.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class DefaultDispenseItemBehavior implements IDispenseItemBehavior {
   public final ItemStack dispense(IBlockSource p_dispense_1_, ItemStack p_dispense_2_) {
      ItemStack lvt_3_1_ = this.dispenseStack(p_dispense_1_, p_dispense_2_);
      this.playDispenseSound(p_dispense_1_);
      this.spawnDispenseParticles(p_dispense_1_, (Direction)p_dispense_1_.getBlockState().get(DispenserBlock.FACING));
      return lvt_3_1_;
   }

   protected ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
      Direction lvt_3_1_ = (Direction)p_82487_1_.getBlockState().get(DispenserBlock.FACING);
      IPosition lvt_4_1_ = DispenserBlock.getDispensePosition(p_82487_1_);
      ItemStack lvt_5_1_ = p_82487_2_.split(1);
      doDispense(p_82487_1_.getWorld(), lvt_5_1_, 6, lvt_3_1_, lvt_4_1_);
      return p_82487_2_;
   }

   public static void doDispense(World p_82486_0_, ItemStack p_82486_1_, int p_82486_2_, Direction p_82486_3_, IPosition p_82486_4_) {
      double lvt_5_1_ = p_82486_4_.getX();
      double lvt_7_1_ = p_82486_4_.getY();
      double lvt_9_1_ = p_82486_4_.getZ();
      if (p_82486_3_.getAxis() == Direction.Axis.Y) {
         lvt_7_1_ -= 0.125D;
      } else {
         lvt_7_1_ -= 0.15625D;
      }

      ItemEntity lvt_11_1_ = new ItemEntity(p_82486_0_, lvt_5_1_, lvt_7_1_, lvt_9_1_, p_82486_1_);
      double lvt_12_1_ = p_82486_0_.rand.nextDouble() * 0.1D + 0.2D;
      lvt_11_1_.setMotion(p_82486_0_.rand.nextGaussian() * 0.007499999832361937D * (double)p_82486_2_ + (double)p_82486_3_.getXOffset() * lvt_12_1_, p_82486_0_.rand.nextGaussian() * 0.007499999832361937D * (double)p_82486_2_ + 0.20000000298023224D, p_82486_0_.rand.nextGaussian() * 0.007499999832361937D * (double)p_82486_2_ + (double)p_82486_3_.getZOffset() * lvt_12_1_);
      p_82486_0_.addEntity(lvt_11_1_);
   }

   protected void playDispenseSound(IBlockSource p_82485_1_) {
      p_82485_1_.getWorld().playEvent(1000, p_82485_1_.getBlockPos(), 0);
   }

   protected void spawnDispenseParticles(IBlockSource p_82489_1_, Direction p_82489_2_) {
      p_82489_1_.getWorld().playEvent(2000, p_82489_1_.getBlockPos(), p_82489_2_.getIndex());
   }
}
