package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SWindowItemsPacket implements IPacket<IClientPlayNetHandler> {
   private int windowId;
   private List<ItemStack> itemStacks;

   public SWindowItemsPacket() {
   }

   public SWindowItemsPacket(int p_i47317_1_, NonNullList<ItemStack> p_i47317_2_) {
      this.windowId = p_i47317_1_;
      this.itemStacks = NonNullList.withSize(p_i47317_2_.size(), ItemStack.EMPTY);

      for(int lvt_3_1_ = 0; lvt_3_1_ < this.itemStacks.size(); ++lvt_3_1_) {
         this.itemStacks.set(lvt_3_1_, ((ItemStack)p_i47317_2_.get(lvt_3_1_)).copy());
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.windowId = p_148837_1_.readUnsignedByte();
      int lvt_2_1_ = p_148837_1_.readShort();
      this.itemStacks = NonNullList.withSize(lvt_2_1_, ItemStack.EMPTY);

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_) {
         this.itemStacks.set(lvt_3_1_, p_148837_1_.readItemStack());
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.windowId);
      p_148840_1_.writeShort(this.itemStacks.size());
      Iterator var2 = this.itemStacks.iterator();

      while(var2.hasNext()) {
         ItemStack lvt_3_1_ = (ItemStack)var2.next();
         p_148840_1_.writeItemStack(lvt_3_1_);
      }

   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleWindowItems(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getWindowId() {
      return this.windowId;
   }

   @OnlyIn(Dist.CLIENT)
   public List<ItemStack> getItemStacks() {
      return this.itemStacks;
   }
}
