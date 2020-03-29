package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.NibbleArray;

public class ChunkLoaderUtil {
   public static ChunkLoaderUtil.AnvilConverterData load(CompoundNBT p_76691_0_) {
      int lvt_1_1_ = p_76691_0_.getInt("xPos");
      int lvt_2_1_ = p_76691_0_.getInt("zPos");
      ChunkLoaderUtil.AnvilConverterData lvt_3_1_ = new ChunkLoaderUtil.AnvilConverterData(lvt_1_1_, lvt_2_1_);
      lvt_3_1_.blocks = p_76691_0_.getByteArray("Blocks");
      lvt_3_1_.data = new NibbleArrayReader(p_76691_0_.getByteArray("Data"), 7);
      lvt_3_1_.skyLight = new NibbleArrayReader(p_76691_0_.getByteArray("SkyLight"), 7);
      lvt_3_1_.blockLight = new NibbleArrayReader(p_76691_0_.getByteArray("BlockLight"), 7);
      lvt_3_1_.heightmap = p_76691_0_.getByteArray("HeightMap");
      lvt_3_1_.terrainPopulated = p_76691_0_.getBoolean("TerrainPopulated");
      lvt_3_1_.field_76702_h = p_76691_0_.getList("Entities", 10);
      lvt_3_1_.tileEntities = p_76691_0_.getList("TileEntities", 10);
      lvt_3_1_.tileTicks = p_76691_0_.getList("TileTicks", 10);

      try {
         lvt_3_1_.lastUpdated = p_76691_0_.getLong("LastUpdate");
      } catch (ClassCastException var5) {
         lvt_3_1_.lastUpdated = (long)p_76691_0_.getInt("LastUpdate");
      }

      return lvt_3_1_;
   }

   public static void convertToAnvilFormat(ChunkLoaderUtil.AnvilConverterData p_76690_0_, CompoundNBT p_76690_1_, BiomeProvider p_76690_2_) {
      p_76690_1_.putInt("xPos", p_76690_0_.x);
      p_76690_1_.putInt("zPos", p_76690_0_.z);
      p_76690_1_.putLong("LastUpdate", p_76690_0_.lastUpdated);
      int[] lvt_3_1_ = new int[p_76690_0_.heightmap.length];

      for(int lvt_4_1_ = 0; lvt_4_1_ < p_76690_0_.heightmap.length; ++lvt_4_1_) {
         lvt_3_1_[lvt_4_1_] = p_76690_0_.heightmap[lvt_4_1_];
      }

      p_76690_1_.putIntArray("HeightMap", lvt_3_1_);
      p_76690_1_.putBoolean("TerrainPopulated", p_76690_0_.terrainPopulated);
      ListNBT lvt_4_2_ = new ListNBT();

      for(int lvt_5_1_ = 0; lvt_5_1_ < 8; ++lvt_5_1_) {
         boolean lvt_6_1_ = true;

         for(int lvt_7_1_ = 0; lvt_7_1_ < 16 && lvt_6_1_; ++lvt_7_1_) {
            for(int lvt_8_1_ = 0; lvt_8_1_ < 16 && lvt_6_1_; ++lvt_8_1_) {
               for(int lvt_9_1_ = 0; lvt_9_1_ < 16; ++lvt_9_1_) {
                  int lvt_10_1_ = lvt_7_1_ << 11 | lvt_9_1_ << 7 | lvt_8_1_ + (lvt_5_1_ << 4);
                  int lvt_11_1_ = p_76690_0_.blocks[lvt_10_1_];
                  if (lvt_11_1_ != 0) {
                     lvt_6_1_ = false;
                     break;
                  }
               }
            }
         }

         if (!lvt_6_1_) {
            byte[] lvt_7_2_ = new byte[4096];
            NibbleArray lvt_8_2_ = new NibbleArray();
            NibbleArray lvt_9_2_ = new NibbleArray();
            NibbleArray lvt_10_2_ = new NibbleArray();

            for(int lvt_11_2_ = 0; lvt_11_2_ < 16; ++lvt_11_2_) {
               for(int lvt_12_1_ = 0; lvt_12_1_ < 16; ++lvt_12_1_) {
                  for(int lvt_13_1_ = 0; lvt_13_1_ < 16; ++lvt_13_1_) {
                     int lvt_14_1_ = lvt_11_2_ << 11 | lvt_13_1_ << 7 | lvt_12_1_ + (lvt_5_1_ << 4);
                     int lvt_15_1_ = p_76690_0_.blocks[lvt_14_1_];
                     lvt_7_2_[lvt_12_1_ << 8 | lvt_13_1_ << 4 | lvt_11_2_] = (byte)(lvt_15_1_ & 255);
                     lvt_8_2_.set(lvt_11_2_, lvt_12_1_, lvt_13_1_, p_76690_0_.data.get(lvt_11_2_, lvt_12_1_ + (lvt_5_1_ << 4), lvt_13_1_));
                     lvt_9_2_.set(lvt_11_2_, lvt_12_1_, lvt_13_1_, p_76690_0_.skyLight.get(lvt_11_2_, lvt_12_1_ + (lvt_5_1_ << 4), lvt_13_1_));
                     lvt_10_2_.set(lvt_11_2_, lvt_12_1_, lvt_13_1_, p_76690_0_.blockLight.get(lvt_11_2_, lvt_12_1_ + (lvt_5_1_ << 4), lvt_13_1_));
                  }
               }
            }

            CompoundNBT lvt_11_3_ = new CompoundNBT();
            lvt_11_3_.putByte("Y", (byte)(lvt_5_1_ & 255));
            lvt_11_3_.putByteArray("Blocks", lvt_7_2_);
            lvt_11_3_.putByteArray("Data", lvt_8_2_.getData());
            lvt_11_3_.putByteArray("SkyLight", lvt_9_2_.getData());
            lvt_11_3_.putByteArray("BlockLight", lvt_10_2_.getData());
            lvt_4_2_.add(lvt_11_3_);
         }
      }

      p_76690_1_.put("Sections", lvt_4_2_);
      p_76690_1_.putIntArray("Biomes", (new BiomeContainer(new ChunkPos(p_76690_0_.x, p_76690_0_.z), p_76690_2_)).func_227055_a_());
      p_76690_1_.put("Entities", p_76690_0_.field_76702_h);
      p_76690_1_.put("TileEntities", p_76690_0_.tileEntities);
      if (p_76690_0_.tileTicks != null) {
         p_76690_1_.put("TileTicks", p_76690_0_.tileTicks);
      }

      p_76690_1_.putBoolean("convertedFromAlphaFormat", true);
   }

   public static class AnvilConverterData {
      public long lastUpdated;
      public boolean terrainPopulated;
      public byte[] heightmap;
      public NibbleArrayReader blockLight;
      public NibbleArrayReader skyLight;
      public NibbleArrayReader data;
      public byte[] blocks;
      public ListNBT field_76702_h;
      public ListNBT tileEntities;
      public ListNBT tileTicks;
      public final int x;
      public final int z;

      public AnvilConverterData(int p_i1999_1_, int p_i1999_2_) {
         this.x = p_i1999_1_;
         this.z = p_i1999_2_;
      }
   }
}
