package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public interface IEntityReader {
   List<Entity> getEntitiesInAABBexcluding(@Nullable Entity var1, AxisAlignedBB var2, @Nullable Predicate<? super Entity> var3);

   <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> var1, AxisAlignedBB var2, @Nullable Predicate<? super T> var3);

   default <T extends Entity> List<T> func_225316_b(Class<? extends T> p_225316_1_, AxisAlignedBB p_225316_2_, @Nullable Predicate<? super T> p_225316_3_) {
      return this.getEntitiesWithinAABB(p_225316_1_, p_225316_2_, p_225316_3_);
   }

   List<? extends PlayerEntity> getPlayers();

   default List<Entity> getEntitiesWithinAABBExcludingEntity(@Nullable Entity p_72839_1_, AxisAlignedBB p_72839_2_) {
      return this.getEntitiesInAABBexcluding(p_72839_1_, p_72839_2_, EntityPredicates.NOT_SPECTATING);
   }

   default boolean checkNoEntityCollision(@Nullable Entity p_195585_1_, VoxelShape p_195585_2_) {
      return p_195585_2_.isEmpty() ? true : this.getEntitiesWithinAABBExcludingEntity(p_195585_1_, p_195585_2_.getBoundingBox()).stream().filter((p_217364_1_) -> {
         return !p_217364_1_.removed && p_217364_1_.preventEntitySpawning && (p_195585_1_ == null || !p_217364_1_.isRidingSameEntity(p_195585_1_));
      }).noneMatch((p_217356_1_) -> {
         return VoxelShapes.compare(p_195585_2_, VoxelShapes.create(p_217356_1_.getBoundingBox()), IBooleanFunction.AND);
      });
   }

   default <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> p_217357_1_, AxisAlignedBB p_217357_2_) {
      return this.getEntitiesWithinAABB(p_217357_1_, p_217357_2_, EntityPredicates.NOT_SPECTATING);
   }

   default <T extends Entity> List<T> func_225317_b(Class<? extends T> p_225317_1_, AxisAlignedBB p_225317_2_) {
      return this.func_225316_b(p_225317_1_, p_225317_2_, EntityPredicates.NOT_SPECTATING);
   }

   default Stream<VoxelShape> getEmptyCollisionShapes(@Nullable Entity p_223439_1_, AxisAlignedBB p_223439_2_, Set<Entity> p_223439_3_) {
      if (p_223439_2_.getAverageEdgeLength() < 1.0E-7D) {
         return Stream.empty();
      } else {
         AxisAlignedBB lvt_4_1_ = p_223439_2_.grow(1.0E-7D);
         Stream var10000 = this.getEntitiesWithinAABBExcludingEntity(p_223439_1_, lvt_4_1_).stream().filter((p_217367_1_) -> {
            return !p_223439_3_.contains(p_217367_1_);
         }).filter((p_223442_1_) -> {
            return p_223439_1_ == null || !p_223439_1_.isRidingSameEntity(p_223442_1_);
         }).flatMap((p_217368_1_) -> {
            return Stream.of(p_217368_1_.getCollisionBoundingBox(), p_223439_1_ == null ? null : p_223439_1_.getCollisionBox(p_217368_1_));
         }).filter(Objects::nonNull);
         lvt_4_1_.getClass();
         return var10000.filter(lvt_4_1_::intersects).map(VoxelShapes::create);
      }
   }

   @Nullable
   default PlayerEntity getClosestPlayer(double p_190525_1_, double p_190525_3_, double p_190525_5_, double p_190525_7_, @Nullable Predicate<Entity> p_190525_9_) {
      double lvt_10_1_ = -1.0D;
      PlayerEntity lvt_12_1_ = null;
      Iterator var13 = this.getPlayers().iterator();

      while(true) {
         PlayerEntity lvt_14_1_;
         double lvt_15_1_;
         do {
            do {
               do {
                  if (!var13.hasNext()) {
                     return lvt_12_1_;
                  }

                  lvt_14_1_ = (PlayerEntity)var13.next();
               } while(p_190525_9_ != null && !p_190525_9_.test(lvt_14_1_));

               lvt_15_1_ = lvt_14_1_.getDistanceSq(p_190525_1_, p_190525_3_, p_190525_5_);
            } while(p_190525_7_ >= 0.0D && lvt_15_1_ >= p_190525_7_ * p_190525_7_);
         } while(lvt_10_1_ != -1.0D && lvt_15_1_ >= lvt_10_1_);

         lvt_10_1_ = lvt_15_1_;
         lvt_12_1_ = lvt_14_1_;
      }
   }

   @Nullable
   default PlayerEntity getClosestPlayer(Entity p_217362_1_, double p_217362_2_) {
      return this.getClosestPlayer(p_217362_1_.func_226277_ct_(), p_217362_1_.func_226278_cu_(), p_217362_1_.func_226281_cx_(), p_217362_2_, false);
   }

   @Nullable
   default PlayerEntity getClosestPlayer(double p_217366_1_, double p_217366_3_, double p_217366_5_, double p_217366_7_, boolean p_217366_9_) {
      Predicate<Entity> lvt_10_1_ = p_217366_9_ ? EntityPredicates.CAN_AI_TARGET : EntityPredicates.NOT_SPECTATING;
      return this.getClosestPlayer(p_217366_1_, p_217366_3_, p_217366_5_, p_217366_7_, lvt_10_1_);
   }

   @Nullable
   default PlayerEntity getClosestPlayer(double p_217365_1_, double p_217365_3_, double p_217365_5_) {
      double lvt_7_1_ = -1.0D;
      PlayerEntity lvt_9_1_ = null;
      Iterator var10 = this.getPlayers().iterator();

      while(true) {
         PlayerEntity lvt_11_1_;
         double lvt_12_1_;
         do {
            do {
               do {
                  if (!var10.hasNext()) {
                     return lvt_9_1_;
                  }

                  lvt_11_1_ = (PlayerEntity)var10.next();
               } while(!EntityPredicates.NOT_SPECTATING.test(lvt_11_1_));

               lvt_12_1_ = lvt_11_1_.getDistanceSq(p_217365_1_, lvt_11_1_.func_226278_cu_(), p_217365_3_);
            } while(p_217365_5_ >= 0.0D && lvt_12_1_ >= p_217365_5_ * p_217365_5_);
         } while(lvt_7_1_ != -1.0D && lvt_12_1_ >= lvt_7_1_);

         lvt_7_1_ = lvt_12_1_;
         lvt_9_1_ = lvt_11_1_;
      }
   }

   default boolean isPlayerWithin(double p_217358_1_, double p_217358_3_, double p_217358_5_, double p_217358_7_) {
      Iterator var9 = this.getPlayers().iterator();

      double lvt_11_1_;
      do {
         PlayerEntity lvt_10_1_;
         do {
            do {
               if (!var9.hasNext()) {
                  return false;
               }

               lvt_10_1_ = (PlayerEntity)var9.next();
            } while(!EntityPredicates.NOT_SPECTATING.test(lvt_10_1_));
         } while(!EntityPredicates.IS_LIVING_ALIVE.test(lvt_10_1_));

         lvt_11_1_ = lvt_10_1_.getDistanceSq(p_217358_1_, p_217358_3_, p_217358_5_);
      } while(p_217358_7_ >= 0.0D && lvt_11_1_ >= p_217358_7_ * p_217358_7_);

      return true;
   }

   @Nullable
   default PlayerEntity getClosestPlayer(EntityPredicate p_217370_1_, LivingEntity p_217370_2_) {
      return (PlayerEntity)this.getClosestEntity(this.getPlayers(), p_217370_1_, p_217370_2_, p_217370_2_.func_226277_ct_(), p_217370_2_.func_226278_cu_(), p_217370_2_.func_226281_cx_());
   }

   @Nullable
   default PlayerEntity getClosestPlayer(EntityPredicate p_217372_1_, LivingEntity p_217372_2_, double p_217372_3_, double p_217372_5_, double p_217372_7_) {
      return (PlayerEntity)this.getClosestEntity(this.getPlayers(), p_217372_1_, p_217372_2_, p_217372_3_, p_217372_5_, p_217372_7_);
   }

   @Nullable
   default PlayerEntity getClosestPlayer(EntityPredicate p_217359_1_, double p_217359_2_, double p_217359_4_, double p_217359_6_) {
      return (PlayerEntity)this.getClosestEntity(this.getPlayers(), p_217359_1_, (LivingEntity)null, p_217359_2_, p_217359_4_, p_217359_6_);
   }

   @Nullable
   default <T extends LivingEntity> T getClosestEntityWithinAABB(Class<? extends T> p_217360_1_, EntityPredicate p_217360_2_, @Nullable LivingEntity p_217360_3_, double p_217360_4_, double p_217360_6_, double p_217360_8_, AxisAlignedBB p_217360_10_) {
      return this.getClosestEntity(this.getEntitiesWithinAABB(p_217360_1_, p_217360_10_, (Predicate)null), p_217360_2_, p_217360_3_, p_217360_4_, p_217360_6_, p_217360_8_);
   }

   @Nullable
   default <T extends LivingEntity> T func_225318_b(Class<? extends T> p_225318_1_, EntityPredicate p_225318_2_, @Nullable LivingEntity p_225318_3_, double p_225318_4_, double p_225318_6_, double p_225318_8_, AxisAlignedBB p_225318_10_) {
      return this.getClosestEntity(this.func_225316_b(p_225318_1_, p_225318_10_, (Predicate)null), p_225318_2_, p_225318_3_, p_225318_4_, p_225318_6_, p_225318_8_);
   }

   @Nullable
   default <T extends LivingEntity> T getClosestEntity(List<? extends T> p_217361_1_, EntityPredicate p_217361_2_, @Nullable LivingEntity p_217361_3_, double p_217361_4_, double p_217361_6_, double p_217361_8_) {
      double lvt_10_1_ = -1.0D;
      T lvt_12_1_ = null;
      Iterator var13 = p_217361_1_.iterator();

      while(true) {
         LivingEntity lvt_14_1_;
         double lvt_15_1_;
         do {
            do {
               if (!var13.hasNext()) {
                  return lvt_12_1_;
               }

               lvt_14_1_ = (LivingEntity)var13.next();
            } while(!p_217361_2_.canTarget(p_217361_3_, lvt_14_1_));

            lvt_15_1_ = lvt_14_1_.getDistanceSq(p_217361_4_, p_217361_6_, p_217361_8_);
         } while(lvt_10_1_ != -1.0D && lvt_15_1_ >= lvt_10_1_);

         lvt_10_1_ = lvt_15_1_;
         lvt_12_1_ = lvt_14_1_;
      }
   }

   default List<PlayerEntity> getTargettablePlayersWithinAABB(EntityPredicate p_217373_1_, LivingEntity p_217373_2_, AxisAlignedBB p_217373_3_) {
      List<PlayerEntity> lvt_4_1_ = Lists.newArrayList();
      Iterator var5 = this.getPlayers().iterator();

      while(var5.hasNext()) {
         PlayerEntity lvt_6_1_ = (PlayerEntity)var5.next();
         if (p_217373_3_.contains(lvt_6_1_.func_226277_ct_(), lvt_6_1_.func_226278_cu_(), lvt_6_1_.func_226281_cx_()) && p_217373_1_.canTarget(p_217373_2_, lvt_6_1_)) {
            lvt_4_1_.add(lvt_6_1_);
         }
      }

      return lvt_4_1_;
   }

   default <T extends LivingEntity> List<T> getTargettableEntitiesWithinAABB(Class<? extends T> p_217374_1_, EntityPredicate p_217374_2_, LivingEntity p_217374_3_, AxisAlignedBB p_217374_4_) {
      List<T> lvt_5_1_ = this.getEntitiesWithinAABB(p_217374_1_, p_217374_4_, (Predicate)null);
      List<T> lvt_6_1_ = Lists.newArrayList();
      Iterator var7 = lvt_5_1_.iterator();

      while(var7.hasNext()) {
         T lvt_8_1_ = (LivingEntity)var7.next();
         if (p_217374_2_.canTarget(p_217374_3_, lvt_8_1_)) {
            lvt_6_1_.add(lvt_8_1_);
         }
      }

      return lvt_6_1_;
   }

   @Nullable
   default PlayerEntity getPlayerByUuid(UUID p_217371_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < this.getPlayers().size(); ++lvt_2_1_) {
         PlayerEntity lvt_3_1_ = (PlayerEntity)this.getPlayers().get(lvt_2_1_);
         if (p_217371_1_.equals(lvt_3_1_.getUniqueID())) {
            return lvt_3_1_;
         }
      }

      return null;
   }
}
