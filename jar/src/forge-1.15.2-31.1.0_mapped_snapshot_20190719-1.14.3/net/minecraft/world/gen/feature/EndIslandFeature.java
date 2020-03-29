package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class EndIslandFeature extends Feature<NoFeatureConfig> {
   public EndIslandFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49880_1_) {
      super(p_i49880_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      float lvt_6_1_ = (float)(p_212245_3_.nextInt(3) + 4);

      for(int lvt_7_1_ = 0; lvt_6_1_ > 0.5F; --lvt_7_1_) {
         for(int lvt_8_1_ = MathHelper.floor(-lvt_6_1_); lvt_8_1_ <= MathHelper.ceil(lvt_6_1_); ++lvt_8_1_) {
            for(int lvt_9_1_ = MathHelper.floor(-lvt_6_1_); lvt_9_1_ <= MathHelper.ceil(lvt_6_1_); ++lvt_9_1_) {
               if ((float)(lvt_8_1_ * lvt_8_1_ + lvt_9_1_ * lvt_9_1_) <= (lvt_6_1_ + 1.0F) * (lvt_6_1_ + 1.0F)) {
                  this.setBlockState(p_212245_1_, p_212245_4_.add(lvt_8_1_, lvt_7_1_, lvt_9_1_), Blocks.END_STONE.getDefaultState());
               }
            }
         }

         lvt_6_1_ = (float)((double)lvt_6_1_ - ((double)p_212245_3_.nextInt(2) + 0.5D));
      }

      return true;
   }
}
