package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class MoveTowardsVillageGoal extends RandomWalkingGoal {
   public MoveTowardsVillageGoal(CreatureEntity p_i50325_1_, double p_i50325_2_) {
      super(p_i50325_1_, p_i50325_2_, 10);
   }

   public boolean shouldExecute() {
      ServerWorld lvt_1_1_ = (ServerWorld)this.creature.world;
      BlockPos lvt_2_1_ = new BlockPos(this.creature);
      return lvt_1_1_.func_217483_b_(lvt_2_1_) ? false : super.shouldExecute();
   }

   @Nullable
   protected Vec3d getPosition() {
      ServerWorld lvt_1_1_ = (ServerWorld)this.creature.world;
      BlockPos lvt_2_1_ = new BlockPos(this.creature);
      SectionPos lvt_3_1_ = SectionPos.from(lvt_2_1_);
      SectionPos lvt_4_1_ = BrainUtil.func_220617_a(lvt_1_1_, lvt_3_1_, 2);
      return lvt_4_1_ != lvt_3_1_ ? RandomPositionGenerator.findRandomTargetBlockTowards(this.creature, 10, 7, new Vec3d(lvt_4_1_.getCenter())) : null;
   }
}
