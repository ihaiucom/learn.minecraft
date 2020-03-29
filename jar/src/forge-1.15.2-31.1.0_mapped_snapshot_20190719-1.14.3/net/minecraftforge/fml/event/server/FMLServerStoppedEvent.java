package net.minecraftforge.fml.event.server;

import net.minecraft.server.MinecraftServer;

public class FMLServerStoppedEvent extends ServerLifecycleEvent {
   public FMLServerStoppedEvent(MinecraftServer server) {
      super(server);
   }
}
