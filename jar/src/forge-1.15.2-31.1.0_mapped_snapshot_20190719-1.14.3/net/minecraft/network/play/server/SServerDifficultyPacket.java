package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.Difficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SServerDifficultyPacket implements IPacket<IClientPlayNetHandler> {
   private Difficulty difficulty;
   private boolean difficultyLocked;

   public SServerDifficultyPacket() {
   }

   public SServerDifficultyPacket(Difficulty p_i46963_1_, boolean p_i46963_2_) {
      this.difficulty = p_i46963_1_;
      this.difficultyLocked = p_i46963_2_;
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleServerDifficulty(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.difficulty = Difficulty.byId(p_148837_1_.readUnsignedByte());
      this.difficultyLocked = p_148837_1_.readBoolean();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.difficulty.getId());
      p_148840_1_.writeBoolean(this.difficultyLocked);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isDifficultyLocked() {
      return this.difficultyLocked;
   }

   @OnlyIn(Dist.CLIENT)
   public Difficulty getDifficulty() {
      return this.difficulty;
   }
}
