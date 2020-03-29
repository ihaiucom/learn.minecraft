package net.minecraftforge.event;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ServerChatEvent extends Event {
   private final String message;
   private final String username;
   private final ServerPlayerEntity player;
   private ITextComponent component;

   public ServerChatEvent(ServerPlayerEntity player, String message, ITextComponent component) {
      this.message = message;
      this.player = player;
      this.username = player.getGameProfile().getName();
      this.component = component;
   }

   public void setComponent(ITextComponent e) {
      this.component = e;
   }

   public ITextComponent getComponent() {
      return this.component;
   }

   public String getMessage() {
      return this.message;
   }

   public String getUsername() {
      return this.username;
   }

   public ServerPlayerEntity getPlayer() {
      return this.player;
   }
}
