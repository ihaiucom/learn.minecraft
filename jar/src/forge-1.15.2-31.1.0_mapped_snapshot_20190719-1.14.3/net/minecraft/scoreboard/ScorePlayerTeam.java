package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ScorePlayerTeam extends Team {
   private final Scoreboard scoreboard;
   private final String name;
   private final Set<String> membershipSet = Sets.newHashSet();
   private ITextComponent displayName;
   private ITextComponent prefix = new StringTextComponent("");
   private ITextComponent suffix = new StringTextComponent("");
   private boolean allowFriendlyFire = true;
   private boolean canSeeFriendlyInvisibles = true;
   private Team.Visible nameTagVisibility;
   private Team.Visible deathMessageVisibility;
   private TextFormatting color;
   private Team.CollisionRule collisionRule;

   public ScorePlayerTeam(Scoreboard p_i2308_1_, String p_i2308_2_) {
      this.nameTagVisibility = Team.Visible.ALWAYS;
      this.deathMessageVisibility = Team.Visible.ALWAYS;
      this.color = TextFormatting.RESET;
      this.collisionRule = Team.CollisionRule.ALWAYS;
      this.scoreboard = p_i2308_1_;
      this.name = p_i2308_2_;
      this.displayName = new StringTextComponent(p_i2308_2_);
   }

   public String getName() {
      return this.name;
   }

   public ITextComponent getDisplayName() {
      return this.displayName;
   }

   public ITextComponent getCommandName() {
      ITextComponent lvt_1_1_ = TextComponentUtils.wrapInSquareBrackets(this.displayName.deepCopy().applyTextStyle((p_211543_1_) -> {
         p_211543_1_.setInsertion(this.name).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(this.name)));
      }));
      TextFormatting lvt_2_1_ = this.getColor();
      if (lvt_2_1_ != TextFormatting.RESET) {
         lvt_1_1_.applyTextStyle(lvt_2_1_);
      }

      return lvt_1_1_;
   }

   public void setDisplayName(ITextComponent p_96664_1_) {
      if (p_96664_1_ == null) {
         throw new IllegalArgumentException("Name cannot be null");
      } else {
         this.displayName = p_96664_1_;
         this.scoreboard.onTeamChanged(this);
      }
   }

   public void setPrefix(@Nullable ITextComponent p_207408_1_) {
      this.prefix = (ITextComponent)(p_207408_1_ == null ? new StringTextComponent("") : p_207408_1_.deepCopy());
      this.scoreboard.onTeamChanged(this);
   }

   public ITextComponent getPrefix() {
      return this.prefix;
   }

   public void setSuffix(@Nullable ITextComponent p_207409_1_) {
      this.suffix = (ITextComponent)(p_207409_1_ == null ? new StringTextComponent("") : p_207409_1_.deepCopy());
      this.scoreboard.onTeamChanged(this);
   }

   public ITextComponent getSuffix() {
      return this.suffix;
   }

   public Collection<String> getMembershipCollection() {
      return this.membershipSet;
   }

   public ITextComponent format(ITextComponent p_200540_1_) {
      ITextComponent lvt_2_1_ = (new StringTextComponent("")).appendSibling(this.prefix).appendSibling(p_200540_1_).appendSibling(this.suffix);
      TextFormatting lvt_3_1_ = this.getColor();
      if (lvt_3_1_ != TextFormatting.RESET) {
         lvt_2_1_.applyTextStyle(lvt_3_1_);
      }

      return lvt_2_1_;
   }

   public static ITextComponent formatMemberName(@Nullable Team p_200541_0_, ITextComponent p_200541_1_) {
      return p_200541_0_ == null ? p_200541_1_.deepCopy() : p_200541_0_.format(p_200541_1_);
   }

   public boolean getAllowFriendlyFire() {
      return this.allowFriendlyFire;
   }

   public void setAllowFriendlyFire(boolean p_96660_1_) {
      this.allowFriendlyFire = p_96660_1_;
      this.scoreboard.onTeamChanged(this);
   }

   public boolean getSeeFriendlyInvisiblesEnabled() {
      return this.canSeeFriendlyInvisibles;
   }

   public void setSeeFriendlyInvisiblesEnabled(boolean p_98300_1_) {
      this.canSeeFriendlyInvisibles = p_98300_1_;
      this.scoreboard.onTeamChanged(this);
   }

   public Team.Visible getNameTagVisibility() {
      return this.nameTagVisibility;
   }

   public Team.Visible getDeathMessageVisibility() {
      return this.deathMessageVisibility;
   }

   public void setNameTagVisibility(Team.Visible p_178772_1_) {
      this.nameTagVisibility = p_178772_1_;
      this.scoreboard.onTeamChanged(this);
   }

   public void setDeathMessageVisibility(Team.Visible p_178773_1_) {
      this.deathMessageVisibility = p_178773_1_;
      this.scoreboard.onTeamChanged(this);
   }

   public Team.CollisionRule getCollisionRule() {
      return this.collisionRule;
   }

   public void setCollisionRule(Team.CollisionRule p_186682_1_) {
      this.collisionRule = p_186682_1_;
      this.scoreboard.onTeamChanged(this);
   }

   public int getFriendlyFlags() {
      int lvt_1_1_ = 0;
      if (this.getAllowFriendlyFire()) {
         lvt_1_1_ |= 1;
      }

      if (this.getSeeFriendlyInvisiblesEnabled()) {
         lvt_1_1_ |= 2;
      }

      return lvt_1_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public void setFriendlyFlags(int p_98298_1_) {
      this.setAllowFriendlyFire((p_98298_1_ & 1) > 0);
      this.setSeeFriendlyInvisiblesEnabled((p_98298_1_ & 2) > 0);
   }

   public void setColor(TextFormatting p_178774_1_) {
      this.color = p_178774_1_;
      this.scoreboard.onTeamChanged(this);
   }

   public TextFormatting getColor() {
      return this.color;
   }
}
