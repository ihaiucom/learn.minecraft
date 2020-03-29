package net.minecraftforge.common.crafting;

import com.google.gson.JsonObject;
import java.util.stream.Stream;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;

public class VanillaIngredientSerializer implements IIngredientSerializer<Ingredient> {
   public static final VanillaIngredientSerializer INSTANCE = new VanillaIngredientSerializer();

   public Ingredient parse(PacketBuffer buffer) {
      return Ingredient.fromItemListStream(Stream.generate(() -> {
         return new Ingredient.SingleItemList(buffer.readItemStack());
      }).limit((long)buffer.readVarInt()));
   }

   public Ingredient parse(JsonObject json) {
      return Ingredient.fromItemListStream(Stream.of(Ingredient.deserializeItemList(json)));
   }

   public void write(PacketBuffer buffer, Ingredient ingredient) {
      ItemStack[] items = ingredient.getMatchingStacks();
      buffer.writeVarInt(items.length);
      ItemStack[] var4 = items;
      int var5 = items.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ItemStack stack = var4[var6];
         buffer.writeItemStack(stack);
      }

   }
}
