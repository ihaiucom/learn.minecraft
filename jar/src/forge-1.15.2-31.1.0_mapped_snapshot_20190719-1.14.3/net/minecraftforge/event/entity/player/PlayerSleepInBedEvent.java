package net.minecraftforge.event.entity.player;

import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class PlayerSleepInBedEvent extends PlayerEvent {
   private PlayerEntity.SleepResult result = null;
   private final Optional<BlockPos> pos;

   public PlayerSleepInBedEvent(PlayerEntity player, Optional<BlockPos> pos) {
      super(player);
      this.pos = pos;
   }

   public PlayerEntity.SleepResult getResultStatus() {
      return this.result;
   }

   public void setResult(PlayerEntity.SleepResult result) {
      this.result = result;
   }

   public BlockPos getPos() {
      return (BlockPos)this.pos.orElse((Object)null);
   }

   public Optional<BlockPos> getOptionalPos() {
      return this.pos;
   }
}
