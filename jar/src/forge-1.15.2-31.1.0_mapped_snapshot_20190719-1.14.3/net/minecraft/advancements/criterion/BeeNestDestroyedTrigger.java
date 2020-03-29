package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class BeeNestDestroyedTrigger extends AbstractCriterionTrigger<BeeNestDestroyedTrigger.Instance> {
   private static final ResourceLocation field_226219_a_ = new ResourceLocation("bee_nest_destroyed");

   public ResourceLocation getId() {
      return field_226219_a_;
   }

   public BeeNestDestroyedTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      Block lvt_3_1_ = func_226221_a_(p_192166_1_);
      ItemPredicate lvt_4_1_ = ItemPredicate.deserialize(p_192166_1_.get("item"));
      MinMaxBounds.IntBound lvt_5_1_ = MinMaxBounds.IntBound.fromJson(p_192166_1_.get("num_bees_inside"));
      return new BeeNestDestroyedTrigger.Instance(lvt_3_1_, lvt_4_1_, lvt_5_1_);
   }

   @Nullable
   private static Block func_226221_a_(JsonObject p_226221_0_) {
      if (p_226221_0_.has("block")) {
         ResourceLocation lvt_1_1_ = new ResourceLocation(JSONUtils.getString(p_226221_0_, "block"));
         return (Block)Registry.BLOCK.getValue(lvt_1_1_).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + lvt_1_1_ + "'");
         });
      } else {
         return null;
      }
   }

   public void func_226223_a_(ServerPlayerEntity p_226223_1_, Block p_226223_2_, ItemStack p_226223_3_, int p_226223_4_) {
      this.func_227070_a_(p_226223_1_.getAdvancements(), (p_226220_3_) -> {
         return p_226220_3_.func_226228_a_(p_226223_2_, p_226223_3_, p_226223_4_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final Block field_226225_a_;
      private final ItemPredicate field_226226_b_;
      private final MinMaxBounds.IntBound field_226227_c_;

      public Instance(Block p_i225706_1_, ItemPredicate p_i225706_2_, MinMaxBounds.IntBound p_i225706_3_) {
         super(BeeNestDestroyedTrigger.field_226219_a_);
         this.field_226225_a_ = p_i225706_1_;
         this.field_226226_b_ = p_i225706_2_;
         this.field_226227_c_ = p_i225706_3_;
      }

      public static BeeNestDestroyedTrigger.Instance func_226229_a_(Block p_226229_0_, ItemPredicate.Builder p_226229_1_, MinMaxBounds.IntBound p_226229_2_) {
         return new BeeNestDestroyedTrigger.Instance(p_226229_0_, p_226229_1_.build(), p_226229_2_);
      }

      public boolean func_226228_a_(Block p_226228_1_, ItemStack p_226228_2_, int p_226228_3_) {
         if (this.field_226225_a_ != null && p_226228_1_ != this.field_226225_a_) {
            return false;
         } else {
            return !this.field_226226_b_.test(p_226228_2_) ? false : this.field_226227_c_.test(p_226228_3_);
         }
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         if (this.field_226225_a_ != null) {
            lvt_1_1_.addProperty("block", Registry.BLOCK.getKey(this.field_226225_a_).toString());
         }

         lvt_1_1_.add("item", this.field_226226_b_.serialize());
         lvt_1_1_.add("num_bees_inside", this.field_226227_c_.serialize());
         return lvt_1_1_;
      }
   }
}
