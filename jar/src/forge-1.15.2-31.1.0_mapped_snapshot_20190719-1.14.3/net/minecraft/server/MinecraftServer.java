package net.minecraft.server;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSource;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.profiler.DebugProfiler;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.profiler.Snooper;
import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.ServerPackFinder;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.management.OpEntry;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.WhiteList;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.test.TestCollection;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.WorldOptimizer;
import net.minecraft.util.concurrent.RecursiveEventLoop;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Bootstrap;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ForcedChunksSaveData;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.chunk.listener.LoggingChunkStatusListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerMultiWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraft.world.storage.CommandStorage;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.SessionLockException;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataCallableSave;
import net.minecraft.world.storage.loot.LootPredicateManager;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.BrandingControl;
import net.minecraftforge.fml.StartupQuery;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import net.minecraftforge.fml.hooks.BasicEventHooks;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer extends RecursiveEventLoop<TickDelayedTask> implements ISnooperInfo, ICommandSource, AutoCloseable, Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final File USER_CACHE_FILE = new File("usercache.json");
   private static final CompletableFuture<Unit> field_223713_i;
   public static final WorldSettings DEMO_WORLD_SETTINGS;
   private final SaveFormat anvilConverterForAnvilFile;
   private final Snooper snooper = new Snooper("server", this, Util.milliTime());
   private final File anvilFile;
   private final List<Runnable> tickables = Lists.newArrayList();
   private final DebugProfiler profiler = new DebugProfiler(this::getTickCounter);
   private final NetworkSystem networkSystem;
   protected final IChunkStatusListenerFactory chunkStatusListenerFactory;
   private final ServerStatusResponse statusResponse = new ServerStatusResponse();
   private final Random random = new Random();
   private final DataFixer dataFixer;
   private String hostname;
   private int serverPort = -1;
   private final Map<DimensionType, ServerWorld> worlds = Maps.newIdentityHashMap();
   private PlayerList playerList;
   private volatile boolean serverRunning = true;
   private boolean serverStopped;
   private int tickCounter;
   protected final Proxy serverProxy;
   private boolean onlineMode;
   private boolean preventProxyConnections;
   private boolean canSpawnAnimals;
   private boolean canSpawnNPCs;
   private boolean pvpEnabled;
   private boolean allowFlight;
   @Nullable
   private String motd;
   private int buildLimit;
   private int maxPlayerIdleMinutes;
   public final long[] tickTimeArray = new long[100];
   @Nullable
   private KeyPair serverKeyPair;
   @Nullable
   private String serverOwner;
   private final String folderName;
   @Nullable
   @OnlyIn(Dist.CLIENT)
   private String worldName;
   private boolean isDemo;
   private boolean enableBonusChest;
   private String resourcePackUrl = "";
   private String resourcePackHash = "";
   private volatile boolean serverIsRunning;
   private long timeOfLastWarning;
   @Nullable
   private ITextComponent userMessage;
   private boolean startProfiling;
   private boolean isGamemodeForced;
   @Nullable
   private final YggdrasilAuthenticationService authService;
   private final MinecraftSessionService sessionService;
   private final GameProfileRepository profileRepo;
   private final PlayerProfileCache profileCache;
   private long nanoTimeSinceStatusRefresh;
   protected final Thread serverThread;
   protected long serverTime;
   private long runTasksUntil;
   private boolean isRunningScheduledTasks;
   @OnlyIn(Dist.CLIENT)
   private boolean worldIconSet;
   private final IReloadableResourceManager resourceManager;
   private final ResourcePackList<ResourcePackInfo> resourcePacks;
   @Nullable
   private FolderPackFinder datapackFinder;
   private final Commands commandManager;
   private final RecipeManager recipeManager;
   private final NetworkTagManager networkTagManager;
   private final ServerScoreboard scoreboard;
   @Nullable
   private CommandStorage field_229733_al_;
   private final CustomServerBossInfoManager customBossEvents;
   private final LootPredicateManager field_229734_an_;
   private final LootTableManager lootTableManager;
   private final AdvancementManager advancementManager;
   private final FunctionManager functionManager;
   private final FrameTimer frameTimer;
   private boolean whitelistEnabled;
   private boolean forceWorldUpgrade;
   private boolean eraseCache;
   private float tickTime;
   private final Executor backgroundExecutor;
   @Nullable
   private String serverId;
   private Map<DimensionType, long[]> perWorldTickTimes;
   private int worldArrayMarker;
   private int worldArrayLast;
   private ServerWorld[] worldArray;

   public MinecraftServer(File p_i50590_1_, Proxy p_i50590_2_, DataFixer p_i50590_3_, Commands p_i50590_4_, YggdrasilAuthenticationService p_i50590_5_, MinecraftSessionService p_i50590_6_, GameProfileRepository p_i50590_7_, PlayerProfileCache p_i50590_8_, IChunkStatusListenerFactory p_i50590_9_, String p_i50590_10_) {
      super("Server");
      this.serverThread = (Thread)Util.make(new Thread(SidedThreadGroups.SERVER, this, "Server thread"), (p_lambda$new$1_0_) -> {
         p_lambda$new$1_0_.setUncaughtExceptionHandler((p_lambda$null$0_0_, p_lambda$null$0_1_) -> {
            LOGGER.error(p_lambda$null$0_1_);
         });
      });
      this.serverTime = Util.milliTime();
      this.resourceManager = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA, this.serverThread);
      this.resourcePacks = new ResourcePackList(ResourcePackInfo::new);
      this.recipeManager = new RecipeManager();
      this.networkTagManager = new NetworkTagManager();
      this.scoreboard = new ServerScoreboard(this);
      this.customBossEvents = new CustomServerBossInfoManager(this);
      this.field_229734_an_ = new LootPredicateManager();
      this.lootTableManager = new LootTableManager(this.field_229734_an_);
      this.advancementManager = new AdvancementManager();
      this.functionManager = new FunctionManager(this);
      this.frameTimer = new FrameTimer();
      this.perWorldTickTimes = Maps.newIdentityHashMap();
      this.worldArrayMarker = 0;
      this.worldArrayLast = -1;
      this.serverProxy = p_i50590_2_;
      this.commandManager = p_i50590_4_;
      this.authService = p_i50590_5_;
      this.sessionService = p_i50590_6_;
      this.profileRepo = p_i50590_7_;
      this.profileCache = p_i50590_8_;
      this.anvilFile = p_i50590_1_;
      this.networkSystem = new NetworkSystem(this);
      this.chunkStatusListenerFactory = p_i50590_9_;
      this.anvilConverterForAnvilFile = new SaveFormat(p_i50590_1_.toPath(), p_i50590_1_.toPath().resolve("../backups"), p_i50590_3_);
      this.dataFixer = p_i50590_3_;
      this.resourceManager.addReloadListener(this.networkTagManager);
      this.resourceManager.addReloadListener(this.field_229734_an_);
      this.resourceManager.addReloadListener(this.recipeManager);
      this.resourceManager.addReloadListener(this.lootTableManager);
      this.resourceManager.addReloadListener(this.functionManager);
      this.resourceManager.addReloadListener(this.advancementManager);
      this.backgroundExecutor = Util.getServerExecutor();
      this.folderName = p_i50590_10_;
   }

   private void func_213204_a(DimensionSavedDataManager p_213204_1_) {
      ScoreboardSaveData scoreboardsavedata = (ScoreboardSaveData)p_213204_1_.getOrCreate(ScoreboardSaveData::new, "scoreboard");
      scoreboardsavedata.setScoreboard(this.getScoreboard());
      this.getScoreboard().addDirtyRunnable(new WorldSavedDataCallableSave(scoreboardsavedata));
   }

   protected abstract boolean init() throws IOException;

   protected void convertMapIfNeeded(String p_71237_1_) {
      if (this.getActiveAnvilConverter().isOldMapFormat(p_71237_1_)) {
         LOGGER.info("Converting map!");
         this.setUserMessage(new TranslationTextComponent("menu.convertingLevel", new Object[0]));
         this.getActiveAnvilConverter().convertMapFormat(p_71237_1_, new IProgressUpdate() {
            private long startTime = Util.milliTime();

            public void displaySavingString(ITextComponent p_200210_1_) {
            }

            @OnlyIn(Dist.CLIENT)
            public void resetProgressAndMessage(ITextComponent p_200211_1_) {
            }

            public void setLoadingProgress(int p_73718_1_) {
               if (Util.milliTime() - this.startTime >= 1000L) {
                  this.startTime = Util.milliTime();
                  MinecraftServer.LOGGER.info("Converting... {}%", p_73718_1_);
               }

            }

            @OnlyIn(Dist.CLIENT)
            public void setDoneWorking() {
            }

            public void displayLoadingString(ITextComponent p_200209_1_) {
            }
         });
      }

      if (this.forceWorldUpgrade) {
         LOGGER.info("Forcing world upgrade!");
         WorldInfo worldinfo = this.getActiveAnvilConverter().getWorldInfo(this.getFolderName());
         if (worldinfo != null) {
            WorldOptimizer worldoptimizer = new WorldOptimizer(this.getFolderName(), this.getActiveAnvilConverter(), worldinfo, this.eraseCache);
            ITextComponent itextcomponent = null;

            while(!worldoptimizer.isFinished()) {
               ITextComponent itextcomponent1 = worldoptimizer.getStatusText();
               if (itextcomponent != itextcomponent1) {
                  itextcomponent = itextcomponent1;
                  LOGGER.info(worldoptimizer.getStatusText().getString());
               }

               int i = worldoptimizer.getTotalChunks();
               if (i > 0) {
                  int j = worldoptimizer.getConverted() + worldoptimizer.getSkipped();
                  LOGGER.info("{}% completed ({} / {} chunks)...", MathHelper.floor((float)j / (float)i * 100.0F), j, i);
               }

               if (this.isServerStopped()) {
                  worldoptimizer.cancel();
               } else {
                  try {
                     Thread.sleep(1000L);
                  } catch (InterruptedException var8) {
                  }
               }
            }
         }
      }

   }

   protected synchronized void setUserMessage(ITextComponent p_200245_1_) {
      this.userMessage = p_200245_1_;
   }

   protected void loadAllWorlds(String p_71247_1_, String p_71247_2_, long p_71247_3_, WorldType p_71247_5_, JsonElement p_71247_6_) {
      this.convertMapIfNeeded(p_71247_1_);
      this.setUserMessage(new TranslationTextComponent("menu.loadingLevel", new Object[0]));
      SaveHandler savehandler = this.getActiveAnvilConverter().getSaveLoader(p_71247_1_, this);
      this.setResourcePackFromWorld(this.getFolderName(), savehandler);
      IChunkStatusListener ichunkstatuslistener = this.chunkStatusListenerFactory.create(11);
      WorldInfo worldinfo = savehandler.loadWorldInfo();
      WorldSettings worldsettings;
      if (worldinfo == null) {
         if (this.isDemo()) {
            worldsettings = DEMO_WORLD_SETTINGS;
         } else {
            worldsettings = new WorldSettings(p_71247_3_, this.getGameType(), this.canStructuresSpawn(), this.isHardcore(), p_71247_5_);
            worldsettings.setGeneratorOptions(p_71247_6_);
            if (this.enableBonusChest) {
               worldsettings.enableBonusChest();
            }
         }

         worldinfo = new WorldInfo(worldsettings, p_71247_2_);
      } else {
         worldinfo.setWorldName(p_71247_2_);
         worldsettings = new WorldSettings(worldinfo);
      }

      worldinfo.func_230145_a_(this.getServerModName(), this.func_230045_q_().isPresent());
      this.loadDataPacks(savehandler.getWorldDirectory(), worldinfo);
      this.loadWorlds(savehandler, worldinfo, worldsettings, ichunkstatuslistener);
      this.setDifficultyForAllWorlds(this.getDifficulty(), true);
      this.loadInitialChunks(ichunkstatuslistener);
   }

   protected void loadWorlds(SaveHandler p_213194_1_, WorldInfo p_213194_2_, WorldSettings p_213194_3_, IChunkStatusListener p_213194_4_) {
      DimensionManager.fireRegister();
      if (this.isDemo()) {
         p_213194_2_.populateFromWorldSettings(DEMO_WORLD_SETTINGS);
      }

      ServerWorld serverworld = new ServerWorld(this, this.backgroundExecutor, p_213194_1_, p_213194_2_, DimensionType.OVERWORLD, this.profiler, p_213194_4_);
      this.worlds.put(DimensionType.OVERWORLD, serverworld);
      DimensionSavedDataManager dimensionsaveddatamanager = serverworld.getSavedData();
      this.func_213204_a(dimensionsaveddatamanager);
      this.field_229733_al_ = new CommandStorage(dimensionsaveddatamanager);
      serverworld.getWorldBorder().copyFrom(p_213194_2_);
      ServerWorld serverworld1 = this.getWorld(DimensionType.OVERWORLD);
      if (!p_213194_2_.isInitialized()) {
         try {
            serverworld1.createSpawnPosition(p_213194_3_);
            if (p_213194_2_.getGenerator() == WorldType.DEBUG_ALL_BLOCK_STATES) {
               this.applyDebugWorldInfo(p_213194_2_);
            }

            p_213194_2_.setInitialized(true);
         } catch (Throwable var12) {
            CrashReport crashreport = CrashReport.makeCrashReport(var12, "Exception initializing level");

            try {
               serverworld1.fillCrashReport(crashreport);
            } catch (Throwable var11) {
            }

            throw new ReportedException(crashreport);
         }

         p_213194_2_.setInitialized(true);
      }

      this.getPlayerList().func_212504_a(serverworld1);
      if (p_213194_2_.getCustomBossEvents() != null) {
         this.getCustomBossEvents().read(p_213194_2_.getCustomBossEvents());
      }

      DimensionType dimensiontype;
      for(Iterator var8 = DimensionType.getAll().iterator(); var8.hasNext(); MinecraftForge.EVENT_BUS.post(new WorldEvent.Load((IWorld)this.worlds.get(dimensiontype)))) {
         dimensiontype = (DimensionType)var8.next();
         if (dimensiontype != DimensionType.OVERWORLD) {
            this.worlds.put(dimensiontype, new ServerMultiWorld(serverworld1, this, this.backgroundExecutor, p_213194_1_, dimensiontype, this.profiler, p_213194_4_));
         }
      }

   }

   private void applyDebugWorldInfo(WorldInfo p_213188_1_) {
      p_213188_1_.setMapFeaturesEnabled(false);
      p_213188_1_.setAllowCommands(true);
      p_213188_1_.setRaining(false);
      p_213188_1_.setThundering(false);
      p_213188_1_.setClearWeatherTime(1000000000);
      p_213188_1_.setDayTime(6000L);
      p_213188_1_.setGameType(GameType.SPECTATOR);
      p_213188_1_.setHardcore(false);
      p_213188_1_.setDifficulty(Difficulty.PEACEFUL);
      p_213188_1_.setDifficultyLocked(true);
      ((GameRules.BooleanValue)p_213188_1_.getGameRulesInstance().get(GameRules.DO_DAYLIGHT_CYCLE)).set(false, this);
   }

   protected void loadDataPacks(File p_195560_1_, WorldInfo p_195560_2_) {
      this.resourcePacks.addPackFinder(new ServerPackFinder());
      this.datapackFinder = new FolderPackFinder(new File(p_195560_1_, "datapacks"));
      this.resourcePacks.addPackFinder(this.datapackFinder);
      this.resourcePacks.reloadPacksFromFinders();
      List<ResourcePackInfo> list = Lists.newArrayList();
      Iterator var4 = p_195560_2_.getEnabledDataPacks().iterator();

      while(var4.hasNext()) {
         String s = (String)var4.next();
         ResourcePackInfo resourcepackinfo = this.resourcePacks.getPackInfo(s);
         if (resourcepackinfo != null) {
            list.add(resourcepackinfo);
         } else {
            LOGGER.warn("Missing data pack {}", s);
         }
      }

      this.resourcePacks.setEnabledPacks(list);
      this.loadDataPacks(p_195560_2_);
      this.func_229737_ba_();
   }

   protected void loadInitialChunks(IChunkStatusListener p_213186_1_) {
      this.setUserMessage(new TranslationTextComponent("menu.generatingTerrain", new Object[0]));
      ServerWorld serverworld = this.getWorld(DimensionType.OVERWORLD);
      LOGGER.info("Preparing start region for dimension " + DimensionType.getKey(serverworld.dimension.getType()));
      BlockPos blockpos = serverworld.getSpawnPoint();
      p_213186_1_.start(new ChunkPos(blockpos));
      ServerChunkProvider serverchunkprovider = serverworld.getChunkProvider();
      serverchunkprovider.getLightManager().func_215598_a(500);
      this.serverTime = Util.milliTime();
      serverchunkprovider.func_217228_a(TicketType.START, new ChunkPos(blockpos), 11, Unit.INSTANCE);

      while(serverchunkprovider.func_217229_b() != 441) {
         this.serverTime = Util.milliTime() + 10L;
         this.runScheduledTasks();
      }

      this.serverTime = Util.milliTime() + 10L;
      this.runScheduledTasks();
      Iterator var5 = DimensionType.getAll().iterator();

      while(true) {
         DimensionType dimensiontype;
         ForcedChunksSaveData forcedchunkssavedata;
         do {
            if (!var5.hasNext()) {
               this.serverTime = Util.milliTime() + 10L;
               this.runScheduledTasks();
               p_213186_1_.stop();
               serverchunkprovider.getLightManager().func_215598_a(5);
               return;
            }

            dimensiontype = (DimensionType)var5.next();
            forcedchunkssavedata = (ForcedChunksSaveData)this.getWorld(dimensiontype).getSavedData().get(ForcedChunksSaveData::new, "chunks");
         } while(forcedchunkssavedata == null);

         ServerWorld serverworld1 = this.getWorld(dimensiontype);
         LongIterator longiterator = forcedchunkssavedata.getChunks().iterator();

         while(longiterator.hasNext()) {
            long i = longiterator.nextLong();
            ChunkPos chunkpos = new ChunkPos(i);
            serverworld1.getChunkProvider().forceChunk(chunkpos, true);
         }
      }
   }

   protected void setResourcePackFromWorld(String p_175584_1_, SaveHandler p_175584_2_) {
      File file1 = new File(p_175584_2_.getWorldDirectory(), "resources.zip");
      if (file1.isFile()) {
         try {
            this.setResourcePack("level://" + URLEncoder.encode(p_175584_1_, StandardCharsets.UTF_8.toString()) + "/resources.zip", "");
         } catch (UnsupportedEncodingException var5) {
            LOGGER.warn("Something went wrong url encoding {}", p_175584_1_);
         }
      }

   }

   public abstract boolean canStructuresSpawn();

   public abstract GameType getGameType();

   public abstract Difficulty getDifficulty();

   public abstract boolean isHardcore();

   public abstract int getOpPermissionLevel();

   public abstract int func_223707_k();

   public abstract boolean allowLoggingRcon();

   public boolean save(boolean p_213211_1_, boolean p_213211_2_, boolean p_213211_3_) {
      boolean flag = false;

      for(Iterator var5 = this.getWorlds().iterator(); var5.hasNext(); flag = true) {
         ServerWorld serverworld = (ServerWorld)var5.next();
         if (!p_213211_1_) {
            LOGGER.info("Saving chunks for level '{}'/{}", serverworld.getWorldInfo().getWorldName(), DimensionType.getKey(serverworld.dimension.getType()));
         }

         try {
            serverworld.save((IProgressUpdate)null, p_213211_2_, serverworld.disableLevelSaving && !p_213211_3_);
         } catch (SessionLockException var8) {
            LOGGER.warn(var8.getMessage());
         }
      }

      ServerWorld serverworld1 = this.getWorld(DimensionType.OVERWORLD);
      WorldInfo worldinfo = serverworld1.getWorldInfo();
      serverworld1.getWorldBorder().copyTo(worldinfo);
      worldinfo.setCustomBossEvents(this.getCustomBossEvents().write());
      serverworld1.getSaveHandler().saveWorldInfoWithPlayer(worldinfo, this.getPlayerList().getHostPlayerData());
      return flag;
   }

   public void close() {
      this.stopServer();
   }

   protected void stopServer() {
      LOGGER.info("Stopping server");
      if (this.getNetworkSystem() != null) {
         this.getNetworkSystem().terminateEndpoints();
      }

      if (this.playerList != null) {
         LOGGER.info("Saving players");
         this.playerList.saveAllPlayerData();
         this.playerList.removeAllPlayers();
      }

      LOGGER.info("Saving worlds");
      Iterator var1 = this.getWorlds().iterator();

      ServerWorld serverworld1;
      while(var1.hasNext()) {
         serverworld1 = (ServerWorld)var1.next();
         if (serverworld1 != null) {
            serverworld1.disableLevelSaving = false;
         }
      }

      this.save(false, true, false);
      var1 = this.getWorlds().iterator();

      while(var1.hasNext()) {
         serverworld1 = (ServerWorld)var1.next();
         if (serverworld1 != null) {
            try {
               MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(serverworld1));
               serverworld1.close();
            } catch (IOException var4) {
               LOGGER.error("Exception closing the level", var4);
            }
         }
      }

      if (this.snooper.isSnooperRunning()) {
         this.snooper.stop();
      }

   }

   public String getServerHostname() {
      return this.hostname;
   }

   public void setHostname(String p_71189_1_) {
      this.hostname = p_71189_1_;
   }

   public boolean isServerRunning() {
      return this.serverRunning;
   }

   public void initiateShutdown(boolean p_71263_1_) {
      this.serverRunning = false;
      if (p_71263_1_) {
         try {
            this.serverThread.join();
         } catch (InterruptedException var3) {
            LOGGER.error("Error while shutting down", var3);
         }
      }

   }

   public void run() {
      try {
         if (this.init()) {
            ServerLifecycleHooks.handleServerStarted(this);
            this.serverTime = Util.milliTime();
            this.statusResponse.setServerDescription(new StringTextComponent(this.motd));
            this.statusResponse.setVersion(new ServerStatusResponse.Version(SharedConstants.getVersion().getName(), SharedConstants.getVersion().getProtocolVersion()));
            this.applyServerIconToResponse(this.statusResponse);

            while(this.serverRunning) {
               long i = Util.milliTime() - this.serverTime;
               if (i > 2000L && this.serverTime - this.timeOfLastWarning >= 15000L) {
                  long j = i / 50L;
                  LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", i, j);
                  this.serverTime += j * 50L;
                  this.timeOfLastWarning = this.serverTime;
               }

               this.serverTime += 50L;
               if (this.startProfiling) {
                  this.startProfiling = false;
                  this.profiler.func_219899_d().func_219939_d();
               }

               this.profiler.startTick();
               this.profiler.startSection("tick");
               this.tick(this::isAheadOfTime);
               this.profiler.endStartSection("nextTickWait");
               this.isRunningScheduledTasks = true;
               this.runTasksUntil = Math.max(Util.milliTime() + 50L, this.serverTime);
               this.runScheduledTasks();
               this.profiler.endSection();
               this.profiler.endTick();
               this.serverIsRunning = true;
            }

            ServerLifecycleHooks.handleServerStopping(this);
            ServerLifecycleHooks.expectServerStopped();
         } else {
            ServerLifecycleHooks.expectServerStopped();
            this.finalTick((CrashReport)null);
         }
      } catch (StartupQuery.AbortedException var68) {
         ServerLifecycleHooks.expectServerStopped();
      } catch (Throwable var69) {
         LOGGER.error("Encountered an unexpected exception", var69);
         CrashReport crashreport;
         if (var69 instanceof ReportedException) {
            crashreport = this.addServerInfoToCrashReport(((ReportedException)var69).getCrashReport());
         } else {
            crashreport = this.addServerInfoToCrashReport(new CrashReport("Exception in server tick loop", var69));
         }

         File file1 = new File(new File(this.getDataDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");
         if (crashreport.saveToFile(file1)) {
            LOGGER.error("This crash report has been saved to: {}", file1.getAbsolutePath());
         } else {
            LOGGER.error("We were unable to save this crash report to disk.");
         }

         ServerLifecycleHooks.expectServerStopped();
         this.finalTick(crashreport);
      } finally {
         try {
            this.serverStopped = true;
            this.stopServer();
         } catch (Throwable var66) {
            LOGGER.error("Exception stopping the server", var66);
         } finally {
            ServerLifecycleHooks.handleServerStopped(this);
            this.systemExitNow();
         }

      }

   }

   private boolean isAheadOfTime() {
      return this.isTaskRunning() || Util.milliTime() < (this.isRunningScheduledTasks ? this.runTasksUntil : this.serverTime);
   }

   protected void runScheduledTasks() {
      this.drainTasks();
      this.driveUntil(() -> {
         return !this.isAheadOfTime();
      });
   }

   protected TickDelayedTask wrapTask(Runnable p_212875_1_) {
      return new TickDelayedTask(this.tickCounter, p_212875_1_);
   }

   protected boolean canRun(TickDelayedTask p_212874_1_) {
      return p_212874_1_.getScheduledTime() + 3 < this.tickCounter || this.isAheadOfTime();
   }

   public boolean driveOne() {
      boolean flag = this.func_213205_aW();
      this.isRunningScheduledTasks = flag;
      return flag;
   }

   private boolean func_213205_aW() {
      if (super.driveOne()) {
         return true;
      } else {
         if (this.isAheadOfTime()) {
            Iterator var1 = this.getWorlds().iterator();

            while(var1.hasNext()) {
               ServerWorld serverworld = (ServerWorld)var1.next();
               if (serverworld.getChunkProvider().func_217234_d()) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   protected void run(TickDelayedTask p_213166_1_) {
      this.getProfiler().func_230035_c_("runTask");
      super.run(p_213166_1_);
   }

   public void applyServerIconToResponse(ServerStatusResponse p_184107_1_) {
      File file1 = this.getFile("server-icon.png");
      if (!file1.exists()) {
         file1 = this.getActiveAnvilConverter().getFile(this.getFolderName(), "icon.png");
      }

      if (file1.isFile()) {
         ByteBuf bytebuf = Unpooled.buffer();

         try {
            BufferedImage bufferedimage = ImageIO.read(file1);
            Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
            Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
            ImageIO.write(bufferedimage, "PNG", new ByteBufOutputStream(bytebuf));
            ByteBuffer bytebuffer = Base64.getEncoder().encode(bytebuf.nioBuffer());
            p_184107_1_.setFavicon("data:image/png;base64," + StandardCharsets.UTF_8.decode(bytebuffer));
         } catch (Exception var9) {
            LOGGER.error("Couldn't load server icon", var9);
         } finally {
            bytebuf.release();
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean isWorldIconSet() {
      this.worldIconSet = this.worldIconSet || this.getWorldIconFile().isFile();
      return this.worldIconSet;
   }

   @OnlyIn(Dist.CLIENT)
   public File getWorldIconFile() {
      return this.getActiveAnvilConverter().getFile(this.getFolderName(), "icon.png");
   }

   public File getDataDirectory() {
      return new File(".");
   }

   protected void finalTick(CrashReport p_71228_1_) {
   }

   protected void systemExitNow() {
   }

   protected void tick(BooleanSupplier p_71217_1_) {
      long i = Util.nanoTime();
      BasicEventHooks.onPreServerTick();
      ++this.tickCounter;
      this.updateTimeLightAndEntities(p_71217_1_);
      if (i - this.nanoTimeSinceStatusRefresh >= 5000000000L) {
         this.nanoTimeSinceStatusRefresh = i;
         this.statusResponse.setPlayers(new ServerStatusResponse.Players(this.getMaxPlayers(), this.getCurrentPlayerCount()));
         GameProfile[] agameprofile = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
         int j = MathHelper.nextInt(this.random, 0, this.getCurrentPlayerCount() - agameprofile.length);

         for(int k = 0; k < agameprofile.length; ++k) {
            agameprofile[k] = ((ServerPlayerEntity)this.playerList.getPlayers().get(j + k)).getGameProfile();
         }

         Collections.shuffle(Arrays.asList(agameprofile));
         this.statusResponse.getPlayers().setPlayers(agameprofile);
         this.statusResponse.invalidateJson();
      }

      if (this.tickCounter % 6000 == 0) {
         LOGGER.debug("Autosave started");
         this.profiler.startSection("save");
         this.playerList.saveAllPlayerData();
         this.save(true, false, false);
         this.profiler.endSection();
         LOGGER.debug("Autosave finished");
      }

      this.profiler.startSection("snooper");
      if (!this.snooper.isSnooperRunning() && this.tickCounter > 100) {
         this.snooper.start();
      }

      if (this.tickCounter % 6000 == 0) {
         this.snooper.addMemoryStatsToSnooper();
      }

      this.profiler.endSection();
      this.profiler.startSection("tallying");
      long l = this.tickTimeArray[this.tickCounter % 100] = Util.nanoTime() - i;
      this.tickTime = this.tickTime * 0.8F + (float)l / 1000000.0F * 0.19999999F;
      long i1 = Util.nanoTime();
      this.frameTimer.addFrame(i1 - i);
      this.profiler.endSection();
      BasicEventHooks.onPostServerTick();
   }

   protected void updateTimeLightAndEntities(BooleanSupplier p_71190_1_) {
      this.profiler.startSection("commandFunctions");
      this.getFunctionManager().tick();
      this.profiler.endStartSection("levels");
      ServerWorld[] var2 = this.getWorldArray();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ServerWorld serverworld = var2[var4];
         long tickStart = Util.nanoTime();
         if (serverworld.dimension.getType() == DimensionType.OVERWORLD || this.getAllowNether()) {
            this.profiler.startSection(() -> {
               return serverworld.getWorldInfo().getWorldName() + " " + Registry.DIMENSION_TYPE.getKey(serverworld.dimension.getType());
            });
            if (this.tickCounter % 20 == 0) {
               this.profiler.startSection("timeSync");
               this.playerList.sendPacketToAllPlayersInDimension(new SUpdateTimePacket(serverworld.getGameTime(), serverworld.getDayTime(), serverworld.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)), serverworld.dimension.getType());
               this.profiler.endSection();
            }

            this.profiler.startSection("tick");
            BasicEventHooks.onPreWorldTick(serverworld);

            try {
               serverworld.tick(p_71190_1_);
            } catch (Throwable var10) {
               CrashReport crashreport = CrashReport.makeCrashReport(var10, "Exception ticking world");
               serverworld.fillCrashReport(crashreport);
               throw new ReportedException(crashreport);
            }

            BasicEventHooks.onPostWorldTick(serverworld);
            this.profiler.endSection();
            this.profiler.endSection();
         }

         ((long[])this.perWorldTickTimes.computeIfAbsent(serverworld.getDimension().getType(), (p_lambda$updateTimeLightAndEntities$4_0_) -> {
            return new long[100];
         }))[this.tickCounter % 100] = Util.nanoTime() - tickStart;
      }

      this.profiler.endStartSection("dim_unloading");
      DimensionManager.unloadWorlds(this, this.tickCounter % 200 == 0);
      this.profiler.endStartSection("connection");
      this.getNetworkSystem().tick();
      this.profiler.endStartSection("players");
      this.playerList.tick();
      if (SharedConstants.developmentMode) {
         TestCollection.field_229570_a_.func_229574_b_();
      }

      this.profiler.endStartSection("server gui refresh");

      for(int i = 0; i < this.tickables.size(); ++i) {
         ((Runnable)this.tickables.get(i)).run();
      }

      this.profiler.endSection();
   }

   public boolean getAllowNether() {
      return true;
   }

   public void registerTickable(Runnable p_82010_1_) {
      this.tickables.add(p_82010_1_);
   }

   public static void main(String[] p_main_0_) {
      OptionParser optionparser = new OptionParser();
      OptionSpec<Void> optionspec = optionparser.accepts("nogui");
      OptionSpec<Void> optionspec1 = optionparser.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
      OptionSpec<Void> optionspec2 = optionparser.accepts("demo");
      OptionSpec<Void> optionspec3 = optionparser.accepts("bonusChest");
      OptionSpec<Void> optionspec4 = optionparser.accepts("forceUpgrade");
      OptionSpec<Void> optionspec5 = optionparser.accepts("eraseCache");
      OptionSpec<Void> optionspec6 = optionparser.accepts("help").forHelp();
      OptionSpec<String> optionspec7 = optionparser.accepts("singleplayer").withRequiredArg();
      OptionSpec<String> optionspec8 = optionparser.accepts("universe").withRequiredArg().defaultsTo(".", new String[0]);
      OptionSpec<String> optionspec9 = optionparser.accepts("world").withRequiredArg();
      OptionSpec<Integer> optionspec10 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(-1, new Integer[0]);
      OptionSpec<String> optionspec11 = optionparser.accepts("serverId").withRequiredArg();
      OptionSpec<String> optionspec12 = optionparser.nonOptions();
      optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);

      try {
         OptionSet optionset = optionparser.parse(p_main_0_);
         if (optionset.has(optionspec6)) {
            optionparser.printHelpOn(System.err);
            return;
         }

         Path path = Paths.get("server.properties");
         ServerPropertiesProvider serverpropertiesprovider = new ServerPropertiesProvider(path);
         serverpropertiesprovider.save();
         Path path1 = Paths.get("eula.txt");
         ServerEula servereula = new ServerEula(path1);
         if (optionset.has(optionspec1)) {
            LOGGER.info("Initialized '" + path.toAbsolutePath().toString() + "' and '" + path1.toAbsolutePath().toString() + "'");
            return;
         }

         if (!servereula.hasAcceptedEULA()) {
            LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
            return;
         }

         CrashReport.func_230188_h_();
         Bootstrap.register();
         Bootstrap.func_218821_c();
         String s = (String)optionset.valueOf(optionspec8);
         YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
         MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
         GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
         PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(s, USER_CACHE_FILE.getName()));
         String s1 = (String)Optional.ofNullable(optionset.valueOf(optionspec9)).orElse(serverpropertiesprovider.getProperties().worldName);
         if (s1 == null || s1.isEmpty() || (new File(s, s1)).getAbsolutePath().equals((new File(s)).getAbsolutePath())) {
            LOGGER.error("Invalid world directory specified, must not be null, empty or the same directory as your universe! " + s1);
            return;
         }

         final DedicatedServer dedicatedserver = new DedicatedServer(new File(s), serverpropertiesprovider, DataFixesManager.getDataFixer(), yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, playerprofilecache, LoggingChunkStatusListener::new, s1);
         dedicatedserver.setServerOwner((String)optionset.valueOf(optionspec7));
         dedicatedserver.setServerPort((Integer)optionset.valueOf(optionspec10));
         dedicatedserver.setDemo(optionset.has(optionspec2));
         dedicatedserver.canCreateBonusChest(optionset.has(optionspec3));
         dedicatedserver.setForceWorldUpgrade(optionset.has(optionspec4));
         dedicatedserver.setEraseCache(optionset.has(optionspec5));
         dedicatedserver.setServerId((String)optionset.valueOf(optionspec11));
         boolean flag = !optionset.has(optionspec) && !optionset.valuesOf(optionspec12).contains("nogui");
         if (flag && !GraphicsEnvironment.isHeadless()) {
            dedicatedserver.setGuiEnabled();
         }

         dedicatedserver.startServerThread();
         Thread thread = new Thread("Server Shutdown Thread") {
            public void run() {
               dedicatedserver.initiateShutdown(true);
               LogManager.shutdown();
            }
         };
         thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
         Runtime.getRuntime().addShutdownHook(thread);
      } catch (Exception var29) {
         LOGGER.fatal("Failed to start the minecraft server", var29);
      }

   }

   protected void setServerId(String p_213208_1_) {
      this.serverId = p_213208_1_;
   }

   protected void setForceWorldUpgrade(boolean p_212204_1_) {
      this.forceWorldUpgrade = p_212204_1_;
   }

   protected void setEraseCache(boolean p_213197_1_) {
      this.eraseCache = p_213197_1_;
   }

   public void startServerThread() {
      this.serverThread.start();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isThreadAlive() {
      return !this.serverThread.isAlive();
   }

   public File getFile(String p_71209_1_) {
      return new File(this.getDataDirectory(), p_71209_1_);
   }

   public void logInfo(String p_71244_1_) {
      LOGGER.info(p_71244_1_);
   }

   public void logWarning(String p_71236_1_) {
      LOGGER.warn(p_71236_1_);
   }

   public ServerWorld getWorld(DimensionType p_71218_1_) {
      return DimensionManager.getWorld(this, p_71218_1_, true, true);
   }

   public Iterable<ServerWorld> getWorlds() {
      return this.worlds.values();
   }

   public String getMinecraftVersion() {
      return SharedConstants.getVersion().getName();
   }

   public int getCurrentPlayerCount() {
      return this.playerList.getCurrentPlayerCount();
   }

   public int getMaxPlayers() {
      return this.playerList.getMaxPlayers();
   }

   public String[] getOnlinePlayerNames() {
      return this.playerList.getOnlinePlayerNames();
   }

   public boolean isDebuggingEnabled() {
      return false;
   }

   public void logSevere(String p_71201_1_) {
      LOGGER.error(p_71201_1_);
   }

   public void logDebug(String p_71198_1_) {
      if (this.isDebuggingEnabled()) {
         LOGGER.info(p_71198_1_);
      }

   }

   public String getServerModName() {
      return BrandingControl.getServerBranding();
   }

   public CrashReport addServerInfoToCrashReport(CrashReport p_71230_1_) {
      if (this.playerList != null) {
         p_71230_1_.getCategory().addDetail("Player Count", () -> {
            return this.playerList.getCurrentPlayerCount() + " / " + this.playerList.getMaxPlayers() + "; " + this.playerList.getPlayers();
         });
      }

      p_71230_1_.getCategory().addDetail("Data Packs", () -> {
         StringBuilder stringbuilder = new StringBuilder();
         Iterator var2 = this.resourcePacks.getEnabledPacks().iterator();

         while(var2.hasNext()) {
            ResourcePackInfo resourcepackinfo = (ResourcePackInfo)var2.next();
            if (stringbuilder.length() > 0) {
               stringbuilder.append(", ");
            }

            stringbuilder.append(resourcepackinfo.getName());
            if (!resourcepackinfo.getCompatibility().func_198968_a()) {
               stringbuilder.append(" (incompatible)");
            }
         }

         return stringbuilder.toString();
      });
      if (this.serverId != null) {
         p_71230_1_.getCategory().addDetail("Server Id", () -> {
            return this.serverId;
         });
      }

      return p_71230_1_;
   }

   public abstract Optional<String> func_230045_q_();

   public boolean isAnvilFileSet() {
      return this.anvilFile != null;
   }

   public void sendMessage(ITextComponent p_145747_1_) {
      LOGGER.info(p_145747_1_.getString());
   }

   public KeyPair getKeyPair() {
      return this.serverKeyPair;
   }

   public int getServerPort() {
      return this.serverPort;
   }

   public void setServerPort(int p_71208_1_) {
      this.serverPort = p_71208_1_;
   }

   public String getServerOwner() {
      return this.serverOwner;
   }

   public void setServerOwner(String p_71224_1_) {
      this.serverOwner = p_71224_1_;
   }

   public boolean isSinglePlayer() {
      return this.serverOwner != null;
   }

   public String getFolderName() {
      return this.folderName;
   }

   @OnlyIn(Dist.CLIENT)
   public void setWorldName(String p_71246_1_) {
      this.worldName = p_71246_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public String getWorldName() {
      return this.worldName;
   }

   public void setKeyPair(KeyPair p_71253_1_) {
      this.serverKeyPair = p_71253_1_;
   }

   public void setDifficultyForAllWorlds(Difficulty p_147139_1_, boolean p_147139_2_) {
      Iterator var3 = this.getWorlds().iterator();

      while(true) {
         ServerWorld serverworld;
         WorldInfo worldinfo;
         do {
            if (!var3.hasNext()) {
               this.getPlayerList().getPlayers().forEach(this::sendDifficultyToPlayer);
               return;
            }

            serverworld = (ServerWorld)var3.next();
            worldinfo = serverworld.getWorldInfo();
         } while(!p_147139_2_ && worldinfo.isDifficultyLocked());

         if (worldinfo.isHardcore()) {
            worldinfo.setDifficulty(Difficulty.HARD);
            serverworld.setAllowedSpawnTypes(true, true);
         } else if (this.isSinglePlayer()) {
            worldinfo.setDifficulty(p_147139_1_);
            serverworld.setAllowedSpawnTypes(serverworld.getDifficulty() != Difficulty.PEACEFUL, true);
         } else {
            worldinfo.setDifficulty(p_147139_1_);
            serverworld.setAllowedSpawnTypes(this.allowSpawnMonsters(), this.canSpawnAnimals);
         }
      }
   }

   public void setDifficultyLocked(boolean p_213209_1_) {
      Iterator var2 = this.getWorlds().iterator();

      while(var2.hasNext()) {
         ServerWorld serverworld = (ServerWorld)var2.next();
         WorldInfo worldinfo = serverworld.getWorldInfo();
         worldinfo.setDifficultyLocked(p_213209_1_);
      }

      this.getPlayerList().getPlayers().forEach(this::sendDifficultyToPlayer);
   }

   private void sendDifficultyToPlayer(ServerPlayerEntity p_213189_1_) {
      WorldInfo worldinfo = p_213189_1_.getServerWorld().getWorldInfo();
      p_213189_1_.connection.sendPacket(new SServerDifficultyPacket(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
   }

   protected boolean allowSpawnMonsters() {
      return true;
   }

   public boolean isDemo() {
      return this.isDemo;
   }

   public void setDemo(boolean p_71204_1_) {
      this.isDemo = p_71204_1_;
   }

   public void canCreateBonusChest(boolean p_71194_1_) {
      this.enableBonusChest = p_71194_1_;
   }

   public SaveFormat getActiveAnvilConverter() {
      return this.anvilConverterForAnvilFile;
   }

   public String getResourcePackUrl() {
      return this.resourcePackUrl;
   }

   public String getResourcePackHash() {
      return this.resourcePackHash;
   }

   public void setResourcePack(String p_180507_1_, String p_180507_2_) {
      this.resourcePackUrl = p_180507_1_;
      this.resourcePackHash = p_180507_2_;
   }

   public void fillSnooper(Snooper p_70000_1_) {
      p_70000_1_.addClientStat("whitelist_enabled", false);
      p_70000_1_.addClientStat("whitelist_count", 0);
      if (this.playerList != null) {
         p_70000_1_.addClientStat("players_current", this.getCurrentPlayerCount());
         p_70000_1_.addClientStat("players_max", this.getMaxPlayers());
         p_70000_1_.addClientStat("players_seen", this.getWorld(DimensionType.OVERWORLD).getSaveHandler().func_215771_d().length);
      }

      p_70000_1_.addClientStat("uses_auth", this.onlineMode);
      p_70000_1_.addClientStat("gui_state", this.getGuiEnabled() ? "enabled" : "disabled");
      p_70000_1_.addClientStat("run_time", (Util.milliTime() - p_70000_1_.getMinecraftStartTimeMillis()) / 60L * 1000L);
      p_70000_1_.addClientStat("avg_tick_ms", (int)(MathHelper.average(this.tickTimeArray) * 1.0E-6D));
      int i = 0;
      Iterator var3 = this.getWorlds().iterator();

      while(var3.hasNext()) {
         ServerWorld serverworld = (ServerWorld)var3.next();
         if (serverworld != null) {
            WorldInfo worldinfo = serverworld.getWorldInfo();
            p_70000_1_.addClientStat("world[" + i + "][dimension]", serverworld.dimension.getType());
            p_70000_1_.addClientStat("world[" + i + "][mode]", worldinfo.getGameType());
            p_70000_1_.addClientStat("world[" + i + "][difficulty]", serverworld.getDifficulty());
            p_70000_1_.addClientStat("world[" + i + "][hardcore]", worldinfo.isHardcore());
            p_70000_1_.addClientStat("world[" + i + "][generator_name]", worldinfo.getGenerator().getName());
            p_70000_1_.addClientStat("world[" + i + "][generator_version]", worldinfo.getGenerator().getVersion());
            p_70000_1_.addClientStat("world[" + i + "][height]", this.buildLimit);
            p_70000_1_.addClientStat("world[" + i + "][chunks_loaded]", serverworld.getChunkProvider().getLoadedChunkCount());
            ++i;
         }
      }

      p_70000_1_.addClientStat("worlds", i);
   }

   public abstract boolean isDedicatedServer();

   public boolean isServerInOnlineMode() {
      return this.onlineMode;
   }

   public void setOnlineMode(boolean p_71229_1_) {
      this.onlineMode = p_71229_1_;
   }

   public boolean getPreventProxyConnections() {
      return this.preventProxyConnections;
   }

   public void setPreventProxyConnections(boolean p_190517_1_) {
      this.preventProxyConnections = p_190517_1_;
   }

   public boolean getCanSpawnAnimals() {
      return this.canSpawnAnimals;
   }

   public void setCanSpawnAnimals(boolean p_71251_1_) {
      this.canSpawnAnimals = p_71251_1_;
   }

   public boolean getCanSpawnNPCs() {
      return this.canSpawnNPCs;
   }

   public abstract boolean shouldUseNativeTransport();

   public void setCanSpawnNPCs(boolean p_71257_1_) {
      this.canSpawnNPCs = p_71257_1_;
   }

   public boolean isPVPEnabled() {
      return this.pvpEnabled;
   }

   public void setAllowPvp(boolean p_71188_1_) {
      this.pvpEnabled = p_71188_1_;
   }

   public boolean isFlightAllowed() {
      return this.allowFlight;
   }

   public void setAllowFlight(boolean p_71245_1_) {
      this.allowFlight = p_71245_1_;
   }

   public abstract boolean isCommandBlockEnabled();

   public String getMOTD() {
      return this.motd;
   }

   public void setMOTD(String p_71205_1_) {
      this.motd = p_71205_1_;
   }

   public int getBuildLimit() {
      return this.buildLimit;
   }

   public void setBuildLimit(int p_71191_1_) {
      this.buildLimit = p_71191_1_;
   }

   public boolean isServerStopped() {
      return this.serverStopped;
   }

   public PlayerList getPlayerList() {
      return this.playerList;
   }

   public void setPlayerList(PlayerList p_184105_1_) {
      this.playerList = p_184105_1_;
   }

   public abstract boolean getPublic();

   public void setGameType(GameType p_71235_1_) {
      Iterator var2 = this.getWorlds().iterator();

      while(var2.hasNext()) {
         ServerWorld serverworld = (ServerWorld)var2.next();
         serverworld.getWorldInfo().setGameType(p_71235_1_);
      }

   }

   @Nullable
   public NetworkSystem getNetworkSystem() {
      return this.networkSystem;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean serverIsInRunLoop() {
      return this.serverIsRunning;
   }

   public boolean getGuiEnabled() {
      return false;
   }

   public abstract boolean shareToLAN(GameType var1, boolean var2, int var3);

   public int getTickCounter() {
      return this.tickCounter;
   }

   public void enableProfiling() {
      this.startProfiling = true;
   }

   @OnlyIn(Dist.CLIENT)
   public Snooper getSnooper() {
      return this.snooper;
   }

   public int getSpawnProtectionSize() {
      return 16;
   }

   public boolean isBlockProtected(World p_175579_1_, BlockPos p_175579_2_, PlayerEntity p_175579_3_) {
      return false;
   }

   public void setForceGamemode(boolean p_104055_1_) {
      this.isGamemodeForced = p_104055_1_;
   }

   public boolean getForceGamemode() {
      return this.isGamemodeForced;
   }

   public int getMaxPlayerIdleMinutes() {
      return this.maxPlayerIdleMinutes;
   }

   public void setPlayerIdleTimeout(int p_143006_1_) {
      this.maxPlayerIdleMinutes = p_143006_1_;
   }

   public MinecraftSessionService getMinecraftSessionService() {
      return this.sessionService;
   }

   public GameProfileRepository getGameProfileRepository() {
      return this.profileRepo;
   }

   public PlayerProfileCache getPlayerProfileCache() {
      return this.profileCache;
   }

   public ServerStatusResponse getServerStatusResponse() {
      return this.statusResponse;
   }

   public void refreshStatusNextTick() {
      this.nanoTimeSinceStatusRefresh = 0L;
   }

   public int getMaxWorldSize() {
      return 29999984;
   }

   public boolean shouldDeferTasks() {
      return super.shouldDeferTasks() && !this.isServerStopped();
   }

   public Thread getExecutionThread() {
      return this.serverThread;
   }

   public int getNetworkCompressionThreshold() {
      return 256;
   }

   public long getServerTime() {
      return this.serverTime;
   }

   public DataFixer getDataFixer() {
      return this.dataFixer;
   }

   public int getSpawnRadius(@Nullable ServerWorld p_184108_1_) {
      return p_184108_1_ != null ? p_184108_1_.getGameRules().getInt(GameRules.SPAWN_RADIUS) : 10;
   }

   public AdvancementManager getAdvancementManager() {
      return this.advancementManager;
   }

   public FunctionManager getFunctionManager() {
      return this.functionManager;
   }

   public void reload() {
      if (!this.isOnExecutionThread()) {
         this.execute(this::reload);
      } else {
         this.getPlayerList().saveAllPlayerData();
         this.resourcePacks.reloadPacksFromFinders();
         this.loadDataPacks(this.getWorld(DimensionType.OVERWORLD).getWorldInfo());
         this.getPlayerList().reloadResources();
         this.func_229737_ba_();
      }

   }

   private void loadDataPacks(WorldInfo p_195568_1_) {
      List<ResourcePackInfo> list = Lists.newArrayList(this.resourcePacks.getEnabledPacks());
      Iterator var3 = this.resourcePacks.getAllPacks().iterator();

      while(var3.hasNext()) {
         ResourcePackInfo resourcepackinfo = (ResourcePackInfo)var3.next();
         if (!p_195568_1_.getDisabledDataPacks().contains(resourcepackinfo.getName()) && !list.contains(resourcepackinfo)) {
            LOGGER.info("Found new data pack {}, loading it automatically", resourcepackinfo.getName());
            resourcepackinfo.getPriority().func_198993_a(list, resourcepackinfo, (p_lambda$loadDataPacks$8_0_) -> {
               return p_lambda$loadDataPacks$8_0_;
            }, false);
         }
      }

      this.resourcePacks.setEnabledPacks(list);
      List<IResourcePack> list1 = Lists.newArrayList();
      this.resourcePacks.getEnabledPacks().forEach((p_lambda$loadDataPacks$9_1_) -> {
         list1.add(p_lambda$loadDataPacks$9_1_.getResourcePack());
      });
      CompletableFuture<Unit> completablefuture = this.resourceManager.reloadResourcesAndThen(this.backgroundExecutor, this, list1, field_223713_i);
      this.driveUntil(completablefuture::isDone);

      try {
         completablefuture.get();
      } catch (Exception var6) {
         LOGGER.error("Failed to reload data packs", var6);
      }

      p_195568_1_.getEnabledDataPacks().clear();
      p_195568_1_.getDisabledDataPacks().clear();
      this.resourcePacks.getEnabledPacks().forEach((p_lambda$loadDataPacks$10_1_) -> {
         p_195568_1_.getEnabledDataPacks().add(p_lambda$loadDataPacks$10_1_.getName());
      });
      this.resourcePacks.getAllPacks().forEach((p_lambda$loadDataPacks$11_2_) -> {
         if (!this.resourcePacks.getEnabledPacks().contains(p_lambda$loadDataPacks$11_2_)) {
            p_195568_1_.getDisabledDataPacks().add(p_lambda$loadDataPacks$11_2_.getName());
         }

      });
   }

   public void kickPlayersNotWhitelisted(CommandSource p_205743_1_) {
      if (this.isWhitelistEnabled()) {
         PlayerList playerlist = p_205743_1_.getServer().getPlayerList();
         WhiteList whitelist = playerlist.getWhitelistedPlayers();
         if (whitelist.isLanServer()) {
            Iterator var4 = Lists.newArrayList(playerlist.getPlayers()).iterator();

            while(var4.hasNext()) {
               ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)var4.next();
               if (!whitelist.isWhitelisted(serverplayerentity.getGameProfile())) {
                  serverplayerentity.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.not_whitelisted", new Object[0]));
               }
            }
         }
      }

   }

   public IReloadableResourceManager getResourceManager() {
      return this.resourceManager;
   }

   public ResourcePackList<ResourcePackInfo> getResourcePacks() {
      return this.resourcePacks;
   }

   public Commands getCommandManager() {
      return this.commandManager;
   }

   public CommandSource getCommandSource() {
      return new CommandSource(this, this.getWorld(DimensionType.OVERWORLD) == null ? Vec3d.ZERO : new Vec3d(this.getWorld(DimensionType.OVERWORLD).getSpawnPoint()), Vec2f.ZERO, this.getWorld(DimensionType.OVERWORLD), 4, "Server", new StringTextComponent("Server"), this, (Entity)null);
   }

   public boolean shouldReceiveFeedback() {
      return true;
   }

   public boolean shouldReceiveErrors() {
      return true;
   }

   public RecipeManager getRecipeManager() {
      return this.recipeManager;
   }

   public NetworkTagManager getNetworkTagManager() {
      return this.networkTagManager;
   }

   public ServerScoreboard getScoreboard() {
      return this.scoreboard;
   }

   public CommandStorage func_229735_aN_() {
      if (this.field_229733_al_ == null) {
         throw new NullPointerException("Called before server init");
      } else {
         return this.field_229733_al_;
      }
   }

   public LootTableManager getLootTableManager() {
      return this.lootTableManager;
   }

   public LootPredicateManager func_229736_aP_() {
      return this.field_229734_an_;
   }

   public GameRules getGameRules() {
      return this.getWorld(DimensionType.OVERWORLD).getGameRules();
   }

   public CustomServerBossInfoManager getCustomBossEvents() {
      return this.customBossEvents;
   }

   public boolean isWhitelistEnabled() {
      return this.whitelistEnabled;
   }

   public void setWhitelistEnabled(boolean p_205741_1_) {
      this.whitelistEnabled = p_205741_1_;
   }

   public float getTickTime() {
      return this.tickTime;
   }

   public int getPermissionLevel(GameProfile p_211833_1_) {
      if (this.getPlayerList().canSendCommands(p_211833_1_)) {
         OpEntry opentry = (OpEntry)this.getPlayerList().getOppedPlayers().getEntry(p_211833_1_);
         if (opentry != null) {
            return opentry.getPermissionLevel();
         } else if (this.func_213199_b(p_211833_1_)) {
            return 4;
         } else if (this.isSinglePlayer()) {
            return this.getPlayerList().commandsAllowedForAll() ? 4 : 0;
         } else {
            return this.getOpPermissionLevel();
         }
      } else {
         return 0;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public FrameTimer getFrameTimer() {
      return this.frameTimer;
   }

   public DebugProfiler getProfiler() {
      return this.profiler;
   }

   public Executor getBackgroundExecutor() {
      return this.backgroundExecutor;
   }

   public abstract boolean func_213199_b(GameProfile var1);

   @Nullable
   public long[] getTickTime(DimensionType p_getTickTime_1_) {
      return (long[])this.perWorldTickTimes.get(p_getTickTime_1_);
   }

   /** @deprecated */
   @Deprecated
   public synchronized Map<DimensionType, ServerWorld> forgeGetWorldMap() {
      return this.worlds;
   }

   /** @deprecated */
   @Deprecated
   public synchronized void markWorldsDirty() {
      ++this.worldArrayMarker;
   }

   private ServerWorld[] getWorldArray() {
      if (this.worldArrayMarker == this.worldArrayLast && this.worldArray != null) {
         return this.worldArray;
      } else {
         this.worldArray = (ServerWorld[])this.worlds.values().stream().toArray((p_lambda$getWorldArray$12_0_) -> {
            return new ServerWorld[p_lambda$getWorldArray$12_0_];
         });
         this.worldArrayLast = this.worldArrayMarker;
         return this.worldArray;
      }
   }

   public void func_223711_a(Path p_223711_1_) throws IOException {
      Path path = p_223711_1_.resolve("levels");
      Iterator var3 = this.worlds.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<DimensionType, ServerWorld> entry = (Entry)var3.next();
         ResourceLocation resourcelocation = DimensionType.getKey((DimensionType)entry.getKey());
         Path path1 = path.resolve(resourcelocation.getNamespace()).resolve(resourcelocation.getPath());
         Files.createDirectories(path1);
         ((ServerWorld)entry.getValue()).func_225322_a(path1);
      }

      this.func_223708_d(p_223711_1_.resolve("gamerules.txt"));
      this.func_223706_e(p_223711_1_.resolve("classpath.txt"));
      this.func_223709_c(p_223711_1_.resolve("example_crash.txt"));
      this.func_223710_b(p_223711_1_.resolve("stats.txt"));
      this.func_223712_f(p_223711_1_.resolve("threads.txt"));
   }

   private void func_223710_b(Path p_223710_1_) throws IOException {
      Writer writer = Files.newBufferedWriter(p_223710_1_);
      Throwable var3 = null;

      try {
         writer.write(String.format("pending_tasks: %d\n", this.func_223704_be()));
         writer.write(String.format("average_tick_time: %f\n", this.getTickTime()));
         writer.write(String.format("tick_times: %s\n", Arrays.toString(this.tickTimeArray)));
         writer.write(String.format("queue: %s\n", Util.getServerExecutor()));
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (writer != null) {
            if (var3 != null) {
               try {
                  writer.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               writer.close();
            }
         }

      }

   }

   private void func_223709_c(Path p_223709_1_) throws IOException {
      CrashReport crashreport = new CrashReport("Server dump", new Exception("dummy"));
      this.addServerInfoToCrashReport(crashreport);
      Writer writer = Files.newBufferedWriter(p_223709_1_);
      Throwable var4 = null;

      try {
         writer.write(crashreport.getCompleteReport());
      } catch (Throwable var13) {
         var4 = var13;
         throw var13;
      } finally {
         if (writer != null) {
            if (var4 != null) {
               try {
                  writer.close();
               } catch (Throwable var12) {
                  var4.addSuppressed(var12);
               }
            } else {
               writer.close();
            }
         }

      }

   }

   private void func_223708_d(Path p_223708_1_) throws IOException {
      Writer writer = Files.newBufferedWriter(p_223708_1_);
      Throwable var3 = null;

      try {
         final List<String> list = Lists.newArrayList();
         final GameRules gamerules = this.getGameRules();
         GameRules.func_223590_a(new GameRules.IRuleEntryVisitor() {
            public <T extends GameRules.RuleValue<T>> void func_223481_a(GameRules.RuleKey<T> p_223481_1_, GameRules.RuleType<T> p_223481_2_) {
               list.add(String.format("%s=%s\n", p_223481_1_.func_223576_a(), gamerules.get(p_223481_1_).toString()));
            }
         });
         Iterator var6 = list.iterator();

         while(var6.hasNext()) {
            String s = (String)var6.next();
            writer.write(s);
         }
      } catch (Throwable var15) {
         var3 = var15;
         throw var15;
      } finally {
         if (writer != null) {
            if (var3 != null) {
               try {
                  writer.close();
               } catch (Throwable var14) {
                  var3.addSuppressed(var14);
               }
            } else {
               writer.close();
            }
         }

      }

   }

   private void func_223706_e(Path p_223706_1_) throws IOException {
      Writer writer = Files.newBufferedWriter(p_223706_1_);
      Throwable var3 = null;

      try {
         String s = System.getProperty("java.class.path");
         String s1 = System.getProperty("path.separator");
         Iterator var6 = Splitter.on(s1).split(s).iterator();

         while(var6.hasNext()) {
            String s2 = (String)var6.next();
            writer.write(s2);
            writer.write("\n");
         }
      } catch (Throwable var15) {
         var3 = var15;
         throw var15;
      } finally {
         if (writer != null) {
            if (var3 != null) {
               try {
                  writer.close();
               } catch (Throwable var14) {
                  var3.addSuppressed(var14);
               }
            } else {
               writer.close();
            }
         }

      }

   }

   private void func_223712_f(Path p_223712_1_) throws IOException {
      ThreadMXBean threadmxbean = ManagementFactory.getThreadMXBean();
      ThreadInfo[] athreadinfo = threadmxbean.dumpAllThreads(true, true);
      Arrays.sort(athreadinfo, Comparator.comparing(ThreadInfo::getThreadName));
      Writer writer = Files.newBufferedWriter(p_223712_1_);
      Throwable var5 = null;

      try {
         ThreadInfo[] var6 = athreadinfo;
         int var7 = athreadinfo.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            ThreadInfo threadinfo = var6[var8];
            writer.write(threadinfo.toString());
            writer.write(10);
         }
      } catch (Throwable var17) {
         var5 = var17;
         throw var17;
      } finally {
         if (writer != null) {
            if (var5 != null) {
               try {
                  writer.close();
               } catch (Throwable var16) {
                  var5.addSuppressed(var16);
               }
            } else {
               writer.close();
            }
         }

      }

   }

   private void func_229737_ba_() {
      Block.BLOCK_STATE_IDS.forEach(BlockState::func_215692_c);
   }

   static {
      field_223713_i = CompletableFuture.completedFuture(Unit.INSTANCE);
      DEMO_WORLD_SETTINGS = (new WorldSettings((long)"North Carolina".hashCode(), GameType.SURVIVAL, true, false, WorldType.DEFAULT)).enableBonusChest();
   }
}
