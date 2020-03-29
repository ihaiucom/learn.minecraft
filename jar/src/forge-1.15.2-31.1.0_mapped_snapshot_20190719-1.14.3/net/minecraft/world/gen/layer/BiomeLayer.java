package net.minecraft.world.gen.layer;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;
import net.minecraftforge.common.BiomeManager;

public class BiomeLayer implements IC0Transformer {
   private static final int BIRCH_FOREST;
   private static final int DESERT;
   private static final int MOUNTAINS;
   private static final int FOREST;
   private static final int SNOWY_TUNDRA;
   private static final int JUNGLE;
   private static final int BADLANDS_PLATEAU;
   private static final int WOODED_BADLANDS_PLATEAU;
   private static final int MUSHROOM_FIELDS;
   private static final int PLAINS;
   private static final int GIANT_TREE_TAIGA;
   private static final int DARK_FOREST;
   private static final int SAVANNA;
   private static final int SWAMP;
   private static final int TAIGA;
   private static final int SNOWY_TAIGA;
   private final int field_227472_v_;
   private List<BiomeManager.BiomeEntry>[] biomes = new ArrayList[BiomeManager.BiomeType.values().length];

   public BiomeLayer(WorldType p_i225882_1_, int p_i225882_2_) {
      BiomeManager.BiomeType[] var3 = BiomeManager.BiomeType.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         BiomeManager.BiomeType type = var3[var5];
         ImmutableList<BiomeManager.BiomeEntry> biomesToAdd = BiomeManager.getBiomes(type);
         int idx = type.ordinal();
         if (this.biomes[idx] == null) {
            this.biomes[idx] = new ArrayList();
         }

         if (biomesToAdd != null) {
            this.biomes[idx].addAll(biomesToAdd);
         }
      }

      int desertIdx = BiomeManager.BiomeType.DESERT.ordinal();
      this.biomes[desertIdx].add(new BiomeManager.BiomeEntry(Biomes.DESERT, 30));
      this.biomes[desertIdx].add(new BiomeManager.BiomeEntry(Biomes.SAVANNA, 20));
      this.biomes[desertIdx].add(new BiomeManager.BiomeEntry(Biomes.PLAINS, 10));
      if (p_i225882_1_ == WorldType.DEFAULT_1_1) {
         this.biomes[desertIdx].clear();
         this.biomes[desertIdx].add(new BiomeManager.BiomeEntry(Biomes.DESERT, 10));
         this.biomes[desertIdx].add(new BiomeManager.BiomeEntry(Biomes.FOREST, 10));
         this.biomes[desertIdx].add(new BiomeManager.BiomeEntry(Biomes.MOUNTAINS, 10));
         this.biomes[desertIdx].add(new BiomeManager.BiomeEntry(Biomes.SWAMP, 10));
         this.biomes[desertIdx].add(new BiomeManager.BiomeEntry(Biomes.PLAINS, 10));
         this.biomes[desertIdx].add(new BiomeManager.BiomeEntry(Biomes.TAIGA, 10));
         this.field_227472_v_ = -1;
      } else {
         this.field_227472_v_ = p_i225882_2_;
      }

   }

   public int apply(INoiseRandom p_202726_1_, int p_202726_2_) {
      if (this.field_227472_v_ >= 0) {
         return this.field_227472_v_;
      } else {
         int i = (p_202726_2_ & 3840) >> 8;
         p_202726_2_ &= -3841;
         if (!LayerUtil.isOcean(p_202726_2_) && p_202726_2_ != MUSHROOM_FIELDS) {
            switch(p_202726_2_) {
            case 1:
               if (i > 0) {
                  return p_202726_1_.random(3) == 0 ? BADLANDS_PLATEAU : WOODED_BADLANDS_PLATEAU;
               }

               return Registry.BIOME.getId(this.getWeightedBiomeEntry(BiomeManager.BiomeType.DESERT, p_202726_1_).biome);
            case 2:
               if (i > 0) {
                  return JUNGLE;
               }

               return Registry.BIOME.getId(this.getWeightedBiomeEntry(BiomeManager.BiomeType.WARM, p_202726_1_).biome);
            case 3:
               if (i > 0) {
                  return GIANT_TREE_TAIGA;
               }

               return Registry.BIOME.getId(this.getWeightedBiomeEntry(BiomeManager.BiomeType.COOL, p_202726_1_).biome);
            case 4:
               return Registry.BIOME.getId(this.getWeightedBiomeEntry(BiomeManager.BiomeType.ICY, p_202726_1_).biome);
            default:
               return MUSHROOM_FIELDS;
            }
         } else {
            return p_202726_2_;
         }
      }
   }

   protected BiomeManager.BiomeEntry getWeightedBiomeEntry(BiomeManager.BiomeType p_getWeightedBiomeEntry_1_, INoiseRandom p_getWeightedBiomeEntry_2_) {
      List<BiomeManager.BiomeEntry> biomeList = this.biomes[p_getWeightedBiomeEntry_1_.ordinal()];
      int totalWeight = WeightedRandom.getTotalWeight(biomeList);
      int weight = BiomeManager.isTypeListModded(p_getWeightedBiomeEntry_1_) ? p_getWeightedBiomeEntry_2_.random(totalWeight) : p_getWeightedBiomeEntry_2_.random(totalWeight / 10) * 10;
      return (BiomeManager.BiomeEntry)WeightedRandom.getRandomItem(biomeList, weight);
   }

   static {
      BIRCH_FOREST = Registry.BIOME.getId(Biomes.BIRCH_FOREST);
      DESERT = Registry.BIOME.getId(Biomes.DESERT);
      MOUNTAINS = Registry.BIOME.getId(Biomes.MOUNTAINS);
      FOREST = Registry.BIOME.getId(Biomes.FOREST);
      SNOWY_TUNDRA = Registry.BIOME.getId(Biomes.SNOWY_TUNDRA);
      JUNGLE = Registry.BIOME.getId(Biomes.JUNGLE);
      BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.BADLANDS_PLATEAU);
      WOODED_BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.WOODED_BADLANDS_PLATEAU);
      MUSHROOM_FIELDS = Registry.BIOME.getId(Biomes.MUSHROOM_FIELDS);
      PLAINS = Registry.BIOME.getId(Biomes.PLAINS);
      GIANT_TREE_TAIGA = Registry.BIOME.getId(Biomes.GIANT_TREE_TAIGA);
      DARK_FOREST = Registry.BIOME.getId(Biomes.DARK_FOREST);
      SAVANNA = Registry.BIOME.getId(Biomes.SAVANNA);
      SWAMP = Registry.BIOME.getId(Biomes.SWAMP);
      TAIGA = Registry.BIOME.getId(Biomes.TAIGA);
      SNOWY_TAIGA = Registry.BIOME.getId(Biomes.SNOWY_TAIGA);
   }
}
