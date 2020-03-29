package net.minecraft.command;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

@FunctionalInterface
public interface ITimerCallback<T> {
   void run(T var1, TimerCallbackManager<T> var2, long var3);

   public abstract static class Serializer<T, C extends ITimerCallback<T>> {
      private final ResourceLocation typeId;
      private final Class<?> clazz;

      public Serializer(ResourceLocation p_i51270_1_, Class<?> p_i51270_2_) {
         this.typeId = p_i51270_1_;
         this.clazz = p_i51270_2_;
      }

      public ResourceLocation func_216310_a() {
         return this.typeId;
      }

      public Class<?> func_216311_b() {
         return this.clazz;
      }

      public abstract void write(CompoundNBT var1, C var2);

      public abstract C read(CompoundNBT var1);
   }
}
