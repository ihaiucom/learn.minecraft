package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class STabCompletePacket implements IPacket<IClientPlayNetHandler> {
   private int transactionId;
   private Suggestions suggestions;

   public STabCompletePacket() {
   }

   public STabCompletePacket(int p_i47941_1_, Suggestions p_i47941_2_) {
      this.transactionId = p_i47941_1_;
      this.suggestions = p_i47941_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.transactionId = p_148837_1_.readVarInt();
      int lvt_2_1_ = p_148837_1_.readVarInt();
      int lvt_3_1_ = p_148837_1_.readVarInt();
      StringRange lvt_4_1_ = StringRange.between(lvt_2_1_, lvt_2_1_ + lvt_3_1_);
      int lvt_5_1_ = p_148837_1_.readVarInt();
      List<Suggestion> lvt_6_1_ = Lists.newArrayListWithCapacity(lvt_5_1_);

      for(int lvt_7_1_ = 0; lvt_7_1_ < lvt_5_1_; ++lvt_7_1_) {
         String lvt_8_1_ = p_148837_1_.readString(32767);
         ITextComponent lvt_9_1_ = p_148837_1_.readBoolean() ? p_148837_1_.readTextComponent() : null;
         lvt_6_1_.add(new Suggestion(lvt_4_1_, lvt_8_1_, lvt_9_1_));
      }

      this.suggestions = new Suggestions(lvt_4_1_, lvt_6_1_);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.transactionId);
      p_148840_1_.writeVarInt(this.suggestions.getRange().getStart());
      p_148840_1_.writeVarInt(this.suggestions.getRange().getLength());
      p_148840_1_.writeVarInt(this.suggestions.getList().size());
      Iterator var2 = this.suggestions.getList().iterator();

      while(var2.hasNext()) {
         Suggestion lvt_3_1_ = (Suggestion)var2.next();
         p_148840_1_.writeString(lvt_3_1_.getText());
         p_148840_1_.writeBoolean(lvt_3_1_.getTooltip() != null);
         if (lvt_3_1_.getTooltip() != null) {
            p_148840_1_.writeTextComponent(TextComponentUtils.toTextComponent(lvt_3_1_.getTooltip()));
         }
      }

   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleTabComplete(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getTransactionId() {
      return this.transactionId;
   }

   @OnlyIn(Dist.CLIENT)
   public Suggestions getSuggestions() {
      return this.suggestions;
   }
}
