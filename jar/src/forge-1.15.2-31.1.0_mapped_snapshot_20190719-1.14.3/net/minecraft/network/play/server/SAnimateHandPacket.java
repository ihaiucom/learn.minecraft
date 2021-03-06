package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SAnimateHandPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private int type;

   public SAnimateHandPacket() {
   }

   public SAnimateHandPacket(Entity p_i46970_1_, int p_i46970_2_) {
      this.entityId = p_i46970_1_.getEntityId();
      this.type = p_i46970_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.type = p_148837_1_.readUnsignedByte();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeByte(this.type);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleAnimation(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityID() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   public int getAnimationType() {
      return this.type;
   }
}
