package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class ConfiguredSurfaceBuilder<SC extends ISurfaceBuilderConfig> {
   public final SurfaceBuilder<SC> builder;
   public final SC config;

   public ConfiguredSurfaceBuilder(SurfaceBuilder<SC> p_i51316_1_, SC p_i51316_2_) {
      this.builder = p_i51316_1_;
      this.config = p_i51316_2_;
   }

   public void buildSurface(Random p_215450_1_, IChunk p_215450_2_, Biome p_215450_3_, int p_215450_4_, int p_215450_5_, int p_215450_6_, double p_215450_7_, BlockState p_215450_9_, BlockState p_215450_10_, int p_215450_11_, long p_215450_12_) {
      this.builder.buildSurface(p_215450_1_, p_215450_2_, p_215450_3_, p_215450_4_, p_215450_5_, p_215450_6_, p_215450_7_, p_215450_9_, p_215450_10_, p_215450_11_, p_215450_12_, this.config);
   }

   public void setSeed(long p_215451_1_) {
      this.builder.setSeed(p_215451_1_);
   }

   public SC getConfig() {
      return this.config;
   }
}
