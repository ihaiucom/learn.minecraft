package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguredFeature<FC extends IFeatureConfig, F extends Feature<FC>> {
   public static final Logger field_227226_a_ = LogManager.getLogger();
   public final F feature;
   public final FC config;

   public ConfiguredFeature(F p_i49900_1_, FC p_i49900_2_) {
      this.feature = p_i49900_1_;
      this.config = p_i49900_2_;
   }

   public ConfiguredFeature(F p_i49901_1_, Dynamic<?> p_i49901_2_) {
      this(p_i49901_1_, p_i49901_1_.createConfig(p_i49901_2_));
   }

   public ConfiguredFeature<?, ?> func_227228_a_(ConfiguredPlacement<?> p_227228_1_) {
      Feature<DecoratedFeatureConfig> lvt_2_1_ = this.feature instanceof FlowersFeature ? Feature.DECORATED_FLOWER : Feature.DECORATED;
      return lvt_2_1_.func_225566_b_(new DecoratedFeatureConfig(this, p_227228_1_));
   }

   public ConfiguredRandomFeatureList<FC> func_227227_a_(float p_227227_1_) {
      return new ConfiguredRandomFeatureList(this, p_227227_1_);
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_222735_1_) {
      return new Dynamic(p_222735_1_, p_222735_1_.createMap(ImmutableMap.of(p_222735_1_.createString("name"), p_222735_1_.createString(Registry.FEATURE.getKey(this.feature).toString()), p_222735_1_.createString("config"), this.config.serialize(p_222735_1_).getValue())));
   }

   public boolean place(IWorld p_222734_1_, ChunkGenerator<? extends GenerationSettings> p_222734_2_, Random p_222734_3_, BlockPos p_222734_4_) {
      return this.feature.place(p_222734_1_, p_222734_2_, p_222734_3_, p_222734_4_, this.config);
   }

   public static <T> ConfiguredFeature<?, ?> deserialize(Dynamic<T> p_222736_0_) {
      String lvt_1_1_ = p_222736_0_.get("name").asString("");
      Feature lvt_2_1_ = (Feature)Registry.FEATURE.getOrDefault(new ResourceLocation(lvt_1_1_));

      try {
         return new ConfiguredFeature(lvt_2_1_, p_222736_0_.get("config").orElseEmptyMap());
      } catch (RuntimeException var4) {
         field_227226_a_.warn("Error while deserializing {}", lvt_1_1_);
         return new ConfiguredFeature(Feature.field_227245_q_, NoFeatureConfig.NO_FEATURE_CONFIG);
      }
   }
}
