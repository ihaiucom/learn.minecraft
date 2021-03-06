package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnGlobalEntityPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private double x;
   private double y;
   private double z;
   private int type;

   public SSpawnGlobalEntityPacket() {
   }

   public SSpawnGlobalEntityPacket(Entity p_i46974_1_) {
      this.entityId = p_i46974_1_.getEntityId();
      this.x = p_i46974_1_.func_226277_ct_();
      this.y = p_i46974_1_.func_226278_cu_();
      this.z = p_i46974_1_.func_226281_cx_();
      if (p_i46974_1_ instanceof LightningBoltEntity) {
         this.type = 1;
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.type = p_148837_1_.readByte();
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeByte(this.type);
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSpawnGlobalEntity(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
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
   public int getType() {
      return this.type;
   }
}
