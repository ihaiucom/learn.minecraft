package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SOpenWindowPacket implements IPacket<IClientPlayNetHandler> {
   private int windowId;
   private int menuId;
   private ITextComponent title;

   public SOpenWindowPacket() {
   }

   public SOpenWindowPacket(int p_i50769_1_, ContainerType<?> p_i50769_2_, ITextComponent p_i50769_3_) {
      this.windowId = p_i50769_1_;
      this.menuId = Registry.MENU.getId(p_i50769_2_);
      this.title = p_i50769_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.windowId = p_148837_1_.readVarInt();
      this.menuId = p_148837_1_.readVarInt();
      this.title = p_148837_1_.readTextComponent();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.windowId);
      p_148840_1_.writeVarInt(this.menuId);
      p_148840_1_.writeTextComponent(this.title);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.func_217272_a(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getWindowId() {
      return this.windowId;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public ContainerType<?> getContainerType() {
      return (ContainerType)Registry.MENU.getByValue(this.menuId);
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getTitle() {
      return this.title;
   }
}
