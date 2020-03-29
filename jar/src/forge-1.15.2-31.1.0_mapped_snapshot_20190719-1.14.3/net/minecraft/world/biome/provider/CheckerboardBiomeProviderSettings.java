package net.minecraft.world.biome.provider;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.storage.WorldInfo;

public class CheckerboardBiomeProviderSettings implements IBiomeProviderSettings {
   private Biome[] biomes;
   private int size;

   public CheckerboardBiomeProviderSettings(WorldInfo p_i225747_1_) {
      this.biomes = new Biome[]{Biomes.PLAINS};
      this.size = 1;
   }

   public CheckerboardBiomeProviderSettings setBiomes(Biome[] p_206860_1_) {
      this.biomes = p_206860_1_;
      return this;
   }

   public CheckerboardBiomeProviderSettings setSize(int p_206861_1_) {
      this.size = p_206861_1_;
      return this;
   }

   public Biome[] getBiomes() {
      return this.biomes;
   }

   public int getSize() {
      return this.size;
   }
}
