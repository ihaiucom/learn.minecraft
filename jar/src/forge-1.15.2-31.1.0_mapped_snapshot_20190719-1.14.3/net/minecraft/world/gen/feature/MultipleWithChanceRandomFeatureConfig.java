package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;

public class MultipleWithChanceRandomFeatureConfig implements IFeatureConfig {
   public final List<ConfiguredFeature<?, ?>> features;
   public final int count;

   public MultipleWithChanceRandomFeatureConfig(List<ConfiguredFeature<?, ?>> p_i51451_1_, int p_i51451_2_) {
      this.features = p_i51451_1_;
      this.count = p_i51451_2_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("features"), p_214634_1_.createList(this.features.stream().map((p_227324_1_) -> {
         return p_227324_1_.serialize(p_214634_1_).getValue();
      })), p_214634_1_.createString("count"), p_214634_1_.createInt(this.count))));
   }

   public static <T> MultipleWithChanceRandomFeatureConfig deserialize(Dynamic<T> p_214653_0_) {
      List<ConfiguredFeature<?, ?>> lvt_1_1_ = p_214653_0_.get("features").asList(ConfiguredFeature::deserialize);
      int lvt_2_1_ = p_214653_0_.get("count").asInt(0);
      return new MultipleWithChanceRandomFeatureConfig(lvt_1_1_, lvt_2_1_);
   }
}
