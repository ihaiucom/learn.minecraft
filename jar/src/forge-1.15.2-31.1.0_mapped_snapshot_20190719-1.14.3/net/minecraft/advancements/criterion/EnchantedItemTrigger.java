package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantedItemTrigger extends AbstractCriterionTrigger<EnchantedItemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("enchanted_item");

   public ResourceLocation getId() {
      return ID;
   }

   public EnchantedItemTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ItemPredicate lvt_3_1_ = ItemPredicate.deserialize(p_192166_1_.get("item"));
      MinMaxBounds.IntBound lvt_4_1_ = MinMaxBounds.IntBound.fromJson(p_192166_1_.get("levels"));
      return new EnchantedItemTrigger.Instance(lvt_3_1_, lvt_4_1_);
   }

   public void trigger(ServerPlayerEntity p_192190_1_, ItemStack p_192190_2_, int p_192190_3_) {
      this.func_227070_a_(p_192190_1_.getAdvancements(), (p_226528_2_) -> {
         return p_226528_2_.test(p_192190_2_, p_192190_3_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.IntBound levels;

      public Instance(ItemPredicate p_i49731_1_, MinMaxBounds.IntBound p_i49731_2_) {
         super(EnchantedItemTrigger.ID);
         this.item = p_i49731_1_;
         this.levels = p_i49731_2_;
      }

      public static EnchantedItemTrigger.Instance any() {
         return new EnchantedItemTrigger.Instance(ItemPredicate.ANY, MinMaxBounds.IntBound.UNBOUNDED);
      }

      public boolean test(ItemStack p_192257_1_, int p_192257_2_) {
         if (!this.item.test(p_192257_1_)) {
            return false;
         } else {
            return this.levels.test(p_192257_2_);
         }
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("item", this.item.serialize());
         lvt_1_1_.add("levels", this.levels.serialize());
         return lvt_1_1_;
      }
   }
}
