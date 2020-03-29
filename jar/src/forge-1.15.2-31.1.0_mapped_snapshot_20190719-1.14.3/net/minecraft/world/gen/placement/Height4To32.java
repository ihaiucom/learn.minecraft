package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class Height4To32 extends SimplePlacement<NoPlacementConfig> {
   public Height4To32(Function<Dynamic<?>, ? extends NoPlacementConfig> p_i51374_1_) {
      super(p_i51374_1_);
   }

   public Stream<BlockPos> getPositions(Random p_212852_1_, NoPlacementConfig p_212852_2_, BlockPos p_212852_3_) {
      int lvt_4_1_ = 3 + p_212852_1_.nextInt(6);
      return IntStream.range(0, lvt_4_1_).mapToObj((p_215060_2_) -> {
         int lvt_3_1_ = p_212852_1_.nextInt(16) + p_212852_3_.getX();
         int lvt_4_1_ = p_212852_1_.nextInt(16) + p_212852_3_.getZ();
         int lvt_5_1_ = p_212852_1_.nextInt(28) + 4;
         return new BlockPos(lvt_3_1_, lvt_5_1_, lvt_4_1_);
      });
   }
}
