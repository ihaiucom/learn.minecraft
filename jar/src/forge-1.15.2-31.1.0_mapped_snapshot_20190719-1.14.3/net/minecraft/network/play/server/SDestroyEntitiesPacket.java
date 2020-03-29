package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SDestroyEntitiesPacket implements IPacket<IClientPlayNetHandler> {
   private int[] entityIDs;

   public SDestroyEntitiesPacket() {
   }

   public SDestroyEntitiesPacket(int... p_i46926_1_) {
      this.entityIDs = p_i46926_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityIDs = new int[p_148837_1_.readVarInt()];

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.entityIDs.length; ++lvt_2_1_) {
         this.entityIDs[lvt_2_1_] = p_148837_1_.readVarInt();
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityIDs.length);
      int[] var2 = this.entityIDs;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int lvt_5_1_ = var2[var4];
         p_148840_1_.writeVarInt(lvt_5_1_);
      }

   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleDestroyEntities(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int[] getEntityIDs() {
      return this.entityIDs;
   }
}
