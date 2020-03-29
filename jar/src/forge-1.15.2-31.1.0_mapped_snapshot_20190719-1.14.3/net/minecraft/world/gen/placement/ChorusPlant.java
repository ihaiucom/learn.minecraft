package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class ChorusPlant extends Placement<NoPlacementConfig> {
   public ChorusPlant(Function<Dynamic<?>, ? extends NoPlacementConfig> p_i51391_1_) {
      super(p_i51391_1_);
   }

   public Stream<BlockPos> getPositions(IWorld p_212848_1_, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random p_212848_3_, NoPlacementConfig p_212848_4_, BlockPos p_212848_5_) {
      int lvt_6_1_ = p_212848_3_.nextInt(5);
      return IntStream.range(0, lvt_6_1_).mapToObj((p_227435_3_) -> {
         int lvt_4_1_ = p_212848_3_.nextInt(16) + p_212848_5_.getX();
         int lvt_5_1_ = p_212848_3_.nextInt(16) + p_212848_5_.getZ();
         int lvt_6_1_ = p_212848_1_.getHeight(Heightmap.Type.MOTION_BLOCKING, lvt_4_1_, lvt_5_1_);
         if (lvt_6_1_ > 0) {
            int lvt_7_1_ = lvt_6_1_ - 1;
            return new BlockPos(lvt_4_1_, lvt_7_1_, lvt_5_1_);
         } else {
            return null;
         }
      }).filter(Objects::nonNull);
   }
}
