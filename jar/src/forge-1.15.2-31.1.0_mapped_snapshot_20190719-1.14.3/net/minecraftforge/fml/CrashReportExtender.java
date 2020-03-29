package net.minecraftforge.fml;

import cpw.mods.modlauncher.log.TransformingThrowablePatternConverter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraftforge.fml.common.ICrashCallable;

public class CrashReportExtender {
   private static List<ICrashCallable> crashCallables = Collections.synchronizedList(new ArrayList());

   public static void enhanceCrashReport(CrashReport crashReport, CrashReportCategory category) {
      Iterator var2 = crashCallables.iterator();

      while(var2.hasNext()) {
         ICrashCallable call = (ICrashCallable)var2.next();
         category.addDetail(call.getLabel(), (ICrashReportDetail)call);
      }

   }

   public static void registerCrashCallable(ICrashCallable callable) {
      crashCallables.add(callable);
   }

   public static void registerCrashCallable(final String headerName, final Callable<String> reportGenerator) {
      registerCrashCallable(new ICrashCallable() {
         public String getLabel() {
            return headerName;
         }

         public String call() throws Exception {
            return (String)reportGenerator.call();
         }
      });
   }

   public static void addCrashReportHeader(StringBuilder stringbuilder, CrashReport crashReport) {
   }

   public static String generateEnhancedStackTrace(Throwable throwable) {
      return TransformingThrowablePatternConverter.generateEnhancedStackTrace(throwable);
   }
}
