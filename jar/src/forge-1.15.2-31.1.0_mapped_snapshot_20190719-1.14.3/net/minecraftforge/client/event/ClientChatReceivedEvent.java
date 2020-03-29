package net.minecraftforge.client.event;

import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ClientChatReceivedEvent extends Event {
   private ITextComponent message;
   private final ChatType type;

   public ClientChatReceivedEvent(ChatType type, ITextComponent message) {
      this.type = type;
      this.setMessage(message);
   }

   public ITextComponent getMessage() {
      return this.message;
   }

   public void setMessage(ITextComponent message) {
      this.message = message;
   }

   public ChatType getType() {
      return this.type;
   }
}
