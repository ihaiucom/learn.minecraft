package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class VillageStructure extends Structure<VillageConfig> {
   public VillageStructure(Function<Dynamic<?>, ? extends VillageConfig> p_i51419_1_) {
      super(p_i51419_1_);
   }

   protected ChunkPos getStartPositionForPosition(ChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
      int lvt_7_1_ = p_211744_1_.getSettings().getVillageDistance();
      int lvt_8_1_ = p_211744_1_.getSettings().getVillageSeparation();
      int lvt_9_1_ = p_211744_3_ + lvt_7_1_ * p_211744_5_;
      int lvt_10_1_ = p_211744_4_ + lvt_7_1_ * p_211744_6_;
      int lvt_11_1_ = lvt_9_1_ < 0 ? lvt_9_1_ - lvt_7_1_ + 1 : lvt_9_1_;
      int lvt_12_1_ = lvt_10_1_ < 0 ? lvt_10_1_ - lvt_7_1_ + 1 : lvt_10_1_;
      int lvt_13_1_ = lvt_11_1_ / lvt_7_1_;
      int lvt_14_1_ = lvt_12_1_ / lvt_7_1_;
      ((SharedSeedRandom)p_211744_2_).setLargeFeatureSeedWithSalt(p_211744_1_.getSeed(), lvt_13_1_, lvt_14_1_, 10387312);
      lvt_13_1_ *= lvt_7_1_;
      lvt_14_1_ *= lvt_7_1_;
      lvt_13_1_ += p_211744_2_.nextInt(lvt_7_1_ - lvt_8_1_);
      lvt_14_1_ += p_211744_2_.nextInt(lvt_7_1_ - lvt_8_1_);
      return new ChunkPos(lvt_13_1_, lvt_14_1_);
   }

   public boolean func_225558_a_(BiomeManager p_225558_1_, ChunkGenerator<?> p_225558_2_, Random p_225558_3_, int p_225558_4_, int p_225558_5_, Biome p_225558_6_) {
      ChunkPos lvt_7_1_ = this.getStartPositionForPosition(p_225558_2_, p_225558_3_, p_225558_4_, p_225558_5_, 0, 0);
      return p_225558_4_ == lvt_7_1_.x && p_225558_5_ == lvt_7_1_.z ? p_225558_2_.hasStructure(p_225558_6_, this) : false;
   }

   public Structure.IStartFactory getStartFactory() {
      return VillageStructure.Start::new;
   }

   public String getStructureName() {
      return "Village";
   }

   public int getSize() {
      return 8;
   }

   public static class Start extends MarginedStructureStart {
      public Start(Structure<?> p_i225821_1_, int p_i225821_2_, int p_i225821_3_, MutableBoundingBox p_i225821_4_, int p_i225821_5_, long p_i225821_6_) {
         super(p_i225821_1_, p_i225821_2_, p_i225821_3_, p_i225821_4_, p_i225821_5_, p_i225821_6_);
      }

      public void init(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         VillageConfig lvt_6_1_ = (VillageConfig)p_214625_1_.getStructureConfig(p_214625_5_, Feature.VILLAGE);
         BlockPos lvt_7_1_ = new BlockPos(p_214625_3_ * 16, 0, p_214625_4_ * 16);
         VillagePieces.func_214838_a(p_214625_1_, p_214625_2_, lvt_7_1_, this.components, this.rand, lvt_6_1_);
         this.recalculateStructureSize();
      }
   }
}
