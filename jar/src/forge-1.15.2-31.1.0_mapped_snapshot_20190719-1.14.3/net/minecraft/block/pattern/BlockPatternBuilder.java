package net.minecraft.block.pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.util.CachedBlockInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class BlockPatternBuilder {
   private static final Joiner COMMA_JOIN = Joiner.on(",");
   private final List<String[]> depth = Lists.newArrayList();
   private final Map<Character, Predicate<CachedBlockInfo>> symbolMap = Maps.newHashMap();
   private int aisleHeight;
   private int rowWidth;

   private BlockPatternBuilder() {
      this.symbolMap.put(' ', Predicates.alwaysTrue());
   }

   public BlockPatternBuilder aisle(String... p_177659_1_) {
      if (!ArrayUtils.isEmpty(p_177659_1_) && !StringUtils.isEmpty(p_177659_1_[0])) {
         if (this.depth.isEmpty()) {
            this.aisleHeight = p_177659_1_.length;
            this.rowWidth = p_177659_1_[0].length();
         }

         if (p_177659_1_.length != this.aisleHeight) {
            throw new IllegalArgumentException("Expected aisle with height of " + this.aisleHeight + ", but was given one with a height of " + p_177659_1_.length + ")");
         } else {
            String[] var2 = p_177659_1_;
            int var3 = p_177659_1_.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               String lvt_5_1_ = var2[var4];
               if (lvt_5_1_.length() != this.rowWidth) {
                  throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + this.rowWidth + ", found one with " + lvt_5_1_.length() + ")");
               }

               char[] var6 = lvt_5_1_.toCharArray();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  char lvt_9_1_ = var6[var8];
                  if (!this.symbolMap.containsKey(lvt_9_1_)) {
                     this.symbolMap.put(lvt_9_1_, (Object)null);
                  }
               }
            }

            this.depth.add(p_177659_1_);
            return this;
         }
      } else {
         throw new IllegalArgumentException("Empty pattern for aisle");
      }
   }

   public static BlockPatternBuilder start() {
      return new BlockPatternBuilder();
   }

   public BlockPatternBuilder where(char p_177662_1_, Predicate<CachedBlockInfo> p_177662_2_) {
      this.symbolMap.put(p_177662_1_, p_177662_2_);
      return this;
   }

   public BlockPattern build() {
      return new BlockPattern(this.makePredicateArray());
   }

   private Predicate<CachedBlockInfo>[][][] makePredicateArray() {
      this.checkMissingPredicates();
      Predicate<CachedBlockInfo>[][][] lvt_1_1_ = (Predicate[][][])((Predicate[][][])Array.newInstance(Predicate.class, new int[]{this.depth.size(), this.aisleHeight, this.rowWidth}));

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.depth.size(); ++lvt_2_1_) {
         for(int lvt_3_1_ = 0; lvt_3_1_ < this.aisleHeight; ++lvt_3_1_) {
            for(int lvt_4_1_ = 0; lvt_4_1_ < this.rowWidth; ++lvt_4_1_) {
               lvt_1_1_[lvt_2_1_][lvt_3_1_][lvt_4_1_] = (Predicate)this.symbolMap.get(((String[])this.depth.get(lvt_2_1_))[lvt_3_1_].charAt(lvt_4_1_));
            }
         }
      }

      return lvt_1_1_;
   }

   private void checkMissingPredicates() {
      List<Character> lvt_1_1_ = Lists.newArrayList();
      Iterator var2 = this.symbolMap.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<Character, Predicate<CachedBlockInfo>> lvt_3_1_ = (Entry)var2.next();
         if (lvt_3_1_.getValue() == null) {
            lvt_1_1_.add(lvt_3_1_.getKey());
         }
      }

      if (!lvt_1_1_.isEmpty()) {
         throw new IllegalStateException("Predicates for character(s) " + COMMA_JOIN.join(lvt_1_1_) + " are missing");
      }
   }
}
