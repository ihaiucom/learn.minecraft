package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.GenerationStage;

public class CaveEdgeConfig implements IPlacementConfig {
   protected final GenerationStage.Carving step;
   protected final float probability;

   public CaveEdgeConfig(GenerationStage.Carving p_i49000_1_, float p_i49000_2_) {
      this.step = p_i49000_1_;
      this.probability = p_i49000_2_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214719_1_) {
      return new Dynamic(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("step"), p_214719_1_.createString(this.step.toString()), p_214719_1_.createString("probability"), p_214719_1_.createFloat(this.probability))));
   }

   public static CaveEdgeConfig deserialize(Dynamic<?> p_214720_0_) {
      GenerationStage.Carving lvt_1_1_ = GenerationStage.Carving.valueOf(p_214720_0_.get("step").asString(""));
      float lvt_2_1_ = p_214720_0_.get("probability").asFloat(0.0F);
      return new CaveEdgeConfig(lvt_1_1_, lvt_2_1_);
   }
}
