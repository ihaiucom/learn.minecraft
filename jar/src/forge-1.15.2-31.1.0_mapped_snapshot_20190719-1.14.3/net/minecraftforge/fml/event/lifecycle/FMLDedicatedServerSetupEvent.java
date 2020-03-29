package net.minecraftforge.fml.event.lifecycle;

import java.util.function.Supplier;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.fml.ModContainer;

public class FMLDedicatedServerSetupEvent extends ModLifecycleEvent {
   private final Supplier<DedicatedServer> serverSupplier;

   public FMLDedicatedServerSetupEvent(Supplier<DedicatedServer> server, ModContainer container) {
      super(container);
      this.serverSupplier = server;
   }

   public Supplier<DedicatedServer> getServerSupplier() {
      return this.serverSupplier;
   }
}
