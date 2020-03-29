package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class SphereReplaceFeature extends Feature<SphereReplaceConfig> {
   public SphereReplaceFeature(Function<Dynamic<?>, ? extends SphereReplaceConfig> p_i49885_1_) {
      super(p_i49885_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, SphereReplaceConfig p_212245_5_) {
      if (!p_212245_1_.getFluidState(p_212245_4_).isTagged(FluidTags.WATER)) {
         return false;
      } else {
         int lvt_6_1_ = 0;
         int lvt_7_1_ = p_212245_3_.nextInt(p_212245_5_.radius - 2) + 2;

         for(int lvt_8_1_ = p_212245_4_.getX() - lvt_7_1_; lvt_8_1_ <= p_212245_4_.getX() + lvt_7_1_; ++lvt_8_1_) {
            for(int lvt_9_1_ = p_212245_4_.getZ() - lvt_7_1_; lvt_9_1_ <= p_212245_4_.getZ() + lvt_7_1_; ++lvt_9_1_) {
               int lvt_10_1_ = lvt_8_1_ - p_212245_4_.getX();
               int lvt_11_1_ = lvt_9_1_ - p_212245_4_.getZ();
               if (lvt_10_1_ * lvt_10_1_ + lvt_11_1_ * lvt_11_1_ <= lvt_7_1_ * lvt_7_1_) {
                  for(int lvt_12_1_ = p_212245_4_.getY() - p_212245_5_.ySize; lvt_12_1_ <= p_212245_4_.getY() + p_212245_5_.ySize; ++lvt_12_1_) {
                     BlockPos lvt_13_1_ = new BlockPos(lvt_8_1_, lvt_12_1_, lvt_9_1_);
                     BlockState lvt_14_1_ = p_212245_1_.getBlockState(lvt_13_1_);
                     Iterator var15 = p_212245_5_.targets.iterator();

                     while(var15.hasNext()) {
                        BlockState lvt_16_1_ = (BlockState)var15.next();
                        if (lvt_16_1_.getBlock() == lvt_14_1_.getBlock()) {
                           p_212245_1_.setBlockState(lvt_13_1_, p_212245_5_.state, 2);
                           ++lvt_6_1_;
                           break;
                        }
                     }
                  }
               }
            }
         }

         return lvt_6_1_ > 0;
      }
   }
}
