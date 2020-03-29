package net.minecraftforge.event.entity.player;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.HasResult;

@Cancelable
@HasResult
public class EntityItemPickupEvent extends PlayerEvent {
   private final ItemEntity item;

   public EntityItemPickupEvent(PlayerEntity player, ItemEntity item) {
      super(player);
      this.item = item;
   }

   public ItemEntity getItem() {
      return this.item;
   }
}
