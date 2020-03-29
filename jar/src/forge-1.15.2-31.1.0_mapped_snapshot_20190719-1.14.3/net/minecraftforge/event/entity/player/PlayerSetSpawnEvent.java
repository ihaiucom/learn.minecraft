package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class PlayerSetSpawnEvent extends PlayerEvent {
   private final boolean forced;
   private final BlockPos newSpawn;

   public PlayerSetSpawnEvent(PlayerEntity player, BlockPos newSpawn, boolean forced) {
      super(player);
      this.newSpawn = newSpawn;
      this.forced = forced;
   }

   public boolean isForced() {
      return this.forced;
   }

   public BlockPos getNewSpawn() {
      return this.newSpawn;
   }
}
