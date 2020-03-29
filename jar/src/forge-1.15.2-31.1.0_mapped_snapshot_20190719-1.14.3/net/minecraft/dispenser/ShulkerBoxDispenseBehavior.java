package net.minecraft.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class ShulkerBoxDispenseBehavior extends OptionalDispenseBehavior {
   protected ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
      this.successful = false;
      Item lvt_3_1_ = p_82487_2_.getItem();
      if (lvt_3_1_ instanceof BlockItem) {
         Direction lvt_4_1_ = (Direction)p_82487_1_.getBlockState().get(DispenserBlock.FACING);
         BlockPos lvt_5_1_ = p_82487_1_.getBlockPos().offset(lvt_4_1_);
         Direction lvt_6_1_ = p_82487_1_.getWorld().isAirBlock(lvt_5_1_.down()) ? lvt_4_1_ : Direction.UP;
         this.successful = ((BlockItem)lvt_3_1_).tryPlace(new DirectionalPlaceContext(p_82487_1_.getWorld(), lvt_5_1_, lvt_4_1_, p_82487_2_, lvt_6_1_)) == ActionResultType.SUCCESS;
      }

      return p_82487_2_;
   }
}
