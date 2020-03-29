package net.minecraft.world.gen.feature.structure;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;

public class DesertPyramidPiece extends ScatteredStructurePiece {
   private final boolean[] hasPlacedChest = new boolean[4];

   public DesertPyramidPiece(Random p_i48658_1_, int p_i48658_2_, int p_i48658_3_) {
      super(IStructurePieceType.TEDP, p_i48658_1_, p_i48658_2_, 64, p_i48658_3_, 21, 15, 21);
   }

   public DesertPyramidPiece(TemplateManager p_i51351_1_, CompoundNBT p_i51351_2_) {
      super(IStructurePieceType.TEDP, p_i51351_2_);
      this.hasPlacedChest[0] = p_i51351_2_.getBoolean("hasPlacedChest0");
      this.hasPlacedChest[1] = p_i51351_2_.getBoolean("hasPlacedChest1");
      this.hasPlacedChest[2] = p_i51351_2_.getBoolean("hasPlacedChest2");
      this.hasPlacedChest[3] = p_i51351_2_.getBoolean("hasPlacedChest3");
   }

   protected void readAdditional(CompoundNBT p_143011_1_) {
      super.readAdditional(p_143011_1_);
      p_143011_1_.putBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
      p_143011_1_.putBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
      p_143011_1_.putBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
      p_143011_1_.putBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
   }

   public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);

      int lvt_6_2_;
      for(lvt_6_2_ = 1; lvt_6_2_ <= 9; ++lvt_6_2_) {
         this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_6_2_, lvt_6_2_, lvt_6_2_, this.width - 1 - lvt_6_2_, lvt_6_2_, this.depth - 1 - lvt_6_2_, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_6_2_ + 1, lvt_6_2_, lvt_6_2_ + 1, this.width - 2 - lvt_6_2_, lvt_6_2_, this.depth - 2 - lvt_6_2_, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      }

      for(lvt_6_2_ = 0; lvt_6_2_ < this.width; ++lvt_6_2_) {
         for(int lvt_7_1_ = 0; lvt_7_1_ < this.depth; ++lvt_7_1_) {
            int lvt_8_1_ = true;
            this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.SANDSTONE.getDefaultState(), lvt_6_2_, -5, lvt_7_1_, p_225577_4_);
         }
      }

      BlockState lvt_6_3_ = (BlockState)Blocks.SANDSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
      BlockState lvt_7_2_ = (BlockState)Blocks.SANDSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
      BlockState lvt_8_2_ = (BlockState)Blocks.SANDSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST);
      BlockState lvt_9_1_ = (BlockState)Blocks.SANDSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.setBlockState(p_225577_1_, lvt_6_3_, 2, 10, 0, p_225577_4_);
      this.setBlockState(p_225577_1_, lvt_7_2_, 2, 10, 4, p_225577_4_);
      this.setBlockState(p_225577_1_, lvt_8_2_, 0, 10, 2, p_225577_4_);
      this.setBlockState(p_225577_1_, lvt_9_1_, 4, 10, 2, p_225577_4_);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.setBlockState(p_225577_1_, lvt_6_3_, this.width - 3, 10, 0, p_225577_4_);
      this.setBlockState(p_225577_1_, lvt_7_2_, this.width - 3, 10, 4, p_225577_4_);
      this.setBlockState(p_225577_1_, lvt_8_2_, this.width - 5, 10, 2, p_225577_4_);
      this.setBlockState(p_225577_1_, lvt_9_1_, this.width - 1, 10, 2, p_225577_4_);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 9, 1, 0, 11, 3, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 9, 1, 1, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 9, 2, 1, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 9, 3, 1, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 10, 3, 1, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 11, 3, 1, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 11, 2, 1, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 11, 1, 1, p_225577_4_);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 1, 2, 8, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 12, 1, 2, 16, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 9, 4, 9, 11, 4, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, 1, 8, 8, 3, 8, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 12, 1, 8, 12, 3, 8, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, 1, 12, 8, 3, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 12, 1, 12, 12, 3, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 5, 9, 5, 7, 11, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, this.width - 6, 5, 9, this.width - 6, 7, 11, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
      this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), 5, 5, 10, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), 5, 6, 10, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), 6, 6, 10, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), this.width - 6, 5, 10, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), this.width - 6, 6, 10, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), this.width - 7, 6, 10, p_225577_4_);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 4, 4, 2, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.setBlockState(p_225577_1_, lvt_6_3_, 2, 4, 5, p_225577_4_);
      this.setBlockState(p_225577_1_, lvt_6_3_, 2, 3, 4, p_225577_4_);
      this.setBlockState(p_225577_1_, lvt_6_3_, this.width - 3, 4, 5, p_225577_4_);
      this.setBlockState(p_225577_1_, lvt_6_3_, this.width - 3, 3, 4, p_225577_4_);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.setBlockState(p_225577_1_, Blocks.SANDSTONE.getDefaultState(), 1, 1, 2, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.SANDSTONE.getDefaultState(), this.width - 2, 1, 2, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.SANDSTONE_SLAB.getDefaultState(), 1, 2, 2, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.SANDSTONE_SLAB.getDefaultState(), this.width - 2, 2, 2, p_225577_4_);
      this.setBlockState(p_225577_1_, lvt_9_1_, 2, 1, 2, p_225577_4_);
      this.setBlockState(p_225577_1_, lvt_8_2_, this.width - 3, 1, 2, p_225577_4_);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 3, 5, 4, 3, 17, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 1, 5, 4, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);

      int lvt_10_3_;
      for(lvt_10_3_ = 5; lvt_10_3_ <= 17; lvt_10_3_ += 2) {
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 4, 1, lvt_10_3_, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), 4, 2, lvt_10_3_, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), this.width - 5, 1, lvt_10_3_, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), this.width - 5, 2, lvt_10_3_, p_225577_4_);
      }

      this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 7, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 8, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 9, 0, 9, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 11, 0, 9, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 8, 0, 10, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 12, 0, 10, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 7, 0, 10, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 13, 0, 10, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 9, 0, 11, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 11, 0, 11, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 12, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 13, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.BLUE_TERRACOTTA.getDefaultState(), 10, 0, 10, p_225577_4_);

      for(lvt_10_3_ = 0; lvt_10_3_ <= this.width - 1; lvt_10_3_ += this.width - 1) {
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), lvt_10_3_, 2, 1, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_, 2, 2, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), lvt_10_3_, 2, 3, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), lvt_10_3_, 3, 1, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_, 3, 2, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), lvt_10_3_, 3, 3, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_, 4, 1, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), lvt_10_3_, 4, 2, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_, 4, 3, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), lvt_10_3_, 5, 1, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_, 5, 2, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), lvt_10_3_, 5, 3, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_, 6, 1, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), lvt_10_3_, 6, 2, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_, 6, 3, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_, 7, 1, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_, 7, 2, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_, 7, 3, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), lvt_10_3_, 8, 1, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), lvt_10_3_, 8, 2, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), lvt_10_3_, 8, 3, p_225577_4_);
      }

      for(lvt_10_3_ = 2; lvt_10_3_ <= this.width - 3; lvt_10_3_ += this.width - 3 - 2) {
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), lvt_10_3_ - 1, 2, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_, 2, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), lvt_10_3_ + 1, 2, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), lvt_10_3_ - 1, 3, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_, 3, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), lvt_10_3_ + 1, 3, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_ - 1, 4, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), lvt_10_3_, 4, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_ + 1, 4, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), lvt_10_3_ - 1, 5, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_, 5, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), lvt_10_3_ + 1, 5, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_ - 1, 6, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), lvt_10_3_, 6, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_ + 1, 6, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_ - 1, 7, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_, 7, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), lvt_10_3_ + 1, 7, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), lvt_10_3_ - 1, 8, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), lvt_10_3_, 8, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), lvt_10_3_ + 1, 8, 0, p_225577_4_);
      }

      this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, 4, 0, 12, 6, 0, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
      this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), 8, 6, 0, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), 12, 6, 0, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 9, 5, 0, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), 10, 5, 0, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 11, 5, 0, p_225577_4_);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, -14, 8, 12, -11, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, -10, 8, 12, -10, 12, Blocks.CHISELED_SANDSTONE.getDefaultState(), Blocks.CHISELED_SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, -9, 8, 12, -9, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 9, -11, 9, 11, -1, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.setBlockState(p_225577_1_, Blocks.STONE_PRESSURE_PLATE.getDefaultState(), 10, -11, 10, p_225577_4_);
      this.fillWithBlocks(p_225577_1_, p_225577_4_, 9, -13, 9, 11, -13, 11, Blocks.TNT.getDefaultState(), Blocks.AIR.getDefaultState(), false);
      this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), 8, -11, 10, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), 8, -10, 10, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), 7, -10, 10, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 7, -11, 10, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), 12, -11, 10, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), 12, -10, 10, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), 13, -10, 10, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 13, -11, 10, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), 10, -11, 8, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), 10, -10, 8, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), 10, -10, 7, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 10, -11, 7, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), 10, -11, 12, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), 10, -10, 12, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.CHISELED_SANDSTONE.getDefaultState(), 10, -10, 13, p_225577_4_);
      this.setBlockState(p_225577_1_, Blocks.CUT_SANDSTONE.getDefaultState(), 10, -11, 13, p_225577_4_);
      Iterator var17 = Direction.Plane.HORIZONTAL.iterator();

      while(var17.hasNext()) {
         Direction lvt_11_1_ = (Direction)var17.next();
         if (!this.hasPlacedChest[lvt_11_1_.getHorizontalIndex()]) {
            int lvt_12_1_ = lvt_11_1_.getXOffset() * 2;
            int lvt_13_1_ = lvt_11_1_.getZOffset() * 2;
            this.hasPlacedChest[lvt_11_1_.getHorizontalIndex()] = this.generateChest(p_225577_1_, p_225577_4_, p_225577_3_, 10 + lvt_12_1_, -11, 10 + lvt_13_1_, LootTables.CHESTS_DESERT_PYRAMID);
         }
      }

      return true;
   }
}
