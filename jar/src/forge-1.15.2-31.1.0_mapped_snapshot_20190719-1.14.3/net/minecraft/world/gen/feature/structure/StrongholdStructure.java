package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class StrongholdStructure extends Structure<NoFeatureConfig> {
   private boolean ranBiomeCheck;
   private ChunkPos[] structureCoords;
   private final List<StructureStart> field_214561_aT = Lists.newArrayList();
   private long seed;

   public StrongholdStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51428_1_) {
      super(p_i51428_1_);
   }

   public boolean func_225558_a_(BiomeManager p_225558_1_, ChunkGenerator<?> p_225558_2_, Random p_225558_3_, int p_225558_4_, int p_225558_5_, Biome p_225558_6_) {
      if (this.seed != p_225558_2_.getSeed()) {
         this.resetData();
      }

      if (!this.ranBiomeCheck) {
         this.reinitializeData(p_225558_2_);
         this.ranBiomeCheck = true;
      }

      ChunkPos[] var7 = this.structureCoords;
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         ChunkPos lvt_10_1_ = var7[var9];
         if (p_225558_4_ == lvt_10_1_.x && p_225558_5_ == lvt_10_1_.z) {
            return true;
         }
      }

      return false;
   }

   private void resetData() {
      this.ranBiomeCheck = false;
      this.structureCoords = null;
      this.field_214561_aT.clear();
   }

   public Structure.IStartFactory getStartFactory() {
      return StrongholdStructure.Start::new;
   }

   public String getStructureName() {
      return "Stronghold";
   }

   public int getSize() {
      return 8;
   }

   @Nullable
   public BlockPos findNearest(World p_211405_1_, ChunkGenerator<? extends GenerationSettings> p_211405_2_, BlockPos p_211405_3_, int p_211405_4_, boolean p_211405_5_) {
      if (!p_211405_2_.getBiomeProvider().hasStructure(this)) {
         return null;
      } else {
         if (this.seed != p_211405_1_.getSeed()) {
            this.resetData();
         }

         if (!this.ranBiomeCheck) {
            this.reinitializeData(p_211405_2_);
            this.ranBiomeCheck = true;
         }

         BlockPos lvt_6_1_ = null;
         BlockPos.Mutable lvt_7_1_ = new BlockPos.Mutable();
         double lvt_8_1_ = Double.MAX_VALUE;
         ChunkPos[] var10 = this.structureCoords;
         int var11 = var10.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            ChunkPos lvt_13_1_ = var10[var12];
            lvt_7_1_.setPos((lvt_13_1_.x << 4) + 8, 32, (lvt_13_1_.z << 4) + 8);
            double lvt_14_1_ = lvt_7_1_.distanceSq(p_211405_3_);
            if (lvt_6_1_ == null) {
               lvt_6_1_ = new BlockPos(lvt_7_1_);
               lvt_8_1_ = lvt_14_1_;
            } else if (lvt_14_1_ < lvt_8_1_) {
               lvt_6_1_ = new BlockPos(lvt_7_1_);
               lvt_8_1_ = lvt_14_1_;
            }
         }

         return lvt_6_1_;
      }
   }

   private void reinitializeData(ChunkGenerator<?> p_202385_1_) {
      this.seed = p_202385_1_.getSeed();
      List<Biome> lvt_2_1_ = Lists.newArrayList();
      Iterator var3 = Registry.BIOME.iterator();

      while(var3.hasNext()) {
         Biome lvt_4_1_ = (Biome)var3.next();
         if (lvt_4_1_ != null && p_202385_1_.hasStructure(lvt_4_1_, this)) {
            lvt_2_1_.add(lvt_4_1_);
         }
      }

      int lvt_3_1_ = p_202385_1_.getSettings().getStrongholdDistance();
      int lvt_4_2_ = p_202385_1_.getSettings().getStrongholdCount();
      int lvt_5_1_ = p_202385_1_.getSettings().getStrongholdSpread();
      this.structureCoords = new ChunkPos[lvt_4_2_];
      int lvt_6_1_ = 0;
      Iterator var7 = this.field_214561_aT.iterator();

      while(var7.hasNext()) {
         StructureStart lvt_8_1_ = (StructureStart)var7.next();
         if (lvt_6_1_ < this.structureCoords.length) {
            this.structureCoords[lvt_6_1_++] = new ChunkPos(lvt_8_1_.getChunkPosX(), lvt_8_1_.getChunkPosZ());
         }
      }

      Random lvt_7_1_ = new Random();
      lvt_7_1_.setSeed(p_202385_1_.getSeed());
      double lvt_8_2_ = lvt_7_1_.nextDouble() * 3.141592653589793D * 2.0D;
      int lvt_10_1_ = lvt_6_1_;
      if (lvt_6_1_ < this.structureCoords.length) {
         int lvt_11_1_ = 0;
         int lvt_12_1_ = 0;

         for(int lvt_13_1_ = 0; lvt_13_1_ < this.structureCoords.length; ++lvt_13_1_) {
            double lvt_14_1_ = (double)(4 * lvt_3_1_ + lvt_3_1_ * lvt_12_1_ * 6) + (lvt_7_1_.nextDouble() - 0.5D) * (double)lvt_3_1_ * 2.5D;
            int lvt_16_1_ = (int)Math.round(Math.cos(lvt_8_2_) * lvt_14_1_);
            int lvt_17_1_ = (int)Math.round(Math.sin(lvt_8_2_) * lvt_14_1_);
            BlockPos lvt_18_1_ = p_202385_1_.getBiomeProvider().func_225531_a_((lvt_16_1_ << 4) + 8, p_202385_1_.getSeaLevel(), (lvt_17_1_ << 4) + 8, 112, lvt_2_1_, lvt_7_1_);
            if (lvt_18_1_ != null) {
               lvt_16_1_ = lvt_18_1_.getX() >> 4;
               lvt_17_1_ = lvt_18_1_.getZ() >> 4;
            }

            if (lvt_13_1_ >= lvt_10_1_) {
               this.structureCoords[lvt_13_1_] = new ChunkPos(lvt_16_1_, lvt_17_1_);
            }

            lvt_8_2_ += 6.283185307179586D / (double)lvt_5_1_;
            ++lvt_11_1_;
            if (lvt_11_1_ == lvt_5_1_) {
               ++lvt_12_1_;
               lvt_11_1_ = 0;
               lvt_5_1_ += 2 * lvt_5_1_ / (lvt_12_1_ + 1);
               lvt_5_1_ = Math.min(lvt_5_1_, this.structureCoords.length - lvt_13_1_);
               lvt_8_2_ += lvt_7_1_.nextDouble() * 3.141592653589793D * 2.0D;
            }
         }
      }

   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225818_1_, int p_i225818_2_, int p_i225818_3_, MutableBoundingBox p_i225818_4_, int p_i225818_5_, long p_i225818_6_) {
         super(p_i225818_1_, p_i225818_2_, p_i225818_3_, p_i225818_4_, p_i225818_5_, p_i225818_6_);
      }

      public void init(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         int lvt_6_1_ = 0;
         long lvt_7_1_ = p_214625_1_.getSeed();

         StrongholdPieces.Stairs2 lvt_9_1_;
         do {
            this.components.clear();
            this.bounds = MutableBoundingBox.getNewBoundingBox();
            this.rand.setLargeFeatureSeed(lvt_7_1_ + (long)(lvt_6_1_++), p_214625_3_, p_214625_4_);
            StrongholdPieces.prepareStructurePieces();
            lvt_9_1_ = new StrongholdPieces.Stairs2(this.rand, (p_214625_3_ << 4) + 2, (p_214625_4_ << 4) + 2);
            this.components.add(lvt_9_1_);
            lvt_9_1_.buildComponent(lvt_9_1_, this.components, this.rand);
            List lvt_10_1_ = lvt_9_1_.pendingChildren;

            while(!lvt_10_1_.isEmpty()) {
               int lvt_11_1_ = this.rand.nextInt(lvt_10_1_.size());
               StructurePiece lvt_12_1_ = (StructurePiece)lvt_10_1_.remove(lvt_11_1_);
               lvt_12_1_.buildComponent(lvt_9_1_, this.components, this.rand);
            }

            this.recalculateStructureSize();
            this.func_214628_a(p_214625_1_.getSeaLevel(), this.rand, 10);
         } while(this.components.isEmpty() || lvt_9_1_.strongholdPortalRoom == null);

         ((StrongholdStructure)this.getStructure()).field_214561_aT.add(this);
      }
   }
}
