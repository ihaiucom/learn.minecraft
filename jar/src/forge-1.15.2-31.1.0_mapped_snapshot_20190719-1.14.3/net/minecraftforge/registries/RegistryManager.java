package net.minecraftforge.registries;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.FMLHandshakeMessages;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryManager {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final RegistryManager ACTIVE = new RegistryManager("ACTIVE");
   public static final RegistryManager VANILLA = new RegistryManager("VANILLA");
   public static final RegistryManager FROZEN = new RegistryManager("FROZEN");
   BiMap<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> registries = HashBiMap.create();
   private BiMap<Class<? extends IForgeRegistryEntry<?>>, ResourceLocation> superTypes = HashBiMap.create();
   private Set<ResourceLocation> persisted = Sets.newHashSet();
   private Set<ResourceLocation> synced = Sets.newHashSet();
   private Map<ResourceLocation, ResourceLocation> legacyNames = new HashMap();
   private final String name;

   public RegistryManager(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public <V extends IForgeRegistryEntry<V>> Class<V> getSuperType(ResourceLocation key) {
      return (Class)this.superTypes.inverse().get(key);
   }

   public <V extends IForgeRegistryEntry<V>> ForgeRegistry<V> getRegistry(ResourceLocation key) {
      return (ForgeRegistry)this.registries.get(key);
   }

   public <V extends IForgeRegistryEntry<V>> IForgeRegistry<V> getRegistry(Class<? super V> cls) {
      return this.getRegistry((ResourceLocation)this.superTypes.get(cls));
   }

   public <V extends IForgeRegistryEntry<V>> ResourceLocation getName(IForgeRegistry<V> reg) {
      return (ResourceLocation)this.registries.inverse().get(reg);
   }

   public <V extends IForgeRegistryEntry<V>> ResourceLocation updateLegacyName(ResourceLocation legacyName) {
      ResourceLocation originalName = legacyName;

      do {
         if (this.getRegistry(legacyName) != null) {
            return legacyName;
         }

         legacyName = (ResourceLocation)this.legacyNames.get(legacyName);
      } while(legacyName != null);

      return originalName;
   }

   public <V extends IForgeRegistryEntry<V>> ForgeRegistry<V> getRegistry(ResourceLocation key, RegistryManager other) {
      if (!this.registries.containsKey(key)) {
         ForgeRegistry<V> ot = other.getRegistry(key);
         if (ot == null) {
            return null;
         }

         this.registries.put(key, ot.copy(this));
         this.superTypes.put(ot.getRegistrySuperType(), key);
         if (other.persisted.contains(key)) {
            this.persisted.add(key);
         }

         if (other.synced.contains(key)) {
            this.synced.add(key);
         }

         other.legacyNames.entrySet().stream().filter((e) -> {
            return ((ResourceLocation)e.getValue()).equals(key);
         }).forEach((e) -> {
            this.addLegacyName((ResourceLocation)e.getKey(), (ResourceLocation)e.getValue());
         });
      }

      return this.getRegistry(key);
   }

   <V extends IForgeRegistryEntry<V>> ForgeRegistry<V> createRegistry(ResourceLocation name, RegistryBuilder<V> builder) {
      Set<Class<?>> parents = Sets.newHashSet();
      this.findSuperTypes(builder.getType(), parents);
      SetView<Class<?>> overlappedTypes = Sets.intersection(parents, this.superTypes.keySet());
      if (!overlappedTypes.isEmpty()) {
         Class<?> foundType = (Class)overlappedTypes.iterator().next();
         LOGGER.error("Found existing registry of type {} named {}, you cannot create a new registry ({}) with type {}, as {} has a parent of that type", foundType, this.superTypes.get(foundType), name, builder.getType(), builder.getType());
         throw new IllegalArgumentException("Duplicate registry parent type found - you can only have one registry for a particular super type");
      } else {
         ForgeRegistry<V> reg = new ForgeRegistry(this, name, builder);
         this.registries.put(name, reg);
         this.superTypes.put(builder.getType(), name);
         if (builder.getSaveToDisc()) {
            this.persisted.add(name);
         }

         if (builder.getSync()) {
            this.synced.add(name);
         }

         Iterator var6 = builder.getLegacyNames().iterator();

         while(var6.hasNext()) {
            ResourceLocation legacyName = (ResourceLocation)var6.next();
            this.addLegacyName(legacyName, name);
         }

         return this.getRegistry(name);
      }
   }

   private void addLegacyName(ResourceLocation legacyName, ResourceLocation name) {
      if (this.legacyNames.containsKey(legacyName)) {
         throw new IllegalArgumentException("Legacy name conflict for registry " + name + ", upgrade path must be linear: " + legacyName);
      } else {
         this.legacyNames.put(legacyName, name);
      }
   }

   private void findSuperTypes(Class<?> type, Set<Class<?>> types) {
      if (type != null && type != Object.class) {
         types.add(type);
         Class[] var3 = type.getInterfaces();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Class<?> interfac = var3[var5];
            this.findSuperTypes(interfac, types);
         }

         this.findSuperTypes(type.getSuperclass(), types);
      }
   }

   public Map<ResourceLocation, ForgeRegistry.Snapshot> takeSnapshot(boolean savingToDisc) {
      Map<ResourceLocation, ForgeRegistry.Snapshot> ret = Maps.newHashMap();
      Set<ResourceLocation> keys = savingToDisc ? this.persisted : this.synced;
      keys.forEach((name) -> {
         ForgeRegistry.Snapshot var10000 = (ForgeRegistry.Snapshot)ret.put(name, this.getRegistry(name).makeSnapshot());
      });
      return ret;
   }

   public void clean() {
      this.persisted.clear();
      this.synced.clear();
      this.registries.clear();
      this.superTypes.clear();
   }

   public static List<Pair<String, FMLHandshakeMessages.S2CRegistry>> generateRegistryPackets(boolean isLocal) {
      return !isLocal ? (List)ACTIVE.takeSnapshot(false).entrySet().stream().map((e) -> {
         return Pair.of("Registry " + e.getKey(), new FMLHandshakeMessages.S2CRegistry((ResourceLocation)e.getKey(), (ForgeRegistry.Snapshot)e.getValue()));
      }).collect(Collectors.toList()) : Collections.emptyList();
   }

   public static List<ResourceLocation> getRegistryNamesForSyncToClient() {
      return (List)ACTIVE.registries.keySet().stream().filter((resloc) -> {
         return ACTIVE.synced.contains(resloc);
      }).collect(Collectors.toList());
   }
}
