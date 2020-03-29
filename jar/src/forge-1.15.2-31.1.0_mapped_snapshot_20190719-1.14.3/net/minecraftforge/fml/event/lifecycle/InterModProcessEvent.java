package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.fml.ModContainer;

public class InterModProcessEvent extends ModLifecycleEvent {
   public InterModProcessEvent(ModContainer container) {
      super(container);
   }
}
