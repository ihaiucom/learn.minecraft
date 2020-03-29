package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnchantRandomly extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<Enchantment> enchantments;

   private EnchantRandomly(ILootCondition[] p_i51238_1_, Collection<Enchantment> p_i51238_2_) {
      super(p_i51238_1_);
      this.enchantments = ImmutableList.copyOf(p_i51238_2_);
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      Random lvt_4_1_ = p_215859_2_.getRandom();
      Enchantment lvt_3_2_;
      if (this.enchantments.isEmpty()) {
         List<Enchantment> lvt_5_1_ = Lists.newArrayList();
         Iterator var6 = Registry.ENCHANTMENT.iterator();

         label32:
         while(true) {
            Enchantment lvt_7_1_;
            do {
               if (!var6.hasNext()) {
                  if (lvt_5_1_.isEmpty()) {
                     LOGGER.warn("Couldn't find a compatible enchantment for {}", p_215859_1_);
                     return p_215859_1_;
                  }

                  lvt_3_2_ = (Enchantment)lvt_5_1_.get(lvt_4_1_.nextInt(lvt_5_1_.size()));
                  break label32;
               }

               lvt_7_1_ = (Enchantment)var6.next();
            } while(p_215859_1_.getItem() != Items.BOOK && !lvt_7_1_.canApply(p_215859_1_));

            lvt_5_1_.add(lvt_7_1_);
         }
      } else {
         lvt_3_2_ = (Enchantment)this.enchantments.get(lvt_4_1_.nextInt(this.enchantments.size()));
      }

      int lvt_5_2_ = MathHelper.nextInt(lvt_4_1_, lvt_3_2_.getMinLevel(), lvt_3_2_.getMaxLevel());
      if (p_215859_1_.getItem() == Items.BOOK) {
         p_215859_1_ = new ItemStack(Items.ENCHANTED_BOOK);
         EnchantedBookItem.addEnchantment(p_215859_1_, new EnchantmentData(lvt_3_2_, lvt_5_2_));
      } else {
         p_215859_1_.addEnchantment(lvt_3_2_, lvt_5_2_);
      }

      return p_215859_1_;
   }

   public static LootFunction.Builder<?> func_215900_c() {
      return builder((p_215899_0_) -> {
         return new EnchantRandomly(p_215899_0_, ImmutableList.of());
      });
   }

   // $FF: synthetic method
   EnchantRandomly(ILootCondition[] p_i51239_1_, Collection p_i51239_2_, Object p_i51239_3_) {
      this(p_i51239_1_, p_i51239_2_);
   }

   public static class Serializer extends LootFunction.Serializer<EnchantRandomly> {
      public Serializer() {
         super(new ResourceLocation("enchant_randomly"), EnchantRandomly.class);
      }

      public void serialize(JsonObject p_186532_1_, EnchantRandomly p_186532_2_, JsonSerializationContext p_186532_3_) {
         super.serialize(p_186532_1_, (LootFunction)p_186532_2_, p_186532_3_);
         if (!p_186532_2_.enchantments.isEmpty()) {
            JsonArray lvt_4_1_ = new JsonArray();
            Iterator var5 = p_186532_2_.enchantments.iterator();

            while(var5.hasNext()) {
               Enchantment lvt_6_1_ = (Enchantment)var5.next();
               ResourceLocation lvt_7_1_ = Registry.ENCHANTMENT.getKey(lvt_6_1_);
               if (lvt_7_1_ == null) {
                  throw new IllegalArgumentException("Don't know how to serialize enchantment " + lvt_6_1_);
               }

               lvt_4_1_.add(new JsonPrimitive(lvt_7_1_.toString()));
            }

            p_186532_1_.add("enchantments", lvt_4_1_);
         }

      }

      public EnchantRandomly deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         List<Enchantment> lvt_4_1_ = Lists.newArrayList();
         if (p_186530_1_.has("enchantments")) {
            JsonArray lvt_5_1_ = JSONUtils.getJsonArray(p_186530_1_, "enchantments");
            Iterator var6 = lvt_5_1_.iterator();

            while(var6.hasNext()) {
               JsonElement lvt_7_1_ = (JsonElement)var6.next();
               String lvt_8_1_ = JSONUtils.getString(lvt_7_1_, "enchantment");
               Enchantment lvt_9_1_ = (Enchantment)Registry.ENCHANTMENT.getValue(new ResourceLocation(lvt_8_1_)).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown enchantment '" + lvt_8_1_ + "'");
               });
               lvt_4_1_.add(lvt_9_1_);
            }
         }

         return new EnchantRandomly(p_186530_3_, lvt_4_1_);
      }

      // $FF: synthetic method
      public LootFunction deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return this.deserialize(p_186530_1_, p_186530_2_, p_186530_3_);
      }
   }
}
