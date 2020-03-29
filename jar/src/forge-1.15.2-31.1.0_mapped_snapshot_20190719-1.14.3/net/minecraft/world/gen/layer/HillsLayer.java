package net.minecraft.world.gen.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset1Transformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum HillsLayer implements IAreaTransformer2, IDimOffset1Transformer {
   INSTANCE;

   private static final Logger LOGGER = LogManager.getLogger();
   private static final int BIRCH_FOREST = Registry.BIOME.getId(Biomes.BIRCH_FOREST);
   private static final int BIRCH_FOREST_HILLS = Registry.BIOME.getId(Biomes.BIRCH_FOREST_HILLS);
   private static final int DESERT = Registry.BIOME.getId(Biomes.DESERT);
   private static final int DESERT_HILLS = Registry.BIOME.getId(Biomes.DESERT_HILLS);
   private static final int MOUNTAINS = Registry.BIOME.getId(Biomes.MOUNTAINS);
   private static final int WOODED_MOUNTAINS = Registry.BIOME.getId(Biomes.WOODED_MOUNTAINS);
   private static final int FOREST = Registry.BIOME.getId(Biomes.FOREST);
   private static final int WOODED_HILLS = Registry.BIOME.getId(Biomes.WOODED_HILLS);
   private static final int SNOWY_TUNDRA = Registry.BIOME.getId(Biomes.SNOWY_TUNDRA);
   private static final int SNOWY_MOUNTAINS = Registry.BIOME.getId(Biomes.SNOWY_MOUNTAINS);
   private static final int JUNGLE = Registry.BIOME.getId(Biomes.JUNGLE);
   private static final int JUNGLE_HILLS = Registry.BIOME.getId(Biomes.JUNGLE_HILLS);
   private static final int BAMBOO_JUNGLE = Registry.BIOME.getId(Biomes.BAMBOO_JUNGLE);
   private static final int BAMBOO_JUNGLE_HILLS = Registry.BIOME.getId(Biomes.BAMBOO_JUNGLE_HILLS);
   private static final int BADLANDS = Registry.BIOME.getId(Biomes.BADLANDS);
   private static final int WOODED_BADLANDS_PLATEAU = Registry.BIOME.getId(Biomes.WOODED_BADLANDS_PLATEAU);
   private static final int PLAINS = Registry.BIOME.getId(Biomes.PLAINS);
   private static final int GIANT_TREE_TAIGA = Registry.BIOME.getId(Biomes.GIANT_TREE_TAIGA);
   private static final int GIANT_TREE_TAIGA_HILLS = Registry.BIOME.getId(Biomes.GIANT_TREE_TAIGA_HILLS);
   private static final int DARK_FOREST = Registry.BIOME.getId(Biomes.DARK_FOREST);
   private static final int SAVANNA = Registry.BIOME.getId(Biomes.SAVANNA);
   private static final int SAVANA_PLATEAU = Registry.BIOME.getId(Biomes.SAVANNA_PLATEAU);
   private static final int TAIGA = Registry.BIOME.getId(Biomes.TAIGA);
   private static final int SNOWY_TAIGA = Registry.BIOME.getId(Biomes.SNOWY_TAIGA);
   private static final int SNOWY_TAIGA_HILLS = Registry.BIOME.getId(Biomes.SNOWY_TAIGA_HILLS);
   private static final int TAIGA_HILLS = Registry.BIOME.getId(Biomes.TAIGA_HILLS);

   public int apply(INoiseRandom p_215723_1_, IArea p_215723_2_, IArea p_215723_3_, int p_215723_4_, int p_215723_5_) {
      int lvt_6_1_ = p_215723_2_.getValue(this.func_215721_a(p_215723_4_ + 1), this.func_215722_b(p_215723_5_ + 1));
      int lvt_7_1_ = p_215723_3_.getValue(this.func_215721_a(p_215723_4_ + 1), this.func_215722_b(p_215723_5_ + 1));
      if (lvt_6_1_ > 255) {
         LOGGER.debug("old! {}", lvt_6_1_);
      }

      int lvt_8_1_ = (lvt_7_1_ - 2) % 29;
      Biome lvt_10_2_;
      if (!LayerUtil.isShallowOcean(lvt_6_1_) && lvt_7_1_ >= 2 && lvt_8_1_ == 1) {
         Biome lvt_9_1_ = (Biome)Registry.BIOME.getByValue(lvt_6_1_);
         if (lvt_9_1_ == null || !lvt_9_1_.isMutation()) {
            lvt_10_2_ = Biome.getMutationForBiome(lvt_9_1_);
            return lvt_10_2_ == null ? lvt_6_1_ : Registry.BIOME.getId(lvt_10_2_);
         }
      }

      if (p_215723_1_.random(3) == 0 || lvt_8_1_ == 0) {
         int lvt_9_2_ = lvt_6_1_;
         if (lvt_6_1_ == DESERT) {
            lvt_9_2_ = DESERT_HILLS;
         } else if (lvt_6_1_ == FOREST) {
            lvt_9_2_ = WOODED_HILLS;
         } else if (lvt_6_1_ == BIRCH_FOREST) {
            lvt_9_2_ = BIRCH_FOREST_HILLS;
         } else if (lvt_6_1_ == DARK_FOREST) {
            lvt_9_2_ = PLAINS;
         } else if (lvt_6_1_ == TAIGA) {
            lvt_9_2_ = TAIGA_HILLS;
         } else if (lvt_6_1_ == GIANT_TREE_TAIGA) {
            lvt_9_2_ = GIANT_TREE_TAIGA_HILLS;
         } else if (lvt_6_1_ == SNOWY_TAIGA) {
            lvt_9_2_ = SNOWY_TAIGA_HILLS;
         } else if (lvt_6_1_ == PLAINS) {
            lvt_9_2_ = p_215723_1_.random(3) == 0 ? WOODED_HILLS : FOREST;
         } else if (lvt_6_1_ == SNOWY_TUNDRA) {
            lvt_9_2_ = SNOWY_MOUNTAINS;
         } else if (lvt_6_1_ == JUNGLE) {
            lvt_9_2_ = JUNGLE_HILLS;
         } else if (lvt_6_1_ == BAMBOO_JUNGLE) {
            lvt_9_2_ = BAMBOO_JUNGLE_HILLS;
         } else if (lvt_6_1_ == LayerUtil.OCEAN) {
            lvt_9_2_ = LayerUtil.DEEP_OCEAN;
         } else if (lvt_6_1_ == LayerUtil.LUKEWARM_OCEAN) {
            lvt_9_2_ = LayerUtil.DEEP_LUKEWARM_OCEAN;
         } else if (lvt_6_1_ == LayerUtil.COLD_OCEAN) {
            lvt_9_2_ = LayerUtil.DEEP_COLD_OCEAN;
         } else if (lvt_6_1_ == LayerUtil.FROZEN_OCEAN) {
            lvt_9_2_ = LayerUtil.DEEP_FROZEN_OCEAN;
         } else if (lvt_6_1_ == MOUNTAINS) {
            lvt_9_2_ = WOODED_MOUNTAINS;
         } else if (lvt_6_1_ == SAVANNA) {
            lvt_9_2_ = SAVANA_PLATEAU;
         } else if (LayerUtil.areBiomesSimilar(lvt_6_1_, WOODED_BADLANDS_PLATEAU)) {
            lvt_9_2_ = BADLANDS;
         } else if ((lvt_6_1_ == LayerUtil.DEEP_OCEAN || lvt_6_1_ == LayerUtil.DEEP_LUKEWARM_OCEAN || lvt_6_1_ == LayerUtil.DEEP_COLD_OCEAN || lvt_6_1_ == LayerUtil.DEEP_FROZEN_OCEAN) && p_215723_1_.random(3) == 0) {
            lvt_9_2_ = p_215723_1_.random(2) == 0 ? PLAINS : FOREST;
         }

         if (lvt_8_1_ == 0 && lvt_9_2_ != lvt_6_1_) {
            lvt_10_2_ = Biome.getMutationForBiome((Biome)Registry.BIOME.getByValue(lvt_9_2_));
            lvt_9_2_ = lvt_10_2_ == null ? lvt_6_1_ : Registry.BIOME.getId(lvt_10_2_);
         }

         if (lvt_9_2_ != lvt_6_1_) {
            int lvt_10_3_ = 0;
            if (LayerUtil.areBiomesSimilar(p_215723_2_.getValue(this.func_215721_a(p_215723_4_ + 1), this.func_215722_b(p_215723_5_ + 0)), lvt_6_1_)) {
               ++lvt_10_3_;
            }

            if (LayerUtil.areBiomesSimilar(p_215723_2_.getValue(this.func_215721_a(p_215723_4_ + 2), this.func_215722_b(p_215723_5_ + 1)), lvt_6_1_)) {
               ++lvt_10_3_;
            }

            if (LayerUtil.areBiomesSimilar(p_215723_2_.getValue(this.func_215721_a(p_215723_4_ + 0), this.func_215722_b(p_215723_5_ + 1)), lvt_6_1_)) {
               ++lvt_10_3_;
            }

            if (LayerUtil.areBiomesSimilar(p_215723_2_.getValue(this.func_215721_a(p_215723_4_ + 1), this.func_215722_b(p_215723_5_ + 2)), lvt_6_1_)) {
               ++lvt_10_3_;
            }

            if (lvt_10_3_ >= 3) {
               return lvt_9_2_;
            }
         }
      }

      return lvt_6_1_;
   }
}
