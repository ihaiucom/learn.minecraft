package net.minecraftforge.event.world;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class ExplosionEvent extends Event {
   private final World world;
   private final Explosion explosion;

   public ExplosionEvent(World world, Explosion explosion) {
      this.world = world;
      this.explosion = explosion;
   }

   public World getWorld() {
      return this.world;
   }

   public Explosion getExplosion() {
      return this.explosion;
   }

   public static class Detonate extends ExplosionEvent {
      private final List<Entity> entityList;

      public Detonate(World world, Explosion explosion, List<Entity> entityList) {
         super(world, explosion);
         this.entityList = entityList;
      }

      public List<BlockPos> getAffectedBlocks() {
         return this.getExplosion().getAffectedBlockPositions();
      }

      public List<Entity> getAffectedEntities() {
         return this.entityList;
      }
   }

   @Cancelable
   public static class Start extends ExplosionEvent {
      public Start(World world, Explosion explosion) {
         super(world, explosion);
      }
   }
}
