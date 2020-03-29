package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class UsedTotemTrigger extends AbstractCriterionTrigger<UsedTotemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("used_totem");

   public ResourceLocation getId() {
      return ID;
   }

   public UsedTotemTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ItemPredicate lvt_3_1_ = ItemPredicate.deserialize(p_192166_1_.get("item"));
      return new UsedTotemTrigger.Instance(lvt_3_1_);
   }

   public void trigger(ServerPlayerEntity p_193187_1_, ItemStack p_193187_2_) {
      this.func_227070_a_(p_193187_1_.getAdvancements(), (p_227409_1_) -> {
         return p_227409_1_.test(p_193187_2_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;

      public Instance(ItemPredicate p_i47564_1_) {
         super(UsedTotemTrigger.ID);
         this.item = p_i47564_1_;
      }

      public static UsedTotemTrigger.Instance usedTotem(IItemProvider p_203941_0_) {
         return new UsedTotemTrigger.Instance(ItemPredicate.Builder.create().item(p_203941_0_).build());
      }

      public boolean test(ItemStack p_193218_1_) {
         return this.item.test(p_193218_1_);
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("item", this.item.serialize());
         return lvt_1_1_;
      }
   }
}
