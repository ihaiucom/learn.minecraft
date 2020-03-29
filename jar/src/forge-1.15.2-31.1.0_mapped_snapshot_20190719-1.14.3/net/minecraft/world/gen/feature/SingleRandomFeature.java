package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;

public class SingleRandomFeature implements IFeatureConfig {
   public final List<ConfiguredFeature<?, ?>> features;

   public SingleRandomFeature(List<ConfiguredFeature<?, ?>> p_i51437_1_) {
      this.features = p_i51437_1_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("features"), p_214634_1_.createList(this.features.stream().map((p_227326_1_) -> {
         return p_227326_1_.serialize(p_214634_1_).getValue();
      })))));
   }

   public static <T> SingleRandomFeature deserialize(Dynamic<T> p_214664_0_) {
      List<ConfiguredFeature<?, ?>> lvt_1_1_ = p_214664_0_.get("features").asList(ConfiguredFeature::deserialize);
      return new SingleRandomFeature(lvt_1_1_);
   }
}
