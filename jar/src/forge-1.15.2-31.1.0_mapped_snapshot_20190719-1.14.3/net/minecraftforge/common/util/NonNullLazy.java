package net.minecraftforge.common.util;

import javax.annotation.Nonnull;

public interface NonNullLazy<T> extends NonNullSupplier<T> {
   static <T> NonNullLazy<T> of(@Nonnull NonNullSupplier<T> supplier) {
      return new NonNullLazy.Fast(supplier);
   }

   static <T> NonNullLazy<T> concurrentOf(@Nonnull NonNullSupplier<T> supplier) {
      return new NonNullLazy.Concurrent(supplier);
   }

   public static final class Concurrent<T> implements NonNullLazy<T> {
      private static final Object lock = new Object();
      private volatile NonNullSupplier<T> supplier;
      private volatile T instance;

      private Concurrent(NonNullSupplier<T> supplier) {
         this.supplier = supplier;
      }

      @Nonnull
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
      Concurrent(NonNullSupplier x0, Object x1) {
         this(x0);
      }
   }

   public static final class Fast<T> implements NonNullLazy<T> {
      private NonNullSupplier<T> supplier;
      private T instance;

      private Fast(NonNullSupplier<T> supplier) {
         this.supplier = supplier;
      }

      @Nonnull
      public final T get() {
         if (this.supplier != null) {
            this.instance = this.supplier.get();
            this.supplier = null;
         }

         return this.instance;
      }

      // $FF: synthetic method
      Fast(NonNullSupplier x0, Object x1) {
         this(x0);
      }
   }
}
