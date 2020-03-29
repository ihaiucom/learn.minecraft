package net.minecraftforge.client.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@OnlyIn(Dist.CLIENT)
public class GuiScreenEvent extends Event {
   private final Screen gui;

   public GuiScreenEvent(Screen gui) {
      this.gui = gui;
   }

   public Screen getGui() {
      return this.gui;
   }

   public static class KeyboardCharTypedEvent extends GuiScreenEvent {
      private final char codePoint;
      private final int modifiers;

      public KeyboardCharTypedEvent(Screen gui, char codePoint, int modifiers) {
         super(gui);
         this.codePoint = codePoint;
         this.modifiers = modifiers;
      }

      public char getCodePoint() {
         return this.codePoint;
      }

      public int getModifiers() {
         return this.modifiers;
      }

      @Cancelable
      public static class Post extends GuiScreenEvent.KeyboardCharTypedEvent {
         public Post(Screen gui, char codePoint, int modifiers) {
            super(gui, codePoint, modifiers);
         }
      }

      @Cancelable
      public static class Pre extends GuiScreenEvent.KeyboardCharTypedEvent {
         public Pre(Screen gui, char codePoint, int modifiers) {
            super(gui, codePoint, modifiers);
         }
      }
   }

   public abstract static class KeyboardKeyReleasedEvent extends GuiScreenEvent.KeyboardKeyEvent {
      public KeyboardKeyReleasedEvent(Screen gui, int keyCode, int scanCode, int modifiers) {
         super(gui, keyCode, scanCode, modifiers);
      }

      @Cancelable
      public static class Post extends GuiScreenEvent.KeyboardKeyReleasedEvent {
         public Post(Screen gui, int keyCode, int scanCode, int modifiers) {
            super(gui, keyCode, scanCode, modifiers);
         }
      }

      @Cancelable
      public static class Pre extends GuiScreenEvent.KeyboardKeyReleasedEvent {
         public Pre(Screen gui, int keyCode, int scanCode, int modifiers) {
            super(gui, keyCode, scanCode, modifiers);
         }
      }
   }

   public abstract static class KeyboardKeyPressedEvent extends GuiScreenEvent.KeyboardKeyEvent {
      public KeyboardKeyPressedEvent(Screen gui, int keyCode, int scanCode, int modifiers) {
         super(gui, keyCode, scanCode, modifiers);
      }

      @Cancelable
      public static class Post extends GuiScreenEvent.KeyboardKeyPressedEvent {
         public Post(Screen gui, int keyCode, int scanCode, int modifiers) {
            super(gui, keyCode, scanCode, modifiers);
         }
      }

      @Cancelable
      public static class Pre extends GuiScreenEvent.KeyboardKeyPressedEvent {
         public Pre(Screen gui, int keyCode, int scanCode, int modifiers) {
            super(gui, keyCode, scanCode, modifiers);
         }
      }
   }

   public abstract static class KeyboardKeyEvent extends GuiScreenEvent {
      private final int keyCode;
      private final int scanCode;
      private final int modifiers;

      public KeyboardKeyEvent(Screen gui, int keyCode, int scanCode, int modifiers) {
         super(gui);
         this.keyCode = keyCode;
         this.scanCode = scanCode;
         this.modifiers = modifiers;
      }

      public int getKeyCode() {
         return this.keyCode;
      }

      public int getScanCode() {
         return this.scanCode;
      }

      public int getModifiers() {
         return this.modifiers;
      }
   }

   public abstract static class MouseScrollEvent extends GuiScreenEvent.MouseInputEvent {
      private final double scrollDelta;

      public MouseScrollEvent(Screen gui, double mouseX, double mouseY, double scrollDelta) {
         super(gui, mouseX, mouseY);
         this.scrollDelta = scrollDelta;
      }

      public double getScrollDelta() {
         return this.scrollDelta;
      }

      @Cancelable
      public static class Post extends GuiScreenEvent.MouseScrollEvent {
         public Post(Screen gui, double mouseX, double mouseY, double scrollDelta) {
            super(gui, mouseX, mouseY, scrollDelta);
         }
      }

      @Cancelable
      public static class Pre extends GuiScreenEvent.MouseScrollEvent {
         public Pre(Screen gui, double mouseX, double mouseY, double scrollDelta) {
            super(gui, mouseX, mouseY, scrollDelta);
         }
      }
   }

   public abstract static class MouseDragEvent extends GuiScreenEvent.MouseInputEvent {
      private final int mouseButton;
      private final double dragX;
      private final double dragY;

      public MouseDragEvent(Screen gui, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
         super(gui, mouseX, mouseY);
         this.mouseButton = mouseButton;
         this.dragX = dragX;
         this.dragY = dragY;
      }

      public int getMouseButton() {
         return this.mouseButton;
      }

      public double getDragX() {
         return this.dragX;
      }

      public double getDragY() {
         return this.dragY;
      }

      @Cancelable
      public static class Post extends GuiScreenEvent.MouseDragEvent {
         public Post(Screen gui, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
            super(gui, mouseX, mouseY, mouseButton, dragX, dragY);
         }
      }

      @Cancelable
      public static class Pre extends GuiScreenEvent.MouseDragEvent {
         public Pre(Screen gui, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
            super(gui, mouseX, mouseY, mouseButton, dragX, dragY);
         }
      }
   }

   public abstract static class MouseReleasedEvent extends GuiScreenEvent.MouseInputEvent {
      private final int button;

      public MouseReleasedEvent(Screen gui, double mouseX, double mouseY, int button) {
         super(gui, mouseX, mouseY);
         this.button = button;
      }

      public int getButton() {
         return this.button;
      }

      @Cancelable
      public static class Post extends GuiScreenEvent.MouseReleasedEvent {
         public Post(Screen gui, double mouseX, double mouseY, int button) {
            super(gui, mouseX, mouseY, button);
         }
      }

      @Cancelable
      public static class Pre extends GuiScreenEvent.MouseReleasedEvent {
         public Pre(Screen gui, double mouseX, double mouseY, int button) {
            super(gui, mouseX, mouseY, button);
         }
      }
   }

   public abstract static class MouseClickedEvent extends GuiScreenEvent.MouseInputEvent {
      private final int button;

      public MouseClickedEvent(Screen gui, double mouseX, double mouseY, int button) {
         super(gui, mouseX, mouseY);
         this.button = button;
      }

      public int getButton() {
         return this.button;
      }

      @Cancelable
      public static class Post extends GuiScreenEvent.MouseClickedEvent {
         public Post(Screen gui, double mouseX, double mouseY, int button) {
            super(gui, mouseX, mouseY, button);
         }
      }

      @Cancelable
      public static class Pre extends GuiScreenEvent.MouseClickedEvent {
         public Pre(Screen gui, double mouseX, double mouseY, int button) {
            super(gui, mouseX, mouseY, button);
         }
      }
   }

   public abstract static class MouseInputEvent extends GuiScreenEvent {
      private final double mouseX;
      private final double mouseY;

      public MouseInputEvent(Screen gui, double mouseX, double mouseY) {
         super(gui);
         this.mouseX = mouseX;
         this.mouseY = mouseY;
      }

      public double getMouseX() {
         return this.mouseX;
      }

      public double getMouseY() {
         return this.mouseY;
      }
   }

   public static class ActionPerformedEvent extends GuiScreenEvent {
      private Button button;
      private List<Button> buttonList;

      public ActionPerformedEvent(Screen gui, Button button, List<Button> buttonList) {
         super(gui);
         this.setButton(button);
         this.setButtonList(new ArrayList(buttonList));
      }

      public Button getButton() {
         return this.button;
      }

      public void setButton(Button button) {
         this.button = button;
      }

      public List<Button> getButtonList() {
         return this.buttonList;
      }

      public void setButtonList(List<Button> buttonList) {
         this.buttonList = buttonList;
      }

      public static class Post extends GuiScreenEvent.ActionPerformedEvent {
         public Post(Screen gui, Button button, List<Button> buttonList) {
            super(gui, button, buttonList);
         }
      }

      @Cancelable
      public static class Pre extends GuiScreenEvent.ActionPerformedEvent {
         public Pre(Screen gui, Button button, List<Button> buttonList) {
            super(gui, button, buttonList);
         }
      }
   }

   @Cancelable
   public static class PotionShiftEvent extends GuiScreenEvent {
      public PotionShiftEvent(Screen gui) {
         super(gui);
      }
   }

   public static class BackgroundDrawnEvent extends GuiScreenEvent {
      public BackgroundDrawnEvent(Screen gui) {
         super(gui);
      }
   }

   public static class DrawScreenEvent extends GuiScreenEvent {
      private final int mouseX;
      private final int mouseY;
      private final float renderPartialTicks;

      public DrawScreenEvent(Screen gui, int mouseX, int mouseY, float renderPartialTicks) {
         super(gui);
         this.mouseX = mouseX;
         this.mouseY = mouseY;
         this.renderPartialTicks = renderPartialTicks;
      }

      public int getMouseX() {
         return this.mouseX;
      }

      public int getMouseY() {
         return this.mouseY;
      }

      public float getRenderPartialTicks() {
         return this.renderPartialTicks;
      }

      public static class Post extends GuiScreenEvent.DrawScreenEvent {
         public Post(Screen gui, int mouseX, int mouseY, float renderPartialTicks) {
            super(gui, mouseX, mouseY, renderPartialTicks);
         }
      }

      @Cancelable
      public static class Pre extends GuiScreenEvent.DrawScreenEvent {
         public Pre(Screen gui, int mouseX, int mouseY, float renderPartialTicks) {
            super(gui, mouseX, mouseY, renderPartialTicks);
         }
      }
   }

   public static class InitGuiEvent extends GuiScreenEvent {
      private Consumer<Widget> add;
      private Consumer<Widget> remove;
      private List<Widget> list;

      public InitGuiEvent(Screen gui, List<Widget> list, Consumer<Widget> add, Consumer<Widget> remove) {
         super(gui);
         this.list = Collections.unmodifiableList(list);
         this.add = add;
         this.remove = remove;
      }

      public List<Widget> getWidgetList() {
         return this.list;
      }

      public void addWidget(Widget button) {
         this.add.accept(button);
      }

      public void removeWidget(Widget button) {
         this.remove.accept(button);
      }

      public static class Post extends GuiScreenEvent.InitGuiEvent {
         public Post(Screen gui, List<Widget> list, Consumer<Widget> add, Consumer<Widget> remove) {
            super(gui, list, add, remove);
         }
      }

      @Cancelable
      public static class Pre extends GuiScreenEvent.InitGuiEvent {
         public Pre(Screen gui, List<Widget> list, Consumer<Widget> add, Consumer<Widget> remove) {
            super(gui, list, add, remove);
         }
      }
   }
}
