package net.minecraft.block;

import net.minecraft.item.DyeColor;

public class StainedGlassPaneBlock extends PaneBlock implements IBeaconBeamColorProvider {
   private final DyeColor color;

   public StainedGlassPaneBlock(DyeColor p_i48322_1_, Block.Properties p_i48322_2_) {
      super(p_i48322_2_);
      this.color = p_i48322_1_;
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(WATERLOGGED, false));
   }

   public DyeColor getColor() {
      return this.color;
   }
}
