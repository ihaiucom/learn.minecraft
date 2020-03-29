package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ICarverConfig;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Biome extends ForgeRegistryEntry<Biome> {
   public static final Logger LOGGER = LogManager.getLogger();
   public static final Set<Biome> BIOMES = Sets.newHashSet();
   public static final ObjectIntIdentityMap<Biome> MUTATION_TO_BASE_ID_MAP = new ObjectIntIdentityMap();
   protected static final PerlinNoiseGenerator TEMPERATURE_NOISE = new PerlinNoiseGenerator(new SharedSeedRandom(1234L), 0, 0);
   public static final PerlinNoiseGenerator INFO_NOISE = new PerlinNoiseGenerator(new SharedSeedRandom(2345L), 0, 0);
   @Nullable
   protected String translationKey;
   protected final float depth;
   protected final float scale;
   protected final float temperature;
   protected final float downfall;
   protected final int waterColor;
   protected final int waterFogColor;
   private final int field_229978_u_;
   @Nullable
   protected final String parent;
   protected final ConfiguredSurfaceBuilder<?> surfaceBuilder;
   protected final Biome.Category category;
   protected final Biome.RainType precipitation;
   protected final Map<GenerationStage.Carving, List<ConfiguredCarver<?>>> carvers = Maps.newHashMap();
   protected final Map<GenerationStage.Decoration, List<ConfiguredFeature<?, ?>>> features = Maps.newHashMap();
   protected final List<ConfiguredFeature<?, ?>> flowers = Lists.newArrayList();
   protected final Map<Structure<?>, IFeatureConfig> structures = Maps.newHashMap();
   private final Map<EntityClassification, List<Biome.SpawnListEntry>> spawns = Maps.newHashMap();
   private final ThreadLocal<Long2FloatLinkedOpenHashMap> field_225488_v = ThreadLocal.withInitial(() -> {
      return (Long2FloatLinkedOpenHashMap)Util.make(() -> {
         Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = new Long2FloatLinkedOpenHashMap(1024, 0.25F) {
            protected void rehash(int p_rehash_1_) {
            }
         };
         long2floatlinkedopenhashmap.defaultReturnValue(Float.NaN);
         return long2floatlinkedopenhashmap;
      });
   });

   @Nullable
   public static Biome getMutationForBiome(Biome p_185356_0_) {
      return (Biome)MUTATION_TO_BASE_ID_MAP.getByValue(Registry.BIOME.getId(p_185356_0_));
   }

   public static <C extends ICarverConfig> ConfiguredCarver<C> createCarver(WorldCarver<C> p_203606_0_, C p_203606_1_) {
      return new ConfiguredCarver(p_203606_0_, p_203606_1_);
   }

   protected Biome(Biome.Builder p_i48975_1_) {
      if (p_i48975_1_.surfaceBuilder != null && p_i48975_1_.precipitation != null && p_i48975_1_.category != null && p_i48975_1_.depth != null && p_i48975_1_.scale != null && p_i48975_1_.temperature != null && p_i48975_1_.downfall != null && p_i48975_1_.waterColor != null && p_i48975_1_.waterFogColor != null) {
         this.surfaceBuilder = p_i48975_1_.surfaceBuilder;
         this.precipitation = p_i48975_1_.precipitation;
         this.category = p_i48975_1_.category;
         this.depth = p_i48975_1_.depth;
         this.scale = p_i48975_1_.scale;
         this.temperature = p_i48975_1_.temperature;
         this.downfall = p_i48975_1_.downfall;
         this.waterColor = p_i48975_1_.waterColor;
         this.waterFogColor = p_i48975_1_.waterFogColor;
         this.field_229978_u_ = this.func_229979_u_();
         this.parent = p_i48975_1_.parent;
         GenerationStage.Decoration[] var2 = GenerationStage.Decoration.values();
         int var3 = var2.length;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            GenerationStage.Decoration generationstage$decoration = var2[var4];
            this.features.put(generationstage$decoration, Lists.newArrayList());
         }

         EntityClassification[] var6 = EntityClassification.values();
         var3 = var6.length;

         for(var4 = 0; var4 < var3; ++var4) {
            EntityClassification entityclassification = var6[var4];
            this.spawns.put(entityclassification, Lists.newArrayList());
         }

      } else {
         throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + p_i48975_1_);
      }
   }

   public boolean isMutation() {
      return this.parent != null;
   }

   private int func_229979_u_() {
      float f = this.temperature;
      f /= 3.0F;
      f = MathHelper.clamp(f, -1.0F, 1.0F);
      return MathHelper.hsvToRGB(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public int func_225529_c_() {
      return this.field_229978_u_;
   }

   protected void addSpawn(EntityClassification p_201866_1_, Biome.SpawnListEntry p_201866_2_) {
      ((List)this.spawns.computeIfAbsent(p_201866_1_, (p_lambda$addSpawn$2_0_) -> {
         return Lists.newArrayList();
      })).add(p_201866_2_);
   }

   public List<Biome.SpawnListEntry> getSpawns(EntityClassification p_76747_1_) {
      return (List)this.spawns.computeIfAbsent(p_76747_1_, (p_lambda$getSpawns$3_0_) -> {
         return Lists.newArrayList();
      });
   }

   public Biome.RainType getPrecipitation() {
      return this.precipitation;
   }

   public boolean isHighHumidity() {
      return this.getDownfall() > 0.85F;
   }

   public float getSpawningChance() {
      return 0.1F;
   }

   public float getTemperature(BlockPos p_180626_1_) {
      if (p_180626_1_.getY() > 64) {
         float f = (float)(TEMPERATURE_NOISE.func_215464_a((double)((float)p_180626_1_.getX() / 8.0F), (double)((float)p_180626_1_.getZ() / 8.0F), false) * 4.0D);
         return this.getDefaultTemperature() - (f + (float)p_180626_1_.getY() - 64.0F) * 0.05F / 30.0F;
      } else {
         return this.getDefaultTemperature();
      }
   }

   public final float func_225486_c(BlockPos p_225486_1_) {
      long i = p_225486_1_.toLong();
      Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = (Long2FloatLinkedOpenHashMap)this.field_225488_v.get();
      float f = long2floatlinkedopenhashmap.get(i);
      if (!Float.isNaN(f)) {
         return f;
      } else {
         float f1 = this.getTemperature(p_225486_1_);
         if (long2floatlinkedopenhashmap.size() == 1024) {
            long2floatlinkedopenhashmap.removeFirstFloat();
         }

         long2floatlinkedopenhashmap.put(i, f1);
         return f1;
      }
   }

   public boolean doesWaterFreeze(IWorldReader p_201848_1_, BlockPos p_201848_2_) {
      return this.doesWaterFreeze(p_201848_1_, p_201848_2_, true);
   }

   public boolean doesWaterFreeze(IWorldReader p_201854_1_, BlockPos p_201854_2_, boolean p_201854_3_) {
      if (this.func_225486_c(p_201854_2_) >= 0.15F) {
         return false;
      } else {
         if (p_201854_2_.getY() >= 0 && p_201854_2_.getY() < p_201854_1_.getDimension().getHeight() && p_201854_1_.func_226658_a_(LightType.BLOCK, p_201854_2_) < 10) {
            BlockState blockstate = p_201854_1_.getBlockState(p_201854_2_);
            IFluidState ifluidstate = p_201854_1_.getFluidState(p_201854_2_);
            if (ifluidstate.getFluid() == Fluids.WATER && blockstate.getBlock() instanceof FlowingFluidBlock) {
               if (!p_201854_3_) {
                  return true;
               }

               boolean flag = p_201854_1_.hasWater(p_201854_2_.west()) && p_201854_1_.hasWater(p_201854_2_.east()) && p_201854_1_.hasWater(p_201854_2_.north()) && p_201854_1_.hasWater(p_201854_2_.south());
               if (!flag) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean doesSnowGenerate(IWorldReader p_201850_1_, BlockPos p_201850_2_) {
      if (this.func_225486_c(p_201850_2_) >= 0.15F) {
         return false;
      } else {
         if (p_201850_2_.getY() >= 0 && p_201850_2_.getY() < 256 && p_201850_1_.func_226658_a_(LightType.BLOCK, p_201850_2_) < 10) {
            BlockState blockstate = p_201850_1_.getBlockState(p_201850_2_);
            if (blockstate.isAir(p_201850_1_, p_201850_2_) && Blocks.SNOW.getDefaultState().isValidPosition(p_201850_1_, p_201850_2_)) {
               return true;
            }
         }

         return false;
      }
   }

   public void addFeature(GenerationStage.Decoration p_203611_1_, ConfiguredFeature<?, ?> p_203611_2_) {
      if (p_203611_2_.feature == Feature.DECORATED_FLOWER) {
         this.flowers.add(p_203611_2_);
      }

      ((List)this.features.get(p_203611_1_)).add(p_203611_2_);
   }

   public <C extends ICarverConfig> void addCarver(GenerationStage.Carving p_203609_1_, ConfiguredCarver<C> p_203609_2_) {
      ((List)this.carvers.computeIfAbsent(p_203609_1_, (p_lambda$addCarver$4_0_) -> {
         return Lists.newArrayList();
      })).add(p_203609_2_);
   }

   public List<ConfiguredCarver<?>> getCarvers(GenerationStage.Carving p_203603_1_) {
      return (List)this.carvers.computeIfAbsent(p_203603_1_, (p_lambda$getCarvers$5_0_) -> {
         return Lists.newArrayList();
      });
   }

   public <C extends IFeatureConfig> void func_226711_a_(ConfiguredFeature<C, ? extends Structure<C>> p_226711_1_) {
      this.structures.put(p_226711_1_.feature, p_226711_1_.config);
   }

   public <C extends IFeatureConfig> boolean hasStructure(Structure<C> p_201858_1_) {
      return this.structures.containsKey(p_201858_1_);
   }

   @Nullable
   public <C extends IFeatureConfig> C getStructureConfig(Structure<C> p_201857_1_) {
      return (IFeatureConfig)this.structures.get(p_201857_1_);
   }

   public List<ConfiguredFeature<?, ?>> getFlowers() {
      return this.flowers;
   }

   public List<ConfiguredFeature<?, ?>> getFeatures(GenerationStage.Decoration p_203607_1_) {
      return (List)this.features.get(p_203607_1_);
   }

   public void decorate(GenerationStage.Decoration p_203608_1_, ChunkGenerator<? extends GenerationSettings> p_203608_2_, IWorld p_203608_3_, long p_203608_4_, SharedSeedRandom p_203608_6_, BlockPos p_203608_7_) {
      int i = 0;

      for(Iterator var9 = ((List)this.features.get(p_203608_1_)).iterator(); var9.hasNext(); ++i) {
         ConfiguredFeature<?, ?> configuredfeature = (ConfiguredFeature)var9.next();
         p_203608_6_.setFeatureSeed(p_203608_4_, i, p_203608_1_.ordinal());

         try {
            configuredfeature.place(p_203608_3_, p_203608_2_, p_203608_6_, p_203608_7_);
         } catch (Exception var13) {
            CrashReport crashreport = CrashReport.makeCrashReport(var13, "Feature placement");
            crashreport.makeCategory("Feature").addDetail("Id", (Object)Registry.FEATURE.getKey(configuredfeature.feature)).addDetail("Description", () -> {
               return configuredfeature.feature.toString();
            });
            throw new ReportedException(crashreport);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int func_225528_a_(double p_225528_1_, double p_225528_3_) {
      double d0 = (double)MathHelper.clamp(this.getDefaultTemperature(), 0.0F, 1.0F);
      double d1 = (double)MathHelper.clamp(this.getDownfall(), 0.0F, 1.0F);
      return GrassColors.get(d0, d1);
   }

   @OnlyIn(Dist.CLIENT)
   public int func_225527_a_() {
      double d0 = (double)MathHelper.clamp(this.getDefaultTemperature(), 0.0F, 1.0F);
      double d1 = (double)MathHelper.clamp(this.getDownfall(), 0.0F, 1.0F);
      return FoliageColors.get(d0, d1);
   }

   public void buildSurface(Random p_206854_1_, IChunk p_206854_2_, int p_206854_3_, int p_206854_4_, int p_206854_5_, double p_206854_6_, BlockState p_206854_8_, BlockState p_206854_9_, int p_206854_10_, long p_206854_11_) {
      this.surfaceBuilder.setSeed(p_206854_11_);
      this.surfaceBuilder.buildSurface(p_206854_1_, p_206854_2_, this, p_206854_3_, p_206854_4_, p_206854_5_, p_206854_6_, p_206854_8_, p_206854_9_, p_206854_10_, p_206854_11_);
   }

   public Biome.TempCategory getTempCategory() {
      if (this.category == Biome.Category.OCEAN) {
         return Biome.TempCategory.OCEAN;
      } else if ((double)this.getDefaultTemperature() < 0.2D) {
         return Biome.TempCategory.COLD;
      } else {
         return (double)this.getDefaultTemperature() < 1.0D ? Biome.TempCategory.MEDIUM : Biome.TempCategory.WARM;
      }
   }

   public final float getDepth() {
      return this.depth;
   }

   public final float getDownfall() {
      return this.downfall;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getDisplayName() {
      return new TranslationTextComponent(this.getTranslationKey(), new Object[0]);
   }

   public String getTranslationKey() {
      if (this.translationKey == null) {
         this.translationKey = Util.makeTranslationKey("biome", Registry.BIOME.getKey(this));
      }

      return this.translationKey;
   }

   public final float getScale() {
      return this.scale;
   }

   public final float getDefaultTemperature() {
      return this.temperature;
   }

   public final int getWaterColor() {
      return this.waterColor;
   }

   public final int getWaterFogColor() {
      return this.waterFogColor;
   }

   public final Biome.Category getCategory() {
      return this.category;
   }

   public ConfiguredSurfaceBuilder<?> getSurfaceBuilder() {
      return this.surfaceBuilder;
   }

   public ISurfaceBuilderConfig getSurfaceBuilderConfig() {
      return this.surfaceBuilder.getConfig();
   }

   @Nullable
   public String getParent() {
      return this.parent;
   }

   public Biome getRiver() {
      if (this == Biomes.SNOWY_TUNDRA) {
         return Biomes.FROZEN_RIVER;
      } else {
         return this != Biomes.MUSHROOM_FIELDS && this != Biomes.MUSHROOM_FIELD_SHORE ? Biomes.RIVER : Biomes.MUSHROOM_FIELD_SHORE;
      }
   }

   public static enum TempCategory {
      OCEAN("ocean"),
      COLD("cold"),
      MEDIUM("medium"),
      WARM("warm");

      private static final Map<String, Biome.TempCategory> field_222358_e = (Map)Arrays.stream(values()).collect(Collectors.toMap(Biome.TempCategory::func_222357_a, (p_lambda$static$0_0_) -> {
         return p_lambda$static$0_0_;
      }));
      private final String field_222359_f;

      private TempCategory(String p_i50594_3_) {
         this.field_222359_f = p_i50594_3_;
      }

      public String func_222357_a() {
         return this.field_222359_f;
      }
   }

   public static class FlowerEntry extends WeightedRandom.Item {
      private final BlockState state;

      public FlowerEntry(BlockState p_i230104_1_, int p_i230104_2_) {
         super(p_i230104_2_);
         this.state = p_i230104_1_;
      }

      public BlockState getState() {
         return this.state;
      }
   }

   public static class SpawnListEntry extends WeightedRandom.Item {
      public final EntityType<?> entityType;
      public final int minGroupCount;
      public final int maxGroupCount;

      public SpawnListEntry(EntityType<?> p_i48588_1_, int p_i48588_2_, int p_i48588_3_, int p_i48588_4_) {
         super(p_i48588_2_);
         this.entityType = p_i48588_1_;
         this.minGroupCount = p_i48588_3_;
         this.maxGroupCount = p_i48588_4_;
      }

      public String toString() {
         return EntityType.getKey(this.entityType) + "*(" + this.minGroupCount + "-" + this.maxGroupCount + "):" + this.itemWeight;
      }
   }

   public static enum RainType {
      NONE("none"),
      RAIN("rain"),
      SNOW("snow");

      private static final Map<String, Biome.RainType> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(Biome.RainType::getName, (p_lambda$static$0_0_) -> {
         return p_lambda$static$0_0_;
      }));
      private final String name;

      private RainType(String p_i50593_3_) {
         this.name = p_i50593_3_;
      }

      public String getName() {
         return this.name;
      }
   }

   public static enum Category {
      NONE("none"),
      TAIGA("taiga"),
      EXTREME_HILLS("extreme_hills"),
      JUNGLE("jungle"),
      MESA("mesa"),
      PLAINS("plains"),
      SAVANNA("savanna"),
      ICY("icy"),
      THEEND("the_end"),
      BEACH("beach"),
      FOREST("forest"),
      OCEAN("ocean"),
      DESERT("desert"),
      RIVER("river"),
      SWAMP("swamp"),
      MUSHROOM("mushroom"),
      NETHER("nether");

      private static final Map<String, Biome.Category> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(Biome.Category::getName, (p_lambda$static$0_0_) -> {
         return p_lambda$static$0_0_;
      }));
      private final String name;

      private Category(String p_i50595_3_) {
         this.name = p_i50595_3_;
      }

      public String getName() {
         return this.name;
      }
   }

   public static class Builder {
      @Nullable
      private ConfiguredSurfaceBuilder<?> surfaceBuilder;
      @Nullable
      private Biome.RainType precipitation;
      @Nullable
      private Biome.Category category;
      @Nullable
      private Float depth;
      @Nullable
      private Float scale;
      @Nullable
      private Float temperature;
      @Nullable
      private Float downfall;
      @Nullable
      private Integer waterColor;
      @Nullable
      private Integer waterFogColor;
      @Nullable
      private String parent;

      public <SC extends ISurfaceBuilderConfig> Biome.Builder surfaceBuilder(SurfaceBuilder<SC> p_222351_1_, SC p_222351_2_) {
         this.surfaceBuilder = new ConfiguredSurfaceBuilder(p_222351_1_, p_222351_2_);
         return this;
      }

      public Biome.Builder surfaceBuilder(ConfiguredSurfaceBuilder<?> p_205416_1_) {
         this.surfaceBuilder = p_205416_1_;
         return this;
      }

      public Biome.Builder precipitation(Biome.RainType p_205415_1_) {
         this.precipitation = p_205415_1_;
         return this;
      }

      public Biome.Builder category(Biome.Category p_205419_1_) {
         this.category = p_205419_1_;
         return this;
      }

      public Biome.Builder depth(float p_205421_1_) {
         this.depth = p_205421_1_;
         return this;
      }

      public Biome.Builder scale(float p_205420_1_) {
         this.scale = p_205420_1_;
         return this;
      }

      public Biome.Builder temperature(float p_205414_1_) {
         this.temperature = p_205414_1_;
         return this;
      }

      public Biome.Builder downfall(float p_205417_1_) {
         this.downfall = p_205417_1_;
         return this;
      }

      public Biome.Builder waterColor(int p_205412_1_) {
         this.waterColor = p_205412_1_;
         return this;
      }

      public Biome.Builder waterFogColor(int p_205413_1_) {
         this.waterFogColor = p_205413_1_;
         return this;
      }

      public Biome.Builder parent(@Nullable String p_205418_1_) {
         this.parent = p_205418_1_;
         return this;
      }

      public String toString() {
         return "BiomeBuilder{\nsurfaceBuilder=" + this.surfaceBuilder + ",\nprecipitation=" + this.precipitation + ",\nbiomeCategory=" + this.category + ",\ndepth=" + this.depth + ",\nscale=" + this.scale + ",\ntemperature=" + this.temperature + ",\ndownfall=" + this.downfall + ",\nwaterColor=" + this.waterColor + ",\nwaterFogColor=" + this.waterFogColor + ",\nparent='" + this.parent + '\'' + "\n" + '}';
      }
   }
}
