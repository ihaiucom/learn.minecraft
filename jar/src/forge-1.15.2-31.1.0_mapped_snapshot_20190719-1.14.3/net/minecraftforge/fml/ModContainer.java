package net.minecraftforge.fml;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.lang3.tuple.Pair;

public abstract class ModContainer {
   protected final String modId;
   protected final String namespace;
   protected final IModInfo modInfo;
   protected ModLoadingStage modLoadingStage;
   protected Supplier<?> contextExtension;
   protected final Map<ModLoadingStage, Consumer<LifecycleEventProvider.LifecycleEvent>> triggerMap;
   protected final Map<ExtensionPoint, Supplier<?>> extensionPoints = new IdentityHashMap();
   protected final EnumMap<ModConfig.Type, ModConfig> configs = new EnumMap(ModConfig.Type.class);
   protected Optional<Consumer<ModConfig.ModConfigEvent>> configHandler = Optional.empty();

   public ModContainer(IModInfo info) {
      this.modId = info.getModId();
      this.namespace = this.modId;
      this.modInfo = info;
      this.triggerMap = new HashMap();
      this.modLoadingStage = ModLoadingStage.CONSTRUCT;
      this.registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> {
         return Pair.of(() -> {
            return this.modInfo.getVersion().toString();
         }, (incoming, isNetwork) -> {
            return Objects.equals(incoming, this.modInfo.getVersion().toString());
         });
      });
   }

   public final String getModId() {
      return this.modId;
   }

   public final String getNamespace() {
      return this.namespace;
   }

   public ModLoadingStage getCurrentState() {
      return this.modLoadingStage;
   }

   public final void transitionState(LifecycleEventProvider.LifecycleEvent event, Consumer<List<ModLoadingException>> errorHandler) {
      if (this.modLoadingStage == event.fromStage()) {
         try {
            ModLoadingContext.get().setActiveContainer(this, this.contextExtension.get());
            ((Consumer)this.triggerMap.getOrDefault(this.modLoadingStage, (e) -> {
            })).accept(event);
            this.modLoadingStage = event.toStage();
            ModLoadingContext.get().setActiveContainer((ModContainer)null, (Object)null);
         } catch (ModLoadingException var4) {
            this.modLoadingStage = ModLoadingStage.ERROR;
            errorHandler.accept(Collections.singletonList(var4));
         }
      }

   }

   public IModInfo getModInfo() {
      return this.modInfo;
   }

   public <T> Optional<T> getCustomExtension(ExtensionPoint<T> point) {
      return Optional.ofNullable(((Supplier)this.extensionPoints.getOrDefault(point, () -> {
         return null;
      })).get());
   }

   public <T> void registerExtensionPoint(ExtensionPoint<T> point, Supplier<T> extension) {
      this.extensionPoints.put(point, extension);
   }

   public void addConfig(ModConfig modConfig) {
      this.configs.put(modConfig.getType(), modConfig);
   }

   public void dispatchConfigEvent(ModConfig.ModConfigEvent event) {
      this.configHandler.ifPresent((configHandler) -> {
         configHandler.accept(event);
      });
   }

   public abstract boolean matches(Object var1);

   public abstract Object getMod();

   protected void acceptEvent(Event e) {
   }
}
