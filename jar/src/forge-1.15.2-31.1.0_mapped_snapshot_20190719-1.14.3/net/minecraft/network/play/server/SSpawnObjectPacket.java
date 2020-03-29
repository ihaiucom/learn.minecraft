package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnObjectPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private UUID uniqueId;
   private double x;
   private double y;
   private double z;
   private int speedX;
   private int speedY;
   private int speedZ;
   private int pitch;
   private int yaw;
   private EntityType<?> type;
   private int data;

   public SSpawnObjectPacket() {
   }

   public SSpawnObjectPacket(int p_i50777_1_, UUID p_i50777_2_, double p_i50777_3_, double p_i50777_5_, double p_i50777_7_, float p_i50777_9_, float p_i50777_10_, EntityType<?> p_i50777_11_, int p_i50777_12_, Vec3d p_i50777_13_) {
      this.entityId = p_i50777_1_;
      this.uniqueId = p_i50777_2_;
      this.x = p_i50777_3_;
      this.y = p_i50777_5_;
      this.z = p_i50777_7_;
      this.pitch = MathHelper.floor(p_i50777_9_ * 256.0F / 360.0F);
      this.yaw = MathHelper.floor(p_i50777_10_ * 256.0F / 360.0F);
      this.type = p_i50777_11_;
      this.data = p_i50777_12_;
      this.speedX = (int)(MathHelper.clamp(p_i50777_13_.x, -3.9D, 3.9D) * 8000.0D);
      this.speedY = (int)(MathHelper.clamp(p_i50777_13_.y, -3.9D, 3.9D) * 8000.0D);
      this.speedZ = (int)(MathHelper.clamp(p_i50777_13_.z, -3.9D, 3.9D) * 8000.0D);
   }

   public SSpawnObjectPacket(Entity p_i50778_1_) {
      this(p_i50778_1_, 0);
   }

   public SSpawnObjectPacket(Entity p_i46976_1_, int p_i46976_2_) {
      this(p_i46976_1_.getEntityId(), p_i46976_1_.getUniqueID(), p_i46976_1_.func_226277_ct_(), p_i46976_1_.func_226278_cu_(), p_i46976_1_.func_226281_cx_(), p_i46976_1_.rotationPitch, p_i46976_1_.rotationYaw, p_i46976_1_.getType(), p_i46976_2_, p_i46976_1_.getMotion());
   }

   public SSpawnObjectPacket(Entity p_i50779_1_, EntityType<?> p_i50779_2_, int p_i50779_3_, BlockPos p_i50779_4_) {
      this(p_i50779_1_.getEntityId(), p_i50779_1_.getUniqueID(), (double)p_i50779_4_.getX(), (double)p_i50779_4_.getY(), (double)p_i50779_4_.getZ(), p_i50779_1_.rotationPitch, p_i50779_1_.rotationYaw, p_i50779_2_, p_i50779_3_, p_i50779_1_.getMotion());
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.uniqueId = p_148837_1_.readUniqueId();
      this.type = (EntityType)Registry.ENTITY_TYPE.getByValue(p_148837_1_.readVarInt());
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
      this.pitch = p_148837_1_.readByte();
      this.yaw = p_148837_1_.readByte();
      this.data = p_148837_1_.readInt();
      this.speedX = p_148837_1_.readShort();
      this.speedY = p_148837_1_.readShort();
      this.speedZ = p_148837_1_.readShort();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeUniqueId(this.uniqueId);
      p_148840_1_.writeVarInt(Registry.ENTITY_TYPE.getId(this.type));
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
      p_148840_1_.writeByte(this.pitch);
      p_148840_1_.writeByte(this.yaw);
      p_148840_1_.writeInt(this.data);
      p_148840_1_.writeShort(this.speedX);
      p_148840_1_.writeShort(this.speedY);
      p_148840_1_.writeShort(this.speedZ);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSpawnObject(this);
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
   public double func_218693_g() {
      return (double)this.speedX / 8000.0D;
   }

   @OnlyIn(Dist.CLIENT)
   public double func_218695_h() {
      return (double)this.speedY / 8000.0D;
   }

   @OnlyIn(Dist.CLIENT)
   public double func_218692_i() {
      return (double)this.speedZ / 8000.0D;
   }

   @OnlyIn(Dist.CLIENT)
   public int getPitch() {
      return this.pitch;
   }

   @OnlyIn(Dist.CLIENT)
   public int getYaw() {
      return this.yaw;
   }

   @OnlyIn(Dist.CLIENT)
   public EntityType<?> getType() {
      return this.type;
   }

   @OnlyIn(Dist.CLIENT)
   public int getData() {
      return this.data;
   }
}
