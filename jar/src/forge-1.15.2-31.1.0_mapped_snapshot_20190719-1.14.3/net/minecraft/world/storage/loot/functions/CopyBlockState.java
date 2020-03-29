package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class CopyBlockState extends LootFunction {
   private final Block field_227543_a_;
   private final Set<IProperty<?>> field_227544_c_;

   private CopyBlockState(ILootCondition[] p_i225890_1_, Block p_i225890_2_, Set<IProperty<?>> p_i225890_3_) {
      super(p_i225890_1_);
      this.field_227543_a_ = p_i225890_2_;
      this.field_227544_c_ = p_i225890_3_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.BLOCK_STATE);
   }

   protected ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      BlockState lvt_3_1_ = (BlockState)p_215859_2_.get(LootParameters.BLOCK_STATE);
      if (lvt_3_1_ != null) {
         CompoundNBT lvt_4_1_ = p_215859_1_.getOrCreateTag();
         CompoundNBT lvt_5_2_;
         if (lvt_4_1_.contains("BlockStateTag", 10)) {
            lvt_5_2_ = lvt_4_1_.getCompound("BlockStateTag");
         } else {
            lvt_5_2_ = new CompoundNBT();
            lvt_4_1_.put("BlockStateTag", lvt_5_2_);
         }

         Stream var10000 = this.field_227544_c_.stream();
         lvt_3_1_.getClass();
         var10000.filter(lvt_3_1_::has).forEach((p_227548_2_) -> {
            lvt_5_2_.putString(p_227548_2_.getName(), func_227546_a_(lvt_3_1_, p_227548_2_));
         });
      }

      return p_215859_1_;
   }

   public static CopyBlockState.Builder func_227545_a_(Block p_227545_0_) {
      return new CopyBlockState.Builder(p_227545_0_);
   }

   private static <T extends Comparable<T>> String func_227546_a_(BlockState p_227546_0_, IProperty<T> p_227546_1_) {
      T lvt_2_1_ = p_227546_0_.get(p_227546_1_);
      return p_227546_1_.getName(lvt_2_1_);
   }

   // $FF: synthetic method
   CopyBlockState(ILootCondition[] p_i225891_1_, Block p_i225891_2_, Set p_i225891_3_, Object p_i225891_4_) {
      this(p_i225891_1_, p_i225891_2_, p_i225891_3_);
   }

   public static class Serializer extends LootFunction.Serializer<CopyBlockState> {
      public Serializer() {
         super(new ResourceLocation("copy_state"), CopyBlockState.class);
      }

      public void serialize(JsonObject p_186532_1_, CopyBlockState p_186532_2_, JsonSerializationContext p_186532_3_) {
         super.serialize(p_186532_1_, (LootFunction)p_186532_2_, p_186532_3_);
         p_186532_1_.addProperty("block", Registry.BLOCK.getKey(p_186532_2_.field_227543_a_).toString());
         JsonArray lvt_4_1_ = new JsonArray();
         p_186532_2_.field_227544_c_.forEach((p_227553_1_) -> {
            lvt_4_1_.add(p_227553_1_.getName());
         });
         p_186532_1_.add("properties", lvt_4_1_);
      }

      public CopyBlockState deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         ResourceLocation lvt_4_1_ = new ResourceLocation(JSONUtils.getString(p_186530_1_, "block"));
         Block lvt_5_1_ = (Block)Registry.BLOCK.getValue(lvt_4_1_).orElseThrow(() -> {
            return new IllegalArgumentException("Can't find block " + lvt_4_1_);
         });
         StateContainer<Block, BlockState> lvt_6_1_ = lvt_5_1_.getStateContainer();
         Set<IProperty<?>> lvt_7_1_ = Sets.newHashSet();
         JsonArray lvt_8_1_ = JSONUtils.getJsonArray(p_186530_1_, "properties", (JsonArray)null);
         if (lvt_8_1_ != null) {
            lvt_8_1_.forEach((p_227554_2_) -> {
               lvt_7_1_.add(lvt_6_1_.getProperty(JSONUtils.getString(p_227554_2_, "property")));
            });
         }

         return new CopyBlockState(p_186530_3_, lvt_5_1_, lvt_7_1_);
      }

      // $FF: synthetic method
      public LootFunction deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return this.deserialize(p_186530_1_, p_186530_2_, p_186530_3_);
      }
   }

   public static class Builder extends LootFunction.Builder<CopyBlockState.Builder> {
      private final Block field_227550_a_;
      private final Set<IProperty<?>> field_227551_b_;

      private Builder(Block p_i225892_1_) {
         this.field_227551_b_ = Sets.newHashSet();
         this.field_227550_a_ = p_i225892_1_;
      }

      public CopyBlockState.Builder func_227552_a_(IProperty<?> p_227552_1_) {
         if (!this.field_227550_a_.getStateContainer().getProperties().contains(p_227552_1_)) {
            throw new IllegalStateException("Property " + p_227552_1_ + " is not present on block " + this.field_227550_a_);
         } else {
            this.field_227551_b_.add(p_227552_1_);
            return this;
         }
      }

      protected CopyBlockState.Builder doCast() {
         return this;
      }

      public ILootFunction build() {
         return new CopyBlockState(this.getConditions(), this.field_227550_a_, this.field_227551_b_);
      }

      // $FF: synthetic method
      protected LootFunction.Builder doCast() {
         return this.doCast();
      }

      // $FF: synthetic method
      Builder(Block p_i225893_1_, Object p_i225893_2_) {
         this(p_i225893_1_);
      }
   }
}
