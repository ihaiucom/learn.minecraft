package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class ProjectileImpactEvent extends EntityEvent {
   private final RayTraceResult ray;

   public ProjectileImpactEvent(Entity entity, RayTraceResult ray) {
      super(entity);
      this.ray = ray;
   }

   public RayTraceResult getRayTraceResult() {
      return this.ray;
   }

   @Cancelable
   public static class Throwable extends ProjectileImpactEvent {
      private final ThrowableEntity throwable;

      public Throwable(ThrowableEntity throwable, RayTraceResult ray) {
         super(throwable, ray);
         this.throwable = throwable;
      }

      public ThrowableEntity getThrowable() {
         return this.throwable;
      }
   }

   @Cancelable
   public static class Fireball extends ProjectileImpactEvent {
      private final DamagingProjectileEntity fireball;

      public Fireball(DamagingProjectileEntity fireball, RayTraceResult ray) {
         super(fireball, ray);
         this.fireball = fireball;
      }

      public DamagingProjectileEntity getFireball() {
         return this.fireball;
      }
   }

   @Cancelable
   public static class Arrow extends ProjectileImpactEvent {
      private final AbstractArrowEntity arrow;

      public Arrow(AbstractArrowEntity arrow, RayTraceResult ray) {
         super(arrow, ray);
         this.arrow = arrow;
      }

      public AbstractArrowEntity getArrow() {
         return this.arrow;
      }
   }
}
