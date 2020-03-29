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

public class SJoinGamePacket implements IPacket<IClientPlayNetHandler> {
   private int playerId;
   private long field_229740_b_;
   private boolean hardcoreMode;
   private GameType gameType;
   private DimensionType dimension;
   private int maxPlayers;
   private WorldType worldType;
   private int field_218729_g;
   private boolean reducedDebugInfo;
   private boolean field_229741_j_;
   private int dimensionInt;

   public SJoinGamePacket() {
   }

   public SJoinGamePacket(int p_i226090_1_, GameType p_i226090_2_, long p_i226090_3_, boolean p_i226090_5_, DimensionType p_i226090_6_, int p_i226090_7_, WorldType p_i226090_8_, int p_i226090_9_, boolean p_i226090_10_, boolean p_i226090_11_) {
      this.playerId = p_i226090_1_;
      this.dimension = p_i226090_6_;
      this.field_229740_b_ = p_i226090_3_;
      this.gameType = p_i226090_2_;
      this.maxPlayers = p_i226090_7_;
      this.hardcoreMode = p_i226090_5_;
      this.worldType = p_i226090_8_;
      this.field_218729_g = p_i226090_9_;
      this.reducedDebugInfo = p_i226090_10_;
      this.field_229741_j_ = p_i226090_11_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.playerId = p_148837_1_.readInt();
      int i = p_148837_1_.readUnsignedByte();
      this.hardcoreMode = (i & 8) == 8;
      int i = i & -9;
      this.gameType = GameType.getByID(i);
      this.dimensionInt = p_148837_1_.readInt();
      this.field_229740_b_ = p_148837_1_.readLong();
      this.maxPlayers = p_148837_1_.readUnsignedByte();
      this.worldType = WorldType.byName(p_148837_1_.readString(16));
      if (this.worldType == null) {
         this.worldType = WorldType.DEFAULT;
      }

      this.field_218729_g = p_148837_1_.readVarInt();
      this.reducedDebugInfo = p_148837_1_.readBoolean();
      this.field_229741_j_ = p_148837_1_.readBoolean();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(this.playerId);
      int i = this.gameType.getID();
      if (this.hardcoreMode) {
         i |= 8;
      }

      p_148840_1_.writeByte(i);
      p_148840_1_.writeInt(this.dimension.getId());
      p_148840_1_.writeLong(this.field_229740_b_);
      p_148840_1_.writeByte(this.maxPlayers);
      p_148840_1_.writeString(this.worldType.getName());
      p_148840_1_.writeVarInt(this.field_218729_g);
      p_148840_1_.writeBoolean(this.reducedDebugInfo);
      p_148840_1_.writeBoolean(this.field_229741_j_);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleJoinGame(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getPlayerId() {
      return this.playerId;
   }

   @OnlyIn(Dist.CLIENT)
   public long func_229742_c_() {
      return this.field_229740_b_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isHardcoreMode() {
      return this.hardcoreMode;
   }

   @OnlyIn(Dist.CLIENT)
   public GameType getGameType() {
      return this.gameType;
   }

   @OnlyIn(Dist.CLIENT)
   public DimensionType getDimension() {
      return this.dimension == null ? (this.dimension = NetworkHooks.getDummyDimType(this.dimensionInt)) : this.dimension;
   }

   @OnlyIn(Dist.CLIENT)
   public WorldType getWorldType() {
      return this.worldType;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_218728_h() {
      return this.field_218729_g;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isReducedDebugInfo() {
      return this.reducedDebugInfo;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_229743_k_() {
      return this.field_229741_j_;
   }
}
