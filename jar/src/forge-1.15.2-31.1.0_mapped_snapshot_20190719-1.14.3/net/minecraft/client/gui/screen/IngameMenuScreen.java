package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IngameMenuScreen extends Screen {
   private final boolean isFullMenu;

   public IngameMenuScreen(boolean p_i51519_1_) {
      super(p_i51519_1_ ? new TranslationTextComponent("menu.game", new Object[0]) : new TranslationTextComponent("menu.paused", new Object[0]));
      this.isFullMenu = p_i51519_1_;
   }

   protected void init() {
      if (this.isFullMenu) {
         this.addButtons();
      }

   }

   private void addButtons() {
      int lvt_1_1_ = true;
      int lvt_2_1_ = true;
      this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, I18n.format("menu.returnToGame"), (p_213070_1_) -> {
         this.minecraft.displayGuiScreen((Screen)null);
         this.minecraft.mouseHelper.grabMouse();
      }));
      this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 48 + -16, 98, 20, I18n.format("gui.advancements"), (p_213065_1_) -> {
         this.minecraft.displayGuiScreen(new AdvancementsScreen(this.minecraft.player.connection.getAdvancementManager()));
      }));
      this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 48 + -16, 98, 20, I18n.format("gui.stats"), (p_213066_1_) -> {
         this.minecraft.displayGuiScreen(new StatsScreen(this, this.minecraft.player.getStats()));
      }));
      String lvt_3_1_ = SharedConstants.getVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";
      this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 72 + -16, 98, 20, I18n.format("menu.sendFeedback"), (p_213072_2_) -> {
         this.minecraft.displayGuiScreen(new ConfirmOpenLinkScreen((p_213069_2_) -> {
            if (p_213069_2_) {
               Util.getOSType().openURI(lvt_3_1_);
            }

            this.minecraft.displayGuiScreen(this);
         }, lvt_3_1_, true));
      }));
      this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 72 + -16, 98, 20, I18n.format("menu.reportBugs"), (p_213063_1_) -> {
         this.minecraft.displayGuiScreen(new ConfirmOpenLinkScreen((p_213064_1_) -> {
            if (p_213064_1_) {
               Util.getOSType().openURI("https://aka.ms/snapshotbugs?ref=game");
            }

            this.minecraft.displayGuiScreen(this);
         }, "https://aka.ms/snapshotbugs?ref=game", true));
      }));
      this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 96 + -16, 98, 20, I18n.format("menu.options"), (p_213071_1_) -> {
         this.minecraft.displayGuiScreen(new OptionsScreen(this, this.minecraft.gameSettings));
      }));
      Button lvt_4_1_ = (Button)this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 96 + -16, 98, 20, I18n.format("menu.shareToLan"), (p_213068_1_) -> {
         this.minecraft.displayGuiScreen(new ShareToLanScreen(this));
      }));
      lvt_4_1_.active = this.minecraft.isSingleplayer() && !this.minecraft.getIntegratedServer().getPublic();
      Button lvt_5_1_ = (Button)this.addButton(new Button(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, I18n.format("menu.returnToMenu"), (p_213067_1_) -> {
         boolean lvt_2_1_ = this.minecraft.isIntegratedServerRunning();
         boolean lvt_3_1_ = this.minecraft.isConnectedToRealms();
         p_213067_1_.active = false;
         this.minecraft.world.sendQuittingDisconnectingPacket();
         if (lvt_2_1_) {
            this.minecraft.func_213231_b(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel", new Object[0])));
         } else {
            this.minecraft.func_213254_o();
         }

         if (lvt_2_1_) {
            this.minecraft.displayGuiScreen(new MainMenuScreen());
         } else if (lvt_3_1_) {
            RealmsBridge lvt_4_1_ = new RealmsBridge();
            lvt_4_1_.switchToRealms(new MainMenuScreen());
         } else {
            this.minecraft.displayGuiScreen(new MultiplayerScreen(new MainMenuScreen()));
         }

      }));
      if (!this.minecraft.isIntegratedServerRunning()) {
         lvt_5_1_.setMessage(I18n.format("menu.disconnect"));
      }

   }

   public void tick() {
      super.tick();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      if (this.isFullMenu) {
         this.renderBackground();
         this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 40, 16777215);
      } else {
         this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 10, 16777215);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}
