package net.minecraftforge.event.village;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraftforge.eventbus.api.Event;

public class VillagerTradesEvent extends Event {
   protected Int2ObjectMap<List<VillagerTrades.ITrade>> trades;
   protected VillagerProfession type;

   public VillagerTradesEvent(Int2ObjectMap<List<VillagerTrades.ITrade>> trades, VillagerProfession type) {
      this.trades = trades;
      this.type = type;
   }

   public Int2ObjectMap<List<VillagerTrades.ITrade>> getTrades() {
      return this.trades;
   }

   public VillagerProfession getType() {
      return this.type;
   }
}
