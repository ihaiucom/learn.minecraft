package net.minecraftforge.event.village;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class VillageSiegeEvent extends Event {
   private final VillageSiege siege;
   private final World world;
   private final PlayerEntity player;
   private final Vec3d attemptedSpawnPos;

   public VillageSiegeEvent(VillageSiege siege, World world, PlayerEntity player, Vec3d attemptedSpawnPos) {
      this.siege = siege;
      this.world = world;
      this.player = player;
      this.attemptedSpawnPos = attemptedSpawnPos;
   }

   public VillageSiege getSiege() {
      return this.siege;
   }

   public World getWorld() {
      return this.world;
   }

   public PlayerEntity getPlayer() {
      return this.player;
   }

   public Vec3d getAttemptedSpawnPos() {
      return this.attemptedSpawnPos;
   }
}
