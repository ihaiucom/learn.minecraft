package net.minecraft.world.gen;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface IWorldGenerationBaseReader {
   boolean hasBlockState(BlockPos var1, Predicate<BlockState> var2);

   BlockPos getHeight(Heightmap.Type var1, BlockPos var2);

   default int getMaxHeight() {
      return this instanceof IWorld ? ((IWorld)this).getWorld().getDimension().getHeight() : 256;
   }
}
