package net.minecraftforge.common.capabilities;

import com.google.common.base.Throwables;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;

public class Capability<T> {
   private final String name;
   private final Capability.IStorage<T> storage;
   private final Callable<? extends T> factory;

   public String getName() {
      return this.name;
   }

   public Capability.IStorage<T> getStorage() {
      return this.storage;
   }

   public void readNBT(T instance, Direction side, INBT nbt) {
      this.storage.readNBT(this, instance, side, nbt);
   }

   @Nullable
   public INBT writeNBT(T instance, Direction side) {
      return this.storage.writeNBT(this, instance, side);
   }

   @Nullable
   public T getDefaultInstance() {
      try {
         return this.factory.call();
      } catch (Exception var2) {
         Throwables.throwIfUnchecked(var2);
         throw new RuntimeException(var2);
      }
   }

   @Nonnull
   public <R> LazyOptional<R> orEmpty(Capability<R> toCheck, LazyOptional<T> inst) {
      return this == toCheck ? inst.cast() : LazyOptional.empty();
   }

   Capability(String name, Capability.IStorage<T> storage, Callable<? extends T> factory) {
      this.name = name;
      this.storage = storage;
      this.factory = factory;
   }

   public interface IStorage<T> {
      @Nullable
      INBT writeNBT(Capability<T> var1, T var2, Direction var3);

      void readNBT(Capability<T> var1, T var2, Direction var3, INBT var4);
   }
}
