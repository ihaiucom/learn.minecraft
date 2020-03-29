package net.minecraftforge.fml;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.registries.RegistryManager;

public final class RegistryObject<T extends IForgeRegistryEntry<? super T>> implements Supplier<T> {
   private final ResourceLocation name;
   @Nullable
   private T value;
   private static RegistryObject<?> EMPTY = new RegistryObject();

   public static <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject<U> of(ResourceLocation name, Supplier<Class<? super T>> registryType) {
      return new RegistryObject(name, registryType);
   }

   public static <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject<U> of(ResourceLocation name, IForgeRegistry<T> registry) {
      return new RegistryObject(name, registry);
   }

   private static <T extends IForgeRegistryEntry<? super T>> RegistryObject<T> empty() {
      RegistryObject<T> t = EMPTY;
      return t;
   }

   private RegistryObject() {
      this.name = null;
   }

   private <V extends IForgeRegistryEntry<V>> RegistryObject(ResourceLocation name, Supplier<Class<? super V>> registryType) {
      this(name, RegistryManager.ACTIVE.getRegistry((Class)registryType.get()));
   }

   private <V extends IForgeRegistryEntry<V>> RegistryObject(ResourceLocation name, IForgeRegistry<V> registry) {
      if (registry == null) {
         throw new IllegalArgumentException("Invalid registry argument, must not be null");
      } else {
         this.name = name;
         ObjectHolderRegistry.addHandler((pred) -> {
            if (pred.test(registry.getRegistryName())) {
               this.value = registry.containsKey(this.name) ? registry.getValue(this.name) : null;
            }

         });
      }
   }

   @Nonnull
   public T get() {
      T ret = this.value;
      Objects.requireNonNull(ret, "Registry Object not present");
      return ret;
   }

   public void updateReference(IForgeRegistry<? extends T> registry) {
      this.value = registry.getValue(this.getId());
   }

   public ResourceLocation getId() {
      return this.name;
   }

   public Stream<T> stream() {
      return this.isPresent() ? Stream.of(this.get()) : Stream.of();
   }

   public boolean isPresent() {
      return this.get() != null;
   }

   public void ifPresent(Consumer<? super T> consumer) {
      if (this.get() != null) {
         consumer.accept(this.get());
      }

   }

   public RegistryObject<T> filter(Predicate<? super T> predicate) {
      Objects.requireNonNull(predicate);
      if (!this.isPresent()) {
         return this;
      } else {
         return predicate.test(this.get()) ? this : empty();
      }
   }

   public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
      Objects.requireNonNull(mapper);
      return !this.isPresent() ? Optional.empty() : Optional.ofNullable(mapper.apply(this.get()));
   }

   public <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
      Objects.requireNonNull(mapper);
      return !this.isPresent() ? Optional.empty() : (Optional)Objects.requireNonNull(mapper.apply(this.get()));
   }

   public <U> Supplier<U> lazyMap(Function<? super T, ? extends U> mapper) {
      Objects.requireNonNull(mapper);
      return () -> {
         return this.isPresent() ? mapper.apply(this.get()) : null;
      };
   }

   public T orElse(T other) {
      return this.isPresent() ? this.get() : other;
   }

   public T orElseGet(Supplier<? extends T> other) {
      return this.isPresent() ? this.get() : (IForgeRegistryEntry)other.get();
   }

   public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
      if (this.get() != null) {
         return this.get();
      } else {
         throw (Throwable)exceptionSupplier.get();
      }
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof RegistryObject ? Objects.equals(((RegistryObject)obj).name, this.name) : false;
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.name);
   }
}
