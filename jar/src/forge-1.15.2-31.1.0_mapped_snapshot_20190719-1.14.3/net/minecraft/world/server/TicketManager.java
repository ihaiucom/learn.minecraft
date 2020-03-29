package net.minecraft.world.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SortedArraySet;
import net.minecraft.util.concurrent.ITaskExecutor;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkDistanceGraph;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkTaskPriorityQueueSorter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TicketManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int PLAYER_TICKET_LEVEL;
   private final Long2ObjectMap<ObjectSet<ServerPlayerEntity>> playersByChunkPos = new Long2ObjectOpenHashMap();
   private final Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> tickets = new Long2ObjectOpenHashMap();
   private final TicketManager.ChunkTicketTracker ticketTracker = new TicketManager.ChunkTicketTracker();
   private final TicketManager.PlayerChunkTracker playerChunkTracker = new TicketManager.PlayerChunkTracker(8);
   private final TicketManager.PlayerTicketTracker playerTicketTracker = new TicketManager.PlayerTicketTracker(33);
   private final Set<ChunkHolder> chunkHolders = Sets.newHashSet();
   private final ChunkTaskPriorityQueueSorter field_219384_l;
   private final ITaskExecutor<ChunkTaskPriorityQueueSorter.FunctionEntry<Runnable>> field_219385_m;
   private final ITaskExecutor<ChunkTaskPriorityQueueSorter.RunnableEntry> field_219386_n;
   private final LongSet field_219387_o = new LongOpenHashSet();
   private final Executor field_219388_p;
   private long currentTime;

   protected TicketManager(Executor p_i50707_1_, Executor p_i50707_2_) {
      p_i50707_2_.getClass();
      ITaskExecutor<Runnable> lvt_3_1_ = ITaskExecutor.inline("player ticket throttler", p_i50707_2_::execute);
      ChunkTaskPriorityQueueSorter lvt_4_1_ = new ChunkTaskPriorityQueueSorter(ImmutableList.of(lvt_3_1_), p_i50707_1_, 4);
      this.field_219384_l = lvt_4_1_;
      this.field_219385_m = lvt_4_1_.func_219087_a(lvt_3_1_, true);
      this.field_219386_n = lvt_4_1_.func_219091_a(lvt_3_1_);
      this.field_219388_p = p_i50707_2_;
   }

   protected void tick() {
      ++this.currentTime;
      ObjectIterator lvt_1_1_ = this.tickets.long2ObjectEntrySet().fastIterator();

      while(lvt_1_1_.hasNext()) {
         Entry<SortedArraySet<Ticket<?>>> lvt_2_1_ = (Entry)lvt_1_1_.next();
         if (((SortedArraySet)lvt_2_1_.getValue()).removeIf((p_219370_1_) -> {
            return p_219370_1_.isExpired(this.currentTime);
         })) {
            this.ticketTracker.updateSourceLevel(lvt_2_1_.getLongKey(), func_229844_a_((SortedArraySet)lvt_2_1_.getValue()), false);
         }

         if (((SortedArraySet)lvt_2_1_.getValue()).isEmpty()) {
            lvt_1_1_.remove();
         }
      }

   }

   private static int func_229844_a_(SortedArraySet<Ticket<?>> p_229844_0_) {
      return !p_229844_0_.isEmpty() ? ((Ticket)p_229844_0_.func_226178_b_()).getLevel() : ChunkManager.MAX_LOADED_LEVEL + 1;
   }

   protected abstract boolean func_219371_a(long var1);

   @Nullable
   protected abstract ChunkHolder func_219335_b(long var1);

   @Nullable
   protected abstract ChunkHolder func_219372_a(long var1, int var3, @Nullable ChunkHolder var4, int var5);

   public boolean func_219353_a(ChunkManager p_219353_1_) {
      this.playerChunkTracker.func_215497_a();
      this.playerTicketTracker.func_215497_a();
      int lvt_2_1_ = Integer.MAX_VALUE - this.ticketTracker.func_215493_a(Integer.MAX_VALUE);
      boolean lvt_3_1_ = lvt_2_1_ != 0;
      if (lvt_3_1_) {
      }

      if (!this.chunkHolders.isEmpty()) {
         this.chunkHolders.forEach((p_219343_1_) -> {
            p_219343_1_.func_219291_a(p_219353_1_);
         });
         this.chunkHolders.clear();
         return true;
      } else {
         if (!this.field_219387_o.isEmpty()) {
            LongIterator lvt_4_1_ = this.field_219387_o.iterator();

            while(lvt_4_1_.hasNext()) {
               long lvt_5_1_ = lvt_4_1_.nextLong();
               if (this.func_229848_e_(lvt_5_1_).stream().anyMatch((p_219369_0_) -> {
                  return p_219369_0_.getType() == TicketType.PLAYER;
               })) {
                  ChunkHolder lvt_7_1_ = p_219353_1_.func_219220_a(lvt_5_1_);
                  if (lvt_7_1_ == null) {
                     throw new IllegalStateException();
                  }

                  CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> lvt_8_1_ = lvt_7_1_.func_219297_b();
                  lvt_8_1_.thenAccept((p_219363_3_) -> {
                     this.field_219388_p.execute(() -> {
                        this.field_219386_n.enqueue(ChunkTaskPriorityQueueSorter.func_219073_a(() -> {
                        }, lvt_5_1_, false));
                     });
                  });
               }
            }

            this.field_219387_o.clear();
         }

         return lvt_3_1_;
      }
   }

   private void register(long p_219347_1_, Ticket<?> p_219347_3_) {
      SortedArraySet<Ticket<?>> lvt_4_1_ = this.func_229848_e_(p_219347_1_);
      int lvt_5_1_ = func_229844_a_(lvt_4_1_);
      Ticket<?> lvt_6_1_ = (Ticket)lvt_4_1_.func_226175_a_(p_219347_3_);
      lvt_6_1_.func_229861_a_(this.currentTime);
      if (p_219347_3_.getLevel() < lvt_5_1_) {
         this.ticketTracker.updateSourceLevel(p_219347_1_, p_219347_3_.getLevel(), true);
      }

   }

   private void func_219349_b(long p_219349_1_, Ticket<?> p_219349_3_) {
      SortedArraySet<Ticket<?>> lvt_4_1_ = this.func_229848_e_(p_219349_1_);
      if (lvt_4_1_.remove(p_219349_3_)) {
      }

      if (lvt_4_1_.isEmpty()) {
         this.tickets.remove(p_219349_1_);
      }

      this.ticketTracker.updateSourceLevel(p_219349_1_, func_229844_a_(lvt_4_1_), false);
   }

   public <T> void registerWithLevel(TicketType<T> p_219356_1_, ChunkPos p_219356_2_, int p_219356_3_, T p_219356_4_) {
      this.register(p_219356_2_.asLong(), new Ticket(p_219356_1_, p_219356_3_, p_219356_4_));
   }

   public <T> void releaseWithLevel(TicketType<T> p_219345_1_, ChunkPos p_219345_2_, int p_219345_3_, T p_219345_4_) {
      Ticket<T> lvt_5_1_ = new Ticket(p_219345_1_, p_219345_3_, p_219345_4_);
      this.func_219349_b(p_219345_2_.asLong(), lvt_5_1_);
   }

   public <T> void register(TicketType<T> p_219331_1_, ChunkPos p_219331_2_, int p_219331_3_, T p_219331_4_) {
      this.register(p_219331_2_.asLong(), new Ticket(p_219331_1_, 33 - p_219331_3_, p_219331_4_));
   }

   public <T> void release(TicketType<T> p_219362_1_, ChunkPos p_219362_2_, int p_219362_3_, T p_219362_4_) {
      Ticket<T> lvt_5_1_ = new Ticket(p_219362_1_, 33 - p_219362_3_, p_219362_4_);
      this.func_219349_b(p_219362_2_.asLong(), lvt_5_1_);
   }

   private SortedArraySet<Ticket<?>> func_229848_e_(long p_229848_1_) {
      return (SortedArraySet)this.tickets.computeIfAbsent(p_229848_1_, (p_229851_0_) -> {
         return SortedArraySet.func_226172_a_(4);
      });
   }

   protected void forceChunk(ChunkPos p_219364_1_, boolean p_219364_2_) {
      Ticket<ChunkPos> lvt_3_1_ = new Ticket(TicketType.FORCED, 31, p_219364_1_);
      if (p_219364_2_) {
         this.register(p_219364_1_.asLong(), lvt_3_1_);
      } else {
         this.func_219349_b(p_219364_1_.asLong(), lvt_3_1_);
      }

   }

   public void updatePlayerPosition(SectionPos p_219341_1_, ServerPlayerEntity p_219341_2_) {
      long lvt_3_1_ = p_219341_1_.asChunkPos().asLong();
      ((ObjectSet)this.playersByChunkPos.computeIfAbsent(lvt_3_1_, (p_219361_0_) -> {
         return new ObjectOpenHashSet();
      })).add(p_219341_2_);
      this.playerChunkTracker.updateSourceLevel(lvt_3_1_, 0, true);
      this.playerTicketTracker.updateSourceLevel(lvt_3_1_, 0, true);
   }

   public void removePlayer(SectionPos p_219367_1_, ServerPlayerEntity p_219367_2_) {
      long lvt_3_1_ = p_219367_1_.asChunkPos().asLong();
      ObjectSet<ServerPlayerEntity> lvt_5_1_ = (ObjectSet)this.playersByChunkPos.get(lvt_3_1_);
      lvt_5_1_.remove(p_219367_2_);
      if (lvt_5_1_.isEmpty()) {
         this.playersByChunkPos.remove(lvt_3_1_);
         this.playerChunkTracker.updateSourceLevel(lvt_3_1_, Integer.MAX_VALUE, false);
         this.playerTicketTracker.updateSourceLevel(lvt_3_1_, Integer.MAX_VALUE, false);
      }

   }

   protected String func_225413_c(long p_225413_1_) {
      SortedArraySet<Ticket<?>> lvt_3_1_ = (SortedArraySet)this.tickets.get(p_225413_1_);
      String lvt_4_2_;
      if (lvt_3_1_ != null && !lvt_3_1_.isEmpty()) {
         lvt_4_2_ = ((Ticket)lvt_3_1_.func_226178_b_()).toString();
      } else {
         lvt_4_2_ = "no_ticket";
      }

      return lvt_4_2_;
   }

   protected void setViewDistance(int p_219354_1_) {
      this.playerTicketTracker.func_215508_a(p_219354_1_);
   }

   public int func_219358_b() {
      this.playerChunkTracker.func_215497_a();
      return this.playerChunkTracker.field_215498_a.size();
   }

   public boolean func_223494_d(long p_223494_1_) {
      this.playerChunkTracker.func_215497_a();
      return this.playerChunkTracker.field_215498_a.containsKey(p_223494_1_);
   }

   public String func_225412_c() {
      return this.field_219384_l.func_225396_a();
   }

   static {
      PLAYER_TICKET_LEVEL = 33 + ChunkStatus.func_222599_a(ChunkStatus.FULL) - 2;
   }

   class ChunkTicketTracker extends ChunkDistanceGraph {
      public ChunkTicketTracker() {
         super(ChunkManager.MAX_LOADED_LEVEL + 2, 16, 256);
      }

      protected int getSourceLevel(long p_215492_1_) {
         SortedArraySet<Ticket<?>> lvt_3_1_ = (SortedArraySet)TicketManager.this.tickets.get(p_215492_1_);
         if (lvt_3_1_ == null) {
            return Integer.MAX_VALUE;
         } else {
            return lvt_3_1_.isEmpty() ? Integer.MAX_VALUE : ((Ticket)lvt_3_1_.func_226178_b_()).getLevel();
         }
      }

      protected int getLevel(long p_215471_1_) {
         if (!TicketManager.this.func_219371_a(p_215471_1_)) {
            ChunkHolder lvt_3_1_ = TicketManager.this.func_219335_b(p_215471_1_);
            if (lvt_3_1_ != null) {
               return lvt_3_1_.func_219299_i();
            }
         }

         return ChunkManager.MAX_LOADED_LEVEL + 1;
      }

      protected void setLevel(long p_215476_1_, int p_215476_3_) {
         ChunkHolder lvt_4_1_ = TicketManager.this.func_219335_b(p_215476_1_);
         int lvt_5_1_ = lvt_4_1_ == null ? ChunkManager.MAX_LOADED_LEVEL + 1 : lvt_4_1_.func_219299_i();
         if (lvt_5_1_ != p_215476_3_) {
            lvt_4_1_ = TicketManager.this.func_219372_a(p_215476_1_, p_215476_3_, lvt_4_1_, lvt_5_1_);
            if (lvt_4_1_ != null) {
               TicketManager.this.chunkHolders.add(lvt_4_1_);
            }

         }
      }

      public int func_215493_a(int p_215493_1_) {
         return this.processUpdates(p_215493_1_);
      }
   }

   class PlayerTicketTracker extends TicketManager.PlayerChunkTracker {
      private int field_215512_e = 0;
      private final Long2IntMap field_215513_f = Long2IntMaps.synchronize(new Long2IntOpenHashMap());
      private final LongSet field_215514_g = new LongOpenHashSet();

      protected PlayerTicketTracker(int p_i50682_2_) {
         super(p_i50682_2_);
         this.field_215513_f.defaultReturnValue(p_i50682_2_ + 2);
      }

      protected void func_215495_a(long p_215495_1_, int p_215495_3_, int p_215495_4_) {
         this.field_215514_g.add(p_215495_1_);
      }

      public void func_215508_a(int p_215508_1_) {
         ObjectIterator var2 = this.field_215498_a.long2ByteEntrySet().iterator();

         while(var2.hasNext()) {
            it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry lvt_3_1_ = (it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry)var2.next();
            byte lvt_4_1_ = lvt_3_1_.getByteValue();
            long lvt_5_1_ = lvt_3_1_.getLongKey();
            this.func_215504_a(lvt_5_1_, lvt_4_1_, this.func_215505_c(lvt_4_1_), lvt_4_1_ <= p_215508_1_ - 2);
         }

         this.field_215512_e = p_215508_1_;
      }

      private void func_215504_a(long p_215504_1_, int p_215504_3_, boolean p_215504_4_, boolean p_215504_5_) {
         if (p_215504_4_ != p_215504_5_) {
            Ticket<?> lvt_6_1_ = new Ticket(TicketType.PLAYER, TicketManager.PLAYER_TICKET_LEVEL, new ChunkPos(p_215504_1_));
            if (p_215504_5_) {
               TicketManager.this.field_219385_m.enqueue(ChunkTaskPriorityQueueSorter.func_219069_a(() -> {
                  TicketManager.this.field_219388_p.execute(() -> {
                     if (this.func_215505_c(this.getLevel(p_215504_1_))) {
                        TicketManager.this.register(p_215504_1_, lvt_6_1_);
                        TicketManager.this.field_219387_o.add(p_215504_1_);
                     } else {
                        TicketManager.this.field_219386_n.enqueue(ChunkTaskPriorityQueueSorter.func_219073_a(() -> {
                        }, p_215504_1_, false));
                     }

                  });
               }, p_215504_1_, () -> {
                  return p_215504_3_;
               }));
            } else {
               TicketManager.this.field_219386_n.enqueue(ChunkTaskPriorityQueueSorter.func_219073_a(() -> {
                  TicketManager.this.field_219388_p.execute(() -> {
                     TicketManager.this.func_219349_b(p_215504_1_, lvt_6_1_);
                  });
               }, p_215504_1_, true));
            }
         }

      }

      public void func_215497_a() {
         super.func_215497_a();
         if (!this.field_215514_g.isEmpty()) {
            LongIterator lvt_1_1_ = this.field_215514_g.iterator();

            while(lvt_1_1_.hasNext()) {
               long lvt_2_1_ = lvt_1_1_.nextLong();
               int lvt_4_1_ = this.field_215513_f.get(lvt_2_1_);
               int lvt_5_1_ = this.getLevel(lvt_2_1_);
               if (lvt_4_1_ != lvt_5_1_) {
                  TicketManager.this.field_219384_l.func_219066_a(new ChunkPos(lvt_2_1_), () -> {
                     return this.field_215513_f.get(lvt_2_1_);
                  }, lvt_5_1_, (p_215506_3_) -> {
                     if (p_215506_3_ >= this.field_215513_f.defaultReturnValue()) {
                        this.field_215513_f.remove(lvt_2_1_);
                     } else {
                        this.field_215513_f.put(lvt_2_1_, p_215506_3_);
                     }

                  });
                  this.func_215504_a(lvt_2_1_, lvt_5_1_, this.func_215505_c(lvt_4_1_), this.func_215505_c(lvt_5_1_));
               }
            }

            this.field_215514_g.clear();
         }

      }

      private boolean func_215505_c(int p_215505_1_) {
         return p_215505_1_ <= this.field_215512_e - 2;
      }
   }

   class PlayerChunkTracker extends ChunkDistanceGraph {
      protected final Long2ByteMap field_215498_a = new Long2ByteOpenHashMap();
      protected final int field_215499_b;

      protected PlayerChunkTracker(int p_i50684_2_) {
         super(p_i50684_2_ + 2, 16, 256);
         this.field_215499_b = p_i50684_2_;
         this.field_215498_a.defaultReturnValue((byte)(p_i50684_2_ + 2));
      }

      protected int getLevel(long p_215471_1_) {
         return this.field_215498_a.get(p_215471_1_);
      }

      protected void setLevel(long p_215476_1_, int p_215476_3_) {
         byte lvt_4_2_;
         if (p_215476_3_ > this.field_215499_b) {
            lvt_4_2_ = this.field_215498_a.remove(p_215476_1_);
         } else {
            lvt_4_2_ = this.field_215498_a.put(p_215476_1_, (byte)p_215476_3_);
         }

         this.func_215495_a(p_215476_1_, lvt_4_2_, p_215476_3_);
      }

      protected void func_215495_a(long p_215495_1_, int p_215495_3_, int p_215495_4_) {
      }

      protected int getSourceLevel(long p_215492_1_) {
         return this.func_215496_d(p_215492_1_) ? 0 : Integer.MAX_VALUE;
      }

      private boolean func_215496_d(long p_215496_1_) {
         ObjectSet<ServerPlayerEntity> lvt_3_1_ = (ObjectSet)TicketManager.this.playersByChunkPos.get(p_215496_1_);
         return lvt_3_1_ != null && !lvt_3_1_.isEmpty();
      }

      public void func_215497_a() {
         this.processUpdates(Integer.MAX_VALUE);
      }
   }
}
