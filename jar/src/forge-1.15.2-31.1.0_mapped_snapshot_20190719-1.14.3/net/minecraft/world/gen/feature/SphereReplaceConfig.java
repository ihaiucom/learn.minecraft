package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class SphereReplaceConfig implements IFeatureConfig {
   public final BlockState state;
   public final int radius;
   public final int ySize;
   public final List<BlockState> targets;

   public SphereReplaceConfig(BlockState p_i49886_1_, int p_i49886_2_, int p_i49886_3_, List<BlockState> p_i49886_4_) {
      this.state = p_i49886_1_;
      this.radius = p_i49886_2_;
      this.ySize = p_i49886_3_;
      this.targets = p_i49886_4_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("state"), BlockState.serialize(p_214634_1_, this.state).getValue(), p_214634_1_.createString("radius"), p_214634_1_.createInt(this.radius), p_214634_1_.createString("y_size"), p_214634_1_.createInt(this.ySize), p_214634_1_.createString("targets"), p_214634_1_.createList(this.targets.stream().map((p_214692_1_) -> {
         return BlockState.serialize(p_214634_1_, p_214692_1_).getValue();
      })))));
   }

   public static <T> SphereReplaceConfig deserialize(Dynamic<T> p_214691_0_) {
      BlockState lvt_1_1_ = (BlockState)p_214691_0_.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      int lvt_2_1_ = p_214691_0_.get("radius").asInt(0);
      int lvt_3_1_ = p_214691_0_.get("y_size").asInt(0);
      List<BlockState> lvt_4_1_ = p_214691_0_.get("targets").asList(BlockState::deserialize);
      return new SphereReplaceConfig(lvt_1_1_, lvt_2_1_, lvt_3_1_, lvt_4_1_);
   }
}
