package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class ConsumeItemTrigger extends AbstractCriterionTrigger<ConsumeItemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("consume_item");

   public ResourceLocation getId() {
      return ID;
   }

   public ConsumeItemTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return new ConsumeItemTrigger.Instance(ItemPredicate.deserialize(p_192166_1_.get("item")));
   }

   public void trigger(ServerPlayerEntity p_193148_1_, ItemStack p_193148_2_) {
      this.func_227070_a_(p_193148_1_.getAdvancements(), (p_226325_1_) -> {
         return p_226325_1_.test(p_193148_2_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;

      public Instance(ItemPredicate p_i47562_1_) {
         super(ConsumeItemTrigger.ID);
         this.item = p_i47562_1_;
      }

      public static ConsumeItemTrigger.Instance any() {
         return new ConsumeItemTrigger.Instance(ItemPredicate.ANY);
      }

      public static ConsumeItemTrigger.Instance forItem(IItemProvider p_203913_0_) {
         return new ConsumeItemTrigger.Instance(new ItemPredicate((Tag)null, p_203913_0_.asItem(), MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, EnchantmentPredicate.field_226534_b_, EnchantmentPredicate.field_226534_b_, (Potion)null, NBTPredicate.ANY));
      }

      public boolean test(ItemStack p_193193_1_) {
         return this.item.test(p_193193_1_);
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("item", this.item.serialize());
         return lvt_1_1_;
      }
   }
}
