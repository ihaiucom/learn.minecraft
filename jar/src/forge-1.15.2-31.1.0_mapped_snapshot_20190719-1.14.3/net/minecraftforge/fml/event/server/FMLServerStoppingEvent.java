package net.minecraftforge.fml.event.server;

import net.minecraft.server.MinecraftServer;

public class FMLServerStoppingEvent extends ServerLifecycleEvent {
   public FMLServerStoppingEvent(MinecraftServer server) {
      super(server);
   }
}
