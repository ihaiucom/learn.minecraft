package net.minecraftforge.fml;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.forgespi.language.ILifecycleEvent;

public enum LifecycleEventProvider {
   CONSTRUCT(() -> {
      return new LifecycleEventProvider.LifecycleEvent(ModLoadingStage.CONSTRUCT);
   }),
   CREATE_REGISTRIES(() -> {
      return new LifecycleEventProvider.LifecycleEvent(ModLoadingStage.CREATE_REGISTRIES);
   }, ModList.inlineDispatcher),
   LOAD_REGISTRIES(() -> {
      return new LifecycleEventProvider.LifecycleEvent(ModLoadingStage.LOAD_REGISTRIES, LifecycleEventProvider.LifecycleEvent.Progression.STAY);
   }, ModList.inlineDispatcher),
   SETUP(() -> {
      return new LifecycleEventProvider.LifecycleEvent(ModLoadingStage.COMMON_SETUP);
   }),
   SIDED_SETUP(() -> {
      return new LifecycleEventProvider.LifecycleEvent(ModLoadingStage.SIDED_SETUP);
   }),
   ENQUEUE_IMC(() -> {
      return new LifecycleEventProvider.LifecycleEvent(ModLoadingStage.ENQUEUE_IMC);
   }),
   PROCESS_IMC(() -> {
      return new LifecycleEventProvider.LifecycleEvent(ModLoadingStage.PROCESS_IMC);
   }),
   COMPLETE(() -> {
      return new LifecycleEventProvider.LifecycleEvent(ModLoadingStage.COMPLETE);
   }),
   GATHERDATA(() -> {
      return new LifecycleEventProvider.GatherDataLifecycleEvent(ModLoadingStage.GATHERDATA);
   }, ModList.inlineDispatcher);

   private final Supplier<? extends LifecycleEventProvider.LifecycleEvent> event;
   private final LifecycleEventProvider.EventHandler<LifecycleEventProvider.LifecycleEvent, Consumer<List<ModLoadingException>>, Executor, Runnable> eventDispatcher;
   private Supplier<Event> customEventSupplier;
   private LifecycleEventProvider.LifecycleEvent.Progression progression;

   private LifecycleEventProvider(Supplier<? extends LifecycleEventProvider.LifecycleEvent> e) {
      this(e, ModList.parallelDispatcher);
   }

   private LifecycleEventProvider(Supplier<? extends LifecycleEventProvider.LifecycleEvent> e, LifecycleEventProvider.EventHandler<LifecycleEventProvider.LifecycleEvent, Consumer<List<ModLoadingException>>, Executor, Runnable> eventDispatcher) {
      this.progression = LifecycleEventProvider.LifecycleEvent.Progression.NEXT;
      this.event = e;
      this.eventDispatcher = eventDispatcher;
   }

   public void setCustomEventSupplier(Supplier<Event> eventSupplier) {
      this.customEventSupplier = eventSupplier;
   }

   public void changeProgression(LifecycleEventProvider.LifecycleEvent.Progression progression) {
      this.progression = progression;
   }

   public void dispatch(Consumer<List<ModLoadingException>> errorHandler, Executor executor, Runnable ticker) {
      LifecycleEventProvider.LifecycleEvent lifecycleEvent = (LifecycleEventProvider.LifecycleEvent)this.event.get();
      lifecycleEvent.setCustomEventSupplier(this.customEventSupplier);
      lifecycleEvent.changeProgression(this.progression);
      this.eventDispatcher.dispatchEvent(lifecycleEvent, errorHandler, executor, ticker);
   }

   public interface EventHandler<T extends LifecycleEventProvider.LifecycleEvent, U extends Consumer<? extends List<? super ModLoadingException>>, V extends Executor, R extends Runnable> {
      void dispatchEvent(T var1, U var2, V var3, R var4);
   }

   private static class GatherDataLifecycleEvent extends LifecycleEventProvider.LifecycleEvent {
      GatherDataLifecycleEvent(ModLoadingStage stage) {
         super(stage);
      }

      public ModLoadingStage fromStage() {
         return ModLoadingStage.COMMON_SETUP;
      }

      public ModLoadingStage toStage() {
         return ModLoadingStage.DONE;
      }
   }

   public static class LifecycleEvent implements ILifecycleEvent<LifecycleEventProvider.LifecycleEvent> {
      private final ModLoadingStage stage;
      private Supplier<Event> customEventSupplier;
      private LifecycleEventProvider.LifecycleEvent.Progression progression;

      LifecycleEvent(ModLoadingStage stage) {
         this(stage, LifecycleEventProvider.LifecycleEvent.Progression.NEXT);
      }

      LifecycleEvent(ModLoadingStage stage, LifecycleEventProvider.LifecycleEvent.Progression progression) {
         this.stage = stage;
         this.progression = progression;
      }

      public ModLoadingStage fromStage() {
         return this.stage;
      }

      public ModLoadingStage toStage() {
         return this.progression.apply(this.stage);
      }

      public void setCustomEventSupplier(Supplier<Event> customEventSupplier) {
         this.customEventSupplier = customEventSupplier;
      }

      public void changeProgression(LifecycleEventProvider.LifecycleEvent.Progression p) {
         this.progression = p;
      }

      public Event getOrBuildEvent(ModContainer modContainer) {
         return (Event)(this.customEventSupplier != null ? (Event)this.customEventSupplier.get() : this.stage.getModEvent(modContainer));
      }

      public String toString() {
         return "LifecycleEvent:" + this.stage;
      }

      public static enum Progression {
         NEXT((current) -> {
            return ModLoadingStage.values()[current.ordinal() + 1];
         }),
         STAY(Function.identity());

         private final Function<ModLoadingStage, ModLoadingStage> edge;

         private Progression(Function<ModLoadingStage, ModLoadingStage> edge) {
            this.edge = edge;
         }

         public ModLoadingStage apply(ModLoadingStage in) {
            return (ModLoadingStage)this.edge.apply(in);
         }
      }
   }
}
