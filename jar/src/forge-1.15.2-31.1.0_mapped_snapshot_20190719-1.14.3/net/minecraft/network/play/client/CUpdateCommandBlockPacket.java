package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CUpdateCommandBlockPacket implements IPacket<IServerPlayNetHandler> {
   private BlockPos pos;
   private String command;
   private boolean trackOutput;
   private boolean conditional;
   private boolean auto;
   private CommandBlockTileEntity.Mode mode;

   public CUpdateCommandBlockPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CUpdateCommandBlockPacket(BlockPos p_i49543_1_, String p_i49543_2_, CommandBlockTileEntity.Mode p_i49543_3_, boolean p_i49543_4_, boolean p_i49543_5_, boolean p_i49543_6_) {
      this.pos = p_i49543_1_;
      this.command = p_i49543_2_;
      this.trackOutput = p_i49543_4_;
      this.conditional = p_i49543_5_;
      this.auto = p_i49543_6_;
      this.mode = p_i49543_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.pos = p_148837_1_.readBlockPos();
      this.command = p_148837_1_.readString(32767);
      this.mode = (CommandBlockTileEntity.Mode)p_148837_1_.readEnumValue(CommandBlockTileEntity.Mode.class);
      int lvt_2_1_ = p_148837_1_.readByte();
      this.trackOutput = (lvt_2_1_ & 1) != 0;
      this.conditional = (lvt_2_1_ & 2) != 0;
      this.auto = (lvt_2_1_ & 4) != 0;
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.pos);
      p_148840_1_.writeString(this.command);
      p_148840_1_.writeEnumValue(this.mode);
      int lvt_2_1_ = 0;
      if (this.trackOutput) {
         lvt_2_1_ |= 1;
      }

      if (this.conditional) {
         lvt_2_1_ |= 2;
      }

      if (this.auto) {
         lvt_2_1_ |= 4;
      }

      p_148840_1_.writeByte(lvt_2_1_);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processUpdateCommandBlock(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public String getCommand() {
      return this.command;
   }

   public boolean shouldTrackOutput() {
      return this.trackOutput;
   }

   public boolean isConditional() {
      return this.conditional;
   }

   public boolean isAuto() {
      return this.auto;
   }

   public CommandBlockTileEntity.Mode getMode() {
      return this.mode;
   }
}
