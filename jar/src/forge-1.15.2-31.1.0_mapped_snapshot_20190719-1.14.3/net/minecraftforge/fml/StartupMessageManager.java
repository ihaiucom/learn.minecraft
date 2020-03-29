package net.minecraftforge.fml;

public class StartupMessageManager {
   public static void addModMessage(String message) {
      net.minecraftforge.fml.loading.progress.StartupMessageManager.addModMessage(message);
   }
}
