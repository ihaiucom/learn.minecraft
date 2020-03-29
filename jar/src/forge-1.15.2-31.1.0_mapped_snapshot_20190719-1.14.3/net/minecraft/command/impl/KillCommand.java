package net.minecraft.command.impl;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TranslationTextComponent;

public class KillCommand {
   public static void register(CommandDispatcher<CommandSource> p_198518_0_) {
      p_198518_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("kill").requires((p_198521_0_) -> {
         return p_198521_0_.hasPermissionLevel(2);
      })).executes((p_198520_0_) -> {
         return killEntities((CommandSource)p_198520_0_.getSource(), ImmutableList.of(((CommandSource)p_198520_0_.getSource()).assertIsEntity()));
      })).then(Commands.argument("targets", EntityArgument.entities()).executes((p_229810_0_) -> {
         return killEntities((CommandSource)p_229810_0_.getSource(), EntityArgument.getEntities(p_229810_0_, "targets"));
      })));
   }

   private static int killEntities(CommandSource p_198519_0_, Collection<? extends Entity> p_198519_1_) {
      Iterator var2 = p_198519_1_.iterator();

      while(var2.hasNext()) {
         Entity lvt_3_1_ = (Entity)var2.next();
         lvt_3_1_.onKillCommand();
      }

      if (p_198519_1_.size() == 1) {
         p_198519_0_.sendFeedback(new TranslationTextComponent("commands.kill.success.single", new Object[]{((Entity)p_198519_1_.iterator().next()).getDisplayName()}), true);
      } else {
         p_198519_0_.sendFeedback(new TranslationTextComponent("commands.kill.success.multiple", new Object[]{p_198519_1_.size()}), true);
      }

      return p_198519_1_.size();
   }
}
