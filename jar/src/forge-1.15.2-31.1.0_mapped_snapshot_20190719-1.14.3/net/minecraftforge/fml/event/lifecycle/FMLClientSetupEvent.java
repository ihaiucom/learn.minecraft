package net.minecraftforge.fml.event.lifecycle;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.ModContainer;

public class FMLClientSetupEvent extends ModLifecycleEvent {
   private final Supplier<Minecraft> minecraftSupplier;

   public FMLClientSetupEvent(Supplier<Minecraft> mc, ModContainer container) {
      super(container);
      this.minecraftSupplier = mc;
   }

   public Supplier<Minecraft> getMinecraftSupplier() {
      return this.minecraftSupplier;
   }
}
