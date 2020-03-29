package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnExperienceOrbPacket implements IPacket<IClientPlayNetHandler> {
   private int entityID;
   private double posX;
   private double posY;
   private double posZ;
   private int xpValue;

   public SSpawnExperienceOrbPacket() {
   }

   public SSpawnExperienceOrbPacket(ExperienceOrbEntity p_i46975_1_) {
      this.entityID = p_i46975_1_.getEntityId();
      this.posX = p_i46975_1_.func_226277_ct_();
      this.posY = p_i46975_1_.func_226278_cu_();
      this.posZ = p_i46975_1_.func_226281_cx_();
      this.xpValue = p_i46975_1_.getXpValue();
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityID = p_148837_1_.readVarInt();
      this.posX = p_148837_1_.readDouble();
      this.posY = p_148837_1_.readDouble();
      this.posZ = p_148837_1_.readDouble();
      this.xpValue = p_148837_1_.readShort();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityID);
      p_148840_1_.writeDouble(this.posX);
      p_148840_1_.writeDouble(this.posY);
      p_148840_1_.writeDouble(this.posZ);
      p_148840_1_.writeShort(this.xpValue);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSpawnExperienceOrb(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityID() {
      return this.entityID;
   }

   @OnlyIn(Dist.CLIENT)
   public double getX() {
      return this.posX;
   }

   @OnlyIn(Dist.CLIENT)
   public double getY() {
      return this.posY;
   }

   @OnlyIn(Dist.CLIENT)
   public double getZ() {
      return this.posZ;
   }

   @OnlyIn(Dist.CLIENT)
   public int getXPValue() {
      return this.xpValue;
   }
}
