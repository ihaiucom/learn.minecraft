package net.minecraft.network.status;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.status.client.CPingPacket;
import net.minecraft.network.status.client.CServerQueryPacket;
import net.minecraft.network.status.server.SPongPacket;
import net.minecraft.network.status.server.SServerInfoPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ServerStatusNetHandler implements IServerStatusNetHandler {
   private static final ITextComponent EXIT_MESSAGE = new TranslationTextComponent("multiplayer.status.request_handled", new Object[0]);
   private final MinecraftServer server;
   private final NetworkManager networkManager;
   private boolean handled;

   public ServerStatusNetHandler(MinecraftServer p_i45299_1_, NetworkManager p_i45299_2_) {
      this.server = p_i45299_1_;
      this.networkManager = p_i45299_2_;
   }

   public void onDisconnect(ITextComponent p_147231_1_) {
   }

   public NetworkManager getNetworkManager() {
      return this.networkManager;
   }

   public void processServerQuery(CServerQueryPacket p_147312_1_) {
      if (this.handled) {
         this.networkManager.closeChannel(EXIT_MESSAGE);
      } else {
         this.handled = true;
         this.networkManager.sendPacket(new SServerInfoPacket(this.server.getServerStatusResponse()));
      }
   }

   public void processPing(CPingPacket p_147311_1_) {
      this.networkManager.sendPacket(new SPongPacket(p_147311_1_.getClientTime()));
      this.networkManager.closeChannel(EXIT_MESSAGE);
   }
}
