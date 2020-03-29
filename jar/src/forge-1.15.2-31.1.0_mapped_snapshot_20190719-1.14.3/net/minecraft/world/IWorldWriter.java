package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface IWorldWriter {
   boolean setBlockState(BlockPos var1, BlockState var2, int var3);

   boolean removeBlock(BlockPos var1, boolean var2);

   default boolean destroyBlock(BlockPos p_175655_1_, boolean p_175655_2_) {
      return this.func_225521_a_(p_175655_1_, p_175655_2_, (Entity)null);
   }

   boolean func_225521_a_(BlockPos var1, boolean var2, @Nullable Entity var3);

   default boolean addEntity(Entity p_217376_1_) {
      return false;
   }
}
