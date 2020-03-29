package net.minecraftforge.event;

import net.minecraft.tags.NetworkTagManager;
import net.minecraftforge.eventbus.api.Event;

public class TagsUpdatedEvent extends Event {
   private final NetworkTagManager manager;

   public TagsUpdatedEvent(NetworkTagManager manager) {
      this.manager = manager;
   }

   public NetworkTagManager getTagManager() {
      return this.manager;
   }
}
