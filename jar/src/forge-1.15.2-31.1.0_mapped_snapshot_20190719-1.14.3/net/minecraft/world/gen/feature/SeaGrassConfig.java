package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class SeaGrassConfig implements IFeatureConfig {
   public final int count;
   public final double tallProbability;

   public SeaGrassConfig(int p_i48776_1_, double p_i48776_2_) {
      this.count = p_i48776_1_;
      this.tallProbability = p_i48776_2_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("count"), p_214634_1_.createInt(this.count), p_214634_1_.createString("tall_seagrass_probability"), p_214634_1_.createDouble(this.tallProbability))));
   }

   public static <T> SeaGrassConfig deserialize(Dynamic<T> p_214659_0_) {
      int lvt_1_1_ = p_214659_0_.get("count").asInt(0);
      double lvt_2_1_ = p_214659_0_.get("tall_seagrass_probability").asDouble(0.0D);
      return new SeaGrassConfig(lvt_1_1_, lvt_2_1_);
   }
}
