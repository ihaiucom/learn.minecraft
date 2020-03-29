package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IStructureReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Structure<C extends IFeatureConfig> extends Feature<C> {
   private static final Logger LOGGER = LogManager.getLogger();

   public Structure(Function<Dynamic<?>, ? extends C> p_i51427_1_) {
      super(p_i51427_1_);
   }

   public ConfiguredFeature<C, ? extends Structure<C>> func_225566_b_(C p_225566_1_) {
      return new ConfiguredFeature(this, p_225566_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, C p_212245_5_) {
      if (!p_212245_1_.getWorldInfo().isMapFeaturesEnabled()) {
         return false;
      } else {
         int lvt_6_1_ = p_212245_4_.getX() >> 4;
         int lvt_7_1_ = p_212245_4_.getZ() >> 4;
         int lvt_8_1_ = lvt_6_1_ << 4;
         int lvt_9_1_ = lvt_7_1_ << 4;
         boolean lvt_10_1_ = false;
         LongIterator var11 = p_212245_1_.getChunk(lvt_6_1_, lvt_7_1_).getStructureReferences(this.getStructureName()).iterator();

         while(var11.hasNext()) {
            Long lvt_12_1_ = (Long)var11.next();
            ChunkPos lvt_13_1_ = new ChunkPos(lvt_12_1_);
            StructureStart lvt_14_1_ = p_212245_1_.getChunk(lvt_13_1_.x, lvt_13_1_.z).getStructureStart(this.getStructureName());
            if (lvt_14_1_ != null && lvt_14_1_ != StructureStart.DUMMY) {
               lvt_14_1_.func_225565_a_(p_212245_1_, p_212245_2_, p_212245_3_, new MutableBoundingBox(lvt_8_1_, lvt_9_1_, lvt_8_1_ + 15, lvt_9_1_ + 15), new ChunkPos(lvt_6_1_, lvt_7_1_));
               lvt_10_1_ = true;
            }
         }

         return lvt_10_1_;
      }
   }

   protected StructureStart getStart(IWorld p_202364_1_, BlockPos p_202364_2_, boolean p_202364_3_) {
      List<StructureStart> lvt_4_1_ = this.getStarts(p_202364_1_, p_202364_2_.getX() >> 4, p_202364_2_.getZ() >> 4);
      Iterator var5 = lvt_4_1_.iterator();

      while(true) {
         StructureStart lvt_6_1_;
         do {
            do {
               if (!var5.hasNext()) {
                  return StructureStart.DUMMY;
               }

               lvt_6_1_ = (StructureStart)var5.next();
            } while(!lvt_6_1_.isValid());
         } while(!lvt_6_1_.getBoundingBox().isVecInside(p_202364_2_));

         if (!p_202364_3_) {
            return lvt_6_1_;
         }

         Iterator var7 = lvt_6_1_.getComponents().iterator();

         while(var7.hasNext()) {
            StructurePiece lvt_8_1_ = (StructurePiece)var7.next();
            if (lvt_8_1_.getBoundingBox().isVecInside(p_202364_2_)) {
               return lvt_6_1_;
            }
         }
      }
   }

   public boolean isPositionInStructure(IWorld p_175796_1_, BlockPos p_175796_2_) {
      return this.getStart(p_175796_1_, p_175796_2_, false).isValid();
   }

   public boolean isPositionInsideStructure(IWorld p_202366_1_, BlockPos p_202366_2_) {
      return this.getStart(p_202366_1_, p_202366_2_, true).isValid();
   }

   @Nullable
   public BlockPos findNearest(World p_211405_1_, ChunkGenerator<? extends GenerationSettings> p_211405_2_, BlockPos p_211405_3_, int p_211405_4_, boolean p_211405_5_) {
      if (!p_211405_2_.getBiomeProvider().hasStructure(this)) {
         return null;
      } else {
         int lvt_6_1_ = p_211405_3_.getX() >> 4;
         int lvt_7_1_ = p_211405_3_.getZ() >> 4;
         int lvt_8_1_ = 0;

         for(SharedSeedRandom lvt_9_1_ = new SharedSeedRandom(); lvt_8_1_ <= p_211405_4_; ++lvt_8_1_) {
            for(int lvt_10_1_ = -lvt_8_1_; lvt_10_1_ <= lvt_8_1_; ++lvt_10_1_) {
               boolean lvt_11_1_ = lvt_10_1_ == -lvt_8_1_ || lvt_10_1_ == lvt_8_1_;

               for(int lvt_12_1_ = -lvt_8_1_; lvt_12_1_ <= lvt_8_1_; ++lvt_12_1_) {
                  boolean lvt_13_1_ = lvt_12_1_ == -lvt_8_1_ || lvt_12_1_ == lvt_8_1_;
                  if (lvt_11_1_ || lvt_13_1_) {
                     ChunkPos lvt_14_1_ = this.getStartPositionForPosition(p_211405_2_, lvt_9_1_, lvt_6_1_, lvt_7_1_, lvt_10_1_, lvt_12_1_);
                     StructureStart lvt_15_1_ = p_211405_1_.getChunk(lvt_14_1_.x, lvt_14_1_.z, ChunkStatus.STRUCTURE_STARTS).getStructureStart(this.getStructureName());
                     if (lvt_15_1_ != null && lvt_15_1_.isValid()) {
                        if (p_211405_5_ && lvt_15_1_.isRefCountBelowMax()) {
                           lvt_15_1_.incrementRefCount();
                           return lvt_15_1_.getPos();
                        }

                        if (!p_211405_5_) {
                           return lvt_15_1_.getPos();
                        }
                     }

                     if (lvt_8_1_ == 0) {
                        break;
                     }
                  }
               }

               if (lvt_8_1_ == 0) {
                  break;
               }
            }
         }

         return null;
      }
   }

   private List<StructureStart> getStarts(IWorld p_202371_1_, int p_202371_2_, int p_202371_3_) {
      List<StructureStart> lvt_4_1_ = Lists.newArrayList();
      IChunk lvt_5_1_ = p_202371_1_.getChunk(p_202371_2_, p_202371_3_, ChunkStatus.STRUCTURE_REFERENCES);
      LongIterator lvt_6_1_ = lvt_5_1_.getStructureReferences(this.getStructureName()).iterator();

      while(lvt_6_1_.hasNext()) {
         long lvt_7_1_ = lvt_6_1_.nextLong();
         IStructureReader lvt_9_1_ = p_202371_1_.getChunk(ChunkPos.getX(lvt_7_1_), ChunkPos.getZ(lvt_7_1_), ChunkStatus.STRUCTURE_STARTS);
         StructureStart lvt_10_1_ = lvt_9_1_.getStructureStart(this.getStructureName());
         if (lvt_10_1_ != null) {
            lvt_4_1_.add(lvt_10_1_);
         }
      }

      return lvt_4_1_;
   }

   protected ChunkPos getStartPositionForPosition(ChunkGenerator<?> p_211744_1_, Random p_211744_2_, int p_211744_3_, int p_211744_4_, int p_211744_5_, int p_211744_6_) {
      return new ChunkPos(p_211744_3_ + p_211744_5_, p_211744_4_ + p_211744_6_);
   }

   public abstract boolean func_225558_a_(BiomeManager var1, ChunkGenerator<?> var2, Random var3, int var4, int var5, Biome var6);

   public abstract Structure.IStartFactory getStartFactory();

   public abstract String getStructureName();

   public abstract int getSize();

   public interface IStartFactory {
      StructureStart create(Structure<?> var1, int var2, int var3, MutableBoundingBox var4, int var5, long var6);
   }
}
