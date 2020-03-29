package net.minecraft.client.network.handshake;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.IHandshakeNetHandler;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

@OnlyIn(Dist.CLIENT)
public class ClientHandshakeNetHandler implements IHandshakeNetHandler {
   private final MinecraftServer server;
   private final NetworkManager networkManager;

   public ClientHandshakeNetHandler(MinecraftServer p_i45287_1_, NetworkManager p_i45287_2_) {
      this.server = p_i45287_1_;
      this.networkManager = p_i45287_2_;
   }

   public void processHandshake(CHandshakePacket p_147383_1_) {
      if (ServerLifecycleHooks.handleServerLogin(p_147383_1_, this.networkManager)) {
         this.networkManager.setConnectionState(p_147383_1_.getRequestedState());
         this.networkManager.setNetHandler(new ServerLoginNetHandler(this.server, this.networkManager));
      }
   }

   public void onDisconnect(ITextComponent p_147231_1_) {
   }

   public NetworkManager getNetworkManager() {
      return this.networkManager;
   }
}
