package net.minecraftforge.registries;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.MutableRegistry;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ClearableRegistry<T> extends MutableRegistry<T> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker REGISTRY = MarkerManager.getMarker("REGISTRY");
   private final IntIdentityHashBiMap<T> ids;
   private final BiMap<ResourceLocation, T> map;
   private final Set<ResourceLocation> keys;
   private List<T> values;
   private Map<ResourceLocation, Set<T>> known;
   private final ResourceLocation name;
   private final boolean isDelegated;
   private int nextId;

   public ClearableRegistry(ResourceLocation name) {
      this(name, (Class)null);
   }

   public ClearableRegistry(ResourceLocation name, Class<T> superType) {
      this.ids = new IntIdentityHashBiMap(256);
      this.map = HashBiMap.create();
      this.keys = Collections.unmodifiableSet(this.map.keySet());
      this.values = new ArrayList();
      this.known = new HashMap();
      this.nextId = 0;
      this.name = name;
      this.isDelegated = superType != null && ForgeRegistryEntry.class.isAssignableFrom(superType);
   }

   @Nullable
   public ResourceLocation getKey(T value) {
      return (ResourceLocation)this.map.inverse().get(value);
   }

   @Nullable
   public int getId(T value) {
      return this.ids.getId(value);
   }

   @Nullable
   public T getByValue(int id) {
      return this.ids.getByValue(id);
   }

   public Iterator<T> iterator() {
      return this.ids.iterator();
   }

   @Nullable
   public T getOrDefault(ResourceLocation key) {
      return this.map.get(key);
   }

   public <V extends T> V register(int id, ResourceLocation key, V value) {
      Validate.isTrue(id >= 0, "Invalid ID, can not be < 0", new Object[0]);
      Validate.notNull(key);
      Validate.notNull(value);
      T old = this.map.get(key);
      if (old != null) {
         LOGGER.debug(REGISTRY, "{}: Adding duplicate key '{}' to registry. Old: {} New: {}", this.name, key, old, value);
         this.values.remove(old);
         if (this.isDelegated) {
            Set<T> others = (Set)this.known.computeIfAbsent(key, (k) -> {
               return new HashSet();
            });
            others.add(old);
            others.forEach((e) -> {
               this.getDelegate(e).changeReference(value);
            });
         }
      }

      this.map.put(key, value);
      this.ids.put(value, id);
      this.values.add(value);
      if (this.nextId <= id) {
         this.nextId = id + 1;
      }

      if (this.isDelegated) {
         this.getDelegate(value).setName(key);
      }

      return value;
   }

   private RegistryDelegate<T> getDelegate(T thing) {
      if (this.isDelegated) {
         return (RegistryDelegate)((ForgeRegistryEntry)thing).delegate;
      } else {
         throw new IllegalStateException("Tried to get existing delegate from registry that is not delegated.");
      }
   }

   public <V extends T> V register(ResourceLocation key, V value) {
      return this.register(this.nextId, key, value);
   }

   public Set<ResourceLocation> keySet() {
      return this.keys;
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   @Nullable
   public T getRandom(Random random) {
      return this.values.isEmpty() ? null : this.values.get(random.nextInt(this.values.size()));
   }

   public boolean containsKey(ResourceLocation key) {
      return this.map.containsKey(key);
   }

   public void clear() {
      LOGGER.debug(REGISTRY, "{}: Clearing registry", this.name);
      if (this.isDelegated) {
         this.known.values().forEach((s) -> {
            s.forEach((e) -> {
               this.getDelegate(e).changeReference(e);
            });
            s.clear();
         });
         this.known.clear();
      }

      this.map.clear();
      this.values.clear();
      this.ids.clear();
      this.nextId = 0;
   }

   public int getNextId() {
      return this.nextId;
   }

   public Optional<T> getValue(ResourceLocation key) {
      return Optional.ofNullable(this.map.get(key));
   }
}
