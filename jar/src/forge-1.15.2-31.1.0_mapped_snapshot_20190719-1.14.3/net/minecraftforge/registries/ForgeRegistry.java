package net.minecraftforge.registries;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import io.netty.buffer.Unpooled;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.loading.AdvancedLogMessageAdapter;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ForgeRegistry<V extends IForgeRegistryEntry<V>> implements IForgeRegistryInternal<V>, IForgeRegistryModifiable<V> {
   public static Marker REGISTRIES = MarkerManager.getMarker("REGISTRIES");
   private static Marker REGISTRYDUMP = MarkerManager.getMarker("REGISTRYDUMP");
   private static Logger LOGGER = LogManager.getLogger();
   private final RegistryManager stage;
   private final BiMap<Integer, V> ids = HashBiMap.create();
   private final BiMap<ResourceLocation, V> names = HashBiMap.create();
   private final Class<V> superType;
   private final Map<ResourceLocation, ResourceLocation> aliases = Maps.newHashMap();
   final Map<ResourceLocation, ?> slaves = Maps.newHashMap();
   private final ResourceLocation defaultKey;
   private final IForgeRegistry.CreateCallback<V> create;
   private final IForgeRegistry.AddCallback<V> add;
   private final IForgeRegistry.ClearCallback<V> clear;
   private final IForgeRegistry.ValidateCallback<V> validate;
   private final IForgeRegistry.BakeCallback<V> bake;
   private final IForgeRegistry.MissingFactory<V> missing;
   private final BitSet availabilityMap;
   private final Set<ResourceLocation> dummies = Sets.newHashSet();
   private final Set<Integer> blocked = Sets.newHashSet();
   private final Multimap<ResourceLocation, V> overrides = ArrayListMultimap.create();
   private final BiMap<ForgeRegistry.OverrideOwner, V> owners = HashBiMap.create();
   private final IForgeRegistry.DummyFactory<V> dummyFactory;
   private final boolean isDelegated;
   private final int min;
   private final int max;
   private final boolean allowOverrides;
   private final boolean isModifiable;
   private V defaultValue = null;
   boolean isFrozen = false;
   private final ResourceLocation name;
   private final RegistryBuilder<V> builder;

   ForgeRegistry(RegistryManager stage, ResourceLocation name, RegistryBuilder<V> builder) {
      this.name = name;
      this.builder = builder;
      this.stage = stage;
      this.superType = builder.getType();
      this.defaultKey = builder.getDefault();
      this.min = builder.getMinId();
      this.max = builder.getMaxId();
      this.availabilityMap = new BitSet(Math.min(this.max + 1, 4095));
      this.create = builder.getCreate();
      this.add = builder.getAdd();
      this.clear = builder.getClear();
      this.validate = builder.getValidate();
      this.bake = builder.getBake();
      this.missing = builder.getMissingFactory();
      this.dummyFactory = builder.getDummyFactory();
      this.isDelegated = ForgeRegistryEntry.class.isAssignableFrom(this.superType);
      this.allowOverrides = builder.getAllowOverrides();
      this.isModifiable = builder.getAllowModifications();
      if (this.create != null) {
         this.create.onCreate(this, stage);
      }

   }

   public void register(V value) {
      this.add(-1, value);
   }

   public Iterator<V> iterator() {
      return new Iterator<V>() {
         int cur = -1;
         V next = null;

         {
            this.next();
         }

         public boolean hasNext() {
            return this.next != null;
         }

         public V next() {
            IForgeRegistryEntry ret = this.next;

            do {
               this.cur = ForgeRegistry.this.availabilityMap.nextSetBit(this.cur + 1);
               this.next = (IForgeRegistryEntry)ForgeRegistry.this.ids.get(this.cur);
            } while(this.next == null && this.cur != -1);

            return ret;
         }
      };
   }

   public ResourceLocation getRegistryName() {
      return this.name;
   }

   public Class<V> getRegistrySuperType() {
      return this.superType;
   }

   public void registerAll(V... values) {
      IForgeRegistryEntry[] var2 = values;
      int var3 = values.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         V value = var2[var4];
         this.register(value);
      }

   }

   public boolean containsKey(ResourceLocation key) {
      while(key != null) {
         if (this.names.containsKey(key)) {
            return true;
         }

         key = (ResourceLocation)this.aliases.get(key);
      }

      return false;
   }

   public boolean containsValue(V value) {
      return this.names.containsValue(value);
   }

   public boolean isEmpty() {
      return this.names.isEmpty();
   }

   public V getValue(ResourceLocation key) {
      V ret = (IForgeRegistryEntry)this.names.get(key);

      for(key = (ResourceLocation)this.aliases.get(key); ret == null && key != null; key = (ResourceLocation)this.aliases.get(key)) {
         ret = (IForgeRegistryEntry)this.names.get(key);
      }

      return ret == null ? this.defaultValue : ret;
   }

   public ResourceLocation getKey(V value) {
      ResourceLocation ret = (ResourceLocation)this.names.inverse().get(value);
      return ret == null ? this.defaultKey : ret;
   }

   public Set<ResourceLocation> getKeys() {
      return Collections.unmodifiableSet(this.names.keySet());
   }

   @Nonnull
   public Collection<V> getValues() {
      return Collections.unmodifiableSet(this.names.values());
   }

   public Set<Entry<ResourceLocation, V>> getEntries() {
      return Collections.unmodifiableSet(this.names.entrySet());
   }

   public <T> T getSlaveMap(ResourceLocation name, Class<T> type) {
      return this.slaves.get(name);
   }

   public void setSlaveMap(ResourceLocation name, Object obj) {
      this.slaves.put(name, obj);
   }

   public int getID(V value) {
      Integer ret = (Integer)this.ids.inverse().get(value);
      if (ret == null && this.defaultValue != null) {
         ret = (Integer)this.ids.inverse().get(this.defaultValue);
      }

      return ret == null ? -1 : ret;
   }

   public int getID(ResourceLocation name) {
      return this.getID((IForgeRegistryEntry)this.names.get(name));
   }

   private int getIDRaw(V value) {
      Integer ret = (Integer)this.ids.inverse().get(value);
      return ret == null ? -1 : ret;
   }

   private int getIDRaw(ResourceLocation name) {
      return this.getIDRaw((IForgeRegistryEntry)this.names.get(name));
   }

   public V getValue(int id) {
      V ret = (IForgeRegistryEntry)this.ids.get(id);
      return ret == null ? this.defaultValue : ret;
   }

   void validateKey() {
      if (this.defaultKey != null) {
         Validate.notNull(this.defaultValue, "Missing default of ForgeRegistry: " + this.defaultKey + " Type: " + this.superType, new Object[0]);
      }

   }

   @Nullable
   public ResourceLocation getDefaultKey() {
      return this.defaultKey;
   }

   ForgeRegistry<V> copy(RegistryManager stage) {
      return new ForgeRegistry(stage, this.name, this.builder);
   }

   int add(int id, V value) {
      String owner = ModLoadingContext.get().getActiveNamespace();
      return this.add(id, value, owner);
   }

   int add(int id, V value, String owner) {
      ResourceLocation key = value == null ? null : value.getRegistryName();
      Preconditions.checkNotNull(key, "Can't use a null-name for the registry, object %s.", value);
      Preconditions.checkNotNull(value, "Can't add null-object to the registry, name %s.", key);
      int idToUse = id;
      if (id < 0 || this.availabilityMap.get(id)) {
         idToUse = this.availabilityMap.nextClearBit(this.min);
      }

      if (idToUse > this.max) {
         throw new RuntimeException(String.format("Invalid id %d - maximum id range exceeded.", idToUse));
      } else {
         V oldEntry = this.getRaw(key);
         if (oldEntry == value) {
            LOGGER.warn(REGISTRIES, "Registry {}: The object {} has been registered twice for the same name {}.", this.superType.getSimpleName(), value, key);
            return this.getID(value);
         } else {
            if (oldEntry != null) {
               if (!this.allowOverrides) {
                  throw new IllegalArgumentException(String.format("The name %s has been registered twice, for %s and %s.", key, this.getRaw(key), value));
               }

               if (owner == null) {
                  throw new IllegalStateException(String.format("Could not determine owner for the override on %s. Value: %s", key, value));
               }

               LOGGER.debug(REGISTRIES, "Registry {} Override: {} {} -> {}", this.superType.getSimpleName(), key, oldEntry, value);
               idToUse = this.getID(oldEntry);
            }

            Integer foundId = (Integer)this.ids.inverse().get(value);
            if (foundId != null) {
               V otherThing = (IForgeRegistryEntry)this.ids.get(foundId);
               throw new IllegalArgumentException(String.format("The object %s{%x} has been registered twice, using the names %s and %s. (Other object at this id is %s{%x})", value, System.identityHashCode(value), this.getKey(value), key, otherThing, System.identityHashCode(otherThing)));
            } else if (this.isLocked()) {
               throw new IllegalStateException(String.format("The object %s (name %s) is being added too late.", value, key));
            } else {
               if (this.defaultKey != null && this.defaultKey.equals(key)) {
                  if (this.defaultValue != null) {
                     throw new IllegalStateException(String.format("Attemped to override already set default value. This is not allowed: The object %s (name %s)", value, key));
                  }

                  this.defaultValue = value;
               }

               this.names.put(key, value);
               this.ids.put(idToUse, value);
               this.availabilityMap.set(idToUse);
               this.owners.put(new ForgeRegistry.OverrideOwner(owner == null ? key.getPath() : owner, key), value);
               if (this.isDelegated) {
                  this.getDelegate(value).setName(key);
                  if (oldEntry != null) {
                     if (!this.overrides.get(key).contains(oldEntry)) {
                        this.overrides.put(key, oldEntry);
                     }

                     this.overrides.get(key).remove(value);
                     if (this.stage == RegistryManager.ACTIVE) {
                        this.getDelegate(oldEntry).changeReference(value);
                     }
                  }
               }

               if (this.add != null) {
                  this.add.onAdd(this, this.stage, idToUse, value, oldEntry);
               }

               if (this.dummies.remove(key)) {
                  LOGGER.debug(REGISTRIES, "Registry {} Dummy Remove: {}", this.superType.getSimpleName(), key);
               }

               LOGGER.trace(REGISTRIES, "Registry {} add: {} {} {} (req. id {})", this.superType.getSimpleName(), key, idToUse, value, id);
               return idToUse;
            }
         }
      }
   }

   public V getRaw(ResourceLocation key) {
      V ret = (IForgeRegistryEntry)this.names.get(key);

      for(key = (ResourceLocation)this.aliases.get(key); ret == null && key != null; key = (ResourceLocation)this.aliases.get(key)) {
         ret = (IForgeRegistryEntry)this.names.get(key);
      }

      return ret;
   }

   void addAlias(ResourceLocation from, ResourceLocation to) {
      if (this.isLocked()) {
         throw new IllegalStateException(String.format("Attempted to register the alias %s -> %s to late", from, to));
      } else {
         this.aliases.put(from, to);
         LOGGER.trace(REGISTRIES, "Registry {} alias: {} -> {}", this.superType.getSimpleName(), from, to);
      }
   }

   void addDummy(ResourceLocation key) {
      if (this.isLocked()) {
         throw new IllegalStateException(String.format("Attempted to register the dummy %s to late", key));
      } else {
         this.dummies.add(key);
         LOGGER.trace(REGISTRIES, "Registry {} dummy: {}", this.superType.getSimpleName(), key);
      }
   }

   private RegistryDelegate<V> getDelegate(V thing) {
      if (this.isDelegated) {
         return (RegistryDelegate)((ForgeRegistryEntry)thing).delegate;
      } else {
         throw new IllegalStateException("Tried to get existing delegate from registry that is not delegated.");
      }
   }

   void resetDelegates() {
      if (this.isDelegated) {
         Iterator var1 = this.iterator();

         IForgeRegistryEntry value;
         while(var1.hasNext()) {
            value = (IForgeRegistryEntry)var1.next();
            this.getDelegate(value).changeReference(value);
         }

         var1 = this.overrides.values().iterator();

         while(var1.hasNext()) {
            value = (IForgeRegistryEntry)var1.next();
            this.getDelegate(value).changeReference(value);
         }

      }
   }

   V getDefault() {
      return this.defaultValue;
   }

   boolean isDummied(ResourceLocation key) {
      return this.dummies.contains(key);
   }

   void validateContent(ResourceLocation registryName) {
      try {
         ObfuscationReflectionHelper.findMethod(BitSet.class, "trimToSize").invoke(this.availabilityMap);
      } catch (Exception var6) {
      }

      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         V obj = (IForgeRegistryEntry)var2.next();
         int id = this.getID(obj);
         ResourceLocation name = this.getKey(obj);
         if (name == null) {
            throw new IllegalStateException(String.format("Registry entry for %s %s, id %d, doesn't yield a name.", registryName, obj, id));
         }

         if (id > this.max) {
            throw new IllegalStateException(String.format("Registry entry for %s %s, name %s uses the too large id %d.", registryName, obj, name, id));
         }

         if (this.getValue(id) != obj) {
            throw new IllegalStateException(String.format("Registry entry for id %d, name %s, doesn't yield the expected %s %s.", id, name, registryName, obj));
         }

         if (this.getValue(name) != obj) {
            throw new IllegalStateException(String.format("Registry entry for name %s, id %d, doesn't yield the expected %s %s.", name, id, registryName, obj));
         }

         if (this.getID(name) != id) {
            throw new IllegalStateException(String.format("Registry entry for name %s doesn't yield the expected id %d.", name, id));
         }

         if (this.validate != null) {
            this.validate.onValidate(this, this.stage, id, name, obj);
         }
      }

   }

   public void bake() {
      if (this.bake != null) {
         this.bake.onBake(this, this.stage);
      }

   }

   void sync(ResourceLocation name, ForgeRegistry<V> from) {
      LOGGER.debug(REGISTRIES, "Registry {} Sync: {} -> {}", this.superType.getSimpleName(), this.stage.getName(), from.stage.getName());
      if (this == from) {
         throw new IllegalArgumentException("WTF We are the same!?!?!");
      } else if (from.superType != this.superType) {
         throw new IllegalArgumentException("Attempted to copy to incompatible registry: " + name + " " + from.superType + " -> " + this.superType);
      } else {
         this.isFrozen = false;
         if (this.clear != null) {
            this.clear.onClear(this, this.stage);
         }

         this.aliases.clear();
         from.aliases.forEach(this::addAlias);
         this.ids.clear();
         this.names.clear();
         this.availabilityMap.clear(0, this.availabilityMap.length());
         this.defaultValue = null;
         this.overrides.clear();
         this.owners.clear();
         boolean errored = false;
         Iterator var4 = from.names.entrySet().iterator();

         while(true) {
            while(var4.hasNext()) {
               Entry<ResourceLocation, V> entry = (Entry)var4.next();
               List<V> overrides = Lists.newArrayList(from.overrides.get(entry.getKey()));
               int id = from.getID((ResourceLocation)entry.getKey());
               if (overrides.isEmpty()) {
                  int realId = this.add(id, (IForgeRegistryEntry)entry.getValue());
                  if (id != realId && id != -1) {
                     LOGGER.warn(REGISTRIES, "Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", this.superType.getSimpleName(), entry.getKey(), id, realId);
                     errored = true;
                  }
               } else {
                  overrides.add(entry.getValue());
                  Iterator var8 = overrides.iterator();

                  while(var8.hasNext()) {
                     V value = (IForgeRegistryEntry)var8.next();
                     ForgeRegistry.OverrideOwner owner = (ForgeRegistry.OverrideOwner)from.owners.inverse().get(value);
                     if (owner == null) {
                        LOGGER.warn(REGISTRIES, "Registry {}: Override did not have an associated owner object. Name: {} Value: {}", this.superType.getSimpleName(), entry.getKey(), value);
                        errored = true;
                     } else {
                        int realId = this.add(id, value, owner.owner);
                        if (id != realId && id != -1) {
                           LOGGER.warn(REGISTRIES, "Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", this.superType.getSimpleName(), entry.getKey(), id, realId);
                           errored = true;
                        }
                     }
                  }
               }
            }

            this.dummies.clear();
            from.dummies.forEach(this::addDummy);
            if (errored) {
               throw new RuntimeException("One of more entry values did not copy to the correct id. Check log for details!");
            }

            return;
         }
      }
   }

   public void clear() {
      if (!this.isModifiable) {
         throw new UnsupportedOperationException("Attempted to clear a non-modifiable Forge Registry");
      } else if (this.isLocked()) {
         throw new IllegalStateException("Attempted to clear the registry to late.");
      } else {
         if (this.clear != null) {
            this.clear.onClear(this, this.stage);
         }

         this.aliases.clear();
         this.dummies.clear();
         this.ids.clear();
         this.names.clear();
         this.availabilityMap.clear(0, this.availabilityMap.length());
      }
   }

   public V remove(ResourceLocation key) {
      if (!this.isModifiable) {
         throw new UnsupportedOperationException("Attempted to remove from a non-modifiable Forge Registry");
      } else if (this.isLocked()) {
         throw new IllegalStateException("Attempted to remove from the registry to late.");
      } else {
         V value = (IForgeRegistryEntry)this.names.remove(key);
         if (value != null) {
            Integer id = (Integer)this.ids.inverse().remove(value);
            if (id == null) {
               throw new IllegalStateException("Removed a entry that did not have an associated id: " + key + " " + value.toString() + " This should never happen unless hackery!");
            }

            LOGGER.trace(REGISTRIES, "Registry {} remove: {} {}", this.superType.getSimpleName(), key, id);
         }

         return value;
      }
   }

   void block(int id) {
      this.blocked.add(id);
      this.availabilityMap.set(id);
   }

   public boolean isLocked() {
      return this.isFrozen;
   }

   public void freeze() {
      this.isFrozen = true;
   }

   public void unfreeze() {
      this.isFrozen = false;
   }

   RegistryEvent.Register<V> getRegisterEvent(ResourceLocation name) {
      return new RegistryEvent.Register(name, this);
   }

   void dump(ResourceLocation name) {
      LOGGER.debug(REGISTRYDUMP, () -> {
         return new AdvancedLogMessageAdapter((sb) -> {
            sb.append("Registry Name: ").append(name).append('\n');
            this.getKeys().stream().map(this::getID).sorted().forEach((id) -> {
               sb.append("\tEntry: ").append(id).append(", ").append(this.getKey(this.getValue(id))).append(", ").append(this.getValue(id)).append('\n');
            });
         });
      });
   }

   public void loadIds(Map<ResourceLocation, Integer> ids, Map<ResourceLocation, String> overrides, Map<ResourceLocation, Integer> missing, Map<ResourceLocation, Integer[]> remapped, ForgeRegistry<V> old, ResourceLocation name) {
      Map<ResourceLocation, String> ovs = Maps.newHashMap(overrides);
      Iterator var8 = ids.entrySet().iterator();

      while(true) {
         Entry entry;
         ResourceLocation itemName;
         IForgeRegistryEntry obj;
         while(var8.hasNext()) {
            entry = (Entry)var8.next();
            itemName = (ResourceLocation)entry.getKey();
            int newId = (Integer)entry.getValue();
            int currId = old.getIDRaw(itemName);
            if (currId == -1) {
               LOGGER.info(REGISTRIES, "Registry {}: Found a missing id from the world {}", this.superType.getSimpleName(), itemName);
               missing.put(itemName, newId);
            } else {
               if (currId != newId) {
                  LOGGER.debug(REGISTRIES, "Registry {}: Fixed {} id mismatch {}: {} (init) -> {} (map).", this.superType.getSimpleName(), name, itemName, currId, newId);
                  remapped.put(itemName, new Integer[]{currId, newId});
               }

               obj = old.getRaw(itemName);
               Preconditions.checkState(obj != null, "objectKey has an ID but no object. Reflection/ASM hackery? Registry bug?");
               List<V> lst = Lists.newArrayList(old.overrides.get(itemName));
               String primaryName = null;
               if (old.overrides.containsKey(itemName)) {
                  if (!overrides.containsKey(itemName)) {
                     lst.add(obj);
                     obj = (IForgeRegistryEntry)old.overrides.get(itemName).iterator().next();
                     primaryName = ((ForgeRegistry.OverrideOwner)old.owners.inverse().get(obj)).owner;
                  } else {
                     primaryName = (String)overrides.get(itemName);
                  }
               }

               Iterator var16 = lst.iterator();

               while(var16.hasNext()) {
                  V value = (IForgeRegistryEntry)var16.next();
                  ForgeRegistry.OverrideOwner owner = (ForgeRegistry.OverrideOwner)old.owners.inverse().get(value);
                  if (owner == null) {
                     LOGGER.warn(REGISTRIES, "Registry {}: Override did not have an associated owner object. Name: {} Value: {}", this.superType.getSimpleName(), entry.getKey(), value);
                  } else if (!primaryName.equals(owner.owner)) {
                     int realId = this.add(newId, value, owner.owner);
                     if (newId != realId) {
                        LOGGER.warn(REGISTRIES, "Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", this.superType.getSimpleName(), entry.getKey(), newId, realId);
                     }
                  }
               }

               int realId = this.add(newId, obj, primaryName == null ? itemName.getPath() : primaryName);
               if (realId != newId) {
                  LOGGER.warn(REGISTRIES, "Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", this.superType.getSimpleName(), entry.getKey(), newId, realId);
               }

               ovs.remove(itemName);
            }
         }

         var8 = ovs.entrySet().iterator();

         while(var8.hasNext()) {
            entry = (Entry)var8.next();
            itemName = (ResourceLocation)entry.getKey();
            String owner = (String)entry.getValue();
            String current = ((ForgeRegistry.OverrideOwner)this.owners.inverse().get(this.getRaw(itemName))).owner;
            if (!owner.equals(current)) {
               obj = (IForgeRegistryEntry)this.owners.get(new ForgeRegistry.OverrideOwner(owner, itemName));
               if (obj == null) {
                  LOGGER.warn(REGISTRIES, "Registry {}: Skipping override for {}, Unknown owner {}", this.superType.getSimpleName(), itemName, owner);
               } else {
                  LOGGER.info(REGISTRIES, "Registry {}: Activating override {} for {}", this.superType.getSimpleName(), owner, itemName);
                  int newId = this.getID(itemName);
                  int realId = this.add(newId, obj, owner);
                  if (newId != realId) {
                     LOGGER.warn(REGISTRIES, "Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", this.superType.getSimpleName(), entry.getKey(), newId, realId);
                  }
               }
            }
         }

         return;
      }
   }

   boolean markDummy(ResourceLocation key, int id) {
      if (this.dummyFactory == null) {
         return false;
      } else {
         V dummy = this.dummyFactory.createDummy(key);
         LOGGER.debug(REGISTRIES, "Registry Dummy Add: {} {} -> {}", key, id, dummy);
         this.availabilityMap.clear(id);
         if (this.containsKey(key)) {
            V value = (IForgeRegistryEntry)this.names.remove(key);
            if (value == null) {
               throw new IllegalStateException("ContainsKey for " + key + " was true, but removing by name returned no value.. This should never happen unless hackery!");
            }

            Integer oldid = (Integer)this.ids.inverse().remove(value);
            if (oldid == null) {
               throw new IllegalStateException("Removed a entry that did not have an associated id: " + key + " " + value.toString() + " This should never happen unless hackery!");
            }

            if (oldid != id) {
               LOGGER.debug(REGISTRIES, "Registry {}: Dummy ID mismatch {} {} -> {}", this.superType.getSimpleName(), key, oldid, id);
            }

            LOGGER.debug(REGISTRIES, "Registry {} remove: {} {}", this.superType.getSimpleName(), key, oldid);
         }

         int realId = this.add(id, dummy);
         if (realId != id) {
            LOGGER.warn(REGISTRIES, "Registry {}: Object did not get ID it asked for. Name: {} Expected: {} Got: {}", this.superType.getSimpleName(), key, id, realId);
         }

         this.dummies.add(key);
         return true;
      }
   }

   public ForgeRegistry.Snapshot makeSnapshot() {
      ForgeRegistry.Snapshot ret = new ForgeRegistry.Snapshot();
      this.ids.forEach((id, value) -> {
         Integer var10000 = (Integer)ret.ids.put(this.getKey(value), id);
      });
      ret.aliases.putAll(this.aliases);
      ret.blocked.addAll(this.blocked);
      ret.dummied.addAll(this.dummies);
      ret.overrides.putAll(this.getOverrideOwners());
      return ret;
   }

   Map<ResourceLocation, String> getOverrideOwners() {
      Map<ResourceLocation, String> ret = Maps.newHashMap();

      ResourceLocation key;
      ForgeRegistry.OverrideOwner owner;
      for(Iterator var2 = this.overrides.keySet().iterator(); var2.hasNext(); ret.put(key, owner.owner)) {
         key = (ResourceLocation)var2.next();
         V obj = (IForgeRegistryEntry)this.names.get(key);
         owner = (ForgeRegistry.OverrideOwner)this.owners.inverse().get(obj);
         if (owner == null) {
            LOGGER.debug(REGISTRIES, "Registry {} {}: Invalid override {} {}", this.superType.getSimpleName(), this.stage.getName(), key, obj);
         }
      }

      return ret;
   }

   public RegistryEvent.MissingMappings<?> getMissingEvent(ResourceLocation name, Map<ResourceLocation, Integer> map) {
      List<RegistryEvent.MissingMappings.Mapping<V>> lst = Lists.newArrayList();
      ForgeRegistry<V> pool = RegistryManager.ACTIVE.getRegistry(name);
      map.forEach((rl, id) -> {
         lst.add(new RegistryEvent.MissingMappings.Mapping(this, pool, rl, id));
      });
      return new RegistryEvent.MissingMappings(name, this, lst);
   }

   void processMissingEvent(ResourceLocation name, ForgeRegistry<V> pool, List<RegistryEvent.MissingMappings.Mapping<V>> mappings, Map<ResourceLocation, Integer> missing, Map<ResourceLocation, Integer[]> remaps, Collection<ResourceLocation> defaulted, Collection<ResourceLocation> failed, boolean injectNetworkDummies) {
      LOGGER.debug(REGISTRIES, "Processing missing event for {}:", name);
      int ignored = 0;
      Iterator var10 = mappings.iterator();

      while(var10.hasNext()) {
         RegistryEvent.MissingMappings.Mapping<V> remap = (RegistryEvent.MissingMappings.Mapping)var10.next();
         RegistryEvent.MissingMappings.Action action = remap.getAction();
         if (action == RegistryEvent.MissingMappings.Action.REMAP) {
            int currId = this.getID(remap.getTarget());
            ResourceLocation newName = pool.getKey(remap.getTarget());
            LOGGER.debug(REGISTRIES, "  Remapping {} -> {}.", remap.key, newName);
            missing.remove(remap.key);
            int realId = this.add(remap.id, remap.getTarget());
            if (realId != remap.id) {
               LOGGER.warn(REGISTRIES, "Registered object did not get ID it asked for. Name: {} Type: {} Expected: {} Got: {}", newName, this.getRegistrySuperType(), remap.id, realId);
            }

            this.addAlias(remap.key, newName);
            if (currId != realId) {
               LOGGER.info(REGISTRIES, "Fixed id mismatch {}: {} (init) -> {} (map).", newName, currId, realId);
               remaps.put(newName, new Integer[]{currId, realId});
            }
         } else {
            if (action == RegistryEvent.MissingMappings.Action.DEFAULT) {
               V m = this.missing == null ? null : this.missing.createMissing(remap.key, injectNetworkDummies);
               if (m == null) {
                  defaulted.add(remap.key);
               } else {
                  this.add(remap.id, m, remap.key.getPath());
               }
            } else if (action == RegistryEvent.MissingMappings.Action.IGNORE) {
               LOGGER.debug(REGISTRIES, "Ignoring {}", remap.key);
               ++ignored;
            } else if (action == RegistryEvent.MissingMappings.Action.FAIL) {
               LOGGER.debug(REGISTRIES, "Failing {}!", remap.key);
               failed.add(remap.key);
            } else if (action == RegistryEvent.MissingMappings.Action.WARN) {
               LOGGER.warn(REGISTRIES, "{} may cause world breakage!", remap.key);
            }

            this.block(remap.id);
         }
      }

      if (failed.isEmpty() && ignored > 0) {
         LOGGER.debug(REGISTRIES, "There were {} missing mappings that have been ignored", ignored);
      }

   }

   private static class OverrideOwner {
      final String owner;
      final ResourceLocation key;

      private OverrideOwner(String owner, ResourceLocation key) {
         this.owner = owner;
         this.key = key;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof ForgeRegistry.OverrideOwner)) {
            return false;
         } else {
            ForgeRegistry.OverrideOwner oo = (ForgeRegistry.OverrideOwner)o;
            return this.owner.equals(oo.owner) && this.key.equals(oo.key);
         }
      }

      public int hashCode() {
         return 31 * this.key.hashCode() + this.owner.hashCode();
      }

      // $FF: synthetic method
      OverrideOwner(String x0, ResourceLocation x1, Object x2) {
         this(x0, x1);
      }
   }

   public static class Snapshot {
      public final Map<ResourceLocation, Integer> ids = Maps.newTreeMap();
      public final Map<ResourceLocation, ResourceLocation> aliases = Maps.newTreeMap();
      public final Set<Integer> blocked = Sets.newTreeSet();
      public final Set<ResourceLocation> dummied = Sets.newTreeSet();
      public final Map<ResourceLocation, String> overrides = Maps.newTreeMap();
      private PacketBuffer binary = null;

      public CompoundNBT write() {
         CompoundNBT data = new CompoundNBT();
         ListNBT ids = new ListNBT();
         this.ids.entrySet().stream().forEach((e) -> {
            CompoundNBT tag = new CompoundNBT();
            tag.putString("K", ((ResourceLocation)e.getKey()).toString());
            tag.putInt("V", (Integer)e.getValue());
            ids.add(tag);
         });
         data.put("ids", ids);
         ListNBT aliases = new ListNBT();
         this.aliases.entrySet().stream().forEach((e) -> {
            CompoundNBT tag = new CompoundNBT();
            tag.putString("K", ((ResourceLocation)e.getKey()).toString());
            tag.putString("V", ((ResourceLocation)e.getKey()).toString());
            aliases.add(tag);
         });
         data.put("aliases", aliases);
         ListNBT overrides = new ListNBT();
         this.overrides.entrySet().stream().forEach((e) -> {
            CompoundNBT tag = new CompoundNBT();
            tag.putString("K", ((ResourceLocation)e.getKey()).toString());
            tag.putString("V", (String)e.getValue());
            overrides.add(tag);
         });
         data.put("overrides", overrides);
         int[] blocked = this.blocked.stream().mapToInt((x) -> {
            return x;
         }).sorted().toArray();
         data.putIntArray("blocked", blocked);
         ListNBT dummied = new ListNBT();
         this.dummied.stream().sorted().forEach((e) -> {
            dummied.add(StringNBT.func_229705_a_(e.toString()));
         });
         data.put("dummied", dummied);
         return data;
      }

      public static ForgeRegistry.Snapshot read(CompoundNBT nbt) {
         ForgeRegistry.Snapshot ret = new ForgeRegistry.Snapshot();
         if (nbt == null) {
            return ret;
         } else {
            ListNBT list = nbt.getList("ids", 10);
            list.forEach((e) -> {
               CompoundNBT comp = (CompoundNBT)e;
               ret.ids.put(new ResourceLocation(comp.getString("K")), comp.getInt("V"));
            });
            list = nbt.getList("aliases", 10);
            list.forEach((e) -> {
               CompoundNBT comp = (CompoundNBT)e;
               ret.aliases.put(new ResourceLocation(comp.getString("K")), new ResourceLocation(comp.getString("V")));
            });
            list = nbt.getList("overrides", 10);
            list.forEach((e) -> {
               CompoundNBT comp = (CompoundNBT)e;
               ret.overrides.put(new ResourceLocation(comp.getString("K")), comp.getString("V"));
            });
            int[] blocked = nbt.getIntArray("blocked");
            int[] var4 = blocked;
            int var5 = blocked.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               int i = var4[var6];
               ret.blocked.add(i);
            }

            list = nbt.getList("dummied", 8);
            list.forEach((e) -> {
               ret.dummied.add(new ResourceLocation(((StringNBT)e).getString()));
            });
            return ret;
         }
      }

      public synchronized PacketBuffer getPacketData() {
         if (this.binary == null) {
            PacketBuffer pkt = new PacketBuffer(Unpooled.buffer());
            pkt.writeVarInt(this.ids.size());
            this.ids.forEach((k, v) -> {
               pkt.writeResourceLocation(k);
               pkt.writeVarInt(v);
            });
            pkt.writeVarInt(this.aliases.size());
            this.aliases.forEach((k, v) -> {
               pkt.writeResourceLocation(k);
               pkt.writeResourceLocation(v);
            });
            pkt.writeVarInt(this.overrides.size());
            this.overrides.forEach((k, v) -> {
               pkt.writeResourceLocation(k);
               pkt.writeString(v, 256);
            });
            pkt.writeVarInt(this.blocked.size());
            this.blocked.forEach(pkt::writeVarInt);
            pkt.writeVarInt(this.dummied.size());
            this.dummied.forEach(pkt::writeResourceLocation);
            this.binary = pkt;
         }

         return new PacketBuffer(this.binary.slice());
      }

      public static ForgeRegistry.Snapshot read(PacketBuffer buff) {
         if (buff == null) {
            return new ForgeRegistry.Snapshot();
         } else {
            ForgeRegistry.Snapshot ret = new ForgeRegistry.Snapshot();
            int len = buff.readVarInt();

            int x;
            for(x = 0; x < len; ++x) {
               ret.ids.put(buff.readResourceLocation(), buff.readVarInt());
            }

            len = buff.readVarInt();

            for(x = 0; x < len; ++x) {
               ret.aliases.put(buff.readResourceLocation(), buff.readResourceLocation());
            }

            len = buff.readVarInt();

            for(x = 0; x < len; ++x) {
               ret.overrides.put(buff.readResourceLocation(), buff.readString(256));
            }

            len = buff.readVarInt();

            for(x = 0; x < len; ++x) {
               ret.blocked.add(buff.readVarInt());
            }

            len = buff.readVarInt();

            for(x = 0; x < len; ++x) {
               ret.dummied.add(buff.readResourceLocation());
            }

            return ret;
         }
      }
   }
}
