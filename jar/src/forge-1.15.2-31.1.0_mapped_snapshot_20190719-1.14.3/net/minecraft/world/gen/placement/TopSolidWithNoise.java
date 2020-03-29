package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class TopSolidWithNoise extends Placement<TopSolidWithNoiseConfig> {
   public TopSolidWithNoise(Function<Dynamic<?>, ? extends TopSolidWithNoiseConfig> p_i51360_1_) {
      super(p_i51360_1_);
   }

   public Stream<BlockPos> getPositions(IWorld p_212848_1_, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random p_212848_3_, TopSolidWithNoiseConfig p_212848_4_, BlockPos p_212848_5_) {
      double lvt_6_1_ = Biome.INFO_NOISE.func_215464_a((double)p_212848_5_.getX() / p_212848_4_.noiseFactor, (double)p_212848_5_.getZ() / p_212848_4_.noiseFactor, false);
      int lvt_8_1_ = (int)Math.ceil((lvt_6_1_ + p_212848_4_.noiseOffset) * (double)p_212848_4_.noiseToCountRatio);
      return IntStream.range(0, lvt_8_1_).mapToObj((p_227451_4_) -> {
         int lvt_5_1_ = p_212848_3_.nextInt(16) + p_212848_5_.getX();
         int lvt_6_1_ = p_212848_3_.nextInt(16) + p_212848_5_.getZ();
         int lvt_7_1_ = p_212848_1_.getHeight(p_212848_4_.heightmap, lvt_5_1_, lvt_6_1_);
         return new BlockPos(lvt_5_1_, lvt_7_1_, lvt_6_1_);
      });
   }
}
