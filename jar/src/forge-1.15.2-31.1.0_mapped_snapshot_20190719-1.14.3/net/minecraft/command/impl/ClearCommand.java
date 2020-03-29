package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemPredicateArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

public class ClearCommand {
   private static final DynamicCommandExceptionType SINGLE_FAILED_EXCEPTION = new DynamicCommandExceptionType((p_208785_0_) -> {
      return new TranslationTextComponent("clear.failed.single", new Object[]{p_208785_0_});
   });
   private static final DynamicCommandExceptionType MULTIPLE_FAILED_EXCEPTION = new DynamicCommandExceptionType((p_208787_0_) -> {
      return new TranslationTextComponent("clear.failed.multiple", new Object[]{p_208787_0_});
   });

   public static void register(CommandDispatcher<CommandSource> p_198243_0_) {
      p_198243_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("clear").requires((p_198247_0_) -> {
         return p_198247_0_.hasPermissionLevel(2);
      })).executes((p_198241_0_) -> {
         return clearInventory((CommandSource)p_198241_0_.getSource(), Collections.singleton(((CommandSource)p_198241_0_.getSource()).asPlayer()), (p_198248_0_) -> {
            return true;
         }, -1);
      })).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((p_198245_0_) -> {
         return clearInventory((CommandSource)p_198245_0_.getSource(), EntityArgument.getPlayers(p_198245_0_, "targets"), (p_198242_0_) -> {
            return true;
         }, -1);
      })).then(((RequiredArgumentBuilder)Commands.argument("item", ItemPredicateArgument.itemPredicate()).executes((p_198240_0_) -> {
         return clearInventory((CommandSource)p_198240_0_.getSource(), EntityArgument.getPlayers(p_198240_0_, "targets"), ItemPredicateArgument.getItemPredicate(p_198240_0_, "item"), -1);
      })).then(Commands.argument("maxCount", IntegerArgumentType.integer(0)).executes((p_198246_0_) -> {
         return clearInventory((CommandSource)p_198246_0_.getSource(), EntityArgument.getPlayers(p_198246_0_, "targets"), ItemPredicateArgument.getItemPredicate(p_198246_0_, "item"), IntegerArgumentType.getInteger(p_198246_0_, "maxCount"));
      })))));
   }

   private static int clearInventory(CommandSource p_198244_0_, Collection<ServerPlayerEntity> p_198244_1_, Predicate<ItemStack> p_198244_2_, int p_198244_3_) throws CommandSyntaxException {
      int lvt_4_1_ = 0;
      Iterator var5 = p_198244_1_.iterator();

      while(var5.hasNext()) {
         ServerPlayerEntity lvt_6_1_ = (ServerPlayerEntity)var5.next();
         lvt_4_1_ += lvt_6_1_.inventory.clearMatchingItems(p_198244_2_, p_198244_3_);
         lvt_6_1_.openContainer.detectAndSendChanges();
         lvt_6_1_.updateHeldItem();
      }

      if (lvt_4_1_ == 0) {
         if (p_198244_1_.size() == 1) {
            throw SINGLE_FAILED_EXCEPTION.create(((ServerPlayerEntity)p_198244_1_.iterator().next()).getName().getFormattedText());
         } else {
            throw MULTIPLE_FAILED_EXCEPTION.create(p_198244_1_.size());
         }
      } else {
         if (p_198244_3_ == 0) {
            if (p_198244_1_.size() == 1) {
               p_198244_0_.sendFeedback(new TranslationTextComponent("commands.clear.test.single", new Object[]{lvt_4_1_, ((ServerPlayerEntity)p_198244_1_.iterator().next()).getDisplayName()}), true);
            } else {
               p_198244_0_.sendFeedback(new TranslationTextComponent("commands.clear.test.multiple", new Object[]{lvt_4_1_, p_198244_1_.size()}), true);
            }
         } else if (p_198244_1_.size() == 1) {
            p_198244_0_.sendFeedback(new TranslationTextComponent("commands.clear.success.single", new Object[]{lvt_4_1_, ((ServerPlayerEntity)p_198244_1_.iterator().next()).getDisplayName()}), true);
         } else {
            p_198244_0_.sendFeedback(new TranslationTextComponent("commands.clear.success.multiple", new Object[]{lvt_4_1_, p_198244_1_.size()}), true);
         }

         return lvt_4_1_;
      }
   }
}
