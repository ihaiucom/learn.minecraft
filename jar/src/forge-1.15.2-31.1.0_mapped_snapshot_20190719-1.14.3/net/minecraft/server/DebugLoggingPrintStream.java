package net.minecraft.server;

import java.io.OutputStream;
import net.minecraft.util.LoggingPrintStream;

public class DebugLoggingPrintStream extends LoggingPrintStream {
   public DebugLoggingPrintStream(String p_i47315_1_, OutputStream p_i47315_2_) {
      super(p_i47315_1_, p_i47315_2_);
   }

   protected void logString(String p_179882_1_) {
      StackTraceElement[] lvt_2_1_ = Thread.currentThread().getStackTrace();
      StackTraceElement lvt_3_1_ = lvt_2_1_[Math.min(3, lvt_2_1_.length)];
      LOGGER.info("[{}]@.({}:{}): {}", this.domain, lvt_3_1_.getFileName(), lvt_3_1_.getLineNumber(), p_179882_1_);
   }
}
