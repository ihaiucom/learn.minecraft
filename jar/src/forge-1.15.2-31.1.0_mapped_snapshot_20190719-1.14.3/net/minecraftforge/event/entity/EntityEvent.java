package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraftforge.eventbus.api.Event;

public class EntityEvent extends Event {
   private final Entity entity;

   public EntityEvent(Entity entity) {
      this.entity = entity;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public static class EyeHeight extends EntityEvent {
      private final Pose pose;
      private final EntitySize size;
      private final float oldHeight;
      private float newHeight;

      public EyeHeight(Entity entity, Pose pose, EntitySize size, float defaultHeight) {
         super(entity);
         this.pose = pose;
         this.size = size;
         this.oldHeight = defaultHeight;
         this.newHeight = defaultHeight;
      }

      public Pose getPose() {
         return this.pose;
      }

      public EntitySize getSize() {
         return this.size;
      }

      public float getOldHeight() {
         return this.oldHeight;
      }

      public float getNewHeight() {
         return this.newHeight;
      }

      public void setNewHeight(float newSize) {
         this.newHeight = newSize;
      }
   }

   public static class EnteringChunk extends EntityEvent {
      private int newChunkX;
      private int newChunkZ;
      private int oldChunkX;
      private int oldChunkZ;

      public EnteringChunk(Entity entity, int newChunkX, int newChunkZ, int oldChunkX, int oldChunkZ) {
         super(entity);
         this.setNewChunkX(newChunkX);
         this.setNewChunkZ(newChunkZ);
         this.setOldChunkX(oldChunkX);
         this.setOldChunkZ(oldChunkZ);
      }

      public int getNewChunkX() {
         return this.newChunkX;
      }

      public void setNewChunkX(int newChunkX) {
         this.newChunkX = newChunkX;
      }

      public int getNewChunkZ() {
         return this.newChunkZ;
      }

      public void setNewChunkZ(int newChunkZ) {
         this.newChunkZ = newChunkZ;
      }

      public int getOldChunkX() {
         return this.oldChunkX;
      }

      public void setOldChunkX(int oldChunkX) {
         this.oldChunkX = oldChunkX;
      }

      public int getOldChunkZ() {
         return this.oldChunkZ;
      }

      public void setOldChunkZ(int oldChunkZ) {
         this.oldChunkZ = oldChunkZ;
      }
   }

   public static class CanUpdate extends EntityEvent {
      private boolean canUpdate = false;

      public CanUpdate(Entity entity) {
         super(entity);
      }

      public boolean getCanUpdate() {
         return this.canUpdate;
      }

      public void setCanUpdate(boolean canUpdate) {
         this.canUpdate = canUpdate;
      }
   }

   public static class EntityConstructing extends EntityEvent {
      public EntityConstructing(Entity entity) {
         super(entity);
      }
   }
}
