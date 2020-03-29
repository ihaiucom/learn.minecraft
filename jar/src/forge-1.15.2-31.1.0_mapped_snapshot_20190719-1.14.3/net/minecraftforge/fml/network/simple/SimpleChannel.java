package net.minecraftforge.fml.network.simple;

import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkInstance;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Pair;

public class SimpleChannel {
   private final NetworkInstance instance;
   private final IndexedMessageCodec indexedCodec;
   private final Optional<Consumer<NetworkEvent.ChannelRegistrationChangeEvent>> registryChangeConsumer;
   private List<Function<Boolean, ? extends List<? extends Pair<String, ?>>>> loginPackets;

   public SimpleChannel(NetworkInstance instance) {
      this(instance, Optional.empty());
   }

   private SimpleChannel(NetworkInstance instance, Optional<Consumer<NetworkEvent.ChannelRegistrationChangeEvent>> registryChangeNotify) {
      this.instance = instance;
      this.indexedCodec = new IndexedMessageCodec(instance);
      this.loginPackets = new ArrayList();
      instance.addListener(this::networkEventListener);
      instance.addGatherListener(this::networkLoginGather);
      this.registryChangeConsumer = registryChangeNotify;
   }

   public SimpleChannel(NetworkInstance instance, Consumer<NetworkEvent.ChannelRegistrationChangeEvent> registryChangeNotify) {
      this(instance, Optional.of(registryChangeNotify));
   }

   private void networkLoginGather(NetworkEvent.GatherLoginPayloadsEvent gatherEvent) {
      this.loginPackets.forEach((packetGenerator) -> {
         ((List)packetGenerator.apply(gatherEvent.isLocal())).forEach((p) -> {
            PacketBuffer pb = new PacketBuffer(Unpooled.buffer());
            this.indexedCodec.build(p.getRight(), pb);
            gatherEvent.add(pb, this.instance.getChannelName(), (String)p.getLeft());
         });
      });
   }

   private void networkEventListener(NetworkEvent networkEvent) {
      if (networkEvent instanceof NetworkEvent.ChannelRegistrationChangeEvent) {
         this.registryChangeConsumer.ifPresent((l) -> {
            l.accept((NetworkEvent.ChannelRegistrationChangeEvent)networkEvent);
         });
      } else {
         this.indexedCodec.consume(networkEvent.getPayload(), networkEvent.getLoginIndex(), networkEvent.getSource());
      }

   }

   public <MSG> int encodeMessage(MSG message, PacketBuffer target) {
      return this.indexedCodec.build(message, target);
   }

   public <MSG> IndexedMessageCodec.MessageHandler<MSG> registerMessage(int index, Class<MSG> messageType, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
      return this.indexedCodec.addCodecIndex(index, messageType, encoder, decoder, messageConsumer);
   }

   private <MSG> Pair<PacketBuffer, Integer> toBuffer(MSG msg) {
      PacketBuffer bufIn = new PacketBuffer(Unpooled.buffer());
      int index = this.encodeMessage(msg, bufIn);
      return Pair.of(bufIn, index);
   }

   public <MSG> void sendToServer(MSG message) {
      this.sendTo(message, Minecraft.getInstance().getConnection().getNetworkManager(), NetworkDirection.PLAY_TO_SERVER);
   }

   public <MSG> void sendTo(MSG message, NetworkManager manager, NetworkDirection direction) {
      manager.sendPacket(this.toVanillaPacket(message, direction));
   }

   public <MSG> void send(PacketDistributor.PacketTarget target, MSG message) {
      target.send(this.toVanillaPacket(message, target.getDirection()));
   }

   public <MSG> IPacket<?> toVanillaPacket(MSG message, NetworkDirection direction) {
      return direction.buildPacket(this.toBuffer(message), this.instance.getChannelName()).getThis();
   }

   public <MSG> void reply(MSG msgToReply, NetworkEvent.Context context) {
      context.getPacketDispatcher().sendPacket(this.instance.getChannelName(), (PacketBuffer)this.toBuffer(msgToReply).getLeft());
   }

   public <M> SimpleChannel.MessageBuilder<M> messageBuilder(Class<M> type, int id) {
      return SimpleChannel.MessageBuilder.forType(this, type, id);
   }

   public static class MessageBuilder<MSG> {
      private SimpleChannel channel;
      private Class<MSG> type;
      private int id;
      private BiConsumer<MSG, PacketBuffer> encoder;
      private Function<PacketBuffer, MSG> decoder;
      private BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer;
      private Function<MSG, Integer> loginIndexGetter;
      private BiConsumer<MSG, Integer> loginIndexSetter;
      private Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators;

      private static <MSG> SimpleChannel.MessageBuilder<MSG> forType(SimpleChannel channel, Class<MSG> type, int id) {
         SimpleChannel.MessageBuilder<MSG> builder = new SimpleChannel.MessageBuilder();
         builder.channel = channel;
         builder.id = id;
         builder.type = type;
         return builder;
      }

      public SimpleChannel.MessageBuilder<MSG> encoder(BiConsumer<MSG, PacketBuffer> encoder) {
         this.encoder = encoder;
         return this;
      }

      public SimpleChannel.MessageBuilder<MSG> decoder(Function<PacketBuffer, MSG> decoder) {
         this.decoder = decoder;
         return this;
      }

      public SimpleChannel.MessageBuilder<MSG> loginIndex(Function<MSG, Integer> loginIndexGetter, BiConsumer<MSG, Integer> loginIndexSetter) {
         this.loginIndexGetter = loginIndexGetter;
         this.loginIndexSetter = loginIndexSetter;
         return this;
      }

      public SimpleChannel.MessageBuilder<MSG> buildLoginPacketList(Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators) {
         this.loginPacketGenerators = loginPacketGenerators;
         return this;
      }

      public SimpleChannel.MessageBuilder<MSG> markAsLoginPacket() {
         this.loginPacketGenerators = (isLocal) -> {
            try {
               return Collections.singletonList(Pair.of(this.type.getName(), this.type.newInstance()));
            } catch (IllegalAccessException | InstantiationException var3) {
               throw new RuntimeException("Inaccessible no-arg constructor for message " + this.type.getName(), var3);
            }
         };
         return this;
      }

      public SimpleChannel.MessageBuilder<MSG> consumer(BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer) {
         this.consumer = consumer;
         return this;
      }

      public SimpleChannel.MessageBuilder<MSG> consumer(SimpleChannel.MessageBuilder.ToBooleanBiFunction<MSG, Supplier<NetworkEvent.Context>> handler) {
         this.consumer = (msg, ctx) -> {
            boolean handled = handler.applyAsBool(msg, ctx);
            ((NetworkEvent.Context)ctx.get()).setPacketHandled(handled);
         };
         return this;
      }

      public void add() {
         IndexedMessageCodec.MessageHandler<MSG> message = this.channel.registerMessage(this.id, this.type, this.encoder, this.decoder, this.consumer);
         if (this.loginIndexSetter != null) {
            message.setLoginIndexSetter(this.loginIndexSetter);
         }

         if (this.loginIndexGetter != null) {
            if (!IntSupplier.class.isAssignableFrom(this.type)) {
               throw new IllegalArgumentException("Login packet type that does not supply an index as an IntSupplier");
            }

            message.setLoginIndexGetter(this.loginIndexGetter);
         }

         if (this.loginPacketGenerators != null) {
            this.channel.loginPackets.add(this.loginPacketGenerators);
         }

      }

      public interface ToBooleanBiFunction<T, U> {
         boolean applyAsBool(T var1, U var2);
      }
   }
}
