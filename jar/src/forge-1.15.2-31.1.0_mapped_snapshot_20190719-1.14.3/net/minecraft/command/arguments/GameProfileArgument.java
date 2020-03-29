package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class GameProfileArgument implements ArgumentType<GameProfileArgument.IProfileProvider> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");
   public static final SimpleCommandExceptionType PLAYER_UNKNOWN = new SimpleCommandExceptionType(new TranslationTextComponent("argument.player.unknown", new Object[0]));

   public static Collection<GameProfile> getGameProfiles(CommandContext<CommandSource> p_197109_0_, String p_197109_1_) throws CommandSyntaxException {
      return ((GameProfileArgument.IProfileProvider)p_197109_0_.getArgument(p_197109_1_, GameProfileArgument.IProfileProvider.class)).getNames((CommandSource)p_197109_0_.getSource());
   }

   public static GameProfileArgument gameProfile() {
      return new GameProfileArgument();
   }

   public GameProfileArgument.IProfileProvider parse(StringReader p_parse_1_) throws CommandSyntaxException {
      if (p_parse_1_.canRead() && p_parse_1_.peek() == '@') {
         EntitySelectorParser lvt_2_1_ = new EntitySelectorParser(p_parse_1_);
         EntitySelector lvt_3_1_ = lvt_2_1_.parse();
         if (lvt_3_1_.includesEntities()) {
            throw EntityArgument.ONLY_PLAYERS_ALLOWED.create();
         } else {
            return new GameProfileArgument.ProfileProvider(lvt_3_1_);
         }
      } else {
         int lvt_2_2_ = p_parse_1_.getCursor();

         while(p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
            p_parse_1_.skip();
         }

         String lvt_3_2_ = p_parse_1_.getString().substring(lvt_2_2_, p_parse_1_.getCursor());
         return (p_197107_1_) -> {
            GameProfile lvt_2_1_ = p_197107_1_.getServer().getPlayerProfileCache().getGameProfileForUsername(lvt_3_2_);
            if (lvt_2_1_ == null) {
               throw PLAYER_UNKNOWN.create();
            } else {
               return Collections.singleton(lvt_2_1_);
            }
         };
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      if (p_listSuggestions_1_.getSource() instanceof ISuggestionProvider) {
         StringReader lvt_3_1_ = new StringReader(p_listSuggestions_2_.getInput());
         lvt_3_1_.setCursor(p_listSuggestions_2_.getStart());
         EntitySelectorParser lvt_4_1_ = new EntitySelectorParser(lvt_3_1_);

         try {
            lvt_4_1_.parse();
         } catch (CommandSyntaxException var6) {
         }

         return lvt_4_1_.fillSuggestions(p_listSuggestions_2_, (p_201943_1_) -> {
            ISuggestionProvider.suggest((Iterable)((ISuggestionProvider)p_listSuggestions_1_.getSource()).getPlayerNames(), p_201943_1_);
         });
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }

   public static class ProfileProvider implements GameProfileArgument.IProfileProvider {
      private final EntitySelector selector;

      public ProfileProvider(EntitySelector p_i48084_1_) {
         this.selector = p_i48084_1_;
      }

      public Collection<GameProfile> getNames(CommandSource p_getNames_1_) throws CommandSyntaxException {
         List<ServerPlayerEntity> lvt_2_1_ = this.selector.selectPlayers(p_getNames_1_);
         if (lvt_2_1_.isEmpty()) {
            throw EntityArgument.PLAYER_NOT_FOUND.create();
         } else {
            List<GameProfile> lvt_3_1_ = Lists.newArrayList();
            Iterator var4 = lvt_2_1_.iterator();

            while(var4.hasNext()) {
               ServerPlayerEntity lvt_5_1_ = (ServerPlayerEntity)var4.next();
               lvt_3_1_.add(lvt_5_1_.getGameProfile());
            }

            return lvt_3_1_;
         }
      }
   }

   @FunctionalInterface
   public interface IProfileProvider {
      Collection<GameProfile> getNames(CommandSource var1) throws CommandSyntaxException;
   }
}
