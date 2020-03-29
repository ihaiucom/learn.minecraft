package net.minecraftforge.common.util;

import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Lazy<T> extends Supplier<T> {
   static <T> Lazy<T> of(@Nonnull Supplier<T> supplier) {
      return new Lazy.Fast(supplier);
   }

   static <T> Lazy<T> concurrentOf(@Nonnull Supplier<T> supplier) {
      return new Lazy.Concurrent(supplier);
   }

   public static final class Concurrent<T> implements Lazy<T> {
      private static final Object lock = new Object();
      private volatile Supplier<T> supplier;
      private volatile T instance;

      private Concurrent(Supplier<T> supplier) {
         this.supplier = supplier;
      }

      @Nullable
      public final T get() {
         if (this.supplier != null) {
            synchronized(lock) {
               if (this.supplier != null) {
                  this.instance = this.supplier.get();
                  this.supplier = null;
               }
            }
         }

         return this.instance;
      }

      // $FF: synthetic method
      Concurrent(Supplier x0, Object x1) {
         this(x0);
      }
   }

   public static final class Fast<T> implements Lazy<T> {
      private Supplier<T> supplier;
      private T instance;

      private Fast(Supplier<T> supplier) {
         this.supplier = supplier;
      }

      @Nullable
      public final T get() {
         if (this.supplier != null) {
            this.instance = this.supplier.get();
            this.supplier = null;
         }

         return this.instance;
      }

      // $FF: synthetic method
      Fast(Supplier x0, Object x1) {
         this(x0);
      }
   }
}
