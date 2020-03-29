package net.minecraft.world.dimension;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.CheckerboardBiomeProvider;
import net.minecraft.world.biome.provider.CheckerboardBiomeProviderSettings;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.DebugChunkGenerator;
import net.minecraft.world.gen.DebugGenerationSettings;
import net.minecraft.world.gen.EndChunkGenerator;
import net.minecraft.world.gen.EndGenerationSettings;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.NetherChunkGenerator;
import net.minecraft.world.gen.NetherGenSettings;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OverworldDimension extends Dimension {
   public OverworldDimension(World p_i49933_1_, DimensionType p_i49933_2_) {
      super(p_i49933_1_, p_i49933_2_, 0.0F);
   }

   public ChunkGenerator<? extends GenerationSettings> createChunkGenerator() {
      WorldType worldtype = this.world.getWorldInfo().getGenerator();
      ChunkGeneratorType<FlatGenerationSettings, FlatChunkGenerator> chunkgeneratortype = ChunkGeneratorType.FLAT;
      ChunkGeneratorType<DebugGenerationSettings, DebugChunkGenerator> chunkgeneratortype1 = ChunkGeneratorType.DEBUG;
      ChunkGeneratorType<NetherGenSettings, NetherChunkGenerator> chunkgeneratortype2 = ChunkGeneratorType.CAVES;
      ChunkGeneratorType<EndGenerationSettings, EndChunkGenerator> chunkgeneratortype3 = ChunkGeneratorType.FLOATING_ISLANDS;
      ChunkGeneratorType<OverworldGenSettings, OverworldChunkGenerator> chunkgeneratortype4 = ChunkGeneratorType.SURFACE;
      BiomeProviderType<SingleBiomeProviderSettings, SingleBiomeProvider> biomeprovidertype = BiomeProviderType.FIXED;
      BiomeProviderType<OverworldBiomeProviderSettings, OverworldBiomeProvider> biomeprovidertype1 = BiomeProviderType.VANILLA_LAYERED;
      BiomeProviderType<CheckerboardBiomeProviderSettings, CheckerboardBiomeProvider> biomeprovidertype2 = BiomeProviderType.CHECKERBOARD;
      if (worldtype == WorldType.FLAT) {
         FlatGenerationSettings flatgenerationsettings = FlatGenerationSettings.createFlatGenerator(new Dynamic(NBTDynamicOps.INSTANCE, this.world.getWorldInfo().getGeneratorOptions()));
         SingleBiomeProviderSettings singlebiomeprovidersettings1 = ((SingleBiomeProviderSettings)biomeprovidertype.func_226840_a_(this.world.getWorldInfo())).setBiome(flatgenerationsettings.getBiome());
         return chunkgeneratortype.create(this.world, biomeprovidertype.create(singlebiomeprovidersettings1), flatgenerationsettings);
      } else if (worldtype == WorldType.DEBUG_ALL_BLOCK_STATES) {
         SingleBiomeProviderSettings singlebiomeprovidersettings = ((SingleBiomeProviderSettings)biomeprovidertype.func_226840_a_(this.world.getWorldInfo())).setBiome(Biomes.PLAINS);
         return chunkgeneratortype1.create(this.world, biomeprovidertype.create(singlebiomeprovidersettings), chunkgeneratortype1.createSettings());
      } else if (worldtype != WorldType.BUFFET) {
         OverworldGenSettings overworldgensettings = (OverworldGenSettings)chunkgeneratortype4.createSettings();
         OverworldBiomeProviderSettings overworldbiomeprovidersettings = ((OverworldBiomeProviderSettings)biomeprovidertype1.func_226840_a_(this.world.getWorldInfo())).setGeneratorSettings(overworldgensettings);
         return chunkgeneratortype4.create(this.world, biomeprovidertype1.create(overworldbiomeprovidersettings), overworldgensettings);
      } else {
         BiomeProvider biomeprovider = null;
         JsonElement jsonelement = (JsonElement)Dynamic.convert(NBTDynamicOps.INSTANCE, JsonOps.INSTANCE, this.world.getWorldInfo().getGeneratorOptions());
         JsonObject jsonobject = jsonelement.getAsJsonObject();
         JsonObject jsonobject1 = jsonobject.getAsJsonObject("biome_source");
         if (jsonobject1 != null && jsonobject1.has("type") && jsonobject1.has("options")) {
            BiomeProviderType<?, ?> biomeprovidertype3 = (BiomeProviderType)Registry.BIOME_SOURCE_TYPE.getOrDefault(new ResourceLocation(jsonobject1.getAsJsonPrimitive("type").getAsString()));
            JsonObject jsonobject2 = jsonobject1.getAsJsonObject("options");
            Biome[] abiome = new Biome[]{Biomes.OCEAN};
            if (jsonobject2.has("biomes")) {
               JsonArray jsonarray = jsonobject2.getAsJsonArray("biomes");
               abiome = jsonarray.size() > 0 ? new Biome[jsonarray.size()] : new Biome[]{Biomes.OCEAN};

               for(int i = 0; i < jsonarray.size(); ++i) {
                  abiome[i] = (Biome)Registry.BIOME.getValue(new ResourceLocation(jsonarray.get(i).getAsString())).orElse(Biomes.OCEAN);
               }
            }

            if (BiomeProviderType.FIXED == biomeprovidertype3) {
               SingleBiomeProviderSettings singlebiomeprovidersettings2 = ((SingleBiomeProviderSettings)biomeprovidertype.func_226840_a_(this.world.getWorldInfo())).setBiome(abiome[0]);
               biomeprovider = biomeprovidertype.create(singlebiomeprovidersettings2);
            }

            if (BiomeProviderType.CHECKERBOARD == biomeprovidertype3) {
               int j = jsonobject2.has("size") ? jsonobject2.getAsJsonPrimitive("size").getAsInt() : 2;
               CheckerboardBiomeProviderSettings checkerboardbiomeprovidersettings = ((CheckerboardBiomeProviderSettings)biomeprovidertype2.func_226840_a_(this.world.getWorldInfo())).setBiomes(abiome).setSize(j);
               biomeprovider = biomeprovidertype2.create(checkerboardbiomeprovidersettings);
            }

            if (BiomeProviderType.VANILLA_LAYERED == biomeprovidertype3) {
               OverworldBiomeProviderSettings overworldbiomeprovidersettings1 = (OverworldBiomeProviderSettings)biomeprovidertype1.func_226840_a_(this.world.getWorldInfo());
               biomeprovider = biomeprovidertype1.create(overworldbiomeprovidersettings1);
            }
         }

         if (biomeprovider == null) {
            biomeprovider = biomeprovidertype.create(((SingleBiomeProviderSettings)biomeprovidertype.func_226840_a_(this.world.getWorldInfo())).setBiome(Biomes.OCEAN));
         }

         BlockState blockstate = Blocks.STONE.getDefaultState();
         BlockState blockstate1 = Blocks.WATER.getDefaultState();
         JsonObject jsonobject3 = jsonobject.getAsJsonObject("chunk_generator");
         if (jsonobject3 != null && jsonobject3.has("options")) {
            JsonObject jsonobject4 = jsonobject3.getAsJsonObject("options");
            String s1;
            if (jsonobject4.has("default_block")) {
               s1 = jsonobject4.getAsJsonPrimitive("default_block").getAsString();
               blockstate = ((Block)Registry.BLOCK.getOrDefault(new ResourceLocation(s1))).getDefaultState();
            }

            if (jsonobject4.has("default_fluid")) {
               s1 = jsonobject4.getAsJsonPrimitive("default_fluid").getAsString();
               blockstate1 = ((Block)Registry.BLOCK.getOrDefault(new ResourceLocation(s1))).getDefaultState();
            }
         }

         if (jsonobject3 != null && jsonobject3.has("type")) {
            ChunkGeneratorType<?, ?> chunkgeneratortype5 = (ChunkGeneratorType)Registry.CHUNK_GENERATOR_TYPE.getOrDefault(new ResourceLocation(jsonobject3.getAsJsonPrimitive("type").getAsString()));
            if (ChunkGeneratorType.CAVES == chunkgeneratortype5) {
               NetherGenSettings nethergensettings = (NetherGenSettings)chunkgeneratortype2.createSettings();
               nethergensettings.setDefaultBlock(blockstate);
               nethergensettings.setDefaultFluid(blockstate1);
               return chunkgeneratortype2.create(this.world, biomeprovider, nethergensettings);
            }

            if (ChunkGeneratorType.FLOATING_ISLANDS == chunkgeneratortype5) {
               EndGenerationSettings endgenerationsettings = (EndGenerationSettings)chunkgeneratortype3.createSettings();
               endgenerationsettings.setSpawnPos(new BlockPos(0, 64, 0));
               endgenerationsettings.setDefaultBlock(blockstate);
               endgenerationsettings.setDefaultFluid(blockstate1);
               return chunkgeneratortype3.create(this.world, biomeprovider, endgenerationsettings);
            }
         }

         OverworldGenSettings overworldgensettings1 = (OverworldGenSettings)chunkgeneratortype4.createSettings();
         overworldgensettings1.setDefaultBlock(blockstate);
         overworldgensettings1.setDefaultFluid(blockstate1);
         return chunkgeneratortype4.create(this.world, biomeprovider, overworldgensettings1);
      }
   }

   @Nullable
   public BlockPos findSpawn(ChunkPos p_206920_1_, boolean p_206920_2_) {
      for(int i = p_206920_1_.getXStart(); i <= p_206920_1_.getXEnd(); ++i) {
         for(int j = p_206920_1_.getZStart(); j <= p_206920_1_.getZEnd(); ++j) {
            BlockPos blockpos = this.findSpawn(i, j, p_206920_2_);
            if (blockpos != null) {
               return blockpos;
            }
         }
      }

      return null;
   }

   @Nullable
   public BlockPos findSpawn(int p_206921_1_, int p_206921_2_, boolean p_206921_3_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_206921_1_, 0, p_206921_2_);
      Biome biome = this.world.func_226691_t_(blockpos$mutable);
      BlockState blockstate = biome.getSurfaceBuilderConfig().getTop();
      if (p_206921_3_ && !blockstate.getBlock().isIn(BlockTags.VALID_SPAWN)) {
         return null;
      } else {
         Chunk chunk = this.world.getChunk(p_206921_1_ >> 4, p_206921_2_ >> 4);
         int i = chunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, p_206921_1_ & 15, p_206921_2_ & 15);
         if (i < 0) {
            return null;
         } else if (chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE, p_206921_1_ & 15, p_206921_2_ & 15) > chunk.getTopBlockY(Heightmap.Type.OCEAN_FLOOR, p_206921_1_ & 15, p_206921_2_ & 15)) {
            return null;
         } else {
            for(int j = i + 1; j >= 0; --j) {
               blockpos$mutable.setPos(p_206921_1_, j, p_206921_2_);
               BlockState blockstate1 = this.world.getBlockState(blockpos$mutable);
               if (!blockstate1.getFluidState().isEmpty()) {
                  break;
               }

               if (blockstate1.equals(blockstate)) {
                  return blockpos$mutable.up().toImmutable();
               }
            }

            return null;
         }
      }
   }

   public float calculateCelestialAngle(long p_76563_1_, float p_76563_3_) {
      double d0 = MathHelper.frac((double)p_76563_1_ / 24000.0D - 0.25D);
      double d1 = 0.5D - Math.cos(d0 * 3.141592653589793D) / 2.0D;
      return (float)(d0 * 2.0D + d1) / 3.0F;
   }

   public boolean isSurfaceWorld() {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d getFogColor(float p_76562_1_, float p_76562_2_) {
      float f = MathHelper.cos(p_76562_1_ * 6.2831855F) * 2.0F + 0.5F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      float f1 = 0.7529412F;
      float f2 = 0.84705883F;
      float f3 = 1.0F;
      f1 *= f * 0.94F + 0.06F;
      f2 *= f * 0.94F + 0.06F;
      f3 *= f * 0.91F + 0.09F;
      return new Vec3d((double)f1, (double)f2, (double)f3);
   }

   public boolean canRespawnHere() {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean doesXZShowFog(int p_76568_1_, int p_76568_2_) {
      return false;
   }
}
