package net.minecraftforge.fml.common;

public class LoaderException extends EnhancedRuntimeException {
   private static final long serialVersionUID = -5675297950958861378L;

   public LoaderException(Throwable wrapped) {
      super(wrapped);
   }

   public LoaderException() {
   }

   public LoaderException(String message) {
      super(message);
   }

   public LoaderException(String message, Throwable cause) {
      super(message, cause);
   }

   protected void printStackTrace(EnhancedRuntimeException.WrappedPrintStream stream) {
   }
}
