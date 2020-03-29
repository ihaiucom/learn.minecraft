package net.minecraft.advancements.criterion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class EnchantmentPredicate {
   public static final EnchantmentPredicate ANY = new EnchantmentPredicate();
   public static final EnchantmentPredicate[] field_226534_b_ = new EnchantmentPredicate[0];
   private final Enchantment enchantment;
   private final MinMaxBounds.IntBound levels;

   public EnchantmentPredicate() {
      this.enchantment = null;
      this.levels = MinMaxBounds.IntBound.UNBOUNDED;
   }

   public EnchantmentPredicate(@Nullable Enchantment p_i49723_1_, MinMaxBounds.IntBound p_i49723_2_) {
      this.enchantment = p_i49723_1_;
      this.levels = p_i49723_2_;
   }

   public boolean test(Map<Enchantment, Integer> p_192463_1_) {
      if (this.enchantment != null) {
         if (!p_192463_1_.containsKey(this.enchantment)) {
            return false;
         }

         int lvt_2_1_ = (Integer)p_192463_1_.get(this.enchantment);
         if (this.levels != null && !this.levels.test(lvt_2_1_)) {
            return false;
         }
      } else if (this.levels != null) {
         Iterator var4 = p_192463_1_.values().iterator();

         Integer lvt_3_1_;
         do {
            if (!var4.hasNext()) {
               return false;
            }

            lvt_3_1_ = (Integer)var4.next();
         } while(!this.levels.test(lvt_3_1_));

         return true;
      }

      return true;
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject lvt_1_1_ = new JsonObject();
         if (this.enchantment != null) {
            lvt_1_1_.addProperty("enchantment", Registry.ENCHANTMENT.getKey(this.enchantment).toString());
         }

         lvt_1_1_.add("levels", this.levels.serialize());
         return lvt_1_1_;
      }
   }

   public static EnchantmentPredicate deserialize(@Nullable JsonElement p_192464_0_) {
      if (p_192464_0_ != null && !p_192464_0_.isJsonNull()) {
         JsonObject lvt_1_1_ = JSONUtils.getJsonObject(p_192464_0_, "enchantment");
         Enchantment lvt_2_1_ = null;
         if (lvt_1_1_.has("enchantment")) {
            ResourceLocation lvt_3_1_ = new ResourceLocation(JSONUtils.getString(lvt_1_1_, "enchantment"));
            lvt_2_1_ = (Enchantment)Registry.ENCHANTMENT.getValue(lvt_3_1_).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown enchantment '" + lvt_3_1_ + "'");
            });
         }

         MinMaxBounds.IntBound lvt_3_2_ = MinMaxBounds.IntBound.fromJson(lvt_1_1_.get("levels"));
         return new EnchantmentPredicate(lvt_2_1_, lvt_3_2_);
      } else {
         return ANY;
      }
   }

   public static EnchantmentPredicate[] deserializeArray(@Nullable JsonElement p_192465_0_) {
      if (p_192465_0_ != null && !p_192465_0_.isJsonNull()) {
         JsonArray lvt_1_1_ = JSONUtils.getJsonArray(p_192465_0_, "enchantments");
         EnchantmentPredicate[] lvt_2_1_ = new EnchantmentPredicate[lvt_1_1_.size()];

         for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.length; ++lvt_3_1_) {
            lvt_2_1_[lvt_3_1_] = deserialize(lvt_1_1_.get(lvt_3_1_));
         }

         return lvt_2_1_;
      } else {
         return field_226534_b_;
      }
   }
}
