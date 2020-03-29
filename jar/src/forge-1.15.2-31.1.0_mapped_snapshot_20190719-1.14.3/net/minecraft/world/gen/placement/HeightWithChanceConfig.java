package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class HeightWithChanceConfig implements IPlacementConfig {
   public final int count;
   public final float chance;

   public HeightWithChanceConfig(int p_i48663_1_, float p_i48663_2_) {
      this.count = p_i48663_1_;
      this.chance = p_i48663_2_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214719_1_) {
      return new Dynamic(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("count"), p_214719_1_.createInt(this.count), p_214719_1_.createString("chance"), p_214719_1_.createFloat(this.chance))));
   }

   public static HeightWithChanceConfig deserialize(Dynamic<?> p_214724_0_) {
      int lvt_1_1_ = p_214724_0_.get("count").asInt(0);
      float lvt_2_1_ = p_214724_0_.get("chance").asFloat(0.0F);
      return new HeightWithChanceConfig(lvt_1_1_, lvt_2_1_);
   }
}
