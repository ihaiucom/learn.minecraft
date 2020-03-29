package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPlayerDiggingPacket implements IPacket<IServerPlayNetHandler> {
   private BlockPos position;
   private Direction facing;
   private CPlayerDiggingPacket.Action action;

   public CPlayerDiggingPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPlayerDiggingPacket(CPlayerDiggingPacket.Action p_i46871_1_, BlockPos p_i46871_2_, Direction p_i46871_3_) {
      this.action = p_i46871_1_;
      this.position = p_i46871_2_.toImmutable();
      this.facing = p_i46871_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.action = (CPlayerDiggingPacket.Action)p_148837_1_.readEnumValue(CPlayerDiggingPacket.Action.class);
      this.position = p_148837_1_.readBlockPos();
      this.facing = Direction.byIndex(p_148837_1_.readUnsignedByte());
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.action);
      p_148840_1_.writeBlockPos(this.position);
      p_148840_1_.writeByte(this.facing.getIndex());
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processPlayerDigging(this);
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public Direction getFacing() {
      return this.facing;
   }

   public CPlayerDiggingPacket.Action getAction() {
      return this.action;
   }

   public static enum Action {
      START_DESTROY_BLOCK,
      ABORT_DESTROY_BLOCK,
      STOP_DESTROY_BLOCK,
      DROP_ALL_ITEMS,
      DROP_ITEM,
      RELEASE_USE_ITEM,
      SWAP_HELD_ITEMS;
   }
}
