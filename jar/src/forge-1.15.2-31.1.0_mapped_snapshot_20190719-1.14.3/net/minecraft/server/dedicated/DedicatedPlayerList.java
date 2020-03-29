package net.minecraft.server.dedicated;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedPlayerList extends PlayerList {
   private static final Logger LOGGER = LogManager.getLogger();

   public DedicatedPlayerList(DedicatedServer p_i1503_1_) {
      super(p_i1503_1_, p_i1503_1_.getServerProperties().maxPlayers);
      ServerProperties lvt_2_1_ = p_i1503_1_.getServerProperties();
      this.setViewDistance(lvt_2_1_.viewDistance);
      super.setWhiteListEnabled((Boolean)lvt_2_1_.whitelistEnabled.get());
      if (!p_i1503_1_.isSinglePlayer()) {
         this.getBannedPlayers().setLanServer(true);
         this.getBannedIPs().setLanServer(true);
      }

      this.loadPlayerBanList();
      this.savePlayerBanList();
      this.loadIPBanList();
      this.saveIPBanList();
      this.loadOpsList();
      this.readWhiteList();
      this.saveOpsList();
      if (!this.getWhitelistedPlayers().getSaveFile().exists()) {
         this.saveWhiteList();
      }

   }

   public void setWhiteListEnabled(boolean p_72371_1_) {
      super.setWhiteListEnabled(p_72371_1_);
      this.getServer().func_213223_o(p_72371_1_);
   }

   public void addOp(GameProfile p_152605_1_) {
      super.addOp(p_152605_1_);
      this.saveOpsList();
   }

   public void removeOp(GameProfile p_152610_1_) {
      super.removeOp(p_152610_1_);
      this.saveOpsList();
   }

   public void reloadWhitelist() {
      this.readWhiteList();
   }

   private void saveIPBanList() {
      try {
         this.getBannedIPs().writeChanges();
      } catch (IOException var2) {
         LOGGER.warn("Failed to save ip banlist: ", var2);
      }

   }

   private void savePlayerBanList() {
      try {
         this.getBannedPlayers().writeChanges();
      } catch (IOException var2) {
         LOGGER.warn("Failed to save user banlist: ", var2);
      }

   }

   private void loadIPBanList() {
      try {
         this.getBannedIPs().readSavedFile();
      } catch (IOException var2) {
         LOGGER.warn("Failed to load ip banlist: ", var2);
      }

   }

   private void loadPlayerBanList() {
      try {
         this.getBannedPlayers().readSavedFile();
      } catch (IOException var2) {
         LOGGER.warn("Failed to load user banlist: ", var2);
      }

   }

   private void loadOpsList() {
      try {
         this.getOppedPlayers().readSavedFile();
      } catch (Exception var2) {
         LOGGER.warn("Failed to load operators list: ", var2);
      }

   }

   private void saveOpsList() {
      try {
         this.getOppedPlayers().writeChanges();
      } catch (Exception var2) {
         LOGGER.warn("Failed to save operators list: ", var2);
      }

   }

   private void readWhiteList() {
      try {
         this.getWhitelistedPlayers().readSavedFile();
      } catch (Exception var2) {
         LOGGER.warn("Failed to load white-list: ", var2);
      }

   }

   private void saveWhiteList() {
      try {
         this.getWhitelistedPlayers().writeChanges();
      } catch (Exception var2) {
         LOGGER.warn("Failed to save white-list: ", var2);
      }

   }

   public boolean canJoin(GameProfile p_152607_1_) {
      return !this.isWhiteListEnabled() || this.canSendCommands(p_152607_1_) || this.getWhitelistedPlayers().isWhitelisted(p_152607_1_);
   }

   public DedicatedServer getServer() {
      return (DedicatedServer)super.getServer();
   }

   public boolean bypassesPlayerLimit(GameProfile p_183023_1_) {
      return this.getOppedPlayers().bypassesPlayerLimit(p_183023_1_);
   }

   // $FF: synthetic method
   public MinecraftServer getServer() {
      return this.getServer();
   }
}
