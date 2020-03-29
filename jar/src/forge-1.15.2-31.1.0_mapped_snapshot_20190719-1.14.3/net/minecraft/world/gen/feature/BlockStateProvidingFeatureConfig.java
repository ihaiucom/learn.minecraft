package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;

public class BlockStateProvidingFeatureConfig implements IFeatureConfig {
   public final BlockStateProvider field_227268_a_;

   public BlockStateProvidingFeatureConfig(BlockStateProvider p_i225830_1_) {
      this.field_227268_a_ = p_i225830_1_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      Builder<T, T> lvt_2_1_ = ImmutableMap.builder();
      lvt_2_1_.put(p_214634_1_.createString("state_provider"), this.field_227268_a_.serialize(p_214634_1_));
      return new Dynamic(p_214634_1_, p_214634_1_.createMap(lvt_2_1_.build()));
   }

   public static <T> BlockStateProvidingFeatureConfig func_227269_a_(Dynamic<T> p_227269_0_) {
      BlockStateProviderType<?> lvt_1_1_ = (BlockStateProviderType)Registry.field_229387_t_.getOrDefault(new ResourceLocation((String)p_227269_0_.get("state_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      return new BlockStateProvidingFeatureConfig(lvt_1_1_.func_227399_a_(p_227269_0_.get("state_provider").orElseEmptyMap()));
   }
}
