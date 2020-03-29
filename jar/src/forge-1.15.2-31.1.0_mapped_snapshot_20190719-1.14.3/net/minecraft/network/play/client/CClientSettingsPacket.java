package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CClientSettingsPacket implements IPacket<IServerPlayNetHandler> {
   private String lang;
   private int view;
   private ChatVisibility chatVisibility;
   private boolean enableColors;
   private int modelPartFlags;
   private HandSide mainHand;

   public CClientSettingsPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CClientSettingsPacket(String p_i50761_1_, int p_i50761_2_, ChatVisibility p_i50761_3_, boolean p_i50761_4_, int p_i50761_5_, HandSide p_i50761_6_) {
      this.lang = p_i50761_1_;
      this.view = p_i50761_2_;
      this.chatVisibility = p_i50761_3_;
      this.enableColors = p_i50761_4_;
      this.modelPartFlags = p_i50761_5_;
      this.mainHand = p_i50761_6_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.lang = p_148837_1_.readString(16);
      this.view = p_148837_1_.readByte();
      this.chatVisibility = (ChatVisibility)p_148837_1_.readEnumValue(ChatVisibility.class);
      this.enableColors = p_148837_1_.readBoolean();
      this.modelPartFlags = p_148837_1_.readUnsignedByte();
      this.mainHand = (HandSide)p_148837_1_.readEnumValue(HandSide.class);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeString(this.lang);
      p_148840_1_.writeByte(this.view);
      p_148840_1_.writeEnumValue(this.chatVisibility);
      p_148840_1_.writeBoolean(this.enableColors);
      p_148840_1_.writeByte(this.modelPartFlags);
      p_148840_1_.writeEnumValue(this.mainHand);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processClientSettings(this);
   }

   public String getLang() {
      return this.lang;
   }

   public ChatVisibility getChatVisibility() {
      return this.chatVisibility;
   }

   public boolean isColorsEnabled() {
      return this.enableColors;
   }

   public int getModelPartFlags() {
      return this.modelPartFlags;
   }

   public HandSide getMainHand() {
      return this.mainHand;
   }
}
