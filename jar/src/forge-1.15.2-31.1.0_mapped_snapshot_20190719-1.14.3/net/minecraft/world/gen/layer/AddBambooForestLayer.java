package net.minecraft.world.gen.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

public enum AddBambooForestLayer implements IC1Transformer {
   INSTANCE;

   private static final int JUNGLE = Registry.BIOME.getId(Biomes.JUNGLE);
   private static final int BAMBOO_JUNGLE = Registry.BIOME.getId(Biomes.BAMBOO_JUNGLE);

   public int apply(INoiseRandom p_202716_1_, int p_202716_2_) {
      return p_202716_1_.random(10) == 0 && p_202716_2_ == JUNGLE ? BAMBOO_JUNGLE : p_202716_2_;
   }
}
