package net.minecraft.entity.ai.goal;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FollowBoatGoal extends Goal {
   private int field_205143_a;
   private final CreatureEntity field_205144_b;
   private LivingEntity field_205145_c;
   private BoatGoals field_205146_d;

   public FollowBoatGoal(CreatureEntity p_i48939_1_) {
      this.field_205144_b = p_i48939_1_;
   }

   public boolean shouldExecute() {
      List<BoatEntity> lvt_1_1_ = this.field_205144_b.world.getEntitiesWithinAABB(BoatEntity.class, this.field_205144_b.getBoundingBox().grow(5.0D));
      boolean lvt_2_1_ = false;
      Iterator var3 = lvt_1_1_.iterator();

      while(var3.hasNext()) {
         BoatEntity lvt_4_1_ = (BoatEntity)var3.next();
         Entity lvt_5_1_ = lvt_4_1_.getControllingPassenger();
         if (lvt_5_1_ instanceof LivingEntity && (MathHelper.abs(((LivingEntity)lvt_5_1_).moveStrafing) > 0.0F || MathHelper.abs(((LivingEntity)lvt_5_1_).moveForward) > 0.0F)) {
            lvt_2_1_ = true;
            break;
         }
      }

      return this.field_205145_c != null && (MathHelper.abs(this.field_205145_c.moveStrafing) > 0.0F || MathHelper.abs(this.field_205145_c.moveForward) > 0.0F) || lvt_2_1_;
   }

   public boolean isPreemptible() {
      return true;
   }

   public boolean shouldContinueExecuting() {
      return this.field_205145_c != null && this.field_205145_c.isPassenger() && (MathHelper.abs(this.field_205145_c.moveStrafing) > 0.0F || MathHelper.abs(this.field_205145_c.moveForward) > 0.0F);
   }

   public void startExecuting() {
      List<BoatEntity> lvt_1_1_ = this.field_205144_b.world.getEntitiesWithinAABB(BoatEntity.class, this.field_205144_b.getBoundingBox().grow(5.0D));
      Iterator var2 = lvt_1_1_.iterator();

      while(var2.hasNext()) {
         BoatEntity lvt_3_1_ = (BoatEntity)var2.next();
         if (lvt_3_1_.getControllingPassenger() != null && lvt_3_1_.getControllingPassenger() instanceof LivingEntity) {
            this.field_205145_c = (LivingEntity)lvt_3_1_.getControllingPassenger();
            break;
         }
      }

      this.field_205143_a = 0;
      this.field_205146_d = BoatGoals.GO_TO_BOAT;
   }

   public void resetTask() {
      this.field_205145_c = null;
   }

   public void tick() {
      boolean lvt_1_1_ = MathHelper.abs(this.field_205145_c.moveStrafing) > 0.0F || MathHelper.abs(this.field_205145_c.moveForward) > 0.0F;
      float lvt_2_1_ = this.field_205146_d == BoatGoals.GO_IN_BOAT_DIRECTION ? (lvt_1_1_ ? 0.17999999F : 0.0F) : 0.135F;
      this.field_205144_b.moveRelative(lvt_2_1_, new Vec3d((double)this.field_205144_b.moveStrafing, (double)this.field_205144_b.moveVertical, (double)this.field_205144_b.moveForward));
      this.field_205144_b.move(MoverType.SELF, this.field_205144_b.getMotion());
      if (--this.field_205143_a <= 0) {
         this.field_205143_a = 10;
         if (this.field_205146_d == BoatGoals.GO_TO_BOAT) {
            BlockPos lvt_3_1_ = (new BlockPos(this.field_205145_c)).offset(this.field_205145_c.getHorizontalFacing().getOpposite());
            lvt_3_1_ = lvt_3_1_.add(0, -1, 0);
            this.field_205144_b.getNavigator().tryMoveToXYZ((double)lvt_3_1_.getX(), (double)lvt_3_1_.getY(), (double)lvt_3_1_.getZ(), 1.0D);
            if (this.field_205144_b.getDistance(this.field_205145_c) < 4.0F) {
               this.field_205143_a = 0;
               this.field_205146_d = BoatGoals.GO_IN_BOAT_DIRECTION;
            }
         } else if (this.field_205146_d == BoatGoals.GO_IN_BOAT_DIRECTION) {
            Direction lvt_3_2_ = this.field_205145_c.getAdjustedHorizontalFacing();
            BlockPos lvt_4_1_ = (new BlockPos(this.field_205145_c)).offset(lvt_3_2_, 10);
            this.field_205144_b.getNavigator().tryMoveToXYZ((double)lvt_4_1_.getX(), (double)(lvt_4_1_.getY() - 1), (double)lvt_4_1_.getZ(), 1.0D);
            if (this.field_205144_b.getDistance(this.field_205145_c) > 12.0F) {
               this.field_205143_a = 0;
               this.field_205146_d = BoatGoals.GO_TO_BOAT;
            }
         }

      }
   }
}
