package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerFlyableFallEvent extends PlayerEvent {
   private float distance;
   private float multiplier;

   public PlayerFlyableFallEvent(PlayerEntity player, float distance, float multiplier) {
      super(player);
      this.distance = distance;
      this.multiplier = multiplier;
   }

   public float getDistance() {
      return this.distance;
   }

   public void setDistance(float distance) {
      this.distance = distance;
   }

   public float getMultiplier() {
      return this.multiplier;
   }

   public void setMultiplier(float multiplier) {
      this.multiplier = multiplier;
   }
}
