package net.minecraftforge.client.event;

import com.google.common.base.Strings;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ClientChatEvent extends Event {
   private String message;
   private final String originalMessage;

   public ClientChatEvent(String message) {
      this.setMessage(message);
      this.originalMessage = Strings.nullToEmpty(message);
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String message) {
      this.message = Strings.nullToEmpty(message);
   }

   public String getOriginalMessage() {
      return this.originalMessage;
   }
}
