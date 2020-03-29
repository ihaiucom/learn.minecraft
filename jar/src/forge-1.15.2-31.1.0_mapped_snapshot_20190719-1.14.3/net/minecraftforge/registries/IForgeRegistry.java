package net.minecraftforge.registries;

import java.util.Collection;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public interface IForgeRegistry<V extends IForgeRegistryEntry<V>> extends Iterable<V> {
   ResourceLocation getRegistryName();

   Class<V> getRegistrySuperType();

   void register(V var1);

   void registerAll(V... var1);

   boolean containsKey(ResourceLocation var1);

   boolean containsValue(V var1);

   boolean isEmpty();

   @Nullable
   V getValue(ResourceLocation var1);

   @Nullable
   ResourceLocation getKey(V var1);

   @Nullable
   ResourceLocation getDefaultKey();

   @Nonnull
   Set<ResourceLocation> getKeys();

   @Nonnull
   Collection<V> getValues();

   @Nonnull
   Set<Entry<ResourceLocation, V>> getEntries();

   <T> T getSlaveMap(ResourceLocation var1, Class<T> var2);

   public interface MissingFactory<V extends IForgeRegistryEntry<V>> {
      V createMissing(ResourceLocation var1, boolean var2);
   }

   public interface DummyFactory<V extends IForgeRegistryEntry<V>> {
      V createDummy(ResourceLocation var1);
   }

   public interface BakeCallback<V extends IForgeRegistryEntry<V>> {
      void onBake(IForgeRegistryInternal<V> var1, RegistryManager var2);
   }

   public interface ValidateCallback<V extends IForgeRegistryEntry<V>> {
      void onValidate(IForgeRegistryInternal<V> var1, RegistryManager var2, int var3, ResourceLocation var4, V var5);
   }

   public interface CreateCallback<V extends IForgeRegistryEntry<V>> {
      void onCreate(IForgeRegistryInternal<V> var1, RegistryManager var2);
   }

   public interface ClearCallback<V extends IForgeRegistryEntry<V>> {
      void onClear(IForgeRegistryInternal<V> var1, RegistryManager var2);
   }

   public interface AddCallback<V extends IForgeRegistryEntry<V>> {
      void onAdd(IForgeRegistryInternal<V> var1, RegistryManager var2, int var3, V var4, @Nullable V var5);
   }
}
