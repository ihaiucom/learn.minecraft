package net.minecraftforge.common.util;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LazyOptional<T> {
   private final NonNullSupplier<T> supplier;
   private AtomicReference<T> resolved;
   private Set<NonNullConsumer<LazyOptional<T>>> listeners = new HashSet();
   private boolean isValid = true;
   @Nonnull
   private static final LazyOptional<Void> EMPTY = new LazyOptional((NonNullSupplier)null);
   private static final Logger LOGGER = LogManager.getLogger();

   public static <T> LazyOptional<T> of(@Nullable NonNullSupplier<T> instanceSupplier) {
      return instanceSupplier == null ? empty() : new LazyOptional(instanceSupplier);
   }

   public static <T> LazyOptional<T> empty() {
      return EMPTY.cast();
   }

   public <X> LazyOptional<X> cast() {
      return this;
   }

   private LazyOptional(@Nullable NonNullSupplier<T> instanceSupplier) {
      this.supplier = instanceSupplier;
   }

   @Nullable
   private T getValue() {
      if (!this.isValid) {
         return null;
      } else if (this.resolved != null) {
         return this.resolved.get();
      } else if (this.supplier != null) {
         this.resolved = new AtomicReference((Object)null);
         T temp = this.supplier.get();
         if (temp == null) {
            LOGGER.catching(Level.WARN, new NullPointerException("Supplier should not return null value"));
            return null;
         } else {
            this.resolved.set(temp);
            return this.resolved.get();
         }
      } else {
         return null;
      }
   }

   private T getValueUnsafe() {
      T ret = this.getValue();
      if (ret == null) {
         throw new IllegalStateException("LazyOptional is empty or otherwise returned null from getValue() unexpectedly");
      } else {
         return ret;
      }
   }

   public boolean isPresent() {
      return this.supplier != null && this.isValid;
   }

   public void ifPresent(NonNullConsumer<? super T> consumer) {
      Objects.requireNonNull(consumer);
      T val = this.getValue();
      if (this.isValid && val != null) {
         consumer.accept(val);
      }

   }

   public <U> LazyOptional<U> map(NonNullFunction<? super T, ? extends U> mapper) {
      Objects.requireNonNull(mapper);
      return this.isPresent() ? of(() -> {
         return mapper.apply(this.getValueUnsafe());
      }) : empty();
   }

   public LazyOptional<T> filter(NonNullPredicate<? super T> predicate) {
      Objects.requireNonNull(predicate);
      T value = this.getValue();
      return value != null && predicate.test(value) ? of(() -> {
         return value;
      }) : empty();
   }

   public T orElse(T other) {
      T val = this.getValue();
      return val != null ? val : other;
   }

   public T orElseGet(NonNullSupplier<? extends T> other) {
      T val = this.getValue();
      return val != null ? val : other.get();
   }

   public <X extends Throwable> T orElseThrow(NonNullSupplier<? extends X> exceptionSupplier) throws X {
      T val = this.getValue();
      if (val != null) {
         return val;
      } else {
         throw (Throwable)exceptionSupplier.get();
      }
   }

   public void addListener(NonNullConsumer<LazyOptional<T>> listener) {
      if (this.isPresent()) {
         this.listeners.add(listener);
      } else {
         listener.accept(this);
      }

   }

   public void invalidate() {
      this.isValid = false;
      this.listeners.forEach((e) -> {
         e.accept(this);
      });
   }
}
