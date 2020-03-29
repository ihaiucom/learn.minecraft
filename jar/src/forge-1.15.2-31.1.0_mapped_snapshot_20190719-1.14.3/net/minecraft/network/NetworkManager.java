package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.util.CryptManager;
import net.minecraft.util.LazyValue;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NetworkManager extends SimpleChannelInboundHandler<IPacket<?>> {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Marker NETWORK_MARKER = MarkerManager.getMarker("NETWORK");
   public static final Marker NETWORK_PACKETS_MARKER;
   public static final AttributeKey<ProtocolType> PROTOCOL_ATTRIBUTE_KEY;
   public static final LazyValue<NioEventLoopGroup> CLIENT_NIO_EVENTLOOP;
   public static final LazyValue<EpollEventLoopGroup> CLIENT_EPOLL_EVENTLOOP;
   public static final LazyValue<DefaultEventLoopGroup> CLIENT_LOCAL_EVENTLOOP;
   private final PacketDirection direction;
   private final Queue<NetworkManager.QueuedPacket> outboundPacketsQueue = Queues.newConcurrentLinkedQueue();
   private Channel channel;
   private SocketAddress socketAddress;
   private INetHandler packetListener;
   private ITextComponent terminationReason;
   private boolean isEncrypted;
   private boolean disconnected;
   private int field_211394_q;
   private int field_211395_r;
   private float field_211396_s;
   private float field_211397_t;
   private int ticks;
   private boolean field_211399_v;
   private Consumer<NetworkManager> activationHandler;

   public NetworkManager(PacketDirection p_i46004_1_) {
      this.direction = p_i46004_1_;
   }

   public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception {
      super.channelActive(p_channelActive_1_);
      this.channel = p_channelActive_1_.channel();
      this.socketAddress = this.channel.remoteAddress();
      if (this.activationHandler != null) {
         this.activationHandler.accept(this);
      }

      try {
         this.setConnectionState(ProtocolType.HANDSHAKING);
      } catch (Throwable var3) {
         LOGGER.fatal(var3);
      }

   }

   public void setConnectionState(ProtocolType p_150723_1_) {
      this.channel.attr(PROTOCOL_ATTRIBUTE_KEY).set(p_150723_1_);
      this.channel.config().setAutoRead(true);
      LOGGER.debug("Enabled auto read");
   }

   public void channelInactive(ChannelHandlerContext p_channelInactive_1_) throws Exception {
      this.closeChannel(new TranslationTextComponent("disconnect.endOfStream", new Object[0]));
   }

   public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) {
      if (p_exceptionCaught_2_ instanceof SkipableEncoderException) {
         LOGGER.debug("Skipping packet due to errors", p_exceptionCaught_2_.getCause());
      } else {
         boolean flag = !this.field_211399_v;
         this.field_211399_v = true;
         if (this.channel.isOpen()) {
            if (p_exceptionCaught_2_ instanceof TimeoutException) {
               LOGGER.debug("Timeout", p_exceptionCaught_2_);
               this.closeChannel(new TranslationTextComponent("disconnect.timeout", new Object[0]));
            } else {
               ITextComponent itextcomponent = new TranslationTextComponent("disconnect.genericReason", new Object[]{"Internal Exception: " + p_exceptionCaught_2_});
               if (flag) {
                  LOGGER.debug("Failed to sent packet", p_exceptionCaught_2_);
                  this.sendPacket(new SDisconnectPacket(itextcomponent), (p_lambda$exceptionCaught$3_2_) -> {
                     this.closeChannel(itextcomponent);
                  });
                  this.disableAutoRead();
               } else {
                  LOGGER.debug("Double fault", p_exceptionCaught_2_);
                  this.closeChannel(itextcomponent);
               }
            }
         }
      }

   }

   protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, IPacket<?> p_channelRead0_2_) throws Exception {
      if (this.channel.isOpen()) {
         try {
            processPacket(p_channelRead0_2_, this.packetListener);
         } catch (ThreadQuickExitException var4) {
         }

         ++this.field_211394_q;
      }

   }

   private static <T extends INetHandler> void processPacket(IPacket<T> p_197664_0_, INetHandler p_197664_1_) {
      p_197664_0_.processPacket(p_197664_1_);
   }

   public void setNetHandler(INetHandler p_150719_1_) {
      Validate.notNull(p_150719_1_, "packetListener", new Object[0]);
      LOGGER.debug("Set listener of {} to {}", this, p_150719_1_);
      this.packetListener = p_150719_1_;
   }

   public void sendPacket(IPacket<?> p_179290_1_) {
      this.sendPacket(p_179290_1_, (GenericFutureListener)null);
   }

   public void sendPacket(IPacket<?> p_201058_1_, @Nullable GenericFutureListener<? extends Future<? super Void>> p_201058_2_) {
      if (this.isChannelOpen()) {
         this.flushOutboundQueue();
         this.dispatchPacket(p_201058_1_, p_201058_2_);
      } else {
         this.outboundPacketsQueue.add(new NetworkManager.QueuedPacket(p_201058_1_, p_201058_2_));
      }

   }

   private void dispatchPacket(IPacket<?> p_150732_1_, @Nullable GenericFutureListener<? extends Future<? super Void>> p_150732_2_) {
      ProtocolType protocoltype = ProtocolType.getFromPacket(p_150732_1_);
      ProtocolType protocoltype1 = (ProtocolType)this.channel.attr(PROTOCOL_ATTRIBUTE_KEY).get();
      ++this.field_211395_r;
      if (protocoltype1 != protocoltype) {
         LOGGER.debug("Disabled auto read");
         this.channel.eventLoop().execute(() -> {
            this.channel.config().setAutoRead(false);
         });
      }

      if (this.channel.eventLoop().inEventLoop()) {
         if (protocoltype != protocoltype1) {
            this.setConnectionState(protocoltype);
         }

         ChannelFuture channelfuture = this.channel.writeAndFlush(p_150732_1_);
         if (p_150732_2_ != null) {
            channelfuture.addListener(p_150732_2_);
         }

         channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      } else {
         this.channel.eventLoop().execute(() -> {
            if (protocoltype != protocoltype1) {
               this.setConnectionState(protocoltype);
            }

            ChannelFuture channelfuture1 = this.channel.writeAndFlush(p_150732_1_);
            if (p_150732_2_ != null) {
               channelfuture1.addListener(p_150732_2_);
            }

            channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
         });
      }

   }

   private void flushOutboundQueue() {
      if (this.channel != null && this.channel.isOpen()) {
         NetworkManager.QueuedPacket networkmanager$queuedpacket;
         synchronized(this.outboundPacketsQueue) {
            while((networkmanager$queuedpacket = (NetworkManager.QueuedPacket)this.outboundPacketsQueue.poll()) != null) {
               this.dispatchPacket(networkmanager$queuedpacket.packet, networkmanager$queuedpacket.field_201049_b);
            }
         }
      }

   }

   public void tick() {
      this.flushOutboundQueue();
      if (this.packetListener instanceof ServerLoginNetHandler) {
         ((ServerLoginNetHandler)this.packetListener).tick();
      }

      if (this.packetListener instanceof ServerPlayNetHandler) {
         ((ServerPlayNetHandler)this.packetListener).tick();
      }

      if (this.channel != null) {
         this.channel.flush();
      }

      if (this.ticks++ % 20 == 0) {
         this.field_211397_t = this.field_211397_t * 0.75F + (float)this.field_211395_r * 0.25F;
         this.field_211396_s = this.field_211396_s * 0.75F + (float)this.field_211394_q * 0.25F;
         this.field_211395_r = 0;
         this.field_211394_q = 0;
      }

   }

   public SocketAddress getRemoteAddress() {
      return this.socketAddress;
   }

   public void closeChannel(ITextComponent p_150718_1_) {
      if (this.channel.isOpen()) {
         this.channel.close().awaitUninterruptibly();
         this.terminationReason = p_150718_1_;
      }

   }

   public boolean isLocalChannel() {
      return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
   }

   @OnlyIn(Dist.CLIENT)
   public static NetworkManager createNetworkManagerAndConnect(InetAddress p_181124_0_, int p_181124_1_, boolean p_181124_2_) {
      if (p_181124_0_ instanceof Inet6Address) {
         System.setProperty("java.net.preferIPv4Stack", "false");
      }

      final NetworkManager networkmanager = new NetworkManager(PacketDirection.CLIENTBOUND);
      networkmanager.activationHandler = NetworkHooks::registerClientLoginChannel;
      Class oclass;
      LazyValue lazyvalue;
      if (Epoll.isAvailable() && p_181124_2_) {
         oclass = EpollSocketChannel.class;
         lazyvalue = CLIENT_EPOLL_EVENTLOOP;
      } else {
         oclass = NioSocketChannel.class;
         lazyvalue = CLIENT_NIO_EVENTLOOP;
      }

      ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group((EventLoopGroup)lazyvalue.getValue())).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel p_initChannel_1_) throws Exception {
            try {
               p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, true);
            } catch (ChannelException var3) {
            }

            p_initChannel_1_.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("splitter", new NettyVarint21FrameDecoder()).addLast("decoder", new NettyPacketDecoder(PacketDirection.CLIENTBOUND)).addLast("prepender", new NettyVarint21FrameEncoder()).addLast("encoder", new NettyPacketEncoder(PacketDirection.SERVERBOUND)).addLast("packet_handler", networkmanager);
         }
      })).channel(oclass)).connect(p_181124_0_, p_181124_1_).syncUninterruptibly();
      return networkmanager;
   }

   @OnlyIn(Dist.CLIENT)
   public static NetworkManager provideLocalClient(SocketAddress p_150722_0_) {
      final NetworkManager networkmanager = new NetworkManager(PacketDirection.CLIENTBOUND);
      networkmanager.activationHandler = NetworkHooks::registerClientLoginChannel;
      ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group((EventLoopGroup)CLIENT_LOCAL_EVENTLOOP.getValue())).handler(new ChannelInitializer<Channel>() {
         protected void initChannel(Channel p_initChannel_1_) throws Exception {
            p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
         }
      })).channel(LocalChannel.class)).connect(p_150722_0_).syncUninterruptibly();
      return networkmanager;
   }

   public void enableEncryption(SecretKey p_150727_1_) {
      this.isEncrypted = true;
      this.channel.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.createNetCipherInstance(2, p_150727_1_)));
      this.channel.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.createNetCipherInstance(1, p_150727_1_)));
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isEncrypted() {
      return this.isEncrypted;
   }

   public boolean isChannelOpen() {
      return this.channel != null && this.channel.isOpen();
   }

   public boolean hasNoChannel() {
      return this.channel == null;
   }

   public INetHandler getNetHandler() {
      return this.packetListener;
   }

   @Nullable
   public ITextComponent getExitMessage() {
      return this.terminationReason;
   }

   public void disableAutoRead() {
      this.channel.config().setAutoRead(false);
   }

   public void setCompressionThreshold(int p_179289_1_) {
      if (p_179289_1_ >= 0) {
         if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
            ((NettyCompressionDecoder)this.channel.pipeline().get("decompress")).setCompressionThreshold(p_179289_1_);
         } else {
            this.channel.pipeline().addBefore("decoder", "decompress", new NettyCompressionDecoder(p_179289_1_));
         }

         if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
            ((NettyCompressionEncoder)this.channel.pipeline().get("compress")).setCompressionThreshold(p_179289_1_);
         } else {
            this.channel.pipeline().addBefore("encoder", "compress", new NettyCompressionEncoder(p_179289_1_));
         }
      } else {
         if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
            this.channel.pipeline().remove("decompress");
         }

         if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
            this.channel.pipeline().remove("compress");
         }
      }

   }

   public void handleDisconnection() {
      if (this.channel != null && !this.channel.isOpen()) {
         if (this.disconnected) {
            LOGGER.warn("handleDisconnection() called twice");
         } else {
            this.disconnected = true;
            if (this.getExitMessage() != null) {
               this.getNetHandler().onDisconnect(this.getExitMessage());
            } else if (this.getNetHandler() != null) {
               this.getNetHandler().onDisconnect(new TranslationTextComponent("multiplayer.disconnect.generic", new Object[0]));
            }
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getPacketsReceived() {
      return this.field_211396_s;
   }

   @OnlyIn(Dist.CLIENT)
   public float getPacketsSent() {
      return this.field_211397_t;
   }

   public Channel channel() {
      return this.channel;
   }

   public PacketDirection getDirection() {
      return this.direction;
   }

   static {
      NETWORK_PACKETS_MARKER = MarkerManager.getMarker("NETWORK_PACKETS", NETWORK_MARKER);
      PROTOCOL_ATTRIBUTE_KEY = AttributeKey.valueOf("protocol");
      CLIENT_NIO_EVENTLOOP = new LazyValue(() -> {
         return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
      });
      CLIENT_EPOLL_EVENTLOOP = new LazyValue(() -> {
         return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
      });
      CLIENT_LOCAL_EVENTLOOP = new LazyValue(() -> {
         return new DefaultEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
      });
   }

   static class QueuedPacket {
      private final IPacket<?> packet;
      @Nullable
      private final GenericFutureListener<? extends Future<? super Void>> field_201049_b;

      public QueuedPacket(IPacket<?> p_i48604_1_, @Nullable GenericFutureListener<? extends Future<? super Void>> p_i48604_2_) {
         this.packet = p_i48604_1_;
         this.field_201049_b = p_i48604_2_;
      }
   }
}
