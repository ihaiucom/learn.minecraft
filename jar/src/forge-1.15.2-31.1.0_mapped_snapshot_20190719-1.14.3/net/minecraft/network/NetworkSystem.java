package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.network.handshake.ClientHandshakeNetHandler;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.network.handshake.ServerHandshakeNetHandler;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LazyValue;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkSystem {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int READ_TIMEOUT = Integer.parseInt(System.getProperty("forge.readTimeout", "30"));
   public static final LazyValue<NioEventLoopGroup> SERVER_NIO_EVENTLOOP = new LazyValue(() -> {
      return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).setThreadFactory(SidedThreadGroups.SERVER).build());
   });
   public static final LazyValue<EpollEventLoopGroup> SERVER_EPOLL_EVENTLOOP = new LazyValue(() -> {
      return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).setThreadFactory(SidedThreadGroups.SERVER).build());
   });
   private final MinecraftServer server;
   public volatile boolean isAlive;
   private final List<ChannelFuture> endpoints = Collections.synchronizedList(Lists.newArrayList());
   private final List<NetworkManager> networkManagers = Collections.synchronizedList(Lists.newArrayList());

   public NetworkSystem(MinecraftServer p_i45292_1_) {
      this.server = p_i45292_1_;
      this.isAlive = true;
   }

   public void addEndpoint(@Nullable InetAddress p_151265_1_, int p_151265_2_) throws IOException {
      if (p_151265_1_ instanceof Inet6Address) {
         System.setProperty("java.net.preferIPv4Stack", "false");
      }

      synchronized(this.endpoints) {
         Class oclass;
         LazyValue lazyvalue;
         if (Epoll.isAvailable() && this.server.shouldUseNativeTransport()) {
            oclass = EpollServerSocketChannel.class;
            lazyvalue = SERVER_EPOLL_EVENTLOOP;
            LOGGER.info("Using epoll channel type");
         } else {
            oclass = NioServerSocketChannel.class;
            lazyvalue = SERVER_NIO_EVENTLOOP;
            LOGGER.info("Using default channel type");
         }

         this.endpoints.add(((ServerBootstrap)((ServerBootstrap)(new ServerBootstrap()).channel(oclass)).childHandler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel p_initChannel_1_) throws Exception {
               try {
                  p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, true);
               } catch (ChannelException var3) {
               }

               p_initChannel_1_.pipeline().addLast("timeout", new ReadTimeoutHandler(NetworkSystem.READ_TIMEOUT)).addLast("legacy_query", new LegacyPingHandler(NetworkSystem.this)).addLast("splitter", new NettyVarint21FrameDecoder()).addLast("decoder", new NettyPacketDecoder(PacketDirection.SERVERBOUND)).addLast("prepender", new NettyVarint21FrameEncoder()).addLast("encoder", new NettyPacketEncoder(PacketDirection.CLIENTBOUND));
               NetworkManager networkmanager = new NetworkManager(PacketDirection.SERVERBOUND);
               NetworkSystem.this.networkManagers.add(networkmanager);
               p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
               networkmanager.setNetHandler(new ServerHandshakeNetHandler(NetworkSystem.this.server, networkmanager));
            }
         }).group((EventLoopGroup)lazyvalue.getValue()).localAddress(p_151265_1_, p_151265_2_)).bind().syncUninterruptibly());
      }
   }

   @OnlyIn(Dist.CLIENT)
   public SocketAddress addLocalEndpoint() {
      ChannelFuture channelfuture;
      synchronized(this.endpoints) {
         channelfuture = ((ServerBootstrap)((ServerBootstrap)(new ServerBootstrap()).channel(LocalServerChannel.class)).childHandler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel p_initChannel_1_) throws Exception {
               NetworkManager networkmanager = new NetworkManager(PacketDirection.SERVERBOUND);
               networkmanager.setNetHandler(new ClientHandshakeNetHandler(NetworkSystem.this.server, networkmanager));
               NetworkSystem.this.networkManagers.add(networkmanager);
               p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
            }
         }).group((EventLoopGroup)SERVER_NIO_EVENTLOOP.getValue()).localAddress(LocalAddress.ANY)).bind().syncUninterruptibly();
         this.endpoints.add(channelfuture);
      }

      return channelfuture.channel().localAddress();
   }

   public void terminateEndpoints() {
      this.isAlive = false;
      Iterator var1 = this.endpoints.iterator();

      while(var1.hasNext()) {
         ChannelFuture channelfuture = (ChannelFuture)var1.next();

         try {
            channelfuture.channel().close().sync();
         } catch (InterruptedException var4) {
            LOGGER.error("Interrupted whilst closing channel");
         }
      }

   }

   public void tick() {
      synchronized(this.networkManagers) {
         Iterator iterator = this.networkManagers.iterator();

         while(true) {
            while(true) {
               NetworkManager networkmanager;
               do {
                  if (!iterator.hasNext()) {
                     return;
                  }

                  networkmanager = (NetworkManager)iterator.next();
               } while(networkmanager.hasNoChannel());

               if (networkmanager.isChannelOpen()) {
                  try {
                     networkmanager.tick();
                  } catch (Exception var8) {
                     if (networkmanager.isLocalChannel()) {
                        CrashReport crashreport = CrashReport.makeCrashReport(var8, "Ticking memory connection");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Ticking connection");
                        crashreportcategory.addDetail("Connection", networkmanager::toString);
                        throw new ReportedException(crashreport);
                     }

                     LOGGER.warn("Failed to handle packet for {}", networkmanager.getRemoteAddress(), var8);
                     ITextComponent itextcomponent = new StringTextComponent("Internal server error");
                     networkmanager.sendPacket(new SDisconnectPacket(itextcomponent), (p_lambda$tick$2_2_) -> {
                        networkmanager.closeChannel(itextcomponent);
                     });
                     networkmanager.disableAutoRead();
                  }
               } else {
                  iterator.remove();
                  networkmanager.handleDisconnection();
               }
            }
         }
      }
   }

   public MinecraftServer getServer() {
      return this.server;
   }
}
