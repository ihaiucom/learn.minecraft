package net.minecraftforge.event.world;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;

public class ChunkWatchEvent extends Event {
   private final ServerWorld world;
   private final ServerPlayerEntity player;
   private final ChunkPos pos;

   public ChunkWatchEvent(ServerPlayerEntity player, ChunkPos pos, ServerWorld world) {
      this.player = player;
      this.pos = pos;
      this.world = world;
   }

   public ServerPlayerEntity getPlayer() {
      return this.player;
   }

   public ChunkPos getPos() {
      return this.pos;
   }

   public ServerWorld getWorld() {
      return this.world;
   }

   public static class UnWatch extends ChunkWatchEvent {
      public UnWatch(ServerPlayerEntity player, ChunkPos pos, ServerWorld world) {
         super(player, pos, world);
      }
   }

   public static class Watch extends ChunkWatchEvent {
      public Watch(ServerPlayerEntity player, ChunkPos pos, ServerWorld world) {
         super(player, pos, world);
      }
   }
}
