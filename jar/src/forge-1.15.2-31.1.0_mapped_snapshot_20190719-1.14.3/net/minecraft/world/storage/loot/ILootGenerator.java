package net.minecraft.world.storage.loot;

import java.util.function.Consumer;
import net.minecraft.item.ItemStack;

public interface ILootGenerator {
   int getEffectiveWeight(float var1);

   void func_216188_a(Consumer<ItemStack> var1, LootContext var2);
}
