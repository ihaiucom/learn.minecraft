package net.minecraftforge.server.permission.context;

import com.google.common.base.Preconditions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class PlayerContext extends Context {
   private final PlayerEntity player;

   public PlayerContext(PlayerEntity ep) {
      this.player = (PlayerEntity)Preconditions.checkNotNull(ep, "Player can't be null in PlayerContext!");
   }

   public World getWorld() {
      return this.player.getEntityWorld();
   }

   public PlayerEntity getPlayer() {
      return this.player;
   }
}
