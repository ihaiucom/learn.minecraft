package net.minecraft.world.biome.provider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.structure.Structure;

public abstract class BiomeProvider implements BiomeManager.IBiomeReader {
   public static final List<Biome> BIOMES_TO_SPAWN_IN;
   protected final Map<Structure<?>, Boolean> hasStructureCache = Maps.newHashMap();
   protected final Set<BlockState> topBlocksCache = Sets.newHashSet();
   protected final Set<Biome> field_226837_c_;

   protected BiomeProvider(Set<Biome> p_i225745_1_) {
      this.field_226837_c_ = p_i225745_1_;
   }

   public List<Biome> getBiomesToSpawnIn() {
      return BIOMES_TO_SPAWN_IN;
   }

   public Set<Biome> func_225530_a_(int p_225530_1_, int p_225530_2_, int p_225530_3_, int p_225530_4_) {
      int lvt_5_1_ = p_225530_1_ - p_225530_4_ >> 2;
      int lvt_6_1_ = p_225530_2_ - p_225530_4_ >> 2;
      int lvt_7_1_ = p_225530_3_ - p_225530_4_ >> 2;
      int lvt_8_1_ = p_225530_1_ + p_225530_4_ >> 2;
      int lvt_9_1_ = p_225530_2_ + p_225530_4_ >> 2;
      int lvt_10_1_ = p_225530_3_ + p_225530_4_ >> 2;
      int lvt_11_1_ = lvt_8_1_ - lvt_5_1_ + 1;
      int lvt_12_1_ = lvt_9_1_ - lvt_6_1_ + 1;
      int lvt_13_1_ = lvt_10_1_ - lvt_7_1_ + 1;
      Set<Biome> lvt_14_1_ = Sets.newHashSet();

      for(int lvt_15_1_ = 0; lvt_15_1_ < lvt_13_1_; ++lvt_15_1_) {
         for(int lvt_16_1_ = 0; lvt_16_1_ < lvt_11_1_; ++lvt_16_1_) {
            for(int lvt_17_1_ = 0; lvt_17_1_ < lvt_12_1_; ++lvt_17_1_) {
               int lvt_18_1_ = lvt_5_1_ + lvt_16_1_;
               int lvt_19_1_ = lvt_6_1_ + lvt_17_1_;
               int lvt_20_1_ = lvt_7_1_ + lvt_15_1_;
               lvt_14_1_.add(this.func_225526_b_(lvt_18_1_, lvt_19_1_, lvt_20_1_));
            }
         }
      }

      return lvt_14_1_;
   }

   @Nullable
   public BlockPos func_225531_a_(int p_225531_1_, int p_225531_2_, int p_225531_3_, int p_225531_4_, List<Biome> p_225531_5_, Random p_225531_6_) {
      int lvt_7_1_ = p_225531_1_ - p_225531_4_ >> 2;
      int lvt_8_1_ = p_225531_3_ - p_225531_4_ >> 2;
      int lvt_9_1_ = p_225531_1_ + p_225531_4_ >> 2;
      int lvt_10_1_ = p_225531_3_ + p_225531_4_ >> 2;
      int lvt_11_1_ = lvt_9_1_ - lvt_7_1_ + 1;
      int lvt_12_1_ = lvt_10_1_ - lvt_8_1_ + 1;
      int lvt_13_1_ = p_225531_2_ >> 2;
      BlockPos lvt_14_1_ = null;
      int lvt_15_1_ = 0;

      for(int lvt_16_1_ = 0; lvt_16_1_ < lvt_12_1_; ++lvt_16_1_) {
         for(int lvt_17_1_ = 0; lvt_17_1_ < lvt_11_1_; ++lvt_17_1_) {
            int lvt_18_1_ = lvt_7_1_ + lvt_17_1_;
            int lvt_19_1_ = lvt_8_1_ + lvt_16_1_;
            if (p_225531_5_.contains(this.func_225526_b_(lvt_18_1_, lvt_13_1_, lvt_19_1_))) {
               if (lvt_14_1_ == null || p_225531_6_.nextInt(lvt_15_1_ + 1) == 0) {
                  lvt_14_1_ = new BlockPos(lvt_18_1_ << 2, p_225531_2_, lvt_19_1_ << 2);
               }

               ++lvt_15_1_;
            }
         }
      }

      return lvt_14_1_;
   }

   public float func_222365_c(int p_222365_1_, int p_222365_2_) {
      return 0.0F;
   }

   public boolean hasStructure(Structure<?> p_205004_1_) {
      return (Boolean)this.hasStructureCache.computeIfAbsent(p_205004_1_, (p_226839_1_) -> {
         return this.field_226837_c_.stream().anyMatch((p_226838_1_) -> {
            return p_226838_1_.hasStructure(p_226839_1_);
         });
      });
   }

   public Set<BlockState> getSurfaceBlocks() {
      if (this.topBlocksCache.isEmpty()) {
         Iterator var1 = this.field_226837_c_.iterator();

         while(var1.hasNext()) {
            Biome lvt_2_1_ = (Biome)var1.next();
            this.topBlocksCache.add(lvt_2_1_.getSurfaceBuilderConfig().getTop());
         }
      }

      return this.topBlocksCache;
   }

   static {
      BIOMES_TO_SPAWN_IN = Lists.newArrayList(new Biome[]{Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.WOODED_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS});
   }
}
