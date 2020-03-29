package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.IParameterized;
import net.minecraft.world.storage.loot.LootContext;

public interface ILootFunction extends IParameterized, BiFunction<ItemStack, LootContext, ItemStack> {
   static Consumer<ItemStack> func_215858_a(BiFunction<ItemStack, LootContext, ItemStack> p_215858_0_, Consumer<ItemStack> p_215858_1_, LootContext p_215858_2_) {
      return (p_215857_3_) -> {
         p_215858_1_.accept(p_215858_0_.apply(p_215857_3_, p_215858_2_));
      };
   }

   public abstract static class Serializer<T extends ILootFunction> {
      private final ResourceLocation lootTableLocation;
      private final Class<T> functionClass;

      protected Serializer(ResourceLocation p_i47002_1_, Class<T> p_i47002_2_) {
         this.lootTableLocation = p_i47002_1_;
         this.functionClass = p_i47002_2_;
      }

      public ResourceLocation getFunctionName() {
         return this.lootTableLocation;
      }

      public Class<T> getFunctionClass() {
         return this.functionClass;
      }

      public abstract void serialize(JsonObject var1, T var2, JsonSerializationContext var3);

      public abstract T deserialize(JsonObject var1, JsonDeserializationContext var2);
   }

   public interface IBuilder {
      ILootFunction build();
   }
}
