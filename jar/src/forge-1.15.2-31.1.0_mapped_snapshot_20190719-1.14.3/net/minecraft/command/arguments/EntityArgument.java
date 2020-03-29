package net.minecraft.command.arguments;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;

public class EntityArgument implements ArgumentType<EntitySelector> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
   public static final SimpleCommandExceptionType TOO_MANY_ENTITIES = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.toomany", new Object[0]));
   public static final SimpleCommandExceptionType TOO_MANY_PLAYERS = new SimpleCommandExceptionType(new TranslationTextComponent("argument.player.toomany", new Object[0]));
   public static final SimpleCommandExceptionType ONLY_PLAYERS_ALLOWED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.player.entities", new Object[0]));
   public static final SimpleCommandExceptionType ENTITY_NOT_FOUND = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.notfound.entity", new Object[0]));
   public static final SimpleCommandExceptionType PLAYER_NOT_FOUND = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.notfound.player", new Object[0]));
   public static final SimpleCommandExceptionType SELECTOR_NOT_ALLOWED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.selector.not_allowed", new Object[0]));
   private final boolean single;
   private final boolean playersOnly;

   protected EntityArgument(boolean p_i47969_1_, boolean p_i47969_2_) {
      this.single = p_i47969_1_;
      this.playersOnly = p_i47969_2_;
   }

   public static EntityArgument entity() {
      return new EntityArgument(true, false);
   }

   public static Entity getEntity(CommandContext<CommandSource> p_197088_0_, String p_197088_1_) throws CommandSyntaxException {
      return ((EntitySelector)p_197088_0_.getArgument(p_197088_1_, EntitySelector.class)).selectOne((CommandSource)p_197088_0_.getSource());
   }

   public static EntityArgument entities() {
      return new EntityArgument(false, false);
   }

   public static Collection<? extends Entity> getEntities(CommandContext<CommandSource> p_197097_0_, String p_197097_1_) throws CommandSyntaxException {
      Collection<? extends Entity> lvt_2_1_ = getEntitiesAllowingNone(p_197097_0_, p_197097_1_);
      if (lvt_2_1_.isEmpty()) {
         throw ENTITY_NOT_FOUND.create();
      } else {
         return lvt_2_1_;
      }
   }

   public static Collection<? extends Entity> getEntitiesAllowingNone(CommandContext<CommandSource> p_197087_0_, String p_197087_1_) throws CommandSyntaxException {
      return ((EntitySelector)p_197087_0_.getArgument(p_197087_1_, EntitySelector.class)).select((CommandSource)p_197087_0_.getSource());
   }

   public static Collection<ServerPlayerEntity> getPlayersAllowingNone(CommandContext<CommandSource> p_201309_0_, String p_201309_1_) throws CommandSyntaxException {
      return ((EntitySelector)p_201309_0_.getArgument(p_201309_1_, EntitySelector.class)).selectPlayers((CommandSource)p_201309_0_.getSource());
   }

   public static EntityArgument player() {
      return new EntityArgument(true, true);
   }

   public static ServerPlayerEntity getPlayer(CommandContext<CommandSource> p_197089_0_, String p_197089_1_) throws CommandSyntaxException {
      return ((EntitySelector)p_197089_0_.getArgument(p_197089_1_, EntitySelector.class)).selectOnePlayer((CommandSource)p_197089_0_.getSource());
   }

   public static EntityArgument players() {
      return new EntityArgument(false, true);
   }

   public static Collection<ServerPlayerEntity> getPlayers(CommandContext<CommandSource> p_197090_0_, String p_197090_1_) throws CommandSyntaxException {
      List<ServerPlayerEntity> lvt_2_1_ = ((EntitySelector)p_197090_0_.getArgument(p_197090_1_, EntitySelector.class)).selectPlayers((CommandSource)p_197090_0_.getSource());
      if (lvt_2_1_.isEmpty()) {
         throw PLAYER_NOT_FOUND.create();
      } else {
         return lvt_2_1_;
      }
   }

   public EntitySelector parse(StringReader p_parse_1_) throws CommandSyntaxException {
      int lvt_2_1_ = false;
      EntitySelectorParser lvt_3_1_ = new EntitySelectorParser(p_parse_1_);
      EntitySelector lvt_4_1_ = lvt_3_1_.parse();
      if (lvt_4_1_.getLimit() > 1 && this.single) {
         if (this.playersOnly) {
            p_parse_1_.setCursor(0);
            throw TOO_MANY_PLAYERS.createWithContext(p_parse_1_);
         } else {
            p_parse_1_.setCursor(0);
            throw TOO_MANY_ENTITIES.createWithContext(p_parse_1_);
         }
      } else if (lvt_4_1_.includesEntities() && this.playersOnly && !lvt_4_1_.isSelfSelector()) {
         p_parse_1_.setCursor(0);
         throw ONLY_PLAYERS_ALLOWED.createWithContext(p_parse_1_);
      } else {
         return lvt_4_1_;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      if (p_listSuggestions_1_.getSource() instanceof ISuggestionProvider) {
         StringReader lvt_3_1_ = new StringReader(p_listSuggestions_2_.getInput());
         lvt_3_1_.setCursor(p_listSuggestions_2_.getStart());
         ISuggestionProvider lvt_4_1_ = (ISuggestionProvider)p_listSuggestions_1_.getSource();
         EntitySelectorParser lvt_5_1_ = new EntitySelectorParser(lvt_3_1_, lvt_4_1_.hasPermissionLevel(2));

         try {
            lvt_5_1_.parse();
         } catch (CommandSyntaxException var7) {
         }

         return lvt_5_1_.fillSuggestions(p_listSuggestions_2_, (p_201942_2_) -> {
            Collection<String> lvt_3_1_ = lvt_4_1_.getPlayerNames();
            Iterable<String> lvt_4_1_x = this.playersOnly ? lvt_3_1_ : Iterables.concat(lvt_3_1_, lvt_4_1_.getTargetedEntity());
            ISuggestionProvider.suggest((Iterable)lvt_4_1_x, p_201942_2_);
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

   public static class Serializer implements IArgumentSerializer<EntityArgument> {
      public void write(EntityArgument p_197072_1_, PacketBuffer p_197072_2_) {
         byte lvt_3_1_ = 0;
         if (p_197072_1_.single) {
            lvt_3_1_ = (byte)(lvt_3_1_ | 1);
         }

         if (p_197072_1_.playersOnly) {
            lvt_3_1_ = (byte)(lvt_3_1_ | 2);
         }

         p_197072_2_.writeByte(lvt_3_1_);
      }

      public EntityArgument read(PacketBuffer p_197071_1_) {
         byte lvt_2_1_ = p_197071_1_.readByte();
         return new EntityArgument((lvt_2_1_ & 1) != 0, (lvt_2_1_ & 2) != 0);
      }

      public void write(EntityArgument p_212244_1_, JsonObject p_212244_2_) {
         p_212244_2_.addProperty("amount", p_212244_1_.single ? "single" : "multiple");
         p_212244_2_.addProperty("type", p_212244_1_.playersOnly ? "players" : "entities");
      }

      // $FF: synthetic method
      public ArgumentType read(PacketBuffer p_197071_1_) {
         return this.read(p_197071_1_);
      }
   }
}
