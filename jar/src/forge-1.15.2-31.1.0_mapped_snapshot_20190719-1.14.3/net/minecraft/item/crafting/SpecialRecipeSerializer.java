package net.minecraft.item.crafting;

import com.google.gson.JsonObject;
import java.util.function.Function;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class SpecialRecipeSerializer<T extends IRecipe<?>> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
   private final Function<ResourceLocation, T> field_222176_t;

   public SpecialRecipeSerializer(Function<ResourceLocation, T> p_i50024_1_) {
      this.field_222176_t = p_i50024_1_;
   }

   public T read(ResourceLocation p_199425_1_, JsonObject p_199425_2_) {
      return (IRecipe)this.field_222176_t.apply(p_199425_1_);
   }

   public T read(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_) {
      return (IRecipe)this.field_222176_t.apply(p_199426_1_);
   }

   public void write(PacketBuffer p_199427_1_, T p_199427_2_) {
   }
}
