package net.minecraft.world.gen;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.EntityClassification;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;

public abstract class ChunkGenerator<C extends GenerationSettings> {
   protected final IWorld world;
   protected final long seed;
   protected final BiomeProvider biomeProvider;
   protected final C settings;

   public ChunkGenerator(IWorld p_i49954_1_, BiomeProvider p_i49954_2_, C p_i49954_3_) {
      this.world = p_i49954_1_;
      this.seed = p_i49954_1_.getSeed();
      this.biomeProvider = p_i49954_2_;
      this.settings = p_i49954_3_;
   }

   public void generateBiomes(IChunk p_222539_1_) {
      ChunkPos chunkpos = p_222539_1_.getPos();
      ((ChunkPrimer)p_222539_1_).func_225548_a_(new BiomeContainer(chunkpos, this.biomeProvider));
   }

   protected Biome func_225552_a_(BiomeManager p_225552_1_, BlockPos p_225552_2_) {
      return p_225552_1_.func_226836_a_(p_225552_2_);
   }

   public void func_225550_a_(BiomeManager p_225550_1_, IChunk p_225550_2_, GenerationStage.Carving p_225550_3_) {
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
      int i = true;
      ChunkPos chunkpos = p_225550_2_.getPos();
      int j = chunkpos.x;
      int k = chunkpos.z;
      Biome biome = this.func_225552_a_(p_225550_1_, chunkpos.asBlockPos());
      BitSet bitset = p_225550_2_.getCarvingMask(p_225550_3_);

      for(int l = j - 8; l <= j + 8; ++l) {
         for(int i1 = k - 8; i1 <= k + 8; ++i1) {
            List<ConfiguredCarver<?>> list = biome.getCarvers(p_225550_3_);
            ListIterator listiterator = list.listIterator();

            while(listiterator.hasNext()) {
               int j1 = listiterator.nextIndex();
               ConfiguredCarver<?> configuredcarver = (ConfiguredCarver)listiterator.next();
               sharedseedrandom.setLargeFeatureSeed(this.seed + (long)j1, l, i1);
               if (configuredcarver.shouldCarve(sharedseedrandom, l, i1)) {
                  configuredcarver.func_227207_a_(p_225550_2_, (p_lambda$func_225550_a_$0_2_) -> {
                     return this.func_225552_a_(p_225550_1_, p_lambda$func_225550_a_$0_2_);
                  }, sharedseedrandom, this.getSeaLevel(), l, i1, j, k, bitset);
               }
            }
         }
      }

   }

   @Nullable
   public BlockPos findNearestStructure(World p_211403_1_, String p_211403_2_, BlockPos p_211403_3_, int p_211403_4_, boolean p_211403_5_) {
      Structure<?> structure = (Structure)Feature.STRUCTURES.get(p_211403_2_.toLowerCase(Locale.ROOT));
      return structure != null ? structure.findNearest(p_211403_1_, this, p_211403_3_, p_211403_4_, p_211403_5_) : null;
   }

   public void decorate(WorldGenRegion p_202092_1_) {
      int i = p_202092_1_.getMainChunkX();
      int j = p_202092_1_.getMainChunkZ();
      int k = i * 16;
      int l = j * 16;
      BlockPos blockpos = new BlockPos(k, 0, l);
      Biome biome = this.func_225552_a_(p_202092_1_.func_225523_d_(), blockpos.add(8, 8, 8));
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
      long i1 = sharedseedrandom.setDecorationSeed(p_202092_1_.getSeed(), k, l);
      GenerationStage.Decoration[] var11 = GenerationStage.Decoration.values();
      int var12 = var11.length;

      for(int var13 = 0; var13 < var12; ++var13) {
         GenerationStage.Decoration generationstage$decoration = var11[var13];

         try {
            biome.decorate(generationstage$decoration, this, p_202092_1_, i1, sharedseedrandom, blockpos);
         } catch (Exception var17) {
            CrashReport crashreport = CrashReport.makeCrashReport(var17, "Biome decoration");
            crashreport.makeCategory("Generation").addDetail("CenterX", (Object)i).addDetail("CenterZ", (Object)j).addDetail("Step", (Object)generationstage$decoration).addDetail("Seed", (Object)i1).addDetail("Biome", (Object)Registry.BIOME.getKey(biome));
            throw new ReportedException(crashreport);
         }
      }

   }

   public abstract void func_225551_a_(WorldGenRegion var1, IChunk var2);

   public void spawnMobs(WorldGenRegion p_202093_1_) {
   }

   public C getSettings() {
      return this.settings;
   }

   public abstract int getGroundHeight();

   public void spawnMobs(ServerWorld p_203222_1_, boolean p_203222_2_, boolean p_203222_3_) {
   }

   public boolean hasStructure(Biome p_202094_1_, Structure<? extends IFeatureConfig> p_202094_2_) {
      return p_202094_1_.hasStructure(p_202094_2_);
   }

   @Nullable
   public <C extends IFeatureConfig> C getStructureConfig(Biome p_202087_1_, Structure<C> p_202087_2_) {
      return p_202087_1_.getStructureConfig(p_202087_2_);
   }

   public BiomeProvider getBiomeProvider() {
      return this.biomeProvider;
   }

   public long getSeed() {
      return this.seed;
   }

   public int getMaxHeight() {
      return 256;
   }

   public List<Biome.SpawnListEntry> getPossibleCreatures(EntityClassification p_177458_1_, BlockPos p_177458_2_) {
      return this.world.func_226691_t_(p_177458_2_).getSpawns(p_177458_1_);
   }

   public void func_227058_a_(BiomeManager p_227058_1_, IChunk p_227058_2_, ChunkGenerator<?> p_227058_3_, TemplateManager p_227058_4_) {
      Iterator var5 = Feature.STRUCTURES.values().iterator();

      while(var5.hasNext()) {
         Structure<?> structure = (Structure)var5.next();
         if (p_227058_3_.getBiomeProvider().hasStructure(structure)) {
            StructureStart structurestart = p_227058_2_.getStructureStart(structure.getStructureName());
            int i = structurestart != null ? structurestart.func_227457_j_() : 0;
            SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
            ChunkPos chunkpos = p_227058_2_.getPos();
            StructureStart structurestart1 = StructureStart.DUMMY;
            Biome biome = p_227058_1_.func_226836_a_(new BlockPos(chunkpos.getXStart() + 9, 0, chunkpos.getZStart() + 9));
            if (structure.func_225558_a_(p_227058_1_, p_227058_3_, sharedseedrandom, chunkpos.x, chunkpos.z, biome)) {
               StructureStart structurestart2 = structure.getStartFactory().create(structure, chunkpos.x, chunkpos.z, MutableBoundingBox.getNewBoundingBox(), i, p_227058_3_.getSeed());
               structurestart2.init(this, p_227058_4_, chunkpos.x, chunkpos.z, biome);
               structurestart1 = structurestart2.isValid() ? structurestart2 : StructureStart.DUMMY;
            }

            p_227058_2_.putStructureStart(structure.getStructureName(), structurestart1);
         }
      }

   }

   public void generateStructureStarts(IWorld p_222528_1_, IChunk p_222528_2_) {
      int i = true;
      int j = p_222528_2_.getPos().x;
      int k = p_222528_2_.getPos().z;
      int l = j << 4;
      int i1 = k << 4;

      for(int j1 = j - 8; j1 <= j + 8; ++j1) {
         for(int k1 = k - 8; k1 <= k + 8; ++k1) {
            long l1 = ChunkPos.asLong(j1, k1);
            Iterator var12 = p_222528_1_.getChunk(j1, k1).getStructureStarts().entrySet().iterator();

            while(var12.hasNext()) {
               Entry<String, StructureStart> entry = (Entry)var12.next();
               StructureStart structurestart = (StructureStart)entry.getValue();
               if (structurestart != StructureStart.DUMMY && structurestart.getBoundingBox().intersectsWith(l, i1, l + 15, i1 + 15)) {
                  p_222528_2_.addStructureReference((String)entry.getKey(), l1);
                  DebugPacketSender.func_218804_a(p_222528_1_, structurestart);
               }
            }
         }
      }

   }

   public abstract void makeBase(IWorld var1, IChunk var2);

   public int getSeaLevel() {
      return this.world.getDimension().getSeaLevel();
   }

   public abstract int func_222529_a(int var1, int var2, Heightmap.Type var3);

   public int func_222532_b(int p_222532_1_, int p_222532_2_, Heightmap.Type p_222532_3_) {
      return this.func_222529_a(p_222532_1_, p_222532_2_, p_222532_3_);
   }

   public int func_222531_c(int p_222531_1_, int p_222531_2_, Heightmap.Type p_222531_3_) {
      return this.func_222529_a(p_222531_1_, p_222531_2_, p_222531_3_) - 1;
   }
}
