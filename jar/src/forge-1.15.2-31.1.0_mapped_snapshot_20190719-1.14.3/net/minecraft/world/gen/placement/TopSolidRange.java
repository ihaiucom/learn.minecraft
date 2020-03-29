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
import net.minecraft.world.gen.Heightmap;

public class TopSolidRange extends Placement<TopSolidRangeConfig> {
   public TopSolidRange(Function<Dynamic<?>, ? extends TopSolidRangeConfig> p_i51359_1_) {
      super(p_i51359_1_);
   }

   public Stream<BlockPos> getPositions(IWorld p_212848_1_, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random p_212848_3_, TopSolidRangeConfig p_212848_4_, BlockPos p_212848_5_) {
      int lvt_6_1_ = p_212848_3_.nextInt(p_212848_4_.max - p_212848_4_.min) + p_212848_4_.min;
      return IntStream.range(0, lvt_6_1_).mapToObj((p_227452_3_) -> {
         int lvt_4_1_ = p_212848_3_.nextInt(16) + p_212848_5_.getX();
         int lvt_5_1_ = p_212848_3_.nextInt(16) + p_212848_5_.getZ();
         int lvt_6_1_ = p_212848_1_.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, lvt_4_1_, lvt_5_1_);
         return new BlockPos(lvt_4_1_, lvt_6_1_, lvt_5_1_);
      });
   }
}
