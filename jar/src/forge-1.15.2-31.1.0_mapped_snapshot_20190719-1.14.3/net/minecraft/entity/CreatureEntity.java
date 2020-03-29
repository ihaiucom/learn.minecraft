package net.minecraft.entity;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class CreatureEntity extends MobEntity {
   protected CreatureEntity(EntityType<? extends CreatureEntity> p_i48575_1_, World p_i48575_2_) {
      super(p_i48575_1_, p_i48575_2_);
   }

   public float getBlockPathWeight(BlockPos p_180484_1_) {
      return this.getBlockPathWeight(p_180484_1_, this.world);
   }

   public float getBlockPathWeight(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      return 0.0F;
   }

   public boolean canSpawn(IWorld p_213380_1_, SpawnReason p_213380_2_) {
      return this.getBlockPathWeight(new BlockPos(this), p_213380_1_) >= 0.0F;
   }

   public boolean hasPath() {
      return !this.getNavigator().noPath();
   }

   protected void updateLeashedState() {
      super.updateLeashedState();
      Entity lvt_1_1_ = this.getLeashHolder();
      if (lvt_1_1_ != null && lvt_1_1_.world == this.world) {
         this.setHomePosAndDistance(new BlockPos(lvt_1_1_), 5);
         float lvt_2_1_ = this.getDistance(lvt_1_1_);
         if (this instanceof TameableEntity && ((TameableEntity)this).isSitting()) {
            if (lvt_2_1_ > 10.0F) {
               this.clearLeashed(true, true);
            }

            return;
         }

         this.onLeashDistance(lvt_2_1_);
         if (lvt_2_1_ > 10.0F) {
            this.clearLeashed(true, true);
            this.goalSelector.disableFlag(Goal.Flag.MOVE);
         } else if (lvt_2_1_ > 6.0F) {
            double lvt_3_1_ = (lvt_1_1_.func_226277_ct_() - this.func_226277_ct_()) / (double)lvt_2_1_;
            double lvt_5_1_ = (lvt_1_1_.func_226278_cu_() - this.func_226278_cu_()) / (double)lvt_2_1_;
            double lvt_7_1_ = (lvt_1_1_.func_226281_cx_() - this.func_226281_cx_()) / (double)lvt_2_1_;
            this.setMotion(this.getMotion().add(Math.copySign(lvt_3_1_ * lvt_3_1_ * 0.4D, lvt_3_1_), Math.copySign(lvt_5_1_ * lvt_5_1_ * 0.4D, lvt_5_1_), Math.copySign(lvt_7_1_ * lvt_7_1_ * 0.4D, lvt_7_1_)));
         } else {
            this.goalSelector.enableFlag(Goal.Flag.MOVE);
            float lvt_3_2_ = 2.0F;
            Vec3d lvt_4_1_ = (new Vec3d(lvt_1_1_.func_226277_ct_() - this.func_226277_ct_(), lvt_1_1_.func_226278_cu_() - this.func_226278_cu_(), lvt_1_1_.func_226281_cx_() - this.func_226281_cx_())).normalize().scale((double)Math.max(lvt_2_1_ - 2.0F, 0.0F));
            this.getNavigator().tryMoveToXYZ(this.func_226277_ct_() + lvt_4_1_.x, this.func_226278_cu_() + lvt_4_1_.y, this.func_226281_cx_() + lvt_4_1_.z, this.followLeashSpeed());
         }
      }

   }

   protected double followLeashSpeed() {
      return 1.0D;
   }

   protected void onLeashDistance(float p_142017_1_) {
   }
}
