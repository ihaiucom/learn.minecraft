package net.minecraftforge.event;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.GenericEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.Validate;

public class RegistryEvent<T extends IForgeRegistryEntry<T>> extends GenericEvent<T> {
   RegistryEvent(Class<T> clazz) {
      super(clazz);
   }

   public static class MissingMappings<T extends IForgeRegistryEntry<T>> extends RegistryEvent<T> {
      private final IForgeRegistry<T> registry;
      private final ResourceLocation name;
      private final ImmutableList<RegistryEvent.MissingMappings.Mapping<T>> mappings;
      private ModContainer activeMod;

      public MissingMappings(ResourceLocation name, IForgeRegistry<T> registry, Collection<RegistryEvent.MissingMappings.Mapping<T>> missed) {
         super(registry.getRegistrySuperType());
         this.registry = registry;
         this.name = name;
         this.mappings = ImmutableList.copyOf(missed);
      }

      public void setModContainer(ModContainer mod) {
         this.activeMod = mod;
      }

      public ResourceLocation getName() {
         return this.name;
      }

      public IForgeRegistry<T> getRegistry() {
         return this.registry;
      }

      public ImmutableList<RegistryEvent.MissingMappings.Mapping<T>> getMappings() {
         return ImmutableList.copyOf((Collection)this.mappings.stream().filter((e) -> {
            return e.key.getNamespace().equals(this.activeMod.getModId());
         }).collect(Collectors.toList()));
      }

      public ImmutableList<RegistryEvent.MissingMappings.Mapping<T>> getAllMappings() {
         return this.mappings;
      }

      public static class Mapping<T extends IForgeRegistryEntry<T>> {
         public final IForgeRegistry<T> registry;
         private final IForgeRegistry<T> pool;
         public final ResourceLocation key;
         public final int id;
         private RegistryEvent.MissingMappings.Action action;
         private T target;

         public Mapping(IForgeRegistry<T> registry, IForgeRegistry<T> pool, ResourceLocation key, int id) {
            this.action = RegistryEvent.MissingMappings.Action.DEFAULT;
            this.registry = registry;
            this.pool = pool;
            this.key = key;
            this.id = id;
         }

         public void ignore() {
            this.action = RegistryEvent.MissingMappings.Action.IGNORE;
         }

         public void warn() {
            this.action = RegistryEvent.MissingMappings.Action.WARN;
         }

         public void fail() {
            this.action = RegistryEvent.MissingMappings.Action.FAIL;
         }

         public void remap(T target) {
            Validate.notNull(target, "Remap target can not be null", new Object[0]);
            Validate.isTrue(this.pool.getKey(target) != null, String.format("The specified entry %s hasn't been registered in registry yet.", target), new Object[0]);
            this.action = RegistryEvent.MissingMappings.Action.REMAP;
            this.target = target;
         }

         public RegistryEvent.MissingMappings.Action getAction() {
            return this.action;
         }

         public T getTarget() {
            return this.target;
         }
      }

      public static enum Action {
         DEFAULT,
         IGNORE,
         WARN,
         FAIL,
         REMAP;
      }
   }

   public static class Register<T extends IForgeRegistryEntry<T>> extends RegistryEvent<T> {
      private final IForgeRegistry<T> registry;
      private final ResourceLocation name;

      public Register(ResourceLocation name, IForgeRegistry<T> registry) {
         super(registry.getRegistrySuperType());
         this.name = name;
         this.registry = registry;
      }

      public IForgeRegistry<T> getRegistry() {
         return this.registry;
      }

      public ResourceLocation getName() {
         return this.name;
      }

      public String toString() {
         return "RegistryEvent.Register<" + this.getName() + ">";
      }
   }

   public static class NewRegistry extends Event {
      public String toString() {
         return "RegistryEvent.NewRegistry";
      }
   }
}
