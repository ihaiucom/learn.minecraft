package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class EnterBlockTrigger extends AbstractCriterionTrigger<EnterBlockTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("enter_block");

   public ResourceLocation getId() {
      return ID;
   }

   public EnterBlockTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      Block lvt_3_1_ = func_226550_a_(p_192166_1_);
      StatePropertiesPredicate lvt_4_1_ = StatePropertiesPredicate.func_227186_a_(p_192166_1_.get("state"));
      if (lvt_3_1_ != null) {
         lvt_4_1_.func_227183_a_(lvt_3_1_.getStateContainer(), (p_226548_1_) -> {
            throw new JsonSyntaxException("Block " + lvt_3_1_ + " has no property " + p_226548_1_);
         });
      }

      return new EnterBlockTrigger.Instance(lvt_3_1_, lvt_4_1_);
   }

   @Nullable
   private static Block func_226550_a_(JsonObject p_226550_0_) {
      if (p_226550_0_.has("block")) {
         ResourceLocation lvt_1_1_ = new ResourceLocation(JSONUtils.getString(p_226550_0_, "block"));
         return (Block)Registry.BLOCK.getValue(lvt_1_1_).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + lvt_1_1_ + "'");
         });
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayerEntity p_192193_1_, BlockState p_192193_2_) {
      this.func_227070_a_(p_192193_1_.getAdvancements(), (p_226549_1_) -> {
         return p_226549_1_.test(p_192193_2_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final Block block;
      private final StatePropertiesPredicate properties;

      public Instance(@Nullable Block p_i225733_1_, StatePropertiesPredicate p_i225733_2_) {
         super(EnterBlockTrigger.ID);
         this.block = p_i225733_1_;
         this.properties = p_i225733_2_;
      }

      public static EnterBlockTrigger.Instance forBlock(Block p_203920_0_) {
         return new EnterBlockTrigger.Instance(p_203920_0_, StatePropertiesPredicate.field_227178_a_);
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         if (this.block != null) {
            lvt_1_1_.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         lvt_1_1_.add("state", this.properties.func_227180_a_());
         return lvt_1_1_;
      }

      public boolean test(BlockState p_192260_1_) {
         if (this.block != null && p_192260_1_.getBlock() != this.block) {
            return false;
         } else {
            return this.properties.func_227181_a_(p_192260_1_);
         }
      }
   }
}
