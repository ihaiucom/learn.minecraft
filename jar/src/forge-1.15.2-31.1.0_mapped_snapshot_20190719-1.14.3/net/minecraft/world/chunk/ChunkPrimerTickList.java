package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ITickList;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.TickPriority;
import net.minecraft.world.chunk.storage.ChunkSerializer;

public class ChunkPrimerTickList<T> implements ITickList<T> {
   protected final Predicate<T> filter;
   private final ChunkPos pos;
   private final ShortList[] packedPositions;

   public ChunkPrimerTickList(Predicate<T> p_i51495_1_, ChunkPos p_i51495_2_) {
      this(p_i51495_1_, p_i51495_2_, new ListNBT());
   }

   public ChunkPrimerTickList(Predicate<T> p_i51496_1_, ChunkPos p_i51496_2_, ListNBT p_i51496_3_) {
      this.packedPositions = new ShortList[16];
      this.filter = p_i51496_1_;
      this.pos = p_i51496_2_;

      for(int lvt_4_1_ = 0; lvt_4_1_ < p_i51496_3_.size(); ++lvt_4_1_) {
         ListNBT lvt_5_1_ = p_i51496_3_.getList(lvt_4_1_);

         for(int lvt_6_1_ = 0; lvt_6_1_ < lvt_5_1_.size(); ++lvt_6_1_) {
            IChunk.getList(this.packedPositions, lvt_4_1_).add(lvt_5_1_.getShort(lvt_6_1_));
         }
      }

   }

   public ListNBT write() {
      return ChunkSerializer.toNbt(this.packedPositions);
   }

   public void postProcess(ITickList<T> p_205381_1_, Function<BlockPos, T> p_205381_2_) {
      for(int lvt_3_1_ = 0; lvt_3_1_ < this.packedPositions.length; ++lvt_3_1_) {
         if (this.packedPositions[lvt_3_1_] != null) {
            ShortListIterator var4 = this.packedPositions[lvt_3_1_].iterator();

            while(var4.hasNext()) {
               Short lvt_5_1_ = (Short)var4.next();
               BlockPos lvt_6_1_ = ChunkPrimer.unpackToWorld(lvt_5_1_, lvt_3_1_, this.pos);
               p_205381_1_.scheduleTick(lvt_6_1_, p_205381_2_.apply(lvt_6_1_), 0);
            }

            this.packedPositions[lvt_3_1_].clear();
         }
      }

   }

   public boolean isTickScheduled(BlockPos p_205359_1_, T p_205359_2_) {
      return false;
   }

   public void scheduleTick(BlockPos p_205362_1_, T p_205362_2_, int p_205362_3_, TickPriority p_205362_4_) {
      IChunk.getList(this.packedPositions, p_205362_1_.getY() >> 4).add(ChunkPrimer.packToLocal(p_205362_1_));
   }

   public boolean isTickPending(BlockPos p_205361_1_, T p_205361_2_) {
      return false;
   }

   public void func_219497_a(Stream<NextTickListEntry<T>> p_219497_1_) {
      p_219497_1_.forEach((p_219506_1_) -> {
         this.scheduleTick(p_219506_1_.position, p_219506_1_.getTarget(), 0, p_219506_1_.priority);
      });
   }
}
