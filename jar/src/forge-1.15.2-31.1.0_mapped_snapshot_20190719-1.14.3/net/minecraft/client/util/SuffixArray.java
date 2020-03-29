package net.minecraft.client.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SuffixArray<T> {
   private static final boolean DEBUG_PRINT_COMPARISONS = Boolean.parseBoolean(System.getProperty("SuffixArray.printComparisons", "false"));
   private static final boolean DEBUG_PRINT_ARRAY = Boolean.parseBoolean(System.getProperty("SuffixArray.printArray", "false"));
   private static final Logger LOGGER = LogManager.getLogger();
   protected final List<T> list = Lists.newArrayList();
   private final IntList chars = new IntArrayList();
   private final IntList wordStarts = new IntArrayList();
   private IntList suffixToT = new IntArrayList();
   private IntList offsets = new IntArrayList();
   private int maxStringLength;

   public void add(T p_194057_1_, String p_194057_2_) {
      this.maxStringLength = Math.max(this.maxStringLength, p_194057_2_.length());
      int lvt_3_1_ = this.list.size();
      this.list.add(p_194057_1_);
      this.wordStarts.add(this.chars.size());

      for(int lvt_4_1_ = 0; lvt_4_1_ < p_194057_2_.length(); ++lvt_4_1_) {
         this.suffixToT.add(lvt_3_1_);
         this.offsets.add(lvt_4_1_);
         this.chars.add(p_194057_2_.charAt(lvt_4_1_));
      }

      this.suffixToT.add(lvt_3_1_);
      this.offsets.add(p_194057_2_.length());
      this.chars.add(-1);
   }

   public void generate() {
      int lvt_1_1_ = this.chars.size();
      int[] lvt_2_1_ = new int[lvt_1_1_];
      final int[] lvt_3_1_ = new int[lvt_1_1_];
      final int[] lvt_4_1_ = new int[lvt_1_1_];
      int[] lvt_5_1_ = new int[lvt_1_1_];
      IntComparator lvt_6_1_ = new IntComparator() {
         public int compare(int p_compare_1_, int p_compare_2_) {
            return lvt_3_1_[p_compare_1_] == lvt_3_1_[p_compare_2_] ? Integer.compare(lvt_4_1_[p_compare_1_], lvt_4_1_[p_compare_2_]) : Integer.compare(lvt_3_1_[p_compare_1_], lvt_3_1_[p_compare_2_]);
         }

         public int compare(Integer p_compare_1_, Integer p_compare_2_) {
            return this.compare(p_compare_1_, p_compare_2_);
         }
      };
      Swapper lvt_7_1_ = (p_194054_3_, p_194054_4_) -> {
         if (p_194054_3_ != p_194054_4_) {
            int lvt_5_1_x = lvt_3_1_[p_194054_3_];
            lvt_3_1_[p_194054_3_] = lvt_3_1_[p_194054_4_];
            lvt_3_1_[p_194054_4_] = lvt_5_1_x;
            lvt_5_1_x = lvt_4_1_[p_194054_3_];
            lvt_4_1_[p_194054_3_] = lvt_4_1_[p_194054_4_];
            lvt_4_1_[p_194054_4_] = lvt_5_1_x;
            lvt_5_1_x = lvt_5_1_[p_194054_3_];
            lvt_5_1_[p_194054_3_] = lvt_5_1_[p_194054_4_];
            lvt_5_1_[p_194054_4_] = lvt_5_1_x;
         }

      };

      int lvt_8_2_;
      for(lvt_8_2_ = 0; lvt_8_2_ < lvt_1_1_; ++lvt_8_2_) {
         lvt_2_1_[lvt_8_2_] = this.chars.getInt(lvt_8_2_);
      }

      lvt_8_2_ = 1;

      for(int lvt_9_1_ = Math.min(lvt_1_1_, this.maxStringLength); lvt_8_2_ * 2 < lvt_9_1_; lvt_8_2_ *= 2) {
         int lvt_10_2_;
         for(lvt_10_2_ = 0; lvt_10_2_ < lvt_1_1_; lvt_5_1_[lvt_10_2_] = lvt_10_2_++) {
            lvt_3_1_[lvt_10_2_] = lvt_2_1_[lvt_10_2_];
            lvt_4_1_[lvt_10_2_] = lvt_10_2_ + lvt_8_2_ < lvt_1_1_ ? lvt_2_1_[lvt_10_2_ + lvt_8_2_] : -2;
         }

         Arrays.quickSort(0, lvt_1_1_, lvt_6_1_, lvt_7_1_);

         for(lvt_10_2_ = 0; lvt_10_2_ < lvt_1_1_; ++lvt_10_2_) {
            if (lvt_10_2_ > 0 && lvt_3_1_[lvt_10_2_] == lvt_3_1_[lvt_10_2_ - 1] && lvt_4_1_[lvt_10_2_] == lvt_4_1_[lvt_10_2_ - 1]) {
               lvt_2_1_[lvt_5_1_[lvt_10_2_]] = lvt_2_1_[lvt_5_1_[lvt_10_2_ - 1]];
            } else {
               lvt_2_1_[lvt_5_1_[lvt_10_2_]] = lvt_10_2_;
            }
         }
      }

      IntList lvt_10_3_ = this.suffixToT;
      IntList lvt_11_1_ = this.offsets;
      this.suffixToT = new IntArrayList(lvt_10_3_.size());
      this.offsets = new IntArrayList(lvt_11_1_.size());

      for(int lvt_12_1_ = 0; lvt_12_1_ < lvt_1_1_; ++lvt_12_1_) {
         int lvt_13_1_ = lvt_5_1_[lvt_12_1_];
         this.suffixToT.add(lvt_10_3_.getInt(lvt_13_1_));
         this.offsets.add(lvt_11_1_.getInt(lvt_13_1_));
      }

      if (DEBUG_PRINT_ARRAY) {
         this.printArray();
      }

   }

   private void printArray() {
      for(int lvt_1_1_ = 0; lvt_1_1_ < this.suffixToT.size(); ++lvt_1_1_) {
         LOGGER.debug("{} {}", lvt_1_1_, this.getString(lvt_1_1_));
      }

      LOGGER.debug("");
   }

   private String getString(int p_194059_1_) {
      int lvt_2_1_ = this.offsets.getInt(p_194059_1_);
      int lvt_3_1_ = this.wordStarts.getInt(this.suffixToT.getInt(p_194059_1_));
      StringBuilder lvt_4_1_ = new StringBuilder();

      for(int lvt_5_1_ = 0; lvt_3_1_ + lvt_5_1_ < this.chars.size(); ++lvt_5_1_) {
         if (lvt_5_1_ == lvt_2_1_) {
            lvt_4_1_.append('^');
         }

         int lvt_6_1_ = this.chars.get(lvt_3_1_ + lvt_5_1_);
         if (lvt_6_1_ == -1) {
            break;
         }

         lvt_4_1_.append((char)lvt_6_1_);
      }

      return lvt_4_1_.toString();
   }

   private int compare(String p_194056_1_, int p_194056_2_) {
      int lvt_3_1_ = this.wordStarts.getInt(this.suffixToT.getInt(p_194056_2_));
      int lvt_4_1_ = this.offsets.getInt(p_194056_2_);

      for(int lvt_5_1_ = 0; lvt_5_1_ < p_194056_1_.length(); ++lvt_5_1_) {
         int lvt_6_1_ = this.chars.getInt(lvt_3_1_ + lvt_4_1_ + lvt_5_1_);
         if (lvt_6_1_ == -1) {
            return 1;
         }

         char lvt_7_1_ = p_194056_1_.charAt(lvt_5_1_);
         char lvt_8_1_ = (char)lvt_6_1_;
         if (lvt_7_1_ < lvt_8_1_) {
            return -1;
         }

         if (lvt_7_1_ > lvt_8_1_) {
            return 1;
         }
      }

      return 0;
   }

   public List<T> search(String p_194055_1_) {
      int lvt_2_1_ = this.suffixToT.size();
      int lvt_3_1_ = 0;
      int lvt_4_1_ = lvt_2_1_;

      int lvt_5_2_;
      int lvt_6_2_;
      while(lvt_3_1_ < lvt_4_1_) {
         lvt_5_2_ = lvt_3_1_ + (lvt_4_1_ - lvt_3_1_) / 2;
         lvt_6_2_ = this.compare(p_194055_1_, lvt_5_2_);
         if (DEBUG_PRINT_COMPARISONS) {
            LOGGER.debug("comparing lower \"{}\" with {} \"{}\": {}", p_194055_1_, lvt_5_2_, this.getString(lvt_5_2_), lvt_6_2_);
         }

         if (lvt_6_2_ > 0) {
            lvt_3_1_ = lvt_5_2_ + 1;
         } else {
            lvt_4_1_ = lvt_5_2_;
         }
      }

      if (lvt_3_1_ >= 0 && lvt_3_1_ < lvt_2_1_) {
         lvt_5_2_ = lvt_3_1_;
         lvt_4_1_ = lvt_2_1_;

         while(lvt_3_1_ < lvt_4_1_) {
            lvt_6_2_ = lvt_3_1_ + (lvt_4_1_ - lvt_3_1_) / 2;
            int lvt_7_1_ = this.compare(p_194055_1_, lvt_6_2_);
            if (DEBUG_PRINT_COMPARISONS) {
               LOGGER.debug("comparing upper \"{}\" with {} \"{}\": {}", p_194055_1_, lvt_6_2_, this.getString(lvt_6_2_), lvt_7_1_);
            }

            if (lvt_7_1_ >= 0) {
               lvt_3_1_ = lvt_6_2_ + 1;
            } else {
               lvt_4_1_ = lvt_6_2_;
            }
         }

         lvt_6_2_ = lvt_3_1_;
         IntSet lvt_7_2_ = new IntOpenHashSet();

         for(int lvt_8_1_ = lvt_5_2_; lvt_8_1_ < lvt_6_2_; ++lvt_8_1_) {
            lvt_7_2_.add(this.suffixToT.getInt(lvt_8_1_));
         }

         int[] lvt_8_2_ = lvt_7_2_.toIntArray();
         java.util.Arrays.sort(lvt_8_2_);
         Set<T> lvt_9_1_ = Sets.newLinkedHashSet();
         int[] var10 = lvt_8_2_;
         int var11 = lvt_8_2_.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            int lvt_13_1_ = var10[var12];
            lvt_9_1_.add(this.list.get(lvt_13_1_));
         }

         return Lists.newArrayList(lvt_9_1_);
      } else {
         return Collections.emptyList();
      }
   }
}
