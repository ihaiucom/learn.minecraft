package net.minecraftforge.client.model;

public class ModelLoadingException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public ModelLoadingException(String message) {
      super(message);
   }

   public ModelLoadingException(String message, Throwable cause) {
      super(message, cause);
   }
}
