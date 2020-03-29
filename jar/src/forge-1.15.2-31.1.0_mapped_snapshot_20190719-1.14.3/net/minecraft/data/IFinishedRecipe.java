package net.minecraft.data;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public interface IFinishedRecipe {
   void serialize(JsonObject var1);

   default JsonObject getRecipeJson() {
      JsonObject lvt_1_1_ = new JsonObject();
      lvt_1_1_.addProperty("type", Registry.RECIPE_SERIALIZER.getKey(this.getSerializer()).toString());
      this.serialize(lvt_1_1_);
      return lvt_1_1_;
   }

   ResourceLocation getID();

   IRecipeSerializer<?> getSerializer();

   @Nullable
   JsonObject getAdvancementJson();

   @Nullable
   ResourceLocation getAdvancementID();
}
