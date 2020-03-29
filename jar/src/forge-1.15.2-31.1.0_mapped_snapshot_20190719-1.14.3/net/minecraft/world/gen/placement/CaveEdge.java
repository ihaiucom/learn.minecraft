package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class CaveEdge extends Placement<CaveEdgeConfig> {
   public CaveEdge(Function<Dynamic<?>, ? extends CaveEdgeConfig> p_i51396_1_) {
      super(p_i51396_1_);
   }

   public Stream<BlockPos> getPositions(IWorld p_212848_1_, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random p_212848_3_, CaveEdgeConfig p_212848_4_, BlockPos p_212848_5_) {
      IChunk lvt_6_1_ = p_212848_1_.getChunk(p_212848_5_);
      ChunkPos lvt_7_1_ = lvt_6_1_.getPos();
      BitSet lvt_8_1_ = lvt_6_1_.getCarvingMask(p_212848_4_.step);
      return IntStream.range(0, lvt_8_1_.length()).filter((p_215067_3_) -> {
         return lvt_8_1_.get(p_215067_3_) && p_212848_3_.nextFloat() < p_212848_4_.probability;
      }).mapToObj((p_215068_1_) -> {
         int lvt_2_1_ = p_215068_1_ & 15;
         int lvt_3_1_ = p_215068_1_ >> 4 & 15;
         int lvt_4_1_ = p_215068_1_ >> 8;
         return new BlockPos(lvt_7_1_.getXStart() + lvt_2_1_, lvt_4_1_, lvt_7_1_.getZStart() + lvt_3_1_);
      });
   }
}
