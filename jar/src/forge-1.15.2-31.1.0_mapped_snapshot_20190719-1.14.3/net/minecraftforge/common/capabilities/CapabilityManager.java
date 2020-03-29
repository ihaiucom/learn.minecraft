package net.minecraftforge.common.capabilities;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

public enum CapabilityManager {
   INSTANCE;

   private static final Logger LOGGER = LogManager.getLogger();
   private static final Type CAP_INJECT = Type.getType(CapabilityInject.class);
   private final IdentityHashMap<String, Capability<?>> providers = new IdentityHashMap();
   private volatile IdentityHashMap<String, List<Function<Capability<?>, Object>>> callbacks;

   public <T> void register(Class<T> type, Capability.IStorage<T> storage, Callable<? extends T> factory) {
      Objects.requireNonNull(type, "Attempted to register a capability with invalid type");
      Objects.requireNonNull(storage, "Attempted to register a capability with no storage implementation");
      Objects.requireNonNull(factory, "Attempted to register a capability with no default implementation factory");
      String realName = type.getName().intern();
      Capability cap;
      synchronized(this.providers) {
         if (this.providers.containsKey(realName)) {
            LOGGER.error(Logging.CAPABILITIES, "Cannot register capability implementation multiple times : {}", realName);
            throw new IllegalArgumentException("Cannot register a capability implementation multiple times : " + realName);
         }

         cap = new Capability(realName, storage, factory);
         this.providers.put(realName, cap);
      }

      ((List)this.callbacks.getOrDefault(realName, Collections.emptyList())).forEach((func) -> {
         func.apply(cap);
      });
   }

   public void injectCapabilities(List<ModFileScanData> data) {
      List<AnnotationData> capabilities = (List)data.stream().map(ModFileScanData::getAnnotations).flatMap(Collection::stream).filter((a) -> {
         return CAP_INJECT.equals(a.getAnnotationType());
      }).collect(Collectors.toList());
      IdentityHashMap<String, List<Function<Capability<?>, Object>>> m = new IdentityHashMap();
      capabilities.forEach((entry) -> {
         attachCapabilityToMethod(m, entry);
      });
      this.callbacks = m;
   }

   private static void attachCapabilityToMethod(Map<String, List<Function<Capability<?>, Object>>> cbs, AnnotationData entry) {
      String targetClass = entry.getClassType().getClassName();
      String targetName = entry.getMemberName();
      Type type = (Type)entry.getAnnotationData().get("value");
      if (type == null) {
         LOGGER.warn(Logging.CAPABILITIES, "Unable to inject capability at {}.{} (Invalid Annotation)", targetClass, targetName);
      } else {
         String capabilityName = type.getInternalName().replace('/', '.').intern();
         List<Function<Capability<?>, Object>> list = (List)cbs.computeIfAbsent(capabilityName, (k) -> {
            return new ArrayList();
         });
         if (entry.getMemberName().indexOf(40) > 0) {
            list.add((input) -> {
               try {
                  Method[] var4 = Class.forName(targetClass).getDeclaredMethods();
                  int var5 = var4.length;

                  for(int var6 = 0; var6 < var5; ++var6) {
                     Method mtd = var4[var6];
                     if (targetName.equals(mtd.getName() + Type.getMethodDescriptor(mtd))) {
                        if ((mtd.getModifiers() & 8) != 8) {
                           LOGGER.warn(Logging.CAPABILITIES, "Unable to inject capability {} at {}.{} (Non-Static)", capabilityName, targetClass, targetName);
                           return null;
                        }

                        mtd.setAccessible(true);
                        mtd.invoke((Object)null, input);
                        return null;
                     }
                  }

                  LOGGER.warn(Logging.CAPABILITIES, "Unable to inject capability {} at {}.{} (Method Not Found)", capabilityName, targetClass, targetName);
               } catch (Exception var8) {
                  LOGGER.warn(Logging.CAPABILITIES, "Unable to inject capability {} at {}.{}", capabilityName, targetClass, targetName, var8);
               }

               return null;
            });
         } else {
            list.add((input) -> {
               try {
                  Field field = Class.forName(targetClass).getDeclaredField(targetName);
                  if ((field.getModifiers() & 8) != 8) {
                     LOGGER.warn(Logging.CAPABILITIES, "Unable to inject capability {} at {}.{} (Non-Static)", capabilityName, targetClass, targetName);
                     return null;
                  }

                  field.setAccessible(true);
                  field.set((Object)null, input);
               } catch (Exception var5) {
                  LOGGER.warn(Logging.CAPABILITIES, "Unable to inject capability {} at {}.{}", capabilityName, targetClass, targetName, var5);
               }

               return null;
            });
         }

      }
   }
}
