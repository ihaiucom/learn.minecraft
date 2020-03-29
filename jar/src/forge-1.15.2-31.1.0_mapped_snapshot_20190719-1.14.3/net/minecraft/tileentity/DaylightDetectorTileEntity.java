package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DaylightDetectorBlock;

public class DaylightDetectorTileEntity extends TileEntity implements ITickableTileEntity {
   public DaylightDetectorTileEntity() {
      super(TileEntityType.DAYLIGHT_DETECTOR);
   }

   public void tick() {
      if (this.world != null && !this.world.isRemote && this.world.getGameTime() % 20L == 0L) {
         BlockState lvt_1_1_ = this.getBlockState();
         Block lvt_2_1_ = lvt_1_1_.getBlock();
         if (lvt_2_1_ instanceof DaylightDetectorBlock) {
            DaylightDetectorBlock.updatePower(lvt_1_1_, this.world, this.pos);
         }
      }

   }
}
