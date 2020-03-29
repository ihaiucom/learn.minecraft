package net.minecraft.client.network.login;

import net.minecraft.network.INetHandler;
import net.minecraft.network.login.server.SCustomPayloadLoginPacket;
import net.minecraft.network.login.server.SDisconnectLoginPacket;
import net.minecraft.network.login.server.SEnableCompressionPacket;
import net.minecraft.network.login.server.SEncryptionRequestPacket;
import net.minecraft.network.login.server.SLoginSuccessPacket;

public interface IClientLoginNetHandler extends INetHandler {
   void handleEncryptionRequest(SEncryptionRequestPacket var1);

   void handleLoginSuccess(SLoginSuccessPacket var1);

   void handleDisconnect(SDisconnectLoginPacket var1);

   void handleEnableCompression(SEnableCompressionPacket var1);

   void handleCustomPayloadLogin(SCustomPayloadLoginPacket var1);
}
