package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnPaintingPacket implements IPacket<IClientPlayNetHandler> {
   private int entityID;
   private UUID uniqueId;
   private BlockPos position;
   private Direction facing;
   private int title;

   public SSpawnPaintingPacket() {
   }

   public SSpawnPaintingPacket(PaintingEntity p_i46972_1_) {
      this.entityID = p_i46972_1_.getEntityId();
      this.uniqueId = p_i46972_1_.getUniqueID();
      this.position = p_i46972_1_.getHangingPosition();
      this.facing = p_i46972_1_.getHorizontalFacing();
      this.title = Registry.MOTIVE.getId(p_i46972_1_.art);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityID = p_148837_1_.readVarInt();
      this.uniqueId = p_148837_1_.readUniqueId();
      this.title = p_148837_1_.readVarInt();
      this.position = p_148837_1_.readBlockPos();
      this.facing = Direction.byHorizontalIndex(p_148837_1_.readUnsignedByte());
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityID);
      p_148840_1_.writeUniqueId(this.uniqueId);
      p_148840_1_.writeVarInt(this.title);
      p_148840_1_.writeBlockPos(this.position);
      p_148840_1_.writeByte(this.facing.getHorizontalIndex());
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSpawnPainting(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityID() {
      return this.entityID;
   }

   @OnlyIn(Dist.CLIENT)
   public UUID getUniqueId() {
      return this.uniqueId;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPosition() {
      return this.position;
   }

   @OnlyIn(Dist.CLIENT)
   public Direction getFacing() {
      return this.facing;
   }

   @OnlyIn(Dist.CLIENT)
   public PaintingType getType() {
      return (PaintingType)Registry.MOTIVE.getByValue(this.title);
   }
}
