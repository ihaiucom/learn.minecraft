package net.minecraftforge.event;

import com.mojang.brigadier.ParseResults;
import net.minecraft.command.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class CommandEvent extends Event {
   private ParseResults<CommandSource> parse;
   private Throwable exception;

   public CommandEvent(ParseResults<CommandSource> parse) {
      this.parse = parse;
   }

   public ParseResults<CommandSource> getParseResults() {
      return this.parse;
   }

   public void setParseResults(ParseResults<CommandSource> parse) {
      this.parse = parse;
   }

   public Throwable getException() {
      return this.exception;
   }

   public void setException(Throwable exception) {
      this.exception = exception;
   }
}
