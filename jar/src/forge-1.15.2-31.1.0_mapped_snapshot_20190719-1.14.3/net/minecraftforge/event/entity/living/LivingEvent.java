package net.minecraftforge.event.entity.living;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

public class LivingEvent extends EntityEvent {
   private final LivingEntity entityLiving;

   public LivingEvent(LivingEntity entity) {
      super(entity);
      this.entityLiving = entity;
   }

   public LivingEntity getEntityLiving() {
      return this.entityLiving;
   }

   public static class LivingJumpEvent extends LivingEvent {
      public LivingJumpEvent(LivingEntity e) {
         super(e);
      }
   }

   @Cancelable
   public static class LivingUpdateEvent extends LivingEvent {
      public LivingUpdateEvent(LivingEntity e) {
         super(e);
      }
   }
}
