package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class CoralClawFeature extends CoralFeature {
   public CoralClawFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49899_1_) {
      super(p_i49899_1_);
   }

   protected boolean func_204623_a(IWorld p_204623_1_, Random p_204623_2_, BlockPos p_204623_3_, BlockState p_204623_4_) {
      if (!this.func_204624_b(p_204623_1_, p_204623_2_, p_204623_3_, p_204623_4_)) {
         return false;
      } else {
         Direction lvt_5_1_ = Direction.Plane.HORIZONTAL.random(p_204623_2_);
         int lvt_6_1_ = p_204623_2_.nextInt(2) + 2;
         List<Direction> lvt_7_1_ = Lists.newArrayList(new Direction[]{lvt_5_1_, lvt_5_1_.rotateY(), lvt_5_1_.rotateYCCW()});
         Collections.shuffle(lvt_7_1_, p_204623_2_);
         List<Direction> lvt_8_1_ = lvt_7_1_.subList(0, lvt_6_1_);
         Iterator var9 = lvt_8_1_.iterator();

         while(var9.hasNext()) {
            Direction lvt_10_1_ = (Direction)var9.next();
            BlockPos.Mutable lvt_11_1_ = new BlockPos.Mutable(p_204623_3_);
            int lvt_12_1_ = p_204623_2_.nextInt(2) + 1;
            lvt_11_1_.move(lvt_10_1_);
            int lvt_13_2_;
            Direction lvt_14_2_;
            if (lvt_10_1_ == lvt_5_1_) {
               lvt_14_2_ = lvt_5_1_;
               lvt_13_2_ = p_204623_2_.nextInt(3) + 2;
            } else {
               lvt_11_1_.move(Direction.UP);
               Direction[] lvt_15_1_ = new Direction[]{lvt_10_1_, Direction.UP};
               lvt_14_2_ = lvt_15_1_[p_204623_2_.nextInt(lvt_15_1_.length)];
               lvt_13_2_ = p_204623_2_.nextInt(3) + 3;
            }

            int lvt_15_3_;
            for(lvt_15_3_ = 0; lvt_15_3_ < lvt_12_1_ && this.func_204624_b(p_204623_1_, p_204623_2_, lvt_11_1_, p_204623_4_); ++lvt_15_3_) {
               lvt_11_1_.move(lvt_14_2_);
            }

            lvt_11_1_.move(lvt_14_2_.getOpposite());
            lvt_11_1_.move(Direction.UP);

            for(lvt_15_3_ = 0; lvt_15_3_ < lvt_13_2_; ++lvt_15_3_) {
               lvt_11_1_.move(lvt_5_1_);
               if (!this.func_204624_b(p_204623_1_, p_204623_2_, lvt_11_1_, p_204623_4_)) {
                  break;
               }

               if (p_204623_2_.nextFloat() < 0.25F) {
                  lvt_11_1_.move(Direction.UP);
               }
            }
         }

         return true;
      }
   }
}
