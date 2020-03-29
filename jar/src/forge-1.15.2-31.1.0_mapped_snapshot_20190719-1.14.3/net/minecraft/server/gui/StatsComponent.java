package net.minecraft.server.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.Timer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;

public class StatsComponent extends JComponent {
   private static final DecimalFormat FORMATTER = (DecimalFormat)Util.make(new DecimalFormat("########0.000"), (p_212730_0_) -> {
      p_212730_0_.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   });
   private final int[] values = new int[256];
   private int vp;
   private final String[] msgs = new String[11];
   private final MinecraftServer server;
   private final Timer field_219054_f;

   public StatsComponent(MinecraftServer p_i2367_1_) {
      this.server = p_i2367_1_;
      this.setPreferredSize(new Dimension(456, 246));
      this.setMinimumSize(new Dimension(456, 246));
      this.setMaximumSize(new Dimension(456, 246));
      this.field_219054_f = new Timer(500, (p_210466_1_) -> {
         this.tick();
      });
      this.field_219054_f.start();
      this.setBackground(Color.BLACK);
   }

   private void tick() {
      long lvt_1_1_ = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
      this.msgs[0] = "Memory use: " + lvt_1_1_ / 1024L / 1024L + " mb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)";
      this.msgs[1] = "Avg tick: " + FORMATTER.format(this.mean(this.server.tickTimeArray) * 1.0E-6D) + " ms";
      this.values[this.vp++ & 255] = (int)(lvt_1_1_ * 100L / Runtime.getRuntime().maxMemory());
      this.repaint();
   }

   private double mean(long[] p_120035_1_) {
      long lvt_2_1_ = 0L;
      long[] var4 = p_120035_1_;
      int var5 = p_120035_1_.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         long lvt_7_1_ = var4[var6];
         lvt_2_1_ += lvt_7_1_;
      }

      return (double)lvt_2_1_ / (double)p_120035_1_.length;
   }

   public void paint(Graphics p_paint_1_) {
      p_paint_1_.setColor(new Color(16777215));
      p_paint_1_.fillRect(0, 0, 456, 246);

      int lvt_2_2_;
      for(lvt_2_2_ = 0; lvt_2_2_ < 256; ++lvt_2_2_) {
         int lvt_3_1_ = this.values[lvt_2_2_ + this.vp & 255];
         p_paint_1_.setColor(new Color(lvt_3_1_ + 28 << 16));
         p_paint_1_.fillRect(lvt_2_2_, 100 - lvt_3_1_, 1, lvt_3_1_);
      }

      p_paint_1_.setColor(Color.BLACK);

      for(lvt_2_2_ = 0; lvt_2_2_ < this.msgs.length; ++lvt_2_2_) {
         String lvt_3_2_ = this.msgs[lvt_2_2_];
         if (lvt_3_2_ != null) {
            p_paint_1_.drawString(lvt_3_2_, 32, 116 + lvt_2_2_ * 16);
         }
      }

   }

   public void func_219053_a() {
      this.field_219054_f.stop();
   }
}
