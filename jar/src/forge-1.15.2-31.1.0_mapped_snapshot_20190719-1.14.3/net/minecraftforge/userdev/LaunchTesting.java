package net.minecraftforge.userdev;

import com.google.common.base.Strings;
import com.mojang.authlib.Agent;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import cpw.mods.modlauncher.Launcher;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;

public class LaunchTesting {
   public static void main(String... args) throws InterruptedException {
      String markerselection = System.getProperty("forge.logging.markers", "");
      Arrays.stream(markerselection.split(",")).forEach((marker) -> {
         System.setProperty("forge.logging.marker." + marker.toLowerCase(Locale.ROOT), "ACCEPT");
      });
      ArgumentList lst = ArgumentList.from(args);
      String target = lst.getOrDefault("launchTarget", (String)System.getenv().get("target"));
      if (target == null) {
         throw new IllegalArgumentException("Environment variable target must be set.");
      } else {
         lst.putLazy("gameDir", ".");
         lst.putLazy("launchTarget", target);
         lst.putLazy("fml.mcpVersion", System.getenv("MCP_VERSION"));
         lst.putLazy("fml.mcVersion", System.getenv("MC_VERSION"));
         lst.putLazy("fml.forgeGroup", System.getenv("FORGE_GROUP"));
         lst.putLazy("fml.forgeVersion", System.getenv("FORGE_VERSION"));
         if (target.contains("client")) {
            hackNatives();
            lst.putLazy("version", "MOD_DEV");
            lst.putLazy("assetIndex", System.getenv("assetIndex"));
            lst.putLazy("assetsDir", (String)System.getenv().getOrDefault("assetDirectory", "assets"));
            String assets = lst.get("assetsDir");
            if (assets == null || !(new File(assets)).exists()) {
               throw new IllegalArgumentException("Environment variable 'assetDirectory' must be set to a valid path.");
            }

            if (!lst.hasValue("accessToken") && !login(lst)) {
               String username = lst.get("username");
               if (username == null) {
                  lst.putLazy("username", "Dev");
               } else {
                  Matcher m = Pattern.compile("#+").matcher(username);
                  StringBuffer replaced = new StringBuffer();

                  while(m.find()) {
                     m.appendReplacement(replaced, getRandomNumbers(m.group().length()));
                  }

                  m.appendTail(replaced);
                  lst.put("username", replaced.toString());
               }

               lst.put("accessToken", "DONT_CRASH");
               lst.put("userProperties", "{}");
            }
         }

         if (Arrays.asList("fmldevclient", "fmldevserver", "fmldevdata", "fmluserdevclient", "fmluserdevserver", "fmluserdevdata").contains(target)) {
            Launcher.main(lst.getArguments());
            Thread.sleep(10000L);
         } else {
            throw new IllegalArgumentException("Unknown value for 'target' property: " + target);
         }
      }
   }

   private static String getRandomNumbers(int length) {
      return Long.toString(System.nanoTime() % (long)((int)Math.pow(10.0D, (double)length)));
   }

   private static void hackNatives() {
      String paths = System.getProperty("java.library.path");
      String nativesDir = (String)System.getenv().get("nativesDirectory");
      if (Strings.isNullOrEmpty(paths)) {
         paths = nativesDir;
      } else {
         paths = paths + File.pathSeparator + nativesDir;
      }

      System.setProperty("java.library.path", paths);

      try {
         Method initializePathMethod = ClassLoader.class.getDeclaredMethod("initializePath", String.class);
         initializePathMethod.setAccessible(true);
         Object usrPathsValue = initializePathMethod.invoke((Object)null, "java.library.path");
         Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
         usrPathsField.setAccessible(true);
         usrPathsField.set((Object)null, usrPathsValue);
      } catch (Throwable var5) {
      }

   }

   private static boolean login(ArgumentList args) {
      if (args.hasValue("username") && args.hasValue("password")) {
         UserAuthentication auth = (new YggdrasilAuthenticationService(Proxy.NO_PROXY, "1")).createUserAuthentication(Agent.MINECRAFT);
         auth.setUsername(args.get("username"));
         auth.setPassword(args.remove("password"));

         try {
            auth.logIn();
         } catch (AuthenticationException var3) {
            LogManager.getLogger().error("Login failed!", var3);
            throw new RuntimeException(var3);
         }

         args.put("username", auth.getSelectedProfile().getName());
         args.put("uuid", auth.getSelectedProfile().getId().toString().replace("-", ""));
         args.put("accessToken", auth.getAuthenticatedToken());
         args.put("userProperties", auth.getUserProperties().toString());
         return true;
      } else {
         args.remove("password");
         return false;
      }
   }
}
