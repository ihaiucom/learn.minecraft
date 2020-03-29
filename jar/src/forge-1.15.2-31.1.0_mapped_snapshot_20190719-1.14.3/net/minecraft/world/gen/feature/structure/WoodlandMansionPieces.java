package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;

public class WoodlandMansionPieces {
   public static void generateMansion(TemplateManager p_191152_0_, BlockPos p_191152_1_, Rotation p_191152_2_, List<WoodlandMansionPieces.MansionTemplate> p_191152_3_, Random p_191152_4_) {
      WoodlandMansionPieces.Grid lvt_5_1_ = new WoodlandMansionPieces.Grid(p_191152_4_);
      WoodlandMansionPieces.Placer lvt_6_1_ = new WoodlandMansionPieces.Placer(p_191152_0_, p_191152_4_);
      lvt_6_1_.createMansion(p_191152_1_, p_191152_2_, p_191152_3_, lvt_5_1_);
   }

   static class ThirdFloor extends WoodlandMansionPieces.SecondFloor {
      private ThirdFloor() {
         super(null);
      }

      // $FF: synthetic method
      ThirdFloor(Object p_i47357_1_) {
         this();
      }
   }

   static class SecondFloor extends WoodlandMansionPieces.RoomCollection {
      private SecondFloor() {
         super(null);
      }

      public String get1x1(Random p_191104_1_) {
         return "1x1_b" + (p_191104_1_.nextInt(4) + 1);
      }

      public String get1x1Secret(Random p_191099_1_) {
         return "1x1_as" + (p_191099_1_.nextInt(4) + 1);
      }

      public String get1x2SideEntrance(Random p_191100_1_, boolean p_191100_2_) {
         return p_191100_2_ ? "1x2_c_stairs" : "1x2_c" + (p_191100_1_.nextInt(4) + 1);
      }

      public String get1x2FrontEntrance(Random p_191098_1_, boolean p_191098_2_) {
         return p_191098_2_ ? "1x2_d_stairs" : "1x2_d" + (p_191098_1_.nextInt(5) + 1);
      }

      public String get1x2Secret(Random p_191102_1_) {
         return "1x2_se" + (p_191102_1_.nextInt(1) + 1);
      }

      public String get2x2(Random p_191101_1_) {
         return "2x2_b" + (p_191101_1_.nextInt(5) + 1);
      }

      public String get2x2Secret(Random p_191103_1_) {
         return "2x2_s1";
      }

      // $FF: synthetic method
      SecondFloor(Object p_i47359_1_) {
         this();
      }
   }

   static class FirstFloor extends WoodlandMansionPieces.RoomCollection {
      private FirstFloor() {
         super(null);
      }

      public String get1x1(Random p_191104_1_) {
         return "1x1_a" + (p_191104_1_.nextInt(5) + 1);
      }

      public String get1x1Secret(Random p_191099_1_) {
         return "1x1_as" + (p_191099_1_.nextInt(4) + 1);
      }

      public String get1x2SideEntrance(Random p_191100_1_, boolean p_191100_2_) {
         return "1x2_a" + (p_191100_1_.nextInt(9) + 1);
      }

      public String get1x2FrontEntrance(Random p_191098_1_, boolean p_191098_2_) {
         return "1x2_b" + (p_191098_1_.nextInt(5) + 1);
      }

      public String get1x2Secret(Random p_191102_1_) {
         return "1x2_s" + (p_191102_1_.nextInt(2) + 1);
      }

      public String get2x2(Random p_191101_1_) {
         return "2x2_a" + (p_191101_1_.nextInt(4) + 1);
      }

      public String get2x2Secret(Random p_191103_1_) {
         return "2x2_s1";
      }

      // $FF: synthetic method
      FirstFloor(Object p_i47364_1_) {
         this();
      }
   }

   abstract static class RoomCollection {
      private RoomCollection() {
      }

      public abstract String get1x1(Random var1);

      public abstract String get1x1Secret(Random var1);

      public abstract String get1x2SideEntrance(Random var1, boolean var2);

      public abstract String get1x2FrontEntrance(Random var1, boolean var2);

      public abstract String get1x2Secret(Random var1);

      public abstract String get2x2(Random var1);

      public abstract String get2x2Secret(Random var1);

      // $FF: synthetic method
      RoomCollection(Object p_i47363_1_) {
         this();
      }
   }

   static class SimpleGrid {
      private final int[][] grid;
      private final int width;
      private final int height;
      private final int valueIfOutside;

      public SimpleGrid(int p_i47358_1_, int p_i47358_2_, int p_i47358_3_) {
         this.width = p_i47358_1_;
         this.height = p_i47358_2_;
         this.valueIfOutside = p_i47358_3_;
         this.grid = new int[p_i47358_1_][p_i47358_2_];
      }

      public void set(int p_191144_1_, int p_191144_2_, int p_191144_3_) {
         if (p_191144_1_ >= 0 && p_191144_1_ < this.width && p_191144_2_ >= 0 && p_191144_2_ < this.height) {
            this.grid[p_191144_1_][p_191144_2_] = p_191144_3_;
         }

      }

      public void set(int p_191142_1_, int p_191142_2_, int p_191142_3_, int p_191142_4_, int p_191142_5_) {
         for(int lvt_6_1_ = p_191142_2_; lvt_6_1_ <= p_191142_4_; ++lvt_6_1_) {
            for(int lvt_7_1_ = p_191142_1_; lvt_7_1_ <= p_191142_3_; ++lvt_7_1_) {
               this.set(lvt_7_1_, lvt_6_1_, p_191142_5_);
            }
         }

      }

      public int get(int p_191145_1_, int p_191145_2_) {
         return p_191145_1_ >= 0 && p_191145_1_ < this.width && p_191145_2_ >= 0 && p_191145_2_ < this.height ? this.grid[p_191145_1_][p_191145_2_] : this.valueIfOutside;
      }

      public void setIf(int p_197588_1_, int p_197588_2_, int p_197588_3_, int p_197588_4_) {
         if (this.get(p_197588_1_, p_197588_2_) == p_197588_3_) {
            this.set(p_197588_1_, p_197588_2_, p_197588_4_);
         }

      }

      public boolean edgesTo(int p_191147_1_, int p_191147_2_, int p_191147_3_) {
         return this.get(p_191147_1_ - 1, p_191147_2_) == p_191147_3_ || this.get(p_191147_1_ + 1, p_191147_2_) == p_191147_3_ || this.get(p_191147_1_, p_191147_2_ + 1) == p_191147_3_ || this.get(p_191147_1_, p_191147_2_ - 1) == p_191147_3_;
      }
   }

   static class Grid {
      private final Random random;
      private final WoodlandMansionPieces.SimpleGrid baseGrid;
      private final WoodlandMansionPieces.SimpleGrid thirdFloorGrid;
      private final WoodlandMansionPieces.SimpleGrid[] floorRooms;
      private final int entranceX;
      private final int entranceY;

      public Grid(Random p_i47362_1_) {
         this.random = p_i47362_1_;
         int lvt_2_1_ = true;
         this.entranceX = 7;
         this.entranceY = 4;
         this.baseGrid = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.baseGrid.set(this.entranceX, this.entranceY, this.entranceX + 1, this.entranceY + 1, 3);
         this.baseGrid.set(this.entranceX - 1, this.entranceY, this.entranceX - 1, this.entranceY + 1, 2);
         this.baseGrid.set(this.entranceX + 2, this.entranceY - 2, this.entranceX + 3, this.entranceY + 3, 5);
         this.baseGrid.set(this.entranceX + 1, this.entranceY - 2, this.entranceX + 1, this.entranceY - 1, 1);
         this.baseGrid.set(this.entranceX + 1, this.entranceY + 2, this.entranceX + 1, this.entranceY + 3, 1);
         this.baseGrid.set(this.entranceX - 1, this.entranceY - 1, 1);
         this.baseGrid.set(this.entranceX - 1, this.entranceY + 2, 1);
         this.baseGrid.set(0, 0, 11, 1, 5);
         this.baseGrid.set(0, 9, 11, 11, 5);
         this.recursiveCorridor(this.baseGrid, this.entranceX, this.entranceY - 2, Direction.WEST, 6);
         this.recursiveCorridor(this.baseGrid, this.entranceX, this.entranceY + 3, Direction.WEST, 6);
         this.recursiveCorridor(this.baseGrid, this.entranceX - 2, this.entranceY - 1, Direction.WEST, 3);
         this.recursiveCorridor(this.baseGrid, this.entranceX - 2, this.entranceY + 2, Direction.WEST, 3);

         while(this.cleanEdges(this.baseGrid)) {
         }

         this.floorRooms = new WoodlandMansionPieces.SimpleGrid[3];
         this.floorRooms[0] = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.floorRooms[1] = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.floorRooms[2] = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.identifyRooms(this.baseGrid, this.floorRooms[0]);
         this.identifyRooms(this.baseGrid, this.floorRooms[1]);
         this.floorRooms[0].set(this.entranceX + 1, this.entranceY, this.entranceX + 1, this.entranceY + 1, 8388608);
         this.floorRooms[1].set(this.entranceX + 1, this.entranceY, this.entranceX + 1, this.entranceY + 1, 8388608);
         this.thirdFloorGrid = new WoodlandMansionPieces.SimpleGrid(this.baseGrid.width, this.baseGrid.height, 5);
         this.setupThirdFloor();
         this.identifyRooms(this.thirdFloorGrid, this.floorRooms[2]);
      }

      public static boolean isHouse(WoodlandMansionPieces.SimpleGrid p_191109_0_, int p_191109_1_, int p_191109_2_) {
         int lvt_3_1_ = p_191109_0_.get(p_191109_1_, p_191109_2_);
         return lvt_3_1_ == 1 || lvt_3_1_ == 2 || lvt_3_1_ == 3 || lvt_3_1_ == 4;
      }

      public boolean isRoomId(WoodlandMansionPieces.SimpleGrid p_191114_1_, int p_191114_2_, int p_191114_3_, int p_191114_4_, int p_191114_5_) {
         return (this.floorRooms[p_191114_4_].get(p_191114_2_, p_191114_3_) & '\uffff') == p_191114_5_;
      }

      @Nullable
      public Direction get1x2RoomDirection(WoodlandMansionPieces.SimpleGrid p_191113_1_, int p_191113_2_, int p_191113_3_, int p_191113_4_, int p_191113_5_) {
         Iterator var6 = Direction.Plane.HORIZONTAL.iterator();

         Direction lvt_7_1_;
         do {
            if (!var6.hasNext()) {
               return null;
            }

            lvt_7_1_ = (Direction)var6.next();
         } while(!this.isRoomId(p_191113_1_, p_191113_2_ + lvt_7_1_.getXOffset(), p_191113_3_ + lvt_7_1_.getZOffset(), p_191113_4_, p_191113_5_));

         return lvt_7_1_;
      }

      private void recursiveCorridor(WoodlandMansionPieces.SimpleGrid p_191110_1_, int p_191110_2_, int p_191110_3_, Direction p_191110_4_, int p_191110_5_) {
         if (p_191110_5_ > 0) {
            p_191110_1_.set(p_191110_2_, p_191110_3_, 1);
            p_191110_1_.setIf(p_191110_2_ + p_191110_4_.getXOffset(), p_191110_3_ + p_191110_4_.getZOffset(), 0, 1);

            Direction lvt_7_1_;
            for(int lvt_6_1_ = 0; lvt_6_1_ < 8; ++lvt_6_1_) {
               lvt_7_1_ = Direction.byHorizontalIndex(this.random.nextInt(4));
               if (lvt_7_1_ != p_191110_4_.getOpposite() && (lvt_7_1_ != Direction.EAST || !this.random.nextBoolean())) {
                  int lvt_8_1_ = p_191110_2_ + p_191110_4_.getXOffset();
                  int lvt_9_1_ = p_191110_3_ + p_191110_4_.getZOffset();
                  if (p_191110_1_.get(lvt_8_1_ + lvt_7_1_.getXOffset(), lvt_9_1_ + lvt_7_1_.getZOffset()) == 0 && p_191110_1_.get(lvt_8_1_ + lvt_7_1_.getXOffset() * 2, lvt_9_1_ + lvt_7_1_.getZOffset() * 2) == 0) {
                     this.recursiveCorridor(p_191110_1_, p_191110_2_ + p_191110_4_.getXOffset() + lvt_7_1_.getXOffset(), p_191110_3_ + p_191110_4_.getZOffset() + lvt_7_1_.getZOffset(), lvt_7_1_, p_191110_5_ - 1);
                     break;
                  }
               }
            }

            Direction lvt_6_2_ = p_191110_4_.rotateY();
            lvt_7_1_ = p_191110_4_.rotateYCCW();
            p_191110_1_.setIf(p_191110_2_ + lvt_6_2_.getXOffset(), p_191110_3_ + lvt_6_2_.getZOffset(), 0, 2);
            p_191110_1_.setIf(p_191110_2_ + lvt_7_1_.getXOffset(), p_191110_3_ + lvt_7_1_.getZOffset(), 0, 2);
            p_191110_1_.setIf(p_191110_2_ + p_191110_4_.getXOffset() + lvt_6_2_.getXOffset(), p_191110_3_ + p_191110_4_.getZOffset() + lvt_6_2_.getZOffset(), 0, 2);
            p_191110_1_.setIf(p_191110_2_ + p_191110_4_.getXOffset() + lvt_7_1_.getXOffset(), p_191110_3_ + p_191110_4_.getZOffset() + lvt_7_1_.getZOffset(), 0, 2);
            p_191110_1_.setIf(p_191110_2_ + p_191110_4_.getXOffset() * 2, p_191110_3_ + p_191110_4_.getZOffset() * 2, 0, 2);
            p_191110_1_.setIf(p_191110_2_ + lvt_6_2_.getXOffset() * 2, p_191110_3_ + lvt_6_2_.getZOffset() * 2, 0, 2);
            p_191110_1_.setIf(p_191110_2_ + lvt_7_1_.getXOffset() * 2, p_191110_3_ + lvt_7_1_.getZOffset() * 2, 0, 2);
         }
      }

      private boolean cleanEdges(WoodlandMansionPieces.SimpleGrid p_191111_1_) {
         boolean lvt_2_1_ = false;

         for(int lvt_3_1_ = 0; lvt_3_1_ < p_191111_1_.height; ++lvt_3_1_) {
            for(int lvt_4_1_ = 0; lvt_4_1_ < p_191111_1_.width; ++lvt_4_1_) {
               if (p_191111_1_.get(lvt_4_1_, lvt_3_1_) == 0) {
                  int lvt_5_1_ = 0;
                  int lvt_5_1_ = lvt_5_1_ + (isHouse(p_191111_1_, lvt_4_1_ + 1, lvt_3_1_) ? 1 : 0);
                  lvt_5_1_ += isHouse(p_191111_1_, lvt_4_1_ - 1, lvt_3_1_) ? 1 : 0;
                  lvt_5_1_ += isHouse(p_191111_1_, lvt_4_1_, lvt_3_1_ + 1) ? 1 : 0;
                  lvt_5_1_ += isHouse(p_191111_1_, lvt_4_1_, lvt_3_1_ - 1) ? 1 : 0;
                  if (lvt_5_1_ >= 3) {
                     p_191111_1_.set(lvt_4_1_, lvt_3_1_, 2);
                     lvt_2_1_ = true;
                  } else if (lvt_5_1_ == 2) {
                     int lvt_6_1_ = 0;
                     int lvt_6_1_ = lvt_6_1_ + (isHouse(p_191111_1_, lvt_4_1_ + 1, lvt_3_1_ + 1) ? 1 : 0);
                     lvt_6_1_ += isHouse(p_191111_1_, lvt_4_1_ - 1, lvt_3_1_ + 1) ? 1 : 0;
                     lvt_6_1_ += isHouse(p_191111_1_, lvt_4_1_ + 1, lvt_3_1_ - 1) ? 1 : 0;
                     lvt_6_1_ += isHouse(p_191111_1_, lvt_4_1_ - 1, lvt_3_1_ - 1) ? 1 : 0;
                     if (lvt_6_1_ <= 1) {
                        p_191111_1_.set(lvt_4_1_, lvt_3_1_, 2);
                        lvt_2_1_ = true;
                     }
                  }
               }
            }
         }

         return lvt_2_1_;
      }

      private void setupThirdFloor() {
         List<Tuple<Integer, Integer>> lvt_1_1_ = Lists.newArrayList();
         WoodlandMansionPieces.SimpleGrid lvt_2_1_ = this.floorRooms[1];

         int lvt_4_2_;
         int lvt_6_2_;
         for(int lvt_3_1_ = 0; lvt_3_1_ < this.thirdFloorGrid.height; ++lvt_3_1_) {
            for(lvt_4_2_ = 0; lvt_4_2_ < this.thirdFloorGrid.width; ++lvt_4_2_) {
               int lvt_5_1_ = lvt_2_1_.get(lvt_4_2_, lvt_3_1_);
               lvt_6_2_ = lvt_5_1_ & 983040;
               if (lvt_6_2_ == 131072 && (lvt_5_1_ & 2097152) == 2097152) {
                  lvt_1_1_.add(new Tuple(lvt_4_2_, lvt_3_1_));
               }
            }
         }

         if (lvt_1_1_.isEmpty()) {
            this.thirdFloorGrid.set(0, 0, this.thirdFloorGrid.width, this.thirdFloorGrid.height, 5);
         } else {
            Tuple<Integer, Integer> lvt_3_2_ = (Tuple)lvt_1_1_.get(this.random.nextInt(lvt_1_1_.size()));
            lvt_4_2_ = lvt_2_1_.get((Integer)lvt_3_2_.getA(), (Integer)lvt_3_2_.getB());
            lvt_2_1_.set((Integer)lvt_3_2_.getA(), (Integer)lvt_3_2_.getB(), lvt_4_2_ | 4194304);
            Direction lvt_5_2_ = this.get1x2RoomDirection(this.baseGrid, (Integer)lvt_3_2_.getA(), (Integer)lvt_3_2_.getB(), 1, lvt_4_2_ & '\uffff');
            lvt_6_2_ = (Integer)lvt_3_2_.getA() + lvt_5_2_.getXOffset();
            int lvt_7_1_ = (Integer)lvt_3_2_.getB() + lvt_5_2_.getZOffset();

            for(int lvt_8_1_ = 0; lvt_8_1_ < this.thirdFloorGrid.height; ++lvt_8_1_) {
               for(int lvt_9_1_ = 0; lvt_9_1_ < this.thirdFloorGrid.width; ++lvt_9_1_) {
                  if (!isHouse(this.baseGrid, lvt_9_1_, lvt_8_1_)) {
                     this.thirdFloorGrid.set(lvt_9_1_, lvt_8_1_, 5);
                  } else if (lvt_9_1_ == (Integer)lvt_3_2_.getA() && lvt_8_1_ == (Integer)lvt_3_2_.getB()) {
                     this.thirdFloorGrid.set(lvt_9_1_, lvt_8_1_, 3);
                  } else if (lvt_9_1_ == lvt_6_2_ && lvt_8_1_ == lvt_7_1_) {
                     this.thirdFloorGrid.set(lvt_9_1_, lvt_8_1_, 3);
                     this.floorRooms[2].set(lvt_9_1_, lvt_8_1_, 8388608);
                  }
               }
            }

            List<Direction> lvt_8_2_ = Lists.newArrayList();
            Iterator var14 = Direction.Plane.HORIZONTAL.iterator();

            while(var14.hasNext()) {
               Direction lvt_10_1_ = (Direction)var14.next();
               if (this.thirdFloorGrid.get(lvt_6_2_ + lvt_10_1_.getXOffset(), lvt_7_1_ + lvt_10_1_.getZOffset()) == 0) {
                  lvt_8_2_.add(lvt_10_1_);
               }
            }

            if (lvt_8_2_.isEmpty()) {
               this.thirdFloorGrid.set(0, 0, this.thirdFloorGrid.width, this.thirdFloorGrid.height, 5);
               lvt_2_1_.set((Integer)lvt_3_2_.getA(), (Integer)lvt_3_2_.getB(), lvt_4_2_);
            } else {
               Direction lvt_9_2_ = (Direction)lvt_8_2_.get(this.random.nextInt(lvt_8_2_.size()));
               this.recursiveCorridor(this.thirdFloorGrid, lvt_6_2_ + lvt_9_2_.getXOffset(), lvt_7_1_ + lvt_9_2_.getZOffset(), lvt_9_2_, 4);

               while(this.cleanEdges(this.thirdFloorGrid)) {
               }

            }
         }
      }

      private void identifyRooms(WoodlandMansionPieces.SimpleGrid p_191116_1_, WoodlandMansionPieces.SimpleGrid p_191116_2_) {
         List<Tuple<Integer, Integer>> lvt_3_1_ = Lists.newArrayList();

         int lvt_4_2_;
         for(lvt_4_2_ = 0; lvt_4_2_ < p_191116_1_.height; ++lvt_4_2_) {
            for(int lvt_5_1_ = 0; lvt_5_1_ < p_191116_1_.width; ++lvt_5_1_) {
               if (p_191116_1_.get(lvt_5_1_, lvt_4_2_) == 2) {
                  lvt_3_1_.add(new Tuple(lvt_5_1_, lvt_4_2_));
               }
            }
         }

         Collections.shuffle(lvt_3_1_, this.random);
         lvt_4_2_ = 10;
         Iterator var19 = lvt_3_1_.iterator();

         while(true) {
            int lvt_7_1_;
            int lvt_8_1_;
            do {
               if (!var19.hasNext()) {
                  return;
               }

               Tuple<Integer, Integer> lvt_6_1_ = (Tuple)var19.next();
               lvt_7_1_ = (Integer)lvt_6_1_.getA();
               lvt_8_1_ = (Integer)lvt_6_1_.getB();
            } while(p_191116_2_.get(lvt_7_1_, lvt_8_1_) != 0);

            int lvt_9_1_ = lvt_7_1_;
            int lvt_10_1_ = lvt_7_1_;
            int lvt_11_1_ = lvt_8_1_;
            int lvt_12_1_ = lvt_8_1_;
            int lvt_13_1_ = 65536;
            if (p_191116_2_.get(lvt_7_1_ + 1, lvt_8_1_) == 0 && p_191116_2_.get(lvt_7_1_, lvt_8_1_ + 1) == 0 && p_191116_2_.get(lvt_7_1_ + 1, lvt_8_1_ + 1) == 0 && p_191116_1_.get(lvt_7_1_ + 1, lvt_8_1_) == 2 && p_191116_1_.get(lvt_7_1_, lvt_8_1_ + 1) == 2 && p_191116_1_.get(lvt_7_1_ + 1, lvt_8_1_ + 1) == 2) {
               lvt_10_1_ = lvt_7_1_ + 1;
               lvt_12_1_ = lvt_8_1_ + 1;
               lvt_13_1_ = 262144;
            } else if (p_191116_2_.get(lvt_7_1_ - 1, lvt_8_1_) == 0 && p_191116_2_.get(lvt_7_1_, lvt_8_1_ + 1) == 0 && p_191116_2_.get(lvt_7_1_ - 1, lvt_8_1_ + 1) == 0 && p_191116_1_.get(lvt_7_1_ - 1, lvt_8_1_) == 2 && p_191116_1_.get(lvt_7_1_, lvt_8_1_ + 1) == 2 && p_191116_1_.get(lvt_7_1_ - 1, lvt_8_1_ + 1) == 2) {
               lvt_9_1_ = lvt_7_1_ - 1;
               lvt_12_1_ = lvt_8_1_ + 1;
               lvt_13_1_ = 262144;
            } else if (p_191116_2_.get(lvt_7_1_ - 1, lvt_8_1_) == 0 && p_191116_2_.get(lvt_7_1_, lvt_8_1_ - 1) == 0 && p_191116_2_.get(lvt_7_1_ - 1, lvt_8_1_ - 1) == 0 && p_191116_1_.get(lvt_7_1_ - 1, lvt_8_1_) == 2 && p_191116_1_.get(lvt_7_1_, lvt_8_1_ - 1) == 2 && p_191116_1_.get(lvt_7_1_ - 1, lvt_8_1_ - 1) == 2) {
               lvt_9_1_ = lvt_7_1_ - 1;
               lvt_11_1_ = lvt_8_1_ - 1;
               lvt_13_1_ = 262144;
            } else if (p_191116_2_.get(lvt_7_1_ + 1, lvt_8_1_) == 0 && p_191116_1_.get(lvt_7_1_ + 1, lvt_8_1_) == 2) {
               lvt_10_1_ = lvt_7_1_ + 1;
               lvt_13_1_ = 131072;
            } else if (p_191116_2_.get(lvt_7_1_, lvt_8_1_ + 1) == 0 && p_191116_1_.get(lvt_7_1_, lvt_8_1_ + 1) == 2) {
               lvt_12_1_ = lvt_8_1_ + 1;
               lvt_13_1_ = 131072;
            } else if (p_191116_2_.get(lvt_7_1_ - 1, lvt_8_1_) == 0 && p_191116_1_.get(lvt_7_1_ - 1, lvt_8_1_) == 2) {
               lvt_9_1_ = lvt_7_1_ - 1;
               lvt_13_1_ = 131072;
            } else if (p_191116_2_.get(lvt_7_1_, lvt_8_1_ - 1) == 0 && p_191116_1_.get(lvt_7_1_, lvt_8_1_ - 1) == 2) {
               lvt_11_1_ = lvt_8_1_ - 1;
               lvt_13_1_ = 131072;
            }

            int lvt_14_1_ = this.random.nextBoolean() ? lvt_9_1_ : lvt_10_1_;
            int lvt_15_1_ = this.random.nextBoolean() ? lvt_11_1_ : lvt_12_1_;
            int lvt_16_1_ = 2097152;
            if (!p_191116_1_.edgesTo(lvt_14_1_, lvt_15_1_, 1)) {
               lvt_14_1_ = lvt_14_1_ == lvt_9_1_ ? lvt_10_1_ : lvt_9_1_;
               lvt_15_1_ = lvt_15_1_ == lvt_11_1_ ? lvt_12_1_ : lvt_11_1_;
               if (!p_191116_1_.edgesTo(lvt_14_1_, lvt_15_1_, 1)) {
                  lvt_15_1_ = lvt_15_1_ == lvt_11_1_ ? lvt_12_1_ : lvt_11_1_;
                  if (!p_191116_1_.edgesTo(lvt_14_1_, lvt_15_1_, 1)) {
                     lvt_14_1_ = lvt_14_1_ == lvt_9_1_ ? lvt_10_1_ : lvt_9_1_;
                     lvt_15_1_ = lvt_15_1_ == lvt_11_1_ ? lvt_12_1_ : lvt_11_1_;
                     if (!p_191116_1_.edgesTo(lvt_14_1_, lvt_15_1_, 1)) {
                        lvt_16_1_ = 0;
                        lvt_14_1_ = lvt_9_1_;
                        lvt_15_1_ = lvt_11_1_;
                     }
                  }
               }
            }

            for(int lvt_17_1_ = lvt_11_1_; lvt_17_1_ <= lvt_12_1_; ++lvt_17_1_) {
               for(int lvt_18_1_ = lvt_9_1_; lvt_18_1_ <= lvt_10_1_; ++lvt_18_1_) {
                  if (lvt_18_1_ == lvt_14_1_ && lvt_17_1_ == lvt_15_1_) {
                     p_191116_2_.set(lvt_18_1_, lvt_17_1_, 1048576 | lvt_16_1_ | lvt_13_1_ | lvt_4_2_);
                  } else {
                     p_191116_2_.set(lvt_18_1_, lvt_17_1_, lvt_13_1_ | lvt_4_2_);
                  }
               }
            }

            ++lvt_4_2_;
         }
      }
   }

   static class Placer {
      private final TemplateManager templateManager;
      private final Random random;
      private int startX;
      private int startY;

      public Placer(TemplateManager p_i47361_1_, Random p_i47361_2_) {
         this.templateManager = p_i47361_1_;
         this.random = p_i47361_2_;
      }

      public void createMansion(BlockPos p_191125_1_, Rotation p_191125_2_, List<WoodlandMansionPieces.MansionTemplate> p_191125_3_, WoodlandMansionPieces.Grid p_191125_4_) {
         WoodlandMansionPieces.PlacementData lvt_5_1_ = new WoodlandMansionPieces.PlacementData();
         lvt_5_1_.position = p_191125_1_;
         lvt_5_1_.rotation = p_191125_2_;
         lvt_5_1_.wallType = "wall_flat";
         WoodlandMansionPieces.PlacementData lvt_6_1_ = new WoodlandMansionPieces.PlacementData();
         this.entrance(p_191125_3_, lvt_5_1_);
         lvt_6_1_.position = lvt_5_1_.position.up(8);
         lvt_6_1_.rotation = lvt_5_1_.rotation;
         lvt_6_1_.wallType = "wall_window";
         if (!p_191125_3_.isEmpty()) {
         }

         WoodlandMansionPieces.SimpleGrid lvt_7_1_ = p_191125_4_.baseGrid;
         WoodlandMansionPieces.SimpleGrid lvt_8_1_ = p_191125_4_.thirdFloorGrid;
         this.startX = p_191125_4_.entranceX + 1;
         this.startY = p_191125_4_.entranceY + 1;
         int lvt_9_1_ = p_191125_4_.entranceX + 1;
         int lvt_10_1_ = p_191125_4_.entranceY;
         this.traverseOuterWalls(p_191125_3_, lvt_5_1_, lvt_7_1_, Direction.SOUTH, this.startX, this.startY, lvt_9_1_, lvt_10_1_);
         this.traverseOuterWalls(p_191125_3_, lvt_6_1_, lvt_7_1_, Direction.SOUTH, this.startX, this.startY, lvt_9_1_, lvt_10_1_);
         WoodlandMansionPieces.PlacementData lvt_11_1_ = new WoodlandMansionPieces.PlacementData();
         lvt_11_1_.position = lvt_5_1_.position.up(19);
         lvt_11_1_.rotation = lvt_5_1_.rotation;
         lvt_11_1_.wallType = "wall_window";
         boolean lvt_12_1_ = false;

         int lvt_14_2_;
         for(int lvt_13_1_ = 0; lvt_13_1_ < lvt_8_1_.height && !lvt_12_1_; ++lvt_13_1_) {
            for(lvt_14_2_ = lvt_8_1_.width - 1; lvt_14_2_ >= 0 && !lvt_12_1_; --lvt_14_2_) {
               if (WoodlandMansionPieces.Grid.isHouse(lvt_8_1_, lvt_14_2_, lvt_13_1_)) {
                  lvt_11_1_.position = lvt_11_1_.position.offset(p_191125_2_.rotate(Direction.SOUTH), 8 + (lvt_13_1_ - this.startY) * 8);
                  lvt_11_1_.position = lvt_11_1_.position.offset(p_191125_2_.rotate(Direction.EAST), (lvt_14_2_ - this.startX) * 8);
                  this.traverseWallPiece(p_191125_3_, lvt_11_1_);
                  this.traverseOuterWalls(p_191125_3_, lvt_11_1_, lvt_8_1_, Direction.SOUTH, lvt_14_2_, lvt_13_1_, lvt_14_2_, lvt_13_1_);
                  lvt_12_1_ = true;
               }
            }
         }

         this.createRoof(p_191125_3_, p_191125_1_.up(16), p_191125_2_, lvt_7_1_, lvt_8_1_);
         this.createRoof(p_191125_3_, p_191125_1_.up(27), p_191125_2_, lvt_8_1_, (WoodlandMansionPieces.SimpleGrid)null);
         if (!p_191125_3_.isEmpty()) {
         }

         WoodlandMansionPieces.RoomCollection[] lvt_13_2_ = new WoodlandMansionPieces.RoomCollection[]{new WoodlandMansionPieces.FirstFloor(), new WoodlandMansionPieces.SecondFloor(), new WoodlandMansionPieces.ThirdFloor()};

         for(lvt_14_2_ = 0; lvt_14_2_ < 3; ++lvt_14_2_) {
            BlockPos lvt_15_1_ = p_191125_1_.up(8 * lvt_14_2_ + (lvt_14_2_ == 2 ? 3 : 0));
            WoodlandMansionPieces.SimpleGrid lvt_16_1_ = p_191125_4_.floorRooms[lvt_14_2_];
            WoodlandMansionPieces.SimpleGrid lvt_17_1_ = lvt_14_2_ == 2 ? lvt_8_1_ : lvt_7_1_;
            String lvt_18_1_ = lvt_14_2_ == 0 ? "carpet_south_1" : "carpet_south_2";
            String lvt_19_1_ = lvt_14_2_ == 0 ? "carpet_west_1" : "carpet_west_2";

            for(int lvt_20_1_ = 0; lvt_20_1_ < lvt_17_1_.height; ++lvt_20_1_) {
               for(int lvt_21_1_ = 0; lvt_21_1_ < lvt_17_1_.width; ++lvt_21_1_) {
                  if (lvt_17_1_.get(lvt_21_1_, lvt_20_1_) == 1) {
                     BlockPos lvt_22_1_ = lvt_15_1_.offset(p_191125_2_.rotate(Direction.SOUTH), 8 + (lvt_20_1_ - this.startY) * 8);
                     lvt_22_1_ = lvt_22_1_.offset(p_191125_2_.rotate(Direction.EAST), (lvt_21_1_ - this.startX) * 8);
                     p_191125_3_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "corridor_floor", lvt_22_1_, p_191125_2_));
                     if (lvt_17_1_.get(lvt_21_1_, lvt_20_1_ - 1) == 1 || (lvt_16_1_.get(lvt_21_1_, lvt_20_1_ - 1) & 8388608) == 8388608) {
                        p_191125_3_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "carpet_north", lvt_22_1_.offset(p_191125_2_.rotate(Direction.EAST), 1).up(), p_191125_2_));
                     }

                     if (lvt_17_1_.get(lvt_21_1_ + 1, lvt_20_1_) == 1 || (lvt_16_1_.get(lvt_21_1_ + 1, lvt_20_1_) & 8388608) == 8388608) {
                        p_191125_3_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "carpet_east", lvt_22_1_.offset(p_191125_2_.rotate(Direction.SOUTH), 1).offset(p_191125_2_.rotate(Direction.EAST), 5).up(), p_191125_2_));
                     }

                     if (lvt_17_1_.get(lvt_21_1_, lvt_20_1_ + 1) == 1 || (lvt_16_1_.get(lvt_21_1_, lvt_20_1_ + 1) & 8388608) == 8388608) {
                        p_191125_3_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, lvt_18_1_, lvt_22_1_.offset(p_191125_2_.rotate(Direction.SOUTH), 5).offset(p_191125_2_.rotate(Direction.WEST), 1), p_191125_2_));
                     }

                     if (lvt_17_1_.get(lvt_21_1_ - 1, lvt_20_1_) == 1 || (lvt_16_1_.get(lvt_21_1_ - 1, lvt_20_1_) & 8388608) == 8388608) {
                        p_191125_3_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, lvt_19_1_, lvt_22_1_.offset(p_191125_2_.rotate(Direction.WEST), 1).offset(p_191125_2_.rotate(Direction.NORTH), 1), p_191125_2_));
                     }
                  }
               }
            }

            String lvt_20_2_ = lvt_14_2_ == 0 ? "indoors_wall_1" : "indoors_wall_2";
            String lvt_21_2_ = lvt_14_2_ == 0 ? "indoors_door_1" : "indoors_door_2";
            List<Direction> lvt_22_2_ = Lists.newArrayList();

            for(int lvt_23_1_ = 0; lvt_23_1_ < lvt_17_1_.height; ++lvt_23_1_) {
               for(int lvt_24_1_ = 0; lvt_24_1_ < lvt_17_1_.width; ++lvt_24_1_) {
                  boolean lvt_25_1_ = lvt_14_2_ == 2 && lvt_17_1_.get(lvt_24_1_, lvt_23_1_) == 3;
                  if (lvt_17_1_.get(lvt_24_1_, lvt_23_1_) == 2 || lvt_25_1_) {
                     int lvt_26_1_ = lvt_16_1_.get(lvt_24_1_, lvt_23_1_);
                     int lvt_27_1_ = lvt_26_1_ & 983040;
                     int lvt_28_1_ = lvt_26_1_ & '\uffff';
                     lvt_25_1_ = lvt_25_1_ && (lvt_26_1_ & 8388608) == 8388608;
                     lvt_22_2_.clear();
                     if ((lvt_26_1_ & 2097152) == 2097152) {
                        Iterator var29 = Direction.Plane.HORIZONTAL.iterator();

                        while(var29.hasNext()) {
                           Direction lvt_30_1_ = (Direction)var29.next();
                           if (lvt_17_1_.get(lvt_24_1_ + lvt_30_1_.getXOffset(), lvt_23_1_ + lvt_30_1_.getZOffset()) == 1) {
                              lvt_22_2_.add(lvt_30_1_);
                           }
                        }
                     }

                     Direction lvt_29_1_ = null;
                     if (!lvt_22_2_.isEmpty()) {
                        lvt_29_1_ = (Direction)lvt_22_2_.get(this.random.nextInt(lvt_22_2_.size()));
                     } else if ((lvt_26_1_ & 1048576) == 1048576) {
                        lvt_29_1_ = Direction.UP;
                     }

                     BlockPos lvt_30_2_ = lvt_15_1_.offset(p_191125_2_.rotate(Direction.SOUTH), 8 + (lvt_23_1_ - this.startY) * 8);
                     lvt_30_2_ = lvt_30_2_.offset(p_191125_2_.rotate(Direction.EAST), -1 + (lvt_24_1_ - this.startX) * 8);
                     if (WoodlandMansionPieces.Grid.isHouse(lvt_17_1_, lvt_24_1_ - 1, lvt_23_1_) && !p_191125_4_.isRoomId(lvt_17_1_, lvt_24_1_ - 1, lvt_23_1_, lvt_14_2_, lvt_28_1_)) {
                        p_191125_3_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, lvt_29_1_ == Direction.WEST ? lvt_21_2_ : lvt_20_2_, lvt_30_2_, p_191125_2_));
                     }

                     BlockPos lvt_31_3_;
                     if (lvt_17_1_.get(lvt_24_1_ + 1, lvt_23_1_) == 1 && !lvt_25_1_) {
                        lvt_31_3_ = lvt_30_2_.offset(p_191125_2_.rotate(Direction.EAST), 8);
                        p_191125_3_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, lvt_29_1_ == Direction.EAST ? lvt_21_2_ : lvt_20_2_, lvt_31_3_, p_191125_2_));
                     }

                     if (WoodlandMansionPieces.Grid.isHouse(lvt_17_1_, lvt_24_1_, lvt_23_1_ + 1) && !p_191125_4_.isRoomId(lvt_17_1_, lvt_24_1_, lvt_23_1_ + 1, lvt_14_2_, lvt_28_1_)) {
                        lvt_31_3_ = lvt_30_2_.offset(p_191125_2_.rotate(Direction.SOUTH), 7);
                        lvt_31_3_ = lvt_31_3_.offset(p_191125_2_.rotate(Direction.EAST), 7);
                        p_191125_3_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, lvt_29_1_ == Direction.SOUTH ? lvt_21_2_ : lvt_20_2_, lvt_31_3_, p_191125_2_.add(Rotation.CLOCKWISE_90)));
                     }

                     if (lvt_17_1_.get(lvt_24_1_, lvt_23_1_ - 1) == 1 && !lvt_25_1_) {
                        lvt_31_3_ = lvt_30_2_.offset(p_191125_2_.rotate(Direction.NORTH), 1);
                        lvt_31_3_ = lvt_31_3_.offset(p_191125_2_.rotate(Direction.EAST), 7);
                        p_191125_3_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, lvt_29_1_ == Direction.NORTH ? lvt_21_2_ : lvt_20_2_, lvt_31_3_, p_191125_2_.add(Rotation.CLOCKWISE_90)));
                     }

                     if (lvt_27_1_ == 65536) {
                        this.addRoom1x1(p_191125_3_, lvt_30_2_, p_191125_2_, lvt_29_1_, lvt_13_2_[lvt_14_2_]);
                     } else {
                        Direction lvt_31_5_;
                        if (lvt_27_1_ == 131072 && lvt_29_1_ != null) {
                           lvt_31_5_ = p_191125_4_.get1x2RoomDirection(lvt_17_1_, lvt_24_1_, lvt_23_1_, lvt_14_2_, lvt_28_1_);
                           boolean lvt_32_1_ = (lvt_26_1_ & 4194304) == 4194304;
                           this.addRoom1x2(p_191125_3_, lvt_30_2_, p_191125_2_, lvt_31_5_, lvt_29_1_, lvt_13_2_[lvt_14_2_], lvt_32_1_);
                        } else if (lvt_27_1_ == 262144 && lvt_29_1_ != null && lvt_29_1_ != Direction.UP) {
                           lvt_31_5_ = lvt_29_1_.rotateY();
                           if (!p_191125_4_.isRoomId(lvt_17_1_, lvt_24_1_ + lvt_31_5_.getXOffset(), lvt_23_1_ + lvt_31_5_.getZOffset(), lvt_14_2_, lvt_28_1_)) {
                              lvt_31_5_ = lvt_31_5_.getOpposite();
                           }

                           this.addRoom2x2(p_191125_3_, lvt_30_2_, p_191125_2_, lvt_31_5_, lvt_29_1_, lvt_13_2_[lvt_14_2_]);
                        } else if (lvt_27_1_ == 262144 && lvt_29_1_ == Direction.UP) {
                           this.addRoom2x2Secret(p_191125_3_, lvt_30_2_, p_191125_2_, lvt_13_2_[lvt_14_2_]);
                        }
                     }
                  }
               }
            }
         }

      }

      private void traverseOuterWalls(List<WoodlandMansionPieces.MansionTemplate> p_191130_1_, WoodlandMansionPieces.PlacementData p_191130_2_, WoodlandMansionPieces.SimpleGrid p_191130_3_, Direction p_191130_4_, int p_191130_5_, int p_191130_6_, int p_191130_7_, int p_191130_8_) {
         int lvt_9_1_ = p_191130_5_;
         int lvt_10_1_ = p_191130_6_;
         Direction lvt_11_1_ = p_191130_4_;

         do {
            if (!WoodlandMansionPieces.Grid.isHouse(p_191130_3_, lvt_9_1_ + p_191130_4_.getXOffset(), lvt_10_1_ + p_191130_4_.getZOffset())) {
               this.traverseTurn(p_191130_1_, p_191130_2_);
               p_191130_4_ = p_191130_4_.rotateY();
               if (lvt_9_1_ != p_191130_7_ || lvt_10_1_ != p_191130_8_ || lvt_11_1_ != p_191130_4_) {
                  this.traverseWallPiece(p_191130_1_, p_191130_2_);
               }
            } else if (WoodlandMansionPieces.Grid.isHouse(p_191130_3_, lvt_9_1_ + p_191130_4_.getXOffset(), lvt_10_1_ + p_191130_4_.getZOffset()) && WoodlandMansionPieces.Grid.isHouse(p_191130_3_, lvt_9_1_ + p_191130_4_.getXOffset() + p_191130_4_.rotateYCCW().getXOffset(), lvt_10_1_ + p_191130_4_.getZOffset() + p_191130_4_.rotateYCCW().getZOffset())) {
               this.traverseInnerTurn(p_191130_1_, p_191130_2_);
               lvt_9_1_ += p_191130_4_.getXOffset();
               lvt_10_1_ += p_191130_4_.getZOffset();
               p_191130_4_ = p_191130_4_.rotateYCCW();
            } else {
               lvt_9_1_ += p_191130_4_.getXOffset();
               lvt_10_1_ += p_191130_4_.getZOffset();
               if (lvt_9_1_ != p_191130_7_ || lvt_10_1_ != p_191130_8_ || lvt_11_1_ != p_191130_4_) {
                  this.traverseWallPiece(p_191130_1_, p_191130_2_);
               }
            }
         } while(lvt_9_1_ != p_191130_7_ || lvt_10_1_ != p_191130_8_ || lvt_11_1_ != p_191130_4_);

      }

      private void createRoof(List<WoodlandMansionPieces.MansionTemplate> p_191123_1_, BlockPos p_191123_2_, Rotation p_191123_3_, WoodlandMansionPieces.SimpleGrid p_191123_4_, @Nullable WoodlandMansionPieces.SimpleGrid p_191123_5_) {
         int lvt_6_2_;
         int lvt_7_2_;
         BlockPos lvt_8_2_;
         boolean lvt_9_3_;
         BlockPos lvt_10_12_;
         for(lvt_6_2_ = 0; lvt_6_2_ < p_191123_4_.height; ++lvt_6_2_) {
            for(lvt_7_2_ = 0; lvt_7_2_ < p_191123_4_.width; ++lvt_7_2_) {
               lvt_8_2_ = p_191123_2_.offset(p_191123_3_.rotate(Direction.SOUTH), 8 + (lvt_6_2_ - this.startY) * 8);
               lvt_8_2_ = lvt_8_2_.offset(p_191123_3_.rotate(Direction.EAST), (lvt_7_2_ - this.startX) * 8);
               lvt_9_3_ = p_191123_5_ != null && WoodlandMansionPieces.Grid.isHouse(p_191123_5_, lvt_7_2_, lvt_6_2_);
               if (WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_, lvt_6_2_) && !lvt_9_3_) {
                  p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "roof", lvt_8_2_.up(3), p_191123_3_));
                  if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_ + 1, lvt_6_2_)) {
                     lvt_10_12_ = lvt_8_2_.offset(p_191123_3_.rotate(Direction.EAST), 6);
                     p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "roof_front", lvt_10_12_, p_191123_3_));
                  }

                  if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_ - 1, lvt_6_2_)) {
                     lvt_10_12_ = lvt_8_2_.offset(p_191123_3_.rotate(Direction.EAST), 0);
                     lvt_10_12_ = lvt_10_12_.offset(p_191123_3_.rotate(Direction.SOUTH), 7);
                     p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "roof_front", lvt_10_12_, p_191123_3_.add(Rotation.CLOCKWISE_180)));
                  }

                  if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_, lvt_6_2_ - 1)) {
                     lvt_10_12_ = lvt_8_2_.offset(p_191123_3_.rotate(Direction.WEST), 1);
                     p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "roof_front", lvt_10_12_, p_191123_3_.add(Rotation.COUNTERCLOCKWISE_90)));
                  }

                  if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_, lvt_6_2_ + 1)) {
                     lvt_10_12_ = lvt_8_2_.offset(p_191123_3_.rotate(Direction.EAST), 6);
                     lvt_10_12_ = lvt_10_12_.offset(p_191123_3_.rotate(Direction.SOUTH), 6);
                     p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "roof_front", lvt_10_12_, p_191123_3_.add(Rotation.CLOCKWISE_90)));
                  }
               }
            }
         }

         if (p_191123_5_ != null) {
            for(lvt_6_2_ = 0; lvt_6_2_ < p_191123_4_.height; ++lvt_6_2_) {
               for(lvt_7_2_ = 0; lvt_7_2_ < p_191123_4_.width; ++lvt_7_2_) {
                  lvt_8_2_ = p_191123_2_.offset(p_191123_3_.rotate(Direction.SOUTH), 8 + (lvt_6_2_ - this.startY) * 8);
                  lvt_8_2_ = lvt_8_2_.offset(p_191123_3_.rotate(Direction.EAST), (lvt_7_2_ - this.startX) * 8);
                  lvt_9_3_ = WoodlandMansionPieces.Grid.isHouse(p_191123_5_, lvt_7_2_, lvt_6_2_);
                  if (WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_, lvt_6_2_) && lvt_9_3_) {
                     if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_ + 1, lvt_6_2_)) {
                        lvt_10_12_ = lvt_8_2_.offset(p_191123_3_.rotate(Direction.EAST), 7);
                        p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "small_wall", lvt_10_12_, p_191123_3_));
                     }

                     if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_ - 1, lvt_6_2_)) {
                        lvt_10_12_ = lvt_8_2_.offset(p_191123_3_.rotate(Direction.WEST), 1);
                        lvt_10_12_ = lvt_10_12_.offset(p_191123_3_.rotate(Direction.SOUTH), 6);
                        p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "small_wall", lvt_10_12_, p_191123_3_.add(Rotation.CLOCKWISE_180)));
                     }

                     if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_, lvt_6_2_ - 1)) {
                        lvt_10_12_ = lvt_8_2_.offset(p_191123_3_.rotate(Direction.WEST), 0);
                        lvt_10_12_ = lvt_10_12_.offset(p_191123_3_.rotate(Direction.NORTH), 1);
                        p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "small_wall", lvt_10_12_, p_191123_3_.add(Rotation.COUNTERCLOCKWISE_90)));
                     }

                     if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_, lvt_6_2_ + 1)) {
                        lvt_10_12_ = lvt_8_2_.offset(p_191123_3_.rotate(Direction.EAST), 6);
                        lvt_10_12_ = lvt_10_12_.offset(p_191123_3_.rotate(Direction.SOUTH), 7);
                        p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "small_wall", lvt_10_12_, p_191123_3_.add(Rotation.CLOCKWISE_90)));
                     }

                     if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_ + 1, lvt_6_2_)) {
                        if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_, lvt_6_2_ - 1)) {
                           lvt_10_12_ = lvt_8_2_.offset(p_191123_3_.rotate(Direction.EAST), 7);
                           lvt_10_12_ = lvt_10_12_.offset(p_191123_3_.rotate(Direction.NORTH), 2);
                           p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "small_wall_corner", lvt_10_12_, p_191123_3_));
                        }

                        if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_, lvt_6_2_ + 1)) {
                           lvt_10_12_ = lvt_8_2_.offset(p_191123_3_.rotate(Direction.EAST), 8);
                           lvt_10_12_ = lvt_10_12_.offset(p_191123_3_.rotate(Direction.SOUTH), 7);
                           p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "small_wall_corner", lvt_10_12_, p_191123_3_.add(Rotation.CLOCKWISE_90)));
                        }
                     }

                     if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_ - 1, lvt_6_2_)) {
                        if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_, lvt_6_2_ - 1)) {
                           lvt_10_12_ = lvt_8_2_.offset(p_191123_3_.rotate(Direction.WEST), 2);
                           lvt_10_12_ = lvt_10_12_.offset(p_191123_3_.rotate(Direction.NORTH), 1);
                           p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "small_wall_corner", lvt_10_12_, p_191123_3_.add(Rotation.COUNTERCLOCKWISE_90)));
                        }

                        if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_, lvt_6_2_ + 1)) {
                           lvt_10_12_ = lvt_8_2_.offset(p_191123_3_.rotate(Direction.WEST), 1);
                           lvt_10_12_ = lvt_10_12_.offset(p_191123_3_.rotate(Direction.SOUTH), 8);
                           p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "small_wall_corner", lvt_10_12_, p_191123_3_.add(Rotation.CLOCKWISE_180)));
                        }
                     }
                  }
               }
            }
         }

         for(lvt_6_2_ = 0; lvt_6_2_ < p_191123_4_.height; ++lvt_6_2_) {
            for(lvt_7_2_ = 0; lvt_7_2_ < p_191123_4_.width; ++lvt_7_2_) {
               lvt_8_2_ = p_191123_2_.offset(p_191123_3_.rotate(Direction.SOUTH), 8 + (lvt_6_2_ - this.startY) * 8);
               lvt_8_2_ = lvt_8_2_.offset(p_191123_3_.rotate(Direction.EAST), (lvt_7_2_ - this.startX) * 8);
               lvt_9_3_ = p_191123_5_ != null && WoodlandMansionPieces.Grid.isHouse(p_191123_5_, lvt_7_2_, lvt_6_2_);
               if (WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_, lvt_6_2_) && !lvt_9_3_) {
                  BlockPos lvt_11_6_;
                  if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_ + 1, lvt_6_2_)) {
                     lvt_10_12_ = lvt_8_2_.offset(p_191123_3_.rotate(Direction.EAST), 6);
                     if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_, lvt_6_2_ + 1)) {
                        lvt_11_6_ = lvt_10_12_.offset(p_191123_3_.rotate(Direction.SOUTH), 6);
                        p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "roof_corner", lvt_11_6_, p_191123_3_));
                     } else if (WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_ + 1, lvt_6_2_ + 1)) {
                        lvt_11_6_ = lvt_10_12_.offset(p_191123_3_.rotate(Direction.SOUTH), 5);
                        p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "roof_inner_corner", lvt_11_6_, p_191123_3_));
                     }

                     if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_, lvt_6_2_ - 1)) {
                        p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "roof_corner", lvt_10_12_, p_191123_3_.add(Rotation.COUNTERCLOCKWISE_90)));
                     } else if (WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_ + 1, lvt_6_2_ - 1)) {
                        lvt_11_6_ = lvt_8_2_.offset(p_191123_3_.rotate(Direction.EAST), 9);
                        lvt_11_6_ = lvt_11_6_.offset(p_191123_3_.rotate(Direction.NORTH), 2);
                        p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "roof_inner_corner", lvt_11_6_, p_191123_3_.add(Rotation.CLOCKWISE_90)));
                     }
                  }

                  if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_ - 1, lvt_6_2_)) {
                     lvt_10_12_ = lvt_8_2_.offset(p_191123_3_.rotate(Direction.EAST), 0);
                     lvt_10_12_ = lvt_10_12_.offset(p_191123_3_.rotate(Direction.SOUTH), 0);
                     if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_, lvt_6_2_ + 1)) {
                        lvt_11_6_ = lvt_10_12_.offset(p_191123_3_.rotate(Direction.SOUTH), 6);
                        p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "roof_corner", lvt_11_6_, p_191123_3_.add(Rotation.CLOCKWISE_90)));
                     } else if (WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_ - 1, lvt_6_2_ + 1)) {
                        lvt_11_6_ = lvt_10_12_.offset(p_191123_3_.rotate(Direction.SOUTH), 8);
                        lvt_11_6_ = lvt_11_6_.offset(p_191123_3_.rotate(Direction.WEST), 3);
                        p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "roof_inner_corner", lvt_11_6_, p_191123_3_.add(Rotation.COUNTERCLOCKWISE_90)));
                     }

                     if (!WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_, lvt_6_2_ - 1)) {
                        p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "roof_corner", lvt_10_12_, p_191123_3_.add(Rotation.CLOCKWISE_180)));
                     } else if (WoodlandMansionPieces.Grid.isHouse(p_191123_4_, lvt_7_2_ - 1, lvt_6_2_ - 1)) {
                        lvt_11_6_ = lvt_10_12_.offset(p_191123_3_.rotate(Direction.SOUTH), 1);
                        p_191123_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "roof_inner_corner", lvt_11_6_, p_191123_3_.add(Rotation.CLOCKWISE_180)));
                     }
                  }
               }
            }
         }

      }

      private void entrance(List<WoodlandMansionPieces.MansionTemplate> p_191133_1_, WoodlandMansionPieces.PlacementData p_191133_2_) {
         Direction lvt_3_1_ = p_191133_2_.rotation.rotate(Direction.WEST);
         p_191133_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "entrance", p_191133_2_.position.offset(lvt_3_1_, 9), p_191133_2_.rotation));
         p_191133_2_.position = p_191133_2_.position.offset(p_191133_2_.rotation.rotate(Direction.SOUTH), 16);
      }

      private void traverseWallPiece(List<WoodlandMansionPieces.MansionTemplate> p_191131_1_, WoodlandMansionPieces.PlacementData p_191131_2_) {
         p_191131_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, p_191131_2_.wallType, p_191131_2_.position.offset(p_191131_2_.rotation.rotate(Direction.EAST), 7), p_191131_2_.rotation));
         p_191131_2_.position = p_191131_2_.position.offset(p_191131_2_.rotation.rotate(Direction.SOUTH), 8);
      }

      private void traverseTurn(List<WoodlandMansionPieces.MansionTemplate> p_191124_1_, WoodlandMansionPieces.PlacementData p_191124_2_) {
         p_191124_2_.position = p_191124_2_.position.offset(p_191124_2_.rotation.rotate(Direction.SOUTH), -1);
         p_191124_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, "wall_corner", p_191124_2_.position, p_191124_2_.rotation));
         p_191124_2_.position = p_191124_2_.position.offset(p_191124_2_.rotation.rotate(Direction.SOUTH), -7);
         p_191124_2_.position = p_191124_2_.position.offset(p_191124_2_.rotation.rotate(Direction.WEST), -6);
         p_191124_2_.rotation = p_191124_2_.rotation.add(Rotation.CLOCKWISE_90);
      }

      private void traverseInnerTurn(List<WoodlandMansionPieces.MansionTemplate> p_191126_1_, WoodlandMansionPieces.PlacementData p_191126_2_) {
         p_191126_2_.position = p_191126_2_.position.offset(p_191126_2_.rotation.rotate(Direction.SOUTH), 6);
         p_191126_2_.position = p_191126_2_.position.offset(p_191126_2_.rotation.rotate(Direction.EAST), 8);
         p_191126_2_.rotation = p_191126_2_.rotation.add(Rotation.COUNTERCLOCKWISE_90);
      }

      private void addRoom1x1(List<WoodlandMansionPieces.MansionTemplate> p_191129_1_, BlockPos p_191129_2_, Rotation p_191129_3_, Direction p_191129_4_, WoodlandMansionPieces.RoomCollection p_191129_5_) {
         Rotation lvt_6_1_ = Rotation.NONE;
         String lvt_7_1_ = p_191129_5_.get1x1(this.random);
         if (p_191129_4_ != Direction.EAST) {
            if (p_191129_4_ == Direction.NORTH) {
               lvt_6_1_ = lvt_6_1_.add(Rotation.COUNTERCLOCKWISE_90);
            } else if (p_191129_4_ == Direction.WEST) {
               lvt_6_1_ = lvt_6_1_.add(Rotation.CLOCKWISE_180);
            } else if (p_191129_4_ == Direction.SOUTH) {
               lvt_6_1_ = lvt_6_1_.add(Rotation.CLOCKWISE_90);
            } else {
               lvt_7_1_ = p_191129_5_.get1x1Secret(this.random);
            }
         }

         BlockPos lvt_8_1_ = Template.getZeroPositionWithTransform(new BlockPos(1, 0, 0), Mirror.NONE, lvt_6_1_, 7, 7);
         lvt_6_1_ = lvt_6_1_.add(p_191129_3_);
         lvt_8_1_ = lvt_8_1_.rotate(p_191129_3_);
         BlockPos lvt_9_1_ = p_191129_2_.add(lvt_8_1_.getX(), 0, lvt_8_1_.getZ());
         p_191129_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, lvt_7_1_, lvt_9_1_, lvt_6_1_));
      }

      private void addRoom1x2(List<WoodlandMansionPieces.MansionTemplate> p_191132_1_, BlockPos p_191132_2_, Rotation p_191132_3_, Direction p_191132_4_, Direction p_191132_5_, WoodlandMansionPieces.RoomCollection p_191132_6_, boolean p_191132_7_) {
         BlockPos lvt_8_14_;
         if (p_191132_5_ == Direction.EAST && p_191132_4_ == Direction.SOUTH) {
            lvt_8_14_ = p_191132_2_.offset(p_191132_3_.rotate(Direction.EAST), 1);
            p_191132_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, p_191132_6_.get1x2SideEntrance(this.random, p_191132_7_), lvt_8_14_, p_191132_3_));
         } else if (p_191132_5_ == Direction.EAST && p_191132_4_ == Direction.NORTH) {
            lvt_8_14_ = p_191132_2_.offset(p_191132_3_.rotate(Direction.EAST), 1);
            lvt_8_14_ = lvt_8_14_.offset(p_191132_3_.rotate(Direction.SOUTH), 6);
            p_191132_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, p_191132_6_.get1x2SideEntrance(this.random, p_191132_7_), lvt_8_14_, p_191132_3_, Mirror.LEFT_RIGHT));
         } else if (p_191132_5_ == Direction.WEST && p_191132_4_ == Direction.NORTH) {
            lvt_8_14_ = p_191132_2_.offset(p_191132_3_.rotate(Direction.EAST), 7);
            lvt_8_14_ = lvt_8_14_.offset(p_191132_3_.rotate(Direction.SOUTH), 6);
            p_191132_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, p_191132_6_.get1x2SideEntrance(this.random, p_191132_7_), lvt_8_14_, p_191132_3_.add(Rotation.CLOCKWISE_180)));
         } else if (p_191132_5_ == Direction.WEST && p_191132_4_ == Direction.SOUTH) {
            lvt_8_14_ = p_191132_2_.offset(p_191132_3_.rotate(Direction.EAST), 7);
            p_191132_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, p_191132_6_.get1x2SideEntrance(this.random, p_191132_7_), lvt_8_14_, p_191132_3_, Mirror.FRONT_BACK));
         } else if (p_191132_5_ == Direction.SOUTH && p_191132_4_ == Direction.EAST) {
            lvt_8_14_ = p_191132_2_.offset(p_191132_3_.rotate(Direction.EAST), 1);
            p_191132_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, p_191132_6_.get1x2SideEntrance(this.random, p_191132_7_), lvt_8_14_, p_191132_3_.add(Rotation.CLOCKWISE_90), Mirror.LEFT_RIGHT));
         } else if (p_191132_5_ == Direction.SOUTH && p_191132_4_ == Direction.WEST) {
            lvt_8_14_ = p_191132_2_.offset(p_191132_3_.rotate(Direction.EAST), 7);
            p_191132_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, p_191132_6_.get1x2SideEntrance(this.random, p_191132_7_), lvt_8_14_, p_191132_3_.add(Rotation.CLOCKWISE_90)));
         } else if (p_191132_5_ == Direction.NORTH && p_191132_4_ == Direction.WEST) {
            lvt_8_14_ = p_191132_2_.offset(p_191132_3_.rotate(Direction.EAST), 7);
            lvt_8_14_ = lvt_8_14_.offset(p_191132_3_.rotate(Direction.SOUTH), 6);
            p_191132_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, p_191132_6_.get1x2SideEntrance(this.random, p_191132_7_), lvt_8_14_, p_191132_3_.add(Rotation.CLOCKWISE_90), Mirror.FRONT_BACK));
         } else if (p_191132_5_ == Direction.NORTH && p_191132_4_ == Direction.EAST) {
            lvt_8_14_ = p_191132_2_.offset(p_191132_3_.rotate(Direction.EAST), 1);
            lvt_8_14_ = lvt_8_14_.offset(p_191132_3_.rotate(Direction.SOUTH), 6);
            p_191132_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, p_191132_6_.get1x2SideEntrance(this.random, p_191132_7_), lvt_8_14_, p_191132_3_.add(Rotation.COUNTERCLOCKWISE_90)));
         } else if (p_191132_5_ == Direction.SOUTH && p_191132_4_ == Direction.NORTH) {
            lvt_8_14_ = p_191132_2_.offset(p_191132_3_.rotate(Direction.EAST), 1);
            lvt_8_14_ = lvt_8_14_.offset(p_191132_3_.rotate(Direction.NORTH), 8);
            p_191132_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, p_191132_6_.get1x2FrontEntrance(this.random, p_191132_7_), lvt_8_14_, p_191132_3_));
         } else if (p_191132_5_ == Direction.NORTH && p_191132_4_ == Direction.SOUTH) {
            lvt_8_14_ = p_191132_2_.offset(p_191132_3_.rotate(Direction.EAST), 7);
            lvt_8_14_ = lvt_8_14_.offset(p_191132_3_.rotate(Direction.SOUTH), 14);
            p_191132_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, p_191132_6_.get1x2FrontEntrance(this.random, p_191132_7_), lvt_8_14_, p_191132_3_.add(Rotation.CLOCKWISE_180)));
         } else if (p_191132_5_ == Direction.WEST && p_191132_4_ == Direction.EAST) {
            lvt_8_14_ = p_191132_2_.offset(p_191132_3_.rotate(Direction.EAST), 15);
            p_191132_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, p_191132_6_.get1x2FrontEntrance(this.random, p_191132_7_), lvt_8_14_, p_191132_3_.add(Rotation.CLOCKWISE_90)));
         } else if (p_191132_5_ == Direction.EAST && p_191132_4_ == Direction.WEST) {
            lvt_8_14_ = p_191132_2_.offset(p_191132_3_.rotate(Direction.WEST), 7);
            lvt_8_14_ = lvt_8_14_.offset(p_191132_3_.rotate(Direction.SOUTH), 6);
            p_191132_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, p_191132_6_.get1x2FrontEntrance(this.random, p_191132_7_), lvt_8_14_, p_191132_3_.add(Rotation.COUNTERCLOCKWISE_90)));
         } else if (p_191132_5_ == Direction.UP && p_191132_4_ == Direction.EAST) {
            lvt_8_14_ = p_191132_2_.offset(p_191132_3_.rotate(Direction.EAST), 15);
            p_191132_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, p_191132_6_.get1x2Secret(this.random), lvt_8_14_, p_191132_3_.add(Rotation.CLOCKWISE_90)));
         } else if (p_191132_5_ == Direction.UP && p_191132_4_ == Direction.SOUTH) {
            lvt_8_14_ = p_191132_2_.offset(p_191132_3_.rotate(Direction.EAST), 1);
            lvt_8_14_ = lvt_8_14_.offset(p_191132_3_.rotate(Direction.NORTH), 0);
            p_191132_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, p_191132_6_.get1x2Secret(this.random), lvt_8_14_, p_191132_3_));
         }

      }

      private void addRoom2x2(List<WoodlandMansionPieces.MansionTemplate> p_191127_1_, BlockPos p_191127_2_, Rotation p_191127_3_, Direction p_191127_4_, Direction p_191127_5_, WoodlandMansionPieces.RoomCollection p_191127_6_) {
         int lvt_7_1_ = 0;
         int lvt_8_1_ = 0;
         Rotation lvt_9_1_ = p_191127_3_;
         Mirror lvt_10_1_ = Mirror.NONE;
         if (p_191127_5_ == Direction.EAST && p_191127_4_ == Direction.SOUTH) {
            lvt_7_1_ = -7;
         } else if (p_191127_5_ == Direction.EAST && p_191127_4_ == Direction.NORTH) {
            lvt_7_1_ = -7;
            lvt_8_1_ = 6;
            lvt_10_1_ = Mirror.LEFT_RIGHT;
         } else if (p_191127_5_ == Direction.NORTH && p_191127_4_ == Direction.EAST) {
            lvt_7_1_ = 1;
            lvt_8_1_ = 14;
            lvt_9_1_ = p_191127_3_.add(Rotation.COUNTERCLOCKWISE_90);
         } else if (p_191127_5_ == Direction.NORTH && p_191127_4_ == Direction.WEST) {
            lvt_7_1_ = 7;
            lvt_8_1_ = 14;
            lvt_9_1_ = p_191127_3_.add(Rotation.COUNTERCLOCKWISE_90);
            lvt_10_1_ = Mirror.LEFT_RIGHT;
         } else if (p_191127_5_ == Direction.SOUTH && p_191127_4_ == Direction.WEST) {
            lvt_7_1_ = 7;
            lvt_8_1_ = -8;
            lvt_9_1_ = p_191127_3_.add(Rotation.CLOCKWISE_90);
         } else if (p_191127_5_ == Direction.SOUTH && p_191127_4_ == Direction.EAST) {
            lvt_7_1_ = 1;
            lvt_8_1_ = -8;
            lvt_9_1_ = p_191127_3_.add(Rotation.CLOCKWISE_90);
            lvt_10_1_ = Mirror.LEFT_RIGHT;
         } else if (p_191127_5_ == Direction.WEST && p_191127_4_ == Direction.NORTH) {
            lvt_7_1_ = 15;
            lvt_8_1_ = 6;
            lvt_9_1_ = p_191127_3_.add(Rotation.CLOCKWISE_180);
         } else if (p_191127_5_ == Direction.WEST && p_191127_4_ == Direction.SOUTH) {
            lvt_7_1_ = 15;
            lvt_10_1_ = Mirror.FRONT_BACK;
         }

         BlockPos lvt_11_1_ = p_191127_2_.offset(p_191127_3_.rotate(Direction.EAST), lvt_7_1_);
         lvt_11_1_ = lvt_11_1_.offset(p_191127_3_.rotate(Direction.SOUTH), lvt_8_1_);
         p_191127_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, p_191127_6_.get2x2(this.random), lvt_11_1_, lvt_9_1_, lvt_10_1_));
      }

      private void addRoom2x2Secret(List<WoodlandMansionPieces.MansionTemplate> p_191128_1_, BlockPos p_191128_2_, Rotation p_191128_3_, WoodlandMansionPieces.RoomCollection p_191128_4_) {
         BlockPos lvt_5_1_ = p_191128_2_.offset(p_191128_3_.rotate(Direction.EAST), 1);
         p_191128_1_.add(new WoodlandMansionPieces.MansionTemplate(this.templateManager, p_191128_4_.get2x2Secret(this.random), lvt_5_1_, p_191128_3_, Mirror.NONE));
      }
   }

   static class PlacementData {
      public Rotation rotation;
      public BlockPos position;
      public String wallType;

      private PlacementData() {
      }

      // $FF: synthetic method
      PlacementData(Object p_i47360_1_) {
         this();
      }
   }

   public static class MansionTemplate extends TemplateStructurePiece {
      private final String templateName;
      private final Rotation rotation;
      private final Mirror mirror;

      public MansionTemplate(TemplateManager p_i47355_1_, String p_i47355_2_, BlockPos p_i47355_3_, Rotation p_i47355_4_) {
         this(p_i47355_1_, p_i47355_2_, p_i47355_3_, p_i47355_4_, Mirror.NONE);
      }

      public MansionTemplate(TemplateManager p_i47356_1_, String p_i47356_2_, BlockPos p_i47356_3_, Rotation p_i47356_4_, Mirror p_i47356_5_) {
         super(IStructurePieceType.WMP, 0);
         this.templateName = p_i47356_2_;
         this.templatePosition = p_i47356_3_;
         this.rotation = p_i47356_4_;
         this.mirror = p_i47356_5_;
         this.loadTemplate(p_i47356_1_);
      }

      public MansionTemplate(TemplateManager p_i50615_1_, CompoundNBT p_i50615_2_) {
         super(IStructurePieceType.WMP, p_i50615_2_);
         this.templateName = p_i50615_2_.getString("Template");
         this.rotation = Rotation.valueOf(p_i50615_2_.getString("Rot"));
         this.mirror = Mirror.valueOf(p_i50615_2_.getString("Mi"));
         this.loadTemplate(p_i50615_1_);
      }

      private void loadTemplate(TemplateManager p_191081_1_) {
         Template lvt_2_1_ = p_191081_1_.getTemplateDefaulted(new ResourceLocation("woodland_mansion/" + this.templateName));
         PlacementSettings lvt_3_1_ = (new PlacementSettings()).setIgnoreEntities(true).setRotation(this.rotation).setMirror(this.mirror).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
         this.setup(lvt_2_1_, this.templatePosition, lvt_3_1_);
      }

      protected void readAdditional(CompoundNBT p_143011_1_) {
         super.readAdditional(p_143011_1_);
         p_143011_1_.putString("Template", this.templateName);
         p_143011_1_.putString("Rot", this.placeSettings.getRotation().name());
         p_143011_1_.putString("Mi", this.placeSettings.getMirror().name());
      }

      protected void handleDataMarker(String p_186175_1_, BlockPos p_186175_2_, IWorld p_186175_3_, Random p_186175_4_, MutableBoundingBox p_186175_5_) {
         if (p_186175_1_.startsWith("Chest")) {
            Rotation lvt_6_1_ = this.placeSettings.getRotation();
            BlockState lvt_7_1_ = Blocks.CHEST.getDefaultState();
            if ("ChestWest".equals(p_186175_1_)) {
               lvt_7_1_ = (BlockState)lvt_7_1_.with(ChestBlock.FACING, lvt_6_1_.rotate(Direction.WEST));
            } else if ("ChestEast".equals(p_186175_1_)) {
               lvt_7_1_ = (BlockState)lvt_7_1_.with(ChestBlock.FACING, lvt_6_1_.rotate(Direction.EAST));
            } else if ("ChestSouth".equals(p_186175_1_)) {
               lvt_7_1_ = (BlockState)lvt_7_1_.with(ChestBlock.FACING, lvt_6_1_.rotate(Direction.SOUTH));
            } else if ("ChestNorth".equals(p_186175_1_)) {
               lvt_7_1_ = (BlockState)lvt_7_1_.with(ChestBlock.FACING, lvt_6_1_.rotate(Direction.NORTH));
            }

            this.generateChest(p_186175_3_, p_186175_5_, p_186175_4_, p_186175_2_, LootTables.CHESTS_WOODLAND_MANSION, lvt_7_1_);
         } else {
            byte var8 = -1;
            switch(p_186175_1_.hashCode()) {
            case -1505748702:
               if (p_186175_1_.equals("Warrior")) {
                  var8 = 1;
               }
               break;
            case 2390418:
               if (p_186175_1_.equals("Mage")) {
                  var8 = 0;
               }
            }

            AbstractIllagerEntity lvt_6_4_;
            switch(var8) {
            case 0:
               lvt_6_4_ = (AbstractIllagerEntity)EntityType.EVOKER.create(p_186175_3_.getWorld());
               break;
            case 1:
               lvt_6_4_ = (AbstractIllagerEntity)EntityType.VINDICATOR.create(p_186175_3_.getWorld());
               break;
            default:
               return;
            }

            lvt_6_4_.enablePersistence();
            lvt_6_4_.moveToBlockPosAndAngles(p_186175_2_, 0.0F, 0.0F);
            lvt_6_4_.onInitialSpawn(p_186175_3_, p_186175_3_.getDifficultyForLocation(new BlockPos(lvt_6_4_)), SpawnReason.STRUCTURE, (ILivingEntityData)null, (CompoundNBT)null);
            p_186175_3_.addEntity(lvt_6_4_);
            p_186175_3_.setBlockState(p_186175_2_, Blocks.AIR.getDefaultState(), 2);
         }

      }
   }
}
