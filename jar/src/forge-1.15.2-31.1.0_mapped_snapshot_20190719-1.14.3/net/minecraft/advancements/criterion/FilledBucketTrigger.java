package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FilledBucketTrigger extends AbstractCriterionTrigger<FilledBucketTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("filled_bucket");

   public ResourceLocation getId() {
      return ID;
   }

   public FilledBucketTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ItemPredicate lvt_3_1_ = ItemPredicate.deserialize(p_192166_1_.get("item"));
      return new FilledBucketTrigger.Instance(lvt_3_1_);
   }

   public void trigger(ServerPlayerEntity p_204817_1_, ItemStack p_204817_2_) {
      this.func_227070_a_(p_204817_1_.getAdvancements(), (p_226627_1_) -> {
         return p_226627_1_.test(p_204817_2_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;

      public Instance(ItemPredicate p_i48918_1_) {
         super(FilledBucketTrigger.ID);
         this.item = p_i48918_1_;
      }

      public static FilledBucketTrigger.Instance forItem(ItemPredicate p_204827_0_) {
         return new FilledBucketTrigger.Instance(p_204827_0_);
      }

      public boolean test(ItemStack p_204826_1_) {
         return this.item.test(p_204826_1_);
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("item", this.item.serialize());
         return lvt_1_1_;
      }
   }
}
