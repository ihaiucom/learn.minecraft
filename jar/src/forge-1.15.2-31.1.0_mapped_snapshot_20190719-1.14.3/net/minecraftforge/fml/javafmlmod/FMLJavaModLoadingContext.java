package net.minecraftforge.fml.javafmlmod;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;

public class FMLJavaModLoadingContext {
   private final FMLModContainer container;

   FMLJavaModLoadingContext(FMLModContainer container) {
      this.container = container;
   }

   public IEventBus getModEventBus() {
      return this.container.getEventBus();
   }

   public static FMLJavaModLoadingContext get() {
      return (FMLJavaModLoadingContext)ModLoadingContext.get().extension();
   }
}
