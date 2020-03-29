package net.minecraftforge.event.entity.item;

import net.minecraft.entity.item.ItemEntity;
import net.minecraftforge.event.entity.EntityEvent;

public class ItemEvent extends EntityEvent {
   private final ItemEntity entityItem;

   public ItemEvent(ItemEntity itemEntity) {
      super(itemEntity);
      this.entityItem = itemEntity;
   }

   public ItemEntity getEntityItem() {
      return this.entityItem;
   }
}
