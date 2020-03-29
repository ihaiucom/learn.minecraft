package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

public class MoveThroughVillageAtNightGoal extends Goal {
   private final CreatureEntity field_220756_a;
   private final int field_220757_b;
   @Nullable
   private BlockPos field_220758_c;

   public MoveThroughVillageAtNightGoal(CreatureEntity p_i50321_1_, int p_i50321_2_) {
      this.field_220756_a = p_i50321_1_;
      this.field_220757_b = p_i50321_2_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean shouldExecute() {
      if (this.field_220756_a.isBeingRidden()) {
         return false;
      } else if (this.field_220756_a.world.isDaytime()) {
         return false;
      } else if (this.field_220756_a.getRNG().nextInt(this.field_220757_b) != 0) {
         return false;
      } else {
         ServerWorld lvt_1_1_ = (ServerWorld)this.field_220756_a.world;
         BlockPos lvt_2_1_ = new BlockPos(this.field_220756_a);
         if (!lvt_1_1_.func_217471_a(lvt_2_1_, 6)) {
            return false;
         } else {
            Vec3d lvt_3_1_ = RandomPositionGenerator.func_221024_a(this.field_220756_a, 15, 7, (p_220755_1_) -> {
               return (double)(-lvt_1_1_.func_217486_a(SectionPos.from(p_220755_1_)));
            });
            this.field_220758_c = lvt_3_1_ == null ? null : new BlockPos(lvt_3_1_);
            return this.field_220758_c != null;
         }
      }
   }

   public boolean shouldContinueExecuting() {
      return this.field_220758_c != null && !this.field_220756_a.getNavigator().noPath() && this.field_220756_a.getNavigator().getTargetPos().equals(this.field_220758_c);
   }

   public void tick() {
      if (this.field_220758_c != null) {
         PathNavigator lvt_1_1_ = this.field_220756_a.getNavigator();
         if (lvt_1_1_.noPath() && !this.field_220758_c.withinDistance(this.field_220756_a.getPositionVec(), 10.0D)) {
            Vec3d lvt_2_1_ = new Vec3d(this.field_220758_c);
            Vec3d lvt_3_1_ = this.field_220756_a.getPositionVec();
            Vec3d lvt_4_1_ = lvt_3_1_.subtract(lvt_2_1_);
            lvt_2_1_ = lvt_4_1_.scale(0.4D).add(lvt_2_1_);
            Vec3d lvt_5_1_ = lvt_2_1_.subtract(lvt_3_1_).normalize().scale(10.0D).add(lvt_3_1_);
            BlockPos lvt_6_1_ = new BlockPos(lvt_5_1_);
            lvt_6_1_ = this.field_220756_a.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, lvt_6_1_);
            if (!lvt_1_1_.tryMoveToXYZ((double)lvt_6_1_.getX(), (double)lvt_6_1_.getY(), (double)lvt_6_1_.getZ(), 1.0D)) {
               this.func_220754_g();
            }
         }

      }
   }

   private void func_220754_g() {
      Random lvt_1_1_ = this.field_220756_a.getRNG();
      BlockPos lvt_2_1_ = this.field_220756_a.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (new BlockPos(this.field_220756_a)).add(-8 + lvt_1_1_.nextInt(16), 0, -8 + lvt_1_1_.nextInt(16)));
      this.field_220756_a.getNavigator().tryMoveToXYZ((double)lvt_2_1_.getX(), (double)lvt_2_1_.getY(), (double)lvt_2_1_.getZ(), 1.0D);
   }
}
