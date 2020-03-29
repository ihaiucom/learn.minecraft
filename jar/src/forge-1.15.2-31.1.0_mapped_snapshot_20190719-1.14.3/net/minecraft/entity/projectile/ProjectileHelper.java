package net.minecraft.entity.projectile;

import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class ProjectileHelper {
   public static RayTraceResult func_221266_a(Entity p_221266_0_, boolean p_221266_1_, boolean p_221266_2_, @Nullable Entity p_221266_3_, RayTraceContext.BlockMode p_221266_4_) {
      return func_221268_a(p_221266_0_, p_221266_1_, p_221266_2_, p_221266_3_, p_221266_4_, true, (p_lambda$func_221266_a$0_2_) -> {
         return !p_lambda$func_221266_a$0_2_.isSpectator() && p_lambda$func_221266_a$0_2_.canBeCollidedWith() && (p_221266_2_ || !p_lambda$func_221266_a$0_2_.isEntityEqual(p_221266_3_)) && !p_lambda$func_221266_a$0_2_.noClip;
      }, p_221266_0_.getBoundingBox().expand(p_221266_0_.getMotion()).grow(1.0D));
   }

   public static RayTraceResult func_221267_a(Entity p_221267_0_, AxisAlignedBB p_221267_1_, Predicate<Entity> p_221267_2_, RayTraceContext.BlockMode p_221267_3_, boolean p_221267_4_) {
      return func_221268_a(p_221267_0_, p_221267_4_, false, (Entity)null, p_221267_3_, false, p_221267_2_, p_221267_1_);
   }

   @Nullable
   public static EntityRayTraceResult func_221271_a(World p_221271_0_, Entity p_221271_1_, Vec3d p_221271_2_, Vec3d p_221271_3_, AxisAlignedBB p_221271_4_, Predicate<Entity> p_221271_5_) {
      return func_221269_a(p_221271_0_, p_221271_1_, p_221271_2_, p_221271_3_, p_221271_4_, p_221271_5_, Double.MAX_VALUE);
   }

   private static RayTraceResult func_221268_a(Entity p_221268_0_, boolean p_221268_1_, boolean p_221268_2_, @Nullable Entity p_221268_3_, RayTraceContext.BlockMode p_221268_4_, boolean p_221268_5_, Predicate<Entity> p_221268_6_, AxisAlignedBB p_221268_7_) {
      Vec3d vec3d = p_221268_0_.getMotion();
      World world = p_221268_0_.world;
      Vec3d vec3d1 = p_221268_0_.getPositionVec();
      if (p_221268_5_ && !world.func_226662_a_(p_221268_0_, p_221268_0_.getBoundingBox(), (Set)(!p_221268_2_ && p_221268_3_ != null ? getEntityAndMount(p_221268_3_) : ImmutableSet.of()))) {
         return new BlockRayTraceResult(vec3d1, Direction.getFacingFromVector(vec3d.x, vec3d.y, vec3d.z), new BlockPos(p_221268_0_), false);
      } else {
         Vec3d vec3d2 = vec3d1.add(vec3d);
         RayTraceResult raytraceresult = world.rayTraceBlocks(new RayTraceContext(vec3d1, vec3d2, p_221268_4_, RayTraceContext.FluidMode.NONE, p_221268_0_));
         if (p_221268_1_) {
            if (((RayTraceResult)raytraceresult).getType() != RayTraceResult.Type.MISS) {
               vec3d2 = ((RayTraceResult)raytraceresult).getHitVec();
            }

            RayTraceResult raytraceresult1 = func_221271_a(world, p_221268_0_, vec3d1, vec3d2, p_221268_7_, p_221268_6_);
            if (raytraceresult1 != null) {
               raytraceresult = raytraceresult1;
            }
         }

         return (RayTraceResult)raytraceresult;
      }
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static EntityRayTraceResult func_221273_a(Entity p_221273_0_, Vec3d p_221273_1_, Vec3d p_221273_2_, AxisAlignedBB p_221273_3_, Predicate<Entity> p_221273_4_, double p_221273_5_) {
      World world = p_221273_0_.world;
      double d0 = p_221273_5_;
      Entity entity = null;
      Vec3d vec3d = null;
      Iterator var12 = world.getEntitiesInAABBexcluding(p_221273_0_, p_221273_3_, p_221273_4_).iterator();

      while(true) {
         while(var12.hasNext()) {
            Entity entity1 = (Entity)var12.next();
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double)entity1.getCollisionBorderSize());
            Optional<Vec3d> optional = axisalignedbb.rayTrace(p_221273_1_, p_221273_2_);
            if (axisalignedbb.contains(p_221273_1_)) {
               if (d0 >= 0.0D) {
                  entity = entity1;
                  vec3d = (Vec3d)optional.orElse(p_221273_1_);
                  d0 = 0.0D;
               }
            } else if (optional.isPresent()) {
               Vec3d vec3d1 = (Vec3d)optional.get();
               double d1 = p_221273_1_.squareDistanceTo(vec3d1);
               if (d1 < d0 || d0 == 0.0D) {
                  if (entity1.getLowestRidingEntity() == p_221273_0_.getLowestRidingEntity() && !entity1.canRiderInteract()) {
                     if (d0 == 0.0D) {
                        entity = entity1;
                        vec3d = vec3d1;
                     }
                  } else {
                     entity = entity1;
                     vec3d = vec3d1;
                     d0 = d1;
                  }
               }
            }
         }

         return entity == null ? null : new EntityRayTraceResult(entity, vec3d);
      }
   }

   @Nullable
   public static EntityRayTraceResult func_221269_a(World p_221269_0_, Entity p_221269_1_, Vec3d p_221269_2_, Vec3d p_221269_3_, AxisAlignedBB p_221269_4_, Predicate<Entity> p_221269_5_, double p_221269_6_) {
      double d0 = p_221269_6_;
      Entity entity = null;
      Iterator var11 = p_221269_0_.getEntitiesInAABBexcluding(p_221269_1_, p_221269_4_, p_221269_5_).iterator();

      while(var11.hasNext()) {
         Entity entity1 = (Entity)var11.next();
         AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow(0.30000001192092896D);
         Optional<Vec3d> optional = axisalignedbb.rayTrace(p_221269_2_, p_221269_3_);
         if (optional.isPresent()) {
            double d1 = p_221269_2_.squareDistanceTo((Vec3d)optional.get());
            if (d1 < d0) {
               entity = entity1;
               d0 = d1;
            }
         }
      }

      return entity == null ? null : new EntityRayTraceResult(entity);
   }

   private static Set<Entity> getEntityAndMount(Entity p_211325_0_) {
      Entity entity = p_211325_0_.getRidingEntity();
      return entity != null ? ImmutableSet.of(p_211325_0_, entity) : ImmutableSet.of(p_211325_0_);
   }

   public static final void rotateTowardsMovement(Entity p_188803_0_, float p_188803_1_) {
      Vec3d vec3d = p_188803_0_.getMotion();
      float f = MathHelper.sqrt(Entity.func_213296_b(vec3d));
      p_188803_0_.rotationYaw = (float)(MathHelper.atan2(vec3d.z, vec3d.x) * 57.2957763671875D) + 90.0F;

      for(p_188803_0_.rotationPitch = (float)(MathHelper.atan2((double)f, vec3d.y) * 57.2957763671875D) - 90.0F; p_188803_0_.rotationPitch - p_188803_0_.prevRotationPitch < -180.0F; p_188803_0_.prevRotationPitch -= 360.0F) {
      }

      while(p_188803_0_.rotationPitch - p_188803_0_.prevRotationPitch >= 180.0F) {
         p_188803_0_.prevRotationPitch += 360.0F;
      }

      while(p_188803_0_.rotationYaw - p_188803_0_.prevRotationYaw < -180.0F) {
         p_188803_0_.prevRotationYaw -= 360.0F;
      }

      while(p_188803_0_.rotationYaw - p_188803_0_.prevRotationYaw >= 180.0F) {
         p_188803_0_.prevRotationYaw += 360.0F;
      }

      p_188803_0_.rotationPitch = MathHelper.lerp(p_188803_1_, p_188803_0_.prevRotationPitch, p_188803_0_.rotationPitch);
      p_188803_0_.rotationYaw = MathHelper.lerp(p_188803_1_, p_188803_0_.prevRotationYaw, p_188803_0_.rotationYaw);
   }

   public static Hand getHandWith(LivingEntity p_221274_0_, Item p_221274_1_) {
      return p_221274_0_.getHeldItemMainhand().getItem() == p_221274_1_ ? Hand.MAIN_HAND : Hand.OFF_HAND;
   }

   public static AbstractArrowEntity func_221272_a(LivingEntity p_221272_0_, ItemStack p_221272_1_, float p_221272_2_) {
      ArrowItem arrowitem = (ArrowItem)((ArrowItem)(p_221272_1_.getItem() instanceof ArrowItem ? p_221272_1_.getItem() : Items.ARROW));
      AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(p_221272_0_.world, p_221272_1_, p_221272_0_);
      abstractarrowentity.setEnchantmentEffectsFromEntity(p_221272_0_, p_221272_2_);
      if (p_221272_1_.getItem() == Items.TIPPED_ARROW && abstractarrowentity instanceof ArrowEntity) {
         ((ArrowEntity)abstractarrowentity).setPotionEffect(p_221272_1_);
      }

      return abstractarrowentity;
   }
}
