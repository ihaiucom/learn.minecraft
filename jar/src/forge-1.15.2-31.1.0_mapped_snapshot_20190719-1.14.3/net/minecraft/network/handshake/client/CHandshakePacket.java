package net.minecraft.network.handshake.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.handshake.IHandshakeNetHandler;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class CHandshakePacket implements IPacket<IHandshakeNetHandler> {
   private int protocolVersion;
   private String ip;
   private int port;
   private ProtocolType requestedState;
   private String fmlVersion = "FML2";

   public CHandshakePacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CHandshakePacket(String p_i47613_1_, int p_i47613_2_, ProtocolType p_i47613_3_) {
      this.protocolVersion = SharedConstants.getVersion().getProtocolVersion();
      this.ip = p_i47613_1_;
      this.port = p_i47613_2_;
      this.requestedState = p_i47613_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.protocolVersion = p_148837_1_.readVarInt();
      this.ip = p_148837_1_.readString(255);
      this.port = p_148837_1_.readUnsignedShort();
      this.requestedState = ProtocolType.getById(p_148837_1_.readVarInt());
      this.fmlVersion = NetworkHooks.getFMLVersion(this.ip);
      this.ip = this.ip.split("\u0000")[0];
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.protocolVersion);
      p_148840_1_.writeString(this.ip + "\u0000" + "FML2" + "\u0000");
      p_148840_1_.writeShort(this.port);
      p_148840_1_.writeVarInt(this.requestedState.getId());
   }

   public void processPacket(IHandshakeNetHandler p_148833_1_) {
      p_148833_1_.processHandshake(this);
   }

   public ProtocolType getRequestedState() {
      return this.requestedState;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }

   public String getFMLVersion() {
      return this.fmlVersion;
   }
}
