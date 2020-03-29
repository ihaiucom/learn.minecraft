package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SScoreboardObjectivePacket implements IPacket<IClientPlayNetHandler> {
   private String objectiveName;
   private ITextComponent displayName;
   private ScoreCriteria.RenderType renderType;
   private int action;

   public SScoreboardObjectivePacket() {
   }

   public SScoreboardObjectivePacket(ScoreObjective p_i46910_1_, int p_i46910_2_) {
      this.objectiveName = p_i46910_1_.getName();
      this.displayName = p_i46910_1_.getDisplayName();
      this.renderType = p_i46910_1_.getRenderType();
      this.action = p_i46910_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.objectiveName = p_148837_1_.readString(16);
      this.action = p_148837_1_.readByte();
      if (this.action == 0 || this.action == 2) {
         this.displayName = p_148837_1_.readTextComponent();
         this.renderType = (ScoreCriteria.RenderType)p_148837_1_.readEnumValue(ScoreCriteria.RenderType.class);
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeString(this.objectiveName);
      p_148840_1_.writeByte(this.action);
      if (this.action == 0 || this.action == 2) {
         p_148840_1_.writeTextComponent(this.displayName);
         p_148840_1_.writeEnumValue(this.renderType);
      }

   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleScoreboardObjective(this);
   }

   @OnlyIn(Dist.CLIENT)
   public String getObjectiveName() {
      return this.objectiveName;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getDisplayName() {
      return this.displayName;
   }

   @OnlyIn(Dist.CLIENT)
   public int getAction() {
      return this.action;
   }

   @OnlyIn(Dist.CLIENT)
   public ScoreCriteria.RenderType getRenderType() {
      return this.renderType;
   }
}
