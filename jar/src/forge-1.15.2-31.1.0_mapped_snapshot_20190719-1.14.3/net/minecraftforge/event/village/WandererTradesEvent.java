package net.minecraftforge.event.village;

import java.util.List;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraftforge.eventbus.api.Event;

public class WandererTradesEvent extends Event {
   protected List<VillagerTrades.ITrade> generic;
   protected List<VillagerTrades.ITrade> rare;

   public WandererTradesEvent(List<VillagerTrades.ITrade> generic, List<VillagerTrades.ITrade> rare) {
      this.generic = generic;
      this.rare = rare;
   }

   public List<VillagerTrades.ITrade> getGenericTrades() {
      return this.generic;
   }

   public List<VillagerTrades.ITrade> getRareTrades() {
      return this.rare;
   }
}
