package net.minecraft.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public abstract class ProjectileDispenseBehavior extends DefaultDispenseItemBehavior {
   public ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
      World lvt_3_1_ = p_82487_1_.getWorld();
      IPosition lvt_4_1_ = DispenserBlock.getDispensePosition(p_82487_1_);
      Direction lvt_5_1_ = (Direction)p_82487_1_.getBlockState().get(DispenserBlock.FACING);
      IProjectile lvt_6_1_ = this.getProjectileEntity(lvt_3_1_, lvt_4_1_, p_82487_2_);
      lvt_6_1_.shoot((double)lvt_5_1_.getXOffset(), (double)((float)lvt_5_1_.getYOffset() + 0.1F), (double)lvt_5_1_.getZOffset(), this.getProjectileVelocity(), this.getProjectileInaccuracy());
      lvt_3_1_.addEntity((Entity)lvt_6_1_);
      p_82487_2_.shrink(1);
      return p_82487_2_;
   }

   protected void playDispenseSound(IBlockSource p_82485_1_) {
      p_82485_1_.getWorld().playEvent(1002, p_82485_1_.getBlockPos(), 0);
   }

   protected abstract IProjectile getProjectileEntity(World var1, IPosition var2, ItemStack var3);

   protected float getProjectileInaccuracy() {
      return 6.0F;
   }

   protected float getProjectileVelocity() {
      return 1.1F;
   }
}
