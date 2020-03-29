package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongList;
import java.util.function.LongPredicate;
import net.minecraft.util.math.MathHelper;

public abstract class LevelBasedGraph {
   private final int levelCount;
   private final LongLinkedOpenHashSet[] updatesByLevel;
   private final Long2ByteMap propagationLevels;
   private int minLevelToUpdate;
   private volatile boolean needsUpdate;

   protected LevelBasedGraph(int p_i51298_1_, final int p_i51298_2_, final int p_i51298_3_) {
      if (p_i51298_1_ >= 254) {
         throw new IllegalArgumentException("Level count must be < 254.");
      } else {
         this.levelCount = p_i51298_1_;
         this.updatesByLevel = new LongLinkedOpenHashSet[p_i51298_1_];

         for(int i = 0; i < p_i51298_1_; ++i) {
            this.updatesByLevel[i] = new LongLinkedOpenHashSet(p_i51298_2_, 0.5F) {
               protected void rehash(int p_rehash_1_) {
                  if (p_rehash_1_ > p_i51298_2_) {
                     super.rehash(p_rehash_1_);
                  }

               }
            };
         }

         this.propagationLevels = new Long2ByteOpenHashMap(p_i51298_3_, 0.5F) {
            protected void rehash(int p_rehash_1_) {
               if (p_rehash_1_ > p_i51298_3_) {
                  super.rehash(p_rehash_1_);
               }

            }
         };
         this.propagationLevels.defaultReturnValue((byte)-1);
         this.minLevelToUpdate = p_i51298_1_;
      }
   }

   private int minLevel(int p_215482_1_, int p_215482_2_) {
      int i = p_215482_1_;
      if (p_215482_1_ > p_215482_2_) {
         i = p_215482_2_;
      }

      if (i > this.levelCount - 1) {
         i = this.levelCount - 1;
      }

      return i;
   }

   private void updateMinLevel(int p_215472_1_) {
      int i = this.minLevelToUpdate;
      this.minLevelToUpdate = p_215472_1_;

      for(int j = i + 1; j < p_215472_1_; ++j) {
         if (!this.updatesByLevel[j].isEmpty()) {
            this.minLevelToUpdate = j;
            break;
         }
      }

   }

   protected void cancelUpdate(long p_215479_1_) {
      int i = this.propagationLevels.get(p_215479_1_) & 255;
      if (i != 255) {
         int j = this.getLevel(p_215479_1_);
         int k = this.minLevel(j, i);
         this.removeToUpdate(p_215479_1_, k, this.levelCount, true);
         this.needsUpdate = this.minLevelToUpdate < this.levelCount;
      }

   }

   public void func_227465_a_(LongPredicate p_227465_1_) {
      LongList longlist = new LongArrayList();
      this.propagationLevels.keySet().forEach((p_lambda$func_227465_a_$0_2_) -> {
         if (p_227465_1_.test(p_lambda$func_227465_a_$0_2_)) {
            longlist.add(p_lambda$func_227465_a_$0_2_);
         }

      });
      longlist.forEach(this::cancelUpdate);
   }

   private void removeToUpdate(long p_215484_1_, int p_215484_3_, int p_215484_4_, boolean p_215484_5_) {
      if (p_215484_5_) {
         this.propagationLevels.remove(p_215484_1_);
      }

      this.updatesByLevel[p_215484_3_].remove(p_215484_1_);
      if (this.updatesByLevel[p_215484_3_].isEmpty() && this.minLevelToUpdate == p_215484_3_) {
         this.updateMinLevel(p_215484_4_);
      }

   }

   private void addToUpdate(long p_215470_1_, int p_215470_3_, int p_215470_4_) {
      this.propagationLevels.put(p_215470_1_, (byte)p_215470_3_);
      this.updatesByLevel[p_215470_4_].add(p_215470_1_);
      if (this.minLevelToUpdate > p_215470_4_) {
         this.minLevelToUpdate = p_215470_4_;
      }

   }

   protected void scheduleUpdate(long p_215473_1_) {
      this.scheduleUpdate(p_215473_1_, p_215473_1_, this.levelCount - 1, false);
   }

   protected void scheduleUpdate(long p_215469_1_, long p_215469_3_, int p_215469_5_, boolean p_215469_6_) {
      this.propagateLevel(p_215469_1_, p_215469_3_, p_215469_5_, this.getLevel(p_215469_3_), this.propagationLevels.get(p_215469_3_) & 255, p_215469_6_);
      this.needsUpdate = this.minLevelToUpdate < this.levelCount;
   }

   private void propagateLevel(long p_215474_1_, long p_215474_3_, int p_215474_5_, int p_215474_6_, int p_215474_7_, boolean p_215474_8_) {
      if (!this.isRoot(p_215474_3_)) {
         p_215474_5_ = MathHelper.clamp(p_215474_5_, 0, this.levelCount - 1);
         p_215474_6_ = MathHelper.clamp(p_215474_6_, 0, this.levelCount - 1);
         boolean flag;
         if (p_215474_7_ == 255) {
            flag = true;
            p_215474_7_ = p_215474_6_;
         } else {
            flag = false;
         }

         int i;
         if (p_215474_8_) {
            i = Math.min(p_215474_7_, p_215474_5_);
         } else {
            i = MathHelper.clamp(this.computeLevel(p_215474_3_, p_215474_1_, p_215474_5_), 0, this.levelCount - 1);
         }

         int j = this.minLevel(p_215474_6_, p_215474_7_);
         if (p_215474_6_ != i) {
            int k = this.minLevel(p_215474_6_, i);
            if (j != k && !flag) {
               this.removeToUpdate(p_215474_3_, j, k, false);
            }

            this.addToUpdate(p_215474_3_, i, k);
         } else if (!flag) {
            this.removeToUpdate(p_215474_3_, j, this.levelCount, true);
         }
      }

   }

   protected final void propagateLevel(long p_215475_1_, long p_215475_3_, int p_215475_5_, boolean p_215475_6_) {
      int i = this.propagationLevels.get(p_215475_3_) & 255;
      int j = MathHelper.clamp(this.getEdgeLevel(p_215475_1_, p_215475_3_, p_215475_5_), 0, this.levelCount - 1);
      if (p_215475_6_) {
         this.propagateLevel(p_215475_1_, p_215475_3_, j, this.getLevel(p_215475_3_), i, true);
      } else {
         int k;
         boolean flag;
         if (i == 255) {
            flag = true;
            k = MathHelper.clamp(this.getLevel(p_215475_3_), 0, this.levelCount - 1);
         } else {
            k = i;
            flag = false;
         }

         if (j == k) {
            this.propagateLevel(p_215475_1_, p_215475_3_, this.levelCount - 1, flag ? k : this.getLevel(p_215475_3_), i, false);
         }
      }

   }

   protected final boolean needsUpdate() {
      return this.needsUpdate;
   }

   protected final int processUpdates(int p_215483_1_) {
      if (this.minLevelToUpdate >= this.levelCount) {
         return p_215483_1_;
      } else {
         while(this.minLevelToUpdate < this.levelCount && p_215483_1_ > 0) {
            --p_215483_1_;
            LongLinkedOpenHashSet longlinkedopenhashset = this.updatesByLevel[this.minLevelToUpdate];
            long i = longlinkedopenhashset.removeFirstLong();
            int j = MathHelper.clamp(this.getLevel(i), 0, this.levelCount - 1);
            if (longlinkedopenhashset.isEmpty()) {
               this.updateMinLevel(this.levelCount);
            }

            int k = this.propagationLevels.remove(i) & 255;
            if (k < j) {
               this.setLevel(i, k);
               this.notifyNeighbors(i, k, true);
            } else if (k > j) {
               this.addToUpdate(i, k, this.minLevel(this.levelCount - 1, k));
               this.setLevel(i, this.levelCount - 1);
               this.notifyNeighbors(i, j, false);
            }
         }

         this.needsUpdate = this.minLevelToUpdate < this.levelCount;
         return p_215483_1_;
      }
   }

   public int func_227467_c_() {
      return this.propagationLevels.size();
   }

   protected abstract boolean isRoot(long var1);

   protected abstract int computeLevel(long var1, long var3, int var5);

   protected abstract void notifyNeighbors(long var1, int var3, boolean var4);

   protected abstract int getLevel(long var1);

   protected abstract void setLevel(long var1, int var3);

   protected abstract int getEdgeLevel(long var1, long var3, int var5);

   protected int queuedUpdateSize() {
      return this.propagationLevels.size();
   }
}
