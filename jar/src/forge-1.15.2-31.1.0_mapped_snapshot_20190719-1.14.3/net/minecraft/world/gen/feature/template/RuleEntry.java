package net.minecraft.world.gen.feature.template;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicDeserializer;
import net.minecraft.util.registry.Registry;

public class RuleEntry {
   private final RuleTest inputPredicate;
   private final RuleTest locationPredicate;
   private final BlockState outputState;
   @Nullable
   private final CompoundNBT outputNbt;

   public RuleEntry(RuleTest p_i51326_1_, RuleTest p_i51326_2_, BlockState p_i51326_3_) {
      this(p_i51326_1_, p_i51326_2_, p_i51326_3_, (CompoundNBT)null);
   }

   public RuleEntry(RuleTest p_i51327_1_, RuleTest p_i51327_2_, BlockState p_i51327_3_, @Nullable CompoundNBT p_i51327_4_) {
      this.inputPredicate = p_i51327_1_;
      this.locationPredicate = p_i51327_2_;
      this.outputState = p_i51327_3_;
      this.outputNbt = p_i51327_4_;
   }

   public boolean test(BlockState p_215211_1_, BlockState p_215211_2_, Random p_215211_3_) {
      return this.inputPredicate.test(p_215211_1_, p_215211_3_) && this.locationPredicate.test(p_215211_2_, p_215211_3_);
   }

   public BlockState getOutputState() {
      return this.outputState;
   }

   @Nullable
   public CompoundNBT getOutputNbt() {
      return this.outputNbt;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_215212_1_) {
      T lvt_2_1_ = p_215212_1_.createMap(ImmutableMap.of(p_215212_1_.createString("input_predicate"), this.inputPredicate.serialize(p_215212_1_).getValue(), p_215212_1_.createString("location_predicate"), this.locationPredicate.serialize(p_215212_1_).getValue(), p_215212_1_.createString("output_state"), BlockState.serialize(p_215212_1_, this.outputState).getValue()));
      return this.outputNbt == null ? new Dynamic(p_215212_1_, lvt_2_1_) : new Dynamic(p_215212_1_, p_215212_1_.mergeInto(lvt_2_1_, p_215212_1_.createString("output_nbt"), (new Dynamic(NBTDynamicOps.INSTANCE, this.outputNbt)).convert(p_215212_1_).getValue()));
   }

   public static <T> RuleEntry deserialize(Dynamic<T> p_215213_0_) {
      Dynamic<T> lvt_1_1_ = p_215213_0_.get("input_predicate").orElseEmptyMap();
      Dynamic<T> lvt_2_1_ = p_215213_0_.get("location_predicate").orElseEmptyMap();
      RuleTest lvt_3_1_ = (RuleTest)IDynamicDeserializer.func_214907_a(lvt_1_1_, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
      RuleTest lvt_4_1_ = (RuleTest)IDynamicDeserializer.func_214907_a(lvt_2_1_, Registry.RULE_TEST, "predicate_type", AlwaysTrueRuleTest.INSTANCE);
      BlockState lvt_5_1_ = BlockState.deserialize(p_215213_0_.get("output_state").orElseEmptyMap());
      CompoundNBT lvt_6_1_ = (CompoundNBT)p_215213_0_.get("output_nbt").map((p_215210_0_) -> {
         return (INBT)p_215210_0_.convert(NBTDynamicOps.INSTANCE).getValue();
      }).orElse((Object)null);
      return new RuleEntry(lvt_3_1_, lvt_4_1_, lvt_5_1_, lvt_6_1_);
   }
}
