package net.minecraftforge.registries;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.UnmodifiableIterator;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.StatType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.LifecycleEventProvider;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.StartupQuery;
import net.minecraftforge.fml.common.EnhancedRuntimeException;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.lifecycle.FMLModIdMappingEvent;
import net.minecraftforge.fml.loading.AdvancedLogMessageAdapter;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameData {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ResourceLocation BLOCKS = new ResourceLocation("block");
   public static final ResourceLocation FLUIDS = new ResourceLocation("fluid");
   public static final ResourceLocation ITEMS = new ResourceLocation("item");
   public static final ResourceLocation POTIONS = new ResourceLocation("mob_effect");
   public static final ResourceLocation BIOMES = new ResourceLocation("biome");
   public static final ResourceLocation SOUNDEVENTS = new ResourceLocation("sound_event");
   public static final ResourceLocation POTIONTYPES = new ResourceLocation("potion");
   public static final ResourceLocation ENCHANTMENTS = new ResourceLocation("enchantment");
   public static final ResourceLocation ENTITIES = new ResourceLocation("entity_type");
   public static final ResourceLocation TILEENTITIES = new ResourceLocation("block_entity_type");
   public static final ResourceLocation PARTICLE_TYPES = new ResourceLocation("particle_type");
   public static final ResourceLocation CONTAINERS = new ResourceLocation("menu");
   public static final ResourceLocation PAINTING_TYPES = new ResourceLocation("motive");
   public static final ResourceLocation RECIPE_SERIALIZERS = new ResourceLocation("recipe_serializer");
   public static final ResourceLocation STAT_TYPES = new ResourceLocation("stat_type");
   public static final ResourceLocation PROFESSIONS = new ResourceLocation("villager_profession");
   public static final ResourceLocation POI_TYPES = new ResourceLocation("point_of_interest_type");
   public static final ResourceLocation MEMORY_MODULE_TYPES = new ResourceLocation("memory_module_type");
   public static final ResourceLocation SENSOR_TYPES = new ResourceLocation("sensor_type");
   public static final ResourceLocation SCHEDULES = new ResourceLocation("schedule");
   public static final ResourceLocation ACTIVITIES = new ResourceLocation("activities");
   public static final ResourceLocation WORLD_CARVERS = new ResourceLocation("carver");
   public static final ResourceLocation SURFACE_BUILDERS = new ResourceLocation("surface_builder");
   public static final ResourceLocation FEATURES = new ResourceLocation("feature");
   public static final ResourceLocation DECORATORS = new ResourceLocation("decorator");
   public static final ResourceLocation BIOME_PROVIDER_TYPES = new ResourceLocation("biome_source_type");
   public static final ResourceLocation CHUNK_GENERATOR_TYPES = new ResourceLocation("chunk_generator_type");
   public static final ResourceLocation CHUNK_STATUS = new ResourceLocation("chunk_status");
   public static final ResourceLocation MODDIMENSIONS = new ResourceLocation("forge:moddimensions");
   public static final ResourceLocation SERIALIZERS = new ResourceLocation("minecraft:dataserializers");
   private static final int MAX_VARINT = 2147483646;
   private static final ResourceLocation BLOCK_TO_ITEM = new ResourceLocation("minecraft:blocktoitemmap");
   private static final ResourceLocation BLOCKSTATE_TO_ID = new ResourceLocation("minecraft:blockstatetoid");
   private static final ResourceLocation SERIALIZER_TO_ENTRY = new ResourceLocation("forge:serializer_to_entry");
   private static final ResourceLocation STRUCTURE_FEATURES = new ResourceLocation("minecraft:structure_feature");
   private static final ResourceLocation STRUCTURES = new ResourceLocation("minecraft:structures");
   private static boolean hasInit = false;
   private static final boolean DISABLE_VANILLA_REGISTRIES = Boolean.parseBoolean(System.getProperty("forge.disableVanillaGameData", "false"));
   private static final BiConsumer<ResourceLocation, ForgeRegistry<?>> LOCK_VANILLA = (name, reg) -> {
      reg.slaves.values().stream().filter((o) -> {
         return o instanceof ILockableRegistry;
      }).forEach((o) -> {
         ((ILockableRegistry)o).lock();
      });
   };
   private static Field regName;

   public static void init() {
      if (DISABLE_VANILLA_REGISTRIES) {
         LOGGER.warn(ForgeRegistry.REGISTRIES, "DISABLING VANILLA REGISTRY CREATION AS PER SYSTEM VARIABLE SETTING! forge.disableVanillaGameData");
      } else if (!hasInit) {
         hasInit = true;
         makeRegistry(BLOCKS, Block.class, new ResourceLocation("air")).addCallback(GameData.BlockCallbacks.INSTANCE).legacyName("blocks").create();
         makeRegistry(FLUIDS, Fluid.class, new ResourceLocation("empty")).create();
         makeRegistry(ITEMS, Item.class, new ResourceLocation("air")).addCallback(GameData.ItemCallbacks.INSTANCE).legacyName("items").create();
         makeRegistry(POTIONS, Effect.class).legacyName("potions").create();
         makeRegistry(BIOMES, Biome.class).legacyName("biomes").create();
         makeRegistry(SOUNDEVENTS, SoundEvent.class).legacyName("soundevents").create();
         makeRegistry(POTIONTYPES, Potion.class, new ResourceLocation("empty")).legacyName("potiontypes").create();
         makeRegistry(ENCHANTMENTS, Enchantment.class).legacyName("enchantments").create();
         makeRegistry(ENTITIES, EntityType.class, new ResourceLocation("pig")).legacyName("entities").create();
         makeRegistry(TILEENTITIES, TileEntityType.class).disableSaving().legacyName("tileentities").create();
         makeRegistry(PARTICLE_TYPES, ParticleType.class).disableSaving().create();
         makeRegistry(CONTAINERS, ContainerType.class).disableSaving().create();
         makeRegistry(PAINTING_TYPES, PaintingType.class, new ResourceLocation("kebab")).create();
         makeRegistry(RECIPE_SERIALIZERS, IRecipeSerializer.class).disableSaving().create();
         makeRegistry(STAT_TYPES, StatType.class).create();
         makeRegistry(PROFESSIONS, VillagerProfession.class, new ResourceLocation("none")).create();
         makeRegistry(POI_TYPES, PointOfInterestType.class, new ResourceLocation("unemployed")).disableSync().create();
         makeRegistry(MEMORY_MODULE_TYPES, MemoryModuleType.class, new ResourceLocation("dummy")).disableSync().create();
         makeRegistry(SENSOR_TYPES, SensorType.class, new ResourceLocation("dummy")).disableSaving().disableSync().create();
         makeRegistry(SCHEDULES, Schedule.class).disableSaving().disableSync().create();
         makeRegistry(ACTIVITIES, Activity.class).disableSaving().disableSync().create();
         makeRegistry(WORLD_CARVERS, WorldCarver.class).disableSaving().disableSync().create();
         makeRegistry(SURFACE_BUILDERS, SurfaceBuilder.class).disableSaving().disableSync().create();
         makeRegistry(FEATURES, Feature.class).addCallback(GameData.FeatureCallbacks.INSTANCE).disableSaving().create();
         makeRegistry(DECORATORS, Placement.class).disableSaving().disableSync().create();
         makeRegistry(BIOME_PROVIDER_TYPES, BiomeProviderType.class).disableSaving().disableSync().create();
         makeRegistry(CHUNK_GENERATOR_TYPES, ChunkGeneratorType.class).disableSaving().disableSync().create();
         makeRegistry(CHUNK_STATUS, ChunkStatus.class, new ResourceLocation("empty")).disableSaving().disableSync().create();
         makeRegistry(MODDIMENSIONS, ModDimension.class).disableSaving().create();
         makeRegistry(SERIALIZERS, DataSerializerEntry.class, 256, 2147483646).disableSaving().disableOverrides().addCallback(GameData.SerializerCallbacks.INSTANCE).create();
      }
   }

   private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(ResourceLocation name, Class<T> type) {
      return (new RegistryBuilder()).setName(name).setType(type).setMaxID(2147483646).addCallback(new NamespacedWrapper.Factory());
   }

   private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(ResourceLocation name, Class<T> type, int min, int max) {
      return (new RegistryBuilder()).setName(name).setType(type).setIDRange(min, max).addCallback(new NamespacedWrapper.Factory());
   }

   private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(ResourceLocation name, Class<T> type, ResourceLocation _default) {
      return (new RegistryBuilder()).setName(name).setType(type).setMaxID(2147483646).addCallback(new NamespacedDefaultedWrapper.Factory()).setDefaultKey(_default);
   }

   public static <V extends IForgeRegistryEntry<V>> DefaultedRegistry<V> getWrapperDefaulted(Class<? super V> cls) {
      IForgeRegistry<V> reg = RegistryManager.ACTIVE.getRegistry(cls);
      Validate.notNull(reg, "Attempted to get vanilla wrapper for unknown registry: " + cls.toString(), new Object[0]);
      DefaultedRegistry<V> ret = (DefaultedRegistry)reg.getSlaveMap(NamespacedDefaultedWrapper.Factory.ID, NamespacedDefaultedWrapper.class);
      Validate.notNull(ret, "Attempted to get vanilla wrapper for registry created incorrectly: " + cls.toString(), new Object[0]);
      return ret;
   }

   public static <V extends IForgeRegistryEntry<V>> SimpleRegistry<V> getWrapper(Class<? super V> cls) {
      IForgeRegistry<V> reg = RegistryManager.ACTIVE.getRegistry(cls);
      Validate.notNull(reg, "Attempted to get vanilla wrapper for unknown registry: " + cls.toString(), new Object[0]);
      SimpleRegistry<V> ret = (SimpleRegistry)reg.getSlaveMap(NamespacedWrapper.Factory.ID, NamespacedWrapper.class);
      Validate.notNull(ret, "Attempted to get vanilla wrapper for registry created incorrectly: " + cls.toString(), new Object[0]);
      return ret;
   }

   public static Map<Block, Item> getBlockItemMap() {
      return (Map)RegistryManager.ACTIVE.getRegistry(Item.class).getSlaveMap(BLOCK_TO_ITEM, Map.class);
   }

   public static ObjectIntIdentityMap<BlockState> getBlockStateIDMap() {
      return (ObjectIntIdentityMap)RegistryManager.ACTIVE.getRegistry(Block.class).getSlaveMap(BLOCKSTATE_TO_ID, ObjectIntIdentityMap.class);
   }

   public static Map<IDataSerializer<?>, DataSerializerEntry> getSerializerMap() {
      return (Map)RegistryManager.ACTIVE.getRegistry(DataSerializerEntry.class).getSlaveMap(SERIALIZER_TO_ENTRY, Map.class);
   }

   public static Registry<Structure<?>> getStructureFeatures() {
      return (Registry)RegistryManager.ACTIVE.getRegistry(Feature.class).getSlaveMap(STRUCTURE_FEATURES, Registry.class);
   }

   public static BiMap<String, Structure<?>> getStructureMap() {
      return (BiMap)RegistryManager.ACTIVE.getRegistry(Feature.class).getSlaveMap(STRUCTURES, BiMap.class);
   }

   public static <K extends IForgeRegistryEntry<K>> K register_impl(K value) {
      Validate.notNull(value, "Attempted to register a null object", new Object[0]);
      Validate.notNull(value.getRegistryName(), String.format("Attempt to register object without having set a registry name %s (type %s)", value, value.getClass().getName()), new Object[0]);
      IForgeRegistry<K> registry = RegistryManager.ACTIVE.getRegistry(value.getRegistryType());
      Validate.notNull(registry, "Attempted to registry object without creating registry first: " + value.getRegistryType().getName(), new Object[0]);
      registry.register(value);
      return value;
   }

   public static void vanillaSnapshot() {
      LOGGER.debug(ForgeRegistry.REGISTRIES, "Creating vanilla freeze snapshot");
      Iterator var0 = RegistryManager.ACTIVE.registries.entrySet().iterator();

      while(var0.hasNext()) {
         Entry<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> r = (Entry)var0.next();
         Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType((ResourceLocation)r.getKey());
         loadRegistry((ResourceLocation)r.getKey(), RegistryManager.ACTIVE, RegistryManager.VANILLA, clazz, true);
      }

      RegistryManager.VANILLA.registries.forEach((name, reg) -> {
         reg.validateContent(name);
         reg.freeze();
      });
      RegistryManager.VANILLA.registries.forEach(LOCK_VANILLA);
      RegistryManager.ACTIVE.registries.forEach(LOCK_VANILLA);
      LOGGER.debug(ForgeRegistry.REGISTRIES, "Vanilla freeze snapshot created");
   }

   public static void freezeData() {
      LOGGER.debug(ForgeRegistry.REGISTRIES, "Freezing registries");
      Iterator var0 = RegistryManager.ACTIVE.registries.entrySet().iterator();

      while(var0.hasNext()) {
         Entry<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> r = (Entry)var0.next();
         Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType((ResourceLocation)r.getKey());
         loadRegistry((ResourceLocation)r.getKey(), RegistryManager.ACTIVE, RegistryManager.FROZEN, clazz, true);
      }

      RegistryManager.FROZEN.registries.forEach((name, reg) -> {
         reg.validateContent(name);
         reg.freeze();
      });
      RegistryManager.ACTIVE.registries.forEach((name, reg) -> {
         reg.freeze();
         reg.bake();
         reg.dump(name);
      });
      fireRemapEvent(ImmutableMap.of(), true);
      LOGGER.debug(ForgeRegistry.REGISTRIES, "All registries frozen");
   }

   public static void revertToFrozen() {
      if (RegistryManager.FROZEN.registries.isEmpty()) {
         LOGGER.warn(ForgeRegistry.REGISTRIES, "Can't revert to frozen GameData state without freezing first.");
      } else {
         RegistryManager.ACTIVE.registries.forEach((name, reg) -> {
            reg.resetDelegates();
         });
         LOGGER.debug(ForgeRegistry.REGISTRIES, "Reverting to frozen data state.");
         Iterator var0 = RegistryManager.ACTIVE.registries.entrySet().iterator();

         while(var0.hasNext()) {
            Entry<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> r = (Entry)var0.next();
            Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType((ResourceLocation)r.getKey());
            loadRegistry((ResourceLocation)r.getKey(), RegistryManager.FROZEN, RegistryManager.ACTIVE, clazz, true);
         }

         RegistryManager.ACTIVE.registries.forEach((name, reg) -> {
            reg.bake();
         });
         fireRemapEvent(ImmutableMap.of(), true);
         ObjectHolderRegistry.applyObjectHolders();
         LOGGER.debug(ForgeRegistry.REGISTRIES, "Frozen state restored.");
      }
   }

   public static void revert(RegistryManager state, ResourceLocation registry, boolean lock) {
      LOGGER.debug(ForgeRegistry.REGISTRIES, "Reverting {} to {}", registry, state.getName());
      Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType(registry);
      loadRegistry(registry, state, RegistryManager.ACTIVE, clazz, lock);
      LOGGER.debug(ForgeRegistry.REGISTRIES, "Reverting complete");
   }

   private static <T extends IForgeRegistryEntry<T>> void loadRegistry(final ResourceLocation registryName, final RegistryManager from, final RegistryManager to, Class<T> regType, boolean freeze) {
      ForgeRegistry<T> fromRegistry = from.getRegistry(registryName);
      ForgeRegistry toRegistry;
      if (fromRegistry == null) {
         toRegistry = to.getRegistry(registryName);
         if (toRegistry == null) {
            throw new EnhancedRuntimeException("Could not find registry to load: " + registryName) {
               private static final long serialVersionUID = 1L;

               protected void printStackTrace(EnhancedRuntimeException.WrappedPrintStream stream) {
                  stream.println("Looking For: " + registryName);
                  stream.println("Found From:");
                  Iterator var2 = from.registries.keySet().iterator();

                  ResourceLocation name;
                  while(var2.hasNext()) {
                     name = (ResourceLocation)var2.next();
                     stream.println("  " + name);
                  }

                  stream.println("Found To:");
                  var2 = to.registries.keySet().iterator();

                  while(var2.hasNext()) {
                     name = (ResourceLocation)var2.next();
                     stream.println("  " + name);
                  }

               }
            };
         }
      } else {
         toRegistry = to.getRegistry(registryName, from);
         toRegistry.sync(registryName, fromRegistry);
         if (freeze) {
            toRegistry.isFrozen = true;
         }
      }

   }

   public static Multimap<ResourceLocation, ResourceLocation> injectSnapshot(Map<ResourceLocation, ForgeRegistry.Snapshot> snapshot, boolean injectFrozenData, boolean isLocalWorld) {
      LOGGER.info(ForgeRegistry.REGISTRIES, "Injecting existing registry data into this {} instance", EffectiveSide.get());
      RegistryManager.ACTIVE.registries.forEach((name, reg) -> {
         reg.validateContent(name);
      });
      RegistryManager.ACTIVE.registries.forEach((name, reg) -> {
         reg.dump(name);
      });
      RegistryManager.ACTIVE.registries.forEach((name, reg) -> {
         reg.resetDelegates();
      });
      snapshot = (Map)snapshot.entrySet().stream().sorted(Entry.comparingByKey()).collect(Collectors.toMap((e) -> {
         return RegistryManager.ACTIVE.updateLegacyName((ResourceLocation)e.getKey());
      }, Entry::getValue, (k1, k2) -> {
         return k1;
      }, LinkedHashMap::new));
      if (isLocalWorld) {
         List<ResourceLocation> missingRegs = (List)snapshot.keySet().stream().filter((name) -> {
            return !RegistryManager.ACTIVE.registries.containsKey(name);
         }).collect(Collectors.toList());
         if (missingRegs.size() > 0) {
            String text = "Forge Mod Loader detected missing/unknown registrie(s).\n\nThere are " + missingRegs.size() + " missing registries in this save.\nIf you continue the missing registries will get removed.\nThis may cause issues, it is advised that you create a world backup before continuing.\n\nMissing Registries:\n";

            ResourceLocation s;
            for(Iterator var5 = missingRegs.iterator(); var5.hasNext(); text = text + s.toString() + "\n") {
               s = (ResourceLocation)var5.next();
            }

            if (!StartupQuery.confirm(text)) {
               StartupQuery.abort();
            }
         }
      }

      RegistryManager STAGING = new RegistryManager("STAGING");
      Map<ResourceLocation, Map<ResourceLocation, Integer[]>> remaps = Maps.newHashMap();
      LinkedHashMap<ResourceLocation, Map<ResourceLocation, Integer>> missing = Maps.newLinkedHashMap();
      snapshot.forEach((key, value) -> {
         Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType(key);
         remaps.put(key, Maps.newLinkedHashMap());
         missing.put(key, Maps.newHashMap());
         loadPersistentDataToStagingRegistry(RegistryManager.ACTIVE, STAGING, (Map)remaps.get(key), (Map)missing.get(key), key, value, clazz);
      });
      snapshot.forEach((key, value) -> {
         value.dummied.forEach((dummy) -> {
            Map<ResourceLocation, Integer> m = (Map)missing.get(key);
            ForgeRegistry<?> reg = STAGING.getRegistry(key);
            if (m.containsKey(dummy)) {
               if (reg.markDummy(dummy, (Integer)m.get(dummy))) {
                  m.remove(dummy);
               }
            } else if (isLocalWorld) {
               LOGGER.debug(ForgeRegistry.REGISTRIES, "Registry {}: Resuscitating dummy entry {}", key, dummy);
            } else {
               int id = reg.getID(dummy);
               LOGGER.warn(ForgeRegistry.REGISTRIES, "Registry {}: The ID {} @ {} is currently locally mapped - it will be replaced with a dummy for this session", dummy, key, id);
               reg.markDummy(dummy, id);
            }

         });
      });
      int count = missing.values().stream().mapToInt(Map::size).sum();
      if (count > 0) {
         LOGGER.debug(ForgeRegistry.REGISTRIES, "There are {} mappings missing - attempting a mod remap", count);
         Multimap<ResourceLocation, ResourceLocation> defaulted = ArrayListMultimap.create();
         Multimap<ResourceLocation, ResourceLocation> failed = ArrayListMultimap.create();
         missing.entrySet().stream().filter((e) -> {
            return ((Map)e.getValue()).size() > 0;
         }).forEach((m) -> {
            ResourceLocation name = (ResourceLocation)m.getKey();
            ForgeRegistry<?> reg = STAGING.getRegistry(name);
            RegistryEvent.MissingMappings<?> event = reg.getMissingEvent(name, (Map)m.getValue());
            MinecraftForge.EVENT_BUS.post(event);
            List<RegistryEvent.MissingMappings.Mapping<?>> lst = (List)event.getAllMappings().stream().filter((e) -> {
               return e.getAction() == RegistryEvent.MissingMappings.Action.DEFAULT;
            }).sorted((a, b) -> {
               return a.toString().compareTo(b.toString());
            }).collect(Collectors.toList());
            if (!lst.isEmpty()) {
               LOGGER.error(ForgeRegistry.REGISTRIES, () -> {
                  return new AdvancedLogMessageAdapter((sb) -> {
                     sb.append("Unidentified mapping from registry ").append(name).append('\n');
                     lst.forEach((map) -> {
                        sb.append('\t').append(map.key).append(": ").append(map.id).append('\n');
                     });
                  });
               });
            }

            event.getAllMappings().stream().filter((e) -> {
               return e.getAction() == RegistryEvent.MissingMappings.Action.FAIL;
            }).forEach((fail) -> {
               failed.put(name, fail.key);
            });
            Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType(name);
            processMissing(clazz, name, STAGING, event, (Map)m.getValue(), (Map)remaps.get(name), defaulted.get(name), failed.get(name), !isLocalWorld);
         });
         if (!defaulted.isEmpty() && !isLocalWorld) {
            return defaulted;
         }

         if (!defaulted.isEmpty()) {
            StringBuilder buf = new StringBuilder();
            buf.append("Forge Mod Loader detected missing registry entries.\n\n").append("There are ").append(defaulted.size()).append(" missing entries in this save.\n").append("If you continue the missing entries will get removed.\n").append("A world backup will be automatically created in your saves directory.\n\n");
            defaulted.asMap().forEach((name, entries) -> {
               buf.append("Missing ").append(name).append(":\n");
               entries.forEach((rl) -> {
                  buf.append("    ").append(rl).append("\n");
               });
            });
            boolean confirmed = StartupQuery.confirm(buf.toString());
            if (!confirmed) {
               StartupQuery.abort();
            }
         }

         if (!defaulted.isEmpty() && isLocalWorld) {
            LOGGER.error(ForgeRegistry.REGISTRIES, "There are unidentified mappings in this world - we are going to attempt to process anyway");
         }
      }

      if (injectFrozenData) {
         missing.forEach((name, m) -> {
            ForgeRegistry<?> reg = STAGING.getRegistry(name);
            m.forEach((rl, id) -> {
               reg.markDummy(rl, id);
            });
         });
         RegistryManager.ACTIVE.registries.forEach((name, reg) -> {
            Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType(name);
            loadFrozenDataToStagingRegistry(STAGING, name, (Map)remaps.get(name), clazz);
         });
      }

      STAGING.registries.forEach((name, reg) -> {
         reg.validateContent(name);
      });
      RegistryManager.ACTIVE.registries.forEach((key, value) -> {
         Class<? extends IForgeRegistryEntry> registrySuperType = RegistryManager.ACTIVE.getSuperType(key);
         loadRegistry(key, STAGING, RegistryManager.ACTIVE, registrySuperType, true);
      });
      RegistryManager.ACTIVE.registries.forEach((name, reg) -> {
         reg.bake();
         reg.dump(name);
      });
      fireRemapEvent(remaps, false);
      ObjectHolderRegistry.applyObjectHolders();
      return ArrayListMultimap.create();
   }

   private static void fireRemapEvent(Map<ResourceLocation, Map<ResourceLocation, Integer[]>> remaps, boolean isFreezing) {
      MinecraftForge.EVENT_BUS.post(new FMLModIdMappingEvent(remaps, isFreezing));
   }

   private static <T extends IForgeRegistryEntry<T>> void loadPersistentDataToStagingRegistry(RegistryManager pool, RegistryManager to, Map<ResourceLocation, Integer[]> remaps, Map<ResourceLocation, Integer> missing, ResourceLocation name, ForgeRegistry.Snapshot snap, Class<T> regType) {
      ForgeRegistry<T> active = pool.getRegistry(name);
      if (active != null) {
         ForgeRegistry<T> _new = to.getRegistry(name, RegistryManager.ACTIVE);
         snap.aliases.forEach(_new::addAlias);
         snap.blocked.forEach(_new::block);
         snap.dummied.forEach(_new::addDummy);
         _new.loadIds(snap.ids, snap.overrides, missing, remaps, active, name);
      }
   }

   private static <T extends IForgeRegistryEntry<T>> void processMissing(Class<T> clazz, ResourceLocation name, RegistryManager STAGING, RegistryEvent.MissingMappings<?> e, Map<ResourceLocation, Integer> missing, Map<ResourceLocation, Integer[]> remaps, Collection<ResourceLocation> defaulted, Collection<ResourceLocation> failed, boolean injectNetworkDummies) {
      List<RegistryEvent.MissingMappings.Mapping<T>> mappings = e.getAllMappings();
      ForgeRegistry<T> active = RegistryManager.ACTIVE.getRegistry(name);
      ForgeRegistry<T> staging = STAGING.getRegistry(name);
      staging.processMissingEvent(name, active, mappings, missing, remaps, defaulted, failed, injectNetworkDummies);
   }

   private static <T extends IForgeRegistryEntry<T>> void loadFrozenDataToStagingRegistry(RegistryManager STAGING, ResourceLocation name, Map<ResourceLocation, Integer[]> remaps, Class<T> clazz) {
      ForgeRegistry<T> frozen = RegistryManager.FROZEN.getRegistry(name);
      ForgeRegistry<T> newRegistry = STAGING.getRegistry(name, RegistryManager.FROZEN);
      Map<ResourceLocation, Integer> _new = Maps.newHashMap();
      frozen.getKeys().stream().filter((key) -> {
         return !newRegistry.containsKey(key);
      }).forEach((key) -> {
         Integer var10000 = (Integer)_new.put(key, frozen.getID(key));
      });
      newRegistry.loadIds(_new, frozen.getOverrideOwners(), Maps.newLinkedHashMap(), remaps, frozen, name);
   }

   public static void fireCreateRegistryEvents() {
      MinecraftForge.EVENT_BUS.post(new RegistryEvent.NewRegistry());
   }

   public static void fireCreateRegistryEvents(LifecycleEventProvider lifecycleEventProvider, Consumer<LifecycleEventProvider> eventDispatcher) {
      RegistryEvent.NewRegistry newRegistryEvent = new RegistryEvent.NewRegistry();
      lifecycleEventProvider.setCustomEventSupplier(() -> {
         return newRegistryEvent;
      });
      eventDispatcher.accept(lifecycleEventProvider);
   }

   public static void fireRegistryEvents(Predicate<ResourceLocation> filter, LifecycleEventProvider lifecycleEventProvider, Consumer<LifecycleEventProvider> eventDispatcher) {
      List<ResourceLocation> keys = Lists.newArrayList(RegistryManager.ACTIVE.registries.keySet());
      keys.sort((o1, o2) -> {
         return String.valueOf(o1).compareToIgnoreCase(String.valueOf(o2));
      });
      keys.remove(BLOCKS);
      keys.remove(ITEMS);
      keys.add(0, BLOCKS);
      keys.add(1, ITEMS);
      int i = 0;

      for(int keysSize = keys.size(); i < keysSize; ++i) {
         ResourceLocation rl = (ResourceLocation)keys.get(i);
         if (filter.test(rl)) {
            ForgeRegistry<?> reg = RegistryManager.ACTIVE.getRegistry(rl);
            reg.unfreeze();
            RegistryEvent.Register<?> registerEvent = reg.getRegisterEvent(rl);
            lifecycleEventProvider.setCustomEventSupplier(() -> {
               return registerEvent;
            });
            lifecycleEventProvider.changeProgression(LifecycleEventProvider.LifecycleEvent.Progression.STAY);
            if (i == keysSize - 1) {
               lifecycleEventProvider.changeProgression(LifecycleEventProvider.LifecycleEvent.Progression.NEXT);
            }

            eventDispatcher.accept(lifecycleEventProvider);
            reg.freeze();
            LOGGER.debug(ForgeRegistry.REGISTRIES, "Applying holder lookups: {}", rl.toString());
            ObjectHolderRegistry.applyObjectHolders(rl::equals);
            LOGGER.debug(ForgeRegistry.REGISTRIES, "Holder lookups applied: {}", rl.toString());
         }
      }

   }

   public static ResourceLocation checkPrefix(String name, boolean warnOverrides) {
      int index = name.lastIndexOf(58);
      String oldPrefix = index == -1 ? "" : name.substring(0, index).toLowerCase(Locale.ROOT);
      name = index == -1 ? name : name.substring(index + 1);
      String prefix = ModLoadingContext.get().getActiveNamespace();
      if (warnOverrides && !oldPrefix.equals(prefix) && oldPrefix.length() > 0) {
         LogManager.getLogger().info("Potentially Dangerous alternative prefix `{}` for name `{}`, expected `{}`. This could be a intended override, but in most cases indicates a broken mod.", oldPrefix, name, prefix);
         prefix = oldPrefix;
      }

      return new ResourceLocation(prefix, name);
   }

   private static void forceRegistryName(IForgeRegistryEntry<?> entry, ResourceLocation name) {
      if (regName == null) {
         try {
            regName = ForgeRegistryEntry.class.getDeclaredField("registryName");
            regName.setAccessible(true);
         } catch (SecurityException | NoSuchFieldException var4) {
            LOGGER.error(ForgeRegistry.REGISTRIES, "Could not get `registryName` field from IForgeRegistryEntry.Impl", var4);
            throw new RuntimeException(var4);
         }
      }

      try {
         regName.set(entry, name);
      } catch (IllegalAccessException | IllegalArgumentException var3) {
         LOGGER.error(ForgeRegistry.REGISTRIES, "Could not set `registryName` field in IForgeRegistryEntry.Impl to `{}`", name.toString(), var3);
         throw new RuntimeException(var3);
      }
   }

   static {
      init();
   }

   private static class FeatureCallbacks implements IForgeRegistry.AddCallback<Feature<?>>, IForgeRegistry.ClearCallback<Feature<?>>, IForgeRegistry.CreateCallback<Feature<?>> {
      static final GameData.FeatureCallbacks INSTANCE = new GameData.FeatureCallbacks();

      public void onAdd(IForgeRegistryInternal<Feature<?>> owner, RegistryManager stage, int id, Feature<?> obj, Feature<?> oldObj) {
         if (obj instanceof Structure) {
            Structure<?> structure = (Structure)obj;
            String key = structure.getStructureName().toLowerCase(Locale.ROOT);
            Registry<Structure<?>> reg = (Registry)owner.getSlaveMap(GameData.STRUCTURE_FEATURES, Registry.class);
            Registry.register((Registry)reg, (String)key, (Object)structure);
            BiMap<String, Structure<?>> map = (BiMap)owner.getSlaveMap(GameData.STRUCTURES, BiMap.class);
            if (oldObj != null && oldObj instanceof Structure) {
               map.remove(((Structure)oldObj).getStructureName());
            }

            map.put(key, structure);
         }

      }

      public void onClear(IForgeRegistryInternal<Feature<?>> owner, RegistryManager stage) {
         ((ClearableRegistry)owner.getSlaveMap(GameData.STRUCTURE_FEATURES, ClearableRegistry.class)).clear();
         ((BiMap)owner.getSlaveMap(GameData.STRUCTURES, BiMap.class)).clear();
      }

      public void onCreate(IForgeRegistryInternal<Feature<?>> owner, RegistryManager stage) {
         owner.setSlaveMap(GameData.STRUCTURE_FEATURES, new ClearableRegistry(owner.getRegistryName()));
         owner.setSlaveMap(GameData.STRUCTURES, HashBiMap.create());
      }
   }

   private static class SerializerCallbacks implements IForgeRegistry.AddCallback<DataSerializerEntry>, IForgeRegistry.ClearCallback<DataSerializerEntry>, IForgeRegistry.CreateCallback<DataSerializerEntry> {
      static final GameData.SerializerCallbacks INSTANCE = new GameData.SerializerCallbacks();

      public void onAdd(IForgeRegistryInternal<DataSerializerEntry> owner, RegistryManager stage, int id, DataSerializerEntry entry, @Nullable DataSerializerEntry oldEntry) {
         Map<IDataSerializer<?>, DataSerializerEntry> map = (Map)owner.getSlaveMap(GameData.SERIALIZER_TO_ENTRY, Map.class);
         if (oldEntry != null) {
            map.remove(oldEntry.getSerializer());
         }

         map.put(entry.getSerializer(), entry);
      }

      public void onClear(IForgeRegistryInternal<DataSerializerEntry> owner, RegistryManager stage) {
         ((Map)owner.getSlaveMap(GameData.SERIALIZER_TO_ENTRY, Map.class)).clear();
      }

      public void onCreate(IForgeRegistryInternal<DataSerializerEntry> owner, RegistryManager stage) {
         owner.setSlaveMap(GameData.SERIALIZER_TO_ENTRY, new IdentityHashMap());
      }
   }

   private static class ItemCallbacks implements IForgeRegistry.AddCallback<Item>, IForgeRegistry.ClearCallback<Item>, IForgeRegistry.CreateCallback<Item> {
      static final GameData.ItemCallbacks INSTANCE = new GameData.ItemCallbacks();

      public void onAdd(IForgeRegistryInternal<Item> owner, RegistryManager stage, int id, Item item, @Nullable Item oldItem) {
         Map blockToItem;
         if (oldItem instanceof BlockItem) {
            blockToItem = (Map)owner.getSlaveMap(GameData.BLOCK_TO_ITEM, Map.class);
            ((BlockItem)oldItem).removeFromBlockToItemMap(blockToItem, item);
         }

         if (item instanceof BlockItem) {
            blockToItem = (Map)owner.getSlaveMap(GameData.BLOCK_TO_ITEM, Map.class);
            ((BlockItem)item).addToBlockToItemMap(blockToItem, item);
         }

      }

      public void onClear(IForgeRegistryInternal<Item> owner, RegistryManager stage) {
         ((Map)owner.getSlaveMap(GameData.BLOCK_TO_ITEM, Map.class)).clear();
      }

      public void onCreate(IForgeRegistryInternal<Item> owner, RegistryManager stage) {
         Map<?, ?> map = (Map)stage.getRegistry(GameData.BLOCKS).getSlaveMap(GameData.BLOCK_TO_ITEM, Map.class);
         owner.setSlaveMap(GameData.BLOCK_TO_ITEM, map);
      }
   }

   private static class BlockCallbacks implements IForgeRegistry.AddCallback<Block>, IForgeRegistry.ClearCallback<Block>, IForgeRegistry.BakeCallback<Block>, IForgeRegistry.CreateCallback<Block>, IForgeRegistry.DummyFactory<Block> {
      static final GameData.BlockCallbacks INSTANCE = new GameData.BlockCallbacks();

      public void onAdd(IForgeRegistryInternal<Block> owner, RegistryManager stage, int id, Block block, @Nullable Block oldBlock) {
         if (oldBlock != null) {
            StateContainer<Block, BlockState> oldContainer = oldBlock.getStateContainer();
            StateContainer<Block, BlockState> newContainer = block.getStateContainer();
            if (block.getRegistryName().getNamespace().equals("minecraft") && !oldContainer.getProperties().equals(newContainer.getProperties())) {
               String oldSequence = (String)oldContainer.getProperties().stream().map((s) -> {
                  return String.format("%s={%s}", s.getName(), s.getAllowedValues().stream().map(Object::toString).collect(Collectors.joining(",")));
               }).collect(Collectors.joining(";"));
               String newSequence = (String)newContainer.getProperties().stream().map((s) -> {
                  return String.format("%s={%s}", s.getName(), s.getAllowedValues().stream().map(Object::toString).collect(Collectors.joining(",")));
               }).collect(Collectors.joining(";"));
               GameData.LOGGER.error(ForgeRegistry.REGISTRIES, () -> {
                  return new AdvancedLogMessageAdapter((sb) -> {
                     sb.append("Registry replacements for vanilla block '").append(block.getRegistryName()).append("' must not change the number or order of blockstates.\n");
                     sb.append("\tOld: ").append(oldSequence).append('\n');
                     sb.append("\tNew: ").append(newSequence);
                  });
               });
               throw new RuntimeException("Invalid vanilla replacement. See log for details.");
            }
         }

      }

      public void onClear(IForgeRegistryInternal<Block> owner, RegistryManager stage) {
         ((GameData.ClearableObjectIntIdentityMap)owner.getSlaveMap(GameData.BLOCKSTATE_TO_ID, GameData.ClearableObjectIntIdentityMap.class)).clear();
      }

      public void onCreate(IForgeRegistryInternal<Block> owner, RegistryManager stage) {
         GameData.ClearableObjectIntIdentityMap<BlockState> idMap = new GameData.ClearableObjectIntIdentityMap<BlockState>() {
            public int get(BlockState key) {
               Integer integer = (Integer)this.identityMap.get(key);
               return integer == null ? -1 : integer;
            }
         };
         owner.setSlaveMap(GameData.BLOCKSTATE_TO_ID, idMap);
         owner.setSlaveMap(GameData.BLOCK_TO_ITEM, Maps.newHashMap());
      }

      public Block createDummy(ResourceLocation key) {
         Block ret = new GameData.BlockCallbacks.BlockDummyAir(Block.Properties.create(Material.AIR));
         GameData.forceRegistryName(ret, key);
         return ret;
      }

      public void onBake(IForgeRegistryInternal<Block> owner, RegistryManager stage) {
         GameData.ClearableObjectIntIdentityMap<BlockState> blockstateMap = (GameData.ClearableObjectIntIdentityMap)owner.getSlaveMap(GameData.BLOCKSTATE_TO_ID, GameData.ClearableObjectIntIdentityMap.class);
         Iterator var4 = owner.iterator();

         while(var4.hasNext()) {
            Block block = (Block)var4.next();
            UnmodifiableIterator var6 = block.getStateContainer().getValidStates().iterator();

            while(var6.hasNext()) {
               BlockState state = (BlockState)var6.next();
               blockstateMap.add(state);
               state.func_215692_c();
            }

            block.getLootTable();
         }

      }

      private static class BlockDummyAir extends AirBlock {
         private BlockDummyAir(Block.Properties properties) {
            super(properties);
         }

         public String getTranslationKey() {
            return "block.minecraft.air";
         }

         // $FF: synthetic method
         BlockDummyAir(Block.Properties x0, Object x1) {
            this(x0);
         }
      }
   }

   static class ClearableObjectIntIdentityMap<I> extends ObjectIntIdentityMap<I> {
      void clear() {
         this.identityMap.clear();
         this.objectList.clear();
         this.nextId = 0;
      }

      void remove(I key) {
         Integer prev = (Integer)this.identityMap.remove(key);
         if (prev != null) {
            this.objectList.set(prev, (Object)null);
         }

      }
   }
}
