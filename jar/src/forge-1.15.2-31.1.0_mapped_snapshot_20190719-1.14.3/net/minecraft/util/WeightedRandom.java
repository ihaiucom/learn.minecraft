package net.minecraft.util;

import java.util.List;
import java.util.Random;

public class WeightedRandom {
   public static int getTotalWeight(List<? extends WeightedRandom.Item> p_76272_0_) {
      int lvt_1_1_ = 0;
      int lvt_2_1_ = 0;

      for(int lvt_3_1_ = p_76272_0_.size(); lvt_2_1_ < lvt_3_1_; ++lvt_2_1_) {
         WeightedRandom.Item lvt_4_1_ = (WeightedRandom.Item)p_76272_0_.get(lvt_2_1_);
         lvt_1_1_ += lvt_4_1_.itemWeight;
      }

      return lvt_1_1_;
   }

   public static <T extends WeightedRandom.Item> T getRandomItem(Random p_76273_0_, List<T> p_76273_1_, int p_76273_2_) {
      if (p_76273_2_ <= 0) {
         throw (IllegalArgumentException)Util.func_229757_c_(new IllegalArgumentException());
      } else {
         int lvt_3_1_ = p_76273_0_.nextInt(p_76273_2_);
         return getRandomItem(p_76273_1_, lvt_3_1_);
      }
   }

   public static <T extends WeightedRandom.Item> T getRandomItem(List<T> p_180166_0_, int p_180166_1_) {
      int lvt_2_1_ = 0;

      for(int lvt_3_1_ = p_180166_0_.size(); lvt_2_1_ < lvt_3_1_; ++lvt_2_1_) {
         T lvt_4_1_ = (WeightedRandom.Item)p_180166_0_.get(lvt_2_1_);
         p_180166_1_ -= lvt_4_1_.itemWeight;
         if (p_180166_1_ < 0) {
            return lvt_4_1_;
         }
      }

      return null;
   }

   public static <T extends WeightedRandom.Item> T getRandomItem(Random p_76271_0_, List<T> p_76271_1_) {
      return getRandomItem(p_76271_0_, p_76271_1_, getTotalWeight(p_76271_1_));
   }

   public static class Item {
      public final int itemWeight;

      public Item(int p_i1556_1_) {
         this.itemWeight = p_i1556_1_;
      }
   }
}
