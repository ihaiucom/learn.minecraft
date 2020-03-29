package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.fml.ModContainer;

public class InterModEnqueueEvent extends ModLifecycleEvent {
   public InterModEnqueueEvent(ModContainer container) {
      super(container);
   }
}
