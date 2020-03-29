package net.minecraftforge.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;

public interface IIngredientSerializer<T extends Ingredient> {
   T parse(PacketBuffer var1);

   T parse(JsonObject var1);

   void write(PacketBuffer var1, T var2);
}
