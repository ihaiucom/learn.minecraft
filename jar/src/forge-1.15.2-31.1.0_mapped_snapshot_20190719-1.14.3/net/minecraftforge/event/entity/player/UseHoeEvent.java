package net.minecraftforge.event.entity.player;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemUseContext;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.HasResult;

@Cancelable
@HasResult
public class UseHoeEvent extends PlayerEvent {
   private final ItemUseContext context;

   public UseHoeEvent(ItemUseContext context) {
      super(context.getPlayer());
      this.context = context;
   }

   @Nonnull
   public ItemUseContext getContext() {
      return this.context;
   }
}
