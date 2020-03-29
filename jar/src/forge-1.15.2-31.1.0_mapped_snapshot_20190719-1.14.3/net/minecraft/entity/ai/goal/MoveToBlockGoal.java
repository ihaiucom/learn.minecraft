package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorldReader;

public abstract class MoveToBlockGoal extends Goal {
   protected final CreatureEntity creature;
   public final double movementSpeed;
   protected int runDelay;
   protected int timeoutCounter;
   private int maxStayTicks;
   protected BlockPos destinationBlock;
   private boolean isAboveDestination;
   private final int searchLength;
   private final int field_203113_j;
   protected int field_203112_e;

   public MoveToBlockGoal(CreatureEntity p_i45888_1_, double p_i45888_2_, int p_i45888_4_) {
      this(p_i45888_1_, p_i45888_2_, p_i45888_4_, 1);
   }

   public MoveToBlockGoal(CreatureEntity p_i48796_1_, double p_i48796_2_, int p_i48796_4_, int p_i48796_5_) {
      this.destinationBlock = BlockPos.ZERO;
      this.creature = p_i48796_1_;
      this.movementSpeed = p_i48796_2_;
      this.searchLength = p_i48796_4_;
      this.field_203112_e = 0;
      this.field_203113_j = p_i48796_5_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
   }

   public boolean shouldExecute() {
      if (this.runDelay > 0) {
         --this.runDelay;
         return false;
      } else {
         this.runDelay = this.getRunDelay(this.creature);
         return this.searchForDestination();
      }
   }

   protected int getRunDelay(CreatureEntity p_203109_1_) {
      return 200 + p_203109_1_.getRNG().nextInt(200);
   }

   public boolean shouldContinueExecuting() {
      return this.timeoutCounter >= -this.maxStayTicks && this.timeoutCounter <= 1200 && this.shouldMoveTo(this.creature.world, this.destinationBlock);
   }

   public void startExecuting() {
      this.func_220725_g();
      this.timeoutCounter = 0;
      this.maxStayTicks = this.creature.getRNG().nextInt(this.creature.getRNG().nextInt(1200) + 1200) + 1200;
   }

   protected void func_220725_g() {
      this.creature.getNavigator().tryMoveToXYZ((double)((float)this.destinationBlock.getX()) + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)((float)this.destinationBlock.getZ()) + 0.5D, this.movementSpeed);
   }

   public double getTargetDistanceSq() {
      return 1.0D;
   }

   public void tick() {
      if (!this.destinationBlock.up().withinDistance(this.creature.getPositionVec(), this.getTargetDistanceSq())) {
         this.isAboveDestination = false;
         ++this.timeoutCounter;
         if (this.shouldMove()) {
            this.creature.getNavigator().tryMoveToXYZ((double)((float)this.destinationBlock.getX()) + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)((float)this.destinationBlock.getZ()) + 0.5D, this.movementSpeed);
         }
      } else {
         this.isAboveDestination = true;
         --this.timeoutCounter;
      }

   }

   public boolean shouldMove() {
      return this.timeoutCounter % 40 == 0;
   }

   protected boolean getIsAboveDestination() {
      return this.isAboveDestination;
   }

   protected boolean searchForDestination() {
      int lvt_1_1_ = this.searchLength;
      int lvt_2_1_ = this.field_203113_j;
      BlockPos lvt_3_1_ = new BlockPos(this.creature);
      BlockPos.Mutable lvt_4_1_ = new BlockPos.Mutable();

      for(int lvt_5_1_ = this.field_203112_e; lvt_5_1_ <= lvt_2_1_; lvt_5_1_ = lvt_5_1_ > 0 ? -lvt_5_1_ : 1 - lvt_5_1_) {
         for(int lvt_6_1_ = 0; lvt_6_1_ < lvt_1_1_; ++lvt_6_1_) {
            for(int lvt_7_1_ = 0; lvt_7_1_ <= lvt_6_1_; lvt_7_1_ = lvt_7_1_ > 0 ? -lvt_7_1_ : 1 - lvt_7_1_) {
               for(int lvt_8_1_ = lvt_7_1_ < lvt_6_1_ && lvt_7_1_ > -lvt_6_1_ ? lvt_6_1_ : 0; lvt_8_1_ <= lvt_6_1_; lvt_8_1_ = lvt_8_1_ > 0 ? -lvt_8_1_ : 1 - lvt_8_1_) {
                  lvt_4_1_.setPos((Vec3i)lvt_3_1_).move(lvt_7_1_, lvt_5_1_ - 1, lvt_8_1_);
                  if (this.creature.isWithinHomeDistanceFromPosition(lvt_4_1_) && this.shouldMoveTo(this.creature.world, lvt_4_1_)) {
                     this.destinationBlock = lvt_4_1_;
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   protected abstract boolean shouldMoveTo(IWorldReader var1, BlockPos var2);
}
