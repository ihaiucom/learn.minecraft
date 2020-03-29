package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.fml.ModContainer;

public class FMLCommonSetupEvent extends ModLifecycleEvent {
   public FMLCommonSetupEvent(ModContainer container) {
      super(container);
   }
}
