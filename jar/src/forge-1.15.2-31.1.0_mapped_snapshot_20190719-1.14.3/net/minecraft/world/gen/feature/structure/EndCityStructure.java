package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class EndCityStructure extends Structure<NoFeatureConfig> {
   public EndCityStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49883_1_) {
      super(p_i49883_1_);
   }

   protected ChunkPos getStartPositionForPosition(ChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
      int lvt_7_1_ = p_211744_1_.getSettings().getEndCityDistance();
      int lvt_8_1_ = p_211744_1_.getSettings().getEndCitySeparation();
      int lvt_9_1_ = p_211744_3_ + lvt_7_1_ * p_211744_5_;
      int lvt_10_1_ = p_211744_4_ + lvt_7_1_ * p_211744_6_;
      int lvt_11_1_ = lvt_9_1_ < 0 ? lvt_9_1_ - lvt_7_1_ + 1 : lvt_9_1_;
      int lvt_12_1_ = lvt_10_1_ < 0 ? lvt_10_1_ - lvt_7_1_ + 1 : lvt_10_1_;
      int lvt_13_1_ = lvt_11_1_ / lvt_7_1_;
      int lvt_14_1_ = lvt_12_1_ / lvt_7_1_;
      ((SharedSeedRandom)p_211744_2_).setLargeFeatureSeedWithSalt(p_211744_1_.getSeed(), lvt_13_1_, lvt_14_1_, 10387313);
      lvt_13_1_ *= lvt_7_1_;
      lvt_14_1_ *= lvt_7_1_;
      lvt_13_1_ += (p_211744_2_.nextInt(lvt_7_1_ - lvt_8_1_) + p_211744_2_.nextInt(lvt_7_1_ - lvt_8_1_)) / 2;
      lvt_14_1_ += (p_211744_2_.nextInt(lvt_7_1_ - lvt_8_1_) + p_211744_2_.nextInt(lvt_7_1_ - lvt_8_1_)) / 2;
      return new ChunkPos(lvt_13_1_, lvt_14_1_);
   }

   public boolean func_225558_a_(BiomeManager p_225558_1_, ChunkGenerator<?> p_225558_2_, Random p_225558_3_, int p_225558_4_, int p_225558_5_, Biome p_225558_6_) {
      ChunkPos lvt_7_1_ = this.getStartPositionForPosition(p_225558_2_, p_225558_3_, p_225558_4_, p_225558_5_, 0, 0);
      if (p_225558_4_ == lvt_7_1_.x && p_225558_5_ == lvt_7_1_.z) {
         if (!p_225558_2_.hasStructure(p_225558_6_, this)) {
            return false;
         } else {
            int lvt_8_1_ = getYPosForStructure(p_225558_4_, p_225558_5_, p_225558_2_);
            return lvt_8_1_ >= 60;
         }
      } else {
         return false;
      }
   }

   public Structure.IStartFactory getStartFactory() {
      return EndCityStructure.Start::new;
   }

   public String getStructureName() {
      return "EndCity";
   }

   public int getSize() {
      return 8;
   }

   private static int getYPosForStructure(int p_191070_0_, int p_191070_1_, ChunkGenerator<?> p_191070_2_) {
      Random lvt_3_1_ = new Random((long)(p_191070_0_ + p_191070_1_ * 10387313));
      Rotation lvt_4_1_ = Rotation.values()[lvt_3_1_.nextInt(Rotation.values().length)];
      int lvt_5_1_ = 5;
      int lvt_6_1_ = 5;
      if (lvt_4_1_ == Rotation.CLOCKWISE_90) {
         lvt_5_1_ = -5;
      } else if (lvt_4_1_ == Rotation.CLOCKWISE_180) {
         lvt_5_1_ = -5;
         lvt_6_1_ = -5;
      } else if (lvt_4_1_ == Rotation.COUNTERCLOCKWISE_90) {
         lvt_6_1_ = -5;
      }

      int lvt_7_1_ = (p_191070_0_ << 4) + 7;
      int lvt_8_1_ = (p_191070_1_ << 4) + 7;
      int lvt_9_1_ = p_191070_2_.func_222531_c(lvt_7_1_, lvt_8_1_, Heightmap.Type.WORLD_SURFACE_WG);
      int lvt_10_1_ = p_191070_2_.func_222531_c(lvt_7_1_, lvt_8_1_ + lvt_6_1_, Heightmap.Type.WORLD_SURFACE_WG);
      int lvt_11_1_ = p_191070_2_.func_222531_c(lvt_7_1_ + lvt_5_1_, lvt_8_1_, Heightmap.Type.WORLD_SURFACE_WG);
      int lvt_12_1_ = p_191070_2_.func_222531_c(lvt_7_1_ + lvt_5_1_, lvt_8_1_ + lvt_6_1_, Heightmap.Type.WORLD_SURFACE_WG);
      return Math.min(Math.min(lvt_9_1_, lvt_10_1_), Math.min(lvt_11_1_, lvt_12_1_));
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225802_1_, int p_i225802_2_, int p_i225802_3_, MutableBoundingBox p_i225802_4_, int p_i225802_5_, long p_i225802_6_) {
         super(p_i225802_1_, p_i225802_2_, p_i225802_3_, p_i225802_4_, p_i225802_5_, p_i225802_6_);
      }

      public void init(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         Rotation lvt_6_1_ = Rotation.values()[this.rand.nextInt(Rotation.values().length)];
         int lvt_7_1_ = EndCityStructure.getYPosForStructure(p_214625_3_, p_214625_4_, p_214625_1_);
         if (lvt_7_1_ >= 60) {
            BlockPos lvt_8_1_ = new BlockPos(p_214625_3_ * 16 + 8, lvt_7_1_, p_214625_4_ * 16 + 8);
            EndCityPieces.startHouseTower(p_214625_2_, lvt_8_1_, lvt_6_1_, this.components, this.rand);
            this.recalculateStructureSize();
         }
      }
   }
}
