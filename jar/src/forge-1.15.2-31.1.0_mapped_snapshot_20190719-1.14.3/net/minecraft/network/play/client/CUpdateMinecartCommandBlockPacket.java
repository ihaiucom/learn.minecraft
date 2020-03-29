package net.minecraft.network.play.client;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.MinecartCommandBlockEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CUpdateMinecartCommandBlockPacket implements IPacket<IServerPlayNetHandler> {
   private int entityId;
   private String command;
   private boolean trackOutput;

   public CUpdateMinecartCommandBlockPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CUpdateMinecartCommandBlockPacket(int p_i49542_1_, String p_i49542_2_, boolean p_i49542_3_) {
      this.entityId = p_i49542_1_;
      this.command = p_i49542_2_;
      this.trackOutput = p_i49542_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.command = p_148837_1_.readString(32767);
      this.trackOutput = p_148837_1_.readBoolean();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeString(this.command);
      p_148840_1_.writeBoolean(this.trackOutput);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processUpdateCommandMinecart(this);
   }

   @Nullable
   public CommandBlockLogic getCommandBlock(World p_210371_1_) {
      Entity lvt_2_1_ = p_210371_1_.getEntityByID(this.entityId);
      return lvt_2_1_ instanceof MinecartCommandBlockEntity ? ((MinecartCommandBlockEntity)lvt_2_1_).getCommandBlockLogic() : null;
   }

   public String getCommand() {
      return this.command;
   }

   public boolean shouldTrackOutput() {
      return this.trackOutput;
   }
}
