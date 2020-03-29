package net.minecraftforge.common.crafting;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class CraftingHelper {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker CRAFTHELPER = MarkerManager.getMarker("CRAFTHELPER");
   private static Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private static final Map<ResourceLocation, IConditionSerializer<?>> conditions = new HashMap();
   private static final BiMap<ResourceLocation, IIngredientSerializer<?>> ingredients = HashBiMap.create();

   public static IConditionSerializer<?> register(IConditionSerializer<?> serializer) {
      ResourceLocation key = serializer.getID();
      if (conditions.containsKey(key)) {
         throw new IllegalStateException("Duplicate recipe condition serializer: " + key);
      } else {
         conditions.put(key, serializer);
         return serializer;
      }
   }

   public static <T extends Ingredient> IIngredientSerializer<T> register(ResourceLocation key, IIngredientSerializer<T> serializer) {
      if (ingredients.containsKey(key)) {
         throw new IllegalStateException("Duplicate recipe ingredient serializer: " + key);
      } else if (ingredients.containsValue(serializer)) {
         throw new IllegalStateException("Duplicate recipe ingredient serializer: " + key + " " + serializer);
      } else {
         ingredients.put(key, serializer);
         return serializer;
      }
   }

   @Nullable
   public static ResourceLocation getID(IIngredientSerializer<?> serializer) {
      return (ResourceLocation)ingredients.inverse().get(serializer);
   }

   public static <T extends Ingredient> void write(PacketBuffer buffer, T ingredient) {
      IIngredientSerializer<T> serializer = ingredient.getSerializer();
      ResourceLocation key = (ResourceLocation)ingredients.inverse().get(serializer);
      if (key == null) {
         throw new IllegalArgumentException("Tried to serialize unregistered Ingredient: " + ingredient + " " + serializer);
      } else {
         if (serializer != VanillaIngredientSerializer.INSTANCE) {
            buffer.writeVarInt(-1);
            buffer.writeResourceLocation(key);
         }

         serializer.write(buffer, ingredient);
      }
   }

   public static Ingredient getIngredient(ResourceLocation type, PacketBuffer buffer) {
      IIngredientSerializer<?> serializer = (IIngredientSerializer)ingredients.get(type);
      if (serializer == null) {
         throw new IllegalArgumentException("Can not deserialize unknown Ingredient type: " + type);
      } else {
         return serializer.parse(buffer);
      }
   }

   public static Ingredient getIngredient(JsonElement json) {
      if (json != null && !json.isJsonNull()) {
         if (json.isJsonArray()) {
            List<Ingredient> ingredients = Lists.newArrayList();
            List<Ingredient> vanilla = Lists.newArrayList();
            json.getAsJsonArray().forEach((ele) -> {
               Ingredient ing = getIngredient(ele);
               if (ing.getClass() == Ingredient.class) {
                  vanilla.add(ing);
               } else {
                  ingredients.add(ing);
               }

            });
            if (!vanilla.isEmpty()) {
               ingredients.add(Ingredient.merge(vanilla));
            }

            if (ingredients.size() == 0) {
               throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            } else {
               return (Ingredient)(ingredients.size() == 1 ? (Ingredient)ingredients.get(0) : new CompoundIngredient(ingredients));
            }
         } else if (!json.isJsonObject()) {
            throw new JsonSyntaxException("Expcted ingredient to be a object or array of objects");
         } else {
            JsonObject obj = (JsonObject)json;
            String type = JSONUtils.getString(obj, "type", "minecraft:item");
            if (type.isEmpty()) {
               throw new JsonSyntaxException("Ingredient type can not be an empty string");
            } else {
               IIngredientSerializer<?> serializer = (IIngredientSerializer)CraftingHelper.ingredients.get(new ResourceLocation(type));
               if (serializer == null) {
                  throw new JsonSyntaxException("Unknown ingredient type: " + type);
               } else {
                  return serializer.parse(obj);
               }
            }
         }
      } else {
         throw new JsonSyntaxException("Json cannot be null");
      }
   }

   public static ItemStack getItemStack(JsonObject json, boolean readNBT) {
      String itemName = JSONUtils.getString(json, "item");
      Item item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
      if (item == null) {
         throw new JsonSyntaxException("Unknown item '" + itemName + "'");
      } else if (readNBT && json.has("nbt")) {
         try {
            JsonElement element = json.get("nbt");
            CompoundNBT nbt;
            if (element.isJsonObject()) {
               nbt = JsonToNBT.getTagFromJson(GSON.toJson(element));
            } else {
               nbt = JsonToNBT.getTagFromJson(JSONUtils.getString(element, "nbt"));
            }

            CompoundNBT tmp = new CompoundNBT();
            if (nbt.contains("ForgeCaps")) {
               tmp.put("ForgeCaps", nbt.get("ForgeCaps"));
               nbt.remove("ForgeCaps");
            }

            tmp.put("tag", nbt);
            tmp.putString("id", itemName);
            tmp.putInt("Count", JSONUtils.getInt(json, "count", 1));
            return ItemStack.read(tmp);
         } catch (CommandSyntaxException var7) {
            throw new JsonSyntaxException("Invalid NBT Entry: " + var7.toString());
         }
      } else {
         return new ItemStack(item, JSONUtils.getInt(json, "count", 1));
      }
   }

   public static boolean processConditions(JsonObject json, String memberName) {
      return !json.has(memberName) || processConditions(JSONUtils.getJsonArray(json, memberName));
   }

   public static boolean processConditions(JsonArray conditions) {
      for(int x = 0; x < conditions.size(); ++x) {
         if (!conditions.get(x).isJsonObject()) {
            throw new JsonSyntaxException("Conditions must be an array of JsonObjects");
         }

         JsonObject json = conditions.get(x).getAsJsonObject();
         if (!getCondition(json).test()) {
            return false;
         }
      }

      return true;
   }

   public static ICondition getCondition(JsonObject json) {
      ResourceLocation type = new ResourceLocation(JSONUtils.getString(json, "type"));
      IConditionSerializer<?> serializer = (IConditionSerializer)conditions.get(type);
      if (serializer == null) {
         throw new JsonSyntaxException("Unknown condition type: " + type.toString());
      } else {
         return serializer.read(json);
      }
   }

   public static <T extends ICondition> JsonObject serialize(T condition) {
      IConditionSerializer<T> serializer = (IConditionSerializer)conditions.get(condition.getID());
      if (serializer == null) {
         throw new JsonSyntaxException("Unknown condition type: " + condition.getID().toString());
      } else {
         return serializer.getJson(condition);
      }
   }
}
