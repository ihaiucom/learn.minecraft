package net.minecraft.util;

import java.util.Random;

public class SharedSeedRandom extends Random {
   private int usageCount;

   public SharedSeedRandom() {
   }

   public SharedSeedRandom(long p_i48691_1_) {
      super(p_i48691_1_);
   }

   public void skip(int p_202423_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < p_202423_1_; ++lvt_2_1_) {
         this.next(1);
      }

   }

   protected int next(int p_next_1_) {
      ++this.usageCount;
      return super.next(p_next_1_);
   }

   public long setBaseChunkSeed(int p_202422_1_, int p_202422_2_) {
      long lvt_3_1_ = (long)p_202422_1_ * 341873128712L + (long)p_202422_2_ * 132897987541L;
      this.setSeed(lvt_3_1_);
      return lvt_3_1_;
   }

   public long setDecorationSeed(long p_202424_1_, int p_202424_3_, int p_202424_4_) {
      this.setSeed(p_202424_1_);
      long lvt_5_1_ = this.nextLong() | 1L;
      long lvt_7_1_ = this.nextLong() | 1L;
      long lvt_9_1_ = (long)p_202424_3_ * lvt_5_1_ + (long)p_202424_4_ * lvt_7_1_ ^ p_202424_1_;
      this.setSeed(lvt_9_1_);
      return lvt_9_1_;
   }

   public long setFeatureSeed(long p_202426_1_, int p_202426_3_, int p_202426_4_) {
      long lvt_5_1_ = p_202426_1_ + (long)p_202426_3_ + (long)(10000 * p_202426_4_);
      this.setSeed(lvt_5_1_);
      return lvt_5_1_;
   }

   public long setLargeFeatureSeed(long p_202425_1_, int p_202425_3_, int p_202425_4_) {
      this.setSeed(p_202425_1_);
      long lvt_5_1_ = this.nextLong();
      long lvt_7_1_ = this.nextLong();
      long lvt_9_1_ = (long)p_202425_3_ * lvt_5_1_ ^ (long)p_202425_4_ * lvt_7_1_ ^ p_202425_1_;
      this.setSeed(lvt_9_1_);
      return lvt_9_1_;
   }

   public long setLargeFeatureSeedWithSalt(long p_202427_1_, int p_202427_3_, int p_202427_4_, int p_202427_5_) {
      long lvt_6_1_ = (long)p_202427_3_ * 341873128712L + (long)p_202427_4_ * 132897987541L + p_202427_1_ + (long)p_202427_5_;
      this.setSeed(lvt_6_1_);
      return lvt_6_1_;
   }

   public static Random seedSlimeChunk(int p_205190_0_, int p_205190_1_, long p_205190_2_, long p_205190_4_) {
      return new Random(p_205190_2_ + (long)(p_205190_0_ * p_205190_0_ * 4987142) + (long)(p_205190_0_ * 5947611) + (long)(p_205190_1_ * p_205190_1_) * 4392871L + (long)(p_205190_1_ * 389711) ^ p_205190_4_);
   }
}
