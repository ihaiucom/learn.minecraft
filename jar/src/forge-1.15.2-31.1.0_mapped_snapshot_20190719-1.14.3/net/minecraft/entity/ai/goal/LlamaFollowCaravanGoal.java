package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.util.math.Vec3d;

public class LlamaFollowCaravanGoal extends Goal {
   public final LlamaEntity llama;
   private double speedModifier;
   private int distCheckCounter;

   public LlamaFollowCaravanGoal(LlamaEntity p_i47305_1_, double p_i47305_2_) {
      this.llama = p_i47305_1_;
      this.speedModifier = p_i47305_2_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean shouldExecute() {
      if (!this.llama.getLeashed() && !this.llama.inCaravan()) {
         List<Entity> lvt_1_1_ = this.llama.world.getEntitiesInAABBexcluding(this.llama, this.llama.getBoundingBox().grow(9.0D, 4.0D, 9.0D), (p_220719_0_) -> {
            EntityType<?> lvt_1_1_ = p_220719_0_.getType();
            return lvt_1_1_ == EntityType.LLAMA || lvt_1_1_ == EntityType.TRADER_LLAMA;
         });
         LlamaEntity lvt_2_1_ = null;
         double lvt_3_1_ = Double.MAX_VALUE;
         Iterator var5 = lvt_1_1_.iterator();

         Entity lvt_6_2_;
         LlamaEntity lvt_7_2_;
         double lvt_8_2_;
         while(var5.hasNext()) {
            lvt_6_2_ = (Entity)var5.next();
            lvt_7_2_ = (LlamaEntity)lvt_6_2_;
            if (lvt_7_2_.inCaravan() && !lvt_7_2_.hasCaravanTrail()) {
               lvt_8_2_ = this.llama.getDistanceSq(lvt_7_2_);
               if (lvt_8_2_ <= lvt_3_1_) {
                  lvt_3_1_ = lvt_8_2_;
                  lvt_2_1_ = lvt_7_2_;
               }
            }
         }

         if (lvt_2_1_ == null) {
            var5 = lvt_1_1_.iterator();

            while(var5.hasNext()) {
               lvt_6_2_ = (Entity)var5.next();
               lvt_7_2_ = (LlamaEntity)lvt_6_2_;
               if (lvt_7_2_.getLeashed() && !lvt_7_2_.hasCaravanTrail()) {
                  lvt_8_2_ = this.llama.getDistanceSq(lvt_7_2_);
                  if (lvt_8_2_ <= lvt_3_1_) {
                     lvt_3_1_ = lvt_8_2_;
                     lvt_2_1_ = lvt_7_2_;
                  }
               }
            }
         }

         if (lvt_2_1_ == null) {
            return false;
         } else if (lvt_3_1_ < 4.0D) {
            return false;
         } else if (!lvt_2_1_.getLeashed() && !this.firstIsLeashed(lvt_2_1_, 1)) {
            return false;
         } else {
            this.llama.joinCaravan(lvt_2_1_);
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      if (this.llama.inCaravan() && this.llama.getCaravanHead().isAlive() && this.firstIsLeashed(this.llama, 0)) {
         double lvt_1_1_ = this.llama.getDistanceSq(this.llama.getCaravanHead());
         if (lvt_1_1_ > 676.0D) {
            if (this.speedModifier <= 3.0D) {
               this.speedModifier *= 1.2D;
               this.distCheckCounter = 40;
               return true;
            }

            if (this.distCheckCounter == 0) {
               return false;
            }
         }

         if (this.distCheckCounter > 0) {
            --this.distCheckCounter;
         }

         return true;
      } else {
         return false;
      }
   }

   public void resetTask() {
      this.llama.leaveCaravan();
      this.speedModifier = 2.1D;
   }

   public void tick() {
      if (this.llama.inCaravan()) {
         LlamaEntity lvt_1_1_ = this.llama.getCaravanHead();
         double lvt_2_1_ = (double)this.llama.getDistance(lvt_1_1_);
         float lvt_4_1_ = 2.0F;
         Vec3d lvt_5_1_ = (new Vec3d(lvt_1_1_.func_226277_ct_() - this.llama.func_226277_ct_(), lvt_1_1_.func_226278_cu_() - this.llama.func_226278_cu_(), lvt_1_1_.func_226281_cx_() - this.llama.func_226281_cx_())).normalize().scale(Math.max(lvt_2_1_ - 2.0D, 0.0D));
         this.llama.getNavigator().tryMoveToXYZ(this.llama.func_226277_ct_() + lvt_5_1_.x, this.llama.func_226278_cu_() + lvt_5_1_.y, this.llama.func_226281_cx_() + lvt_5_1_.z, this.speedModifier);
      }
   }

   private boolean firstIsLeashed(LlamaEntity p_190858_1_, int p_190858_2_) {
      if (p_190858_2_ > 8) {
         return false;
      } else if (p_190858_1_.inCaravan()) {
         if (p_190858_1_.getCaravanHead().getLeashed()) {
            return true;
         } else {
            LlamaEntity var10001 = p_190858_1_.getCaravanHead();
            ++p_190858_2_;
            return this.firstIsLeashed(var10001, p_190858_2_);
         }
      } else {
         return false;
      }
   }
}
