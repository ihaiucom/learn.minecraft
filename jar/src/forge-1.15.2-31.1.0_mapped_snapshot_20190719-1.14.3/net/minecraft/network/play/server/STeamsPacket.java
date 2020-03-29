package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class STeamsPacket implements IPacket<IClientPlayNetHandler> {
   private String name = "";
   private ITextComponent displayName = new StringTextComponent("");
   private ITextComponent prefix = new StringTextComponent("");
   private ITextComponent suffix = new StringTextComponent("");
   private String nameTagVisibility;
   private String collisionRule;
   private TextFormatting color;
   private final Collection<String> players;
   private int action;
   private int friendlyFlags;

   public STeamsPacket() {
      this.nameTagVisibility = Team.Visible.ALWAYS.internalName;
      this.collisionRule = Team.CollisionRule.ALWAYS.name;
      this.color = TextFormatting.RESET;
      this.players = Lists.newArrayList();
   }

   public STeamsPacket(ScorePlayerTeam p_i46907_1_, int p_i46907_2_) {
      this.nameTagVisibility = Team.Visible.ALWAYS.internalName;
      this.collisionRule = Team.CollisionRule.ALWAYS.name;
      this.color = TextFormatting.RESET;
      this.players = Lists.newArrayList();
      this.name = p_i46907_1_.getName();
      this.action = p_i46907_2_;
      if (p_i46907_2_ == 0 || p_i46907_2_ == 2) {
         this.displayName = p_i46907_1_.getDisplayName();
         this.friendlyFlags = p_i46907_1_.getFriendlyFlags();
         this.nameTagVisibility = p_i46907_1_.getNameTagVisibility().internalName;
         this.collisionRule = p_i46907_1_.getCollisionRule().name;
         this.color = p_i46907_1_.getColor();
         this.prefix = p_i46907_1_.getPrefix();
         this.suffix = p_i46907_1_.getSuffix();
      }

      if (p_i46907_2_ == 0) {
         this.players.addAll(p_i46907_1_.getMembershipCollection());
      }

   }

   public STeamsPacket(ScorePlayerTeam p_i46908_1_, Collection<String> p_i46908_2_, int p_i46908_3_) {
      this.nameTagVisibility = Team.Visible.ALWAYS.internalName;
      this.collisionRule = Team.CollisionRule.ALWAYS.name;
      this.color = TextFormatting.RESET;
      this.players = Lists.newArrayList();
      if (p_i46908_3_ != 3 && p_i46908_3_ != 4) {
         throw new IllegalArgumentException("Method must be join or leave for player constructor");
      } else if (p_i46908_2_ != null && !p_i46908_2_.isEmpty()) {
         this.action = p_i46908_3_;
         this.name = p_i46908_1_.getName();
         this.players.addAll(p_i46908_2_);
      } else {
         throw new IllegalArgumentException("Players cannot be null/empty");
      }
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.name = p_148837_1_.readString(16);
      this.action = p_148837_1_.readByte();
      if (this.action == 0 || this.action == 2) {
         this.displayName = p_148837_1_.readTextComponent();
         this.friendlyFlags = p_148837_1_.readByte();
         this.nameTagVisibility = p_148837_1_.readString(40);
         this.collisionRule = p_148837_1_.readString(40);
         this.color = (TextFormatting)p_148837_1_.readEnumValue(TextFormatting.class);
         this.prefix = p_148837_1_.readTextComponent();
         this.suffix = p_148837_1_.readTextComponent();
      }

      if (this.action == 0 || this.action == 3 || this.action == 4) {
         int lvt_2_1_ = p_148837_1_.readVarInt();

         for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_) {
            this.players.add(p_148837_1_.readString(40));
         }
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeString(this.name);
      p_148840_1_.writeByte(this.action);
      if (this.action == 0 || this.action == 2) {
         p_148840_1_.writeTextComponent(this.displayName);
         p_148840_1_.writeByte(this.friendlyFlags);
         p_148840_1_.writeString(this.nameTagVisibility);
         p_148840_1_.writeString(this.collisionRule);
         p_148840_1_.writeEnumValue(this.color);
         p_148840_1_.writeTextComponent(this.prefix);
         p_148840_1_.writeTextComponent(this.suffix);
      }

      if (this.action == 0 || this.action == 3 || this.action == 4) {
         p_148840_1_.writeVarInt(this.players.size());
         Iterator var2 = this.players.iterator();

         while(var2.hasNext()) {
            String lvt_3_1_ = (String)var2.next();
            p_148840_1_.writeString(lvt_3_1_);
         }
      }

   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleTeams(this);
   }

   @OnlyIn(Dist.CLIENT)
   public String getName() {
      return this.name;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getDisplayName() {
      return this.displayName;
   }

   @OnlyIn(Dist.CLIENT)
   public Collection<String> getPlayers() {
      return this.players;
   }

   @OnlyIn(Dist.CLIENT)
   public int getAction() {
      return this.action;
   }

   @OnlyIn(Dist.CLIENT)
   public int getFriendlyFlags() {
      return this.friendlyFlags;
   }

   @OnlyIn(Dist.CLIENT)
   public TextFormatting getColor() {
      return this.color;
   }

   @OnlyIn(Dist.CLIENT)
   public String getNameTagVisibility() {
      return this.nameTagVisibility;
   }

   @OnlyIn(Dist.CLIENT)
   public String getCollisionRule() {
      return this.collisionRule;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getPrefix() {
      return this.prefix;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getSuffix() {
      return this.suffix;
   }
}
