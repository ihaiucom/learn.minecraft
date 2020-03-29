package net.minecraftforge.fml.event.server;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;

public class FMLServerStartingEvent extends ServerLifecycleEvent {
   public FMLServerStartingEvent(MinecraftServer server) {
      super(server);
   }

   public CommandDispatcher<CommandSource> getCommandDispatcher() {
      return this.server.getCommandManager().getDispatcher();
   }
}
