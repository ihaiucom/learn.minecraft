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

public class DarkOakTreePlacement extends Placement<NoPlacementConfig> {
   public DarkOakTreePlacement(Function<Dynamic<?>, ? extends NoPlacementConfig> p_i51377_1_) {
      super(p_i51377_1_);
   }

   public Stream<BlockPos> getPositions(IWorld p_212848_1_, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random p_212848_3_, NoPlacementConfig p_212848_4_, BlockPos p_212848_5_) {
      return IntStream.range(0, 16).mapToObj((p_227445_3_) -> {
         int lvt_4_1_ = p_227445_3_ / 4;
         int lvt_5_1_ = p_227445_3_ % 4;
         int lvt_6_1_ = lvt_4_1_ * 4 + 1 + p_212848_3_.nextInt(3) + p_212848_5_.getX();
         int lvt_7_1_ = lvt_5_1_ * 4 + 1 + p_212848_3_.nextInt(3) + p_212848_5_.getZ();
         int lvt_8_1_ = p_212848_1_.getHeight(Heightmap.Type.MOTION_BLOCKING, lvt_6_1_, lvt_7_1_);
         return new BlockPos(lvt_6_1_, lvt_8_1_, lvt_7_1_);
      });
   }
}
