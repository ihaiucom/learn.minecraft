package net.minecraft.world.biome.provider;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.storage.WorldInfo;

public class SingleBiomeProviderSettings implements IBiomeProviderSettings {
   private Biome biome;

   public SingleBiomeProviderSettings(WorldInfo p_i225748_1_) {
      this.biome = Biomes.PLAINS;
   }

   public SingleBiomeProviderSettings setBiome(Biome p_205436_1_) {
      this.biome = p_205436_1_;
      return this;
   }

   public Biome getBiome() {
      return this.biome;
   }
}
