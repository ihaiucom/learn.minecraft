package net.minecraftforge.fml.common;

import com.google.common.base.Preconditions;
import cpw.mods.modlauncher.api.INameMappingService.Domain;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.StringJoiner;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ObfuscationReflectionHelper {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker REFLECTION = MarkerManager.getMarker("REFLECTION");

   @Nonnull
   public static String remapName(Domain domain, String name) {
      return (String)FMLLoader.getNameFunction("srg").map((f) -> {
         return (String)f.apply(domain, name);
      }).orElse(name);
   }

   @Nullable
   public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, String fieldName) {
      try {
         return findField(classToAccess, fieldName).get(instance);
      } catch (ObfuscationReflectionHelper.UnableToFindFieldException var4) {
         LOGGER.error(REFLECTION, "Unable to locate field {} ({}) on type {}", fieldName, remapName(Domain.FIELD, fieldName), classToAccess.getName(), var4);
         throw var4;
      } catch (IllegalAccessException var5) {
         LOGGER.error(REFLECTION, "Unable to access field {} ({}) on type {}", fieldName, remapName(Domain.FIELD, fieldName), classToAccess.getName(), var5);
         throw new ObfuscationReflectionHelper.UnableToAccessFieldException(var5);
      }
   }

   public static <T, E> void setPrivateValue(@Nonnull Class<? super T> classToAccess, @Nonnull T instance, @Nullable E value, @Nonnull String fieldName) {
      try {
         findField(classToAccess, fieldName).set(instance, value);
      } catch (ObfuscationReflectionHelper.UnableToFindFieldException var5) {
         LOGGER.error("Unable to locate any field {} on type {}", fieldName, classToAccess.getName(), var5);
         throw var5;
      } catch (IllegalAccessException var6) {
         LOGGER.error("Unable to set any field {} on type {}", fieldName, classToAccess.getName(), var6);
         throw new ObfuscationReflectionHelper.UnableToAccessFieldException(var6);
      }
   }

   @Nonnull
   public static Method findMethod(@Nonnull Class<?> clazz, @Nonnull String methodName, @Nonnull Class<?>... parameterTypes) {
      Preconditions.checkNotNull(clazz, "Class to find method on cannot be null.");
      Preconditions.checkNotNull(methodName, "Name of method to find cannot be null.");
      Preconditions.checkArgument(!methodName.isEmpty(), "Name of method to find cannot be empty.");
      Preconditions.checkNotNull(parameterTypes, "Parameter types of method to find cannot be null.");

      try {
         Method m = clazz.getDeclaredMethod(remapName(Domain.METHOD, methodName), parameterTypes);
         m.setAccessible(true);
         return m;
      } catch (Exception var4) {
         throw new ObfuscationReflectionHelper.UnableToFindMethodException(var4);
      }
   }

   @Nonnull
   public static <T> Constructor<T> findConstructor(@Nonnull Class<T> clazz, @Nonnull Class<?>... parameterTypes) {
      Preconditions.checkNotNull(clazz, "Class to find constructor on cannot be null.");
      Preconditions.checkNotNull(parameterTypes, "Parameter types of constructor to find cannot be null.");

      try {
         Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
         constructor.setAccessible(true);
         return constructor;
      } catch (NoSuchMethodException var9) {
         StringBuilder desc = new StringBuilder();
         desc.append(clazz.getSimpleName());
         StringJoiner joiner = new StringJoiner(", ", "(", ")");
         Class[] var5 = parameterTypes;
         int var6 = parameterTypes.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Class<?> type = var5[var7];
            joiner.add(type.getSimpleName());
         }

         desc.append(joiner);
         throw new ObfuscationReflectionHelper.UnknownConstructorException("Could not find constructor '" + desc.toString() + "' in " + clazz);
      }
   }

   @Nonnull
   public static <T> Field findField(@Nonnull Class<? super T> clazz, @Nonnull String fieldName) {
      Preconditions.checkNotNull(clazz, "Class to find field on cannot be null.");
      Preconditions.checkNotNull(fieldName, "Name of field to find cannot be null.");
      Preconditions.checkArgument(!fieldName.isEmpty(), "Name of field to find cannot be empty.");

      try {
         Field f = clazz.getDeclaredField(remapName(Domain.FIELD, fieldName));
         f.setAccessible(true);
         return f;
      } catch (Exception var3) {
         throw new ObfuscationReflectionHelper.UnableToFindFieldException(var3);
      }
   }

   public static class UnknownConstructorException extends RuntimeException {
      public UnknownConstructorException(String message) {
         super(message);
      }
   }

   public static class UnableToFindMethodException extends RuntimeException {
      public UnableToFindMethodException(Throwable failed) {
         super(failed);
      }
   }

   public static class UnableToFindFieldException extends RuntimeException {
      private UnableToFindFieldException(Exception e) {
         super(e);
      }

      // $FF: synthetic method
      UnableToFindFieldException(Exception x0, Object x1) {
         this(x0);
      }
   }

   public static class UnableToAccessFieldException extends RuntimeException {
      private UnableToAccessFieldException(Exception e) {
         super(e);
      }

      // $FF: synthetic method
      UnableToAccessFieldException(Exception x0, Object x1) {
         this(x0);
      }
   }
}
