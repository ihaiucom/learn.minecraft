package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public abstract class AbstractBigMushroomFeature extends Feature<BigMushroomFeatureConfig> {
   public AbstractBigMushroomFeature(Function<Dynamic<?>, ? extends BigMushroomFeatureConfig> p_i225795_1_) {
      super(p_i225795_1_);
   }

   protected void func_227210_a_(IWorld p_227210_1_, Random p_227210_2_, BlockPos p_227210_3_, BigMushroomFeatureConfig p_227210_4_, int p_227210_5_, BlockPos.Mutable p_227210_6_) {
      for(int i = 0; i < p_227210_5_; ++i) {
         p_227210_6_.setPos((Vec3i)p_227210_3_).move(Direction.UP, i);
         if (p_227210_1_.getBlockState(p_227210_6_).canBeReplacedByLogs(p_227210_1_, p_227210_6_)) {
            this.setBlockState(p_227210_1_, p_227210_6_, p_227210_4_.field_227273_b_.func_225574_a_(p_227210_2_, p_227210_3_));
         }
      }

   }

   protected int func_227211_a_(Random p_227211_1_) {
      int i = p_227211_1_.nextInt(3) + 4;
      if (p_227211_1_.nextInt(12) == 0) {
         i *= 2;
      }

      return i;
   }

   protected boolean func_227209_a_(IWorld p_227209_1_, BlockPos p_227209_2_, int p_227209_3_, BlockPos.Mutable p_227209_4_, BigMushroomFeatureConfig p_227209_5_) {
      int i = p_227209_2_.getY();
      if (i >= 1 && i + p_227209_3_ + 1 < p_227209_1_.getMaxHeight()) {
         Block block = p_227209_1_.getBlockState(p_227209_2_.down()).getBlock();
         if (!func_227250_b_(block)) {
            return false;
         } else {
            for(int j = 0; j <= p_227209_3_; ++j) {
               int k = this.func_225563_a_(-1, -1, p_227209_5_.field_227274_c_, j);

               for(int l = -k; l <= k; ++l) {
                  for(int i1 = -k; i1 <= k; ++i1) {
                     BlockState blockstate = p_227209_1_.getBlockState(p_227209_4_.setPos((Vec3i)p_227209_2_).move(l, j, i1));
                     if (!blockstate.isAir(p_227209_1_, p_227209_4_) && !blockstate.isIn(BlockTags.LEAVES)) {
                        return false;
                     }
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, BigMushroomFeatureConfig p_212245_5_) {
      int i = this.func_227211_a_(p_212245_3_);
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      if (!this.func_227209_a_(p_212245_1_, p_212245_4_, i, blockpos$mutable, p_212245_5_)) {
         return false;
      } else {
         this.func_225564_a_(p_212245_1_, p_212245_3_, p_212245_4_, i, blockpos$mutable, p_212245_5_);
         this.func_227210_a_(p_212245_1_, p_212245_3_, p_212245_4_, p_212245_5_, i, blockpos$mutable);
         return true;
      }
   }

   protected abstract int func_225563_a_(int var1, int var2, int var3, int var4);

   protected abstract void func_225564_a_(IWorld var1, Random var2, BlockPos var3, int var4, BlockPos.Mutable var5, BigMushroomFeatureConfig var6);
}
