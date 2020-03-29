package net.minecraft.network.rcon;

import net.minecraft.server.dedicated.ServerProperties;

public interface IServer {
   ServerProperties getServerProperties();

   String getHostname();

   int getPort();

   String getMotd();

   String getMinecraftVersion();

   int getCurrentPlayerCount();

   int getMaxPlayers();

   String[] getOnlinePlayerNames();

   String getFolderName();

   String getPlugins();

   String handleRConCommand(String var1);

   boolean isDebuggingEnabled();

   void logInfo(String var1);

   void logWarning(String var1);

   void logSevere(String var1);

   void logDebug(String var1);
}
