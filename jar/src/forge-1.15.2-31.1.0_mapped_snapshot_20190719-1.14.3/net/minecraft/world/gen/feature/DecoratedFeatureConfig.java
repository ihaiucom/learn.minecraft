package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.placement.ConfiguredPlacement;

public class DecoratedFeatureConfig implements IFeatureConfig {
   public final ConfiguredFeature<?, ?> feature;
   public final ConfiguredPlacement<?> decorator;

   public DecoratedFeatureConfig(ConfiguredFeature<?, ?> p_i49891_1_, ConfiguredPlacement<?> p_i49891_2_) {
      this.feature = p_i49891_1_;
      this.decorator = p_i49891_2_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("feature"), this.feature.serialize(p_214634_1_).getValue(), p_214634_1_.createString("decorator"), this.decorator.serialize(p_214634_1_).getValue())));
   }

   public String toString() {
      return String.format("< %s [%s | %s] >", this.getClass().getSimpleName(), Registry.FEATURE.getKey(this.feature.feature), Registry.DECORATOR.getKey(this.decorator.decorator));
   }

   public static <T> DecoratedFeatureConfig deserialize(Dynamic<T> p_214688_0_) {
      ConfiguredFeature<?, ?> lvt_1_1_ = ConfiguredFeature.deserialize(p_214688_0_.get("feature").orElseEmptyMap());
      ConfiguredPlacement<?> lvt_2_1_ = ConfiguredPlacement.deserialize(p_214688_0_.get("decorator").orElseEmptyMap());
      return new DecoratedFeatureConfig(lvt_1_1_, lvt_2_1_);
   }
}
