package net.minecraftforge.fml.javafmlmod;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraftforge.eventbus.EventBusErrorMessage;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventListener;
import net.minecraftforge.fml.AutomaticEventSubscriber;
import net.minecraftforge.fml.LifecycleEventProvider;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FMLModContainer extends ModContainer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ModFileScanData scanResults;
   private final IEventBus eventBus;
   private Object modInstance;
   private final Class<?> modClass;

   public FMLModContainer(IModInfo info, String className, ClassLoader modClassLoader, ModFileScanData modFileScanResults) {
      super(info);
      LOGGER.debug(Logging.LOADING, "Creating FMLModContainer instance for {} with classLoader {} & {}", className, modClassLoader, this.getClass().getClassLoader());
      this.scanResults = modFileScanResults;
      this.triggerMap.put(ModLoadingStage.CONSTRUCT, this.dummy().andThen(this::beforeEvent).andThen(this::constructMod).andThen(this::afterEvent));
      this.triggerMap.put(ModLoadingStage.CREATE_REGISTRIES, this.dummy().andThen(this::beforeEvent).andThen(this::fireEvent).andThen(this::afterEvent));
      this.triggerMap.put(ModLoadingStage.LOAD_REGISTRIES, this.dummy().andThen(this::beforeEvent).andThen(this::fireEvent).andThen(this::afterEvent));
      this.triggerMap.put(ModLoadingStage.COMMON_SETUP, this.dummy().andThen(this::beforeEvent).andThen(this::preinitMod).andThen(this::fireEvent).andThen(this::afterEvent));
      this.triggerMap.put(ModLoadingStage.SIDED_SETUP, this.dummy().andThen(this::beforeEvent).andThen(this::fireEvent).andThen(this::afterEvent));
      this.triggerMap.put(ModLoadingStage.ENQUEUE_IMC, this.dummy().andThen(this::beforeEvent).andThen(this::initMod).andThen(this::fireEvent).andThen(this::afterEvent));
      this.triggerMap.put(ModLoadingStage.PROCESS_IMC, this.dummy().andThen(this::beforeEvent).andThen(this::fireEvent).andThen(this::afterEvent));
      this.triggerMap.put(ModLoadingStage.COMPLETE, this.dummy().andThen(this::beforeEvent).andThen(this::completeLoading).andThen(this::fireEvent).andThen(this::afterEvent));
      this.triggerMap.put(ModLoadingStage.GATHERDATA, this.dummy().andThen(this::beforeEvent).andThen(this::fireEvent).andThen(this::afterEvent));
      this.eventBus = BusBuilder.builder().setExceptionHandler(this::onEventFailed).setTrackPhases(false).build();
      this.configHandler = Optional.of((event) -> {
         this.eventBus.post(event);
      });
      FMLJavaModLoadingContext contextExtension = new FMLJavaModLoadingContext(this);
      this.contextExtension = () -> {
         return contextExtension;
      };

      try {
         this.modClass = Class.forName(className, true, modClassLoader);
         LOGGER.debug(Logging.LOADING, "Loaded modclass {} with {}", this.modClass.getName(), this.modClass.getClassLoader());
      } catch (Throwable var7) {
         LOGGER.error(Logging.LOADING, "Failed to load class {}", className, var7);
         throw new ModLoadingException(info, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmodclass", var7, new Object[0]);
      }
   }

   private void completeLoading(LifecycleEventProvider.LifecycleEvent lifecycleEvent) {
   }

   private void initMod(LifecycleEventProvider.LifecycleEvent lifecycleEvent) {
   }

   private Consumer<LifecycleEventProvider.LifecycleEvent> dummy() {
      return (s) -> {
      };
   }

   private void onEventFailed(IEventBus iEventBus, Event event, IEventListener[] iEventListeners, int i, Throwable throwable) {
      LOGGER.error(new EventBusErrorMessage(event, i, iEventListeners, throwable));
   }

   private void beforeEvent(LifecycleEventProvider.LifecycleEvent lifecycleEvent) {
   }

   private void fireEvent(LifecycleEventProvider.LifecycleEvent lifecycleEvent) {
      Event event = lifecycleEvent.getOrBuildEvent(this);
      LOGGER.debug(Logging.LOADING, "Firing event for modid {} : {}", this.getModId(), event);

      try {
         this.eventBus.post(event);
         LOGGER.debug(Logging.LOADING, "Fired event for modid {} : {}", this.getModId(), event);
      } catch (Throwable var4) {
         LOGGER.error(Logging.LOADING, "Caught exception during event {} dispatch for modid {}", event, this.getModId(), var4);
         throw new ModLoadingException(this.modInfo, lifecycleEvent.fromStage(), "fml.modloading.errorduringevent", var4, new Object[0]);
      }
   }

   private void afterEvent(LifecycleEventProvider.LifecycleEvent lifecycleEvent) {
      if (this.getCurrentState() == ModLoadingStage.ERROR) {
         LOGGER.error(Logging.LOADING, "An error occurred while dispatching event {} to {}", lifecycleEvent.fromStage(), this.getModId());
      }

   }

   private void preinitMod(LifecycleEventProvider.LifecycleEvent lifecycleEvent) {
   }

   private void constructMod(LifecycleEventProvider.LifecycleEvent event) {
      try {
         LOGGER.debug(Logging.LOADING, "Loading mod instance {} of type {}", this.getModId(), this.modClass.getName());
         this.modInstance = this.modClass.newInstance();
         LOGGER.debug(Logging.LOADING, "Loaded mod instance {} of type {}", this.getModId(), this.modClass.getName());
      } catch (Throwable var4) {
         LOGGER.error(Logging.LOADING, "Failed to create mod instance. ModID: {}, class {}", this.getModId(), this.modClass.getName(), var4);
         throw new ModLoadingException(this.modInfo, event.fromStage(), "fml.modloading.failedtoloadmod", var4, new Object[]{this.modClass});
      }

      try {
         LOGGER.debug(Logging.LOADING, "Injecting Automatic event subscribers for {}", this.getModId());
         AutomaticEventSubscriber.inject(this, this.scanResults, this.modClass.getClassLoader());
         LOGGER.debug(Logging.LOADING, "Completed Automatic event subscribers for {}", this.getModId());
      } catch (Throwable var3) {
         LOGGER.error(Logging.LOADING, "Failed to register automatic subscribers. ModID: {}, class {}", this.getModId(), this.modClass.getName(), var3);
         throw new ModLoadingException(this.modInfo, event.fromStage(), "fml.modloading.failedtoloadmod", var3, new Object[]{this.modClass});
      }
   }

   public boolean matches(Object mod) {
      return mod == this.modInstance;
   }

   public Object getMod() {
      return this.modInstance;
   }

   public IEventBus getEventBus() {
      return this.eventBus;
   }

   protected void acceptEvent(Event e) {
      this.eventBus.post(e);
   }
}
