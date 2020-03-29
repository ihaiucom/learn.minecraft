package net.minecraftforge.server.console;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import net.minecraft.command.CommandSource;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

final class ConsoleCommandCompleter implements Completer {
   private static final Logger logger = LogManager.getLogger();
   private final DedicatedServer server;

   public ConsoleCommandCompleter(DedicatedServer server) {
      this.server = (DedicatedServer)Preconditions.checkNotNull(server, "server");
   }

   public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
      String buffer = line.line();
      boolean prefix;
      if (!buffer.isEmpty() && buffer.charAt(0) == '/') {
         prefix = true;
      } else {
         buffer = '/' + buffer;
         prefix = false;
      }

      StringReader stringReader = new StringReader(buffer);
      if (stringReader.canRead() && stringReader.peek() == '/') {
         stringReader.skip();
      }

      try {
         ParseResults<CommandSource> results = this.server.getCommandManager().getDispatcher().parse(stringReader, this.server.getCommandSource());
         Suggestions tabComplete = (Suggestions)this.server.getCommandManager().getDispatcher().getCompletionSuggestions(results).get();
         Iterator var10 = tabComplete.getList().iterator();

         while(true) {
            String completion;
            do {
               if (!var10.hasNext()) {
                  return;
               }

               Suggestion suggestion = (Suggestion)var10.next();
               completion = suggestion.getText();
            } while(completion.isEmpty());

            boolean hasPrefix = prefix || completion.charAt(0) != '/';
            Candidate candidate = new Candidate(hasPrefix ? completion : completion.substring(1));
            candidates.add(candidate);
         }
      } catch (InterruptedException var15) {
         Thread.currentThread().interrupt();
      } catch (ExecutionException var16) {
         logger.error("Failed to tab complete", var16);
      }

   }
}
