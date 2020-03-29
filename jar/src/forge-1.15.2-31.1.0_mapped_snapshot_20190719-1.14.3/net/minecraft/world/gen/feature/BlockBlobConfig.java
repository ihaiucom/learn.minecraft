package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class BlockBlobConfig implements IFeatureConfig {
   public final BlockState state;
   public final int startRadius;

   public BlockBlobConfig(BlockState p_i49916_1_, int p_i49916_2_) {
      this.state = p_i49916_1_;
      this.startRadius = p_i49916_2_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("state"), BlockState.serialize(p_214634_1_, this.state).getValue(), p_214634_1_.createString("start_radius"), p_214634_1_.createInt(this.startRadius))));
   }

   public static <T> BlockBlobConfig deserialize(Dynamic<T> p_214682_0_) {
      BlockState lvt_1_1_ = (BlockState)p_214682_0_.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      int lvt_2_1_ = p_214682_0_.get("start_radius").asInt(0);
      return new BlockBlobConfig(lvt_1_1_, lvt_2_1_);
   }
}
