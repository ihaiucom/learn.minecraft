package net.minecraftforge.fml.javafmlmod;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.forgespi.language.ILifecycleEvent;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.IModLanguageProvider;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.language.IModLanguageProvider.IModLanguageLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

public class FMLJavaModLanguageProvider implements IModLanguageProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Type MODANNOTATION = Type.getType("Lnet/minecraftforge/fml/common/Mod;");

   public String name() {
      return "javafml";
   }

   public Consumer<ModFileScanData> getFileVisitor() {
      return (scanResult) -> {
         Map<String, FMLJavaModLanguageProvider.FMLModTarget> modTargetMap = (Map)scanResult.getAnnotations().stream().filter((ad) -> {
            return ad.getAnnotationType().equals(MODANNOTATION);
         }).peek((ad) -> {
            LOGGER.debug(Logging.SCAN, "Found @Mod class {} with id {}", ad.getClassType().getClassName(), ad.getAnnotationData().get("value"));
         }).map((ad) -> {
            return new FMLJavaModLanguageProvider.FMLModTarget(ad.getClassType().getClassName(), (String)ad.getAnnotationData().get("value"));
         }).collect(Collectors.toMap(FMLJavaModLanguageProvider.FMLModTarget::getModId, Function.identity(), (a, b) -> {
            return a;
         }));
         scanResult.addLanguageLoader(modTargetMap);
      };
   }

   public <R extends ILifecycleEvent<R>> void consumeLifecycleEvent(Supplier<R> consumeEvent) {
   }

   private static class FMLModTarget implements IModLanguageLoader {
      private static final Logger LOGGER;
      private final String className;
      private final String modId;

      private FMLModTarget(String className, String modId) {
         this.className = className;
         this.modId = modId;
      }

      public String getModId() {
         return this.modId;
      }

      public <T> T loadMod(IModInfo info, ClassLoader modClassLoader, ModFileScanData modFileScanResults) {
         try {
            Class<?> fmlContainer = Class.forName("net.minecraftforge.fml.javafmlmod.FMLModContainer", true, Thread.currentThread().getContextClassLoader());
            LOGGER.debug(Logging.LOADING, "Loading FMLModContainer from classloader {} - got {}", Thread.currentThread().getContextClassLoader(), fmlContainer.getClassLoader());
            Constructor<?> constructor = fmlContainer.getConstructor(IModInfo.class, String.class, ClassLoader.class, ModFileScanData.class);
            return constructor.newInstance(info, this.className, modClassLoader, modFileScanResults);
         } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException var6) {
            LOGGER.fatal(Logging.LOADING, "Unable to load FMLModContainer, wut?", var6);
            throw new RuntimeException(var6);
         }
      }

      // $FF: synthetic method
      FMLModTarget(String x0, String x1, Object x2) {
         this(x0, x1);
      }

      static {
         LOGGER = FMLJavaModLanguageProvider.LOGGER;
      }
   }
}
