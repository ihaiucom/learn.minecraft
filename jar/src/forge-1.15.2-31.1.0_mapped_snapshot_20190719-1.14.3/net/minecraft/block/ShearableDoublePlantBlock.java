package net.minecraft.block;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraftforge.common.IShearable;

public class ShearableDoublePlantBlock extends DoublePlantBlock implements IShearable {
   public static final EnumProperty<DoubleBlockHalf> field_208063_b;

   public ShearableDoublePlantBlock(Block.Properties p_i49975_1_) {
      super(p_i49975_1_);
   }

   public boolean isReplaceable(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      boolean flag = super.isReplaceable(p_196253_1_, p_196253_2_);
      return flag && p_196253_2_.getItem().getItem() == this.asItem() ? false : flag;
   }

   static {
      field_208063_b = DoublePlantBlock.HALF;
   }
}
