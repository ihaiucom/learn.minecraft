package net.minecraft.world.server;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ServerChunkProvider extends AbstractChunkProvider {
   private static final int field_217238_b = (int)Math.pow(17.0D, 2.0D);
   private static final List<ChunkStatus> field_217239_c = ChunkStatus.getAll();
   private final TicketManager ticketManager;
   public final ChunkGenerator<?> generator;
   public final ServerWorld world;
   private final Thread mainThread;
   private final ServerWorldLightManager lightManager;
   private final ServerChunkProvider.ChunkExecutor executor;
   public final ChunkManager chunkManager;
   private final DimensionSavedDataManager savedData;
   private long lastGameTime;
   private boolean spawnHostiles = true;
   private boolean spawnPassives = true;
   private final long[] recentPositions = new long[4];
   private final ChunkStatus[] recentStatuses = new ChunkStatus[4];
   private final IChunk[] recentChunks = new IChunk[4];

   public ServerChunkProvider(ServerWorld p_i51537_1_, File p_i51537_2_, DataFixer p_i51537_3_, TemplateManager p_i51537_4_, Executor p_i51537_5_, ChunkGenerator<?> p_i51537_6_, int p_i51537_7_, IChunkStatusListener p_i51537_8_, Supplier<DimensionSavedDataManager> p_i51537_9_) {
      this.world = p_i51537_1_;
      this.executor = new ServerChunkProvider.ChunkExecutor(p_i51537_1_);
      this.generator = p_i51537_6_;
      this.mainThread = Thread.currentThread();
      File lvt_10_1_ = p_i51537_1_.getDimension().getType().getDirectory(p_i51537_2_);
      File lvt_11_1_ = new File(lvt_10_1_, "data");
      lvt_11_1_.mkdirs();
      this.savedData = new DimensionSavedDataManager(lvt_11_1_, p_i51537_3_);
      this.chunkManager = new ChunkManager(p_i51537_1_, p_i51537_2_, p_i51537_3_, p_i51537_4_, p_i51537_5_, this.executor, this, this.getChunkGenerator(), p_i51537_8_, p_i51537_9_, p_i51537_7_);
      this.lightManager = this.chunkManager.getLightManager();
      this.ticketManager = this.chunkManager.func_219246_e();
      this.invalidateCaches();
   }

   public ServerWorldLightManager getLightManager() {
      return this.lightManager;
   }

   @Nullable
   private ChunkHolder func_217213_a(long p_217213_1_) {
      return this.chunkManager.func_219219_b(p_217213_1_);
   }

   public int func_217229_b() {
      return this.chunkManager.func_219174_c();
   }

   private void func_225315_a(long p_225315_1_, IChunk p_225315_3_, ChunkStatus p_225315_4_) {
      for(int lvt_5_1_ = 3; lvt_5_1_ > 0; --lvt_5_1_) {
         this.recentPositions[lvt_5_1_] = this.recentPositions[lvt_5_1_ - 1];
         this.recentStatuses[lvt_5_1_] = this.recentStatuses[lvt_5_1_ - 1];
         this.recentChunks[lvt_5_1_] = this.recentChunks[lvt_5_1_ - 1];
      }

      this.recentPositions[0] = p_225315_1_;
      this.recentStatuses[0] = p_225315_4_;
      this.recentChunks[0] = p_225315_3_;
   }

   @Nullable
   public IChunk getChunk(int p_212849_1_, int p_212849_2_, ChunkStatus p_212849_3_, boolean p_212849_4_) {
      if (Thread.currentThread() != this.mainThread) {
         return (IChunk)CompletableFuture.supplyAsync(() -> {
            return this.getChunk(p_212849_1_, p_212849_2_, p_212849_3_, p_212849_4_);
         }, this.executor).join();
      } else {
         IProfiler lvt_5_1_ = this.world.getProfiler();
         lvt_5_1_.func_230035_c_("getChunk");
         long lvt_6_1_ = ChunkPos.asLong(p_212849_1_, p_212849_2_);

         IChunk lvt_9_1_;
         for(int lvt_8_1_ = 0; lvt_8_1_ < 4; ++lvt_8_1_) {
            if (lvt_6_1_ == this.recentPositions[lvt_8_1_] && p_212849_3_ == this.recentStatuses[lvt_8_1_]) {
               lvt_9_1_ = this.recentChunks[lvt_8_1_];
               if (lvt_9_1_ != null || !p_212849_4_) {
                  return lvt_9_1_;
               }
            }
         }

         lvt_5_1_.func_230035_c_("getChunkCacheMiss");
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> lvt_8_2_ = this.func_217233_c(p_212849_1_, p_212849_2_, p_212849_3_, p_212849_4_);
         this.executor.driveUntil(lvt_8_2_::isDone);
         lvt_9_1_ = (IChunk)((Either)lvt_8_2_.join()).map((p_222874_0_) -> {
            return p_222874_0_;
         }, (p_222870_1_) -> {
            if (p_212849_4_) {
               throw (IllegalStateException)Util.func_229757_c_(new IllegalStateException("Chunk not there when requested: " + p_222870_1_));
            } else {
               return null;
            }
         });
         this.func_225315_a(lvt_6_1_, lvt_9_1_, p_212849_3_);
         return lvt_9_1_;
      }
   }

   @Nullable
   public Chunk func_225313_a(int p_225313_1_, int p_225313_2_) {
      if (Thread.currentThread() != this.mainThread) {
         return null;
      } else {
         this.world.getProfiler().func_230035_c_("getChunkNow");
         long lvt_3_1_ = ChunkPos.asLong(p_225313_1_, p_225313_2_);

         for(int lvt_5_1_ = 0; lvt_5_1_ < 4; ++lvt_5_1_) {
            if (lvt_3_1_ == this.recentPositions[lvt_5_1_] && this.recentStatuses[lvt_5_1_] == ChunkStatus.FULL) {
               IChunk lvt_6_1_ = this.recentChunks[lvt_5_1_];
               return lvt_6_1_ instanceof Chunk ? (Chunk)lvt_6_1_ : null;
            }
         }

         ChunkHolder lvt_5_2_ = this.func_217213_a(lvt_3_1_);
         if (lvt_5_2_ == null) {
            return null;
         } else {
            Either<IChunk, ChunkHolder.IChunkLoadingError> lvt_6_2_ = (Either)lvt_5_2_.func_225410_b(ChunkStatus.FULL).getNow((Object)null);
            if (lvt_6_2_ == null) {
               return null;
            } else {
               IChunk lvt_7_1_ = (IChunk)lvt_6_2_.left().orElse((Object)null);
               if (lvt_7_1_ != null) {
                  this.func_225315_a(lvt_3_1_, lvt_7_1_, ChunkStatus.FULL);
                  if (lvt_7_1_ instanceof Chunk) {
                     return (Chunk)lvt_7_1_;
                  }
               }

               return null;
            }
         }
      }
   }

   private void invalidateCaches() {
      Arrays.fill(this.recentPositions, ChunkPos.SENTINEL);
      Arrays.fill(this.recentStatuses, (Object)null);
      Arrays.fill(this.recentChunks, (Object)null);
   }

   @OnlyIn(Dist.CLIENT)
   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_217232_b(int p_217232_1_, int p_217232_2_, ChunkStatus p_217232_3_, boolean p_217232_4_) {
      boolean lvt_5_1_ = Thread.currentThread() == this.mainThread;
      CompletableFuture lvt_6_2_;
      if (lvt_5_1_) {
         lvt_6_2_ = this.func_217233_c(p_217232_1_, p_217232_2_, p_217232_3_, p_217232_4_);
         this.executor.driveUntil(lvt_6_2_::isDone);
      } else {
         lvt_6_2_ = CompletableFuture.supplyAsync(() -> {
            return this.func_217233_c(p_217232_1_, p_217232_2_, p_217232_3_, p_217232_4_);
         }, this.executor).thenCompose((p_217211_0_) -> {
            return p_217211_0_;
         });
      }

      return lvt_6_2_;
   }

   private CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_217233_c(int p_217233_1_, int p_217233_2_, ChunkStatus p_217233_3_, boolean p_217233_4_) {
      ChunkPos lvt_5_1_ = new ChunkPos(p_217233_1_, p_217233_2_);
      long lvt_6_1_ = lvt_5_1_.asLong();
      int lvt_8_1_ = 33 + ChunkStatus.func_222599_a(p_217233_3_);
      ChunkHolder lvt_9_1_ = this.func_217213_a(lvt_6_1_);
      if (p_217233_4_) {
         this.ticketManager.registerWithLevel(TicketType.UNKNOWN, lvt_5_1_, lvt_8_1_, lvt_5_1_);
         if (this.func_217224_a(lvt_9_1_, lvt_8_1_)) {
            IProfiler lvt_10_1_ = this.world.getProfiler();
            lvt_10_1_.startSection("chunkLoad");
            this.func_217235_l();
            lvt_9_1_ = this.func_217213_a(lvt_6_1_);
            lvt_10_1_.endSection();
            if (this.func_217224_a(lvt_9_1_, lvt_8_1_)) {
               throw (IllegalStateException)Util.func_229757_c_(new IllegalStateException("No chunk holder after ticket has been added"));
            }
         }
      }

      return this.func_217224_a(lvt_9_1_, lvt_8_1_) ? ChunkHolder.MISSING_CHUNK_FUTURE : lvt_9_1_.func_219276_a(p_217233_3_, this.chunkManager);
   }

   private boolean func_217224_a(@Nullable ChunkHolder p_217224_1_, int p_217224_2_) {
      return p_217224_1_ == null || p_217224_1_.func_219299_i() > p_217224_2_;
   }

   public boolean chunkExists(int p_73149_1_, int p_73149_2_) {
      ChunkHolder lvt_3_1_ = this.func_217213_a((new ChunkPos(p_73149_1_, p_73149_2_)).asLong());
      int lvt_4_1_ = 33 + ChunkStatus.func_222599_a(ChunkStatus.FULL);
      return !this.func_217224_a(lvt_3_1_, lvt_4_1_);
   }

   public IBlockReader getChunkForLight(int p_217202_1_, int p_217202_2_) {
      long lvt_3_1_ = ChunkPos.asLong(p_217202_1_, p_217202_2_);
      ChunkHolder lvt_5_1_ = this.func_217213_a(lvt_3_1_);
      if (lvt_5_1_ == null) {
         return null;
      } else {
         int lvt_6_1_ = field_217239_c.size() - 1;

         while(true) {
            ChunkStatus lvt_7_1_ = (ChunkStatus)field_217239_c.get(lvt_6_1_);
            Optional<IChunk> lvt_8_1_ = ((Either)lvt_5_1_.func_219301_a(lvt_7_1_).getNow(ChunkHolder.MISSING_CHUNK)).left();
            if (lvt_8_1_.isPresent()) {
               return (IBlockReader)lvt_8_1_.get();
            }

            if (lvt_7_1_ == ChunkStatus.LIGHT.getParent()) {
               return null;
            }

            --lvt_6_1_;
         }
      }
   }

   public World getWorld() {
      return this.world;
   }

   public boolean func_217234_d() {
      return this.executor.driveOne();
   }

   private boolean func_217235_l() {
      boolean lvt_1_1_ = this.ticketManager.func_219353_a(this.chunkManager);
      boolean lvt_2_1_ = this.chunkManager.func_219245_b();
      if (!lvt_1_1_ && !lvt_2_1_) {
         return false;
      } else {
         this.invalidateCaches();
         return true;
      }
   }

   public boolean isChunkLoaded(Entity p_217204_1_) {
      long lvt_2_1_ = ChunkPos.asLong(MathHelper.floor(p_217204_1_.func_226277_ct_()) >> 4, MathHelper.floor(p_217204_1_.func_226281_cx_()) >> 4);
      return this.isChunkLoaded(lvt_2_1_, ChunkHolder::func_219297_b);
   }

   public boolean isChunkLoaded(ChunkPos p_222865_1_) {
      return this.isChunkLoaded(p_222865_1_.asLong(), ChunkHolder::func_219297_b);
   }

   public boolean canTick(BlockPos p_222866_1_) {
      long lvt_2_1_ = ChunkPos.asLong(p_222866_1_.getX() >> 4, p_222866_1_.getZ() >> 4);
      return this.isChunkLoaded(lvt_2_1_, ChunkHolder::func_219296_a);
   }

   public boolean func_223435_b(Entity p_223435_1_) {
      long lvt_2_1_ = ChunkPos.asLong(MathHelper.floor(p_223435_1_.func_226277_ct_()) >> 4, MathHelper.floor(p_223435_1_.func_226281_cx_()) >> 4);
      return this.isChunkLoaded(lvt_2_1_, ChunkHolder::func_223492_c);
   }

   private boolean isChunkLoaded(long p_222872_1_, Function<ChunkHolder, CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>>> p_222872_3_) {
      ChunkHolder lvt_4_1_ = this.func_217213_a(p_222872_1_);
      if (lvt_4_1_ == null) {
         return false;
      } else {
         Either<Chunk, ChunkHolder.IChunkLoadingError> lvt_5_1_ = (Either)((CompletableFuture)p_222872_3_.apply(lvt_4_1_)).getNow(ChunkHolder.UNLOADED_CHUNK);
         return lvt_5_1_.left().isPresent();
      }
   }

   public void save(boolean p_217210_1_) {
      this.func_217235_l();
      this.chunkManager.save(p_217210_1_);
   }

   public void close() throws IOException {
      this.save(true);
      this.lightManager.close();
      this.chunkManager.close();
   }

   public void tick(BooleanSupplier p_217207_1_) {
      this.world.getProfiler().startSection("purge");
      this.ticketManager.tick();
      this.func_217235_l();
      this.world.getProfiler().endStartSection("chunks");
      this.func_217220_m();
      this.world.getProfiler().endStartSection("unload");
      this.chunkManager.tick(p_217207_1_);
      this.world.getProfiler().endSection();
      this.invalidateCaches();
   }

   private void func_217220_m() {
      long lvt_1_1_ = this.world.getGameTime();
      long lvt_3_1_ = lvt_1_1_ - this.lastGameTime;
      this.lastGameTime = lvt_1_1_;
      WorldInfo lvt_5_1_ = this.world.getWorldInfo();
      boolean lvt_6_1_ = lvt_5_1_.getGenerator() == WorldType.DEBUG_ALL_BLOCK_STATES;
      boolean lvt_7_1_ = this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING);
      if (!lvt_6_1_) {
         this.world.getProfiler().startSection("pollingChunks");
         int lvt_8_1_ = this.world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
         BlockPos lvt_9_1_ = this.world.getSpawnPoint();
         boolean lvt_10_1_ = lvt_5_1_.getGameTime() % 400L == 0L;
         this.world.getProfiler().startSection("naturalSpawnCount");
         int lvt_11_1_ = this.ticketManager.func_219358_b();
         EntityClassification[] lvt_12_1_ = EntityClassification.values();
         Object2IntMap<EntityClassification> lvt_13_1_ = this.world.countEntities();
         this.world.getProfiler().endSection();
         this.chunkManager.func_223491_f().forEach((p_223434_10_) -> {
            Optional<Chunk> lvt_11_1_x = ((Either)p_223434_10_.func_219297_b().getNow(ChunkHolder.UNLOADED_CHUNK)).left();
            if (lvt_11_1_x.isPresent()) {
               Chunk lvt_12_1_x = (Chunk)lvt_11_1_x.get();
               this.world.getProfiler().startSection("broadcast");
               p_223434_10_.sendChanges(lvt_12_1_x);
               this.world.getProfiler().endSection();
               ChunkPos lvt_13_1_x = p_223434_10_.getPosition();
               if (!this.chunkManager.isOutsideSpawningRadius(lvt_13_1_x)) {
                  lvt_12_1_x.setInhabitedTime(lvt_12_1_x.getInhabitedTime() + lvt_3_1_);
                  if (lvt_7_1_ && (this.spawnHostiles || this.spawnPassives) && this.world.getWorldBorder().contains(lvt_12_1_x.getPos())) {
                     this.world.getProfiler().startSection("spawner");
                     EntityClassification[] var14 = lvt_12_1_;
                     int var15 = lvt_12_1_.length;

                     for(int var16 = 0; var16 < var15; ++var16) {
                        EntityClassification lvt_17_1_ = var14[var16];
                        if (lvt_17_1_ != EntityClassification.MISC && (!lvt_17_1_.getPeacefulCreature() || this.spawnPassives) && (lvt_17_1_.getPeacefulCreature() || this.spawnHostiles) && (!lvt_17_1_.getAnimal() || lvt_10_1_)) {
                           int lvt_18_1_ = lvt_17_1_.getMaxNumberOfCreature() * lvt_11_1_ / field_217238_b;
                           if (lvt_13_1_.getInt(lvt_17_1_) <= lvt_18_1_) {
                              WorldEntitySpawner.func_226701_a_(lvt_17_1_, this.world, lvt_12_1_x, lvt_9_1_);
                           }
                        }
                     }

                     this.world.getProfiler().endSection();
                  }

                  this.world.func_217441_a(lvt_12_1_x, lvt_8_1_);
               }
            }
         });
         this.world.getProfiler().startSection("customSpawners");
         if (lvt_7_1_) {
            this.generator.spawnMobs(this.world, this.spawnHostiles, this.spawnPassives);
         }

         this.world.getProfiler().endSection();
         this.world.getProfiler().endSection();
      }

      this.chunkManager.tickEntityTracker();
   }

   public String makeString() {
      return "ServerChunkCache: " + this.getLoadedChunkCount();
   }

   @VisibleForTesting
   public int func_225314_f() {
      return this.executor.func_223704_be();
   }

   public ChunkGenerator<?> getChunkGenerator() {
      return this.generator;
   }

   public int getLoadedChunkCount() {
      return this.chunkManager.getLoadedChunkCount();
   }

   public void markBlockChanged(BlockPos p_217217_1_) {
      int lvt_2_1_ = p_217217_1_.getX() >> 4;
      int lvt_3_1_ = p_217217_1_.getZ() >> 4;
      ChunkHolder lvt_4_1_ = this.func_217213_a(ChunkPos.asLong(lvt_2_1_, lvt_3_1_));
      if (lvt_4_1_ != null) {
         lvt_4_1_.markBlockChanged(p_217217_1_.getX() & 15, p_217217_1_.getY(), p_217217_1_.getZ() & 15);
      }

   }

   public void markLightChanged(LightType p_217201_1_, SectionPos p_217201_2_) {
      this.executor.execute(() -> {
         ChunkHolder lvt_3_1_ = this.func_217213_a(p_217201_2_.asChunkPos().asLong());
         if (lvt_3_1_ != null) {
            lvt_3_1_.markLightChanged(p_217201_1_, p_217201_2_.getSectionY());
         }

      });
   }

   public <T> void func_217228_a(TicketType<T> p_217228_1_, ChunkPos p_217228_2_, int p_217228_3_, T p_217228_4_) {
      this.ticketManager.register(p_217228_1_, p_217228_2_, p_217228_3_, p_217228_4_);
   }

   public <T> void func_217222_b(TicketType<T> p_217222_1_, ChunkPos p_217222_2_, int p_217222_3_, T p_217222_4_) {
      this.ticketManager.release(p_217222_1_, p_217222_2_, p_217222_3_, p_217222_4_);
   }

   public void forceChunk(ChunkPos p_217206_1_, boolean p_217206_2_) {
      this.ticketManager.forceChunk(p_217206_1_, p_217206_2_);
   }

   public void updatePlayerPosition(ServerPlayerEntity p_217221_1_) {
      this.chunkManager.updatePlayerPosition(p_217221_1_);
   }

   public void untrack(Entity p_217226_1_) {
      this.chunkManager.untrack(p_217226_1_);
   }

   public void track(Entity p_217230_1_) {
      this.chunkManager.track(p_217230_1_);
   }

   public void sendToTrackingAndSelf(Entity p_217216_1_, IPacket<?> p_217216_2_) {
      this.chunkManager.sendToTrackingAndSelf(p_217216_1_, p_217216_2_);
   }

   public void sendToAllTracking(Entity p_217218_1_, IPacket<?> p_217218_2_) {
      this.chunkManager.sendToAllTracking(p_217218_1_, p_217218_2_);
   }

   public void func_217219_a(int p_217219_1_) {
      this.chunkManager.setViewDistance(p_217219_1_);
   }

   public void setAllowedSpawnTypes(boolean p_217203_1_, boolean p_217203_2_) {
      this.spawnHostiles = p_217203_1_;
      this.spawnPassives = p_217203_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public String func_217208_a(ChunkPos p_217208_1_) {
      return this.chunkManager.func_219170_a(p_217208_1_);
   }

   public DimensionSavedDataManager getSavedData() {
      return this.savedData;
   }

   public PointOfInterestManager func_217231_i() {
      return this.chunkManager.func_219189_h();
   }

   // $FF: synthetic method
   public WorldLightManager getLightManager() {
      return this.getLightManager();
   }

   // $FF: synthetic method
   public IBlockReader getWorld() {
      return this.getWorld();
   }

   final class ChunkExecutor extends ThreadTaskExecutor<Runnable> {
      private ChunkExecutor(World p_i50985_2_) {
         super("Chunk source main thread executor for " + Registry.DIMENSION_TYPE.getKey(p_i50985_2_.getDimension().getType()));
      }

      protected Runnable wrapTask(Runnable p_212875_1_) {
         return p_212875_1_;
      }

      protected boolean canRun(Runnable p_212874_1_) {
         return true;
      }

      protected boolean shouldDeferTasks() {
         return true;
      }

      protected Thread getExecutionThread() {
         return ServerChunkProvider.this.mainThread;
      }

      protected void run(Runnable p_213166_1_) {
         ServerChunkProvider.this.world.getProfiler().func_230035_c_("runTask");
         super.run(p_213166_1_);
      }

      protected boolean driveOne() {
         if (ServerChunkProvider.this.func_217235_l()) {
            return true;
         } else {
            ServerChunkProvider.this.lightManager.func_215588_z_();
            return super.driveOne();
         }
      }

      // $FF: synthetic method
      ChunkExecutor(World p_i50986_2_, Object p_i50986_3_) {
         this(p_i50986_2_);
      }
   }
}
