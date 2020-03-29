package net.minecraftforge.event.entity.living;

import java.util.Collection;
import java.util.Collections;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;

public class PotionColorCalculationEvent extends LivingEvent {
   private int color;
   private boolean hideParticle;
   private final Collection<EffectInstance> effectList;

   public PotionColorCalculationEvent(LivingEntity entity, int color, boolean hideParticle, Collection<EffectInstance> effectList) {
      super(entity);
      this.color = color;
      this.effectList = effectList;
      this.hideParticle = hideParticle;
   }

   public int getColor() {
      return this.color;
   }

   public void setColor(int color) {
      this.color = color;
   }

   public boolean areParticlesHidden() {
      return this.hideParticle;
   }

   public void shouldHideParticles(boolean hideParticle) {
      this.hideParticle = hideParticle;
   }

   public Collection<EffectInstance> getEffects() {
      return Collections.unmodifiableCollection(this.effectList);
   }
}
