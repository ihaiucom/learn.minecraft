package net.minecraftforge.fml.common;

public class LoaderExceptionModCrash extends LoaderException {
   private static final long serialVersionUID = 1L;

   public LoaderExceptionModCrash(String message, Throwable cause) {
      super(message, cause);
   }

   public synchronized Throwable fillInStackTrace() {
      return this;
   }
}
