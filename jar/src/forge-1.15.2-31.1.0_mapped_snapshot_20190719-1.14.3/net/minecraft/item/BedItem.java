package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class BedItem extends BlockItem {
   public BedItem(Block p_i48528_1_, Item.Properties p_i48528_2_) {
      super(p_i48528_1_, p_i48528_2_);
   }

   protected boolean placeBlock(BlockItemUseContext p_195941_1_, BlockState p_195941_2_) {
      return p_195941_1_.getWorld().setBlockState(p_195941_1_.getPos(), p_195941_2_, 26);
   }
}
