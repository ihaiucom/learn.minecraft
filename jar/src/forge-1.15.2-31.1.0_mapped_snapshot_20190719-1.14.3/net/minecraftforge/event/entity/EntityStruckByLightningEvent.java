package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class EntityStruckByLightningEvent extends EntityEvent {
   private final LightningBoltEntity lightning;

   public EntityStruckByLightningEvent(Entity entity, LightningBoltEntity lightning) {
      super(entity);
      this.lightning = lightning;
   }

   public LightningBoltEntity getLightning() {
      return this.lightning;
   }
}
