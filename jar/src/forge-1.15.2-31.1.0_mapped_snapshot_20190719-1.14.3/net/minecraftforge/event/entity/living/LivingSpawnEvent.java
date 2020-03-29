package net.minecraftforge.event.entity.living;

import javax.annotation.Nullable;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.HasResult;

public class LivingSpawnEvent extends LivingEvent {
   private final IWorld world;
   private final double x;
   private final double y;
   private final double z;

   public LivingSpawnEvent(MobEntity entity, IWorld world, double x, double y, double z) {
      super(entity);
      this.world = world;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public IWorld getWorld() {
      return this.world;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   @HasResult
   public static class AllowDespawn extends LivingSpawnEvent {
      public AllowDespawn(MobEntity entity) {
         super(entity, entity.world, entity.func_226277_ct_(), entity.func_226278_cu_(), entity.func_226281_cx_());
      }
   }

   @Cancelable
   public static class SpecialSpawn extends LivingSpawnEvent {
      @Nullable
      private final AbstractSpawner spawner;
      private final SpawnReason spawnReason;

      public SpecialSpawn(MobEntity entity, World world, double x, double y, double z, @Nullable AbstractSpawner spawner, SpawnReason spawnReason) {
         super(entity, world, x, y, z);
         this.spawner = spawner;
         this.spawnReason = spawnReason;
      }

      @Nullable
      public AbstractSpawner getSpawner() {
         return this.spawner;
      }

      public SpawnReason getSpawnReason() {
         return this.spawnReason;
      }
   }

   @HasResult
   public static class CheckSpawn extends LivingSpawnEvent {
      @Nullable
      private final AbstractSpawner spawner;
      private final SpawnReason spawnReason;

      public CheckSpawn(MobEntity entity, IWorld world, double x, double y, double z, @Nullable AbstractSpawner spawner, SpawnReason spawnReason) {
         super(entity, world, x, y, z);
         this.spawner = spawner;
         this.spawnReason = spawnReason;
      }

      public boolean isSpawner() {
         return this.spawner != null;
      }

      @Nullable
      public AbstractSpawner getSpawner() {
         return this.spawner;
      }

      public SpawnReason getSpawnReason() {
         return this.spawnReason;
      }
   }
}
