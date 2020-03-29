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
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class SetAttributes extends LootFunction {
   private final List<SetAttributes.Modifier> modifiers;

   private SetAttributes(ILootCondition[] p_i51228_1_, List<SetAttributes.Modifier> p_i51228_2_) {
      super(p_i51228_1_);
      this.modifiers = ImmutableList.copyOf(p_i51228_2_);
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      Random lvt_3_1_ = p_215859_2_.getRandom();
      Iterator var4 = this.modifiers.iterator();

      while(var4.hasNext()) {
         SetAttributes.Modifier lvt_5_1_ = (SetAttributes.Modifier)var4.next();
         UUID lvt_6_1_ = lvt_5_1_.uuid;
         if (lvt_6_1_ == null) {
            lvt_6_1_ = UUID.randomUUID();
         }

         EquipmentSlotType lvt_7_1_ = lvt_5_1_.slots[lvt_3_1_.nextInt(lvt_5_1_.slots.length)];
         p_215859_1_.addAttributeModifier(lvt_5_1_.attributeName, new AttributeModifier(lvt_6_1_, lvt_5_1_.modifierName, (double)lvt_5_1_.amount.generateFloat(lvt_3_1_), lvt_5_1_.operation), lvt_7_1_);
      }

      return p_215859_1_;
   }

   // $FF: synthetic method
   SetAttributes(ILootCondition[] p_i51229_1_, List p_i51229_2_, Object p_i51229_3_) {
      this(p_i51229_1_, p_i51229_2_);
   }

   static class Modifier {
      private final String modifierName;
      private final String attributeName;
      private final AttributeModifier.Operation operation;
      private final RandomValueRange amount;
      @Nullable
      private final UUID uuid;
      private final EquipmentSlotType[] slots;

      private Modifier(String p_i50835_1_, String p_i50835_2_, AttributeModifier.Operation p_i50835_3_, RandomValueRange p_i50835_4_, EquipmentSlotType[] p_i50835_5_, @Nullable UUID p_i50835_6_) {
         this.modifierName = p_i50835_1_;
         this.attributeName = p_i50835_2_;
         this.operation = p_i50835_3_;
         this.amount = p_i50835_4_;
         this.uuid = p_i50835_6_;
         this.slots = p_i50835_5_;
      }

      public JsonObject serialize(JsonSerializationContext p_186592_1_) {
         JsonObject lvt_2_1_ = new JsonObject();
         lvt_2_1_.addProperty("name", this.modifierName);
         lvt_2_1_.addProperty("attribute", this.attributeName);
         lvt_2_1_.addProperty("operation", func_216244_a(this.operation));
         lvt_2_1_.add("amount", p_186592_1_.serialize(this.amount));
         if (this.uuid != null) {
            lvt_2_1_.addProperty("id", this.uuid.toString());
         }

         if (this.slots.length == 1) {
            lvt_2_1_.addProperty("slot", this.slots[0].getName());
         } else {
            JsonArray lvt_3_1_ = new JsonArray();
            EquipmentSlotType[] var4 = this.slots;
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               EquipmentSlotType lvt_7_1_ = var4[var6];
               lvt_3_1_.add(new JsonPrimitive(lvt_7_1_.getName()));
            }

            lvt_2_1_.add("slot", lvt_3_1_);
         }

         return lvt_2_1_;
      }

      public static SetAttributes.Modifier deserialize(JsonObject p_186586_0_, JsonDeserializationContext p_186586_1_) {
         String lvt_2_1_ = JSONUtils.getString(p_186586_0_, "name");
         String lvt_3_1_ = JSONUtils.getString(p_186586_0_, "attribute");
         AttributeModifier.Operation lvt_4_1_ = func_216246_a(JSONUtils.getString(p_186586_0_, "operation"));
         RandomValueRange lvt_5_1_ = (RandomValueRange)JSONUtils.deserializeClass(p_186586_0_, "amount", p_186586_1_, RandomValueRange.class);
         UUID lvt_7_1_ = null;
         EquipmentSlotType[] lvt_6_2_;
         if (JSONUtils.isString(p_186586_0_, "slot")) {
            lvt_6_2_ = new EquipmentSlotType[]{EquipmentSlotType.fromString(JSONUtils.getString(p_186586_0_, "slot"))};
         } else {
            if (!JSONUtils.isJsonArray(p_186586_0_, "slot")) {
               throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
            }

            JsonArray lvt_8_1_ = JSONUtils.getJsonArray(p_186586_0_, "slot");
            lvt_6_2_ = new EquipmentSlotType[lvt_8_1_.size()];
            int lvt_9_1_ = 0;

            JsonElement lvt_11_1_;
            for(Iterator var10 = lvt_8_1_.iterator(); var10.hasNext(); lvt_6_2_[lvt_9_1_++] = EquipmentSlotType.fromString(JSONUtils.getString(lvt_11_1_, "slot"))) {
               lvt_11_1_ = (JsonElement)var10.next();
            }

            if (lvt_6_2_.length == 0) {
               throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
            }
         }

         if (p_186586_0_.has("id")) {
            String lvt_8_2_ = JSONUtils.getString(p_186586_0_, "id");

            try {
               lvt_7_1_ = UUID.fromString(lvt_8_2_);
            } catch (IllegalArgumentException var12) {
               throw new JsonSyntaxException("Invalid attribute modifier id '" + lvt_8_2_ + "' (must be UUID format, with dashes)");
            }
         }

         return new SetAttributes.Modifier(lvt_2_1_, lvt_3_1_, lvt_4_1_, lvt_5_1_, lvt_6_2_, lvt_7_1_);
      }

      private static String func_216244_a(AttributeModifier.Operation p_216244_0_) {
         switch(p_216244_0_) {
         case ADDITION:
            return "addition";
         case MULTIPLY_BASE:
            return "multiply_base";
         case MULTIPLY_TOTAL:
            return "multiply_total";
         default:
            throw new IllegalArgumentException("Unknown operation " + p_216244_0_);
         }
      }

      private static AttributeModifier.Operation func_216246_a(String p_216246_0_) {
         byte var2 = -1;
         switch(p_216246_0_.hashCode()) {
         case -1226589444:
            if (p_216246_0_.equals("addition")) {
               var2 = 0;
            }
            break;
         case -78229492:
            if (p_216246_0_.equals("multiply_base")) {
               var2 = 1;
            }
            break;
         case 1886894441:
            if (p_216246_0_.equals("multiply_total")) {
               var2 = 2;
            }
         }

         switch(var2) {
         case 0:
            return AttributeModifier.Operation.ADDITION;
         case 1:
            return AttributeModifier.Operation.MULTIPLY_BASE;
         case 2:
            return AttributeModifier.Operation.MULTIPLY_TOTAL;
         default:
            throw new JsonSyntaxException("Unknown attribute modifier operation " + p_216246_0_);
         }
      }
   }

   public static class Serializer extends LootFunction.Serializer<SetAttributes> {
      public Serializer() {
         super(new ResourceLocation("set_attributes"), SetAttributes.class);
      }

      public void serialize(JsonObject p_186532_1_, SetAttributes p_186532_2_, JsonSerializationContext p_186532_3_) {
         super.serialize(p_186532_1_, (LootFunction)p_186532_2_, p_186532_3_);
         JsonArray lvt_4_1_ = new JsonArray();
         Iterator var5 = p_186532_2_.modifiers.iterator();

         while(var5.hasNext()) {
            SetAttributes.Modifier lvt_6_1_ = (SetAttributes.Modifier)var5.next();
            lvt_4_1_.add(lvt_6_1_.serialize(p_186532_3_));
         }

         p_186532_1_.add("modifiers", lvt_4_1_);
      }

      public SetAttributes deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         JsonArray lvt_4_1_ = JSONUtils.getJsonArray(p_186530_1_, "modifiers");
         List<SetAttributes.Modifier> lvt_5_1_ = Lists.newArrayListWithExpectedSize(lvt_4_1_.size());
         Iterator var6 = lvt_4_1_.iterator();

         while(var6.hasNext()) {
            JsonElement lvt_7_1_ = (JsonElement)var6.next();
            lvt_5_1_.add(SetAttributes.Modifier.deserialize(JSONUtils.getJsonObject(lvt_7_1_, "modifier"), p_186530_2_));
         }

         if (lvt_5_1_.isEmpty()) {
            throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
         } else {
            return new SetAttributes(p_186530_3_, lvt_5_1_);
         }
      }

      // $FF: synthetic method
      public LootFunction deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return this.deserialize(p_186530_1_, p_186530_2_, p_186530_3_);
      }
   }
}
