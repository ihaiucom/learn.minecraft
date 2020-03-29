package net.minecraft.dispenser;

public abstract class OptionalDispenseBehavior extends DefaultDispenseItemBehavior {
   protected boolean successful = true;

   protected void playDispenseSound(IBlockSource p_82485_1_) {
      p_82485_1_.getWorld().playEvent(this.successful ? 1000 : 1001, p_82485_1_.getBlockPos(), 0);
   }
}
