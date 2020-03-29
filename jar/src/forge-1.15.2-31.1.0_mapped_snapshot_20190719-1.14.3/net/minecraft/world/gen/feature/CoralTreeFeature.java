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
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;

public class CoralTreeFeature extends CoralFeature {
   public CoralTreeFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49896_1_) {
      super(p_i49896_1_);
   }

   protected boolean func_204623_a(IWorld p_204623_1_, Random p_204623_2_, BlockPos p_204623_3_, BlockState p_204623_4_) {
      BlockPos.Mutable lvt_5_1_ = new BlockPos.Mutable(p_204623_3_);
      int lvt_6_1_ = p_204623_2_.nextInt(3) + 1;

      for(int lvt_7_1_ = 0; lvt_7_1_ < lvt_6_1_; ++lvt_7_1_) {
         if (!this.func_204624_b(p_204623_1_, p_204623_2_, lvt_5_1_, p_204623_4_)) {
            return true;
         }

         lvt_5_1_.move(Direction.UP);
      }

      BlockPos lvt_7_2_ = lvt_5_1_.toImmutable();
      int lvt_8_1_ = p_204623_2_.nextInt(3) + 2;
      List<Direction> lvt_9_1_ = Lists.newArrayList(Direction.Plane.HORIZONTAL);
      Collections.shuffle(lvt_9_1_, p_204623_2_);
      List<Direction> lvt_10_1_ = lvt_9_1_.subList(0, lvt_8_1_);
      Iterator var11 = lvt_10_1_.iterator();

      while(var11.hasNext()) {
         Direction lvt_12_1_ = (Direction)var11.next();
         lvt_5_1_.setPos((Vec3i)lvt_7_2_);
         lvt_5_1_.move(lvt_12_1_);
         int lvt_13_1_ = p_204623_2_.nextInt(5) + 2;
         int lvt_14_1_ = 0;

         for(int lvt_15_1_ = 0; lvt_15_1_ < lvt_13_1_ && this.func_204624_b(p_204623_1_, p_204623_2_, lvt_5_1_, p_204623_4_); ++lvt_15_1_) {
            ++lvt_14_1_;
            lvt_5_1_.move(Direction.UP);
            if (lvt_15_1_ == 0 || lvt_14_1_ >= 2 && p_204623_2_.nextFloat() < 0.25F) {
               lvt_5_1_.move(lvt_12_1_);
               lvt_14_1_ = 0;
            }
         }
      }

      return true;
   }
}
