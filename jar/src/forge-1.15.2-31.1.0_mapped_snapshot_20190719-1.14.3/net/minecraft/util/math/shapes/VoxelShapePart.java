package net.minecraft.util.math.shapes;

import net.minecraft.util.AxisRotation;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class VoxelShapePart {
   private static final Direction.Axis[] AXIS_VALUES = Direction.Axis.values();
   protected final int xSize;
   protected final int ySize;
   protected final int zSize;

   protected VoxelShapePart(int p_i47686_1_, int p_i47686_2_, int p_i47686_3_) {
      this.xSize = p_i47686_1_;
      this.ySize = p_i47686_2_;
      this.zSize = p_i47686_3_;
   }

   public boolean containsWithRotation(AxisRotation p_197824_1_, int p_197824_2_, int p_197824_3_, int p_197824_4_) {
      return this.contains(p_197824_1_.getCoordinate(p_197824_2_, p_197824_3_, p_197824_4_, Direction.Axis.X), p_197824_1_.getCoordinate(p_197824_2_, p_197824_3_, p_197824_4_, Direction.Axis.Y), p_197824_1_.getCoordinate(p_197824_2_, p_197824_3_, p_197824_4_, Direction.Axis.Z));
   }

   public boolean contains(int p_197818_1_, int p_197818_2_, int p_197818_3_) {
      if (p_197818_1_ >= 0 && p_197818_2_ >= 0 && p_197818_3_ >= 0) {
         return p_197818_1_ < this.xSize && p_197818_2_ < this.ySize && p_197818_3_ < this.zSize ? this.isFilled(p_197818_1_, p_197818_2_, p_197818_3_) : false;
      } else {
         return false;
      }
   }

   public boolean isFilledWithRotation(AxisRotation p_197829_1_, int p_197829_2_, int p_197829_3_, int p_197829_4_) {
      return this.isFilled(p_197829_1_.getCoordinate(p_197829_2_, p_197829_3_, p_197829_4_, Direction.Axis.X), p_197829_1_.getCoordinate(p_197829_2_, p_197829_3_, p_197829_4_, Direction.Axis.Y), p_197829_1_.getCoordinate(p_197829_2_, p_197829_3_, p_197829_4_, Direction.Axis.Z));
   }

   public abstract boolean isFilled(int var1, int var2, int var3);

   public abstract void setFilled(int var1, int var2, int var3, boolean var4, boolean var5);

   public boolean isEmpty() {
      Direction.Axis[] var1 = AXIS_VALUES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Direction.Axis lvt_4_1_ = var1[var3];
         if (this.getStart(lvt_4_1_) >= this.getEnd(lvt_4_1_)) {
            return true;
         }
      }

      return false;
   }

   public abstract int getStart(Direction.Axis var1);

   public abstract int getEnd(Direction.Axis var1);

   @OnlyIn(Dist.CLIENT)
   public int firstFilled(Direction.Axis p_197826_1_, int p_197826_2_, int p_197826_3_) {
      int lvt_4_1_ = this.getSize(p_197826_1_);
      if (p_197826_2_ >= 0 && p_197826_3_ >= 0) {
         Direction.Axis lvt_5_1_ = AxisRotation.FORWARD.rotate(p_197826_1_);
         Direction.Axis lvt_6_1_ = AxisRotation.BACKWARD.rotate(p_197826_1_);
         if (p_197826_2_ < this.getSize(lvt_5_1_) && p_197826_3_ < this.getSize(lvt_6_1_)) {
            AxisRotation lvt_7_1_ = AxisRotation.from(Direction.Axis.X, p_197826_1_);

            for(int lvt_8_1_ = 0; lvt_8_1_ < lvt_4_1_; ++lvt_8_1_) {
               if (this.isFilledWithRotation(lvt_7_1_, lvt_8_1_, p_197826_2_, p_197826_3_)) {
                  return lvt_8_1_;
               }
            }

            return lvt_4_1_;
         } else {
            return lvt_4_1_;
         }
      } else {
         return lvt_4_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public int lastFilled(Direction.Axis p_197836_1_, int p_197836_2_, int p_197836_3_) {
      if (p_197836_2_ >= 0 && p_197836_3_ >= 0) {
         Direction.Axis lvt_4_1_ = AxisRotation.FORWARD.rotate(p_197836_1_);
         Direction.Axis lvt_5_1_ = AxisRotation.BACKWARD.rotate(p_197836_1_);
         if (p_197836_2_ < this.getSize(lvt_4_1_) && p_197836_3_ < this.getSize(lvt_5_1_)) {
            int lvt_6_1_ = this.getSize(p_197836_1_);
            AxisRotation lvt_7_1_ = AxisRotation.from(Direction.Axis.X, p_197836_1_);

            for(int lvt_8_1_ = lvt_6_1_ - 1; lvt_8_1_ >= 0; --lvt_8_1_) {
               if (this.isFilledWithRotation(lvt_7_1_, lvt_8_1_, p_197836_2_, p_197836_3_)) {
                  return lvt_8_1_ + 1;
               }
            }

            return 0;
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public int getSize(Direction.Axis p_197819_1_) {
      return p_197819_1_.getCoordinate(this.xSize, this.ySize, this.zSize);
   }

   public int getXSize() {
      return this.getSize(Direction.Axis.X);
   }

   public int getYSize() {
      return this.getSize(Direction.Axis.Y);
   }

   public int getZSize() {
      return this.getSize(Direction.Axis.Z);
   }

   @OnlyIn(Dist.CLIENT)
   public void forEachEdge(VoxelShapePart.ILineConsumer p_197828_1_, boolean p_197828_2_) {
      this.forEachEdgeOnAxis(p_197828_1_, AxisRotation.NONE, p_197828_2_);
      this.forEachEdgeOnAxis(p_197828_1_, AxisRotation.FORWARD, p_197828_2_);
      this.forEachEdgeOnAxis(p_197828_1_, AxisRotation.BACKWARD, p_197828_2_);
   }

   @OnlyIn(Dist.CLIENT)
   private void forEachEdgeOnAxis(VoxelShapePart.ILineConsumer p_197832_1_, AxisRotation p_197832_2_, boolean p_197832_3_) {
      AxisRotation lvt_5_1_ = p_197832_2_.reverse();
      int lvt_6_1_ = this.getSize(lvt_5_1_.rotate(Direction.Axis.X));
      int lvt_7_1_ = this.getSize(lvt_5_1_.rotate(Direction.Axis.Y));
      int lvt_8_1_ = this.getSize(lvt_5_1_.rotate(Direction.Axis.Z));

      for(int lvt_9_1_ = 0; lvt_9_1_ <= lvt_6_1_; ++lvt_9_1_) {
         for(int lvt_10_1_ = 0; lvt_10_1_ <= lvt_7_1_; ++lvt_10_1_) {
            int lvt_4_1_ = -1;

            for(int lvt_11_1_ = 0; lvt_11_1_ <= lvt_8_1_; ++lvt_11_1_) {
               int lvt_12_1_ = 0;
               int lvt_13_1_ = 0;

               for(int lvt_14_1_ = 0; lvt_14_1_ <= 1; ++lvt_14_1_) {
                  for(int lvt_15_1_ = 0; lvt_15_1_ <= 1; ++lvt_15_1_) {
                     if (this.containsWithRotation(lvt_5_1_, lvt_9_1_ + lvt_14_1_ - 1, lvt_10_1_ + lvt_15_1_ - 1, lvt_11_1_)) {
                        ++lvt_12_1_;
                        lvt_13_1_ ^= lvt_14_1_ ^ lvt_15_1_;
                     }
                  }
               }

               if (lvt_12_1_ == 1 || lvt_12_1_ == 3 || lvt_12_1_ == 2 && (lvt_13_1_ & 1) == 0) {
                  if (p_197832_3_) {
                     if (lvt_4_1_ == -1) {
                        lvt_4_1_ = lvt_11_1_;
                     }
                  } else {
                     p_197832_1_.consume(lvt_5_1_.getCoordinate(lvt_9_1_, lvt_10_1_, lvt_11_1_, Direction.Axis.X), lvt_5_1_.getCoordinate(lvt_9_1_, lvt_10_1_, lvt_11_1_, Direction.Axis.Y), lvt_5_1_.getCoordinate(lvt_9_1_, lvt_10_1_, lvt_11_1_, Direction.Axis.Z), lvt_5_1_.getCoordinate(lvt_9_1_, lvt_10_1_, lvt_11_1_ + 1, Direction.Axis.X), lvt_5_1_.getCoordinate(lvt_9_1_, lvt_10_1_, lvt_11_1_ + 1, Direction.Axis.Y), lvt_5_1_.getCoordinate(lvt_9_1_, lvt_10_1_, lvt_11_1_ + 1, Direction.Axis.Z));
                  }
               } else if (lvt_4_1_ != -1) {
                  p_197832_1_.consume(lvt_5_1_.getCoordinate(lvt_9_1_, lvt_10_1_, lvt_4_1_, Direction.Axis.X), lvt_5_1_.getCoordinate(lvt_9_1_, lvt_10_1_, lvt_4_1_, Direction.Axis.Y), lvt_5_1_.getCoordinate(lvt_9_1_, lvt_10_1_, lvt_4_1_, Direction.Axis.Z), lvt_5_1_.getCoordinate(lvt_9_1_, lvt_10_1_, lvt_11_1_, Direction.Axis.X), lvt_5_1_.getCoordinate(lvt_9_1_, lvt_10_1_, lvt_11_1_, Direction.Axis.Y), lvt_5_1_.getCoordinate(lvt_9_1_, lvt_10_1_, lvt_11_1_, Direction.Axis.Z));
                  lvt_4_1_ = -1;
               }
            }
         }
      }

   }

   protected boolean isZAxisLineFull(int p_197833_1_, int p_197833_2_, int p_197833_3_, int p_197833_4_) {
      for(int lvt_5_1_ = p_197833_1_; lvt_5_1_ < p_197833_2_; ++lvt_5_1_) {
         if (!this.contains(p_197833_3_, p_197833_4_, lvt_5_1_)) {
            return false;
         }
      }

      return true;
   }

   protected void setZAxisLine(int p_197834_1_, int p_197834_2_, int p_197834_3_, int p_197834_4_, boolean p_197834_5_) {
      for(int lvt_6_1_ = p_197834_1_; lvt_6_1_ < p_197834_2_; ++lvt_6_1_) {
         this.setFilled(p_197834_3_, p_197834_4_, lvt_6_1_, false, p_197834_5_);
      }

   }

   protected boolean isXZRectangleFull(int p_197827_1_, int p_197827_2_, int p_197827_3_, int p_197827_4_, int p_197827_5_) {
      for(int lvt_6_1_ = p_197827_1_; lvt_6_1_ < p_197827_2_; ++lvt_6_1_) {
         if (!this.isZAxisLineFull(p_197827_3_, p_197827_4_, lvt_6_1_, p_197827_5_)) {
            return false;
         }
      }

      return true;
   }

   public void forEachBox(VoxelShapePart.ILineConsumer p_197831_1_, boolean p_197831_2_) {
      VoxelShapePart lvt_3_1_ = new BitSetVoxelShapePart(this);

      for(int lvt_4_1_ = 0; lvt_4_1_ <= this.xSize; ++lvt_4_1_) {
         for(int lvt_5_1_ = 0; lvt_5_1_ <= this.ySize; ++lvt_5_1_) {
            int lvt_6_1_ = -1;

            for(int lvt_7_1_ = 0; lvt_7_1_ <= this.zSize; ++lvt_7_1_) {
               if (lvt_3_1_.contains(lvt_4_1_, lvt_5_1_, lvt_7_1_)) {
                  if (p_197831_2_) {
                     if (lvt_6_1_ == -1) {
                        lvt_6_1_ = lvt_7_1_;
                     }
                  } else {
                     p_197831_1_.consume(lvt_4_1_, lvt_5_1_, lvt_7_1_, lvt_4_1_ + 1, lvt_5_1_ + 1, lvt_7_1_ + 1);
                  }
               } else if (lvt_6_1_ != -1) {
                  int lvt_8_1_ = lvt_4_1_;
                  int lvt_9_1_ = lvt_4_1_;
                  int lvt_10_1_ = lvt_5_1_;
                  int lvt_11_1_ = lvt_5_1_;
                  lvt_3_1_.setZAxisLine(lvt_6_1_, lvt_7_1_, lvt_4_1_, lvt_5_1_, false);

                  while(lvt_3_1_.isZAxisLineFull(lvt_6_1_, lvt_7_1_, lvt_8_1_ - 1, lvt_10_1_)) {
                     lvt_3_1_.setZAxisLine(lvt_6_1_, lvt_7_1_, lvt_8_1_ - 1, lvt_10_1_, false);
                     --lvt_8_1_;
                  }

                  while(lvt_3_1_.isZAxisLineFull(lvt_6_1_, lvt_7_1_, lvt_9_1_ + 1, lvt_10_1_)) {
                     lvt_3_1_.setZAxisLine(lvt_6_1_, lvt_7_1_, lvt_9_1_ + 1, lvt_10_1_, false);
                     ++lvt_9_1_;
                  }

                  int lvt_12_2_;
                  while(lvt_3_1_.isXZRectangleFull(lvt_8_1_, lvt_9_1_ + 1, lvt_6_1_, lvt_7_1_, lvt_10_1_ - 1)) {
                     for(lvt_12_2_ = lvt_8_1_; lvt_12_2_ <= lvt_9_1_; ++lvt_12_2_) {
                        lvt_3_1_.setZAxisLine(lvt_6_1_, lvt_7_1_, lvt_12_2_, lvt_10_1_ - 1, false);
                     }

                     --lvt_10_1_;
                  }

                  while(lvt_3_1_.isXZRectangleFull(lvt_8_1_, lvt_9_1_ + 1, lvt_6_1_, lvt_7_1_, lvt_11_1_ + 1)) {
                     for(lvt_12_2_ = lvt_8_1_; lvt_12_2_ <= lvt_9_1_; ++lvt_12_2_) {
                        lvt_3_1_.setZAxisLine(lvt_6_1_, lvt_7_1_, lvt_12_2_, lvt_11_1_ + 1, false);
                     }

                     ++lvt_11_1_;
                  }

                  p_197831_1_.consume(lvt_8_1_, lvt_10_1_, lvt_6_1_, lvt_9_1_ + 1, lvt_11_1_ + 1, lvt_7_1_);
                  lvt_6_1_ = -1;
               }
            }
         }
      }

   }

   public void forEachFace(VoxelShapePart.IFaceConsumer p_211540_1_) {
      this.forEachFaceOnAxis(p_211540_1_, AxisRotation.NONE);
      this.forEachFaceOnAxis(p_211540_1_, AxisRotation.FORWARD);
      this.forEachFaceOnAxis(p_211540_1_, AxisRotation.BACKWARD);
   }

   private void forEachFaceOnAxis(VoxelShapePart.IFaceConsumer p_211541_1_, AxisRotation p_211541_2_) {
      AxisRotation lvt_3_1_ = p_211541_2_.reverse();
      Direction.Axis lvt_4_1_ = lvt_3_1_.rotate(Direction.Axis.Z);
      int lvt_5_1_ = this.getSize(lvt_3_1_.rotate(Direction.Axis.X));
      int lvt_6_1_ = this.getSize(lvt_3_1_.rotate(Direction.Axis.Y));
      int lvt_7_1_ = this.getSize(lvt_4_1_);
      Direction lvt_8_1_ = Direction.getFacingFromAxisDirection(lvt_4_1_, Direction.AxisDirection.NEGATIVE);
      Direction lvt_9_1_ = Direction.getFacingFromAxisDirection(lvt_4_1_, Direction.AxisDirection.POSITIVE);

      for(int lvt_10_1_ = 0; lvt_10_1_ < lvt_5_1_; ++lvt_10_1_) {
         for(int lvt_11_1_ = 0; lvt_11_1_ < lvt_6_1_; ++lvt_11_1_) {
            boolean lvt_12_1_ = false;

            for(int lvt_13_1_ = 0; lvt_13_1_ <= lvt_7_1_; ++lvt_13_1_) {
               boolean lvt_14_1_ = lvt_13_1_ != lvt_7_1_ && this.isFilledWithRotation(lvt_3_1_, lvt_10_1_, lvt_11_1_, lvt_13_1_);
               if (!lvt_12_1_ && lvt_14_1_) {
                  p_211541_1_.consume(lvt_8_1_, lvt_3_1_.getCoordinate(lvt_10_1_, lvt_11_1_, lvt_13_1_, Direction.Axis.X), lvt_3_1_.getCoordinate(lvt_10_1_, lvt_11_1_, lvt_13_1_, Direction.Axis.Y), lvt_3_1_.getCoordinate(lvt_10_1_, lvt_11_1_, lvt_13_1_, Direction.Axis.Z));
               }

               if (lvt_12_1_ && !lvt_14_1_) {
                  p_211541_1_.consume(lvt_9_1_, lvt_3_1_.getCoordinate(lvt_10_1_, lvt_11_1_, lvt_13_1_ - 1, Direction.Axis.X), lvt_3_1_.getCoordinate(lvt_10_1_, lvt_11_1_, lvt_13_1_ - 1, Direction.Axis.Y), lvt_3_1_.getCoordinate(lvt_10_1_, lvt_11_1_, lvt_13_1_ - 1, Direction.Axis.Z));
               }

               lvt_12_1_ = lvt_14_1_;
            }
         }
      }

   }

   public interface IFaceConsumer {
      void consume(Direction var1, int var2, int var3, int var4);
   }

   public interface ILineConsumer {
      void consume(int var1, int var2, int var3, int var4, int var5, int var6);
   }
}
