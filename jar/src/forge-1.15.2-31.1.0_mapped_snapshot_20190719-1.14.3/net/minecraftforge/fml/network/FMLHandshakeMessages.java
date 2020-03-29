package net.minecraftforge.fml.network;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

public class FMLHandshakeMessages {
   public static class S2CConfigData extends FMLHandshakeMessages.LoginIndexedMessage {
      private final String fileName;
      private final byte[] fileData;

      public S2CConfigData(String configFileName, byte[] configFileData) {
         this.fileName = configFileName;
         this.fileData = configFileData;
      }

      void encode(PacketBuffer buffer) {
         buffer.writeString(this.fileName);
         buffer.writeByteArray(this.fileData);
      }

      public static FMLHandshakeMessages.S2CConfigData decode(PacketBuffer buffer) {
         return new FMLHandshakeMessages.S2CConfigData(buffer.readString(128), buffer.readByteArray());
      }

      public String getFileName() {
         return this.fileName;
      }

      public byte[] getBytes() {
         return this.fileData;
      }
   }

   public static class S2CRegistry extends FMLHandshakeMessages.LoginIndexedMessage {
      private ResourceLocation registryName;
      @Nullable
      private ForgeRegistry.Snapshot snapshot;

      public S2CRegistry(ResourceLocation name, @Nullable ForgeRegistry.Snapshot snapshot) {
         this.registryName = name;
         this.snapshot = snapshot;
      }

      void encode(PacketBuffer buffer) {
         buffer.writeResourceLocation(this.registryName);
         buffer.writeBoolean(this.hasSnapshot());
         if (this.hasSnapshot()) {
            buffer.writeBytes((ByteBuf)this.snapshot.getPacketData());
         }

      }

      public static FMLHandshakeMessages.S2CRegistry decode(PacketBuffer buffer) {
         ResourceLocation name = buffer.readResourceLocation();
         ForgeRegistry.Snapshot snapshot = null;
         if (buffer.readBoolean()) {
            snapshot = ForgeRegistry.Snapshot.read(buffer);
         }

         return new FMLHandshakeMessages.S2CRegistry(name, snapshot);
      }

      public ResourceLocation getRegistryName() {
         return this.registryName;
      }

      public boolean hasSnapshot() {
         return this.snapshot != null;
      }

      @Nullable
      public ForgeRegistry.Snapshot getSnapshot() {
         return this.snapshot;
      }
   }

   public static class C2SAcknowledge extends FMLHandshakeMessages.LoginIndexedMessage {
      public void encode(PacketBuffer buf) {
      }

      public static FMLHandshakeMessages.C2SAcknowledge decode(PacketBuffer buf) {
         return new FMLHandshakeMessages.C2SAcknowledge();
      }
   }

   public static class C2SModListReply extends FMLHandshakeMessages.LoginIndexedMessage {
      private List<String> mods;
      private Map<ResourceLocation, String> channels;
      private Map<ResourceLocation, String> registries;

      public C2SModListReply() {
         this.mods = (List)ModList.get().getMods().stream().map(ModInfo::getModId).collect(Collectors.toList());
         this.channels = NetworkRegistry.buildChannelVersions();
         this.registries = Maps.newHashMap();
      }

      private C2SModListReply(List<String> mods, Map<ResourceLocation, String> channels, Map<ResourceLocation, String> registries) {
         this.mods = mods;
         this.channels = channels;
         this.registries = registries;
      }

      public static FMLHandshakeMessages.C2SModListReply decode(PacketBuffer input) {
         List<String> mods = new ArrayList();
         int len = input.readVarInt();

         for(int x = 0; x < len; ++x) {
            mods.add(input.readString(256));
         }

         Map<ResourceLocation, String> channels = new HashMap();
         len = input.readVarInt();

         for(int x = 0; x < len; ++x) {
            channels.put(input.readResourceLocation(), input.readString(256));
         }

         Map<ResourceLocation, String> registries = new HashMap();
         len = input.readVarInt();

         for(int x = 0; x < len; ++x) {
            registries.put(input.readResourceLocation(), input.readString(256));
         }

         return new FMLHandshakeMessages.C2SModListReply(mods, channels, registries);
      }

      public void encode(PacketBuffer output) {
         output.writeVarInt(this.mods.size());
         this.mods.forEach((m) -> {
            output.writeString(m, 256);
         });
         output.writeVarInt(this.channels.size());
         this.channels.forEach((k, v) -> {
            output.writeResourceLocation(k);
            output.writeString(v, 256);
         });
         output.writeVarInt(this.registries.size());
         this.registries.forEach((k, v) -> {
            output.writeResourceLocation(k);
            output.writeString(v, 256);
         });
      }

      public List<String> getModList() {
         return this.mods;
      }

      public Map<ResourceLocation, String> getRegistries() {
         return this.registries;
      }

      public Map<ResourceLocation, String> getChannels() {
         return this.channels;
      }
   }

   public static class S2CModList extends FMLHandshakeMessages.LoginIndexedMessage {
      private List<String> mods;
      private Map<ResourceLocation, String> channels;
      private List<ResourceLocation> registries;

      public S2CModList() {
         this.mods = (List)ModList.get().getMods().stream().map(ModInfo::getModId).collect(Collectors.toList());
         this.channels = NetworkRegistry.buildChannelVersions();
         this.registries = RegistryManager.getRegistryNamesForSyncToClient();
      }

      private S2CModList(List<String> mods, Map<ResourceLocation, String> channels, List<ResourceLocation> registries) {
         this.mods = mods;
         this.channels = channels;
         this.registries = registries;
      }

      public static FMLHandshakeMessages.S2CModList decode(PacketBuffer input) {
         List<String> mods = new ArrayList();
         int len = input.readVarInt();

         for(int x = 0; x < len; ++x) {
            mods.add(input.readString(256));
         }

         Map<ResourceLocation, String> channels = new HashMap();
         len = input.readVarInt();

         for(int x = 0; x < len; ++x) {
            channels.put(input.readResourceLocation(), input.readString(256));
         }

         List<ResourceLocation> registries = new ArrayList();
         len = input.readVarInt();

         for(int x = 0; x < len; ++x) {
            registries.add(input.readResourceLocation());
         }

         return new FMLHandshakeMessages.S2CModList(mods, channels, registries);
      }

      public void encode(PacketBuffer output) {
         output.writeVarInt(this.mods.size());
         this.mods.forEach((m) -> {
            output.writeString(m, 256);
         });
         output.writeVarInt(this.channels.size());
         this.channels.forEach((k, v) -> {
            output.writeResourceLocation(k);
            output.writeString(v, 256);
         });
         output.writeVarInt(this.registries.size());
         this.registries.forEach(output::writeResourceLocation);
      }

      public List<String> getModList() {
         return this.mods;
      }

      public List<ResourceLocation> getRegistries() {
         return this.registries;
      }

      public Map<ResourceLocation, String> getChannels() {
         return this.channels;
      }
   }

   static class LoginIndexedMessage implements IntSupplier {
      private int loginIndex;

      void setLoginIndex(int loginIndex) {
         this.loginIndex = loginIndex;
      }

      int getLoginIndex() {
         return this.loginIndex;
      }

      public int getAsInt() {
         return this.getLoginIndex();
      }
   }
}
