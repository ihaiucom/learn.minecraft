package net.minecraftforge.server.permission.context;

import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class WorldContext extends Context {
   private final World world;

   public WorldContext(World w) {
      this.world = (World)Preconditions.checkNotNull(w, "World can't be null in WorldContext!");
   }

   public World getWorld() {
      return this.world;
   }

   @Nullable
   public PlayerEntity getPlayer() {
      return null;
   }
}
