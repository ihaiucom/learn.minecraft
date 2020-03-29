package net.minecraftforge.event.entity.player;

import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Event.HasResult;

@HasResult
public class SleepingTimeCheckEvent extends PlayerEvent {
   private final Optional<BlockPos> sleepingLocation;

   public SleepingTimeCheckEvent(PlayerEntity player, Optional<BlockPos> sleepingLocation) {
      super(player);
      this.sleepingLocation = sleepingLocation;
   }

   public Optional<BlockPos> getSleepingLocation() {
      return this.sleepingLocation;
   }
}
