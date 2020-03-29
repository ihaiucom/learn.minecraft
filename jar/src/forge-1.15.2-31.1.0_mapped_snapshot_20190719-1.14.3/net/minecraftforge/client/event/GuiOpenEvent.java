package net.minecraftforge.client.event;

import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class GuiOpenEvent extends Event {
   private Screen gui;

   public GuiOpenEvent(Screen gui) {
      this.setGui(gui);
   }

   public Screen getGui() {
      return this.gui;
   }

   public void setGui(Screen gui) {
      this.gui = gui;
   }
}
