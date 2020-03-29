package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CUpdateSignPacket implements IPacket<IServerPlayNetHandler> {
   private BlockPos pos;
   private String[] lines;

   public CUpdateSignPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CUpdateSignPacket(BlockPos p_i49822_1_, ITextComponent p_i49822_2_, ITextComponent p_i49822_3_, ITextComponent p_i49822_4_, ITextComponent p_i49822_5_) {
      this.pos = p_i49822_1_;
      this.lines = new String[]{p_i49822_2_.getString(), p_i49822_3_.getString(), p_i49822_4_.getString(), p_i49822_5_.getString()};
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.pos = p_148837_1_.readBlockPos();
      this.lines = new String[4];

      for(int lvt_2_1_ = 0; lvt_2_1_ < 4; ++lvt_2_1_) {
         this.lines[lvt_2_1_] = p_148837_1_.readString(384);
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.pos);

      for(int lvt_2_1_ = 0; lvt_2_1_ < 4; ++lvt_2_1_) {
         p_148840_1_.writeString(this.lines[lvt_2_1_]);
      }

   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processUpdateSign(this);
   }

   public BlockPos getPosition() {
      return this.pos;
   }

   public String[] getLines() {
      return this.lines;
   }
}
