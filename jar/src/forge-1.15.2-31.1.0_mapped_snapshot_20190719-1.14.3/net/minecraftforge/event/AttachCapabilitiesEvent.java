package net.minecraftforge.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.eventbus.api.GenericEvent;

public class AttachCapabilitiesEvent<T> extends GenericEvent<T> {
   private final T obj;
   private final Map<ResourceLocation, ICapabilityProvider> caps = Maps.newLinkedHashMap();
   private final Map<ResourceLocation, ICapabilityProvider> view;
   private final List<Runnable> listeners;
   private final List<Runnable> listenersView;

   public AttachCapabilitiesEvent(Class<T> type, T obj) {
      super(type);
      this.view = Collections.unmodifiableMap(this.caps);
      this.listeners = Lists.newArrayList();
      this.listenersView = Collections.unmodifiableList(this.listeners);
      this.obj = obj;
   }

   public T getObject() {
      return this.obj;
   }

   public void addCapability(ResourceLocation key, ICapabilityProvider cap) {
      if (this.caps.containsKey(key)) {
         throw new IllegalStateException("Duplicate Capability Key: " + key + " " + cap);
      } else {
         this.caps.put(key, cap);
      }
   }

   public Map<ResourceLocation, ICapabilityProvider> getCapabilities() {
      return this.view;
   }

   public void addListener(Runnable listener) {
      this.listeners.add(listener);
   }

   public List<Runnable> getListeners() {
      return this.listenersView;
   }
}
