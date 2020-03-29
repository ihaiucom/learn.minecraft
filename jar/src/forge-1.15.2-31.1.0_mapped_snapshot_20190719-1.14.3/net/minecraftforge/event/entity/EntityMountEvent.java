package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class EntityMountEvent extends EntityEvent {
   private final Entity entityMounting;
   private final Entity entityBeingMounted;
   private final World worldObj;
   private final boolean isMounting;

   public EntityMountEvent(Entity entityMounting, Entity entityBeingMounted, World entityWorld, boolean isMounting) {
      super(entityMounting);
      this.entityMounting = entityMounting;
      this.entityBeingMounted = entityBeingMounted;
      this.worldObj = entityWorld;
      this.isMounting = isMounting;
   }

   public boolean isMounting() {
      return this.isMounting;
   }

   public boolean isDismounting() {
      return !this.isMounting;
   }

   public Entity getEntityMounting() {
      return this.entityMounting;
   }

   public Entity getEntityBeingMounted() {
      return this.entityBeingMounted;
   }

   public World getWorldObj() {
      return this.worldObj;
   }
}
