package net.minecraftforge.fml.mclanguageprovider;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.forgespi.language.ILifecycleEvent;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.IModLanguageProvider;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.language.IModLanguageProvider.IModLanguageLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftModLanguageProvider implements IModLanguageProvider {
   private static final Logger LOGGER = LogManager.getLogger();

   public String name() {
      return "minecraft";
   }

   public Consumer<ModFileScanData> getFileVisitor() {
      return (sd) -> {
         sd.addLanguageLoader(Collections.singletonMap("minecraft", new MinecraftModLanguageProvider.MinecraftModTarget()));
      };
   }

   public <R extends ILifecycleEvent<R>> void consumeLifecycleEvent(Supplier<R> consumeEvent) {
   }

   public static class MinecraftModContainer extends ModContainer {
      private static final String MCMODINSTANCE = "minecraft, the mod";

      public MinecraftModContainer(IModInfo info) {
         super(info);
         this.contextExtension = () -> {
            return null;
         };
      }

      public boolean matches(Object mod) {
         return Objects.equals(mod, "minecraft, the mod");
      }

      public Object getMod() {
         return "minecraft, the mod";
      }
   }

   public static class MinecraftModTarget implements IModLanguageLoader {
      public <T> T loadMod(IModInfo info, ClassLoader modClassLoader, ModFileScanData modFileScanResults) {
         try {
            Class<?> mcModClass = Class.forName("net.minecraftforge.fml.mclanguageprovider.MinecraftModLanguageProvider$MinecraftModContainer", true, modClassLoader);
            return mcModClass.getConstructor(IModInfo.class).newInstance(info);
         } catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException var5) {
            MinecraftModLanguageProvider.LOGGER.fatal(Logging.LOADING, "Unable to load MinecraftModContainer, wut?", var5);
            throw new RuntimeException(var5);
         }
      }
   }
}
