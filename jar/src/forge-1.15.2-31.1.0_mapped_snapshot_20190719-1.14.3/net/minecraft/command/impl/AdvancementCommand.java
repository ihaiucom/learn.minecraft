package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class AdvancementCommand {
   private static final SuggestionProvider<CommandSource> SUGGEST_ADVANCEMENTS = (p_198206_0_, p_198206_1_) -> {
      Collection<Advancement> lvt_2_1_ = ((CommandSource)p_198206_0_.getSource()).getServer().getAdvancementManager().getAllAdvancements();
      return ISuggestionProvider.func_212476_a(lvt_2_1_.stream().map(Advancement::getId), p_198206_1_);
   };

   public static void register(CommandDispatcher<CommandSource> p_198199_0_) {
      p_198199_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("advancement").requires((p_198205_0_) -> {
         return p_198205_0_.hasPermissionLevel(2);
      })).then(Commands.literal("grant").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.literal("only").then(((RequiredArgumentBuilder)Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_ADVANCEMENTS).executes((p_198202_0_) -> {
         return forEachAdvancement((CommandSource)p_198202_0_.getSource(), EntityArgument.getPlayers(p_198202_0_, "targets"), AdvancementCommand.Action.GRANT, getMatchingAdvancements(ResourceLocationArgument.getAdvancement(p_198202_0_, "advancement"), AdvancementCommand.Mode.ONLY));
      })).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((p_198209_0_, p_198209_1_) -> {
         return ISuggestionProvider.suggest((Iterable)ResourceLocationArgument.getAdvancement(p_198209_0_, "advancement").getCriteria().keySet(), p_198209_1_);
      }).executes((p_198212_0_) -> {
         return updateCriterion((CommandSource)p_198212_0_.getSource(), EntityArgument.getPlayers(p_198212_0_, "targets"), AdvancementCommand.Action.GRANT, ResourceLocationArgument.getAdvancement(p_198212_0_, "advancement"), StringArgumentType.getString(p_198212_0_, "criterion"));
      }))))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_ADVANCEMENTS).executes((p_198215_0_) -> {
         return forEachAdvancement((CommandSource)p_198215_0_.getSource(), EntityArgument.getPlayers(p_198215_0_, "targets"), AdvancementCommand.Action.GRANT, getMatchingAdvancements(ResourceLocationArgument.getAdvancement(p_198215_0_, "advancement"), AdvancementCommand.Mode.FROM));
      })))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_ADVANCEMENTS).executes((p_198204_0_) -> {
         return forEachAdvancement((CommandSource)p_198204_0_.getSource(), EntityArgument.getPlayers(p_198204_0_, "targets"), AdvancementCommand.Action.GRANT, getMatchingAdvancements(ResourceLocationArgument.getAdvancement(p_198204_0_, "advancement"), AdvancementCommand.Mode.UNTIL));
      })))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_ADVANCEMENTS).executes((p_198211_0_) -> {
         return forEachAdvancement((CommandSource)p_198211_0_.getSource(), EntityArgument.getPlayers(p_198211_0_, "targets"), AdvancementCommand.Action.GRANT, getMatchingAdvancements(ResourceLocationArgument.getAdvancement(p_198211_0_, "advancement"), AdvancementCommand.Mode.THROUGH));
      })))).then(Commands.literal("everything").executes((p_198217_0_) -> {
         return forEachAdvancement((CommandSource)p_198217_0_.getSource(), EntityArgument.getPlayers(p_198217_0_, "targets"), AdvancementCommand.Action.GRANT, ((CommandSource)p_198217_0_.getSource()).getServer().getAdvancementManager().getAllAdvancements());
      }))))).then(Commands.literal("revoke").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.literal("only").then(((RequiredArgumentBuilder)Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_ADVANCEMENTS).executes((p_198198_0_) -> {
         return forEachAdvancement((CommandSource)p_198198_0_.getSource(), EntityArgument.getPlayers(p_198198_0_, "targets"), AdvancementCommand.Action.REVOKE, getMatchingAdvancements(ResourceLocationArgument.getAdvancement(p_198198_0_, "advancement"), AdvancementCommand.Mode.ONLY));
      })).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((p_198210_0_, p_198210_1_) -> {
         return ISuggestionProvider.suggest((Iterable)ResourceLocationArgument.getAdvancement(p_198210_0_, "advancement").getCriteria().keySet(), p_198210_1_);
      }).executes((p_198200_0_) -> {
         return updateCriterion((CommandSource)p_198200_0_.getSource(), EntityArgument.getPlayers(p_198200_0_, "targets"), AdvancementCommand.Action.REVOKE, ResourceLocationArgument.getAdvancement(p_198200_0_, "advancement"), StringArgumentType.getString(p_198200_0_, "criterion"));
      }))))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_ADVANCEMENTS).executes((p_198208_0_) -> {
         return forEachAdvancement((CommandSource)p_198208_0_.getSource(), EntityArgument.getPlayers(p_198208_0_, "targets"), AdvancementCommand.Action.REVOKE, getMatchingAdvancements(ResourceLocationArgument.getAdvancement(p_198208_0_, "advancement"), AdvancementCommand.Mode.FROM));
      })))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_ADVANCEMENTS).executes((p_198201_0_) -> {
         return forEachAdvancement((CommandSource)p_198201_0_.getSource(), EntityArgument.getPlayers(p_198201_0_, "targets"), AdvancementCommand.Action.REVOKE, getMatchingAdvancements(ResourceLocationArgument.getAdvancement(p_198201_0_, "advancement"), AdvancementCommand.Mode.UNTIL));
      })))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_ADVANCEMENTS).executes((p_198197_0_) -> {
         return forEachAdvancement((CommandSource)p_198197_0_.getSource(), EntityArgument.getPlayers(p_198197_0_, "targets"), AdvancementCommand.Action.REVOKE, getMatchingAdvancements(ResourceLocationArgument.getAdvancement(p_198197_0_, "advancement"), AdvancementCommand.Mode.THROUGH));
      })))).then(Commands.literal("everything").executes((p_198213_0_) -> {
         return forEachAdvancement((CommandSource)p_198213_0_.getSource(), EntityArgument.getPlayers(p_198213_0_, "targets"), AdvancementCommand.Action.REVOKE, ((CommandSource)p_198213_0_.getSource()).getServer().getAdvancementManager().getAllAdvancements());
      })))));
   }

   private static int forEachAdvancement(CommandSource p_198214_0_, Collection<ServerPlayerEntity> p_198214_1_, AdvancementCommand.Action p_198214_2_, Collection<Advancement> p_198214_3_) {
      int lvt_4_1_ = 0;

      ServerPlayerEntity lvt_6_1_;
      for(Iterator var5 = p_198214_1_.iterator(); var5.hasNext(); lvt_4_1_ += p_198214_2_.applyToAdvancements(lvt_6_1_, p_198214_3_)) {
         lvt_6_1_ = (ServerPlayerEntity)var5.next();
      }

      if (lvt_4_1_ == 0) {
         if (p_198214_3_.size() == 1) {
            if (p_198214_1_.size() == 1) {
               throw new CommandException(new TranslationTextComponent(p_198214_2_.getPrefix() + ".one.to.one.failure", new Object[]{((Advancement)p_198214_3_.iterator().next()).getDisplayText(), ((ServerPlayerEntity)p_198214_1_.iterator().next()).getDisplayName()}));
            } else {
               throw new CommandException(new TranslationTextComponent(p_198214_2_.getPrefix() + ".one.to.many.failure", new Object[]{((Advancement)p_198214_3_.iterator().next()).getDisplayText(), p_198214_1_.size()}));
            }
         } else if (p_198214_1_.size() == 1) {
            throw new CommandException(new TranslationTextComponent(p_198214_2_.getPrefix() + ".many.to.one.failure", new Object[]{p_198214_3_.size(), ((ServerPlayerEntity)p_198214_1_.iterator().next()).getDisplayName()}));
         } else {
            throw new CommandException(new TranslationTextComponent(p_198214_2_.getPrefix() + ".many.to.many.failure", new Object[]{p_198214_3_.size(), p_198214_1_.size()}));
         }
      } else {
         if (p_198214_3_.size() == 1) {
            if (p_198214_1_.size() == 1) {
               p_198214_0_.sendFeedback(new TranslationTextComponent(p_198214_2_.getPrefix() + ".one.to.one.success", new Object[]{((Advancement)p_198214_3_.iterator().next()).getDisplayText(), ((ServerPlayerEntity)p_198214_1_.iterator().next()).getDisplayName()}), true);
            } else {
               p_198214_0_.sendFeedback(new TranslationTextComponent(p_198214_2_.getPrefix() + ".one.to.many.success", new Object[]{((Advancement)p_198214_3_.iterator().next()).getDisplayText(), p_198214_1_.size()}), true);
            }
         } else if (p_198214_1_.size() == 1) {
            p_198214_0_.sendFeedback(new TranslationTextComponent(p_198214_2_.getPrefix() + ".many.to.one.success", new Object[]{p_198214_3_.size(), ((ServerPlayerEntity)p_198214_1_.iterator().next()).getDisplayName()}), true);
         } else {
            p_198214_0_.sendFeedback(new TranslationTextComponent(p_198214_2_.getPrefix() + ".many.to.many.success", new Object[]{p_198214_3_.size(), p_198214_1_.size()}), true);
         }

         return lvt_4_1_;
      }
   }

   private static int updateCriterion(CommandSource p_198203_0_, Collection<ServerPlayerEntity> p_198203_1_, AdvancementCommand.Action p_198203_2_, Advancement p_198203_3_, String p_198203_4_) {
      int lvt_5_1_ = 0;
      if (!p_198203_3_.getCriteria().containsKey(p_198203_4_)) {
         throw new CommandException(new TranslationTextComponent("commands.advancement.criterionNotFound", new Object[]{p_198203_3_.getDisplayText(), p_198203_4_}));
      } else {
         Iterator var6 = p_198203_1_.iterator();

         while(var6.hasNext()) {
            ServerPlayerEntity lvt_7_1_ = (ServerPlayerEntity)var6.next();
            if (p_198203_2_.applyToCriterion(lvt_7_1_, p_198203_3_, p_198203_4_)) {
               ++lvt_5_1_;
            }
         }

         if (lvt_5_1_ == 0) {
            if (p_198203_1_.size() == 1) {
               throw new CommandException(new TranslationTextComponent(p_198203_2_.getPrefix() + ".criterion.to.one.failure", new Object[]{p_198203_4_, p_198203_3_.getDisplayText(), ((ServerPlayerEntity)p_198203_1_.iterator().next()).getDisplayName()}));
            } else {
               throw new CommandException(new TranslationTextComponent(p_198203_2_.getPrefix() + ".criterion.to.many.failure", new Object[]{p_198203_4_, p_198203_3_.getDisplayText(), p_198203_1_.size()}));
            }
         } else {
            if (p_198203_1_.size() == 1) {
               p_198203_0_.sendFeedback(new TranslationTextComponent(p_198203_2_.getPrefix() + ".criterion.to.one.success", new Object[]{p_198203_4_, p_198203_3_.getDisplayText(), ((ServerPlayerEntity)p_198203_1_.iterator().next()).getDisplayName()}), true);
            } else {
               p_198203_0_.sendFeedback(new TranslationTextComponent(p_198203_2_.getPrefix() + ".criterion.to.many.success", new Object[]{p_198203_4_, p_198203_3_.getDisplayText(), p_198203_1_.size()}), true);
            }

            return lvt_5_1_;
         }
      }
   }

   private static List<Advancement> getMatchingAdvancements(Advancement p_198216_0_, AdvancementCommand.Mode p_198216_1_) {
      List<Advancement> lvt_2_1_ = Lists.newArrayList();
      if (p_198216_1_.includesParents) {
         for(Advancement lvt_3_1_ = p_198216_0_.getParent(); lvt_3_1_ != null; lvt_3_1_ = lvt_3_1_.getParent()) {
            lvt_2_1_.add(lvt_3_1_);
         }
      }

      lvt_2_1_.add(p_198216_0_);
      if (p_198216_1_.includesChildren) {
         addAllChildren(p_198216_0_, lvt_2_1_);
      }

      return lvt_2_1_;
   }

   private static void addAllChildren(Advancement p_198207_0_, List<Advancement> p_198207_1_) {
      Iterator var2 = p_198207_0_.getChildren().iterator();

      while(var2.hasNext()) {
         Advancement lvt_3_1_ = (Advancement)var2.next();
         p_198207_1_.add(lvt_3_1_);
         addAllChildren(lvt_3_1_, p_198207_1_);
      }

   }

   static enum Mode {
      ONLY(false, false),
      THROUGH(true, true),
      FROM(false, true),
      UNTIL(true, false),
      EVERYTHING(true, true);

      private final boolean includesParents;
      private final boolean includesChildren;

      private Mode(boolean p_i48091_3_, boolean p_i48091_4_) {
         this.includesParents = p_i48091_3_;
         this.includesChildren = p_i48091_4_;
      }
   }

   static enum Action {
      GRANT("grant") {
         protected boolean applyToAdvancement(ServerPlayerEntity p_198179_1_, Advancement p_198179_2_) {
            AdvancementProgress lvt_3_1_ = p_198179_1_.getAdvancements().getProgress(p_198179_2_);
            if (lvt_3_1_.isDone()) {
               return false;
            } else {
               Iterator var4 = lvt_3_1_.getRemaningCriteria().iterator();

               while(var4.hasNext()) {
                  String lvt_5_1_ = (String)var4.next();
                  p_198179_1_.getAdvancements().grantCriterion(p_198179_2_, lvt_5_1_);
               }

               return true;
            }
         }

         protected boolean applyToCriterion(ServerPlayerEntity p_198182_1_, Advancement p_198182_2_, String p_198182_3_) {
            return p_198182_1_.getAdvancements().grantCriterion(p_198182_2_, p_198182_3_);
         }
      },
      REVOKE("revoke") {
         protected boolean applyToAdvancement(ServerPlayerEntity p_198179_1_, Advancement p_198179_2_) {
            AdvancementProgress lvt_3_1_ = p_198179_1_.getAdvancements().getProgress(p_198179_2_);
            if (!lvt_3_1_.hasProgress()) {
               return false;
            } else {
               Iterator var4 = lvt_3_1_.getCompletedCriteria().iterator();

               while(var4.hasNext()) {
                  String lvt_5_1_ = (String)var4.next();
                  p_198179_1_.getAdvancements().revokeCriterion(p_198179_2_, lvt_5_1_);
               }

               return true;
            }
         }

         protected boolean applyToCriterion(ServerPlayerEntity p_198182_1_, Advancement p_198182_2_, String p_198182_3_) {
            return p_198182_1_.getAdvancements().revokeCriterion(p_198182_2_, p_198182_3_);
         }
      };

      private final String prefix;

      private Action(String p_i48092_3_) {
         this.prefix = "commands.advancement." + p_i48092_3_;
      }

      public int applyToAdvancements(ServerPlayerEntity p_198180_1_, Iterable<Advancement> p_198180_2_) {
         int lvt_3_1_ = 0;
         Iterator var4 = p_198180_2_.iterator();

         while(var4.hasNext()) {
            Advancement lvt_5_1_ = (Advancement)var4.next();
            if (this.applyToAdvancement(p_198180_1_, lvt_5_1_)) {
               ++lvt_3_1_;
            }
         }

         return lvt_3_1_;
      }

      protected abstract boolean applyToAdvancement(ServerPlayerEntity var1, Advancement var2);

      protected abstract boolean applyToCriterion(ServerPlayerEntity var1, Advancement var2, String var3);

      protected String getPrefix() {
         return this.prefix;
      }

      // $FF: synthetic method
      Action(String p_i48093_3_, Object p_i48093_4_) {
         this(p_i48093_3_);
      }
   }
}
