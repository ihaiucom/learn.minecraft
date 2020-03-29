package net.minecraftforge.registries;

import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public interface IForgeRegistryEntry<V> {
   V setRegistryName(ResourceLocation var1);

   @Nullable
   ResourceLocation getRegistryName();

   Class<V> getRegistryType();
}
