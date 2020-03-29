package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BambooBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BambooLeaves;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class BambooFeature extends Feature<ProbabilityConfig> {
   private static final BlockState field_214566_a;
   private static final BlockState field_214567_aS;
   private static final BlockState field_214568_aT;
   private static final BlockState field_214569_aU;

   public BambooFeature(Function<Dynamic<?>, ? extends ProbabilityConfig> p_i49919_1_) {
      super(p_i49919_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, ProbabilityConfig p_212245_5_) {
      int lvt_6_1_ = 0;
      BlockPos.Mutable lvt_7_1_ = new BlockPos.Mutable(p_212245_4_);
      BlockPos.Mutable lvt_8_1_ = new BlockPos.Mutable(p_212245_4_);
      if (p_212245_1_.isAirBlock(lvt_7_1_)) {
         if (Blocks.BAMBOO.getDefaultState().isValidPosition(p_212245_1_, lvt_7_1_)) {
            int lvt_9_1_ = p_212245_3_.nextInt(12) + 5;
            int lvt_10_1_;
            if (p_212245_3_.nextFloat() < p_212245_5_.probability) {
               lvt_10_1_ = p_212245_3_.nextInt(4) + 1;

               for(int lvt_11_1_ = p_212245_4_.getX() - lvt_10_1_; lvt_11_1_ <= p_212245_4_.getX() + lvt_10_1_; ++lvt_11_1_) {
                  for(int lvt_12_1_ = p_212245_4_.getZ() - lvt_10_1_; lvt_12_1_ <= p_212245_4_.getZ() + lvt_10_1_; ++lvt_12_1_) {
                     int lvt_13_1_ = lvt_11_1_ - p_212245_4_.getX();
                     int lvt_14_1_ = lvt_12_1_ - p_212245_4_.getZ();
                     if (lvt_13_1_ * lvt_13_1_ + lvt_14_1_ * lvt_14_1_ <= lvt_10_1_ * lvt_10_1_) {
                        lvt_8_1_.setPos(lvt_11_1_, p_212245_1_.getHeight(Heightmap.Type.WORLD_SURFACE, lvt_11_1_, lvt_12_1_) - 1, lvt_12_1_);
                        if (func_227250_b_(p_212245_1_.getBlockState(lvt_8_1_).getBlock())) {
                           p_212245_1_.setBlockState(lvt_8_1_, Blocks.PODZOL.getDefaultState(), 2);
                        }
                     }
                  }
               }
            }

            for(lvt_10_1_ = 0; lvt_10_1_ < lvt_9_1_ && p_212245_1_.isAirBlock(lvt_7_1_); ++lvt_10_1_) {
               p_212245_1_.setBlockState(lvt_7_1_, field_214566_a, 2);
               lvt_7_1_.move(Direction.UP, 1);
            }

            if (lvt_7_1_.getY() - p_212245_4_.getY() >= 3) {
               p_212245_1_.setBlockState(lvt_7_1_, field_214567_aS, 2);
               p_212245_1_.setBlockState(lvt_7_1_.move(Direction.DOWN, 1), field_214568_aT, 2);
               p_212245_1_.setBlockState(lvt_7_1_.move(Direction.DOWN, 1), field_214569_aU, 2);
            }
         }

         ++lvt_6_1_;
      }

      return lvt_6_1_ > 0;
   }

   static {
      field_214566_a = (BlockState)((BlockState)((BlockState)Blocks.BAMBOO.getDefaultState().with(BambooBlock.PROPERTY_AGE, 1)).with(BambooBlock.PROPERTY_BAMBOO_LEAVES, BambooLeaves.NONE)).with(BambooBlock.PROPERTY_STAGE, 0);
      field_214567_aS = (BlockState)((BlockState)field_214566_a.with(BambooBlock.PROPERTY_BAMBOO_LEAVES, BambooLeaves.LARGE)).with(BambooBlock.PROPERTY_STAGE, 1);
      field_214568_aT = (BlockState)field_214566_a.with(BambooBlock.PROPERTY_BAMBOO_LEAVES, BambooLeaves.LARGE);
      field_214569_aU = (BlockState)field_214566_a.with(BambooBlock.PROPERTY_BAMBOO_LEAVES, BambooLeaves.SMALL);
   }
}
