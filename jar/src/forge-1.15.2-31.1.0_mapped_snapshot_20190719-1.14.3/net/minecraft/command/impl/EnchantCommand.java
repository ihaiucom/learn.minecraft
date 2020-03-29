package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EnchantmentArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

public class EnchantCommand {
   private static final DynamicCommandExceptionType NONLIVING_ENTITY_EXCEPTION = new DynamicCommandExceptionType((p_208839_0_) -> {
      return new TranslationTextComponent("commands.enchant.failed.entity", new Object[]{p_208839_0_});
   });
   private static final DynamicCommandExceptionType ITEMLESS_EXCEPTION = new DynamicCommandExceptionType((p_208835_0_) -> {
      return new TranslationTextComponent("commands.enchant.failed.itemless", new Object[]{p_208835_0_});
   });
   private static final DynamicCommandExceptionType INCOMPATIBLE_ENCHANTS_EXCEPTION = new DynamicCommandExceptionType((p_208837_0_) -> {
      return new TranslationTextComponent("commands.enchant.failed.incompatible", new Object[]{p_208837_0_});
   });
   private static final Dynamic2CommandExceptionType INVALID_LEVEL = new Dynamic2CommandExceptionType((p_208840_0_, p_208840_1_) -> {
      return new TranslationTextComponent("commands.enchant.failed.level", new Object[]{p_208840_0_, p_208840_1_});
   });
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.enchant.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_202649_0_) {
      p_202649_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("enchant").requires((p_203630_0_) -> {
         return p_203630_0_.hasPermissionLevel(2);
      })).then(Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)Commands.argument("enchantment", EnchantmentArgument.enchantment()).executes((p_202648_0_) -> {
         return enchant((CommandSource)p_202648_0_.getSource(), EntityArgument.getEntities(p_202648_0_, "targets"), EnchantmentArgument.getEnchantment(p_202648_0_, "enchantment"), 1);
      })).then(Commands.argument("level", IntegerArgumentType.integer(0)).executes((p_202650_0_) -> {
         return enchant((CommandSource)p_202650_0_.getSource(), EntityArgument.getEntities(p_202650_0_, "targets"), EnchantmentArgument.getEnchantment(p_202650_0_, "enchantment"), IntegerArgumentType.getInteger(p_202650_0_, "level"));
      })))));
   }

   private static int enchant(CommandSource p_202651_0_, Collection<? extends Entity> p_202651_1_, Enchantment p_202651_2_, int p_202651_3_) throws CommandSyntaxException {
      if (p_202651_3_ > p_202651_2_.getMaxLevel()) {
         throw INVALID_LEVEL.create(p_202651_3_, p_202651_2_.getMaxLevel());
      } else {
         int lvt_4_1_ = 0;
         Iterator var5 = p_202651_1_.iterator();

         while(true) {
            while(true) {
               while(true) {
                  while(var5.hasNext()) {
                     Entity lvt_6_1_ = (Entity)var5.next();
                     if (lvt_6_1_ instanceof LivingEntity) {
                        LivingEntity lvt_7_1_ = (LivingEntity)lvt_6_1_;
                        ItemStack lvt_8_1_ = lvt_7_1_.getHeldItemMainhand();
                        if (!lvt_8_1_.isEmpty()) {
                           if (p_202651_2_.canApply(lvt_8_1_) && EnchantmentHelper.areAllCompatibleWith(EnchantmentHelper.getEnchantments(lvt_8_1_).keySet(), p_202651_2_)) {
                              lvt_8_1_.addEnchantment(p_202651_2_, p_202651_3_);
                              ++lvt_4_1_;
                           } else if (p_202651_1_.size() == 1) {
                              throw INCOMPATIBLE_ENCHANTS_EXCEPTION.create(lvt_8_1_.getItem().getDisplayName(lvt_8_1_).getString());
                           }
                        } else if (p_202651_1_.size() == 1) {
                           throw ITEMLESS_EXCEPTION.create(lvt_7_1_.getName().getString());
                        }
                     } else if (p_202651_1_.size() == 1) {
                        throw NONLIVING_ENTITY_EXCEPTION.create(lvt_6_1_.getName().getString());
                     }
                  }

                  if (lvt_4_1_ == 0) {
                     throw FAILED_EXCEPTION.create();
                  }

                  if (p_202651_1_.size() == 1) {
                     p_202651_0_.sendFeedback(new TranslationTextComponent("commands.enchant.success.single", new Object[]{p_202651_2_.getDisplayName(p_202651_3_), ((Entity)p_202651_1_.iterator().next()).getDisplayName()}), true);
                  } else {
                     p_202651_0_.sendFeedback(new TranslationTextComponent("commands.enchant.success.multiple", new Object[]{p_202651_2_.getDisplayName(p_202651_3_), p_202651_1_.size()}), true);
                  }

                  return lvt_4_1_;
               }
            }
         }
      }
   }
}
