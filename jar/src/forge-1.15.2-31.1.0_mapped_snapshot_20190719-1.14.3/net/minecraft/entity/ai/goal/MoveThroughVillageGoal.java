package net.minecraft.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class MoveThroughVillageGoal extends Goal {
   protected final CreatureEntity entity;
   private final double movementSpeed;
   private Path path;
   private BlockPos field_220735_d;
   private final boolean isNocturnal;
   private final List<BlockPos> doorList = Lists.newArrayList();
   private final int field_220736_g;
   private final BooleanSupplier field_220737_h;

   public MoveThroughVillageGoal(CreatureEntity p_i50324_1_, double p_i50324_2_, boolean p_i50324_4_, int p_i50324_5_, BooleanSupplier p_i50324_6_) {
      this.entity = p_i50324_1_;
      this.movementSpeed = p_i50324_2_;
      this.isNocturnal = p_i50324_4_;
      this.field_220736_g = p_i50324_5_;
      this.field_220737_h = p_i50324_6_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      if (!(p_i50324_1_.getNavigator() instanceof GroundPathNavigator)) {
         throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
      }
   }

   public boolean shouldExecute() {
      this.resizeDoorList();
      if (this.isNocturnal && this.entity.world.isDaytime()) {
         return false;
      } else {
         ServerWorld lvt_1_1_ = (ServerWorld)this.entity.world;
         BlockPos lvt_2_1_ = new BlockPos(this.entity);
         if (!lvt_1_1_.func_217471_a(lvt_2_1_, 6)) {
            return false;
         } else {
            Vec3d lvt_3_1_ = RandomPositionGenerator.func_221024_a(this.entity, 15, 7, (p_220734_3_) -> {
               if (!lvt_1_1_.func_217483_b_(p_220734_3_)) {
                  return Double.NEGATIVE_INFINITY;
               } else {
                  Optional<BlockPos> lvt_4_1_ = lvt_1_1_.func_217443_B().func_219127_a(PointOfInterestType.field_221053_a, this::func_220733_a, p_220734_3_, 10, PointOfInterestManager.Status.IS_OCCUPIED);
                  return !lvt_4_1_.isPresent() ? Double.NEGATIVE_INFINITY : -((BlockPos)lvt_4_1_.get()).distanceSq(lvt_2_1_);
               }
            });
            if (lvt_3_1_ == null) {
               return false;
            } else {
               Optional<BlockPos> lvt_4_1_ = lvt_1_1_.func_217443_B().func_219127_a(PointOfInterestType.field_221053_a, this::func_220733_a, new BlockPos(lvt_3_1_), 10, PointOfInterestManager.Status.IS_OCCUPIED);
               if (!lvt_4_1_.isPresent()) {
                  return false;
               } else {
                  this.field_220735_d = ((BlockPos)lvt_4_1_.get()).toImmutable();
                  GroundPathNavigator lvt_5_1_ = (GroundPathNavigator)this.entity.getNavigator();
                  boolean lvt_6_1_ = lvt_5_1_.getEnterDoors();
                  lvt_5_1_.setBreakDoors(this.field_220737_h.getAsBoolean());
                  this.path = lvt_5_1_.getPathToPos(this.field_220735_d, 0);
                  lvt_5_1_.setBreakDoors(lvt_6_1_);
                  if (this.path == null) {
                     Vec3d lvt_7_1_ = RandomPositionGenerator.findRandomTargetBlockTowards(this.entity, 10, 7, new Vec3d(this.field_220735_d));
                     if (lvt_7_1_ == null) {
                        return false;
                     }

                     lvt_5_1_.setBreakDoors(this.field_220737_h.getAsBoolean());
                     this.path = this.entity.getNavigator().func_225466_a(lvt_7_1_.x, lvt_7_1_.y, lvt_7_1_.z, 0);
                     lvt_5_1_.setBreakDoors(lvt_6_1_);
                     if (this.path == null) {
                        return false;
                     }
                  }

                  for(int lvt_7_2_ = 0; lvt_7_2_ < this.path.getCurrentPathLength(); ++lvt_7_2_) {
                     PathPoint lvt_8_1_ = this.path.getPathPointFromIndex(lvt_7_2_);
                     BlockPos lvt_9_1_ = new BlockPos(lvt_8_1_.x, lvt_8_1_.y + 1, lvt_8_1_.z);
                     if (InteractDoorGoal.func_220695_a(this.entity.world, lvt_9_1_)) {
                        this.path = this.entity.getNavigator().func_225466_a((double)lvt_8_1_.x, (double)lvt_8_1_.y, (double)lvt_8_1_.z, 0);
                        break;
                     }
                  }

                  return this.path != null;
               }
            }
         }
      }
   }

   public boolean shouldContinueExecuting() {
      if (this.entity.getNavigator().noPath()) {
         return false;
      } else {
         return !this.field_220735_d.withinDistance(this.entity.getPositionVec(), (double)(this.entity.getWidth() + (float)this.field_220736_g));
      }
   }

   public void startExecuting() {
      this.entity.getNavigator().setPath(this.path, this.movementSpeed);
   }

   public void resetTask() {
      if (this.entity.getNavigator().noPath() || this.field_220735_d.withinDistance(this.entity.getPositionVec(), (double)this.field_220736_g)) {
         this.doorList.add(this.field_220735_d);
      }

   }

   private boolean func_220733_a(BlockPos p_220733_1_) {
      Iterator var2 = this.doorList.iterator();

      BlockPos lvt_3_1_;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         lvt_3_1_ = (BlockPos)var2.next();
      } while(!Objects.equals(p_220733_1_, lvt_3_1_));

      return false;
   }

   private void resizeDoorList() {
      if (this.doorList.size() > 15) {
         this.doorList.remove(0);
      }

   }
}
