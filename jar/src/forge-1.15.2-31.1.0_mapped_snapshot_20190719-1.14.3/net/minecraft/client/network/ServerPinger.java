package net.minecraft.client.network;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.status.IClientStatusNetHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.status.client.CPingPacket;
import net.minecraft.network.status.client.CServerQueryPacket;
import net.minecraft.network.status.server.SPongPacket;
import net.minecraft.network.status.server.SServerInfoPacket;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.ClientHooks;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ServerPinger {
   private static final Splitter PING_RESPONSE_SPLITTER = Splitter.on('\u0000').limit(6);
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<NetworkManager> pingDestinations = Collections.synchronizedList(Lists.newArrayList());

   public void ping(final ServerData p_147224_1_) throws UnknownHostException {
      ServerAddress serveraddress = ServerAddress.fromString(p_147224_1_.serverIP);
      final NetworkManager networkmanager = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(serveraddress.getIP()), serveraddress.getPort(), false);
      this.pingDestinations.add(networkmanager);
      p_147224_1_.serverMOTD = I18n.format("multiplayer.status.pinging");
      p_147224_1_.pingToServer = -1L;
      p_147224_1_.playerList = null;
      networkmanager.setNetHandler(new IClientStatusNetHandler() {
         private boolean successful;
         private boolean receivedStatus;
         private long pingSentAt;

         public void handleServerInfo(SServerInfoPacket p_147397_1_) {
            if (this.receivedStatus) {
               networkmanager.closeChannel(new TranslationTextComponent("multiplayer.status.unrequested", new Object[0]));
            } else {
               this.receivedStatus = true;
               ServerStatusResponse serverstatusresponse = p_147397_1_.getResponse();
               if (serverstatusresponse.getServerDescription() != null) {
                  p_147224_1_.serverMOTD = serverstatusresponse.getServerDescription().getFormattedText();
               } else {
                  p_147224_1_.serverMOTD = "";
               }

               if (serverstatusresponse.getVersion() != null) {
                  p_147224_1_.gameVersion = serverstatusresponse.getVersion().getName();
                  p_147224_1_.version = serverstatusresponse.getVersion().getProtocol();
               } else {
                  p_147224_1_.gameVersion = I18n.format("multiplayer.status.old");
                  p_147224_1_.version = 0;
               }

               if (serverstatusresponse.getPlayers() == null) {
                  p_147224_1_.populationInfo = TextFormatting.DARK_GRAY + I18n.format("multiplayer.status.unknown");
               } else {
                  p_147224_1_.populationInfo = TextFormatting.GRAY + "" + serverstatusresponse.getPlayers().getOnlinePlayerCount() + "" + TextFormatting.DARK_GRAY + "/" + TextFormatting.GRAY + serverstatusresponse.getPlayers().getMaxPlayers();
                  if (ArrayUtils.isNotEmpty(serverstatusresponse.getPlayers().getPlayers())) {
                     StringBuilder stringbuilder = new StringBuilder();
                     GameProfile[] var4 = serverstatusresponse.getPlayers().getPlayers();
                     int var5 = var4.length;

                     for(int var6 = 0; var6 < var5; ++var6) {
                        GameProfile gameprofile = var4[var6];
                        if (stringbuilder.length() > 0) {
                           stringbuilder.append("\n");
                        }

                        stringbuilder.append(gameprofile.getName());
                     }

                     if (serverstatusresponse.getPlayers().getPlayers().length < serverstatusresponse.getPlayers().getOnlinePlayerCount()) {
                        if (stringbuilder.length() > 0) {
                           stringbuilder.append("\n");
                        }

                        stringbuilder.append(I18n.format("multiplayer.status.and_more", serverstatusresponse.getPlayers().getOnlinePlayerCount() - serverstatusresponse.getPlayers().getPlayers().length));
                     }

                     p_147224_1_.playerList = stringbuilder.toString();
                  }
               }

               if (serverstatusresponse.getFavicon() != null) {
                  String s = serverstatusresponse.getFavicon();
                  if (s.startsWith("data:image/png;base64,")) {
                     p_147224_1_.setBase64EncodedIconData(s.substring("data:image/png;base64,".length()));
                  } else {
                     ServerPinger.LOGGER.error("Invalid server icon (unknown format)");
                  }
               } else {
                  p_147224_1_.setBase64EncodedIconData((String)null);
               }

               ClientHooks.processForgeListPingData(serverstatusresponse, p_147224_1_);
               this.pingSentAt = Util.milliTime();
               networkmanager.sendPacket(new CPingPacket(this.pingSentAt));
               this.successful = true;
            }

         }

         public void handlePong(SPongPacket p_147398_1_) {
            long i = this.pingSentAt;
            long j = Util.milliTime();
            p_147224_1_.pingToServer = j - i;
            networkmanager.closeChannel(new TranslationTextComponent("multiplayer.status.finished", new Object[0]));
         }

         public void onDisconnect(ITextComponent p_147231_1_) {
            if (!this.successful) {
               ServerPinger.LOGGER.error("Can't ping {}: {}", p_147224_1_.serverIP, p_147231_1_.getString());
               p_147224_1_.serverMOTD = TextFormatting.DARK_RED + I18n.format("multiplayer.status.cannot_connect");
               p_147224_1_.populationInfo = "";
               ServerPinger.this.tryCompatibilityPing(p_147224_1_);
            }

         }

         public NetworkManager getNetworkManager() {
            return networkmanager;
         }
      });

      try {
         networkmanager.sendPacket(new CHandshakePacket(serveraddress.getIP(), serveraddress.getPort(), ProtocolType.STATUS));
         networkmanager.sendPacket(new CServerQueryPacket());
      } catch (Throwable var5) {
         LOGGER.error(var5);
      }

   }

   private void tryCompatibilityPing(final ServerData p_147225_1_) {
      final ServerAddress serveraddress = ServerAddress.fromString(p_147225_1_.serverIP);
      ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group((EventLoopGroup)NetworkManager.CLIENT_NIO_EVENTLOOP.getValue())).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel p_initChannel_1_) throws Exception {
            try {
               p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, true);
            } catch (ChannelException var3) {
            }

            p_initChannel_1_.pipeline().addLast(new ChannelHandler[]{new SimpleChannelInboundHandler<ByteBuf>() {
               public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {
                  super.channelActive(p_channelActive_1_);
                  ByteBuf bytebuf = Unpooled.buffer();

                  try {
                     bytebuf.writeByte(254);
                     bytebuf.writeByte(1);
                     bytebuf.writeByte(250);
                     char[] achar = "MC|PingHost".toCharArray();
                     bytebuf.writeShort(achar.length);
                     char[] var4 = achar;
                     int var5 = achar.length;

                     int var6;
                     char c1;
                     for(var6 = 0; var6 < var5; ++var6) {
                        c1 = var4[var6];
                        bytebuf.writeChar(c1);
                     }

                     bytebuf.writeShort(7 + 2 * serveraddress.getIP().length());
                     bytebuf.writeByte(127);
                     achar = serveraddress.getIP().toCharArray();
                     bytebuf.writeShort(achar.length);
                     var4 = achar;
                     var5 = achar.length;

                     for(var6 = 0; var6 < var5; ++var6) {
                        c1 = var4[var6];
                        bytebuf.writeChar(c1);
                     }

                     bytebuf.writeInt(serveraddress.getPort());
                     p_channelActive_1_.channel().writeAndFlush(bytebuf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                  } finally {
                     bytebuf.release();
                  }
               }

               protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, ByteBuf p_channelRead0_2_) throws Exception {
                  short short1 = p_channelRead0_2_.readUnsignedByte();
                  if (short1 == 255) {
                     String s = new String(p_channelRead0_2_.readBytes(p_channelRead0_2_.readShort() * 2).array(), StandardCharsets.UTF_16BE);
                     String[] astring = (String[])Iterables.toArray(ServerPinger.PING_RESPONSE_SPLITTER.split(s), String.class);
                     if ("§1".equals(astring[0])) {
                        int i = MathHelper.getInt(astring[1], 0);
                        String s1 = astring[2];
                        String s2 = astring[3];
                        int j = MathHelper.getInt(astring[4], -1);
                        int k = MathHelper.getInt(astring[5], -1);
                        p_147225_1_.version = -1;
                        p_147225_1_.gameVersion = s1;
                        p_147225_1_.serverMOTD = s2;
                        p_147225_1_.populationInfo = TextFormatting.GRAY + "" + j + "" + TextFormatting.DARK_GRAY + "/" + TextFormatting.GRAY + k;
                     }
                  }

                  p_channelRead0_1_.close();
               }

               public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) throws Exception {
                  p_exceptionCaught_1_.close();
               }
            }});
         }
      })).channel(NioSocketChannel.class)).connect(serveraddress.getIP(), serveraddress.getPort());
   }

   public void pingPendingNetworks() {
      synchronized(this.pingDestinations) {
         Iterator iterator = this.pingDestinations.iterator();

         while(iterator.hasNext()) {
            NetworkManager networkmanager = (NetworkManager)iterator.next();
            if (networkmanager.isChannelOpen()) {
               networkmanager.tick();
            } else {
               iterator.remove();
               networkmanager.handleDisconnection();
            }
         }

      }
   }

   public void clearPendingNetworks() {
      synchronized(this.pingDestinations) {
         Iterator iterator = this.pingDestinations.iterator();

         while(iterator.hasNext()) {
            NetworkManager networkmanager = (NetworkManager)iterator.next();
            if (networkmanager.isChannelOpen()) {
               iterator.remove();
               networkmanager.closeChannel(new TranslationTextComponent("multiplayer.status.cancelled", new Object[0]));
            }
         }

      }
   }
}
