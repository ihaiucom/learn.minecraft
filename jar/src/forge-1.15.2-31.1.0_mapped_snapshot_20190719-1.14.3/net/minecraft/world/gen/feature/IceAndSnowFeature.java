package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyDirtBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class IceAndSnowFeature extends Feature<NoFeatureConfig> {
   public IceAndSnowFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51435_1_) {
      super(p_i51435_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      BlockPos.Mutable lvt_6_1_ = new BlockPos.Mutable();
      BlockPos.Mutable lvt_7_1_ = new BlockPos.Mutable();

      for(int lvt_8_1_ = 0; lvt_8_1_ < 16; ++lvt_8_1_) {
         for(int lvt_9_1_ = 0; lvt_9_1_ < 16; ++lvt_9_1_) {
            int lvt_10_1_ = p_212245_4_.getX() + lvt_8_1_;
            int lvt_11_1_ = p_212245_4_.getZ() + lvt_9_1_;
            int lvt_12_1_ = p_212245_1_.getHeight(Heightmap.Type.MOTION_BLOCKING, lvt_10_1_, lvt_11_1_);
            lvt_6_1_.setPos(lvt_10_1_, lvt_12_1_, lvt_11_1_);
            lvt_7_1_.setPos((Vec3i)lvt_6_1_).move(Direction.DOWN, 1);
            Biome lvt_13_1_ = p_212245_1_.func_226691_t_(lvt_6_1_);
            if (lvt_13_1_.doesWaterFreeze(p_212245_1_, lvt_7_1_, false)) {
               p_212245_1_.setBlockState(lvt_7_1_, Blocks.ICE.getDefaultState(), 2);
            }

            if (lvt_13_1_.doesSnowGenerate(p_212245_1_, lvt_6_1_)) {
               p_212245_1_.setBlockState(lvt_6_1_, Blocks.SNOW.getDefaultState(), 2);
               BlockState lvt_14_1_ = p_212245_1_.getBlockState(lvt_7_1_);
               if (lvt_14_1_.has(SnowyDirtBlock.SNOWY)) {
                  p_212245_1_.setBlockState(lvt_7_1_, (BlockState)lvt_14_1_.with(SnowyDirtBlock.SNOWY, true), 2);
               }
            }
         }
      }

      return true;
   }
}
