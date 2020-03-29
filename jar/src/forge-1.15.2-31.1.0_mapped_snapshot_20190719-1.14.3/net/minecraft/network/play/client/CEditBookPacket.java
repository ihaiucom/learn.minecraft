package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CEditBookPacket implements IPacket<IServerPlayNetHandler> {
   private ItemStack stack;
   private boolean updateAll;
   private Hand hand;

   public CEditBookPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CEditBookPacket(ItemStack p_i49823_1_, boolean p_i49823_2_, Hand p_i49823_3_) {
      this.stack = p_i49823_1_.copy();
      this.updateAll = p_i49823_2_;
      this.hand = p_i49823_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.stack = p_148837_1_.readItemStack();
      this.updateAll = p_148837_1_.readBoolean();
      this.hand = (Hand)p_148837_1_.readEnumValue(Hand.class);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeItemStack(this.stack);
      p_148840_1_.writeBoolean(this.updateAll);
      p_148840_1_.writeEnumValue(this.hand);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processEditBook(this);
   }

   public ItemStack getStack() {
      return this.stack;
   }

   public boolean shouldUpdateAll() {
      return this.updateAll;
   }

   public Hand getHand() {
      return this.hand;
   }
}
