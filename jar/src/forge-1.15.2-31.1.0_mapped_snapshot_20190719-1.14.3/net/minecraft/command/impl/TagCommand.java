package net.minecraft.command.impl;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class TagCommand {
   private static final SimpleCommandExceptionType ADD_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.tag.add.failed", new Object[0]));
   private static final SimpleCommandExceptionType REMOVE_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.tag.remove.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198743_0_) {
      p_198743_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tag").requires((p_198751_0_) -> {
         return p_198751_0_.hasPermissionLevel(2);
      })).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).then(Commands.literal("add").then(Commands.argument("name", StringArgumentType.word()).executes((p_198746_0_) -> {
         return addTag((CommandSource)p_198746_0_.getSource(), EntityArgument.getEntities(p_198746_0_, "targets"), StringArgumentType.getString(p_198746_0_, "name"));
      })))).then(Commands.literal("remove").then(Commands.argument("name", StringArgumentType.word()).suggests((p_198745_0_, p_198745_1_) -> {
         return ISuggestionProvider.suggest((Iterable)getAllTags(EntityArgument.getEntities(p_198745_0_, "targets")), p_198745_1_);
      }).executes((p_198742_0_) -> {
         return removeTag((CommandSource)p_198742_0_.getSource(), EntityArgument.getEntities(p_198742_0_, "targets"), StringArgumentType.getString(p_198742_0_, "name"));
      })))).then(Commands.literal("list").executes((p_198747_0_) -> {
         return listTags((CommandSource)p_198747_0_.getSource(), EntityArgument.getEntities(p_198747_0_, "targets"));
      }))));
   }

   private static Collection<String> getAllTags(Collection<? extends Entity> p_198748_0_) {
      Set<String> lvt_1_1_ = Sets.newHashSet();
      Iterator var2 = p_198748_0_.iterator();

      while(var2.hasNext()) {
         Entity lvt_3_1_ = (Entity)var2.next();
         lvt_1_1_.addAll(lvt_3_1_.getTags());
      }

      return lvt_1_1_;
   }

   private static int addTag(CommandSource p_198749_0_, Collection<? extends Entity> p_198749_1_, String p_198749_2_) throws CommandSyntaxException {
      int lvt_3_1_ = 0;
      Iterator var4 = p_198749_1_.iterator();

      while(var4.hasNext()) {
         Entity lvt_5_1_ = (Entity)var4.next();
         if (lvt_5_1_.addTag(p_198749_2_)) {
            ++lvt_3_1_;
         }
      }

      if (lvt_3_1_ == 0) {
         throw ADD_FAILED.create();
      } else {
         if (p_198749_1_.size() == 1) {
            p_198749_0_.sendFeedback(new TranslationTextComponent("commands.tag.add.success.single", new Object[]{p_198749_2_, ((Entity)p_198749_1_.iterator().next()).getDisplayName()}), true);
         } else {
            p_198749_0_.sendFeedback(new TranslationTextComponent("commands.tag.add.success.multiple", new Object[]{p_198749_2_, p_198749_1_.size()}), true);
         }

         return lvt_3_1_;
      }
   }

   private static int removeTag(CommandSource p_198750_0_, Collection<? extends Entity> p_198750_1_, String p_198750_2_) throws CommandSyntaxException {
      int lvt_3_1_ = 0;
      Iterator var4 = p_198750_1_.iterator();

      while(var4.hasNext()) {
         Entity lvt_5_1_ = (Entity)var4.next();
         if (lvt_5_1_.removeTag(p_198750_2_)) {
            ++lvt_3_1_;
         }
      }

      if (lvt_3_1_ == 0) {
         throw REMOVE_FAILED.create();
      } else {
         if (p_198750_1_.size() == 1) {
            p_198750_0_.sendFeedback(new TranslationTextComponent("commands.tag.remove.success.single", new Object[]{p_198750_2_, ((Entity)p_198750_1_.iterator().next()).getDisplayName()}), true);
         } else {
            p_198750_0_.sendFeedback(new TranslationTextComponent("commands.tag.remove.success.multiple", new Object[]{p_198750_2_, p_198750_1_.size()}), true);
         }

         return lvt_3_1_;
      }
   }

   private static int listTags(CommandSource p_198744_0_, Collection<? extends Entity> p_198744_1_) {
      Set<String> lvt_2_1_ = Sets.newHashSet();
      Iterator var3 = p_198744_1_.iterator();

      while(var3.hasNext()) {
         Entity lvt_4_1_ = (Entity)var3.next();
         lvt_2_1_.addAll(lvt_4_1_.getTags());
      }

      if (p_198744_1_.size() == 1) {
         Entity lvt_3_1_ = (Entity)p_198744_1_.iterator().next();
         if (lvt_2_1_.isEmpty()) {
            p_198744_0_.sendFeedback(new TranslationTextComponent("commands.tag.list.single.empty", new Object[]{lvt_3_1_.getDisplayName()}), false);
         } else {
            p_198744_0_.sendFeedback(new TranslationTextComponent("commands.tag.list.single.success", new Object[]{lvt_3_1_.getDisplayName(), lvt_2_1_.size(), TextComponentUtils.makeGreenSortedList(lvt_2_1_)}), false);
         }
      } else if (lvt_2_1_.isEmpty()) {
         p_198744_0_.sendFeedback(new TranslationTextComponent("commands.tag.list.multiple.empty", new Object[]{p_198744_1_.size()}), false);
      } else {
         p_198744_0_.sendFeedback(new TranslationTextComponent("commands.tag.list.multiple.success", new Object[]{p_198744_1_.size(), lvt_2_1_.size(), TextComponentUtils.makeGreenSortedList(lvt_2_1_)}), false);
      }

      return lvt_2_1_.size();
   }
}
