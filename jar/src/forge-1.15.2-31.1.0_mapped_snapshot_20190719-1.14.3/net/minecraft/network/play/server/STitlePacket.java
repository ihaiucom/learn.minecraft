package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class STitlePacket implements IPacket<IClientPlayNetHandler> {
   private STitlePacket.Type type;
   private ITextComponent message;
   private int fadeInTime;
   private int displayTime;
   private int fadeOutTime;

   public STitlePacket() {
   }

   public STitlePacket(STitlePacket.Type p_i46899_1_, ITextComponent p_i46899_2_) {
      this(p_i46899_1_, p_i46899_2_, -1, -1, -1);
   }

   public STitlePacket(int p_i46900_1_, int p_i46900_2_, int p_i46900_3_) {
      this(STitlePacket.Type.TIMES, (ITextComponent)null, p_i46900_1_, p_i46900_2_, p_i46900_3_);
   }

   public STitlePacket(STitlePacket.Type p_i46901_1_, @Nullable ITextComponent p_i46901_2_, int p_i46901_3_, int p_i46901_4_, int p_i46901_5_) {
      this.type = p_i46901_1_;
      this.message = p_i46901_2_;
      this.fadeInTime = p_i46901_3_;
      this.displayTime = p_i46901_4_;
      this.fadeOutTime = p_i46901_5_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.type = (STitlePacket.Type)p_148837_1_.readEnumValue(STitlePacket.Type.class);
      if (this.type == STitlePacket.Type.TITLE || this.type == STitlePacket.Type.SUBTITLE || this.type == STitlePacket.Type.ACTIONBAR) {
         this.message = p_148837_1_.readTextComponent();
      }

      if (this.type == STitlePacket.Type.TIMES) {
         this.fadeInTime = p_148837_1_.readInt();
         this.displayTime = p_148837_1_.readInt();
         this.fadeOutTime = p_148837_1_.readInt();
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.type);
      if (this.type == STitlePacket.Type.TITLE || this.type == STitlePacket.Type.SUBTITLE || this.type == STitlePacket.Type.ACTIONBAR) {
         p_148840_1_.writeTextComponent(this.message);
      }

      if (this.type == STitlePacket.Type.TIMES) {
         p_148840_1_.writeInt(this.fadeInTime);
         p_148840_1_.writeInt(this.displayTime);
         p_148840_1_.writeInt(this.fadeOutTime);
      }

   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleTitle(this);
   }

   @OnlyIn(Dist.CLIENT)
   public STitlePacket.Type getType() {
      return this.type;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getMessage() {
      return this.message;
   }

   @OnlyIn(Dist.CLIENT)
   public int getFadeInTime() {
      return this.fadeInTime;
   }

   @OnlyIn(Dist.CLIENT)
   public int getDisplayTime() {
      return this.displayTime;
   }

   @OnlyIn(Dist.CLIENT)
   public int getFadeOutTime() {
      return this.fadeOutTime;
   }

   public static enum Type {
      TITLE,
      SUBTITLE,
      ACTIONBAR,
      TIMES,
      CLEAR,
      RESET;
   }
}
