package net.minecraftforge.event;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class LootTableLoadEvent extends Event {
   private final ResourceLocation name;
   private LootTable table;
   private LootTableManager lootTableManager;

   public LootTableLoadEvent(ResourceLocation name, LootTable table, LootTableManager lootTableManager) {
      this.name = name;
      this.table = table;
      this.lootTableManager = lootTableManager;
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public LootTable getTable() {
      return this.table;
   }

   public LootTableManager getLootTableManager() {
      return this.lootTableManager;
   }

   public void setTable(LootTable table) {
      this.table = table;
   }
}
