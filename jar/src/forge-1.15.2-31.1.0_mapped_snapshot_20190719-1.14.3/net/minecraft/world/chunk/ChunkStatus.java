package net.minecraft.world.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.ServerWorldLightManager;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ChunkStatus extends ForgeRegistryEntry<ChunkStatus> {
   private static final EnumSet<Heightmap.Type> field_222618_n;
   private static final EnumSet<Heightmap.Type> field_222619_o;
   private static final ChunkStatus.ILoadingWorker NOOP_LOADING_WORKER;
   public static final ChunkStatus EMPTY;
   public static final ChunkStatus STRUCTURE_STARTS;
   public static final ChunkStatus STRUCTURE_REFERENCES;
   public static final ChunkStatus BIOMES;
   public static final ChunkStatus NOISE;
   public static final ChunkStatus SURFACE;
   public static final ChunkStatus CARVERS;
   public static final ChunkStatus LIQUID_CARVERS;
   public static final ChunkStatus FEATURES;
   public static final ChunkStatus LIGHT;
   public static final ChunkStatus SPAWN;
   public static final ChunkStatus HEIGHTMAPS;
   public static final ChunkStatus FULL;
   private static final List<ChunkStatus> field_222620_p;
   private static final IntList field_222621_q;
   private final String name;
   private final int ordinal;
   private final ChunkStatus parent;
   private final ChunkStatus.IGenerationWorker generationWorker;
   private final ChunkStatus.ILoadingWorker field_225500_w;
   private final int taskRange;
   private final ChunkStatus.Type type;
   private final EnumSet<Heightmap.Type> heightmaps;

   private static CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_223206_a(ChunkStatus p_223206_0_, ServerWorldLightManager p_223206_1_, IChunk p_223206_2_) {
      boolean flag = func_223197_a(p_223206_0_, p_223206_2_);
      if (!p_223206_2_.getStatus().isAtLeast(p_223206_0_)) {
         ((ChunkPrimer)p_223206_2_).setStatus(p_223206_0_);
      }

      return p_223206_1_.lightChunk(p_223206_2_, flag).thenApply(Either::left);
   }

   private static ChunkStatus registerSelective(String p_223203_0_, @Nullable ChunkStatus p_223203_1_, int p_223203_2_, EnumSet<Heightmap.Type> p_223203_3_, ChunkStatus.Type p_223203_4_, ChunkStatus.ISelectiveWorker p_223203_5_) {
      return register(p_223203_0_, p_223203_1_, p_223203_2_, p_223203_3_, p_223203_4_, p_223203_5_);
   }

   private static ChunkStatus register(String p_223207_0_, @Nullable ChunkStatus p_223207_1_, int p_223207_2_, EnumSet<Heightmap.Type> p_223207_3_, ChunkStatus.Type p_223207_4_, ChunkStatus.IGenerationWorker p_223207_5_) {
      return register(p_223207_0_, p_223207_1_, p_223207_2_, p_223207_3_, p_223207_4_, p_223207_5_, NOOP_LOADING_WORKER);
   }

   private static ChunkStatus register(String p_223196_0_, @Nullable ChunkStatus p_223196_1_, int p_223196_2_, EnumSet<Heightmap.Type> p_223196_3_, ChunkStatus.Type p_223196_4_, ChunkStatus.IGenerationWorker p_223196_5_, ChunkStatus.ILoadingWorker p_223196_6_) {
      return (ChunkStatus)Registry.register((Registry)Registry.CHUNK_STATUS, (String)p_223196_0_, (Object)(new ChunkStatus(p_223196_0_, p_223196_1_, p_223196_2_, p_223196_3_, p_223196_4_, p_223196_5_, p_223196_6_)));
   }

   public static List<ChunkStatus> getAll() {
      List<ChunkStatus> list = Lists.newArrayList();

      ChunkStatus chunkstatus;
      for(chunkstatus = FULL; chunkstatus.getParent() != chunkstatus; chunkstatus = chunkstatus.getParent()) {
         list.add(chunkstatus);
      }

      list.add(chunkstatus);
      Collections.reverse(list);
      return list;
   }

   private static boolean func_223197_a(ChunkStatus p_223197_0_, IChunk p_223197_1_) {
      return p_223197_1_.getStatus().isAtLeast(p_223197_0_) && p_223197_1_.hasLight();
   }

   public static ChunkStatus func_222581_a(int p_222581_0_) {
      if (p_222581_0_ >= field_222620_p.size()) {
         return EMPTY;
      } else {
         return p_222581_0_ < 0 ? FULL : (ChunkStatus)field_222620_p.get(p_222581_0_);
      }
   }

   public static int func_222600_b() {
      return field_222620_p.size();
   }

   public static int func_222599_a(ChunkStatus p_222599_0_) {
      return field_222621_q.getInt(p_222599_0_.ordinal());
   }

   public ChunkStatus(String p_i51520_1_, @Nullable ChunkStatus p_i51520_2_, int p_i51520_3_, EnumSet<Heightmap.Type> p_i51520_4_, ChunkStatus.Type p_i51520_5_, ChunkStatus.IGenerationWorker p_i51520_6_, ChunkStatus.ILoadingWorker p_i51520_7_) {
      this.name = p_i51520_1_;
      this.parent = p_i51520_2_ == null ? this : p_i51520_2_;
      this.generationWorker = p_i51520_6_;
      this.field_225500_w = p_i51520_7_;
      this.taskRange = p_i51520_3_;
      this.type = p_i51520_5_;
      this.heightmaps = p_i51520_4_;
      this.ordinal = p_i51520_2_ == null ? 0 : p_i51520_2_.ordinal() + 1;
   }

   public int ordinal() {
      return this.ordinal;
   }

   public String getName() {
      return this.name;
   }

   public ChunkStatus getParent() {
      return this.parent;
   }

   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_223198_a(ServerWorld p_223198_1_, ChunkGenerator<?> p_223198_2_, TemplateManager p_223198_3_, ServerWorldLightManager p_223198_4_, Function<IChunk, CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> p_223198_5_, List<IChunk> p_223198_6_) {
      return this.generationWorker.doWork(this, p_223198_1_, p_223198_2_, p_223198_3_, p_223198_4_, p_223198_5_, p_223198_6_, (IChunk)p_223198_6_.get(p_223198_6_.size() / 2));
   }

   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_223201_a(ServerWorld p_223201_1_, TemplateManager p_223201_2_, ServerWorldLightManager p_223201_3_, Function<IChunk, CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> p_223201_4_, IChunk p_223201_5_) {
      return this.field_225500_w.doWork(this, p_223201_1_, p_223201_2_, p_223201_3_, p_223201_4_, p_223201_5_);
   }

   public int getTaskRange() {
      return this.taskRange;
   }

   public ChunkStatus.Type getType() {
      return this.type;
   }

   public static ChunkStatus byName(String p_222591_0_) {
      return (ChunkStatus)Registry.CHUNK_STATUS.getOrDefault(ResourceLocation.tryCreate(p_222591_0_));
   }

   public EnumSet<Heightmap.Type> getHeightMaps() {
      return this.heightmaps;
   }

   public boolean isAtLeast(ChunkStatus p_209003_1_) {
      return this.ordinal() >= p_209003_1_.ordinal();
   }

   public String toString() {
      return Registry.CHUNK_STATUS.getKey(this).toString();
   }

   static {
      field_222618_n = EnumSet.of(Heightmap.Type.OCEAN_FLOOR_WG, Heightmap.Type.WORLD_SURFACE_WG);
      field_222619_o = EnumSet.of(Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE, Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      NOOP_LOADING_WORKER = (p_lambda$static$0_0_, p_lambda$static$0_1_, p_lambda$static$0_2_, p_lambda$static$0_3_, p_lambda$static$0_4_, p_lambda$static$0_5_) -> {
         if (p_lambda$static$0_5_ instanceof ChunkPrimer && !p_lambda$static$0_5_.getStatus().isAtLeast(p_lambda$static$0_0_)) {
            ((ChunkPrimer)p_lambda$static$0_5_).setStatus(p_lambda$static$0_0_);
         }

         return CompletableFuture.completedFuture(Either.left(p_lambda$static$0_5_));
      };
      EMPTY = registerSelective("empty", (ChunkStatus)null, -1, field_222618_n, ChunkStatus.Type.PROTOCHUNK, (p_lambda$static$1_0_, p_lambda$static$1_1_, p_lambda$static$1_2_, p_lambda$static$1_3_) -> {
      });
      STRUCTURE_STARTS = register("structure_starts", EMPTY, 0, field_222618_n, ChunkStatus.Type.PROTOCHUNK, (p_lambda$static$2_0_, p_lambda$static$2_1_, p_lambda$static$2_2_, p_lambda$static$2_3_, p_lambda$static$2_4_, p_lambda$static$2_5_, p_lambda$static$2_6_, p_lambda$static$2_7_) -> {
         if (!p_lambda$static$2_7_.getStatus().isAtLeast(p_lambda$static$2_0_)) {
            if (p_lambda$static$2_1_.getWorldInfo().isMapFeaturesEnabled()) {
               p_lambda$static$2_2_.func_227058_a_(p_lambda$static$2_1_.func_225523_d_().func_226835_a_(p_lambda$static$2_2_.getBiomeProvider()), p_lambda$static$2_7_, p_lambda$static$2_2_, p_lambda$static$2_3_);
            }

            if (p_lambda$static$2_7_ instanceof ChunkPrimer) {
               ((ChunkPrimer)p_lambda$static$2_7_).setStatus(p_lambda$static$2_0_);
            }
         }

         return CompletableFuture.completedFuture(Either.left(p_lambda$static$2_7_));
      });
      STRUCTURE_REFERENCES = registerSelective("structure_references", STRUCTURE_STARTS, 8, field_222618_n, ChunkStatus.Type.PROTOCHUNK, (p_lambda$static$3_0_, p_lambda$static$3_1_, p_lambda$static$3_2_, p_lambda$static$3_3_) -> {
         p_lambda$static$3_1_.generateStructureStarts(new WorldGenRegion(p_lambda$static$3_0_, p_lambda$static$3_2_), p_lambda$static$3_3_);
      });
      BIOMES = registerSelective("biomes", STRUCTURE_REFERENCES, 0, field_222618_n, ChunkStatus.Type.PROTOCHUNK, (p_lambda$static$4_0_, p_lambda$static$4_1_, p_lambda$static$4_2_, p_lambda$static$4_3_) -> {
         p_lambda$static$4_1_.generateBiomes(p_lambda$static$4_3_);
      });
      NOISE = registerSelective("noise", BIOMES, 8, field_222618_n, ChunkStatus.Type.PROTOCHUNK, (p_lambda$static$5_0_, p_lambda$static$5_1_, p_lambda$static$5_2_, p_lambda$static$5_3_) -> {
         p_lambda$static$5_1_.makeBase(new WorldGenRegion(p_lambda$static$5_0_, p_lambda$static$5_2_), p_lambda$static$5_3_);
      });
      SURFACE = registerSelective("surface", NOISE, 0, field_222618_n, ChunkStatus.Type.PROTOCHUNK, (p_lambda$static$6_0_, p_lambda$static$6_1_, p_lambda$static$6_2_, p_lambda$static$6_3_) -> {
         p_lambda$static$6_1_.func_225551_a_(new WorldGenRegion(p_lambda$static$6_0_, p_lambda$static$6_2_), p_lambda$static$6_3_);
      });
      CARVERS = registerSelective("carvers", SURFACE, 0, field_222618_n, ChunkStatus.Type.PROTOCHUNK, (p_lambda$static$7_0_, p_lambda$static$7_1_, p_lambda$static$7_2_, p_lambda$static$7_3_) -> {
         p_lambda$static$7_1_.func_225550_a_(p_lambda$static$7_0_.func_225523_d_().func_226835_a_(p_lambda$static$7_1_.getBiomeProvider()), p_lambda$static$7_3_, GenerationStage.Carving.AIR);
      });
      LIQUID_CARVERS = registerSelective("liquid_carvers", CARVERS, 0, field_222619_o, ChunkStatus.Type.PROTOCHUNK, (p_lambda$static$8_0_, p_lambda$static$8_1_, p_lambda$static$8_2_, p_lambda$static$8_3_) -> {
         p_lambda$static$8_1_.func_225550_a_(p_lambda$static$8_0_.func_225523_d_().func_226835_a_(p_lambda$static$8_1_.getBiomeProvider()), p_lambda$static$8_3_, GenerationStage.Carving.LIQUID);
      });
      FEATURES = register("features", LIQUID_CARVERS, 8, field_222619_o, ChunkStatus.Type.PROTOCHUNK, (p_lambda$static$9_0_, p_lambda$static$9_1_, p_lambda$static$9_2_, p_lambda$static$9_3_, p_lambda$static$9_4_, p_lambda$static$9_5_, p_lambda$static$9_6_, p_lambda$static$9_7_) -> {
         ChunkPrimer chunkprimer = (ChunkPrimer)p_lambda$static$9_7_;
         chunkprimer.setLightManager(p_lambda$static$9_4_);
         if (!p_lambda$static$9_7_.getStatus().isAtLeast(p_lambda$static$9_0_)) {
            Heightmap.func_222690_a(p_lambda$static$9_7_, EnumSet.of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE));
            p_lambda$static$9_2_.decorate(new WorldGenRegion(p_lambda$static$9_1_, p_lambda$static$9_6_));
            chunkprimer.setStatus(p_lambda$static$9_0_);
         }

         return CompletableFuture.completedFuture(Either.left(p_lambda$static$9_7_));
      });
      LIGHT = register("light", FEATURES, 1, field_222619_o, ChunkStatus.Type.PROTOCHUNK, (p_lambda$static$10_0_, p_lambda$static$10_1_, p_lambda$static$10_2_, p_lambda$static$10_3_, p_lambda$static$10_4_, p_lambda$static$10_5_, p_lambda$static$10_6_, p_lambda$static$10_7_) -> {
         return func_223206_a(p_lambda$static$10_0_, p_lambda$static$10_4_, p_lambda$static$10_7_);
      }, (p_lambda$static$11_0_, p_lambda$static$11_1_, p_lambda$static$11_2_, p_lambda$static$11_3_, p_lambda$static$11_4_, p_lambda$static$11_5_) -> {
         return func_223206_a(p_lambda$static$11_0_, p_lambda$static$11_3_, p_lambda$static$11_5_);
      });
      SPAWN = registerSelective("spawn", LIGHT, 0, field_222619_o, ChunkStatus.Type.PROTOCHUNK, (p_lambda$static$12_0_, p_lambda$static$12_1_, p_lambda$static$12_2_, p_lambda$static$12_3_) -> {
         p_lambda$static$12_1_.spawnMobs(new WorldGenRegion(p_lambda$static$12_0_, p_lambda$static$12_2_));
      });
      HEIGHTMAPS = registerSelective("heightmaps", SPAWN, 0, field_222619_o, ChunkStatus.Type.PROTOCHUNK, (p_lambda$static$13_0_, p_lambda$static$13_1_, p_lambda$static$13_2_, p_lambda$static$13_3_) -> {
      });
      FULL = register("full", HEIGHTMAPS, 0, field_222619_o, ChunkStatus.Type.LEVELCHUNK, (p_lambda$static$14_0_, p_lambda$static$14_1_, p_lambda$static$14_2_, p_lambda$static$14_3_, p_lambda$static$14_4_, p_lambda$static$14_5_, p_lambda$static$14_6_, p_lambda$static$14_7_) -> {
         return (CompletableFuture)p_lambda$static$14_5_.apply(p_lambda$static$14_7_);
      }, (p_lambda$static$15_0_, p_lambda$static$15_1_, p_lambda$static$15_2_, p_lambda$static$15_3_, p_lambda$static$15_4_, p_lambda$static$15_5_) -> {
         return (CompletableFuture)p_lambda$static$15_4_.apply(p_lambda$static$15_5_);
      });
      field_222620_p = ImmutableList.of(FULL, FEATURES, LIQUID_CARVERS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS);
      field_222621_q = (IntList)Util.make(new IntArrayList(getAll().size()), (p_lambda$static$16_0_) -> {
         int i = 0;

         for(int j = getAll().size() - 1; j >= 0; --j) {
            while(i + 1 < field_222620_p.size() && j <= ((ChunkStatus)field_222620_p.get(i + 1)).ordinal()) {
               ++i;
            }

            p_lambda$static$16_0_.add(0, i);
         }

      });
   }

   public static enum Type {
      PROTOCHUNK,
      LEVELCHUNK;
   }

   interface ISelectiveWorker extends ChunkStatus.IGenerationWorker {
      default CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> doWork(ChunkStatus p_doWork_1_, ServerWorld p_doWork_2_, ChunkGenerator<?> p_doWork_3_, TemplateManager p_doWork_4_, ServerWorldLightManager p_doWork_5_, Function<IChunk, CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> p_doWork_6_, List<IChunk> p_doWork_7_, IChunk p_doWork_8_) {
         if (!p_doWork_8_.getStatus().isAtLeast(p_doWork_1_)) {
            this.doWork(p_doWork_2_, p_doWork_3_, p_doWork_7_, p_doWork_8_);
            if (p_doWork_8_ instanceof ChunkPrimer) {
               ((ChunkPrimer)p_doWork_8_).setStatus(p_doWork_1_);
            }
         }

         return CompletableFuture.completedFuture(Either.left(p_doWork_8_));
      }

      void doWork(ServerWorld var1, ChunkGenerator<?> var2, List<IChunk> var3, IChunk var4);
   }

   interface ILoadingWorker {
      CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> doWork(ChunkStatus var1, ServerWorld var2, TemplateManager var3, ServerWorldLightManager var4, Function<IChunk, CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> var5, IChunk var6);
   }

   interface IGenerationWorker {
      CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> doWork(ChunkStatus var1, ServerWorld var2, ChunkGenerator<?> var3, TemplateManager var4, ServerWorldLightManager var5, Function<IChunk, CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> var6, List<IChunk> var7, IChunk var8);
   }
}
