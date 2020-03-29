package net.minecraftforge.event.entity.living;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class EnderTeleportEvent extends LivingEvent {
   private double targetX;
   private double targetY;
   private double targetZ;
   private float attackDamage;

   public EnderTeleportEvent(LivingEntity entity, double targetX, double targetY, double targetZ, float attackDamage) {
      super(entity);
      this.setTargetX(targetX);
      this.setTargetY(targetY);
      this.setTargetZ(targetZ);
      this.setAttackDamage(attackDamage);
   }

   public double getTargetX() {
      return this.targetX;
   }

   public void setTargetX(double targetX) {
      this.targetX = targetX;
   }

   public double getTargetY() {
      return this.targetY;
   }

   public void setTargetY(double targetY) {
      this.targetY = targetY;
   }

   public double getTargetZ() {
      return this.targetZ;
   }

   public void setTargetZ(double targetZ) {
      this.targetZ = targetZ;
   }

   public float getAttackDamage() {
      return this.attackDamage;
   }

   public void setAttackDamage(float attackDamage) {
      this.attackDamage = attackDamage;
   }
}
