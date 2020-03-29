package net.minecraftforge.event.entity.player;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event.HasResult;

@HasResult
public class SleepingLocationCheckEvent extends LivingEvent {
   private final BlockPos sleepingLocation;

   public SleepingLocationCheckEvent(LivingEntity player, BlockPos sleepingLocation) {
      super(player);
      this.sleepingLocation = sleepingLocation;
   }

   public BlockPos getSleepingLocation() {
      return this.sleepingLocation;
   }
}
