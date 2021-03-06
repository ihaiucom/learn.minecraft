package net.minecraftforge.registries;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DefaultedRegistry;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class NamespacedDefaultedWrapper<T extends IForgeRegistryEntry<T>> extends DefaultedRegistry<T> implements ILockableRegistry {
   private static final Logger LOGGER = LogManager.getLogger();
   private boolean locked;
   private ForgeRegistry<T> delegate;

   private NamespacedDefaultedWrapper(ForgeRegistry<T> owner) {
      super(owner.getRegistryName().toString());
      this.locked = false;
      this.delegate = owner;
   }

   public <V extends T> V register(int id, ResourceLocation key, V value) {
      if (this.locked) {
         throw new IllegalStateException("Can not register to a locked registry. Modder should use Forge Register methods.");
      } else {
         Validate.notNull(value);
         if (value.getRegistryName() == null) {
            value.setRegistryName(key);
         }

         int realId = this.delegate.add(id, value);
         if (realId != id && id != -1) {
            LOGGER.warn("Registered object did not get ID it asked for. Name: {} Type: {} Expected: {} Got: {}", key, value.getRegistryType().getName(), id, realId);
         }

         return value;
      }
   }

   public <V extends T> V register(ResourceLocation key, V value) {
      return this.register(-1, key, (IForgeRegistryEntry)value);
   }

   public Optional<T> getValue(@Nullable ResourceLocation name) {
      return Optional.ofNullable(this.delegate.getRaw(name));
   }

   @Nullable
   public T getOrDefault(@Nullable ResourceLocation name) {
      return this.delegate.getValue(name);
   }

   @Nullable
   public ResourceLocation getKey(T value) {
      return this.delegate.getKey(value);
   }

   public boolean containsKey(ResourceLocation key) {
      return this.delegate.containsKey(key);
   }

   public int getId(@Nullable T value) {
      return this.delegate.getID(value);
   }

   @Nullable
   public T getByValue(int id) {
      return this.delegate.getValue(id);
   }

   public Iterator<T> iterator() {
      return this.delegate.iterator();
   }

   public Set<ResourceLocation> keySet() {
      return this.delegate.getKeys();
   }

   @Nullable
   public T getRandom(Random random) {
      Collection<T> values = this.delegate.getValues();
      return (IForgeRegistryEntry)values.stream().skip((long)random.nextInt(values.size())).findFirst().orElse(this.delegate.getDefault());
   }

   public ResourceLocation getDefaultKey() {
      return this.delegate.getDefaultKey();
   }

   public boolean isEmpty() {
      return this.delegate.isEmpty();
   }

   public void lock() {
      this.locked = true;
   }

   // $FF: synthetic method
   NamespacedDefaultedWrapper(ForgeRegistry x0, Object x1) {
      this(x0);
   }

   public static class Factory<V extends IForgeRegistryEntry<V>> implements IForgeRegistry.CreateCallback<V> {
      public static final ResourceLocation ID = new ResourceLocation("forge", "registry_defaulted_wrapper");

      public void onCreate(IForgeRegistryInternal<V> owner, RegistryManager stage) {
         owner.setSlaveMap(ID, new NamespacedDefaultedWrapper((ForgeRegistry)owner));
      }
   }
}
