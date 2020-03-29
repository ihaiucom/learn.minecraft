package net.minecraftforge.common.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;

public class NBTIngredient extends Ingredient {
   private final ItemStack stack;

   protected NBTIngredient(ItemStack stack) {
      super(Stream.of(new Ingredient.SingleItemList(stack)));
      this.stack = stack;
   }

   public boolean test(@Nullable ItemStack input) {
      if (input == null) {
         return false;
      } else {
         return this.stack.getItem() == input.getItem() && this.stack.getDamage() == input.getDamage() && this.stack.areShareTagsEqual(input);
      }
   }

   public boolean isSimple() {
      return false;
   }

   public IIngredientSerializer<? extends Ingredient> getSerializer() {
      return NBTIngredient.Serializer.INSTANCE;
   }

   public JsonElement serialize() {
      JsonObject json = new JsonObject();
      json.addProperty("type", CraftingHelper.getID(NBTIngredient.Serializer.INSTANCE).toString());
      json.addProperty("item", this.stack.getItem().getRegistryName().toString());
      json.addProperty("count", this.stack.getCount());
      if (this.stack.hasTag()) {
         json.addProperty("nbt", this.stack.getTag().toString());
      }

      return json;
   }

   public static class Serializer implements IIngredientSerializer<NBTIngredient> {
      public static final NBTIngredient.Serializer INSTANCE = new NBTIngredient.Serializer();

      public NBTIngredient parse(PacketBuffer buffer) {
         return new NBTIngredient(buffer.readItemStack());
      }

      public NBTIngredient parse(JsonObject json) {
         return new NBTIngredient(CraftingHelper.getItemStack(json, true));
      }

      public void write(PacketBuffer buffer, NBTIngredient ingredient) {
         buffer.writeItemStack(ingredient.stack);
      }
   }
}
