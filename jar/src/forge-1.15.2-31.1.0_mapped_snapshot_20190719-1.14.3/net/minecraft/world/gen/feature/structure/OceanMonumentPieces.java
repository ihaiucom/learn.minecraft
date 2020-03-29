package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class OceanMonumentPieces {
   static class YZDoubleRoomFitHelper implements OceanMonumentPieces.IMonumentRoomFitHelper {
      private YZDoubleRoomFitHelper() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition p_175969_1_) {
         if (p_175969_1_.hasOpening[Direction.NORTH.getIndex()] && !p_175969_1_.connections[Direction.NORTH.getIndex()].claimed && p_175969_1_.hasOpening[Direction.UP.getIndex()] && !p_175969_1_.connections[Direction.UP.getIndex()].claimed) {
            OceanMonumentPieces.RoomDefinition lvt_2_1_ = p_175969_1_.connections[Direction.NORTH.getIndex()];
            return lvt_2_1_.hasOpening[Direction.UP.getIndex()] && !lvt_2_1_.connections[Direction.UP.getIndex()].claimed;
         } else {
            return false;
         }
      }

      public OceanMonumentPieces.Piece create(Direction p_175968_1_, OceanMonumentPieces.RoomDefinition p_175968_2_, Random p_175968_3_) {
         p_175968_2_.claimed = true;
         p_175968_2_.connections[Direction.NORTH.getIndex()].claimed = true;
         p_175968_2_.connections[Direction.UP.getIndex()].claimed = true;
         p_175968_2_.connections[Direction.NORTH.getIndex()].connections[Direction.UP.getIndex()].claimed = true;
         return new OceanMonumentPieces.DoubleYZRoom(p_175968_1_, p_175968_2_);
      }

      // $FF: synthetic method
      YZDoubleRoomFitHelper(Object p_i46472_1_) {
         this();
      }
   }

   static class XYDoubleRoomFitHelper implements OceanMonumentPieces.IMonumentRoomFitHelper {
      private XYDoubleRoomFitHelper() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition p_175969_1_) {
         if (p_175969_1_.hasOpening[Direction.EAST.getIndex()] && !p_175969_1_.connections[Direction.EAST.getIndex()].claimed && p_175969_1_.hasOpening[Direction.UP.getIndex()] && !p_175969_1_.connections[Direction.UP.getIndex()].claimed) {
            OceanMonumentPieces.RoomDefinition lvt_2_1_ = p_175969_1_.connections[Direction.EAST.getIndex()];
            return lvt_2_1_.hasOpening[Direction.UP.getIndex()] && !lvt_2_1_.connections[Direction.UP.getIndex()].claimed;
         } else {
            return false;
         }
      }

      public OceanMonumentPieces.Piece create(Direction p_175968_1_, OceanMonumentPieces.RoomDefinition p_175968_2_, Random p_175968_3_) {
         p_175968_2_.claimed = true;
         p_175968_2_.connections[Direction.EAST.getIndex()].claimed = true;
         p_175968_2_.connections[Direction.UP.getIndex()].claimed = true;
         p_175968_2_.connections[Direction.EAST.getIndex()].connections[Direction.UP.getIndex()].claimed = true;
         return new OceanMonumentPieces.DoubleXYRoom(p_175968_1_, p_175968_2_);
      }

      // $FF: synthetic method
      XYDoubleRoomFitHelper(Object p_i46474_1_) {
         this();
      }
   }

   static class ZDoubleRoomFitHelper implements OceanMonumentPieces.IMonumentRoomFitHelper {
      private ZDoubleRoomFitHelper() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition p_175969_1_) {
         return p_175969_1_.hasOpening[Direction.NORTH.getIndex()] && !p_175969_1_.connections[Direction.NORTH.getIndex()].claimed;
      }

      public OceanMonumentPieces.Piece create(Direction p_175968_1_, OceanMonumentPieces.RoomDefinition p_175968_2_, Random p_175968_3_) {
         OceanMonumentPieces.RoomDefinition lvt_4_1_ = p_175968_2_;
         if (!p_175968_2_.hasOpening[Direction.NORTH.getIndex()] || p_175968_2_.connections[Direction.NORTH.getIndex()].claimed) {
            lvt_4_1_ = p_175968_2_.connections[Direction.SOUTH.getIndex()];
         }

         lvt_4_1_.claimed = true;
         lvt_4_1_.connections[Direction.NORTH.getIndex()].claimed = true;
         return new OceanMonumentPieces.DoubleZRoom(p_175968_1_, lvt_4_1_);
      }

      // $FF: synthetic method
      ZDoubleRoomFitHelper(Object p_i46471_1_) {
         this();
      }
   }

   static class XDoubleRoomFitHelper implements OceanMonumentPieces.IMonumentRoomFitHelper {
      private XDoubleRoomFitHelper() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition p_175969_1_) {
         return p_175969_1_.hasOpening[Direction.EAST.getIndex()] && !p_175969_1_.connections[Direction.EAST.getIndex()].claimed;
      }

      public OceanMonumentPieces.Piece create(Direction p_175968_1_, OceanMonumentPieces.RoomDefinition p_175968_2_, Random p_175968_3_) {
         p_175968_2_.claimed = true;
         p_175968_2_.connections[Direction.EAST.getIndex()].claimed = true;
         return new OceanMonumentPieces.DoubleXRoom(p_175968_1_, p_175968_2_);
      }

      // $FF: synthetic method
      XDoubleRoomFitHelper(Object p_i46475_1_) {
         this();
      }
   }

   static class YDoubleRoomFitHelper implements OceanMonumentPieces.IMonumentRoomFitHelper {
      private YDoubleRoomFitHelper() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition p_175969_1_) {
         return p_175969_1_.hasOpening[Direction.UP.getIndex()] && !p_175969_1_.connections[Direction.UP.getIndex()].claimed;
      }

      public OceanMonumentPieces.Piece create(Direction p_175968_1_, OceanMonumentPieces.RoomDefinition p_175968_2_, Random p_175968_3_) {
         p_175968_2_.claimed = true;
         p_175968_2_.connections[Direction.UP.getIndex()].claimed = true;
         return new OceanMonumentPieces.DoubleYRoom(p_175968_1_, p_175968_2_);
      }

      // $FF: synthetic method
      YDoubleRoomFitHelper(Object p_i46473_1_) {
         this();
      }
   }

   static class FitSimpleRoomTopHelper implements OceanMonumentPieces.IMonumentRoomFitHelper {
      private FitSimpleRoomTopHelper() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition p_175969_1_) {
         return !p_175969_1_.hasOpening[Direction.WEST.getIndex()] && !p_175969_1_.hasOpening[Direction.EAST.getIndex()] && !p_175969_1_.hasOpening[Direction.NORTH.getIndex()] && !p_175969_1_.hasOpening[Direction.SOUTH.getIndex()] && !p_175969_1_.hasOpening[Direction.UP.getIndex()];
      }

      public OceanMonumentPieces.Piece create(Direction p_175968_1_, OceanMonumentPieces.RoomDefinition p_175968_2_, Random p_175968_3_) {
         p_175968_2_.claimed = true;
         return new OceanMonumentPieces.SimpleTopRoom(p_175968_1_, p_175968_2_);
      }

      // $FF: synthetic method
      FitSimpleRoomTopHelper(Object p_i46469_1_) {
         this();
      }
   }

   static class FitSimpleRoomHelper implements OceanMonumentPieces.IMonumentRoomFitHelper {
      private FitSimpleRoomHelper() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition p_175969_1_) {
         return true;
      }

      public OceanMonumentPieces.Piece create(Direction p_175968_1_, OceanMonumentPieces.RoomDefinition p_175968_2_, Random p_175968_3_) {
         p_175968_2_.claimed = true;
         return new OceanMonumentPieces.SimpleRoom(p_175968_1_, p_175968_2_, p_175968_3_);
      }

      // $FF: synthetic method
      FitSimpleRoomHelper(Object p_i46470_1_) {
         this();
      }
   }

   interface IMonumentRoomFitHelper {
      boolean fits(OceanMonumentPieces.RoomDefinition var1);

      OceanMonumentPieces.Piece create(Direction var1, OceanMonumentPieces.RoomDefinition var2, Random var3);
   }

   static class RoomDefinition {
      private final int index;
      private final OceanMonumentPieces.RoomDefinition[] connections = new OceanMonumentPieces.RoomDefinition[6];
      private final boolean[] hasOpening = new boolean[6];
      private boolean claimed;
      private boolean isSource;
      private int scanIndex;

      public RoomDefinition(int p_i45584_1_) {
         this.index = p_i45584_1_;
      }

      public void setConnection(Direction p_175957_1_, OceanMonumentPieces.RoomDefinition p_175957_2_) {
         this.connections[p_175957_1_.getIndex()] = p_175957_2_;
         p_175957_2_.connections[p_175957_1_.getOpposite().getIndex()] = this;
      }

      public void updateOpenings() {
         for(int lvt_1_1_ = 0; lvt_1_1_ < 6; ++lvt_1_1_) {
            this.hasOpening[lvt_1_1_] = this.connections[lvt_1_1_] != null;
         }

      }

      public boolean findSource(int p_175959_1_) {
         if (this.isSource) {
            return true;
         } else {
            this.scanIndex = p_175959_1_;

            for(int lvt_2_1_ = 0; lvt_2_1_ < 6; ++lvt_2_1_) {
               if (this.connections[lvt_2_1_] != null && this.hasOpening[lvt_2_1_] && this.connections[lvt_2_1_].scanIndex != p_175959_1_ && this.connections[lvt_2_1_].findSource(p_175959_1_)) {
                  return true;
               }
            }

            return false;
         }
      }

      public boolean isSpecial() {
         return this.index >= 75;
      }

      public int countOpenings() {
         int lvt_1_1_ = 0;

         for(int lvt_2_1_ = 0; lvt_2_1_ < 6; ++lvt_2_1_) {
            if (this.hasOpening[lvt_2_1_]) {
               ++lvt_1_1_;
            }
         }

         return lvt_1_1_;
      }
   }

   public static class Penthouse extends OceanMonumentPieces.Piece {
      public Penthouse(Direction p_i45591_1_, MutableBoundingBox p_i45591_2_) {
         super(IStructurePieceType.OMPENTHOUSE, p_i45591_1_, p_i45591_2_);
      }

      public Penthouse(TemplateManager p_i50651_1_, CompoundNBT p_i50651_2_) {
         super(IStructurePieceType.OMPENTHOUSE, p_i50651_2_);
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, -1, 2, 11, -1, 11, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, -1, 0, 1, -1, 11, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 12, -1, 0, 13, -1, 11, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, -1, 0, 11, -1, 1, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, -1, 12, 11, -1, 13, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 0, 0, 0, 0, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 13, 0, 0, 13, 0, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 0, 0, 12, 0, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 0, 13, 12, 0, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);

         for(int lvt_6_1_ = 2; lvt_6_1_ <= 11; lvt_6_1_ += 3) {
            this.setBlockState(p_225577_1_, SEA_LANTERN, 0, 0, lvt_6_1_, p_225577_4_);
            this.setBlockState(p_225577_1_, SEA_LANTERN, 13, 0, lvt_6_1_, p_225577_4_);
            this.setBlockState(p_225577_1_, SEA_LANTERN, lvt_6_1_, 0, 0, p_225577_4_);
         }

         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 0, 3, 4, 0, 9, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 9, 0, 3, 11, 0, 9, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 4, 0, 9, 9, 0, 11, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 5, 0, 8, p_225577_4_);
         this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 8, 0, 8, p_225577_4_);
         this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 10, 0, 10, p_225577_4_);
         this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 3, 0, 10, p_225577_4_);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 0, 3, 3, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 10, 0, 3, 10, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 0, 10, 7, 0, 10, DARK_PRISMARINE, DARK_PRISMARINE, false);
         int lvt_6_2_ = 3;

         for(int lvt_7_1_ = 0; lvt_7_1_ < 2; ++lvt_7_1_) {
            for(int lvt_8_1_ = 2; lvt_8_1_ <= 8; lvt_8_1_ += 3) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_6_2_, 0, lvt_8_1_, lvt_6_2_, 2, lvt_8_1_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            lvt_6_2_ = 10;
         }

         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 0, 10, 5, 2, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, 0, 10, 8, 2, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, -1, 7, 7, -1, 8, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.makeOpening(p_225577_1_, p_225577_4_, 6, -1, 3, 7, -1, 4);
         this.spawnElder(p_225577_1_, p_225577_4_, 6, 1, 6);
         return true;
      }
   }

   public static class WingRoom extends OceanMonumentPieces.Piece {
      private int mainDesign;

      public WingRoom(Direction p_i45585_1_, MutableBoundingBox p_i45585_2_, int p_i45585_3_) {
         super(IStructurePieceType.OMWR, p_i45585_1_, p_i45585_2_);
         this.mainDesign = p_i45585_3_ & 1;
      }

      public WingRoom(TemplateManager p_i50643_1_, CompoundNBT p_i50643_2_) {
         super(IStructurePieceType.OMWR, p_i50643_2_);
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         if (this.mainDesign == 0) {
            int lvt_6_2_;
            for(lvt_6_2_ = 0; lvt_6_2_ < 4; ++lvt_6_2_) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 10 - lvt_6_2_, 3 - lvt_6_2_, 20 - lvt_6_2_, 12 + lvt_6_2_, 3 - lvt_6_2_, 20, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 0, 6, 15, 0, 16, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 0, 6, 6, 3, 20, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 16, 0, 6, 16, 3, 20, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 1, 7, 7, 1, 20, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 15, 1, 7, 15, 1, 20, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 1, 6, 9, 3, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 13, 1, 6, 15, 3, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, 1, 7, 9, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 13, 1, 7, 14, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 9, 0, 5, 13, 0, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 10, 0, 7, 12, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, 0, 10, 8, 0, 12, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 14, 0, 10, 14, 0, 12, DARK_PRISMARINE, DARK_PRISMARINE, false);

            for(lvt_6_2_ = 18; lvt_6_2_ >= 7; lvt_6_2_ -= 3) {
               this.setBlockState(p_225577_1_, SEA_LANTERN, 6, 3, lvt_6_2_, p_225577_4_);
               this.setBlockState(p_225577_1_, SEA_LANTERN, 16, 3, lvt_6_2_, p_225577_4_);
            }

            this.setBlockState(p_225577_1_, SEA_LANTERN, 10, 0, 10, p_225577_4_);
            this.setBlockState(p_225577_1_, SEA_LANTERN, 12, 0, 10, p_225577_4_);
            this.setBlockState(p_225577_1_, SEA_LANTERN, 10, 0, 12, p_225577_4_);
            this.setBlockState(p_225577_1_, SEA_LANTERN, 12, 0, 12, p_225577_4_);
            this.setBlockState(p_225577_1_, SEA_LANTERN, 8, 3, 6, p_225577_4_);
            this.setBlockState(p_225577_1_, SEA_LANTERN, 14, 3, 6, p_225577_4_);
            this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 4, 2, 4, p_225577_4_);
            this.setBlockState(p_225577_1_, SEA_LANTERN, 4, 1, 4, p_225577_4_);
            this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 4, 0, 4, p_225577_4_);
            this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 18, 2, 4, p_225577_4_);
            this.setBlockState(p_225577_1_, SEA_LANTERN, 18, 1, 4, p_225577_4_);
            this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 18, 0, 4, p_225577_4_);
            this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 4, 2, 18, p_225577_4_);
            this.setBlockState(p_225577_1_, SEA_LANTERN, 4, 1, 18, p_225577_4_);
            this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 4, 0, 18, p_225577_4_);
            this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 18, 2, 18, p_225577_4_);
            this.setBlockState(p_225577_1_, SEA_LANTERN, 18, 1, 18, p_225577_4_);
            this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 18, 0, 18, p_225577_4_);
            this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 9, 7, 20, p_225577_4_);
            this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 13, 7, 20, p_225577_4_);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 0, 21, 7, 4, 21, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 15, 0, 21, 16, 4, 21, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.spawnElder(p_225577_1_, p_225577_4_, 11, 2, 16);
         } else if (this.mainDesign == 1) {
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 9, 3, 18, 13, 3, 20, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 9, 0, 18, 9, 2, 18, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 13, 0, 18, 13, 2, 18, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            int lvt_6_3_ = 9;
            int lvt_7_1_ = true;
            int lvt_8_1_ = true;

            int lvt_9_3_;
            for(lvt_9_3_ = 0; lvt_9_3_ < 2; ++lvt_9_3_) {
               this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, lvt_6_3_, 6, 20, p_225577_4_);
               this.setBlockState(p_225577_1_, SEA_LANTERN, lvt_6_3_, 5, 20, p_225577_4_);
               this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, lvt_6_3_, 4, 20, p_225577_4_);
               lvt_6_3_ = 13;
            }

            this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 3, 7, 15, 3, 14, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            lvt_6_3_ = 10;

            for(lvt_9_3_ = 0; lvt_9_3_ < 2; ++lvt_9_3_) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_6_3_, 0, 10, lvt_6_3_, 6, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_6_3_, 0, 12, lvt_6_3_, 6, 12, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.setBlockState(p_225577_1_, SEA_LANTERN, lvt_6_3_, 0, 10, p_225577_4_);
               this.setBlockState(p_225577_1_, SEA_LANTERN, lvt_6_3_, 0, 12, p_225577_4_);
               this.setBlockState(p_225577_1_, SEA_LANTERN, lvt_6_3_, 4, 10, p_225577_4_);
               this.setBlockState(p_225577_1_, SEA_LANTERN, lvt_6_3_, 4, 12, p_225577_4_);
               lvt_6_3_ = 12;
            }

            lvt_6_3_ = 8;

            for(lvt_9_3_ = 0; lvt_9_3_ < 2; ++lvt_9_3_) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_6_3_, 0, 7, lvt_6_3_, 2, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_6_3_, 0, 14, lvt_6_3_, 2, 14, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               lvt_6_3_ = 14;
            }

            this.fillWithBlocks(p_225577_1_, p_225577_4_, 8, 3, 8, 8, 3, 13, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 14, 3, 8, 14, 3, 13, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.spawnElder(p_225577_1_, p_225577_4_, 11, 5, 13);
         }

         return true;
      }
   }

   public static class MonumentCoreRoom extends OceanMonumentPieces.Piece {
      public MonumentCoreRoom(Direction p_i50663_1_, OceanMonumentPieces.RoomDefinition p_i50663_2_) {
         super(IStructurePieceType.OMCR, 1, p_i50663_1_, p_i50663_2_, 2, 2, 2);
      }

      public MonumentCoreRoom(TemplateManager p_i50664_1_, CompoundNBT p_i50664_2_) {
         super(IStructurePieceType.OMCR, p_i50664_2_);
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         this.generateBoxOnFillOnly(p_225577_1_, p_225577_4_, 1, 8, 0, 14, 8, 14, ROUGH_PRISMARINE);
         int lvt_6_1_ = true;
         BlockState lvt_7_2_ = BRICKS_PRISMARINE;
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 7, 0, 0, 7, 15, lvt_7_2_, lvt_7_2_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 15, 7, 0, 15, 7, 15, lvt_7_2_, lvt_7_2_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 7, 0, 15, 7, 0, lvt_7_2_, lvt_7_2_, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 7, 15, 14, 7, 15, lvt_7_2_, lvt_7_2_, false);

         int lvt_6_3_;
         for(lvt_6_3_ = 1; lvt_6_3_ <= 6; ++lvt_6_3_) {
            lvt_7_2_ = BRICKS_PRISMARINE;
            if (lvt_6_3_ == 2 || lvt_6_3_ == 6) {
               lvt_7_2_ = ROUGH_PRISMARINE;
            }

            for(int lvt_8_1_ = 0; lvt_8_1_ <= 15; lvt_8_1_ += 15) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_8_1_, lvt_6_3_, 0, lvt_8_1_, lvt_6_3_, 1, lvt_7_2_, lvt_7_2_, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_8_1_, lvt_6_3_, 6, lvt_8_1_, lvt_6_3_, 9, lvt_7_2_, lvt_7_2_, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_8_1_, lvt_6_3_, 14, lvt_8_1_, lvt_6_3_, 15, lvt_7_2_, lvt_7_2_, false);
            }

            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, lvt_6_3_, 0, 1, lvt_6_3_, 0, lvt_7_2_, lvt_7_2_, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, lvt_6_3_, 0, 9, lvt_6_3_, 0, lvt_7_2_, lvt_7_2_, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 14, lvt_6_3_, 0, 14, lvt_6_3_, 0, lvt_7_2_, lvt_7_2_, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, lvt_6_3_, 15, 14, lvt_6_3_, 15, lvt_7_2_, lvt_7_2_, false);
         }

         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 3, 6, 9, 6, 9, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 4, 7, 8, 5, 8, Blocks.GOLD_BLOCK.getDefaultState(), Blocks.GOLD_BLOCK.getDefaultState(), false);

         for(lvt_6_3_ = 3; lvt_6_3_ <= 6; lvt_6_3_ += 3) {
            for(int lvt_7_3_ = 6; lvt_7_3_ <= 9; lvt_7_3_ += 3) {
               this.setBlockState(p_225577_1_, SEA_LANTERN, lvt_7_3_, lvt_6_3_, 6, p_225577_4_);
               this.setBlockState(p_225577_1_, SEA_LANTERN, lvt_7_3_, lvt_6_3_, 9, p_225577_4_);
            }
         }

         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 1, 6, 5, 2, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 1, 9, 5, 2, 9, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 10, 1, 6, 10, 2, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 10, 1, 9, 10, 2, 9, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 1, 5, 6, 2, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 9, 1, 5, 9, 2, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 1, 10, 6, 2, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 9, 1, 10, 9, 2, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 2, 5, 5, 6, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 2, 10, 5, 6, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 10, 2, 5, 10, 6, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 10, 2, 10, 10, 6, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 7, 1, 5, 7, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 10, 7, 1, 10, 7, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 7, 9, 5, 7, 14, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 10, 7, 9, 10, 7, 14, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 7, 5, 6, 7, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 7, 10, 6, 7, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 9, 7, 5, 14, 7, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 9, 7, 10, 14, 7, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 1, 2, 2, 1, 3, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 1, 2, 3, 1, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 13, 1, 2, 13, 1, 3, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 12, 1, 2, 12, 1, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 1, 12, 2, 1, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 1, 13, 3, 1, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 13, 1, 12, 13, 1, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 12, 1, 13, 12, 1, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         return true;
      }
   }

   public static class DoubleYZRoom extends OceanMonumentPieces.Piece {
      public DoubleYZRoom(Direction p_i50655_1_, OceanMonumentPieces.RoomDefinition p_i50655_2_) {
         super(IStructurePieceType.OMDYZR, 1, p_i50655_1_, p_i50655_2_, 1, 2, 2);
      }

      public DoubleYZRoom(TemplateManager p_i50656_1_, CompoundNBT p_i50656_2_) {
         super(IStructurePieceType.OMDYZR, p_i50656_2_);
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         OceanMonumentPieces.RoomDefinition lvt_6_1_ = this.roomDefinition.connections[Direction.NORTH.getIndex()];
         OceanMonumentPieces.RoomDefinition lvt_7_1_ = this.roomDefinition;
         OceanMonumentPieces.RoomDefinition lvt_8_1_ = lvt_6_1_.connections[Direction.UP.getIndex()];
         OceanMonumentPieces.RoomDefinition lvt_9_1_ = lvt_7_1_.connections[Direction.UP.getIndex()];
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_225577_1_, p_225577_4_, 0, 8, lvt_6_1_.hasOpening[Direction.DOWN.getIndex()]);
            this.generateDefaultFloor(p_225577_1_, p_225577_4_, 0, 0, lvt_7_1_.hasOpening[Direction.DOWN.getIndex()]);
         }

         if (lvt_9_1_.connections[Direction.UP.getIndex()] == null) {
            this.generateBoxOnFillOnly(p_225577_1_, p_225577_4_, 1, 8, 1, 6, 8, 7, ROUGH_PRISMARINE);
         }

         if (lvt_8_1_.connections[Direction.UP.getIndex()] == null) {
            this.generateBoxOnFillOnly(p_225577_1_, p_225577_4_, 1, 8, 8, 6, 8, 14, ROUGH_PRISMARINE);
         }

         int lvt_10_2_;
         BlockState lvt_11_2_;
         for(lvt_10_2_ = 1; lvt_10_2_ <= 7; ++lvt_10_2_) {
            lvt_11_2_ = BRICKS_PRISMARINE;
            if (lvt_10_2_ == 2 || lvt_10_2_ == 6) {
               lvt_11_2_ = ROUGH_PRISMARINE;
            }

            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, lvt_10_2_, 0, 0, lvt_10_2_, 15, lvt_11_2_, lvt_11_2_, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, lvt_10_2_, 0, 7, lvt_10_2_, 15, lvt_11_2_, lvt_11_2_, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, lvt_10_2_, 0, 6, lvt_10_2_, 0, lvt_11_2_, lvt_11_2_, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, lvt_10_2_, 15, 6, lvt_10_2_, 15, lvt_11_2_, lvt_11_2_, false);
         }

         for(lvt_10_2_ = 1; lvt_10_2_ <= 7; ++lvt_10_2_) {
            lvt_11_2_ = DARK_PRISMARINE;
            if (lvt_10_2_ == 2 || lvt_10_2_ == 6) {
               lvt_11_2_ = SEA_LANTERN;
            }

            this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, lvt_10_2_, 7, 4, lvt_10_2_, 8, lvt_11_2_, lvt_11_2_, false);
         }

         if (lvt_7_1_.hasOpening[Direction.SOUTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 3, 1, 0, 4, 2, 0);
         }

         if (lvt_7_1_.hasOpening[Direction.EAST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 7, 1, 3, 7, 2, 4);
         }

         if (lvt_7_1_.hasOpening[Direction.WEST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 0, 1, 3, 0, 2, 4);
         }

         if (lvt_6_1_.hasOpening[Direction.NORTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 3, 1, 15, 4, 2, 15);
         }

         if (lvt_6_1_.hasOpening[Direction.WEST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 0, 1, 11, 0, 2, 12);
         }

         if (lvt_6_1_.hasOpening[Direction.EAST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 7, 1, 11, 7, 2, 12);
         }

         if (lvt_9_1_.hasOpening[Direction.SOUTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 3, 5, 0, 4, 6, 0);
         }

         if (lvt_9_1_.hasOpening[Direction.EAST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 7, 5, 3, 7, 6, 4);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 4, 2, 6, 4, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 1, 2, 6, 3, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 1, 5, 6, 3, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         }

         if (lvt_9_1_.hasOpening[Direction.WEST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 0, 5, 3, 0, 6, 4);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 4, 2, 2, 4, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 2, 1, 3, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 5, 1, 3, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         }

         if (lvt_8_1_.hasOpening[Direction.NORTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 3, 5, 15, 4, 6, 15);
         }

         if (lvt_8_1_.hasOpening[Direction.WEST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 0, 5, 11, 0, 6, 12);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 4, 10, 2, 4, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 10, 1, 3, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 13, 1, 3, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         }

         if (lvt_8_1_.hasOpening[Direction.EAST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 7, 5, 11, 7, 6, 12);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 4, 10, 6, 4, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 1, 10, 6, 3, 10, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 1, 13, 6, 3, 13, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         }

         return true;
      }
   }

   public static class DoubleXYRoom extends OceanMonumentPieces.Piece {
      public DoubleXYRoom(Direction p_i50659_1_, OceanMonumentPieces.RoomDefinition p_i50659_2_) {
         super(IStructurePieceType.OMDXYR, 1, p_i50659_1_, p_i50659_2_, 2, 2, 1);
      }

      public DoubleXYRoom(TemplateManager p_i50660_1_, CompoundNBT p_i50660_2_) {
         super(IStructurePieceType.OMDXYR, p_i50660_2_);
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         OceanMonumentPieces.RoomDefinition lvt_6_1_ = this.roomDefinition.connections[Direction.EAST.getIndex()];
         OceanMonumentPieces.RoomDefinition lvt_7_1_ = this.roomDefinition;
         OceanMonumentPieces.RoomDefinition lvt_8_1_ = lvt_7_1_.connections[Direction.UP.getIndex()];
         OceanMonumentPieces.RoomDefinition lvt_9_1_ = lvt_6_1_.connections[Direction.UP.getIndex()];
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_225577_1_, p_225577_4_, 8, 0, lvt_6_1_.hasOpening[Direction.DOWN.getIndex()]);
            this.generateDefaultFloor(p_225577_1_, p_225577_4_, 0, 0, lvt_7_1_.hasOpening[Direction.DOWN.getIndex()]);
         }

         if (lvt_8_1_.connections[Direction.UP.getIndex()] == null) {
            this.generateBoxOnFillOnly(p_225577_1_, p_225577_4_, 1, 8, 1, 7, 8, 6, ROUGH_PRISMARINE);
         }

         if (lvt_9_1_.connections[Direction.UP.getIndex()] == null) {
            this.generateBoxOnFillOnly(p_225577_1_, p_225577_4_, 8, 8, 1, 14, 8, 6, ROUGH_PRISMARINE);
         }

         for(int lvt_10_1_ = 1; lvt_10_1_ <= 7; ++lvt_10_1_) {
            BlockState lvt_11_1_ = BRICKS_PRISMARINE;
            if (lvt_10_1_ == 2 || lvt_10_1_ == 6) {
               lvt_11_1_ = ROUGH_PRISMARINE;
            }

            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, lvt_10_1_, 0, 0, lvt_10_1_, 7, lvt_11_1_, lvt_11_1_, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 15, lvt_10_1_, 0, 15, lvt_10_1_, 7, lvt_11_1_, lvt_11_1_, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, lvt_10_1_, 0, 15, lvt_10_1_, 0, lvt_11_1_, lvt_11_1_, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, lvt_10_1_, 7, 14, lvt_10_1_, 7, lvt_11_1_, lvt_11_1_, false);
         }

         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 1, 3, 2, 7, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 1, 2, 4, 7, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 1, 5, 4, 7, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 13, 1, 3, 13, 7, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 11, 1, 2, 12, 7, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 11, 1, 5, 12, 7, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 1, 3, 5, 3, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 10, 1, 3, 10, 3, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 7, 2, 10, 7, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 5, 2, 5, 7, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 10, 5, 2, 10, 7, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 5, 5, 5, 7, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 10, 5, 5, 10, 7, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 6, 6, 2, p_225577_4_);
         this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 9, 6, 2, p_225577_4_);
         this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 6, 6, 5, p_225577_4_);
         this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 9, 6, 5, p_225577_4_);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 4, 3, 6, 4, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 9, 4, 3, 10, 4, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.setBlockState(p_225577_1_, SEA_LANTERN, 5, 4, 2, p_225577_4_);
         this.setBlockState(p_225577_1_, SEA_LANTERN, 5, 4, 5, p_225577_4_);
         this.setBlockState(p_225577_1_, SEA_LANTERN, 10, 4, 2, p_225577_4_);
         this.setBlockState(p_225577_1_, SEA_LANTERN, 10, 4, 5, p_225577_4_);
         if (lvt_7_1_.hasOpening[Direction.SOUTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 3, 1, 0, 4, 2, 0);
         }

         if (lvt_7_1_.hasOpening[Direction.NORTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 3, 1, 7, 4, 2, 7);
         }

         if (lvt_7_1_.hasOpening[Direction.WEST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 0, 1, 3, 0, 2, 4);
         }

         if (lvt_6_1_.hasOpening[Direction.SOUTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 11, 1, 0, 12, 2, 0);
         }

         if (lvt_6_1_.hasOpening[Direction.NORTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 11, 1, 7, 12, 2, 7);
         }

         if (lvt_6_1_.hasOpening[Direction.EAST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 15, 1, 3, 15, 2, 4);
         }

         if (lvt_8_1_.hasOpening[Direction.SOUTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 3, 5, 0, 4, 6, 0);
         }

         if (lvt_8_1_.hasOpening[Direction.NORTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 3, 5, 7, 4, 6, 7);
         }

         if (lvt_8_1_.hasOpening[Direction.WEST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 0, 5, 3, 0, 6, 4);
         }

         if (lvt_9_1_.hasOpening[Direction.SOUTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 11, 5, 0, 12, 6, 0);
         }

         if (lvt_9_1_.hasOpening[Direction.NORTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 11, 5, 7, 12, 6, 7);
         }

         if (lvt_9_1_.hasOpening[Direction.EAST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 15, 5, 3, 15, 6, 4);
         }

         return true;
      }
   }

   public static class DoubleZRoom extends OceanMonumentPieces.Piece {
      public DoubleZRoom(Direction p_i50653_1_, OceanMonumentPieces.RoomDefinition p_i50653_2_) {
         super(IStructurePieceType.OMDZR, 1, p_i50653_1_, p_i50653_2_, 1, 1, 2);
      }

      public DoubleZRoom(TemplateManager p_i50654_1_, CompoundNBT p_i50654_2_) {
         super(IStructurePieceType.OMDZR, p_i50654_2_);
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         OceanMonumentPieces.RoomDefinition lvt_6_1_ = this.roomDefinition.connections[Direction.NORTH.getIndex()];
         OceanMonumentPieces.RoomDefinition lvt_7_1_ = this.roomDefinition;
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_225577_1_, p_225577_4_, 0, 8, lvt_6_1_.hasOpening[Direction.DOWN.getIndex()]);
            this.generateDefaultFloor(p_225577_1_, p_225577_4_, 0, 0, lvt_7_1_.hasOpening[Direction.DOWN.getIndex()]);
         }

         if (lvt_7_1_.connections[Direction.UP.getIndex()] == null) {
            this.generateBoxOnFillOnly(p_225577_1_, p_225577_4_, 1, 4, 1, 6, 4, 7, ROUGH_PRISMARINE);
         }

         if (lvt_6_1_.connections[Direction.UP.getIndex()] == null) {
            this.generateBoxOnFillOnly(p_225577_1_, p_225577_4_, 1, 4, 8, 6, 4, 14, ROUGH_PRISMARINE);
         }

         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 0, 0, 3, 15, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 3, 0, 7, 3, 15, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 3, 0, 7, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 3, 15, 6, 3, 15, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 0, 2, 15, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 2, 0, 7, 2, 15, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 2, 0, 7, 2, 0, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 2, 15, 6, 2, 15, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 1, 0, 0, 1, 15, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 1, 0, 7, 1, 15, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 0, 7, 1, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 15, 6, 1, 15, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 1, 1, 1, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 1, 1, 6, 1, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 3, 1, 1, 3, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 3, 1, 6, 3, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 13, 1, 1, 14, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 1, 13, 6, 1, 14, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 3, 13, 1, 3, 14, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 3, 13, 6, 3, 14, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 1, 6, 2, 3, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 1, 6, 5, 3, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 1, 9, 2, 3, 9, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 1, 9, 5, 3, 9, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 2, 6, 4, 2, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 2, 9, 4, 2, 9, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 2, 7, 2, 2, 8, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 2, 7, 5, 2, 8, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.setBlockState(p_225577_1_, SEA_LANTERN, 2, 2, 5, p_225577_4_);
         this.setBlockState(p_225577_1_, SEA_LANTERN, 5, 2, 5, p_225577_4_);
         this.setBlockState(p_225577_1_, SEA_LANTERN, 2, 2, 10, p_225577_4_);
         this.setBlockState(p_225577_1_, SEA_LANTERN, 5, 2, 10, p_225577_4_);
         this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 2, 3, 5, p_225577_4_);
         this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 5, 3, 5, p_225577_4_);
         this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 2, 3, 10, p_225577_4_);
         this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, 5, 3, 10, p_225577_4_);
         if (lvt_7_1_.hasOpening[Direction.SOUTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 3, 1, 0, 4, 2, 0);
         }

         if (lvt_7_1_.hasOpening[Direction.EAST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 7, 1, 3, 7, 2, 4);
         }

         if (lvt_7_1_.hasOpening[Direction.WEST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 0, 1, 3, 0, 2, 4);
         }

         if (lvt_6_1_.hasOpening[Direction.NORTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 3, 1, 15, 4, 2, 15);
         }

         if (lvt_6_1_.hasOpening[Direction.WEST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 0, 1, 11, 0, 2, 12);
         }

         if (lvt_6_1_.hasOpening[Direction.EAST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 7, 1, 11, 7, 2, 12);
         }

         return true;
      }
   }

   public static class DoubleXRoom extends OceanMonumentPieces.Piece {
      public DoubleXRoom(Direction p_i50661_1_, OceanMonumentPieces.RoomDefinition p_i50661_2_) {
         super(IStructurePieceType.OMDXR, 1, p_i50661_1_, p_i50661_2_, 2, 1, 1);
      }

      public DoubleXRoom(TemplateManager p_i50662_1_, CompoundNBT p_i50662_2_) {
         super(IStructurePieceType.OMDXR, p_i50662_2_);
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         OceanMonumentPieces.RoomDefinition lvt_6_1_ = this.roomDefinition.connections[Direction.EAST.getIndex()];
         OceanMonumentPieces.RoomDefinition lvt_7_1_ = this.roomDefinition;
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_225577_1_, p_225577_4_, 8, 0, lvt_6_1_.hasOpening[Direction.DOWN.getIndex()]);
            this.generateDefaultFloor(p_225577_1_, p_225577_4_, 0, 0, lvt_7_1_.hasOpening[Direction.DOWN.getIndex()]);
         }

         if (lvt_7_1_.connections[Direction.UP.getIndex()] == null) {
            this.generateBoxOnFillOnly(p_225577_1_, p_225577_4_, 1, 4, 1, 7, 4, 6, ROUGH_PRISMARINE);
         }

         if (lvt_6_1_.connections[Direction.UP.getIndex()] == null) {
            this.generateBoxOnFillOnly(p_225577_1_, p_225577_4_, 8, 4, 1, 14, 4, 6, ROUGH_PRISMARINE);
         }

         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 0, 0, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 15, 3, 0, 15, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 3, 0, 15, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 3, 7, 14, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 0, 2, 7, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 15, 2, 0, 15, 2, 7, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 2, 0, 15, 2, 0, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 2, 7, 14, 2, 7, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 1, 0, 0, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 15, 1, 0, 15, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 0, 15, 1, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 7, 14, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 1, 0, 10, 1, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 2, 0, 9, 2, 3, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 3, 0, 10, 3, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.setBlockState(p_225577_1_, SEA_LANTERN, 6, 2, 3, p_225577_4_);
         this.setBlockState(p_225577_1_, SEA_LANTERN, 9, 2, 3, p_225577_4_);
         if (lvt_7_1_.hasOpening[Direction.SOUTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 3, 1, 0, 4, 2, 0);
         }

         if (lvt_7_1_.hasOpening[Direction.NORTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 3, 1, 7, 4, 2, 7);
         }

         if (lvt_7_1_.hasOpening[Direction.WEST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 0, 1, 3, 0, 2, 4);
         }

         if (lvt_6_1_.hasOpening[Direction.SOUTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 11, 1, 0, 12, 2, 0);
         }

         if (lvt_6_1_.hasOpening[Direction.NORTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 11, 1, 7, 12, 2, 7);
         }

         if (lvt_6_1_.hasOpening[Direction.EAST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 15, 1, 3, 15, 2, 4);
         }

         return true;
      }
   }

   public static class DoubleYRoom extends OceanMonumentPieces.Piece {
      public DoubleYRoom(Direction p_i50657_1_, OceanMonumentPieces.RoomDefinition p_i50657_2_) {
         super(IStructurePieceType.OMDYR, 1, p_i50657_1_, p_i50657_2_, 1, 2, 1);
      }

      public DoubleYRoom(TemplateManager p_i50658_1_, CompoundNBT p_i50658_2_) {
         super(IStructurePieceType.OMDYR, p_i50658_2_);
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_225577_1_, p_225577_4_, 0, 0, this.roomDefinition.hasOpening[Direction.DOWN.getIndex()]);
         }

         OceanMonumentPieces.RoomDefinition lvt_6_1_ = this.roomDefinition.connections[Direction.UP.getIndex()];
         if (lvt_6_1_.connections[Direction.UP.getIndex()] == null) {
            this.generateBoxOnFillOnly(p_225577_1_, p_225577_4_, 1, 8, 1, 6, 8, 6, ROUGH_PRISMARINE);
         }

         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 4, 0, 0, 4, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 4, 0, 7, 4, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 4, 0, 6, 4, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 4, 7, 6, 4, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 4, 1, 2, 4, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 4, 2, 1, 4, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 4, 1, 5, 4, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 4, 2, 6, 4, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 4, 5, 2, 4, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 4, 5, 1, 4, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 4, 5, 5, 4, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 4, 5, 6, 4, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         OceanMonumentPieces.RoomDefinition lvt_7_1_ = this.roomDefinition;

         for(int lvt_8_1_ = 1; lvt_8_1_ <= 5; lvt_8_1_ += 4) {
            int lvt_9_1_ = 0;
            if (lvt_7_1_.hasOpening[Direction.SOUTH.getIndex()]) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, lvt_8_1_, lvt_9_1_, 2, lvt_8_1_ + 2, lvt_9_1_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, lvt_8_1_, lvt_9_1_, 5, lvt_8_1_ + 2, lvt_9_1_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, lvt_8_1_ + 2, lvt_9_1_, 4, lvt_8_1_ + 2, lvt_9_1_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            } else {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, lvt_8_1_, lvt_9_1_, 7, lvt_8_1_ + 2, lvt_9_1_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, lvt_8_1_ + 1, lvt_9_1_, 7, lvt_8_1_ + 1, lvt_9_1_, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            }

            lvt_9_1_ = 7;
            if (lvt_7_1_.hasOpening[Direction.NORTH.getIndex()]) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, lvt_8_1_, lvt_9_1_, 2, lvt_8_1_ + 2, lvt_9_1_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, lvt_8_1_, lvt_9_1_, 5, lvt_8_1_ + 2, lvt_9_1_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, lvt_8_1_ + 2, lvt_9_1_, 4, lvt_8_1_ + 2, lvt_9_1_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            } else {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, lvt_8_1_, lvt_9_1_, 7, lvt_8_1_ + 2, lvt_9_1_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, lvt_8_1_ + 1, lvt_9_1_, 7, lvt_8_1_ + 1, lvt_9_1_, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            }

            int lvt_10_1_ = 0;
            if (lvt_7_1_.hasOpening[Direction.WEST.getIndex()]) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_10_1_, lvt_8_1_, 2, lvt_10_1_, lvt_8_1_ + 2, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_10_1_, lvt_8_1_, 5, lvt_10_1_, lvt_8_1_ + 2, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_10_1_, lvt_8_1_ + 2, 3, lvt_10_1_, lvt_8_1_ + 2, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            } else {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_10_1_, lvt_8_1_, 0, lvt_10_1_, lvt_8_1_ + 2, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_10_1_, lvt_8_1_ + 1, 0, lvt_10_1_, lvt_8_1_ + 1, 7, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            }

            lvt_10_1_ = 7;
            if (lvt_7_1_.hasOpening[Direction.EAST.getIndex()]) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_10_1_, lvt_8_1_, 2, lvt_10_1_, lvt_8_1_ + 2, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_10_1_, lvt_8_1_, 5, lvt_10_1_, lvt_8_1_ + 2, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_10_1_, lvt_8_1_ + 2, 3, lvt_10_1_, lvt_8_1_ + 2, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            } else {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_10_1_, lvt_8_1_, 0, lvt_10_1_, lvt_8_1_ + 2, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_10_1_, lvt_8_1_ + 1, 0, lvt_10_1_, lvt_8_1_ + 1, 7, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            }

            lvt_7_1_ = lvt_6_1_;
         }

         return true;
      }
   }

   public static class SimpleTopRoom extends OceanMonumentPieces.Piece {
      public SimpleTopRoom(Direction p_i50644_1_, OceanMonumentPieces.RoomDefinition p_i50644_2_) {
         super(IStructurePieceType.OMSIMPLET, 1, p_i50644_1_, p_i50644_2_, 1, 1, 1);
      }

      public SimpleTopRoom(TemplateManager p_i50645_1_, CompoundNBT p_i50645_2_) {
         super(IStructurePieceType.OMSIMPLET, p_i50645_2_);
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_225577_1_, p_225577_4_, 0, 0, this.roomDefinition.hasOpening[Direction.DOWN.getIndex()]);
         }

         if (this.roomDefinition.connections[Direction.UP.getIndex()] == null) {
            this.generateBoxOnFillOnly(p_225577_1_, p_225577_4_, 1, 4, 1, 6, 4, 6, ROUGH_PRISMARINE);
         }

         for(int lvt_6_1_ = 1; lvt_6_1_ <= 6; ++lvt_6_1_) {
            for(int lvt_7_1_ = 1; lvt_7_1_ <= 6; ++lvt_7_1_) {
               if (p_225577_3_.nextInt(3) != 0) {
                  int lvt_8_1_ = 2 + (p_225577_3_.nextInt(4) == 0 ? 0 : 1);
                  BlockState lvt_9_1_ = Blocks.WET_SPONGE.getDefaultState();
                  this.fillWithBlocks(p_225577_1_, p_225577_4_, lvt_6_1_, lvt_8_1_, lvt_7_1_, lvt_6_1_, 3, lvt_7_1_, lvt_9_1_, lvt_9_1_, false);
               }
            }
         }

         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 1, 0, 0, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 1, 0, 7, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 0, 6, 1, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 7, 6, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 0, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 2, 0, 7, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 2, 0, 6, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 2, 7, 6, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 0, 0, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 3, 0, 7, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 3, 0, 6, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 3, 7, 6, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 1, 3, 0, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 1, 3, 7, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 1, 0, 4, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 1, 7, 4, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
         if (this.roomDefinition.hasOpening[Direction.SOUTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 3, 1, 0, 4, 2, 0);
         }

         return true;
      }
   }

   public static class SimpleRoom extends OceanMonumentPieces.Piece {
      private int mainDesign;

      public SimpleRoom(Direction p_i45587_1_, OceanMonumentPieces.RoomDefinition p_i45587_2_, Random p_i45587_3_) {
         super(IStructurePieceType.OMSIMPLE, 1, p_i45587_1_, p_i45587_2_, 1, 1, 1);
         this.mainDesign = p_i45587_3_.nextInt(3);
      }

      public SimpleRoom(TemplateManager p_i50646_1_, CompoundNBT p_i50646_2_) {
         super(IStructurePieceType.OMSIMPLE, p_i50646_2_);
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(p_225577_1_, p_225577_4_, 0, 0, this.roomDefinition.hasOpening[Direction.DOWN.getIndex()]);
         }

         if (this.roomDefinition.connections[Direction.UP.getIndex()] == null) {
            this.generateBoxOnFillOnly(p_225577_1_, p_225577_4_, 1, 4, 1, 6, 4, 6, ROUGH_PRISMARINE);
         }

         boolean lvt_6_1_ = this.mainDesign != 0 && p_225577_3_.nextBoolean() && !this.roomDefinition.hasOpening[Direction.DOWN.getIndex()] && !this.roomDefinition.hasOpening[Direction.UP.getIndex()] && this.roomDefinition.countOpenings() > 1;
         if (this.mainDesign == 0) {
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 1, 0, 2, 1, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 0, 2, 3, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 0, 2, 2, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 2, 0, 2, 2, 0, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.setBlockState(p_225577_1_, SEA_LANTERN, 1, 2, 1, p_225577_4_);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 1, 0, 7, 1, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 3, 0, 7, 3, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 2, 0, 7, 2, 2, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 2, 0, 6, 2, 0, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.setBlockState(p_225577_1_, SEA_LANTERN, 6, 2, 1, p_225577_4_);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 1, 5, 2, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 5, 2, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 5, 0, 2, 7, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 2, 7, 2, 2, 7, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.setBlockState(p_225577_1_, SEA_LANTERN, 1, 2, 6, p_225577_4_);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 1, 5, 7, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 3, 5, 7, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 2, 5, 7, 2, 7, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 2, 7, 6, 2, 7, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.setBlockState(p_225577_1_, SEA_LANTERN, 6, 2, 6, p_225577_4_);
            if (this.roomDefinition.hasOpening[Direction.SOUTH.getIndex()]) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 3, 0, 4, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            } else {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 3, 0, 4, 3, 1, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 2, 0, 4, 2, 0, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 1, 0, 4, 1, 1, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            if (this.roomDefinition.hasOpening[Direction.NORTH.getIndex()]) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 3, 7, 4, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            } else {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 3, 6, 4, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 2, 7, 4, 2, 7, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 1, 6, 4, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            if (this.roomDefinition.hasOpening[Direction.WEST.getIndex()]) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 3, 0, 3, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            } else {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 3, 1, 3, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 3, 0, 2, 4, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 1, 3, 1, 1, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            if (this.roomDefinition.hasOpening[Direction.EAST.getIndex()]) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 3, 3, 7, 3, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            } else {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 3, 3, 7, 3, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 2, 3, 7, 2, 4, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 1, 3, 7, 1, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }
         } else if (this.mainDesign == 1) {
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 1, 2, 2, 3, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 2, 1, 5, 2, 3, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 1, 5, 5, 3, 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 1, 2, 5, 3, 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.setBlockState(p_225577_1_, SEA_LANTERN, 2, 2, 2, p_225577_4_);
            this.setBlockState(p_225577_1_, SEA_LANTERN, 2, 2, 5, p_225577_4_);
            this.setBlockState(p_225577_1_, SEA_LANTERN, 5, 2, 5, p_225577_4_);
            this.setBlockState(p_225577_1_, SEA_LANTERN, 5, 2, 2, p_225577_4_);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 1, 0, 1, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 1, 1, 0, 3, 1, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 1, 7, 1, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 1, 6, 0, 3, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 1, 7, 7, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 1, 6, 7, 3, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 1, 0, 7, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 1, 1, 7, 3, 1, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.setBlockState(p_225577_1_, ROUGH_PRISMARINE, 1, 2, 0, p_225577_4_);
            this.setBlockState(p_225577_1_, ROUGH_PRISMARINE, 0, 2, 1, p_225577_4_);
            this.setBlockState(p_225577_1_, ROUGH_PRISMARINE, 1, 2, 7, p_225577_4_);
            this.setBlockState(p_225577_1_, ROUGH_PRISMARINE, 0, 2, 6, p_225577_4_);
            this.setBlockState(p_225577_1_, ROUGH_PRISMARINE, 6, 2, 7, p_225577_4_);
            this.setBlockState(p_225577_1_, ROUGH_PRISMARINE, 7, 2, 6, p_225577_4_);
            this.setBlockState(p_225577_1_, ROUGH_PRISMARINE, 6, 2, 0, p_225577_4_);
            this.setBlockState(p_225577_1_, ROUGH_PRISMARINE, 7, 2, 1, p_225577_4_);
            if (!this.roomDefinition.hasOpening[Direction.SOUTH.getIndex()]) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 3, 0, 6, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 2, 0, 6, 2, 0, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 0, 6, 1, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            if (!this.roomDefinition.hasOpening[Direction.NORTH.getIndex()]) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 3, 7, 6, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 2, 7, 6, 2, 7, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 7, 6, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            if (!this.roomDefinition.hasOpening[Direction.WEST.getIndex()]) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 1, 0, 3, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 1, 0, 2, 6, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 1, 1, 0, 1, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            if (!this.roomDefinition.hasOpening[Direction.EAST.getIndex()]) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 3, 1, 7, 3, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 2, 1, 7, 2, 6, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 1, 1, 7, 1, 6, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }
         } else if (this.mainDesign == 2) {
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 1, 0, 0, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 1, 0, 7, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 0, 6, 1, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 7, 6, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 0, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 2, 0, 7, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 2, 0, 6, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 2, 7, 6, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 0, 0, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 3, 0, 7, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 3, 0, 6, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 3, 7, 6, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 1, 3, 0, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 1, 3, 7, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 1, 0, 4, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 1, 7, 4, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            if (this.roomDefinition.hasOpening[Direction.SOUTH.getIndex()]) {
               this.makeOpening(p_225577_1_, p_225577_4_, 3, 1, 0, 4, 2, 0);
            }

            if (this.roomDefinition.hasOpening[Direction.NORTH.getIndex()]) {
               this.makeOpening(p_225577_1_, p_225577_4_, 3, 1, 7, 4, 2, 7);
            }

            if (this.roomDefinition.hasOpening[Direction.WEST.getIndex()]) {
               this.makeOpening(p_225577_1_, p_225577_4_, 0, 1, 3, 0, 2, 4);
            }

            if (this.roomDefinition.hasOpening[Direction.EAST.getIndex()]) {
               this.makeOpening(p_225577_1_, p_225577_4_, 7, 1, 3, 7, 2, 4);
            }
         }

         if (lvt_6_1_) {
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 1, 3, 4, 1, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 2, 3, 4, 2, 4, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 3, 3, 3, 4, 3, 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         }

         return true;
      }
   }

   public static class EntryRoom extends OceanMonumentPieces.Piece {
      public EntryRoom(Direction p_i45592_1_, OceanMonumentPieces.RoomDefinition p_i45592_2_) {
         super(IStructurePieceType.OMENTRY, 1, p_i45592_1_, p_i45592_2_, 1, 1, 1);
      }

      public EntryRoom(TemplateManager p_i50652_1_, CompoundNBT p_i50652_2_) {
         super(IStructurePieceType.OMENTRY, p_i50652_2_);
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 3, 0, 2, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 3, 0, 7, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 2, 0, 1, 2, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 6, 2, 0, 7, 2, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 1, 0, 0, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 7, 1, 0, 7, 1, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 1, 7, 7, 3, 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 1, 1, 0, 2, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         this.fillWithBlocks(p_225577_1_, p_225577_4_, 5, 1, 0, 6, 3, 0, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         if (this.roomDefinition.hasOpening[Direction.NORTH.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 3, 1, 7, 4, 2, 7);
         }

         if (this.roomDefinition.hasOpening[Direction.WEST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 0, 1, 3, 1, 2, 4);
         }

         if (this.roomDefinition.hasOpening[Direction.EAST.getIndex()]) {
            this.makeOpening(p_225577_1_, p_225577_4_, 6, 1, 3, 7, 2, 4);
         }

         return true;
      }
   }

   public static class MonumentBuilding extends OceanMonumentPieces.Piece {
      private OceanMonumentPieces.RoomDefinition sourceRoom;
      private OceanMonumentPieces.RoomDefinition coreRoom;
      private final List<OceanMonumentPieces.Piece> childPieces = Lists.newArrayList();

      public MonumentBuilding(Random p_i45599_1_, int p_i45599_2_, int p_i45599_3_, Direction p_i45599_4_) {
         super(IStructurePieceType.OMB, 0);
         this.setCoordBaseMode(p_i45599_4_);
         Direction lvt_5_1_ = this.getCoordBaseMode();
         if (lvt_5_1_.getAxis() == Direction.Axis.Z) {
            this.boundingBox = new MutableBoundingBox(p_i45599_2_, 39, p_i45599_3_, p_i45599_2_ + 58 - 1, 61, p_i45599_3_ + 58 - 1);
         } else {
            this.boundingBox = new MutableBoundingBox(p_i45599_2_, 39, p_i45599_3_, p_i45599_2_ + 58 - 1, 61, p_i45599_3_ + 58 - 1);
         }

         List<OceanMonumentPieces.RoomDefinition> lvt_6_1_ = this.generateRoomGraph(p_i45599_1_);
         this.sourceRoom.claimed = true;
         this.childPieces.add(new OceanMonumentPieces.EntryRoom(lvt_5_1_, this.sourceRoom));
         this.childPieces.add(new OceanMonumentPieces.MonumentCoreRoom(lvt_5_1_, this.coreRoom));
         List<OceanMonumentPieces.IMonumentRoomFitHelper> lvt_7_1_ = Lists.newArrayList();
         lvt_7_1_.add(new OceanMonumentPieces.XYDoubleRoomFitHelper());
         lvt_7_1_.add(new OceanMonumentPieces.YZDoubleRoomFitHelper());
         lvt_7_1_.add(new OceanMonumentPieces.ZDoubleRoomFitHelper());
         lvt_7_1_.add(new OceanMonumentPieces.XDoubleRoomFitHelper());
         lvt_7_1_.add(new OceanMonumentPieces.YDoubleRoomFitHelper());
         lvt_7_1_.add(new OceanMonumentPieces.FitSimpleRoomTopHelper());
         lvt_7_1_.add(new OceanMonumentPieces.FitSimpleRoomHelper());
         Iterator var8 = lvt_6_1_.iterator();

         while(true) {
            while(true) {
               OceanMonumentPieces.RoomDefinition lvt_9_1_;
               do {
                  do {
                     if (!var8.hasNext()) {
                        int lvt_8_1_ = this.boundingBox.minY;
                        int lvt_9_2_ = this.getXWithOffset(9, 22);
                        int lvt_10_1_ = this.getZWithOffset(9, 22);
                        Iterator var18 = this.childPieces.iterator();

                        while(var18.hasNext()) {
                           OceanMonumentPieces.Piece lvt_12_1_ = (OceanMonumentPieces.Piece)var18.next();
                           lvt_12_1_.getBoundingBox().offset(lvt_9_2_, lvt_8_1_, lvt_10_1_);
                        }

                        MutableBoundingBox lvt_11_2_ = MutableBoundingBox.createProper(this.getXWithOffset(1, 1), this.getYWithOffset(1), this.getZWithOffset(1, 1), this.getXWithOffset(23, 21), this.getYWithOffset(8), this.getZWithOffset(23, 21));
                        MutableBoundingBox lvt_12_2_ = MutableBoundingBox.createProper(this.getXWithOffset(34, 1), this.getYWithOffset(1), this.getZWithOffset(34, 1), this.getXWithOffset(56, 21), this.getYWithOffset(8), this.getZWithOffset(56, 21));
                        MutableBoundingBox lvt_13_1_ = MutableBoundingBox.createProper(this.getXWithOffset(22, 22), this.getYWithOffset(13), this.getZWithOffset(22, 22), this.getXWithOffset(35, 35), this.getYWithOffset(17), this.getZWithOffset(35, 35));
                        int lvt_14_1_ = p_i45599_1_.nextInt();
                        this.childPieces.add(new OceanMonumentPieces.WingRoom(lvt_5_1_, lvt_11_2_, lvt_14_1_++));
                        this.childPieces.add(new OceanMonumentPieces.WingRoom(lvt_5_1_, lvt_12_2_, lvt_14_1_++));
                        this.childPieces.add(new OceanMonumentPieces.Penthouse(lvt_5_1_, lvt_13_1_));
                        return;
                     }

                     lvt_9_1_ = (OceanMonumentPieces.RoomDefinition)var8.next();
                  } while(lvt_9_1_.claimed);
               } while(lvt_9_1_.isSpecial());

               Iterator var10 = lvt_7_1_.iterator();

               while(var10.hasNext()) {
                  OceanMonumentPieces.IMonumentRoomFitHelper lvt_11_1_ = (OceanMonumentPieces.IMonumentRoomFitHelper)var10.next();
                  if (lvt_11_1_.fits(lvt_9_1_)) {
                     this.childPieces.add(lvt_11_1_.create(lvt_5_1_, lvt_9_1_, p_i45599_1_));
                     break;
                  }
               }
            }
         }
      }

      public MonumentBuilding(TemplateManager p_i50665_1_, CompoundNBT p_i50665_2_) {
         super(IStructurePieceType.OMB, p_i50665_2_);
      }

      private List<OceanMonumentPieces.RoomDefinition> generateRoomGraph(Random p_175836_1_) {
         OceanMonumentPieces.RoomDefinition[] lvt_2_1_ = new OceanMonumentPieces.RoomDefinition[75];

         int lvt_3_4_;
         int lvt_4_4_;
         boolean lvt_5_3_;
         int lvt_6_4_;
         for(lvt_3_4_ = 0; lvt_3_4_ < 5; ++lvt_3_4_) {
            for(lvt_4_4_ = 0; lvt_4_4_ < 4; ++lvt_4_4_) {
               lvt_5_3_ = false;
               lvt_6_4_ = getRoomIndex(lvt_3_4_, 0, lvt_4_4_);
               lvt_2_1_[lvt_6_4_] = new OceanMonumentPieces.RoomDefinition(lvt_6_4_);
            }
         }

         for(lvt_3_4_ = 0; lvt_3_4_ < 5; ++lvt_3_4_) {
            for(lvt_4_4_ = 0; lvt_4_4_ < 4; ++lvt_4_4_) {
               lvt_5_3_ = true;
               lvt_6_4_ = getRoomIndex(lvt_3_4_, 1, lvt_4_4_);
               lvt_2_1_[lvt_6_4_] = new OceanMonumentPieces.RoomDefinition(lvt_6_4_);
            }
         }

         for(lvt_3_4_ = 1; lvt_3_4_ < 4; ++lvt_3_4_) {
            for(lvt_4_4_ = 0; lvt_4_4_ < 2; ++lvt_4_4_) {
               lvt_5_3_ = true;
               lvt_6_4_ = getRoomIndex(lvt_3_4_, 2, lvt_4_4_);
               lvt_2_1_[lvt_6_4_] = new OceanMonumentPieces.RoomDefinition(lvt_6_4_);
            }
         }

         this.sourceRoom = lvt_2_1_[GRIDROOM_SOURCE_INDEX];

         int var8;
         int var9;
         int lvt_11_1_;
         int lvt_12_2_;
         int lvt_13_1_;
         for(lvt_3_4_ = 0; lvt_3_4_ < 5; ++lvt_3_4_) {
            for(lvt_4_4_ = 0; lvt_4_4_ < 5; ++lvt_4_4_) {
               for(int lvt_5_4_ = 0; lvt_5_4_ < 3; ++lvt_5_4_) {
                  lvt_6_4_ = getRoomIndex(lvt_3_4_, lvt_5_4_, lvt_4_4_);
                  if (lvt_2_1_[lvt_6_4_] != null) {
                     Direction[] var7 = Direction.values();
                     var8 = var7.length;

                     for(var9 = 0; var9 < var8; ++var9) {
                        Direction lvt_10_1_ = var7[var9];
                        lvt_11_1_ = lvt_3_4_ + lvt_10_1_.getXOffset();
                        lvt_12_2_ = lvt_5_4_ + lvt_10_1_.getYOffset();
                        lvt_13_1_ = lvt_4_4_ + lvt_10_1_.getZOffset();
                        if (lvt_11_1_ >= 0 && lvt_11_1_ < 5 && lvt_13_1_ >= 0 && lvt_13_1_ < 5 && lvt_12_2_ >= 0 && lvt_12_2_ < 3) {
                           int lvt_14_1_ = getRoomIndex(lvt_11_1_, lvt_12_2_, lvt_13_1_);
                           if (lvt_2_1_[lvt_14_1_] != null) {
                              if (lvt_13_1_ == lvt_4_4_) {
                                 lvt_2_1_[lvt_6_4_].setConnection(lvt_10_1_, lvt_2_1_[lvt_14_1_]);
                              } else {
                                 lvt_2_1_[lvt_6_4_].setConnection(lvt_10_1_.getOpposite(), lvt_2_1_[lvt_14_1_]);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         OceanMonumentPieces.RoomDefinition lvt_3_5_ = new OceanMonumentPieces.RoomDefinition(1003);
         OceanMonumentPieces.RoomDefinition lvt_4_5_ = new OceanMonumentPieces.RoomDefinition(1001);
         OceanMonumentPieces.RoomDefinition lvt_5_5_ = new OceanMonumentPieces.RoomDefinition(1002);
         lvt_2_1_[GRIDROOM_TOP_CONNECT_INDEX].setConnection(Direction.UP, lvt_3_5_);
         lvt_2_1_[GRIDROOM_LEFTWING_CONNECT_INDEX].setConnection(Direction.SOUTH, lvt_4_5_);
         lvt_2_1_[GRIDROOM_RIGHTWING_CONNECT_INDEX].setConnection(Direction.SOUTH, lvt_5_5_);
         lvt_3_5_.claimed = true;
         lvt_4_5_.claimed = true;
         lvt_5_5_.claimed = true;
         this.sourceRoom.isSource = true;
         this.coreRoom = lvt_2_1_[getRoomIndex(p_175836_1_.nextInt(4), 0, 2)];
         this.coreRoom.claimed = true;
         this.coreRoom.connections[Direction.EAST.getIndex()].claimed = true;
         this.coreRoom.connections[Direction.NORTH.getIndex()].claimed = true;
         this.coreRoom.connections[Direction.EAST.getIndex()].connections[Direction.NORTH.getIndex()].claimed = true;
         this.coreRoom.connections[Direction.UP.getIndex()].claimed = true;
         this.coreRoom.connections[Direction.EAST.getIndex()].connections[Direction.UP.getIndex()].claimed = true;
         this.coreRoom.connections[Direction.NORTH.getIndex()].connections[Direction.UP.getIndex()].claimed = true;
         this.coreRoom.connections[Direction.EAST.getIndex()].connections[Direction.NORTH.getIndex()].connections[Direction.UP.getIndex()].claimed = true;
         List<OceanMonumentPieces.RoomDefinition> lvt_6_5_ = Lists.newArrayList();
         OceanMonumentPieces.RoomDefinition[] var20 = lvt_2_1_;
         var8 = lvt_2_1_.length;

         for(var9 = 0; var9 < var8; ++var9) {
            OceanMonumentPieces.RoomDefinition lvt_10_2_ = var20[var9];
            if (lvt_10_2_ != null) {
               lvt_10_2_.updateOpenings();
               lvt_6_5_.add(lvt_10_2_);
            }
         }

         lvt_3_5_.updateOpenings();
         Collections.shuffle(lvt_6_5_, p_175836_1_);
         int lvt_7_1_ = 1;
         Iterator var22 = lvt_6_5_.iterator();

         label95:
         while(var22.hasNext()) {
            OceanMonumentPieces.RoomDefinition lvt_9_1_ = (OceanMonumentPieces.RoomDefinition)var22.next();
            int lvt_10_3_ = 0;
            lvt_11_1_ = 0;

            while(true) {
               while(true) {
                  do {
                     if (lvt_10_3_ >= 2 || lvt_11_1_ >= 5) {
                        continue label95;
                     }

                     ++lvt_11_1_;
                     lvt_12_2_ = p_175836_1_.nextInt(6);
                  } while(!lvt_9_1_.hasOpening[lvt_12_2_]);

                  lvt_13_1_ = Direction.byIndex(lvt_12_2_).getOpposite().getIndex();
                  lvt_9_1_.hasOpening[lvt_12_2_] = false;
                  lvt_9_1_.connections[lvt_12_2_].hasOpening[lvt_13_1_] = false;
                  if (lvt_9_1_.findSource(lvt_7_1_++) && lvt_9_1_.connections[lvt_12_2_].findSource(lvt_7_1_++)) {
                     ++lvt_10_3_;
                  } else {
                     lvt_9_1_.hasOpening[lvt_12_2_] = true;
                     lvt_9_1_.connections[lvt_12_2_].hasOpening[lvt_13_1_] = true;
                  }
               }
            }
         }

         lvt_6_5_.add(lvt_3_5_);
         lvt_6_5_.add(lvt_4_5_);
         lvt_6_5_.add(lvt_5_5_);
         return lvt_6_5_;
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         int lvt_6_1_ = Math.max(p_225577_1_.getSeaLevel(), 64) - this.boundingBox.minY;
         this.makeOpening(p_225577_1_, p_225577_4_, 0, 0, 0, 58, lvt_6_1_, 58);
         this.generateWing(false, 0, p_225577_1_, p_225577_3_, p_225577_4_);
         this.generateWing(true, 33, p_225577_1_, p_225577_3_, p_225577_4_);
         this.generateEntranceArchs(p_225577_1_, p_225577_3_, p_225577_4_);
         this.generateEntranceWall(p_225577_1_, p_225577_3_, p_225577_4_);
         this.generateRoofPiece(p_225577_1_, p_225577_3_, p_225577_4_);
         this.generateLowerWall(p_225577_1_, p_225577_3_, p_225577_4_);
         this.generateMiddleWall(p_225577_1_, p_225577_3_, p_225577_4_);
         this.generateUpperWall(p_225577_1_, p_225577_3_, p_225577_4_);

         int lvt_7_1_;
         label72:
         for(lvt_7_1_ = 0; lvt_7_1_ < 7; ++lvt_7_1_) {
            int lvt_8_1_ = 0;

            while(true) {
               while(true) {
                  if (lvt_8_1_ >= 7) {
                     continue label72;
                  }

                  if (lvt_8_1_ == 0 && lvt_7_1_ == 3) {
                     lvt_8_1_ = 6;
                  }

                  int lvt_9_1_ = lvt_7_1_ * 9;
                  int lvt_10_1_ = lvt_8_1_ * 9;

                  for(int lvt_11_1_ = 0; lvt_11_1_ < 4; ++lvt_11_1_) {
                     for(int lvt_12_1_ = 0; lvt_12_1_ < 4; ++lvt_12_1_) {
                        this.setBlockState(p_225577_1_, BRICKS_PRISMARINE, lvt_9_1_ + lvt_11_1_, 0, lvt_10_1_ + lvt_12_1_, p_225577_4_);
                        this.replaceAirAndLiquidDownwards(p_225577_1_, BRICKS_PRISMARINE, lvt_9_1_ + lvt_11_1_, -1, lvt_10_1_ + lvt_12_1_, p_225577_4_);
                     }
                  }

                  if (lvt_7_1_ != 0 && lvt_7_1_ != 6) {
                     lvt_8_1_ += 6;
                  } else {
                     ++lvt_8_1_;
                  }
               }
            }
         }

         for(lvt_7_1_ = 0; lvt_7_1_ < 5; ++lvt_7_1_) {
            this.makeOpening(p_225577_1_, p_225577_4_, -1 - lvt_7_1_, 0 + lvt_7_1_ * 2, -1 - lvt_7_1_, -1 - lvt_7_1_, 23, 58 + lvt_7_1_);
            this.makeOpening(p_225577_1_, p_225577_4_, 58 + lvt_7_1_, 0 + lvt_7_1_ * 2, -1 - lvt_7_1_, 58 + lvt_7_1_, 23, 58 + lvt_7_1_);
            this.makeOpening(p_225577_1_, p_225577_4_, 0 - lvt_7_1_, 0 + lvt_7_1_ * 2, -1 - lvt_7_1_, 57 + lvt_7_1_, 23, -1 - lvt_7_1_);
            this.makeOpening(p_225577_1_, p_225577_4_, 0 - lvt_7_1_, 0 + lvt_7_1_ * 2, 58 + lvt_7_1_, 57 + lvt_7_1_, 23, 58 + lvt_7_1_);
         }

         Iterator var13 = this.childPieces.iterator();

         while(var13.hasNext()) {
            OceanMonumentPieces.Piece lvt_8_2_ = (OceanMonumentPieces.Piece)var13.next();
            if (lvt_8_2_.getBoundingBox().intersectsWith(p_225577_4_)) {
               lvt_8_2_.func_225577_a_(p_225577_1_, p_225577_2_, p_225577_3_, p_225577_4_, p_225577_5_);
            }
         }

         return true;
      }

      private void generateWing(boolean p_175840_1_, int p_175840_2_, IWorld p_175840_3_, Random p_175840_4_, MutableBoundingBox p_175840_5_) {
         int lvt_6_1_ = true;
         if (this.doesChunkIntersect(p_175840_5_, p_175840_2_, 0, p_175840_2_ + 23, 20)) {
            this.fillWithBlocks(p_175840_3_, p_175840_5_, p_175840_2_ + 0, 0, 0, p_175840_2_ + 24, 0, 20, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.makeOpening(p_175840_3_, p_175840_5_, p_175840_2_ + 0, 1, 0, p_175840_2_ + 24, 10, 20);

            int lvt_7_2_;
            for(lvt_7_2_ = 0; lvt_7_2_ < 4; ++lvt_7_2_) {
               this.fillWithBlocks(p_175840_3_, p_175840_5_, p_175840_2_ + lvt_7_2_, lvt_7_2_ + 1, lvt_7_2_, p_175840_2_ + lvt_7_2_, lvt_7_2_ + 1, 20, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_175840_3_, p_175840_5_, p_175840_2_ + lvt_7_2_ + 7, lvt_7_2_ + 5, lvt_7_2_ + 7, p_175840_2_ + lvt_7_2_ + 7, lvt_7_2_ + 5, 20, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_175840_3_, p_175840_5_, p_175840_2_ + 17 - lvt_7_2_, lvt_7_2_ + 5, lvt_7_2_ + 7, p_175840_2_ + 17 - lvt_7_2_, lvt_7_2_ + 5, 20, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_175840_3_, p_175840_5_, p_175840_2_ + 24 - lvt_7_2_, lvt_7_2_ + 1, lvt_7_2_, p_175840_2_ + 24 - lvt_7_2_, lvt_7_2_ + 1, 20, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_175840_3_, p_175840_5_, p_175840_2_ + lvt_7_2_ + 1, lvt_7_2_ + 1, lvt_7_2_, p_175840_2_ + 23 - lvt_7_2_, lvt_7_2_ + 1, lvt_7_2_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_175840_3_, p_175840_5_, p_175840_2_ + lvt_7_2_ + 8, lvt_7_2_ + 5, lvt_7_2_ + 7, p_175840_2_ + 16 - lvt_7_2_, lvt_7_2_ + 5, lvt_7_2_ + 7, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            this.fillWithBlocks(p_175840_3_, p_175840_5_, p_175840_2_ + 4, 4, 4, p_175840_2_ + 6, 4, 20, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175840_3_, p_175840_5_, p_175840_2_ + 7, 4, 4, p_175840_2_ + 17, 4, 6, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175840_3_, p_175840_5_, p_175840_2_ + 18, 4, 4, p_175840_2_ + 20, 4, 20, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175840_3_, p_175840_5_, p_175840_2_ + 11, 8, 11, p_175840_2_ + 13, 8, 20, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.setBlockState(p_175840_3_, DOT_DECO_DATA, p_175840_2_ + 12, 9, 12, p_175840_5_);
            this.setBlockState(p_175840_3_, DOT_DECO_DATA, p_175840_2_ + 12, 9, 15, p_175840_5_);
            this.setBlockState(p_175840_3_, DOT_DECO_DATA, p_175840_2_ + 12, 9, 18, p_175840_5_);
            lvt_7_2_ = p_175840_2_ + (p_175840_1_ ? 19 : 5);
            int lvt_8_1_ = p_175840_2_ + (p_175840_1_ ? 5 : 19);

            int lvt_9_3_;
            for(lvt_9_3_ = 20; lvt_9_3_ >= 5; lvt_9_3_ -= 3) {
               this.setBlockState(p_175840_3_, DOT_DECO_DATA, lvt_7_2_, 5, lvt_9_3_, p_175840_5_);
            }

            for(lvt_9_3_ = 19; lvt_9_3_ >= 7; lvt_9_3_ -= 3) {
               this.setBlockState(p_175840_3_, DOT_DECO_DATA, lvt_8_1_, 5, lvt_9_3_, p_175840_5_);
            }

            for(lvt_9_3_ = 0; lvt_9_3_ < 4; ++lvt_9_3_) {
               int lvt_10_1_ = p_175840_1_ ? p_175840_2_ + 24 - (17 - lvt_9_3_ * 3) : p_175840_2_ + 17 - lvt_9_3_ * 3;
               this.setBlockState(p_175840_3_, DOT_DECO_DATA, lvt_10_1_, 5, 5, p_175840_5_);
            }

            this.setBlockState(p_175840_3_, DOT_DECO_DATA, lvt_8_1_, 5, 5, p_175840_5_);
            this.fillWithBlocks(p_175840_3_, p_175840_5_, p_175840_2_ + 11, 1, 12, p_175840_2_ + 13, 7, 12, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175840_3_, p_175840_5_, p_175840_2_ + 12, 1, 11, p_175840_2_ + 12, 7, 13, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         }

      }

      private void generateEntranceArchs(IWorld p_175839_1_, Random p_175839_2_, MutableBoundingBox p_175839_3_) {
         if (this.doesChunkIntersect(p_175839_3_, 22, 5, 35, 17)) {
            this.makeOpening(p_175839_1_, p_175839_3_, 25, 0, 0, 32, 8, 20);

            for(int lvt_4_1_ = 0; lvt_4_1_ < 4; ++lvt_4_1_) {
               this.fillWithBlocks(p_175839_1_, p_175839_3_, 24, 2, 5 + lvt_4_1_ * 4, 24, 4, 5 + lvt_4_1_ * 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_175839_1_, p_175839_3_, 22, 4, 5 + lvt_4_1_ * 4, 23, 4, 5 + lvt_4_1_ * 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.setBlockState(p_175839_1_, BRICKS_PRISMARINE, 25, 5, 5 + lvt_4_1_ * 4, p_175839_3_);
               this.setBlockState(p_175839_1_, BRICKS_PRISMARINE, 26, 6, 5 + lvt_4_1_ * 4, p_175839_3_);
               this.setBlockState(p_175839_1_, SEA_LANTERN, 26, 5, 5 + lvt_4_1_ * 4, p_175839_3_);
               this.fillWithBlocks(p_175839_1_, p_175839_3_, 33, 2, 5 + lvt_4_1_ * 4, 33, 4, 5 + lvt_4_1_ * 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_175839_1_, p_175839_3_, 34, 4, 5 + lvt_4_1_ * 4, 35, 4, 5 + lvt_4_1_ * 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.setBlockState(p_175839_1_, BRICKS_PRISMARINE, 32, 5, 5 + lvt_4_1_ * 4, p_175839_3_);
               this.setBlockState(p_175839_1_, BRICKS_PRISMARINE, 31, 6, 5 + lvt_4_1_ * 4, p_175839_3_);
               this.setBlockState(p_175839_1_, SEA_LANTERN, 31, 5, 5 + lvt_4_1_ * 4, p_175839_3_);
               this.fillWithBlocks(p_175839_1_, p_175839_3_, 27, 6, 5 + lvt_4_1_ * 4, 30, 6, 5 + lvt_4_1_ * 4, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            }
         }

      }

      private void generateEntranceWall(IWorld p_175837_1_, Random p_175837_2_, MutableBoundingBox p_175837_3_) {
         if (this.doesChunkIntersect(p_175837_3_, 15, 20, 42, 21)) {
            this.fillWithBlocks(p_175837_1_, p_175837_3_, 15, 0, 21, 42, 0, 21, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.makeOpening(p_175837_1_, p_175837_3_, 26, 1, 21, 31, 3, 21);
            this.fillWithBlocks(p_175837_1_, p_175837_3_, 21, 12, 21, 36, 12, 21, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175837_1_, p_175837_3_, 17, 11, 21, 40, 11, 21, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175837_1_, p_175837_3_, 16, 10, 21, 41, 10, 21, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175837_1_, p_175837_3_, 15, 7, 21, 42, 9, 21, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175837_1_, p_175837_3_, 16, 6, 21, 41, 6, 21, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175837_1_, p_175837_3_, 17, 5, 21, 40, 5, 21, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175837_1_, p_175837_3_, 21, 4, 21, 36, 4, 21, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175837_1_, p_175837_3_, 22, 3, 21, 26, 3, 21, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175837_1_, p_175837_3_, 31, 3, 21, 35, 3, 21, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175837_1_, p_175837_3_, 23, 2, 21, 25, 2, 21, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175837_1_, p_175837_3_, 32, 2, 21, 34, 2, 21, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175837_1_, p_175837_3_, 28, 4, 20, 29, 4, 21, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.setBlockState(p_175837_1_, BRICKS_PRISMARINE, 27, 3, 21, p_175837_3_);
            this.setBlockState(p_175837_1_, BRICKS_PRISMARINE, 30, 3, 21, p_175837_3_);
            this.setBlockState(p_175837_1_, BRICKS_PRISMARINE, 26, 2, 21, p_175837_3_);
            this.setBlockState(p_175837_1_, BRICKS_PRISMARINE, 31, 2, 21, p_175837_3_);
            this.setBlockState(p_175837_1_, BRICKS_PRISMARINE, 25, 1, 21, p_175837_3_);
            this.setBlockState(p_175837_1_, BRICKS_PRISMARINE, 32, 1, 21, p_175837_3_);

            int lvt_4_3_;
            for(lvt_4_3_ = 0; lvt_4_3_ < 7; ++lvt_4_3_) {
               this.setBlockState(p_175837_1_, DARK_PRISMARINE, 28 - lvt_4_3_, 6 + lvt_4_3_, 21, p_175837_3_);
               this.setBlockState(p_175837_1_, DARK_PRISMARINE, 29 + lvt_4_3_, 6 + lvt_4_3_, 21, p_175837_3_);
            }

            for(lvt_4_3_ = 0; lvt_4_3_ < 4; ++lvt_4_3_) {
               this.setBlockState(p_175837_1_, DARK_PRISMARINE, 28 - lvt_4_3_, 9 + lvt_4_3_, 21, p_175837_3_);
               this.setBlockState(p_175837_1_, DARK_PRISMARINE, 29 + lvt_4_3_, 9 + lvt_4_3_, 21, p_175837_3_);
            }

            this.setBlockState(p_175837_1_, DARK_PRISMARINE, 28, 12, 21, p_175837_3_);
            this.setBlockState(p_175837_1_, DARK_PRISMARINE, 29, 12, 21, p_175837_3_);

            for(lvt_4_3_ = 0; lvt_4_3_ < 3; ++lvt_4_3_) {
               this.setBlockState(p_175837_1_, DARK_PRISMARINE, 22 - lvt_4_3_ * 2, 8, 21, p_175837_3_);
               this.setBlockState(p_175837_1_, DARK_PRISMARINE, 22 - lvt_4_3_ * 2, 9, 21, p_175837_3_);
               this.setBlockState(p_175837_1_, DARK_PRISMARINE, 35 + lvt_4_3_ * 2, 8, 21, p_175837_3_);
               this.setBlockState(p_175837_1_, DARK_PRISMARINE, 35 + lvt_4_3_ * 2, 9, 21, p_175837_3_);
            }

            this.makeOpening(p_175837_1_, p_175837_3_, 15, 13, 21, 42, 15, 21);
            this.makeOpening(p_175837_1_, p_175837_3_, 15, 1, 21, 15, 6, 21);
            this.makeOpening(p_175837_1_, p_175837_3_, 16, 1, 21, 16, 5, 21);
            this.makeOpening(p_175837_1_, p_175837_3_, 17, 1, 21, 20, 4, 21);
            this.makeOpening(p_175837_1_, p_175837_3_, 21, 1, 21, 21, 3, 21);
            this.makeOpening(p_175837_1_, p_175837_3_, 22, 1, 21, 22, 2, 21);
            this.makeOpening(p_175837_1_, p_175837_3_, 23, 1, 21, 24, 1, 21);
            this.makeOpening(p_175837_1_, p_175837_3_, 42, 1, 21, 42, 6, 21);
            this.makeOpening(p_175837_1_, p_175837_3_, 41, 1, 21, 41, 5, 21);
            this.makeOpening(p_175837_1_, p_175837_3_, 37, 1, 21, 40, 4, 21);
            this.makeOpening(p_175837_1_, p_175837_3_, 36, 1, 21, 36, 3, 21);
            this.makeOpening(p_175837_1_, p_175837_3_, 33, 1, 21, 34, 1, 21);
            this.makeOpening(p_175837_1_, p_175837_3_, 35, 1, 21, 35, 2, 21);
         }

      }

      private void generateRoofPiece(IWorld p_175841_1_, Random p_175841_2_, MutableBoundingBox p_175841_3_) {
         if (this.doesChunkIntersect(p_175841_3_, 21, 21, 36, 36)) {
            this.fillWithBlocks(p_175841_1_, p_175841_3_, 21, 0, 22, 36, 0, 36, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.makeOpening(p_175841_1_, p_175841_3_, 21, 1, 22, 36, 23, 36);

            for(int lvt_4_1_ = 0; lvt_4_1_ < 4; ++lvt_4_1_) {
               this.fillWithBlocks(p_175841_1_, p_175841_3_, 21 + lvt_4_1_, 13 + lvt_4_1_, 21 + lvt_4_1_, 36 - lvt_4_1_, 13 + lvt_4_1_, 21 + lvt_4_1_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_175841_1_, p_175841_3_, 21 + lvt_4_1_, 13 + lvt_4_1_, 36 - lvt_4_1_, 36 - lvt_4_1_, 13 + lvt_4_1_, 36 - lvt_4_1_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_175841_1_, p_175841_3_, 21 + lvt_4_1_, 13 + lvt_4_1_, 22 + lvt_4_1_, 21 + lvt_4_1_, 13 + lvt_4_1_, 35 - lvt_4_1_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
               this.fillWithBlocks(p_175841_1_, p_175841_3_, 36 - lvt_4_1_, 13 + lvt_4_1_, 22 + lvt_4_1_, 36 - lvt_4_1_, 13 + lvt_4_1_, 35 - lvt_4_1_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            this.fillWithBlocks(p_175841_1_, p_175841_3_, 25, 16, 25, 32, 16, 32, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175841_1_, p_175841_3_, 25, 17, 25, 25, 19, 25, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_175841_1_, p_175841_3_, 32, 17, 25, 32, 19, 25, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_175841_1_, p_175841_3_, 25, 17, 32, 25, 19, 32, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_175841_1_, p_175841_3_, 32, 17, 32, 32, 19, 32, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.setBlockState(p_175841_1_, BRICKS_PRISMARINE, 26, 20, 26, p_175841_3_);
            this.setBlockState(p_175841_1_, BRICKS_PRISMARINE, 27, 21, 27, p_175841_3_);
            this.setBlockState(p_175841_1_, SEA_LANTERN, 27, 20, 27, p_175841_3_);
            this.setBlockState(p_175841_1_, BRICKS_PRISMARINE, 26, 20, 31, p_175841_3_);
            this.setBlockState(p_175841_1_, BRICKS_PRISMARINE, 27, 21, 30, p_175841_3_);
            this.setBlockState(p_175841_1_, SEA_LANTERN, 27, 20, 30, p_175841_3_);
            this.setBlockState(p_175841_1_, BRICKS_PRISMARINE, 31, 20, 31, p_175841_3_);
            this.setBlockState(p_175841_1_, BRICKS_PRISMARINE, 30, 21, 30, p_175841_3_);
            this.setBlockState(p_175841_1_, SEA_LANTERN, 30, 20, 30, p_175841_3_);
            this.setBlockState(p_175841_1_, BRICKS_PRISMARINE, 31, 20, 26, p_175841_3_);
            this.setBlockState(p_175841_1_, BRICKS_PRISMARINE, 30, 21, 27, p_175841_3_);
            this.setBlockState(p_175841_1_, SEA_LANTERN, 30, 20, 27, p_175841_3_);
            this.fillWithBlocks(p_175841_1_, p_175841_3_, 28, 21, 27, 29, 21, 27, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175841_1_, p_175841_3_, 27, 21, 28, 27, 21, 29, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175841_1_, p_175841_3_, 28, 21, 30, 29, 21, 30, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175841_1_, p_175841_3_, 30, 21, 28, 30, 21, 29, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         }

      }

      private void generateLowerWall(IWorld p_175835_1_, Random p_175835_2_, MutableBoundingBox p_175835_3_) {
         int lvt_4_3_;
         if (this.doesChunkIntersect(p_175835_3_, 0, 21, 6, 58)) {
            this.fillWithBlocks(p_175835_1_, p_175835_3_, 0, 0, 21, 6, 0, 57, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.makeOpening(p_175835_1_, p_175835_3_, 0, 1, 21, 6, 7, 57);
            this.fillWithBlocks(p_175835_1_, p_175835_3_, 4, 4, 21, 6, 4, 53, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);

            for(lvt_4_3_ = 0; lvt_4_3_ < 4; ++lvt_4_3_) {
               this.fillWithBlocks(p_175835_1_, p_175835_3_, lvt_4_3_, lvt_4_3_ + 1, 21, lvt_4_3_, lvt_4_3_ + 1, 57 - lvt_4_3_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            for(lvt_4_3_ = 23; lvt_4_3_ < 53; lvt_4_3_ += 3) {
               this.setBlockState(p_175835_1_, DOT_DECO_DATA, 5, 5, lvt_4_3_, p_175835_3_);
            }

            this.setBlockState(p_175835_1_, DOT_DECO_DATA, 5, 5, 52, p_175835_3_);

            for(lvt_4_3_ = 0; lvt_4_3_ < 4; ++lvt_4_3_) {
               this.fillWithBlocks(p_175835_1_, p_175835_3_, lvt_4_3_, lvt_4_3_ + 1, 21, lvt_4_3_, lvt_4_3_ + 1, 57 - lvt_4_3_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            this.fillWithBlocks(p_175835_1_, p_175835_3_, 4, 1, 52, 6, 3, 52, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175835_1_, p_175835_3_, 5, 1, 51, 5, 3, 53, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         }

         if (this.doesChunkIntersect(p_175835_3_, 51, 21, 58, 58)) {
            this.fillWithBlocks(p_175835_1_, p_175835_3_, 51, 0, 21, 57, 0, 57, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.makeOpening(p_175835_1_, p_175835_3_, 51, 1, 21, 57, 7, 57);
            this.fillWithBlocks(p_175835_1_, p_175835_3_, 51, 4, 21, 53, 4, 53, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);

            for(lvt_4_3_ = 0; lvt_4_3_ < 4; ++lvt_4_3_) {
               this.fillWithBlocks(p_175835_1_, p_175835_3_, 57 - lvt_4_3_, lvt_4_3_ + 1, 21, 57 - lvt_4_3_, lvt_4_3_ + 1, 57 - lvt_4_3_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            for(lvt_4_3_ = 23; lvt_4_3_ < 53; lvt_4_3_ += 3) {
               this.setBlockState(p_175835_1_, DOT_DECO_DATA, 52, 5, lvt_4_3_, p_175835_3_);
            }

            this.setBlockState(p_175835_1_, DOT_DECO_DATA, 52, 5, 52, p_175835_3_);
            this.fillWithBlocks(p_175835_1_, p_175835_3_, 51, 1, 52, 53, 3, 52, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175835_1_, p_175835_3_, 52, 1, 51, 52, 3, 53, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         }

         if (this.doesChunkIntersect(p_175835_3_, 0, 51, 57, 57)) {
            this.fillWithBlocks(p_175835_1_, p_175835_3_, 7, 0, 51, 50, 0, 57, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.makeOpening(p_175835_1_, p_175835_3_, 7, 1, 51, 50, 10, 57);

            for(lvt_4_3_ = 0; lvt_4_3_ < 4; ++lvt_4_3_) {
               this.fillWithBlocks(p_175835_1_, p_175835_3_, lvt_4_3_ + 1, lvt_4_3_ + 1, 57 - lvt_4_3_, 56 - lvt_4_3_, lvt_4_3_ + 1, 57 - lvt_4_3_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }
         }

      }

      private void generateMiddleWall(IWorld p_175842_1_, Random p_175842_2_, MutableBoundingBox p_175842_3_) {
         int lvt_4_6_;
         if (this.doesChunkIntersect(p_175842_3_, 7, 21, 13, 50)) {
            this.fillWithBlocks(p_175842_1_, p_175842_3_, 7, 0, 21, 13, 0, 50, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.makeOpening(p_175842_1_, p_175842_3_, 7, 1, 21, 13, 10, 50);
            this.fillWithBlocks(p_175842_1_, p_175842_3_, 11, 8, 21, 13, 8, 53, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);

            for(lvt_4_6_ = 0; lvt_4_6_ < 4; ++lvt_4_6_) {
               this.fillWithBlocks(p_175842_1_, p_175842_3_, lvt_4_6_ + 7, lvt_4_6_ + 5, 21, lvt_4_6_ + 7, lvt_4_6_ + 5, 54, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            for(lvt_4_6_ = 21; lvt_4_6_ <= 45; lvt_4_6_ += 3) {
               this.setBlockState(p_175842_1_, DOT_DECO_DATA, 12, 9, lvt_4_6_, p_175842_3_);
            }
         }

         if (this.doesChunkIntersect(p_175842_3_, 44, 21, 50, 54)) {
            this.fillWithBlocks(p_175842_1_, p_175842_3_, 44, 0, 21, 50, 0, 50, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.makeOpening(p_175842_1_, p_175842_3_, 44, 1, 21, 50, 10, 50);
            this.fillWithBlocks(p_175842_1_, p_175842_3_, 44, 8, 21, 46, 8, 53, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);

            for(lvt_4_6_ = 0; lvt_4_6_ < 4; ++lvt_4_6_) {
               this.fillWithBlocks(p_175842_1_, p_175842_3_, 50 - lvt_4_6_, lvt_4_6_ + 5, 21, 50 - lvt_4_6_, lvt_4_6_ + 5, 54, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            for(lvt_4_6_ = 21; lvt_4_6_ <= 45; lvt_4_6_ += 3) {
               this.setBlockState(p_175842_1_, DOT_DECO_DATA, 45, 9, lvt_4_6_, p_175842_3_);
            }
         }

         if (this.doesChunkIntersect(p_175842_3_, 8, 44, 49, 54)) {
            this.fillWithBlocks(p_175842_1_, p_175842_3_, 14, 0, 44, 43, 0, 50, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.makeOpening(p_175842_1_, p_175842_3_, 14, 1, 44, 43, 10, 50);

            for(lvt_4_6_ = 12; lvt_4_6_ <= 45; lvt_4_6_ += 3) {
               this.setBlockState(p_175842_1_, DOT_DECO_DATA, lvt_4_6_, 9, 45, p_175842_3_);
               this.setBlockState(p_175842_1_, DOT_DECO_DATA, lvt_4_6_, 9, 52, p_175842_3_);
               if (lvt_4_6_ == 12 || lvt_4_6_ == 18 || lvt_4_6_ == 24 || lvt_4_6_ == 33 || lvt_4_6_ == 39 || lvt_4_6_ == 45) {
                  this.setBlockState(p_175842_1_, DOT_DECO_DATA, lvt_4_6_, 9, 47, p_175842_3_);
                  this.setBlockState(p_175842_1_, DOT_DECO_DATA, lvt_4_6_, 9, 50, p_175842_3_);
                  this.setBlockState(p_175842_1_, DOT_DECO_DATA, lvt_4_6_, 10, 45, p_175842_3_);
                  this.setBlockState(p_175842_1_, DOT_DECO_DATA, lvt_4_6_, 10, 46, p_175842_3_);
                  this.setBlockState(p_175842_1_, DOT_DECO_DATA, lvt_4_6_, 10, 51, p_175842_3_);
                  this.setBlockState(p_175842_1_, DOT_DECO_DATA, lvt_4_6_, 10, 52, p_175842_3_);
                  this.setBlockState(p_175842_1_, DOT_DECO_DATA, lvt_4_6_, 11, 47, p_175842_3_);
                  this.setBlockState(p_175842_1_, DOT_DECO_DATA, lvt_4_6_, 11, 50, p_175842_3_);
                  this.setBlockState(p_175842_1_, DOT_DECO_DATA, lvt_4_6_, 12, 48, p_175842_3_);
                  this.setBlockState(p_175842_1_, DOT_DECO_DATA, lvt_4_6_, 12, 49, p_175842_3_);
               }
            }

            for(lvt_4_6_ = 0; lvt_4_6_ < 3; ++lvt_4_6_) {
               this.fillWithBlocks(p_175842_1_, p_175842_3_, 8 + lvt_4_6_, 5 + lvt_4_6_, 54, 49 - lvt_4_6_, 5 + lvt_4_6_, 54, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            }

            this.fillWithBlocks(p_175842_1_, p_175842_3_, 11, 8, 54, 46, 8, 54, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_175842_1_, p_175842_3_, 14, 8, 44, 43, 8, 53, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         }

      }

      private void generateUpperWall(IWorld p_175838_1_, Random p_175838_2_, MutableBoundingBox p_175838_3_) {
         int lvt_4_6_;
         if (this.doesChunkIntersect(p_175838_3_, 14, 21, 20, 43)) {
            this.fillWithBlocks(p_175838_1_, p_175838_3_, 14, 0, 21, 20, 0, 43, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.makeOpening(p_175838_1_, p_175838_3_, 14, 1, 22, 20, 14, 43);
            this.fillWithBlocks(p_175838_1_, p_175838_3_, 18, 12, 22, 20, 12, 39, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175838_1_, p_175838_3_, 18, 12, 21, 20, 12, 21, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);

            for(lvt_4_6_ = 0; lvt_4_6_ < 4; ++lvt_4_6_) {
               this.fillWithBlocks(p_175838_1_, p_175838_3_, lvt_4_6_ + 14, lvt_4_6_ + 9, 21, lvt_4_6_ + 14, lvt_4_6_ + 9, 43 - lvt_4_6_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            for(lvt_4_6_ = 23; lvt_4_6_ <= 39; lvt_4_6_ += 3) {
               this.setBlockState(p_175838_1_, DOT_DECO_DATA, 19, 13, lvt_4_6_, p_175838_3_);
            }
         }

         if (this.doesChunkIntersect(p_175838_3_, 37, 21, 43, 43)) {
            this.fillWithBlocks(p_175838_1_, p_175838_3_, 37, 0, 21, 43, 0, 43, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.makeOpening(p_175838_1_, p_175838_3_, 37, 1, 22, 43, 14, 43);
            this.fillWithBlocks(p_175838_1_, p_175838_3_, 37, 12, 22, 39, 12, 39, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175838_1_, p_175838_3_, 37, 12, 21, 39, 12, 21, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);

            for(lvt_4_6_ = 0; lvt_4_6_ < 4; ++lvt_4_6_) {
               this.fillWithBlocks(p_175838_1_, p_175838_3_, 43 - lvt_4_6_, lvt_4_6_ + 9, 21, 43 - lvt_4_6_, lvt_4_6_ + 9, 43 - lvt_4_6_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            for(lvt_4_6_ = 23; lvt_4_6_ <= 39; lvt_4_6_ += 3) {
               this.setBlockState(p_175838_1_, DOT_DECO_DATA, 38, 13, lvt_4_6_, p_175838_3_);
            }
         }

         if (this.doesChunkIntersect(p_175838_3_, 15, 37, 42, 43)) {
            this.fillWithBlocks(p_175838_1_, p_175838_3_, 21, 0, 37, 36, 0, 43, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.makeOpening(p_175838_1_, p_175838_3_, 21, 1, 37, 36, 14, 43);
            this.fillWithBlocks(p_175838_1_, p_175838_3_, 21, 12, 37, 36, 12, 39, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);

            for(lvt_4_6_ = 0; lvt_4_6_ < 4; ++lvt_4_6_) {
               this.fillWithBlocks(p_175838_1_, p_175838_3_, 15 + lvt_4_6_, lvt_4_6_ + 9, 43 - lvt_4_6_, 42 - lvt_4_6_, lvt_4_6_ + 9, 43 - lvt_4_6_, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            }

            for(lvt_4_6_ = 21; lvt_4_6_ <= 36; lvt_4_6_ += 3) {
               this.setBlockState(p_175838_1_, DOT_DECO_DATA, lvt_4_6_, 13, 38, p_175838_3_);
            }
         }

      }
   }

   public abstract static class Piece extends StructurePiece {
      protected static final BlockState ROUGH_PRISMARINE;
      protected static final BlockState BRICKS_PRISMARINE;
      protected static final BlockState DARK_PRISMARINE;
      protected static final BlockState DOT_DECO_DATA;
      protected static final BlockState SEA_LANTERN;
      protected static final BlockState WATER;
      protected static final Set<Block> field_212180_g;
      protected static final int GRIDROOM_SOURCE_INDEX;
      protected static final int GRIDROOM_TOP_CONNECT_INDEX;
      protected static final int GRIDROOM_LEFTWING_CONNECT_INDEX;
      protected static final int GRIDROOM_RIGHTWING_CONNECT_INDEX;
      protected OceanMonumentPieces.RoomDefinition roomDefinition;

      protected static final int getRoomIndex(int p_175820_0_, int p_175820_1_, int p_175820_2_) {
         return p_175820_1_ * 25 + p_175820_2_ * 5 + p_175820_0_;
      }

      public Piece(IStructurePieceType p_i50647_1_, int p_i50647_2_) {
         super(p_i50647_1_, p_i50647_2_);
      }

      public Piece(IStructurePieceType p_i50648_1_, Direction p_i50648_2_, MutableBoundingBox p_i50648_3_) {
         super(p_i50648_1_, 1);
         this.setCoordBaseMode(p_i50648_2_);
         this.boundingBox = p_i50648_3_;
      }

      protected Piece(IStructurePieceType p_i50649_1_, int p_i50649_2_, Direction p_i50649_3_, OceanMonumentPieces.RoomDefinition p_i50649_4_, int p_i50649_5_, int p_i50649_6_, int p_i50649_7_) {
         super(p_i50649_1_, p_i50649_2_);
         this.setCoordBaseMode(p_i50649_3_);
         this.roomDefinition = p_i50649_4_;
         int lvt_8_1_ = p_i50649_4_.index;
         int lvt_9_1_ = lvt_8_1_ % 5;
         int lvt_10_1_ = lvt_8_1_ / 5 % 5;
         int lvt_11_1_ = lvt_8_1_ / 25;
         if (p_i50649_3_ != Direction.NORTH && p_i50649_3_ != Direction.SOUTH) {
            this.boundingBox = new MutableBoundingBox(0, 0, 0, p_i50649_7_ * 8 - 1, p_i50649_6_ * 4 - 1, p_i50649_5_ * 8 - 1);
         } else {
            this.boundingBox = new MutableBoundingBox(0, 0, 0, p_i50649_5_ * 8 - 1, p_i50649_6_ * 4 - 1, p_i50649_7_ * 8 - 1);
         }

         switch(p_i50649_3_) {
         case NORTH:
            this.boundingBox.offset(lvt_9_1_ * 8, lvt_11_1_ * 4, -(lvt_10_1_ + p_i50649_7_) * 8 + 1);
            break;
         case SOUTH:
            this.boundingBox.offset(lvt_9_1_ * 8, lvt_11_1_ * 4, lvt_10_1_ * 8);
            break;
         case WEST:
            this.boundingBox.offset(-(lvt_10_1_ + p_i50649_7_) * 8 + 1, lvt_11_1_ * 4, lvt_9_1_ * 8);
            break;
         default:
            this.boundingBox.offset(lvt_10_1_ * 8, lvt_11_1_ * 4, lvt_9_1_ * 8);
         }

      }

      public Piece(IStructurePieceType p_i50650_1_, CompoundNBT p_i50650_2_) {
         super(p_i50650_1_, p_i50650_2_);
      }

      protected void readAdditional(CompoundNBT p_143011_1_) {
      }

      protected void makeOpening(IWorld p_209179_1_, MutableBoundingBox p_209179_2_, int p_209179_3_, int p_209179_4_, int p_209179_5_, int p_209179_6_, int p_209179_7_, int p_209179_8_) {
         for(int lvt_9_1_ = p_209179_4_; lvt_9_1_ <= p_209179_7_; ++lvt_9_1_) {
            for(int lvt_10_1_ = p_209179_3_; lvt_10_1_ <= p_209179_6_; ++lvt_10_1_) {
               for(int lvt_11_1_ = p_209179_5_; lvt_11_1_ <= p_209179_8_; ++lvt_11_1_) {
                  BlockState lvt_12_1_ = this.getBlockStateFromPos(p_209179_1_, lvt_10_1_, lvt_9_1_, lvt_11_1_, p_209179_2_);
                  if (!field_212180_g.contains(lvt_12_1_.getBlock())) {
                     if (this.getYWithOffset(lvt_9_1_) >= p_209179_1_.getSeaLevel() && lvt_12_1_ != WATER) {
                        this.setBlockState(p_209179_1_, Blocks.AIR.getDefaultState(), lvt_10_1_, lvt_9_1_, lvt_11_1_, p_209179_2_);
                     } else {
                        this.setBlockState(p_209179_1_, WATER, lvt_10_1_, lvt_9_1_, lvt_11_1_, p_209179_2_);
                     }
                  }
               }
            }
         }

      }

      protected void generateDefaultFloor(IWorld p_175821_1_, MutableBoundingBox p_175821_2_, int p_175821_3_, int p_175821_4_, boolean p_175821_5_) {
         if (p_175821_5_) {
            this.fillWithBlocks(p_175821_1_, p_175821_2_, p_175821_3_ + 0, 0, p_175821_4_ + 0, p_175821_3_ + 2, 0, p_175821_4_ + 8 - 1, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175821_1_, p_175821_2_, p_175821_3_ + 5, 0, p_175821_4_ + 0, p_175821_3_ + 8 - 1, 0, p_175821_4_ + 8 - 1, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175821_1_, p_175821_2_, p_175821_3_ + 3, 0, p_175821_4_ + 0, p_175821_3_ + 4, 0, p_175821_4_ + 2, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175821_1_, p_175821_2_, p_175821_3_ + 3, 0, p_175821_4_ + 5, p_175821_3_ + 4, 0, p_175821_4_ + 8 - 1, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
            this.fillWithBlocks(p_175821_1_, p_175821_2_, p_175821_3_ + 3, 0, p_175821_4_ + 2, p_175821_3_ + 4, 0, p_175821_4_ + 2, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_175821_1_, p_175821_2_, p_175821_3_ + 3, 0, p_175821_4_ + 5, p_175821_3_ + 4, 0, p_175821_4_ + 5, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_175821_1_, p_175821_2_, p_175821_3_ + 2, 0, p_175821_4_ + 3, p_175821_3_ + 2, 0, p_175821_4_ + 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
            this.fillWithBlocks(p_175821_1_, p_175821_2_, p_175821_3_ + 5, 0, p_175821_4_ + 3, p_175821_3_ + 5, 0, p_175821_4_ + 4, BRICKS_PRISMARINE, BRICKS_PRISMARINE, false);
         } else {
            this.fillWithBlocks(p_175821_1_, p_175821_2_, p_175821_3_ + 0, 0, p_175821_4_ + 0, p_175821_3_ + 8 - 1, 0, p_175821_4_ + 8 - 1, ROUGH_PRISMARINE, ROUGH_PRISMARINE, false);
         }

      }

      protected void generateBoxOnFillOnly(IWorld p_175819_1_, MutableBoundingBox p_175819_2_, int p_175819_3_, int p_175819_4_, int p_175819_5_, int p_175819_6_, int p_175819_7_, int p_175819_8_, BlockState p_175819_9_) {
         for(int lvt_10_1_ = p_175819_4_; lvt_10_1_ <= p_175819_7_; ++lvt_10_1_) {
            for(int lvt_11_1_ = p_175819_3_; lvt_11_1_ <= p_175819_6_; ++lvt_11_1_) {
               for(int lvt_12_1_ = p_175819_5_; lvt_12_1_ <= p_175819_8_; ++lvt_12_1_) {
                  if (this.getBlockStateFromPos(p_175819_1_, lvt_11_1_, lvt_10_1_, lvt_12_1_, p_175819_2_) == WATER) {
                     this.setBlockState(p_175819_1_, p_175819_9_, lvt_11_1_, lvt_10_1_, lvt_12_1_, p_175819_2_);
                  }
               }
            }
         }

      }

      protected boolean doesChunkIntersect(MutableBoundingBox p_175818_1_, int p_175818_2_, int p_175818_3_, int p_175818_4_, int p_175818_5_) {
         int lvt_6_1_ = this.getXWithOffset(p_175818_2_, p_175818_3_);
         int lvt_7_1_ = this.getZWithOffset(p_175818_2_, p_175818_3_);
         int lvt_8_1_ = this.getXWithOffset(p_175818_4_, p_175818_5_);
         int lvt_9_1_ = this.getZWithOffset(p_175818_4_, p_175818_5_);
         return p_175818_1_.intersectsWith(Math.min(lvt_6_1_, lvt_8_1_), Math.min(lvt_7_1_, lvt_9_1_), Math.max(lvt_6_1_, lvt_8_1_), Math.max(lvt_7_1_, lvt_9_1_));
      }

      protected boolean spawnElder(IWorld p_175817_1_, MutableBoundingBox p_175817_2_, int p_175817_3_, int p_175817_4_, int p_175817_5_) {
         int lvt_6_1_ = this.getXWithOffset(p_175817_3_, p_175817_5_);
         int lvt_7_1_ = this.getYWithOffset(p_175817_4_);
         int lvt_8_1_ = this.getZWithOffset(p_175817_3_, p_175817_5_);
         if (p_175817_2_.isVecInside(new BlockPos(lvt_6_1_, lvt_7_1_, lvt_8_1_))) {
            ElderGuardianEntity lvt_9_1_ = (ElderGuardianEntity)EntityType.ELDER_GUARDIAN.create(p_175817_1_.getWorld());
            lvt_9_1_.heal(lvt_9_1_.getMaxHealth());
            lvt_9_1_.setLocationAndAngles((double)lvt_6_1_ + 0.5D, (double)lvt_7_1_, (double)lvt_8_1_ + 0.5D, 0.0F, 0.0F);
            lvt_9_1_.onInitialSpawn(p_175817_1_, p_175817_1_.getDifficultyForLocation(new BlockPos(lvt_9_1_)), SpawnReason.STRUCTURE, (ILivingEntityData)null, (CompoundNBT)null);
            p_175817_1_.addEntity(lvt_9_1_);
            return true;
         } else {
            return false;
         }
      }

      static {
         ROUGH_PRISMARINE = Blocks.PRISMARINE.getDefaultState();
         BRICKS_PRISMARINE = Blocks.PRISMARINE_BRICKS.getDefaultState();
         DARK_PRISMARINE = Blocks.DARK_PRISMARINE.getDefaultState();
         DOT_DECO_DATA = BRICKS_PRISMARINE;
         SEA_LANTERN = Blocks.SEA_LANTERN.getDefaultState();
         WATER = Blocks.WATER.getDefaultState();
         field_212180_g = ImmutableSet.builder().add(Blocks.ICE).add(Blocks.PACKED_ICE).add(Blocks.BLUE_ICE).add(WATER.getBlock()).build();
         GRIDROOM_SOURCE_INDEX = getRoomIndex(2, 0, 0);
         GRIDROOM_TOP_CONNECT_INDEX = getRoomIndex(2, 2, 0);
         GRIDROOM_LEFTWING_CONNECT_INDEX = getRoomIndex(0, 1, 0);
         GRIDROOM_RIGHTWING_CONNECT_INDEX = getRoomIndex(4, 1, 0);
      }
   }
}
