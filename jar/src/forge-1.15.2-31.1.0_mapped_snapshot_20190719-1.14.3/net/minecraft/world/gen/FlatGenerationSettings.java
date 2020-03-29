package net.minecraft.world.gen;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatGenerationSettings extends GenerationSettings {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ConfiguredFeature<?, ?> MINESHAFT;
   private static final ConfiguredFeature<?, ?> VILLAGE;
   private static final ConfiguredFeature<?, ?> STRONGHOLD;
   private static final ConfiguredFeature<?, ?> SWAMP_HUT;
   private static final ConfiguredFeature<?, ?> DESERT_PYRAMID;
   private static final ConfiguredFeature<?, ?> JUNGLE_TEMPLE;
   private static final ConfiguredFeature<?, ?> IGLOO;
   private static final ConfiguredFeature<?, ?> SHIPWRECK;
   private static final ConfiguredFeature<?, ?> OCEAN_MONUMENT;
   private static final ConfiguredFeature<?, ?> LAKE_WATER;
   private static final ConfiguredFeature<?, ?> LAKE_LAVA;
   private static final ConfiguredFeature<?, ?> END_CITY;
   private static final ConfiguredFeature<?, ?> WOODLAND_MANSION;
   private static final ConfiguredFeature<?, ?> FORTRESS;
   private static final ConfiguredFeature<?, ?> OCEAN_RUIN;
   private static final ConfiguredFeature<?, ?> PILLAGER_OUTPOST;
   public static final Map<ConfiguredFeature<?, ?>, GenerationStage.Decoration> FEATURE_STAGES;
   public static final Map<String, ConfiguredFeature<?, ?>[]> STRUCTURES;
   public static final Map<ConfiguredFeature<?, ?>, IFeatureConfig> FEATURE_CONFIGS;
   private final List<FlatLayerInfo> flatLayers = Lists.newArrayList();
   private final Map<String, Map<String, String>> worldFeatures = Maps.newHashMap();
   private Biome biomeToUse;
   private final BlockState[] states = new BlockState[256];
   private boolean allAir;
   private int field_202246_E;

   @Nullable
   public static Block getBlock(String p_212683_0_) {
      try {
         ResourceLocation lvt_1_1_ = new ResourceLocation(p_212683_0_);
         return (Block)Registry.BLOCK.getValue(lvt_1_1_).orElse((Object)null);
      } catch (IllegalArgumentException var2) {
         LOGGER.warn("Invalid blockstate: {}", p_212683_0_, var2);
         return null;
      }
   }

   public Biome getBiome() {
      return this.biomeToUse;
   }

   public void setBiome(Biome p_82647_1_) {
      this.biomeToUse = p_82647_1_;
   }

   public Map<String, Map<String, String>> getWorldFeatures() {
      return this.worldFeatures;
   }

   public List<FlatLayerInfo> getFlatLayers() {
      return this.flatLayers;
   }

   public void updateLayers() {
      int lvt_1_2_ = 0;

      Iterator var2;
      FlatLayerInfo lvt_3_2_;
      for(var2 = this.flatLayers.iterator(); var2.hasNext(); lvt_1_2_ += lvt_3_2_.getLayerCount()) {
         lvt_3_2_ = (FlatLayerInfo)var2.next();
         lvt_3_2_.setMinY(lvt_1_2_);
      }

      this.field_202246_E = 0;
      this.allAir = true;
      lvt_1_2_ = 0;
      var2 = this.flatLayers.iterator();

      while(var2.hasNext()) {
         lvt_3_2_ = (FlatLayerInfo)var2.next();

         for(int lvt_4_1_ = lvt_3_2_.getMinY(); lvt_4_1_ < lvt_3_2_.getMinY() + lvt_3_2_.getLayerCount(); ++lvt_4_1_) {
            BlockState lvt_5_1_ = lvt_3_2_.getLayerMaterial();
            if (lvt_5_1_.getBlock() != Blocks.AIR) {
               this.allAir = false;
               this.states[lvt_4_1_] = lvt_5_1_;
            }
         }

         if (lvt_3_2_.getLayerMaterial().getBlock() == Blocks.AIR) {
            lvt_1_2_ += lvt_3_2_.getLayerCount();
         } else {
            this.field_202246_E += lvt_3_2_.getLayerCount() + lvt_1_2_;
            lvt_1_2_ = 0;
         }
      }

   }

   public String toString() {
      StringBuilder lvt_1_1_ = new StringBuilder();

      int lvt_2_2_;
      for(lvt_2_2_ = 0; lvt_2_2_ < this.flatLayers.size(); ++lvt_2_2_) {
         if (lvt_2_2_ > 0) {
            lvt_1_1_.append(",");
         }

         lvt_1_1_.append(this.flatLayers.get(lvt_2_2_));
      }

      lvt_1_1_.append(";");
      lvt_1_1_.append(Registry.BIOME.getKey(this.biomeToUse));
      lvt_1_1_.append(";");
      if (!this.worldFeatures.isEmpty()) {
         lvt_2_2_ = 0;
         Iterator var3 = this.worldFeatures.entrySet().iterator();

         while(true) {
            Map lvt_5_1_;
            do {
               if (!var3.hasNext()) {
                  return lvt_1_1_.toString();
               }

               Entry<String, Map<String, String>> lvt_4_1_ = (Entry)var3.next();
               if (lvt_2_2_++ > 0) {
                  lvt_1_1_.append(",");
               }

               lvt_1_1_.append(((String)lvt_4_1_.getKey()).toLowerCase(Locale.ROOT));
               lvt_5_1_ = (Map)lvt_4_1_.getValue();
            } while(lvt_5_1_.isEmpty());

            lvt_1_1_.append("(");
            int lvt_6_1_ = 0;
            Iterator var7 = lvt_5_1_.entrySet().iterator();

            while(var7.hasNext()) {
               Entry<String, String> lvt_8_1_ = (Entry)var7.next();
               if (lvt_6_1_++ > 0) {
                  lvt_1_1_.append(" ");
               }

               lvt_1_1_.append((String)lvt_8_1_.getKey());
               lvt_1_1_.append("=");
               lvt_1_1_.append((String)lvt_8_1_.getValue());
            }

            lvt_1_1_.append(")");
         }
      } else {
         return lvt_1_1_.toString();
      }
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   private static FlatLayerInfo deserializeLayer(String p_197526_0_, int p_197526_1_) {
      String[] lvt_2_1_ = p_197526_0_.split("\\*", 2);
      int lvt_3_2_;
      if (lvt_2_1_.length == 2) {
         try {
            lvt_3_2_ = Math.max(Integer.parseInt(lvt_2_1_[0]), 0);
         } catch (NumberFormatException var9) {
            LOGGER.error("Error while parsing flat world string => {}", var9.getMessage());
            return null;
         }
      } else {
         lvt_3_2_ = 1;
      }

      int lvt_4_2_ = Math.min(p_197526_1_ + lvt_3_2_, 256);
      int lvt_5_1_ = lvt_4_2_ - p_197526_1_;

      Block lvt_6_2_;
      try {
         lvt_6_2_ = getBlock(lvt_2_1_[lvt_2_1_.length - 1]);
      } catch (Exception var8) {
         LOGGER.error("Error while parsing flat world string => {}", var8.getMessage());
         return null;
      }

      if (lvt_6_2_ == null) {
         LOGGER.error("Error while parsing flat world string => Unknown block, {}", lvt_2_1_[lvt_2_1_.length - 1]);
         return null;
      } else {
         FlatLayerInfo lvt_7_2_ = new FlatLayerInfo(lvt_5_1_, lvt_6_2_);
         lvt_7_2_.setMinY(p_197526_1_);
         return lvt_7_2_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static List<FlatLayerInfo> deserializeLayers(String p_197527_0_) {
      List<FlatLayerInfo> lvt_1_1_ = Lists.newArrayList();
      String[] lvt_2_1_ = p_197527_0_.split(",");
      int lvt_3_1_ = 0;
      String[] var4 = lvt_2_1_;
      int var5 = lvt_2_1_.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String lvt_7_1_ = var4[var6];
         FlatLayerInfo lvt_8_1_ = deserializeLayer(lvt_7_1_, lvt_3_1_);
         if (lvt_8_1_ == null) {
            return Collections.emptyList();
         }

         lvt_1_1_.add(lvt_8_1_);
         lvt_3_1_ += lvt_8_1_.getLayerCount();
      }

      return lvt_1_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public <T> Dynamic<T> func_210834_a(DynamicOps<T> p_210834_1_) {
      T lvt_2_1_ = p_210834_1_.createList(this.flatLayers.stream().map((p_210837_1_) -> {
         return p_210834_1_.createMap(ImmutableMap.of(p_210834_1_.createString("height"), p_210834_1_.createInt(p_210837_1_.getLayerCount()), p_210834_1_.createString("block"), p_210834_1_.createString(Registry.BLOCK.getKey(p_210837_1_.getLayerMaterial().getBlock()).toString())));
      }));
      T lvt_3_1_ = p_210834_1_.createMap((Map)this.worldFeatures.entrySet().stream().map((p_210833_1_) -> {
         return Pair.of(p_210834_1_.createString(((String)p_210833_1_.getKey()).toLowerCase(Locale.ROOT)), p_210834_1_.createMap((Map)((Map)p_210833_1_.getValue()).entrySet().stream().map((p_210836_1_) -> {
            return Pair.of(p_210834_1_.createString((String)p_210836_1_.getKey()), p_210834_1_.createString((String)p_210836_1_.getValue()));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      return new Dynamic(p_210834_1_, p_210834_1_.createMap(ImmutableMap.of(p_210834_1_.createString("layers"), lvt_2_1_, p_210834_1_.createString("biome"), p_210834_1_.createString(Registry.BIOME.getKey(this.biomeToUse).toString()), p_210834_1_.createString("structures"), lvt_3_1_)));
   }

   public static FlatGenerationSettings createFlatGenerator(Dynamic<?> p_210835_0_) {
      FlatGenerationSettings lvt_1_1_ = (FlatGenerationSettings)ChunkGeneratorType.FLAT.createSettings();
      List<Pair<Integer, Block>> lvt_2_1_ = p_210835_0_.get("layers").asList((p_210838_0_) -> {
         return Pair.of(p_210838_0_.get("height").asInt(1), getBlock(p_210838_0_.get("block").asString("")));
      });
      if (lvt_2_1_.stream().anyMatch((p_211743_0_) -> {
         return p_211743_0_.getSecond() == null;
      })) {
         return getDefaultFlatGenerator();
      } else {
         List<FlatLayerInfo> lvt_3_1_ = (List)lvt_2_1_.stream().map((p_211740_0_) -> {
            return new FlatLayerInfo((Integer)p_211740_0_.getFirst(), (Block)p_211740_0_.getSecond());
         }).collect(Collectors.toList());
         if (lvt_3_1_.isEmpty()) {
            return getDefaultFlatGenerator();
         } else {
            lvt_1_1_.getFlatLayers().addAll(lvt_3_1_);
            lvt_1_1_.updateLayers();
            lvt_1_1_.setBiome((Biome)Registry.BIOME.getOrDefault(new ResourceLocation(p_210835_0_.get("biome").asString(""))));
            p_210835_0_.get("structures").flatMap(Dynamic::getMapValues).ifPresent((p_211738_1_) -> {
               p_211738_1_.keySet().forEach((p_211739_1_) -> {
                  p_211739_1_.asString().map((p_211742_1_) -> {
                     return (Map)lvt_1_1_.getWorldFeatures().put(p_211742_1_, Maps.newHashMap());
                  });
               });
            });
            return lvt_1_1_;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static FlatGenerationSettings createFlatGeneratorFromString(String p_82651_0_) {
      Iterator<String> lvt_1_1_ = Splitter.on(';').split(p_82651_0_).iterator();
      if (!lvt_1_1_.hasNext()) {
         return getDefaultFlatGenerator();
      } else {
         FlatGenerationSettings lvt_2_1_ = (FlatGenerationSettings)ChunkGeneratorType.FLAT.createSettings();
         List<FlatLayerInfo> lvt_3_1_ = deserializeLayers((String)lvt_1_1_.next());
         if (lvt_3_1_.isEmpty()) {
            return getDefaultFlatGenerator();
         } else {
            lvt_2_1_.getFlatLayers().addAll(lvt_3_1_);
            lvt_2_1_.updateLayers();
            Biome lvt_4_1_ = Biomes.PLAINS;
            if (lvt_1_1_.hasNext()) {
               try {
                  ResourceLocation lvt_5_1_ = new ResourceLocation((String)lvt_1_1_.next());
                  lvt_4_1_ = (Biome)Registry.BIOME.getValue(lvt_5_1_).orElseThrow(() -> {
                     return new IllegalArgumentException("Invalid Biome: " + lvt_5_1_);
                  });
               } catch (Exception var17) {
                  LOGGER.error("Error while parsing flat world string => {}", var17.getMessage());
               }
            }

            lvt_2_1_.setBiome(lvt_4_1_);
            if (lvt_1_1_.hasNext()) {
               String[] lvt_5_3_ = ((String)lvt_1_1_.next()).toLowerCase(Locale.ROOT).split(",");
               String[] var6 = lvt_5_3_;
               int var7 = lvt_5_3_.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  String lvt_9_1_ = var6[var8];
                  String[] lvt_10_1_ = lvt_9_1_.split("\\(", 2);
                  if (!lvt_10_1_[0].isEmpty()) {
                     lvt_2_1_.addStructure(lvt_10_1_[0]);
                     if (lvt_10_1_.length > 1 && lvt_10_1_[1].endsWith(")") && lvt_10_1_[1].length() > 1) {
                        String[] lvt_11_1_ = lvt_10_1_[1].substring(0, lvt_10_1_[1].length() - 1).split(" ");
                        String[] var12 = lvt_11_1_;
                        int var13 = lvt_11_1_.length;

                        for(int var14 = 0; var14 < var13; ++var14) {
                           String lvt_15_1_ = var12[var14];
                           String[] lvt_16_1_ = lvt_15_1_.split("=", 2);
                           if (lvt_16_1_.length == 2) {
                              lvt_2_1_.setStructureOption(lvt_10_1_[0], lvt_16_1_[0], lvt_16_1_[1]);
                           }
                        }
                     }
                  }
               }
            } else {
               lvt_2_1_.getWorldFeatures().put("village", Maps.newHashMap());
            }

            return lvt_2_1_;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void addStructure(String p_202234_1_) {
      Map<String, String> lvt_2_1_ = Maps.newHashMap();
      this.worldFeatures.put(p_202234_1_, lvt_2_1_);
   }

   @OnlyIn(Dist.CLIENT)
   private void setStructureOption(String p_202229_1_, String p_202229_2_, String p_202229_3_) {
      ((Map)this.worldFeatures.get(p_202229_1_)).put(p_202229_2_, p_202229_3_);
      if ("village".equals(p_202229_1_) && "distance".equals(p_202229_2_)) {
         this.villageDistance = MathHelper.getInt(p_202229_3_, this.villageDistance, 9);
      }

      if ("biome_1".equals(p_202229_1_) && "distance".equals(p_202229_2_)) {
         this.biomeFeatureDistance = MathHelper.getInt(p_202229_3_, this.biomeFeatureDistance, 9);
      }

      if ("stronghold".equals(p_202229_1_)) {
         if ("distance".equals(p_202229_2_)) {
            this.strongholdDistance = MathHelper.getInt(p_202229_3_, this.strongholdDistance, 1);
         } else if ("count".equals(p_202229_2_)) {
            this.strongholdCount = MathHelper.getInt(p_202229_3_, this.strongholdCount, 1);
         } else if ("spread".equals(p_202229_2_)) {
            this.strongholdSpread = MathHelper.getInt(p_202229_3_, this.strongholdSpread, 1);
         }
      }

      if ("oceanmonument".equals(p_202229_1_)) {
         if ("separation".equals(p_202229_2_)) {
            this.oceanMonumentSeparation = MathHelper.getInt(p_202229_3_, this.oceanMonumentSeparation, 1);
         } else if ("spacing".equals(p_202229_2_)) {
            this.oceanMonumentSpacing = MathHelper.getInt(p_202229_3_, this.oceanMonumentSpacing, 1);
         }
      }

      if ("endcity".equals(p_202229_1_) && "distance".equals(p_202229_2_)) {
         this.endCityDistance = MathHelper.getInt(p_202229_3_, this.endCityDistance, 1);
      }

      if ("mansion".equals(p_202229_1_) && "distance".equals(p_202229_2_)) {
         this.mansionDistance = MathHelper.getInt(p_202229_3_, this.mansionDistance, 1);
      }

   }

   public static FlatGenerationSettings getDefaultFlatGenerator() {
      FlatGenerationSettings lvt_0_1_ = (FlatGenerationSettings)ChunkGeneratorType.FLAT.createSettings();
      lvt_0_1_.setBiome(Biomes.PLAINS);
      lvt_0_1_.getFlatLayers().add(new FlatLayerInfo(1, Blocks.BEDROCK));
      lvt_0_1_.getFlatLayers().add(new FlatLayerInfo(2, Blocks.DIRT));
      lvt_0_1_.getFlatLayers().add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
      lvt_0_1_.updateLayers();
      lvt_0_1_.getWorldFeatures().put("village", Maps.newHashMap());
      return lvt_0_1_;
   }

   public boolean isAllAir() {
      return this.allAir;
   }

   public BlockState[] getStates() {
      return this.states;
   }

   public void func_214990_a(int p_214990_1_) {
      this.states[p_214990_1_] = null;
   }

   static {
      MINESHAFT = Feature.MINESHAFT.func_225566_b_(new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL)).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG));
      VILLAGE = Feature.VILLAGE.func_225566_b_(new VillageConfig("village/plains/town_centers", 6)).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG));
      STRONGHOLD = Feature.STRONGHOLD.func_225566_b_(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG));
      SWAMP_HUT = Feature.SWAMP_HUT.func_225566_b_(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG));
      DESERT_PYRAMID = Feature.DESERT_PYRAMID.func_225566_b_(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG));
      JUNGLE_TEMPLE = Feature.JUNGLE_TEMPLE.func_225566_b_(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG));
      IGLOO = Feature.IGLOO.func_225566_b_(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG));
      SHIPWRECK = Feature.SHIPWRECK.func_225566_b_(new ShipwreckConfig(false)).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG));
      OCEAN_MONUMENT = Feature.OCEAN_MONUMENT.func_225566_b_(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG));
      LAKE_WATER = Feature.LAKE.func_225566_b_(new BlockStateFeatureConfig(Blocks.WATER.getDefaultState())).func_227228_a_(Placement.WATER_LAKE.func_227446_a_(new ChanceConfig(4)));
      LAKE_LAVA = Feature.LAKE.func_225566_b_(new BlockStateFeatureConfig(Blocks.LAVA.getDefaultState())).func_227228_a_(Placement.LAVA_LAKE.func_227446_a_(new ChanceConfig(80)));
      END_CITY = Feature.END_CITY.func_225566_b_(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG));
      WOODLAND_MANSION = Feature.WOODLAND_MANSION.func_225566_b_(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG));
      FORTRESS = Feature.NETHER_BRIDGE.func_225566_b_(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG));
      OCEAN_RUIN = Feature.OCEAN_RUIN.func_225566_b_(new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.1F)).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG));
      PILLAGER_OUTPOST = Feature.PILLAGER_OUTPOST.func_225566_b_(IFeatureConfig.NO_FEATURE_CONFIG).func_227228_a_(Placement.NOPE.func_227446_a_(IPlacementConfig.NO_PLACEMENT_CONFIG));
      FEATURE_STAGES = (Map)Util.make(Maps.newHashMap(), (p_209406_0_) -> {
         p_209406_0_.put(MINESHAFT, GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
         p_209406_0_.put(VILLAGE, GenerationStage.Decoration.SURFACE_STRUCTURES);
         p_209406_0_.put(STRONGHOLD, GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
         p_209406_0_.put(SWAMP_HUT, GenerationStage.Decoration.SURFACE_STRUCTURES);
         p_209406_0_.put(DESERT_PYRAMID, GenerationStage.Decoration.SURFACE_STRUCTURES);
         p_209406_0_.put(JUNGLE_TEMPLE, GenerationStage.Decoration.SURFACE_STRUCTURES);
         p_209406_0_.put(IGLOO, GenerationStage.Decoration.SURFACE_STRUCTURES);
         p_209406_0_.put(SHIPWRECK, GenerationStage.Decoration.SURFACE_STRUCTURES);
         p_209406_0_.put(OCEAN_RUIN, GenerationStage.Decoration.SURFACE_STRUCTURES);
         p_209406_0_.put(LAKE_WATER, GenerationStage.Decoration.LOCAL_MODIFICATIONS);
         p_209406_0_.put(LAKE_LAVA, GenerationStage.Decoration.LOCAL_MODIFICATIONS);
         p_209406_0_.put(END_CITY, GenerationStage.Decoration.SURFACE_STRUCTURES);
         p_209406_0_.put(WOODLAND_MANSION, GenerationStage.Decoration.SURFACE_STRUCTURES);
         p_209406_0_.put(FORTRESS, GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
         p_209406_0_.put(OCEAN_MONUMENT, GenerationStage.Decoration.SURFACE_STRUCTURES);
         p_209406_0_.put(PILLAGER_OUTPOST, GenerationStage.Decoration.SURFACE_STRUCTURES);
      });
      STRUCTURES = (Map)Util.make(Maps.newHashMap(), (p_209404_0_) -> {
         p_209404_0_.put("mineshaft", new ConfiguredFeature[]{MINESHAFT});
         p_209404_0_.put("village", new ConfiguredFeature[]{VILLAGE});
         p_209404_0_.put("stronghold", new ConfiguredFeature[]{STRONGHOLD});
         p_209404_0_.put("biome_1", new ConfiguredFeature[]{SWAMP_HUT, DESERT_PYRAMID, JUNGLE_TEMPLE, IGLOO, OCEAN_RUIN, SHIPWRECK});
         p_209404_0_.put("oceanmonument", new ConfiguredFeature[]{OCEAN_MONUMENT});
         p_209404_0_.put("lake", new ConfiguredFeature[]{LAKE_WATER});
         p_209404_0_.put("lava_lake", new ConfiguredFeature[]{LAKE_LAVA});
         p_209404_0_.put("endcity", new ConfiguredFeature[]{END_CITY});
         p_209404_0_.put("mansion", new ConfiguredFeature[]{WOODLAND_MANSION});
         p_209404_0_.put("fortress", new ConfiguredFeature[]{FORTRESS});
         p_209404_0_.put("pillager_outpost", new ConfiguredFeature[]{PILLAGER_OUTPOST});
      });
      FEATURE_CONFIGS = (Map)Util.make(Maps.newHashMap(), (p_209405_0_) -> {
         p_209405_0_.put(MINESHAFT, new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL));
         p_209405_0_.put(VILLAGE, new VillageConfig("village/plains/town_centers", 6));
         p_209405_0_.put(STRONGHOLD, IFeatureConfig.NO_FEATURE_CONFIG);
         p_209405_0_.put(SWAMP_HUT, IFeatureConfig.NO_FEATURE_CONFIG);
         p_209405_0_.put(DESERT_PYRAMID, IFeatureConfig.NO_FEATURE_CONFIG);
         p_209405_0_.put(JUNGLE_TEMPLE, IFeatureConfig.NO_FEATURE_CONFIG);
         p_209405_0_.put(IGLOO, IFeatureConfig.NO_FEATURE_CONFIG);
         p_209405_0_.put(OCEAN_RUIN, new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F));
         p_209405_0_.put(SHIPWRECK, new ShipwreckConfig(false));
         p_209405_0_.put(OCEAN_MONUMENT, IFeatureConfig.NO_FEATURE_CONFIG);
         p_209405_0_.put(END_CITY, IFeatureConfig.NO_FEATURE_CONFIG);
         p_209405_0_.put(WOODLAND_MANSION, IFeatureConfig.NO_FEATURE_CONFIG);
         p_209405_0_.put(FORTRESS, IFeatureConfig.NO_FEATURE_CONFIG);
         p_209405_0_.put(PILLAGER_OUTPOST, IFeatureConfig.NO_FEATURE_CONFIG);
      });
   }
}
