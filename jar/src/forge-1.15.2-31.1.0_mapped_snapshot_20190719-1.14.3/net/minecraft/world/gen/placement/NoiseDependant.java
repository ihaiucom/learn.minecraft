package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NoiseDependant implements IPlacementConfig {
   public final double noiseLevel;
   public final int belowNoise;
   public final int aboveNoise;

   public NoiseDependant(double p_i48685_1_, int p_i48685_3_, int p_i48685_4_) {
      this.noiseLevel = p_i48685_1_;
      this.belowNoise = p_i48685_3_;
      this.aboveNoise = p_i48685_4_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214719_1_) {
      return new Dynamic(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("noise_level"), p_214719_1_.createDouble(this.noiseLevel), p_214719_1_.createString("below_noise"), p_214719_1_.createInt(this.belowNoise), p_214719_1_.createString("above_noise"), p_214719_1_.createInt(this.aboveNoise))));
   }

   public static NoiseDependant deserialize(Dynamic<?> p_214734_0_) {
      double lvt_1_1_ = p_214734_0_.get("noise_level").asDouble(0.0D);
      int lvt_3_1_ = p_214734_0_.get("below_noise").asInt(0);
      int lvt_4_1_ = p_214734_0_.get("above_noise").asInt(0);
      return new NoiseDependant(lvt_1_1_, lvt_3_1_, lvt_4_1_);
   }
}
