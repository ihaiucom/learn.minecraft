package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class BlockWithContextConfig implements IFeatureConfig {
   public final BlockState toPlace;
   public final List<BlockState> placeOn;
   public final List<BlockState> placeIn;
   public final List<BlockState> placeUnder;

   public BlockWithContextConfig(BlockState p_i51439_1_, List<BlockState> p_i51439_2_, List<BlockState> p_i51439_3_, List<BlockState> p_i51439_4_) {
      this.toPlace = p_i51439_1_;
      this.placeOn = p_i51439_2_;
      this.placeIn = p_i51439_3_;
      this.placeUnder = p_i51439_4_;
   }

   public BlockWithContextConfig(BlockState p_i49003_1_, BlockState[] p_i49003_2_, BlockState[] p_i49003_3_, BlockState[] p_i49003_4_) {
      this(p_i49003_1_, (List)Lists.newArrayList(p_i49003_2_), (List)Lists.newArrayList(p_i49003_3_), (List)Lists.newArrayList(p_i49003_4_));
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      T lvt_2_1_ = BlockState.serialize(p_214634_1_, this.toPlace).getValue();
      T lvt_3_1_ = p_214634_1_.createList(this.placeOn.stream().map((p_214662_1_) -> {
         return BlockState.serialize(p_214634_1_, p_214662_1_).getValue();
      }));
      T lvt_4_1_ = p_214634_1_.createList(this.placeIn.stream().map((p_214661_1_) -> {
         return BlockState.serialize(p_214634_1_, p_214661_1_).getValue();
      }));
      T lvt_5_1_ = p_214634_1_.createList(this.placeUnder.stream().map((p_214660_1_) -> {
         return BlockState.serialize(p_214634_1_, p_214660_1_).getValue();
      }));
      return new Dynamic(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("to_place"), lvt_2_1_, p_214634_1_.createString("place_on"), lvt_3_1_, p_214634_1_.createString("place_in"), lvt_4_1_, p_214634_1_.createString("place_under"), lvt_5_1_)));
   }

   public static <T> BlockWithContextConfig deserialize(Dynamic<T> p_214663_0_) {
      BlockState lvt_1_1_ = (BlockState)p_214663_0_.get("to_place").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      List<BlockState> lvt_2_1_ = p_214663_0_.get("place_on").asList(BlockState::deserialize);
      List<BlockState> lvt_3_1_ = p_214663_0_.get("place_in").asList(BlockState::deserialize);
      List<BlockState> lvt_4_1_ = p_214663_0_.get("place_under").asList(BlockState::deserialize);
      return new BlockWithContextConfig(lvt_1_1_, lvt_2_1_, lvt_3_1_, lvt_4_1_);
   }
}
