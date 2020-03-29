package net.minecraft.util.math.shapes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.AxisRotation;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class VoxelShapes {
   private static final VoxelShape FULL_CUBE = (VoxelShape)Util.make(() -> {
      VoxelShapePart lvt_0_1_ = new BitSetVoxelShapePart(1, 1, 1);
      lvt_0_1_.setFilled(0, 0, 0, true, true);
      return new VoxelShapeCube(lvt_0_1_);
   });
   public static final VoxelShape INFINITY = create(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
   private static final VoxelShape EMPTY = new VoxelShapeArray(new BitSetVoxelShapePart(0, 0, 0), new DoubleArrayList(new double[]{0.0D}), new DoubleArrayList(new double[]{0.0D}), new DoubleArrayList(new double[]{0.0D}));

   public static VoxelShape empty() {
      return EMPTY;
   }

   public static VoxelShape fullCube() {
      return FULL_CUBE;
   }

   public static VoxelShape create(double p_197873_0_, double p_197873_2_, double p_197873_4_, double p_197873_6_, double p_197873_8_, double p_197873_10_) {
      return create(new AxisAlignedBB(p_197873_0_, p_197873_2_, p_197873_4_, p_197873_6_, p_197873_8_, p_197873_10_));
   }

   public static VoxelShape create(AxisAlignedBB p_197881_0_) {
      int lvt_1_1_ = getPrecisionBits(p_197881_0_.minX, p_197881_0_.maxX);
      int lvt_2_1_ = getPrecisionBits(p_197881_0_.minY, p_197881_0_.maxY);
      int lvt_3_1_ = getPrecisionBits(p_197881_0_.minZ, p_197881_0_.maxZ);
      if (lvt_1_1_ >= 0 && lvt_2_1_ >= 0 && lvt_3_1_ >= 0) {
         if (lvt_1_1_ == 0 && lvt_2_1_ == 0 && lvt_3_1_ == 0) {
            return p_197881_0_.contains(0.5D, 0.5D, 0.5D) ? fullCube() : empty();
         } else {
            int lvt_4_1_ = 1 << lvt_1_1_;
            int lvt_5_1_ = 1 << lvt_2_1_;
            int lvt_6_1_ = 1 << lvt_3_1_;
            int lvt_7_1_ = (int)Math.round(p_197881_0_.minX * (double)lvt_4_1_);
            int lvt_8_1_ = (int)Math.round(p_197881_0_.maxX * (double)lvt_4_1_);
            int lvt_9_1_ = (int)Math.round(p_197881_0_.minY * (double)lvt_5_1_);
            int lvt_10_1_ = (int)Math.round(p_197881_0_.maxY * (double)lvt_5_1_);
            int lvt_11_1_ = (int)Math.round(p_197881_0_.minZ * (double)lvt_6_1_);
            int lvt_12_1_ = (int)Math.round(p_197881_0_.maxZ * (double)lvt_6_1_);
            BitSetVoxelShapePart lvt_13_1_ = new BitSetVoxelShapePart(lvt_4_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_, lvt_9_1_, lvt_11_1_, lvt_8_1_, lvt_10_1_, lvt_12_1_);

            for(long lvt_14_1_ = (long)lvt_7_1_; lvt_14_1_ < (long)lvt_8_1_; ++lvt_14_1_) {
               for(long lvt_16_1_ = (long)lvt_9_1_; lvt_16_1_ < (long)lvt_10_1_; ++lvt_16_1_) {
                  for(long lvt_18_1_ = (long)lvt_11_1_; lvt_18_1_ < (long)lvt_12_1_; ++lvt_18_1_) {
                     lvt_13_1_.setFilled((int)lvt_14_1_, (int)lvt_16_1_, (int)lvt_18_1_, false, true);
                  }
               }
            }

            return new VoxelShapeCube(lvt_13_1_);
         }
      } else {
         return new VoxelShapeArray(FULL_CUBE.part, new double[]{p_197881_0_.minX, p_197881_0_.maxX}, new double[]{p_197881_0_.minY, p_197881_0_.maxY}, new double[]{p_197881_0_.minZ, p_197881_0_.maxZ});
      }
   }

   private static int getPrecisionBits(double p_197885_0_, double p_197885_2_) {
      if (p_197885_0_ >= -1.0E-7D && p_197885_2_ <= 1.0000001D) {
         for(int lvt_4_1_ = 0; lvt_4_1_ <= 3; ++lvt_4_1_) {
            double lvt_5_1_ = p_197885_0_ * (double)(1 << lvt_4_1_);
            double lvt_7_1_ = p_197885_2_ * (double)(1 << lvt_4_1_);
            boolean lvt_9_1_ = Math.abs(lvt_5_1_ - Math.floor(lvt_5_1_)) < 1.0E-7D;
            boolean lvt_10_1_ = Math.abs(lvt_7_1_ - Math.floor(lvt_7_1_)) < 1.0E-7D;
            if (lvt_9_1_ && lvt_10_1_) {
               return lvt_4_1_;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   protected static long lcm(int p_197877_0_, int p_197877_1_) {
      return (long)p_197877_0_ * (long)(p_197877_1_ / IntMath.gcd(p_197877_0_, p_197877_1_));
   }

   public static VoxelShape or(VoxelShape p_197872_0_, VoxelShape p_197872_1_) {
      return combineAndSimplify(p_197872_0_, p_197872_1_, IBooleanFunction.OR);
   }

   public static VoxelShape or(VoxelShape p_216384_0_, VoxelShape... p_216384_1_) {
      return (VoxelShape)Arrays.stream(p_216384_1_).reduce(p_216384_0_, VoxelShapes::or);
   }

   public static VoxelShape combineAndSimplify(VoxelShape p_197878_0_, VoxelShape p_197878_1_, IBooleanFunction p_197878_2_) {
      return combine(p_197878_0_, p_197878_1_, p_197878_2_).simplify();
   }

   public static VoxelShape combine(VoxelShape p_197882_0_, VoxelShape p_197882_1_, IBooleanFunction p_197882_2_) {
      if (p_197882_2_.apply(false, false)) {
         throw (IllegalArgumentException)Util.func_229757_c_(new IllegalArgumentException());
      } else if (p_197882_0_ == p_197882_1_) {
         return p_197882_2_.apply(true, true) ? p_197882_0_ : empty();
      } else {
         boolean lvt_3_1_ = p_197882_2_.apply(true, false);
         boolean lvt_4_1_ = p_197882_2_.apply(false, true);
         if (p_197882_0_.isEmpty()) {
            return lvt_4_1_ ? p_197882_1_ : empty();
         } else if (p_197882_1_.isEmpty()) {
            return lvt_3_1_ ? p_197882_0_ : empty();
         } else {
            IDoubleListMerger lvt_5_1_ = makeListMerger(1, p_197882_0_.getValues(Direction.Axis.X), p_197882_1_.getValues(Direction.Axis.X), lvt_3_1_, lvt_4_1_);
            IDoubleListMerger lvt_6_1_ = makeListMerger(lvt_5_1_.func_212435_a().size() - 1, p_197882_0_.getValues(Direction.Axis.Y), p_197882_1_.getValues(Direction.Axis.Y), lvt_3_1_, lvt_4_1_);
            IDoubleListMerger lvt_7_1_ = makeListMerger((lvt_5_1_.func_212435_a().size() - 1) * (lvt_6_1_.func_212435_a().size() - 1), p_197882_0_.getValues(Direction.Axis.Z), p_197882_1_.getValues(Direction.Axis.Z), lvt_3_1_, lvt_4_1_);
            BitSetVoxelShapePart lvt_8_1_ = BitSetVoxelShapePart.func_197852_a(p_197882_0_.part, p_197882_1_.part, lvt_5_1_, lvt_6_1_, lvt_7_1_, p_197882_2_);
            return (VoxelShape)(lvt_5_1_ instanceof DoubleCubeMergingList && lvt_6_1_ instanceof DoubleCubeMergingList && lvt_7_1_ instanceof DoubleCubeMergingList ? new VoxelShapeCube(lvt_8_1_) : new VoxelShapeArray(lvt_8_1_, lvt_5_1_.func_212435_a(), lvt_6_1_.func_212435_a(), lvt_7_1_.func_212435_a()));
         }
      }
   }

   public static boolean compare(VoxelShape p_197879_0_, VoxelShape p_197879_1_, IBooleanFunction p_197879_2_) {
      if (p_197879_2_.apply(false, false)) {
         throw (IllegalArgumentException)Util.func_229757_c_(new IllegalArgumentException());
      } else if (p_197879_0_ == p_197879_1_) {
         return p_197879_2_.apply(true, true);
      } else if (p_197879_0_.isEmpty()) {
         return p_197879_2_.apply(false, !p_197879_1_.isEmpty());
      } else if (p_197879_1_.isEmpty()) {
         return p_197879_2_.apply(!p_197879_0_.isEmpty(), false);
      } else {
         boolean lvt_3_1_ = p_197879_2_.apply(true, false);
         boolean lvt_4_1_ = p_197879_2_.apply(false, true);
         Direction.Axis[] var5 = AxisRotation.AXES;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Direction.Axis lvt_8_1_ = var5[var7];
            if (p_197879_0_.getEnd(lvt_8_1_) < p_197879_1_.getStart(lvt_8_1_) - 1.0E-7D) {
               return lvt_3_1_ || lvt_4_1_;
            }

            if (p_197879_1_.getEnd(lvt_8_1_) < p_197879_0_.getStart(lvt_8_1_) - 1.0E-7D) {
               return lvt_3_1_ || lvt_4_1_;
            }
         }

         IDoubleListMerger lvt_5_1_ = makeListMerger(1, p_197879_0_.getValues(Direction.Axis.X), p_197879_1_.getValues(Direction.Axis.X), lvt_3_1_, lvt_4_1_);
         IDoubleListMerger lvt_6_1_ = makeListMerger(lvt_5_1_.func_212435_a().size() - 1, p_197879_0_.getValues(Direction.Axis.Y), p_197879_1_.getValues(Direction.Axis.Y), lvt_3_1_, lvt_4_1_);
         IDoubleListMerger lvt_7_1_ = makeListMerger((lvt_5_1_.func_212435_a().size() - 1) * (lvt_6_1_.func_212435_a().size() - 1), p_197879_0_.getValues(Direction.Axis.Z), p_197879_1_.getValues(Direction.Axis.Z), lvt_3_1_, lvt_4_1_);
         return func_197874_a(lvt_5_1_, lvt_6_1_, lvt_7_1_, p_197879_0_.part, p_197879_1_.part, p_197879_2_);
      }
   }

   private static boolean func_197874_a(IDoubleListMerger p_197874_0_, IDoubleListMerger p_197874_1_, IDoubleListMerger p_197874_2_, VoxelShapePart p_197874_3_, VoxelShapePart p_197874_4_, IBooleanFunction p_197874_5_) {
      return !p_197874_0_.forMergedIndexes((p_199861_5_, p_199861_6_, p_199861_7_) -> {
         return p_197874_1_.forMergedIndexes((p_199860_6_, p_199860_7_, p_199860_8_) -> {
            return p_197874_2_.forMergedIndexes((p_199862_7_, p_199862_8_, p_199862_9_) -> {
               return !p_197874_5_.apply(p_197874_3_.contains(p_199861_5_, p_199860_6_, p_199862_7_), p_197874_4_.contains(p_199861_6_, p_199860_7_, p_199862_8_));
            });
         });
      });
   }

   public static double getAllowedOffset(Direction.Axis p_212437_0_, AxisAlignedBB p_212437_1_, Stream<VoxelShape> p_212437_2_, double p_212437_3_) {
      for(Iterator lvt_5_1_ = p_212437_2_.iterator(); lvt_5_1_.hasNext(); p_212437_3_ = ((VoxelShape)lvt_5_1_.next()).getAllowedOffset(p_212437_0_, p_212437_1_, p_212437_3_)) {
         if (Math.abs(p_212437_3_) < 1.0E-7D) {
            return 0.0D;
         }
      }

      return p_212437_3_;
   }

   public static double getAllowedOffset(Direction.Axis p_216383_0_, AxisAlignedBB p_216383_1_, IWorldReader p_216383_2_, double p_216383_3_, ISelectionContext p_216383_5_, Stream<VoxelShape> p_216383_6_) {
      return getAllowedOffset(p_216383_1_, p_216383_2_, p_216383_3_, p_216383_5_, AxisRotation.from(p_216383_0_, Direction.Axis.Z), p_216383_6_);
   }

   private static double getAllowedOffset(AxisAlignedBB p_216386_0_, IWorldReader p_216386_1_, double p_216386_2_, ISelectionContext p_216386_4_, AxisRotation p_216386_5_, Stream<VoxelShape> p_216386_6_) {
      if (p_216386_0_.getXSize() >= 1.0E-6D && p_216386_0_.getYSize() >= 1.0E-6D && p_216386_0_.getZSize() >= 1.0E-6D) {
         if (Math.abs(p_216386_2_) < 1.0E-7D) {
            return 0.0D;
         } else {
            AxisRotation lvt_7_1_ = p_216386_5_.reverse();
            Direction.Axis lvt_8_1_ = lvt_7_1_.rotate(Direction.Axis.X);
            Direction.Axis lvt_9_1_ = lvt_7_1_.rotate(Direction.Axis.Y);
            Direction.Axis lvt_10_1_ = lvt_7_1_.rotate(Direction.Axis.Z);
            BlockPos.Mutable lvt_11_1_ = new BlockPos.Mutable();
            int lvt_12_1_ = MathHelper.floor(p_216386_0_.getMin(lvt_8_1_) - 1.0E-7D) - 1;
            int lvt_13_1_ = MathHelper.floor(p_216386_0_.getMax(lvt_8_1_) + 1.0E-7D) + 1;
            int lvt_14_1_ = MathHelper.floor(p_216386_0_.getMin(lvt_9_1_) - 1.0E-7D) - 1;
            int lvt_15_1_ = MathHelper.floor(p_216386_0_.getMax(lvt_9_1_) + 1.0E-7D) + 1;
            double lvt_16_1_ = p_216386_0_.getMin(lvt_10_1_) - 1.0E-7D;
            double lvt_18_1_ = p_216386_0_.getMax(lvt_10_1_) + 1.0E-7D;
            boolean lvt_20_1_ = p_216386_2_ > 0.0D;
            int lvt_21_1_ = lvt_20_1_ ? MathHelper.floor(p_216386_0_.getMax(lvt_10_1_) - 1.0E-7D) - 1 : MathHelper.floor(p_216386_0_.getMin(lvt_10_1_) + 1.0E-7D) + 1;
            int lvt_22_1_ = getDifferenceFloored(p_216386_2_, lvt_16_1_, lvt_18_1_);
            int lvt_23_1_ = lvt_20_1_ ? 1 : -1;
            int lvt_24_1_ = lvt_21_1_;

            while(true) {
               if (lvt_20_1_) {
                  if (lvt_24_1_ > lvt_22_1_) {
                     break;
                  }
               } else if (lvt_24_1_ < lvt_22_1_) {
                  break;
               }

               for(int lvt_25_1_ = lvt_12_1_; lvt_25_1_ <= lvt_13_1_; ++lvt_25_1_) {
                  for(int lvt_26_1_ = lvt_14_1_; lvt_26_1_ <= lvt_15_1_; ++lvt_26_1_) {
                     int lvt_27_1_ = 0;
                     if (lvt_25_1_ == lvt_12_1_ || lvt_25_1_ == lvt_13_1_) {
                        ++lvt_27_1_;
                     }

                     if (lvt_26_1_ == lvt_14_1_ || lvt_26_1_ == lvt_15_1_) {
                        ++lvt_27_1_;
                     }

                     if (lvt_24_1_ == lvt_21_1_ || lvt_24_1_ == lvt_22_1_) {
                        ++lvt_27_1_;
                     }

                     if (lvt_27_1_ < 3) {
                        lvt_11_1_.func_218295_a(lvt_7_1_, lvt_25_1_, lvt_26_1_, lvt_24_1_);
                        BlockState lvt_28_1_ = p_216386_1_.getBlockState(lvt_11_1_);
                        if ((lvt_27_1_ != 1 || lvt_28_1_.func_215704_f()) && (lvt_27_1_ != 2 || lvt_28_1_.getBlock() == Blocks.MOVING_PISTON)) {
                           p_216386_2_ = lvt_28_1_.getCollisionShape(p_216386_1_, lvt_11_1_, p_216386_4_).getAllowedOffset(lvt_10_1_, p_216386_0_.offset((double)(-lvt_11_1_.getX()), (double)(-lvt_11_1_.getY()), (double)(-lvt_11_1_.getZ())), p_216386_2_);
                           if (Math.abs(p_216386_2_) < 1.0E-7D) {
                              return 0.0D;
                           }

                           lvt_22_1_ = getDifferenceFloored(p_216386_2_, lvt_16_1_, lvt_18_1_);
                        }
                     }
                  }
               }

               lvt_24_1_ += lvt_23_1_;
            }

            double[] lvt_24_2_ = new double[]{p_216386_2_};
            p_216386_6_.forEach((p_216388_3_) -> {
               lvt_24_2_[0] = p_216388_3_.getAllowedOffset(lvt_10_1_, p_216386_0_, lvt_24_2_[0]);
            });
            return lvt_24_2_[0];
         }
      } else {
         return p_216386_2_;
      }
   }

   private static int getDifferenceFloored(double p_216385_0_, double p_216385_2_, double p_216385_4_) {
      return p_216385_0_ > 0.0D ? MathHelper.floor(p_216385_4_ + p_216385_0_) + 1 : MathHelper.floor(p_216385_2_ + p_216385_0_) - 1;
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean isCubeSideCovered(VoxelShape p_197875_0_, VoxelShape p_197875_1_, Direction p_197875_2_) {
      if (p_197875_0_ == fullCube() && p_197875_1_ == fullCube()) {
         return true;
      } else if (p_197875_1_.isEmpty()) {
         return false;
      } else {
         Direction.Axis lvt_3_1_ = p_197875_2_.getAxis();
         Direction.AxisDirection lvt_4_1_ = p_197875_2_.getAxisDirection();
         VoxelShape lvt_5_1_ = lvt_4_1_ == Direction.AxisDirection.POSITIVE ? p_197875_0_ : p_197875_1_;
         VoxelShape lvt_6_1_ = lvt_4_1_ == Direction.AxisDirection.POSITIVE ? p_197875_1_ : p_197875_0_;
         IBooleanFunction lvt_7_1_ = lvt_4_1_ == Direction.AxisDirection.POSITIVE ? IBooleanFunction.ONLY_FIRST : IBooleanFunction.ONLY_SECOND;
         return DoubleMath.fuzzyEquals(lvt_5_1_.getEnd(lvt_3_1_), 1.0D, 1.0E-7D) && DoubleMath.fuzzyEquals(lvt_6_1_.getStart(lvt_3_1_), 0.0D, 1.0E-7D) && !compare(new SplitVoxelShape(lvt_5_1_, lvt_3_1_, lvt_5_1_.part.getSize(lvt_3_1_) - 1), new SplitVoxelShape(lvt_6_1_, lvt_3_1_, 0), lvt_7_1_);
      }
   }

   public static VoxelShape func_216387_a(VoxelShape p_216387_0_, Direction p_216387_1_) {
      if (p_216387_0_ == fullCube()) {
         return fullCube();
      } else {
         Direction.Axis lvt_4_1_ = p_216387_1_.getAxis();
         boolean lvt_2_2_;
         int lvt_3_2_;
         if (p_216387_1_.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            lvt_2_2_ = DoubleMath.fuzzyEquals(p_216387_0_.getEnd(lvt_4_1_), 1.0D, 1.0E-7D);
            lvt_3_2_ = p_216387_0_.part.getSize(lvt_4_1_) - 1;
         } else {
            lvt_2_2_ = DoubleMath.fuzzyEquals(p_216387_0_.getStart(lvt_4_1_), 0.0D, 1.0E-7D);
            lvt_3_2_ = 0;
         }

         return (VoxelShape)(!lvt_2_2_ ? empty() : new SplitVoxelShape(p_216387_0_, lvt_4_1_, lvt_3_2_));
      }
   }

   public static boolean doAdjacentCubeSidesFillSquare(VoxelShape p_204642_0_, VoxelShape p_204642_1_, Direction p_204642_2_) {
      if (p_204642_0_ != fullCube() && p_204642_1_ != fullCube()) {
         Direction.Axis lvt_3_1_ = p_204642_2_.getAxis();
         Direction.AxisDirection lvt_4_1_ = p_204642_2_.getAxisDirection();
         VoxelShape lvt_5_1_ = lvt_4_1_ == Direction.AxisDirection.POSITIVE ? p_204642_0_ : p_204642_1_;
         VoxelShape lvt_6_1_ = lvt_4_1_ == Direction.AxisDirection.POSITIVE ? p_204642_1_ : p_204642_0_;
         if (!DoubleMath.fuzzyEquals(lvt_5_1_.getEnd(lvt_3_1_), 1.0D, 1.0E-7D)) {
            lvt_5_1_ = empty();
         }

         if (!DoubleMath.fuzzyEquals(lvt_6_1_.getStart(lvt_3_1_), 0.0D, 1.0E-7D)) {
            lvt_6_1_ = empty();
         }

         return !compare(fullCube(), combine(new SplitVoxelShape(lvt_5_1_, lvt_3_1_, lvt_5_1_.part.getSize(lvt_3_1_) - 1), new SplitVoxelShape(lvt_6_1_, lvt_3_1_, 0), IBooleanFunction.OR), IBooleanFunction.ONLY_FIRST);
      } else {
         return true;
      }
   }

   public static boolean func_223416_b(VoxelShape p_223416_0_, VoxelShape p_223416_1_) {
      if (p_223416_0_ != fullCube() && p_223416_1_ != fullCube()) {
         if (p_223416_0_.isEmpty() && p_223416_1_.isEmpty()) {
            return false;
         } else {
            return !compare(fullCube(), combine(p_223416_0_, p_223416_1_, IBooleanFunction.OR), IBooleanFunction.ONLY_FIRST);
         }
      } else {
         return true;
      }
   }

   @VisibleForTesting
   protected static IDoubleListMerger makeListMerger(int p_199410_0_, DoubleList p_199410_1_, DoubleList p_199410_2_, boolean p_199410_3_, boolean p_199410_4_) {
      int lvt_5_1_ = p_199410_1_.size() - 1;
      int lvt_6_1_ = p_199410_2_.size() - 1;
      if (p_199410_1_ instanceof DoubleRangeList && p_199410_2_ instanceof DoubleRangeList) {
         long lvt_7_1_ = lcm(lvt_5_1_, lvt_6_1_);
         if ((long)p_199410_0_ * lvt_7_1_ <= 256L) {
            return new DoubleCubeMergingList(lvt_5_1_, lvt_6_1_);
         }
      }

      if (p_199410_1_.getDouble(lvt_5_1_) < p_199410_2_.getDouble(0) - 1.0E-7D) {
         return new NonOverlappingMerger(p_199410_1_, p_199410_2_, false);
      } else if (p_199410_2_.getDouble(lvt_6_1_) < p_199410_1_.getDouble(0) - 1.0E-7D) {
         return new NonOverlappingMerger(p_199410_2_, p_199410_1_, true);
      } else if (lvt_5_1_ == lvt_6_1_ && Objects.equals(p_199410_1_, p_199410_2_)) {
         if (p_199410_1_ instanceof SimpleDoubleMerger) {
            return (IDoubleListMerger)p_199410_1_;
         } else {
            return (IDoubleListMerger)(p_199410_2_ instanceof SimpleDoubleMerger ? (IDoubleListMerger)p_199410_2_ : new SimpleDoubleMerger(p_199410_1_));
         }
      } else {
         return new IndirectMerger(p_199410_1_, p_199410_2_, p_199410_3_, p_199410_4_);
      }
   }

   public interface ILineConsumer {
      void consume(double var1, double var3, double var5, double var7, double var9, double var11);
   }
}
