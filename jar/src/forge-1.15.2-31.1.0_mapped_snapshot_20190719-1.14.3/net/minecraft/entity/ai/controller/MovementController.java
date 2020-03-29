package net.minecraft.entity.ai.controller;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;

public class MovementController {
   protected final MobEntity mob;
   protected double posX;
   protected double posY;
   protected double posZ;
   protected double speed;
   protected float moveForward;
   protected float moveStrafe;
   protected MovementController.Action action;

   public MovementController(MobEntity p_i1614_1_) {
      this.action = MovementController.Action.WAIT;
      this.mob = p_i1614_1_;
   }

   public boolean isUpdating() {
      return this.action == MovementController.Action.MOVE_TO;
   }

   public double getSpeed() {
      return this.speed;
   }

   public void setMoveTo(double p_75642_1_, double p_75642_3_, double p_75642_5_, double p_75642_7_) {
      this.posX = p_75642_1_;
      this.posY = p_75642_3_;
      this.posZ = p_75642_5_;
      this.speed = p_75642_7_;
      if (this.action != MovementController.Action.JUMPING) {
         this.action = MovementController.Action.MOVE_TO;
      }

   }

   public void strafe(float p_188488_1_, float p_188488_2_) {
      this.action = MovementController.Action.STRAFE;
      this.moveForward = p_188488_1_;
      this.moveStrafe = p_188488_2_;
      this.speed = 0.25D;
   }

   public void tick() {
      float lvt_9_1_;
      if (this.action == MovementController.Action.STRAFE) {
         float lvt_1_1_ = (float)this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
         float lvt_2_1_ = (float)this.speed * lvt_1_1_;
         float lvt_3_1_ = this.moveForward;
         float lvt_4_1_ = this.moveStrafe;
         float lvt_5_1_ = MathHelper.sqrt(lvt_3_1_ * lvt_3_1_ + lvt_4_1_ * lvt_4_1_);
         if (lvt_5_1_ < 1.0F) {
            lvt_5_1_ = 1.0F;
         }

         lvt_5_1_ = lvt_2_1_ / lvt_5_1_;
         lvt_3_1_ *= lvt_5_1_;
         lvt_4_1_ *= lvt_5_1_;
         float lvt_6_1_ = MathHelper.sin(this.mob.rotationYaw * 0.017453292F);
         float lvt_7_1_ = MathHelper.cos(this.mob.rotationYaw * 0.017453292F);
         float lvt_8_1_ = lvt_3_1_ * lvt_7_1_ - lvt_4_1_ * lvt_6_1_;
         lvt_9_1_ = lvt_4_1_ * lvt_7_1_ + lvt_3_1_ * lvt_6_1_;
         PathNavigator lvt_10_1_ = this.mob.getNavigator();
         if (lvt_10_1_ != null) {
            NodeProcessor lvt_11_1_ = lvt_10_1_.getNodeProcessor();
            if (lvt_11_1_ != null && lvt_11_1_.getPathNodeType(this.mob.world, MathHelper.floor(this.mob.func_226277_ct_() + (double)lvt_8_1_), MathHelper.floor(this.mob.func_226278_cu_()), MathHelper.floor(this.mob.func_226281_cx_() + (double)lvt_9_1_)) != PathNodeType.WALKABLE) {
               this.moveForward = 1.0F;
               this.moveStrafe = 0.0F;
               lvt_2_1_ = lvt_1_1_;
            }
         }

         this.mob.setAIMoveSpeed(lvt_2_1_);
         this.mob.setMoveForward(this.moveForward);
         this.mob.setMoveStrafing(this.moveStrafe);
         this.action = MovementController.Action.WAIT;
      } else if (this.action == MovementController.Action.MOVE_TO) {
         this.action = MovementController.Action.WAIT;
         double lvt_1_2_ = this.posX - this.mob.func_226277_ct_();
         double lvt_3_2_ = this.posZ - this.mob.func_226281_cx_();
         double lvt_5_2_ = this.posY - this.mob.func_226278_cu_();
         double lvt_7_2_ = lvt_1_2_ * lvt_1_2_ + lvt_5_2_ * lvt_5_2_ + lvt_3_2_ * lvt_3_2_;
         if (lvt_7_2_ < 2.500000277905201E-7D) {
            this.mob.setMoveForward(0.0F);
            return;
         }

         lvt_9_1_ = (float)(MathHelper.atan2(lvt_3_2_, lvt_1_2_) * 57.2957763671875D) - 90.0F;
         this.mob.rotationYaw = this.limitAngle(this.mob.rotationYaw, lvt_9_1_, 90.0F);
         this.mob.setAIMoveSpeed((float)(this.speed * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
         BlockPos lvt_10_2_ = new BlockPos(this.mob);
         BlockState lvt_11_2_ = this.mob.world.getBlockState(lvt_10_2_);
         Block lvt_12_1_ = lvt_11_2_.getBlock();
         VoxelShape lvt_13_1_ = lvt_11_2_.getCollisionShape(this.mob.world, lvt_10_2_);
         if (lvt_5_2_ > (double)this.mob.stepHeight && lvt_1_2_ * lvt_1_2_ + lvt_3_2_ * lvt_3_2_ < (double)Math.max(1.0F, this.mob.getWidth()) || !lvt_13_1_.isEmpty() && this.mob.func_226278_cu_() < lvt_13_1_.getEnd(Direction.Axis.Y) + (double)lvt_10_2_.getY() && !lvt_12_1_.isIn(BlockTags.DOORS) && !lvt_12_1_.isIn(BlockTags.FENCES)) {
            this.mob.getJumpController().setJumping();
            this.action = MovementController.Action.JUMPING;
         }
      } else if (this.action == MovementController.Action.JUMPING) {
         this.mob.setAIMoveSpeed((float)(this.speed * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
         if (this.mob.onGround) {
            this.action = MovementController.Action.WAIT;
         }
      } else {
         this.mob.setMoveForward(0.0F);
      }

   }

   protected float limitAngle(float p_75639_1_, float p_75639_2_, float p_75639_3_) {
      float lvt_4_1_ = MathHelper.wrapDegrees(p_75639_2_ - p_75639_1_);
      if (lvt_4_1_ > p_75639_3_) {
         lvt_4_1_ = p_75639_3_;
      }

      if (lvt_4_1_ < -p_75639_3_) {
         lvt_4_1_ = -p_75639_3_;
      }

      float lvt_5_1_ = p_75639_1_ + lvt_4_1_;
      if (lvt_5_1_ < 0.0F) {
         lvt_5_1_ += 360.0F;
      } else if (lvt_5_1_ > 360.0F) {
         lvt_5_1_ -= 360.0F;
      }

      return lvt_5_1_;
   }

   public double getX() {
      return this.posX;
   }

   public double getY() {
      return this.posY;
   }

   public double getZ() {
      return this.posZ;
   }

   public static enum Action {
      WAIT,
      MOVE_TO,
      STRAFE,
      JUMPING;
   }
}
