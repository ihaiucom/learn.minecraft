package net.minecraft.util.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;

public class ScoreTextComponent extends TextComponent implements ITargetedTextComponent {
   private final String name;
   @Nullable
   private final EntitySelector selector;
   private final String objective;
   private String value = "";

   public ScoreTextComponent(String p_i45997_1_, String p_i45997_2_) {
      this.name = p_i45997_1_;
      this.objective = p_i45997_2_;
      EntitySelector lvt_3_1_ = null;

      try {
         EntitySelectorParser lvt_4_1_ = new EntitySelectorParser(new StringReader(p_i45997_1_));
         lvt_3_1_ = lvt_4_1_.parse();
      } catch (CommandSyntaxException var5) {
      }

      this.selector = lvt_3_1_;
   }

   public String getName() {
      return this.name;
   }

   public String getObjective() {
      return this.objective;
   }

   public void setValue(String p_179997_1_) {
      this.value = p_179997_1_;
   }

   public String getUnformattedComponentText() {
      return this.value;
   }

   private void resolve(CommandSource p_197665_1_) {
      MinecraftServer lvt_2_1_ = p_197665_1_.getServer();
      if (lvt_2_1_ != null && lvt_2_1_.isAnvilFileSet() && StringUtils.isNullOrEmpty(this.value)) {
         Scoreboard lvt_3_1_ = lvt_2_1_.getScoreboard();
         ScoreObjective lvt_4_1_ = lvt_3_1_.getObjective(this.objective);
         if (lvt_3_1_.entityHasObjective(this.name, lvt_4_1_)) {
            Score lvt_5_1_ = lvt_3_1_.getOrCreateScore(this.name, lvt_4_1_);
            this.setValue(String.format("%d", lvt_5_1_.getScorePoints()));
         } else {
            this.value = "";
         }
      }

   }

   public ScoreTextComponent shallowCopy() {
      ScoreTextComponent lvt_1_1_ = new ScoreTextComponent(this.name, this.objective);
      lvt_1_1_.setValue(this.value);
      return lvt_1_1_;
   }

   public ITextComponent createNames(@Nullable CommandSource p_197668_1_, @Nullable Entity p_197668_2_, int p_197668_3_) throws CommandSyntaxException {
      if (p_197668_1_ == null) {
         return this.shallowCopy();
      } else {
         String lvt_4_4_;
         if (this.selector != null) {
            List<? extends Entity> lvt_5_1_ = this.selector.select(p_197668_1_);
            if (lvt_5_1_.isEmpty()) {
               lvt_4_4_ = this.name;
            } else {
               if (lvt_5_1_.size() != 1) {
                  throw EntityArgument.TOO_MANY_ENTITIES.create();
               }

               lvt_4_4_ = ((Entity)lvt_5_1_.get(0)).getScoreboardName();
            }
         } else {
            lvt_4_4_ = this.name;
         }

         String lvt_5_2_ = p_197668_2_ != null && lvt_4_4_.equals("*") ? p_197668_2_.getScoreboardName() : lvt_4_4_;
         ScoreTextComponent lvt_6_1_ = new ScoreTextComponent(lvt_5_2_, this.objective);
         lvt_6_1_.setValue(this.value);
         lvt_6_1_.resolve(p_197668_1_);
         return lvt_6_1_;
      }
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof ScoreTextComponent)) {
         return false;
      } else {
         ScoreTextComponent lvt_2_1_ = (ScoreTextComponent)p_equals_1_;
         return this.name.equals(lvt_2_1_.name) && this.objective.equals(lvt_2_1_.objective) && super.equals(p_equals_1_);
      }
   }

   public String toString() {
      return "ScoreComponent{name='" + this.name + '\'' + "objective='" + this.objective + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }

   // $FF: synthetic method
   public ITextComponent shallowCopy() {
      return this.shallowCopy();
   }
}
