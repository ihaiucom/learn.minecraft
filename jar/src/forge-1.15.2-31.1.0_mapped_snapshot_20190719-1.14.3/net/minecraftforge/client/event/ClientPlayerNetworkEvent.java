package net.minecraftforge.client.event;

import javax.annotation.Nullable;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.eventbus.api.Event;

public class ClientPlayerNetworkEvent extends Event {
   private final PlayerController controller;
   private final ClientPlayerEntity player;
   private final NetworkManager networkManager;

   @Nullable
   public PlayerController getController() {
      return this.controller;
   }

   @Nullable
   public ClientPlayerEntity getPlayer() {
      return this.player;
   }

   @Nullable
   public NetworkManager getNetworkManager() {
      return this.networkManager;
   }

   ClientPlayerNetworkEvent(PlayerController controller, ClientPlayerEntity player, NetworkManager networkManager) {
      this.controller = controller;
      this.player = player;
      this.networkManager = networkManager;
   }

   public static class RespawnEvent extends ClientPlayerNetworkEvent {
      private final ClientPlayerEntity oldPlayer;

      public RespawnEvent(PlayerController pc, ClientPlayerEntity oldPlayer, ClientPlayerEntity newPlayer, NetworkManager networkManager) {
         super(pc, newPlayer, networkManager);
         this.oldPlayer = oldPlayer;
      }

      public ClientPlayerEntity getOldPlayer() {
         return this.oldPlayer;
      }

      public ClientPlayerEntity getNewPlayer() {
         return super.getPlayer();
      }
   }

   public static class LoggedOutEvent extends ClientPlayerNetworkEvent {
      public LoggedOutEvent(PlayerController controller, ClientPlayerEntity player, NetworkManager networkManager) {
         super(controller, player, networkManager);
      }
   }

   public static class LoggedInEvent extends ClientPlayerNetworkEvent {
      public LoggedInEvent(PlayerController controller, ClientPlayerEntity player, NetworkManager networkManager) {
         super(controller, player, networkManager);
      }
   }
}
