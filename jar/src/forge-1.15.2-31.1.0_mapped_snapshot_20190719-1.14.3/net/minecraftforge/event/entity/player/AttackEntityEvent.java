package net.minecraftforge.event.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class AttackEntityEvent extends PlayerEvent {
   private final Entity target;

   public AttackEntityEvent(PlayerEntity player, Entity target) {
      super(player);
      this.target = target;
   }

   public Entity getTarget() {
      return this.target;
   }
}
