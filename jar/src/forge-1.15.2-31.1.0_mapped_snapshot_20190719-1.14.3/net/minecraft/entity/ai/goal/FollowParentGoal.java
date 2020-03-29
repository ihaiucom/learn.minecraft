package net.minecraft.entity.ai.goal;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.passive.AnimalEntity;

public class FollowParentGoal extends Goal {
   private final AnimalEntity field_75348_a;
   private AnimalEntity field_75346_b;
   private final double moveSpeed;
   private int delayCounter;

   public FollowParentGoal(AnimalEntity p_i1626_1_, double p_i1626_2_) {
      this.field_75348_a = p_i1626_1_;
      this.moveSpeed = p_i1626_2_;
   }

   public boolean shouldExecute() {
      if (this.field_75348_a.getGrowingAge() >= 0) {
         return false;
      } else {
         List<AnimalEntity> lvt_1_1_ = this.field_75348_a.world.getEntitiesWithinAABB(this.field_75348_a.getClass(), this.field_75348_a.getBoundingBox().grow(8.0D, 4.0D, 8.0D));
         AnimalEntity lvt_2_1_ = null;
         double lvt_3_1_ = Double.MAX_VALUE;
         Iterator var5 = lvt_1_1_.iterator();

         while(var5.hasNext()) {
            AnimalEntity lvt_6_1_ = (AnimalEntity)var5.next();
            if (lvt_6_1_.getGrowingAge() >= 0) {
               double lvt_7_1_ = this.field_75348_a.getDistanceSq(lvt_6_1_);
               if (lvt_7_1_ <= lvt_3_1_) {
                  lvt_3_1_ = lvt_7_1_;
                  lvt_2_1_ = lvt_6_1_;
               }
            }
         }

         if (lvt_2_1_ == null) {
            return false;
         } else if (lvt_3_1_ < 9.0D) {
            return false;
         } else {
            this.field_75346_b = lvt_2_1_;
            return true;
         }
      }
   }

   public boolean shouldContinueExecuting() {
      if (this.field_75348_a.getGrowingAge() >= 0) {
         return false;
      } else if (!this.field_75346_b.isAlive()) {
         return false;
      } else {
         double lvt_1_1_ = this.field_75348_a.getDistanceSq(this.field_75346_b);
         return lvt_1_1_ >= 9.0D && lvt_1_1_ <= 256.0D;
      }
   }

   public void startExecuting() {
      this.delayCounter = 0;
   }

   public void resetTask() {
      this.field_75346_b = null;
   }

   public void tick() {
      if (--this.delayCounter <= 0) {
         this.delayCounter = 10;
         this.field_75348_a.getNavigator().tryMoveToEntityLiving(this.field_75346_b, this.moveSpeed);
      }
   }
}
