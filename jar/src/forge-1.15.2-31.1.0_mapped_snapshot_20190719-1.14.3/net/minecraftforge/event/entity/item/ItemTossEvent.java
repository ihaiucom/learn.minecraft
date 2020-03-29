package net.minecraftforge.event.entity.item;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class ItemTossEvent extends ItemEvent {
   private final PlayerEntity player;

   public ItemTossEvent(ItemEntity entityItem, PlayerEntity player) {
      super(entityItem);
      this.player = player;
   }

   public PlayerEntity getPlayer() {
      return this.player;
   }
}
