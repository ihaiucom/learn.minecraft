package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CommandSuggestionHelper {
   private static final Pattern field_228092_a_ = Pattern.compile("(\\s+)");
   private final Minecraft field_228093_b_;
   private final Screen field_228094_c_;
   private final TextFieldWidget field_228095_d_;
   private final FontRenderer field_228096_e_;
   private final boolean field_228097_f_;
   private final boolean field_228098_g_;
   private final int field_228099_h_;
   private final int field_228100_i_;
   private final boolean field_228101_j_;
   private final int field_228102_k_;
   private final List<String> field_228103_l_ = Lists.newArrayList();
   private int field_228104_m_;
   private int field_228105_n_;
   private ParseResults<ISuggestionProvider> field_228106_o_;
   private CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> field_228107_p_;
   private CommandSuggestionHelper.Suggestions field_228108_q_;
   private boolean field_228109_r_;
   private boolean field_228110_s_;

   public CommandSuggestionHelper(Minecraft p_i225919_1_, Screen p_i225919_2_, TextFieldWidget p_i225919_3_, FontRenderer p_i225919_4_, boolean p_i225919_5_, boolean p_i225919_6_, int p_i225919_7_, int p_i225919_8_, boolean p_i225919_9_, int p_i225919_10_) {
      this.field_228093_b_ = p_i225919_1_;
      this.field_228094_c_ = p_i225919_2_;
      this.field_228095_d_ = p_i225919_3_;
      this.field_228096_e_ = p_i225919_4_;
      this.field_228097_f_ = p_i225919_5_;
      this.field_228098_g_ = p_i225919_6_;
      this.field_228099_h_ = p_i225919_7_;
      this.field_228100_i_ = p_i225919_8_;
      this.field_228101_j_ = p_i225919_9_;
      this.field_228102_k_ = p_i225919_10_;
      p_i225919_3_.setTextFormatter(this::func_228122_a_);
   }

   public void func_228124_a_(boolean p_228124_1_) {
      this.field_228109_r_ = p_228124_1_;
      if (!p_228124_1_) {
         this.field_228108_q_ = null;
      }

   }

   public boolean func_228115_a_(int p_228115_1_, int p_228115_2_, int p_228115_3_) {
      if (this.field_228108_q_ != null && this.field_228108_q_.func_228154_b_(p_228115_1_, p_228115_2_, p_228115_3_)) {
         return true;
      } else if (this.field_228094_c_.getFocused() == this.field_228095_d_ && p_228115_1_ == 258) {
         this.func_228128_b_(true);
         return true;
      } else {
         return false;
      }
   }

   public boolean func_228112_a_(double p_228112_1_) {
      return this.field_228108_q_ != null && this.field_228108_q_.func_228147_a_(MathHelper.clamp(p_228112_1_, -1.0D, 1.0D));
   }

   public boolean func_228113_a_(double p_228113_1_, double p_228113_3_, int p_228113_5_) {
      return this.field_228108_q_ != null && this.field_228108_q_.func_228150_a_((int)p_228113_1_, (int)p_228113_3_, p_228113_5_);
   }

   public void func_228128_b_(boolean p_228128_1_) {
      if (this.field_228107_p_ != null && this.field_228107_p_.isDone()) {
         com.mojang.brigadier.suggestion.Suggestions lvt_2_1_ = (com.mojang.brigadier.suggestion.Suggestions)this.field_228107_p_.join();
         if (!lvt_2_1_.isEmpty()) {
            int lvt_3_1_ = 0;

            Suggestion lvt_5_1_;
            for(Iterator var4 = lvt_2_1_.getList().iterator(); var4.hasNext(); lvt_3_1_ = Math.max(lvt_3_1_, this.field_228096_e_.getStringWidth(lvt_5_1_.getText()))) {
               lvt_5_1_ = (Suggestion)var4.next();
            }

            int lvt_4_1_ = MathHelper.clamp(this.field_228095_d_.func_195611_j(lvt_2_1_.getRange().getStart()), 0, this.field_228095_d_.func_195611_j(0) + this.field_228095_d_.getAdjustedWidth() - lvt_3_1_);
            int lvt_5_2_ = this.field_228101_j_ ? this.field_228094_c_.height - 12 : 72;
            this.field_228108_q_ = new CommandSuggestionHelper.Suggestions(lvt_4_1_, lvt_5_2_, lvt_3_1_, lvt_2_1_, p_228128_1_);
         }
      }

   }

   public void func_228111_a_() {
      String lvt_1_1_ = this.field_228095_d_.getText();
      if (this.field_228106_o_ != null && !this.field_228106_o_.getReader().getString().equals(lvt_1_1_)) {
         this.field_228106_o_ = null;
      }

      if (!this.field_228110_s_) {
         this.field_228095_d_.setSuggestion((String)null);
         this.field_228108_q_ = null;
      }

      this.field_228103_l_.clear();
      StringReader lvt_2_1_ = new StringReader(lvt_1_1_);
      boolean lvt_3_1_ = lvt_2_1_.canRead() && lvt_2_1_.peek() == '/';
      if (lvt_3_1_) {
         lvt_2_1_.skip();
      }

      boolean lvt_4_1_ = this.field_228097_f_ || lvt_3_1_;
      int lvt_5_1_ = this.field_228095_d_.getCursorPosition();
      int lvt_7_1_;
      if (lvt_4_1_) {
         CommandDispatcher<ISuggestionProvider> lvt_6_1_ = this.field_228093_b_.player.connection.func_195515_i();
         if (this.field_228106_o_ == null) {
            this.field_228106_o_ = lvt_6_1_.parse(lvt_2_1_, this.field_228093_b_.player.connection.getSuggestionProvider());
         }

         lvt_7_1_ = this.field_228098_g_ ? lvt_2_1_.getCursor() : 1;
         if (lvt_5_1_ >= lvt_7_1_ && (this.field_228108_q_ == null || !this.field_228110_s_)) {
            this.field_228107_p_ = lvt_6_1_.getCompletionSuggestions(this.field_228106_o_, lvt_5_1_);
            this.field_228107_p_.thenRun(() -> {
               if (this.field_228107_p_.isDone()) {
                  this.func_228125_b_();
               }
            });
         }
      } else {
         String lvt_6_2_ = lvt_1_1_.substring(0, lvt_5_1_);
         lvt_7_1_ = func_228121_a_(lvt_6_2_);
         Collection<String> lvt_8_1_ = this.field_228093_b_.player.connection.getSuggestionProvider().getPlayerNames();
         this.field_228107_p_ = ISuggestionProvider.suggest((Iterable)lvt_8_1_, new SuggestionsBuilder(lvt_6_2_, lvt_7_1_));
      }

   }

   private static int func_228121_a_(String p_228121_0_) {
      if (Strings.isNullOrEmpty(p_228121_0_)) {
         return 0;
      } else {
         int lvt_1_1_ = 0;

         for(Matcher lvt_2_1_ = field_228092_a_.matcher(p_228121_0_); lvt_2_1_.find(); lvt_1_1_ = lvt_2_1_.end()) {
         }

         return lvt_1_1_;
      }
   }

   public void func_228125_b_() {
      if (this.field_228095_d_.getCursorPosition() == this.field_228095_d_.getText().length()) {
         if (((com.mojang.brigadier.suggestion.Suggestions)this.field_228107_p_.join()).isEmpty() && !this.field_228106_o_.getExceptions().isEmpty()) {
            int lvt_1_1_ = 0;
            Iterator var2 = this.field_228106_o_.getExceptions().entrySet().iterator();

            while(var2.hasNext()) {
               Entry<CommandNode<ISuggestionProvider>, CommandSyntaxException> lvt_3_1_ = (Entry)var2.next();
               CommandSyntaxException lvt_4_1_ = (CommandSyntaxException)lvt_3_1_.getValue();
               if (lvt_4_1_.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                  ++lvt_1_1_;
               } else {
                  this.field_228103_l_.add(lvt_4_1_.getMessage());
               }
            }

            if (lvt_1_1_ > 0) {
               this.field_228103_l_.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
            }
         } else if (this.field_228106_o_.getReader().canRead()) {
            this.field_228103_l_.add(Commands.func_227481_a_(this.field_228106_o_).getMessage());
         }
      }

      this.field_228104_m_ = 0;
      this.field_228105_n_ = this.field_228094_c_.width;
      if (this.field_228103_l_.isEmpty()) {
         this.func_228120_a_(TextFormatting.GRAY);
      }

      this.field_228108_q_ = null;
      if (this.field_228109_r_ && this.field_228093_b_.gameSettings.autoSuggestCommands) {
         this.func_228128_b_(false);
      }

   }

   private void func_228120_a_(TextFormatting p_228120_1_) {
      CommandContextBuilder<ISuggestionProvider> lvt_2_1_ = this.field_228106_o_.getContext();
      SuggestionContext<ISuggestionProvider> lvt_3_1_ = lvt_2_1_.findSuggestionContext(this.field_228095_d_.getCursorPosition());
      Map<CommandNode<ISuggestionProvider>, String> lvt_4_1_ = this.field_228093_b_.player.connection.func_195515_i().getSmartUsage(lvt_3_1_.parent, this.field_228093_b_.player.connection.getSuggestionProvider());
      List<String> lvt_5_1_ = Lists.newArrayList();
      int lvt_6_1_ = 0;
      Iterator var7 = lvt_4_1_.entrySet().iterator();

      while(var7.hasNext()) {
         Entry<CommandNode<ISuggestionProvider>, String> lvt_8_1_ = (Entry)var7.next();
         if (!(lvt_8_1_.getKey() instanceof LiteralCommandNode)) {
            lvt_5_1_.add(p_228120_1_ + (String)lvt_8_1_.getValue());
            lvt_6_1_ = Math.max(lvt_6_1_, this.field_228096_e_.getStringWidth((String)lvt_8_1_.getValue()));
         }
      }

      if (!lvt_5_1_.isEmpty()) {
         this.field_228103_l_.addAll(lvt_5_1_);
         this.field_228104_m_ = MathHelper.clamp(this.field_228095_d_.func_195611_j(lvt_3_1_.startPos), 0, this.field_228095_d_.func_195611_j(0) + this.field_228095_d_.getAdjustedWidth() - lvt_6_1_);
         this.field_228105_n_ = lvt_6_1_;
      }

   }

   private String func_228122_a_(String p_228122_1_, int p_228122_2_) {
      return this.field_228106_o_ != null ? func_228116_a_(this.field_228106_o_, p_228122_1_, p_228122_2_) : p_228122_1_;
   }

   @Nullable
   private static String func_228127_b_(String p_228127_0_, String p_228127_1_) {
      return p_228127_1_.startsWith(p_228127_0_) ? p_228127_1_.substring(p_228127_0_.length()) : null;
   }

   public static String func_228116_a_(ParseResults<ISuggestionProvider> p_228116_0_, String p_228116_1_, int p_228116_2_) {
      TextFormatting[] lvt_3_1_ = new TextFormatting[]{TextFormatting.AQUA, TextFormatting.YELLOW, TextFormatting.GREEN, TextFormatting.LIGHT_PURPLE, TextFormatting.GOLD};
      String lvt_4_1_ = TextFormatting.GRAY.toString();
      StringBuilder lvt_5_1_ = new StringBuilder(lvt_4_1_);
      int lvt_6_1_ = 0;
      int lvt_7_1_ = -1;
      CommandContextBuilder<ISuggestionProvider> lvt_8_1_ = p_228116_0_.getContext().getLastChild();
      Iterator var9 = lvt_8_1_.getArguments().values().iterator();

      while(var9.hasNext()) {
         ParsedArgument<ISuggestionProvider, ?> lvt_10_1_ = (ParsedArgument)var9.next();
         ++lvt_7_1_;
         if (lvt_7_1_ >= lvt_3_1_.length) {
            lvt_7_1_ = 0;
         }

         int lvt_11_1_ = Math.max(lvt_10_1_.getRange().getStart() - p_228116_2_, 0);
         if (lvt_11_1_ >= p_228116_1_.length()) {
            break;
         }

         int lvt_12_1_ = Math.min(lvt_10_1_.getRange().getEnd() - p_228116_2_, p_228116_1_.length());
         if (lvt_12_1_ > 0) {
            lvt_5_1_.append(p_228116_1_, lvt_6_1_, lvt_11_1_);
            lvt_5_1_.append(lvt_3_1_[lvt_7_1_]);
            lvt_5_1_.append(p_228116_1_, lvt_11_1_, lvt_12_1_);
            lvt_5_1_.append(lvt_4_1_);
            lvt_6_1_ = lvt_12_1_;
         }
      }

      if (p_228116_0_.getReader().canRead()) {
         int lvt_9_1_ = Math.max(p_228116_0_.getReader().getCursor() - p_228116_2_, 0);
         if (lvt_9_1_ < p_228116_1_.length()) {
            int lvt_10_2_ = Math.min(lvt_9_1_ + p_228116_0_.getReader().getRemainingLength(), p_228116_1_.length());
            lvt_5_1_.append(p_228116_1_, lvt_6_1_, lvt_9_1_);
            lvt_5_1_.append(TextFormatting.RED);
            lvt_5_1_.append(p_228116_1_, lvt_9_1_, lvt_10_2_);
            lvt_6_1_ = lvt_10_2_;
         }
      }

      lvt_5_1_.append(p_228116_1_, lvt_6_1_, p_228116_1_.length());
      return lvt_5_1_.toString();
   }

   public void func_228114_a_(int p_228114_1_, int p_228114_2_) {
      if (this.field_228108_q_ != null) {
         this.field_228108_q_.func_228149_a_(p_228114_1_, p_228114_2_);
      } else {
         int lvt_3_1_ = 0;

         for(Iterator var4 = this.field_228103_l_.iterator(); var4.hasNext(); ++lvt_3_1_) {
            String lvt_5_1_ = (String)var4.next();
            int lvt_6_1_ = this.field_228101_j_ ? this.field_228094_c_.height - 14 - 13 - 12 * lvt_3_1_ : 72 + 12 * lvt_3_1_;
            AbstractGui.fill(this.field_228104_m_ - 1, lvt_6_1_, this.field_228104_m_ + this.field_228105_n_ + 1, lvt_6_1_ + 12, this.field_228102_k_);
            this.field_228096_e_.drawStringWithShadow(lvt_5_1_, (float)this.field_228104_m_, (float)(lvt_6_1_ + 2), -1);
         }
      }

   }

   public String func_228129_c_() {
      return this.field_228108_q_ != null ? "\n" + this.field_228108_q_.func_228155_c_() : "";
   }

   @OnlyIn(Dist.CLIENT)
   public class Suggestions {
      private final Rectangle2d field_228138_b_;
      private final com.mojang.brigadier.suggestion.Suggestions field_228139_c_;
      private final String field_228140_d_;
      private int field_228141_e_;
      private int field_228142_f_;
      private Vec2f field_228143_g_;
      private boolean field_228144_h_;
      private int field_228145_i_;

      private Suggestions(int p_i225920_2_, int p_i225920_3_, int p_i225920_4_, com.mojang.brigadier.suggestion.Suggestions p_i225920_5_, boolean p_i225920_6_) {
         this.field_228143_g_ = Vec2f.ZERO;
         int lvt_7_1_ = p_i225920_2_ - 1;
         int lvt_8_1_ = CommandSuggestionHelper.this.field_228101_j_ ? p_i225920_3_ - 3 - Math.min(p_i225920_5_.getList().size(), CommandSuggestionHelper.this.field_228100_i_) * 12 : p_i225920_3_;
         this.field_228138_b_ = new Rectangle2d(lvt_7_1_, lvt_8_1_, p_i225920_4_ + 1, Math.min(p_i225920_5_.getList().size(), CommandSuggestionHelper.this.field_228100_i_) * 12);
         this.field_228139_c_ = p_i225920_5_;
         this.field_228140_d_ = CommandSuggestionHelper.this.field_228095_d_.getText();
         this.field_228145_i_ = p_i225920_6_ ? -1 : 0;
         this.func_228153_b_(0);
      }

      public void func_228149_a_(int p_228149_1_, int p_228149_2_) {
         int lvt_3_1_ = Math.min(this.field_228139_c_.getList().size(), CommandSuggestionHelper.this.field_228100_i_);
         int lvt_4_1_ = -5592406;
         boolean lvt_5_1_ = this.field_228141_e_ > 0;
         boolean lvt_6_1_ = this.field_228139_c_.getList().size() > this.field_228141_e_ + lvt_3_1_;
         boolean lvt_7_1_ = lvt_5_1_ || lvt_6_1_;
         boolean lvt_8_1_ = this.field_228143_g_.x != (float)p_228149_1_ || this.field_228143_g_.y != (float)p_228149_2_;
         if (lvt_8_1_) {
            this.field_228143_g_ = new Vec2f((float)p_228149_1_, (float)p_228149_2_);
         }

         if (lvt_7_1_) {
            AbstractGui.fill(this.field_228138_b_.getX(), this.field_228138_b_.getY() - 1, this.field_228138_b_.getX() + this.field_228138_b_.getWidth(), this.field_228138_b_.getY(), CommandSuggestionHelper.this.field_228102_k_);
            AbstractGui.fill(this.field_228138_b_.getX(), this.field_228138_b_.getY() + this.field_228138_b_.getHeight(), this.field_228138_b_.getX() + this.field_228138_b_.getWidth(), this.field_228138_b_.getY() + this.field_228138_b_.getHeight() + 1, CommandSuggestionHelper.this.field_228102_k_);
            int lvt_9_2_;
            if (lvt_5_1_) {
               for(lvt_9_2_ = 0; lvt_9_2_ < this.field_228138_b_.getWidth(); ++lvt_9_2_) {
                  if (lvt_9_2_ % 2 == 0) {
                     AbstractGui.fill(this.field_228138_b_.getX() + lvt_9_2_, this.field_228138_b_.getY() - 1, this.field_228138_b_.getX() + lvt_9_2_ + 1, this.field_228138_b_.getY(), -1);
                  }
               }
            }

            if (lvt_6_1_) {
               for(lvt_9_2_ = 0; lvt_9_2_ < this.field_228138_b_.getWidth(); ++lvt_9_2_) {
                  if (lvt_9_2_ % 2 == 0) {
                     AbstractGui.fill(this.field_228138_b_.getX() + lvt_9_2_, this.field_228138_b_.getY() + this.field_228138_b_.getHeight(), this.field_228138_b_.getX() + lvt_9_2_ + 1, this.field_228138_b_.getY() + this.field_228138_b_.getHeight() + 1, -1);
                  }
               }
            }
         }

         boolean lvt_9_3_ = false;

         for(int lvt_10_1_ = 0; lvt_10_1_ < lvt_3_1_; ++lvt_10_1_) {
            Suggestion lvt_11_1_ = (Suggestion)this.field_228139_c_.getList().get(lvt_10_1_ + this.field_228141_e_);
            AbstractGui.fill(this.field_228138_b_.getX(), this.field_228138_b_.getY() + 12 * lvt_10_1_, this.field_228138_b_.getX() + this.field_228138_b_.getWidth(), this.field_228138_b_.getY() + 12 * lvt_10_1_ + 12, CommandSuggestionHelper.this.field_228102_k_);
            if (p_228149_1_ > this.field_228138_b_.getX() && p_228149_1_ < this.field_228138_b_.getX() + this.field_228138_b_.getWidth() && p_228149_2_ > this.field_228138_b_.getY() + 12 * lvt_10_1_ && p_228149_2_ < this.field_228138_b_.getY() + 12 * lvt_10_1_ + 12) {
               if (lvt_8_1_) {
                  this.func_228153_b_(lvt_10_1_ + this.field_228141_e_);
               }

               lvt_9_3_ = true;
            }

            CommandSuggestionHelper.this.field_228096_e_.drawStringWithShadow(lvt_11_1_.getText(), (float)(this.field_228138_b_.getX() + 1), (float)(this.field_228138_b_.getY() + 2 + 12 * lvt_10_1_), lvt_10_1_ + this.field_228141_e_ == this.field_228142_f_ ? -256 : -5592406);
         }

         if (lvt_9_3_) {
            Message lvt_10_2_ = ((Suggestion)this.field_228139_c_.getList().get(this.field_228142_f_)).getTooltip();
            if (lvt_10_2_ != null) {
               CommandSuggestionHelper.this.field_228094_c_.renderTooltip(TextComponentUtils.toTextComponent(lvt_10_2_).getFormattedText(), p_228149_1_, p_228149_2_);
            }
         }

      }

      public boolean func_228150_a_(int p_228150_1_, int p_228150_2_, int p_228150_3_) {
         if (!this.field_228138_b_.contains(p_228150_1_, p_228150_2_)) {
            return false;
         } else {
            int lvt_4_1_ = (p_228150_2_ - this.field_228138_b_.getY()) / 12 + this.field_228141_e_;
            if (lvt_4_1_ >= 0 && lvt_4_1_ < this.field_228139_c_.getList().size()) {
               this.func_228153_b_(lvt_4_1_);
               this.func_228146_a_();
            }

            return true;
         }
      }

      public boolean func_228147_a_(double p_228147_1_) {
         int lvt_3_1_ = (int)(CommandSuggestionHelper.this.field_228093_b_.mouseHelper.getMouseX() * (double)CommandSuggestionHelper.this.field_228093_b_.func_228018_at_().getScaledWidth() / (double)CommandSuggestionHelper.this.field_228093_b_.func_228018_at_().getWidth());
         int lvt_4_1_ = (int)(CommandSuggestionHelper.this.field_228093_b_.mouseHelper.getMouseY() * (double)CommandSuggestionHelper.this.field_228093_b_.func_228018_at_().getScaledHeight() / (double)CommandSuggestionHelper.this.field_228093_b_.func_228018_at_().getHeight());
         if (this.field_228138_b_.contains(lvt_3_1_, lvt_4_1_)) {
            this.field_228141_e_ = MathHelper.clamp((int)((double)this.field_228141_e_ - p_228147_1_), 0, Math.max(this.field_228139_c_.getList().size() - CommandSuggestionHelper.this.field_228100_i_, 0));
            return true;
         } else {
            return false;
         }
      }

      public boolean func_228154_b_(int p_228154_1_, int p_228154_2_, int p_228154_3_) {
         if (p_228154_1_ == 265) {
            this.func_228148_a_(-1);
            this.field_228144_h_ = false;
            return true;
         } else if (p_228154_1_ == 264) {
            this.func_228148_a_(1);
            this.field_228144_h_ = false;
            return true;
         } else if (p_228154_1_ == 258) {
            if (this.field_228144_h_) {
               this.func_228148_a_(Screen.hasShiftDown() ? -1 : 1);
            }

            this.func_228146_a_();
            return true;
         } else if (p_228154_1_ == 256) {
            this.func_228152_b_();
            return true;
         } else {
            return false;
         }
      }

      public void func_228148_a_(int p_228148_1_) {
         this.func_228153_b_(this.field_228142_f_ + p_228148_1_);
         int lvt_2_1_ = this.field_228141_e_;
         int lvt_3_1_ = this.field_228141_e_ + CommandSuggestionHelper.this.field_228100_i_ - 1;
         if (this.field_228142_f_ < lvt_2_1_) {
            this.field_228141_e_ = MathHelper.clamp(this.field_228142_f_, 0, Math.max(this.field_228139_c_.getList().size() - CommandSuggestionHelper.this.field_228100_i_, 0));
         } else if (this.field_228142_f_ > lvt_3_1_) {
            this.field_228141_e_ = MathHelper.clamp(this.field_228142_f_ + CommandSuggestionHelper.this.field_228099_h_ - CommandSuggestionHelper.this.field_228100_i_, 0, Math.max(this.field_228139_c_.getList().size() - CommandSuggestionHelper.this.field_228100_i_, 0));
         }

      }

      public void func_228153_b_(int p_228153_1_) {
         this.field_228142_f_ = p_228153_1_;
         if (this.field_228142_f_ < 0) {
            this.field_228142_f_ += this.field_228139_c_.getList().size();
         }

         if (this.field_228142_f_ >= this.field_228139_c_.getList().size()) {
            this.field_228142_f_ -= this.field_228139_c_.getList().size();
         }

         Suggestion lvt_2_1_ = (Suggestion)this.field_228139_c_.getList().get(this.field_228142_f_);
         CommandSuggestionHelper.this.field_228095_d_.setSuggestion(CommandSuggestionHelper.func_228127_b_(CommandSuggestionHelper.this.field_228095_d_.getText(), lvt_2_1_.apply(this.field_228140_d_)));
         if (NarratorChatListener.INSTANCE.isActive() && this.field_228145_i_ != this.field_228142_f_) {
            NarratorChatListener.INSTANCE.func_216864_a(this.func_228155_c_());
         }

      }

      public void func_228146_a_() {
         Suggestion lvt_1_1_ = (Suggestion)this.field_228139_c_.getList().get(this.field_228142_f_);
         CommandSuggestionHelper.this.field_228110_s_ = true;
         CommandSuggestionHelper.this.field_228095_d_.setText(lvt_1_1_.apply(this.field_228140_d_));
         int lvt_2_1_ = lvt_1_1_.getRange().getStart() + lvt_1_1_.getText().length();
         CommandSuggestionHelper.this.field_228095_d_.func_212422_f(lvt_2_1_);
         CommandSuggestionHelper.this.field_228095_d_.setSelectionPos(lvt_2_1_);
         this.func_228153_b_(this.field_228142_f_);
         CommandSuggestionHelper.this.field_228110_s_ = false;
         this.field_228144_h_ = true;
      }

      private String func_228155_c_() {
         this.field_228145_i_ = this.field_228142_f_;
         List<Suggestion> lvt_1_1_ = this.field_228139_c_.getList();
         Suggestion lvt_2_1_ = (Suggestion)lvt_1_1_.get(this.field_228142_f_);
         Message lvt_3_1_ = lvt_2_1_.getTooltip();
         return lvt_3_1_ != null ? I18n.format("narration.suggestion.tooltip", this.field_228142_f_ + 1, lvt_1_1_.size(), lvt_2_1_.getText(), lvt_3_1_.getString()) : I18n.format("narration.suggestion", this.field_228142_f_ + 1, lvt_1_1_.size(), lvt_2_1_.getText());
      }

      public void func_228152_b_() {
         CommandSuggestionHelper.this.field_228108_q_ = null;
      }

      // $FF: synthetic method
      Suggestions(int p_i225921_2_, int p_i225921_3_, int p_i225921_4_, com.mojang.brigadier.suggestion.Suggestions p_i225921_5_, boolean p_i225921_6_, Object p_i225921_7_) {
         this(p_i225921_2_, p_i225921_3_, p_i225921_4_, p_i225921_5_, p_i225921_6_);
      }
   }
}
