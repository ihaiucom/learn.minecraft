package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class AtHeight64 extends Placement<FrequencyConfig> {
   public AtHeight64(Function<Dynamic<?>, ? extends FrequencyConfig> p_i51383_1_) {
      super(p_i51383_1_);
   }

   public Stream<BlockPos> getPositions(IWorld p_212848_1_, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random p_212848_3_, FrequencyConfig p_212848_4_, BlockPos p_212848_5_) {
      return IntStream.range(0, p_212848_4_.count).mapToObj((p_215048_2_) -> {
         int lvt_3_1_ = p_212848_3_.nextInt(16) + p_212848_5_.getX();
         int lvt_4_1_ = p_212848_3_.nextInt(16) + p_212848_5_.getZ();
         int lvt_5_1_ = true;
         return new BlockPos(lvt_3_1_, 64, lvt_4_1_);
      });
   }
}
