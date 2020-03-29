package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class OceanMonumentStructure extends Structure<NoFeatureConfig> {
   private static final List<Biome.SpawnListEntry> MONUMENT_ENEMIES;

   public OceanMonumentStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51474_1_) {
      super(p_i51474_1_);
   }

   protected ChunkPos getStartPositionForPosition(ChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
      int lvt_7_1_ = p_211744_1_.getSettings().getOceanMonumentSpacing();
      int lvt_8_1_ = p_211744_1_.getSettings().getOceanMonumentSeparation();
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
         Set<Biome> lvt_8_1_ = p_225558_2_.getBiomeProvider().func_225530_a_(p_225558_4_ * 16 + 9, p_225558_2_.getSeaLevel(), p_225558_5_ * 16 + 9, 16);
         Iterator var9 = lvt_8_1_.iterator();

         Biome lvt_10_1_;
         do {
            if (!var9.hasNext()) {
               Set<Biome> lvt_9_1_ = p_225558_2_.getBiomeProvider().func_225530_a_(p_225558_4_ * 16 + 9, p_225558_2_.getSeaLevel(), p_225558_5_ * 16 + 9, 29);
               Iterator var13 = lvt_9_1_.iterator();

               Biome lvt_11_1_;
               do {
                  if (!var13.hasNext()) {
                     return true;
                  }

                  lvt_11_1_ = (Biome)var13.next();
               } while(lvt_11_1_.getCategory() == Biome.Category.OCEAN || lvt_11_1_.getCategory() == Biome.Category.RIVER);

               return false;
            }

            lvt_10_1_ = (Biome)var9.next();
         } while(p_225558_2_.hasStructure(lvt_10_1_, this));

         return false;
      } else {
         return false;
      }
   }

   public Structure.IStartFactory getStartFactory() {
      return OceanMonumentStructure.Start::new;
   }

   public String getStructureName() {
      return "Monument";
   }

   public int getSize() {
      return 8;
   }

   public List<Biome.SpawnListEntry> getSpawnList() {
      return MONUMENT_ENEMIES;
   }

   static {
      MONUMENT_ENEMIES = Lists.newArrayList(new Biome.SpawnListEntry[]{new Biome.SpawnListEntry(EntityType.GUARDIAN, 1, 2, 4)});
   }

   public static class Start extends StructureStart {
      private boolean wasCreated;

      public Start(Structure<?> p_i225814_1_, int p_i225814_2_, int p_i225814_3_, MutableBoundingBox p_i225814_4_, int p_i225814_5_, long p_i225814_6_) {
         super(p_i225814_1_, p_i225814_2_, p_i225814_3_, p_i225814_4_, p_i225814_5_, p_i225814_6_);
      }

      public void init(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         this.func_214633_b(p_214625_3_, p_214625_4_);
      }

      private void func_214633_b(int p_214633_1_, int p_214633_2_) {
         int lvt_3_1_ = p_214633_1_ * 16 - 29;
         int lvt_4_1_ = p_214633_2_ * 16 - 29;
         Direction lvt_5_1_ = Direction.Plane.HORIZONTAL.random(this.rand);
         this.components.add(new OceanMonumentPieces.MonumentBuilding(this.rand, lvt_3_1_, lvt_4_1_, lvt_5_1_));
         this.recalculateStructureSize();
         this.wasCreated = true;
      }

      public void func_225565_a_(IWorld p_225565_1_, ChunkGenerator<?> p_225565_2_, Random p_225565_3_, MutableBoundingBox p_225565_4_, ChunkPos p_225565_5_) {
         if (!this.wasCreated) {
            this.components.clear();
            this.func_214633_b(this.getChunkPosX(), this.getChunkPosZ());
         }

         super.func_225565_a_(p_225565_1_, p_225565_2_, p_225565_3_, p_225565_4_, p_225565_5_);
      }
   }
}
