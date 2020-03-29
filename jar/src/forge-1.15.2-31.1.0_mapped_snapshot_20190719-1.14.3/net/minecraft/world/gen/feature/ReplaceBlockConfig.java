package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class ReplaceBlockConfig implements IFeatureConfig {
   public final BlockState target;
   public final BlockState state;

   public ReplaceBlockConfig(BlockState p_i51445_1_, BlockState p_i51445_2_) {
      this.target = p_i51445_1_;
      this.state = p_i51445_2_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("target"), BlockState.serialize(p_214634_1_, this.target).getValue(), p_214634_1_.createString("state"), BlockState.serialize(p_214634_1_, this.state).getValue())));
   }

   public static <T> ReplaceBlockConfig deserialize(Dynamic<T> p_214657_0_) {
      BlockState lvt_1_1_ = (BlockState)p_214657_0_.get("target").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      BlockState lvt_2_1_ = (BlockState)p_214657_0_.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      return new ReplaceBlockConfig(lvt_1_1_, lvt_2_1_);
   }
}
