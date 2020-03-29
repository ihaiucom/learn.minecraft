package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityVelocityPacket implements IPacket<IClientPlayNetHandler> {
   private int entityID;
   private int motionX;
   private int motionY;
   private int motionZ;

   public SEntityVelocityPacket() {
   }

   public SEntityVelocityPacket(Entity p_i46914_1_) {
      this(p_i46914_1_.getEntityId(), p_i46914_1_.getMotion());
   }

   public SEntityVelocityPacket(int p_i50764_1_, Vec3d p_i50764_2_) {
      this.entityID = p_i50764_1_;
      double lvt_3_1_ = 3.9D;
      double lvt_5_1_ = MathHelper.clamp(p_i50764_2_.x, -3.9D, 3.9D);
      double lvt_7_1_ = MathHelper.clamp(p_i50764_2_.y, -3.9D, 3.9D);
      double lvt_9_1_ = MathHelper.clamp(p_i50764_2_.z, -3.9D, 3.9D);
      this.motionX = (int)(lvt_5_1_ * 8000.0D);
      this.motionY = (int)(lvt_7_1_ * 8000.0D);
      this.motionZ = (int)(lvt_9_1_ * 8000.0D);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityID = p_148837_1_.readVarInt();
      this.motionX = p_148837_1_.readShort();
      this.motionY = p_148837_1_.readShort();
      this.motionZ = p_148837_1_.readShort();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityID);
      p_148840_1_.writeShort(this.motionX);
      p_148840_1_.writeShort(this.motionY);
      p_148840_1_.writeShort(this.motionZ);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleEntityVelocity(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityID() {
      return this.entityID;
   }

   @OnlyIn(Dist.CLIENT)
   public int getMotionX() {
      return this.motionX;
   }

   @OnlyIn(Dist.CLIENT)
   public int getMotionY() {
      return this.motionY;
   }

   @OnlyIn(Dist.CLIENT)
   public int getMotionZ() {
      return this.motionZ;
   }
}
