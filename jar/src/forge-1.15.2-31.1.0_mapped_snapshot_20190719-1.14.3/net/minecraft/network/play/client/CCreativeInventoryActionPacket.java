package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CCreativeInventoryActionPacket implements IPacket<IServerPlayNetHandler> {
   private int slotId;
   private ItemStack stack;

   public CCreativeInventoryActionPacket() {
      this.stack = ItemStack.EMPTY;
   }

   @OnlyIn(Dist.CLIENT)
   public CCreativeInventoryActionPacket(int p_i46862_1_, ItemStack p_i46862_2_) {
      this.stack = ItemStack.EMPTY;
      this.slotId = p_i46862_1_;
      this.stack = p_i46862_2_.copy();
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processCreativeInventoryAction(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.slotId = p_148837_1_.readShort();
      this.stack = p_148837_1_.readItemStack();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeShort(this.slotId);
      p_148840_1_.writeItemStack(this.stack, false);
   }

   public int getSlotId() {
      return this.slotId;
   }

   public ItemStack getStack() {
      return this.stack;
   }
}
