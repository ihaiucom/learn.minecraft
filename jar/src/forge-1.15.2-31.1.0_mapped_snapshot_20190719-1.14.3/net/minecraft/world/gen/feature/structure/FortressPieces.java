package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;

public class FortressPieces {
   private static final FortressPieces.PieceWeight[] PRIMARY_COMPONENTS = new FortressPieces.PieceWeight[]{new FortressPieces.PieceWeight(FortressPieces.Straight.class, 30, 0, true), new FortressPieces.PieceWeight(FortressPieces.Crossing3.class, 10, 4), new FortressPieces.PieceWeight(FortressPieces.Crossing.class, 10, 4), new FortressPieces.PieceWeight(FortressPieces.Stairs.class, 10, 3), new FortressPieces.PieceWeight(FortressPieces.Throne.class, 5, 2), new FortressPieces.PieceWeight(FortressPieces.Entrance.class, 5, 1)};
   private static final FortressPieces.PieceWeight[] SECONDARY_COMPONENTS = new FortressPieces.PieceWeight[]{new FortressPieces.PieceWeight(FortressPieces.Corridor5.class, 25, 0, true), new FortressPieces.PieceWeight(FortressPieces.Crossing2.class, 15, 5), new FortressPieces.PieceWeight(FortressPieces.Corridor2.class, 5, 10), new FortressPieces.PieceWeight(FortressPieces.Corridor.class, 5, 10), new FortressPieces.PieceWeight(FortressPieces.Corridor3.class, 10, 3, true), new FortressPieces.PieceWeight(FortressPieces.Corridor4.class, 7, 2), new FortressPieces.PieceWeight(FortressPieces.NetherStalkRoom.class, 5, 2)};

   private static FortressPieces.Piece findAndCreateBridgePieceFactory(FortressPieces.PieceWeight p_175887_0_, List<StructurePiece> p_175887_1_, Random p_175887_2_, int p_175887_3_, int p_175887_4_, int p_175887_5_, Direction p_175887_6_, int p_175887_7_) {
      Class<? extends FortressPieces.Piece> lvt_8_1_ = p_175887_0_.weightClass;
      FortressPieces.Piece lvt_9_1_ = null;
      if (lvt_8_1_ == FortressPieces.Straight.class) {
         lvt_9_1_ = FortressPieces.Straight.createPiece(p_175887_1_, p_175887_2_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (lvt_8_1_ == FortressPieces.Crossing3.class) {
         lvt_9_1_ = FortressPieces.Crossing3.createPiece(p_175887_1_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (lvt_8_1_ == FortressPieces.Crossing.class) {
         lvt_9_1_ = FortressPieces.Crossing.createPiece(p_175887_1_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (lvt_8_1_ == FortressPieces.Stairs.class) {
         lvt_9_1_ = FortressPieces.Stairs.createPiece(p_175887_1_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_7_, p_175887_6_);
      } else if (lvt_8_1_ == FortressPieces.Throne.class) {
         lvt_9_1_ = FortressPieces.Throne.createPiece(p_175887_1_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_7_, p_175887_6_);
      } else if (lvt_8_1_ == FortressPieces.Entrance.class) {
         lvt_9_1_ = FortressPieces.Entrance.createPiece(p_175887_1_, p_175887_2_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (lvt_8_1_ == FortressPieces.Corridor5.class) {
         lvt_9_1_ = FortressPieces.Corridor5.createPiece(p_175887_1_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (lvt_8_1_ == FortressPieces.Corridor2.class) {
         lvt_9_1_ = FortressPieces.Corridor2.createPiece(p_175887_1_, p_175887_2_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (lvt_8_1_ == FortressPieces.Corridor.class) {
         lvt_9_1_ = FortressPieces.Corridor.createPiece(p_175887_1_, p_175887_2_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (lvt_8_1_ == FortressPieces.Corridor3.class) {
         lvt_9_1_ = FortressPieces.Corridor3.createPiece(p_175887_1_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (lvt_8_1_ == FortressPieces.Corridor4.class) {
         lvt_9_1_ = FortressPieces.Corridor4.func_214814_a(p_175887_1_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (lvt_8_1_ == FortressPieces.Crossing2.class) {
         lvt_9_1_ = FortressPieces.Crossing2.createPiece(p_175887_1_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      } else if (lvt_8_1_ == FortressPieces.NetherStalkRoom.class) {
         lvt_9_1_ = FortressPieces.NetherStalkRoom.createPiece(p_175887_1_, p_175887_3_, p_175887_4_, p_175887_5_, p_175887_6_, p_175887_7_);
      }

      return (FortressPieces.Piece)lvt_9_1_;
   }

   public static class Corridor4 extends FortressPieces.Piece {
      public Corridor4(int p_i50277_1_, MutableBoundingBox p_i50277_2_, Direction p_i50277_3_) {
         super(IStructurePieceType.NECTB, p_i50277_1_);
         this.setCoordBaseMode(p_i50277_3_);
         this.boundingBox = p_i50277_2_;
      }

      public Corridor4(TemplateManager p_i50278_1_, CompoundNBT p_i50278_2_) {
         super(IStructurePieceType.NECTB, p_i50278_2_);
      }

      public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         int lvt_4_1_ = 1;
         Direction lvt_5_1_ = this.getCoordBaseMode();
         if (lvt_5_1_ == Direction.WEST || lvt_5_1_ == Direction.NORTH) {
            lvt_4_1_ = 5;
         }

         this.getNextComponentX((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, lvt_4_1_, p_74861_3_.nextInt(8) > 0);
         this.getNextComponentZ((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, lvt_4_1_, p_74861_3_.nextInt(8) > 0);
      }

      public static FortressPieces.Corridor4 func_214814_a(List<StructurePiece> p_214814_0_, int p_214814_1_, int p_214814_2_, int p_214814_3_, Direction p_214814_4_, int p_214814_5_) {
         MutableBoundingBox lvt_6_1_ = MutableBoundingBox.getComponentToAddBoundingBox(p_214814_1_, p_214814_2_, p_214814_3_, -3, 0, 0, 9, 7, 9, p_214814_4_);
         return isAboveGround(lvt_6_1_) && StructurePiece.findIntersecting(p_214814_0_, lvt_6_1_) == null ? new FortressPieces.Corridor4(p_214814_5_, lvt_6_1_, p_214814_4_) : null;
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         BlockState lvt_6_1_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
         BlockState lvt_7_1_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 0, 0, 8, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 8, 5, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 6, 0, 8, 6, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 2, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 2, 0, 8, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 3, 0, 1, 4, 0, lvt_7_1_, lvt_7_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 3, 0, 7, 4, 0, lvt_7_1_, lvt_7_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 4, 8, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 4, 2, 2, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 1, 4, 7, 2, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 3, 8, 7, 3, 8, lvt_7_1_, lvt_7_1_, false);
         this.setBlockState(p_225577_1_, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true)).with(FenceBlock.SOUTH, true), 0, 3, 8, p_225577_4_);
         this.setBlockState(p_225577_1_, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.SOUTH, true), 8, 3, 8, p_225577_4_);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 6, 0, 3, 7, lvt_6_1_, lvt_6_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, 3, 6, 8, 3, 7, lvt_6_1_, lvt_6_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 4, 0, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, 3, 4, 8, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 3, 5, 2, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 3, 5, 7, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 4, 5, 1, 5, 5, lvt_7_1_, lvt_7_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 4, 5, 7, 5, 5, lvt_7_1_, lvt_7_1_, false);

         for(int lvt_8_1_ = 0; lvt_8_1_ <= 5; ++lvt_8_1_) {
            for(int lvt_9_1_ = 0; lvt_9_1_ <= 8; ++lvt_9_1_) {
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_9_1_, -1, lvt_8_1_, p_225577_4_);
            }
         }

         return true;
      }
   }

   public static class Corridor3 extends FortressPieces.Piece {
      public Corridor3(int p_i50280_1_, MutableBoundingBox p_i50280_2_, Direction p_i50280_3_) {
         super(IStructurePieceType.NECCS, p_i50280_1_);
         this.setCoordBaseMode(p_i50280_3_);
         this.boundingBox = p_i50280_2_;
      }

      public Corridor3(TemplateManager p_i50281_1_, CompoundNBT p_i50281_2_) {
         super(IStructurePieceType.NECCS, p_i50281_2_);
      }

      public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.getNextComponentNormal((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 1, 0, true);
      }

      public static FortressPieces.Corridor3 createPiece(List<StructurePiece> p_175883_0_, int p_175883_1_, int p_175883_2_, int p_175883_3_, Direction p_175883_4_, int p_175883_5_) {
         MutableBoundingBox lvt_6_1_ = MutableBoundingBox.getComponentToAddBoundingBox(p_175883_1_, p_175883_2_, p_175883_3_, -1, -7, 0, 5, 14, 10, p_175883_4_);
         return isAboveGround(lvt_6_1_) && StructurePiece.findIntersecting(p_175883_0_, lvt_6_1_) == null ? new FortressPieces.Corridor3(p_175883_5_, lvt_6_1_, p_175883_4_) : null;
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         BlockState lvt_6_1_ = (BlockState)Blocks.NETHER_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
         BlockState lvt_7_1_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);

         for(int lvt_8_1_ = 0; lvt_8_1_ <= 9; ++lvt_8_1_) {
            int lvt_9_1_ = Math.max(1, 7 - lvt_8_1_);
            int lvt_10_1_ = Math.min(Math.max(lvt_9_1_ + 5, 14 - lvt_8_1_), 13);
            int lvt_11_1_ = lvt_8_1_;
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 0, lvt_8_1_, 4, lvt_9_1_, lvt_8_1_, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, lvt_9_1_ + 1, lvt_8_1_, 3, lvt_10_1_ - 1, lvt_8_1_, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            if (lvt_8_1_ <= 6) {
               this.setBlockState(p_225577_1_, lvt_6_1_, 1, lvt_9_1_ + 1, lvt_8_1_, p_225577_4_);
               this.setBlockState(p_225577_1_, lvt_6_1_, 2, lvt_9_1_ + 1, lvt_8_1_, p_225577_4_);
               this.setBlockState(p_225577_1_, lvt_6_1_, 3, lvt_9_1_ + 1, lvt_8_1_, p_225577_4_);
            }

            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, lvt_10_1_, lvt_8_1_, 4, lvt_10_1_, lvt_8_1_, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, lvt_9_1_ + 1, lvt_8_1_, 0, lvt_10_1_ - 1, lvt_8_1_, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, lvt_9_1_ + 1, lvt_8_1_, 4, lvt_10_1_ - 1, lvt_8_1_, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            if ((lvt_8_1_ & 1) == 0) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, lvt_9_1_ + 2, lvt_8_1_, 0, lvt_9_1_ + 3, lvt_8_1_, lvt_7_1_, lvt_7_1_, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, lvt_9_1_ + 2, lvt_8_1_, 4, lvt_9_1_ + 3, lvt_8_1_, lvt_7_1_, lvt_7_1_, false);
            }

            for(int lvt_12_1_ = 0; lvt_12_1_ <= 4; ++lvt_12_1_) {
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_12_1_, -1, lvt_11_1_, p_225577_4_);
            }
         }

         return true;
      }
   }

   public static class Corridor extends FortressPieces.Piece {
      private boolean chest;

      public Corridor(int p_i45615_1_, Random p_i45615_2_, MutableBoundingBox p_i45615_3_, Direction p_i45615_4_) {
         super(IStructurePieceType.NESCLT, p_i45615_1_);
         this.setCoordBaseMode(p_i45615_4_);
         this.boundingBox = p_i45615_3_;
         this.chest = p_i45615_2_.nextInt(3) == 0;
      }

      public Corridor(TemplateManager p_i50272_1_, CompoundNBT p_i50272_2_) {
         super(IStructurePieceType.NESCLT, p_i50272_2_);
         this.chest = p_i50272_2_.getBoolean("Chest");
      }

      protected void readAdditional(CompoundNBT p_143011_1_) {
         super.readAdditional(p_143011_1_);
         p_143011_1_.putBoolean("Chest", this.chest);
      }

      public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.getNextComponentX((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, 1, true);
      }

      public static FortressPieces.Corridor createPiece(List<StructurePiece> p_175879_0_, Random p_175879_1_, int p_175879_2_, int p_175879_3_, int p_175879_4_, Direction p_175879_5_, int p_175879_6_) {
         MutableBoundingBox lvt_7_1_ = MutableBoundingBox.getComponentToAddBoundingBox(p_175879_2_, p_175879_3_, p_175879_4_, -1, 0, 0, 5, 7, 5, p_175879_5_);
         return isAboveGround(lvt_7_1_) && StructurePiece.findIntersecting(p_175879_0_, lvt_7_1_) == null ? new FortressPieces.Corridor(p_175879_6_, p_175879_1_, lvt_7_1_, p_175879_5_) : null;
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         BlockState lvt_6_1_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
         BlockState lvt_7_1_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 3, 1, 4, 4, 1, lvt_7_1_, lvt_7_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 3, 3, 4, 4, 3, lvt_7_1_, lvt_7_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 4, 3, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 3, 4, 1, 4, 4, lvt_6_1_, lvt_6_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 3, 4, 3, 4, 4, lvt_6_1_, lvt_6_1_, false);
         if (this.chest && p_225577_4_.isVecInside(new BlockPos(this.getXWithOffset(3, 3), this.getYWithOffset(2), this.getZWithOffset(3, 3)))) {
            this.chest = false;
            this.generateChest(p_225577_1_, p_225577_4_, p_225577_3_, 3, 2, 3, LootTables.CHESTS_NETHER_BRIDGE);
         }

         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

         for(int lvt_8_1_ = 0; lvt_8_1_ <= 4; ++lvt_8_1_) {
            for(int lvt_9_1_ = 0; lvt_9_1_ <= 4; ++lvt_9_1_) {
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_8_1_, -1, lvt_9_1_, p_225577_4_);
            }
         }

         return true;
      }
   }

   public static class Corridor2 extends FortressPieces.Piece {
      private boolean chest;

      public Corridor2(int p_i45613_1_, Random p_i45613_2_, MutableBoundingBox p_i45613_3_, Direction p_i45613_4_) {
         super(IStructurePieceType.NESCRT, p_i45613_1_);
         this.setCoordBaseMode(p_i45613_4_);
         this.boundingBox = p_i45613_3_;
         this.chest = p_i45613_2_.nextInt(3) == 0;
      }

      public Corridor2(TemplateManager p_i50266_1_, CompoundNBT p_i50266_2_) {
         super(IStructurePieceType.NESCRT, p_i50266_2_);
         this.chest = p_i50266_2_.getBoolean("Chest");
      }

      protected void readAdditional(CompoundNBT p_143011_1_) {
         super.readAdditional(p_143011_1_);
         p_143011_1_.putBoolean("Chest", this.chest);
      }

      public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.getNextComponentZ((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, 1, true);
      }

      public static FortressPieces.Corridor2 createPiece(List<StructurePiece> p_175876_0_, Random p_175876_1_, int p_175876_2_, int p_175876_3_, int p_175876_4_, Direction p_175876_5_, int p_175876_6_) {
         MutableBoundingBox lvt_7_1_ = MutableBoundingBox.getComponentToAddBoundingBox(p_175876_2_, p_175876_3_, p_175876_4_, -1, 0, 0, 5, 7, 5, p_175876_5_);
         return isAboveGround(lvt_7_1_) && StructurePiece.findIntersecting(p_175876_0_, lvt_7_1_) == null ? new FortressPieces.Corridor2(p_175876_6_, p_175876_1_, lvt_7_1_, p_175876_5_) : null;
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         BlockState lvt_6_1_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
         BlockState lvt_7_1_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 1, 0, 4, 1, lvt_7_1_, lvt_7_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 3, 0, 4, 3, lvt_7_1_, lvt_7_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 3, 4, 1, 4, 4, lvt_6_1_, lvt_6_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 3, 4, 3, 4, 4, lvt_6_1_, lvt_6_1_, false);
         if (this.chest && p_225577_4_.isVecInside(new BlockPos(this.getXWithOffset(1, 3), this.getYWithOffset(2), this.getZWithOffset(1, 3)))) {
            this.chest = false;
            this.generateChest(p_225577_1_, p_225577_4_, p_225577_3_, 1, 2, 3, LootTables.CHESTS_NETHER_BRIDGE);
         }

         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

         for(int lvt_8_1_ = 0; lvt_8_1_ <= 4; ++lvt_8_1_) {
            for(int lvt_9_1_ = 0; lvt_9_1_ <= 4; ++lvt_9_1_) {
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_8_1_, -1, lvt_9_1_, p_225577_4_);
            }
         }

         return true;
      }
   }

   public static class Crossing2 extends FortressPieces.Piece {
      public Crossing2(int p_i50273_1_, MutableBoundingBox p_i50273_2_, Direction p_i50273_3_) {
         super(IStructurePieceType.NESCSC, p_i50273_1_);
         this.setCoordBaseMode(p_i50273_3_);
         this.boundingBox = p_i50273_2_;
      }

      public Crossing2(TemplateManager p_i50274_1_, CompoundNBT p_i50274_2_) {
         super(IStructurePieceType.NESCSC, p_i50274_2_);
      }

      public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.getNextComponentNormal((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 1, 0, true);
         this.getNextComponentX((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, 1, true);
         this.getNextComponentZ((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, 1, true);
      }

      public static FortressPieces.Crossing2 createPiece(List<StructurePiece> p_175878_0_, int p_175878_1_, int p_175878_2_, int p_175878_3_, Direction p_175878_4_, int p_175878_5_) {
         MutableBoundingBox lvt_6_1_ = MutableBoundingBox.getComponentToAddBoundingBox(p_175878_1_, p_175878_2_, p_175878_3_, -1, 0, 0, 5, 7, 5, p_175878_4_);
         return isAboveGround(lvt_6_1_) && StructurePiece.findIntersecting(p_175878_0_, lvt_6_1_) == null ? new FortressPieces.Crossing2(p_175878_5_, lvt_6_1_, p_175878_4_) : null;
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 4, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

         for(int lvt_6_1_ = 0; lvt_6_1_ <= 4; ++lvt_6_1_) {
            for(int lvt_7_1_ = 0; lvt_7_1_ <= 4; ++lvt_7_1_) {
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_6_1_, -1, lvt_7_1_, p_225577_4_);
            }
         }

         return true;
      }
   }

   public static class Corridor5 extends FortressPieces.Piece {
      public Corridor5(int p_i50268_1_, MutableBoundingBox p_i50268_2_, Direction p_i50268_3_) {
         super(IStructurePieceType.NESC, p_i50268_1_);
         this.setCoordBaseMode(p_i50268_3_);
         this.boundingBox = p_i50268_2_;
      }

      public Corridor5(TemplateManager p_i50269_1_, CompoundNBT p_i50269_2_) {
         super(IStructurePieceType.NESC, p_i50269_2_);
      }

      public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.getNextComponentNormal((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 1, 0, true);
      }

      public static FortressPieces.Corridor5 createPiece(List<StructurePiece> p_175877_0_, int p_175877_1_, int p_175877_2_, int p_175877_3_, Direction p_175877_4_, int p_175877_5_) {
         MutableBoundingBox lvt_6_1_ = MutableBoundingBox.getComponentToAddBoundingBox(p_175877_1_, p_175877_2_, p_175877_3_, -1, 0, 0, 5, 7, 5, p_175877_4_);
         return isAboveGround(lvt_6_1_) && StructurePiece.findIntersecting(p_175877_0_, lvt_6_1_) == null ? new FortressPieces.Corridor5(p_175877_5_, lvt_6_1_, p_175877_4_) : null;
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         BlockState lvt_6_1_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 1, 0, 4, 1, lvt_6_1_, lvt_6_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 3, 0, 4, 3, lvt_6_1_, lvt_6_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 3, 1, 4, 4, 1, lvt_6_1_, lvt_6_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 3, 3, 4, 4, 3, lvt_6_1_, lvt_6_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

         for(int lvt_7_1_ = 0; lvt_7_1_ <= 4; ++lvt_7_1_) {
            for(int lvt_8_1_ = 0; lvt_8_1_ <= 4; ++lvt_8_1_) {
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_7_1_, -1, lvt_8_1_, p_225577_4_);
            }
         }

         return true;
      }
   }

   public static class NetherStalkRoom extends FortressPieces.Piece {
      public NetherStalkRoom(int p_i50264_1_, MutableBoundingBox p_i50264_2_, Direction p_i50264_3_) {
         super(IStructurePieceType.NECSR, p_i50264_1_);
         this.setCoordBaseMode(p_i50264_3_);
         this.boundingBox = p_i50264_2_;
      }

      public NetherStalkRoom(TemplateManager p_i50265_1_, CompoundNBT p_i50265_2_) {
         super(IStructurePieceType.NECSR, p_i50265_2_);
      }

      public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.getNextComponentNormal((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 5, 3, true);
         this.getNextComponentNormal((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 5, 11, true);
      }

      public static FortressPieces.NetherStalkRoom createPiece(List<StructurePiece> p_175875_0_, int p_175875_1_, int p_175875_2_, int p_175875_3_, Direction p_175875_4_, int p_175875_5_) {
         MutableBoundingBox lvt_6_1_ = MutableBoundingBox.getComponentToAddBoundingBox(p_175875_1_, p_175875_2_, p_175875_3_, -5, -3, 0, 13, 14, 13, p_175875_4_);
         return isAboveGround(lvt_6_1_) && StructurePiece.findIntersecting(p_175875_0_, lvt_6_1_) == null ? new FortressPieces.NetherStalkRoom(p_175875_5_, lvt_6_1_, p_175875_4_) : null;
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 5, 0, 12, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         BlockState lvt_6_1_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
         BlockState lvt_7_1_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
         BlockState lvt_8_1_ = (BlockState)lvt_7_1_.with(FenceBlock.WEST, true);
         BlockState lvt_9_1_ = (BlockState)lvt_7_1_.with(FenceBlock.EAST, true);

         int lvt_10_2_;
         for(lvt_10_2_ = 1; lvt_10_2_ <= 11; lvt_10_2_ += 2) {
            this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_10_2_, 10, 0, lvt_10_2_, 11, 0, lvt_6_1_, lvt_6_1_, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_10_2_, 10, 12, lvt_10_2_, 11, 12, lvt_6_1_, lvt_6_1_, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 10, lvt_10_2_, 0, 11, lvt_10_2_, lvt_7_1_, lvt_7_1_, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 12, 10, lvt_10_2_, 12, 11, lvt_10_2_, lvt_7_1_, lvt_7_1_, false);
            this.setBlockState(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_10_2_, 13, 0, p_225577_4_);
            this.setBlockState(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_10_2_, 13, 12, p_225577_4_);
            this.setBlockState(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), 0, 13, lvt_10_2_, p_225577_4_);
            this.setBlockState(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), 12, 13, lvt_10_2_, p_225577_4_);
            if (lvt_10_2_ != 11) {
               this.setBlockState(p_225577_1_, lvt_6_1_, lvt_10_2_ + 1, 13, 0, p_225577_4_);
               this.setBlockState(p_225577_1_, lvt_6_1_, lvt_10_2_ + 1, 13, 12, p_225577_4_);
               this.setBlockState(p_225577_1_, lvt_7_1_, 0, 13, lvt_10_2_ + 1, p_225577_4_);
               this.setBlockState(p_225577_1_, lvt_7_1_, 12, 13, lvt_10_2_ + 1, p_225577_4_);
            }
         }

         this.setBlockState(p_225577_1_, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.EAST, true), 0, 13, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, true)).with(FenceBlock.EAST, true), 0, 13, 12, p_225577_4_);
         this.setBlockState(p_225577_1_, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, true)).with(FenceBlock.WEST, true), 12, 13, 12, p_225577_4_);
         this.setBlockState(p_225577_1_, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.WEST, true), 12, 13, 0, p_225577_4_);

         for(lvt_10_2_ = 3; lvt_10_2_ <= 9; lvt_10_2_ += 2) {
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 7, lvt_10_2_, 1, 8, lvt_10_2_, lvt_8_1_, lvt_8_1_, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 11, 7, lvt_10_2_, 11, 8, lvt_10_2_, lvt_9_1_, lvt_9_1_, false);
         }

         BlockState lvt_10_3_ = (BlockState)Blocks.NETHER_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);

         int lvt_11_1_;
         int lvt_13_3_;
         for(lvt_11_1_ = 0; lvt_11_1_ <= 6; ++lvt_11_1_) {
            int lvt_12_1_ = lvt_11_1_ + 4;

            for(lvt_13_3_ = 5; lvt_13_3_ <= 7; ++lvt_13_3_) {
               this.setBlockState(p_225577_1_, lvt_10_3_, lvt_13_3_, 5 + lvt_11_1_, lvt_12_1_, p_225577_4_);
            }

            if (lvt_12_1_ >= 5 && lvt_12_1_ <= 8) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 5, lvt_12_1_, 7, lvt_11_1_ + 4, lvt_12_1_, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            } else if (lvt_12_1_ >= 9 && lvt_12_1_ <= 10) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 8, lvt_12_1_, 7, lvt_11_1_ + 4, lvt_12_1_, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            }

            if (lvt_11_1_ >= 1) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 6 + lvt_11_1_, lvt_12_1_, 7, 9 + lvt_11_1_, lvt_12_1_, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            }
         }

         for(lvt_11_1_ = 5; lvt_11_1_ <= 7; ++lvt_11_1_) {
            this.setBlockState(p_225577_1_, lvt_10_3_, lvt_11_1_, 12, 11, p_225577_4_);
         }

         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 6, 7, 5, 7, 7, lvt_9_1_, lvt_9_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 6, 7, 7, 7, 7, lvt_8_1_, lvt_8_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 13, 12, 7, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 5, 2, 3, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 5, 9, 3, 5, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 5, 4, 2, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 9, 5, 2, 10, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 9, 5, 9, 10, 5, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 10, 5, 4, 10, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         BlockState lvt_11_3_ = (BlockState)lvt_10_3_.with(StairsBlock.FACING, Direction.EAST);
         BlockState lvt_12_2_ = (BlockState)lvt_10_3_.with(StairsBlock.FACING, Direction.WEST);
         this.setBlockState(p_225577_1_, lvt_12_2_, 4, 5, 2, p_225577_4_);
         this.setBlockState(p_225577_1_, lvt_12_2_, 4, 5, 3, p_225577_4_);
         this.setBlockState(p_225577_1_, lvt_12_2_, 4, 5, 9, p_225577_4_);
         this.setBlockState(p_225577_1_, lvt_12_2_, 4, 5, 10, p_225577_4_);
         this.setBlockState(p_225577_1_, lvt_11_3_, 8, 5, 2, p_225577_4_);
         this.setBlockState(p_225577_1_, lvt_11_3_, 8, 5, 3, p_225577_4_);
         this.setBlockState(p_225577_1_, lvt_11_3_, 8, 5, 9, p_225577_4_);
         this.setBlockState(p_225577_1_, lvt_11_3_, 8, 5, 10, p_225577_4_);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 4, 4, 4, 4, 8, Blocks.SOUL_SAND.getDefaultState(), Blocks.SOUL_SAND.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, 4, 4, 9, 4, 8, Blocks.SOUL_SAND.getDefaultState(), Blocks.SOUL_SAND.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 5, 4, 4, 5, 8, Blocks.NETHER_WART.getDefaultState(), Blocks.NETHER_WART.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, 5, 4, 9, 5, 8, Blocks.NETHER_WART.getDefaultState(), Blocks.NETHER_WART.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

         int lvt_14_2_;
         for(lvt_13_3_ = 4; lvt_13_3_ <= 8; ++lvt_13_3_) {
            for(lvt_14_2_ = 0; lvt_14_2_ <= 2; ++lvt_14_2_) {
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_13_3_, -1, lvt_14_2_, p_225577_4_);
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_13_3_, -1, 12 - lvt_14_2_, p_225577_4_);
            }
         }

         for(lvt_13_3_ = 0; lvt_13_3_ <= 2; ++lvt_13_3_) {
            for(lvt_14_2_ = 4; lvt_14_2_ <= 8; ++lvt_14_2_) {
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_13_3_, -1, lvt_14_2_, p_225577_4_);
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), 12 - lvt_13_3_, -1, lvt_14_2_, p_225577_4_);
            }
         }

         return true;
      }
   }

   public static class Entrance extends FortressPieces.Piece {
      public Entrance(int p_i45617_1_, Random p_i45617_2_, MutableBoundingBox p_i45617_3_, Direction p_i45617_4_) {
         super(IStructurePieceType.NECE, p_i45617_1_);
         this.setCoordBaseMode(p_i45617_4_);
         this.boundingBox = p_i45617_3_;
      }

      public Entrance(TemplateManager p_i50276_1_, CompoundNBT p_i50276_2_) {
         super(IStructurePieceType.NECE, p_i50276_2_);
      }

      public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.getNextComponentNormal((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 5, 3, true);
      }

      public static FortressPieces.Entrance createPiece(List<StructurePiece> p_175881_0_, Random p_175881_1_, int p_175881_2_, int p_175881_3_, int p_175881_4_, Direction p_175881_5_, int p_175881_6_) {
         MutableBoundingBox lvt_7_1_ = MutableBoundingBox.getComponentToAddBoundingBox(p_175881_2_, p_175881_3_, p_175881_4_, -5, -3, 0, 13, 14, 13, p_175881_5_);
         return isAboveGround(lvt_7_1_) && StructurePiece.findIntersecting(p_175881_0_, lvt_7_1_) == null ? new FortressPieces.Entrance(p_175881_6_, p_175881_1_, lvt_7_1_, p_175881_5_) : null;
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 5, 0, 12, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 8, 0, 7, 8, 0, Blocks.NETHER_BRICK_FENCE.getDefaultState(), Blocks.NETHER_BRICK_FENCE.getDefaultState(), false);
         BlockState lvt_6_1_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
         BlockState lvt_7_1_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);

         int lvt_8_4_;
         for(lvt_8_4_ = 1; lvt_8_4_ <= 11; lvt_8_4_ += 2) {
            this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_8_4_, 10, 0, lvt_8_4_, 11, 0, lvt_6_1_, lvt_6_1_, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_8_4_, 10, 12, lvt_8_4_, 11, 12, lvt_6_1_, lvt_6_1_, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 10, lvt_8_4_, 0, 11, lvt_8_4_, lvt_7_1_, lvt_7_1_, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 12, 10, lvt_8_4_, 12, 11, lvt_8_4_, lvt_7_1_, lvt_7_1_, false);
            this.setBlockState(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_8_4_, 13, 0, p_225577_4_);
            this.setBlockState(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_8_4_, 13, 12, p_225577_4_);
            this.setBlockState(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), 0, 13, lvt_8_4_, p_225577_4_);
            this.setBlockState(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), 12, 13, lvt_8_4_, p_225577_4_);
            if (lvt_8_4_ != 11) {
               this.setBlockState(p_225577_1_, lvt_6_1_, lvt_8_4_ + 1, 13, 0, p_225577_4_);
               this.setBlockState(p_225577_1_, lvt_6_1_, lvt_8_4_ + 1, 13, 12, p_225577_4_);
               this.setBlockState(p_225577_1_, lvt_7_1_, 0, 13, lvt_8_4_ + 1, p_225577_4_);
               this.setBlockState(p_225577_1_, lvt_7_1_, 12, 13, lvt_8_4_ + 1, p_225577_4_);
            }
         }

         this.setBlockState(p_225577_1_, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.EAST, true), 0, 13, 0, p_225577_4_);
         this.setBlockState(p_225577_1_, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, true)).with(FenceBlock.EAST, true), 0, 13, 12, p_225577_4_);
         this.setBlockState(p_225577_1_, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, true)).with(FenceBlock.WEST, true), 12, 13, 12, p_225577_4_);
         this.setBlockState(p_225577_1_, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.WEST, true), 12, 13, 0, p_225577_4_);

         for(lvt_8_4_ = 3; lvt_8_4_ <= 9; lvt_8_4_ += 2) {
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 7, lvt_8_4_, 1, 8, lvt_8_4_, (BlockState)lvt_7_1_.with(FenceBlock.WEST, true), (BlockState)lvt_7_1_.with(FenceBlock.WEST, true), false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 11, 7, lvt_8_4_, 11, 8, lvt_8_4_, (BlockState)lvt_7_1_.with(FenceBlock.EAST, true), (BlockState)lvt_7_1_.with(FenceBlock.EAST, true), false);
         }

         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

         int lvt_9_2_;
         for(lvt_8_4_ = 4; lvt_8_4_ <= 8; ++lvt_8_4_) {
            for(lvt_9_2_ = 0; lvt_9_2_ <= 2; ++lvt_9_2_) {
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_8_4_, -1, lvt_9_2_, p_225577_4_);
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_8_4_, -1, 12 - lvt_9_2_, p_225577_4_);
            }
         }

         for(lvt_8_4_ = 0; lvt_8_4_ <= 2; ++lvt_8_4_) {
            for(lvt_9_2_ = 4; lvt_9_2_ <= 8; ++lvt_9_2_) {
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_8_4_, -1, lvt_9_2_, p_225577_4_);
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), 12 - lvt_8_4_, -1, lvt_9_2_, p_225577_4_);
            }
         }

         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 5, 5, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 1, 6, 6, 4, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.setBlockState(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), 6, 0, 6, p_225577_4_);
         this.setBlockState(p_225577_1_, Blocks.LAVA.getDefaultState(), 6, 5, 6, p_225577_4_);
         BlockPos lvt_8_5_ = new BlockPos(this.getXWithOffset(6, 6), this.getYWithOffset(5), this.getZWithOffset(6, 6));
         if (p_225577_4_.isVecInside(lvt_8_5_)) {
            p_225577_1_.getPendingFluidTicks().scheduleTick(lvt_8_5_, Fluids.LAVA, 0);
         }

         return true;
      }
   }

   public static class Throne extends FortressPieces.Piece {
      private boolean hasSpawner;

      public Throne(int p_i50262_1_, MutableBoundingBox p_i50262_2_, Direction p_i50262_3_) {
         super(IStructurePieceType.NEMT, p_i50262_1_);
         this.setCoordBaseMode(p_i50262_3_);
         this.boundingBox = p_i50262_2_;
      }

      public Throne(TemplateManager p_i50263_1_, CompoundNBT p_i50263_2_) {
         super(IStructurePieceType.NEMT, p_i50263_2_);
         this.hasSpawner = p_i50263_2_.getBoolean("Mob");
      }

      protected void readAdditional(CompoundNBT p_143011_1_) {
         super.readAdditional(p_143011_1_);
         p_143011_1_.putBoolean("Mob", this.hasSpawner);
      }

      public static FortressPieces.Throne createPiece(List<StructurePiece> p_175874_0_, int p_175874_1_, int p_175874_2_, int p_175874_3_, int p_175874_4_, Direction p_175874_5_) {
         MutableBoundingBox lvt_6_1_ = MutableBoundingBox.getComponentToAddBoundingBox(p_175874_1_, p_175874_2_, p_175874_3_, -2, 0, 0, 7, 8, 9, p_175874_5_);
         return isAboveGround(lvt_6_1_) && StructurePiece.findIntersecting(p_175874_0_, lvt_6_1_) == null ? new FortressPieces.Throne(p_175874_4_, lvt_6_1_, p_175874_5_) : null;
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 6, 7, 7, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 0, 0, 5, 1, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 2, 1, 5, 2, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 3, 2, 5, 3, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 4, 3, 5, 4, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 2, 0, 1, 4, 2, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 2, 0, 5, 4, 2, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 5, 2, 1, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 5, 2, 5, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 5, 3, 0, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 5, 3, 6, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 5, 8, 5, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         BlockState lvt_6_1_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
         BlockState lvt_7_1_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
         this.setBlockState(p_225577_1_, (BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true), 1, 6, 3, p_225577_4_);
         this.setBlockState(p_225577_1_, (BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true), 5, 6, 3, p_225577_4_);
         this.setBlockState(p_225577_1_, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true)).with(FenceBlock.NORTH, true), 0, 6, 3, p_225577_4_);
         this.setBlockState(p_225577_1_, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.NORTH, true), 6, 6, 3, p_225577_4_);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 6, 4, 0, 6, 7, lvt_7_1_, lvt_7_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 6, 4, 6, 6, 7, lvt_7_1_, lvt_7_1_, false);
         this.setBlockState(p_225577_1_, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true)).with(FenceBlock.SOUTH, true), 0, 6, 8, p_225577_4_);
         this.setBlockState(p_225577_1_, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.SOUTH, true), 6, 6, 8, p_225577_4_);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 6, 8, 5, 6, 8, lvt_6_1_, lvt_6_1_, false);
         this.setBlockState(p_225577_1_, (BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true), 1, 7, 8, p_225577_4_);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 7, 8, 4, 7, 8, lvt_6_1_, lvt_6_1_, false);
         this.setBlockState(p_225577_1_, (BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true), 5, 7, 8, p_225577_4_);
         this.setBlockState(p_225577_1_, (BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true), 2, 8, 8, p_225577_4_);
         this.setBlockState(p_225577_1_, lvt_6_1_, 3, 8, 8, p_225577_4_);
         this.setBlockState(p_225577_1_, (BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true), 4, 8, 8, p_225577_4_);
         if (!this.hasSpawner) {
            BlockPos lvt_8_1_ = new BlockPos(this.getXWithOffset(3, 5), this.getYWithOffset(5), this.getZWithOffset(3, 5));
            if (p_225577_4_.isVecInside(lvt_8_1_)) {
               this.hasSpawner = true;
               p_225577_1_.setBlockState(lvt_8_1_, Blocks.SPAWNER.getDefaultState(), 2);
               TileEntity lvt_9_1_ = p_225577_1_.getTileEntity(lvt_8_1_);
               if (lvt_9_1_ instanceof MobSpawnerTileEntity) {
                  ((MobSpawnerTileEntity)lvt_9_1_).getSpawnerBaseLogic().setEntityType(EntityType.BLAZE);
               }
            }
         }

         for(int lvt_8_2_ = 0; lvt_8_2_ <= 6; ++lvt_8_2_) {
            for(int lvt_9_2_ = 0; lvt_9_2_ <= 6; ++lvt_9_2_) {
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_8_2_, -1, lvt_9_2_, p_225577_4_);
            }
         }

         return true;
      }
   }

   public static class Stairs extends FortressPieces.Piece {
      public Stairs(int p_i50255_1_, MutableBoundingBox p_i50255_2_, Direction p_i50255_3_) {
         super(IStructurePieceType.NESR, p_i50255_1_);
         this.setCoordBaseMode(p_i50255_3_);
         this.boundingBox = p_i50255_2_;
      }

      public Stairs(TemplateManager p_i50256_1_, CompoundNBT p_i50256_2_) {
         super(IStructurePieceType.NESR, p_i50256_2_);
      }

      public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.getNextComponentZ((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 6, 2, false);
      }

      public static FortressPieces.Stairs createPiece(List<StructurePiece> p_175872_0_, int p_175872_1_, int p_175872_2_, int p_175872_3_, int p_175872_4_, Direction p_175872_5_) {
         MutableBoundingBox lvt_6_1_ = MutableBoundingBox.getComponentToAddBoundingBox(p_175872_1_, p_175872_2_, p_175872_3_, -2, 0, 0, 7, 11, 7, p_175872_5_);
         return isAboveGround(lvt_6_1_) && StructurePiece.findIntersecting(p_175872_0_, lvt_6_1_) == null ? new FortressPieces.Stairs(p_175872_4_, lvt_6_1_, p_175872_5_) : null;
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 6, 10, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 1, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 2, 0, 6, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 1, 0, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 2, 1, 6, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 2, 6, 5, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         BlockState lvt_6_1_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
         BlockState lvt_7_1_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 2, 0, 5, 4, lvt_7_1_, lvt_7_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 3, 2, 6, 5, 2, lvt_7_1_, lvt_7_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 3, 4, 6, 5, 4, lvt_7_1_, lvt_7_1_, false);
         this.setBlockState(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), 5, 2, 5, p_225577_4_);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 2, 5, 4, 3, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 2, 5, 3, 4, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 2, 5, 2, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 2, 5, 1, 6, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 7, 1, 5, 7, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 8, 2, 6, 8, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 6, 0, 4, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 5, 0, 4, 5, 0, lvt_6_1_, lvt_6_1_, false);

         for(int lvt_8_1_ = 0; lvt_8_1_ <= 6; ++lvt_8_1_) {
            for(int lvt_9_1_ = 0; lvt_9_1_ <= 6; ++lvt_9_1_) {
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_8_1_, -1, lvt_9_1_, p_225577_4_);
            }
         }

         return true;
      }
   }

   public static class Crossing extends FortressPieces.Piece {
      public Crossing(int p_i50258_1_, MutableBoundingBox p_i50258_2_, Direction p_i50258_3_) {
         super(IStructurePieceType.NERC, p_i50258_1_);
         this.setCoordBaseMode(p_i50258_3_);
         this.boundingBox = p_i50258_2_;
      }

      public Crossing(TemplateManager p_i50259_1_, CompoundNBT p_i50259_2_) {
         super(IStructurePieceType.NERC, p_i50259_2_);
      }

      public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.getNextComponentNormal((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 2, 0, false);
         this.getNextComponentX((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, 2, false);
         this.getNextComponentZ((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 0, 2, false);
      }

      public static FortressPieces.Crossing createPiece(List<StructurePiece> p_175873_0_, int p_175873_1_, int p_175873_2_, int p_175873_3_, Direction p_175873_4_, int p_175873_5_) {
         MutableBoundingBox lvt_6_1_ = MutableBoundingBox.getComponentToAddBoundingBox(p_175873_1_, p_175873_2_, p_175873_3_, -2, 0, 0, 7, 9, 7, p_175873_4_);
         return isAboveGround(lvt_6_1_) && StructurePiece.findIntersecting(p_175873_0_, lvt_6_1_) == null ? new FortressPieces.Crossing(p_175873_5_, lvt_6_1_, p_175873_4_) : null;
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 6, 7, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 1, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 6, 1, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 2, 0, 6, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 2, 6, 6, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 0, 6, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 5, 0, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 2, 0, 6, 6, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 2, 5, 6, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         BlockState lvt_6_1_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
         BlockState lvt_7_1_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 6, 0, 4, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 5, 0, 4, 5, 0, lvt_6_1_, lvt_6_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 6, 6, 4, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 5, 6, 4, 5, 6, lvt_6_1_, lvt_6_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 6, 2, 0, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 5, 2, 0, 5, 4, lvt_7_1_, lvt_7_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 6, 2, 6, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 5, 2, 6, 5, 4, lvt_7_1_, lvt_7_1_, false);

         for(int lvt_8_1_ = 0; lvt_8_1_ <= 6; ++lvt_8_1_) {
            for(int lvt_9_1_ = 0; lvt_9_1_ <= 6; ++lvt_9_1_) {
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_8_1_, -1, lvt_9_1_, p_225577_4_);
            }
         }

         return true;
      }
   }

   public static class Crossing3 extends FortressPieces.Piece {
      public Crossing3(int p_i50286_1_, MutableBoundingBox p_i50286_2_, Direction p_i50286_3_) {
         super(IStructurePieceType.NEBCR, p_i50286_1_);
         this.setCoordBaseMode(p_i50286_3_);
         this.boundingBox = p_i50286_2_;
      }

      protected Crossing3(Random p_i2042_1_, int p_i2042_2_, int p_i2042_3_) {
         super(IStructurePieceType.NEBCR, 0);
         this.setCoordBaseMode(Direction.Plane.HORIZONTAL.random(p_i2042_1_));
         if (this.getCoordBaseMode().getAxis() == Direction.Axis.Z) {
            this.boundingBox = new MutableBoundingBox(p_i2042_2_, 64, p_i2042_3_, p_i2042_2_ + 19 - 1, 73, p_i2042_3_ + 19 - 1);
         } else {
            this.boundingBox = new MutableBoundingBox(p_i2042_2_, 64, p_i2042_3_, p_i2042_2_ + 19 - 1, 73, p_i2042_3_ + 19 - 1);
         }

      }

      protected Crossing3(IStructurePieceType p_i50287_1_, CompoundNBT p_i50287_2_) {
         super(p_i50287_1_, p_i50287_2_);
      }

      public Crossing3(TemplateManager p_i50288_1_, CompoundNBT p_i50288_2_) {
         this(IStructurePieceType.NEBCR, p_i50288_2_);
      }

      public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.getNextComponentNormal((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 8, 3, false);
         this.getNextComponentX((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 3, 8, false);
         this.getNextComponentZ((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 3, 8, false);
      }

      public static FortressPieces.Crossing3 createPiece(List<StructurePiece> p_175885_0_, int p_175885_1_, int p_175885_2_, int p_175885_3_, Direction p_175885_4_, int p_175885_5_) {
         MutableBoundingBox lvt_6_1_ = MutableBoundingBox.getComponentToAddBoundingBox(p_175885_1_, p_175885_2_, p_175885_3_, -8, -3, 0, 19, 10, 19, p_175885_4_);
         return isAboveGround(lvt_6_1_) && StructurePiece.findIntersecting(p_175885_0_, lvt_6_1_) == null ? new FortressPieces.Crossing3(p_175885_5_, lvt_6_1_, p_175885_4_) : null;
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 3, 0, 11, 4, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 7, 18, 4, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, 5, 0, 10, 7, 18, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 5, 8, 18, 7, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 5, 0, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 5, 11, 7, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 11, 5, 0, 11, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 11, 5, 11, 11, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 5, 7, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 11, 5, 7, 18, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 5, 11, 7, 5, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 11, 5, 11, 18, 5, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 2, 0, 11, 2, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 2, 13, 11, 2, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 0, 0, 11, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 0, 15, 11, 1, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

         int lvt_6_2_;
         int lvt_7_2_;
         for(lvt_6_2_ = 7; lvt_6_2_ <= 11; ++lvt_6_2_) {
            for(lvt_7_2_ = 0; lvt_7_2_ <= 2; ++lvt_7_2_) {
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_6_2_, -1, lvt_7_2_, p_225577_4_);
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_6_2_, -1, 18 - lvt_7_2_, p_225577_4_);
            }
         }

         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 7, 5, 2, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 13, 2, 7, 18, 2, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 0, 7, 3, 1, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 15, 0, 7, 18, 1, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

         for(lvt_6_2_ = 0; lvt_6_2_ <= 2; ++lvt_6_2_) {
            for(lvt_7_2_ = 7; lvt_7_2_ <= 11; ++lvt_7_2_) {
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_6_2_, -1, lvt_7_2_, p_225577_4_);
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), 18 - lvt_6_2_, -1, lvt_7_2_, p_225577_4_);
            }
         }

         return true;
      }
   }

   public static class End extends FortressPieces.Piece {
      private final int fillSeed;

      public End(int p_i45621_1_, Random p_i45621_2_, MutableBoundingBox p_i45621_3_, Direction p_i45621_4_) {
         super(IStructurePieceType.NEBEF, p_i45621_1_);
         this.setCoordBaseMode(p_i45621_4_);
         this.boundingBox = p_i45621_3_;
         this.fillSeed = p_i45621_2_.nextInt();
      }

      public End(TemplateManager p_i50285_1_, CompoundNBT p_i50285_2_) {
         super(IStructurePieceType.NEBEF, p_i50285_2_);
         this.fillSeed = p_i50285_2_.getInt("Seed");
      }

      public static FortressPieces.End createPiece(List<StructurePiece> p_175884_0_, Random p_175884_1_, int p_175884_2_, int p_175884_3_, int p_175884_4_, Direction p_175884_5_, int p_175884_6_) {
         MutableBoundingBox lvt_7_1_ = MutableBoundingBox.getComponentToAddBoundingBox(p_175884_2_, p_175884_3_, p_175884_4_, -1, -3, 0, 5, 10, 8, p_175884_5_);
         return isAboveGround(lvt_7_1_) && StructurePiece.findIntersecting(p_175884_0_, lvt_7_1_) == null ? new FortressPieces.End(p_175884_6_, p_175884_1_, lvt_7_1_, p_175884_5_) : null;
      }

      protected void readAdditional(CompoundNBT p_143011_1_) {
         super.readAdditional(p_143011_1_);
         p_143011_1_.putInt("Seed", this.fillSeed);
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         Random lvt_6_1_ = new Random((long)this.fillSeed);

         int lvt_7_5_;
         int lvt_8_3_;
         int lvt_9_2_;
         for(lvt_7_5_ = 0; lvt_7_5_ <= 4; ++lvt_7_5_) {
            for(lvt_8_3_ = 3; lvt_8_3_ <= 4; ++lvt_8_3_) {
               lvt_9_2_ = lvt_6_1_.nextInt(8);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_7_5_, lvt_8_3_, 0, lvt_7_5_, lvt_8_3_, lvt_9_2_, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            }
         }

         lvt_7_5_ = lvt_6_1_.nextInt(8);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 5, 0, 0, 5, lvt_7_5_, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         lvt_7_5_ = lvt_6_1_.nextInt(8);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 5, 0, 4, 5, lvt_7_5_, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

         for(lvt_7_5_ = 0; lvt_7_5_ <= 4; ++lvt_7_5_) {
            lvt_8_3_ = lvt_6_1_.nextInt(5);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_7_5_, 2, 0, lvt_7_5_, 2, lvt_8_3_, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         }

         for(lvt_7_5_ = 0; lvt_7_5_ <= 4; ++lvt_7_5_) {
            for(lvt_8_3_ = 0; lvt_8_3_ <= 1; ++lvt_8_3_) {
               lvt_9_2_ = lvt_6_1_.nextInt(3);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_7_5_, lvt_8_3_, 0, lvt_7_5_, lvt_8_3_, lvt_9_2_, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            }
         }

         return true;
      }
   }

   public static class Straight extends FortressPieces.Piece {
      public Straight(int p_i45620_1_, Random p_i45620_2_, MutableBoundingBox p_i45620_3_, Direction p_i45620_4_) {
         super(IStructurePieceType.NEBS, p_i45620_1_);
         this.setCoordBaseMode(p_i45620_4_);
         this.boundingBox = p_i45620_3_;
      }

      public Straight(TemplateManager p_i50283_1_, CompoundNBT p_i50283_2_) {
         super(IStructurePieceType.NEBS, p_i50283_2_);
      }

      public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         this.getNextComponentNormal((FortressPieces.Start)p_74861_1_, p_74861_2_, p_74861_3_, 1, 3, false);
      }

      public static FortressPieces.Straight createPiece(List<StructurePiece> p_175882_0_, Random p_175882_1_, int p_175882_2_, int p_175882_3_, int p_175882_4_, Direction p_175882_5_, int p_175882_6_) {
         MutableBoundingBox lvt_7_1_ = MutableBoundingBox.getComponentToAddBoundingBox(p_175882_2_, p_175882_3_, p_175882_4_, -1, -3, 0, 5, 10, 19, p_175882_5_);
         return isAboveGround(lvt_7_1_) && StructurePiece.findIntersecting(p_175882_0_, lvt_7_1_) == null ? new FortressPieces.Straight(p_175882_6_, p_175882_1_, lvt_7_1_, p_175882_5_) : null;
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 0, 4, 4, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 5, 0, 3, 7, 18, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 5, 0, 0, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 5, 0, 4, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 4, 2, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 13, 4, 2, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 0, 0, 4, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 0, 15, 4, 1, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

         for(int lvt_6_1_ = 0; lvt_6_1_ <= 4; ++lvt_6_1_) {
            for(int lvt_7_1_ = 0; lvt_7_1_ <= 2; ++lvt_7_1_) {
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_6_1_, -1, lvt_7_1_, p_225577_4_);
               this.replaceAirAndLiquidDownwards(p_225577_1_, Blocks.NETHER_BRICKS.getDefaultState(), lvt_6_1_, -1, 18 - lvt_7_1_, p_225577_4_);
            }
         }

         BlockState lvt_6_2_ = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
         BlockState lvt_7_2_ = (BlockState)lvt_6_2_.with(FenceBlock.EAST, true);
         BlockState lvt_8_1_ = (BlockState)lvt_6_2_.with(FenceBlock.WEST, true);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 1, 1, 0, 4, 1, lvt_7_2_, lvt_7_2_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 4, 0, 4, 4, lvt_7_2_, lvt_7_2_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 14, 0, 4, 14, lvt_7_2_, lvt_7_2_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 1, 17, 0, 4, 17, lvt_7_2_, lvt_7_2_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 1, 1, 4, 4, 1, lvt_8_1_, lvt_8_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 3, 4, 4, 4, 4, lvt_8_1_, lvt_8_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 3, 14, 4, 4, 14, lvt_8_1_, lvt_8_1_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 1, 17, 4, 4, 17, lvt_8_1_, lvt_8_1_, false);
         return true;
      }
   }

   public static class Start extends FortressPieces.Crossing3 {
      public FortressPieces.PieceWeight lastPlaced;
      public List<FortressPieces.PieceWeight> primaryWeights;
      public List<FortressPieces.PieceWeight> secondaryWeights;
      public final List<StructurePiece> pendingChildren = Lists.newArrayList();

      public Start(Random p_i2059_1_, int p_i2059_2_, int p_i2059_3_) {
         super(p_i2059_1_, p_i2059_2_, p_i2059_3_);
         this.primaryWeights = Lists.newArrayList();
         FortressPieces.PieceWeight[] var4 = FortressPieces.PRIMARY_COMPONENTS;
         int var5 = var4.length;

         int var6;
         FortressPieces.PieceWeight lvt_7_2_;
         for(var6 = 0; var6 < var5; ++var6) {
            lvt_7_2_ = var4[var6];
            lvt_7_2_.placeCount = 0;
            this.primaryWeights.add(lvt_7_2_);
         }

         this.secondaryWeights = Lists.newArrayList();
         var4 = FortressPieces.SECONDARY_COMPONENTS;
         var5 = var4.length;

         for(var6 = 0; var6 < var5; ++var6) {
            lvt_7_2_ = var4[var6];
            lvt_7_2_.placeCount = 0;
            this.secondaryWeights.add(lvt_7_2_);
         }

      }

      public Start(TemplateManager p_i50253_1_, CompoundNBT p_i50253_2_) {
         super(IStructurePieceType.NESTART, p_i50253_2_);
      }
   }

   abstract static class Piece extends StructurePiece {
      protected Piece(IStructurePieceType p_i50260_1_, int p_i50260_2_) {
         super(p_i50260_1_, p_i50260_2_);
      }

      public Piece(IStructurePieceType p_i50261_1_, CompoundNBT p_i50261_2_) {
         super(p_i50261_1_, p_i50261_2_);
      }

      protected void readAdditional(CompoundNBT p_143011_1_) {
      }

      private int getTotalWeight(List<FortressPieces.PieceWeight> p_74960_1_) {
         boolean lvt_2_1_ = false;
         int lvt_3_1_ = 0;

         FortressPieces.PieceWeight lvt_5_1_;
         for(Iterator var4 = p_74960_1_.iterator(); var4.hasNext(); lvt_3_1_ += lvt_5_1_.weight) {
            lvt_5_1_ = (FortressPieces.PieceWeight)var4.next();
            if (lvt_5_1_.maxPlaceCount > 0 && lvt_5_1_.placeCount < lvt_5_1_.maxPlaceCount) {
               lvt_2_1_ = true;
            }
         }

         return lvt_2_1_ ? lvt_3_1_ : -1;
      }

      private FortressPieces.Piece generatePiece(FortressPieces.Start p_175871_1_, List<FortressPieces.PieceWeight> p_175871_2_, List<StructurePiece> p_175871_3_, Random p_175871_4_, int p_175871_5_, int p_175871_6_, int p_175871_7_, Direction p_175871_8_, int p_175871_9_) {
         int lvt_10_1_ = this.getTotalWeight(p_175871_2_);
         boolean lvt_11_1_ = lvt_10_1_ > 0 && p_175871_9_ <= 30;
         int lvt_12_1_ = 0;

         while(lvt_12_1_ < 5 && lvt_11_1_) {
            ++lvt_12_1_;
            int lvt_13_1_ = p_175871_4_.nextInt(lvt_10_1_);
            Iterator var14 = p_175871_2_.iterator();

            while(var14.hasNext()) {
               FortressPieces.PieceWeight lvt_15_1_ = (FortressPieces.PieceWeight)var14.next();
               lvt_13_1_ -= lvt_15_1_.weight;
               if (lvt_13_1_ < 0) {
                  if (!lvt_15_1_.doPlace(p_175871_9_) || lvt_15_1_ == p_175871_1_.lastPlaced && !lvt_15_1_.allowInRow) {
                     break;
                  }

                  FortressPieces.Piece lvt_16_1_ = FortressPieces.findAndCreateBridgePieceFactory(lvt_15_1_, p_175871_3_, p_175871_4_, p_175871_5_, p_175871_6_, p_175871_7_, p_175871_8_, p_175871_9_);
                  if (lvt_16_1_ != null) {
                     ++lvt_15_1_.placeCount;
                     p_175871_1_.lastPlaced = lvt_15_1_;
                     if (!lvt_15_1_.isValid()) {
                        p_175871_2_.remove(lvt_15_1_);
                     }

                     return lvt_16_1_;
                  }
               }
            }
         }

         return FortressPieces.End.createPiece(p_175871_3_, p_175871_4_, p_175871_5_, p_175871_6_, p_175871_7_, p_175871_8_, p_175871_9_);
      }

      private StructurePiece generateAndAddPiece(FortressPieces.Start p_175870_1_, List<StructurePiece> p_175870_2_, Random p_175870_3_, int p_175870_4_, int p_175870_5_, int p_175870_6_, @Nullable Direction p_175870_7_, int p_175870_8_, boolean p_175870_9_) {
         if (Math.abs(p_175870_4_ - p_175870_1_.getBoundingBox().minX) <= 112 && Math.abs(p_175870_6_ - p_175870_1_.getBoundingBox().minZ) <= 112) {
            List<FortressPieces.PieceWeight> lvt_10_1_ = p_175870_1_.primaryWeights;
            if (p_175870_9_) {
               lvt_10_1_ = p_175870_1_.secondaryWeights;
            }

            StructurePiece lvt_11_1_ = this.generatePiece(p_175870_1_, lvt_10_1_, p_175870_2_, p_175870_3_, p_175870_4_, p_175870_5_, p_175870_6_, p_175870_7_, p_175870_8_ + 1);
            if (lvt_11_1_ != null) {
               p_175870_2_.add(lvt_11_1_);
               p_175870_1_.pendingChildren.add(lvt_11_1_);
            }

            return lvt_11_1_;
         } else {
            return FortressPieces.End.createPiece(p_175870_2_, p_175870_3_, p_175870_4_, p_175870_5_, p_175870_6_, p_175870_7_, p_175870_8_);
         }
      }

      @Nullable
      protected StructurePiece getNextComponentNormal(FortressPieces.Start p_74963_1_, List<StructurePiece> p_74963_2_, Random p_74963_3_, int p_74963_4_, int p_74963_5_, boolean p_74963_6_) {
         Direction lvt_7_1_ = this.getCoordBaseMode();
         if (lvt_7_1_ != null) {
            switch(lvt_7_1_) {
            case NORTH:
               return this.generateAndAddPiece(p_74963_1_, p_74963_2_, p_74963_3_, this.boundingBox.minX + p_74963_4_, this.boundingBox.minY + p_74963_5_, this.boundingBox.minZ - 1, lvt_7_1_, this.getComponentType(), p_74963_6_);
            case SOUTH:
               return this.generateAndAddPiece(p_74963_1_, p_74963_2_, p_74963_3_, this.boundingBox.minX + p_74963_4_, this.boundingBox.minY + p_74963_5_, this.boundingBox.maxZ + 1, lvt_7_1_, this.getComponentType(), p_74963_6_);
            case WEST:
               return this.generateAndAddPiece(p_74963_1_, p_74963_2_, p_74963_3_, this.boundingBox.minX - 1, this.boundingBox.minY + p_74963_5_, this.boundingBox.minZ + p_74963_4_, lvt_7_1_, this.getComponentType(), p_74963_6_);
            case EAST:
               return this.generateAndAddPiece(p_74963_1_, p_74963_2_, p_74963_3_, this.boundingBox.maxX + 1, this.boundingBox.minY + p_74963_5_, this.boundingBox.minZ + p_74963_4_, lvt_7_1_, this.getComponentType(), p_74963_6_);
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece getNextComponentX(FortressPieces.Start p_74961_1_, List<StructurePiece> p_74961_2_, Random p_74961_3_, int p_74961_4_, int p_74961_5_, boolean p_74961_6_) {
         Direction lvt_7_1_ = this.getCoordBaseMode();
         if (lvt_7_1_ != null) {
            switch(lvt_7_1_) {
            case NORTH:
               return this.generateAndAddPiece(p_74961_1_, p_74961_2_, p_74961_3_, this.boundingBox.minX - 1, this.boundingBox.minY + p_74961_4_, this.boundingBox.minZ + p_74961_5_, Direction.WEST, this.getComponentType(), p_74961_6_);
            case SOUTH:
               return this.generateAndAddPiece(p_74961_1_, p_74961_2_, p_74961_3_, this.boundingBox.minX - 1, this.boundingBox.minY + p_74961_4_, this.boundingBox.minZ + p_74961_5_, Direction.WEST, this.getComponentType(), p_74961_6_);
            case WEST:
               return this.generateAndAddPiece(p_74961_1_, p_74961_2_, p_74961_3_, this.boundingBox.minX + p_74961_5_, this.boundingBox.minY + p_74961_4_, this.boundingBox.minZ - 1, Direction.NORTH, this.getComponentType(), p_74961_6_);
            case EAST:
               return this.generateAndAddPiece(p_74961_1_, p_74961_2_, p_74961_3_, this.boundingBox.minX + p_74961_5_, this.boundingBox.minY + p_74961_4_, this.boundingBox.minZ - 1, Direction.NORTH, this.getComponentType(), p_74961_6_);
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece getNextComponentZ(FortressPieces.Start p_74965_1_, List<StructurePiece> p_74965_2_, Random p_74965_3_, int p_74965_4_, int p_74965_5_, boolean p_74965_6_) {
         Direction lvt_7_1_ = this.getCoordBaseMode();
         if (lvt_7_1_ != null) {
            switch(lvt_7_1_) {
            case NORTH:
               return this.generateAndAddPiece(p_74965_1_, p_74965_2_, p_74965_3_, this.boundingBox.maxX + 1, this.boundingBox.minY + p_74965_4_, this.boundingBox.minZ + p_74965_5_, Direction.EAST, this.getComponentType(), p_74965_6_);
            case SOUTH:
               return this.generateAndAddPiece(p_74965_1_, p_74965_2_, p_74965_3_, this.boundingBox.maxX + 1, this.boundingBox.minY + p_74965_4_, this.boundingBox.minZ + p_74965_5_, Direction.EAST, this.getComponentType(), p_74965_6_);
            case WEST:
               return this.generateAndAddPiece(p_74965_1_, p_74965_2_, p_74965_3_, this.boundingBox.minX + p_74965_5_, this.boundingBox.minY + p_74965_4_, this.boundingBox.maxZ + 1, Direction.SOUTH, this.getComponentType(), p_74965_6_);
            case EAST:
               return this.generateAndAddPiece(p_74965_1_, p_74965_2_, p_74965_3_, this.boundingBox.minX + p_74965_5_, this.boundingBox.minY + p_74965_4_, this.boundingBox.maxZ + 1, Direction.SOUTH, this.getComponentType(), p_74965_6_);
            }
         }

         return null;
      }

      protected static boolean isAboveGround(MutableBoundingBox p_74964_0_) {
         return p_74964_0_ != null && p_74964_0_.minY > 10;
      }
   }

   static class PieceWeight {
      public final Class<? extends FortressPieces.Piece> weightClass;
      public final int weight;
      public int placeCount;
      public final int maxPlaceCount;
      public final boolean allowInRow;

      public PieceWeight(Class<? extends FortressPieces.Piece> p_i2055_1_, int p_i2055_2_, int p_i2055_3_, boolean p_i2055_4_) {
         this.weightClass = p_i2055_1_;
         this.weight = p_i2055_2_;
         this.maxPlaceCount = p_i2055_3_;
         this.allowInRow = p_i2055_4_;
      }

      public PieceWeight(Class<? extends FortressPieces.Piece> p_i2056_1_, int p_i2056_2_, int p_i2056_3_) {
         this(p_i2056_1_, p_i2056_2_, p_i2056_3_, false);
      }

      public boolean doPlace(int p_78822_1_) {
         return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
      }

      public boolean isValid() {
         return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
      }
   }
}
