package net.minecraftforge.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.client.ClientModLoader;
import net.minecraftforge.fml.loading.FMLConfig;

@OnlyIn(Dist.CLIENT)
public class NotificationModUpdateScreen extends Screen {
   private static final ResourceLocation VERSION_CHECK_ICONS = new ResourceLocation("forge", "textures/gui/version_check_icons.png");
   private final Button modButton;
   private VersionChecker.Status showNotification = null;
   private boolean hasCheckedForUpdates = false;

   public NotificationModUpdateScreen(Button modButton) {
      super(new TranslationTextComponent("forge.menu.updatescreen.title", new Object[0]));
      this.modButton = modButton;
   }

   public void init() {
      if (!this.hasCheckedForUpdates) {
         if (this.modButton != null) {
            this.showNotification = ClientModLoader.checkForUpdates();
         }

         this.hasCheckedForUpdates = true;
      }

   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      if (this.showNotification != null && this.showNotification.shouldDraw() && FMLConfig.runVersionCheck()) {
         Minecraft.getInstance().getTextureManager().bindTexture(VERSION_CHECK_ICONS);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         int x = this.modButton.x;
         int y = this.modButton.y;
         int w = this.modButton.getWidth();
         int h = this.modButton.getHeight();
         blit(x + w - (h / 2 + 4), y + (h / 2 - 4), (float)(this.showNotification.getSheetOffset() * 8), this.showNotification.isAnimated() && (System.currentTimeMillis() / 800L & 1L) == 1L ? 8.0F : 0.0F, 8, 8, 64, 16);
         RenderSystem.popMatrix();
      }
   }

   public static NotificationModUpdateScreen init(MainMenuScreen guiMainMenu, Button modButton) {
      NotificationModUpdateScreen notificationModUpdateScreen = new NotificationModUpdateScreen(modButton);
      notificationModUpdateScreen.init(guiMainMenu.getMinecraft(), guiMainMenu.width, guiMainMenu.height);
      notificationModUpdateScreen.init();
      return notificationModUpdateScreen;
   }
}
