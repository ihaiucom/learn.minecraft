package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class FillLayerFeature extends Feature<FillLayerConfig> {
   public FillLayerFeature(Function<Dynamic<?>, ? extends FillLayerConfig> p_i49877_1_) {
      super(p_i49877_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, FillLayerConfig p_212245_5_) {
      BlockPos.Mutable lvt_6_1_ = new BlockPos.Mutable();

      for(int lvt_7_1_ = 0; lvt_7_1_ < 16; ++lvt_7_1_) {
         for(int lvt_8_1_ = 0; lvt_8_1_ < 16; ++lvt_8_1_) {
            int lvt_9_1_ = p_212245_4_.getX() + lvt_7_1_;
            int lvt_10_1_ = p_212245_4_.getZ() + lvt_8_1_;
            int lvt_11_1_ = p_212245_5_.height;
            lvt_6_1_.setPos(lvt_9_1_, lvt_11_1_, lvt_10_1_);
            if (p_212245_1_.getBlockState(lvt_6_1_).isAir()) {
               p_212245_1_.setBlockState(lvt_6_1_, p_212245_5_.state, 2);
            }
         }
      }

      return true;
   }
}
