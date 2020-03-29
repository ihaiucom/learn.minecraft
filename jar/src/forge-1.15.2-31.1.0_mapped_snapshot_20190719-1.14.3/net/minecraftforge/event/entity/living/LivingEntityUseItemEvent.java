package net.minecraftforge.event.entity.living;

import javax.annotation.Nonnull;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;

public class LivingEntityUseItemEvent extends LivingEvent {
   private final ItemStack item;
   private int duration;

   private LivingEntityUseItemEvent(LivingEntity entity, @Nonnull ItemStack item, int duration) {
      super(entity);
      this.item = item;
      this.setDuration(duration);
   }

   @Nonnull
   public ItemStack getItem() {
      return this.item;
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   // $FF: synthetic method
   LivingEntityUseItemEvent(LivingEntity x0, ItemStack x1, int x2, Object x3) {
      this(x0, x1, x2);
   }

   public static class Finish extends LivingEntityUseItemEvent {
      private ItemStack result;

      public Finish(LivingEntity entity, @Nonnull ItemStack item, int duration, @Nonnull ItemStack result) {
         super(entity, item, duration, null);
         this.setResultStack(result);
      }

      @Nonnull
      public ItemStack getResultStack() {
         return this.result;
      }

      public void setResultStack(@Nonnull ItemStack result) {
         this.result = result;
      }
   }

   @Cancelable
   public static class Stop extends LivingEntityUseItemEvent {
      public Stop(LivingEntity entity, @Nonnull ItemStack item, int duration) {
         super(entity, item, duration, null);
      }
   }

   @Cancelable
   public static class Tick extends LivingEntityUseItemEvent {
      public Tick(LivingEntity entity, @Nonnull ItemStack item, int duration) {
         super(entity, item, duration, null);
      }
   }

   @Cancelable
   public static class Start extends LivingEntityUseItemEvent {
      public Start(LivingEntity entity, @Nonnull ItemStack item, int duration) {
         super(entity, item, duration, null);
      }
   }
}
