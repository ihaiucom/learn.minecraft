package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.text.TranslationTextComponent;

public class RecipeCommand {
   private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.recipe.give.failed", new Object[0]));
   private static final SimpleCommandExceptionType TAKE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.recipe.take.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198589_0_) {
      p_198589_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("recipe").requires((p_198593_0_) -> {
         return p_198593_0_.hasPermissionLevel(2);
      })).then(Commands.literal("give").then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.argument("recipe", ResourceLocationArgument.resourceLocation()).suggests(SuggestionProviders.ALL_RECIPES).executes((p_198588_0_) -> {
         return giveRecipes((CommandSource)p_198588_0_.getSource(), EntityArgument.getPlayers(p_198588_0_, "targets"), Collections.singleton(ResourceLocationArgument.getRecipe(p_198588_0_, "recipe")));
      }))).then(Commands.literal("*").executes((p_198591_0_) -> {
         return giveRecipes((CommandSource)p_198591_0_.getSource(), EntityArgument.getPlayers(p_198591_0_, "targets"), ((CommandSource)p_198591_0_.getSource()).getServer().getRecipeManager().getRecipes());
      }))))).then(Commands.literal("take").then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.argument("recipe", ResourceLocationArgument.resourceLocation()).suggests(SuggestionProviders.ALL_RECIPES).executes((p_198587_0_) -> {
         return takeRecipes((CommandSource)p_198587_0_.getSource(), EntityArgument.getPlayers(p_198587_0_, "targets"), Collections.singleton(ResourceLocationArgument.getRecipe(p_198587_0_, "recipe")));
      }))).then(Commands.literal("*").executes((p_198592_0_) -> {
         return takeRecipes((CommandSource)p_198592_0_.getSource(), EntityArgument.getPlayers(p_198592_0_, "targets"), ((CommandSource)p_198592_0_.getSource()).getServer().getRecipeManager().getRecipes());
      })))));
   }

   private static int giveRecipes(CommandSource p_198594_0_, Collection<ServerPlayerEntity> p_198594_1_, Collection<IRecipe<?>> p_198594_2_) throws CommandSyntaxException {
      int lvt_3_1_ = 0;

      ServerPlayerEntity lvt_5_1_;
      for(Iterator var4 = p_198594_1_.iterator(); var4.hasNext(); lvt_3_1_ += lvt_5_1_.unlockRecipes(p_198594_2_)) {
         lvt_5_1_ = (ServerPlayerEntity)var4.next();
      }

      if (lvt_3_1_ == 0) {
         throw GIVE_FAILED_EXCEPTION.create();
      } else {
         if (p_198594_1_.size() == 1) {
            p_198594_0_.sendFeedback(new TranslationTextComponent("commands.recipe.give.success.single", new Object[]{p_198594_2_.size(), ((ServerPlayerEntity)p_198594_1_.iterator().next()).getDisplayName()}), true);
         } else {
            p_198594_0_.sendFeedback(new TranslationTextComponent("commands.recipe.give.success.multiple", new Object[]{p_198594_2_.size(), p_198594_1_.size()}), true);
         }

         return lvt_3_1_;
      }
   }

   private static int takeRecipes(CommandSource p_198590_0_, Collection<ServerPlayerEntity> p_198590_1_, Collection<IRecipe<?>> p_198590_2_) throws CommandSyntaxException {
      int lvt_3_1_ = 0;

      ServerPlayerEntity lvt_5_1_;
      for(Iterator var4 = p_198590_1_.iterator(); var4.hasNext(); lvt_3_1_ += lvt_5_1_.resetRecipes(p_198590_2_)) {
         lvt_5_1_ = (ServerPlayerEntity)var4.next();
      }

      if (lvt_3_1_ == 0) {
         throw TAKE_FAILED_EXCEPTION.create();
      } else {
         if (p_198590_1_.size() == 1) {
            p_198590_0_.sendFeedback(new TranslationTextComponent("commands.recipe.take.success.single", new Object[]{p_198590_2_.size(), ((ServerPlayerEntity)p_198590_1_.iterator().next()).getDisplayName()}), true);
         } else {
            p_198590_0_.sendFeedback(new TranslationTextComponent("commands.recipe.take.success.multiple", new Object[]{p_198590_2_.size(), p_198590_1_.size()}), true);
         }

         return lvt_3_1_;
      }
   }
}
