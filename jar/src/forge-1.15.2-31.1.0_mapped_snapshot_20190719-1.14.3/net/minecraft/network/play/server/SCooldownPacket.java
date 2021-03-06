package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SCooldownPacket implements IPacket<IClientPlayNetHandler> {
   private Item item;
   private int ticks;

   public SCooldownPacket() {
   }

   public SCooldownPacket(Item p_i46950_1_, int p_i46950_2_) {
      this.item = p_i46950_1_;
      this.ticks = p_i46950_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.item = Item.getItemById(p_148837_1_.readVarInt());
      this.ticks = p_148837_1_.readVarInt();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(Item.getIdFromItem(this.item));
      p_148840_1_.writeVarInt(this.ticks);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleCooldown(this);
   }

   @OnlyIn(Dist.CLIENT)
   public Item getItem() {
      return this.item;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTicks() {
      return this.ticks;
   }
}
