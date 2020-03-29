package net.minecraftforge.fml.network.simple;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class IndexedMessageCodec {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker SIMPLENET = MarkerManager.getMarker("SIMPLENET");
   private final Short2ObjectArrayMap<IndexedMessageCodec.MessageHandler<?>> indicies;
   private final Object2ObjectArrayMap<Class<?>, IndexedMessageCodec.MessageHandler<?>> types;
   private final NetworkInstance networkInstance;

   public IndexedMessageCodec() {
      this((NetworkInstance)null);
   }

   public IndexedMessageCodec(NetworkInstance instance) {
      this.indicies = new Short2ObjectArrayMap();
      this.types = new Object2ObjectArrayMap();
      this.networkInstance = instance;
   }

   public <MSG> IndexedMessageCodec.MessageHandler<MSG> findMessageType(MSG msgToReply) {
      return (IndexedMessageCodec.MessageHandler)this.types.get(msgToReply.getClass());
   }

   <MSG> IndexedMessageCodec.MessageHandler<MSG> findIndex(short i) {
      return (IndexedMessageCodec.MessageHandler)this.indicies.get(i);
   }

   private static <M> void tryDecode(PacketBuffer payload, Supplier<NetworkEvent.Context> context, int payloadIndex, IndexedMessageCodec.MessageHandler<M> codec) {
      codec.decoder.map((d) -> {
         return d.apply(payload);
      }).map((p) -> {
         if (payloadIndex != Integer.MIN_VALUE) {
            codec.getLoginIndexSetter().ifPresent((f) -> {
               f.accept(p, payloadIndex);
            });
         }

         return p;
      }).ifPresent((m) -> {
         codec.messageConsumer.accept(m, context);
      });
   }

   private static <M> int tryEncode(PacketBuffer target, M message, IndexedMessageCodec.MessageHandler<M> codec) {
      codec.encoder.ifPresent((encoder) -> {
         target.writeByte(codec.index & 255);
         encoder.accept(message, target);
      });
      return (Integer)((Function)codec.loginIndexGetter.orElse((m) -> {
         return Integer.MIN_VALUE;
      })).apply(message);
   }

   public <MSG> int build(MSG message, PacketBuffer target) {
      IndexedMessageCodec.MessageHandler<MSG> messageHandler = (IndexedMessageCodec.MessageHandler)this.types.get(message.getClass());
      if (messageHandler == null) {
         LOGGER.error(SIMPLENET, "Received invalid message {} on channel {}", message.getClass().getName(), Optional.ofNullable(this.networkInstance).map(NetworkInstance::getChannelName).map(Objects::toString).orElse("MISSING CHANNEL"));
         throw new IllegalArgumentException("Invalid message " + message.getClass().getName());
      } else {
         return tryEncode(target, message, messageHandler);
      }
   }

   void consume(PacketBuffer payload, int payloadIndex, Supplier<NetworkEvent.Context> context) {
      if (payload == null) {
         LOGGER.error(SIMPLENET, "Received empty payload on channel {}", Optional.ofNullable(this.networkInstance).map(NetworkInstance::getChannelName).map(Objects::toString).orElse("MISSING CHANNEL"));
      } else {
         short discriminator = payload.readUnsignedByte();
         IndexedMessageCodec.MessageHandler<?> messageHandler = (IndexedMessageCodec.MessageHandler)this.indicies.get(discriminator);
         if (messageHandler == null) {
            LOGGER.error(SIMPLENET, "Received invalid discriminator byte {} on channel {}", discriminator, Optional.ofNullable(this.networkInstance).map(NetworkInstance::getChannelName).map(Objects::toString).orElse("MISSING CHANNEL"));
         } else {
            tryDecode(payload, context, payloadIndex, messageHandler);
         }
      }
   }

   <MSG> IndexedMessageCodec.MessageHandler<MSG> addCodecIndex(int index, Class<MSG> messageType, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
      return new IndexedMessageCodec.MessageHandler(index, messageType, encoder, decoder, messageConsumer);
   }

   class MessageHandler<MSG> {
      private final Optional<BiConsumer<MSG, PacketBuffer>> encoder;
      private final Optional<Function<PacketBuffer, MSG>> decoder;
      private final int index;
      private final BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer;
      private final Class<MSG> messageType;
      private Optional<BiConsumer<MSG, Integer>> loginIndexSetter;
      private Optional<Function<MSG, Integer>> loginIndexGetter;

      public MessageHandler(int index, Class<MSG> messageType, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
         this.index = index;
         this.messageType = messageType;
         this.encoder = Optional.ofNullable(encoder);
         this.decoder = Optional.ofNullable(decoder);
         this.messageConsumer = messageConsumer;
         this.loginIndexGetter = Optional.empty();
         this.loginIndexSetter = Optional.empty();
         IndexedMessageCodec.this.indicies.put((short)(index & 255), this);
         IndexedMessageCodec.this.types.put(messageType, this);
      }

      void setLoginIndexSetter(BiConsumer<MSG, Integer> loginIndexSetter) {
         this.loginIndexSetter = Optional.of(loginIndexSetter);
      }

      Optional<BiConsumer<MSG, Integer>> getLoginIndexSetter() {
         return this.loginIndexSetter;
      }

      void setLoginIndexGetter(Function<MSG, Integer> loginIndexGetter) {
         this.loginIndexGetter = Optional.of(loginIndexGetter);
      }

      public Optional<Function<MSG, Integer>> getLoginIndexGetter() {
         return this.loginIndexGetter;
      }

      MSG newInstance() {
         try {
            return this.messageType.newInstance();
         } catch (IllegalAccessException | InstantiationException var2) {
            IndexedMessageCodec.LOGGER.error("Invalid login message", var2);
            throw new RuntimeException(var2);
         }
      }
   }
}
