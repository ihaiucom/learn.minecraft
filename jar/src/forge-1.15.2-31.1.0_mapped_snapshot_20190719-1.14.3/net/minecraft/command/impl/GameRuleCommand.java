package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;

public class GameRuleCommand {
   public static void register(CommandDispatcher<CommandSource> p_198487_0_) {
      final LiteralArgumentBuilder<CommandSource> lvt_1_1_ = (LiteralArgumentBuilder)Commands.literal("gamerule").requires((p_198491_0_) -> {
         return p_198491_0_.hasPermissionLevel(2);
      });
      GameRules.func_223590_a(new GameRules.IRuleEntryVisitor() {
         public <T extends GameRules.RuleValue<T>> void func_223481_a(GameRules.RuleKey<T> p_223481_1_, GameRules.RuleType<T> p_223481_2_) {
            lvt_1_1_.then(((LiteralArgumentBuilder)Commands.literal(p_223481_1_.func_223576_a()).executes((p_223483_1_) -> {
               return GameRuleCommand.func_223486_b((CommandSource)p_223483_1_.getSource(), p_223481_1_);
            })).then(p_223481_2_.func_223581_a("value").executes((p_223482_1_) -> {
               return GameRuleCommand.func_223485_b(p_223482_1_, p_223481_1_);
            })));
         }
      });
      p_198487_0_.register(lvt_1_1_);
   }

   private static <T extends GameRules.RuleValue<T>> int func_223485_b(CommandContext<CommandSource> p_223485_0_, GameRules.RuleKey<T> p_223485_1_) {
      CommandSource lvt_2_1_ = (CommandSource)p_223485_0_.getSource();
      T lvt_3_1_ = lvt_2_1_.getServer().getGameRules().get(p_223485_1_);
      lvt_3_1_.func_223554_b(p_223485_0_, "value");
      lvt_2_1_.sendFeedback(new TranslationTextComponent("commands.gamerule.set", new Object[]{p_223485_1_.func_223576_a(), lvt_3_1_.toString()}), true);
      return lvt_3_1_.func_223557_c();
   }

   private static <T extends GameRules.RuleValue<T>> int func_223486_b(CommandSource p_223486_0_, GameRules.RuleKey<T> p_223486_1_) {
      T lvt_2_1_ = p_223486_0_.getServer().getGameRules().get(p_223486_1_);
      p_223486_0_.sendFeedback(new TranslationTextComponent("commands.gamerule.query", new Object[]{p_223486_1_.func_223576_a(), lvt_2_1_.toString()}), false);
      return lvt_2_1_.func_223557_c();
   }
}
