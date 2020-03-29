package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;

public class MineshaftPieces {
   private static MineshaftPieces.Piece createRandomShaftPiece(List<StructurePiece> p_189940_0_, Random p_189940_1_, int p_189940_2_, int p_189940_3_, int p_189940_4_, @Nullable Direction p_189940_5_, int p_189940_6_, MineshaftStructure.Type p_189940_7_) {
      int i = p_189940_1_.nextInt(100);
      MutableBoundingBox mutableboundingbox1;
      if (i >= 80) {
         mutableboundingbox1 = MineshaftPieces.Cross.findCrossing(p_189940_0_, p_189940_1_, p_189940_2_, p_189940_3_, p_189940_4_, p_189940_5_);
         if (mutableboundingbox1 != null) {
            return new MineshaftPieces.Cross(p_189940_6_, mutableboundingbox1, p_189940_5_, p_189940_7_);
         }
      } else if (i >= 70) {
         mutableboundingbox1 = MineshaftPieces.Stairs.findStairs(p_189940_0_, p_189940_1_, p_189940_2_, p_189940_3_, p_189940_4_, p_189940_5_);
         if (mutableboundingbox1 != null) {
            return new MineshaftPieces.Stairs(p_189940_6_, mutableboundingbox1, p_189940_5_, p_189940_7_);
         }
      } else {
         mutableboundingbox1 = MineshaftPieces.Corridor.findCorridorSize(p_189940_0_, p_189940_1_, p_189940_2_, p_189940_3_, p_189940_4_, p_189940_5_);
         if (mutableboundingbox1 != null) {
            return new MineshaftPieces.Corridor(p_189940_6_, p_189940_1_, mutableboundingbox1, p_189940_5_, p_189940_7_);
         }
      }

      return null;
   }

   private static MineshaftPieces.Piece generateAndAddPiece(StructurePiece p_189938_0_, List<StructurePiece> p_189938_1_, Random p_189938_2_, int p_189938_3_, int p_189938_4_, int p_189938_5_, Direction p_189938_6_, int p_189938_7_) {
      if (p_189938_7_ > 8) {
         return null;
      } else if (Math.abs(p_189938_3_ - p_189938_0_.getBoundingBox().minX) <= 80 && Math.abs(p_189938_5_ - p_189938_0_.getBoundingBox().minZ) <= 80) {
         MineshaftStructure.Type mineshaftstructure$type = ((MineshaftPieces.Piece)p_189938_0_).mineShaftType;
         MineshaftPieces.Piece mineshaftpieces$piece = createRandomShaftPiece(p_189938_1_, p_189938_2_, p_189938_3_, p_189938_4_, p_189938_5_, p_189938_6_, p_189938_7_ + 1, mineshaftstructure$type);
         if (mineshaftpieces$piece != null) {
            p_189938_1_.add(mineshaftpieces$piece);
            mineshaftpieces$piece.buildComponent(p_189938_0_, p_189938_1_, p_189938_2_);
         }

         return mineshaftpieces$piece;
      } else {
         return null;
      }
   }

   public static class Stairs extends MineshaftPieces.Piece {
      public Stairs(int p_i50449_1_, MutableBoundingBox p_i50449_2_, Direction p_i50449_3_, MineshaftStructure.Type p_i50449_4_) {
         super(IStructurePieceType.MSSTAIRS, p_i50449_1_, p_i50449_4_);
         this.setCoordBaseMode(p_i50449_3_);
         this.boundingBox = p_i50449_2_;
      }

      public Stairs(TemplateManager p_i50450_1_, CompoundNBT p_i50450_2_) {
         super(IStructurePieceType.MSSTAIRS, p_i50450_2_);
      }

      public static MutableBoundingBox findStairs(List<StructurePiece> p_175812_0_, Random p_175812_1_, int p_175812_2_, int p_175812_3_, int p_175812_4_, Direction p_175812_5_) {
         MutableBoundingBox mutableboundingbox = new MutableBoundingBox(p_175812_2_, p_175812_3_ - 5, p_175812_4_, p_175812_2_, p_175812_3_ + 3 - 1, p_175812_4_);
         switch(p_175812_5_) {
         case NORTH:
         default:
            mutableboundingbox.maxX = p_175812_2_ + 3 - 1;
            mutableboundingbox.minZ = p_175812_4_ - 8;
            break;
         case SOUTH:
            mutableboundingbox.maxX = p_175812_2_ + 3 - 1;
            mutableboundingbox.maxZ = p_175812_4_ + 8;
            break;
         case WEST:
            mutableboundingbox.minX = p_175812_2_ - 8;
            mutableboundingbox.maxZ = p_175812_4_ + 3 - 1;
            break;
         case EAST:
            mutableboundingbox.maxX = p_175812_2_ + 8;
            mutableboundingbox.maxZ = p_175812_4_ + 3 - 1;
         }

         return StructurePiece.findIntersecting(p_175812_0_, mutableboundingbox) != null ? null : mutableboundingbox;
      }

      public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         int i = this.getComponentType();
         Direction direction = this.getCoordBaseMode();
         if (direction != null) {
            switch(direction) {
            case NORTH:
            default:
               MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
               break;
            case SOUTH:
               MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
               break;
            case WEST:
               MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.WEST, i);
               break;
            case EAST:
               MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, Direction.EAST, i);
            }
         }

      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         if (this.isLiquidInStructureBoundingBox(p_225577_1_, p_225577_4_)) {
            return false;
         } else {
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 5, 0, 2, 7, 1, CAVE_AIR, CAVE_AIR, false);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 0, 7, 2, 2, 8, CAVE_AIR, CAVE_AIR, false);

            for(int i = 0; i < 5; ++i) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, CAVE_AIR, CAVE_AIR, false);
            }

            return true;
         }
      }
   }

   public static class Room extends MineshaftPieces.Piece {
      private final List<MutableBoundingBox> connectedRooms = Lists.newLinkedList();

      public Room(int p_i47137_1_, Random p_i47137_2_, int p_i47137_3_, int p_i47137_4_, MineshaftStructure.Type p_i47137_5_) {
         super(IStructurePieceType.MSROOM, p_i47137_1_, p_i47137_5_);
         this.mineShaftType = p_i47137_5_;
         this.boundingBox = new MutableBoundingBox(p_i47137_3_, 50, p_i47137_4_, p_i47137_3_ + 7 + p_i47137_2_.nextInt(6), 54 + p_i47137_2_.nextInt(6), p_i47137_4_ + 7 + p_i47137_2_.nextInt(6));
      }

      public Room(TemplateManager p_i50451_1_, CompoundNBT p_i50451_2_) {
         super(IStructurePieceType.MSROOM, p_i50451_2_);
         ListNBT listnbt = p_i50451_2_.getList("Entrances", 11);

         for(int i = 0; i < listnbt.size(); ++i) {
            this.connectedRooms.add(new MutableBoundingBox(listnbt.getIntArray(i)));
         }

      }

      public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         int i = this.getComponentType();
         int j = this.boundingBox.getYSize() - 3 - 1;
         if (j <= 0) {
            j = 1;
         }

         int k;
         MineshaftPieces.Piece structurepiece;
         MutableBoundingBox mutableboundingbox3;
         for(k = 0; k < this.boundingBox.getXSize(); k += 4) {
            k += p_74861_3_.nextInt(this.boundingBox.getXSize());
            if (k + 3 > this.boundingBox.getXSize()) {
               break;
            }

            structurepiece = MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX + k, this.boundingBox.minY + p_74861_3_.nextInt(j) + 1, this.boundingBox.minZ - 1, Direction.NORTH, i);
            if (structurepiece != null) {
               mutableboundingbox3 = structurepiece.getBoundingBox();
               this.connectedRooms.add(new MutableBoundingBox(mutableboundingbox3.minX, mutableboundingbox3.minY, this.boundingBox.minZ, mutableboundingbox3.maxX, mutableboundingbox3.maxY, this.boundingBox.minZ + 1));
            }
         }

         for(k = 0; k < this.boundingBox.getXSize(); k += 4) {
            k += p_74861_3_.nextInt(this.boundingBox.getXSize());
            if (k + 3 > this.boundingBox.getXSize()) {
               break;
            }

            structurepiece = MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX + k, this.boundingBox.minY + p_74861_3_.nextInt(j) + 1, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
            if (structurepiece != null) {
               mutableboundingbox3 = structurepiece.getBoundingBox();
               this.connectedRooms.add(new MutableBoundingBox(mutableboundingbox3.minX, mutableboundingbox3.minY, this.boundingBox.maxZ - 1, mutableboundingbox3.maxX, mutableboundingbox3.maxY, this.boundingBox.maxZ));
            }
         }

         for(k = 0; k < this.boundingBox.getZSize(); k += 4) {
            k += p_74861_3_.nextInt(this.boundingBox.getZSize());
            if (k + 3 > this.boundingBox.getZSize()) {
               break;
            }

            structurepiece = MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX - 1, this.boundingBox.minY + p_74861_3_.nextInt(j) + 1, this.boundingBox.minZ + k, Direction.WEST, i);
            if (structurepiece != null) {
               mutableboundingbox3 = structurepiece.getBoundingBox();
               this.connectedRooms.add(new MutableBoundingBox(this.boundingBox.minX, mutableboundingbox3.minY, mutableboundingbox3.minZ, this.boundingBox.minX + 1, mutableboundingbox3.maxY, mutableboundingbox3.maxZ));
            }
         }

         for(k = 0; k < this.boundingBox.getZSize(); k += 4) {
            k += p_74861_3_.nextInt(this.boundingBox.getZSize());
            if (k + 3 > this.boundingBox.getZSize()) {
               break;
            }

            structurepiece = MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.maxX + 1, this.boundingBox.minY + p_74861_3_.nextInt(j) + 1, this.boundingBox.minZ + k, Direction.EAST, i);
            if (structurepiece != null) {
               mutableboundingbox3 = structurepiece.getBoundingBox();
               this.connectedRooms.add(new MutableBoundingBox(this.boundingBox.maxX - 1, mutableboundingbox3.minY, mutableboundingbox3.minZ, this.boundingBox.maxX, mutableboundingbox3.maxY, mutableboundingbox3.maxZ));
            }
         }

      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         if (this.isLiquidInStructureBoundingBox(p_225577_1_, p_225577_4_)) {
            return false;
         } else {
            this.fillWithBlocks(p_225577_1_, p_225577_4_, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.minY, this.boundingBox.maxZ, Blocks.DIRT.getDefaultState(), CAVE_AIR, true);
            this.fillWithBlocks(p_225577_1_, p_225577_4_, this.boundingBox.minX, this.boundingBox.minY + 1, this.boundingBox.minZ, this.boundingBox.maxX, Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY), this.boundingBox.maxZ, CAVE_AIR, CAVE_AIR, false);
            Iterator var6 = this.connectedRooms.iterator();

            while(var6.hasNext()) {
               MutableBoundingBox mutableboundingbox = (MutableBoundingBox)var6.next();
               this.fillWithBlocks(p_225577_1_, p_225577_4_, mutableboundingbox.minX, mutableboundingbox.maxY - 2, mutableboundingbox.minZ, mutableboundingbox.maxX, mutableboundingbox.maxY, mutableboundingbox.maxZ, CAVE_AIR, CAVE_AIR, false);
            }

            this.randomlyRareFillWithBlocks(p_225577_1_, p_225577_4_, this.boundingBox.minX, this.boundingBox.minY + 4, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ, CAVE_AIR, false);
            return true;
         }
      }

      public void offset(int p_181138_1_, int p_181138_2_, int p_181138_3_) {
         super.offset(p_181138_1_, p_181138_2_, p_181138_3_);
         Iterator var4 = this.connectedRooms.iterator();

         while(var4.hasNext()) {
            MutableBoundingBox mutableboundingbox = (MutableBoundingBox)var4.next();
            mutableboundingbox.offset(p_181138_1_, p_181138_2_, p_181138_3_);
         }

      }

      protected void readAdditional(CompoundNBT p_143011_1_) {
         super.readAdditional(p_143011_1_);
         ListNBT listnbt = new ListNBT();
         Iterator var3 = this.connectedRooms.iterator();

         while(var3.hasNext()) {
            MutableBoundingBox mutableboundingbox = (MutableBoundingBox)var3.next();
            listnbt.add(mutableboundingbox.toNBTTagIntArray());
         }

         p_143011_1_.put("Entrances", listnbt);
      }
   }

   abstract static class Piece extends StructurePiece {
      protected MineshaftStructure.Type mineShaftType;

      public Piece(IStructurePieceType p_i50452_1_, int p_i50452_2_, MineshaftStructure.Type p_i50452_3_) {
         super(p_i50452_1_, p_i50452_2_);
         this.mineShaftType = p_i50452_3_;
      }

      public Piece(IStructurePieceType p_i50453_1_, CompoundNBT p_i50453_2_) {
         super(p_i50453_1_, p_i50453_2_);
         this.mineShaftType = MineshaftStructure.Type.byId(p_i50453_2_.getInt("MST"));
      }

      protected void readAdditional(CompoundNBT p_143011_1_) {
         p_143011_1_.putInt("MST", this.mineShaftType.ordinal());
      }

      protected BlockState getPlanksBlock() {
         switch(this.mineShaftType) {
         case NORMAL:
         default:
            return Blocks.OAK_PLANKS.getDefaultState();
         case MESA:
            return Blocks.DARK_OAK_PLANKS.getDefaultState();
         }
      }

      protected BlockState getFenceBlock() {
         switch(this.mineShaftType) {
         case NORMAL:
         default:
            return Blocks.OAK_FENCE.getDefaultState();
         case MESA:
            return Blocks.DARK_OAK_FENCE.getDefaultState();
         }
      }

      protected boolean isSupportingBox(IBlockReader p_189918_1_, MutableBoundingBox p_189918_2_, int p_189918_3_, int p_189918_4_, int p_189918_5_, int p_189918_6_) {
         for(int i = p_189918_3_; i <= p_189918_4_; ++i) {
            if (this.getBlockStateFromPos(p_189918_1_, i, p_189918_5_ + 1, p_189918_6_, p_189918_2_).isAir()) {
               return false;
            }
         }

         return true;
      }
   }

   public static class Cross extends MineshaftPieces.Piece {
      private final Direction field_74953_a;
      private final boolean isMultipleFloors;

      public Cross(TemplateManager p_i50454_1_, CompoundNBT p_i50454_2_) {
         super(IStructurePieceType.MSCROSSING, p_i50454_2_);
         this.isMultipleFloors = p_i50454_2_.getBoolean("tf");
         this.field_74953_a = Direction.byHorizontalIndex(p_i50454_2_.getInt("D"));
      }

      protected void readAdditional(CompoundNBT p_143011_1_) {
         super.readAdditional(p_143011_1_);
         p_143011_1_.putBoolean("tf", this.isMultipleFloors);
         p_143011_1_.putInt("D", this.field_74953_a.getHorizontalIndex());
      }

      public Cross(int p_i50455_1_, MutableBoundingBox p_i50455_2_, @Nullable Direction p_i50455_3_, MineshaftStructure.Type p_i50455_4_) {
         super(IStructurePieceType.MSCROSSING, p_i50455_1_, p_i50455_4_);
         this.field_74953_a = p_i50455_3_;
         this.boundingBox = p_i50455_2_;
         this.isMultipleFloors = p_i50455_2_.getYSize() > 3;
      }

      public static MutableBoundingBox findCrossing(List<StructurePiece> p_175813_0_, Random p_175813_1_, int p_175813_2_, int p_175813_3_, int p_175813_4_, Direction p_175813_5_) {
         MutableBoundingBox mutableboundingbox = new MutableBoundingBox(p_175813_2_, p_175813_3_, p_175813_4_, p_175813_2_, p_175813_3_ + 3 - 1, p_175813_4_);
         if (p_175813_1_.nextInt(4) == 0) {
            mutableboundingbox.maxY += 4;
         }

         switch(p_175813_5_) {
         case NORTH:
         default:
            mutableboundingbox.minX = p_175813_2_ - 1;
            mutableboundingbox.maxX = p_175813_2_ + 3;
            mutableboundingbox.minZ = p_175813_4_ - 4;
            break;
         case SOUTH:
            mutableboundingbox.minX = p_175813_2_ - 1;
            mutableboundingbox.maxX = p_175813_2_ + 3;
            mutableboundingbox.maxZ = p_175813_4_ + 3 + 1;
            break;
         case WEST:
            mutableboundingbox.minX = p_175813_2_ - 4;
            mutableboundingbox.minZ = p_175813_4_ - 1;
            mutableboundingbox.maxZ = p_175813_4_ + 3;
            break;
         case EAST:
            mutableboundingbox.maxX = p_175813_2_ + 3 + 1;
            mutableboundingbox.minZ = p_175813_4_ - 1;
            mutableboundingbox.maxZ = p_175813_4_ + 3;
         }

         return StructurePiece.findIntersecting(p_175813_0_, mutableboundingbox) != null ? null : mutableboundingbox;
      }

      public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         int i = this.getComponentType();
         switch(this.field_74953_a) {
         case NORTH:
         default:
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
            break;
         case SOUTH:
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
            break;
         case WEST:
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.WEST, i);
            break;
         case EAST:
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i);
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
            MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, Direction.EAST, i);
         }

         if (this.isMultipleFloors) {
            if (p_74861_3_.nextBoolean()) {
               MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, Direction.NORTH, i);
            }

            if (p_74861_3_.nextBoolean()) {
               MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.WEST, i);
            }

            if (p_74861_3_.nextBoolean()) {
               MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, Direction.EAST, i);
            }

            if (p_74861_3_.nextBoolean()) {
               MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, Direction.SOUTH, i);
            }
         }

      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         if (this.isLiquidInStructureBoundingBox(p_225577_1_, p_225577_4_)) {
            return false;
         } else {
            BlockState blockstate = this.getPlanksBlock();
            if (this.isMultipleFloors) {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ, CAVE_AIR, CAVE_AIR, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ - 1, CAVE_AIR, CAVE_AIR, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, this.boundingBox.minX + 1, this.boundingBox.maxY - 2, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, CAVE_AIR, CAVE_AIR, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, this.boundingBox.minX, this.boundingBox.maxY - 2, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, CAVE_AIR, CAVE_AIR, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, this.boundingBox.minX + 1, this.boundingBox.minY + 3, this.boundingBox.minZ + 1, this.boundingBox.maxX - 1, this.boundingBox.minY + 3, this.boundingBox.maxZ - 1, CAVE_AIR, CAVE_AIR, false);
            } else {
               this.fillWithBlocks(p_225577_1_, p_225577_4_, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, CAVE_AIR, CAVE_AIR, false);
               this.fillWithBlocks(p_225577_1_, p_225577_4_, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, CAVE_AIR, CAVE_AIR, false);
            }

            this.placeSupportPillar(p_225577_1_, p_225577_4_, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
            this.placeSupportPillar(p_225577_1_, p_225577_4_, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);
            this.placeSupportPillar(p_225577_1_, p_225577_4_, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxY);
            this.placeSupportPillar(p_225577_1_, p_225577_4_, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxY);

            for(int i = this.boundingBox.minX; i <= this.boundingBox.maxX; ++i) {
               for(int j = this.boundingBox.minZ; j <= this.boundingBox.maxZ; ++j) {
                  if (this.getBlockStateFromPos(p_225577_1_, i, this.boundingBox.minY - 1, j, p_225577_4_).isAir() && this.getSkyBrightness(p_225577_1_, i, this.boundingBox.minY - 1, j, p_225577_4_)) {
                     this.setBlockState(p_225577_1_, blockstate, i, this.boundingBox.minY - 1, j, p_225577_4_);
                  }
               }
            }

            return true;
         }
      }

      private void placeSupportPillar(IWorld p_189923_1_, MutableBoundingBox p_189923_2_, int p_189923_3_, int p_189923_4_, int p_189923_5_, int p_189923_6_) {
         if (!this.getBlockStateFromPos(p_189923_1_, p_189923_3_, p_189923_6_ + 1, p_189923_5_, p_189923_2_).isAir()) {
            this.fillWithBlocks(p_189923_1_, p_189923_2_, p_189923_3_, p_189923_4_, p_189923_5_, p_189923_3_, p_189923_6_, p_189923_5_, this.getPlanksBlock(), CAVE_AIR, false);
         }

      }
   }

   public static class Corridor extends MineshaftPieces.Piece {
      private final boolean hasRails;
      private final boolean hasSpiders;
      private boolean spawnerPlaced;
      private final int sectionCount;

      public Corridor(TemplateManager p_i50456_1_, CompoundNBT p_i50456_2_) {
         super(IStructurePieceType.MSCORRIDOR, p_i50456_2_);
         this.hasRails = p_i50456_2_.getBoolean("hr");
         this.hasSpiders = p_i50456_2_.getBoolean("sc");
         this.spawnerPlaced = p_i50456_2_.getBoolean("hps");
         this.sectionCount = p_i50456_2_.getInt("Num");
      }

      protected void readAdditional(CompoundNBT p_143011_1_) {
         super.readAdditional(p_143011_1_);
         p_143011_1_.putBoolean("hr", this.hasRails);
         p_143011_1_.putBoolean("sc", this.hasSpiders);
         p_143011_1_.putBoolean("hps", this.spawnerPlaced);
         p_143011_1_.putInt("Num", this.sectionCount);
      }

      public Corridor(int p_i47140_1_, Random p_i47140_2_, MutableBoundingBox p_i47140_3_, Direction p_i47140_4_, MineshaftStructure.Type p_i47140_5_) {
         super(IStructurePieceType.MSCORRIDOR, p_i47140_1_, p_i47140_5_);
         this.setCoordBaseMode(p_i47140_4_);
         this.boundingBox = p_i47140_3_;
         this.hasRails = p_i47140_2_.nextInt(3) == 0;
         this.hasSpiders = !this.hasRails && p_i47140_2_.nextInt(23) == 0;
         if (this.getCoordBaseMode().getAxis() == Direction.Axis.Z) {
            this.sectionCount = p_i47140_3_.getZSize() / 5;
         } else {
            this.sectionCount = p_i47140_3_.getXSize() / 5;
         }

      }

      public static MutableBoundingBox findCorridorSize(List<StructurePiece> p_175814_0_, Random p_175814_1_, int p_175814_2_, int p_175814_3_, int p_175814_4_, Direction p_175814_5_) {
         MutableBoundingBox mutableboundingbox = new MutableBoundingBox(p_175814_2_, p_175814_3_, p_175814_4_, p_175814_2_, p_175814_3_ + 3 - 1, p_175814_4_);

         int i;
         for(i = p_175814_1_.nextInt(3) + 2; i > 0; --i) {
            int j = i * 5;
            switch(p_175814_5_) {
            case NORTH:
            default:
               mutableboundingbox.maxX = p_175814_2_ + 3 - 1;
               mutableboundingbox.minZ = p_175814_4_ - (j - 1);
               break;
            case SOUTH:
               mutableboundingbox.maxX = p_175814_2_ + 3 - 1;
               mutableboundingbox.maxZ = p_175814_4_ + j - 1;
               break;
            case WEST:
               mutableboundingbox.minX = p_175814_2_ - (j - 1);
               mutableboundingbox.maxZ = p_175814_4_ + 3 - 1;
               break;
            case EAST:
               mutableboundingbox.maxX = p_175814_2_ + j - 1;
               mutableboundingbox.maxZ = p_175814_4_ + 3 - 1;
            }

            if (StructurePiece.findIntersecting(p_175814_0_, mutableboundingbox) == null) {
               break;
            }
         }

         return i > 0 ? mutableboundingbox : null;
      }

      public void buildComponent(StructurePiece p_74861_1_, List<StructurePiece> p_74861_2_, Random p_74861_3_) {
         int i = this.getComponentType();
         int j = p_74861_3_.nextInt(4);
         Direction direction = this.getCoordBaseMode();
         if (direction != null) {
            switch(direction) {
            case NORTH:
            default:
               if (j <= 1) {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX, this.boundingBox.minY - 1 + p_74861_3_.nextInt(3), this.boundingBox.minZ - 1, direction, i);
               } else if (j == 2) {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + p_74861_3_.nextInt(3), this.boundingBox.minZ, Direction.WEST, i);
               } else {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + p_74861_3_.nextInt(3), this.boundingBox.minZ, Direction.EAST, i);
               }
               break;
            case SOUTH:
               if (j <= 1) {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX, this.boundingBox.minY - 1 + p_74861_3_.nextInt(3), this.boundingBox.maxZ + 1, direction, i);
               } else if (j == 2) {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + p_74861_3_.nextInt(3), this.boundingBox.maxZ - 3, Direction.WEST, i);
               } else {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + p_74861_3_.nextInt(3), this.boundingBox.maxZ - 3, Direction.EAST, i);
               }
               break;
            case WEST:
               if (j <= 1) {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + p_74861_3_.nextInt(3), this.boundingBox.minZ, direction, i);
               } else if (j == 2) {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX, this.boundingBox.minY - 1 + p_74861_3_.nextInt(3), this.boundingBox.minZ - 1, Direction.NORTH, i);
               } else {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX, this.boundingBox.minY - 1 + p_74861_3_.nextInt(3), this.boundingBox.maxZ + 1, Direction.SOUTH, i);
               }
               break;
            case EAST:
               if (j <= 1) {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + p_74861_3_.nextInt(3), this.boundingBox.minZ, direction, i);
               } else if (j == 2) {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + p_74861_3_.nextInt(3), this.boundingBox.minZ - 1, Direction.NORTH, i);
               } else {
                  MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + p_74861_3_.nextInt(3), this.boundingBox.maxZ + 1, Direction.SOUTH, i);
               }
            }
         }

         if (i < 8) {
            int k;
            int l;
            if (direction != Direction.NORTH && direction != Direction.SOUTH) {
               for(k = this.boundingBox.minX + 3; k + 3 <= this.boundingBox.maxX; k += 5) {
                  l = p_74861_3_.nextInt(5);
                  if (l == 0) {
                     MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, k, this.boundingBox.minY, this.boundingBox.minZ - 1, Direction.NORTH, i + 1);
                  } else if (l == 1) {
                     MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, k, this.boundingBox.minY, this.boundingBox.maxZ + 1, Direction.SOUTH, i + 1);
                  }
               }
            } else {
               for(k = this.boundingBox.minZ + 3; k + 3 <= this.boundingBox.maxZ; k += 5) {
                  l = p_74861_3_.nextInt(5);
                  if (l == 0) {
                     MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.minX - 1, this.boundingBox.minY, k, Direction.WEST, i + 1);
                  } else if (l == 1) {
                     MineshaftPieces.generateAndAddPiece(p_74861_1_, p_74861_2_, p_74861_3_, this.boundingBox.maxX + 1, this.boundingBox.minY, k, Direction.EAST, i + 1);
                  }
               }
            }
         }

      }

      protected boolean generateChest(IWorld p_186167_1_, MutableBoundingBox p_186167_2_, Random p_186167_3_, int p_186167_4_, int p_186167_5_, int p_186167_6_, ResourceLocation p_186167_7_) {
         BlockPos blockpos = new BlockPos(this.getXWithOffset(p_186167_4_, p_186167_6_), this.getYWithOffset(p_186167_5_), this.getZWithOffset(p_186167_4_, p_186167_6_));
         if (p_186167_2_.isVecInside(blockpos) && p_186167_1_.getBlockState(blockpos).isAir(p_186167_1_, blockpos) && !p_186167_1_.getBlockState(blockpos.down()).isAir(p_186167_1_, blockpos.down())) {
            BlockState blockstate = (BlockState)Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, p_186167_3_.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
            this.setBlockState(p_186167_1_, blockstate, p_186167_4_, p_186167_5_, p_186167_6_, p_186167_2_);
            ChestMinecartEntity chestminecartentity = new ChestMinecartEntity(p_186167_1_.getWorld(), (double)((float)blockpos.getX() + 0.5F), (double)((float)blockpos.getY() + 0.5F), (double)((float)blockpos.getZ() + 0.5F));
            chestminecartentity.setLootTable(p_186167_7_, p_186167_3_.nextLong());
            p_186167_1_.addEntity(chestminecartentity);
            return true;
         } else {
            return false;
         }
      }

      public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
         if (this.isLiquidInStructureBoundingBox(p_225577_1_, p_225577_4_)) {
            return false;
         } else {
            int i = false;
            int j = true;
            int k = false;
            int l = true;
            int i1 = this.sectionCount * 5 - 1;
            BlockState blockstate = this.getPlanksBlock();
            this.fillWithBlocks(p_225577_1_, p_225577_4_, 0, 0, 0, 2, 1, i1, CAVE_AIR, CAVE_AIR, false);
            this.generateMaybeBox(p_225577_1_, p_225577_4_, p_225577_3_, 0.8F, 0, 2, 0, 2, 2, i1, CAVE_AIR, CAVE_AIR, false, false);
            if (this.hasSpiders) {
               this.generateMaybeBox(p_225577_1_, p_225577_4_, p_225577_3_, 0.6F, 0, 0, 0, 2, 1, i1, Blocks.COBWEB.getDefaultState(), CAVE_AIR, false, true);
            }

            int l2;
            int j3;
            for(l2 = 0; l2 < this.sectionCount; ++l2) {
               j3 = 2 + l2 * 5;
               this.placeSupport(p_225577_1_, p_225577_4_, 0, 0, j3, 2, 2, p_225577_3_);
               this.placeCobWeb(p_225577_1_, p_225577_4_, p_225577_3_, 0.1F, 0, 2, j3 - 1);
               this.placeCobWeb(p_225577_1_, p_225577_4_, p_225577_3_, 0.1F, 2, 2, j3 - 1);
               this.placeCobWeb(p_225577_1_, p_225577_4_, p_225577_3_, 0.1F, 0, 2, j3 + 1);
               this.placeCobWeb(p_225577_1_, p_225577_4_, p_225577_3_, 0.1F, 2, 2, j3 + 1);
               this.placeCobWeb(p_225577_1_, p_225577_4_, p_225577_3_, 0.05F, 0, 2, j3 - 2);
               this.placeCobWeb(p_225577_1_, p_225577_4_, p_225577_3_, 0.05F, 2, 2, j3 - 2);
               this.placeCobWeb(p_225577_1_, p_225577_4_, p_225577_3_, 0.05F, 0, 2, j3 + 2);
               this.placeCobWeb(p_225577_1_, p_225577_4_, p_225577_3_, 0.05F, 2, 2, j3 + 2);
               if (p_225577_3_.nextInt(100) == 0) {
                  this.generateChest(p_225577_1_, p_225577_4_, p_225577_3_, 2, 0, j3 - 1, LootTables.CHESTS_ABANDONED_MINESHAFT);
               }

               if (p_225577_3_.nextInt(100) == 0) {
                  this.generateChest(p_225577_1_, p_225577_4_, p_225577_3_, 0, 0, j3 + 1, LootTables.CHESTS_ABANDONED_MINESHAFT);
               }

               if (this.hasSpiders && !this.spawnerPlaced) {
                  int l1 = this.getYWithOffset(0);
                  int i2 = j3 - 1 + p_225577_3_.nextInt(3);
                  int j2 = this.getXWithOffset(1, i2);
                  int k2 = this.getZWithOffset(1, i2);
                  BlockPos blockpos = new BlockPos(j2, l1, k2);
                  if (p_225577_4_.isVecInside(blockpos) && this.getSkyBrightness(p_225577_1_, 1, 0, i2, p_225577_4_)) {
                     this.spawnerPlaced = true;
                     p_225577_1_.setBlockState(blockpos, Blocks.SPAWNER.getDefaultState(), 2);
                     TileEntity tileentity = p_225577_1_.getTileEntity(blockpos);
                     if (tileentity instanceof MobSpawnerTileEntity) {
                        ((MobSpawnerTileEntity)tileentity).getSpawnerBaseLogic().setEntityType(EntityType.CAVE_SPIDER);
                     }
                  }
               }
            }

            for(l2 = 0; l2 <= 2; ++l2) {
               for(j3 = 0; j3 <= i1; ++j3) {
                  int k3 = true;
                  BlockState blockstate3 = this.getBlockStateFromPos(p_225577_1_, l2, -1, j3, p_225577_4_);
                  if (blockstate3.isAir() && this.getSkyBrightness(p_225577_1_, l2, -1, j3, p_225577_4_)) {
                     int l3 = true;
                     this.setBlockState(p_225577_1_, blockstate, l2, -1, j3, p_225577_4_);
                  }
               }
            }

            if (this.hasRails) {
               BlockState blockstate1 = (BlockState)Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, RailShape.NORTH_SOUTH);

               for(j3 = 0; j3 <= i1; ++j3) {
                  BlockState blockstate2 = this.getBlockStateFromPos(p_225577_1_, 1, -1, j3, p_225577_4_);
                  if (!blockstate2.isAir() && blockstate2.isOpaqueCube(p_225577_1_, new BlockPos(this.getXWithOffset(1, j3), this.getYWithOffset(-1), this.getZWithOffset(1, j3)))) {
                     float f = this.getSkyBrightness(p_225577_1_, 1, 0, j3, p_225577_4_) ? 0.7F : 0.9F;
                     this.randomlyPlaceBlock(p_225577_1_, p_225577_4_, p_225577_3_, f, 1, 0, j3, blockstate1);
                  }
               }
            }

            return true;
         }
      }

      private void placeSupport(IWorld p_189921_1_, MutableBoundingBox p_189921_2_, int p_189921_3_, int p_189921_4_, int p_189921_5_, int p_189921_6_, int p_189921_7_, Random p_189921_8_) {
         if (this.isSupportingBox(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_7_, p_189921_6_, p_189921_5_)) {
            BlockState blockstate = this.getPlanksBlock();
            BlockState blockstate1 = this.getFenceBlock();
            this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_4_, p_189921_5_, p_189921_3_, p_189921_6_ - 1, p_189921_5_, (BlockState)blockstate1.with(FenceBlock.WEST, true), CAVE_AIR, false);
            this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_7_, p_189921_4_, p_189921_5_, p_189921_7_, p_189921_6_ - 1, p_189921_5_, (BlockState)blockstate1.with(FenceBlock.EAST, true), CAVE_AIR, false);
            if (p_189921_8_.nextInt(4) == 0) {
               this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_6_, p_189921_5_, p_189921_3_, p_189921_6_, p_189921_5_, blockstate, CAVE_AIR, false);
               this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_7_, p_189921_6_, p_189921_5_, p_189921_7_, p_189921_6_, p_189921_5_, blockstate, CAVE_AIR, false);
            } else {
               this.fillWithBlocks(p_189921_1_, p_189921_2_, p_189921_3_, p_189921_6_, p_189921_5_, p_189921_7_, p_189921_6_, p_189921_5_, blockstate, CAVE_AIR, false);
               this.randomlyPlaceBlock(p_189921_1_, p_189921_2_, p_189921_8_, 0.05F, p_189921_3_ + 1, p_189921_6_, p_189921_5_ - 1, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, Direction.NORTH));
               this.randomlyPlaceBlock(p_189921_1_, p_189921_2_, p_189921_8_, 0.05F, p_189921_3_ + 1, p_189921_6_, p_189921_5_ + 1, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, Direction.SOUTH));
            }
         }

      }

      private void placeCobWeb(IWorld p_189922_1_, MutableBoundingBox p_189922_2_, Random p_189922_3_, float p_189922_4_, int p_189922_5_, int p_189922_6_, int p_189922_7_) {
         if (this.getSkyBrightness(p_189922_1_, p_189922_5_, p_189922_6_, p_189922_7_, p_189922_2_)) {
            this.randomlyPlaceBlock(p_189922_1_, p_189922_2_, p_189922_3_, p_189922_4_, p_189922_5_, p_189922_6_, p_189922_7_, Blocks.COBWEB.getDefaultState());
         }

      }
   }
}
