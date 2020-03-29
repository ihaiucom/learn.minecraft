package net.minecraft.world.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ITickList;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.TickPriority;

public class ServerTickList<T> implements ITickList<T> {
   protected final Predicate<T> filter;
   private final Function<T, ResourceLocation> serializer;
   private final Function<ResourceLocation, T> deserializer;
   private final Set<NextTickListEntry<T>> pendingTickListEntriesHashSet = Sets.newHashSet();
   private final TreeSet<NextTickListEntry<T>> pendingTickListEntriesTreeSet = Sets.newTreeSet(NextTickListEntry.func_223192_a());
   private final ServerWorld world;
   private final Queue<NextTickListEntry<T>> pendingTickListEntriesThisTick = Queues.newArrayDeque();
   private final List<NextTickListEntry<T>> field_223189_h = Lists.newArrayList();
   private final Consumer<NextTickListEntry<T>> tickFunction;

   public ServerTickList(ServerWorld p_i48979_1_, Predicate<T> p_i48979_2_, Function<T, ResourceLocation> p_i48979_3_, Function<ResourceLocation, T> p_i48979_4_, Consumer<NextTickListEntry<T>> p_i48979_5_) {
      this.filter = p_i48979_2_;
      this.serializer = p_i48979_3_;
      this.deserializer = p_i48979_4_;
      this.world = p_i48979_1_;
      this.tickFunction = p_i48979_5_;
   }

   public void tick() {
      int lvt_1_1_ = this.pendingTickListEntriesTreeSet.size();
      if (lvt_1_1_ != this.pendingTickListEntriesHashSet.size()) {
         throw new IllegalStateException("TickNextTick list out of synch");
      } else {
         if (lvt_1_1_ > 65536) {
            lvt_1_1_ = 65536;
         }

         ServerChunkProvider lvt_2_1_ = this.world.getChunkProvider();
         Iterator<NextTickListEntry<T>> lvt_3_1_ = this.pendingTickListEntriesTreeSet.iterator();
         this.world.getProfiler().startSection("cleaning");

         NextTickListEntry lvt_4_2_;
         while(lvt_1_1_ > 0 && lvt_3_1_.hasNext()) {
            lvt_4_2_ = (NextTickListEntry)lvt_3_1_.next();
            if (lvt_4_2_.scheduledTime > this.world.getGameTime()) {
               break;
            }

            if (lvt_2_1_.canTick(lvt_4_2_.position)) {
               lvt_3_1_.remove();
               this.pendingTickListEntriesHashSet.remove(lvt_4_2_);
               this.pendingTickListEntriesThisTick.add(lvt_4_2_);
               --lvt_1_1_;
            }
         }

         this.world.getProfiler().endStartSection("ticking");

         while((lvt_4_2_ = (NextTickListEntry)this.pendingTickListEntriesThisTick.poll()) != null) {
            if (lvt_2_1_.canTick(lvt_4_2_.position)) {
               try {
                  this.field_223189_h.add(lvt_4_2_);
                  this.tickFunction.accept(lvt_4_2_);
               } catch (Throwable var8) {
                  CrashReport lvt_6_1_ = CrashReport.makeCrashReport(var8, "Exception while ticking");
                  CrashReportCategory lvt_7_1_ = lvt_6_1_.makeCategory("Block being ticked");
                  CrashReportCategory.addBlockInfo(lvt_7_1_, lvt_4_2_.position, (BlockState)null);
                  throw new ReportedException(lvt_6_1_);
               }
            } else {
               this.scheduleTick(lvt_4_2_.position, lvt_4_2_.getTarget(), 0);
            }
         }

         this.world.getProfiler().endSection();
         this.field_223189_h.clear();
         this.pendingTickListEntriesThisTick.clear();
      }
   }

   public boolean isTickPending(BlockPos p_205361_1_, T p_205361_2_) {
      return this.pendingTickListEntriesThisTick.contains(new NextTickListEntry(p_205361_1_, p_205361_2_));
   }

   public void func_219497_a(Stream<NextTickListEntry<T>> p_219497_1_) {
      p_219497_1_.forEach(this::func_219504_a);
   }

   public List<NextTickListEntry<T>> func_223188_a(ChunkPos p_223188_1_, boolean p_223188_2_, boolean p_223188_3_) {
      int lvt_4_1_ = (p_223188_1_.x << 4) - 2;
      int lvt_5_1_ = lvt_4_1_ + 16 + 2;
      int lvt_6_1_ = (p_223188_1_.z << 4) - 2;
      int lvt_7_1_ = lvt_6_1_ + 16 + 2;
      return this.getPending(new MutableBoundingBox(lvt_4_1_, 0, lvt_6_1_, lvt_5_1_, 256, lvt_7_1_), p_223188_2_, p_223188_3_);
   }

   public List<NextTickListEntry<T>> getPending(MutableBoundingBox p_205366_1_, boolean p_205366_2_, boolean p_205366_3_) {
      List<NextTickListEntry<T>> lvt_4_1_ = this.func_223187_a((List)null, this.pendingTickListEntriesTreeSet, p_205366_1_, p_205366_2_);
      if (p_205366_2_ && lvt_4_1_ != null) {
         this.pendingTickListEntriesHashSet.removeAll(lvt_4_1_);
      }

      lvt_4_1_ = this.func_223187_a(lvt_4_1_, this.pendingTickListEntriesThisTick, p_205366_1_, p_205366_2_);
      if (!p_205366_3_) {
         lvt_4_1_ = this.func_223187_a(lvt_4_1_, this.field_223189_h, p_205366_1_, p_205366_2_);
      }

      return lvt_4_1_ == null ? Collections.emptyList() : lvt_4_1_;
   }

   @Nullable
   private List<NextTickListEntry<T>> func_223187_a(@Nullable List<NextTickListEntry<T>> p_223187_1_, Collection<NextTickListEntry<T>> p_223187_2_, MutableBoundingBox p_223187_3_, boolean p_223187_4_) {
      Iterator lvt_5_1_ = p_223187_2_.iterator();

      while(lvt_5_1_.hasNext()) {
         NextTickListEntry<T> lvt_6_1_ = (NextTickListEntry)lvt_5_1_.next();
         BlockPos lvt_7_1_ = lvt_6_1_.position;
         if (lvt_7_1_.getX() >= p_223187_3_.minX && lvt_7_1_.getX() < p_223187_3_.maxX && lvt_7_1_.getZ() >= p_223187_3_.minZ && lvt_7_1_.getZ() < p_223187_3_.maxZ) {
            if (p_223187_4_) {
               lvt_5_1_.remove();
            }

            if (p_223187_1_ == null) {
               p_223187_1_ = Lists.newArrayList();
            }

            ((List)p_223187_1_).add(lvt_6_1_);
         }
      }

      return (List)p_223187_1_;
   }

   public void copyTicks(MutableBoundingBox p_205368_1_, BlockPos p_205368_2_) {
      List<NextTickListEntry<T>> lvt_3_1_ = this.getPending(p_205368_1_, false, false);
      Iterator var4 = lvt_3_1_.iterator();

      while(var4.hasNext()) {
         NextTickListEntry<T> lvt_5_1_ = (NextTickListEntry)var4.next();
         if (p_205368_1_.isVecInside(lvt_5_1_.position)) {
            BlockPos lvt_6_1_ = lvt_5_1_.position.add(p_205368_2_);
            T lvt_7_1_ = lvt_5_1_.getTarget();
            this.func_219504_a(new NextTickListEntry(lvt_6_1_, lvt_7_1_, lvt_5_1_.scheduledTime, lvt_5_1_.priority));
         }
      }

   }

   public ListNBT func_219503_a(ChunkPos p_219503_1_) {
      List<NextTickListEntry<T>> lvt_2_1_ = this.func_223188_a(p_219503_1_, false, true);
      return func_219502_a(this.serializer, lvt_2_1_, this.world.getGameTime());
   }

   public static <T> ListNBT func_219502_a(Function<T, ResourceLocation> p_219502_0_, Iterable<NextTickListEntry<T>> p_219502_1_, long p_219502_2_) {
      ListNBT lvt_4_1_ = new ListNBT();
      Iterator var5 = p_219502_1_.iterator();

      while(var5.hasNext()) {
         NextTickListEntry<T> lvt_6_1_ = (NextTickListEntry)var5.next();
         CompoundNBT lvt_7_1_ = new CompoundNBT();
         lvt_7_1_.putString("i", ((ResourceLocation)p_219502_0_.apply(lvt_6_1_.getTarget())).toString());
         lvt_7_1_.putInt("x", lvt_6_1_.position.getX());
         lvt_7_1_.putInt("y", lvt_6_1_.position.getY());
         lvt_7_1_.putInt("z", lvt_6_1_.position.getZ());
         lvt_7_1_.putInt("t", (int)(lvt_6_1_.scheduledTime - p_219502_2_));
         lvt_7_1_.putInt("p", lvt_6_1_.priority.getPriority());
         lvt_4_1_.add(lvt_7_1_);
      }

      return lvt_4_1_;
   }

   public boolean isTickScheduled(BlockPos p_205359_1_, T p_205359_2_) {
      return this.pendingTickListEntriesHashSet.contains(new NextTickListEntry(p_205359_1_, p_205359_2_));
   }

   public void scheduleTick(BlockPos p_205362_1_, T p_205362_2_, int p_205362_3_, TickPriority p_205362_4_) {
      if (!this.filter.test(p_205362_2_)) {
         this.func_219504_a(new NextTickListEntry(p_205362_1_, p_205362_2_, (long)p_205362_3_ + this.world.getGameTime(), p_205362_4_));
      }

   }

   private void func_219504_a(NextTickListEntry<T> p_219504_1_) {
      if (!this.pendingTickListEntriesHashSet.contains(p_219504_1_)) {
         this.pendingTickListEntriesHashSet.add(p_219504_1_);
         this.pendingTickListEntriesTreeSet.add(p_219504_1_);
      }

   }

   public int func_225420_a() {
      return this.pendingTickListEntriesHashSet.size();
   }
}
