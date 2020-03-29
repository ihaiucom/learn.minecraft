package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class AtSurfaceWithExtraConfig implements IPlacementConfig {
   public final int count;
   public final float extraChance;
   public final int extraCount;

   public AtSurfaceWithExtraConfig(int p_i48662_1_, float p_i48662_2_, int p_i48662_3_) {
      this.count = p_i48662_1_;
      this.extraChance = p_i48662_2_;
      this.extraCount = p_i48662_3_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214719_1_) {
      return new Dynamic(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("count"), p_214719_1_.createInt(this.count), p_214719_1_.createString("extra_chance"), p_214719_1_.createFloat(this.extraChance), p_214719_1_.createString("extra_count"), p_214719_1_.createInt(this.extraCount))));
   }

   public static AtSurfaceWithExtraConfig deserialize(Dynamic<?> p_214723_0_) {
      int lvt_1_1_ = p_214723_0_.get("count").asInt(0);
      float lvt_2_1_ = p_214723_0_.get("extra_chance").asFloat(0.0F);
      int lvt_3_1_ = p_214723_0_.get("extra_count").asInt(0);
      return new AtSurfaceWithExtraConfig(lvt_1_1_, lvt_2_1_, lvt_3_1_);
   }
}
