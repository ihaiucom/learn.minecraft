package net.minecraftforge.client.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.MovementInput;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class InputUpdateEvent extends PlayerEvent {
   private final MovementInput movementInput;

   public InputUpdateEvent(PlayerEntity player, MovementInput movementInput) {
      super(player);
      this.movementInput = movementInput;
   }

   public MovementInput getMovementInput() {
      return this.movementInput;
   }
}
