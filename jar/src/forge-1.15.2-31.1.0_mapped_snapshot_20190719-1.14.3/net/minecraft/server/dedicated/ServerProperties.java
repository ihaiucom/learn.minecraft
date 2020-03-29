package net.minecraft.server.dedicated;

import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;

public class ServerProperties extends PropertyManager<ServerProperties> {
   public final boolean onlineMode = this.func_218982_a("online-mode", true);
   public final boolean preventProxyConnections = this.func_218982_a("prevent-proxy-connections", false);
   public final String serverIp = this.func_218973_a("server-ip", "");
   public final boolean spawnAnimals = this.func_218982_a("spawn-animals", true);
   public final boolean spawnNPCs = this.func_218982_a("spawn-npcs", true);
   public final boolean allowPvp = this.func_218982_a("pvp", true);
   public final boolean allowFlight = this.func_218982_a("allow-flight", false);
   public final String resourcePack = this.func_218973_a("resource-pack", "");
   public final String motd = this.func_218973_a("motd", "A Minecraft Server");
   public final boolean forceGamemode = this.func_218982_a("force-gamemode", false);
   public final boolean enforceWhitelist = this.func_218982_a("enforce-whitelist", false);
   public final boolean generateStructures = this.func_218982_a("generate-structures", true);
   public final Difficulty difficulty;
   public final GameType gamemode;
   public final String worldName;
   public final String worldSeed;
   public final WorldType worldType;
   public final String generatorSettings;
   public final int serverPort;
   public final int maxBuildHeight;
   public final Boolean announceAdvancements;
   public final boolean enableQuery;
   public final int queryPort;
   public final boolean enableRcon;
   public final int rconPort;
   public final String rconPassword;
   public final String resourcePackHash;
   public final String resourcePackSha1;
   public final boolean hardcore;
   public final boolean allowNether;
   public final boolean spawnMonsters;
   public final boolean field_218993_F;
   public final boolean useNativeTransport;
   public final boolean enableCommandBlock;
   public final int spawnProtection;
   public final int opPermissionLevel;
   public final int field_225395_K;
   public final long maxTickTime;
   public final int viewDistance;
   public final int maxPlayers;
   public final int networkCompressionThreshold;
   public final boolean broadcastRconToOps;
   public final boolean broadcastConsoleToOps;
   public final int maxWorldSize;
   public final PropertyManager<ServerProperties>.Property<Integer> playerIdleTimeout;
   public final PropertyManager<ServerProperties>.Property<Boolean> whitelistEnabled;

   public ServerProperties(Properties p_i50719_1_) {
      super(p_i50719_1_);
      this.difficulty = (Difficulty)this.func_218983_a("difficulty", func_218964_a(Difficulty::byId, Difficulty::byName), Difficulty::getTranslationKey, Difficulty.EASY);
      this.gamemode = (GameType)this.func_218983_a("gamemode", func_218964_a(GameType::getByID, GameType::getByName), GameType::getName, GameType.SURVIVAL);
      this.worldName = this.func_218973_a("level-name", "world");
      this.worldSeed = this.func_218973_a("level-seed", "");
      this.worldType = (WorldType)this.func_218983_a("level-type", WorldType::byName, WorldType::getName, WorldType.DEFAULT);
      this.generatorSettings = this.func_218973_a("generator-settings", "");
      this.serverPort = this.func_218968_a("server-port", 25565);
      this.maxBuildHeight = this.func_218962_a("max-build-height", (p_218987_0_) -> {
         return MathHelper.clamp((p_218987_0_ + 8) / 16 * 16, 64, 256);
      }, 256);
      this.announceAdvancements = this.func_218978_b("announce-player-achievements");
      this.enableQuery = this.func_218982_a("enable-query", false);
      this.queryPort = this.func_218968_a("query.port", 25565);
      this.enableRcon = this.func_218982_a("enable-rcon", false);
      this.rconPort = this.func_218968_a("rcon.port", 25575);
      this.rconPassword = this.func_218973_a("rcon.password", "");
      this.resourcePackHash = this.func_218980_a("resource-pack-hash");
      this.resourcePackSha1 = this.func_218973_a("resource-pack-sha1", "");
      this.hardcore = this.func_218982_a("hardcore", false);
      this.allowNether = this.func_218982_a("allow-nether", true);
      this.spawnMonsters = this.func_218982_a("spawn-monsters", true);
      if (this.func_218982_a("snooper-enabled", true)) {
      }

      this.field_218993_F = false;
      this.useNativeTransport = this.func_218982_a("use-native-transport", true);
      this.enableCommandBlock = this.func_218982_a("enable-command-block", false);
      this.spawnProtection = this.func_218968_a("spawn-protection", 16);
      this.opPermissionLevel = this.func_218968_a("op-permission-level", 4);
      this.field_225395_K = this.func_218968_a("function-permission-level", 2);
      this.maxTickTime = this.func_218967_a("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
      this.viewDistance = this.func_218968_a("view-distance", 10);
      this.maxPlayers = this.func_218968_a("max-players", 20);
      this.networkCompressionThreshold = this.func_218968_a("network-compression-threshold", 256);
      this.broadcastRconToOps = this.func_218982_a("broadcast-rcon-to-ops", true);
      this.broadcastConsoleToOps = this.func_218982_a("broadcast-console-to-ops", true);
      this.maxWorldSize = this.func_218962_a("max-world-size", (p_218986_0_) -> {
         return MathHelper.clamp(p_218986_0_, 1, 29999984);
      }, 29999984);
      this.playerIdleTimeout = this.func_218974_b("player-idle-timeout", 0);
      this.whitelistEnabled = this.func_218961_b("white-list", false);
   }

   public static ServerProperties create(Path p_218985_0_) {
      return new ServerProperties(load(p_218985_0_));
   }

   protected ServerProperties func_212857_b_(Properties p_212857_1_) {
      return new ServerProperties(p_212857_1_);
   }

   // $FF: synthetic method
   protected PropertyManager func_212857_b_(Properties p_212857_1_) {
      return this.func_212857_b_(p_212857_1_);
   }
}
