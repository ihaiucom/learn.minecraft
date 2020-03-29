package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.LanServerPingThread;
import net.minecraft.command.Commands;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.profiler.DebugProfiler;
import net.minecraft.profiler.Snooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.CryptManager;
import net.minecraft.util.SharedConstants;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class IntegratedServer extends MinecraftServer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft mc;
   private final WorldSettings worldSettings;
   private boolean isGamePaused;
   private int serverPort = -1;
   private LanServerPingThread lanServerPing;
   private UUID playerUuid;

   public IntegratedServer(Minecraft p_i50895_1_, String p_i50895_2_, String p_i50895_3_, WorldSettings p_i50895_4_, YggdrasilAuthenticationService p_i50895_5_, MinecraftSessionService p_i50895_6_, GameProfileRepository p_i50895_7_, PlayerProfileCache p_i50895_8_, IChunkStatusListenerFactory p_i50895_9_) {
      super(new File(p_i50895_1_.gameDir, "saves"), p_i50895_1_.getProxy(), p_i50895_1_.getDataFixer(), new Commands(false), p_i50895_5_, p_i50895_6_, p_i50895_7_, p_i50895_8_, p_i50895_9_, p_i50895_2_);
      this.setServerOwner(p_i50895_1_.getSession().getUsername());
      this.setWorldName(p_i50895_3_);
      this.setDemo(p_i50895_1_.isDemo());
      this.canCreateBonusChest(p_i50895_4_.isBonusChestEnabled());
      this.setBuildLimit(256);
      this.setPlayerList(new IntegratedPlayerList(this));
      this.mc = p_i50895_1_;
      this.worldSettings = this.isDemo() ? MinecraftServer.DEMO_WORLD_SETTINGS : p_i50895_4_;
   }

   public void loadAllWorlds(String p_71247_1_, String p_71247_2_, long p_71247_3_, WorldType p_71247_5_, JsonElement p_71247_6_) {
      this.convertMapIfNeeded(p_71247_1_);
      SaveHandler savehandler = this.getActiveAnvilConverter().getSaveLoader(p_71247_1_, this);
      this.setResourcePackFromWorld(this.getFolderName(), savehandler);
      IChunkStatusListener ichunkstatuslistener = this.chunkStatusListenerFactory.create(11);
      WorldInfo worldinfo = savehandler.loadWorldInfo();
      if (worldinfo == null) {
         worldinfo = new WorldInfo(this.worldSettings, p_71247_2_);
      } else {
         worldinfo.setWorldName(p_71247_2_);
      }

      worldinfo.func_230145_a_(this.getServerModName(), this.func_230045_q_().isPresent());
      this.loadDataPacks(savehandler.getWorldDirectory(), worldinfo);
      this.loadWorlds(savehandler, worldinfo, this.worldSettings, ichunkstatuslistener);
      if (this.getWorld(DimensionType.OVERWORLD).getWorldInfo().getDifficulty() == null) {
         this.setDifficultyForAllWorlds(this.mc.gameSettings.difficulty, true);
      }

      this.loadInitialChunks(ichunkstatuslistener);
   }

   public boolean init() throws IOException {
      LOGGER.info("Starting integrated minecraft server version " + SharedConstants.getVersion().getName());
      this.setOnlineMode(true);
      this.setCanSpawnAnimals(true);
      this.setCanSpawnNPCs(true);
      this.setAllowPvp(true);
      this.setAllowFlight(true);
      LOGGER.info("Generating keypair");
      this.setKeyPair(CryptManager.generateKeyPair());
      if (!ServerLifecycleHooks.handleServerAboutToStart(this)) {
         return false;
      } else {
         this.loadAllWorlds(this.getFolderName(), this.getWorldName(), this.worldSettings.getSeed(), this.worldSettings.getTerrainType(), this.worldSettings.getGeneratorOptions());
         this.setMOTD(this.getServerOwner() + " - " + this.getWorld(DimensionType.OVERWORLD).getWorldInfo().getWorldName());
         return ServerLifecycleHooks.handleServerStarting(this);
      }
   }

   public void tick(BooleanSupplier p_71217_1_) {
      boolean flag = this.isGamePaused;
      this.isGamePaused = Minecraft.getInstance().getConnection() != null && Minecraft.getInstance().isGamePaused();
      DebugProfiler debugprofiler = this.getProfiler();
      if (!flag && this.isGamePaused) {
         debugprofiler.startSection("autoSave");
         LOGGER.info("Saving and pausing game...");
         this.getPlayerList().saveAllPlayerData();
         this.save(false, false, false);
         debugprofiler.endSection();
      }

      if (!this.isGamePaused) {
         super.tick(p_71217_1_);
         int i = Math.max(2, this.mc.gameSettings.renderDistanceChunks + -1);
         if (i != this.getPlayerList().getViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", i, this.getPlayerList().getViewDistance());
            this.getPlayerList().setViewDistance(i);
         }
      }

   }

   public boolean canStructuresSpawn() {
      return false;
   }

   public GameType getGameType() {
      return this.worldSettings.getGameType();
   }

   public Difficulty getDifficulty() {
      return this.mc.world == null ? this.mc.gameSettings.difficulty : this.mc.world.getWorldInfo().getDifficulty();
   }

   public boolean isHardcore() {
      return this.worldSettings.getHardcoreEnabled();
   }

   public boolean allowLoggingRcon() {
      return true;
   }

   public boolean allowLogging() {
      return true;
   }

   public File getDataDirectory() {
      return this.mc.gameDir;
   }

   public boolean isDedicatedServer() {
      return false;
   }

   public boolean shouldUseNativeTransport() {
      return false;
   }

   public void finalTick(CrashReport p_71228_1_) {
      this.mc.crashed(p_71228_1_);
   }

   public CrashReport addServerInfoToCrashReport(CrashReport p_71230_1_) {
      p_71230_1_ = super.addServerInfoToCrashReport(p_71230_1_);
      p_71230_1_.getCategory().addDetail("Type", (Object)"Integrated Server (map_client.txt)");
      p_71230_1_.getCategory().addDetail("Is Modded", () -> {
         return (String)this.func_230045_q_().orElse("Probably not. Jar signature remains and both client + server brands are untouched.");
      });
      return p_71230_1_;
   }

   public Optional<String> func_230045_q_() {
      String s = ClientBrandRetriever.getClientModName();
      if (!s.equals("vanilla")) {
         return Optional.of("Definitely; Client brand changed to '" + s + "'");
      } else {
         s = this.getServerModName();
         if (!"vanilla".equals(s)) {
            return Optional.of("Definitely; Server brand changed to '" + s + "'");
         } else {
            return Minecraft.class.getSigners() == null ? Optional.of("Very likely; Jar signature invalidated") : Optional.empty();
         }
      }
   }

   public void fillSnooper(Snooper p_70000_1_) {
      super.fillSnooper(p_70000_1_);
      p_70000_1_.addClientStat("snooper_partner", this.mc.getSnooper().getUniqueID());
   }

   public boolean shareToLAN(GameType p_195565_1_, boolean p_195565_2_, int p_195565_3_) {
      try {
         this.getNetworkSystem().addEndpoint((InetAddress)null, p_195565_3_);
         LOGGER.info("Started serving on {}", p_195565_3_);
         this.serverPort = p_195565_3_;
         this.lanServerPing = new LanServerPingThread(this.getMOTD(), p_195565_3_ + "");
         this.lanServerPing.start();
         this.getPlayerList().setGameType(p_195565_1_);
         this.getPlayerList().setCommandsAllowedForAll(p_195565_2_);
         int i = this.getPermissionLevel(this.mc.player.getGameProfile());
         this.mc.player.setPermissionLevel(i);
         Iterator var5 = this.getPlayerList().getPlayers().iterator();

         while(var5.hasNext()) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)var5.next();
            this.getCommandManager().send(serverplayerentity);
         }

         return true;
      } catch (IOException var7) {
         return false;
      }
   }

   public void stopServer() {
      super.stopServer();
      if (this.lanServerPing != null) {
         this.lanServerPing.interrupt();
         this.lanServerPing = null;
      }

   }

   public void initiateShutdown(boolean p_71263_1_) {
      if (this.isServerRunning()) {
         this.runImmediately(() -> {
            Iterator var1 = Lists.newArrayList(this.getPlayerList().getPlayers()).iterator();

            while(var1.hasNext()) {
               ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)var1.next();
               if (!serverplayerentity.getUniqueID().equals(this.playerUuid)) {
                  this.getPlayerList().playerLoggedOut(serverplayerentity);
               }
            }

         });
      }

      super.initiateShutdown(p_71263_1_);
      if (this.lanServerPing != null) {
         this.lanServerPing.interrupt();
         this.lanServerPing = null;
      }

   }

   public boolean getPublic() {
      return this.serverPort > -1;
   }

   public int getServerPort() {
      return this.serverPort;
   }

   public void setGameType(GameType p_71235_1_) {
      super.setGameType(p_71235_1_);
      this.getPlayerList().setGameType(p_71235_1_);
   }

   public boolean isCommandBlockEnabled() {
      return true;
   }

   public int getOpPermissionLevel() {
      return 2;
   }

   public int func_223707_k() {
      return 2;
   }

   public void setPlayerUuid(UUID p_211527_1_) {
      this.playerUuid = p_211527_1_;
   }

   public boolean func_213199_b(GameProfile p_213199_1_) {
      return p_213199_1_.getName().equalsIgnoreCase(this.getServerOwner());
   }
}
