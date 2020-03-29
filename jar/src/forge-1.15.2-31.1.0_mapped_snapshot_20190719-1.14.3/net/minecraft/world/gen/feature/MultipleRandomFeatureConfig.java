package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;

public class MultipleRandomFeatureConfig implements IFeatureConfig {
   public final List<ConfiguredRandomFeatureList<?>> features;
   public final ConfiguredFeature<?, ?> defaultFeature;

   public MultipleRandomFeatureConfig(List<ConfiguredRandomFeatureList<?>> p_i51455_1_, ConfiguredFeature<?, ?> p_i51455_2_) {
      this.features = p_i51455_1_;
      this.defaultFeature = p_i51455_2_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      T lvt_2_1_ = p_214634_1_.createList(this.features.stream().map((p_227288_1_) -> {
         return p_227288_1_.serialize(p_214634_1_).getValue();
      }));
      T lvt_3_1_ = this.defaultFeature.serialize(p_214634_1_).getValue();
      return new Dynamic(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("features"), lvt_2_1_, p_214634_1_.createString("default"), lvt_3_1_)));
   }

   public static <T> MultipleRandomFeatureConfig deserialize(Dynamic<T> p_214648_0_) {
      List<ConfiguredRandomFeatureList<?>> lvt_1_1_ = p_214648_0_.get("features").asList(ConfiguredRandomFeatureList::func_214840_a);
      ConfiguredFeature<?, ?> lvt_2_1_ = ConfiguredFeature.deserialize(p_214648_0_.get("default").orElseEmptyMap());
      return new MultipleRandomFeatureConfig(lvt_1_1_, lvt_2_1_);
   }
}
