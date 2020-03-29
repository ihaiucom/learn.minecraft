package net.minecraftforge.registries;

import net.minecraft.util.ResourceLocation;

public interface IForgeRegistryInternal<V extends IForgeRegistryEntry<V>> extends IForgeRegistry<V> {
   void setSlaveMap(ResourceLocation var1, Object var2);
}
