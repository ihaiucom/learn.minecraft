package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.fml.ModContainer;

public class FMLLoadCompleteEvent extends ModLifecycleEvent {
   public FMLLoadCompleteEvent(ModContainer container) {
      super(container);
   }
}
