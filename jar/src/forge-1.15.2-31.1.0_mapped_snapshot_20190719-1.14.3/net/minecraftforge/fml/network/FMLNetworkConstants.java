package net.minecraftforge.fml.network;

import io.netty.util.AttributeKey;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class FMLNetworkConstants {
   public static final String FMLNETMARKER = "FML";
   public static final int FMLNETVERSION = 2;
   public static final String NETVERSION = "FML2";
   public static final String NOVERSION = "NONE";
   static final Marker NETWORK = MarkerManager.getMarker("FMLNETWORK");
   static final AttributeKey<String> FML_NETVERSION = AttributeKey.valueOf("fml:netversion");
   static final AttributeKey<FMLHandshakeHandler> FML_HANDSHAKE_HANDLER = AttributeKey.valueOf("fml:handshake");
   static final AttributeKey<FMLMCRegisterPacketHandler.ChannelList> FML_MC_REGISTRY = AttributeKey.valueOf("minecraft:netregistry");
   static final ResourceLocation FML_HANDSHAKE_RESOURCE = new ResourceLocation("fml:handshake");
   static final ResourceLocation FML_PLAY_RESOURCE = new ResourceLocation("fml:play");
   static final ResourceLocation MC_REGISTER_RESOURCE = new ResourceLocation("minecraft:register");
   static final ResourceLocation MC_UNREGISTER_RESOURCE = new ResourceLocation("minecraft:unregister");
   static final SimpleChannel handshakeChannel = NetworkInitialization.getHandshakeChannel();
   static final SimpleChannel playChannel = NetworkInitialization.getPlayChannel();
   static final List<EventNetworkChannel> mcRegChannels = NetworkInitialization.buildMCRegistrationChannels();
   public static final String IGNORESERVERONLY = "OHNOES\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31";

   public static String init() {
      return "FML2";
   }
}
