package net.minecraft.util;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;

public final class EntityPredicates {
   public static final Predicate<Entity> IS_ALIVE = Entity::isAlive;
   public static final Predicate<LivingEntity> IS_LIVING_ALIVE = LivingEntity::isAlive;
   public static final Predicate<Entity> IS_STANDALONE = (p_200821_0_) -> {
      return p_200821_0_.isAlive() && !p_200821_0_.isBeingRidden() && !p_200821_0_.isPassenger();
   };
   public static final Predicate<Entity> HAS_INVENTORY = (p_200822_0_) -> {
      return p_200822_0_ instanceof IInventory && p_200822_0_.isAlive();
   };
   public static final Predicate<Entity> CAN_AI_TARGET = (p_200824_0_) -> {
      return !(p_200824_0_ instanceof PlayerEntity) || !p_200824_0_.isSpectator() && !((PlayerEntity)p_200824_0_).isCreative();
   };
   public static final Predicate<Entity> NOT_SPECTATING = (p_200818_0_) -> {
      return !p_200818_0_.isSpectator();
   };

   public static Predicate<Entity> withinRange(double p_188443_0_, double p_188443_2_, double p_188443_4_, double p_188443_6_) {
      double lvt_8_1_ = p_188443_6_ * p_188443_6_;
      return (p_200819_8_) -> {
         return p_200819_8_ != null && p_200819_8_.getDistanceSq(p_188443_0_, p_188443_2_, p_188443_4_) <= lvt_8_1_;
      };
   }

   public static Predicate<Entity> pushableBy(Entity p_200823_0_) {
      Team lvt_1_1_ = p_200823_0_.getTeam();
      Team.CollisionRule lvt_2_1_ = lvt_1_1_ == null ? Team.CollisionRule.ALWAYS : lvt_1_1_.getCollisionRule();
      return (Predicate)(lvt_2_1_ == Team.CollisionRule.NEVER ? Predicates.alwaysFalse() : NOT_SPECTATING.and((p_210290_3_) -> {
         if (!p_210290_3_.canBePushed()) {
            return false;
         } else if (p_200823_0_.world.isRemote && (!(p_210290_3_ instanceof PlayerEntity) || !((PlayerEntity)p_210290_3_).isUser())) {
            return false;
         } else {
            Team lvt_4_1_ = p_210290_3_.getTeam();
            Team.CollisionRule lvt_5_1_ = lvt_4_1_ == null ? Team.CollisionRule.ALWAYS : lvt_4_1_.getCollisionRule();
            if (lvt_5_1_ == Team.CollisionRule.NEVER) {
               return false;
            } else {
               boolean lvt_6_1_ = lvt_1_1_ != null && lvt_1_1_.isSameTeam(lvt_4_1_);
               if ((lvt_2_1_ == Team.CollisionRule.PUSH_OWN_TEAM || lvt_5_1_ == Team.CollisionRule.PUSH_OWN_TEAM) && lvt_6_1_) {
                  return false;
               } else {
                  return lvt_2_1_ != Team.CollisionRule.PUSH_OTHER_TEAMS && lvt_5_1_ != Team.CollisionRule.PUSH_OTHER_TEAMS || lvt_6_1_;
               }
            }
         }
      }));
   }

   public static Predicate<Entity> notRiding(Entity p_200820_0_) {
      return (p_210289_1_) -> {
         while(true) {
            if (p_210289_1_.isPassenger()) {
               p_210289_1_ = p_210289_1_.getRidingEntity();
               if (p_210289_1_ != p_200820_0_) {
                  continue;
               }

               return false;
            }

            return true;
         }
      };
   }

   public static class ArmoredMob implements Predicate<Entity> {
      private final ItemStack armor;

      public ArmoredMob(ItemStack p_i1584_1_) {
         this.armor = p_i1584_1_;
      }

      public boolean test(@Nullable Entity p_test_1_) {
         if (!p_test_1_.isAlive()) {
            return false;
         } else if (!(p_test_1_ instanceof LivingEntity)) {
            return false;
         } else {
            LivingEntity lvt_2_1_ = (LivingEntity)p_test_1_;
            return lvt_2_1_.func_213365_e(this.armor);
         }
      }

      // $FF: synthetic method
      public boolean test(@Nullable Object p_test_1_) {
         return this.test((Entity)p_test_1_);
      }
   }
}
