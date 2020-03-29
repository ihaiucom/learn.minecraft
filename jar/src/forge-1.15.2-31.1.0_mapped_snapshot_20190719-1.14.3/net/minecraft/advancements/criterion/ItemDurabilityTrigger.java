package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemDurabilityTrigger extends AbstractCriterionTrigger<ItemDurabilityTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("item_durability_changed");

   public ResourceLocation getId() {
      return ID;
   }

   public ItemDurabilityTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ItemPredicate lvt_3_1_ = ItemPredicate.deserialize(p_192166_1_.get("item"));
      MinMaxBounds.IntBound lvt_4_1_ = MinMaxBounds.IntBound.fromJson(p_192166_1_.get("durability"));
      MinMaxBounds.IntBound lvt_5_1_ = MinMaxBounds.IntBound.fromJson(p_192166_1_.get("delta"));
      return new ItemDurabilityTrigger.Instance(lvt_3_1_, lvt_4_1_, lvt_5_1_);
   }

   public void trigger(ServerPlayerEntity p_193158_1_, ItemStack p_193158_2_, int p_193158_3_) {
      this.func_227070_a_(p_193158_1_.getAdvancements(), (p_226653_2_) -> {
         return p_226653_2_.test(p_193158_2_, p_193158_3_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.IntBound durability;
      private final MinMaxBounds.IntBound delta;

      public Instance(ItemPredicate p_i49703_1_, MinMaxBounds.IntBound p_i49703_2_, MinMaxBounds.IntBound p_i49703_3_) {
         super(ItemDurabilityTrigger.ID);
         this.item = p_i49703_1_;
         this.durability = p_i49703_2_;
         this.delta = p_i49703_3_;
      }

      public static ItemDurabilityTrigger.Instance forItemDamage(ItemPredicate p_211182_0_, MinMaxBounds.IntBound p_211182_1_) {
         return new ItemDurabilityTrigger.Instance(p_211182_0_, p_211182_1_, MinMaxBounds.IntBound.UNBOUNDED);
      }

      public boolean test(ItemStack p_193197_1_, int p_193197_2_) {
         if (!this.item.test(p_193197_1_)) {
            return false;
         } else if (!this.durability.test(p_193197_1_.getMaxDamage() - p_193197_2_)) {
            return false;
         } else {
            return this.delta.test(p_193197_1_.getDamage() - p_193197_2_);
         }
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("item", this.item.serialize());
         lvt_1_1_.add("durability", this.durability.serialize());
         lvt_1_1_.add("delta", this.delta.serialize());
         return lvt_1_1_;
      }
   }
}
