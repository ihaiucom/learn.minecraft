package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnMobPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private UUID uniqueId;
   private int type;
   private double x;
   private double y;
   private double z;
   private int velocityX;
   private int velocityY;
   private int velocityZ;
   private byte yaw;
   private byte pitch;
   private byte headPitch;

   public SSpawnMobPacket() {
   }

   public SSpawnMobPacket(LivingEntity p_i46973_1_) {
      this.entityId = p_i46973_1_.getEntityId();
      this.uniqueId = p_i46973_1_.getUniqueID();
      this.type = Registry.ENTITY_TYPE.getId(p_i46973_1_.getType());
      this.x = p_i46973_1_.func_226277_ct_();
      this.y = p_i46973_1_.func_226278_cu_();
      this.z = p_i46973_1_.func_226281_cx_();
      this.yaw = (byte)((int)(p_i46973_1_.rotationYaw * 256.0F / 360.0F));
      this.pitch = (byte)((int)(p_i46973_1_.rotationPitch * 256.0F / 360.0F));
      this.headPitch = (byte)((int)(p_i46973_1_.rotationYawHead * 256.0F / 360.0F));
      double lvt_2_1_ = 3.9D;
      Vec3d lvt_4_1_ = p_i46973_1_.getMotion();
      double lvt_5_1_ = MathHelper.clamp(lvt_4_1_.x, -3.9D, 3.9D);
      double lvt_7_1_ = MathHelper.clamp(lvt_4_1_.y, -3.9D, 3.9D);
      double lvt_9_1_ = MathHelper.clamp(lvt_4_1_.z, -3.9D, 3.9D);
      this.velocityX = (int)(lvt_5_1_ * 8000.0D);
      this.velocityY = (int)(lvt_7_1_ * 8000.0D);
      this.velocityZ = (int)(lvt_9_1_ * 8000.0D);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.uniqueId = p_148837_1_.readUniqueId();
      this.type = p_148837_1_.readVarInt();
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
      this.yaw = p_148837_1_.readByte();
      this.pitch = p_148837_1_.readByte();
      this.headPitch = p_148837_1_.readByte();
      this.velocityX = p_148837_1_.readShort();
      this.velocityY = p_148837_1_.readShort();
      this.velocityZ = p_148837_1_.readShort();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeUniqueId(this.uniqueId);
      p_148840_1_.writeVarInt(this.type);
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
      p_148840_1_.writeByte(this.yaw);
      p_148840_1_.writeByte(this.pitch);
      p_148840_1_.writeByte(this.headPitch);
      p_148840_1_.writeShort(this.velocityX);
      p_148840_1_.writeShort(this.velocityY);
      p_148840_1_.writeShort(this.velocityZ);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSpawnMob(this);
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
   public int getEntityType() {
      return this.type;
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
   public int getVelocityX() {
      return this.velocityX;
   }

   @OnlyIn(Dist.CLIENT)
   public int getVelocityY() {
      return this.velocityY;
   }

   @OnlyIn(Dist.CLIENT)
   public int getVelocityZ() {
      return this.velocityZ;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getYaw() {
      return this.yaw;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getPitch() {
      return this.pitch;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getHeadPitch() {
      return this.headPitch;
   }
}
