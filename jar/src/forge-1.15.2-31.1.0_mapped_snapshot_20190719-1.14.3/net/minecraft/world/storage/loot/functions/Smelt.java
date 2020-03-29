package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Smelt extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();

   private Smelt(ILootCondition[] p_i46619_1_) {
      super(p_i46619_1_);
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      if (p_215859_1_.isEmpty()) {
         return p_215859_1_;
      } else {
         Optional<FurnaceRecipe> optional = p_215859_2_.getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(new ItemStack[]{p_215859_1_}), p_215859_2_.getWorld());
         if (optional.isPresent()) {
            ItemStack itemstack = ((FurnaceRecipe)optional.get()).getRecipeOutput();
            if (!itemstack.isEmpty()) {
               ItemStack itemstack1 = itemstack.copy();
               itemstack1.setCount(p_215859_1_.getCount() * itemstack.getCount());
               return itemstack1;
            }
         }

         LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", p_215859_1_);
         return p_215859_1_;
      }
   }

   public static LootFunction.Builder<?> func_215953_b() {
      return builder(Smelt::new);
   }

   // $FF: synthetic method
   Smelt(ILootCondition[] p_i51214_1_, Object p_i51214_2_) {
      this(p_i51214_1_);
   }

   public static class Serializer extends LootFunction.Serializer<Smelt> {
      protected Serializer() {
         super(new ResourceLocation("furnace_smelt"), Smelt.class);
      }

      public Smelt deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return new Smelt(p_186530_3_);
      }
   }
}
