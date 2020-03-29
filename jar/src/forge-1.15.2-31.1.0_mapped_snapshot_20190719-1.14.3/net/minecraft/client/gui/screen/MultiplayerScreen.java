package net.minecraft.client.gui.screen;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.ServerPinger;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class MultiplayerScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ServerPinger oldServerPinger = new ServerPinger();
   private final Screen parentScreen;
   protected ServerSelectionList serverListSelector;
   private ServerList savedServerList;
   private Button btnEditServer;
   private Button btnSelectServer;
   private Button btnDeleteServer;
   private String hoveringText;
   private ServerData selectedServer;
   private LanServerDetector.LanServerList lanServerList;
   private LanServerDetector.LanServerFindThread lanServerDetector;
   private boolean initialized;

   public MultiplayerScreen(Screen p_i1040_1_) {
      super(new TranslationTextComponent("multiplayer.title", new Object[0]));
      this.parentScreen = p_i1040_1_;
   }

   protected void init() {
      super.init();
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      if (this.initialized) {
         this.serverListSelector.updateSize(this.width, this.height, 32, this.height - 64);
      } else {
         this.initialized = true;
         this.savedServerList = new ServerList(this.minecraft);
         this.savedServerList.loadServerList();
         this.lanServerList = new LanServerDetector.LanServerList();

         try {
            this.lanServerDetector = new LanServerDetector.LanServerFindThread(this.lanServerList);
            this.lanServerDetector.start();
         } catch (Exception var2) {
            LOGGER.warn("Unable to start LAN server detection: {}", var2.getMessage());
         }

         this.serverListSelector = new ServerSelectionList(this, this.minecraft, this.width, this.height, 32, this.height - 64, 36);
         this.serverListSelector.updateOnlineServers(this.savedServerList);
      }

      this.children.add(this.serverListSelector);
      this.btnSelectServer = (Button)this.addButton(new Button(this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("selectServer.select"), (p_214293_1_) -> {
         this.connectToSelected();
      }));
      this.addButton(new Button(this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("selectServer.direct"), (p_214286_1_) -> {
         this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false);
         this.minecraft.displayGuiScreen(new ServerListScreen(this, this::func_214290_d, this.selectedServer));
      }));
      this.addButton(new Button(this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.format("selectServer.add"), (p_214288_1_) -> {
         this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false);
         this.minecraft.displayGuiScreen(new AddServerScreen(this, this::func_214284_c, this.selectedServer));
      }));
      this.btnEditServer = (Button)this.addButton(new Button(this.width / 2 - 154, this.height - 28, 70, 20, I18n.format("selectServer.edit"), (p_214283_1_) -> {
         ServerSelectionList.Entry lvt_2_1_ = (ServerSelectionList.Entry)this.serverListSelector.getSelected();
         if (lvt_2_1_ instanceof ServerSelectionList.NormalEntry) {
            ServerData lvt_3_1_ = ((ServerSelectionList.NormalEntry)lvt_2_1_).getServerData();
            this.selectedServer = new ServerData(lvt_3_1_.serverName, lvt_3_1_.serverIP, false);
            this.selectedServer.copyFrom(lvt_3_1_);
            this.minecraft.displayGuiScreen(new AddServerScreen(this, this::func_214292_b, this.selectedServer));
         }

      }));
      this.btnDeleteServer = (Button)this.addButton(new Button(this.width / 2 - 74, this.height - 28, 70, 20, I18n.format("selectServer.delete"), (p_214294_1_) -> {
         ServerSelectionList.Entry lvt_2_1_ = (ServerSelectionList.Entry)this.serverListSelector.getSelected();
         if (lvt_2_1_ instanceof ServerSelectionList.NormalEntry) {
            String lvt_3_1_ = ((ServerSelectionList.NormalEntry)lvt_2_1_).getServerData().serverName;
            if (lvt_3_1_ != null) {
               ITextComponent lvt_4_1_ = new TranslationTextComponent("selectServer.deleteQuestion", new Object[0]);
               ITextComponent lvt_5_1_ = new TranslationTextComponent("selectServer.deleteWarning", new Object[]{lvt_3_1_});
               String lvt_6_1_ = I18n.format("selectServer.deleteButton");
               String lvt_7_1_ = I18n.format("gui.cancel");
               this.minecraft.displayGuiScreen(new ConfirmScreen(this::func_214285_a, lvt_4_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_));
            }
         }

      }));
      this.addButton(new Button(this.width / 2 + 4, this.height - 28, 70, 20, I18n.format("selectServer.refresh"), (p_214291_1_) -> {
         this.refreshServerList();
      }));
      this.addButton(new Button(this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.format("gui.cancel"), (p_214289_1_) -> {
         this.minecraft.displayGuiScreen(this.parentScreen);
      }));
      this.func_214295_b();
   }

   public void tick() {
      super.tick();
      if (this.lanServerList.getWasUpdated()) {
         List<LanServerInfo> lvt_1_1_ = this.lanServerList.getLanServers();
         this.lanServerList.setWasNotUpdated();
         this.serverListSelector.updateNetworkServers(lvt_1_1_);
      }

      this.oldServerPinger.pingPendingNetworks();
   }

   public void removed() {
      this.minecraft.keyboardListener.enableRepeatEvents(false);
      if (this.lanServerDetector != null) {
         this.lanServerDetector.interrupt();
         this.lanServerDetector = null;
      }

      this.oldServerPinger.clearPendingNetworks();
   }

   private void refreshServerList() {
      this.minecraft.displayGuiScreen(new MultiplayerScreen(this.parentScreen));
   }

   private void func_214285_a(boolean p_214285_1_) {
      ServerSelectionList.Entry lvt_2_1_ = (ServerSelectionList.Entry)this.serverListSelector.getSelected();
      if (p_214285_1_ && lvt_2_1_ instanceof ServerSelectionList.NormalEntry) {
         this.savedServerList.func_217506_a(((ServerSelectionList.NormalEntry)lvt_2_1_).getServerData());
         this.savedServerList.saveServerList();
         this.serverListSelector.setSelected((ServerSelectionList.Entry)null);
         this.serverListSelector.updateOnlineServers(this.savedServerList);
      }

      this.minecraft.displayGuiScreen(this);
   }

   private void func_214292_b(boolean p_214292_1_) {
      ServerSelectionList.Entry lvt_2_1_ = (ServerSelectionList.Entry)this.serverListSelector.getSelected();
      if (p_214292_1_ && lvt_2_1_ instanceof ServerSelectionList.NormalEntry) {
         ServerData lvt_3_1_ = ((ServerSelectionList.NormalEntry)lvt_2_1_).getServerData();
         lvt_3_1_.serverName = this.selectedServer.serverName;
         lvt_3_1_.serverIP = this.selectedServer.serverIP;
         lvt_3_1_.copyFrom(this.selectedServer);
         this.savedServerList.saveServerList();
         this.serverListSelector.updateOnlineServers(this.savedServerList);
      }

      this.minecraft.displayGuiScreen(this);
   }

   private void func_214284_c(boolean p_214284_1_) {
      if (p_214284_1_) {
         this.savedServerList.addServerData(this.selectedServer);
         this.savedServerList.saveServerList();
         this.serverListSelector.setSelected((ServerSelectionList.Entry)null);
         this.serverListSelector.updateOnlineServers(this.savedServerList);
      }

      this.minecraft.displayGuiScreen(this);
   }

   private void func_214290_d(boolean p_214290_1_) {
      if (p_214290_1_) {
         this.connectToServer(this.selectedServer);
      } else {
         this.minecraft.displayGuiScreen(this);
      }

   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (p_keyPressed_1_ == 294) {
         this.refreshServerList();
         return true;
      } else if (this.serverListSelector.getSelected() != null) {
         if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
            return this.serverListSelector.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
         } else {
            this.connectToSelected();
            return true;
         }
      } else {
         return false;
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.hoveringText = null;
      this.renderBackground();
      this.serverListSelector.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
      if (this.hoveringText != null) {
         this.renderTooltip(Lists.newArrayList(Splitter.on("\n").split(this.hoveringText)), p_render_1_, p_render_2_);
      }

   }

   public void connectToSelected() {
      ServerSelectionList.Entry lvt_1_1_ = (ServerSelectionList.Entry)this.serverListSelector.getSelected();
      if (lvt_1_1_ instanceof ServerSelectionList.NormalEntry) {
         this.connectToServer(((ServerSelectionList.NormalEntry)lvt_1_1_).getServerData());
      } else if (lvt_1_1_ instanceof ServerSelectionList.LanDetectedEntry) {
         LanServerInfo lvt_2_1_ = ((ServerSelectionList.LanDetectedEntry)lvt_1_1_).getServerData();
         this.connectToServer(new ServerData(lvt_2_1_.getServerMotd(), lvt_2_1_.getServerIpPort(), true));
      }

   }

   private void connectToServer(ServerData p_146791_1_) {
      this.minecraft.displayGuiScreen(new ConnectingScreen(this, this.minecraft, p_146791_1_));
   }

   public void func_214287_a(ServerSelectionList.Entry p_214287_1_) {
      this.serverListSelector.setSelected(p_214287_1_);
      this.func_214295_b();
   }

   protected void func_214295_b() {
      this.btnSelectServer.active = false;
      this.btnEditServer.active = false;
      this.btnDeleteServer.active = false;
      ServerSelectionList.Entry lvt_1_1_ = (ServerSelectionList.Entry)this.serverListSelector.getSelected();
      if (lvt_1_1_ != null && !(lvt_1_1_ instanceof ServerSelectionList.LanScanEntry)) {
         this.btnSelectServer.active = true;
         if (lvt_1_1_ instanceof ServerSelectionList.NormalEntry) {
            this.btnEditServer.active = true;
            this.btnDeleteServer.active = true;
         }
      }

   }

   public ServerPinger getOldServerPinger() {
      return this.oldServerPinger;
   }

   public void setHoveringText(String p_146793_1_) {
      this.hoveringText = p_146793_1_;
   }

   public ServerList getServerList() {
      return this.savedServerList;
   }
}
