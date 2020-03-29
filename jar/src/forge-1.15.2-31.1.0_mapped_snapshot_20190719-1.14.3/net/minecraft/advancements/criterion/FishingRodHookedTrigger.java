package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FishingRodHookedTrigger extends AbstractCriterionTrigger<FishingRodHookedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("fishing_rod_hooked");

   public ResourceLocation getId() {
      return ID;
   }

   public FishingRodHookedTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      ItemPredicate lvt_3_1_ = ItemPredicate.deserialize(p_192166_1_.get("rod"));
      EntityPredicate lvt_4_1_ = EntityPredicate.deserialize(p_192166_1_.get("entity"));
      ItemPredicate lvt_5_1_ = ItemPredicate.deserialize(p_192166_1_.get("item"));
      return new FishingRodHookedTrigger.Instance(lvt_3_1_, lvt_4_1_, lvt_5_1_);
   }

   public void trigger(ServerPlayerEntity p_204820_1_, ItemStack p_204820_2_, FishingBobberEntity p_204820_3_, Collection<ItemStack> p_204820_4_) {
      this.func_227070_a_(p_204820_1_.getAdvancements(), (p_226628_4_) -> {
         return p_226628_4_.test(p_204820_1_, p_204820_2_, p_204820_3_, p_204820_4_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate rod;
      private final EntityPredicate entity;
      private final ItemPredicate item;

      public Instance(ItemPredicate p_i48916_1_, EntityPredicate p_i48916_2_, ItemPredicate p_i48916_3_) {
         super(FishingRodHookedTrigger.ID);
         this.rod = p_i48916_1_;
         this.entity = p_i48916_2_;
         this.item = p_i48916_3_;
      }

      public static FishingRodHookedTrigger.Instance create(ItemPredicate p_204829_0_, EntityPredicate p_204829_1_, ItemPredicate p_204829_2_) {
         return new FishingRodHookedTrigger.Instance(p_204829_0_, p_204829_1_, p_204829_2_);
      }

      public boolean test(ServerPlayerEntity p_204830_1_, ItemStack p_204830_2_, FishingBobberEntity p_204830_3_, Collection<ItemStack> p_204830_4_) {
         if (!this.rod.test(p_204830_2_)) {
            return false;
         } else if (!this.entity.test(p_204830_1_, p_204830_3_.caughtEntity)) {
            return false;
         } else {
            if (this.item != ItemPredicate.ANY) {
               boolean lvt_5_1_ = false;
               if (p_204830_3_.caughtEntity instanceof ItemEntity) {
                  ItemEntity lvt_6_1_ = (ItemEntity)p_204830_3_.caughtEntity;
                  if (this.item.test(lvt_6_1_.getItem())) {
                     lvt_5_1_ = true;
                  }
               }

               Iterator var8 = p_204830_4_.iterator();

               while(var8.hasNext()) {
                  ItemStack lvt_7_1_ = (ItemStack)var8.next();
                  if (this.item.test(lvt_7_1_)) {
                     lvt_5_1_ = true;
                     break;
                  }
               }

               if (!lvt_5_1_) {
                  return false;
               }
            }

            return true;
         }
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("rod", this.rod.serialize());
         lvt_1_1_.add("entity", this.entity.serialize());
         lvt_1_1_.add("item", this.item.serialize());
         return lvt_1_1_;
      }
   }
}
