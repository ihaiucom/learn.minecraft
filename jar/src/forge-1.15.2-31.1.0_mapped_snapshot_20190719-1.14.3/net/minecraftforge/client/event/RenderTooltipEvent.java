package net.minecraftforge.client.event;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public abstract class RenderTooltipEvent extends Event {
   @Nonnull
   protected final ItemStack stack;
   protected final List<String> lines;
   protected int x;
   protected int y;
   protected FontRenderer fr;

   public RenderTooltipEvent(@Nonnull ItemStack stack, @Nonnull List<String> lines, int x, int y, @Nonnull FontRenderer fr) {
      this.stack = stack;
      this.lines = Collections.unmodifiableList(lines);
      this.x = x;
      this.y = y;
      this.fr = fr;
   }

   @Nonnull
   public ItemStack getStack() {
      return this.stack;
   }

   @Nonnull
   public List<String> getLines() {
      return this.lines;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   @Nonnull
   public FontRenderer getFontRenderer() {
      return this.fr;
   }

   public static class Color extends RenderTooltipEvent {
      private final int originalBackground;
      private final int originalBorderStart;
      private final int originalBorderEnd;
      private int background;
      private int borderStart;
      private int borderEnd;

      public Color(@Nonnull ItemStack stack, @Nonnull List<String> textLines, int x, int y, @Nonnull FontRenderer fr, int background, int borderStart, int borderEnd) {
         super(stack, textLines, x, y, fr);
         this.originalBackground = background;
         this.originalBorderStart = borderStart;
         this.originalBorderEnd = borderEnd;
         this.background = background;
         this.borderStart = borderStart;
         this.borderEnd = borderEnd;
      }

      public int getBackground() {
         return this.background;
      }

      public void setBackground(int background) {
         this.background = background;
      }

      public int getBorderStart() {
         return this.borderStart;
      }

      public void setBorderStart(int borderStart) {
         this.borderStart = borderStart;
      }

      public int getBorderEnd() {
         return this.borderEnd;
      }

      public void setBorderEnd(int borderEnd) {
         this.borderEnd = borderEnd;
      }

      public int getOriginalBackground() {
         return this.originalBackground;
      }

      public int getOriginalBorderStart() {
         return this.originalBorderStart;
      }

      public int getOriginalBorderEnd() {
         return this.originalBorderEnd;
      }
   }

   public static class PostText extends RenderTooltipEvent.Post {
      public PostText(@Nonnull ItemStack stack, @Nonnull List<String> textLines, int x, int y, @Nonnull FontRenderer fr, int width, int height) {
         super(stack, textLines, x, y, fr, width, height);
      }
   }

   public static class PostBackground extends RenderTooltipEvent.Post {
      public PostBackground(@Nonnull ItemStack stack, @Nonnull List<String> textLines, int x, int y, @Nonnull FontRenderer fr, int width, int height) {
         super(stack, textLines, x, y, fr, width, height);
      }
   }

   protected abstract static class Post extends RenderTooltipEvent {
      private final int width;
      private final int height;

      public Post(@Nonnull ItemStack stack, @Nonnull List<String> textLines, int x, int y, @Nonnull FontRenderer fr, int width, int height) {
         super(stack, textLines, x, y, fr);
         this.width = width;
         this.height = height;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }
   }

   @Cancelable
   public static class Pre extends RenderTooltipEvent {
      private int screenWidth;
      private int screenHeight;
      private int maxWidth;

      public Pre(@Nonnull ItemStack stack, @Nonnull List<String> lines, int x, int y, int screenWidth, int screenHeight, int maxWidth, @Nonnull FontRenderer fr) {
         super(stack, lines, x, y, fr);
         this.screenWidth = screenWidth;
         this.screenHeight = screenHeight;
         this.maxWidth = maxWidth;
      }

      public int getScreenWidth() {
         return this.screenWidth;
      }

      public void setScreenWidth(int screenWidth) {
         this.screenWidth = screenWidth;
      }

      public int getScreenHeight() {
         return this.screenHeight;
      }

      public void setScreenHeight(int screenHeight) {
         this.screenHeight = screenHeight;
      }

      public int getMaxWidth() {
         return this.maxWidth;
      }

      public void setMaxWidth(int maxWidth) {
         this.maxWidth = maxWidth;
      }

      public void setFontRenderer(@Nonnull FontRenderer fr) {
         this.fr = fr;
      }

      public void setX(int x) {
         this.x = x;
      }

      public void setY(int y) {
         this.y = y;
      }
   }
}
