package net.minecraftforge.registries;

import com.google.common.collect.Maps;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

public class ObjectHolderRegistry {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Set<Consumer<Predicate<ResourceLocation>>> objectHolders = new HashSet();
   private static final Type OBJECT_HOLDER = Type.getType(ObjectHolder.class);
   private static final Type MOD = Type.getType(Mod.class);

   public static void addHandler(Consumer<Predicate<ResourceLocation>> ref) {
      objectHolders.add(ref);
   }

   public static boolean removeHandler(Consumer<Predicate<ResourceLocation>> ref) {
      return objectHolders.remove(ref);
   }

   public static void findObjectHolders() {
      LOGGER.debug(ForgeRegistry.REGISTRIES, "Processing ObjectHolder annotations");
      List<AnnotationData> annotations = (List)ModList.get().getAllScanData().stream().map(ModFileScanData::getAnnotations).flatMap(Collection::stream).filter((a) -> {
         return OBJECT_HOLDER.equals(a.getAnnotationType()) || MOD.equals(a.getAnnotationType());
      }).collect(Collectors.toList());
      Map<Type, String> classModIds = Maps.newHashMap();
      Map<Type, Class<?>> classCache = Maps.newHashMap();
      annotations.stream().filter((a) -> {
         return MOD.equals(a.getAnnotationType());
      }).forEach((data) -> {
         String var10000 = (String)classModIds.put(data.getClassType(), (String)data.getAnnotationData().get("value"));
      });
      annotations.stream().filter((a) -> {
         return OBJECT_HOLDER.equals(a.getAnnotationType());
      }).filter((a) -> {
         return a.getTargetType() == ElementType.TYPE;
      }).forEach((data) -> {
         scanTarget(classModIds, classCache, data.getClassType(), (String)null, (String)data.getAnnotationData().get("value"), true, data.getClassType().getClassName().startsWith("net.minecraft."));
      });
      annotations.stream().filter((a) -> {
         return OBJECT_HOLDER.equals(a.getAnnotationType());
      }).filter((a) -> {
         return a.getTargetType() == ElementType.FIELD;
      }).forEach((data) -> {
         scanTarget(classModIds, classCache, data.getClassType(), data.getMemberName(), (String)data.getAnnotationData().get("value"), false, false);
      });
      LOGGER.debug(ForgeRegistry.REGISTRIES, "Found {} ObjectHolder annotations", objectHolders.size());
   }

   private static void scanTarget(Map<Type, String> classModIds, Map<Type, Class<?>> classCache, Type type, @Nullable String annotationTarget, String value, boolean isClass, boolean extractFromValue) {
      Class clazz;
      if (classCache.containsKey(type)) {
         clazz = (Class)classCache.get(type);
      } else {
         try {
            clazz = Class.forName(type.getClassName(), extractFromValue, ObjectHolderRegistry.class.getClassLoader());
            classCache.put(type, clazz);
         } catch (ClassNotFoundException var11) {
            throw new RuntimeException(var11);
         }
      }

      if (isClass) {
         scanClassForFields(classModIds, type, value, clazz, extractFromValue);
      } else {
         if (value.indexOf(58) == -1) {
            String prefix = (String)classModIds.get(type);
            if (prefix == null) {
               LOGGER.warn(ForgeRegistry.REGISTRIES, "Found an unqualified ObjectHolder annotation ({}) without a modid context at {}.{}, ignoring", value, type, annotationTarget);
               throw new IllegalStateException("Unqualified reference to ObjectHolder");
            }

            value = prefix + ':' + value;
         }

         try {
            Field f = clazz.getDeclaredField(annotationTarget);
            ObjectHolderRef ref = new ObjectHolderRef(f, value, extractFromValue);
            if (ref.isValid()) {
               addHandler(ref);
            }
         } catch (NoSuchFieldException var10) {
            throw new RuntimeException(var10);
         }
      }

   }

   private static void scanClassForFields(Map<Type, String> classModIds, Type targetClass, String value, Class<?> clazz, boolean extractFromExistingValues) {
      classModIds.put(targetClass, value);
      int flags = true;
      Field[] var6 = clazz.getFields();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Field f = var6[var8];
         if ((f.getModifiers() & 4105) == 4105 && !f.isAnnotationPresent(ObjectHolder.class)) {
            ObjectHolderRef ref = new ObjectHolderRef(f, value + ':' + f.getName().toLowerCase(Locale.ENGLISH), extractFromExistingValues);
            if (ref.isValid()) {
               addHandler(ref);
            }
         }
      }

   }

   public static void applyObjectHolders() {
      LOGGER.debug(ForgeRegistry.REGISTRIES, "Applying holder lookups");
      applyObjectHolders((key) -> {
         return true;
      });
      LOGGER.debug(ForgeRegistry.REGISTRIES, "Holder lookups applied");
   }

   public static void applyObjectHolders(Predicate<ResourceLocation> filter) {
      objectHolders.forEach((e) -> {
         e.accept(filter);
      });
   }
}
