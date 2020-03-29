package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SCollectItemPacket implements IPacket<IClientPlayNetHandler> {
   private int collectedItemEntityId;
   private int entityId;
   private int collectedQuantity;

   public SCollectItemPacket() {
   }

   public SCollectItemPacket(int p_i47316_1_, int p_i47316_2_, int p_i47316_3_) {
      this.collectedItemEntityId = p_i47316_1_;
      this.entityId = p_i47316_2_;
      this.collectedQuantity = p_i47316_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.collectedItemEntityId = p_148837_1_.readVarInt();
      this.entityId = p_148837_1_.readVarInt();
      this.collectedQuantity = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.collectedItemEntityId);
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeVarInt(this.collectedQuantity);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleCollectItem(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getCollectedItemEntityID() {
      return this.collectedItemEntityId;
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityID() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   public int getAmount() {
      return this.collectedQuantity;
   }
}
