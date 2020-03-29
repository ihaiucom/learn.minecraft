package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class DesertWellsFeature extends Feature<NoFeatureConfig> {
   private static final BlockStateMatcher IS_SAND;
   private final BlockState sandSlab;
   private final BlockState sandstone;
   private final BlockState water;

   public DesertWellsFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49887_1_) {
      super(p_i49887_1_);
      this.sandSlab = Blocks.SANDSTONE_SLAB.getDefaultState();
      this.sandstone = Blocks.SANDSTONE.getDefaultState();
      this.water = Blocks.WATER.getDefaultState();
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      for(p_212245_4_ = p_212245_4_.up(); p_212245_1_.isAirBlock(p_212245_4_) && p_212245_4_.getY() > 2; p_212245_4_ = p_212245_4_.down()) {
      }

      if (!IS_SAND.test(p_212245_1_.getBlockState(p_212245_4_))) {
         return false;
      } else {
         int lvt_6_4_;
         int lvt_7_5_;
         for(lvt_6_4_ = -2; lvt_6_4_ <= 2; ++lvt_6_4_) {
            for(lvt_7_5_ = -2; lvt_7_5_ <= 2; ++lvt_7_5_) {
               if (p_212245_1_.isAirBlock(p_212245_4_.add(lvt_6_4_, -1, lvt_7_5_)) && p_212245_1_.isAirBlock(p_212245_4_.add(lvt_6_4_, -2, lvt_7_5_))) {
                  return false;
               }
            }
         }

         for(lvt_6_4_ = -1; lvt_6_4_ <= 0; ++lvt_6_4_) {
            for(lvt_7_5_ = -2; lvt_7_5_ <= 2; ++lvt_7_5_) {
               for(int lvt_8_1_ = -2; lvt_8_1_ <= 2; ++lvt_8_1_) {
                  p_212245_1_.setBlockState(p_212245_4_.add(lvt_7_5_, lvt_6_4_, lvt_8_1_), this.sandstone, 2);
               }
            }
         }

         p_212245_1_.setBlockState(p_212245_4_, this.water, 2);
         Iterator var9 = Direction.Plane.HORIZONTAL.iterator();

         while(var9.hasNext()) {
            Direction lvt_7_3_ = (Direction)var9.next();
            p_212245_1_.setBlockState(p_212245_4_.offset(lvt_7_3_), this.water, 2);
         }

         for(lvt_6_4_ = -2; lvt_6_4_ <= 2; ++lvt_6_4_) {
            for(lvt_7_5_ = -2; lvt_7_5_ <= 2; ++lvt_7_5_) {
               if (lvt_6_4_ == -2 || lvt_6_4_ == 2 || lvt_7_5_ == -2 || lvt_7_5_ == 2) {
                  p_212245_1_.setBlockState(p_212245_4_.add(lvt_6_4_, 1, lvt_7_5_), this.sandstone, 2);
               }
            }
         }

         p_212245_1_.setBlockState(p_212245_4_.add(2, 1, 0), this.sandSlab, 2);
         p_212245_1_.setBlockState(p_212245_4_.add(-2, 1, 0), this.sandSlab, 2);
         p_212245_1_.setBlockState(p_212245_4_.add(0, 1, 2), this.sandSlab, 2);
         p_212245_1_.setBlockState(p_212245_4_.add(0, 1, -2), this.sandSlab, 2);

         for(lvt_6_4_ = -1; lvt_6_4_ <= 1; ++lvt_6_4_) {
            for(lvt_7_5_ = -1; lvt_7_5_ <= 1; ++lvt_7_5_) {
               if (lvt_6_4_ == 0 && lvt_7_5_ == 0) {
                  p_212245_1_.setBlockState(p_212245_4_.add(lvt_6_4_, 4, lvt_7_5_), this.sandstone, 2);
               } else {
                  p_212245_1_.setBlockState(p_212245_4_.add(lvt_6_4_, 4, lvt_7_5_), this.sandSlab, 2);
               }
            }
         }

         for(lvt_6_4_ = 1; lvt_6_4_ <= 3; ++lvt_6_4_) {
            p_212245_1_.setBlockState(p_212245_4_.add(-1, lvt_6_4_, -1), this.sandstone, 2);
            p_212245_1_.setBlockState(p_212245_4_.add(-1, lvt_6_4_, 1), this.sandstone, 2);
            p_212245_1_.setBlockState(p_212245_4_.add(1, lvt_6_4_, -1), this.sandstone, 2);
            p_212245_1_.setBlockState(p_212245_4_.add(1, lvt_6_4_, 1), this.sandstone, 2);
         }

         return true;
      }
   }

   static {
      IS_SAND = BlockStateMatcher.forBlock(Blocks.SAND);
   }
}
