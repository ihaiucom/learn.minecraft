package net.minecraftforge.client.event;

import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.eventbus.api.Event;

public abstract class ColorHandlerEvent extends Event {
   public static class Item extends ColorHandlerEvent {
      private final ItemColors itemColors;
      private final BlockColors blockColors;

      public Item(ItemColors itemColors, BlockColors blockColors) {
         this.itemColors = itemColors;
         this.blockColors = blockColors;
      }

      public ItemColors getItemColors() {
         return this.itemColors;
      }

      public BlockColors getBlockColors() {
         return this.blockColors;
      }
   }

   public static class Block extends ColorHandlerEvent {
      private final BlockColors blockColors;

      public Block(BlockColors blockColors) {
         this.blockColors = blockColors;
      }

      public BlockColors getBlockColors() {
         return this.blockColors;
      }
   }
}
