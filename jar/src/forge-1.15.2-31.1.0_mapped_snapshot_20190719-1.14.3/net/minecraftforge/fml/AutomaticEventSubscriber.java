package net.minecraftforge.fml;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation.EnumHolder;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

public class AutomaticEventSubscriber {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Type AUTO_SUBSCRIBER = Type.getType(Mod.EventBusSubscriber.class);

   public static void inject(ModContainer mod, ModFileScanData scanData, ClassLoader loader) {
      if (scanData != null) {
         LOGGER.debug(Logging.LOADING, "Attempting to inject @EventBusSubscriber classes into the eventbus for {}", mod.getModId());
         List<AnnotationData> ebsTargets = (List)scanData.getAnnotations().stream().filter((annotationData) -> {
            return AUTO_SUBSCRIBER.equals(annotationData.getAnnotationType());
         }).collect(Collectors.toList());
         ebsTargets.forEach((ad) -> {
            List<EnumHolder> sidesValue = (List)ad.getAnnotationData().getOrDefault("value", Arrays.asList(new EnumHolder((String)null, "CLIENT"), new EnumHolder((String)null, "DEDICATED_SERVER")));
            EnumSet<Dist> sides = (EnumSet)sidesValue.stream().map((eh) -> {
               return Dist.valueOf(eh.getValue());
            }).collect(Collectors.toCollection(() -> {
               return EnumSet.noneOf(Dist.class);
            }));
            String modId = (String)ad.getAnnotationData().getOrDefault("modid", mod.getModId());
            EnumHolder busTargetHolder = (EnumHolder)ad.getAnnotationData().getOrDefault("bus", new EnumHolder((String)null, "FORGE"));
            Mod.EventBusSubscriber.Bus busTarget = Mod.EventBusSubscriber.Bus.valueOf(busTargetHolder.getValue());
            if (Objects.equals(mod.getModId(), modId) && sides.contains(FMLEnvironment.dist)) {
               try {
                  LOGGER.debug(Logging.LOADING, "Auto-subscribing {} to {}", ad.getClassType().getClassName(), busTarget);
                  ((IEventBus)busTarget.bus().get()).register(Class.forName(ad.getClassType().getClassName(), true, loader));
               } catch (ClassNotFoundException var9) {
                  LOGGER.fatal(Logging.LOADING, "Failed to load mod class {} for @EventBusSubscriber annotation", ad.getClassType(), var9);
                  throw new RuntimeException(var9);
               }
            }

         });
      }
   }
}
