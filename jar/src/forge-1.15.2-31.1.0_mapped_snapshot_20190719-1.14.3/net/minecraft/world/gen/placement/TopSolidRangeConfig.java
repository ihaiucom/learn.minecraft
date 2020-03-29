package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class TopSolidRangeConfig implements IPlacementConfig {
   public final int min;
   public final int max;

   public TopSolidRangeConfig(int p_i51375_1_, int p_i51375_2_) {
      this.min = p_i51375_1_;
      this.max = p_i51375_2_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214719_1_) {
      return new Dynamic(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("min"), p_214719_1_.createInt(this.min), p_214719_1_.createString("max"), p_214719_1_.createInt(this.max))));
   }

   public static TopSolidRangeConfig deserialize(Dynamic<?> p_214725_0_) {
      int lvt_1_1_ = p_214725_0_.get("min").asInt(0);
      int lvt_2_1_ = p_214725_0_.get("max").asInt(0);
      return new TopSolidRangeConfig(lvt_1_1_, lvt_2_1_);
   }
}
