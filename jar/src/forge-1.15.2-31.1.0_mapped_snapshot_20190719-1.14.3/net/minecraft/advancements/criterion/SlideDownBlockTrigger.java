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

public class SlideDownBlockTrigger extends AbstractCriterionTrigger<SlideDownBlockTrigger.Instance> {
   private static final ResourceLocation field_227147_a_ = new ResourceLocation("slide_down_block");

   public ResourceLocation getId() {
      return field_227147_a_;
   }

   public SlideDownBlockTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      Block lvt_3_1_ = func_227150_a_(p_192166_1_);
      StatePropertiesPredicate lvt_4_1_ = StatePropertiesPredicate.func_227186_a_(p_192166_1_.get("state"));
      if (lvt_3_1_ != null) {
         lvt_4_1_.func_227183_a_(lvt_3_1_.getStateContainer(), (p_227148_1_) -> {
            throw new JsonSyntaxException("Block " + lvt_3_1_ + " has no property " + p_227148_1_);
         });
      }

      return new SlideDownBlockTrigger.Instance(lvt_3_1_, lvt_4_1_);
   }

   @Nullable
   private static Block func_227150_a_(JsonObject p_227150_0_) {
      if (p_227150_0_.has("block")) {
         ResourceLocation lvt_1_1_ = new ResourceLocation(JSONUtils.getString(p_227150_0_, "block"));
         return (Block)Registry.BLOCK.getValue(lvt_1_1_).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + lvt_1_1_ + "'");
         });
      } else {
         return null;
      }
   }

   public void func_227152_a_(ServerPlayerEntity p_227152_1_, BlockState p_227152_2_) {
      this.func_227070_a_(p_227152_1_.getAdvancements(), (p_227149_1_) -> {
         return p_227149_1_.func_227157_a_(p_227152_2_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final Block field_227154_a_;
      private final StatePropertiesPredicate field_227155_b_;

      public Instance(@Nullable Block p_i225786_1_, StatePropertiesPredicate p_i225786_2_) {
         super(SlideDownBlockTrigger.field_227147_a_);
         this.field_227154_a_ = p_i225786_1_;
         this.field_227155_b_ = p_i225786_2_;
      }

      public static SlideDownBlockTrigger.Instance func_227156_a_(Block p_227156_0_) {
         return new SlideDownBlockTrigger.Instance(p_227156_0_, StatePropertiesPredicate.field_227178_a_);
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         if (this.field_227154_a_ != null) {
            lvt_1_1_.addProperty("block", Registry.BLOCK.getKey(this.field_227154_a_).toString());
         }

         lvt_1_1_.add("state", this.field_227155_b_.func_227180_a_());
         return lvt_1_1_;
      }

      public boolean func_227157_a_(BlockState p_227157_1_) {
         if (this.field_227154_a_ != null && p_227157_1_.getBlock() != this.field_227154_a_) {
            return false;
         } else {
            return this.field_227155_b_.func_227181_a_(p_227157_1_);
         }
      }
   }
}
