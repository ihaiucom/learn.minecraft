package net.minecraft.util.registry;

import net.minecraft.util.ResourceLocation;

public abstract class MutableRegistry<T> extends Registry<T> {
   public abstract <V extends T> V register(int var1, ResourceLocation var2, V var3);

   public abstract <V extends T> V register(ResourceLocation var1, V var2);

   public abstract boolean isEmpty();
}
