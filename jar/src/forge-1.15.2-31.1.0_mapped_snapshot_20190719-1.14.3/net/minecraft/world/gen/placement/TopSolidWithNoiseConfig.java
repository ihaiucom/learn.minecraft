package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.Heightmap;

public class TopSolidWithNoiseConfig implements IPlacementConfig {
   public final int noiseToCountRatio;
   public final double noiseFactor;
   public final double noiseOffset;
   public final Heightmap.Type heightmap;

   public TopSolidWithNoiseConfig(int p_i51376_1_, double p_i51376_2_, double p_i51376_4_, Heightmap.Type p_i51376_6_) {
      this.noiseToCountRatio = p_i51376_1_;
      this.noiseFactor = p_i51376_2_;
      this.noiseOffset = p_i51376_4_;
      this.heightmap = p_i51376_6_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214719_1_) {
      return new Dynamic(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("noise_to_count_ratio"), p_214719_1_.createInt(this.noiseToCountRatio), p_214719_1_.createString("noise_factor"), p_214719_1_.createDouble(this.noiseFactor), p_214719_1_.createString("noise_offset"), p_214719_1_.createDouble(this.noiseOffset), p_214719_1_.createString("heightmap"), p_214719_1_.createString(this.heightmap.getId()))));
   }

   public static TopSolidWithNoiseConfig deserialize(Dynamic<?> p_214726_0_) {
      int lvt_1_1_ = p_214726_0_.get("noise_to_count_ratio").asInt(10);
      double lvt_2_1_ = p_214726_0_.get("noise_factor").asDouble(80.0D);
      double lvt_4_1_ = p_214726_0_.get("noise_offset").asDouble(0.0D);
      Heightmap.Type lvt_6_1_ = Heightmap.Type.func_203501_a(p_214726_0_.get("heightmap").asString("OCEAN_FLOOR_WG"));
      return new TopSolidWithNoiseConfig(lvt_1_1_, lvt_2_1_, lvt_4_1_, lvt_6_1_);
   }
}
