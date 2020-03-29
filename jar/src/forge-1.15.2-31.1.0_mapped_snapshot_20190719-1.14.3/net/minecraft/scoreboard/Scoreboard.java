package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Scoreboard {
   private final Map<String, ScoreObjective> scoreObjectives = Maps.newHashMap();
   private final Map<ScoreCriteria, List<ScoreObjective>> scoreObjectiveCriterias = Maps.newHashMap();
   private final Map<String, Map<ScoreObjective, Score>> entitiesScoreObjectives = Maps.newHashMap();
   private final ScoreObjective[] objectiveDisplaySlots = new ScoreObjective[19];
   private final Map<String, ScorePlayerTeam> teams = Maps.newHashMap();
   private final Map<String, ScorePlayerTeam> teamMemberships = Maps.newHashMap();
   private static String[] displaySlots;

   @OnlyIn(Dist.CLIENT)
   public boolean hasObjective(String p_197900_1_) {
      return this.scoreObjectives.containsKey(p_197900_1_);
   }

   public ScoreObjective getOrCreateObjective(String p_197899_1_) {
      return (ScoreObjective)this.scoreObjectives.get(p_197899_1_);
   }

   @Nullable
   public ScoreObjective getObjective(@Nullable String p_96518_1_) {
      return (ScoreObjective)this.scoreObjectives.get(p_96518_1_);
   }

   public ScoreObjective addObjective(String p_199868_1_, ScoreCriteria p_199868_2_, ITextComponent p_199868_3_, ScoreCriteria.RenderType p_199868_4_) {
      if (p_199868_1_.length() > 16) {
         throw new IllegalArgumentException("The objective name '" + p_199868_1_ + "' is too long!");
      } else if (this.scoreObjectives.containsKey(p_199868_1_)) {
         throw new IllegalArgumentException("An objective with the name '" + p_199868_1_ + "' already exists!");
      } else {
         ScoreObjective lvt_5_1_ = new ScoreObjective(this, p_199868_1_, p_199868_2_, p_199868_3_, p_199868_4_);
         ((List)this.scoreObjectiveCriterias.computeIfAbsent(p_199868_2_, (p_197903_0_) -> {
            return Lists.newArrayList();
         })).add(lvt_5_1_);
         this.scoreObjectives.put(p_199868_1_, lvt_5_1_);
         this.onObjectiveAdded(lvt_5_1_);
         return lvt_5_1_;
      }
   }

   public final void forAllObjectives(ScoreCriteria p_197893_1_, String p_197893_2_, Consumer<Score> p_197893_3_) {
      ((List)this.scoreObjectiveCriterias.getOrDefault(p_197893_1_, Collections.emptyList())).forEach((p_197906_3_) -> {
         p_197893_3_.accept(this.getOrCreateScore(p_197893_2_, p_197906_3_));
      });
   }

   public boolean entityHasObjective(String p_178819_1_, ScoreObjective p_178819_2_) {
      Map<ScoreObjective, Score> lvt_3_1_ = (Map)this.entitiesScoreObjectives.get(p_178819_1_);
      if (lvt_3_1_ == null) {
         return false;
      } else {
         Score lvt_4_1_ = (Score)lvt_3_1_.get(p_178819_2_);
         return lvt_4_1_ != null;
      }
   }

   public Score getOrCreateScore(String p_96529_1_, ScoreObjective p_96529_2_) {
      if (p_96529_1_.length() > 40) {
         throw new IllegalArgumentException("The player name '" + p_96529_1_ + "' is too long!");
      } else {
         Map<ScoreObjective, Score> lvt_3_1_ = (Map)this.entitiesScoreObjectives.computeIfAbsent(p_96529_1_, (p_197898_0_) -> {
            return Maps.newHashMap();
         });
         return (Score)lvt_3_1_.computeIfAbsent(p_96529_2_, (p_197904_2_) -> {
            Score lvt_3_1_ = new Score(this, p_197904_2_, p_96529_1_);
            lvt_3_1_.setScorePoints(0);
            return lvt_3_1_;
         });
      }
   }

   public Collection<Score> getSortedScores(ScoreObjective p_96534_1_) {
      List<Score> lvt_2_1_ = Lists.newArrayList();
      Iterator var3 = this.entitiesScoreObjectives.values().iterator();

      while(var3.hasNext()) {
         Map<ScoreObjective, Score> lvt_4_1_ = (Map)var3.next();
         Score lvt_5_1_ = (Score)lvt_4_1_.get(p_96534_1_);
         if (lvt_5_1_ != null) {
            lvt_2_1_.add(lvt_5_1_);
         }
      }

      lvt_2_1_.sort(Score.SCORE_COMPARATOR);
      return lvt_2_1_;
   }

   public Collection<ScoreObjective> getScoreObjectives() {
      return this.scoreObjectives.values();
   }

   public Collection<String> func_197897_d() {
      return this.scoreObjectives.keySet();
   }

   public Collection<String> getObjectiveNames() {
      return Lists.newArrayList(this.entitiesScoreObjectives.keySet());
   }

   public void removeObjectiveFromEntity(String p_178822_1_, @Nullable ScoreObjective p_178822_2_) {
      Map lvt_3_2_;
      if (p_178822_2_ == null) {
         lvt_3_2_ = (Map)this.entitiesScoreObjectives.remove(p_178822_1_);
         if (lvt_3_2_ != null) {
            this.onPlayerRemoved(p_178822_1_);
         }
      } else {
         lvt_3_2_ = (Map)this.entitiesScoreObjectives.get(p_178822_1_);
         if (lvt_3_2_ != null) {
            Score lvt_4_1_ = (Score)lvt_3_2_.remove(p_178822_2_);
            if (lvt_3_2_.size() < 1) {
               Map<ScoreObjective, Score> lvt_5_1_ = (Map)this.entitiesScoreObjectives.remove(p_178822_1_);
               if (lvt_5_1_ != null) {
                  this.onPlayerRemoved(p_178822_1_);
               }
            } else if (lvt_4_1_ != null) {
               this.onPlayerScoreRemoved(p_178822_1_, p_178822_2_);
            }
         }
      }

   }

   public Map<ScoreObjective, Score> getObjectivesForEntity(String p_96510_1_) {
      Map<ScoreObjective, Score> lvt_2_1_ = (Map)this.entitiesScoreObjectives.get(p_96510_1_);
      if (lvt_2_1_ == null) {
         lvt_2_1_ = Maps.newHashMap();
      }

      return (Map)lvt_2_1_;
   }

   public void removeObjective(ScoreObjective p_96519_1_) {
      this.scoreObjectives.remove(p_96519_1_.getName());

      for(int lvt_2_1_ = 0; lvt_2_1_ < 19; ++lvt_2_1_) {
         if (this.getObjectiveInDisplaySlot(lvt_2_1_) == p_96519_1_) {
            this.setObjectiveInDisplaySlot(lvt_2_1_, (ScoreObjective)null);
         }
      }

      List<ScoreObjective> lvt_2_2_ = (List)this.scoreObjectiveCriterias.get(p_96519_1_.getCriteria());
      if (lvt_2_2_ != null) {
         lvt_2_2_.remove(p_96519_1_);
      }

      Iterator var3 = this.entitiesScoreObjectives.values().iterator();

      while(var3.hasNext()) {
         Map<ScoreObjective, Score> lvt_4_1_ = (Map)var3.next();
         lvt_4_1_.remove(p_96519_1_);
      }

      this.onObjectiveRemoved(p_96519_1_);
   }

   public void setObjectiveInDisplaySlot(int p_96530_1_, @Nullable ScoreObjective p_96530_2_) {
      this.objectiveDisplaySlots[p_96530_1_] = p_96530_2_;
   }

   @Nullable
   public ScoreObjective getObjectiveInDisplaySlot(int p_96539_1_) {
      return this.objectiveDisplaySlots[p_96539_1_];
   }

   public ScorePlayerTeam getTeam(String p_96508_1_) {
      return (ScorePlayerTeam)this.teams.get(p_96508_1_);
   }

   public ScorePlayerTeam createTeam(String p_96527_1_) {
      if (p_96527_1_.length() > 16) {
         throw new IllegalArgumentException("The team name '" + p_96527_1_ + "' is too long!");
      } else {
         ScorePlayerTeam lvt_2_1_ = this.getTeam(p_96527_1_);
         if (lvt_2_1_ != null) {
            throw new IllegalArgumentException("A team with the name '" + p_96527_1_ + "' already exists!");
         } else {
            lvt_2_1_ = new ScorePlayerTeam(this, p_96527_1_);
            this.teams.put(p_96527_1_, lvt_2_1_);
            this.onTeamAdded(lvt_2_1_);
            return lvt_2_1_;
         }
      }
   }

   public void removeTeam(ScorePlayerTeam p_96511_1_) {
      this.teams.remove(p_96511_1_.getName());
      Iterator var2 = p_96511_1_.getMembershipCollection().iterator();

      while(var2.hasNext()) {
         String lvt_3_1_ = (String)var2.next();
         this.teamMemberships.remove(lvt_3_1_);
      }

      this.onTeamRemoved(p_96511_1_);
   }

   public boolean addPlayerToTeam(String p_197901_1_, ScorePlayerTeam p_197901_2_) {
      if (p_197901_1_.length() > 40) {
         throw new IllegalArgumentException("The player name '" + p_197901_1_ + "' is too long!");
      } else {
         if (this.getPlayersTeam(p_197901_1_) != null) {
            this.removePlayerFromTeams(p_197901_1_);
         }

         this.teamMemberships.put(p_197901_1_, p_197901_2_);
         return p_197901_2_.getMembershipCollection().add(p_197901_1_);
      }
   }

   public boolean removePlayerFromTeams(String p_96524_1_) {
      ScorePlayerTeam lvt_2_1_ = this.getPlayersTeam(p_96524_1_);
      if (lvt_2_1_ != null) {
         this.removePlayerFromTeam(p_96524_1_, lvt_2_1_);
         return true;
      } else {
         return false;
      }
   }

   public void removePlayerFromTeam(String p_96512_1_, ScorePlayerTeam p_96512_2_) {
      if (this.getPlayersTeam(p_96512_1_) != p_96512_2_) {
         throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + p_96512_2_.getName() + "'.");
      } else {
         this.teamMemberships.remove(p_96512_1_);
         p_96512_2_.getMembershipCollection().remove(p_96512_1_);
      }
   }

   public Collection<String> getTeamNames() {
      return this.teams.keySet();
   }

   public Collection<ScorePlayerTeam> getTeams() {
      return this.teams.values();
   }

   @Nullable
   public ScorePlayerTeam getPlayersTeam(String p_96509_1_) {
      return (ScorePlayerTeam)this.teamMemberships.get(p_96509_1_);
   }

   public void onObjectiveAdded(ScoreObjective p_96522_1_) {
   }

   public void onObjectiveChanged(ScoreObjective p_199869_1_) {
   }

   public void onObjectiveRemoved(ScoreObjective p_96533_1_) {
   }

   public void onScoreChanged(Score p_96536_1_) {
   }

   public void onPlayerRemoved(String p_96516_1_) {
   }

   public void onPlayerScoreRemoved(String p_178820_1_, ScoreObjective p_178820_2_) {
   }

   public void onTeamAdded(ScorePlayerTeam p_96523_1_) {
   }

   public void onTeamChanged(ScorePlayerTeam p_96538_1_) {
   }

   public void onTeamRemoved(ScorePlayerTeam p_96513_1_) {
   }

   public static String getObjectiveDisplaySlot(int p_96517_0_) {
      switch(p_96517_0_) {
      case 0:
         return "list";
      case 1:
         return "sidebar";
      case 2:
         return "belowName";
      default:
         if (p_96517_0_ >= 3 && p_96517_0_ <= 18) {
            TextFormatting lvt_1_1_ = TextFormatting.fromColorIndex(p_96517_0_ - 3);
            if (lvt_1_1_ != null && lvt_1_1_ != TextFormatting.RESET) {
               return "sidebar.team." + lvt_1_1_.getFriendlyName();
            }
         }

         return null;
      }
   }

   public static int getObjectiveDisplaySlotNumber(String p_96537_0_) {
      if ("list".equalsIgnoreCase(p_96537_0_)) {
         return 0;
      } else if ("sidebar".equalsIgnoreCase(p_96537_0_)) {
         return 1;
      } else if ("belowName".equalsIgnoreCase(p_96537_0_)) {
         return 2;
      } else {
         if (p_96537_0_.startsWith("sidebar.team.")) {
            String lvt_1_1_ = p_96537_0_.substring("sidebar.team.".length());
            TextFormatting lvt_2_1_ = TextFormatting.getValueByName(lvt_1_1_);
            if (lvt_2_1_ != null && lvt_2_1_.getColorIndex() >= 0) {
               return lvt_2_1_.getColorIndex() + 3;
            }
         }

         return -1;
      }
   }

   public static String[] getDisplaySlotStrings() {
      if (displaySlots == null) {
         displaySlots = new String[19];

         for(int lvt_0_1_ = 0; lvt_0_1_ < 19; ++lvt_0_1_) {
            displaySlots[lvt_0_1_] = getObjectiveDisplaySlot(lvt_0_1_);
         }
      }

      return displaySlots;
   }

   public void removeEntity(Entity p_181140_1_) {
      if (p_181140_1_ != null && !(p_181140_1_ instanceof PlayerEntity) && !p_181140_1_.isAlive()) {
         String lvt_2_1_ = p_181140_1_.getCachedUniqueIdString();
         this.removeObjectiveFromEntity(lvt_2_1_, (ScoreObjective)null);
         this.removePlayerFromTeams(lvt_2_1_);
      }
   }

   protected ListNBT func_197902_i() {
      ListNBT lvt_1_1_ = new ListNBT();
      this.entitiesScoreObjectives.values().stream().map(Map::values).forEach((p_197894_1_) -> {
         p_197894_1_.stream().filter((p_209546_0_) -> {
            return p_209546_0_.getObjective() != null;
         }).forEach((p_197896_1_) -> {
            CompoundNBT lvt_2_1_ = new CompoundNBT();
            lvt_2_1_.putString("Name", p_197896_1_.getPlayerName());
            lvt_2_1_.putString("Objective", p_197896_1_.getObjective().getName());
            lvt_2_1_.putInt("Score", p_197896_1_.getScorePoints());
            lvt_2_1_.putBoolean("Locked", p_197896_1_.isLocked());
            lvt_1_1_.add(lvt_2_1_);
         });
      });
      return lvt_1_1_;
   }

   protected void func_197905_a(ListNBT p_197905_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < p_197905_1_.size(); ++lvt_2_1_) {
         CompoundNBT lvt_3_1_ = p_197905_1_.getCompound(lvt_2_1_);
         ScoreObjective lvt_4_1_ = this.getOrCreateObjective(lvt_3_1_.getString("Objective"));
         String lvt_5_1_ = lvt_3_1_.getString("Name");
         if (lvt_5_1_.length() > 40) {
            lvt_5_1_ = lvt_5_1_.substring(0, 40);
         }

         Score lvt_6_1_ = this.getOrCreateScore(lvt_5_1_, lvt_4_1_);
         lvt_6_1_.setScorePoints(lvt_3_1_.getInt("Score"));
         if (lvt_3_1_.contains("Locked")) {
            lvt_6_1_.setLocked(lvt_3_1_.getBoolean("Locked"));
         }
      }

   }
}
