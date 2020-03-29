package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class VinesFeature extends Feature<NoFeatureConfig> {
   private static final Direction[] DIRECTIONS = Direction.values();

   public VinesFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51418_1_) {
      super(p_i51418_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_212245_4_);

      for(int i = p_212245_4_.getY(); i < p_212245_1_.getWorld().getDimension().getHeight(); ++i) {
         blockpos$mutable.setPos((Vec3i)p_212245_4_);
         blockpos$mutable.move(p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4), 0, p_212245_3_.nextInt(4) - p_212245_3_.nextInt(4));
         blockpos$mutable.setY(i);
         if (p_212245_1_.isAirBlock(blockpos$mutable)) {
            Direction[] var8 = DIRECTIONS;
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               Direction direction = var8[var10];
               if (direction != Direction.DOWN && VineBlock.canAttachTo(p_212245_1_, blockpos$mutable, direction)) {
                  p_212245_1_.setBlockState(blockpos$mutable, (BlockState)Blocks.VINE.getDefaultState().with(VineBlock.getPropertyFor(direction), true), 2);
                  break;
               }
            }
         }
      }

      return true;
   }
}
