package net.minecraftforge.client.event;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraftforge.eventbus.api.Event;

public class GuiContainerEvent extends Event {
   private final ContainerScreen guiContainer;

   public GuiContainerEvent(ContainerScreen guiContainer) {
      this.guiContainer = guiContainer;
   }

   public ContainerScreen getGuiContainer() {
      return this.guiContainer;
   }

   public static class DrawBackground extends GuiContainerEvent {
      private final int mouseX;
      private final int mouseY;

      public DrawBackground(ContainerScreen guiContainer, int mouseX, int mouseY) {
         super(guiContainer);
         this.mouseX = mouseX;
         this.mouseY = mouseY;
      }

      public int getMouseX() {
         return this.mouseX;
      }

      public int getMouseY() {
         return this.mouseY;
      }
   }

   public static class DrawForeground extends GuiContainerEvent {
      private final int mouseX;
      private final int mouseY;

      public DrawForeground(ContainerScreen guiContainer, int mouseX, int mouseY) {
         super(guiContainer);
         this.mouseX = mouseX;
         this.mouseY = mouseY;
      }

      public int getMouseX() {
         return this.mouseX;
      }

      public int getMouseY() {
         return this.mouseY;
      }
   }
}
