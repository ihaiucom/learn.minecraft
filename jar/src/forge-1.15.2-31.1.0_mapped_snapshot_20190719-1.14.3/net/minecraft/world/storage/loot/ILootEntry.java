package net.minecraft.world.storage.loot;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
interface ILootEntry {
   ILootEntry field_216139_a = (p_216134_0_, p_216134_1_) -> {
      return false;
   };
   ILootEntry field_216140_b = (p_216136_0_, p_216136_1_) -> {
      return true;
   };

   boolean expand(LootContext var1, Consumer<ILootGenerator> var2);

   default ILootEntry sequence(ILootEntry p_216133_1_) {
      Objects.requireNonNull(p_216133_1_);
      return (p_216137_2_, p_216137_3_) -> {
         return this.expand(p_216137_2_, p_216137_3_) && p_216133_1_.expand(p_216137_2_, p_216137_3_);
      };
   }

   default ILootEntry alternate(ILootEntry p_216135_1_) {
      Objects.requireNonNull(p_216135_1_);
      return (p_216138_2_, p_216138_3_) -> {
         return this.expand(p_216138_2_, p_216138_3_) || p_216135_1_.expand(p_216138_2_, p_216138_3_);
      };
   }
}
