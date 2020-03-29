package net.minecraft.entity.ai.goal;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class WaterAvoidingRandomFlyingGoal extends WaterAvoidingRandomWalkingGoal {
   public WaterAvoidingRandomFlyingGoal(CreatureEntity p_i47413_1_, double p_i47413_2_) {
      super(p_i47413_1_, p_i47413_2_);
   }

   @Nullable
   protected Vec3d getPosition() {
      Vec3d lvt_1_1_ = null;
      if (this.creature.isInWater()) {
         lvt_1_1_ = RandomPositionGenerator.getLandPos(this.creature, 15, 15);
      }

      if (this.creature.getRNG().nextFloat() >= this.probability) {
         lvt_1_1_ = this.getTreePos();
      }

      return lvt_1_1_ == null ? super.getPosition() : lvt_1_1_;
   }

   @Nullable
   private Vec3d getTreePos() {
      BlockPos lvt_1_1_ = new BlockPos(this.creature);
      BlockPos.Mutable lvt_2_1_ = new BlockPos.Mutable();
      BlockPos.Mutable lvt_3_1_ = new BlockPos.Mutable();
      Iterable<BlockPos> lvt_4_1_ = BlockPos.getAllInBoxMutable(MathHelper.floor(this.creature.func_226277_ct_() - 3.0D), MathHelper.floor(this.creature.func_226278_cu_() - 6.0D), MathHelper.floor(this.creature.func_226281_cx_() - 3.0D), MathHelper.floor(this.creature.func_226277_ct_() + 3.0D), MathHelper.floor(this.creature.func_226278_cu_() + 6.0D), MathHelper.floor(this.creature.func_226281_cx_() + 3.0D));
      Iterator var5 = lvt_4_1_.iterator();

      BlockPos lvt_6_1_;
      boolean lvt_8_1_;
      do {
         do {
            if (!var5.hasNext()) {
               return null;
            }

            lvt_6_1_ = (BlockPos)var5.next();
         } while(lvt_1_1_.equals(lvt_6_1_));

         Block lvt_7_1_ = this.creature.world.getBlockState(lvt_3_1_.setPos((Vec3i)lvt_6_1_).move(Direction.DOWN)).getBlock();
         lvt_8_1_ = lvt_7_1_ instanceof LeavesBlock || lvt_7_1_.isIn(BlockTags.LOGS);
      } while(!lvt_8_1_ || !this.creature.world.isAirBlock(lvt_6_1_) || !this.creature.world.isAirBlock(lvt_2_1_.setPos((Vec3i)lvt_6_1_).move(Direction.UP)));

      return new Vec3d(lvt_6_1_);
   }
}
