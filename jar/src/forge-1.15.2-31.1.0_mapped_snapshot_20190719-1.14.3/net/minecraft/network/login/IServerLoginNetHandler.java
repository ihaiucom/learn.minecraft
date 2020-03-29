package net.minecraft.network.login;

import net.minecraft.network.INetHandler;
import net.minecraft.network.login.client.CCustomPayloadLoginPacket;
import net.minecraft.network.login.client.CEncryptionResponsePacket;
import net.minecraft.network.login.client.CLoginStartPacket;

public interface IServerLoginNetHandler extends INetHandler {
   void processLoginStart(CLoginStartPacket var1);

   void processEncryptionResponse(CEncryptionResponsePacket var1);

   void processCustomPayloadLogin(CCustomPayloadLoginPacket var1);
}
