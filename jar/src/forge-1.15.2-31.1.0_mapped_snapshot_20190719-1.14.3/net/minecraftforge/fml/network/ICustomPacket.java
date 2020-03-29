package net.minecraftforge.fml.network;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.client.CCustomPayloadLoginPacket;
import net.minecraft.network.login.server.SCustomPayloadLoginPacket;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.unsafe.UnsafeHacks;

public interface ICustomPacket<T extends IPacket<?>> {
   default PacketBuffer getInternalData() {
      return (PacketBuffer)((ICustomPacket.Fields)ICustomPacket.Fields.lookup.get(this.getClass())).data.map((f) -> {
         return (PacketBuffer)UnsafeHacks.getField(f, this);
      }).orElse((Object)null);
   }

   default ResourceLocation getName() {
      return (ResourceLocation)((ICustomPacket.Fields)ICustomPacket.Fields.lookup.get(this.getClass())).channel.map((f) -> {
         return (ResourceLocation)UnsafeHacks.getField(f, this);
      }).orElse(FMLLoginWrapper.WRAPPER);
   }

   default int getIndex() {
      return (Integer)((ICustomPacket.Fields)ICustomPacket.Fields.lookup.get(this.getClass())).index.map((f) -> {
         return UnsafeHacks.getIntField(f, this);
      }).orElse(Integer.MIN_VALUE);
   }

   default void setData(PacketBuffer buffer) {
      ((ICustomPacket.Fields)ICustomPacket.Fields.lookup.get(this.getClass())).data.ifPresent((f) -> {
         UnsafeHacks.setField(f, this, buffer);
      });
   }

   default void setName(ResourceLocation channelName) {
      ((ICustomPacket.Fields)ICustomPacket.Fields.lookup.get(this.getClass())).channel.ifPresent((f) -> {
         UnsafeHacks.setField(f, this, channelName);
      });
   }

   default void setIndex(int index) {
      ((ICustomPacket.Fields)ICustomPacket.Fields.lookup.get(this.getClass())).index.ifPresent((f) -> {
         UnsafeHacks.setIntField(f, this, index);
      });
   }

   default NetworkDirection getDirection() {
      return NetworkDirection.directionFor(this.getClass());
   }

   default T getThis() {
      return (IPacket)this;
   }

   public static enum Fields {
      CPACKETCUSTOMPAYLOAD(CCustomPayloadPacket.class),
      SPACKETCUSTOMPAYLOAD(SCustomPayloadPlayPacket.class),
      CPACKETCUSTOMLOGIN(CCustomPayloadLoginPacket.class),
      SPACKETCUSTOMLOGIN(SCustomPayloadLoginPacket.class);

      static final Reference2ReferenceArrayMap<Class<?>, ICustomPacket.Fields> lookup = (Reference2ReferenceArrayMap)Stream.of(values()).collect(Collectors.toMap(ICustomPacket.Fields::getClazz, Function.identity(), (m1, m2) -> {
         return m1;
      }, Reference2ReferenceArrayMap::new));
      private final Class<?> clazz;
      final Optional<Field> data;
      final Optional<Field> channel;
      final Optional<Field> index;

      private Fields(Class<?> customPacketClass) {
         this.clazz = customPacketClass;
         Field[] fields = customPacketClass.getDeclaredFields();
         this.data = Arrays.stream(fields).filter((f) -> {
            return !Modifier.isStatic(f.getModifiers()) && f.getType() == PacketBuffer.class;
         }).findFirst();
         this.channel = Arrays.stream(fields).filter((f) -> {
            return !Modifier.isStatic(f.getModifiers()) && f.getType() == ResourceLocation.class;
         }).findFirst();
         this.index = Arrays.stream(fields).filter((f) -> {
            return !Modifier.isStatic(f.getModifiers()) && f.getType() == Integer.TYPE;
         }).findFirst();
      }

      private Class<?> getClazz() {
         return this.clazz;
      }
   }
}
