package net.minecraftforge.common;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeDictionary {
   private static final boolean DEBUG = false;
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<ResourceLocation, BiomeDictionary.BiomeInfo> biomeInfoMap = new HashMap();

   public static void addTypes(Biome biome, BiomeDictionary.Type... types) {
      Preconditions.checkArgument(ForgeRegistries.BIOMES.containsValue(biome), "Cannot add types to unregistered biome %s", biome);
      Collection<BiomeDictionary.Type> supertypes = listSupertypes(types);
      Collections.addAll(supertypes, types);
      Iterator var3 = supertypes.iterator();

      while(var3.hasNext()) {
         BiomeDictionary.Type type = (BiomeDictionary.Type)var3.next();
         type.biomes.add(biome);
      }

      BiomeDictionary.BiomeInfo biomeInfo = getBiomeInfo(biome);
      Collections.addAll(biomeInfo.types, types);
      biomeInfo.types.addAll(supertypes);
   }

   @Nonnull
   public static Set<Biome> getBiomes(BiomeDictionary.Type type) {
      return type.biomesUn;
   }

   @Nonnull
   public static Set<BiomeDictionary.Type> getTypes(Biome biome) {
      ensureHasTypes(biome);
      return getBiomeInfo(biome).typesUn;
   }

   public static boolean areSimilar(Biome biomeA, Biome biomeB) {
      Iterator var2 = getTypes(biomeA).iterator();

      BiomeDictionary.Type type;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         type = (BiomeDictionary.Type)var2.next();
      } while(!getTypes(biomeB).contains(type));

      return true;
   }

   public static boolean hasType(Biome biome, BiomeDictionary.Type type) {
      return getTypes(biome).contains(type);
   }

   public static boolean hasAnyType(Biome biome) {
      return !getBiomeInfo(biome).types.isEmpty();
   }

   public static void makeBestGuess(Biome biome) {
      BiomeDictionary.Type type = BiomeDictionary.Type.fromVanilla(biome.getCategory());
      if (type != null) {
         addTypes(biome, type);
      }

      if (biome.getDownfall() > 0.85F) {
         addTypes(biome, BiomeDictionary.Type.WET);
      }

      if (biome.getDownfall() < 0.15F) {
         addTypes(biome, BiomeDictionary.Type.DRY);
      }

      if (biome.getDefaultTemperature() > 0.85F) {
         addTypes(biome, BiomeDictionary.Type.HOT);
      }

      if (biome.getDefaultTemperature() < 0.15F) {
         addTypes(biome, BiomeDictionary.Type.COLD);
      }

      if (biome.isHighHumidity() && biome.getDepth() < 0.0F && biome.getScale() <= 0.3F && biome.getScale() >= 0.0F) {
         addTypes(biome, BiomeDictionary.Type.SWAMP);
      }

      if (biome.getDepth() <= -0.5F) {
         if (biome.getScale() == 0.0F) {
            addTypes(biome, BiomeDictionary.Type.RIVER);
         } else {
            addTypes(biome, BiomeDictionary.Type.OCEAN);
         }
      }

      if (biome.getScale() >= 0.4F && biome.getScale() < 1.5F) {
         addTypes(biome, BiomeDictionary.Type.HILLS);
      }

      if (biome.getScale() >= 1.5F) {
         addTypes(biome, BiomeDictionary.Type.MOUNTAIN);
      }

   }

   private static BiomeDictionary.BiomeInfo getBiomeInfo(Biome biome) {
      return (BiomeDictionary.BiomeInfo)biomeInfoMap.computeIfAbsent(biome.getRegistryName(), (k) -> {
         return new BiomeDictionary.BiomeInfo();
      });
   }

   static void ensureHasTypes(Biome biome) {
      if (!hasAnyType(biome)) {
         makeBestGuess(biome);
         LOGGER.warn("No types have been added to Biome {}, types have been assigned on a best-effort guess: {}", biome.getRegistryName(), !getBiomeInfo(biome).types.isEmpty() ? getBiomeInfo(biome).types : "could not guess types");
      }

   }

   private static Collection<BiomeDictionary.Type> listSupertypes(BiomeDictionary.Type... types) {
      Set<BiomeDictionary.Type> supertypes = new HashSet();
      Deque<BiomeDictionary.Type> next = new ArrayDeque();
      Collections.addAll(next, types);

      while(!next.isEmpty()) {
         BiomeDictionary.Type type = (BiomeDictionary.Type)next.remove();
         Iterator var4 = BiomeDictionary.Type.byName.values().iterator();

         while(var4.hasNext()) {
            BiomeDictionary.Type sType = (BiomeDictionary.Type)var4.next();
            if (sType.subTypes.contains(type) && supertypes.add(sType)) {
               next.add(sType);
            }
         }
      }

      return supertypes;
   }

   private static void registerVanillaBiomes() {
      addTypes(Biomes.OCEAN, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.PLAINS, BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.DESERT, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SANDY, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.MOUNTAINS, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.FOREST, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.TAIGA, BiomeDictionary.Type.COLD, BiomeDictionary.Type.CONIFEROUS, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.SWAMP, BiomeDictionary.Type.WET, BiomeDictionary.Type.SWAMP, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.RIVER, BiomeDictionary.Type.RIVER, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.NETHER, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.NETHER);
      addTypes(Biomes.THE_END, BiomeDictionary.Type.COLD, BiomeDictionary.Type.DRY, BiomeDictionary.Type.END);
      addTypes(Biomes.FROZEN_OCEAN, BiomeDictionary.Type.COLD, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.FROZEN_RIVER, BiomeDictionary.Type.COLD, BiomeDictionary.Type.RIVER, BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.SNOWY_TUNDRA, BiomeDictionary.Type.COLD, BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.WASTELAND, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.SNOWY_MOUNTAINS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.MUSHROOM_FIELDS, BiomeDictionary.Type.MUSHROOM, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.MUSHROOM_FIELD_SHORE, BiomeDictionary.Type.MUSHROOM, BiomeDictionary.Type.BEACH, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.BEACH, BiomeDictionary.Type.BEACH, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.DESERT_HILLS, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SANDY, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.WOODED_HILLS, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.TAIGA_HILLS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.CONIFEROUS, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.MOUNTAIN_EDGE, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.JUNGLE, BiomeDictionary.Type.HOT, BiomeDictionary.Type.WET, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.JUNGLE_HILLS, BiomeDictionary.Type.HOT, BiomeDictionary.Type.WET, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.JUNGLE_EDGE, BiomeDictionary.Type.HOT, BiomeDictionary.Type.WET, BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.DEEP_OCEAN, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.STONE_SHORE, BiomeDictionary.Type.BEACH, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.SNOWY_BEACH, BiomeDictionary.Type.COLD, BiomeDictionary.Type.BEACH, BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.BIRCH_FOREST, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.BIRCH_FOREST_HILLS, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.DARK_FOREST, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.SNOWY_TAIGA, BiomeDictionary.Type.COLD, BiomeDictionary.Type.CONIFEROUS, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.SNOWY_TAIGA_HILLS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.CONIFEROUS, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.GIANT_TREE_TAIGA, BiomeDictionary.Type.COLD, BiomeDictionary.Type.CONIFEROUS, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.GIANT_TREE_TAIGA_HILLS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.CONIFEROUS, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.WOODED_MOUNTAINS, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.SAVANNA, BiomeDictionary.Type.HOT, BiomeDictionary.Type.SAVANNA, BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.SAVANNA_PLATEAU, BiomeDictionary.Type.HOT, BiomeDictionary.Type.SAVANNA, BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.PLATEAU);
      addTypes(Biomes.BADLANDS, BiomeDictionary.Type.MESA, BiomeDictionary.Type.SANDY, BiomeDictionary.Type.DRY, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.WOODED_BADLANDS_PLATEAU, BiomeDictionary.Type.MESA, BiomeDictionary.Type.SANDY, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.PLATEAU);
      addTypes(Biomes.BADLANDS_PLATEAU, BiomeDictionary.Type.MESA, BiomeDictionary.Type.SANDY, BiomeDictionary.Type.DRY, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.PLATEAU);
      addTypes(Biomes.SMALL_END_ISLANDS, BiomeDictionary.Type.END);
      addTypes(Biomes.END_MIDLANDS, BiomeDictionary.Type.END);
      addTypes(Biomes.END_HIGHLANDS, BiomeDictionary.Type.END);
      addTypes(Biomes.END_BARRENS, BiomeDictionary.Type.END);
      addTypes(Biomes.WARM_OCEAN, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.HOT, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.LUKEWARM_OCEAN, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.COLD_OCEAN, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.COLD, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.DEEP_WARM_OCEAN, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.HOT, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.DEEP_LUKEWARM_OCEAN, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.DEEP_COLD_OCEAN, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.COLD, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.DEEP_FROZEN_OCEAN, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.COLD, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.THE_VOID, BiomeDictionary.Type.VOID);
      addTypes(Biomes.SUNFLOWER_PLAINS, BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.DESERT_LAKES, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SANDY, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.GRAVELLY_MOUNTAINS, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.FLOWER_FOREST, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.TAIGA_MOUNTAINS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.CONIFEROUS, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.SWAMP_HILLS, BiomeDictionary.Type.WET, BiomeDictionary.Type.SWAMP, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.ICE_SPIKES, BiomeDictionary.Type.COLD, BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.MODIFIED_JUNGLE, BiomeDictionary.Type.HOT, BiomeDictionary.Type.WET, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.MODIFIED);
      addTypes(Biomes.MODIFIED_JUNGLE_EDGE, BiomeDictionary.Type.HOT, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.MODIFIED);
      addTypes(Biomes.TALL_BIRCH_FOREST, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.TALL_BIRCH_HILLS, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.DARK_FOREST_HILLS, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.SNOWY_TAIGA_MOUNTAINS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.CONIFEROUS, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.GIANT_SPRUCE_TAIGA, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.GIANT_SPRUCE_TAIGA_HILLS, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.MODIFIED_GRAVELLY_MOUNTAINS, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.MODIFIED);
      addTypes(Biomes.SHATTERED_SAVANNA, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.SAVANNA, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.SHATTERED_SAVANNA_PLATEAU, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.SAVANNA, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.PLATEAU);
      addTypes(Biomes.ERODED_BADLANDS, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD);
      addTypes(Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.HILLS, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.PLATEAU, BiomeDictionary.Type.MODIFIED);
      addTypes(Biomes.MODIFIED_BADLANDS_PLATEAU, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.RARE, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.PLATEAU, BiomeDictionary.Type.MODIFIED);
   }

   // $FF: synthetic method
   private static void lambda$registerVanillaBiomes$2(StringBuilder buf, String name, BiomeDictionary.Type type) {
      buf.append("    ").append(type.name).append(": ").append((String)type.biomes.stream().map((b) -> {
         return b.getRegistryName().toString();
      }).collect(Collectors.joining(", "))).append('\n');
   }

   static {
      registerVanillaBiomes();
   }

   private static class BiomeInfo {
      private final Set<BiomeDictionary.Type> types;
      private final Set<BiomeDictionary.Type> typesUn;

      private BiomeInfo() {
         this.types = new HashSet();
         this.typesUn = Collections.unmodifiableSet(this.types);
      }

      // $FF: synthetic method
      BiomeInfo(Object x0) {
         this();
      }
   }

   public static final class Type {
      private static final Map<String, BiomeDictionary.Type> byName = new HashMap();
      private static Collection<BiomeDictionary.Type> allTypes;
      public static final BiomeDictionary.Type HOT;
      public static final BiomeDictionary.Type COLD;
      public static final BiomeDictionary.Type SPARSE;
      public static final BiomeDictionary.Type DENSE;
      public static final BiomeDictionary.Type WET;
      public static final BiomeDictionary.Type DRY;
      public static final BiomeDictionary.Type SAVANNA;
      public static final BiomeDictionary.Type CONIFEROUS;
      public static final BiomeDictionary.Type JUNGLE;
      public static final BiomeDictionary.Type SPOOKY;
      public static final BiomeDictionary.Type DEAD;
      public static final BiomeDictionary.Type LUSH;
      public static final BiomeDictionary.Type MUSHROOM;
      public static final BiomeDictionary.Type MAGICAL;
      public static final BiomeDictionary.Type RARE;
      public static final BiomeDictionary.Type PLATEAU;
      public static final BiomeDictionary.Type MODIFIED;
      public static final BiomeDictionary.Type OCEAN;
      public static final BiomeDictionary.Type RIVER;
      public static final BiomeDictionary.Type WATER;
      public static final BiomeDictionary.Type MESA;
      public static final BiomeDictionary.Type FOREST;
      public static final BiomeDictionary.Type PLAINS;
      public static final BiomeDictionary.Type MOUNTAIN;
      public static final BiomeDictionary.Type HILLS;
      public static final BiomeDictionary.Type SWAMP;
      public static final BiomeDictionary.Type SANDY;
      public static final BiomeDictionary.Type SNOWY;
      public static final BiomeDictionary.Type WASTELAND;
      public static final BiomeDictionary.Type BEACH;
      public static final BiomeDictionary.Type VOID;
      public static final BiomeDictionary.Type OVERWORLD;
      public static final BiomeDictionary.Type NETHER;
      public static final BiomeDictionary.Type END;
      private final String name;
      private final List<BiomeDictionary.Type> subTypes;
      private final Set<Biome> biomes = new HashSet();
      private final Set<Biome> biomesUn;

      private Type(String name, BiomeDictionary.Type... subTypes) {
         this.biomesUn = Collections.unmodifiableSet(this.biomes);
         this.name = name;
         this.subTypes = ImmutableList.copyOf(subTypes);
         byName.put(name, this);
      }

      public String getName() {
         return this.name;
      }

      public String toString() {
         return this.name;
      }

      public static BiomeDictionary.Type getType(String name, BiomeDictionary.Type... subTypes) {
         name = name.toUpperCase();
         BiomeDictionary.Type t = (BiomeDictionary.Type)byName.get(name);
         if (t == null) {
            t = new BiomeDictionary.Type(name, subTypes);
         }

         return t;
      }

      public static Collection<BiomeDictionary.Type> getAll() {
         return allTypes;
      }

      @Nullable
      public static BiomeDictionary.Type fromVanilla(Biome.Category category) {
         if (category == Biome.Category.NONE) {
            return null;
         } else {
            return category == Biome.Category.THEEND ? VOID : getType(category.name());
         }
      }

      static {
         allTypes = Collections.unmodifiableCollection(byName.values());
         HOT = new BiomeDictionary.Type("HOT", new BiomeDictionary.Type[0]);
         COLD = new BiomeDictionary.Type("COLD", new BiomeDictionary.Type[0]);
         SPARSE = new BiomeDictionary.Type("SPARSE", new BiomeDictionary.Type[0]);
         DENSE = new BiomeDictionary.Type("DENSE", new BiomeDictionary.Type[0]);
         WET = new BiomeDictionary.Type("WET", new BiomeDictionary.Type[0]);
         DRY = new BiomeDictionary.Type("DRY", new BiomeDictionary.Type[0]);
         SAVANNA = new BiomeDictionary.Type("SAVANNA", new BiomeDictionary.Type[0]);
         CONIFEROUS = new BiomeDictionary.Type("CONIFEROUS", new BiomeDictionary.Type[0]);
         JUNGLE = new BiomeDictionary.Type("JUNGLE", new BiomeDictionary.Type[0]);
         SPOOKY = new BiomeDictionary.Type("SPOOKY", new BiomeDictionary.Type[0]);
         DEAD = new BiomeDictionary.Type("DEAD", new BiomeDictionary.Type[0]);
         LUSH = new BiomeDictionary.Type("LUSH", new BiomeDictionary.Type[0]);
         MUSHROOM = new BiomeDictionary.Type("MUSHROOM", new BiomeDictionary.Type[0]);
         MAGICAL = new BiomeDictionary.Type("MAGICAL", new BiomeDictionary.Type[0]);
         RARE = new BiomeDictionary.Type("RARE", new BiomeDictionary.Type[0]);
         PLATEAU = new BiomeDictionary.Type("PLATEAU", new BiomeDictionary.Type[0]);
         MODIFIED = new BiomeDictionary.Type("MODIFIED", new BiomeDictionary.Type[0]);
         OCEAN = new BiomeDictionary.Type("OCEAN", new BiomeDictionary.Type[0]);
         RIVER = new BiomeDictionary.Type("RIVER", new BiomeDictionary.Type[0]);
         WATER = new BiomeDictionary.Type("WATER", new BiomeDictionary.Type[]{OCEAN, RIVER});
         MESA = new BiomeDictionary.Type("MESA", new BiomeDictionary.Type[0]);
         FOREST = new BiomeDictionary.Type("FOREST", new BiomeDictionary.Type[0]);
         PLAINS = new BiomeDictionary.Type("PLAINS", new BiomeDictionary.Type[0]);
         MOUNTAIN = new BiomeDictionary.Type("MOUNTAIN", new BiomeDictionary.Type[0]);
         HILLS = new BiomeDictionary.Type("HILLS", new BiomeDictionary.Type[0]);
         SWAMP = new BiomeDictionary.Type("SWAMP", new BiomeDictionary.Type[0]);
         SANDY = new BiomeDictionary.Type("SANDY", new BiomeDictionary.Type[0]);
         SNOWY = new BiomeDictionary.Type("SNOWY", new BiomeDictionary.Type[0]);
         WASTELAND = new BiomeDictionary.Type("WASTELAND", new BiomeDictionary.Type[0]);
         BEACH = new BiomeDictionary.Type("BEACH", new BiomeDictionary.Type[0]);
         VOID = new BiomeDictionary.Type("VOID", new BiomeDictionary.Type[0]);
         OVERWORLD = new BiomeDictionary.Type("OVERWORLD", new BiomeDictionary.Type[0]);
         NETHER = new BiomeDictionary.Type("NETHER", new BiomeDictionary.Type[0]);
         END = new BiomeDictionary.Type("END", new BiomeDictionary.Type[0]);
      }
   }
}
