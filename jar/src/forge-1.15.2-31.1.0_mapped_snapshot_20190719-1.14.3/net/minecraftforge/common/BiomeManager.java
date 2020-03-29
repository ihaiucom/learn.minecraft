package net.minecraftforge.common;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;

public class BiomeManager {
   private static BiomeManager.TrackedList<BiomeManager.BiomeEntry>[] biomes = setupBiomes();
   public static List<Biome> oceanBiomes = new ArrayList();

   private static BiomeManager.TrackedList<BiomeManager.BiomeEntry>[] setupBiomes() {
      BiomeManager.TrackedList<BiomeManager.BiomeEntry>[] currentBiomes = new BiomeManager.TrackedList[BiomeManager.BiomeType.values().length];
      List<BiomeManager.BiomeEntry> list = new ArrayList();
      list.add(new BiomeManager.BiomeEntry(Biomes.FOREST, 10));
      list.add(new BiomeManager.BiomeEntry(Biomes.DARK_FOREST, 10));
      list.add(new BiomeManager.BiomeEntry(Biomes.MOUNTAINS, 10));
      list.add(new BiomeManager.BiomeEntry(Biomes.PLAINS, 10));
      list.add(new BiomeManager.BiomeEntry(Biomes.BIRCH_FOREST, 10));
      list.add(new BiomeManager.BiomeEntry(Biomes.SWAMP, 10));
      currentBiomes[BiomeManager.BiomeType.WARM.ordinal()] = new BiomeManager.TrackedList(list);
      list.clear();
      list.add(new BiomeManager.BiomeEntry(Biomes.FOREST, 10));
      list.add(new BiomeManager.BiomeEntry(Biomes.MOUNTAINS, 10));
      list.add(new BiomeManager.BiomeEntry(Biomes.TAIGA, 10));
      list.add(new BiomeManager.BiomeEntry(Biomes.PLAINS, 10));
      currentBiomes[BiomeManager.BiomeType.COOL.ordinal()] = new BiomeManager.TrackedList(list);
      list.clear();
      list.add(new BiomeManager.BiomeEntry(Biomes.SNOWY_TUNDRA, 30));
      list.add(new BiomeManager.BiomeEntry(Biomes.SNOWY_TAIGA, 10));
      currentBiomes[BiomeManager.BiomeType.ICY.ordinal()] = new BiomeManager.TrackedList(list);
      list.clear();
      currentBiomes[BiomeManager.BiomeType.DESERT.ordinal()] = new BiomeManager.TrackedList(list);
      return currentBiomes;
   }

   public static void addSpawnBiome(Biome biome) {
      if (!BiomeProvider.BIOMES_TO_SPAWN_IN.contains(biome)) {
         BiomeProvider.BIOMES_TO_SPAWN_IN.add(biome);
      }

   }

   public static void removeSpawnBiome(Biome biome) {
      if (BiomeProvider.BIOMES_TO_SPAWN_IN.contains(biome)) {
         BiomeProvider.BIOMES_TO_SPAWN_IN.remove(biome);
      }

   }

   public static void addBiome(BiomeManager.BiomeType type, BiomeManager.BiomeEntry entry) {
      int idx = type.ordinal();
      List<BiomeManager.BiomeEntry> list = idx > biomes.length ? null : biomes[idx];
      if (list != null) {
         list.add(entry);
      }

   }

   public static void removeBiome(BiomeManager.BiomeType type, BiomeManager.BiomeEntry entry) {
      int idx = type.ordinal();
      List<BiomeManager.BiomeEntry> list = idx > biomes.length ? null : biomes[idx];
      if (list != null && list.contains(entry)) {
         list.remove(entry);
      }

   }

   @Nullable
   public static ImmutableList<BiomeManager.BiomeEntry> getBiomes(BiomeManager.BiomeType type) {
      int idx = type.ordinal();
      List<BiomeManager.BiomeEntry> list = idx >= biomes.length ? null : biomes[idx];
      return list != null ? ImmutableList.copyOf(list) : null;
   }

   public static boolean isTypeListModded(BiomeManager.BiomeType type) {
      int idx = type.ordinal();
      BiomeManager.TrackedList<BiomeManager.BiomeEntry> list = idx > biomes.length ? null : biomes[idx];
      return list != null ? list.isModded() : false;
   }

   static {
      oceanBiomes.add(Biomes.OCEAN);
      oceanBiomes.add(Biomes.DEEP_OCEAN);
      oceanBiomes.add(Biomes.FROZEN_OCEAN);
   }

   private static class TrackedList<E> extends ArrayList<E> {
      private static final long serialVersionUID = 1L;
      private boolean isModded = false;

      public TrackedList(Collection<? extends E> c) {
         super(c);
      }

      public E set(int index, E element) {
         this.isModded = true;
         return super.set(index, element);
      }

      public boolean add(E e) {
         this.isModded = true;
         return super.add(e);
      }

      public void add(int index, E element) {
         this.isModded = true;
         super.add(index, element);
      }

      public E remove(int index) {
         this.isModded = true;
         return super.remove(index);
      }

      public boolean remove(Object o) {
         this.isModded = true;
         return super.remove(o);
      }

      public void clear() {
         this.isModded = true;
         super.clear();
      }

      public boolean addAll(Collection<? extends E> c) {
         this.isModded = true;
         return super.addAll(c);
      }

      public boolean addAll(int index, Collection<? extends E> c) {
         this.isModded = true;
         return super.addAll(index, c);
      }

      public boolean removeAll(Collection<?> c) {
         this.isModded = true;
         return super.removeAll(c);
      }

      public boolean retainAll(Collection<?> c) {
         this.isModded = true;
         return super.retainAll(c);
      }

      public boolean isModded() {
         return this.isModded;
      }
   }

   public static class BiomeEntry extends WeightedRandom.Item {
      public final Biome biome;

      public BiomeEntry(Biome biome, int weight) {
         super(weight);
         this.biome = biome;
      }
   }

   public static enum BiomeType {
      DESERT,
      WARM,
      COOL,
      ICY;

      public static BiomeManager.BiomeType create(String name) {
         return null;
      }
   }
}
