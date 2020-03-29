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

public class ItemArgument implements ArgumentType<ItemInput> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");

   public static ItemArgument item() {
      return new ItemArgument();
   }

   public ItemInput parse(StringReader p_parse_1_) throws CommandSyntaxException {
      ItemParser lvt_2_1_ = (new ItemParser(p_parse_1_, false)).parse();
      return new ItemInput(lvt_2_1_.getItem(), lvt_2_1_.getNbt());
   }

   public static <S> ItemInput getItem(CommandContext<S> p_197316_0_, String p_197316_1_) {
      return (ItemInput)p_197316_0_.getArgument(p_197316_1_, ItemInput.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      StringReader lvt_3_1_ = new StringReader(p_listSuggestions_2_.getInput());
      lvt_3_1_.setCursor(p_listSuggestions_2_.getStart());
      ItemParser lvt_4_1_ = new ItemParser(lvt_3_1_, false);

      try {
         lvt_4_1_.parse();
      } catch (CommandSyntaxException var6) {
      }

      return lvt_4_1_.func_197329_a(p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }
}
