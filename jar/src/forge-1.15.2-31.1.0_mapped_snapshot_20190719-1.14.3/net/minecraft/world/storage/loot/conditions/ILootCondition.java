package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Predicate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.IParameterized;
import net.minecraft.world.storage.loot.LootContext;

@FunctionalInterface
public interface ILootCondition extends IParameterized, Predicate<LootContext> {
   public abstract static class AbstractSerializer<T extends ILootCondition> {
      private final ResourceLocation lootTableLocation;
      private final Class<T> conditionClass;

      protected AbstractSerializer(ResourceLocation p_i47021_1_, Class<T> p_i47021_2_) {
         this.lootTableLocation = p_i47021_1_;
         this.conditionClass = p_i47021_2_;
      }

      public ResourceLocation getLootTableLocation() {
         return this.lootTableLocation;
      }

      public Class<T> getConditionClass() {
         return this.conditionClass;
      }

      public abstract void serialize(JsonObject var1, T var2, JsonSerializationContext var3);

      public abstract T deserialize(JsonObject var1, JsonDeserializationContext var2);
   }

   @FunctionalInterface
   public interface IBuilder {
      ILootCondition build();

      default ILootCondition.IBuilder inverted() {
         return Inverted.builder(this);
      }

      default Alternative.Builder alternative(ILootCondition.IBuilder p_216297_1_) {
         return Alternative.builder(this, p_216297_1_);
      }
   }
}
