package net.minecraft.world.gen.placement;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class NetherFire extends SimplePlacement<FrequencyConfig> {
   public NetherFire(Function<Dynamic<?>, ? extends FrequencyConfig> p_i51356_1_) {
      super(p_i51356_1_);
   }

   public Stream<BlockPos> getPositions(Random p_212852_1_, FrequencyConfig p_212852_2_, BlockPos p_212852_3_) {
      List<BlockPos> lvt_4_1_ = Lists.newArrayList();

      for(int lvt_5_1_ = 0; lvt_5_1_ < p_212852_1_.nextInt(p_212852_1_.nextInt(p_212852_2_.count) + 1) + 1; ++lvt_5_1_) {
         int lvt_6_1_ = p_212852_1_.nextInt(16) + p_212852_3_.getX();
         int lvt_7_1_ = p_212852_1_.nextInt(16) + p_212852_3_.getZ();
         int lvt_8_1_ = p_212852_1_.nextInt(120) + 4;
         lvt_4_1_.add(new BlockPos(lvt_6_1_, lvt_8_1_, lvt_7_1_));
      }

      return lvt_4_1_.stream();
   }
}
