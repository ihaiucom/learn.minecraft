package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SDisplayObjectivePacket;
import net.minecraft.network.play.server.SScoreboardObjectivePacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.network.play.server.SUpdateScorePacket;
import net.minecraft.server.MinecraftServer;

public class ServerScoreboard extends Scoreboard {
   private final MinecraftServer server;
   private final Set<ScoreObjective> addedObjectives = Sets.newHashSet();
   private Runnable[] dirtyRunnables = new Runnable[0];

   public ServerScoreboard(MinecraftServer p_i1501_1_) {
      this.server = p_i1501_1_;
   }

   public void onScoreChanged(Score p_96536_1_) {
      super.onScoreChanged(p_96536_1_);
      if (this.addedObjectives.contains(p_96536_1_.getObjective())) {
         this.server.getPlayerList().sendPacketToAllPlayers(new SUpdateScorePacket(ServerScoreboard.Action.CHANGE, p_96536_1_.getObjective().getName(), p_96536_1_.getPlayerName(), p_96536_1_.getScorePoints()));
      }

      this.markSaveDataDirty();
   }

   public void onPlayerRemoved(String p_96516_1_) {
      super.onPlayerRemoved(p_96516_1_);
      this.server.getPlayerList().sendPacketToAllPlayers(new SUpdateScorePacket(ServerScoreboard.Action.REMOVE, (String)null, p_96516_1_, 0));
      this.markSaveDataDirty();
   }

   public void onPlayerScoreRemoved(String p_178820_1_, ScoreObjective p_178820_2_) {
      super.onPlayerScoreRemoved(p_178820_1_, p_178820_2_);
      if (this.addedObjectives.contains(p_178820_2_)) {
         this.server.getPlayerList().sendPacketToAllPlayers(new SUpdateScorePacket(ServerScoreboard.Action.REMOVE, p_178820_2_.getName(), p_178820_1_, 0));
      }

      this.markSaveDataDirty();
   }

   public void setObjectiveInDisplaySlot(int p_96530_1_, @Nullable ScoreObjective p_96530_2_) {
      ScoreObjective lvt_3_1_ = this.getObjectiveInDisplaySlot(p_96530_1_);
      super.setObjectiveInDisplaySlot(p_96530_1_, p_96530_2_);
      if (lvt_3_1_ != p_96530_2_ && lvt_3_1_ != null) {
         if (this.getObjectiveDisplaySlotCount(lvt_3_1_) > 0) {
            this.server.getPlayerList().sendPacketToAllPlayers(new SDisplayObjectivePacket(p_96530_1_, p_96530_2_));
         } else {
            this.sendDisplaySlotRemovalPackets(lvt_3_1_);
         }
      }

      if (p_96530_2_ != null) {
         if (this.addedObjectives.contains(p_96530_2_)) {
            this.server.getPlayerList().sendPacketToAllPlayers(new SDisplayObjectivePacket(p_96530_1_, p_96530_2_));
         } else {
            this.addObjective(p_96530_2_);
         }
      }

      this.markSaveDataDirty();
   }

   public boolean addPlayerToTeam(String p_197901_1_, ScorePlayerTeam p_197901_2_) {
      if (super.addPlayerToTeam(p_197901_1_, p_197901_2_)) {
         this.server.getPlayerList().sendPacketToAllPlayers(new STeamsPacket(p_197901_2_, Arrays.asList(p_197901_1_), 3));
         this.markSaveDataDirty();
         return true;
      } else {
         return false;
      }
   }

   public void removePlayerFromTeam(String p_96512_1_, ScorePlayerTeam p_96512_2_) {
      super.removePlayerFromTeam(p_96512_1_, p_96512_2_);
      this.server.getPlayerList().sendPacketToAllPlayers(new STeamsPacket(p_96512_2_, Arrays.asList(p_96512_1_), 4));
      this.markSaveDataDirty();
   }

   public void onObjectiveAdded(ScoreObjective p_96522_1_) {
      super.onObjectiveAdded(p_96522_1_);
      this.markSaveDataDirty();
   }

   public void onObjectiveChanged(ScoreObjective p_199869_1_) {
      super.onObjectiveChanged(p_199869_1_);
      if (this.addedObjectives.contains(p_199869_1_)) {
         this.server.getPlayerList().sendPacketToAllPlayers(new SScoreboardObjectivePacket(p_199869_1_, 2));
      }

      this.markSaveDataDirty();
   }

   public void onObjectiveRemoved(ScoreObjective p_96533_1_) {
      super.onObjectiveRemoved(p_96533_1_);
      if (this.addedObjectives.contains(p_96533_1_)) {
         this.sendDisplaySlotRemovalPackets(p_96533_1_);
      }

      this.markSaveDataDirty();
   }

   public void onTeamAdded(ScorePlayerTeam p_96523_1_) {
      super.onTeamAdded(p_96523_1_);
      this.server.getPlayerList().sendPacketToAllPlayers(new STeamsPacket(p_96523_1_, 0));
      this.markSaveDataDirty();
   }

   public void onTeamChanged(ScorePlayerTeam p_96538_1_) {
      super.onTeamChanged(p_96538_1_);
      this.server.getPlayerList().sendPacketToAllPlayers(new STeamsPacket(p_96538_1_, 2));
      this.markSaveDataDirty();
   }

   public void onTeamRemoved(ScorePlayerTeam p_96513_1_) {
      super.onTeamRemoved(p_96513_1_);
      this.server.getPlayerList().sendPacketToAllPlayers(new STeamsPacket(p_96513_1_, 1));
      this.markSaveDataDirty();
   }

   public void addDirtyRunnable(Runnable p_186684_1_) {
      this.dirtyRunnables = (Runnable[])Arrays.copyOf(this.dirtyRunnables, this.dirtyRunnables.length + 1);
      this.dirtyRunnables[this.dirtyRunnables.length - 1] = p_186684_1_;
   }

   protected void markSaveDataDirty() {
      Runnable[] var1 = this.dirtyRunnables;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Runnable lvt_4_1_ = var1[var3];
         lvt_4_1_.run();
      }

   }

   public List<IPacket<?>> getCreatePackets(ScoreObjective p_96550_1_) {
      List<IPacket<?>> lvt_2_1_ = Lists.newArrayList();
      lvt_2_1_.add(new SScoreboardObjectivePacket(p_96550_1_, 0));

      for(int lvt_3_1_ = 0; lvt_3_1_ < 19; ++lvt_3_1_) {
         if (this.getObjectiveInDisplaySlot(lvt_3_1_) == p_96550_1_) {
            lvt_2_1_.add(new SDisplayObjectivePacket(lvt_3_1_, p_96550_1_));
         }
      }

      Iterator var5 = this.getSortedScores(p_96550_1_).iterator();

      while(var5.hasNext()) {
         Score lvt_4_1_ = (Score)var5.next();
         lvt_2_1_.add(new SUpdateScorePacket(ServerScoreboard.Action.CHANGE, lvt_4_1_.getObjective().getName(), lvt_4_1_.getPlayerName(), lvt_4_1_.getScorePoints()));
      }

      return lvt_2_1_;
   }

   public void addObjective(ScoreObjective p_96549_1_) {
      List<IPacket<?>> lvt_2_1_ = this.getCreatePackets(p_96549_1_);
      Iterator var3 = this.server.getPlayerList().getPlayers().iterator();

      while(var3.hasNext()) {
         ServerPlayerEntity lvt_4_1_ = (ServerPlayerEntity)var3.next();
         Iterator var5 = lvt_2_1_.iterator();

         while(var5.hasNext()) {
            IPacket<?> lvt_6_1_ = (IPacket)var5.next();
            lvt_4_1_.connection.sendPacket(lvt_6_1_);
         }
      }

      this.addedObjectives.add(p_96549_1_);
   }

   public List<IPacket<?>> getDestroyPackets(ScoreObjective p_96548_1_) {
      List<IPacket<?>> lvt_2_1_ = Lists.newArrayList();
      lvt_2_1_.add(new SScoreboardObjectivePacket(p_96548_1_, 1));

      for(int lvt_3_1_ = 0; lvt_3_1_ < 19; ++lvt_3_1_) {
         if (this.getObjectiveInDisplaySlot(lvt_3_1_) == p_96548_1_) {
            lvt_2_1_.add(new SDisplayObjectivePacket(lvt_3_1_, p_96548_1_));
         }
      }

      return lvt_2_1_;
   }

   public void sendDisplaySlotRemovalPackets(ScoreObjective p_96546_1_) {
      List<IPacket<?>> lvt_2_1_ = this.getDestroyPackets(p_96546_1_);
      Iterator var3 = this.server.getPlayerList().getPlayers().iterator();

      while(var3.hasNext()) {
         ServerPlayerEntity lvt_4_1_ = (ServerPlayerEntity)var3.next();
         Iterator var5 = lvt_2_1_.iterator();

         while(var5.hasNext()) {
            IPacket<?> lvt_6_1_ = (IPacket)var5.next();
            lvt_4_1_.connection.sendPacket(lvt_6_1_);
         }
      }

      this.addedObjectives.remove(p_96546_1_);
   }

   public int getObjectiveDisplaySlotCount(ScoreObjective p_96552_1_) {
      int lvt_2_1_ = 0;

      for(int lvt_3_1_ = 0; lvt_3_1_ < 19; ++lvt_3_1_) {
         if (this.getObjectiveInDisplaySlot(lvt_3_1_) == p_96552_1_) {
            ++lvt_2_1_;
         }
      }

      return lvt_2_1_;
   }

   public static enum Action {
      CHANGE,
      REMOVE;
   }
}
