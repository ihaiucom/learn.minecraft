package net.minecraft.world.gen;

import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProvider;

public interface IChunkGeneratorFactory<C extends GenerationSettings, T extends ChunkGenerator<C>> {
   T create(World var1, BiomeProvider var2, C var3);
}
