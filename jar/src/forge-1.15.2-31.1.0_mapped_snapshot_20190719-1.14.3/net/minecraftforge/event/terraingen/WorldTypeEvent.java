package net.minecraftforge.event.terraingen;

import net.minecraft.world.WorldType;
import net.minecraftforge.eventbus.api.Event;

public class WorldTypeEvent extends Event {
   private final WorldType worldType;

   public WorldTypeEvent(WorldType worldType) {
      this.worldType = worldType;
   }

   public WorldType getWorldType() {
      return this.worldType;
   }

   public static class BiomeSize extends WorldTypeEvent {
      private final int originalSize;
      private int newSize;

      public BiomeSize(WorldType worldType, int original) {
         super(worldType);
         this.originalSize = original;
         this.setNewSize(original);
      }

      public int getOriginalSize() {
         return this.originalSize;
      }

      public int getNewSize() {
         return this.newSize;
      }

      public void setNewSize(int newSize) {
         this.newSize = newSize;
      }
   }
}
