package net.minecraftforge.event.entity.living;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class LivingKnockBackEvent extends LivingEvent {
   protected Entity attacker;
   protected float strength;
   protected double ratioX;
   protected double ratioZ;
   protected final Entity originalAttacker;
   protected final float originalStrength;
   protected final double originalRatioX;
   protected final double originalRatioZ;

   public LivingKnockBackEvent(LivingEntity target, Entity attacker, float strength, double ratioX, double ratioZ) {
      super(target);
      this.attacker = this.originalAttacker = attacker;
      this.strength = this.originalStrength = strength;
      this.ratioX = this.originalRatioX = ratioX;
      this.ratioZ = this.originalRatioZ = ratioZ;
   }

   public Entity getAttacker() {
      return this.attacker;
   }

   public float getStrength() {
      return this.strength;
   }

   public double getRatioX() {
      return this.ratioX;
   }

   public double getRatioZ() {
      return this.ratioZ;
   }

   public Entity getOriginalAttacker() {
      return this.originalAttacker;
   }

   public float getOriginalStrength() {
      return this.originalStrength;
   }

   public double getOriginalRatioX() {
      return this.originalRatioX;
   }

   public double getOriginalRatioZ() {
      return this.originalRatioZ;
   }

   public void setAttacker(Entity attacker) {
      this.attacker = attacker;
   }

   public void setStrength(float strength) {
      this.strength = strength;
   }

   public void setRatioX(double ratioX) {
      this.ratioX = ratioX;
   }

   public void setRatioZ(double ratioZ) {
      this.ratioZ = ratioZ;
   }
}
