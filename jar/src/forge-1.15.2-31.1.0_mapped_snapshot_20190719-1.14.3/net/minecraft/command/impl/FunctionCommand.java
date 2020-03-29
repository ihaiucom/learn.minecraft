package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.FunctionArgument;
import net.minecraft.util.text.TranslationTextComponent;

public class FunctionCommand {
   public static final SuggestionProvider<CommandSource> FUNCTION_SUGGESTER = (p_198477_0_, p_198477_1_) -> {
      FunctionManager lvt_2_1_ = ((CommandSource)p_198477_0_.getSource()).getServer().getFunctionManager();
      ISuggestionProvider.suggestIterable(lvt_2_1_.getTagCollection().getRegisteredTags(), p_198477_1_, "#");
      return ISuggestionProvider.suggestIterable(lvt_2_1_.getFunctions().keySet(), p_198477_1_);
   };

   public static void register(CommandDispatcher<CommandSource> p_198476_0_) {
      p_198476_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("function").requires((p_198480_0_) -> {
         return p_198480_0_.hasPermissionLevel(2);
      })).then(Commands.argument("name", FunctionArgument.func_200021_a()).suggests(FUNCTION_SUGGESTER).executes((p_198479_0_) -> {
         return executeFunctions((CommandSource)p_198479_0_.getSource(), FunctionArgument.getFunctions(p_198479_0_, "name"));
      })));
   }

   private static int executeFunctions(CommandSource p_200025_0_, Collection<FunctionObject> p_200025_1_) {
      int lvt_2_1_ = 0;

      FunctionObject lvt_4_1_;
      for(Iterator var3 = p_200025_1_.iterator(); var3.hasNext(); lvt_2_1_ += p_200025_0_.getServer().getFunctionManager().execute(lvt_4_1_, p_200025_0_.withFeedbackDisabled().withMinPermissionLevel(2))) {
         lvt_4_1_ = (FunctionObject)var3.next();
      }

      if (p_200025_1_.size() == 1) {
         p_200025_0_.sendFeedback(new TranslationTextComponent("commands.function.success.single", new Object[]{lvt_2_1_, ((FunctionObject)p_200025_1_.iterator().next()).getId()}), true);
      } else {
         p_200025_0_.sendFeedback(new TranslationTextComponent("commands.function.success.multiple", new Object[]{lvt_2_1_, p_200025_1_.size()}), true);
      }

      return lvt_2_1_;
   }
}
