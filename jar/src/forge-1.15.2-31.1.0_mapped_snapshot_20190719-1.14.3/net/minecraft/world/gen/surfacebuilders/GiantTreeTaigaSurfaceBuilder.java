package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class GiantTreeTaigaSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
   public GiantTreeTaigaSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> p_i51312_1_) {
      super(p_i51312_1_);
   }

   public void buildSurface(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      if (p_205610_7_ > 1.75D) {
         SurfaceBuilder.DEFAULT.buildSurface(p_205610_1_, p_205610_2_, p_205610_3_, p_205610_4_, p_205610_5_, p_205610_6_, p_205610_7_, p_205610_9_, p_205610_10_, p_205610_11_, p_205610_12_, SurfaceBuilder.CORASE_DIRT_DIRT_GRAVEL_CONFIG);
      } else if (p_205610_7_ > -0.95D) {
         SurfaceBuilder.DEFAULT.buildSurface(p_205610_1_, p_205610_2_, p_205610_3_, p_205610_4_, p_205610_5_, p_205610_6_, p_205610_7_, p_205610_9_, p_205610_10_, p_205610_11_, p_205610_12_, SurfaceBuilder.PODZOL_DIRT_GRAVEL_CONFIG);
      } else {
         SurfaceBuilder.DEFAULT.buildSurface(p_205610_1_, p_205610_2_, p_205610_3_, p_205610_4_, p_205610_5_, p_205610_6_, p_205610_7_, p_205610_9_, p_205610_10_, p_205610_11_, p_205610_12_, SurfaceBuilder.GRASS_DIRT_GRAVEL_CONFIG);
      }

   }
}
