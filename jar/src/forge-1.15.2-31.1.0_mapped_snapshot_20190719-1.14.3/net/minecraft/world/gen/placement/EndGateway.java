package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class EndGateway extends Placement<NoPlacementConfig> {
   public EndGateway(Function<Dynamic<?>, ? extends NoPlacementConfig> p_i51373_1_) {
      super(p_i51373_1_);
   }

   public Stream<BlockPos> getPositions(IWorld p_212848_1_, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random p_212848_3_, NoPlacementConfig p_212848_4_, BlockPos p_212848_5_) {
      if (p_212848_3_.nextInt(700) == 0) {
         int lvt_6_1_ = p_212848_3_.nextInt(16) + p_212848_5_.getX();
         int lvt_7_1_ = p_212848_3_.nextInt(16) + p_212848_5_.getZ();
         int lvt_8_1_ = p_212848_1_.getHeight(Heightmap.Type.MOTION_BLOCKING, lvt_6_1_, lvt_7_1_);
         if (lvt_8_1_ > 0) {
            int lvt_9_1_ = lvt_8_1_ + 3 + p_212848_3_.nextInt(7);
            return Stream.of(new BlockPos(lvt_6_1_, lvt_9_1_, lvt_7_1_));
         }
      }

      return Stream.empty();
   }
}
