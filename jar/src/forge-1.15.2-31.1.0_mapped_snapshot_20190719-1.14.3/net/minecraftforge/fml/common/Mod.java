package net.minecraftforge.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Mod {
   String value();

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.TYPE})
   public @interface EventBusSubscriber {
      Dist[] value() default {Dist.CLIENT, Dist.DEDICATED_SERVER};

      String modid() default "";

      Mod.EventBusSubscriber.Bus bus() default Mod.EventBusSubscriber.Bus.FORGE;

      public static enum Bus {
         FORGE(() -> {
            return MinecraftForge.EVENT_BUS;
         }),
         MOD(() -> {
            return FMLJavaModLoadingContext.get().getModEventBus();
         });

         private final Supplier<IEventBus> busSupplier;

         private Bus(Supplier<IEventBus> eventBusSupplier) {
            this.busSupplier = eventBusSupplier;
         }

         public Supplier<IEventBus> bus() {
            return this.busSupplier;
         }
      }
   }
}
