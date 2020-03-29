package net.minecraftforge.fml;

import java.util.concurrent.Callable;
import java.util.function.Supplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public final class DistExecutor {
   private DistExecutor() {
   }

   public static <T> T callWhenOn(Dist dist, Supplier<Callable<T>> toRun) {
      if (dist == FMLEnvironment.dist) {
         try {
            return ((Callable)toRun.get()).call();
         } catch (Exception var3) {
            throw new RuntimeException(var3);
         }
      } else {
         return null;
      }
   }

   public static void runWhenOn(Dist dist, Supplier<Runnable> toRun) {
      if (dist == FMLEnvironment.dist) {
         ((Runnable)toRun.get()).run();
      }

   }

   public static <T> T runForDist(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
      switch(FMLEnvironment.dist) {
      case CLIENT:
         return ((Supplier)clientTarget.get()).get();
      case DEDICATED_SERVER:
         return ((Supplier)serverTarget.get()).get();
      default:
         throw new IllegalArgumentException("UNSIDED?");
      }
   }
}
