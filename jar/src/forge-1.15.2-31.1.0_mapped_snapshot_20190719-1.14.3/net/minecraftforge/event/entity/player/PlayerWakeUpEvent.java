package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerWakeUpEvent extends PlayerEvent {
   private final boolean wakeImmediately;
   private final boolean updateWorld;

   public PlayerWakeUpEvent(PlayerEntity player, boolean wakeImmediately, boolean updateWorld) {
      super(player);
      this.wakeImmediately = wakeImmediately;
      this.updateWorld = updateWorld;
   }

   public boolean wakeImmediately() {
      return this.wakeImmediately;
   }

   public boolean updateWorld() {
      return this.updateWorld;
   }
}
