package net.minecraftforge.server.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.command.ISuggestionProvider;

public class EnumArgument<T extends Enum<T>> implements ArgumentType<T> {
   private final Class<T> enumClass;

   public static <R extends Enum<R>> EnumArgument<R> enumArgument(Class<R> enumClass) {
      return new EnumArgument(enumClass);
   }

   private EnumArgument(Class<T> enumClass) {
      this.enumClass = enumClass;
   }

   public T parse(StringReader reader) throws CommandSyntaxException {
      return Enum.valueOf(this.enumClass, reader.readUnquotedString());
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return ISuggestionProvider.suggest(Stream.of(this.enumClass.getEnumConstants()).map(Object::toString), builder);
   }

   public Collection<String> getExamples() {
      return (Collection)Stream.of(this.enumClass.getEnumConstants()).map(Object::toString).collect(Collectors.toList());
   }
}
