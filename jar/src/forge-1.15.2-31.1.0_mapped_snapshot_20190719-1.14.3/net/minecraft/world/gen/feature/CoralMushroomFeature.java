package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class CoralMushroomFeature extends CoralFeature {
   public CoralMushroomFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49897_1_) {
      super(p_i49897_1_);
   }

   protected boolean func_204623_a(IWorld p_204623_1_, Random p_204623_2_, BlockPos p_204623_3_, BlockState p_204623_4_) {
      int lvt_5_1_ = p_204623_2_.nextInt(3) + 3;
      int lvt_6_1_ = p_204623_2_.nextInt(3) + 3;
      int lvt_7_1_ = p_204623_2_.nextInt(3) + 3;
      int lvt_8_1_ = p_204623_2_.nextInt(3) + 1;
      BlockPos.Mutable lvt_9_1_ = new BlockPos.Mutable(p_204623_3_);

      for(int lvt_10_1_ = 0; lvt_10_1_ <= lvt_6_1_; ++lvt_10_1_) {
         for(int lvt_11_1_ = 0; lvt_11_1_ <= lvt_5_1_; ++lvt_11_1_) {
            for(int lvt_12_1_ = 0; lvt_12_1_ <= lvt_7_1_; ++lvt_12_1_) {
               lvt_9_1_.setPos(lvt_10_1_ + p_204623_3_.getX(), lvt_11_1_ + p_204623_3_.getY(), lvt_12_1_ + p_204623_3_.getZ());
               lvt_9_1_.move(Direction.DOWN, lvt_8_1_);
               if ((lvt_10_1_ != 0 && lvt_10_1_ != lvt_6_1_ || lvt_11_1_ != 0 && lvt_11_1_ != lvt_5_1_) && (lvt_12_1_ != 0 && lvt_12_1_ != lvt_7_1_ || lvt_11_1_ != 0 && lvt_11_1_ != lvt_5_1_) && (lvt_10_1_ != 0 && lvt_10_1_ != lvt_6_1_ || lvt_12_1_ != 0 && lvt_12_1_ != lvt_7_1_) && (lvt_10_1_ == 0 || lvt_10_1_ == lvt_6_1_ || lvt_11_1_ == 0 || lvt_11_1_ == lvt_5_1_ || lvt_12_1_ == 0 || lvt_12_1_ == lvt_7_1_) && p_204623_2_.nextFloat() >= 0.1F && !this.func_204624_b(p_204623_1_, p_204623_2_, lvt_9_1_, p_204623_4_)) {
               }
            }
         }
      }

      return true;
   }
}
