package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;

public class BigMushroomFeatureConfig implements IFeatureConfig {
   public final BlockStateProvider field_227272_a_;
   public final BlockStateProvider field_227273_b_;
   public final int field_227274_c_;

   public BigMushroomFeatureConfig(BlockStateProvider p_i225832_1_, BlockStateProvider p_i225832_2_, int p_i225832_3_) {
      this.field_227272_a_ = p_i225832_1_;
      this.field_227273_b_ = p_i225832_2_;
      this.field_227274_c_ = p_i225832_3_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      Builder<T, T> lvt_2_1_ = ImmutableMap.builder();
      lvt_2_1_.put(p_214634_1_.createString("cap_provider"), this.field_227272_a_.serialize(p_214634_1_)).put(p_214634_1_.createString("stem_provider"), this.field_227273_b_.serialize(p_214634_1_)).put(p_214634_1_.createString("foliage_radius"), p_214634_1_.createInt(this.field_227274_c_));
      return new Dynamic(p_214634_1_, p_214634_1_.createMap(lvt_2_1_.build()));
   }

   public static <T> BigMushroomFeatureConfig deserialize(Dynamic<T> p_222853_0_) {
      BlockStateProviderType<?> lvt_1_1_ = (BlockStateProviderType)Registry.field_229387_t_.getOrDefault(new ResourceLocation((String)p_222853_0_.get("cap_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      BlockStateProviderType<?> lvt_2_1_ = (BlockStateProviderType)Registry.field_229387_t_.getOrDefault(new ResourceLocation((String)p_222853_0_.get("stem_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      return new BigMushroomFeatureConfig(lvt_1_1_.func_227399_a_(p_222853_0_.get("cap_provider").orElseEmptyMap()), lvt_2_1_.func_227399_a_(p_222853_0_.get("stem_provider").orElseEmptyMap()), p_222853_0_.get("foliage_radius").asInt(2));
   }
}
