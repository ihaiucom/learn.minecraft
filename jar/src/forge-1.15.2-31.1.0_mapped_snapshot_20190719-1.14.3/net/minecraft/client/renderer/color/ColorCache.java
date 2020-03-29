package net.minecraft.client.renderer.color;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntSupplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColorCache {
   private final ThreadLocal<ColorCache.Entry> field_228066_a_ = ThreadLocal.withInitial(() -> {
      return new ColorCache.Entry();
   });
   private final Long2ObjectLinkedOpenHashMap<int[]> field_228067_b_ = new Long2ObjectLinkedOpenHashMap(256, 0.25F);
   private final ReentrantReadWriteLock field_228068_c_ = new ReentrantReadWriteLock();

   public int func_228071_a_(BlockPos p_228071_1_, IntSupplier p_228071_2_) {
      int lvt_3_1_ = p_228071_1_.getX() >> 4;
      int lvt_4_1_ = p_228071_1_.getZ() >> 4;
      ColorCache.Entry lvt_5_1_ = (ColorCache.Entry)this.field_228066_a_.get();
      if (lvt_5_1_.field_228074_a_ != lvt_3_1_ || lvt_5_1_.field_228075_b_ != lvt_4_1_) {
         lvt_5_1_.field_228074_a_ = lvt_3_1_;
         lvt_5_1_.field_228075_b_ = lvt_4_1_;
         lvt_5_1_.field_228076_c_ = this.func_228073_b_(lvt_3_1_, lvt_4_1_);
      }

      int lvt_6_1_ = p_228071_1_.getX() & 15;
      int lvt_7_1_ = p_228071_1_.getZ() & 15;
      int lvt_8_1_ = lvt_7_1_ << 4 | lvt_6_1_;
      int lvt_9_1_ = lvt_5_1_.field_228076_c_[lvt_8_1_];
      if (lvt_9_1_ != -1) {
         return lvt_9_1_;
      } else {
         int lvt_10_1_ = p_228071_2_.getAsInt();
         lvt_5_1_.field_228076_c_[lvt_8_1_] = lvt_10_1_;
         return lvt_10_1_;
      }
   }

   public void func_228070_a_(int p_228070_1_, int p_228070_2_) {
      try {
         this.field_228068_c_.writeLock().lock();

         for(int lvt_3_1_ = -1; lvt_3_1_ <= 1; ++lvt_3_1_) {
            for(int lvt_4_1_ = -1; lvt_4_1_ <= 1; ++lvt_4_1_) {
               long lvt_5_1_ = ChunkPos.asLong(p_228070_1_ + lvt_3_1_, p_228070_2_ + lvt_4_1_);
               this.field_228067_b_.remove(lvt_5_1_);
            }
         }
      } finally {
         this.field_228068_c_.writeLock().unlock();
      }

   }

   public void func_228069_a_() {
      try {
         this.field_228068_c_.writeLock().lock();
         this.field_228067_b_.clear();
      } finally {
         this.field_228068_c_.writeLock().unlock();
      }

   }

   private int[] func_228073_b_(int p_228073_1_, int p_228073_2_) {
      long lvt_3_1_ = ChunkPos.asLong(p_228073_1_, p_228073_2_);
      this.field_228068_c_.readLock().lock();

      int[] lvt_5_2_;
      try {
         lvt_5_2_ = (int[])this.field_228067_b_.get(lvt_3_1_);
      } finally {
         this.field_228068_c_.readLock().unlock();
      }

      if (lvt_5_2_ != null) {
         return lvt_5_2_;
      } else {
         int[] lvt_6_1_ = new int[256];
         Arrays.fill(lvt_6_1_, -1);

         try {
            this.field_228068_c_.writeLock().lock();
            if (this.field_228067_b_.size() >= 256) {
               this.field_228067_b_.removeFirst();
            }

            this.field_228067_b_.put(lvt_3_1_, lvt_6_1_);
         } finally {
            this.field_228068_c_.writeLock().unlock();
         }

         return lvt_6_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Entry {
      public int field_228074_a_;
      public int field_228075_b_;
      public int[] field_228076_c_;

      private Entry() {
         this.field_228074_a_ = Integer.MIN_VALUE;
         this.field_228075_b_ = Integer.MIN_VALUE;
      }

      // $FF: synthetic method
      Entry(Object p_i225918_1_) {
         this();
      }
   }
}
