package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class TallBlockItem extends BlockItem {
   public TallBlockItem(Block p_i48511_1_, Item.Properties p_i48511_2_) {
      super(p_i48511_1_, p_i48511_2_);
   }

   protected boolean placeBlock(BlockItemUseContext p_195941_1_, BlockState p_195941_2_) {
      p_195941_1_.getWorld().setBlockState(p_195941_1_.getPos().up(), Blocks.AIR.getDefaultState(), 27);
      return super.placeBlock(p_195941_1_, p_195941_2_);
   }
}
