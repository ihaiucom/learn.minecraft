package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RandomSwimmingGoal extends RandomWalkingGoal {
   public RandomSwimmingGoal(CreatureEntity p_i48937_1_, double p_i48937_2_, int p_i48937_4_) {
      super(p_i48937_1_, p_i48937_2_, p_i48937_4_);
   }

   @Nullable
   protected Vec3d getPosition() {
      Vec3d lvt_1_1_ = RandomPositionGenerator.findRandomTarget(this.creature, 10, 7);

      for(int var2 = 0; lvt_1_1_ != null && !this.creature.world.getBlockState(new BlockPos(lvt_1_1_)).allowsMovement(this.creature.world, new BlockPos(lvt_1_1_), PathType.WATER) && var2++ < 10; lvt_1_1_ = RandomPositionGenerator.findRandomTarget(this.creature, 10, 7)) {
      }

      return lvt_1_1_;
   }
}
