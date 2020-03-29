package net.minecraft.client;

import com.google.common.collect.Queues;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.platform.PlatformDescriptors;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.LoadingGui;
import net.minecraft.client.gui.ResourceLoadProgressGui;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.fonts.FontResourceManager;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DirtMessageScreen;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MemoryErrorScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraft.client.gui.screen.WinGameScreen;
import net.minecraft.client.gui.screen.WorkingScreen;
import net.minecraft.client.gui.screen.WorldLoadProgressScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.login.ClientLoginNetHandler;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.IWindowEventListener;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderTypeBuffers;
import net.minecraft.client.renderer.ScreenSize;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VirtualScreen;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.PaintingSpriteUploader;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.client.resources.DownloadingPackFinder;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.LegacyResourcePackWrapper;
import net.minecraft.client.resources.LegacyResourcePackWrapperV4;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.client.settings.AmbientOcclusionStatus;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.client.settings.CreativeSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.util.IMutableSearchTree;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.SearchTree;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.client.util.SearchTreeReloadable;
import net.minecraft.client.util.Splashes;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.client.CLoginStartPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.profiler.DataPoint;
import net.minecraft.profiler.DebugProfiler;
import net.minecraft.profiler.IProfileResult;
import net.minecraft.profiler.IProfiler;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.profiler.Snooper;
import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.Direction;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Timer;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.RecursiveEventLoop;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.Bootstrap;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.KeybindTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.listener.ChainedChunkStatusListener;
import net.minecraft.world.chunk.listener.TrackingChunkStatusListener;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.dimension.NetherDimension;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.StartupQuery;
import net.minecraftforge.fml.client.ClientHooks;
import net.minecraftforge.fml.client.ClientModLoader;
import net.minecraftforge.fml.hooks.BasicEventHooks;
import net.minecraftforge.fml.loading.progress.EarlyProgressVisualization;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Minecraft extends RecursiveEventLoop<Runnable> implements ISnooperInfo, IWindowEventListener {
   private static Minecraft instance;
   private static final Logger LOGGER = LogManager.getLogger();
   public static final boolean IS_RUNNING_ON_MAC;
   public static final ResourceLocation DEFAULT_FONT_RENDERER_NAME;
   public static final ResourceLocation standardGalacticFontRenderer;
   private static final CompletableFuture<Unit> field_223714_G;
   private final File fileResourcepacks;
   private final PropertyMap profileProperties;
   public final TextureManager textureManager;
   private final DataFixer dataFixer;
   private final VirtualScreen virtualScreen;
   private final MainWindow mainWindow;
   private final Timer timer = new Timer(20.0F, 0L);
   private final Snooper snooper = new Snooper("client", this, Util.milliTime());
   private final RenderTypeBuffers field_228006_P_;
   public final WorldRenderer worldRenderer;
   private final EntityRendererManager renderManager;
   private final ItemRenderer itemRenderer;
   private final FirstPersonRenderer firstPersonRenderer;
   public final ParticleManager particles;
   private final SearchTreeManager searchTreeManager = new SearchTreeManager();
   private final Session session;
   public final FontRenderer fontRenderer;
   public final GameRenderer gameRenderer;
   public final DebugRenderer debugRenderer;
   private final AtomicReference<TrackingChunkStatusListener> field_213277_ad = new AtomicReference();
   public final IngameGui ingameGUI;
   public final GameSettings gameSettings;
   private final CreativeSettings creativeSettings;
   public final MouseHelper mouseHelper;
   public final KeyboardListener keyboardListener;
   public final File gameDir;
   private final String launchedVersion;
   private final String versionType;
   private final Proxy proxy;
   private final SaveFormat saveFormat;
   public final FrameTimer frameTimer = new FrameTimer();
   private final boolean jvm64bit;
   private final boolean isDemo;
   private final DebugProfiler profiler = new DebugProfiler(() -> {
      return this.timer.elapsedTicks;
   });
   private final IReloadableResourceManager resourceManager;
   private final DownloadingPackFinder packFinder;
   private final ResourcePackList<ClientResourcePackInfo> resourcePackRepository;
   private final LanguageManager languageManager;
   private final BlockColors blockColors;
   private final ItemColors itemColors;
   private final Framebuffer framebuffer;
   private final SoundHandler soundHandler;
   private final MusicTicker musicTicker;
   private final FontResourceManager fontResourceMananger;
   private final Splashes splashes;
   private final MinecraftSessionService sessionService;
   private final SkinManager skinManager;
   private final ModelManager modelManager;
   private final BlockRendererDispatcher blockRenderDispatcher;
   private final PaintingSpriteUploader paintingSprites;
   private final PotionSpriteUploader potionSprites;
   private final ToastGui toastGui;
   private final MinecraftGame game = new MinecraftGame(this);
   private final Tutorial tutorial;
   public static byte[] memoryReserve;
   @Nullable
   public PlayerController playerController;
   @Nullable
   public ClientWorld world;
   @Nullable
   public ClientPlayerEntity player;
   @Nullable
   private IntegratedServer integratedServer;
   @Nullable
   private ServerData currentServerData;
   @Nullable
   private NetworkManager networkManager;
   private boolean integratedServerIsRunning;
   @Nullable
   public Entity renderViewEntity;
   @Nullable
   public Entity pointedEntity;
   @Nullable
   public RayTraceResult objectMouseOver;
   private int rightClickDelayTimer;
   protected int leftClickCounter;
   private boolean isGamePaused;
   private float renderPartialTicksPaused;
   private long startNanoTime = Util.nanoTime();
   private long debugUpdateTime;
   private int fpsCounter;
   public boolean skipRenderWorld;
   @Nullable
   public Screen currentScreen;
   @Nullable
   public LoadingGui loadingGui;
   private boolean connectedToRealms;
   private Thread thread;
   private volatile boolean running = true;
   @Nullable
   private CrashReport crashReporter;
   private static int debugFPS;
   public String debug = "";
   public boolean field_228004_B_;
   public boolean field_228005_C_;
   public boolean renderChunksMany = true;
   private boolean isWindowFocused;
   private final Queue<Runnable> field_213275_aU = Queues.newConcurrentLinkedQueue();
   @Nullable
   private CompletableFuture<Void> field_213276_aV;
   private String debugProfilerName = "root";

   public Minecraft(GameConfiguration p_i45547_1_) {
      super("Client");
      instance = this;
      ForgeHooksClient.invalidateLog4jThreadCache();
      this.gameDir = p_i45547_1_.folderInfo.gameDir;
      File file1 = p_i45547_1_.folderInfo.assetsDir;
      this.fileResourcepacks = p_i45547_1_.folderInfo.resourcePacksDir;
      this.launchedVersion = p_i45547_1_.gameInfo.version;
      this.versionType = p_i45547_1_.gameInfo.versionType;
      this.profileProperties = p_i45547_1_.userInfo.profileProperties;
      this.packFinder = new DownloadingPackFinder(new File(this.gameDir, "server-resource-packs"), p_i45547_1_.folderInfo.getAssetsIndex());
      this.resourcePackRepository = new ResourcePackList(Minecraft::func_228011_a_);
      this.resourcePackRepository.addPackFinder(this.packFinder);
      this.resourcePackRepository.addPackFinder(new FolderPackFinder(this.fileResourcepacks));
      this.proxy = p_i45547_1_.userInfo.proxy;
      this.sessionService = (new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString())).createMinecraftSessionService();
      this.session = p_i45547_1_.userInfo.session;
      LOGGER.info("Setting user: {}", this.session.getUsername());
      this.isDemo = p_i45547_1_.gameInfo.isDemo;
      this.jvm64bit = isJvm64bit();
      this.integratedServer = null;
      String s;
      int i;
      if (p_i45547_1_.serverInfo.serverName != null) {
         s = p_i45547_1_.serverInfo.serverName;
         i = p_i45547_1_.serverInfo.serverPort;
      } else {
         s = null;
         i = 0;
      }

      Bootstrap.register();
      Bootstrap.func_218821_c();
      KeybindTextComponent.displaySupplierFunction = KeyBinding::getDisplayString;
      this.dataFixer = DataFixesManager.getDataFixer();
      this.toastGui = new ToastGui(this);
      this.tutorial = new Tutorial(this);
      this.thread = Thread.currentThread();
      this.gameSettings = new GameSettings(this, this.gameDir);
      this.creativeSettings = new CreativeSettings(this.gameDir, this.dataFixer);
      this.startTimerHackThread();
      LOGGER.info("Backend library: {}", RenderSystem.getBackendDescription());
      ScreenSize screensize;
      if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0) {
         screensize = new ScreenSize(this.gameSettings.overrideWidth, this.gameSettings.overrideHeight, p_i45547_1_.displayInfo.fullscreenWidth, p_i45547_1_.displayInfo.fullscreenHeight, p_i45547_1_.displayInfo.fullscreen);
      } else {
         screensize = p_i45547_1_.displayInfo;
      }

      EarlyProgressVisualization.INSTANCE.join();
      Util.nanoTimeSupplier = RenderSystem.initBackendSystem();
      this.virtualScreen = new VirtualScreen(this);
      this.mainWindow = this.virtualScreen.create(screensize, this.gameSettings.fullscreenResolution, this.func_230149_ax_());
      this.setGameFocused(true);

      try {
         InputStream inputstream = this.getPackFinder().getVanillaPack().getResourceStream(ResourcePackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_16x16.png"));
         InputStream inputstream1 = this.getPackFinder().getVanillaPack().getResourceStream(ResourcePackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_32x32.png"));
         this.mainWindow.setWindowIcon(inputstream, inputstream1);
      } catch (IOException var10) {
         LOGGER.error("Couldn't set icon", var10);
      }

      this.mainWindow.setFramerateLimit(this.gameSettings.framerateLimit);
      this.mouseHelper = new MouseHelper(this);
      this.keyboardListener = new KeyboardListener(this);
      this.keyboardListener.setupCallbacks(this.mainWindow.getHandle());
      RenderSystem.initRenderer(this.gameSettings.glDebugVerbosity, false);
      this.framebuffer = new Framebuffer(this.mainWindow.getFramebufferWidth(), this.mainWindow.getFramebufferHeight(), true, IS_RUNNING_ON_MAC);
      this.framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.resourceManager = new SimpleReloadableResourceManager(ResourcePackType.CLIENT_RESOURCES, this.thread);
      ClientModLoader.begin(this, this.resourcePackRepository, this.resourceManager, this.packFinder);
      this.gameSettings.fillResourcePackList(this.resourcePackRepository);
      this.resourcePackRepository.reloadPacksFromFinders();
      this.languageManager = new LanguageManager(this.gameSettings.language);
      this.resourceManager.addReloadListener(this.languageManager);
      this.textureManager = new TextureManager(this.resourceManager);
      this.resourceManager.addReloadListener(this.textureManager);
      this.skinManager = new SkinManager(this.textureManager, new File(file1, "skins"), this.sessionService);
      this.saveFormat = new SaveFormat(this.gameDir.toPath().resolve("saves"), this.gameDir.toPath().resolve("backups"), this.dataFixer);
      this.soundHandler = new SoundHandler(this.resourceManager, this.gameSettings);
      this.resourceManager.addReloadListener(this.soundHandler);
      this.splashes = new Splashes(this.session);
      this.resourceManager.addReloadListener(this.splashes);
      this.musicTicker = new MusicTicker(this);
      this.fontResourceMananger = new FontResourceManager(this.textureManager, this.getForceUnicodeFont());
      this.resourceManager.addReloadListener(this.fontResourceMananger.func_216884_a());
      FontRenderer fontrenderer = this.fontResourceMananger.getFontRenderer(DEFAULT_FONT_RENDERER_NAME);
      if (fontrenderer == null) {
         throw new IllegalStateException("Default font is null");
      } else {
         this.fontRenderer = fontrenderer;
         this.fontRenderer.setBidiFlag(this.languageManager.isCurrentLanguageBidirectional());
         this.resourceManager.addReloadListener(new GrassColorReloadListener());
         this.resourceManager.addReloadListener(new FoliageColorReloadListener());
         this.mainWindow.func_227799_a_("Startup");
         RenderSystem.setupDefaultState(0, 0, this.mainWindow.getFramebufferWidth(), this.mainWindow.getFramebufferHeight());
         this.mainWindow.func_227799_a_("Post startup");
         this.blockColors = BlockColors.init();
         this.itemColors = ItemColors.init(this.blockColors);
         this.modelManager = new ModelManager(this.textureManager, this.blockColors, this.gameSettings.mipmapLevels);
         this.resourceManager.addReloadListener(this.modelManager);
         this.itemRenderer = new ItemRenderer(this.textureManager, this.modelManager, this.itemColors);
         this.renderManager = new EntityRendererManager(this.textureManager, this.itemRenderer, this.resourceManager, this.fontRenderer, this.gameSettings);
         this.firstPersonRenderer = new FirstPersonRenderer(this);
         this.resourceManager.addReloadListener(this.itemRenderer);
         this.field_228006_P_ = new RenderTypeBuffers();
         this.gameRenderer = new GameRenderer(this, this.resourceManager, this.field_228006_P_);
         this.resourceManager.addReloadListener(this.gameRenderer);
         this.blockRenderDispatcher = new BlockRendererDispatcher(this.modelManager.getBlockModelShapes(), this.blockColors);
         this.resourceManager.addReloadListener(this.blockRenderDispatcher);
         this.worldRenderer = new WorldRenderer(this, this.field_228006_P_);
         this.resourceManager.addReloadListener(this.worldRenderer);
         this.populateSearchTreeManager();
         this.resourceManager.addReloadListener(this.searchTreeManager);
         this.particles = new ParticleManager(this.world, this.textureManager);
         ModLoader.get().postEvent(new ParticleFactoryRegisterEvent());
         this.resourceManager.addReloadListener(this.particles);
         this.paintingSprites = new PaintingSpriteUploader(this.textureManager);
         this.resourceManager.addReloadListener(this.paintingSprites);
         this.potionSprites = new PotionSpriteUploader(this.textureManager);
         this.resourceManager.addReloadListener(this.potionSprites);
         this.ingameGUI = new ForgeIngameGui(this);
         this.mouseHelper.registerCallbacks(this.mainWindow.getHandle());
         this.debugRenderer = new DebugRenderer(this);
         RenderSystem.setErrorCallback(this::disableVSyncAfterGlError);
         if (this.gameSettings.fullscreen && !this.mainWindow.isFullscreen()) {
            this.mainWindow.toggleFullscreen();
            this.gameSettings.fullscreen = this.mainWindow.isFullscreen();
         }

         this.mainWindow.setVsync(this.gameSettings.vsync);
         this.mainWindow.func_224798_d(this.gameSettings.field_225307_E);
         this.mainWindow.func_227801_c_();
         this.updateWindowSize();
         ResourceLoadProgressGui.loadLogoTexture(this);
         List<IResourcePack> list = (List)this.resourcePackRepository.getEnabledPacks().stream().map(ResourcePackInfo::getResourcePack).collect(Collectors.toList());
         this.setLoadingGui(new ResourceLoadProgressGui(this, this.resourceManager.reloadResources(Util.getServerExecutor(), this, field_223714_G, list), (p_lambda$new$2_4_) -> {
            Util.acceptOrElse(p_lambda$new$2_4_, this::func_229988_a_, () -> {
               this.languageManager.parseLanguageMetadata(list);
               if (SharedConstants.developmentMode) {
                  this.checkMissingData();
               }

               if (!ClientModLoader.completeModLoading()) {
                  if (s != null) {
                     this.displayGuiScreen(new ConnectingScreen(new MainMenuScreen(), this, s, i));
                  } else {
                     this.displayGuiScreen(new MainMenuScreen(true));
                  }

               }
            });
         }, false));
      }
   }

   public void func_230150_b_() {
      this.mainWindow.func_230148_b_(this.func_230149_ax_());
   }

   private String func_230149_ax_() {
      StringBuilder stringbuilder = new StringBuilder("Minecraft");
      if (this.func_230151_c_()) {
         stringbuilder.append("*");
      }

      stringbuilder.append(" ");
      stringbuilder.append(SharedConstants.getVersion().getName());
      ClientPlayNetHandler clientplaynethandler = this.getConnection();
      if (clientplaynethandler != null && clientplaynethandler.getNetworkManager().isChannelOpen()) {
         stringbuilder.append(" - ");
         if (this.integratedServer != null && !this.integratedServer.getPublic()) {
            stringbuilder.append(I18n.format("title.singleplayer"));
         } else if (this.isConnectedToRealms()) {
            stringbuilder.append(I18n.format("title.multiplayer.realms"));
         } else if (this.integratedServer != null || this.currentServerData != null && this.currentServerData.isOnLAN()) {
            stringbuilder.append(I18n.format("title.multiplayer.lan"));
         } else {
            stringbuilder.append(I18n.format("title.multiplayer.other"));
         }
      }

      return stringbuilder.toString();
   }

   public boolean func_230151_c_() {
      return !"vanilla".equals(ClientBrandRetriever.getClientModName()) || Minecraft.class.getSigners() == null;
   }

   private void func_229988_a_(Throwable p_229988_1_) {
      if (this.resourcePackRepository.getEnabledPacks().size() > 1) {
         StringTextComponent itextcomponent;
         if (p_229988_1_ instanceof SimpleReloadableResourceManager.FailedPackException) {
            itextcomponent = new StringTextComponent(((SimpleReloadableResourceManager.FailedPackException)p_229988_1_).func_230028_a().getName());
         } else {
            itextcomponent = null;
         }

         LOGGER.info("Caught error loading resourcepacks, removing all selected resourcepacks", p_229988_1_);
         this.resourcePackRepository.setEnabledPacks(Collections.emptyList());
         this.gameSettings.resourcePacks.clear();
         this.gameSettings.incompatibleResourcePacks.clear();
         this.gameSettings.saveOptions();
         this.reloadResources().thenRun(() -> {
            ToastGui toastgui = this.getToastGui();
            SystemToast.addOrUpdate(toastgui, SystemToast.Type.PACK_LOAD_FAILURE, new TranslationTextComponent("resourcePack.load_fail", new Object[0]), itextcomponent);
         });
      } else {
         Util.func_229756_b_(p_229988_1_);
      }

   }

   public void run() {
      this.thread = Thread.currentThread();

      try {
         boolean flag = false;

         while(this.running) {
            if (this.crashReporter != null) {
               displayCrashReport(this.crashReporter);
               return;
            }

            try {
               this.runGameLoop(!flag);
            } catch (OutOfMemoryError var3) {
               if (flag) {
                  throw var3;
               }

               this.freeMemory();
               this.displayGuiScreen(new MemoryErrorScreen());
               System.gc();
               LOGGER.fatal("Out of memory", var3);
               flag = true;
            }
         }
      } catch (ReportedException var4) {
         this.addGraphicsAndWorldToCrashReport(var4.getCrashReport());
         this.freeMemory();
         LOGGER.fatal("Reported exception thrown!", var4);
         displayCrashReport(var4.getCrashReport());
      } catch (Throwable var5) {
         CrashReport crashreport = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", var5));
         LOGGER.fatal("Unreported exception thrown!", var5);
         this.freeMemory();
         displayCrashReport(crashreport);
      }

   }

   public void populateSearchTreeManager() {
      SearchTree<ItemStack> searchtree = new SearchTree((p_lambda$populateSearchTreeManager$6_0_) -> {
         return p_lambda$populateSearchTreeManager$6_0_.getTooltip((PlayerEntity)null, ITooltipFlag.TooltipFlags.NORMAL).stream().map((p_lambda$null$4_0_) -> {
            return TextFormatting.getTextWithoutFormattingCodes(p_lambda$null$4_0_.getString()).trim();
         }).filter((p_lambda$null$5_0_) -> {
            return !p_lambda$null$5_0_.isEmpty();
         });
      }, (p_lambda$populateSearchTreeManager$7_0_) -> {
         return Stream.of(Registry.ITEM.getKey(p_lambda$populateSearchTreeManager$7_0_.getItem()));
      });
      SearchTreeReloadable<ItemStack> searchtreereloadable = new SearchTreeReloadable((p_lambda$populateSearchTreeManager$8_0_) -> {
         return p_lambda$populateSearchTreeManager$8_0_.getItem().getTags().stream();
      });
      NonNullList<ItemStack> nonnulllist = NonNullList.create();
      Iterator var4 = Registry.ITEM.iterator();

      while(var4.hasNext()) {
         Item item = (Item)var4.next();
         item.fillItemGroup(ItemGroup.SEARCH, nonnulllist);
      }

      nonnulllist.forEach((p_lambda$populateSearchTreeManager$9_2_) -> {
         searchtree.func_217872_a(p_lambda$populateSearchTreeManager$9_2_);
         searchtreereloadable.func_217872_a(p_lambda$populateSearchTreeManager$9_2_);
      });
      SearchTree<RecipeList> searchtree1 = new SearchTree((p_lambda$populateSearchTreeManager$13_0_) -> {
         return p_lambda$populateSearchTreeManager$13_0_.getRecipes().stream().flatMap((p_lambda$null$10_0_) -> {
            return p_lambda$null$10_0_.getRecipeOutput().getTooltip((PlayerEntity)null, ITooltipFlag.TooltipFlags.NORMAL).stream();
         }).map((p_lambda$null$11_0_) -> {
            return TextFormatting.getTextWithoutFormattingCodes(p_lambda$null$11_0_.getString()).trim();
         }).filter((p_lambda$null$12_0_) -> {
            return !p_lambda$null$12_0_.isEmpty();
         });
      }, (p_lambda$populateSearchTreeManager$15_0_) -> {
         return p_lambda$populateSearchTreeManager$15_0_.getRecipes().stream().map((p_lambda$null$14_0_) -> {
            return Registry.ITEM.getKey(p_lambda$null$14_0_.getRecipeOutput().getItem());
         });
      });
      this.searchTreeManager.add(SearchTreeManager.field_215359_a, searchtree);
      this.searchTreeManager.add(SearchTreeManager.field_215360_b, searchtreereloadable);
      this.searchTreeManager.add(SearchTreeManager.RECIPES, searchtree1);
   }

   private void disableVSyncAfterGlError(int p_195545_1_, long p_195545_2_) {
      this.gameSettings.vsync = false;
      this.gameSettings.saveOptions();
   }

   private static boolean isJvm64bit() {
      String[] astring = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
      String[] var1 = astring;
      int var2 = astring.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String s = var1[var3];
         String s1 = System.getProperty(s);
         if (s1 != null && s1.contains("64")) {
            return true;
         }
      }

      return false;
   }

   public Framebuffer getFramebuffer() {
      return this.framebuffer;
   }

   public String getVersion() {
      return this.launchedVersion;
   }

   public String getVersionType() {
      return this.versionType;
   }

   private void startTimerHackThread() {
      Thread thread = new Thread("Timer hack thread") {
         public void run() {
            while(Minecraft.this.running) {
               try {
                  Thread.sleep(2147483647L);
               } catch (InterruptedException var2) {
               }
            }

         }
      };
      thread.setDaemon(true);
      thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      thread.start();
   }

   public void crashed(CrashReport p_71404_1_) {
      this.crashReporter = p_71404_1_;
   }

   public static void displayCrashReport(CrashReport p_71377_0_) {
      File file1 = new File(getInstance().gameDir, "crash-reports");
      File file2 = new File(file1, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-client.txt");
      Bootstrap.printToSYSOUT(p_71377_0_.getCompleteReport());
      if (p_71377_0_.getFile() != null) {
         Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + p_71377_0_.getFile());
         ServerLifecycleHooks.handleExit(-1);
      } else if (p_71377_0_.saveToFile(file2)) {
         Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
         ServerLifecycleHooks.handleExit(-1);
      } else {
         Bootstrap.printToSYSOUT("#@?@# Game crashed! Crash report could not be saved. #@?@#");
         ServerLifecycleHooks.handleExit(-2);
      }

   }

   public boolean getForceUnicodeFont() {
      return this.gameSettings.forceUnicodeFont;
   }

   /** @deprecated */
   @Deprecated
   public CompletableFuture<Void> reloadResources() {
      if (this.field_213276_aV != null) {
         return this.field_213276_aV;
      } else {
         CompletableFuture<Void> completablefuture = new CompletableFuture();
         if (this.loadingGui instanceof ResourceLoadProgressGui) {
            this.field_213276_aV = completablefuture;
            return completablefuture;
         } else {
            this.resourcePackRepository.reloadPacksFromFinders();
            List<IResourcePack> list = (List)this.resourcePackRepository.getEnabledPacks().stream().map(ResourcePackInfo::getResourcePack).collect(Collectors.toList());
            this.setLoadingGui(new ResourceLoadProgressGui(this, this.resourceManager.reloadResources(Util.getServerExecutor(), this, field_223714_G, list), (p_lambda$reloadResources$17_3_) -> {
               Util.acceptOrElse(p_lambda$reloadResources$17_3_, this::func_229988_a_, () -> {
                  this.languageManager.parseLanguageMetadata(list);
                  this.worldRenderer.loadRenderers();
                  completablefuture.complete((Void)null);
               });
            }, true));
            return completablefuture;
         }
      }
   }

   private void checkMissingData() {
      boolean flag = false;
      BlockModelShapes blockmodelshapes = this.getBlockRendererDispatcher().getBlockModelShapes();
      IBakedModel ibakedmodel = blockmodelshapes.getModelManager().getMissingModel();
      Iterator var4 = Registry.BLOCK.iterator();

      while(var4.hasNext()) {
         Block block = (Block)var4.next();
         UnmodifiableIterator var6 = block.getStateContainer().getValidStates().iterator();

         while(var6.hasNext()) {
            BlockState blockstate = (BlockState)var6.next();
            if (blockstate.getRenderType() == BlockRenderType.MODEL) {
               IBakedModel ibakedmodel1 = blockmodelshapes.getModel(blockstate);
               if (ibakedmodel1 == ibakedmodel) {
                  LOGGER.debug("Missing model for: {}", blockstate);
                  flag = true;
               }
            }
         }
      }

      TextureAtlasSprite textureatlassprite1 = ibakedmodel.getParticleTexture();
      Iterator var13 = Registry.BLOCK.iterator();

      while(var13.hasNext()) {
         Block block1 = (Block)var13.next();
         UnmodifiableIterator var17 = block1.getStateContainer().getValidStates().iterator();

         while(var17.hasNext()) {
            BlockState blockstate1 = (BlockState)var17.next();
            TextureAtlasSprite textureatlassprite = blockmodelshapes.getTexture(blockstate1);
            if (!blockstate1.isAir() && textureatlassprite == textureatlassprite1) {
               LOGGER.debug("Missing particle icon for: {}", blockstate1);
               flag = true;
            }
         }
      }

      NonNullList<ItemStack> nonnulllist = NonNullList.create();
      Iterator var16 = Registry.ITEM.iterator();

      while(var16.hasNext()) {
         Item item = (Item)var16.next();
         nonnulllist.clear();
         item.fillItemGroup(ItemGroup.SEARCH, nonnulllist);
         Iterator var20 = nonnulllist.iterator();

         while(var20.hasNext()) {
            ItemStack itemstack = (ItemStack)var20.next();
            String s = itemstack.getTranslationKey();
            String s1 = (new TranslationTextComponent(s, new Object[0])).getString();
            if (s1.toLowerCase(Locale.ROOT).equals(item.getTranslationKey())) {
               LOGGER.debug("Missing translation for: {} {} {}", itemstack, s, itemstack.getItem());
            }
         }
      }

      flag |= ScreenManager.isMissingScreen();
      if (flag) {
         throw new IllegalStateException("Your game data is foobar, fix the errors above!");
      }
   }

   public SaveFormat getSaveLoader() {
      return this.saveFormat;
   }

   public void displayGuiScreen(@Nullable Screen p_147108_1_) {
      if (p_147108_1_ == null && this.world == null) {
         p_147108_1_ = new MainMenuScreen();
      } else if (p_147108_1_ == null && this.player.getHealth() <= 0.0F) {
         if (this.player.func_228353_F_()) {
            p_147108_1_ = new DeathScreen((ITextComponent)null, this.world.getWorldInfo().isHardcore());
         } else {
            this.player.respawnPlayer();
         }
      }

      Screen old = this.currentScreen;
      GuiOpenEvent event = new GuiOpenEvent((Screen)p_147108_1_);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         Screen p_147108_1_ = event.getGui();
         if (old != null && p_147108_1_ != old) {
            old.removed();
         }

         if (p_147108_1_ instanceof MainMenuScreen || p_147108_1_ instanceof MultiplayerScreen) {
            this.gameSettings.showDebugInfo = false;
            this.ingameGUI.getChatGUI().clearChatMessages(true);
         }

         this.currentScreen = p_147108_1_;
         if (p_147108_1_ != null) {
            this.mouseHelper.ungrabMouse();
            KeyBinding.unPressAllKeys();
            p_147108_1_.init(this, this.mainWindow.getScaledWidth(), this.mainWindow.getScaledHeight());
            this.skipRenderWorld = false;
            NarratorChatListener.INSTANCE.func_216864_a(p_147108_1_.getNarrationMessage());
         } else {
            this.soundHandler.resume();
            this.mouseHelper.grabMouse();
         }

         this.func_230150_b_();
      }
   }

   public void setLoadingGui(@Nullable LoadingGui p_213268_1_) {
      this.loadingGui = p_213268_1_;
   }

   public void shutdownMinecraftApplet() {
      try {
         LOGGER.info("Stopping!");

         try {
            NarratorChatListener.INSTANCE.func_216867_c();
         } catch (Throwable var7) {
         }

         try {
            if (this.world != null) {
               this.world.sendQuittingDisconnectingPacket();
            }

            this.func_213254_o();
         } catch (Throwable var6) {
         }

         if (this.currentScreen != null) {
            this.currentScreen.removed();
         }

         this.close();
      } finally {
         Util.nanoTimeSupplier = System::nanoTime;
         if (this.crashReporter == null) {
            System.exit(0);
         }

      }

   }

   public void close() {
      try {
         this.modelManager.close();
         this.fontResourceMananger.close();
         this.gameRenderer.close();
         this.worldRenderer.close();
         this.soundHandler.unloadSounds();
         this.resourcePackRepository.close();
         this.particles.func_215232_a();
         this.potionSprites.close();
         this.paintingSprites.close();
         this.textureManager.close();
         Util.shutdownServerExecutor();
      } catch (Throwable var5) {
         LOGGER.error("Shutdown failure!", var5);
         throw var5;
      } finally {
         this.virtualScreen.close();
         this.mainWindow.close();
      }

   }

   private void runGameLoop(boolean p_195542_1_) {
      this.mainWindow.func_227799_a_("Pre render");
      long i = Util.nanoTime();
      this.profiler.startTick();
      if (this.mainWindow.func_227800_b_()) {
         this.shutdown();
      }

      if (this.field_213276_aV != null && !(this.loadingGui instanceof ResourceLoadProgressGui)) {
         CompletableFuture<Void> completablefuture = this.field_213276_aV;
         this.field_213276_aV = null;
         this.reloadResources().thenRun(() -> {
            completablefuture.complete((Void)null);
         });
      }

      Runnable runnable;
      while((runnable = (Runnable)this.field_213275_aU.poll()) != null) {
         runnable.run();
      }

      if (p_195542_1_) {
         this.timer.updateTimer(Util.milliTime());
         this.profiler.startSection("scheduledExecutables");
         this.drainTasks();
         this.profiler.endSection();
      }

      this.profiler.startSection("tick");
      int l;
      if (p_195542_1_) {
         for(l = 0; l < Math.min(10, this.timer.elapsedTicks); ++l) {
            this.runTick();
         }
      }

      this.mouseHelper.updatePlayerLook();
      this.mainWindow.func_227799_a_("Render");
      this.profiler.endStartSection("sound");
      this.soundHandler.updateListener(this.gameRenderer.getActiveRenderInfo());
      this.profiler.endSection();
      this.profiler.startSection("render");
      RenderSystem.pushMatrix();
      RenderSystem.clear(16640, IS_RUNNING_ON_MAC);
      this.framebuffer.bindFramebuffer(true);
      FogRenderer.func_228370_a_();
      this.profiler.startSection("display");
      RenderSystem.enableTexture();
      this.profiler.endSection();
      if (!this.skipRenderWorld) {
         BasicEventHooks.onRenderTickStart(this.timer.renderPartialTicks);
         this.profiler.endStartSection("gameRenderer");
         this.gameRenderer.updateCameraAndRender(this.isGamePaused ? this.renderPartialTicksPaused : this.timer.renderPartialTicks, i, p_195542_1_);
         this.profiler.endStartSection("toasts");
         this.toastGui.render();
         this.profiler.endSection();
         BasicEventHooks.onRenderTickEnd(this.timer.renderPartialTicks);
      }

      this.profiler.endTick();
      if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart && !this.gameSettings.hideGUI) {
         this.profiler.func_219899_d().func_219939_d();
         this.drawProfiler();
      } else {
         this.profiler.func_219899_d().func_219938_b();
      }

      this.framebuffer.unbindFramebuffer();
      RenderSystem.popMatrix();
      RenderSystem.pushMatrix();
      this.framebuffer.framebufferRender(this.mainWindow.getFramebufferWidth(), this.mainWindow.getFramebufferHeight());
      RenderSystem.popMatrix();
      this.profiler.startTick();
      this.profiler.startSection("updateDisplay");
      this.mainWindow.func_227802_e_();
      l = this.getFramerateLimit();
      if ((double)l < AbstractOption.FRAMERATE_LIMIT.getMaxValue()) {
         RenderSystem.limitDisplayFPS(l);
      }

      this.profiler.endStartSection("yield");
      Thread.yield();
      this.profiler.endSection();
      this.mainWindow.func_227799_a_("Post render");
      ++this.fpsCounter;
      boolean flag = this.isSingleplayer() && (this.currentScreen != null && this.currentScreen.isPauseScreen() || this.loadingGui != null && this.loadingGui.isPauseScreen()) && !this.integratedServer.getPublic();
      if (this.isGamePaused != flag) {
         if (this.isGamePaused) {
            this.renderPartialTicksPaused = this.timer.renderPartialTicks;
         } else {
            this.timer.renderPartialTicks = this.renderPartialTicksPaused;
         }

         this.isGamePaused = flag;
      }

      long k = Util.nanoTime();
      this.frameTimer.addFrame(k - this.startNanoTime);
      this.startNanoTime = k;

      while(Util.milliTime() >= this.debugUpdateTime + 1000L) {
         debugFPS = this.fpsCounter;
         this.debug = String.format("%d fps T: %s%s%s%s B: %d", debugFPS, (double)this.gameSettings.framerateLimit == AbstractOption.FRAMERATE_LIMIT.getMaxValue() ? "inf" : this.gameSettings.framerateLimit, this.gameSettings.vsync ? " vsync" : "", this.gameSettings.fancyGraphics ? "" : " fast", this.gameSettings.cloudOption == CloudOption.OFF ? "" : (this.gameSettings.cloudOption == CloudOption.FAST ? " fast-clouds" : " fancy-clouds"), this.gameSettings.biomeBlendRadius);
         this.debugUpdateTime += 1000L;
         this.fpsCounter = 0;
         this.snooper.addMemoryStatsToSnooper();
         if (!this.snooper.isSnooperRunning()) {
            this.snooper.start();
         }
      }

      this.profiler.endTick();
   }

   public void updateWindowSize() {
      int i = this.mainWindow.calcGuiScale(this.gameSettings.guiScale, this.getForceUnicodeFont());
      this.mainWindow.setGuiScale((double)i);
      if (this.currentScreen != null) {
         this.currentScreen.resize(this, this.mainWindow.getScaledWidth(), this.mainWindow.getScaledHeight());
      }

      Framebuffer framebuffer = this.getFramebuffer();
      framebuffer.func_216491_a(this.mainWindow.getFramebufferWidth(), this.mainWindow.getFramebufferHeight(), IS_RUNNING_ON_MAC);
      this.gameRenderer.updateShaderGroupSize(this.mainWindow.getFramebufferWidth(), this.mainWindow.getFramebufferHeight());
      this.mouseHelper.setIgnoreFirstMove();
   }

   private int getFramerateLimit() {
      return this.world == null && (this.currentScreen != null || this.loadingGui != null) ? 60 : this.mainWindow.getLimitFramerate();
   }

   public void freeMemory() {
      try {
         memoryReserve = new byte[0];
         this.worldRenderer.deleteAllDisplayLists();
      } catch (Throwable var3) {
      }

      try {
         System.gc();
         if (this.integratedServerIsRunning && this.integratedServer != null) {
            this.integratedServer.initiateShutdown(true);
         }

         this.func_213231_b(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel", new Object[0])));
      } catch (Throwable var2) {
      }

      System.gc();
   }

   void updateDebugProfilerName(int p_71383_1_) {
      IProfileResult iprofileresult = this.profiler.func_219899_d().func_219937_c();
      List<DataPoint> list = iprofileresult.getDataPoints(this.debugProfilerName);
      if (!list.isEmpty()) {
         DataPoint datapoint = (DataPoint)list.remove(0);
         if (p_71383_1_ == 0) {
            if (!datapoint.name.isEmpty()) {
               int i = this.debugProfilerName.lastIndexOf(30);
               if (i >= 0) {
                  this.debugProfilerName = this.debugProfilerName.substring(0, i);
               }
            }
         } else {
            --p_71383_1_;
            if (p_71383_1_ < list.size() && !"unspecified".equals(((DataPoint)list.get(p_71383_1_)).name)) {
               if (!this.debugProfilerName.isEmpty()) {
                  this.debugProfilerName = this.debugProfilerName + '\u001e';
               }

               this.debugProfilerName = this.debugProfilerName + ((DataPoint)list.get(p_71383_1_)).name;
            }
         }
      }

   }

   private void drawProfiler() {
      if (this.profiler.func_219899_d().isEnabled()) {
         IProfileResult iprofileresult = this.profiler.func_219899_d().func_219937_c();
         List<DataPoint> list = iprofileresult.getDataPoints(this.debugProfilerName);
         DataPoint datapoint = (DataPoint)list.remove(0);
         RenderSystem.clear(256, IS_RUNNING_ON_MAC);
         RenderSystem.matrixMode(5889);
         RenderSystem.loadIdentity();
         RenderSystem.ortho(0.0D, (double)this.mainWindow.getFramebufferWidth(), (double)this.mainWindow.getFramebufferHeight(), 0.0D, 1000.0D, 3000.0D);
         RenderSystem.matrixMode(5888);
         RenderSystem.loadIdentity();
         RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
         RenderSystem.lineWidth(1.0F);
         RenderSystem.disableTexture();
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         int i = true;
         int j = this.mainWindow.getFramebufferWidth() - 160 - 10;
         int k = this.mainWindow.getFramebufferHeight() - 320;
         RenderSystem.enableBlend();
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
         bufferbuilder.func_225582_a_((double)((float)j - 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0D).func_225586_a_(200, 0, 0, 0).endVertex();
         bufferbuilder.func_225582_a_((double)((float)j - 176.0F), (double)(k + 320), 0.0D).func_225586_a_(200, 0, 0, 0).endVertex();
         bufferbuilder.func_225582_a_((double)((float)j + 176.0F), (double)(k + 320), 0.0D).func_225586_a_(200, 0, 0, 0).endVertex();
         bufferbuilder.func_225582_a_((double)((float)j + 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0D).func_225586_a_(200, 0, 0, 0).endVertex();
         tessellator.draw();
         RenderSystem.disableBlend();
         double d0 = 0.0D;

         DataPoint datapoint1;
         int k2;
         int j2;
         for(Iterator var11 = list.iterator(); var11.hasNext(); d0 += datapoint1.relTime) {
            datapoint1 = (DataPoint)var11.next();
            int l = MathHelper.floor(datapoint1.relTime / 4.0D) + 1;
            bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
            k2 = datapoint1.getTextColor();
            j2 = k2 >> 16 & 255;
            int k1 = k2 >> 8 & 255;
            int l1 = k2 & 255;
            bufferbuilder.func_225582_a_((double)j, (double)k, 0.0D).func_225586_a_(j2, k1, l1, 255).endVertex();

            int l2;
            float f3;
            float f4;
            float f5;
            for(l2 = l; l2 >= 0; --l2) {
               f3 = (float)((d0 + datapoint1.relTime * (double)l2 / (double)l) * 6.2831854820251465D / 100.0D);
               f4 = MathHelper.sin(f3) * 160.0F;
               f5 = MathHelper.cos(f3) * 160.0F * 0.5F;
               bufferbuilder.func_225582_a_((double)((float)j + f4), (double)((float)k - f5), 0.0D).func_225586_a_(j2, k1, l1, 255).endVertex();
            }

            tessellator.draw();
            bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

            for(l2 = l; l2 >= 0; --l2) {
               f3 = (float)((d0 + datapoint1.relTime * (double)l2 / (double)l) * 6.2831854820251465D / 100.0D);
               f4 = MathHelper.sin(f3) * 160.0F;
               f5 = MathHelper.cos(f3) * 160.0F * 0.5F;
               if (f5 <= 0.0F) {
                  bufferbuilder.func_225582_a_((double)((float)j + f4), (double)((float)k - f5), 0.0D).func_225586_a_(j2 >> 1, k1 >> 1, l1 >> 1, 255).endVertex();
                  bufferbuilder.func_225582_a_((double)((float)j + f4), (double)((float)k - f5 + 10.0F), 0.0D).func_225586_a_(j2 >> 1, k1 >> 1, l1 >> 1, 255).endVertex();
               }
            }

            tessellator.draw();
         }

         DecimalFormat decimalformat = new DecimalFormat("##0.00");
         decimalformat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
         RenderSystem.enableTexture();
         String s = IProfileResult.func_225434_b(datapoint.name);
         String s1 = "";
         if (!"unspecified".equals(s)) {
            s1 = s1 + "[0] ";
         }

         if (s.isEmpty()) {
            s1 = s1 + "ROOT ";
         } else {
            s1 = s1 + s + ' ';
         }

         k2 = 16777215;
         this.fontRenderer.drawStringWithShadow(s1, (float)(j - 160), (float)(k - 80 - 16), 16777215);
         s1 = decimalformat.format(datapoint.rootRelTime) + "%";
         this.fontRenderer.drawStringWithShadow(s1, (float)(j + 160 - this.fontRenderer.getStringWidth(s1)), (float)(k - 80 - 16), 16777215);

         for(j2 = 0; j2 < list.size(); ++j2) {
            DataPoint datapoint2 = (DataPoint)list.get(j2);
            StringBuilder stringbuilder = new StringBuilder();
            if ("unspecified".equals(datapoint2.name)) {
               stringbuilder.append("[?] ");
            } else {
               stringbuilder.append("[").append(j2 + 1).append("] ");
            }

            String s2 = stringbuilder.append(datapoint2.name).toString();
            this.fontRenderer.drawStringWithShadow(s2, (float)(j - 160), (float)(k + 80 + j2 * 8 + 20), datapoint2.getTextColor());
            s2 = decimalformat.format(datapoint2.relTime) + "%";
            this.fontRenderer.drawStringWithShadow(s2, (float)(j + 160 - 50 - this.fontRenderer.getStringWidth(s2)), (float)(k + 80 + j2 * 8 + 20), datapoint2.getTextColor());
            s2 = decimalformat.format(datapoint2.rootRelTime) + "%";
            this.fontRenderer.drawStringWithShadow(s2, (float)(j + 160 - this.fontRenderer.getStringWidth(s2)), (float)(k + 80 + j2 * 8 + 20), datapoint2.getTextColor());
         }
      }

   }

   public void shutdown() {
      this.running = false;
   }

   public boolean func_228025_l_() {
      return this.running;
   }

   public void displayInGameMenu(boolean p_71385_1_) {
      if (this.currentScreen == null) {
         boolean flag = this.isSingleplayer() && !this.integratedServer.getPublic();
         if (flag) {
            this.displayGuiScreen(new IngameMenuScreen(!p_71385_1_));
            this.soundHandler.pause();
         } else {
            this.displayGuiScreen(new IngameMenuScreen(true));
         }
      }

   }

   private void sendClickBlockToController(boolean p_147115_1_) {
      if (!p_147115_1_) {
         this.leftClickCounter = 0;
      }

      if (this.leftClickCounter <= 0 && !this.player.isHandActive()) {
         if (p_147115_1_ && this.objectMouseOver != null && this.objectMouseOver.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)this.objectMouseOver;
            BlockPos blockpos = blockraytraceresult.getPos();
            if (!this.world.isAirBlock(blockpos)) {
               InputEvent.ClickInputEvent inputEvent = ForgeHooksClient.onClickInput(0, this.gameSettings.keyBindAttack, Hand.MAIN_HAND);
               if (inputEvent.isCanceled()) {
                  if (inputEvent.shouldSwingHand()) {
                     this.particles.addBlockHitEffects(blockpos, blockraytraceresult);
                     this.player.swingArm(Hand.MAIN_HAND);
                  }

                  return;
               }

               Direction direction = blockraytraceresult.getFace();
               if (this.playerController.onPlayerDamageBlock(blockpos, direction) && inputEvent.shouldSwingHand()) {
                  this.particles.addBlockHitEffects(blockpos, blockraytraceresult);
                  this.player.swingArm(Hand.MAIN_HAND);
               }
            }
         } else {
            this.playerController.resetBlockRemoving();
         }
      }

   }

   private void clickMouse() {
      if (this.leftClickCounter <= 0) {
         if (this.objectMouseOver == null) {
            LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.playerController.isNotCreative()) {
               this.leftClickCounter = 10;
            }
         } else if (!this.player.isRowingBoat()) {
            InputEvent.ClickInputEvent inputEvent = ForgeHooksClient.onClickInput(0, this.gameSettings.keyBindAttack, Hand.MAIN_HAND);
            if (!inputEvent.isCanceled()) {
               switch(this.objectMouseOver.getType()) {
               case ENTITY:
                  this.playerController.attackEntity(this.player, ((EntityRayTraceResult)this.objectMouseOver).getEntity());
                  break;
               case BLOCK:
                  BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)this.objectMouseOver;
                  BlockPos blockpos = blockraytraceresult.getPos();
                  if (!this.world.isAirBlock(blockpos)) {
                     this.playerController.clickBlock(blockpos, blockraytraceresult.getFace());
                     break;
                  }
               case MISS:
                  if (this.playerController.isNotCreative()) {
                     this.leftClickCounter = 10;
                  }

                  this.player.resetCooldown();
                  ForgeHooks.onEmptyLeftClick(this.player);
               }
            }

            if (inputEvent.shouldSwingHand()) {
               this.player.swingArm(Hand.MAIN_HAND);
            }
         }
      }

   }

   private void rightClickMouse() {
      if (!this.playerController.getIsHittingBlock()) {
         this.rightClickDelayTimer = 4;
         if (!this.player.isRowingBoat()) {
            if (this.objectMouseOver == null) {
               LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
            }

            Hand[] var1 = Hand.values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               Hand hand = var1[var3];
               InputEvent.ClickInputEvent inputEvent = ForgeHooksClient.onClickInput(1, this.gameSettings.keyBindUseItem, hand);
               if (inputEvent.isCanceled()) {
                  if (inputEvent.shouldSwingHand()) {
                     this.player.swingArm(hand);
                  }

                  return;
               }

               ItemStack itemstack = this.player.getHeldItem(hand);
               if (this.objectMouseOver != null) {
                  switch(this.objectMouseOver.getType()) {
                  case ENTITY:
                     EntityRayTraceResult entityraytraceresult = (EntityRayTraceResult)this.objectMouseOver;
                     Entity entity = entityraytraceresult.getEntity();
                     ActionResultType actionresulttype = this.playerController.interactWithEntity(this.player, entity, entityraytraceresult, hand);
                     if (!actionresulttype.func_226246_a_()) {
                        actionresulttype = this.playerController.interactWithEntity(this.player, entity, hand);
                     }

                     if (actionresulttype.func_226246_a_()) {
                        if (actionresulttype.func_226247_b_() && inputEvent.shouldSwingHand()) {
                           this.player.swingArm(hand);
                        }

                        return;
                     }
                     break;
                  case BLOCK:
                     BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)this.objectMouseOver;
                     int i = itemstack.getCount();
                     ActionResultType actionresulttype1 = this.playerController.func_217292_a(this.player, this.world, hand, blockraytraceresult);
                     if (actionresulttype1.func_226246_a_()) {
                        if (actionresulttype1.func_226247_b_()) {
                           if (inputEvent.shouldSwingHand()) {
                              this.player.swingArm(hand);
                           }

                           if (!itemstack.isEmpty() && (itemstack.getCount() != i || this.playerController.isInCreativeMode())) {
                              this.gameRenderer.itemRenderer.resetEquippedProgress(hand);
                           }
                        }

                        return;
                     }

                     if (actionresulttype1 == ActionResultType.FAIL) {
                        return;
                     }
                  }
               }

               if (itemstack.isEmpty() && (this.objectMouseOver == null || this.objectMouseOver.getType() == RayTraceResult.Type.MISS)) {
                  ForgeHooks.onEmptyClick(this.player, hand);
               }

               if (!itemstack.isEmpty()) {
                  ActionResultType actionresulttype2 = this.playerController.processRightClick(this.player, this.world, hand);
                  if (actionresulttype2.func_226246_a_()) {
                     if (actionresulttype2.func_226247_b_()) {
                        this.player.swingArm(hand);
                     }

                     this.gameRenderer.itemRenderer.resetEquippedProgress(hand);
                     return;
                  }
               }
            }
         }
      }

   }

   public MusicTicker getMusicTicker() {
      return this.musicTicker;
   }

   public void runTick() {
      if (this.rightClickDelayTimer > 0) {
         --this.rightClickDelayTimer;
      }

      BasicEventHooks.onPreClientTick();
      this.profiler.startSection("gui");
      if (!this.isGamePaused) {
         this.ingameGUI.tick();
      }

      this.profiler.endSection();
      this.gameRenderer.getMouseOver(1.0F);
      this.tutorial.onMouseHover(this.world, this.objectMouseOver);
      this.profiler.startSection("gameMode");
      if (!this.isGamePaused && this.world != null) {
         this.playerController.tick();
      }

      this.profiler.endStartSection("textures");
      if (this.world != null) {
         this.textureManager.tick();
      }

      if (this.currentScreen == null && this.player != null) {
         if (this.player.getHealth() <= 0.0F && !(this.currentScreen instanceof DeathScreen)) {
            this.displayGuiScreen((Screen)null);
         } else if (this.player.isSleeping() && this.world != null) {
            this.displayGuiScreen(new SleepInMultiplayerScreen());
         }
      } else if (this.currentScreen != null && this.currentScreen instanceof SleepInMultiplayerScreen && !this.player.isSleeping()) {
         this.displayGuiScreen((Screen)null);
      }

      if (this.currentScreen != null) {
         this.leftClickCounter = 10000;
      }

      if (this.currentScreen != null) {
         Screen.wrapScreenError(() -> {
            this.currentScreen.tick();
         }, "Ticking screen", this.currentScreen.getClass().getCanonicalName());
      }

      if (!this.gameSettings.showDebugInfo) {
         this.ingameGUI.func_212910_m();
      }

      if (this.loadingGui == null && (this.currentScreen == null || this.currentScreen.passEvents)) {
         this.profiler.endStartSection("Keybindings");
         this.processKeyBinds();
         if (this.leftClickCounter > 0) {
            --this.leftClickCounter;
         }
      }

      if (this.world != null) {
         this.profiler.endStartSection("gameRenderer");
         if (!this.isGamePaused) {
            this.gameRenderer.tick();
         }

         this.profiler.endStartSection("levelRenderer");
         if (!this.isGamePaused) {
            this.worldRenderer.tick();
         }

         this.profiler.endStartSection("level");
         if (!this.isGamePaused) {
            if (this.world.func_228332_n_() > 0) {
               this.world.func_225605_c_(this.world.func_228332_n_() - 1);
            }

            this.world.tickEntities();
         }
      } else if (this.gameRenderer.getShaderGroup() != null) {
         this.gameRenderer.stopUseShader();
      }

      if (!this.isGamePaused) {
         this.musicTicker.tick();
      }

      this.soundHandler.tick(this.isGamePaused);
      if (this.world != null) {
         if (!this.isGamePaused) {
            this.world.setAllowedSpawnTypes(this.world.getDifficulty() != Difficulty.PEACEFUL, true);
            this.tutorial.tick();

            try {
               this.world.tick(() -> {
                  return true;
               });
            } catch (Throwable var4) {
               CrashReport crashreport = CrashReport.makeCrashReport(var4, "Exception in world tick");
               if (this.world == null) {
                  CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected level");
                  crashreportcategory.addDetail("Problem", (Object)"Level is null!");
               } else {
                  this.world.fillCrashReport(crashreport);
               }

               throw new ReportedException(crashreport);
            }
         }

         this.profiler.endStartSection("animateTick");
         if (!this.isGamePaused && this.world != null) {
            this.world.animateTick(MathHelper.floor(this.player.func_226277_ct_()), MathHelper.floor(this.player.func_226278_cu_()), MathHelper.floor(this.player.func_226281_cx_()));
         }

         this.profiler.endStartSection("particles");
         if (!this.isGamePaused) {
            this.particles.tick();
         }
      } else if (this.networkManager != null) {
         this.profiler.endStartSection("pendingConnection");
         this.networkManager.tick();
      }

      this.profiler.endStartSection("keyboard");
      this.keyboardListener.tick();
      this.profiler.endSection();
      BasicEventHooks.onPostClientTick();
   }

   private void processKeyBinds() {
      for(; this.gameSettings.keyBindTogglePerspective.isPressed(); this.worldRenderer.setDisplayListEntitiesDirty()) {
         ++this.gameSettings.thirdPersonView;
         if (this.gameSettings.thirdPersonView > 2) {
            this.gameSettings.thirdPersonView = 0;
         }

         if (this.gameSettings.thirdPersonView == 0) {
            this.gameRenderer.loadEntityShader(this.getRenderViewEntity());
         } else if (this.gameSettings.thirdPersonView == 1) {
            this.gameRenderer.loadEntityShader((Entity)null);
         }
      }

      while(this.gameSettings.keyBindSmoothCamera.isPressed()) {
         this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
      }

      for(int i = 0; i < 9; ++i) {
         boolean flag = this.gameSettings.keyBindSaveToolbar.isKeyDown();
         boolean flag1 = this.gameSettings.keyBindLoadToolbar.isKeyDown();
         if (this.gameSettings.keyBindsHotbar[i].isPressed()) {
            if (this.player.isSpectator()) {
               this.ingameGUI.getSpectatorGui().onHotbarSelected(i);
            } else if (this.player.isCreative() && this.currentScreen == null && (flag1 || flag)) {
               CreativeScreen.handleHotbarSnapshots(this, i, flag1, flag);
            } else {
               this.player.inventory.currentItem = i;
            }
         }
      }

      while(this.gameSettings.keyBindInventory.isPressed()) {
         if (this.playerController.isRidingHorse()) {
            this.player.sendHorseInventory();
         } else {
            this.tutorial.openInventory();
            this.displayGuiScreen(new InventoryScreen(this.player));
         }
      }

      while(this.gameSettings.keyBindAdvancements.isPressed()) {
         this.displayGuiScreen(new AdvancementsScreen(this.player.connection.getAdvancementManager()));
      }

      while(this.gameSettings.keyBindSwapHands.isPressed()) {
         if (!this.player.isSpectator()) {
            this.getConnection().sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.SWAP_HELD_ITEMS, BlockPos.ZERO, Direction.DOWN));
         }
      }

      while(this.gameSettings.keyBindDrop.isPressed()) {
         if (!this.player.isSpectator() && this.player.func_225609_n_(Screen.hasControlDown())) {
            this.player.swingArm(Hand.MAIN_HAND);
         }
      }

      boolean flag2 = this.gameSettings.chatVisibility != ChatVisibility.HIDDEN;
      if (flag2) {
         while(this.gameSettings.keyBindChat.isPressed()) {
            this.displayGuiScreen(new ChatScreen(""));
         }

         if (this.currentScreen == null && this.loadingGui == null && this.gameSettings.keyBindCommand.isPressed()) {
            this.displayGuiScreen(new ChatScreen("/"));
         }
      }

      if (this.player.isHandActive()) {
         if (!this.gameSettings.keyBindUseItem.isKeyDown()) {
            this.playerController.onStoppedUsingItem(this.player);
         }

         label113:
         while(true) {
            if (!this.gameSettings.keyBindAttack.isPressed()) {
               while(this.gameSettings.keyBindUseItem.isPressed()) {
               }

               while(true) {
                  if (this.gameSettings.keyBindPickBlock.isPressed()) {
                     continue;
                  }
                  break label113;
               }
            }
         }
      } else {
         while(this.gameSettings.keyBindAttack.isPressed()) {
            this.clickMouse();
         }

         while(this.gameSettings.keyBindUseItem.isPressed()) {
            this.rightClickMouse();
         }

         while(this.gameSettings.keyBindPickBlock.isPressed()) {
            this.middleClickMouse();
         }
      }

      if (this.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0 && !this.player.isHandActive()) {
         this.rightClickMouse();
      }

      this.sendClickBlockToController(this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() && this.mouseHelper.isMouseGrabbed());
   }

   public void launchIntegratedServer(String p_71371_1_, String p_71371_2_, @Nullable WorldSettings p_71371_3_) {
      this.func_213254_o();
      SaveHandler savehandler = this.saveFormat.getSaveLoader(p_71371_1_, (MinecraftServer)null);
      WorldInfo worldinfo = savehandler.loadWorldInfo();
      if (worldinfo == null && p_71371_3_ != null) {
         worldinfo = new WorldInfo(p_71371_3_, p_71371_1_);
         savehandler.saveWorldInfo(worldinfo);
      }

      if (p_71371_3_ == null) {
         p_71371_3_ = new WorldSettings(worldinfo);
      }

      this.field_213277_ad.set((TrackingChunkStatusListener)null);

      try {
         YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString());
         MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
         GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
         PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(this.gameDir, MinecraftServer.USER_CACHE_FILE.getName()));
         SkullTileEntity.setProfileCache(playerprofilecache);
         SkullTileEntity.setSessionService(minecraftsessionservice);
         PlayerProfileCache.setOnlineMode(false);
         this.integratedServer = new IntegratedServer(this, p_71371_1_, p_71371_2_, p_71371_3_, yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, playerprofilecache, (p_lambda$launchIntegratedServer$21_1_) -> {
            TrackingChunkStatusListener trackingchunkstatuslistener = new TrackingChunkStatusListener(p_lambda$launchIntegratedServer$21_1_ + 0);
            trackingchunkstatuslistener.func_219521_a();
            this.field_213277_ad.set(trackingchunkstatuslistener);
            Queue var10003 = this.field_213275_aU;
            var10003.getClass();
            return new ChainedChunkStatusListener(trackingchunkstatuslistener, var10003::add);
         });
         this.integratedServer.startServerThread();
         this.integratedServerIsRunning = true;
      } catch (Throwable var11) {
         CrashReport crashreport = CrashReport.makeCrashReport(var11, "Starting integrated server");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Starting integrated server");
         crashreportcategory.addDetail("Level ID", (Object)p_71371_1_);
         crashreportcategory.addDetail("Level Name", (Object)p_71371_2_);
         throw new ReportedException(crashreport);
      }

      while(this.field_213277_ad.get() == null) {
         Thread.yield();
      }

      WorldLoadProgressScreen worldloadprogressscreen = new WorldLoadProgressScreen((TrackingChunkStatusListener)this.field_213277_ad.get());
      this.displayGuiScreen(worldloadprogressscreen);

      do {
         if (this.integratedServer.serverIsInRunLoop()) {
            SocketAddress socketaddress = this.integratedServer.getNetworkSystem().addLocalEndpoint();
            NetworkManager networkmanager = NetworkManager.provideLocalClient(socketaddress);
            networkmanager.setNetHandler(new ClientLoginNetHandler(networkmanager, this, (Screen)null, (p_lambda$launchIntegratedServer$22_0_) -> {
            }));
            networkmanager.sendPacket(new CHandshakePacket(socketaddress.toString(), 0, ProtocolType.LOGIN));
            GameProfile gameProfile = this.getSession().getProfile();
            if (!this.getSession().hasCachedProperties()) {
               gameProfile = this.sessionService.fillProfileProperties(gameProfile, true);
               this.getSession().setProperties(gameProfile.getProperties());
            }

            networkmanager.sendPacket(new CLoginStartPacket(gameProfile));
            this.networkManager = networkmanager;
            return;
         }

         if (!StartupQuery.check() || this.integratedServer.isServerStopped()) {
            this.displayGuiScreen((Screen)null);
            return;
         }

         if (this.currentScreen == null) {
            this.displayGuiScreen(worldloadprogressscreen);
         }

         worldloadprogressscreen.tick();
         this.runGameLoop(false);

         try {
            Thread.sleep(16L);
         } catch (InterruptedException var10) {
         }
      } while(this.crashReporter == null);

      displayCrashReport(this.crashReporter);
   }

   public void loadWorld(ClientWorld p_71403_1_) {
      if (this.world != null) {
         MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(this.world));
      }

      WorkingScreen workingscreen = new WorkingScreen();
      workingscreen.displaySavingString(new TranslationTextComponent("connect.joining", new Object[0]));
      this.func_213241_c(workingscreen);
      this.world = p_71403_1_;
      this.updateWorldRenderer(p_71403_1_);
      if (!this.integratedServerIsRunning) {
         AuthenticationService authenticationservice = new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString());
         MinecraftSessionService minecraftsessionservice = authenticationservice.createMinecraftSessionService();
         GameProfileRepository gameprofilerepository = authenticationservice.createProfileRepository();
         PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(this.gameDir, MinecraftServer.USER_CACHE_FILE.getName()));
         SkullTileEntity.setProfileCache(playerprofilecache);
         SkullTileEntity.setSessionService(minecraftsessionservice);
         PlayerProfileCache.setOnlineMode(false);
      }

   }

   public void func_213254_o() {
      this.func_213231_b(new WorkingScreen());
   }

   public void func_213231_b(Screen p_213231_1_) {
      ClientPlayNetHandler clientplaynethandler = this.getConnection();
      if (clientplaynethandler != null) {
         this.dropTasks();
         clientplaynethandler.cleanup();
      }

      IntegratedServer integratedserver = this.integratedServer;
      this.integratedServer = null;
      this.gameRenderer.resetData();
      ClientHooks.firePlayerLogout(this.playerController, this.player);
      this.playerController = null;
      NarratorChatListener.INSTANCE.clear();
      this.func_213241_c(p_213231_1_);
      if (this.world != null) {
         MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(this.world));
         if (integratedserver != null) {
            while(!integratedserver.isThreadAlive()) {
               this.runGameLoop(false);
            }
         }

         this.packFinder.clearResourcePack();
         this.ingameGUI.resetPlayersOverlayFooterHeader();
         this.currentServerData = null;
         this.integratedServerIsRunning = false;
         ClientHooks.handleClientWorldClosing(this.world);
         this.game.func_216815_b();
      }

      this.world = null;
      this.updateWorldRenderer((ClientWorld)null);
      this.player = null;
   }

   private void func_213241_c(Screen p_213241_1_) {
      this.musicTicker.stop();
      this.soundHandler.stop();
      this.renderViewEntity = null;
      this.networkManager = null;
      this.displayGuiScreen(p_213241_1_);
      this.runGameLoop(false);
   }

   private void updateWorldRenderer(@Nullable ClientWorld p_213257_1_) {
      this.worldRenderer.setWorldAndLoadRenderers(p_213257_1_);
      this.particles.clearEffects(p_213257_1_);
      TileEntityRendererDispatcher.instance.setWorld(p_213257_1_);
      this.func_230150_b_();
      MinecraftForgeClient.clearRenderCache();
   }

   public final boolean isDemo() {
      return this.isDemo;
   }

   @Nullable
   public ClientPlayNetHandler getConnection() {
      return this.player == null ? null : this.player.connection;
   }

   public static boolean isGuiEnabled() {
      return !instance.gameSettings.hideGUI;
   }

   public static boolean isFancyGraphicsEnabled() {
      return instance.gameSettings.fancyGraphics;
   }

   public static boolean isAmbientOcclusionEnabled() {
      return instance.gameSettings.ambientOcclusionStatus != AmbientOcclusionStatus.OFF;
   }

   private void middleClickMouse() {
      if (this.objectMouseOver != null && this.objectMouseOver.getType() != RayTraceResult.Type.MISS && !ForgeHooksClient.onClickInput(2, this.gameSettings.keyBindPickBlock, Hand.MAIN_HAND).isCanceled()) {
         ForgeHooks.onPickBlock(this.objectMouseOver, this.player, this.world);
      }

   }

   public ItemStack storeTEInStack(ItemStack p_184119_1_, TileEntity p_184119_2_) {
      CompoundNBT compoundnbt = p_184119_2_.write(new CompoundNBT());
      CompoundNBT compoundnbt1;
      if (p_184119_1_.getItem() instanceof SkullItem && compoundnbt.contains("Owner")) {
         compoundnbt1 = compoundnbt.getCompound("Owner");
         p_184119_1_.getOrCreateTag().put("SkullOwner", compoundnbt1);
         return p_184119_1_;
      } else {
         p_184119_1_.setTagInfo("BlockEntityTag", compoundnbt);
         compoundnbt1 = new CompoundNBT();
         ListNBT listnbt = new ListNBT();
         listnbt.add(StringNBT.func_229705_a_("\"(+NBT)\""));
         compoundnbt1.put("Lore", listnbt);
         p_184119_1_.setTagInfo("display", compoundnbt1);
         return p_184119_1_;
      }
   }

   public CrashReport addGraphicsAndWorldToCrashReport(CrashReport p_71396_1_) {
      func_228009_a_(this.languageManager, this.launchedVersion, this.gameSettings, p_71396_1_);
      if (this.world != null) {
         this.world.fillCrashReport(p_71396_1_);
      }

      return p_71396_1_;
   }

   public static void func_228009_a_(@Nullable LanguageManager p_228009_0_, String p_228009_1_, @Nullable GameSettings p_228009_2_, CrashReport p_228009_3_) {
      CrashReportCategory crashreportcategory = p_228009_3_.getCategory();
      crashreportcategory.addDetail("Launched Version", () -> {
         return p_228009_1_;
      });
      crashreportcategory.addDetail("Backend library", RenderSystem::getBackendDescription);
      crashreportcategory.addDetail("Backend API", RenderSystem::getApiDescription);
      crashreportcategory.addDetail("GL Caps", RenderSystem::getCapsString);
      crashreportcategory.addDetail("Using VBOs", () -> {
         return "Yes";
      });
      crashreportcategory.addDetail("Is Modded", () -> {
         String s = ClientBrandRetriever.getClientModName();
         if (!"vanilla".equals(s)) {
            return "Definitely; Client brand changed to '" + s + "'";
         } else {
            return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.";
         }
      });
      crashreportcategory.addDetail("Type", (Object)"Client (map_client.txt)");
      if (p_228009_2_ != null) {
         crashreportcategory.addDetail("Resource Packs", () -> {
            StringBuilder stringbuilder = new StringBuilder();
            Iterator var2 = p_228009_2_.resourcePacks.iterator();

            while(var2.hasNext()) {
               String s = (String)var2.next();
               if (stringbuilder.length() > 0) {
                  stringbuilder.append(", ");
               }

               stringbuilder.append(s);
               if (p_228009_2_.incompatibleResourcePacks.contains(s)) {
                  stringbuilder.append(" (incompatible)");
               }
            }

            return stringbuilder.toString();
         });
      }

      if (p_228009_0_ != null) {
         crashreportcategory.addDetail("Current Language", () -> {
            return p_228009_0_.getCurrentLanguage().toString();
         });
      }

      crashreportcategory.addDetail("CPU", PlatformDescriptors::func_227775_b_);
   }

   public static Minecraft getInstance() {
      return instance;
   }

   /** @deprecated */
   @Deprecated
   public CompletableFuture<Void> func_213245_w() {
      return this.supplyAsync(this::reloadResources).thenCompose((p_lambda$func_213245_w$28_0_) -> {
         return p_lambda$func_213245_w$28_0_;
      });
   }

   public void fillSnooper(Snooper p_70000_1_) {
      p_70000_1_.addClientStat("fps", debugFPS);
      p_70000_1_.addClientStat("vsync_enabled", this.gameSettings.vsync);
      p_70000_1_.addClientStat("display_frequency", this.mainWindow.func_227798_a_());
      p_70000_1_.addClientStat("display_type", this.mainWindow.isFullscreen() ? "fullscreen" : "windowed");
      p_70000_1_.addClientStat("run_time", (Util.milliTime() - p_70000_1_.getMinecraftStartTimeMillis()) / 60L * 1000L);
      p_70000_1_.addClientStat("current_action", this.getCurrentAction());
      p_70000_1_.addClientStat("language", this.gameSettings.language == null ? "en_us" : this.gameSettings.language);
      String s = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
      p_70000_1_.addClientStat("endianness", s);
      p_70000_1_.addClientStat("subtitles", this.gameSettings.showSubtitles);
      p_70000_1_.addClientStat("touch", this.gameSettings.touchscreen ? "touch" : "mouse");
      int i = 0;
      Iterator var4 = this.resourcePackRepository.getEnabledPacks().iterator();

      while(var4.hasNext()) {
         ClientResourcePackInfo clientresourcepackinfo = (ClientResourcePackInfo)var4.next();
         if (!clientresourcepackinfo.isAlwaysEnabled() && !clientresourcepackinfo.isOrderLocked()) {
            p_70000_1_.addClientStat("resource_pack[" + i++ + "]", clientresourcepackinfo.getName());
         }
      }

      p_70000_1_.addClientStat("resource_packs", i);
      if (this.integratedServer != null) {
         p_70000_1_.addClientStat("snooper_partner", this.integratedServer.getSnooper().getUniqueID());
      }

   }

   private String getCurrentAction() {
      if (this.integratedServer != null) {
         return this.integratedServer.getPublic() ? "hosting_lan" : "singleplayer";
      } else if (this.currentServerData != null) {
         return this.currentServerData.isOnLAN() ? "playing_lan" : "multiplayer";
      } else {
         return "out_of_game";
      }
   }

   public void setServerData(@Nullable ServerData p_71351_1_) {
      this.currentServerData = p_71351_1_;
   }

   @Nullable
   public ServerData getCurrentServerData() {
      return this.currentServerData;
   }

   public boolean isIntegratedServerRunning() {
      return this.integratedServerIsRunning;
   }

   public boolean isSingleplayer() {
      return this.integratedServerIsRunning && this.integratedServer != null;
   }

   @Nullable
   public IntegratedServer getIntegratedServer() {
      return this.integratedServer;
   }

   public Snooper getSnooper() {
      return this.snooper;
   }

   public Session getSession() {
      return this.session;
   }

   public PropertyMap getProfileProperties() {
      if (this.profileProperties.isEmpty()) {
         GameProfile gameprofile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
         this.profileProperties.putAll(gameprofile.getProperties());
      }

      return this.profileProperties;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   public TextureManager getTextureManager() {
      return this.textureManager;
   }

   public IResourceManager getResourceManager() {
      return this.resourceManager;
   }

   public ResourcePackList<ClientResourcePackInfo> getResourcePackList() {
      return this.resourcePackRepository;
   }

   public DownloadingPackFinder getPackFinder() {
      return this.packFinder;
   }

   public File getFileResourcePacks() {
      return this.fileResourcepacks;
   }

   public LanguageManager getLanguageManager() {
      return this.languageManager;
   }

   public Function<ResourceLocation, TextureAtlasSprite> func_228015_a_(ResourceLocation p_228015_1_) {
      AtlasTexture var10000 = this.modelManager.func_229356_a_(p_228015_1_);
      return var10000::getSprite;
   }

   public boolean isJava64bit() {
      return this.jvm64bit;
   }

   public boolean isGamePaused() {
      return this.isGamePaused;
   }

   public SoundHandler getSoundHandler() {
      return this.soundHandler;
   }

   public MusicTicker.MusicType getAmbientMusicType() {
      MusicTicker.MusicType type = this.world != null && this.world.dimension != null ? this.world.dimension.getMusicType() : null;
      if (type != null) {
         return type;
      } else if (this.currentScreen instanceof WinGameScreen) {
         return MusicTicker.MusicType.CREDITS;
      } else if (this.player == null) {
         return MusicTicker.MusicType.MENU;
      } else if (this.player.world.dimension instanceof NetherDimension) {
         return MusicTicker.MusicType.NETHER;
      } else if (this.player.world.dimension instanceof EndDimension) {
         return this.ingameGUI.getBossOverlay().shouldPlayEndBossMusic() ? MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END;
      } else {
         Biome.Category biome$category = this.player.world.func_226691_t_(new BlockPos(this.player)).getCategory();
         if (this.musicTicker.isPlaying(MusicTicker.MusicType.UNDER_WATER) || this.player.canSwim() && !this.musicTicker.isPlaying(MusicTicker.MusicType.GAME) && (biome$category == Biome.Category.OCEAN || biome$category == Biome.Category.RIVER)) {
            return MusicTicker.MusicType.UNDER_WATER;
         } else {
            return this.player.abilities.isCreativeMode && this.player.abilities.allowFlying ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME;
         }
      }
   }

   public MinecraftSessionService getSessionService() {
      return this.sessionService;
   }

   public SkinManager getSkinManager() {
      return this.skinManager;
   }

   @Nullable
   public Entity getRenderViewEntity() {
      return this.renderViewEntity;
   }

   public void setRenderViewEntity(Entity p_175607_1_) {
      this.renderViewEntity = p_175607_1_;
      this.gameRenderer.loadEntityShader(p_175607_1_);
   }

   protected Thread getExecutionThread() {
      return this.thread;
   }

   protected Runnable wrapTask(Runnable p_212875_1_) {
      return p_212875_1_;
   }

   protected boolean canRun(Runnable p_212874_1_) {
      return true;
   }

   public BlockRendererDispatcher getBlockRendererDispatcher() {
      return this.blockRenderDispatcher;
   }

   public EntityRendererManager getRenderManager() {
      return this.renderManager;
   }

   public ItemRenderer getItemRenderer() {
      return this.itemRenderer;
   }

   public FirstPersonRenderer getFirstPersonRenderer() {
      return this.firstPersonRenderer;
   }

   public <T> IMutableSearchTree<T> func_213253_a(SearchTreeManager.Key<T> p_213253_1_) {
      return this.searchTreeManager.get(p_213253_1_);
   }

   public FrameTimer getFrameTimer() {
      return this.frameTimer;
   }

   public boolean isConnectedToRealms() {
      return this.connectedToRealms;
   }

   public void setConnectedToRealms(boolean p_181537_1_) {
      this.connectedToRealms = p_181537_1_;
   }

   public DataFixer getDataFixer() {
      return this.dataFixer;
   }

   public float getRenderPartialTicks() {
      return this.timer.renderPartialTicks;
   }

   public float getTickLength() {
      return this.timer.elapsedPartialTicks;
   }

   public BlockColors getBlockColors() {
      return this.blockColors;
   }

   public boolean isReducedDebug() {
      return this.player != null && this.player.hasReducedDebug() || this.gameSettings.reducedDebugInfo;
   }

   public ToastGui getToastGui() {
      return this.toastGui;
   }

   public Tutorial getTutorial() {
      return this.tutorial;
   }

   public boolean isGameFocused() {
      return this.isWindowFocused;
   }

   public CreativeSettings getCreativeSettings() {
      return this.creativeSettings;
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public FontResourceManager getFontResourceManager() {
      return this.fontResourceMananger;
   }

   public PaintingSpriteUploader getPaintingSpriteUploader() {
      return this.paintingSprites;
   }

   public PotionSpriteUploader getPotionSpriteUploader() {
      return this.potionSprites;
   }

   public void setGameFocused(boolean p_213228_1_) {
      this.isWindowFocused = p_213228_1_;
   }

   public IProfiler getProfiler() {
      return this.profiler;
   }

   public MinecraftGame getMinecraftGame() {
      return this.game;
   }

   public Splashes getSplashes() {
      return this.splashes;
   }

   @Nullable
   public LoadingGui getLoadingGui() {
      return this.loadingGui;
   }

   public boolean func_228017_as_() {
      return false;
   }

   public MainWindow func_228018_at_() {
      return this.mainWindow;
   }

   public RenderTypeBuffers func_228019_au_() {
      return this.field_228006_P_;
   }

   private static ClientResourcePackInfo func_228011_a_(String p_228011_0_, boolean p_228011_1_, Supplier<IResourcePack> p_228011_2_, IResourcePack p_228011_3_, PackMetadataSection p_228011_4_, ResourcePackInfo.Priority p_228011_5_) {
      int i = p_228011_4_.getPackFormat();
      Supplier<IResourcePack> supplier = p_228011_2_;
      if (i <= 3) {
         supplier = func_228021_b_(p_228011_2_);
      }

      if (i <= 4) {
         supplier = func_228022_c_(supplier);
      }

      return new ClientResourcePackInfo(p_228011_0_, p_228011_1_, supplier, p_228011_3_, p_228011_4_, p_228011_5_, p_228011_3_.isHidden());
   }

   private static Supplier<IResourcePack> func_228021_b_(Supplier<IResourcePack> p_228021_0_) {
      return () -> {
         return new LegacyResourcePackWrapper((IResourcePack)p_228021_0_.get(), LegacyResourcePackWrapper.NEW_TO_LEGACY_MAP);
      };
   }

   private static Supplier<IResourcePack> func_228022_c_(Supplier<IResourcePack> p_228022_0_) {
      return () -> {
         return new LegacyResourcePackWrapperV4((IResourcePack)p_228022_0_.get());
      };
   }

   public void func_228020_b_(int p_228020_1_) {
      this.modelManager.func_229355_a_(p_228020_1_);
   }

   public ItemColors getItemColors() {
      return this.itemColors;
   }

   public SearchTreeManager getSearchTreeManager() {
      return this.searchTreeManager;
   }

   static {
      IS_RUNNING_ON_MAC = Util.getOSType() == Util.OS.OSX;
      DEFAULT_FONT_RENDERER_NAME = new ResourceLocation("default");
      standardGalacticFontRenderer = new ResourceLocation("alt");
      field_223714_G = CompletableFuture.completedFuture(Unit.INSTANCE);
      memoryReserve = new byte[10485760];
   }
}
