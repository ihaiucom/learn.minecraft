package net.minecraft.client;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.VideoMode;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.client.settings.AmbientOcclusionStatus;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.NarratorStatus;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.client.settings.ToggleableKeyBinding;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.HandSide;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.world.Difficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.ClientModLoader;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GameSettings {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final Type TYPE_LIST_STRING = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class};
      }

      public Type getRawType() {
         return List.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };
   private static final Splitter field_230003_aR_ = Splitter.on(':').limit(2);
   public double mouseSensitivity = 0.5D;
   public int renderDistanceChunks = -1;
   public int framerateLimit = 120;
   public CloudOption cloudOption;
   public boolean fancyGraphics;
   public AmbientOcclusionStatus ambientOcclusionStatus;
   public List<String> resourcePacks;
   public List<String> incompatibleResourcePacks;
   public ChatVisibility chatVisibility;
   public double chatOpacity;
   public double accessibilityTextBackgroundOpacity;
   @Nullable
   public String fullscreenResolution;
   public boolean hideServerAddress;
   public boolean advancedItemTooltips;
   public boolean pauseOnLostFocus;
   private final Set<PlayerModelPart> setModelParts;
   public HandSide mainHand;
   public int overrideWidth;
   public int overrideHeight;
   public boolean heldItemTooltips;
   public double chatScale;
   public double chatWidth;
   public double chatHeightUnfocused;
   public double chatHeightFocused;
   public int mipmapLevels;
   private final Map<SoundCategory, Float> soundLevels;
   public boolean useNativeTransport;
   public AttackIndicatorStatus attackIndicator;
   public TutorialSteps tutorialStep;
   public int biomeBlendRadius;
   public double mouseWheelSensitivity;
   public boolean field_225307_E;
   public int glDebugVerbosity;
   public boolean autoJump;
   public boolean autoSuggestCommands;
   public boolean chatColor;
   public boolean chatLinks;
   public boolean chatLinksPrompt;
   public boolean vsync;
   public boolean entityShadows;
   public boolean forceUnicodeFont;
   public boolean invertMouse;
   public boolean discreteMouseScroll;
   public boolean realmsNotifications;
   public boolean reducedDebugInfo;
   public boolean snooper;
   public boolean showSubtitles;
   public boolean accessibilityTextBackground;
   public boolean touchscreen;
   public boolean fullscreen;
   public boolean viewBobbing;
   public boolean field_228044_Y_;
   public boolean field_228045_Z_;
   public boolean field_230152_Z_;
   public final KeyBinding keyBindForward;
   public final KeyBinding keyBindLeft;
   public final KeyBinding keyBindBack;
   public final KeyBinding keyBindRight;
   public final KeyBinding keyBindJump;
   public final KeyBinding field_228046_af_;
   public final KeyBinding keyBindSprint;
   public final KeyBinding keyBindInventory;
   public final KeyBinding keyBindSwapHands;
   public final KeyBinding keyBindDrop;
   public final KeyBinding keyBindUseItem;
   public final KeyBinding keyBindAttack;
   public final KeyBinding keyBindPickBlock;
   public final KeyBinding keyBindChat;
   public final KeyBinding keyBindPlayerList;
   public final KeyBinding keyBindCommand;
   public final KeyBinding keyBindScreenshot;
   public final KeyBinding keyBindTogglePerspective;
   public final KeyBinding keyBindSmoothCamera;
   public final KeyBinding keyBindFullscreen;
   public final KeyBinding keyBindSpectatorOutlines;
   public final KeyBinding keyBindAdvancements;
   public final KeyBinding[] keyBindsHotbar;
   public final KeyBinding keyBindSaveToolbar;
   public final KeyBinding keyBindLoadToolbar;
   public KeyBinding[] keyBindings;
   protected Minecraft mc;
   private final File optionsFile;
   public Difficulty difficulty;
   public boolean hideGUI;
   public int thirdPersonView;
   public boolean showDebugInfo;
   public boolean showDebugProfilerChart;
   public boolean showLagometer;
   public String lastServer;
   public boolean smoothCamera;
   public double fov;
   public double gamma;
   public int guiScale;
   public ParticleStatus particles;
   public NarratorStatus narrator;
   public String language;

   public GameSettings(Minecraft p_i46326_1_, File p_i46326_2_) {
      this.cloudOption = CloudOption.FANCY;
      this.fancyGraphics = true;
      this.ambientOcclusionStatus = AmbientOcclusionStatus.MAX;
      this.resourcePacks = Lists.newArrayList();
      this.incompatibleResourcePacks = Lists.newArrayList();
      this.chatVisibility = ChatVisibility.FULL;
      this.chatOpacity = 1.0D;
      this.accessibilityTextBackgroundOpacity = 0.5D;
      this.pauseOnLostFocus = true;
      this.setModelParts = Sets.newHashSet(PlayerModelPart.values());
      this.mainHand = HandSide.RIGHT;
      this.heldItemTooltips = true;
      this.chatScale = 1.0D;
      this.chatWidth = 1.0D;
      this.chatHeightUnfocused = 0.44366195797920227D;
      this.chatHeightFocused = 1.0D;
      this.mipmapLevels = 4;
      this.soundLevels = Maps.newEnumMap(SoundCategory.class);
      this.useNativeTransport = true;
      this.attackIndicator = AttackIndicatorStatus.CROSSHAIR;
      this.tutorialStep = TutorialSteps.MOVEMENT;
      this.biomeBlendRadius = 2;
      this.mouseWheelSensitivity = 1.0D;
      this.field_225307_E = true;
      this.glDebugVerbosity = 1;
      this.autoJump = true;
      this.autoSuggestCommands = true;
      this.chatColor = true;
      this.chatLinks = true;
      this.chatLinksPrompt = true;
      this.vsync = true;
      this.entityShadows = true;
      this.realmsNotifications = true;
      this.snooper = true;
      this.accessibilityTextBackground = true;
      this.viewBobbing = true;
      this.keyBindForward = new KeyBinding("key.forward", 87, "key.categories.movement");
      this.keyBindLeft = new KeyBinding("key.left", 65, "key.categories.movement");
      this.keyBindBack = new KeyBinding("key.back", 83, "key.categories.movement");
      this.keyBindRight = new KeyBinding("key.right", 68, "key.categories.movement");
      this.keyBindJump = new KeyBinding("key.jump", 32, "key.categories.movement");
      this.field_228046_af_ = new ToggleableKeyBinding("key.sneak", 340, "key.categories.movement", () -> {
         return this.field_228044_Y_;
      });
      this.keyBindSprint = new ToggleableKeyBinding("key.sprint", 341, "key.categories.movement", () -> {
         return this.field_228045_Z_;
      });
      this.keyBindInventory = new KeyBinding("key.inventory", 69, "key.categories.inventory");
      this.keyBindSwapHands = new KeyBinding("key.swapHands", 70, "key.categories.inventory");
      this.keyBindDrop = new KeyBinding("key.drop", 81, "key.categories.inventory");
      this.keyBindUseItem = new KeyBinding("key.use", InputMappings.Type.MOUSE, 1, "key.categories.gameplay");
      this.keyBindAttack = new KeyBinding("key.attack", InputMappings.Type.MOUSE, 0, "key.categories.gameplay");
      this.keyBindPickBlock = new KeyBinding("key.pickItem", InputMappings.Type.MOUSE, 2, "key.categories.gameplay");
      this.keyBindChat = new KeyBinding("key.chat", 84, "key.categories.multiplayer");
      this.keyBindPlayerList = new KeyBinding("key.playerlist", 258, "key.categories.multiplayer");
      this.keyBindCommand = new KeyBinding("key.command", 47, "key.categories.multiplayer");
      this.keyBindScreenshot = new KeyBinding("key.screenshot", 291, "key.categories.misc");
      this.keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 294, "key.categories.misc");
      this.keyBindSmoothCamera = new KeyBinding("key.smoothCamera", InputMappings.INPUT_INVALID.getKeyCode(), "key.categories.misc");
      this.keyBindFullscreen = new KeyBinding("key.fullscreen", 300, "key.categories.misc");
      this.keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", InputMappings.INPUT_INVALID.getKeyCode(), "key.categories.misc");
      this.keyBindAdvancements = new KeyBinding("key.advancements", 76, "key.categories.misc");
      this.keyBindsHotbar = new KeyBinding[]{new KeyBinding("key.hotbar.1", 49, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 50, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 51, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 52, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 53, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 54, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 55, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 56, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 57, "key.categories.inventory")};
      this.keyBindSaveToolbar = new KeyBinding("key.saveToolbarActivator", 67, "key.categories.creative");
      this.keyBindLoadToolbar = new KeyBinding("key.loadToolbarActivator", 88, "key.categories.creative");
      this.keyBindings = (KeyBinding[])ArrayUtils.addAll(new KeyBinding[]{this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.field_228046_af_, this.keyBindSprint, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindFullscreen, this.keyBindSpectatorOutlines, this.keyBindSwapHands, this.keyBindSaveToolbar, this.keyBindLoadToolbar, this.keyBindAdvancements}, this.keyBindsHotbar);
      this.difficulty = Difficulty.NORMAL;
      this.lastServer = "";
      this.fov = 70.0D;
      this.particles = ParticleStatus.ALL;
      this.narrator = NarratorStatus.OFF;
      this.language = "en_us";
      this.setForgeKeybindProperties();
      this.mc = p_i46326_1_;
      this.optionsFile = new File(p_i46326_2_, "options.txt");
      if (p_i46326_1_.isJava64bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
         AbstractOption.RENDER_DISTANCE.func_216728_a(32.0F);
      } else {
         AbstractOption.RENDER_DISTANCE.func_216728_a(16.0F);
      }

      this.renderDistanceChunks = p_i46326_1_.isJava64bit() ? 12 : 8;
      this.loadOptions();
   }

   public float func_216840_a(float p_216840_1_) {
      return this.accessibilityTextBackground ? p_216840_1_ : (float)this.accessibilityTextBackgroundOpacity;
   }

   public int func_216841_b(float p_216841_1_) {
      return (int)(this.func_216840_a(p_216841_1_) * 255.0F) << 24 & -16777216;
   }

   public int func_216839_a(int p_216839_1_) {
      return this.accessibilityTextBackground ? p_216839_1_ : (int)(this.accessibilityTextBackgroundOpacity * 255.0D) << 24 & -16777216;
   }

   public void setKeyBindingCode(KeyBinding p_198014_1_, InputMappings.Input p_198014_2_) {
      p_198014_1_.bind(p_198014_2_);
      this.saveOptions();
   }

   public void loadOptions() {
      try {
         if (!this.optionsFile.exists()) {
            return;
         }

         this.soundLevels.clear();
         CompoundNBT compoundnbt = new CompoundNBT();
         BufferedReader bufferedreader = Files.newReader(this.optionsFile, Charsets.UTF_8);
         Throwable var3 = null;

         try {
            bufferedreader.lines().forEach((p_lambda$loadOptions$2_1_) -> {
               try {
                  Iterator<String> iterator = field_230003_aR_.split(p_lambda$loadOptions$2_1_).iterator();
                  compoundnbt.putString((String)iterator.next(), (String)iterator.next());
               } catch (Exception var3) {
                  LOGGER.warn("Skipping bad option: {}", p_lambda$loadOptions$2_1_);
               }

            });
         } catch (Throwable var18) {
            var3 = var18;
            throw var18;
         } finally {
            if (bufferedreader != null) {
               if (var3 != null) {
                  try {
                     bufferedreader.close();
                  } catch (Throwable var17) {
                     var3.addSuppressed(var17);
                  }
               } else {
                  bufferedreader.close();
               }
            }

         }

         CompoundNBT compoundnbt1 = this.dataFix(compoundnbt);
         Iterator var23 = compoundnbt1.keySet().iterator();

         while(var23.hasNext()) {
            String s = (String)var23.next();
            String s1 = compoundnbt1.getString(s);

            try {
               if ("autoJump".equals(s)) {
                  AbstractOption.AUTO_JUMP.set(this, s1);
               }

               if ("autoSuggestions".equals(s)) {
                  AbstractOption.AUTO_SUGGEST_COMMANDS.set(this, s1);
               }

               if ("chatColors".equals(s)) {
                  AbstractOption.CHAT_COLOR.set(this, s1);
               }

               if ("chatLinks".equals(s)) {
                  AbstractOption.CHAT_LINKS.set(this, s1);
               }

               if ("chatLinksPrompt".equals(s)) {
                  AbstractOption.CHAT_LINKS_PROMPT.set(this, s1);
               }

               if ("enableVsync".equals(s)) {
                  AbstractOption.VSYNC.set(this, s1);
               }

               if ("entityShadows".equals(s)) {
                  AbstractOption.ENTITY_SHADOWS.set(this, s1);
               }

               if ("forceUnicodeFont".equals(s)) {
                  AbstractOption.FORCE_UNICODE_FONT.set(this, s1);
               }

               if ("discrete_mouse_scroll".equals(s)) {
                  AbstractOption.DISCRETE_MOUSE_SCROLL.set(this, s1);
               }

               if ("invertYMouse".equals(s)) {
                  AbstractOption.INVERT_MOUSE.set(this, s1);
               }

               if ("realmsNotifications".equals(s)) {
                  AbstractOption.REALMS_NOTIFICATIONS.set(this, s1);
               }

               if ("reducedDebugInfo".equals(s)) {
                  AbstractOption.REDUCED_DEBUG_INFO.set(this, s1);
               }

               if ("showSubtitles".equals(s)) {
                  AbstractOption.SHOW_SUBTITLES.set(this, s1);
               }

               if ("snooperEnabled".equals(s)) {
                  AbstractOption.SNOOPER.set(this, s1);
               }

               if ("touchscreen".equals(s)) {
                  AbstractOption.TOUCHSCREEN.set(this, s1);
               }

               if ("fullscreen".equals(s)) {
                  AbstractOption.FULLSCREEN.set(this, s1);
               }

               if ("bobView".equals(s)) {
                  AbstractOption.VIEW_BOBBING.set(this, s1);
               }

               if ("toggleCrouch".equals(s)) {
                  this.field_228044_Y_ = "true".equals(s1);
               }

               if ("toggleSprint".equals(s)) {
                  this.field_228045_Z_ = "true".equals(s1);
               }

               if ("mouseSensitivity".equals(s)) {
                  this.mouseSensitivity = (double)parseFloat(s1);
               }

               if ("fov".equals(s)) {
                  this.fov = (double)(parseFloat(s1) * 40.0F + 70.0F);
               }

               if ("gamma".equals(s)) {
                  this.gamma = (double)parseFloat(s1);
               }

               if ("renderDistance".equals(s)) {
                  this.renderDistanceChunks = Integer.parseInt(s1);
               }

               if ("guiScale".equals(s)) {
                  this.guiScale = Integer.parseInt(s1);
               }

               if ("particles".equals(s)) {
                  this.particles = ParticleStatus.byId(Integer.parseInt(s1));
               }

               if ("maxFps".equals(s)) {
                  this.framerateLimit = Integer.parseInt(s1);
                  if (this.mc.func_228018_at_() != null) {
                     this.mc.func_228018_at_().setFramerateLimit(this.framerateLimit);
                  }
               }

               if ("difficulty".equals(s)) {
                  this.difficulty = Difficulty.byId(Integer.parseInt(s1));
               }

               if ("fancyGraphics".equals(s)) {
                  this.fancyGraphics = "true".equals(s1);
               }

               if ("tutorialStep".equals(s)) {
                  this.tutorialStep = TutorialSteps.byName(s1);
               }

               if ("ao".equals(s)) {
                  if ("true".equals(s1)) {
                     this.ambientOcclusionStatus = AmbientOcclusionStatus.MAX;
                  } else if ("false".equals(s1)) {
                     this.ambientOcclusionStatus = AmbientOcclusionStatus.OFF;
                  } else {
                     this.ambientOcclusionStatus = AmbientOcclusionStatus.func_216570_a(Integer.parseInt(s1));
                  }
               }

               if ("renderClouds".equals(s)) {
                  if ("true".equals(s1)) {
                     this.cloudOption = CloudOption.FANCY;
                  } else if ("false".equals(s1)) {
                     this.cloudOption = CloudOption.OFF;
                  } else if ("fast".equals(s1)) {
                     this.cloudOption = CloudOption.FAST;
                  }
               }

               if ("attackIndicator".equals(s)) {
                  this.attackIndicator = AttackIndicatorStatus.byId(Integer.parseInt(s1));
               }

               if ("resourcePacks".equals(s)) {
                  this.resourcePacks = (List)JSONUtils.fromJson(GSON, s1, TYPE_LIST_STRING);
                  if (this.resourcePacks == null) {
                     this.resourcePacks = Lists.newArrayList();
                  }
               }

               if ("incompatibleResourcePacks".equals(s)) {
                  this.incompatibleResourcePacks = (List)JSONUtils.fromJson(GSON, s1, TYPE_LIST_STRING);
                  if (this.incompatibleResourcePacks == null) {
                     this.incompatibleResourcePacks = Lists.newArrayList();
                  }
               }

               if ("lastServer".equals(s)) {
                  this.lastServer = s1;
               }

               if ("lang".equals(s)) {
                  this.language = s1;
               }

               if ("chatVisibility".equals(s)) {
                  this.chatVisibility = ChatVisibility.func_221252_a(Integer.parseInt(s1));
               }

               if ("chatOpacity".equals(s)) {
                  this.chatOpacity = (double)parseFloat(s1);
               }

               if ("textBackgroundOpacity".equals(s)) {
                  this.accessibilityTextBackgroundOpacity = (double)parseFloat(s1);
               }

               if ("backgroundForChatOnly".equals(s)) {
                  this.accessibilityTextBackground = "true".equals(s1);
               }

               if ("fullscreenResolution".equals(s)) {
                  this.fullscreenResolution = s1;
               }

               if ("hideServerAddress".equals(s)) {
                  this.hideServerAddress = "true".equals(s1);
               }

               if ("advancedItemTooltips".equals(s)) {
                  this.advancedItemTooltips = "true".equals(s1);
               }

               if ("pauseOnLostFocus".equals(s)) {
                  this.pauseOnLostFocus = "true".equals(s1);
               }

               if ("overrideHeight".equals(s)) {
                  this.overrideHeight = Integer.parseInt(s1);
               }

               if ("overrideWidth".equals(s)) {
                  this.overrideWidth = Integer.parseInt(s1);
               }

               if ("heldItemTooltips".equals(s)) {
                  this.heldItemTooltips = "true".equals(s1);
               }

               if ("chatHeightFocused".equals(s)) {
                  this.chatHeightFocused = (double)parseFloat(s1);
               }

               if ("chatHeightUnfocused".equals(s)) {
                  this.chatHeightUnfocused = (double)parseFloat(s1);
               }

               if ("chatScale".equals(s)) {
                  this.chatScale = (double)parseFloat(s1);
               }

               if ("chatWidth".equals(s)) {
                  this.chatWidth = (double)parseFloat(s1);
               }

               if ("mipmapLevels".equals(s)) {
                  this.mipmapLevels = Integer.parseInt(s1);
               }

               if ("useNativeTransport".equals(s)) {
                  this.useNativeTransport = "true".equals(s1);
               }

               if ("mainHand".equals(s)) {
                  this.mainHand = "left".equals(s1) ? HandSide.LEFT : HandSide.RIGHT;
               }

               if ("narrator".equals(s)) {
                  this.narrator = NarratorStatus.byId(Integer.parseInt(s1));
               }

               if ("biomeBlendRadius".equals(s)) {
                  this.biomeBlendRadius = Integer.parseInt(s1);
               }

               if ("mouseWheelSensitivity".equals(s)) {
                  this.mouseWheelSensitivity = (double)parseFloat(s1);
               }

               if ("rawMouseInput".equals(s)) {
                  this.field_225307_E = "true".equals(s1);
               }

               if ("glDebugVerbosity".equals(s)) {
                  this.glDebugVerbosity = Integer.parseInt(s1);
               }

               if ("skipMultiplayerWarning".equals(s)) {
                  this.field_230152_Z_ = "true".equals(s1);
               }

               KeyBinding[] var6 = this.keyBindings;
               int var7 = var6.length;

               int var8;
               for(var8 = 0; var8 < var7; ++var8) {
                  KeyBinding keybinding = var6[var8];
                  if (s.equals("key_" + keybinding.getKeyDescription())) {
                     if (s1.indexOf(58) != -1) {
                        String[] pts = s1.split(":");
                        keybinding.setKeyModifierAndCode(KeyModifier.valueFromString(pts[1]), InputMappings.getInputByName(pts[0]));
                     } else {
                        keybinding.setKeyModifierAndCode(KeyModifier.NONE, InputMappings.getInputByName(s1));
                     }
                  }
               }

               SoundCategory[] var24 = SoundCategory.values();
               var7 = var24.length;

               for(var8 = 0; var8 < var7; ++var8) {
                  SoundCategory soundcategory = var24[var8];
                  if (s.equals("soundCategory_" + soundcategory.getName())) {
                     this.soundLevels.put(soundcategory, parseFloat(s1));
                  }
               }

               PlayerModelPart[] var25 = PlayerModelPart.values();
               var7 = var25.length;

               for(var8 = 0; var8 < var7; ++var8) {
                  PlayerModelPart playermodelpart = var25[var8];
                  if (s.equals("modelPart_" + playermodelpart.getPartName())) {
                     this.setModelPartEnabled(playermodelpart, "true".equals(s1));
                  }
               }
            } catch (Exception var20) {
               LOGGER.warn("Skipping bad option: {}:{}", s, s1);
            }
         }

         KeyBinding.resetKeyBindingArrayAndHash();
      } catch (Exception var21) {
         LOGGER.error("Failed to load options", var21);
      }

   }

   private CompoundNBT dataFix(CompoundNBT p_189988_1_) {
      int i = 0;

      try {
         i = Integer.parseInt(p_189988_1_.getString("version"));
      } catch (RuntimeException var4) {
      }

      return NBTUtil.update(this.mc.getDataFixer(), DefaultTypeReferences.OPTIONS, p_189988_1_, i);
   }

   private static float parseFloat(String p_74305_0_) {
      if ("true".equals(p_74305_0_)) {
         return 1.0F;
      } else {
         return "false".equals(p_74305_0_) ? 0.0F : Float.parseFloat(p_74305_0_);
      }
   }

   public void saveOptions() {
      if (!ClientModLoader.isLoading()) {
         try {
            PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));
            Throwable var2 = null;

            try {
               printwriter.println("version:" + SharedConstants.getVersion().getWorldVersion());
               printwriter.println("autoJump:" + AbstractOption.AUTO_JUMP.get(this));
               printwriter.println("autoSuggestions:" + AbstractOption.AUTO_SUGGEST_COMMANDS.get(this));
               printwriter.println("chatColors:" + AbstractOption.CHAT_COLOR.get(this));
               printwriter.println("chatLinks:" + AbstractOption.CHAT_LINKS.get(this));
               printwriter.println("chatLinksPrompt:" + AbstractOption.CHAT_LINKS_PROMPT.get(this));
               printwriter.println("enableVsync:" + AbstractOption.VSYNC.get(this));
               printwriter.println("entityShadows:" + AbstractOption.ENTITY_SHADOWS.get(this));
               printwriter.println("forceUnicodeFont:" + AbstractOption.FORCE_UNICODE_FONT.get(this));
               printwriter.println("discrete_mouse_scroll:" + AbstractOption.DISCRETE_MOUSE_SCROLL.get(this));
               printwriter.println("invertYMouse:" + AbstractOption.INVERT_MOUSE.get(this));
               printwriter.println("realmsNotifications:" + AbstractOption.REALMS_NOTIFICATIONS.get(this));
               printwriter.println("reducedDebugInfo:" + AbstractOption.REDUCED_DEBUG_INFO.get(this));
               printwriter.println("snooperEnabled:" + AbstractOption.SNOOPER.get(this));
               printwriter.println("showSubtitles:" + AbstractOption.SHOW_SUBTITLES.get(this));
               printwriter.println("touchscreen:" + AbstractOption.TOUCHSCREEN.get(this));
               printwriter.println("fullscreen:" + AbstractOption.FULLSCREEN.get(this));
               printwriter.println("bobView:" + AbstractOption.VIEW_BOBBING.get(this));
               printwriter.println("toggleCrouch:" + this.field_228044_Y_);
               printwriter.println("toggleSprint:" + this.field_228045_Z_);
               printwriter.println("mouseSensitivity:" + this.mouseSensitivity);
               printwriter.println("fov:" + (this.fov - 70.0D) / 40.0D);
               printwriter.println("gamma:" + this.gamma);
               printwriter.println("renderDistance:" + this.renderDistanceChunks);
               printwriter.println("guiScale:" + this.guiScale);
               printwriter.println("particles:" + this.particles.func_216832_b());
               printwriter.println("maxFps:" + this.framerateLimit);
               printwriter.println("difficulty:" + this.difficulty.getId());
               printwriter.println("fancyGraphics:" + this.fancyGraphics);
               printwriter.println("ao:" + this.ambientOcclusionStatus.func_216572_a());
               printwriter.println("biomeBlendRadius:" + this.biomeBlendRadius);
               switch(this.cloudOption) {
               case FANCY:
                  printwriter.println("renderClouds:true");
                  break;
               case FAST:
                  printwriter.println("renderClouds:fast");
                  break;
               case OFF:
                  printwriter.println("renderClouds:false");
               }

               printwriter.println("resourcePacks:" + GSON.toJson(this.resourcePacks));
               printwriter.println("incompatibleResourcePacks:" + GSON.toJson(this.incompatibleResourcePacks));
               printwriter.println("lastServer:" + this.lastServer);
               printwriter.println("lang:" + this.language);
               printwriter.println("chatVisibility:" + this.chatVisibility.func_221254_a());
               printwriter.println("chatOpacity:" + this.chatOpacity);
               printwriter.println("textBackgroundOpacity:" + this.accessibilityTextBackgroundOpacity);
               printwriter.println("backgroundForChatOnly:" + this.accessibilityTextBackground);
               if (this.mc.func_228018_at_().getVideoMode().isPresent()) {
                  printwriter.println("fullscreenResolution:" + ((VideoMode)this.mc.func_228018_at_().getVideoMode().get()).getSettingsString());
               }

               printwriter.println("hideServerAddress:" + this.hideServerAddress);
               printwriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
               printwriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
               printwriter.println("overrideWidth:" + this.overrideWidth);
               printwriter.println("overrideHeight:" + this.overrideHeight);
               printwriter.println("heldItemTooltips:" + this.heldItemTooltips);
               printwriter.println("chatHeightFocused:" + this.chatHeightFocused);
               printwriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
               printwriter.println("chatScale:" + this.chatScale);
               printwriter.println("chatWidth:" + this.chatWidth);
               printwriter.println("mipmapLevels:" + this.mipmapLevels);
               printwriter.println("useNativeTransport:" + this.useNativeTransport);
               printwriter.println("mainHand:" + (this.mainHand == HandSide.LEFT ? "left" : "right"));
               printwriter.println("attackIndicator:" + this.attackIndicator.func_216751_a());
               printwriter.println("narrator:" + this.narrator.func_216827_a());
               printwriter.println("tutorialStep:" + this.tutorialStep.getName());
               printwriter.println("mouseWheelSensitivity:" + this.mouseWheelSensitivity);
               printwriter.println("rawMouseInput:" + AbstractOption.field_225302_l.get(this));
               printwriter.println("glDebugVerbosity:" + this.glDebugVerbosity);
               printwriter.println("skipMultiplayerWarning:" + this.field_230152_Z_);
               KeyBinding[] var3 = this.keyBindings;
               int var4 = var3.length;

               int var5;
               for(var5 = 0; var5 < var4; ++var5) {
                  KeyBinding keybinding = var3[var5];
                  printwriter.println("key_" + keybinding.getKeyDescription() + ":" + keybinding.getTranslationKey() + (keybinding.getKeyModifier() != KeyModifier.NONE ? ":" + keybinding.getKeyModifier() : ""));
               }

               SoundCategory[] var18 = SoundCategory.values();
               var4 = var18.length;

               for(var5 = 0; var5 < var4; ++var5) {
                  SoundCategory soundcategory = var18[var5];
                  printwriter.println("soundCategory_" + soundcategory.getName() + ":" + this.getSoundLevel(soundcategory));
               }

               PlayerModelPart[] var19 = PlayerModelPart.values();
               var4 = var19.length;

               for(var5 = 0; var5 < var4; ++var5) {
                  PlayerModelPart playermodelpart = var19[var5];
                  printwriter.println("modelPart_" + playermodelpart.getPartName() + ":" + this.setModelParts.contains(playermodelpart));
               }
            } catch (Throwable var15) {
               var2 = var15;
               throw var15;
            } finally {
               if (printwriter != null) {
                  if (var2 != null) {
                     try {
                        printwriter.close();
                     } catch (Throwable var14) {
                        var2.addSuppressed(var14);
                     }
                  } else {
                     printwriter.close();
                  }
               }

            }
         } catch (Exception var17) {
            LOGGER.error("Failed to save options", var17);
         }

         this.sendSettingsToServer();
      }
   }

   public float getSoundLevel(SoundCategory p_186711_1_) {
      return this.soundLevels.containsKey(p_186711_1_) ? (Float)this.soundLevels.get(p_186711_1_) : 1.0F;
   }

   public void setSoundLevel(SoundCategory p_186712_1_, float p_186712_2_) {
      this.soundLevels.put(p_186712_1_, p_186712_2_);
      this.mc.getSoundHandler().setSoundLevel(p_186712_1_, p_186712_2_);
   }

   public void sendSettingsToServer() {
      if (this.mc.player != null) {
         int i = 0;

         PlayerModelPart playermodelpart;
         for(Iterator var2 = this.setModelParts.iterator(); var2.hasNext(); i |= playermodelpart.getPartMask()) {
            playermodelpart = (PlayerModelPart)var2.next();
         }

         this.mc.player.connection.sendPacket(new CClientSettingsPacket(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColor, i, this.mainHand));
      }

   }

   public Set<PlayerModelPart> getModelParts() {
      return ImmutableSet.copyOf(this.setModelParts);
   }

   public void setModelPartEnabled(PlayerModelPart p_178878_1_, boolean p_178878_2_) {
      if (p_178878_2_) {
         this.setModelParts.add(p_178878_1_);
      } else {
         this.setModelParts.remove(p_178878_1_);
      }

      this.sendSettingsToServer();
   }

   public void switchModelPartEnabled(PlayerModelPart p_178877_1_) {
      if (this.getModelParts().contains(p_178877_1_)) {
         this.setModelParts.remove(p_178877_1_);
      } else {
         this.setModelParts.add(p_178877_1_);
      }

      this.sendSettingsToServer();
   }

   public CloudOption getCloudOption() {
      return this.renderDistanceChunks >= 4 ? this.cloudOption : CloudOption.OFF;
   }

   public boolean isUsingNativeTransport() {
      return this.useNativeTransport;
   }

   public void fillResourcePackList(ResourcePackList<ClientResourcePackInfo> p_198017_1_) {
      p_198017_1_.reloadPacksFromFinders();
      Set<ClientResourcePackInfo> set = Sets.newLinkedHashSet();
      Iterator iterator = this.resourcePacks.iterator();

      while(true) {
         while(iterator.hasNext()) {
            String s = (String)iterator.next();
            ClientResourcePackInfo clientresourcepackinfo = (ClientResourcePackInfo)p_198017_1_.getPackInfo(s);
            if (clientresourcepackinfo == null && !s.startsWith("file/")) {
               clientresourcepackinfo = (ClientResourcePackInfo)p_198017_1_.getPackInfo("file/" + s);
            }

            if (clientresourcepackinfo == null) {
               LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", s);
               iterator.remove();
            } else if (!clientresourcepackinfo.getCompatibility().func_198968_a() && !this.incompatibleResourcePacks.contains(s)) {
               LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", s);
               iterator.remove();
            } else if (clientresourcepackinfo.getCompatibility().func_198968_a() && this.incompatibleResourcePacks.contains(s)) {
               LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", s);
               this.incompatibleResourcePacks.remove(s);
            } else {
               set.add(clientresourcepackinfo);
            }
         }

         p_198017_1_.setEnabledPacks(set);
         return;
      }
   }

   private void setForgeKeybindProperties() {
      KeyConflictContext inGame = KeyConflictContext.IN_GAME;
      this.keyBindForward.setKeyConflictContext(inGame);
      this.keyBindLeft.setKeyConflictContext(inGame);
      this.keyBindBack.setKeyConflictContext(inGame);
      this.keyBindRight.setKeyConflictContext(inGame);
      this.keyBindJump.setKeyConflictContext(inGame);
      this.field_228046_af_.setKeyConflictContext(inGame);
      this.keyBindSprint.setKeyConflictContext(inGame);
      this.keyBindAttack.setKeyConflictContext(inGame);
      this.keyBindChat.setKeyConflictContext(inGame);
      this.keyBindPlayerList.setKeyConflictContext(inGame);
      this.keyBindCommand.setKeyConflictContext(inGame);
      this.keyBindTogglePerspective.setKeyConflictContext(inGame);
      this.keyBindSmoothCamera.setKeyConflictContext(inGame);
      this.keyBindSwapHands.setKeyConflictContext(inGame);
   }
}
