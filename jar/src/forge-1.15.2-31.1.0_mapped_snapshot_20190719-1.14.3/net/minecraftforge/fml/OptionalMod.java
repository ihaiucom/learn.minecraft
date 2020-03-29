package net.minecraftforge.fml;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class OptionalMod<T> {
   private final String modId;
   private T value;
   private boolean searched;
   private static OptionalMod<?> EMPTY = new OptionalMod(true);

   public static <M> OptionalMod<M> of(String modId) {
      return new OptionalMod(modId);
   }

   private static <T> OptionalMod<T> empty() {
      OptionalMod<T> t = EMPTY;
      return t;
   }

   private OptionalMod(boolean searched) {
      this.searched = searched;
      this.modId = "";
   }

   private OptionalMod(String modId) {
      this.modId = modId;
   }

   private T getValue() {
      if (!this.searched) {
         this.value = ModList.get().getModObjectById(this.modId).orElse((Object)null);
         this.searched = true;
      }

      return this.value;
   }

   public T get() {
      if (this.getValue() == null) {
         throw new NoSuchElementException("No value present");
      } else {
         return this.getValue();
      }
   }

   public String getModId() {
      return this.modId;
   }

   public boolean isPresent() {
      return this.getValue() != null;
   }

   public void ifPresent(Consumer<? super T> consumer) {
      if (this.getValue() != null) {
         consumer.accept(this.getValue());
      }

   }

   public OptionalMod<T> filter(Predicate<? super T> predicate) {
      Objects.requireNonNull(predicate);
      if (!this.isPresent()) {
         return this;
      } else {
         return predicate.test(this.getValue()) ? this : empty();
      }
   }

   public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
      Objects.requireNonNull(mapper);
      return !this.isPresent() ? Optional.empty() : Optional.ofNullable(mapper.apply(this.getValue()));
   }

   public <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
      Objects.requireNonNull(mapper);
      return !this.isPresent() ? Optional.empty() : (Optional)Objects.requireNonNull(mapper.apply(this.getValue()));
   }

   public T orElse(T other) {
      return this.getValue() != null ? this.getValue() : other;
   }

   public T orElseGet(Supplier<? extends T> other) {
      return this.getValue() != null ? this.getValue() : other.get();
   }

   public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
      if (this.getValue() != null) {
         return this.getValue();
      } else {
         throw (Throwable)exceptionSupplier.get();
      }
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof OptionalMod ? Objects.equals(((OptionalMod)obj).modId, this.modId) : false;
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.modId);
   }
}
