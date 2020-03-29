package net.minecraftforge.client.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Event;

public class FOVUpdateEvent extends Event {
   private final PlayerEntity entity;
   private final float fov;
   private float newfov;

   public FOVUpdateEvent(PlayerEntity entity, float fov) {
      this.entity = entity;
      this.fov = fov;
      this.setNewfov(fov);
   }

   public PlayerEntity getEntity() {
      return this.entity;
   }

   public float getFov() {
      return this.fov;
   }

   public float getNewfov() {
      return this.newfov;
   }

   public void setNewfov(float newfov) {
      this.newfov = newfov;
   }
}
