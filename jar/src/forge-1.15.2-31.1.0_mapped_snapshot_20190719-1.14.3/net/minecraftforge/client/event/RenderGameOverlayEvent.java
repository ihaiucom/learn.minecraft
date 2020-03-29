package net.minecraftforge.client.event;

import java.util.ArrayList;
import net.minecraft.client.MainWindow;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class RenderGameOverlayEvent extends Event {
   private final float partialTicks;
   private final MainWindow window;
   private final RenderGameOverlayEvent.ElementType type;

   public float getPartialTicks() {
      return this.partialTicks;
   }

   public MainWindow getWindow() {
      return this.window;
   }

   public RenderGameOverlayEvent.ElementType getType() {
      return this.type;
   }

   public RenderGameOverlayEvent(float partialTicks, MainWindow window) {
      this.partialTicks = partialTicks;
      this.window = window;
      this.type = null;
   }

   private RenderGameOverlayEvent(RenderGameOverlayEvent parent, RenderGameOverlayEvent.ElementType type) {
      this.partialTicks = parent.getPartialTicks();
      this.window = parent.getWindow();
      this.type = type;
   }

   // $FF: synthetic method
   RenderGameOverlayEvent(RenderGameOverlayEvent x0, RenderGameOverlayEvent.ElementType x1, Object x2) {
      this(x0, x1);
   }

   public static class Chat extends RenderGameOverlayEvent.Pre {
      private int posX;
      private int posY;

      public Chat(RenderGameOverlayEvent parent, int posX, int posY) {
         super(parent, RenderGameOverlayEvent.ElementType.CHAT);
         this.setPosX(posX);
         this.setPosY(posY);
      }

      public int getPosX() {
         return this.posX;
      }

      public void setPosX(int posX) {
         this.posX = posX;
      }

      public int getPosY() {
         return this.posY;
      }

      public void setPosY(int posY) {
         this.posY = posY;
      }
   }

   public static class Text extends RenderGameOverlayEvent.Pre {
      private final ArrayList<String> left;
      private final ArrayList<String> right;

      public Text(RenderGameOverlayEvent parent, ArrayList<String> left, ArrayList<String> right) {
         super(parent, RenderGameOverlayEvent.ElementType.TEXT);
         this.left = left;
         this.right = right;
      }

      public ArrayList<String> getLeft() {
         return this.left;
      }

      public ArrayList<String> getRight() {
         return this.right;
      }
   }

   public static class BossInfo extends RenderGameOverlayEvent.Pre {
      private final ClientBossInfo bossInfo;
      private final int x;
      private final int y;
      private int increment;

      public BossInfo(RenderGameOverlayEvent parent, RenderGameOverlayEvent.ElementType type, ClientBossInfo bossInfo, int x, int y, int increment) {
         super(parent, type);
         this.bossInfo = bossInfo;
         this.x = x;
         this.y = y;
         this.increment = increment;
      }

      public ClientBossInfo getBossInfo() {
         return this.bossInfo;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public int getIncrement() {
         return this.increment;
      }

      public void setIncrement(int increment) {
         this.increment = increment;
      }
   }

   public static class Post extends RenderGameOverlayEvent {
      public Post(RenderGameOverlayEvent parent, RenderGameOverlayEvent.ElementType type) {
         super(parent, type, null);
      }

      public boolean isCancelable() {
         return false;
      }
   }

   public static class Pre extends RenderGameOverlayEvent {
      public Pre(RenderGameOverlayEvent parent, RenderGameOverlayEvent.ElementType type) {
         super(parent, type, null);
      }
   }

   public static enum ElementType {
      ALL,
      HELMET,
      PORTAL,
      CROSSHAIRS,
      BOSSHEALTH,
      BOSSINFO,
      ARMOR,
      HEALTH,
      FOOD,
      AIR,
      HOTBAR,
      EXPERIENCE,
      TEXT,
      HEALTHMOUNT,
      JUMPBAR,
      CHAT,
      PLAYER_LIST,
      DEBUG,
      POTION_ICONS,
      SUBTITLES,
      FPS_GRAPH,
      VIGNETTE;
   }
}
