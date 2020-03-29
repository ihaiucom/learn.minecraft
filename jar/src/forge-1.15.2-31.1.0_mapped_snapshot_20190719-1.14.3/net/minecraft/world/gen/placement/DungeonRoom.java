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

public class DungeonRoom extends Placement<ChanceConfig> {
   public DungeonRoom(Function<Dynamic<?>, ? extends ChanceConfig> p_i51366_1_) {
      super(p_i51366_1_);
   }

   public Stream<BlockPos> getPositions(IWorld p_212848_1_, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random p_212848_3_, ChanceConfig p_212848_4_, BlockPos p_212848_5_) {
      int lvt_6_1_ = p_212848_4_.chance;
      return IntStream.range(0, lvt_6_1_).mapToObj((p_227448_3_) -> {
         int lvt_4_1_ = p_212848_3_.nextInt(16) + p_212848_5_.getX();
         int lvt_5_1_ = p_212848_3_.nextInt(16) + p_212848_5_.getZ();
         int lvt_6_1_ = p_212848_3_.nextInt(p_212848_2_.getMaxHeight());
         return new BlockPos(lvt_4_1_, lvt_6_1_, lvt_5_1_);
      });
   }
}
