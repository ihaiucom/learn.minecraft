package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;

public interface IChunkLightProvider {
   @Nullable
   IBlockReader getChunkForLight(int var1, int var2);

   default void markLightChanged(LightType p_217201_1_, SectionPos p_217201_2_) {
   }

   IBlockReader getWorld();
}
