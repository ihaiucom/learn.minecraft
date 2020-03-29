package net.minecraft.world.gen;

import java.util.List;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.CatSpawner;
import net.minecraft.world.spawner.PatrolSpawner;
import net.minecraft.world.spawner.PhantomSpawner;
import net.minecraft.world.spawner.WorldEntitySpawner;

public class OverworldChunkGenerator extends NoiseChunkGenerator<OverworldGenSettings> {
   private static final float[] field_222576_h = (float[])Util.make(new float[25], (p_222575_0_) -> {
      for(int lvt_1_1_ = -2; lvt_1_1_ <= 2; ++lvt_1_1_) {
         for(int lvt_2_1_ = -2; lvt_2_1_ <= 2; ++lvt_2_1_) {
            float lvt_3_1_ = 10.0F / MathHelper.sqrt((float)(lvt_1_1_ * lvt_1_1_ + lvt_2_1_ * lvt_2_1_) + 0.2F);
            p_222575_0_[lvt_1_1_ + 2 + (lvt_2_1_ + 2) * 5] = lvt_3_1_;
         }
      }

   });
   private final OctavesNoiseGenerator depthNoise;
   private final boolean field_222577_j;
   private final PhantomSpawner phantomSpawner = new PhantomSpawner();
   private final PatrolSpawner patrolSpawner = new PatrolSpawner();
   private final CatSpawner catSpawner = new CatSpawner();
   private final VillageSiege field_225495_n = new VillageSiege();

   public OverworldChunkGenerator(IWorld p_i48957_1_, BiomeProvider p_i48957_2_, OverworldGenSettings p_i48957_3_) {
      super(p_i48957_1_, p_i48957_2_, 4, 8, 256, p_i48957_3_, true);
      this.randomSeed.skip(2620);
      this.depthNoise = new OctavesNoiseGenerator(this.randomSeed, 15, 0);
      this.field_222577_j = p_i48957_1_.getWorldInfo().getGenerator() == WorldType.AMPLIFIED;
   }

   public void spawnMobs(WorldGenRegion p_202093_1_) {
      int lvt_2_1_ = p_202093_1_.getMainChunkX();
      int lvt_3_1_ = p_202093_1_.getMainChunkZ();
      Biome lvt_4_1_ = p_202093_1_.func_226691_t_((new ChunkPos(lvt_2_1_, lvt_3_1_)).asBlockPos());
      SharedSeedRandom lvt_5_1_ = new SharedSeedRandom();
      lvt_5_1_.setDecorationSeed(p_202093_1_.getSeed(), lvt_2_1_ << 4, lvt_3_1_ << 4);
      WorldEntitySpawner.performWorldGenSpawning(p_202093_1_, lvt_4_1_, lvt_2_1_, lvt_3_1_, lvt_5_1_);
   }

   protected void func_222548_a(double[] p_222548_1_, int p_222548_2_, int p_222548_3_) {
      double lvt_4_1_ = 684.4119873046875D;
      double lvt_6_1_ = 684.4119873046875D;
      double lvt_8_1_ = 8.555149841308594D;
      double lvt_10_1_ = 4.277574920654297D;
      int lvt_12_1_ = true;
      int lvt_13_1_ = true;
      this.func_222546_a(p_222548_1_, p_222548_2_, p_222548_3_, 684.4119873046875D, 684.4119873046875D, 8.555149841308594D, 4.277574920654297D, 3, -10);
   }

   protected double func_222545_a(double p_222545_1_, double p_222545_3_, int p_222545_5_) {
      double lvt_6_1_ = 8.5D;
      double lvt_8_1_ = ((double)p_222545_5_ - (8.5D + p_222545_1_ * 8.5D / 8.0D * 4.0D)) * 12.0D * 128.0D / 256.0D / p_222545_3_;
      if (lvt_8_1_ < 0.0D) {
         lvt_8_1_ *= 4.0D;
      }

      return lvt_8_1_;
   }

   protected double[] func_222549_a(int p_222549_1_, int p_222549_2_) {
      double[] lvt_3_1_ = new double[2];
      float lvt_4_1_ = 0.0F;
      float lvt_5_1_ = 0.0F;
      float lvt_6_1_ = 0.0F;
      int lvt_7_1_ = true;
      int lvt_8_1_ = this.getSeaLevel();
      float lvt_9_1_ = this.biomeProvider.func_225526_b_(p_222549_1_, lvt_8_1_, p_222549_2_).getDepth();

      for(int lvt_10_1_ = -2; lvt_10_1_ <= 2; ++lvt_10_1_) {
         for(int lvt_11_1_ = -2; lvt_11_1_ <= 2; ++lvt_11_1_) {
            Biome lvt_12_1_ = this.biomeProvider.func_225526_b_(p_222549_1_ + lvt_10_1_, lvt_8_1_, p_222549_2_ + lvt_11_1_);
            float lvt_13_1_ = lvt_12_1_.getDepth();
            float lvt_14_1_ = lvt_12_1_.getScale();
            if (this.field_222577_j && lvt_13_1_ > 0.0F) {
               lvt_13_1_ = 1.0F + lvt_13_1_ * 2.0F;
               lvt_14_1_ = 1.0F + lvt_14_1_ * 4.0F;
            }

            float lvt_15_1_ = field_222576_h[lvt_10_1_ + 2 + (lvt_11_1_ + 2) * 5] / (lvt_13_1_ + 2.0F);
            if (lvt_12_1_.getDepth() > lvt_9_1_) {
               lvt_15_1_ /= 2.0F;
            }

            lvt_4_1_ += lvt_14_1_ * lvt_15_1_;
            lvt_5_1_ += lvt_13_1_ * lvt_15_1_;
            lvt_6_1_ += lvt_15_1_;
         }
      }

      lvt_4_1_ /= lvt_6_1_;
      lvt_5_1_ /= lvt_6_1_;
      lvt_4_1_ = lvt_4_1_ * 0.9F + 0.1F;
      lvt_5_1_ = (lvt_5_1_ * 4.0F - 1.0F) / 8.0F;
      lvt_3_1_[0] = (double)lvt_5_1_ + this.func_222574_c(p_222549_1_, p_222549_2_);
      lvt_3_1_[1] = (double)lvt_4_1_;
      return lvt_3_1_;
   }

   private double func_222574_c(int p_222574_1_, int p_222574_2_) {
      double lvt_3_1_ = this.depthNoise.func_215462_a((double)(p_222574_1_ * 200), 10.0D, (double)(p_222574_2_ * 200), 1.0D, 0.0D, true) * 65535.0D / 8000.0D;
      if (lvt_3_1_ < 0.0D) {
         lvt_3_1_ = -lvt_3_1_ * 0.3D;
      }

      lvt_3_1_ = lvt_3_1_ * 3.0D - 2.0D;
      if (lvt_3_1_ < 0.0D) {
         lvt_3_1_ /= 28.0D;
      } else {
         if (lvt_3_1_ > 1.0D) {
            lvt_3_1_ = 1.0D;
         }

         lvt_3_1_ /= 40.0D;
      }

      return lvt_3_1_;
   }

   public List<Biome.SpawnListEntry> getPossibleCreatures(EntityClassification p_177458_1_, BlockPos p_177458_2_) {
      if (Feature.SWAMP_HUT.func_202383_b(this.world, p_177458_2_)) {
         if (p_177458_1_ == EntityClassification.MONSTER) {
            return Feature.SWAMP_HUT.getSpawnList();
         }

         if (p_177458_1_ == EntityClassification.CREATURE) {
            return Feature.SWAMP_HUT.getCreatureSpawnList();
         }
      } else if (p_177458_1_ == EntityClassification.MONSTER) {
         if (Feature.PILLAGER_OUTPOST.isPositionInStructure(this.world, p_177458_2_)) {
            return Feature.PILLAGER_OUTPOST.getSpawnList();
         }

         if (Feature.OCEAN_MONUMENT.isPositionInStructure(this.world, p_177458_2_)) {
            return Feature.OCEAN_MONUMENT.getSpawnList();
         }
      }

      return super.getPossibleCreatures(p_177458_1_, p_177458_2_);
   }

   public void spawnMobs(ServerWorld p_203222_1_, boolean p_203222_2_, boolean p_203222_3_) {
      this.phantomSpawner.tick(p_203222_1_, p_203222_2_, p_203222_3_);
      this.patrolSpawner.tick(p_203222_1_, p_203222_2_, p_203222_3_);
      this.catSpawner.tick(p_203222_1_, p_203222_2_, p_203222_3_);
      this.field_225495_n.func_225477_a(p_203222_1_, p_203222_2_, p_203222_3_);
   }

   public int getGroundHeight() {
      return this.world.getSeaLevel() + 1;
   }

   public int getSeaLevel() {
      return 63;
   }
}
