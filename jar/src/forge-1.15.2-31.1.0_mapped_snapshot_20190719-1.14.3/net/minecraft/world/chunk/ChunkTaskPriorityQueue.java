package net.minecraft.world.chunk;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ChunkManager;

public class ChunkTaskPriorityQueue<T> {
   public static final int field_219419_a;
   private final List<Long2ObjectLinkedOpenHashMap<List<Optional<T>>>> field_219420_b;
   private volatile int field_219421_c;
   private final String field_219422_d;
   private final LongSet field_219423_e;
   private final int field_219424_f;

   public ChunkTaskPriorityQueue(String p_i50714_1_, int p_i50714_2_) {
      this.field_219420_b = (List)IntStream.range(0, field_219419_a).mapToObj((p_219415_0_) -> {
         return new Long2ObjectLinkedOpenHashMap();
      }).collect(Collectors.toList());
      this.field_219421_c = field_219419_a;
      this.field_219423_e = new LongOpenHashSet();
      this.field_219422_d = p_i50714_1_;
      this.field_219424_f = p_i50714_2_;
   }

   protected void func_219407_a(int p_219407_1_, ChunkPos p_219407_2_, int p_219407_3_) {
      if (p_219407_1_ < field_219419_a) {
         Long2ObjectLinkedOpenHashMap<List<Optional<T>>> lvt_4_1_ = (Long2ObjectLinkedOpenHashMap)this.field_219420_b.get(p_219407_1_);
         List<Optional<T>> lvt_5_1_ = (List)lvt_4_1_.remove(p_219407_2_.asLong());
         if (p_219407_1_ == this.field_219421_c) {
            while(this.field_219421_c < field_219419_a && ((Long2ObjectLinkedOpenHashMap)this.field_219420_b.get(this.field_219421_c)).isEmpty()) {
               ++this.field_219421_c;
            }
         }

         if (lvt_5_1_ != null && !lvt_5_1_.isEmpty()) {
            ((List)((Long2ObjectLinkedOpenHashMap)this.field_219420_b.get(p_219407_3_)).computeIfAbsent(p_219407_2_.asLong(), (p_219411_0_) -> {
               return Lists.newArrayList();
            })).addAll(lvt_5_1_);
            this.field_219421_c = Math.min(this.field_219421_c, p_219407_3_);
         }

      }
   }

   protected void func_219412_a(Optional<T> p_219412_1_, long p_219412_2_, int p_219412_4_) {
      ((List)((Long2ObjectLinkedOpenHashMap)this.field_219420_b.get(p_219412_4_)).computeIfAbsent(p_219412_2_, (p_219410_0_) -> {
         return Lists.newArrayList();
      })).add(p_219412_1_);
      this.field_219421_c = Math.min(this.field_219421_c, p_219412_4_);
   }

   protected void func_219416_a(long p_219416_1_, boolean p_219416_3_) {
      Iterator var4 = this.field_219420_b.iterator();

      while(var4.hasNext()) {
         Long2ObjectLinkedOpenHashMap<List<Optional<T>>> lvt_5_1_ = (Long2ObjectLinkedOpenHashMap)var4.next();
         List<Optional<T>> lvt_6_1_ = (List)lvt_5_1_.get(p_219416_1_);
         if (lvt_6_1_ != null) {
            if (p_219416_3_) {
               lvt_6_1_.clear();
            } else {
               lvt_6_1_.removeIf((p_219413_0_) -> {
                  return !p_219413_0_.isPresent();
               });
            }

            if (lvt_6_1_.isEmpty()) {
               lvt_5_1_.remove(p_219416_1_);
            }
         }
      }

      while(this.field_219421_c < field_219419_a && ((Long2ObjectLinkedOpenHashMap)this.field_219420_b.get(this.field_219421_c)).isEmpty()) {
         ++this.field_219421_c;
      }

      this.field_219423_e.remove(p_219416_1_);
   }

   private Runnable func_219418_a(long p_219418_1_) {
      return () -> {
         this.field_219423_e.add(p_219418_1_);
      };
   }

   @Nullable
   public Stream<Either<T, Runnable>> func_219417_a() {
      if (this.field_219423_e.size() >= this.field_219424_f) {
         return null;
      } else if (this.field_219421_c >= field_219419_a) {
         return null;
      } else {
         int lvt_1_1_ = this.field_219421_c;
         Long2ObjectLinkedOpenHashMap<List<Optional<T>>> lvt_2_1_ = (Long2ObjectLinkedOpenHashMap)this.field_219420_b.get(lvt_1_1_);
         long lvt_3_1_ = lvt_2_1_.firstLongKey();

         List lvt_5_1_;
         for(lvt_5_1_ = (List)lvt_2_1_.removeFirst(); this.field_219421_c < field_219419_a && ((Long2ObjectLinkedOpenHashMap)this.field_219420_b.get(this.field_219421_c)).isEmpty(); ++this.field_219421_c) {
         }

         return lvt_5_1_.stream().map((p_219408_3_) -> {
            return (Either)p_219408_3_.map(Either::left).orElseGet(() -> {
               return Either.right(this.func_219418_a(lvt_3_1_));
            });
         });
      }
   }

   public String toString() {
      return this.field_219422_d + " " + this.field_219421_c + "...";
   }

   @VisibleForTesting
   LongSet func_225414_b() {
      return new LongOpenHashSet(this.field_219423_e);
   }

   static {
      field_219419_a = ChunkManager.MAX_LOADED_LEVEL + 2;
   }
}
