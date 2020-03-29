package net.minecraft.command;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.command.impl.AdvancementCommand;
import net.minecraft.command.impl.BanCommand;
import net.minecraft.command.impl.BanIpCommand;
import net.minecraft.command.impl.BanListCommand;
import net.minecraft.command.impl.BossBarCommand;
import net.minecraft.command.impl.ClearCommand;
import net.minecraft.command.impl.CloneCommand;
import net.minecraft.command.impl.DataPackCommand;
import net.minecraft.command.impl.DeOpCommand;
import net.minecraft.command.impl.DebugCommand;
import net.minecraft.command.impl.DefaultGameModeCommand;
import net.minecraft.command.impl.DifficultyCommand;
import net.minecraft.command.impl.EffectCommand;
import net.minecraft.command.impl.EnchantCommand;
import net.minecraft.command.impl.ExecuteCommand;
import net.minecraft.command.impl.ExperienceCommand;
import net.minecraft.command.impl.FillCommand;
import net.minecraft.command.impl.ForceLoadCommand;
import net.minecraft.command.impl.FunctionCommand;
import net.minecraft.command.impl.GameModeCommand;
import net.minecraft.command.impl.GameRuleCommand;
import net.minecraft.command.impl.GiveCommand;
import net.minecraft.command.impl.HelpCommand;
import net.minecraft.command.impl.KickCommand;
import net.minecraft.command.impl.KillCommand;
import net.minecraft.command.impl.ListCommand;
import net.minecraft.command.impl.LocateCommand;
import net.minecraft.command.impl.LootCommand;
import net.minecraft.command.impl.MeCommand;
import net.minecraft.command.impl.MessageCommand;
import net.minecraft.command.impl.OpCommand;
import net.minecraft.command.impl.PardonCommand;
import net.minecraft.command.impl.PardonIpCommand;
import net.minecraft.command.impl.ParticleCommand;
import net.minecraft.command.impl.PlaySoundCommand;
import net.minecraft.command.impl.PublishCommand;
import net.minecraft.command.impl.RecipeCommand;
import net.minecraft.command.impl.ReloadCommand;
import net.minecraft.command.impl.ReplaceItemCommand;
import net.minecraft.command.impl.SaveAllCommand;
import net.minecraft.command.impl.SaveOffCommand;
import net.minecraft.command.impl.SaveOnCommand;
import net.minecraft.command.impl.SayCommand;
import net.minecraft.command.impl.ScheduleCommand;
import net.minecraft.command.impl.ScoreboardCommand;
import net.minecraft.command.impl.SeedCommand;
import net.minecraft.command.impl.SetBlockCommand;
import net.minecraft.command.impl.SetIdleTimeoutCommand;
import net.minecraft.command.impl.SetWorldSpawnCommand;
import net.minecraft.command.impl.SpawnPointCommand;
import net.minecraft.command.impl.SpectateCommand;
import net.minecraft.command.impl.SpreadPlayersCommand;
import net.minecraft.command.impl.StopCommand;
import net.minecraft.command.impl.StopSoundCommand;
import net.minecraft.command.impl.SummonCommand;
import net.minecraft.command.impl.TagCommand;
import net.minecraft.command.impl.TeamCommand;
import net.minecraft.command.impl.TeamMsgCommand;
import net.minecraft.command.impl.TeleportCommand;
import net.minecraft.command.impl.TellRawCommand;
import net.minecraft.command.impl.TimeCommand;
import net.minecraft.command.impl.TitleCommand;
import net.minecraft.command.impl.TriggerCommand;
import net.minecraft.command.impl.WeatherCommand;
import net.minecraft.command.impl.WhitelistCommand;
import net.minecraft.command.impl.WorldBorderCommand;
import net.minecraft.command.impl.data.DataCommand;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SCommandListPacket;
import net.minecraft.test.TestCommand;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Commands {
   private static final Logger LOGGER = LogManager.getLogger();
   private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher();

   public Commands(boolean p_i49161_1_) {
      AdvancementCommand.register(this.dispatcher);
      ExecuteCommand.register(this.dispatcher);
      BossBarCommand.register(this.dispatcher);
      ClearCommand.register(this.dispatcher);
      CloneCommand.register(this.dispatcher);
      DataCommand.register(this.dispatcher);
      DataPackCommand.register(this.dispatcher);
      DebugCommand.register(this.dispatcher);
      DefaultGameModeCommand.register(this.dispatcher);
      DifficultyCommand.register(this.dispatcher);
      EffectCommand.register(this.dispatcher);
      MeCommand.register(this.dispatcher);
      EnchantCommand.register(this.dispatcher);
      ExperienceCommand.register(this.dispatcher);
      FillCommand.register(this.dispatcher);
      ForceLoadCommand.register(this.dispatcher);
      FunctionCommand.register(this.dispatcher);
      GameModeCommand.register(this.dispatcher);
      GameRuleCommand.register(this.dispatcher);
      GiveCommand.register(this.dispatcher);
      HelpCommand.register(this.dispatcher);
      KickCommand.register(this.dispatcher);
      KillCommand.register(this.dispatcher);
      ListCommand.register(this.dispatcher);
      LocateCommand.register(this.dispatcher);
      LootCommand.register(this.dispatcher);
      MessageCommand.register(this.dispatcher);
      ParticleCommand.register(this.dispatcher);
      PlaySoundCommand.register(this.dispatcher);
      PublishCommand.register(this.dispatcher);
      ReloadCommand.register(this.dispatcher);
      RecipeCommand.register(this.dispatcher);
      ReplaceItemCommand.register(this.dispatcher);
      SayCommand.register(this.dispatcher);
      ScheduleCommand.register(this.dispatcher);
      ScoreboardCommand.register(this.dispatcher);
      SeedCommand.register(this.dispatcher);
      SetBlockCommand.register(this.dispatcher);
      SpawnPointCommand.register(this.dispatcher);
      SetWorldSpawnCommand.register(this.dispatcher);
      SpectateCommand.func_229826_a_(this.dispatcher);
      SpreadPlayersCommand.register(this.dispatcher);
      StopSoundCommand.register(this.dispatcher);
      SummonCommand.register(this.dispatcher);
      TagCommand.register(this.dispatcher);
      TeamCommand.register(this.dispatcher);
      TeamMsgCommand.register(this.dispatcher);
      TeleportCommand.register(this.dispatcher);
      TellRawCommand.register(this.dispatcher);
      TimeCommand.register(this.dispatcher);
      TitleCommand.register(this.dispatcher);
      TriggerCommand.register(this.dispatcher);
      WeatherCommand.register(this.dispatcher);
      WorldBorderCommand.register(this.dispatcher);
      if (SharedConstants.developmentMode) {
         TestCommand.func_229613_a_(this.dispatcher);
      }

      if (p_i49161_1_) {
         BanIpCommand.register(this.dispatcher);
         BanListCommand.register(this.dispatcher);
         BanCommand.register(this.dispatcher);
         DeOpCommand.register(this.dispatcher);
         OpCommand.register(this.dispatcher);
         PardonCommand.register(this.dispatcher);
         PardonIpCommand.register(this.dispatcher);
         SaveAllCommand.register(this.dispatcher);
         SaveOffCommand.register(this.dispatcher);
         SaveOnCommand.register(this.dispatcher);
         SetIdleTimeoutCommand.register(this.dispatcher);
         StopCommand.register(this.dispatcher);
         WhitelistCommand.register(this.dispatcher);
      }

      this.dispatcher.findAmbiguities((p_lambda$new$0_1_, p_lambda$new$0_2_, p_lambda$new$0_3_, p_lambda$new$0_4_) -> {
         LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", this.dispatcher.getPath(p_lambda$new$0_2_), this.dispatcher.getPath(p_lambda$new$0_3_), p_lambda$new$0_4_);
      });
      this.dispatcher.setConsumer((p_lambda$new$1_0_, p_lambda$new$1_1_, p_lambda$new$1_2_) -> {
         ((CommandSource)p_lambda$new$1_0_.getSource()).onCommandComplete(p_lambda$new$1_0_, p_lambda$new$1_1_, p_lambda$new$1_2_);
      });
   }

   public int handleCommand(CommandSource p_197059_1_, String p_197059_2_) {
      StringReader stringreader = new StringReader(p_197059_2_);
      if (stringreader.canRead() && stringreader.peek() == '/') {
         stringreader.skip();
      }

      p_197059_1_.getServer().getProfiler().startSection(p_197059_2_);

      try {
         int j;
         byte b1;
         byte b2;
         try {
            ParseResults<CommandSource> parse = this.dispatcher.parse(stringreader, p_197059_1_);
            CommandEvent event = new CommandEvent(parse);
            if (!MinecraftForge.EVENT_BUS.post(event)) {
               int lvt_4_3_ = this.dispatcher.execute(event.getParseResults());
               j = lvt_4_3_;
               return j;
            }

            if (event.getException() != null) {
               Throwables.throwIfUnchecked(event.getException());
            }

            b2 = 1;
            return b2;
         } catch (CommandException var13) {
            p_197059_1_.sendErrorMessage(var13.getComponent());
            b1 = 0;
            b2 = b1;
            return b2;
         } catch (CommandSyntaxException var14) {
            p_197059_1_.sendErrorMessage(TextComponentUtils.toTextComponent(var14.getRawMessage()));
            if (var14.getInput() != null && var14.getCursor() >= 0) {
               int k = Math.min(var14.getInput().length(), var14.getCursor());
               ITextComponent itextcomponent1 = (new StringTextComponent("")).applyTextStyle(TextFormatting.GRAY).applyTextStyle((p_lambda$handleCommand$2_1_) -> {
                  p_lambda$handleCommand$2_1_.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, p_197059_2_));
               });
               if (k > 10) {
                  itextcomponent1.appendText("...");
               }

               itextcomponent1.appendText(var14.getInput().substring(Math.max(0, k - 10), k));
               if (k < var14.getInput().length()) {
                  ITextComponent itextcomponent2 = (new StringTextComponent(var14.getInput().substring(k))).applyTextStyles(new TextFormatting[]{TextFormatting.RED, TextFormatting.UNDERLINE});
                  itextcomponent1.appendSibling(itextcomponent2);
               }

               itextcomponent1.appendSibling((new TranslationTextComponent("command.context.here", new Object[0])).applyTextStyles(new TextFormatting[]{TextFormatting.RED, TextFormatting.ITALIC}));
               p_197059_1_.sendErrorMessage(itextcomponent1);
            }
         } catch (Exception var15) {
            StringTextComponent var10000 = new StringTextComponent;
            var10000.<init>(var15.getMessage() == null ? var15.getClass().getName() : var15.getMessage());
            ITextComponent itextcomponent = var10000;
            if (LOGGER.isDebugEnabled()) {
               LOGGER.error("Command exception: {}", p_197059_2_, var15);
               StackTraceElement[] astacktraceelement = var15.getStackTrace();

               for(j = 0; j < Math.min(astacktraceelement.length, 3); ++j) {
                  itextcomponent.appendText("\n\n").appendText(astacktraceelement[j].getMethodName()).appendText("\n ").appendText(astacktraceelement[j].getFileName()).appendText(":").appendText(String.valueOf(astacktraceelement[j].getLineNumber()));
               }
            }

            p_197059_1_.sendErrorMessage((new TranslationTextComponent("command.failed", new Object[0])).applyTextStyle((p_lambda$handleCommand$3_1_) -> {
               p_lambda$handleCommand$3_1_.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, itextcomponent));
            }));
            if (SharedConstants.developmentMode) {
               p_197059_1_.sendErrorMessage(new StringTextComponent(Util.func_229758_d_(var15)));
               LOGGER.error("'" + p_197059_2_ + "' threw an exception", var15);
            }

            b2 = 0;
            byte var23 = b2;
            return var23;
         }

         byte b0 = 0;
         b1 = b0;
         return b1;
      } finally {
         p_197059_1_.getServer().getProfiler().endSection();
      }
   }

   public void send(ServerPlayerEntity p_197051_1_) {
      Map<CommandNode<CommandSource>, CommandNode<ISuggestionProvider>> map = Maps.newHashMap();
      RootCommandNode<ISuggestionProvider> rootcommandnode = new RootCommandNode();
      map.put(this.dispatcher.getRoot(), rootcommandnode);
      this.commandSourceNodesToSuggestionNodes(this.dispatcher.getRoot(), rootcommandnode, p_197051_1_.getCommandSource(), map);
      p_197051_1_.connection.sendPacket(new SCommandListPacket(rootcommandnode));
   }

   private void commandSourceNodesToSuggestionNodes(CommandNode<CommandSource> p_197052_1_, CommandNode<ISuggestionProvider> p_197052_2_, CommandSource p_197052_3_, Map<CommandNode<CommandSource>, CommandNode<ISuggestionProvider>> p_197052_4_) {
      Iterator var5 = p_197052_1_.getChildren().iterator();

      while(var5.hasNext()) {
         CommandNode<CommandSource> commandnode = (CommandNode)var5.next();
         if (commandnode.canUse(p_197052_3_)) {
            ArgumentBuilder<ISuggestionProvider, ?> argumentbuilder = commandnode.createBuilder();
            argumentbuilder.requires((p_lambda$commandSourceNodesToSuggestionNodes$4_0_) -> {
               return true;
            });
            if (argumentbuilder.getCommand() != null) {
               argumentbuilder.executes((p_lambda$commandSourceNodesToSuggestionNodes$5_0_) -> {
                  return 0;
               });
            }

            if (argumentbuilder instanceof RequiredArgumentBuilder) {
               RequiredArgumentBuilder<ISuggestionProvider, ?> requiredargumentbuilder = (RequiredArgumentBuilder)argumentbuilder;
               if (requiredargumentbuilder.getSuggestionsProvider() != null) {
                  requiredargumentbuilder.suggests(SuggestionProviders.ensureKnown(requiredargumentbuilder.getSuggestionsProvider()));
               }
            }

            if (argumentbuilder.getRedirect() != null) {
               argumentbuilder.redirect((CommandNode)p_197052_4_.get(argumentbuilder.getRedirect()));
            }

            CommandNode<ISuggestionProvider> commandnode1 = argumentbuilder.build();
            p_197052_4_.put(commandnode, commandnode1);
            p_197052_2_.addChild(commandnode1);
            if (!commandnode.getChildren().isEmpty()) {
               this.commandSourceNodesToSuggestionNodes(commandnode, commandnode1, p_197052_3_, p_197052_4_);
            }
         }
      }

   }

   public static LiteralArgumentBuilder<CommandSource> literal(String p_197057_0_) {
      return LiteralArgumentBuilder.literal(p_197057_0_);
   }

   public static <T> RequiredArgumentBuilder<CommandSource, T> argument(String p_197056_0_, ArgumentType<T> p_197056_1_) {
      return RequiredArgumentBuilder.argument(p_197056_0_, p_197056_1_);
   }

   public static Predicate<String> func_212590_a(Commands.IParser p_212590_0_) {
      return (p_lambda$func_212590_a$6_1_) -> {
         try {
            p_212590_0_.parse(new StringReader(p_lambda$func_212590_a$6_1_));
            return true;
         } catch (CommandSyntaxException var3) {
            return false;
         }
      };
   }

   public CommandDispatcher<CommandSource> getDispatcher() {
      return this.dispatcher;
   }

   @Nullable
   public static <S> CommandSyntaxException func_227481_a_(ParseResults<S> p_227481_0_) {
      if (!p_227481_0_.getReader().canRead()) {
         return null;
      } else if (p_227481_0_.getExceptions().size() == 1) {
         return (CommandSyntaxException)p_227481_0_.getExceptions().values().iterator().next();
      } else {
         return p_227481_0_.getContext().getRange().isEmpty() ? CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(p_227481_0_.getReader()) : CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(p_227481_0_.getReader());
      }
   }

   @FunctionalInterface
   public interface IParser {
      void parse(StringReader var1) throws CommandSyntaxException;
   }
}
