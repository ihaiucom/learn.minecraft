package net.minecraftforge.fml;

import java.util.List;
import java.util.stream.Collectors;

public class LoadingFailedException extends RuntimeException {
   private final List<ModLoadingException> loadingExceptions;

   public LoadingFailedException(List<ModLoadingException> loadingExceptions) {
      this.loadingExceptions = loadingExceptions;
   }

   public List<ModLoadingException> getErrors() {
      return this.loadingExceptions;
   }

   public String getMessage() {
      return "Loading errors encountered: " + (String)this.loadingExceptions.stream().map(ModLoadingException::getMessage).collect(Collectors.joining(",\n\t", "[\n\t", "\n]"));
   }
}
