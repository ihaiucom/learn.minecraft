package net.minecraft.client.network.login;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.login.client.CCustomPayloadLoginPacket;
import net.minecraft.network.login.client.CEncryptionResponsePacket;
import net.minecraft.network.login.server.SCustomPayloadLoginPacket;
import net.minecraft.network.login.server.SDisconnectLoginPacket;
import net.minecraft.network.login.server.SEnableCompressionPacket;
import net.minecraft.network.login.server.SEncryptionRequestPacket;
import net.minecraft.network.login.server.SLoginSuccessPacket;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreenProxy;
import net.minecraft.util.CryptManager;
import net.minecraft.util.HTTPUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientLoginNetHandler implements IClientLoginNetHandler {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft mc;
   @Nullable
   private final Screen previousGuiScreen;
   private final Consumer<ITextComponent> statusMessageConsumer;
   private final NetworkManager networkManager;
   private GameProfile gameProfile;

   public ClientLoginNetHandler(NetworkManager p_i49527_1_, Minecraft p_i49527_2_, @Nullable Screen p_i49527_3_, Consumer<ITextComponent> p_i49527_4_) {
      this.networkManager = p_i49527_1_;
      this.mc = p_i49527_2_;
      this.previousGuiScreen = p_i49527_3_;
      this.statusMessageConsumer = p_i49527_4_;
   }

   public void handleEncryptionRequest(SEncryptionRequestPacket p_147389_1_) {
      SecretKey secretkey = CryptManager.createNewSharedKey();
      PublicKey publickey = p_147389_1_.getPublicKey();
      String s = (new BigInteger(CryptManager.getServerIdHash(p_147389_1_.getServerId(), publickey, secretkey))).toString(16);
      CEncryptionResponsePacket cencryptionresponsepacket = new CEncryptionResponsePacket(secretkey, publickey, p_147389_1_.getVerifyToken());
      this.statusMessageConsumer.accept(new TranslationTextComponent("connect.authorizing", new Object[0]));
      HTTPUtil.DOWNLOADER_EXECUTOR.submit(() -> {
         ITextComponent itextcomponent = this.joinServer(s);
         if (itextcomponent != null) {
            if (this.mc.getCurrentServerData() == null || !this.mc.getCurrentServerData().isOnLAN()) {
               this.networkManager.closeChannel(itextcomponent);
               return;
            }

            LOGGER.warn(itextcomponent.getString());
         }

         this.statusMessageConsumer.accept(new TranslationTextComponent("connect.encrypting", new Object[0]));
         this.networkManager.sendPacket(cencryptionresponsepacket, (p_lambda$null$0_2_) -> {
            this.networkManager.enableEncryption(secretkey);
         });
      });
   }

   @Nullable
   private ITextComponent joinServer(String p_209522_1_) {
      try {
         this.getSessionService().joinServer(this.mc.getSession().getProfile(), this.mc.getSession().getToken(), p_209522_1_);
         return null;
      } catch (AuthenticationUnavailableException var3) {
         return new TranslationTextComponent("disconnect.loginFailedInfo", new Object[]{new TranslationTextComponent("disconnect.loginFailedInfo.serversUnavailable", new Object[0])});
      } catch (InvalidCredentialsException var4) {
         return new TranslationTextComponent("disconnect.loginFailedInfo", new Object[]{new TranslationTextComponent("disconnect.loginFailedInfo.invalidSession", new Object[0])});
      } catch (AuthenticationException var5) {
         return new TranslationTextComponent("disconnect.loginFailedInfo", new Object[]{var5.getMessage()});
      }
   }

   private MinecraftSessionService getSessionService() {
      return this.mc.getSessionService();
   }

   public void handleLoginSuccess(SLoginSuccessPacket p_147390_1_) {
      this.statusMessageConsumer.accept(new TranslationTextComponent("connect.joining", new Object[0]));
      this.gameProfile = p_147390_1_.getProfile();
      this.networkManager.setConnectionState(ProtocolType.PLAY);
      NetworkHooks.handleClientLoginSuccess(this.networkManager);
      this.networkManager.setNetHandler(new ClientPlayNetHandler(this.mc, this.previousGuiScreen, this.networkManager, this.gameProfile));
   }

   public void onDisconnect(ITextComponent p_147231_1_) {
      if (this.previousGuiScreen != null && this.previousGuiScreen instanceof RealmsScreenProxy) {
         this.mc.displayGuiScreen((new DisconnectedRealmsScreen(((RealmsScreenProxy)this.previousGuiScreen).getScreen(), "connect.failed", p_147231_1_)).getProxy());
      } else {
         this.mc.displayGuiScreen(new DisconnectedScreen(this.previousGuiScreen, "connect.failed", p_147231_1_));
      }

   }

   public NetworkManager getNetworkManager() {
      return this.networkManager;
   }

   public void handleDisconnect(SDisconnectLoginPacket p_147388_1_) {
      this.networkManager.closeChannel(p_147388_1_.getReason());
   }

   public void handleEnableCompression(SEnableCompressionPacket p_180464_1_) {
      if (!this.networkManager.isLocalChannel()) {
         this.networkManager.setCompressionThreshold(p_180464_1_.getCompressionThreshold());
      }

   }

   public void handleCustomPayloadLogin(SCustomPayloadLoginPacket p_209521_1_) {
      if (!NetworkHooks.onCustomPayload(p_209521_1_, this.networkManager)) {
         this.statusMessageConsumer.accept(new TranslationTextComponent("connect.negotiating", new Object[0]));
         this.networkManager.sendPacket(new CCustomPayloadLoginPacket(p_209521_1_.getTransaction(), (PacketBuffer)null));
      }
   }
}
