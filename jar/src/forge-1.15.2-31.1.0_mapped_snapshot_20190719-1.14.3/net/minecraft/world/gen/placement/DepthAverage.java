package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class DepthAverage extends SimplePlacement<DepthAverageConfig> {
   public DepthAverage(Function<Dynamic<?>, ? extends DepthAverageConfig> p_i51385_1_) {
      super(p_i51385_1_);
   }

   public Stream<BlockPos> getPositions(Random p_212852_1_, DepthAverageConfig p_212852_2_, BlockPos p_212852_3_) {
      int lvt_4_1_ = p_212852_2_.count;
      int lvt_5_1_ = p_212852_2_.baseline;
      int lvt_6_1_ = p_212852_2_.spread;
      return IntStream.range(0, lvt_4_1_).mapToObj((p_227439_4_) -> {
         int lvt_5_1_x = p_212852_1_.nextInt(16) + p_212852_3_.getX();
         int lvt_6_1_x = p_212852_1_.nextInt(16) + p_212852_3_.getZ();
         int lvt_7_1_ = p_212852_1_.nextInt(lvt_6_1_) + p_212852_1_.nextInt(lvt_6_1_) - lvt_6_1_ + lvt_5_1_;
         return new BlockPos(lvt_5_1_x, lvt_7_1_, lvt_6_1_x);
      });
   }
}
