package net.minecraftforge.fml;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;

public enum ModLoadingStage {
   ERROR((Supplier)null),
   VALIDATE((Supplier)null),
   CONSTRUCT((Supplier)null),
   CREATE_REGISTRIES((Supplier)null),
   LOAD_REGISTRIES((Supplier)null),
   COMMON_SETUP(() -> {
      return FMLCommonSetupEvent::new;
   }),
   SIDED_SETUP,
   ENQUEUE_IMC,
   PROCESS_IMC,
   COMPLETE,
   DONE,
   GATHERDATA;

   private final Supplier<Function<ModContainer, ModLifecycleEvent>> modLifecycleEventFunction;

   private ModLoadingStage(Supplier<Function<ModContainer, ModLifecycleEvent>> modLifecycleEventFunction) {
      this.modLifecycleEventFunction = modLifecycleEventFunction;
   }

   public ModLifecycleEvent getModEvent(ModContainer modContainer) {
      return (ModLifecycleEvent)((Function)this.modLifecycleEventFunction.get()).apply(modContainer);
   }

   static {
      SidedProvider var10004 = SidedProvider.SIDED_SETUP_EVENT;
      var10004.getClass();
      SIDED_SETUP = new ModLoadingStage("SIDED_SETUP", 6, var10004::get);
      ENQUEUE_IMC = new ModLoadingStage("ENQUEUE_IMC", 7, () -> {
         return InterModEnqueueEvent::new;
      });
      PROCESS_IMC = new ModLoadingStage("PROCESS_IMC", 8, () -> {
         return InterModProcessEvent::new;
      });
      COMPLETE = new ModLoadingStage("COMPLETE", 9, () -> {
         return FMLLoadCompleteEvent::new;
      });
      DONE = new ModLoadingStage("DONE", 10, (Supplier)null);
      ModLoader var0 = ModLoader.get();
      var0.getClass();
      GATHERDATA = new ModLoadingStage("GATHERDATA", 11, var0::getDataGeneratorEvent);
   }
}
