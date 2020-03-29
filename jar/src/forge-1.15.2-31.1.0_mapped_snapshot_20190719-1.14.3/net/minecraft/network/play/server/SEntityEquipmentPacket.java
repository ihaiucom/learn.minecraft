package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityEquipmentPacket implements IPacket<IClientPlayNetHandler> {
   private int entityID;
   private EquipmentSlotType equipmentSlot;
   private ItemStack itemStack;

   public SEntityEquipmentPacket() {
      this.itemStack = ItemStack.EMPTY;
   }

   public SEntityEquipmentPacket(int p_i46913_1_, EquipmentSlotType p_i46913_2_, ItemStack p_i46913_3_) {
      this.itemStack = ItemStack.EMPTY;
      this.entityID = p_i46913_1_;
      this.equipmentSlot = p_i46913_2_;
      this.itemStack = p_i46913_3_.copy();
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityID = p_148837_1_.readVarInt();
      this.equipmentSlot = (EquipmentSlotType)p_148837_1_.readEnumValue(EquipmentSlotType.class);
      this.itemStack = p_148837_1_.readItemStack();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityID);
      p_148840_1_.writeEnumValue(this.equipmentSlot);
      p_148840_1_.writeItemStack(this.itemStack);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleEntityEquipment(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItemStack() {
      return this.itemStack;
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityID() {
      return this.entityID;
   }

   @OnlyIn(Dist.CLIENT)
   public EquipmentSlotType getEquipmentSlot() {
      return this.equipmentSlot;
   }
}
