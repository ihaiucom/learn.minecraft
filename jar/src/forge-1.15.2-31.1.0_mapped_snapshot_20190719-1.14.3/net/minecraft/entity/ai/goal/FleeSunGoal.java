package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FleeSunGoal extends Goal {
   protected final CreatureEntity creature;
   private double shelterX;
   private double shelterY;
   private double shelterZ;
   private final double movementSpeed;
   private final World world;

   public FleeSunGoal(CreatureEntity p_i1623_1_, double p_i1623_2_) {
      this.creature = p_i1623_1_;
      this.movementSpeed = p_i1623_2_;
      this.world = p_i1623_1_.world;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean shouldExecute() {
      if (this.creature.getAttackTarget() != null) {
         return false;
      } else if (!this.world.isDaytime()) {
         return false;
      } else if (!this.creature.isBurning()) {
         return false;
      } else if (!this.world.func_226660_f_(new BlockPos(this.creature))) {
         return false;
      } else {
         return !this.creature.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty() ? false : this.func_220702_g();
      }
   }

   protected boolean func_220702_g() {
      Vec3d lvt_1_1_ = this.findPossibleShelter();
      if (lvt_1_1_ == null) {
         return false;
      } else {
         this.shelterX = lvt_1_1_.x;
         this.shelterY = lvt_1_1_.y;
         this.shelterZ = lvt_1_1_.z;
         return true;
      }
   }

   public boolean shouldContinueExecuting() {
      return !this.creature.getNavigator().noPath();
   }

   public void startExecuting() {
      this.creature.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
   }

   @Nullable
   protected Vec3d findPossibleShelter() {
      Random lvt_1_1_ = this.creature.getRNG();
      BlockPos lvt_2_1_ = new BlockPos(this.creature);

      for(int lvt_3_1_ = 0; lvt_3_1_ < 10; ++lvt_3_1_) {
         BlockPos lvt_4_1_ = lvt_2_1_.add(lvt_1_1_.nextInt(20) - 10, lvt_1_1_.nextInt(6) - 3, lvt_1_1_.nextInt(20) - 10);
         if (!this.world.func_226660_f_(lvt_4_1_) && this.creature.getBlockPathWeight(lvt_4_1_) < 0.0F) {
            return new Vec3d(lvt_4_1_);
         }
      }

      return null;
   }
}
