package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateHealthPacket implements IPacket<IClientPlayNetHandler> {
   private float health;
   private int foodLevel;
   private float saturationLevel;

   public SUpdateHealthPacket() {
   }

   public SUpdateHealthPacket(float p_i46911_1_, int p_i46911_2_, float p_i46911_3_) {
      this.health = p_i46911_1_;
      this.foodLevel = p_i46911_2_;
      this.saturationLevel = p_i46911_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.health = p_148837_1_.readFloat();
      this.foodLevel = p_148837_1_.readVarInt();
      this.saturationLevel = p_148837_1_.readFloat();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeFloat(this.health);
      p_148840_1_.writeVarInt(this.foodLevel);
      p_148840_1_.writeFloat(this.saturationLevel);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleUpdateHealth(this);
   }

   @OnlyIn(Dist.CLIENT)
   public float getHealth() {
      return this.health;
   }

   @OnlyIn(Dist.CLIENT)
   public int getFoodLevel() {
      return this.foodLevel;
   }

   @OnlyIn(Dist.CLIENT)
   public float getSaturationLevel() {
      return this.saturationLevel;
   }
}
