package net.minecraftforge.common;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multiset;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerMultiWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.StartupQuery;
import net.minecraftforge.fml.server.ServerModLoader;
import net.minecraftforge.registries.ClearableRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class DimensionManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker DIMMGR = MarkerManager.getMarker("DIMS");
   private static final ClearableRegistry<DimensionType> REGISTRY = new ClearableRegistry(new ResourceLocation("dimension_type"), DimensionType.class);
   private static final Int2ObjectMap<DimensionManager.Data> dimensions = Int2ObjectMaps.synchronize(new Int2ObjectLinkedOpenHashMap());
   private static final IntSet unloadQueue = IntSets.synchronize(new IntLinkedOpenHashSet());
   private static final ConcurrentMap<World, World> weakWorldMap = (new MapMaker()).weakKeys().weakValues().makeMap();
   private static final Multiset<Integer> leakedWorlds = HashMultiset.create();
   private static final Map<ResourceLocation, DimensionManager.SavedEntry> savedEntries = new HashMap();
   private static volatile Set<World> playerWorlds = new HashSet();

   public static DimensionType registerOrGetDimension(ResourceLocation name, ModDimension type, PacketBuffer data, boolean hasSkyLight) {
      return (DimensionType)REGISTRY.getValue(name).orElseGet(() -> {
         return registerDimension(name, type, data, hasSkyLight);
      });
   }

   public static DimensionType registerDimension(ResourceLocation name, ModDimension type, PacketBuffer data, boolean hasSkyLight) {
      Validate.notNull(name, "Can not register a dimension with null name", new Object[0]);
      Validate.isTrue(!REGISTRY.containsKey(name), "Dimension: " + name + " Already registered", new Object[0]);
      Validate.notNull(type, "Can not register a null dimension type", new Object[0]);
      int id = REGISTRY.getNextId();
      DimensionManager.SavedEntry old = (DimensionManager.SavedEntry)savedEntries.get(name);
      if (old != null) {
         id = old.getId();
         if (!type.getRegistryName().equals(old.getType())) {
            LOGGER.info(DIMMGR, "Changing ModDimension for '{}' from '{}' to '{}'", name.toString(), old.getType() == null ? null : old.getType().toString(), type.getRegistryName().toString());
         }

         savedEntries.remove(name);
      }

      DimensionType instance = new DimensionType(id, "", name.getNamespace() + "/" + name.getPath(), type.getFactory(), hasSkyLight, type.getMagnifier(), type, data);
      REGISTRY.register(id, name, instance);
      LOGGER.info(DIMMGR, "Registered dimension {} of type {} and id {}", name.toString(), type.getRegistryName().toString(), id);
      return instance;
   }

   public static boolean keepLoaded(DimensionType dim, boolean value) {
      Validate.notNull(dim, "Dimension type must not be null", new Object[0]);
      DimensionManager.Data data = getData(dim);
      boolean ret = data.keepLoaded;
      data.keepLoaded = value;
      return ret;
   }

   public static boolean keepLoaded(DimensionType dim) {
      Validate.notNull(dim, "Dimension type must not be null", new Object[0]);
      DimensionManager.Data data = (DimensionManager.Data)dimensions.get(dim.getId());
      return data == null ? false : data.keepLoaded;
   }

   @Nullable
   public static ServerWorld getWorld(MinecraftServer server, DimensionType dim, boolean resetUnloadDelay, boolean forceLoad) {
      Validate.notNull(server, "Must provide server when creating world", new Object[0]);
      Validate.notNull(dim, "Dimension type must not be null", new Object[0]);
      if (ServerModLoader.hasErrors()) {
         throw new RuntimeException("The server has failed to initialize correctly due to mod loading errors. Examine the crash report for more details.");
      } else if (StartupQuery.pendingQuery()) {
         return null;
      } else {
         if (resetUnloadDelay && unloadQueue.contains(dim.getId())) {
            getData(dim).ticksWaited = 0;
         }

         ServerWorld ret = (ServerWorld)server.forgeGetWorldMap().get(dim);
         if (ret == null && forceLoad) {
            ret = initWorld(server, dim);
         }

         return ret;
      }
   }

   public static void unregisterDimension(int id) {
      Validate.isTrue(dimensions.containsKey(id), String.format("Failed to unregister dimension for id %d; No provider registered", id), new Object[0]);
      dimensions.remove(id);
   }

   public static DimensionType registerDimensionInternal(int id, ResourceLocation name, ModDimension type, PacketBuffer data, boolean hasSkyLight) {
      Validate.notNull(name, "Can not register a dimension with null name", new Object[0]);
      Validate.notNull(type, "Can not register a null dimension type", new Object[0]);
      Validate.isTrue(!REGISTRY.containsKey(name), "Dimension: " + name + " Already registered", new Object[0]);
      Validate.isTrue(REGISTRY.getByValue(id) == null, "Dimension with id " + id + " already registered as name " + REGISTRY.getKey(REGISTRY.getByValue(id)), new Object[0]);
      DimensionType instance = new DimensionType(id, "", name.getNamespace() + "/" + name.getPath(), type.getFactory(), hasSkyLight, type.getMagnifier(), type, data);
      REGISTRY.register(id, name, instance);
      LOGGER.info(DIMMGR, "Registered dimension {} of type {} and id {}", name.toString(), type.getRegistryName().toString(), id);
      return instance;
   }

   public static ServerWorld initWorld(MinecraftServer server, DimensionType dim) {
      Validate.isTrue(dim != DimensionType.OVERWORLD, "Can not hotload overworld. This must be loaded at all times by main Server.", new Object[0]);
      Validate.notNull(server, "Must provide server when creating world", new Object[0]);
      Validate.notNull(dim, "Must provide dimension when creating world", new Object[0]);
      ServerWorld overworld = getWorld(server, DimensionType.OVERWORLD, false, false);
      Validate.notNull(overworld, "Cannot Hotload Dim: Overworld is not Loaded!", new Object[0]);
      ServerWorld world = new ServerMultiWorld(overworld, server, server.getBackgroundExecutor(), overworld.getSaveHandler(), dim, server.getProfiler(), new DimensionManager.NoopChunkStatusListener());
      if (!server.isSinglePlayer()) {
         world.getWorldInfo().setGameType(server.getGameType());
      }

      server.forgeGetWorldMap().put(dim, world);
      server.markWorldsDirty();
      MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(world));
      return world;
   }

   private static boolean canUnloadWorld(ServerWorld world) {
      return world.getDimension().getType() != DimensionType.OVERWORLD && world.getPlayers().isEmpty() && world.getForcedChunks().isEmpty() && !getData(world.getDimension().getType()).keepLoaded && !playerWorlds.contains(world);
   }

   public static void unloadWorld(ServerWorld world) {
      if (world != null && canUnloadWorld(world)) {
         int id = world.getDimension().getType().getId();
         if (unloadQueue.add(id)) {
            LOGGER.debug(DIMMGR, "Queueing dimension {} to unload", id);
         }

      }
   }

   public static void unloadWorlds(MinecraftServer server, boolean checkLeaks) {
      IntIterator queueIterator = unloadQueue.iterator();

      while(true) {
         while(queueIterator.hasNext()) {
            int id = queueIterator.nextInt();
            DimensionType dim = DimensionType.getById(id);
            if (dim == null) {
               LOGGER.warn(DIMMGR, "Dimension with unknown type '{}' added to unload queue, removing", id);
               queueIterator.remove();
            } else {
               DimensionManager.Data dimension = (DimensionManager.Data)dimensions.computeIfAbsent(id, (k) -> {
                  return new DimensionManager.Data();
               });
               if (dimension.ticksWaited < (Integer)ForgeConfig.SERVER.dimensionUnloadQueueDelay.get()) {
                  ++dimension.ticksWaited;
               } else {
                  queueIterator.remove();
                  ServerWorld w = (ServerWorld)server.forgeGetWorldMap().get(dim);
                  dimension.ticksWaited = 0;
                  if (w != null && canUnloadWorld(w)) {
                     try {
                        w.save((IProgressUpdate)null, true, true);
                     } catch (Exception var16) {
                        LOGGER.error(DIMMGR, "Caught an exception while saving all chunks:", var16);
                     } finally {
                        MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(w));
                        LOGGER.debug(DIMMGR, "Unloading dimension {}", id);

                        try {
                           w.close();
                        } catch (IOException var15) {
                           LOGGER.error("Exception closing the level", var15);
                        }

                        server.forgeGetWorldMap().remove(dim);
                        server.markWorldsDirty();
                     }
                  } else {
                     LOGGER.debug(DIMMGR, "Aborting unload for dimension {} as status changed", id);
                  }
               }
            }
         }

         if (checkLeaks) {
            List<World> allWorlds = Lists.newArrayList(weakWorldMap.keySet());
            allWorlds.removeAll(server.forgeGetWorldMap().values());
            Stream var10000 = allWorlds.stream().map(System::identityHashCode);
            Multiset var10001 = leakedWorlds;
            var10000.forEach(var10001::add);
            Iterator var19 = allWorlds.iterator();

            while(var19.hasNext()) {
               World w = (World)var19.next();
               int hash = System.identityHashCode(w);
               int leakCount = leakedWorlds.count(hash);
               if (leakCount == 5) {
                  LOGGER.debug(DIMMGR, "The world {} ({}) may have leaked: first encounter (5 occurrences).\n", Integer.toHexString(hash), w.getWorldInfo().getWorldName());
               } else if (leakCount % 5 == 0) {
                  LOGGER.debug(DIMMGR, "The world {} ({}) may have leaked: seen {} times.\n", Integer.toHexString(hash), w.getWorldInfo().getWorldName(), leakCount);
               }
            }
         }

         return;
      }
   }

   public static void writeRegistry(CompoundNBT data) {
      data.putInt("version", 1);
      List<DimensionManager.SavedEntry> list = new ArrayList();
      Iterator var2 = REGISTRY.iterator();

      while(var2.hasNext()) {
         DimensionType type = (DimensionType)var2.next();
         list.add(new DimensionManager.SavedEntry(type));
      }

      savedEntries.values().forEach(list::add);
      Collections.sort(list, (a, b) -> {
         return a.id - b.id;
      });
      ListNBT lst = new ListNBT();
      list.forEach((e) -> {
         lst.add(e.write());
      });
      data.put("entries", lst);
   }

   public static void readRegistry(CompoundNBT data) {
      int version = data.getInt("version");
      if (version != 1) {
         throw new IllegalStateException("Attempted to load world with unknown Dimension data format: " + version);
      } else {
         LOGGER.debug(DIMMGR, "Reading Dimension Entries.");
         Map<ResourceLocation, DimensionType> vanilla = (Map)REGISTRY.stream().filter(DimensionType::isVanilla).collect(Collectors.toMap(REGISTRY::getKey, (v) -> {
            return v;
         }));
         REGISTRY.clear();
         vanilla.forEach((key, value) -> {
            LOGGER.debug(DIMMGR, "Registering vanilla entry ID: {} Name: {} Value: {}", value.getId() + 1, key.toString(), value.toString());
            REGISTRY.register(value.getId() + 1, key, value);
         });
         savedEntries.clear();
         boolean error = false;
         ListNBT list = data.getList("entries", 10);

         for(int x = 0; x < list.size(); ++x) {
            DimensionManager.SavedEntry entry = new DimensionManager.SavedEntry(list.getCompound(x));
            if (entry.type == null) {
               DimensionType type = (DimensionType)REGISTRY.getOrDefault(entry.name);
               if (type == null) {
                  LOGGER.error(DIMMGR, "Vanilla entry '{}' id {} in save file not found in registry.", entry.name.toString(), entry.id);
                  error = true;
               } else {
                  int id = REGISTRY.getId(type);
                  if (id != entry.id) {
                     LOGGER.error(DIMMGR, "Vanilla entry '{}' id {} in save file has incorrect in {} in registry.", entry.name.toString(), entry.id, id);
                     error = true;
                  }
               }
            } else {
               ModDimension mod = (ModDimension)ForgeRegistries.MOD_DIMENSIONS.getValue(entry.type);
               if (mod == null) {
                  LOGGER.error(DIMMGR, "Modded dimension entry '{}' id {} type {} in save file missing ModDimension.", entry.name.toString(), entry.id, entry.type.toString());
                  savedEntries.put(entry.name, entry);
               } else {
                  registerDimensionInternal(entry.id, entry.name, mod, entry.data == null ? null : new PacketBuffer(Unpooled.wrappedBuffer(entry.data)), entry.skyLight());
               }
            }
         }

      }
   }

   public static void fireRegister() {
      MinecraftForge.EVENT_BUS.post(new RegisterDimensionsEvent(savedEntries));
      if (!savedEntries.isEmpty()) {
         savedEntries.values().forEach((entry) -> {
            LOGGER.warn(DIMMGR, "Missing Dimension Name: '{}' Id: {} Type: '{}", entry.name.toString(), entry.id, entry.type.toString());
         });
      }

   }

   /** @deprecated */
   @Deprecated
   public static MutableRegistry<DimensionType> getRegistry() {
      return REGISTRY;
   }

   private static DimensionManager.Data getData(DimensionType dim) {
      return (DimensionManager.Data)dimensions.computeIfAbsent(dim.getId(), (k) -> {
         return new DimensionManager.Data();
      });
   }

   public static boolean rebuildPlayerMap(PlayerList players, boolean changed) {
      playerWorlds = (Set)players.getPlayers().stream().map((e) -> {
         return e.world;
      }).collect(Collectors.toSet());
      return changed;
   }

   private static class NoopChunkStatusListener implements IChunkStatusListener {
      private NoopChunkStatusListener() {
      }

      public void start(ChunkPos center) {
      }

      public void statusChanged(ChunkPos p_219508_1_, ChunkStatus p_219508_2_) {
      }

      public void stop() {
      }

      // $FF: synthetic method
      NoopChunkStatusListener(Object x0) {
         this();
      }
   }

   public static class SavedEntry {
      int id;
      ResourceLocation name;
      ResourceLocation type;
      byte[] data;
      boolean skyLight;

      public int getId() {
         return this.id;
      }

      public ResourceLocation getName() {
         return this.name;
      }

      @Nullable
      public ResourceLocation getType() {
         return this.type;
      }

      @Nullable
      public byte[] getData() {
         return this.data;
      }

      public boolean skyLight() {
         return this.skyLight;
      }

      private SavedEntry(CompoundNBT data) {
         this.id = data.getInt("id");
         this.name = new ResourceLocation(data.getString("name"));
         this.type = data.contains("type", 8) ? new ResourceLocation(data.getString("type")) : null;
         this.data = data.contains("data", 7) ? data.getByteArray("data") : null;
         this.skyLight = data.contains("sky_light", 99) ? data.getBoolean("sky_light") : true;
      }

      private SavedEntry(DimensionType data) {
         this.id = DimensionManager.REGISTRY.getId(data);
         this.name = DimensionManager.REGISTRY.getKey(data);
         if (data.getModType() != null) {
            this.type = data.getModType().getRegistryName();
         }

         if (data.getData() != null) {
            this.data = data.getData().array();
         }

         this.skyLight = data.func_218272_d();
      }

      private CompoundNBT write() {
         CompoundNBT ret = new CompoundNBT();
         ret.putInt("id", this.id);
         ret.putString("name", this.name.toString());
         if (this.type != null) {
            ret.putString("type", this.type.toString());
         }

         if (this.data != null) {
            ret.putByteArray("data", this.data);
         }

         ret.putBoolean("sky_light", this.skyLight);
         return ret;
      }

      // $FF: synthetic method
      SavedEntry(DimensionType x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      SavedEntry(CompoundNBT x0, Object x1) {
         this(x0);
      }
   }

   private static class Data {
      int ticksWaited;
      boolean keepLoaded;

      private Data() {
         this.ticksWaited = 0;
         this.keepLoaded = false;
      }

      // $FF: synthetic method
      Data(Object x0) {
         this();
      }
   }
}
