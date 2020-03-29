package net.minecraft.client.gui.screen;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.login.ClientLoginNetHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.client.CLoginStartPacket;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ConnectingScreen extends Screen {
   private static final AtomicInteger CONNECTION_ID = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();
   private NetworkManager networkManager;
   private boolean cancel;
   private final Screen previousGuiScreen;
   private ITextComponent field_209515_s = new TranslationTextComponent("connect.connecting", new Object[0]);
   private long field_213000_g = -1L;

   public ConnectingScreen(Screen p_i1181_1_, Minecraft p_i1181_2_, ServerData p_i1181_3_) {
      super(NarratorChatListener.field_216868_a);
      this.minecraft = p_i1181_2_;
      this.previousGuiScreen = p_i1181_1_;
      ServerAddress lvt_4_1_ = ServerAddress.fromString(p_i1181_3_.serverIP);
      p_i1181_2_.func_213254_o();
      p_i1181_2_.setServerData(p_i1181_3_);
      this.connect(lvt_4_1_.getIP(), lvt_4_1_.getPort());
   }

   public ConnectingScreen(Screen p_i1182_1_, Minecraft p_i1182_2_, String p_i1182_3_, int p_i1182_4_) {
      super(NarratorChatListener.field_216868_a);
      this.minecraft = p_i1182_2_;
      this.previousGuiScreen = p_i1182_1_;
      p_i1182_2_.func_213254_o();
      this.connect(p_i1182_3_, p_i1182_4_);
   }

   private void connect(final String p_146367_1_, final int p_146367_2_) {
      LOGGER.info("Connecting to {}, {}", p_146367_1_, p_146367_2_);
      Thread lvt_3_1_ = new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet()) {
         public void run() {
            InetAddress lvt_1_1_ = null;

            try {
               if (ConnectingScreen.this.cancel) {
                  return;
               }

               lvt_1_1_ = InetAddress.getByName(p_146367_1_);
               ConnectingScreen.this.networkManager = NetworkManager.createNetworkManagerAndConnect(lvt_1_1_, p_146367_2_, ConnectingScreen.this.minecraft.gameSettings.isUsingNativeTransport());
               ConnectingScreen.this.networkManager.setNetHandler(new ClientLoginNetHandler(ConnectingScreen.this.networkManager, ConnectingScreen.this.minecraft, ConnectingScreen.this.previousGuiScreen, (p_209549_1_) -> {
                  ConnectingScreen.this.func_209514_a(p_209549_1_);
               }));
               ConnectingScreen.this.networkManager.sendPacket(new CHandshakePacket(p_146367_1_, p_146367_2_, ProtocolType.LOGIN));
               ConnectingScreen.this.networkManager.sendPacket(new CLoginStartPacket(ConnectingScreen.this.minecraft.getSession().getProfile()));
            } catch (UnknownHostException var4) {
               if (ConnectingScreen.this.cancel) {
                  return;
               }

               ConnectingScreen.LOGGER.error("Couldn't connect to server", var4);
               ConnectingScreen.this.minecraft.execute(() -> {
                  ConnectingScreen.this.minecraft.displayGuiScreen(new DisconnectedScreen(ConnectingScreen.this.previousGuiScreen, "connect.failed", new TranslationTextComponent("disconnect.genericReason", new Object[]{"Unknown host"})));
               });
            } catch (Exception var5) {
               if (ConnectingScreen.this.cancel) {
                  return;
               }

               ConnectingScreen.LOGGER.error("Couldn't connect to server", var5);
               String lvt_3_1_ = lvt_1_1_ == null ? var5.toString() : var5.toString().replaceAll(lvt_1_1_ + ":" + p_146367_2_, "");
               ConnectingScreen.this.minecraft.execute(() -> {
                  ConnectingScreen.this.minecraft.displayGuiScreen(new DisconnectedScreen(ConnectingScreen.this.previousGuiScreen, "connect.failed", new TranslationTextComponent("disconnect.genericReason", new Object[]{lvt_3_1_})));
               });
            }

         }
      };
      lvt_3_1_.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      lvt_3_1_.start();
   }

   private void func_209514_a(ITextComponent p_209514_1_) {
      this.field_209515_s = p_209514_1_;
   }

   public void tick() {
      if (this.networkManager != null) {
         if (this.networkManager.isChannelOpen()) {
            this.networkManager.tick();
         } else {
            this.networkManager.handleDisconnection();
         }
      }

   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, I18n.format("gui.cancel"), (p_212999_1_) -> {
         this.cancel = true;
         if (this.networkManager != null) {
            this.networkManager.closeChannel(new TranslationTextComponent("connect.aborted", new Object[0]));
         }

         this.minecraft.displayGuiScreen(this.previousGuiScreen);
      }));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      long lvt_4_1_ = Util.milliTime();
      if (lvt_4_1_ - this.field_213000_g > 2000L) {
         this.field_213000_g = lvt_4_1_;
         NarratorChatListener.INSTANCE.func_216864_a((new TranslationTextComponent("narrator.joining", new Object[0])).getString());
      }

      this.drawCenteredString(this.font, this.field_209515_s.getFormattedText(), this.width / 2, this.height / 2 - 50, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}
