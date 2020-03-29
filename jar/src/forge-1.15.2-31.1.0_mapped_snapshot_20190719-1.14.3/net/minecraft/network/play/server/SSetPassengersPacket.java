package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSetPassengersPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private int[] passengerIds;

   public SSetPassengersPacket() {
   }

   public SSetPassengersPacket(Entity p_i46909_1_) {
      this.entityId = p_i46909_1_.getEntityId();
      List<Entity> lvt_2_1_ = p_i46909_1_.getPassengers();
      this.passengerIds = new int[lvt_2_1_.size()];

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.size(); ++lvt_3_1_) {
         this.passengerIds[lvt_3_1_] = ((Entity)lvt_2_1_.get(lvt_3_1_)).getEntityId();
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.passengerIds = p_148837_1_.readVarIntArray();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeVarIntArray(this.passengerIds);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetPassengers(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int[] getPassengerIds() {
      return this.passengerIds;
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
   }
}
