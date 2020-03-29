package net.minecraft.network.handshake;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.network.login.server.SDisconnectLoginPacket;
import net.minecraft.network.status.ServerStatusNetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ServerHandshakeNetHandler implements IHandshakeNetHandler {
   private final MinecraftServer server;
   private final NetworkManager networkManager;

   public ServerHandshakeNetHandler(MinecraftServer p_i45295_1_, NetworkManager p_i45295_2_) {
      this.server = p_i45295_1_;
      this.networkManager = p_i45295_2_;
   }

   public void processHandshake(CHandshakePacket p_147383_1_) {
      if (ServerLifecycleHooks.handleServerLogin(p_147383_1_, this.networkManager)) {
         switch(p_147383_1_.getRequestedState()) {
         case LOGIN:
            this.networkManager.setConnectionState(ProtocolType.LOGIN);
            TranslationTextComponent itextcomponent1;
            if (p_147383_1_.getProtocolVersion() > SharedConstants.getVersion().getProtocolVersion()) {
               itextcomponent1 = new TranslationTextComponent("multiplayer.disconnect.outdated_server", new Object[]{SharedConstants.getVersion().getName()});
               this.networkManager.sendPacket(new SDisconnectLoginPacket(itextcomponent1));
               this.networkManager.closeChannel(itextcomponent1);
            } else if (p_147383_1_.getProtocolVersion() < SharedConstants.getVersion().getProtocolVersion()) {
               itextcomponent1 = new TranslationTextComponent("multiplayer.disconnect.outdated_client", new Object[]{SharedConstants.getVersion().getName()});
               this.networkManager.sendPacket(new SDisconnectLoginPacket(itextcomponent1));
               this.networkManager.closeChannel(itextcomponent1);
            } else {
               this.networkManager.setNetHandler(new ServerLoginNetHandler(this.server, this.networkManager));
            }
            break;
         case STATUS:
            this.networkManager.setConnectionState(ProtocolType.STATUS);
            this.networkManager.setNetHandler(new ServerStatusNetHandler(this.server, this.networkManager));
            break;
         default:
            throw new UnsupportedOperationException("Invalid intention " + p_147383_1_.getRequestedState());
         }

      }
   }

   public void onDisconnect(ITextComponent p_147231_1_) {
   }

   public NetworkManager getNetworkManager() {
      return this.networkManager;
   }
}
