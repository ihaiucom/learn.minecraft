package net.minecraftforge.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

public class TickEvent extends Event {
   public final TickEvent.Type type;
   public final LogicalSide side;
   public final TickEvent.Phase phase;

   public TickEvent(TickEvent.Type type, LogicalSide side, TickEvent.Phase phase) {
      this.type = type;
      this.side = side;
      this.phase = phase;
   }

   public static class RenderTickEvent extends TickEvent {
      public final float renderTickTime;

      public RenderTickEvent(TickEvent.Phase phase, float renderTickTime) {
         super(TickEvent.Type.RENDER, LogicalSide.CLIENT, phase);
         this.renderTickTime = renderTickTime;
      }
   }

   public static class PlayerTickEvent extends TickEvent {
      public final PlayerEntity player;

      public PlayerTickEvent(TickEvent.Phase phase, PlayerEntity player) {
         super(TickEvent.Type.PLAYER, player instanceof ServerPlayerEntity ? LogicalSide.SERVER : LogicalSide.CLIENT, phase);
         this.player = player;
      }
   }

   public static class WorldTickEvent extends TickEvent {
      public final World world;

      public WorldTickEvent(LogicalSide side, TickEvent.Phase phase, World world) {
         super(TickEvent.Type.WORLD, side, phase);
         this.world = world;
      }
   }

   public static class ClientTickEvent extends TickEvent {
      public ClientTickEvent(TickEvent.Phase phase) {
         super(TickEvent.Type.CLIENT, LogicalSide.CLIENT, phase);
      }
   }

   public static class ServerTickEvent extends TickEvent {
      public ServerTickEvent(TickEvent.Phase phase) {
         super(TickEvent.Type.SERVER, LogicalSide.SERVER, phase);
      }
   }

   public static enum Phase {
      START,
      END;
   }

   public static enum Type {
      WORLD,
      PLAYER,
      CLIENT,
      SERVER,
      RENDER;
   }
}
