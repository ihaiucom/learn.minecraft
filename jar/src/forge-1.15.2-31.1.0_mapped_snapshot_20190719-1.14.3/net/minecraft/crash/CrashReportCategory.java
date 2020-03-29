package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CrashReportCategory {
   private final CrashReport crashReport;
   private final String name;
   private final List<CrashReportCategory.Entry> children = Lists.newArrayList();
   private StackTraceElement[] stackTrace = new StackTraceElement[0];

   public CrashReportCategory(CrashReport p_i1353_1_, String p_i1353_2_) {
      this.crashReport = p_i1353_1_;
      this.name = p_i1353_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public static String getCoordinateInfo(double p_85074_0_, double p_85074_2_, double p_85074_4_) {
      return String.format(Locale.ROOT, "%.2f,%.2f,%.2f - %s", p_85074_0_, p_85074_2_, p_85074_4_, getCoordinateInfo(new BlockPos(p_85074_0_, p_85074_2_, p_85074_4_)));
   }

   public static String getCoordinateInfo(BlockPos p_180522_0_) {
      return getCoordinateInfo(p_180522_0_.getX(), p_180522_0_.getY(), p_180522_0_.getZ());
   }

   public static String getCoordinateInfo(int p_184876_0_, int p_184876_1_, int p_184876_2_) {
      StringBuilder stringbuilder = new StringBuilder();

      try {
         stringbuilder.append(String.format("World: (%d,%d,%d)", p_184876_0_, p_184876_1_, p_184876_2_));
      } catch (Throwable var16) {
         stringbuilder.append("(Error finding world loc)");
      }

      stringbuilder.append(", ");

      int k2;
      int l2;
      int i3;
      int j3;
      int k3;
      int l3;
      int i4;
      int j4;
      int k4;
      try {
         k2 = p_184876_0_ >> 4;
         l2 = p_184876_2_ >> 4;
         i3 = p_184876_0_ & 15;
         j3 = p_184876_1_ >> 4;
         k3 = p_184876_2_ & 15;
         l3 = k2 << 4;
         i4 = l2 << 4;
         j4 = (k2 + 1 << 4) - 1;
         k4 = (l2 + 1 << 4) - 1;
         stringbuilder.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", i3, j3, k3, k2, l2, l3, i4, j4, k4));
      } catch (Throwable var15) {
         stringbuilder.append("(Error finding chunk loc)");
      }

      stringbuilder.append(", ");

      try {
         k2 = p_184876_0_ >> 9;
         l2 = p_184876_2_ >> 9;
         i3 = k2 << 5;
         j3 = l2 << 5;
         k3 = (k2 + 1 << 5) - 1;
         l3 = (l2 + 1 << 5) - 1;
         i4 = k2 << 9;
         j4 = l2 << 9;
         k4 = (k2 + 1 << 9) - 1;
         int j2 = (l2 + 1 << 9) - 1;
         stringbuilder.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", k2, l2, i3, j3, k3, l3, i4, j4, k4, j2));
      } catch (Throwable var14) {
         stringbuilder.append("(Error finding world loc)");
      }

      return stringbuilder.toString();
   }

   public CrashReportCategory addDetail(String p_189529_1_, ICrashReportDetail<String> p_189529_2_) {
      try {
         this.addDetail(p_189529_1_, p_189529_2_.call());
      } catch (Throwable var4) {
         this.addCrashSectionThrowable(p_189529_1_, var4);
      }

      return this;
   }

   public CrashReportCategory addDetail(String p_71507_1_, Object p_71507_2_) {
      this.children.add(new CrashReportCategory.Entry(p_71507_1_, p_71507_2_));
      return this;
   }

   public void addCrashSectionThrowable(String p_71499_1_, Throwable p_71499_2_) {
      this.addDetail(p_71499_1_, (Object)p_71499_2_);
   }

   public int getPrunedStackTrace(int p_85073_1_) {
      StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();
      if (astacktraceelement.length <= 0) {
         return 0;
      } else {
         int len = astacktraceelement.length - 3 - p_85073_1_;
         if (len <= 0) {
            len = astacktraceelement.length;
         }

         this.stackTrace = new StackTraceElement[len];
         System.arraycopy(astacktraceelement, astacktraceelement.length - len, this.stackTrace, 0, this.stackTrace.length);
         return this.stackTrace.length;
      }
   }

   public boolean firstTwoElementsOfStackTraceMatch(StackTraceElement p_85069_1_, StackTraceElement p_85069_2_) {
      if (this.stackTrace.length != 0 && p_85069_1_ != null) {
         StackTraceElement stacktraceelement = this.stackTrace[0];
         if (stacktraceelement.isNativeMethod() == p_85069_1_.isNativeMethod() && stacktraceelement.getClassName().equals(p_85069_1_.getClassName()) && stacktraceelement.getFileName().equals(p_85069_1_.getFileName()) && stacktraceelement.getMethodName().equals(p_85069_1_.getMethodName())) {
            if (p_85069_2_ != null != this.stackTrace.length > 1) {
               return false;
            } else if (p_85069_2_ != null && !this.stackTrace[1].equals(p_85069_2_)) {
               return false;
            } else {
               this.stackTrace[0] = p_85069_1_;
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void trimStackTraceEntriesFromBottom(int p_85070_1_) {
      StackTraceElement[] astacktraceelement = new StackTraceElement[this.stackTrace.length - p_85070_1_];
      System.arraycopy(this.stackTrace, 0, astacktraceelement, 0, astacktraceelement.length);
      this.stackTrace = astacktraceelement;
   }

   public void appendToStringBuilder(StringBuilder p_85072_1_) {
      p_85072_1_.append("-- ").append(this.name).append(" --\n");
      p_85072_1_.append("Details:");
      Iterator var2 = this.children.iterator();

      while(var2.hasNext()) {
         CrashReportCategory.Entry crashreportcategory$entry = (CrashReportCategory.Entry)var2.next();
         p_85072_1_.append("\n\t");
         p_85072_1_.append(crashreportcategory$entry.getKey());
         p_85072_1_.append(": ");
         p_85072_1_.append(crashreportcategory$entry.getValue());
      }

      if (this.stackTrace != null && this.stackTrace.length > 0) {
         p_85072_1_.append("\nStacktrace:");
         StackTraceElement[] var6 = this.stackTrace;
         int var7 = var6.length;

         for(int var4 = 0; var4 < var7; ++var4) {
            StackTraceElement stacktraceelement = var6[var4];
            p_85072_1_.append("\n\tat ");
            p_85072_1_.append(stacktraceelement);
         }
      }

   }

   public StackTraceElement[] getStackTrace() {
      return this.stackTrace;
   }

   public static void addBlockInfo(CrashReportCategory p_175750_0_, BlockPos p_175750_1_, @Nullable BlockState p_175750_2_) {
      if (p_175750_2_ != null) {
         p_175750_0_.addDetail("Block", p_175750_2_::toString);
      }

      p_175750_0_.addDetail("Block location", () -> {
         return getCoordinateInfo(p_175750_1_);
      });
   }

   static class Entry {
      private final String key;
      private final String value;

      public Entry(String p_i1352_1_, Object p_i1352_2_) {
         this.key = p_i1352_1_;
         if (p_i1352_2_ == null) {
            this.value = "~~NULL~~";
         } else if (p_i1352_2_ instanceof Throwable) {
            Throwable throwable = (Throwable)p_i1352_2_;
            this.value = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
         } else {
            this.value = p_i1352_2_.toString();
         }

      }

      public String getKey() {
         return this.key;
      }

      public String getValue() {
         return this.value;
      }
   }
}
