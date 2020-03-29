package net.minecraftforge.event.entity.living;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class LivingHurtEvent extends LivingEvent {
   private final DamageSource source;
   private float amount;

   public LivingHurtEvent(LivingEntity entity, DamageSource source, float amount) {
      super(entity);
      this.source = source;
      this.amount = amount;
   }

   public DamageSource getSource() {
      return this.source;
   }

   public float getAmount() {
      return this.amount;
   }

   public void setAmount(float amount) {
      this.amount = amount;
   }
}
