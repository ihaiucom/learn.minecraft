package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class EntityTravelToDimensionEvent extends EntityEvent {
   private final DimensionType dimension;

   public EntityTravelToDimensionEvent(Entity entity, DimensionType dimension) {
      super(entity);
      this.dimension = dimension;
   }

   public DimensionType getDimension() {
      return this.dimension;
   }
}
