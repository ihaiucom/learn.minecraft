package net.minecraftforge.fml.network;

import java.util.Arrays;
import java.util.List;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.RegistryManager;

class NetworkInitialization {
   public static SimpleChannel getHandshakeChannel() {
      SimpleChannel handshakeChannel = NetworkRegistry.ChannelBuilder.named(FMLNetworkConstants.FML_HANDSHAKE_RESOURCE).clientAcceptedVersions((a) -> {
         return true;
      }).serverAcceptedVersions((a) -> {
         return true;
      }).networkProtocolVersion(() -> {
         return "FML2";
      }).simpleChannel();
      handshakeChannel.messageBuilder(FMLHandshakeMessages.C2SAcknowledge.class, 99).loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).decoder(FMLHandshakeMessages.C2SAcknowledge::decode).encoder(FMLHandshakeMessages.C2SAcknowledge::encode).consumer(FMLHandshakeHandler.indexFirst(FMLHandshakeHandler::handleClientAck)).add();
      handshakeChannel.messageBuilder(FMLHandshakeMessages.S2CModList.class, 1).loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).decoder(FMLHandshakeMessages.S2CModList::decode).encoder(FMLHandshakeMessages.S2CModList::encode).markAsLoginPacket().consumer(FMLHandshakeHandler.biConsumerFor(FMLHandshakeHandler::handleServerModListOnClient)).add();
      handshakeChannel.messageBuilder(FMLHandshakeMessages.C2SModListReply.class, 2).loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).decoder(FMLHandshakeMessages.C2SModListReply::decode).encoder(FMLHandshakeMessages.C2SModListReply::encode).consumer(FMLHandshakeHandler.indexFirst(FMLHandshakeHandler::handleClientModListOnServer)).add();
      handshakeChannel.messageBuilder(FMLHandshakeMessages.S2CRegistry.class, 3).loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).decoder(FMLHandshakeMessages.S2CRegistry::decode).encoder(FMLHandshakeMessages.S2CRegistry::encode).buildLoginPacketList(RegistryManager::generateRegistryPackets).consumer(FMLHandshakeHandler.biConsumerFor(FMLHandshakeHandler::handleRegistryMessage)).add();
      SimpleChannel.MessageBuilder var10000 = handshakeChannel.messageBuilder(FMLHandshakeMessages.S2CConfigData.class, 4).loginIndex(FMLHandshakeMessages.LoginIndexedMessage::getLoginIndex, FMLHandshakeMessages.LoginIndexedMessage::setLoginIndex).decoder(FMLHandshakeMessages.S2CConfigData::decode).encoder(FMLHandshakeMessages.S2CConfigData::encode);
      ConfigTracker var10001 = ConfigTracker.INSTANCE;
      var10001.getClass();
      var10000.buildLoginPacketList(var10001::syncConfigs).consumer(FMLHandshakeHandler.biConsumerFor(FMLHandshakeHandler::handleConfigSync)).add();
      return handshakeChannel;
   }

   public static SimpleChannel getPlayChannel() {
      SimpleChannel playChannel = NetworkRegistry.ChannelBuilder.named(FMLNetworkConstants.FML_PLAY_RESOURCE).clientAcceptedVersions((a) -> {
         return true;
      }).serverAcceptedVersions((a) -> {
         return true;
      }).networkProtocolVersion(() -> {
         return "FML2";
      }).simpleChannel();
      playChannel.messageBuilder(FMLPlayMessages.SpawnEntity.class, 0).decoder(FMLPlayMessages.SpawnEntity::decode).encoder(FMLPlayMessages.SpawnEntity::encode).consumer(FMLPlayMessages.SpawnEntity::handle).add();
      playChannel.messageBuilder(FMLPlayMessages.OpenContainer.class, 1).decoder(FMLPlayMessages.OpenContainer::decode).encoder(FMLPlayMessages.OpenContainer::encode).consumer(FMLPlayMessages.OpenContainer::handle).add();
      playChannel.messageBuilder(FMLPlayMessages.DimensionInfoMessage.class, 2).decoder(FMLPlayMessages.DimensionInfoMessage::decode).encoder(FMLPlayMessages.DimensionInfoMessage::encode).consumer(FMLPlayMessages.DimensionInfoMessage::handle).add();
      return playChannel;
   }

   public static List<EventNetworkChannel> buildMCRegistrationChannels() {
      EventNetworkChannel mcRegChannel = NetworkRegistry.ChannelBuilder.named(FMLNetworkConstants.MC_REGISTER_RESOURCE).clientAcceptedVersions((a) -> {
         return true;
      }).serverAcceptedVersions((a) -> {
         return true;
      }).networkProtocolVersion(() -> {
         return "FML2";
      }).eventNetworkChannel();
      FMLMCRegisterPacketHandler var10001 = FMLMCRegisterPacketHandler.INSTANCE;
      mcRegChannel.addListener(var10001::registerListener);
      EventNetworkChannel mcUnregChannel = NetworkRegistry.ChannelBuilder.named(FMLNetworkConstants.MC_UNREGISTER_RESOURCE).clientAcceptedVersions((a) -> {
         return true;
      }).serverAcceptedVersions((a) -> {
         return true;
      }).networkProtocolVersion(() -> {
         return "FML2";
      }).eventNetworkChannel();
      var10001 = FMLMCRegisterPacketHandler.INSTANCE;
      mcUnregChannel.addListener(var10001::unregisterListener);
      return Arrays.asList(mcRegChannel, mcUnregChannel);
   }
}
