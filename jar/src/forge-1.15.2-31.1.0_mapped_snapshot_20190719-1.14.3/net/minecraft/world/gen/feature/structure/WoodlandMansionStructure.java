package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class WoodlandMansionStructure extends Structure<NoFeatureConfig> {
   public WoodlandMansionStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51413_1_) {
      super(p_i51413_1_);
   }

   protected ChunkPos getStartPositionForPosition(ChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
      int lvt_7_1_ = p_211744_1_.getSettings().getMansionDistance();
      int lvt_8_1_ = p_211744_1_.getSettings().getMansionSeparation();
      int lvt_9_1_ = p_211744_3_ + lvt_7_1_ * p_211744_5_;
      int lvt_10_1_ = p_211744_4_ + lvt_7_1_ * p_211744_6_;
      int lvt_11_1_ = lvt_9_1_ < 0 ? lvt_9_1_ - lvt_7_1_ + 1 : lvt_9_1_;
      int lvt_12_1_ = lvt_10_1_ < 0 ? lvt_10_1_ - lvt_7_1_ + 1 : lvt_10_1_;
      int lvt_13_1_ = lvt_11_1_ / lvt_7_1_;
      int lvt_14_1_ = lvt_12_1_ / lvt_7_1_;
      ((SharedSeedRandom)p_211744_2_).setLargeFeatureSeedWithSalt(p_211744_1_.getSeed(), lvt_13_1_, lvt_14_1_, 10387319);
      lvt_13_1_ *= lvt_7_1_;
      lvt_14_1_ *= lvt_7_1_;
      lvt_13_1_ += (p_211744_2_.nextInt(lvt_7_1_ - lvt_8_1_) + p_211744_2_.nextInt(lvt_7_1_ - lvt_8_1_)) / 2;
      lvt_14_1_ += (p_211744_2_.nextInt(lvt_7_1_ - lvt_8_1_) + p_211744_2_.nextInt(lvt_7_1_ - lvt_8_1_)) / 2;
      return new ChunkPos(lvt_13_1_, lvt_14_1_);
   }

   public boolean func_225558_a_(BiomeManager p_225558_1_, ChunkGenerator<?> p_225558_2_, Random p_225558_3_, int p_225558_4_, int p_225558_5_, Biome p_225558_6_) {
      ChunkPos lvt_7_1_ = this.getStartPositionForPosition(p_225558_2_, p_225558_3_, p_225558_4_, p_225558_5_, 0, 0);
      if (p_225558_4_ == lvt_7_1_.x && p_225558_5_ == lvt_7_1_.z) {
         Set<Biome> lvt_8_1_ = p_225558_2_.getBiomeProvider().func_225530_a_(p_225558_4_ * 16 + 9, p_225558_2_.getSeaLevel(), p_225558_5_ * 16 + 9, 32);
         Iterator var9 = lvt_8_1_.iterator();

         Biome lvt_10_1_;
         do {
            if (!var9.hasNext()) {
               return true;
            }

            lvt_10_1_ = (Biome)var9.next();
         } while(p_225558_2_.hasStructure(lvt_10_1_, this));

         return false;
      } else {
         return false;
      }
   }

   public Structure.IStartFactory getStartFactory() {
      return WoodlandMansionStructure.Start::new;
   }

   public String getStructureName() {
      return "Mansion";
   }

   public int getSize() {
      return 8;
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225823_1_, int p_i225823_2_, int p_i225823_3_, MutableBoundingBox p_i225823_4_, int p_i225823_5_, long p_i225823_6_) {
         super(p_i225823_1_, p_i225823_2_, p_i225823_3_, p_i225823_4_, p_i225823_5_, p_i225823_6_);
      }

      public void init(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         Rotation lvt_6_1_ = Rotation.values()[this.rand.nextInt(Rotation.values().length)];
         int lvt_7_1_ = 5;
         int lvt_8_1_ = 5;
         if (lvt_6_1_ == Rotation.CLOCKWISE_90) {
            lvt_7_1_ = -5;
         } else if (lvt_6_1_ == Rotation.CLOCKWISE_180) {
            lvt_7_1_ = -5;
            lvt_8_1_ = -5;
         } else if (lvt_6_1_ == Rotation.COUNTERCLOCKWISE_90) {
            lvt_8_1_ = -5;
         }

         int lvt_9_1_ = (p_214625_3_ << 4) + 7;
         int lvt_10_1_ = (p_214625_4_ << 4) + 7;
         int lvt_11_1_ = p_214625_1_.func_222531_c(lvt_9_1_, lvt_10_1_, Heightmap.Type.WORLD_SURFACE_WG);
         int lvt_12_1_ = p_214625_1_.func_222531_c(lvt_9_1_, lvt_10_1_ + lvt_8_1_, Heightmap.Type.WORLD_SURFACE_WG);
         int lvt_13_1_ = p_214625_1_.func_222531_c(lvt_9_1_ + lvt_7_1_, lvt_10_1_, Heightmap.Type.WORLD_SURFACE_WG);
         int lvt_14_1_ = p_214625_1_.func_222531_c(lvt_9_1_ + lvt_7_1_, lvt_10_1_ + lvt_8_1_, Heightmap.Type.WORLD_SURFACE_WG);
         int lvt_15_1_ = Math.min(Math.min(lvt_11_1_, lvt_12_1_), Math.min(lvt_13_1_, lvt_14_1_));
         if (lvt_15_1_ >= 60) {
            BlockPos lvt_16_1_ = new BlockPos(p_214625_3_ * 16 + 8, lvt_15_1_ + 1, p_214625_4_ * 16 + 8);
            List<WoodlandMansionPieces.MansionTemplate> lvt_17_1_ = Lists.newLinkedList();
            WoodlandMansionPieces.generateMansion(p_214625_2_, lvt_16_1_, lvt_6_1_, lvt_17_1_, this.rand);
            this.components.addAll(lvt_17_1_);
            this.recalculateStructureSize();
         }
      }

      public void func_225565_a_(IWorld p_225565_1_, ChunkGenerator<?> p_225565_2_, Random p_225565_3_, MutableBoundingBox p_225565_4_, ChunkPos p_225565_5_) {
         super.func_225565_a_(p_225565_1_, p_225565_2_, p_225565_3_, p_225565_4_, p_225565_5_);
         int lvt_6_1_ = this.bounds.minY;

         for(int lvt_7_1_ = p_225565_4_.minX; lvt_7_1_ <= p_225565_4_.maxX; ++lvt_7_1_) {
            for(int lvt_8_1_ = p_225565_4_.minZ; lvt_8_1_ <= p_225565_4_.maxZ; ++lvt_8_1_) {
               BlockPos lvt_9_1_ = new BlockPos(lvt_7_1_, lvt_6_1_, lvt_8_1_);
               if (!p_225565_1_.isAirBlock(lvt_9_1_) && this.bounds.isVecInside(lvt_9_1_)) {
                  boolean lvt_10_1_ = false;
                  Iterator var11 = this.components.iterator();

                  while(var11.hasNext()) {
                     StructurePiece lvt_12_1_ = (StructurePiece)var11.next();
                     if (lvt_12_1_.getBoundingBox().isVecInside(lvt_9_1_)) {
                        lvt_10_1_ = true;
                        break;
                     }
                  }

                  if (lvt_10_1_) {
                     for(int lvt_11_1_ = lvt_6_1_ - 1; lvt_11_1_ > 1; --lvt_11_1_) {
                        BlockPos lvt_12_2_ = new BlockPos(lvt_7_1_, lvt_11_1_, lvt_8_1_);
                        if (!p_225565_1_.isAirBlock(lvt_12_2_) && !p_225565_1_.getBlockState(lvt_12_2_).getMaterial().isLiquid()) {
                           break;
                        }

                        p_225565_1_.setBlockState(lvt_12_2_, Blocks.COBBLESTONE.getDefaultState(), 2);
                     }
                  }
               }
            }
         }

      }
   }
}
