package net.minecraft.profiler;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FilledProfileResult implements IProfileResult {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final IProfilerSection field_230090_b_ = new IProfilerSection() {
      public long func_230037_a_() {
         return 0L;
      }

      public long func_230038_b_() {
         return 0L;
      }

      public Object2LongMap<String> func_230039_c_() {
         return Object2LongMaps.emptyMap();
      }
   };
   private static final Splitter field_230091_c_ = Splitter.on('\u001e');
   private static final Comparator<Entry<String, FilledProfileResult.Section>> field_230092_d_ = Entry.comparingByValue(Comparator.comparingLong((p_230096_0_) -> {
      return p_230096_0_.field_230108_b_;
   })).reversed();
   private final Map<String, ? extends IProfilerSection> field_230093_e_;
   private final long timeStop;
   private final int ticksStop;
   private final long timeStart;
   private final int ticksStart;
   private final int field_223509_h;

   public FilledProfileResult(Map<String, ? extends IProfilerSection> p_i50407_1_, long p_i50407_2_, int p_i50407_4_, long p_i50407_5_, int p_i50407_7_) {
      this.field_230093_e_ = p_i50407_1_;
      this.timeStop = p_i50407_2_;
      this.ticksStop = p_i50407_4_;
      this.timeStart = p_i50407_5_;
      this.ticksStart = p_i50407_7_;
      this.field_223509_h = p_i50407_7_ - p_i50407_4_;
   }

   private IProfilerSection func_230104_c_(String p_230104_1_) {
      IProfilerSection lvt_2_1_ = (IProfilerSection)this.field_230093_e_.get(p_230104_1_);
      return lvt_2_1_ != null ? lvt_2_1_ : field_230090_b_;
   }

   public List<DataPoint> getDataPoints(String p_219917_1_) {
      IProfilerSection lvt_3_1_ = this.func_230104_c_("root");
      long lvt_4_1_ = lvt_3_1_.func_230037_a_();
      IProfilerSection lvt_6_1_ = this.func_230104_c_(p_219917_1_);
      long lvt_7_1_ = lvt_6_1_.func_230037_a_();
      long lvt_9_1_ = lvt_6_1_.func_230038_b_();
      List<DataPoint> lvt_11_1_ = Lists.newArrayList();
      if (!p_219917_1_.isEmpty()) {
         p_219917_1_ = p_219917_1_ + '\u001e';
      }

      long lvt_12_1_ = 0L;
      Iterator var14 = this.field_230093_e_.keySet().iterator();

      while(var14.hasNext()) {
         String lvt_15_1_ = (String)var14.next();
         if (func_230097_a_(p_219917_1_, lvt_15_1_)) {
            lvt_12_1_ += this.func_230104_c_(lvt_15_1_).func_230037_a_();
         }
      }

      float lvt_14_1_ = (float)lvt_12_1_;
      if (lvt_12_1_ < lvt_7_1_) {
         lvt_12_1_ = lvt_7_1_;
      }

      if (lvt_4_1_ < lvt_12_1_) {
         lvt_4_1_ = lvt_12_1_;
      }

      Iterator var26 = this.field_230093_e_.keySet().iterator();

      while(var26.hasNext()) {
         String lvt_16_1_ = (String)var26.next();
         if (func_230097_a_(p_219917_1_, lvt_16_1_)) {
            IProfilerSection lvt_17_1_ = this.func_230104_c_(lvt_16_1_);
            long lvt_18_1_ = lvt_17_1_.func_230037_a_();
            double lvt_20_1_ = (double)lvt_18_1_ * 100.0D / (double)lvt_12_1_;
            double lvt_22_1_ = (double)lvt_18_1_ * 100.0D / (double)lvt_4_1_;
            String lvt_24_1_ = lvt_16_1_.substring(p_219917_1_.length());
            lvt_11_1_.add(new DataPoint(lvt_24_1_, lvt_20_1_, lvt_22_1_, lvt_17_1_.func_230038_b_()));
         }
      }

      if ((float)lvt_12_1_ > lvt_14_1_) {
         lvt_11_1_.add(new DataPoint("unspecified", (double)((float)lvt_12_1_ - lvt_14_1_) * 100.0D / (double)lvt_12_1_, (double)((float)lvt_12_1_ - lvt_14_1_) * 100.0D / (double)lvt_4_1_, lvt_9_1_));
      }

      Collections.sort(lvt_11_1_);
      lvt_11_1_.add(0, new DataPoint(p_219917_1_, 100.0D, (double)lvt_12_1_ * 100.0D / (double)lvt_4_1_, lvt_9_1_));
      return lvt_11_1_;
   }

   private static boolean func_230097_a_(String p_230097_0_, String p_230097_1_) {
      return p_230097_1_.length() > p_230097_0_.length() && p_230097_1_.startsWith(p_230097_0_) && p_230097_1_.indexOf(30, p_230097_0_.length() + 1) < 0;
   }

   private Map<String, FilledProfileResult.Section> func_230106_h_() {
      Map<String, FilledProfileResult.Section> lvt_1_1_ = Maps.newTreeMap();
      this.field_230093_e_.forEach((p_230101_1_, p_230101_2_) -> {
         Object2LongMap<String> lvt_3_1_ = p_230101_2_.func_230039_c_();
         if (!lvt_3_1_.isEmpty()) {
            List<String> lvt_4_1_ = field_230091_c_.splitToList(p_230101_1_);
            lvt_3_1_.forEach((p_230103_2_, p_230103_3_) -> {
               ((FilledProfileResult.Section)lvt_1_1_.computeIfAbsent(p_230103_2_, (p_230105_0_) -> {
                  return new FilledProfileResult.Section();
               })).func_230112_a_(lvt_4_1_.iterator(), p_230103_3_);
            });
         }

      });
      return lvt_1_1_;
   }

   public long timeStop() {
      return this.timeStop;
   }

   public int ticksStop() {
      return this.ticksStop;
   }

   public long timeStart() {
      return this.timeStart;
   }

   public int ticksStart() {
      return this.ticksStart;
   }

   public boolean writeToFile(File p_219919_1_) {
      p_219919_1_.getParentFile().mkdirs();
      OutputStreamWriter lvt_2_1_ = null;

      boolean var4;
      try {
         lvt_2_1_ = new OutputStreamWriter(new FileOutputStream(p_219919_1_), StandardCharsets.UTF_8);
         lvt_2_1_.write(this.inlineIntoCrashReport(this.nanoTime(), this.ticksSpend()));
         boolean var3 = true;
         return var3;
      } catch (Throwable var8) {
         LOGGER.error("Could not save profiler results to {}", p_219919_1_, var8);
         var4 = false;
      } finally {
         IOUtils.closeQuietly(lvt_2_1_);
      }

      return var4;
   }

   protected String inlineIntoCrashReport(long p_219929_1_, int p_219929_3_) {
      StringBuilder lvt_4_1_ = new StringBuilder();
      lvt_4_1_.append("---- Minecraft Profiler Results ----\n");
      lvt_4_1_.append("// ");
      lvt_4_1_.append(getWittyString());
      lvt_4_1_.append("\n\n");
      lvt_4_1_.append("Version: ").append(SharedConstants.getVersion().getId()).append('\n');
      lvt_4_1_.append("Time span: ").append(p_219929_1_ / 1000000L).append(" ms\n");
      lvt_4_1_.append("Tick span: ").append(p_219929_3_).append(" ticks\n");
      lvt_4_1_.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", (float)p_219929_3_ / ((float)p_219929_1_ / 1.0E9F))).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
      lvt_4_1_.append("--- BEGIN PROFILE DUMP ---\n\n");
      this.format(0, "root", lvt_4_1_);
      lvt_4_1_.append("--- END PROFILE DUMP ---\n\n");
      Map<String, FilledProfileResult.Section> lvt_5_1_ = this.func_230106_h_();
      if (!lvt_5_1_.isEmpty()) {
         lvt_4_1_.append("--- BEGIN COUNTER DUMP ---\n\n");
         this.func_230102_a_(lvt_5_1_, lvt_4_1_, p_219929_3_);
         lvt_4_1_.append("--- END COUNTER DUMP ---\n\n");
      }

      return lvt_4_1_.toString();
   }

   private static StringBuilder func_230098_a_(StringBuilder p_230098_0_, int p_230098_1_) {
      p_230098_0_.append(String.format("[%02d] ", p_230098_1_));

      for(int lvt_2_1_ = 0; lvt_2_1_ < p_230098_1_; ++lvt_2_1_) {
         p_230098_0_.append("|   ");
      }

      return p_230098_0_;
   }

   private void format(int p_219928_1_, String p_219928_2_, StringBuilder p_219928_3_) {
      List<DataPoint> lvt_4_1_ = this.getDataPoints(p_219928_2_);
      Object2LongMap<String> lvt_5_1_ = ((IProfilerSection)this.field_230093_e_.get(p_219928_2_)).func_230039_c_();
      lvt_5_1_.forEach((p_230100_3_, p_230100_4_) -> {
         func_230098_a_(p_219928_3_, p_219928_1_).append('#').append(p_230100_3_).append(' ').append(p_230100_4_).append('/').append(p_230100_4_ / (long)this.field_223509_h).append('\n');
      });
      if (lvt_4_1_.size() >= 3) {
         for(int lvt_6_1_ = 1; lvt_6_1_ < lvt_4_1_.size(); ++lvt_6_1_) {
            DataPoint lvt_7_1_ = (DataPoint)lvt_4_1_.get(lvt_6_1_);
            func_230098_a_(p_219928_3_, p_219928_1_).append(lvt_7_1_.name).append('(').append(lvt_7_1_.field_223511_c).append('/').append(String.format(Locale.ROOT, "%.0f", (float)lvt_7_1_.field_223511_c / (float)this.field_223509_h)).append(')').append(" - ").append(String.format(Locale.ROOT, "%.2f", lvt_7_1_.relTime)).append("%/").append(String.format(Locale.ROOT, "%.2f", lvt_7_1_.rootRelTime)).append("%\n");
            if (!"unspecified".equals(lvt_7_1_.name)) {
               try {
                  this.format(p_219928_1_ + 1, p_219928_2_ + '\u001e' + lvt_7_1_.name, p_219928_3_);
               } catch (Exception var9) {
                  p_219928_3_.append("[[ EXCEPTION ").append(var9).append(" ]]");
               }
            }
         }

      }
   }

   private void func_230095_a_(int p_230095_1_, String p_230095_2_, FilledProfileResult.Section p_230095_3_, int p_230095_4_, StringBuilder p_230095_5_) {
      func_230098_a_(p_230095_5_, p_230095_1_).append(p_230095_2_).append(" total:").append(p_230095_3_.field_230107_a_).append('/').append(p_230095_3_.field_230108_b_).append(" average: ").append(p_230095_3_.field_230107_a_ / (long)p_230095_4_).append('/').append(p_230095_3_.field_230108_b_ / (long)p_230095_4_).append('\n');
      p_230095_3_.field_230109_c_.entrySet().stream().sorted(field_230092_d_).forEach((p_230094_4_) -> {
         this.func_230095_a_(p_230095_1_ + 1, (String)p_230094_4_.getKey(), (FilledProfileResult.Section)p_230094_4_.getValue(), p_230095_4_, p_230095_5_);
      });
   }

   private void func_230102_a_(Map<String, FilledProfileResult.Section> p_230102_1_, StringBuilder p_230102_2_, int p_230102_3_) {
      p_230102_1_.forEach((p_230099_3_, p_230099_4_) -> {
         p_230102_2_.append("-- Counter: ").append(p_230099_3_).append(" --\n");
         this.func_230095_a_(0, "root", (FilledProfileResult.Section)p_230099_4_.field_230109_c_.get("root"), p_230102_3_, p_230102_2_);
         p_230102_2_.append("\n\n");
      });
   }

   private static String getWittyString() {
      String[] lvt_0_1_ = new String[]{"Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};

      try {
         return lvt_0_1_[(int)(Util.nanoTime() % (long)lvt_0_1_.length)];
      } catch (Throwable var2) {
         return "Witty comment unavailable :(";
      }
   }

   public int ticksSpend() {
      return this.field_223509_h;
   }

   static class Section {
      private long field_230107_a_;
      private long field_230108_b_;
      private final Map<String, FilledProfileResult.Section> field_230109_c_;

      private Section() {
         this.field_230109_c_ = Maps.newHashMap();
      }

      public void func_230112_a_(Iterator<String> p_230112_1_, long p_230112_2_) {
         this.field_230108_b_ += p_230112_2_;
         if (!p_230112_1_.hasNext()) {
            this.field_230107_a_ += p_230112_2_;
         } else {
            ((FilledProfileResult.Section)this.field_230109_c_.computeIfAbsent(p_230112_1_.next(), (p_230111_0_) -> {
               return new FilledProfileResult.Section();
            })).func_230112_a_(p_230112_1_, p_230112_2_);
         }

      }

      // $FF: synthetic method
      Section(Object p_i230049_1_) {
         this();
      }
   }
}
