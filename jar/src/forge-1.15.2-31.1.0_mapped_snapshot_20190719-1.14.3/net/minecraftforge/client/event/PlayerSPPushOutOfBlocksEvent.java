package net.minecraftforge.client.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class PlayerSPPushOutOfBlocksEvent extends PlayerEvent {
   private double minY;

   public PlayerSPPushOutOfBlocksEvent(PlayerEntity player) {
      super(player);
      this.minY = player.func_226278_cu_() + 0.5D;
   }

   public void setMinY(double value) {
      this.minY = value;
   }

   public double getMinY() {
      return this.minY;
   }
}
