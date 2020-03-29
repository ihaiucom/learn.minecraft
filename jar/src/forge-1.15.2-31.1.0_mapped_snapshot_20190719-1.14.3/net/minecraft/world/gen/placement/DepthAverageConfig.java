package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class DepthAverageConfig implements IPlacementConfig {
   public final int count;
   public final int baseline;
   public final int spread;

   public DepthAverageConfig(int p_i48661_1_, int p_i48661_2_, int p_i48661_3_) {
      this.count = p_i48661_1_;
      this.baseline = p_i48661_2_;
      this.spread = p_i48661_3_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214719_1_) {
      return new Dynamic(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("count"), p_214719_1_.createInt(this.count), p_214719_1_.createString("baseline"), p_214719_1_.createInt(this.baseline), p_214719_1_.createString("spread"), p_214719_1_.createInt(this.spread))));
   }

   public static DepthAverageConfig deserialize(Dynamic<?> p_214729_0_) {
      int lvt_1_1_ = p_214729_0_.get("count").asInt(0);
      int lvt_2_1_ = p_214729_0_.get("baseline").asInt(0);
      int lvt_3_1_ = p_214729_0_.get("spread").asInt(0);
      return new DepthAverageConfig(lvt_1_1_, lvt_2_1_, lvt_3_1_);
   }
}
