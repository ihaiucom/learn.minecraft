package net.minecraftforge.common;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class VillagerTradingManager {
   private static final Map<VillagerProfession, Int2ObjectMap<VillagerTrades.ITrade[]>> VANILLA_TRADES = new HashMap();
   private static final Int2ObjectMap<VillagerTrades.ITrade[]> WANDERER_TRADES = new Int2ObjectOpenHashMap();

   static void loadTrades(FMLServerAboutToStartEvent e) {
      postWandererEvent();
      postVillagerEvents();
   }

   private static void postWandererEvent() {
      List<VillagerTrades.ITrade> generic = NonNullList.create();
      List<VillagerTrades.ITrade> rare = NonNullList.create();
      Arrays.stream((Object[])WANDERER_TRADES.get(1)).forEach(generic::add);
      Arrays.stream((Object[])WANDERER_TRADES.get(2)).forEach(rare::add);
      MinecraftForge.EVENT_BUS.post(new WandererTradesEvent(generic, rare));
      VillagerTrades.field_221240_b.put(1, generic.toArray(new VillagerTrades.ITrade[0]));
      VillagerTrades.field_221240_b.put(2, rare.toArray(new VillagerTrades.ITrade[0]));
   }

   private static void postVillagerEvents() {
      Iterator var0 = ForgeRegistries.PROFESSIONS.iterator();

      while(var0.hasNext()) {
         VillagerProfession prof = (VillagerProfession)var0.next();
         Int2ObjectMap<VillagerTrades.ITrade[]> trades = (Int2ObjectMap)VANILLA_TRADES.getOrDefault(prof, new Int2ObjectOpenHashMap());
         Int2ObjectMap<List<VillagerTrades.ITrade>> mutableTrades = new Int2ObjectOpenHashMap();

         for(int i = 1; i < 6; ++i) {
            mutableTrades.put(i, NonNullList.create());
         }

         trades.int2ObjectEntrySet().forEach((e) -> {
            Stream var10000 = Arrays.stream((Object[])e.getValue());
            List var10001 = (List)mutableTrades.get(e.getIntKey());
            var10000.forEach(var10001::add);
         });
         MinecraftForge.EVENT_BUS.post(new VillagerTradesEvent(mutableTrades, prof));
         Int2ObjectMap<VillagerTrades.ITrade[]> newTrades = new Int2ObjectOpenHashMap();
         mutableTrades.int2ObjectEntrySet().forEach((e) -> {
            VillagerTrades.ITrade[] var10000 = (VillagerTrades.ITrade[])newTrades.put(e.getIntKey(), ((List)e.getValue()).toArray(new VillagerTrades.ITrade[0]));
         });
         VillagerTrades.field_221239_a.put(prof, newTrades);
      }

   }

   static {
      VillagerTrades.field_221239_a.entrySet().forEach((e) -> {
         Int2ObjectMap<VillagerTrades.ITrade[]> copy = new Int2ObjectOpenHashMap();
         ((Int2ObjectMap)e.getValue()).int2ObjectEntrySet().forEach((ent) -> {
            VillagerTrades.ITrade[] var10000 = (VillagerTrades.ITrade[])copy.put(ent.getIntKey(), Arrays.copyOf((Object[])ent.getValue(), ((VillagerTrades.ITrade[])ent.getValue()).length));
         });
         VANILLA_TRADES.put(e.getKey(), copy);
      });
      VillagerTrades.field_221240_b.int2ObjectEntrySet().forEach((e) -> {
         VillagerTrades.ITrade[] var10000 = (VillagerTrades.ITrade[])WANDERER_TRADES.put(e.getIntKey(), Arrays.copyOf((Object[])e.getValue(), ((VillagerTrades.ITrade[])e.getValue()).length));
      });
   }
}
