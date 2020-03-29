package net.minecraftforge.common.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class StackList implements Ingredient.IItemList {
   private Collection<ItemStack> items;

   public StackList(Collection<ItemStack> items) {
      this.items = Collections.unmodifiableCollection(items);
   }

   public Collection<ItemStack> getStacks() {
      return this.items;
   }

   public JsonObject serialize() {
      if (this.items.size() == 1) {
         return this.toJson((ItemStack)this.items.iterator().next());
      } else {
         JsonObject ret = new JsonObject();
         JsonArray array = new JsonArray();
         this.items.forEach((stack) -> {
            array.add(this.toJson(stack));
         });
         ret.add("items", array);
         return ret;
      }
   }

   private JsonObject toJson(ItemStack stack) {
      JsonObject ret = new JsonObject();
      ret.addProperty("item", stack.getItem().getRegistryName().toString());
      if (stack.getCount() != 1) {
         ret.addProperty("count", stack.getCount());
      }

      if (stack.getTag() != null) {
         ret.addProperty("nbt", stack.getTag().toString());
      }

      return ret;
   }
}
