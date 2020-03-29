package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class SwampHutPiece extends ScatteredStructurePiece {
   private boolean witch;
   private boolean field_214822_f;

   public SwampHutPiece(Random p_i48652_1_, int p_i48652_2_, int p_i48652_3_) {
      super(IStructurePieceType.TESH, p_i48652_1_, p_i48652_2_, 64, p_i48652_3_, 7, 7, 9);
   }

   public SwampHutPiece(TemplateManager p_i51340_1_, CompoundNBT p_i51340_2_) {
      super(IStructurePieceType.TESH, p_i51340_2_);
      this.witch = p_i51340_2_.getBoolean("Witch");
      this.field_214822_f = p_i51340_2_.getBoolean("Cat");
   }

   protected void readAdditional(CompoundNBT p_143011_1_) {
      super.readAdditional(p_143011_1_);
      p_143011_1_.putBoolean("Witch", this.witch);
      p_143011_1_.putBoolean("Cat", this.field_214822_f);
   }

   public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
      if (!this.func_202580_a(p_225577_1_, p_225577_4_, 0)) {
         return false;
      } else {
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 1, 5, 1, 7, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 4, 2, 5, 4, 7, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 1, 0, 4, 1, 0, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 2, 2, 3, 3, 2, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 2, 3, 1, 3, 6, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 2, 3, 5, 3, 6, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 2, 7, 4, 3, 7, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 0, 2, 1, 3, 2, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 0, 2, 5, 3, 2, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 0, 7, 1, 3, 7, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 0, 7, 5, 3, 7, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
         this.setBlockState(p_225577_1_, Blocks.OAK_FENCE.getDefaultState(), 2, 3, 2, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.OAK_FENCE.getDefaultState(), 3, 3, 7, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), 1, 3, 4, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), 5, 3, 4, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.AIR.getDefaultState(), 5, 3, 5, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.POTTED_RED_MUSHROOM.getDefaultState(), 1, 3, 5, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CRAFTING_TABLE.getDefaultState(), 3, 2, 6, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.CAULDRON.getDefaultState(), 4, 2, 6, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.OAK_FENCE.getDefaultState(), 1, 2, 1, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.OAK_FENCE.getDefaultState(), 5, 2, 1, p_225577_4_);
         BlockState lvt_6_1_ = (BlockState)Blocks.SPRUCE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
         BlockState lvt_7_1_ = (BlockState)Blocks.SPRUCE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST);
         BlockState lvt_8_1_ = (BlockState)Blocks.SPRUCE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST);
         BlockState lvt_9_1_ = (BlockState)Blocks.SPRUCE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 4, 1, 6, 4, 1, lvt_6_1_, lvt_6_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 4, 2, 0, 4, 7, lvt_7_1_, lvt_7_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 4, 2, 6, 4, 7, lvt_8_1_, lvt_8_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 4, 8, 6, 4, 8, lvt_9_1_, lvt_9_1_, false);
         this.setBlockState(p_225577_1_, (BlockState)lvt_6_1_.with(StairsBlock.SHAPE, StairsShape.OUTER_RIGHT), 0, 4, 1, p_225577_4_);
         this.setBlockState(p_225577_1_, (BlockState)lvt_6_1_.with(StairsBlock.SHAPE, StairsShape.OUTER_LEFT), 6, 4, 1, p_225577_4_);
         this.setBlockState(p_225577_1_, (BlockState)lvt_9_1_.with(StairsBlock.SHAPE, StairsShape.OUTER_LEFT), 0, 4, 8, p_225577_4_);
         this.setBlockState(p_225577_1_, (BlockState)lvt_9_1_.with(StairsBlock.SHAPE, StairsShape.OUTER_RIGHT), 6, 4, 8, p_225577_4_);

         int lvt_10_2_;
         int lvt_11_2_;
         for(lvt_10_2_ = 2; lvt_10_2_ <= 7; lvt_10_2_ += 5) {
            for(lvt_11_2_ = 1; lvt_11_2_ <= 5; lvt_11_2_ += 4) {
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.OAK_LOG.getDefaultState(), lvt_11_2_, -1, lvt_10_2_, p_225577_4_);
            }
         }

         if (!this.witch) {
            lvt_10_2_ = this.getXWithOffset(2, 5);
            lvt_11_2_ = this.getYWithOffset(2);
            int lvt_12_1_ = this.getZWithOffset(2, 5);
            if (p_225577_4_.isVecInside(new BlockPos(lvt_10_2_, lvt_11_2_, lvt_12_1_))) {
               this.witch = true;
               WitchEntity lvt_13_1_ = (WitchEntity)EntityType.WITCH.create(p_225577_1_.getWorld());
               lvt_13_1_.enablePersistence();
               lvt_13_1_.setLocationAndAngles((double)lvt_10_2_ + 0.5D, (double)lvt_11_2_, (double)lvt_12_1_ + 0.5D, 0.0F, 0.0F);
               lvt_13_1_.onInitialSpawn(p_225577_1_, p_225577_1_.getDifficultyForLocation(new BlockPos(lvt_10_2_, lvt_11_2_, lvt_12_1_)), SpawnReason.STRUCTURE, (ILivingEntityData)null, (CompoundNBT)null);
               p_225577_1_.addEntity(lvt_13_1_);
            }
         }

         this.func_214821_a(p_225577_1_, p_225577_4_);
         return true;
      }
   }

   private void func_214821_a(IWorld p_214821_1_, MutableBoundingBox p_214821_2_) {
      if (!this.field_214822_f) {
         int lvt_3_1_ = this.getXWithOffset(2, 5);
         int lvt_4_1_ = this.getYWithOffset(2);
         int lvt_5_1_ = this.getZWithOffset(2, 5);
         if (p_214821_2_.isVecInside(new BlockPos(lvt_3_1_, lvt_4_1_, lvt_5_1_))) {
            this.field_214822_f = true;
            CatEntity lvt_6_1_ = (CatEntity)EntityType.CAT.create(p_214821_1_.getWorld());
            lvt_6_1_.enablePersistence();
            lvt_6_1_.setLocationAndAngles((double)lvt_3_1_ + 0.5D, (double)lvt_4_1_, (double)lvt_5_1_ + 0.5D, 0.0F, 0.0F);
            lvt_6_1_.onInitialSpawn(p_214821_1_, p_214821_1_.getDifficultyForLocation(new BlockPos(lvt_3_1_, lvt_4_1_, lvt_5_1_)), SpawnReason.STRUCTURE, (ILivingEntityData)null, (CompoundNBT)null);
            p_214821_1_.addEntity(lvt_6_1_);
         }
      }

   }
}
