package net.minecraft.item;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.IWorldReader;

public class WallOrFloorItem extends BlockItem {
   protected final Block wallBlock;

   public WallOrFloorItem(Block p_i48462_1_, Block p_i48462_2_, Item.Properties p_i48462_3_) {
      super(p_i48462_1_, p_i48462_3_);
      this.wallBlock = p_i48462_2_;
   }

   @Nullable
   protected BlockState getStateForPlacement(BlockItemUseContext p_195945_1_) {
      BlockState blockstate = this.wallBlock.getStateForPlacement(p_195945_1_);
      BlockState blockstate1 = null;
      IWorldReader iworldreader = p_195945_1_.getWorld();
      BlockPos blockpos = p_195945_1_.getPos();
      Direction[] var6 = p_195945_1_.getNearestLookingDirections();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Direction direction = var6[var8];
         if (direction != Direction.UP) {
            BlockState blockstate2 = direction == Direction.DOWN ? this.getBlock().getStateForPlacement(p_195945_1_) : blockstate;
            if (blockstate2 != null && blockstate2.isValidPosition(iworldreader, blockpos)) {
               blockstate1 = blockstate2;
               break;
            }
         }
      }

      return blockstate1 != null && iworldreader.func_226663_a_(blockstate1, blockpos, ISelectionContext.dummy()) ? blockstate1 : null;
   }

   public void addToBlockToItemMap(Map<Block, Item> p_195946_1_, Item p_195946_2_) {
      super.addToBlockToItemMap(p_195946_1_, p_195946_2_);
      p_195946_1_.put(this.wallBlock, p_195946_2_);
   }

   public void removeFromBlockToItemMap(Map<Block, Item> p_removeFromBlockToItemMap_1_, Item p_removeFromBlockToItemMap_2_) {
      super.removeFromBlockToItemMap(p_removeFromBlockToItemMap_1_, p_removeFromBlockToItemMap_2_);
      p_removeFromBlockToItemMap_1_.remove(this.wallBlock);
   }
}
