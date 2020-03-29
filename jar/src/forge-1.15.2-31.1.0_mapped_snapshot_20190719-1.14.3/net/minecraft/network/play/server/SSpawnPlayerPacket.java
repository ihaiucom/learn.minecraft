package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnPlayerPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private UUID uniqueId;
   private double x;
   private double y;
   private double z;
   private byte yaw;
   private byte pitch;

   public SSpawnPlayerPacket() {
   }

   public SSpawnPlayerPacket(PlayerEntity p_i46971_1_) {
      this.entityId = p_i46971_1_.getEntityId();
      this.uniqueId = p_i46971_1_.getGameProfile().getId();
      this.x = p_i46971_1_.func_226277_ct_();
      this.y = p_i46971_1_.func_226278_cu_();
      this.z = p_i46971_1_.func_226281_cx_();
      this.yaw = (byte)((int)(p_i46971_1_.rotationYaw * 256.0F / 360.0F));
      this.pitch = (byte)((int)(p_i46971_1_.rotationPitch * 256.0F / 360.0F));
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.uniqueId = p_148837_1_.readUniqueId();
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
      this.yaw = p_148837_1_.readByte();
      this.pitch = p_148837_1_.readByte();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeUniqueId(this.uniqueId);
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
      p_148840_1_.writeByte(this.yaw);
      p_148840_1_.writeByte(this.pitch);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSpawnPlayer(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityID() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   public UUID getUniqueId() {
      return this.uniqueId;
   }

   @OnlyIn(Dist.CLIENT)
   public double getX() {
      return this.x;
   }

   @OnlyIn(Dist.CLIENT)
   public double getY() {
      return this.y;
   }

   @OnlyIn(Dist.CLIENT)
   public double getZ() {
      return this.z;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getYaw() {
      return this.yaw;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getPitch() {
      return this.pitch;
   }
}
