package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class EntityJoinWorldEvent extends EntityEvent {
   private final World world;

   public EntityJoinWorldEvent(Entity entity, World world) {
      super(entity);
      this.world = world;
   }

   public World getWorld() {
      return this.world;
   }
}
