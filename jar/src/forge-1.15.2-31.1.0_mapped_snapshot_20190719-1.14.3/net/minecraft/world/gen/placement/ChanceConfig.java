package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class ChanceConfig implements IPlacementConfig {
   public final int chance;

   public ChanceConfig(int p_i48665_1_) {
      this.chance = p_i48665_1_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214719_1_) {
      return new Dynamic(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("chance"), p_214719_1_.createInt(this.chance))));
   }

   public static ChanceConfig deserialize(Dynamic<?> p_214722_0_) {
      int lvt_1_1_ = p_214722_0_.get("chance").asInt(0);
      return new ChanceConfig(lvt_1_1_);
   }
}
