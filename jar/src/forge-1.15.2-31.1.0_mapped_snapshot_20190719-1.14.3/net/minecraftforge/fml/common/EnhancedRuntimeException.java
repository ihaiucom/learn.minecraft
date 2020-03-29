package net.minecraftforge.fml.common;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class EnhancedRuntimeException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public EnhancedRuntimeException() {
   }

   public EnhancedRuntimeException(String message) {
      super(message);
   }

   public EnhancedRuntimeException(String message, Throwable cause) {
      super(message, cause);
   }

   public EnhancedRuntimeException(Throwable cause) {
      super(cause);
   }

   public String getMessage() {
      StackTraceElement[] stack = Thread.currentThread().getStackTrace();
      if (stack.length > 2 && stack[2].getClassName().startsWith("org.apache.logging.log4j.")) {
         final StringWriter buf = new StringWriter();
         String msg = super.getMessage();
         if (msg != null) {
            buf.append(msg);
         }

         buf.append('\n');
         this.printStackTrace(new EnhancedRuntimeException.WrappedPrintStream() {
            public void println(String line) {
               buf.append(line).append('\n');
            }
         });
         return buf.toString();
      } else {
         return super.getMessage();
      }
   }

   public void printStackTrace(final PrintWriter s) {
      this.printStackTrace(new EnhancedRuntimeException.WrappedPrintStream() {
         public void println(String line) {
            s.println(line);
         }
      });
      super.printStackTrace(s);
   }

   public void printStackTrace(final PrintStream s) {
      this.printStackTrace(new EnhancedRuntimeException.WrappedPrintStream() {
         public void println(String line) {
            s.println(line);
         }
      });
      super.printStackTrace(s);
   }

   protected abstract void printStackTrace(EnhancedRuntimeException.WrappedPrintStream var1);

   public abstract static class WrappedPrintStream {
      public abstract void println(String var1);
   }
}
