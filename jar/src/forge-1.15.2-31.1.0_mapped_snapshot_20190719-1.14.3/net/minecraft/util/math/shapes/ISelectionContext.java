package net.minecraft.util.math.shapes;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeSelectionContext;

public interface ISelectionContext extends IForgeSelectionContext {
   static ISelectionContext dummy() {
      return EntitySelectionContext.DUMMY;
   }

   static ISelectionContext forEntity(Entity p_216374_0_) {
      return new EntitySelectionContext(p_216374_0_);
   }

   boolean func_225581_b_();

   boolean func_216378_a(VoxelShape var1, BlockPos var2, boolean var3);

   boolean hasItem(Item var1);
}
