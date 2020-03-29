package net.minecraft.world.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.network.play.server.SUpdateChunkPositionPacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.CSVWriter;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import net.minecraft.util.concurrent.ITaskExecutor;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.GameRules;
import net.minecraft.world.TrackedEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkTaskPriorityQueue;
import net.minecraft.world.chunk.ChunkTaskPriorityQueueSorter;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.PlayerGenerationTracker;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.chunk.storage.ChunkLoader;
import net.minecraft.world.chunk.storage.ChunkSerializer;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.SessionLockException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkManager extends ChunkLoader implements ChunkHolder.IPlayerProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final int MAX_LOADED_LEVEL = 33 + ChunkStatus.func_222600_b();
   private final Long2ObjectLinkedOpenHashMap<ChunkHolder> field_219251_e = new Long2ObjectLinkedOpenHashMap();
   private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> field_219252_f;
   private final Long2ObjectLinkedOpenHashMap<ChunkHolder> chunksToUnload;
   private final LongSet field_219254_h;
   private final ServerWorld world;
   private final ServerWorldLightManager lightManager;
   private final ThreadTaskExecutor<Runnable> mainThread;
   private final ChunkGenerator<?> generator;
   private final Supplier<DimensionSavedDataManager> field_219259_m;
   private final PointOfInterestManager field_219260_n;
   private final LongSet field_219261_o;
   private boolean field_219262_p;
   private final ChunkTaskPriorityQueueSorter field_219263_q;
   private final ITaskExecutor<ChunkTaskPriorityQueueSorter.FunctionEntry<Runnable>> field_219264_r;
   private final ITaskExecutor<ChunkTaskPriorityQueueSorter.FunctionEntry<Runnable>> field_219265_s;
   private final IChunkStatusListener field_219266_t;
   private final ChunkManager.ProxyTicketManager ticketManager;
   private final AtomicInteger field_219268_v;
   private final TemplateManager field_219269_w;
   private final File field_219270_x;
   private final PlayerGenerationTracker playerGenerationTracker;
   private final Int2ObjectMap<ChunkManager.EntityTracker> entities;
   private final Queue<Runnable> saveTasks;
   private int viewDistance;

   public ChunkManager(ServerWorld p_i51538_1_, File p_i51538_2_, DataFixer p_i51538_3_, TemplateManager p_i51538_4_, Executor p_i51538_5_, ThreadTaskExecutor<Runnable> p_i51538_6_, IChunkLightProvider p_i51538_7_, ChunkGenerator<?> p_i51538_8_, IChunkStatusListener p_i51538_9_, Supplier<DimensionSavedDataManager> p_i51538_10_, int p_i51538_11_) {
      super(new File(p_i51538_1_.getDimension().getType().getDirectory(p_i51538_2_), "region"), p_i51538_3_);
      this.field_219252_f = this.field_219251_e.clone();
      this.chunksToUnload = new Long2ObjectLinkedOpenHashMap();
      this.field_219254_h = new LongOpenHashSet();
      this.field_219261_o = new LongOpenHashSet();
      this.field_219268_v = new AtomicInteger();
      this.playerGenerationTracker = new PlayerGenerationTracker();
      this.entities = new Int2ObjectOpenHashMap();
      this.saveTasks = Queues.newConcurrentLinkedQueue();
      this.field_219269_w = p_i51538_4_;
      this.field_219270_x = p_i51538_1_.getDimension().getType().getDirectory(p_i51538_2_);
      this.world = p_i51538_1_;
      this.generator = p_i51538_8_;
      this.mainThread = p_i51538_6_;
      DelegatedTaskExecutor<Runnable> delegatedtaskexecutor = DelegatedTaskExecutor.create(p_i51538_5_, "worldgen");
      p_i51538_6_.getClass();
      ITaskExecutor<Runnable> itaskexecutor = ITaskExecutor.inline("main", p_i51538_6_::enqueue);
      this.field_219266_t = p_i51538_9_;
      DelegatedTaskExecutor<Runnable> delegatedtaskexecutor1 = DelegatedTaskExecutor.create(p_i51538_5_, "light");
      this.field_219263_q = new ChunkTaskPriorityQueueSorter(ImmutableList.of(delegatedtaskexecutor, itaskexecutor, delegatedtaskexecutor1), p_i51538_5_, Integer.MAX_VALUE);
      this.field_219264_r = this.field_219263_q.func_219087_a(delegatedtaskexecutor, false);
      this.field_219265_s = this.field_219263_q.func_219087_a(itaskexecutor, false);
      this.lightManager = new ServerWorldLightManager(p_i51538_7_, this, this.world.getDimension().hasSkyLight(), delegatedtaskexecutor1, this.field_219263_q.func_219087_a(delegatedtaskexecutor1, false));
      this.ticketManager = new ChunkManager.ProxyTicketManager(p_i51538_5_, p_i51538_6_);
      this.field_219259_m = p_i51538_10_;
      this.field_219260_n = new PointOfInterestManager(new File(this.field_219270_x, "poi"), p_i51538_3_);
      this.setViewDistance(p_i51538_11_);
   }

   private static double getDistanceSquaredToChunk(ChunkPos p_219217_0_, Entity p_219217_1_) {
      double d0 = (double)(p_219217_0_.x * 16 + 8);
      double d1 = (double)(p_219217_0_.z * 16 + 8);
      double d2 = d0 - p_219217_1_.func_226277_ct_();
      double d3 = d1 - p_219217_1_.func_226281_cx_();
      return d2 * d2 + d3 * d3;
   }

   private static int func_219215_b(ChunkPos p_219215_0_, ServerPlayerEntity p_219215_1_, boolean p_219215_2_) {
      int i;
      int j;
      if (p_219215_2_) {
         SectionPos sectionpos = p_219215_1_.getManagedSectionPos();
         i = sectionpos.getSectionX();
         j = sectionpos.getSectionZ();
      } else {
         i = MathHelper.floor(p_219215_1_.func_226277_ct_() / 16.0D);
         j = MathHelper.floor(p_219215_1_.func_226281_cx_() / 16.0D);
      }

      return getChunkDistance(p_219215_0_, i, j);
   }

   private static int getChunkDistance(ChunkPos p_219232_0_, int p_219232_1_, int p_219232_2_) {
      int i = p_219232_0_.x - p_219232_1_;
      int j = p_219232_0_.z - p_219232_2_;
      return Math.max(Math.abs(i), Math.abs(j));
   }

   protected ServerWorldLightManager getLightManager() {
      return this.lightManager;
   }

   @Nullable
   protected ChunkHolder func_219220_a(long p_219220_1_) {
      return (ChunkHolder)this.field_219251_e.get(p_219220_1_);
   }

   @Nullable
   protected ChunkHolder func_219219_b(long p_219219_1_) {
      return (ChunkHolder)this.field_219252_f.get(p_219219_1_);
   }

   protected IntSupplier func_219191_c(long p_219191_1_) {
      return () -> {
         ChunkHolder chunkholder = this.func_219219_b(p_219191_1_);
         return chunkholder == null ? ChunkTaskPriorityQueue.field_219419_a - 1 : Math.min(chunkholder.func_219281_j(), ChunkTaskPriorityQueue.field_219419_a - 1);
      };
   }

   @OnlyIn(Dist.CLIENT)
   public String func_219170_a(ChunkPos p_219170_1_) {
      ChunkHolder chunkholder = this.func_219219_b(p_219170_1_.asLong());
      if (chunkholder == null) {
         return "null";
      } else {
         String s = chunkholder.func_219299_i() + "\n";
         ChunkStatus chunkstatus = chunkholder.func_219285_d();
         IChunk ichunk = chunkholder.func_219287_e();
         if (chunkstatus != null) {
            s = s + "St: §" + chunkstatus.ordinal() + chunkstatus + '§' + "r\n";
         }

         if (ichunk != null) {
            s = s + "Ch: §" + ichunk.getStatus().ordinal() + ichunk.getStatus() + '§' + "r\n";
         }

         ChunkHolder.LocationType chunkholder$locationtype = chunkholder.func_219300_g();
         s = s + "§" + chunkholder$locationtype.ordinal() + chunkholder$locationtype;
         return s + '§' + "r";
      }
   }

   private CompletableFuture<Either<List<IChunk>, ChunkHolder.IChunkLoadingError>> func_219236_a(ChunkPos p_219236_1_, int p_219236_2_, IntFunction<ChunkStatus> p_219236_3_) {
      List<CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> list = Lists.newArrayList();
      int i = p_219236_1_.x;
      int j = p_219236_1_.z;

      for(int k = -p_219236_2_; k <= p_219236_2_; ++k) {
         for(int l = -p_219236_2_; l <= p_219236_2_; ++l) {
            int i1 = Math.max(Math.abs(l), Math.abs(k));
            final ChunkPos chunkpos = new ChunkPos(i + l, j + k);
            long j1 = chunkpos.asLong();
            ChunkHolder chunkholder = this.func_219220_a(j1);
            if (chunkholder == null) {
               return CompletableFuture.completedFuture(Either.right(new ChunkHolder.IChunkLoadingError() {
                  public String toString() {
                     return "Unloaded " + chunkpos.toString();
                  }
               }));
            }

            ChunkStatus chunkstatus = (ChunkStatus)p_219236_3_.apply(i1);
            CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = chunkholder.func_219276_a(chunkstatus, this);
            list.add(completablefuture);
         }
      }

      CompletableFuture<List<Either<IChunk, ChunkHolder.IChunkLoadingError>>> completablefuture1 = Util.gather(list);
      return completablefuture1.thenApply((p_lambda$func_219236_a$1_4_) -> {
         List<IChunk> list1 = Lists.newArrayList();
         final int k1 = 0;

         for(Iterator var7 = p_lambda$func_219236_a$1_4_.iterator(); var7.hasNext(); ++k1) {
            final Either<IChunk, ChunkHolder.IChunkLoadingError> either = (Either)var7.next();
            Optional<IChunk> optional = either.left();
            if (!optional.isPresent()) {
               return Either.right(new ChunkHolder.IChunkLoadingError() {
                  public String toString() {
                     return "Unloaded " + new ChunkPos(p_lambda$func_219236_a$1_1_ + k1 % (p_lambda$func_219236_a$1_2_ * 2 + 1), p_lambda$func_219236_a$1_3_ + k1 / (p_lambda$func_219236_a$1_2_ * 2 + 1)) + " " + ((ChunkHolder.IChunkLoadingError)either.right().get()).toString();
                  }
               });
            }

            list1.add(optional.get());
         }

         return Either.left(list1);
      });
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> func_219188_b(ChunkPos p_219188_1_) {
      return this.func_219236_a(p_219188_1_, 2, (p_lambda$func_219188_b$2_0_) -> {
         return ChunkStatus.FULL;
      }).thenApplyAsync((p_lambda$func_219188_b$4_0_) -> {
         return p_lambda$func_219188_b$4_0_.mapLeft((p_lambda$null$3_0_) -> {
            return (Chunk)p_lambda$null$3_0_.get(p_lambda$null$3_0_.size() / 2);
         });
      }, this.mainThread);
   }

   @Nullable
   private ChunkHolder func_219213_a(long p_219213_1_, int p_219213_3_, @Nullable ChunkHolder p_219213_4_, int p_219213_5_) {
      if (p_219213_5_ > MAX_LOADED_LEVEL && p_219213_3_ > MAX_LOADED_LEVEL) {
         return p_219213_4_;
      } else {
         if (p_219213_4_ != null) {
            p_219213_4_.func_219292_a(p_219213_3_);
         }

         if (p_219213_4_ != null) {
            if (p_219213_3_ > MAX_LOADED_LEVEL) {
               this.field_219261_o.add(p_219213_1_);
            } else {
               this.field_219261_o.remove(p_219213_1_);
            }
         }

         if (p_219213_3_ <= MAX_LOADED_LEVEL && p_219213_4_ == null) {
            p_219213_4_ = (ChunkHolder)this.chunksToUnload.remove(p_219213_1_);
            if (p_219213_4_ != null) {
               p_219213_4_.func_219292_a(p_219213_3_);
            } else {
               p_219213_4_ = new ChunkHolder(new ChunkPos(p_219213_1_), p_219213_3_, this.lightManager, this.field_219263_q, this);
            }

            this.field_219251_e.put(p_219213_1_, p_219213_4_);
            this.field_219262_p = true;
         }

         return p_219213_4_;
      }
   }

   public void close() throws IOException {
      try {
         this.field_219263_q.close();
         this.field_219260_n.close();
      } finally {
         super.close();
      }

   }

   protected void save(boolean p_219177_1_) {
      if (p_219177_1_) {
         List<ChunkHolder> list = (List)this.field_219252_f.values().stream().filter(ChunkHolder::func_219289_k).peek(ChunkHolder::func_219303_l).collect(Collectors.toList());
         MutableBoolean mutableboolean = new MutableBoolean();

         do {
            mutableboolean.setFalse();
            list.stream().map((p_lambda$save$5_1_) -> {
               CompletableFuture completablefuture;
               do {
                  completablefuture = p_lambda$save$5_1_.func_219302_f();
                  this.mainThread.driveUntil(completablefuture::isDone);
               } while(completablefuture != p_lambda$save$5_1_.func_219302_f());

               return (IChunk)completablefuture.join();
            }).filter((p_lambda$save$6_0_) -> {
               return p_lambda$save$6_0_ instanceof ChunkPrimerWrapper || p_lambda$save$6_0_ instanceof Chunk;
            }).filter(this::func_219229_a).forEach((p_lambda$save$7_1_) -> {
               mutableboolean.setTrue();
            });
         } while(mutableboolean.isTrue());

         this.scheduleUnloads(() -> {
            return true;
         });
         this.func_227079_i_();
         LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", this.field_219270_x.getName());
      } else {
         this.field_219252_f.values().stream().filter(ChunkHolder::func_219289_k).forEach((p_lambda$save$9_1_) -> {
            IChunk ichunk = (IChunk)p_lambda$save$9_1_.func_219302_f().getNow((IChunk)null);
            if (ichunk instanceof ChunkPrimerWrapper || ichunk instanceof Chunk) {
               this.func_219229_a(ichunk);
               p_lambda$save$9_1_.func_219303_l();
            }

         });
      }

   }

   protected void tick(BooleanSupplier p_219204_1_) {
      IProfiler iprofiler = this.world.getProfiler();
      iprofiler.startSection("poi");
      this.field_219260_n.func_219115_a(p_219204_1_);
      iprofiler.endStartSection("chunk_unload");
      if (!this.world.isSaveDisabled()) {
         this.scheduleUnloads(p_219204_1_);
         if (this.field_219251_e.isEmpty()) {
            DimensionManager.unloadWorld(this.world);
         }
      }

      iprofiler.endSection();
   }

   private void scheduleUnloads(BooleanSupplier p_223155_1_) {
      LongIterator longiterator = this.field_219261_o.iterator();

      for(int i = 0; longiterator.hasNext() && (p_223155_1_.getAsBoolean() || i < 200 || this.field_219261_o.size() > 2000); longiterator.remove()) {
         long j = longiterator.nextLong();
         ChunkHolder chunkholder = (ChunkHolder)this.field_219251_e.remove(j);
         if (chunkholder != null) {
            this.chunksToUnload.put(j, chunkholder);
            this.field_219262_p = true;
            ++i;
            this.scheduleSave(j, chunkholder);
         }
      }

      Runnable runnable;
      while((p_223155_1_.getAsBoolean() || this.saveTasks.size() > 2000) && (runnable = (Runnable)this.saveTasks.poll()) != null) {
         runnable.run();
      }

   }

   private void scheduleSave(long p_219212_1_, ChunkHolder p_219212_3_) {
      CompletableFuture<IChunk> completablefuture = p_219212_3_.func_219302_f();
      Consumer var10001 = (p_lambda$scheduleSave$10_5_) -> {
         CompletableFuture<IChunk> completablefuture1 = p_219212_3_.func_219302_f();
         if (completablefuture1 != completablefuture) {
            this.scheduleSave(p_219212_1_, p_219212_3_);
         } else if (this.chunksToUnload.remove(p_219212_1_, p_219212_3_) && p_lambda$scheduleSave$10_5_ != null) {
            if (p_lambda$scheduleSave$10_5_ instanceof Chunk) {
               ((Chunk)p_lambda$scheduleSave$10_5_).setLoaded(false);
               MinecraftForge.EVENT_BUS.post(new ChunkEvent.Unload((Chunk)p_lambda$scheduleSave$10_5_));
            }

            this.func_219229_a(p_lambda$scheduleSave$10_5_);
            if (this.field_219254_h.remove(p_219212_1_) && p_lambda$scheduleSave$10_5_ instanceof Chunk) {
               Chunk chunk = (Chunk)p_lambda$scheduleSave$10_5_;
               this.world.onChunkUnloading(chunk);
            }

            this.lightManager.updateChunkStatus(p_lambda$scheduleSave$10_5_.getPos());
            this.lightManager.func_215588_z_();
            this.field_219266_t.statusChanged(p_lambda$scheduleSave$10_5_.getPos(), (ChunkStatus)null);
         }

      };
      Queue var10002 = this.saveTasks;
      var10002.getClass();
      completablefuture.thenAcceptAsync(var10001, var10002::add).whenComplete((p_lambda$scheduleSave$11_1_, p_lambda$scheduleSave$11_2_) -> {
         if (p_lambda$scheduleSave$11_2_ != null) {
            LOGGER.error("Failed to save chunk " + p_219212_3_.getPosition(), p_lambda$scheduleSave$11_2_);
         }

      });
   }

   protected boolean func_219245_b() {
      if (!this.field_219262_p) {
         return false;
      } else {
         this.field_219252_f = this.field_219251_e.clone();
         this.field_219262_p = false;
         return true;
      }
   }

   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_219244_a(ChunkHolder p_219244_1_, ChunkStatus p_219244_2_) {
      ChunkPos chunkpos = p_219244_1_.getPosition();
      if (p_219244_2_ == ChunkStatus.EMPTY) {
         return this.func_223172_f(chunkpos);
      } else {
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = p_219244_1_.func_219276_a(p_219244_2_.getParent(), this);
         return completablefuture.thenComposeAsync((p_lambda$func_219244_a$13_4_) -> {
            Optional<IChunk> optional = p_lambda$func_219244_a$13_4_.left();
            if (!optional.isPresent()) {
               return CompletableFuture.completedFuture(p_lambda$func_219244_a$13_4_);
            } else {
               if (p_219244_2_ == ChunkStatus.LIGHT) {
                  this.ticketManager.registerWithLevel(TicketType.LIGHT, chunkpos, 33 + ChunkStatus.func_222599_a(ChunkStatus.FEATURES), chunkpos);
               }

               IChunk ichunk = (IChunk)optional.get();
               if (ichunk.getStatus().isAtLeast(p_219244_2_)) {
                  CompletableFuture completablefuture1;
                  if (p_219244_2_ == ChunkStatus.LIGHT) {
                     completablefuture1 = this.func_223156_b(p_219244_1_, p_219244_2_);
                  } else {
                     completablefuture1 = p_219244_2_.func_223201_a(this.world, this.field_219269_w, this.lightManager, (p_lambda$null$12_2_) -> {
                        return this.func_219200_b(p_219244_1_);
                     }, ichunk);
                  }

                  this.field_219266_t.statusChanged(chunkpos, p_219244_2_);
                  return completablefuture1;
               } else {
                  return this.func_223156_b(p_219244_1_, p_219244_2_);
               }
            }
         }, this.mainThread);
      }
   }

   private CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_223172_f(ChunkPos p_223172_1_) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            this.world.getProfiler().func_230035_c_("chunkLoad");
            CompoundNBT compoundnbt = this.loadChunkData(p_223172_1_);
            if (compoundnbt != null) {
               boolean flag = compoundnbt.contains("Level", 10) && compoundnbt.getCompound("Level").contains("Status", 8);
               if (flag) {
                  IChunk ichunk = ChunkSerializer.read(this.world, this.field_219269_w, this.field_219260_n, p_223172_1_, compoundnbt);
                  ichunk.setLastSaveTime(this.world.getGameTime());
                  MinecraftForge.EVENT_BUS.post(new ChunkEvent.Load(ichunk));
                  return Either.left(ichunk);
               }

               LOGGER.error("Chunk file at {} is missing level data, skipping", p_223172_1_);
            }
         } catch (ReportedException var5) {
            Throwable throwable = var5.getCause();
            if (!(throwable instanceof IOException)) {
               throw var5;
            }

            LOGGER.error("Couldn't load chunk {}", p_223172_1_, throwable);
         } catch (Exception var6) {
            LOGGER.error("Couldn't load chunk {}", p_223172_1_, var6);
         }

         return Either.left(new ChunkPrimer(p_223172_1_, UpgradeData.EMPTY));
      }, this.mainThread);
   }

   private CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_223156_b(ChunkHolder p_223156_1_, ChunkStatus p_223156_2_) {
      ChunkPos chunkpos = p_223156_1_.getPosition();
      CompletableFuture<Either<List<IChunk>, ChunkHolder.IChunkLoadingError>> completablefuture = this.func_219236_a(chunkpos, p_223156_2_.getTaskRange(), (p_lambda$func_223156_b$15_2_) -> {
         return this.func_219205_a(p_223156_2_, p_lambda$func_223156_b$15_2_);
      });
      this.world.getProfiler().func_230036_c_(() -> {
         return "chunkGenerate " + p_223156_2_.getName();
      });
      return completablefuture.thenComposeAsync((p_lambda$func_223156_b$20_4_) -> {
         return (CompletionStage)p_lambda$func_223156_b$20_4_.map((p_lambda$null$18_4_) -> {
            try {
               CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture1 = p_223156_2_.func_223198_a(this.world, this.generator, this.field_219269_w, this.lightManager, (p_lambda$null$17_2_) -> {
                  return this.func_219200_b(p_223156_1_);
               }, p_lambda$null$18_4_);
               this.field_219266_t.statusChanged(chunkpos, p_223156_2_);
               return completablefuture1;
            } catch (Exception var8) {
               CrashReport crashreport = CrashReport.makeCrashReport(var8, "Exception generating new chunk");
               CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
               crashreportcategory.addDetail("Location", (Object)String.format("%d,%d", chunkpos.x, chunkpos.z));
               crashreportcategory.addDetail("Position hash", (Object)ChunkPos.asLong(chunkpos.x, chunkpos.z));
               crashreportcategory.addDetail("Generator", (Object)this.generator);
               throw new ReportedException(crashreport);
            }
         }, (p_lambda$null$19_2_) -> {
            this.func_219209_c(chunkpos);
            return CompletableFuture.completedFuture(Either.right(p_lambda$null$19_2_));
         });
      }, (p_lambda$func_223156_b$21_2_) -> {
         this.field_219264_r.enqueue(ChunkTaskPriorityQueueSorter.func_219081_a(p_223156_1_, p_lambda$func_223156_b$21_2_));
      });
   }

   protected void func_219209_c(ChunkPos p_219209_1_) {
      this.mainThread.enqueue(Util.namedRunnable(() -> {
         this.ticketManager.releaseWithLevel(TicketType.LIGHT, p_219209_1_, 33 + ChunkStatus.func_222599_a(ChunkStatus.FEATURES), p_219209_1_);
      }, () -> {
         return "release light ticket " + p_219209_1_;
      }));
   }

   private ChunkStatus func_219205_a(ChunkStatus p_219205_1_, int p_219205_2_) {
      ChunkStatus chunkstatus;
      if (p_219205_2_ == 0) {
         chunkstatus = p_219205_1_.getParent();
      } else {
         chunkstatus = ChunkStatus.func_222581_a(ChunkStatus.func_222599_a(p_219205_1_) + p_219205_2_);
      }

      return chunkstatus;
   }

   private CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_219200_b(ChunkHolder p_219200_1_) {
      CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = p_219200_1_.func_219301_a(ChunkStatus.FULL.getParent());
      return completablefuture.thenApplyAsync((p_lambda$func_219200_b$26_2_) -> {
         ChunkStatus chunkstatus = ChunkHolder.func_219278_b(p_219200_1_.func_219299_i());
         return !chunkstatus.isAtLeast(ChunkStatus.FULL) ? ChunkHolder.MISSING_CHUNK : p_lambda$func_219200_b$26_2_.mapLeft((p_lambda$null$25_2_) -> {
            ChunkPos chunkpos = p_219200_1_.getPosition();
            Chunk chunk;
            if (p_lambda$null$25_2_ instanceof ChunkPrimerWrapper) {
               chunk = ((ChunkPrimerWrapper)p_lambda$null$25_2_).func_217336_u();
            } else {
               chunk = new Chunk(this.world, (ChunkPrimer)p_lambda$null$25_2_);
               p_219200_1_.func_219294_a(new ChunkPrimerWrapper(chunk));
            }

            chunk.func_217314_a(() -> {
               return ChunkHolder.func_219286_c(p_219200_1_.func_219299_i());
            });
            chunk.func_217318_w();
            if (this.field_219254_h.add(chunkpos.asLong())) {
               chunk.setLoaded(true);
               this.world.addTileEntities(chunk.getTileEntityMap().values());
               List<Entity> list = null;
               ClassInheritanceMultiMap<Entity>[] aclassinheritancemultimap = chunk.getEntityLists();
               int i = aclassinheritancemultimap.length;

               for(int j = 0; j < i; ++j) {
                  Iterator var9 = aclassinheritancemultimap[j].iterator();

                  while(var9.hasNext()) {
                     Entity entity = (Entity)var9.next();
                     if (!(entity instanceof PlayerEntity) && !this.world.addEntityIfNotDuplicate(entity)) {
                        if (list == null) {
                           list = Lists.newArrayList(new Entity[]{entity});
                        } else {
                           list.add(entity);
                        }
                     }
                  }
               }

               if (list != null) {
                  list.forEach(chunk::removeEntity);
               }

               MinecraftForge.EVENT_BUS.post(new ChunkEvent.Load(chunk));
            }

            return chunk;
         });
      }, (p_lambda$func_219200_b$27_2_) -> {
         ITaskExecutor var10000 = this.field_219265_s;
         long var10002 = p_219200_1_.getPosition().asLong();
         p_219200_1_.getClass();
         var10000.enqueue(ChunkTaskPriorityQueueSorter.func_219069_a(p_lambda$func_219200_b$27_2_, var10002, p_219200_1_::func_219299_i));
      });
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> func_219179_a(ChunkHolder p_219179_1_) {
      ChunkPos chunkpos = p_219179_1_.getPosition();
      CompletableFuture<Either<List<IChunk>, ChunkHolder.IChunkLoadingError>> completablefuture = this.func_219236_a(chunkpos, 1, (p_lambda$func_219179_a$28_0_) -> {
         return ChunkStatus.FULL;
      });
      CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> completablefuture1 = completablefuture.thenApplyAsync((p_lambda$func_219179_a$30_0_) -> {
         return p_lambda$func_219179_a$30_0_.flatMap((p_lambda$null$29_0_) -> {
            Chunk chunk = (Chunk)p_lambda$null$29_0_.get(p_lambda$null$29_0_.size() / 2);
            chunk.postProcess();
            return Either.left(chunk);
         });
      }, (p_lambda$func_219179_a$31_2_) -> {
         this.field_219265_s.enqueue(ChunkTaskPriorityQueueSorter.func_219081_a(p_219179_1_, p_lambda$func_219179_a$31_2_));
      });
      completablefuture1.thenAcceptAsync((p_lambda$func_219179_a$34_2_) -> {
         p_lambda$func_219179_a$34_2_.mapLeft((p_lambda$null$33_2_) -> {
            this.field_219268_v.getAndIncrement();
            IPacket<?>[] ipacket = new IPacket[2];
            this.getTrackingPlayers(chunkpos, false).forEach((p_lambda$null$32_3_) -> {
               this.sendChunkData(p_lambda$null$32_3_, ipacket, p_lambda$null$33_2_);
            });
            return Either.left(p_lambda$null$33_2_);
         });
      }, (p_lambda$func_219179_a$35_2_) -> {
         this.field_219265_s.enqueue(ChunkTaskPriorityQueueSorter.func_219081_a(p_219179_1_, p_lambda$func_219179_a$35_2_));
      });
      return completablefuture1;
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> func_222961_b(ChunkHolder p_222961_1_) {
      return p_222961_1_.func_219276_a(ChunkStatus.FULL, this).thenApplyAsync((p_lambda$func_222961_b$37_0_) -> {
         return p_lambda$func_222961_b$37_0_.mapLeft((p_lambda$null$36_0_) -> {
            Chunk chunk = (Chunk)p_lambda$null$36_0_;
            chunk.func_222879_B();
            return chunk;
         });
      }, (p_lambda$func_222961_b$38_2_) -> {
         this.field_219265_s.enqueue(ChunkTaskPriorityQueueSorter.func_219081_a(p_222961_1_, p_lambda$func_222961_b$38_2_));
      });
   }

   public int func_219174_c() {
      return this.field_219268_v.get();
   }

   private boolean func_219229_a(IChunk p_219229_1_) {
      this.field_219260_n.saveIfDirty(p_219229_1_.getPos());
      if (!p_219229_1_.isModified()) {
         return false;
      } else {
         try {
            this.world.checkSessionLock();
         } catch (SessionLockException var6) {
            LOGGER.error("Couldn't save chunk; already in use by another instance of Minecraft?", var6);
            return false;
         }

         p_219229_1_.setLastSaveTime(this.world.getGameTime());
         p_219229_1_.setModified(false);
         ChunkPos chunkpos = p_219229_1_.getPos();

         try {
            ChunkStatus chunkstatus = p_219229_1_.getStatus();
            CompoundNBT compoundnbt;
            if (chunkstatus.getType() != ChunkStatus.Type.LEVELCHUNK) {
               compoundnbt = this.loadChunkData(chunkpos);
               if (compoundnbt != null && ChunkSerializer.getChunkStatus(compoundnbt) == ChunkStatus.Type.LEVELCHUNK) {
                  return false;
               }

               if (chunkstatus == ChunkStatus.EMPTY && p_219229_1_.getStructureStarts().values().stream().noneMatch(StructureStart::isValid)) {
                  return false;
               }
            }

            this.world.getProfiler().func_230035_c_("chunkSave");
            compoundnbt = ChunkSerializer.write(this.world, p_219229_1_);
            MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Save(p_219229_1_, compoundnbt));
            this.writeChunk(chunkpos, compoundnbt);
            return true;
         } catch (Exception var5) {
            LOGGER.error("Failed to save chunk {},{}", chunkpos.x, chunkpos.z, var5);
            return false;
         }
      }
   }

   protected void setViewDistance(int p_219175_1_) {
      int i = MathHelper.clamp(p_219175_1_ + 1, 3, 33);
      if (i != this.viewDistance) {
         int j = this.viewDistance;
         this.viewDistance = i;
         this.ticketManager.setViewDistance(this.viewDistance);
         ObjectIterator var4 = this.field_219251_e.values().iterator();

         while(var4.hasNext()) {
            ChunkHolder chunkholder = (ChunkHolder)var4.next();
            ChunkPos chunkpos = chunkholder.getPosition();
            IPacket<?>[] ipacket = new IPacket[2];
            this.getTrackingPlayers(chunkpos, false).forEach((p_lambda$setViewDistance$39_4_) -> {
               int k = func_219215_b(chunkpos, p_lambda$setViewDistance$39_4_, true);
               boolean flag = k <= j;
               boolean flag1 = k <= this.viewDistance;
               this.setChunkLoadedAtClient(p_lambda$setViewDistance$39_4_, chunkpos, ipacket, flag, flag1);
            });
         }
      }

   }

   protected void setChunkLoadedAtClient(ServerPlayerEntity p_219199_1_, ChunkPos p_219199_2_, IPacket<?>[] p_219199_3_, boolean p_219199_4_, boolean p_219199_5_) {
      if (p_219199_1_.world == this.world) {
         if (p_219199_5_ && !p_219199_4_) {
            ChunkHolder chunkholder = this.func_219219_b(p_219199_2_.asLong());
            if (chunkholder != null) {
               Chunk chunk = chunkholder.func_219298_c();
               if (chunk != null) {
                  this.sendChunkData(p_219199_1_, p_219199_3_, chunk);
               }

               DebugPacketSender.func_218802_a(this.world, p_219199_2_);
            }
         }

         if (!p_219199_5_ && p_219199_4_) {
            p_219199_1_.sendChunkUnload(p_219199_2_);
         }
      }

   }

   public int getLoadedChunkCount() {
      return this.field_219252_f.size();
   }

   protected ChunkManager.ProxyTicketManager func_219246_e() {
      return this.ticketManager;
   }

   protected Iterable<ChunkHolder> func_223491_f() {
      return Iterables.unmodifiableIterable(this.field_219252_f.values());
   }

   void func_225406_a(Writer p_225406_1_) throws IOException {
      CSVWriter csvwriter = CSVWriter.func_225428_a().func_225423_a("x").func_225423_a("z").func_225423_a("level").func_225423_a("in_memory").func_225423_a("status").func_225423_a("full_status").func_225423_a("accessible_ready").func_225423_a("ticking_ready").func_225423_a("entity_ticking_ready").func_225423_a("ticket").func_225423_a("spawning").func_225423_a("entity_count").func_225423_a("block_entity_count").func_225422_a(p_225406_1_);
      ObjectBidirectionalIterator var3 = this.field_219252_f.long2ObjectEntrySet().iterator();

      while(var3.hasNext()) {
         Entry<ChunkHolder> entry = (Entry)var3.next();
         ChunkPos chunkpos = new ChunkPos(entry.getLongKey());
         ChunkHolder chunkholder = (ChunkHolder)entry.getValue();
         Optional<IChunk> optional = Optional.ofNullable(chunkholder.func_219287_e());
         Optional<Chunk> optional1 = optional.flatMap((p_lambda$func_225406_a$40_0_) -> {
            return p_lambda$func_225406_a$40_0_ instanceof Chunk ? Optional.of((Chunk)p_lambda$func_225406_a$40_0_) : Optional.empty();
         });
         csvwriter.func_225426_a(chunkpos.x, chunkpos.z, chunkholder.func_219299_i(), optional.isPresent(), optional.map(IChunk::getStatus).orElse((ChunkStatus)null), optional1.map(Chunk::func_217321_u).orElse((ChunkHolder.LocationType)null), func_225402_a(chunkholder.func_223492_c()), func_225402_a(chunkholder.func_219296_a()), func_225402_a(chunkholder.func_219297_b()), this.ticketManager.func_225413_c(entry.getLongKey()), !this.isOutsideSpawningRadius(chunkpos), optional1.map((p_lambda$func_225406_a$41_0_) -> {
            return Stream.of(p_lambda$func_225406_a$41_0_.getEntityLists()).mapToInt(ClassInheritanceMultiMap::size).sum();
         }).orElse(0), optional1.map((p_lambda$func_225406_a$42_0_) -> {
            return p_lambda$func_225406_a$42_0_.getTileEntityMap().size();
         }).orElse(0));
      }

   }

   private static String func_225402_a(CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> p_225402_0_) {
      try {
         Either<Chunk, ChunkHolder.IChunkLoadingError> either = (Either)p_225402_0_.getNow((Either)null);
         return either != null ? (String)either.map((p_lambda$func_225402_a$43_0_) -> {
            return "done";
         }, (p_lambda$func_225402_a$44_0_) -> {
            return "unloaded";
         }) : "not completed";
      } catch (CompletionException var2) {
         return "failed " + var2.getCause().getMessage();
      } catch (CancellationException var3) {
         return "cancelled";
      }
   }

   @Nullable
   private CompoundNBT loadChunkData(ChunkPos p_219178_1_) throws IOException {
      CompoundNBT compoundnbt = this.func_227078_e_(p_219178_1_);
      return compoundnbt == null ? null : this.updateChunkData(this.world.getDimension().getType(), this.field_219259_m, compoundnbt);
   }

   boolean isOutsideSpawningRadius(ChunkPos p_219243_1_) {
      long i = p_219243_1_.asLong();
      return !this.ticketManager.func_223494_d(i) ? true : this.playerGenerationTracker.getGeneratingPlayers(i).noneMatch((p_lambda$isOutsideSpawningRadius$45_1_) -> {
         return !p_lambda$isOutsideSpawningRadius$45_1_.isSpectator() && getDistanceSquaredToChunk(p_219243_1_, p_lambda$isOutsideSpawningRadius$45_1_) < 16384.0D;
      });
   }

   private boolean cannotGenerateChunks(ServerPlayerEntity p_219187_1_) {
      return p_219187_1_.isSpectator() && !this.world.getGameRules().getBoolean(GameRules.SPECTATORS_GENERATE_CHUNKS);
   }

   void setPlayerTracking(ServerPlayerEntity p_219234_1_, boolean p_219234_2_) {
      boolean flag = this.cannotGenerateChunks(p_219234_1_);
      boolean flag1 = this.playerGenerationTracker.cannotGenerateChunks(p_219234_1_);
      int i = MathHelper.floor(p_219234_1_.func_226277_ct_()) >> 4;
      int j = MathHelper.floor(p_219234_1_.func_226281_cx_()) >> 4;
      if (p_219234_2_) {
         this.playerGenerationTracker.addPlayer(ChunkPos.asLong(i, j), p_219234_1_, flag);
         this.func_223489_c(p_219234_1_);
         if (!flag) {
            this.ticketManager.updatePlayerPosition(SectionPos.from((Entity)p_219234_1_), p_219234_1_);
         }
      } else {
         SectionPos sectionpos = p_219234_1_.getManagedSectionPos();
         this.playerGenerationTracker.removePlayer(sectionpos.asChunkPos().asLong(), p_219234_1_);
         if (!flag1) {
            this.ticketManager.removePlayer(sectionpos, p_219234_1_);
         }
      }

      for(int l = i - this.viewDistance; l <= i + this.viewDistance; ++l) {
         for(int k = j - this.viewDistance; k <= j + this.viewDistance; ++k) {
            ChunkPos chunkpos = new ChunkPos(l, k);
            ForgeEventFactory.fireChunkWatch(p_219234_2_, p_219234_1_, chunkpos, this.world);
            this.setChunkLoadedAtClient(p_219234_1_, chunkpos, new IPacket[2], !p_219234_2_, p_219234_2_);
         }
      }

   }

   private SectionPos func_223489_c(ServerPlayerEntity p_223489_1_) {
      SectionPos sectionpos = SectionPos.from((Entity)p_223489_1_);
      p_223489_1_.setManagedSectionPos(sectionpos);
      p_223489_1_.connection.sendPacket(new SUpdateChunkPositionPacket(sectionpos.getSectionX(), sectionpos.getSectionZ()));
      return sectionpos;
   }

   public void updatePlayerPosition(ServerPlayerEntity p_219183_1_) {
      ObjectIterator var2 = this.entities.values().iterator();

      while(var2.hasNext()) {
         ChunkManager.EntityTracker chunkmanager$entitytracker = (ChunkManager.EntityTracker)var2.next();
         if (chunkmanager$entitytracker.entity == p_219183_1_) {
            chunkmanager$entitytracker.updateTrackingState(this.world.getPlayers());
         } else {
            chunkmanager$entitytracker.updateTrackingState(p_219183_1_);
         }
      }

      int l1 = MathHelper.floor(p_219183_1_.func_226277_ct_()) >> 4;
      int i2 = MathHelper.floor(p_219183_1_.func_226281_cx_()) >> 4;
      SectionPos sectionpos = p_219183_1_.getManagedSectionPos();
      SectionPos sectionpos1 = SectionPos.from((Entity)p_219183_1_);
      long i = sectionpos.asChunkPos().asLong();
      long j = sectionpos1.asChunkPos().asLong();
      boolean flag = this.playerGenerationTracker.func_225419_d(p_219183_1_);
      boolean flag1 = this.cannotGenerateChunks(p_219183_1_);
      boolean flag2 = sectionpos.asLong() != sectionpos1.asLong();
      if (flag2 || flag != flag1) {
         this.func_223489_c(p_219183_1_);
         if (!flag) {
            this.ticketManager.removePlayer(sectionpos, p_219183_1_);
         }

         if (!flag1) {
            this.ticketManager.updatePlayerPosition(sectionpos1, p_219183_1_);
         }

         if (!flag && flag1) {
            this.playerGenerationTracker.disableGeneration(p_219183_1_);
         }

         if (flag && !flag1) {
            this.playerGenerationTracker.enableGeneration(p_219183_1_);
         }

         if (i != j) {
            this.playerGenerationTracker.updatePlayerPosition(i, j, p_219183_1_);
         }
      }

      int k = sectionpos.getSectionX();
      int l = sectionpos.getSectionZ();
      int j2;
      int l2;
      if (Math.abs(k - l1) <= this.viewDistance * 2 && Math.abs(l - i2) <= this.viewDistance * 2) {
         j2 = Math.min(l1, k) - this.viewDistance;
         l2 = Math.min(i2, l) - this.viewDistance;
         int j3 = Math.max(l1, k) + this.viewDistance;
         int k3 = Math.max(i2, l) + this.viewDistance;

         for(int l3 = j2; l3 <= j3; ++l3) {
            for(int k1 = l2; k1 <= k3; ++k1) {
               ChunkPos chunkpos1 = new ChunkPos(l3, k1);
               boolean flag5 = getChunkDistance(chunkpos1, k, l) <= this.viewDistance;
               boolean flag6 = getChunkDistance(chunkpos1, l1, i2) <= this.viewDistance;
               this.setChunkLoadedAtClient(p_219183_1_, chunkpos1, new IPacket[2], flag5, flag6);
            }
         }
      } else {
         ChunkPos chunkpos2;
         boolean flag7;
         boolean flag8;
         for(j2 = k - this.viewDistance; j2 <= k + this.viewDistance; ++j2) {
            for(l2 = l - this.viewDistance; l2 <= l + this.viewDistance; ++l2) {
               chunkpos2 = new ChunkPos(j2, l2);
               flag7 = true;
               flag8 = false;
               this.setChunkLoadedAtClient(p_219183_1_, chunkpos2, new IPacket[2], true, false);
            }
         }

         for(j2 = l1 - this.viewDistance; j2 <= l1 + this.viewDistance; ++j2) {
            for(l2 = i2 - this.viewDistance; l2 <= i2 + this.viewDistance; ++l2) {
               chunkpos2 = new ChunkPos(j2, l2);
               flag7 = false;
               flag8 = true;
               this.setChunkLoadedAtClient(p_219183_1_, chunkpos2, new IPacket[2], false, true);
            }
         }
      }

   }

   public Stream<ServerPlayerEntity> getTrackingPlayers(ChunkPos p_219097_1_, boolean p_219097_2_) {
      return this.playerGenerationTracker.getGeneratingPlayers(p_219097_1_.asLong()).filter((p_lambda$getTrackingPlayers$46_3_) -> {
         int i = func_219215_b(p_219097_1_, p_lambda$getTrackingPlayers$46_3_, true);
         if (i > this.viewDistance) {
            return false;
         } else {
            return !p_219097_2_ || i == this.viewDistance;
         }
      });
   }

   protected void track(Entity p_219210_1_) {
      if (!(p_219210_1_ instanceof EnderDragonPartEntity) && !(p_219210_1_ instanceof LightningBoltEntity)) {
         EntityType<?> entitytype = p_219210_1_.getType();
         int i = entitytype.getTrackingRange() * 16;
         int j = entitytype.getUpdateFrequency();
         if (this.entities.containsKey(p_219210_1_.getEntityId())) {
            throw (IllegalStateException)Util.func_229757_c_(new IllegalStateException("Entity is already tracked!"));
         }

         ChunkManager.EntityTracker chunkmanager$entitytracker = new ChunkManager.EntityTracker(p_219210_1_, i, j, entitytype.shouldSendVelocityUpdates());
         this.entities.put(p_219210_1_.getEntityId(), chunkmanager$entitytracker);
         chunkmanager$entitytracker.updateTrackingState(this.world.getPlayers());
         if (p_219210_1_ instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_219210_1_;
            this.setPlayerTracking(serverplayerentity, true);
            ObjectIterator var7 = this.entities.values().iterator();

            while(var7.hasNext()) {
               ChunkManager.EntityTracker chunkmanager$entitytracker1 = (ChunkManager.EntityTracker)var7.next();
               if (chunkmanager$entitytracker1.entity != serverplayerentity) {
                  chunkmanager$entitytracker1.updateTrackingState(serverplayerentity);
               }
            }
         }
      }

   }

   protected void untrack(Entity p_219231_1_) {
      if (p_219231_1_ instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_219231_1_;
         this.setPlayerTracking(serverplayerentity, false);
         ObjectIterator var3 = this.entities.values().iterator();

         while(var3.hasNext()) {
            ChunkManager.EntityTracker chunkmanager$entitytracker = (ChunkManager.EntityTracker)var3.next();
            chunkmanager$entitytracker.func_219399_a(serverplayerentity);
         }
      }

      ChunkManager.EntityTracker chunkmanager$entitytracker1 = (ChunkManager.EntityTracker)this.entities.remove(p_219231_1_.getEntityId());
      if (chunkmanager$entitytracker1 != null) {
         chunkmanager$entitytracker1.removeAllTrackers();
      }

   }

   protected void tickEntityTracker() {
      List<ServerPlayerEntity> list = Lists.newArrayList();
      List<ServerPlayerEntity> list1 = this.world.getPlayers();

      ObjectIterator var3;
      ChunkManager.EntityTracker chunkmanager$entitytracker1;
      for(var3 = this.entities.values().iterator(); var3.hasNext(); chunkmanager$entitytracker1.entry.tick()) {
         chunkmanager$entitytracker1 = (ChunkManager.EntityTracker)var3.next();
         SectionPos sectionpos = chunkmanager$entitytracker1.pos;
         SectionPos sectionpos1 = SectionPos.from(chunkmanager$entitytracker1.entity);
         if (!Objects.equals(sectionpos, sectionpos1)) {
            chunkmanager$entitytracker1.updateTrackingState(list1);
            Entity entity = chunkmanager$entitytracker1.entity;
            if (entity instanceof ServerPlayerEntity) {
               list.add((ServerPlayerEntity)entity);
            }

            chunkmanager$entitytracker1.pos = sectionpos1;
         }
      }

      if (!list.isEmpty()) {
         var3 = this.entities.values().iterator();

         while(var3.hasNext()) {
            chunkmanager$entitytracker1 = (ChunkManager.EntityTracker)var3.next();
            chunkmanager$entitytracker1.updateTrackingState((List)list);
         }
      }

   }

   protected void sendToAllTracking(Entity p_219222_1_, IPacket<?> p_219222_2_) {
      ChunkManager.EntityTracker chunkmanager$entitytracker = (ChunkManager.EntityTracker)this.entities.get(p_219222_1_.getEntityId());
      if (chunkmanager$entitytracker != null) {
         chunkmanager$entitytracker.sendToAllTracking(p_219222_2_);
      }

   }

   protected void sendToTrackingAndSelf(Entity p_219225_1_, IPacket<?> p_219225_2_) {
      ChunkManager.EntityTracker chunkmanager$entitytracker = (ChunkManager.EntityTracker)this.entities.get(p_219225_1_.getEntityId());
      if (chunkmanager$entitytracker != null) {
         chunkmanager$entitytracker.sendToTrackingAndSelf(p_219225_2_);
      }

   }

   private void sendChunkData(ServerPlayerEntity p_219180_1_, IPacket<?>[] p_219180_2_, Chunk p_219180_3_) {
      if (p_219180_2_[0] == null) {
         p_219180_2_[0] = new SChunkDataPacket(p_219180_3_, 65535);
         p_219180_2_[1] = new SUpdateLightPacket(p_219180_3_.getPos(), this.lightManager);
      }

      p_219180_1_.sendChunkLoad(p_219180_3_.getPos(), p_219180_2_[0], p_219180_2_[1]);
      DebugPacketSender.func_218802_a(this.world, p_219180_3_.getPos());
      List<Entity> list = Lists.newArrayList();
      List<Entity> list1 = Lists.newArrayList();
      ObjectIterator var6 = this.entities.values().iterator();

      while(var6.hasNext()) {
         ChunkManager.EntityTracker chunkmanager$entitytracker = (ChunkManager.EntityTracker)var6.next();
         Entity entity = chunkmanager$entitytracker.entity;
         if (entity != p_219180_1_ && entity.chunkCoordX == p_219180_3_.getPos().x && entity.chunkCoordZ == p_219180_3_.getPos().z) {
            chunkmanager$entitytracker.updateTrackingState(p_219180_1_);
            if (entity instanceof MobEntity && ((MobEntity)entity).getLeashHolder() != null) {
               list.add(entity);
            }

            if (!entity.getPassengers().isEmpty()) {
               list1.add(entity);
            }
         }
      }

      Iterator var9;
      Entity entity2;
      if (!list.isEmpty()) {
         var9 = list.iterator();

         while(var9.hasNext()) {
            entity2 = (Entity)var9.next();
            p_219180_1_.connection.sendPacket(new SMountEntityPacket(entity2, ((MobEntity)entity2).getLeashHolder()));
         }
      }

      if (!list1.isEmpty()) {
         var9 = list1.iterator();

         while(var9.hasNext()) {
            entity2 = (Entity)var9.next();
            p_219180_1_.connection.sendPacket(new SSetPassengersPacket(entity2));
         }
      }

   }

   protected PointOfInterestManager func_219189_h() {
      return this.field_219260_n;
   }

   public CompletableFuture<Void> func_222973_a(Chunk p_222973_1_) {
      return this.mainThread.runAsync(() -> {
         p_222973_1_.func_222880_a(this.world);
      });
   }

   class ProxyTicketManager extends TicketManager {
      protected ProxyTicketManager(Executor p_i50469_2_, Executor p_i50469_3_) {
         super(p_i50469_2_, p_i50469_3_);
      }

      protected boolean func_219371_a(long p_219371_1_) {
         return ChunkManager.this.field_219261_o.contains(p_219371_1_);
      }

      @Nullable
      protected ChunkHolder func_219335_b(long p_219335_1_) {
         return ChunkManager.this.func_219220_a(p_219335_1_);
      }

      @Nullable
      protected ChunkHolder func_219372_a(long p_219372_1_, int p_219372_3_, @Nullable ChunkHolder p_219372_4_, int p_219372_5_) {
         return ChunkManager.this.func_219213_a(p_219372_1_, p_219372_3_, p_219372_4_, p_219372_5_);
      }
   }

   class EntityTracker {
      private final TrackedEntity entry;
      private final Entity entity;
      private final int range;
      private SectionPos pos;
      private final Set<ServerPlayerEntity> trackingPlayers = Sets.newHashSet();

      public EntityTracker(Entity p_i50468_2_, int p_i50468_3_, int p_i50468_4_, boolean p_i50468_5_) {
         this.entry = new TrackedEntity(ChunkManager.this.world, p_i50468_2_, p_i50468_4_, p_i50468_5_, this::sendToAllTracking);
         this.entity = p_i50468_2_;
         this.range = p_i50468_3_;
         this.pos = SectionPos.from(p_i50468_2_);
      }

      public boolean equals(Object p_equals_1_) {
         if (p_equals_1_ instanceof ChunkManager.EntityTracker) {
            return ((ChunkManager.EntityTracker)p_equals_1_).entity.getEntityId() == this.entity.getEntityId();
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.entity.getEntityId();
      }

      public void sendToAllTracking(IPacket<?> p_219391_1_) {
         Iterator var2 = this.trackingPlayers.iterator();

         while(var2.hasNext()) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)var2.next();
            serverplayerentity.connection.sendPacket(p_219391_1_);
         }

      }

      public void sendToTrackingAndSelf(IPacket<?> p_219392_1_) {
         this.sendToAllTracking(p_219392_1_);
         if (this.entity instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity)this.entity).connection.sendPacket(p_219392_1_);
         }

      }

      public void removeAllTrackers() {
         Iterator var1 = this.trackingPlayers.iterator();

         while(var1.hasNext()) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)var1.next();
            this.entry.untrack(serverplayerentity);
         }

      }

      public void func_219399_a(ServerPlayerEntity p_219399_1_) {
         if (this.trackingPlayers.remove(p_219399_1_)) {
            this.entry.untrack(p_219399_1_);
         }

      }

      public void updateTrackingState(ServerPlayerEntity p_219400_1_) {
         if (p_219400_1_ != this.entity) {
            Vec3d vec3d = p_219400_1_.getPositionVec().subtract(this.entry.func_219456_b());
            int i = Math.min(this.func_229843_b_(), (ChunkManager.this.viewDistance - 1) * 16);
            boolean flag = vec3d.x >= (double)(-i) && vec3d.x <= (double)i && vec3d.z >= (double)(-i) && vec3d.z <= (double)i && this.entity.isSpectatedByPlayer(p_219400_1_);
            if (flag) {
               boolean flag1 = this.entity.forceSpawn;
               if (!flag1) {
                  ChunkPos chunkpos = new ChunkPos(this.entity.chunkCoordX, this.entity.chunkCoordZ);
                  ChunkHolder chunkholder = ChunkManager.this.func_219219_b(chunkpos.asLong());
                  if (chunkholder != null && chunkholder.func_219298_c() != null) {
                     flag1 = ChunkManager.func_219215_b(chunkpos, p_219400_1_, false) <= ChunkManager.this.viewDistance;
                  }
               }

               if (flag1 && this.trackingPlayers.add(p_219400_1_)) {
                  this.entry.track(p_219400_1_);
               }
            } else if (this.trackingPlayers.remove(p_219400_1_)) {
               this.entry.untrack(p_219400_1_);
            }
         }

      }

      private int func_229843_b_() {
         Collection<Entity> collection = this.entity.getRecursivePassengers();
         int i = this.range;
         Iterator var3 = collection.iterator();

         while(var3.hasNext()) {
            Entity entity = (Entity)var3.next();
            int j = entity.getType().getTrackingRange() * 16;
            if (j > i) {
               i = j;
            }
         }

         return i;
      }

      public void updateTrackingState(List<ServerPlayerEntity> p_219397_1_) {
         Iterator var2 = p_219397_1_.iterator();

         while(var2.hasNext()) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)var2.next();
            this.updateTrackingState(serverplayerentity);
         }

      }
   }
}
