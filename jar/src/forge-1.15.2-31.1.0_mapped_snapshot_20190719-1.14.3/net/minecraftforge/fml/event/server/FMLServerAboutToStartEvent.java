package net.minecraftforge.fml.event.server;

import net.minecraft.server.MinecraftServer;

public class FMLServerAboutToStartEvent extends ServerLifecycleEvent {
   public FMLServerAboutToStartEvent(MinecraftServer server) {
      super(server);
   }
}
