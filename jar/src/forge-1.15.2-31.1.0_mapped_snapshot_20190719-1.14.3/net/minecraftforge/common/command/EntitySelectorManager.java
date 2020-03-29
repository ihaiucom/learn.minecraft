package net.minecraftforge.common.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.HashMap;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;

public class EntitySelectorManager {
   private static final HashMap<String, IEntitySelectorType> REGISTRY = new HashMap();

   public static void register(String token, IEntitySelectorType type) {
      if (token.isEmpty()) {
         throw new IllegalArgumentException("Token must not be empty");
      } else if (Arrays.asList("p", "a", "r", "s", "e").contains(token)) {
         throw new IllegalArgumentException("Token clashes with vanilla @" + token);
      } else {
         char[] var2 = token.toCharArray();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            char c = var2[var4];
            if (!StringReader.isAllowedInUnquotedString(c)) {
               throw new IllegalArgumentException("Token must only contain allowed characters");
            }
         }

         REGISTRY.put(token, type);
      }
   }

   public static EntitySelector parseSelector(EntitySelectorParser parser) throws CommandSyntaxException {
      if (parser.getReader().canRead()) {
         int i = parser.getReader().getCursor();
         String token = parser.getReader().readUnquotedString();
         IEntitySelectorType type = (IEntitySelectorType)REGISTRY.get(token);
         if (type != null) {
            return type.build(parser);
         }

         parser.getReader().setCursor(i);
      }

      return null;
   }

   public static void fillSelectorSuggestions(SuggestionsBuilder suggestionBuilder) {
      REGISTRY.forEach((token, type) -> {
         suggestionBuilder.suggest("@" + token, type.getSuggestionTooltip());
      });
   }
}
