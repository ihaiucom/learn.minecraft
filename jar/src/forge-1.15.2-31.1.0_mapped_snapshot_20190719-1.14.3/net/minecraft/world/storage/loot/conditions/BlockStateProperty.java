package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Set;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;

public class BlockStateProperty implements ILootCondition {
   private final Block block;
   private final StatePropertiesPredicate properties;

   private BlockStateProperty(Block p_i225896_1_, StatePropertiesPredicate p_i225896_2_) {
      this.block = p_i225896_1_;
      this.properties = p_i225896_2_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.BLOCK_STATE);
   }

   public boolean test(LootContext p_test_1_) {
      BlockState lvt_2_1_ = (BlockState)p_test_1_.get(LootParameters.BLOCK_STATE);
      return lvt_2_1_ != null && this.block == lvt_2_1_.getBlock() && this.properties.func_227181_a_(lvt_2_1_);
   }

   public static BlockStateProperty.Builder builder(Block p_215985_0_) {
      return new BlockStateProperty.Builder(p_215985_0_);
   }

   // $FF: synthetic method
   public boolean test(Object p_test_1_) {
      return this.test((LootContext)p_test_1_);
   }

   // $FF: synthetic method
   BlockStateProperty(Block p_i225897_1_, StatePropertiesPredicate p_i225897_2_, Object p_i225897_3_) {
      this(p_i225897_1_, p_i225897_2_);
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<BlockStateProperty> {
      protected Serializer() {
         super(new ResourceLocation("block_state_property"), BlockStateProperty.class);
      }

      public void serialize(JsonObject p_186605_1_, BlockStateProperty p_186605_2_, JsonSerializationContext p_186605_3_) {
         p_186605_1_.addProperty("block", Registry.BLOCK.getKey(p_186605_2_.block).toString());
         p_186605_1_.add("properties", p_186605_2_.properties.func_227180_a_());
      }

      public BlockStateProperty deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         ResourceLocation lvt_3_1_ = new ResourceLocation(JSONUtils.getString(p_186603_1_, "block"));
         Block lvt_4_1_ = (Block)Registry.BLOCK.getValue(lvt_3_1_).orElseThrow(() -> {
            return new IllegalArgumentException("Can't find block " + lvt_3_1_);
         });
         StatePropertiesPredicate lvt_5_1_ = StatePropertiesPredicate.func_227186_a_(p_186603_1_.get("properties"));
         lvt_5_1_.func_227183_a_(lvt_4_1_.getStateContainer(), (p_227568_1_) -> {
            throw new JsonSyntaxException("Block " + lvt_4_1_ + " has no property " + p_227568_1_);
         });
         return new BlockStateProperty(lvt_4_1_, lvt_5_1_);
      }

      // $FF: synthetic method
      public ILootCondition deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return this.deserialize(p_186603_1_, p_186603_2_);
      }
   }

   public static class Builder implements ILootCondition.IBuilder {
      private final Block block;
      private StatePropertiesPredicate desiredProperties;

      public Builder(Block p_i50576_1_) {
         this.desiredProperties = StatePropertiesPredicate.field_227178_a_;
         this.block = p_i50576_1_;
      }

      public BlockStateProperty.Builder func_227567_a_(StatePropertiesPredicate.Builder p_227567_1_) {
         this.desiredProperties = p_227567_1_.func_227196_b_();
         return this;
      }

      public ILootCondition build() {
         return new BlockStateProperty(this.block, this.desiredProperties);
      }
   }
}
