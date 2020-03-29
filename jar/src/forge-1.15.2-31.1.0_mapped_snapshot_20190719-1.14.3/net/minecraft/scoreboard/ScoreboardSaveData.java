package net.minecraft.scoreboard;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreboardSaveData extends WorldSavedData {
   private static final Logger LOGGER = LogManager.getLogger();
   private Scoreboard scoreboard;
   private CompoundNBT delayedInitNbt;

   public ScoreboardSaveData() {
      super("scoreboard");
   }

   public void setScoreboard(Scoreboard p_96499_1_) {
      this.scoreboard = p_96499_1_;
      if (this.delayedInitNbt != null) {
         this.read(this.delayedInitNbt);
      }

   }

   public void read(CompoundNBT p_76184_1_) {
      if (this.scoreboard == null) {
         this.delayedInitNbt = p_76184_1_;
      } else {
         this.readObjectives(p_76184_1_.getList("Objectives", 10));
         this.scoreboard.func_197905_a(p_76184_1_.getList("PlayerScores", 10));
         if (p_76184_1_.contains("DisplaySlots", 10)) {
            this.readDisplayConfig(p_76184_1_.getCompound("DisplaySlots"));
         }

         if (p_76184_1_.contains("Teams", 9)) {
            this.readTeams(p_76184_1_.getList("Teams", 10));
         }

      }
   }

   protected void readTeams(ListNBT p_96498_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < p_96498_1_.size(); ++lvt_2_1_) {
         CompoundNBT lvt_3_1_ = p_96498_1_.getCompound(lvt_2_1_);
         String lvt_4_1_ = lvt_3_1_.getString("Name");
         if (lvt_4_1_.length() > 16) {
            lvt_4_1_ = lvt_4_1_.substring(0, 16);
         }

         ScorePlayerTeam lvt_5_1_ = this.scoreboard.createTeam(lvt_4_1_);
         ITextComponent lvt_6_1_ = ITextComponent.Serializer.fromJson(lvt_3_1_.getString("DisplayName"));
         if (lvt_6_1_ != null) {
            lvt_5_1_.setDisplayName(lvt_6_1_);
         }

         if (lvt_3_1_.contains("TeamColor", 8)) {
            lvt_5_1_.setColor(TextFormatting.getValueByName(lvt_3_1_.getString("TeamColor")));
         }

         if (lvt_3_1_.contains("AllowFriendlyFire", 99)) {
            lvt_5_1_.setAllowFriendlyFire(lvt_3_1_.getBoolean("AllowFriendlyFire"));
         }

         if (lvt_3_1_.contains("SeeFriendlyInvisibles", 99)) {
            lvt_5_1_.setSeeFriendlyInvisiblesEnabled(lvt_3_1_.getBoolean("SeeFriendlyInvisibles"));
         }

         ITextComponent lvt_7_2_;
         if (lvt_3_1_.contains("MemberNamePrefix", 8)) {
            lvt_7_2_ = ITextComponent.Serializer.fromJson(lvt_3_1_.getString("MemberNamePrefix"));
            if (lvt_7_2_ != null) {
               lvt_5_1_.setPrefix(lvt_7_2_);
            }
         }

         if (lvt_3_1_.contains("MemberNameSuffix", 8)) {
            lvt_7_2_ = ITextComponent.Serializer.fromJson(lvt_3_1_.getString("MemberNameSuffix"));
            if (lvt_7_2_ != null) {
               lvt_5_1_.setSuffix(lvt_7_2_);
            }
         }

         Team.Visible lvt_7_4_;
         if (lvt_3_1_.contains("NameTagVisibility", 8)) {
            lvt_7_4_ = Team.Visible.getByName(lvt_3_1_.getString("NameTagVisibility"));
            if (lvt_7_4_ != null) {
               lvt_5_1_.setNameTagVisibility(lvt_7_4_);
            }
         }

         if (lvt_3_1_.contains("DeathMessageVisibility", 8)) {
            lvt_7_4_ = Team.Visible.getByName(lvt_3_1_.getString("DeathMessageVisibility"));
            if (lvt_7_4_ != null) {
               lvt_5_1_.setDeathMessageVisibility(lvt_7_4_);
            }
         }

         if (lvt_3_1_.contains("CollisionRule", 8)) {
            Team.CollisionRule lvt_7_5_ = Team.CollisionRule.getByName(lvt_3_1_.getString("CollisionRule"));
            if (lvt_7_5_ != null) {
               lvt_5_1_.setCollisionRule(lvt_7_5_);
            }
         }

         this.loadTeamPlayers(lvt_5_1_, lvt_3_1_.getList("Players", 8));
      }

   }

   protected void loadTeamPlayers(ScorePlayerTeam p_96502_1_, ListNBT p_96502_2_) {
      for(int lvt_3_1_ = 0; lvt_3_1_ < p_96502_2_.size(); ++lvt_3_1_) {
         this.scoreboard.addPlayerToTeam(p_96502_2_.getString(lvt_3_1_), p_96502_1_);
      }

   }

   protected void readDisplayConfig(CompoundNBT p_96504_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < 19; ++lvt_2_1_) {
         if (p_96504_1_.contains("slot_" + lvt_2_1_, 8)) {
            String lvt_3_1_ = p_96504_1_.getString("slot_" + lvt_2_1_);
            ScoreObjective lvt_4_1_ = this.scoreboard.getObjective(lvt_3_1_);
            this.scoreboard.setObjectiveInDisplaySlot(lvt_2_1_, lvt_4_1_);
         }
      }

   }

   protected void readObjectives(ListNBT p_96501_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < p_96501_1_.size(); ++lvt_2_1_) {
         CompoundNBT lvt_3_1_ = p_96501_1_.getCompound(lvt_2_1_);
         ScoreCriteria.func_216390_a(lvt_3_1_.getString("CriteriaName")).ifPresent((p_215164_2_) -> {
            String lvt_3_1_x = lvt_3_1_.getString("Name");
            if (lvt_3_1_x.length() > 16) {
               lvt_3_1_x = lvt_3_1_x.substring(0, 16);
            }

            ITextComponent lvt_4_1_ = ITextComponent.Serializer.fromJson(lvt_3_1_.getString("DisplayName"));
            ScoreCriteria.RenderType lvt_5_1_ = ScoreCriteria.RenderType.byId(lvt_3_1_.getString("RenderType"));
            this.scoreboard.addObjective(lvt_3_1_x, p_215164_2_, lvt_4_1_, lvt_5_1_);
         });
      }

   }

   public CompoundNBT write(CompoundNBT p_189551_1_) {
      if (this.scoreboard == null) {
         LOGGER.warn("Tried to save scoreboard without having a scoreboard...");
         return p_189551_1_;
      } else {
         p_189551_1_.put("Objectives", this.objectivesToNbt());
         p_189551_1_.put("PlayerScores", this.scoreboard.func_197902_i());
         p_189551_1_.put("Teams", this.teamsToNbt());
         this.fillInDisplaySlots(p_189551_1_);
         return p_189551_1_;
      }
   }

   protected ListNBT teamsToNbt() {
      ListNBT lvt_1_1_ = new ListNBT();
      Collection<ScorePlayerTeam> lvt_2_1_ = this.scoreboard.getTeams();
      Iterator var3 = lvt_2_1_.iterator();

      while(var3.hasNext()) {
         ScorePlayerTeam lvt_4_1_ = (ScorePlayerTeam)var3.next();
         CompoundNBT lvt_5_1_ = new CompoundNBT();
         lvt_5_1_.putString("Name", lvt_4_1_.getName());
         lvt_5_1_.putString("DisplayName", ITextComponent.Serializer.toJson(lvt_4_1_.getDisplayName()));
         if (lvt_4_1_.getColor().getColorIndex() >= 0) {
            lvt_5_1_.putString("TeamColor", lvt_4_1_.getColor().getFriendlyName());
         }

         lvt_5_1_.putBoolean("AllowFriendlyFire", lvt_4_1_.getAllowFriendlyFire());
         lvt_5_1_.putBoolean("SeeFriendlyInvisibles", lvt_4_1_.getSeeFriendlyInvisiblesEnabled());
         lvt_5_1_.putString("MemberNamePrefix", ITextComponent.Serializer.toJson(lvt_4_1_.getPrefix()));
         lvt_5_1_.putString("MemberNameSuffix", ITextComponent.Serializer.toJson(lvt_4_1_.getSuffix()));
         lvt_5_1_.putString("NameTagVisibility", lvt_4_1_.getNameTagVisibility().internalName);
         lvt_5_1_.putString("DeathMessageVisibility", lvt_4_1_.getDeathMessageVisibility().internalName);
         lvt_5_1_.putString("CollisionRule", lvt_4_1_.getCollisionRule().name);
         ListNBT lvt_6_1_ = new ListNBT();
         Iterator var7 = lvt_4_1_.getMembershipCollection().iterator();

         while(var7.hasNext()) {
            String lvt_8_1_ = (String)var7.next();
            lvt_6_1_.add(StringNBT.func_229705_a_(lvt_8_1_));
         }

         lvt_5_1_.put("Players", lvt_6_1_);
         lvt_1_1_.add(lvt_5_1_);
      }

      return lvt_1_1_;
   }

   protected void fillInDisplaySlots(CompoundNBT p_96497_1_) {
      CompoundNBT lvt_2_1_ = new CompoundNBT();
      boolean lvt_3_1_ = false;

      for(int lvt_4_1_ = 0; lvt_4_1_ < 19; ++lvt_4_1_) {
         ScoreObjective lvt_5_1_ = this.scoreboard.getObjectiveInDisplaySlot(lvt_4_1_);
         if (lvt_5_1_ != null) {
            lvt_2_1_.putString("slot_" + lvt_4_1_, lvt_5_1_.getName());
            lvt_3_1_ = true;
         }
      }

      if (lvt_3_1_) {
         p_96497_1_.put("DisplaySlots", lvt_2_1_);
      }

   }

   protected ListNBT objectivesToNbt() {
      ListNBT lvt_1_1_ = new ListNBT();
      Collection<ScoreObjective> lvt_2_1_ = this.scoreboard.getScoreObjectives();
      Iterator var3 = lvt_2_1_.iterator();

      while(var3.hasNext()) {
         ScoreObjective lvt_4_1_ = (ScoreObjective)var3.next();
         if (lvt_4_1_.getCriteria() != null) {
            CompoundNBT lvt_5_1_ = new CompoundNBT();
            lvt_5_1_.putString("Name", lvt_4_1_.getName());
            lvt_5_1_.putString("CriteriaName", lvt_4_1_.getCriteria().getName());
            lvt_5_1_.putString("DisplayName", ITextComponent.Serializer.toJson(lvt_4_1_.getDisplayName()));
            lvt_5_1_.putString("RenderType", lvt_4_1_.getRenderType().getId());
            lvt_1_1_.add(lvt_5_1_);
         }
      }

      return lvt_1_1_;
   }
}
