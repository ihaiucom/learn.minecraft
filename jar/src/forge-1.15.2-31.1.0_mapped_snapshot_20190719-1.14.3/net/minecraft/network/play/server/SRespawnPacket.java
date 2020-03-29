package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class SRespawnPacket implements IPacket<IClientPlayNetHandler> {
   private DimensionType dimensionID;
   private long field_229746_b_;
   private GameType gameType;
   private WorldType worldType;
   private int dimensionInt;

   public SRespawnPacket() {
   }

   public SRespawnPacket(DimensionType p_i226091_1_, long p_i226091_2_, WorldType p_i226091_4_, GameType p_i226091_5_) {
      this.dimensionID = p_i226091_1_;
      this.field_229746_b_ = p_i226091_2_;
      this.gameType = p_i226091_5_;
      this.worldType = p_i226091_4_;
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleRespawn(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.dimensionInt = p_148837_1_.readInt();
      this.field_229746_b_ = p_148837_1_.readLong();
      this.gameType = GameType.getByID(p_148837_1_.readUnsignedByte());
      this.worldType = WorldType.byName(p_148837_1_.readString(16));
      if (this.worldType == null) {
         this.worldType = WorldType.DEFAULT;
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(this.dimensionID.getId());
      p_148840_1_.writeLong(this.field_229746_b_);
      p_148840_1_.writeByte(this.gameType.getID());
      p_148840_1_.writeString(this.worldType.getName());
   }

   @OnlyIn(Dist.CLIENT)
   public DimensionType getDimension() {
      return this.dimensionID == null ? (this.dimensionID = NetworkHooks.getDummyDimType(this.dimensionInt)) : this.dimensionID;
   }

   @OnlyIn(Dist.CLIENT)
   public long func_229747_c_() {
      return this.field_229746_b_;
   }

   @OnlyIn(Dist.CLIENT)
   public GameType getGameType() {
      return this.gameType;
   }

   @OnlyIn(Dist.CLIENT)
   public WorldType getWorldType() {
      return this.worldType;
   }
}
