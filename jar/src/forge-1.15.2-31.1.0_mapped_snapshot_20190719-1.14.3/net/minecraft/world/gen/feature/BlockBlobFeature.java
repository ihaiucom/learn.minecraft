package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class BlockBlobFeature extends Feature<BlockBlobConfig> {
   public BlockBlobFeature(Function<Dynamic<?>, ? extends BlockBlobConfig> p_i49915_1_) {
      super(p_i49915_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, BlockBlobConfig p_212245_5_) {
      while(true) {
         label48: {
            if (p_212245_4_.getY() > 3) {
               if (p_212245_1_.isAirBlock(p_212245_4_.down())) {
                  break label48;
               }

               Block lvt_6_1_ = p_212245_1_.getBlockState(p_212245_4_.down()).getBlock();
               if (!func_227250_b_(lvt_6_1_) && !func_227249_a_(lvt_6_1_)) {
                  break label48;
               }
            }

            if (p_212245_4_.getY() <= 3) {
               return false;
            }

            int lvt_6_2_ = p_212245_5_.startRadius;

            for(int lvt_7_1_ = 0; lvt_6_2_ >= 0 && lvt_7_1_ < 3; ++lvt_7_1_) {
               int lvt_8_1_ = lvt_6_2_ + p_212245_3_.nextInt(2);
               int lvt_9_1_ = lvt_6_2_ + p_212245_3_.nextInt(2);
               int lvt_10_1_ = lvt_6_2_ + p_212245_3_.nextInt(2);
               float lvt_11_1_ = (float)(lvt_8_1_ + lvt_9_1_ + lvt_10_1_) * 0.333F + 0.5F;
               Iterator var12 = BlockPos.getAllInBoxMutable(p_212245_4_.add(-lvt_8_1_, -lvt_9_1_, -lvt_10_1_), p_212245_4_.add(lvt_8_1_, lvt_9_1_, lvt_10_1_)).iterator();

               while(var12.hasNext()) {
                  BlockPos lvt_13_1_ = (BlockPos)var12.next();
                  if (lvt_13_1_.distanceSq(p_212245_4_) <= (double)(lvt_11_1_ * lvt_11_1_)) {
                     p_212245_1_.setBlockState(lvt_13_1_, p_212245_5_.state, 4);
                  }
               }

               p_212245_4_ = p_212245_4_.add(-(lvt_6_2_ + 1) + p_212245_3_.nextInt(2 + lvt_6_2_ * 2), 0 - p_212245_3_.nextInt(2), -(lvt_6_2_ + 1) + p_212245_3_.nextInt(2 + lvt_6_2_ * 2));
            }

            return true;
         }

         p_212245_4_ = p_212245_4_.down();
      }
   }
}
