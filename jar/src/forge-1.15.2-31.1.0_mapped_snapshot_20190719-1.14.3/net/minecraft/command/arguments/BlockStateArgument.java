package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;

public class BlockStateArgument implements ArgumentType<BlockStateInput> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");

   public static BlockStateArgument blockState() {
      return new BlockStateArgument();
   }

   public BlockStateInput parse(StringReader p_parse_1_) throws CommandSyntaxException {
      BlockStateParser lvt_2_1_ = (new BlockStateParser(p_parse_1_, false)).parse(true);
      return new BlockStateInput(lvt_2_1_.getState(), lvt_2_1_.getProperties().keySet(), lvt_2_1_.getNbt());
   }

   public static BlockStateInput getBlockState(CommandContext<CommandSource> p_197238_0_, String p_197238_1_) {
      return (BlockStateInput)p_197238_0_.getArgument(p_197238_1_, BlockStateInput.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      StringReader lvt_3_1_ = new StringReader(p_listSuggestions_2_.getInput());
      lvt_3_1_.setCursor(p_listSuggestions_2_.getStart());
      BlockStateParser lvt_4_1_ = new BlockStateParser(lvt_3_1_, false);

      try {
         lvt_4_1_.parse(true);
      } catch (CommandSyntaxException var6) {
      }

      return lvt_4_1_.getSuggestions(p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }
}
