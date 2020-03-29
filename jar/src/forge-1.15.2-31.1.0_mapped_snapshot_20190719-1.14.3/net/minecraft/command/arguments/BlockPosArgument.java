package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class BlockPosArgument implements ArgumentType<ILocationArgument> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
   public static final SimpleCommandExceptionType POS_UNLOADED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.pos.unloaded", new Object[0]));
   public static final SimpleCommandExceptionType POS_OUT_OF_WORLD = new SimpleCommandExceptionType(new TranslationTextComponent("argument.pos.outofworld", new Object[0]));

   public static BlockPosArgument blockPos() {
      return new BlockPosArgument();
   }

   public static BlockPos getLoadedBlockPos(CommandContext<CommandSource> p_197273_0_, String p_197273_1_) throws CommandSyntaxException {
      BlockPos lvt_2_1_ = ((ILocationArgument)p_197273_0_.getArgument(p_197273_1_, ILocationArgument.class)).getBlockPos((CommandSource)p_197273_0_.getSource());
      if (!((CommandSource)p_197273_0_.getSource()).getWorld().isBlockLoaded(lvt_2_1_)) {
         throw POS_UNLOADED.create();
      } else {
         ((CommandSource)p_197273_0_.getSource()).getWorld();
         if (!ServerWorld.isValid(lvt_2_1_)) {
            throw POS_OUT_OF_WORLD.create();
         } else {
            return lvt_2_1_;
         }
      }
   }

   public static BlockPos getBlockPos(CommandContext<CommandSource> p_197274_0_, String p_197274_1_) throws CommandSyntaxException {
      return ((ILocationArgument)p_197274_0_.getArgument(p_197274_1_, ILocationArgument.class)).getBlockPos((CommandSource)p_197274_0_.getSource());
   }

   public ILocationArgument parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return (ILocationArgument)(p_parse_1_.canRead() && p_parse_1_.peek() == '^' ? LocalLocationArgument.parse(p_parse_1_) : LocationInput.parseInt(p_parse_1_));
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      if (!(p_listSuggestions_1_.getSource() instanceof ISuggestionProvider)) {
         return Suggestions.empty();
      } else {
         String lvt_3_1_ = p_listSuggestions_2_.getRemaining();
         Object lvt_4_2_;
         if (!lvt_3_1_.isEmpty() && lvt_3_1_.charAt(0) == '^') {
            lvt_4_2_ = Collections.singleton(ISuggestionProvider.Coordinates.DEFAULT_LOCAL);
         } else {
            lvt_4_2_ = ((ISuggestionProvider)p_listSuggestions_1_.getSource()).func_217294_q();
         }

         return ISuggestionProvider.func_209000_a(lvt_3_1_, (Collection)lvt_4_2_, p_listSuggestions_2_, Commands.func_212590_a(this::parse));
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }
}
