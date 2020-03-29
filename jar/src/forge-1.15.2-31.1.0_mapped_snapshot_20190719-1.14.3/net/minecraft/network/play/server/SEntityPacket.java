package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityPacket implements IPacket<IClientPlayNetHandler> {
   protected int entityId;
   protected short posX;
   protected short posY;
   protected short posZ;
   protected byte yaw;
   protected byte pitch;
   protected boolean onGround;
   protected boolean rotating;
   protected boolean field_229744_i_;

   public static long func_218743_a(double p_218743_0_) {
      return MathHelper.lfloor(p_218743_0_ * 4096.0D);
   }

   public static Vec3d func_218744_a(long p_218744_0_, long p_218744_2_, long p_218744_4_) {
      return (new Vec3d((double)p_218744_0_, (double)p_218744_2_, (double)p_218744_4_)).scale(2.44140625E-4D);
   }

   public SEntityPacket() {
   }

   public SEntityPacket(int p_i46936_1_) {
      this.entityId = p_i46936_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleEntityMovement(this);
   }

   public String toString() {
      return "Entity_" + super.toString();
   }

   @OnlyIn(Dist.CLIENT)
   public Entity getEntity(World p_149065_1_) {
      return p_149065_1_.getEntityByID(this.entityId);
   }

   @OnlyIn(Dist.CLIENT)
   public short getX() {
      return this.posX;
   }

   @OnlyIn(Dist.CLIENT)
   public short getY() {
      return this.posY;
   }

   @OnlyIn(Dist.CLIENT)
   public short getZ() {
      return this.posZ;
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
   public boolean isRotating() {
      return this.rotating;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_229745_h_() {
      return this.field_229744_i_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean getOnGround() {
      return this.onGround;
   }

   public static class LookPacket extends SEntityPacket {
      public LookPacket() {
         this.rotating = true;
      }

      public LookPacket(int p_i47081_1_, byte p_i47081_2_, byte p_i47081_3_, boolean p_i47081_4_) {
         super(p_i47081_1_);
         this.yaw = p_i47081_2_;
         this.pitch = p_i47081_3_;
         this.rotating = true;
         this.onGround = p_i47081_4_;
      }

      public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
         super.readPacketData(p_148837_1_);
         this.yaw = p_148837_1_.readByte();
         this.pitch = p_148837_1_.readByte();
         this.onGround = p_148837_1_.readBoolean();
      }

      public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
         super.writePacketData(p_148840_1_);
         p_148840_1_.writeByte(this.yaw);
         p_148840_1_.writeByte(this.pitch);
         p_148840_1_.writeBoolean(this.onGround);
      }
   }

   public static class RelativeMovePacket extends SEntityPacket {
      public RelativeMovePacket() {
         this.field_229744_i_ = true;
      }

      public RelativeMovePacket(int p_i49990_1_, short p_i49990_2_, short p_i49990_3_, short p_i49990_4_, boolean p_i49990_5_) {
         super(p_i49990_1_);
         this.posX = p_i49990_2_;
         this.posY = p_i49990_3_;
         this.posZ = p_i49990_4_;
         this.onGround = p_i49990_5_;
         this.field_229744_i_ = true;
      }

      public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
         super.readPacketData(p_148837_1_);
         this.posX = p_148837_1_.readShort();
         this.posY = p_148837_1_.readShort();
         this.posZ = p_148837_1_.readShort();
         this.onGround = p_148837_1_.readBoolean();
      }

      public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
         super.writePacketData(p_148840_1_);
         p_148840_1_.writeShort(this.posX);
         p_148840_1_.writeShort(this.posY);
         p_148840_1_.writeShort(this.posZ);
         p_148840_1_.writeBoolean(this.onGround);
      }
   }

   public static class MovePacket extends SEntityPacket {
      public MovePacket() {
         this.rotating = true;
         this.field_229744_i_ = true;
      }

      public MovePacket(int p_i49988_1_, short p_i49988_2_, short p_i49988_3_, short p_i49988_4_, byte p_i49988_5_, byte p_i49988_6_, boolean p_i49988_7_) {
         super(p_i49988_1_);
         this.posX = p_i49988_2_;
         this.posY = p_i49988_3_;
         this.posZ = p_i49988_4_;
         this.yaw = p_i49988_5_;
         this.pitch = p_i49988_6_;
         this.onGround = p_i49988_7_;
         this.rotating = true;
         this.field_229744_i_ = true;
      }

      public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
         super.readPacketData(p_148837_1_);
         this.posX = p_148837_1_.readShort();
         this.posY = p_148837_1_.readShort();
         this.posZ = p_148837_1_.readShort();
         this.yaw = p_148837_1_.readByte();
         this.pitch = p_148837_1_.readByte();
         this.onGround = p_148837_1_.readBoolean();
      }

      public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
         super.writePacketData(p_148840_1_);
         p_148840_1_.writeShort(this.posX);
         p_148840_1_.writeShort(this.posY);
         p_148840_1_.writeShort(this.posZ);
         p_148840_1_.writeByte(this.yaw);
         p_148840_1_.writeByte(this.pitch);
         p_148840_1_.writeBoolean(this.onGround);
      }
   }
}
