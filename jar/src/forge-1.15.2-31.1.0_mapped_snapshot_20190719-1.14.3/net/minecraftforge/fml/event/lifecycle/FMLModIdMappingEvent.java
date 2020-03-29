package net.minecraftforge.fml.event.lifecycle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModContainer;

public class FMLModIdMappingEvent extends ModLifecycleEvent {
   private final Map<ResourceLocation, ImmutableList<FMLModIdMappingEvent.ModRemapping>> remaps;
   private final ImmutableSet<ResourceLocation> keys;
   public final boolean isFrozen;

   public FMLModIdMappingEvent(Map<ResourceLocation, Map<ResourceLocation, Integer[]>> remaps, boolean isFrozen) {
      super((ModContainer)null);
      this.isFrozen = isFrozen;
      this.remaps = Maps.newHashMap();
      remaps.forEach((name, rm) -> {
         List<FMLModIdMappingEvent.ModRemapping> tmp = Lists.newArrayList();
         rm.forEach((key, value) -> {
            tmp.add(new FMLModIdMappingEvent.ModRemapping(name, key, value[0], value[1]));
         });
         tmp.sort(Comparator.comparingInt((o) -> {
            return o.newId;
         }));
         this.remaps.put(name, ImmutableList.copyOf(tmp));
      });
      this.keys = ImmutableSet.copyOf(this.remaps.keySet());
   }

   public ImmutableSet<ResourceLocation> getRegistries() {
      return this.keys;
   }

   public ImmutableList<FMLModIdMappingEvent.ModRemapping> getRemaps(ResourceLocation registry) {
      return (ImmutableList)this.remaps.get(registry);
   }

   public class ModRemapping {
      public final ResourceLocation registry;
      public final ResourceLocation key;
      public final int oldId;
      public final int newId;

      private ModRemapping(ResourceLocation registry, ResourceLocation key, int oldId, int newId) {
         this.registry = registry;
         this.key = key;
         this.oldId = oldId;
         this.newId = newId;
      }

      // $FF: synthetic method
      ModRemapping(ResourceLocation x1, ResourceLocation x2, int x3, int x4, Object x5) {
         this(x1, x2, x3, x4);
      }
   }
}
