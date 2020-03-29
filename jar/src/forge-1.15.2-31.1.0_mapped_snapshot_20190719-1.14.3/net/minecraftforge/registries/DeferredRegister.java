package net.minecraftforge.registries;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;

public class DeferredRegister<T extends IForgeRegistryEntry<T>> {
   private final IForgeRegistry<T> type;
   private final String modid;
   private final Map<RegistryObject<T>, Supplier<? extends T>> entries = new LinkedHashMap();
   private final Set<RegistryObject<T>> entriesView;

   public DeferredRegister(IForgeRegistry<T> reg, String modid) {
      this.entriesView = Collections.unmodifiableSet(this.entries.keySet());
      this.type = reg;
      this.modid = modid;
   }

   public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> sup) {
      Objects.requireNonNull(name);
      Objects.requireNonNull(sup);
      ResourceLocation key = new ResourceLocation(this.modid, name);
      RegistryObject<I> ret = RegistryObject.of(key, this.type);
      if (this.entries.putIfAbsent(ret, () -> {
         return (IForgeRegistryEntry)((IForgeRegistryEntry)sup.get()).setRegistryName(key);
      }) != null) {
         throw new IllegalArgumentException("Duplicate registration " + name);
      } else {
         return ret;
      }
   }

   public void register(IEventBus bus) {
      bus.addListener(this::addEntries);
   }

   public Collection<RegistryObject<T>> getEntries() {
      return this.entriesView;
   }

   private void addEntries(RegistryEvent.Register<?> event) {
      if (event.getGenericType() == this.type.getRegistrySuperType()) {
         IForgeRegistry<T> reg = event.getRegistry();
         Iterator var3 = this.entries.entrySet().iterator();

         while(var3.hasNext()) {
            Entry<RegistryObject<T>, Supplier<? extends T>> e = (Entry)var3.next();
            reg.register((IForgeRegistryEntry)((Supplier)e.getValue()).get());
            ((RegistryObject)e.getKey()).updateReference(reg);
         }
      }

   }
}
