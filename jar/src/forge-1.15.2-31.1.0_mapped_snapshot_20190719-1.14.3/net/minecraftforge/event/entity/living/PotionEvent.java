package net.minecraftforge.event.entity.living;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.HasResult;

public class PotionEvent extends LivingEvent {
   @Nullable
   protected final EffectInstance effect;

   public PotionEvent(LivingEntity living, EffectInstance effect) {
      super(living);
      this.effect = effect;
   }

   @Nullable
   public EffectInstance getPotionEffect() {
      return this.effect;
   }

   public static class PotionExpiryEvent extends PotionEvent {
      public PotionExpiryEvent(LivingEntity living, EffectInstance effect) {
         super(living, effect);
      }
   }

   public static class PotionAddedEvent extends PotionEvent {
      private final EffectInstance oldEffect;

      public PotionAddedEvent(LivingEntity living, EffectInstance oldEffect, EffectInstance newEffect) {
         super(living, newEffect);
         this.oldEffect = oldEffect;
      }

      @Nonnull
      public EffectInstance getPotionEffect() {
         return super.getPotionEffect();
      }

      @Nullable
      public EffectInstance getOldPotionEffect() {
         return this.oldEffect;
      }
   }

   @HasResult
   public static class PotionApplicableEvent extends PotionEvent {
      public PotionApplicableEvent(LivingEntity living, EffectInstance effect) {
         super(living, effect);
      }

      @Nonnull
      public EffectInstance getPotionEffect() {
         return super.getPotionEffect();
      }
   }

   @Cancelable
   public static class PotionRemoveEvent extends PotionEvent {
      private final Effect potion;

      public PotionRemoveEvent(LivingEntity living, Effect potion) {
         super(living, living.getActivePotionEffect(potion));
         this.potion = potion;
      }

      public PotionRemoveEvent(LivingEntity living, EffectInstance effect) {
         super(living, effect);
         this.potion = effect.getPotion();
      }

      public Effect getPotion() {
         return this.potion;
      }

      @Nullable
      public EffectInstance getPotionEffect() {
         return super.getPotionEffect();
      }
   }
}
