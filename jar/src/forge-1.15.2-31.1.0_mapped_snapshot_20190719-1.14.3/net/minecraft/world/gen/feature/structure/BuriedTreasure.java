package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;

public class BuriedTreasure {
   public static class Piece extends StructurePiece {
      public Piece(BlockPos p_i48882_1_) {
         super(IStructurePieceType.BTP, 0);
         this.boundingBox = new MutableBoundingBox(p_i48882_1_.getX(), p_i48882_1_.getY(), p_i48882_1_.getZ(), p_i48882_1_.getX(), p_i48882_1_.getY(), p_i48882_1_.getZ());
      }

      public Piece(TemplateManager p_i50677_1_, CompoundNBT p_i50677_2_) {
         super(IStructurePieceType.BTP, p_i50677_2_);
      }

      protected void readAdditional(CompoundNBT p_143011_1_) {
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         int lvt_6_1_ = p_225577_1_.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, this.boundingBox.minX, this.boundingBox.minZ);
         BlockPos.Mutable lvt_7_1_ = new BlockPos.Mutable(this.boundingBox.minX, lvt_6_1_, this.boundingBox.minZ);

         while(lvt_7_1_.getY() > 0) {
            BlockState lvt_8_1_ = p_225577_1_.getBlockState(lvt_7_1_);
            BlockState lvt_9_1_ = p_225577_1_.getBlockState(lvt_7_1_.down());
            if (lvt_9_1_ == Blocks.SANDSTONE.getDefaultState() || lvt_9_1_ == Blocks.STONE.getDefaultState() || lvt_9_1_ == Blocks.ANDESITE.getDefaultState() || lvt_9_1_ == Blocks.GRANITE.getDefaultState() || lvt_9_1_ == Blocks.DIORITE.getDefaultState()) {
               BlockState lvt_10_1_ = !lvt_8_1_.isAir() && !this.func_204295_a(lvt_8_1_) ? lvt_8_1_ : Blocks.SAND.getDefaultState();
               Direction[] var11 = Direction.values();
               int var12 = var11.length;

               for(int var13 = 0; var13 < var12; ++var13) {
                  Direction lvt_14_1_ = var11[var13];
                  BlockPos lvt_15_1_ = lvt_7_1_.offset(lvt_14_1_);
                  BlockState lvt_16_1_ = p_225577_1_.getBlockState(lvt_15_1_);
                  if (lvt_16_1_.isAir() || this.func_204295_a(lvt_16_1_)) {
                     BlockPos lvt_17_1_ = lvt_15_1_.down();
                     BlockState lvt_18_1_ = p_225577_1_.getBlockState(lvt_17_1_);
                     if ((lvt_18_1_.isAir() || this.func_204295_a(lvt_18_1_)) && lvt_14_1_ != Direction.UP) {
                        p_225577_1_.setBlockState(lvt_15_1_, lvt_9_1_, 3);
                     } else {
                        p_225577_1_.setBlockState(lvt_15_1_, lvt_10_1_, 3);
                     }
                  }
               }

               this.boundingBox = new MutableBoundingBox(lvt_7_1_.getX(), lvt_7_1_.getY(), lvt_7_1_.getZ(), lvt_7_1_.getX(), lvt_7_1_.getY(), lvt_7_1_.getZ());
               return this.generateChest(p_225577_1_, p_225577_4_, p_225577_3_, lvt_7_1_, LootTables.CHESTS_BURIED_TREASURE, (BlockState)null);
            }

            lvt_7_1_.move(0, -1, 0);
         }

         return false;
      }

      private boolean func_204295_a(BlockState p_204295_1_) {
         return p_204295_1_ == Blocks.WATER.getDefaultState() || p_204295_1_ == Blocks.LAVA.getDefaultState();
      }
   }
}
