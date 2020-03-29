package net.minecraft.world.gen.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum EdgeBiomeLayer implements ICastleTransformer {
   INSTANCE;

   private static final int DESERT = Registry.BIOME.getId(Biomes.DESERT);
   private static final int MOUNTAINS = Registry.BIOME.getId(Biomes.MOUNTAINS);
   private static final int WOODED_MOUNTAINS = Registry.BIOME.getId(Biomes.WOODED_MOUNTAINS);
   private static final int SNOWY_TUNDRA = Registry.BIOME.getId(Biomes.SNOWY_TUNDRA);
   private static final int JUNGLE = Registry.BIOME.getId(Biomes.JUNGLE);
   private static final int BAMBOO_JUNGLE = Registry.BIOME.getId(Biomes.BAMBOO_JUNGLE);
   private static final int JUNGLE_EDGE = Registry.BIOME.getId(Biomes.JUNGLE_EDGE);
   private static final int BADLANDS = Registry.BIOME.getId(Biomes.BADLANDS);
   private static final int BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.BADLANDS_PLATEAU);
   private static final int WOODED_BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.WOODED_BADLANDS_PLATEAU);
   private static final int PLAINS = Registry.BIOME.getId(Biomes.PLAINS);
   private static final int GIANT_TREE_TAIGA = Registry.BIOME.getId(Biomes.GIANT_TREE_TAIGA);
   private static final int MOUNTAIN_EDGE = Registry.BIOME.getId(Biomes.MOUNTAIN_EDGE);
   private static final int SWAMP = Registry.BIOME.getId(Biomes.SWAMP);
   private static final int TAIGA = Registry.BIOME.getId(Biomes.TAIGA);
   private static final int SNOWY_TAIGA = Registry.BIOME.getId(Biomes.SNOWY_TAIGA);

   public int apply(INoiseRandom p_202748_1_, int p_202748_2_, int p_202748_3_, int p_202748_4_, int p_202748_5_, int p_202748_6_) {
      int[] lvt_7_1_ = new int[1];
      if (!this.func_202751_a(lvt_7_1_, p_202748_2_, p_202748_3_, p_202748_4_, p_202748_5_, p_202748_6_, MOUNTAINS, MOUNTAIN_EDGE) && !this.replaceBiomeEdge(lvt_7_1_, p_202748_2_, p_202748_3_, p_202748_4_, p_202748_5_, p_202748_6_, WOODED_BADLANDS_PLATEAU, BADLANDS) && !this.replaceBiomeEdge(lvt_7_1_, p_202748_2_, p_202748_3_, p_202748_4_, p_202748_5_, p_202748_6_, BADLANDS_PLATEAU, BADLANDS) && !this.replaceBiomeEdge(lvt_7_1_, p_202748_2_, p_202748_3_, p_202748_4_, p_202748_5_, p_202748_6_, GIANT_TREE_TAIGA, TAIGA)) {
         if (p_202748_6_ == DESERT && (p_202748_2_ == SNOWY_TUNDRA || p_202748_3_ == SNOWY_TUNDRA || p_202748_5_ == SNOWY_TUNDRA || p_202748_4_ == SNOWY_TUNDRA)) {
            return WOODED_MOUNTAINS;
         } else {
            if (p_202748_6_ == SWAMP) {
               if (p_202748_2_ == DESERT || p_202748_3_ == DESERT || p_202748_5_ == DESERT || p_202748_4_ == DESERT || p_202748_2_ == SNOWY_TAIGA || p_202748_3_ == SNOWY_TAIGA || p_202748_5_ == SNOWY_TAIGA || p_202748_4_ == SNOWY_TAIGA || p_202748_2_ == SNOWY_TUNDRA || p_202748_3_ == SNOWY_TUNDRA || p_202748_5_ == SNOWY_TUNDRA || p_202748_4_ == SNOWY_TUNDRA) {
                  return PLAINS;
               }

               if (p_202748_2_ == JUNGLE || p_202748_4_ == JUNGLE || p_202748_3_ == JUNGLE || p_202748_5_ == JUNGLE || p_202748_2_ == BAMBOO_JUNGLE || p_202748_4_ == BAMBOO_JUNGLE || p_202748_3_ == BAMBOO_JUNGLE || p_202748_5_ == BAMBOO_JUNGLE) {
                  return JUNGLE_EDGE;
               }
            }

            return p_202748_6_;
         }
      } else {
         return lvt_7_1_[0];
      }
   }

   private boolean func_202751_a(int[] p_202751_1_, int p_202751_2_, int p_202751_3_, int p_202751_4_, int p_202751_5_, int p_202751_6_, int p_202751_7_, int p_202751_8_) {
      if (!LayerUtil.areBiomesSimilar(p_202751_6_, p_202751_7_)) {
         return false;
      } else {
         if (this.canBiomesBeNeighbors(p_202751_2_, p_202751_7_) && this.canBiomesBeNeighbors(p_202751_3_, p_202751_7_) && this.canBiomesBeNeighbors(p_202751_5_, p_202751_7_) && this.canBiomesBeNeighbors(p_202751_4_, p_202751_7_)) {
            p_202751_1_[0] = p_202751_6_;
         } else {
            p_202751_1_[0] = p_202751_8_;
         }

         return true;
      }
   }

   private boolean replaceBiomeEdge(int[] p_151635_1_, int p_151635_2_, int p_151635_3_, int p_151635_4_, int p_151635_5_, int p_151635_6_, int p_151635_7_, int p_151635_8_) {
      if (p_151635_6_ != p_151635_7_) {
         return false;
      } else {
         if (LayerUtil.areBiomesSimilar(p_151635_2_, p_151635_7_) && LayerUtil.areBiomesSimilar(p_151635_3_, p_151635_7_) && LayerUtil.areBiomesSimilar(p_151635_5_, p_151635_7_) && LayerUtil.areBiomesSimilar(p_151635_4_, p_151635_7_)) {
            p_151635_1_[0] = p_151635_6_;
         } else {
            p_151635_1_[0] = p_151635_8_;
         }

         return true;
      }
   }

   private boolean canBiomesBeNeighbors(int p_151634_1_, int p_151634_2_) {
      if (LayerUtil.areBiomesSimilar(p_151634_1_, p_151634_2_)) {
         return true;
      } else {
         Biome lvt_3_1_ = (Biome)Registry.BIOME.getByValue(p_151634_1_);
         Biome lvt_4_1_ = (Biome)Registry.BIOME.getByValue(p_151634_2_);
         if (lvt_3_1_ != null && lvt_4_1_ != null) {
            Biome.TempCategory lvt_5_1_ = lvt_3_1_.getTempCategory();
            Biome.TempCategory lvt_6_1_ = lvt_4_1_.getTempCategory();
            return lvt_5_1_ == lvt_6_1_ || lvt_5_1_ == Biome.TempCategory.MEDIUM || lvt_6_1_ == Biome.TempCategory.MEDIUM;
         } else {
            return false;
         }
      }
   }
}
