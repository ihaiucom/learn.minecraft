package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class ItemPredicate {
   private static final Map<ResourceLocation, Function<JsonObject, ItemPredicate>> custom_predicates = new HashMap();
   private static final Map<ResourceLocation, Function<JsonObject, ItemPredicate>> unmod_predicates;
   public static final ItemPredicate ANY;
   @Nullable
   private final Tag<Item> tag;
   @Nullable
   private final Item item;
   private final MinMaxBounds.IntBound count;
   private final MinMaxBounds.IntBound durability;
   private final EnchantmentPredicate[] enchantments;
   private final EnchantmentPredicate[] field_226656_g_;
   @Nullable
   private final Potion potion;
   private final NBTPredicate nbt;

   public ItemPredicate() {
      this.tag = null;
      this.item = null;
      this.potion = null;
      this.count = MinMaxBounds.IntBound.UNBOUNDED;
      this.durability = MinMaxBounds.IntBound.UNBOUNDED;
      this.enchantments = EnchantmentPredicate.field_226534_b_;
      this.field_226656_g_ = EnchantmentPredicate.field_226534_b_;
      this.nbt = NBTPredicate.ANY;
   }

   public ItemPredicate(@Nullable Tag<Item> p_i225740_1_, @Nullable Item p_i225740_2_, MinMaxBounds.IntBound p_i225740_3_, MinMaxBounds.IntBound p_i225740_4_, EnchantmentPredicate[] p_i225740_5_, EnchantmentPredicate[] p_i225740_6_, @Nullable Potion p_i225740_7_, NBTPredicate p_i225740_8_) {
      this.tag = p_i225740_1_;
      this.item = p_i225740_2_;
      this.count = p_i225740_3_;
      this.durability = p_i225740_4_;
      this.enchantments = p_i225740_5_;
      this.field_226656_g_ = p_i225740_6_;
      this.potion = p_i225740_7_;
      this.nbt = p_i225740_8_;
   }

   public boolean test(ItemStack p_192493_1_) {
      if (this == ANY) {
         return true;
      } else if (this.tag != null && !this.tag.contains(p_192493_1_.getItem())) {
         return false;
      } else if (this.item != null && p_192493_1_.getItem() != this.item) {
         return false;
      } else if (!this.count.test(p_192493_1_.getCount())) {
         return false;
      } else if (!this.durability.isUnbounded() && !p_192493_1_.isDamageable()) {
         return false;
      } else if (!this.durability.test(p_192493_1_.getMaxDamage() - p_192493_1_.getDamage())) {
         return false;
      } else if (!this.nbt.test(p_192493_1_)) {
         return false;
      } else {
         Map map1;
         EnchantmentPredicate[] var3;
         int var4;
         int var5;
         EnchantmentPredicate enchantmentpredicate1;
         if (this.enchantments.length > 0) {
            map1 = EnchantmentHelper.func_226652_a_(p_192493_1_.getEnchantmentTagList());
            var3 = this.enchantments;
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
               enchantmentpredicate1 = var3[var5];
               if (!enchantmentpredicate1.test(map1)) {
                  return false;
               }
            }
         }

         if (this.field_226656_g_.length > 0) {
            map1 = EnchantmentHelper.func_226652_a_(EnchantedBookItem.getEnchantments(p_192493_1_));
            var3 = this.field_226656_g_;
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
               enchantmentpredicate1 = var3[var5];
               if (!enchantmentpredicate1.test(map1)) {
                  return false;
               }
            }
         }

         Potion potion = PotionUtils.getPotionFromItem(p_192493_1_);
         return this.potion == null || this.potion == potion;
      }
   }

   public static ItemPredicate deserialize(@Nullable JsonElement p_192492_0_) {
      if (p_192492_0_ != null && !p_192492_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.getJsonObject(p_192492_0_, "item");
         if (jsonobject.has("type")) {
            ResourceLocation rl = new ResourceLocation(JSONUtils.getString(jsonobject, "type"));
            if (custom_predicates.containsKey(rl)) {
               return (ItemPredicate)((Function)custom_predicates.get(rl)).apply(jsonobject);
            } else {
               throw new JsonSyntaxException("There is no ItemPredicate of type " + rl);
            }
         } else {
            MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(jsonobject.get("count"));
            MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.fromJson(jsonobject.get("durability"));
            if (jsonobject.has("data")) {
               throw new JsonParseException("Disallowed data tag found");
            } else {
               NBTPredicate nbtpredicate = NBTPredicate.deserialize(jsonobject.get("nbt"));
               Item item = null;
               if (jsonobject.has("item")) {
                  ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(jsonobject, "item"));
                  item = (Item)Registry.ITEM.getValue(resourcelocation).orElseThrow(() -> {
                     return new JsonSyntaxException("Unknown item id '" + resourcelocation + "'");
                  });
               }

               Tag<Item> tag = null;
               if (jsonobject.has("tag")) {
                  ResourceLocation resourcelocation1 = new ResourceLocation(JSONUtils.getString(jsonobject, "tag"));
                  tag = ItemTags.getCollection().get(resourcelocation1);
                  if (tag == null) {
                     throw new JsonSyntaxException("Unknown item tag '" + resourcelocation1 + "'");
                  }
               }

               Potion potion = null;
               if (jsonobject.has("potion")) {
                  ResourceLocation resourcelocation2 = new ResourceLocation(JSONUtils.getString(jsonobject, "potion"));
                  potion = (Potion)Registry.POTION.getValue(resourcelocation2).orElseThrow(() -> {
                     return new JsonSyntaxException("Unknown potion '" + resourcelocation2 + "'");
                  });
               }

               EnchantmentPredicate[] aenchantmentpredicate1 = EnchantmentPredicate.deserializeArray(jsonobject.get("enchantments"));
               EnchantmentPredicate[] aenchantmentpredicate = EnchantmentPredicate.deserializeArray(jsonobject.get("stored_enchantments"));
               return new ItemPredicate(tag, item, minmaxbounds$intbound, minmaxbounds$intbound1, aenchantmentpredicate1, aenchantmentpredicate, potion, nbtpredicate);
            }
         }
      } else {
         return ANY;
      }
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.item != null) {
            jsonobject.addProperty("item", Registry.ITEM.getKey(this.item).toString());
         }

         if (this.tag != null) {
            jsonobject.addProperty("tag", this.tag.getId().toString());
         }

         jsonobject.add("count", this.count.serialize());
         jsonobject.add("durability", this.durability.serialize());
         jsonobject.add("nbt", this.nbt.serialize());
         JsonArray jsonarray1;
         EnchantmentPredicate[] var3;
         int var4;
         int var5;
         EnchantmentPredicate enchantmentpredicate1;
         if (this.enchantments.length > 0) {
            jsonarray1 = new JsonArray();
            var3 = this.enchantments;
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
               enchantmentpredicate1 = var3[var5];
               jsonarray1.add(enchantmentpredicate1.serialize());
            }

            jsonobject.add("enchantments", jsonarray1);
         }

         if (this.field_226656_g_.length > 0) {
            jsonarray1 = new JsonArray();
            var3 = this.field_226656_g_;
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
               enchantmentpredicate1 = var3[var5];
               jsonarray1.add(enchantmentpredicate1.serialize());
            }

            jsonobject.add("stored_enchantments", jsonarray1);
         }

         if (this.potion != null) {
            jsonobject.addProperty("potion", Registry.POTION.getKey(this.potion).toString());
         }

         return jsonobject;
      }
   }

   public static ItemPredicate[] deserializeArray(@Nullable JsonElement p_192494_0_) {
      if (p_192494_0_ != null && !p_192494_0_.isJsonNull()) {
         JsonArray jsonarray = JSONUtils.getJsonArray(p_192494_0_, "items");
         ItemPredicate[] aitempredicate = new ItemPredicate[jsonarray.size()];

         for(int i = 0; i < aitempredicate.length; ++i) {
            aitempredicate[i] = deserialize(jsonarray.get(i));
         }

         return aitempredicate;
      } else {
         return new ItemPredicate[0];
      }
   }

   public static void register(ResourceLocation p_register_0_, Function<JsonObject, ItemPredicate> p_register_1_) {
      custom_predicates.put(p_register_0_, p_register_1_);
   }

   public static Map<ResourceLocation, Function<JsonObject, ItemPredicate>> getPredicates() {
      return unmod_predicates;
   }

   static {
      unmod_predicates = Collections.unmodifiableMap(custom_predicates);
      ANY = new ItemPredicate();
   }

   public static class Builder {
      private final List<EnchantmentPredicate> enchantments = Lists.newArrayList();
      private final List<EnchantmentPredicate> field_226657_b_ = Lists.newArrayList();
      @Nullable
      private Item item;
      @Nullable
      private Tag<Item> tag;
      private MinMaxBounds.IntBound count;
      private MinMaxBounds.IntBound durability;
      @Nullable
      private Potion potion;
      private NBTPredicate nbt;

      private Builder() {
         this.count = MinMaxBounds.IntBound.UNBOUNDED;
         this.durability = MinMaxBounds.IntBound.UNBOUNDED;
         this.nbt = NBTPredicate.ANY;
      }

      public static ItemPredicate.Builder create() {
         return new ItemPredicate.Builder();
      }

      public ItemPredicate.Builder item(IItemProvider p_200308_1_) {
         this.item = p_200308_1_.asItem();
         return this;
      }

      public ItemPredicate.Builder tag(Tag<Item> p_200307_1_) {
         this.tag = p_200307_1_;
         return this;
      }

      public ItemPredicate.Builder nbt(CompoundNBT p_218002_1_) {
         this.nbt = new NBTPredicate(p_218002_1_);
         return this;
      }

      public ItemPredicate.Builder enchantment(EnchantmentPredicate p_218003_1_) {
         this.enchantments.add(p_218003_1_);
         return this;
      }

      public ItemPredicate build() {
         return new ItemPredicate(this.tag, this.item, this.count, this.durability, (EnchantmentPredicate[])this.enchantments.toArray(EnchantmentPredicate.field_226534_b_), (EnchantmentPredicate[])this.field_226657_b_.toArray(EnchantmentPredicate.field_226534_b_), this.potion, this.nbt);
      }
   }
}
