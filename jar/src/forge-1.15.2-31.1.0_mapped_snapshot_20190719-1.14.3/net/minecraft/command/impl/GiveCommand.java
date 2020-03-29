package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;

public class GiveCommand {
   public static void register(CommandDispatcher<CommandSource> p_198494_0_) {
      p_198494_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("give").requires((p_198496_0_) -> {
         return p_198496_0_.hasPermissionLevel(2);
      })).then(Commands.argument("targets", EntityArgument.players()).then(((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item()).executes((p_198493_0_) -> {
         return giveItem((CommandSource)p_198493_0_.getSource(), ItemArgument.getItem(p_198493_0_, "item"), EntityArgument.getPlayers(p_198493_0_, "targets"), 1);
      })).then(Commands.argument("count", IntegerArgumentType.integer(1)).executes((p_198495_0_) -> {
         return giveItem((CommandSource)p_198495_0_.getSource(), ItemArgument.getItem(p_198495_0_, "item"), EntityArgument.getPlayers(p_198495_0_, "targets"), IntegerArgumentType.getInteger(p_198495_0_, "count"));
      })))));
   }

   private static int giveItem(CommandSource p_198497_0_, ItemInput p_198497_1_, Collection<ServerPlayerEntity> p_198497_2_, int p_198497_3_) throws CommandSyntaxException {
      Iterator var4 = p_198497_2_.iterator();

      label40:
      while(var4.hasNext()) {
         ServerPlayerEntity lvt_5_1_ = (ServerPlayerEntity)var4.next();
         int lvt_6_1_ = p_198497_3_;

         while(true) {
            while(true) {
               if (lvt_6_1_ <= 0) {
                  continue label40;
               }

               int lvt_7_1_ = Math.min(p_198497_1_.getItem().getMaxStackSize(), lvt_6_1_);
               lvt_6_1_ -= lvt_7_1_;
               ItemStack lvt_8_1_ = p_198497_1_.createStack(lvt_7_1_, false);
               boolean lvt_9_1_ = lvt_5_1_.inventory.addItemStackToInventory(lvt_8_1_);
               ItemEntity lvt_10_1_;
               if (lvt_9_1_ && lvt_8_1_.isEmpty()) {
                  lvt_8_1_.setCount(1);
                  lvt_10_1_ = lvt_5_1_.dropItem(lvt_8_1_, false);
                  if (lvt_10_1_ != null) {
                     lvt_10_1_.makeFakeItem();
                  }

                  lvt_5_1_.world.playSound((PlayerEntity)null, lvt_5_1_.func_226277_ct_(), lvt_5_1_.func_226278_cu_(), lvt_5_1_.func_226281_cx_(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((lvt_5_1_.getRNG().nextFloat() - lvt_5_1_.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                  lvt_5_1_.container.detectAndSendChanges();
               } else {
                  lvt_10_1_ = lvt_5_1_.dropItem(lvt_8_1_, false);
                  if (lvt_10_1_ != null) {
                     lvt_10_1_.setNoPickupDelay();
                     lvt_10_1_.setOwnerId(lvt_5_1_.getUniqueID());
                  }
               }
            }
         }
      }

      if (p_198497_2_.size() == 1) {
         p_198497_0_.sendFeedback(new TranslationTextComponent("commands.give.success.single", new Object[]{p_198497_3_, p_198497_1_.createStack(p_198497_3_, false).getTextComponent(), ((ServerPlayerEntity)p_198497_2_.iterator().next()).getDisplayName()}), true);
      } else {
         p_198497_0_.sendFeedback(new TranslationTextComponent("commands.give.success.single", new Object[]{p_198497_3_, p_198497_1_.createStack(p_198497_3_, false).getTextComponent(), p_198497_2_.size()}), true);
      }

      return p_198497_2_.size();
   }
}
