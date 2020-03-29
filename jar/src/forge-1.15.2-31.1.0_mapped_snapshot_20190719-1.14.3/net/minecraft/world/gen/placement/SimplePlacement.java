package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public abstract class SimplePlacement<DC extends IPlacementConfig> extends Placement<DC> {
   public SimplePlacement(Function<Dynamic<?>, ? extends DC> p_i51362_1_) {
      super(p_i51362_1_);
   }

   public final Stream<BlockPos> getPositions(IWorld p_212848_1_, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random p_212848_3_, DC p_212848_4_, BlockPos p_212848_5_) {
      return this.getPositions(p_212848_3_, p_212848_4_, p_212848_5_);
   }

   protected abstract Stream<BlockPos> getPositions(Random var1, DC var2, BlockPos var3);
}
