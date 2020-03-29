package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class TwoFeatureChoiceConfig implements IFeatureConfig {
   public final ConfiguredFeature<?, ?> field_227285_a_;
   public final ConfiguredFeature<?, ?> field_227286_b_;

   public TwoFeatureChoiceConfig(ConfiguredFeature<?, ?> p_i225835_1_, ConfiguredFeature<?, ?> p_i225835_2_) {
      this.field_227285_a_ = p_i225835_1_;
      this.field_227286_b_ = p_i225835_2_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("feature_true"), this.field_227285_a_.serialize(p_214634_1_).getValue(), p_214634_1_.createString("feature_false"), this.field_227286_b_.serialize(p_214634_1_).getValue())));
   }

   public static <T> TwoFeatureChoiceConfig func_227287_a_(Dynamic<T> p_227287_0_) {
      ConfiguredFeature<?, ?> lvt_1_1_ = ConfiguredFeature.deserialize(p_227287_0_.get("feature_true").orElseEmptyMap());
      ConfiguredFeature<?, ?> lvt_2_1_ = ConfiguredFeature.deserialize(p_227287_0_.get("feature_false").orElseEmptyMap());
      return new TwoFeatureChoiceConfig(lvt_1_1_, lvt_2_1_);
   }
}
