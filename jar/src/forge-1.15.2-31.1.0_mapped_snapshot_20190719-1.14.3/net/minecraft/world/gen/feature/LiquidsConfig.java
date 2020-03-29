package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

public class LiquidsConfig implements IFeatureConfig {
   public final IFluidState state;
   public final boolean field_227363_b_;
   public final int field_227364_c_;
   public final int field_227365_d_;
   public final Set<Block> field_227366_f_;

   public LiquidsConfig(IFluidState p_i225841_1_, boolean p_i225841_2_, int p_i225841_3_, int p_i225841_4_, Set<Block> p_i225841_5_) {
      this.state = p_i225841_1_;
      this.field_227363_b_ = p_i225841_2_;
      this.field_227364_c_ = p_i225841_3_;
      this.field_227365_d_ = p_i225841_4_;
      this.field_227366_f_ = p_i225841_5_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      Object var10004 = p_214634_1_.createString("state");
      Object var10005 = IFluidState.serialize(p_214634_1_, this.state).getValue();
      Object var10006 = p_214634_1_.createString("requires_block_below");
      Object var10007 = p_214634_1_.createBoolean(this.field_227363_b_);
      Object var10008 = p_214634_1_.createString("rock_count");
      Object var10009 = p_214634_1_.createInt(this.field_227364_c_);
      Object var10010 = p_214634_1_.createString("hole_count");
      Object var10011 = p_214634_1_.createInt(this.field_227365_d_);
      Object var10012 = p_214634_1_.createString("valid_blocks");
      Stream var10014 = this.field_227366_f_.stream();
      DefaultedRegistry var10015 = Registry.BLOCK;
      var10015.getClass();
      var10014 = var10014.map(var10015::getKey).map(ResourceLocation::toString);
      p_214634_1_.getClass();
      return new Dynamic(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(var10004, var10005, var10006, var10007, var10008, var10009, var10010, var10011, var10012, p_214634_1_.createList(var10014.map(p_214634_1_::createString)))));
   }

   public static <T> LiquidsConfig deserialize(Dynamic<T> p_214677_0_) {
      return new LiquidsConfig((IFluidState)p_214677_0_.get("state").map(IFluidState::deserialize).orElse(Fluids.EMPTY.getDefaultState()), p_214677_0_.get("requires_block_below").asBoolean(true), p_214677_0_.get("rock_count").asInt(4), p_214677_0_.get("hole_count").asInt(1), ImmutableSet.copyOf(p_214677_0_.get("valid_blocks").asList((p_227367_0_) -> {
         return (Block)Registry.BLOCK.getOrDefault(new ResourceLocation(p_227367_0_.asString("minecraft:air")));
      })));
   }
}
