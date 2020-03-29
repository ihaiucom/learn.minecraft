package net.minecraft.client.gui.screen;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.fml.BrandingControl;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;

@OnlyIn(Dist.CLIENT)
public class MainMenuScreen extends Screen {
   public static final RenderSkyboxCube PANORAMA_RESOURCES = new RenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama"));
   private static final ResourceLocation PANORAMA_OVERLAY_TEXTURES = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
   private static final ResourceLocation ACCESSIBILITY_TEXTURES = new ResourceLocation("textures/gui/accessibility.png");
   private final boolean showTitleWronglySpelled;
   @Nullable
   private String splashText;
   private Button buttonResetDemo;
   private static final ResourceLocation MINECRAFT_TITLE_TEXTURES = new ResourceLocation("textures/gui/title/minecraft.png");
   private static final ResourceLocation MINECRAFT_TITLE_EDITION = new ResourceLocation("textures/gui/title/edition.png");
   private boolean hasCheckedForRealmsNotification;
   private Screen realmsNotification;
   private int widthCopyright;
   private int widthCopyrightRest;
   private final RenderSkybox panorama;
   private final boolean showFadeInAnimation;
   private long firstRenderTime;
   private NotificationModUpdateScreen modUpdateNotification;

   public MainMenuScreen() {
      this(false);
   }

   public MainMenuScreen(boolean p_i51107_1_) {
      super(new TranslationTextComponent("narrator.screen.title", new Object[0]));
      this.panorama = new RenderSkybox(PANORAMA_RESOURCES);
      this.showFadeInAnimation = p_i51107_1_;
      this.showTitleWronglySpelled = (double)(new Random()).nextFloat() < 1.0E-4D;
   }

   private boolean areRealmsNotificationsEnabled() {
      return this.minecraft.gameSettings.realmsNotifications && this.realmsNotification != null;
   }

   public void tick() {
      if (this.areRealmsNotificationsEnabled()) {
         this.realmsNotification.tick();
      }

   }

   public static CompletableFuture<Void> loadAsync(TextureManager p_213097_0_, Executor p_213097_1_) {
      return CompletableFuture.allOf(p_213097_0_.loadAsync(MINECRAFT_TITLE_TEXTURES, p_213097_1_), p_213097_0_.loadAsync(MINECRAFT_TITLE_EDITION, p_213097_1_), p_213097_0_.loadAsync(PANORAMA_OVERLAY_TEXTURES, p_213097_1_), PANORAMA_RESOURCES.loadAsync(p_213097_0_, p_213097_1_));
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      if (this.splashText == null) {
         this.splashText = this.minecraft.getSplashes().getSplashText();
      }

      this.widthCopyright = this.font.getStringWidth("Copyright Mojang AB. Do not distribute!");
      this.widthCopyrightRest = this.width - this.widthCopyright - 2;
      int i = true;
      int j = this.height / 4 + 48;
      Button modbutton = null;
      if (this.minecraft.isDemo()) {
         this.addDemoButtons(j, 24);
      } else {
         this.addSingleplayerMultiplayerButtons(j, 24);
         modbutton = (Button)this.addButton(new Button(this.width / 2 - 100, j + 48, 98, 20, I18n.format("fml.menu.mods"), (p_lambda$init$0_1_) -> {
            this.minecraft.displayGuiScreen(new ModListScreen(this));
         }));
      }

      this.addButton(new ImageButton(this.width / 2 - 124, j + 72 + 12, 20, 20, 0, 106, 20, Button.WIDGETS_LOCATION, 256, 256, (p_lambda$init$1_1_) -> {
         this.minecraft.displayGuiScreen(new LanguageScreen(this, this.minecraft.gameSettings, this.minecraft.getLanguageManager()));
      }, I18n.format("narrator.button.language")));
      this.addButton(new Button(this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.format("menu.options"), (p_lambda$init$2_1_) -> {
         this.minecraft.displayGuiScreen(new OptionsScreen(this, this.minecraft.gameSettings));
      }));
      this.addButton(new Button(this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.format("menu.quit"), (p_lambda$init$3_1_) -> {
         this.minecraft.shutdown();
      }));
      this.addButton(new ImageButton(this.width / 2 + 104, j + 72 + 12, 20, 20, 0, 0, 20, ACCESSIBILITY_TEXTURES, 32, 64, (p_lambda$init$4_1_) -> {
         this.minecraft.displayGuiScreen(new AccessibilityScreen(this, this.minecraft.gameSettings));
      }, I18n.format("narrator.button.accessibility")));
      this.minecraft.setConnectedToRealms(false);
      if (this.minecraft.gameSettings.realmsNotifications && !this.hasCheckedForRealmsNotification) {
         RealmsBridge realmsbridge = new RealmsBridge();
         this.realmsNotification = realmsbridge.getNotificationScreen(this);
         this.hasCheckedForRealmsNotification = true;
      }

      if (this.areRealmsNotificationsEnabled()) {
         this.realmsNotification.init(this.minecraft, this.width, this.height);
      }

      this.modUpdateNotification = NotificationModUpdateScreen.init(this, modbutton);
   }

   private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_) {
      this.addButton(new Button(this.width / 2 - 100, p_73969_1_, 200, 20, I18n.format("menu.singleplayer"), (p_lambda$addSingleplayerMultiplayerButtons$5_1_) -> {
         this.minecraft.displayGuiScreen(new WorldSelectionScreen(this));
      }));
      this.addButton(new Button(this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 1, 200, 20, I18n.format("menu.multiplayer"), (p_lambda$addSingleplayerMultiplayerButtons$6_1_) -> {
         if (this.minecraft.gameSettings.field_230152_Z_) {
            this.minecraft.displayGuiScreen(new MultiplayerScreen(this));
         } else {
            this.minecraft.displayGuiScreen(new MultiplayerWarningScreen(this));
         }

      }));
      this.addButton(new Button(this.width / 2 + 2, p_73969_1_ + p_73969_2_ * 2, 98, 20, I18n.format("menu.online"), (p_lambda$addSingleplayerMultiplayerButtons$7_1_) -> {
         this.switchToRealms();
      }));
   }

   private void addDemoButtons(int p_73972_1_, int p_73972_2_) {
      this.addButton(new Button(this.width / 2 - 100, p_73972_1_, 200, 20, I18n.format("menu.playdemo"), (p_lambda$addDemoButtons$8_1_) -> {
         this.minecraft.launchIntegratedServer("Demo_World", "Demo_World", MinecraftServer.DEMO_WORLD_SETTINGS);
      }));
      this.buttonResetDemo = (Button)this.addButton(new Button(this.width / 2 - 100, p_73972_1_ + p_73972_2_ * 1, 200, 20, I18n.format("menu.resetdemo"), (p_lambda$addDemoButtons$9_1_) -> {
         SaveFormat saveformat1 = this.minecraft.getSaveLoader();
         WorldInfo worldinfo1 = saveformat1.getWorldInfo("Demo_World");
         if (worldinfo1 != null) {
            this.minecraft.displayGuiScreen(new ConfirmScreen(this::deleteDemoWorld, new TranslationTextComponent("selectWorld.deleteQuestion", new Object[0]), new TranslationTextComponent("selectWorld.deleteWarning", new Object[]{worldinfo1.getWorldName()}), I18n.format("selectWorld.deleteButton"), I18n.format("gui.cancel")));
         }

      }));
      SaveFormat saveformat = this.minecraft.getSaveLoader();
      WorldInfo worldinfo = saveformat.getWorldInfo("Demo_World");
      if (worldinfo == null) {
         this.buttonResetDemo.active = false;
      }

   }

   private void switchToRealms() {
      RealmsBridge realmsbridge = new RealmsBridge();
      realmsbridge.switchToRealms(this);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      if (this.firstRenderTime == 0L && this.showFadeInAnimation) {
         this.firstRenderTime = Util.milliTime();
      }

      float f = this.showFadeInAnimation ? (float)(Util.milliTime() - this.firstRenderTime) / 1000.0F : 1.0F;
      fill(0, 0, this.width, this.height, -1);
      this.panorama.render(p_render_3_, MathHelper.clamp(f, 0.0F, 1.0F));
      int i = true;
      int j = this.width / 2 - 137;
      int k = true;
      this.minecraft.getTextureManager().bindTexture(PANORAMA_OVERLAY_TEXTURES);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.showFadeInAnimation ? (float)MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
      blit(0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
      float f1 = this.showFadeInAnimation ? MathHelper.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
      int l = MathHelper.ceil(f1 * 255.0F) << 24;
      if ((l & -67108864) != 0) {
         this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, f1);
         if (this.showTitleWronglySpelled) {
            this.blit(j + 0, 30, 0, 0, 99, 44);
            this.blit(j + 99, 30, 129, 0, 27, 44);
            this.blit(j + 99 + 26, 30, 126, 0, 3, 44);
            this.blit(j + 99 + 26 + 3, 30, 99, 0, 26, 44);
            this.blit(j + 155, 30, 0, 45, 155, 44);
         } else {
            this.blit(j + 0, 30, 0, 0, 155, 44);
            this.blit(j + 155, 30, 0, 45, 155, 44);
         }

         this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_EDITION);
         blit(j + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
         ForgeHooksClient.renderMainMenu(this, this.font, this.width, this.height);
         if (this.splashText != null) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)(this.width / 2 + 90), 70.0F, 0.0F);
            RenderSystem.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
            float f2 = 1.8F - MathHelper.abs(MathHelper.sin((float)(Util.milliTime() % 1000L) / 1000.0F * 6.2831855F) * 0.1F);
            f2 = f2 * 100.0F / (float)(this.font.getStringWidth(this.splashText) + 32);
            RenderSystem.scalef(f2, f2, f2);
            this.drawCenteredString(this.font, this.splashText, 0, -8, 16776960 | l);
            RenderSystem.popMatrix();
         }

         String s = "Minecraft " + SharedConstants.getVersion().getName();
         if (this.minecraft.isDemo()) {
            s = s + " Demo";
         } else {
            s = s + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
         }

         if (this.minecraft.func_230151_c_()) {
            s = s + I18n.format("menu.modded");
         }

         BrandingControl.forEachLine(true, true, (p_lambda$render$10_2_, p_lambda$render$10_3_) -> {
            FontRenderer var10001 = this.font;
            int var10004 = this.height;
            int var10006 = p_lambda$render$10_2_;
            this.font.getClass();
            this.drawString(var10001, p_lambda$render$10_3_, 2, var10004 - (10 + var10006 * (9 + 1)), 16777215 | l);
         });
         BrandingControl.forEachAboveCopyrightLine((p_lambda$render$11_2_, p_lambda$render$11_3_) -> {
            FontRenderer var10001 = this.font;
            int var10003 = this.width - this.font.getStringWidth(p_lambda$render$11_3_);
            int var10004 = this.height;
            int var10006 = p_lambda$render$11_2_ + 1;
            this.font.getClass();
            this.drawString(var10001, p_lambda$render$11_3_, var10003, var10004 - (10 + var10006 * (9 + 1)), 16777215 | l);
         });
         this.drawString(this.font, "Copyright Mojang AB. Do not distribute!", this.widthCopyrightRest, this.height - 10, 16777215 | l);
         if (p_render_1_ > this.widthCopyrightRest && p_render_1_ < this.widthCopyrightRest + this.widthCopyright && p_render_2_ > this.height - 10 && p_render_2_ < this.height) {
            fill(this.widthCopyrightRest, this.height - 1, this.widthCopyrightRest + this.widthCopyright, this.height, 16777215 | l);
         }

         Iterator var11 = this.buttons.iterator();

         while(var11.hasNext()) {
            Widget widget = (Widget)var11.next();
            widget.setAlpha(f1);
         }

         super.render(p_render_1_, p_render_2_, p_render_3_);
         if (this.areRealmsNotificationsEnabled() && f1 >= 1.0F) {
            this.realmsNotification.render(p_render_1_, p_render_2_, p_render_3_);
         }

         this.modUpdateNotification.render(p_render_1_, p_render_2_, p_render_3_);
      }

   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else if (this.areRealmsNotificationsEnabled() && this.realmsNotification.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         if (p_mouseClicked_1_ > (double)this.widthCopyrightRest && p_mouseClicked_1_ < (double)(this.widthCopyrightRest + this.widthCopyright) && p_mouseClicked_3_ > (double)(this.height - 10) && p_mouseClicked_3_ < (double)this.height) {
            this.minecraft.displayGuiScreen(new WinGameScreen(false, Runnables.doNothing()));
         }

         return false;
      }
   }

   public void removed() {
      if (this.realmsNotification != null) {
         this.realmsNotification.removed();
      }

   }

   private void deleteDemoWorld(boolean p_213087_1_) {
      if (p_213087_1_) {
         SaveFormat saveformat = this.minecraft.getSaveLoader();
         saveformat.deleteWorldDirectory("Demo_World");
      }

      this.minecraft.displayGuiScreen(this);
   }
}
