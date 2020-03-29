package net.minecraftforge.fml.network;

import io.netty.buffer.Unpooled;
import io.netty.util.Attribute;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

public class FMLMCRegisterPacketHandler {
   public static final FMLMCRegisterPacketHandler INSTANCE = new FMLMCRegisterPacketHandler();

   public void addChannels(Set<ResourceLocation> locations, NetworkManager manager) {
      getFrom(manager).locations.addAll(locations);
   }

   void registerListener(NetworkEvent evt) {
      FMLMCRegisterPacketHandler.ChannelList channelList = getFrom(evt);
      channelList.updateFrom(evt.getSource(), evt.getPayload(), NetworkEvent.RegistrationChangeType.REGISTER);
      ((NetworkEvent.Context)evt.getSource().get()).setPacketHandled(true);
   }

   void unregisterListener(NetworkEvent evt) {
      FMLMCRegisterPacketHandler.ChannelList channelList = getFrom(evt);
      channelList.updateFrom(evt.getSource(), evt.getPayload(), NetworkEvent.RegistrationChangeType.UNREGISTER);
      ((NetworkEvent.Context)evt.getSource().get()).setPacketHandled(true);
   }

   private static FMLMCRegisterPacketHandler.ChannelList getFrom(NetworkManager manager) {
      return fromAttr(manager.channel().attr(FMLNetworkConstants.FML_MC_REGISTRY));
   }

   private static FMLMCRegisterPacketHandler.ChannelList getFrom(NetworkEvent event) {
      return fromAttr(((NetworkEvent.Context)event.getSource().get()).attr(FMLNetworkConstants.FML_MC_REGISTRY));
   }

   private static FMLMCRegisterPacketHandler.ChannelList fromAttr(Attribute<FMLMCRegisterPacketHandler.ChannelList> attr) {
      attr.setIfAbsent(new FMLMCRegisterPacketHandler.ChannelList());
      return (FMLMCRegisterPacketHandler.ChannelList)attr.get();
   }

   public void sendRegistry(NetworkManager manager, NetworkDirection dir) {
      PacketBuffer pb = new PacketBuffer(Unpooled.buffer());
      pb.writeBytes(getFrom(manager).toByteArray());
      ICustomPacket<IPacket<?>> iPacketICustomPacket = dir.buildPacket(Pair.of(pb, 0), FMLNetworkConstants.MC_REGISTER_RESOURCE);
      manager.sendPacket(iPacketICustomPacket.getThis());
   }

   public static class ChannelList {
      private Set<ResourceLocation> locations = new HashSet();

      public void updateFrom(Supplier<NetworkEvent.Context> source, PacketBuffer buffer, NetworkEvent.RegistrationChangeType changeType) {
         byte[] data = new byte[Math.max(buffer.readableBytes(), 0)];
         buffer.readBytes(data);
         Set<ResourceLocation> oldLocations = this.locations;
         this.locations = this.bytesToResLocation(data);
         oldLocations.addAll(this.locations);
         oldLocations.stream().map(NetworkRegistry::findTarget).filter(Optional::isPresent).map(Optional::get).forEach((t) -> {
            t.dispatchEvent(new NetworkEvent.ChannelRegistrationChangeEvent(source, changeType));
         });
      }

      byte[] toByteArray() {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         Iterator var2 = this.locations.iterator();

         while(var2.hasNext()) {
            ResourceLocation rl = (ResourceLocation)var2.next();

            try {
               bos.write(rl.toString().getBytes(StandardCharsets.UTF_8));
               bos.write(0);
            } catch (IOException var5) {
            }
         }

         return bos.toByteArray();
      }

      private Set<ResourceLocation> bytesToResLocation(byte[] all) {
         HashSet<ResourceLocation> rl = new HashSet();
         int last = 0;

         for(int cur = 0; cur < all.length; ++cur) {
            if (all[cur] == 0) {
               String s = new String(all, last, cur - last, StandardCharsets.UTF_8);
               rl.add(new ResourceLocation(s));
               last = cur + 1;
            }
         }

         return rl;
      }
   }
}
