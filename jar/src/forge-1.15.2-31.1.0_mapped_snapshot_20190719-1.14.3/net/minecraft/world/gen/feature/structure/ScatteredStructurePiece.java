package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;

public abstract class ScatteredStructurePiece extends StructurePiece {
   protected final int width;
   protected final int height;
   protected final int depth;
   protected int hPos = -1;

   protected ScatteredStructurePiece(IStructurePieceType p_i51344_1_, Random p_i51344_2_, int p_i51344_3_, int p_i51344_4_, int p_i51344_5_, int p_i51344_6_, int p_i51344_7_, int p_i51344_8_) {
      super(p_i51344_1_, 0);
      this.width = p_i51344_6_;
      this.height = p_i51344_7_;
      this.depth = p_i51344_8_;
      this.setCoordBaseMode(Direction.Plane.HORIZONTAL.random(p_i51344_2_));
      if (this.getCoordBaseMode().getAxis() == Direction.Axis.Z) {
         this.boundingBox = new MutableBoundingBox(p_i51344_3_, p_i51344_4_, p_i51344_5_, p_i51344_3_ + p_i51344_6_ - 1, p_i51344_4_ + p_i51344_7_ - 1, p_i51344_5_ + p_i51344_8_ - 1);
      } else {
         this.boundingBox = new MutableBoundingBox(p_i51344_3_, p_i51344_4_, p_i51344_5_, p_i51344_3_ + p_i51344_8_ - 1, p_i51344_4_ + p_i51344_7_ - 1, p_i51344_5_ + p_i51344_6_ - 1);
      }

   }

   protected ScatteredStructurePiece(IStructurePieceType p_i51345_1_, CompoundNBT p_i51345_2_) {
      super(p_i51345_1_, p_i51345_2_);
      this.width = p_i51345_2_.getInt("Width");
      this.height = p_i51345_2_.getInt("Height");
      this.depth = p_i51345_2_.getInt("Depth");
      this.hPos = p_i51345_2_.getInt("HPos");
   }

   protected void readAdditional(CompoundNBT p_143011_1_) {
      p_143011_1_.putInt("Width", this.width);
      p_143011_1_.putInt("Height", this.height);
      p_143011_1_.putInt("Depth", this.depth);
      p_143011_1_.putInt("HPos", this.hPos);
   }

   protected boolean func_202580_a(IWorld p_202580_1_, MutableBoundingBox p_202580_2_, int p_202580_3_) {
      if (this.hPos >= 0) {
         return true;
      } else {
         int lvt_4_1_ = 0;
         int lvt_5_1_ = 0;
         BlockPos.Mutable lvt_6_1_ = new BlockPos.Mutable();

         for(int lvt_7_1_ = this.boundingBox.minZ; lvt_7_1_ <= this.boundingBox.maxZ; ++lvt_7_1_) {
            for(int lvt_8_1_ = this.boundingBox.minX; lvt_8_1_ <= this.boundingBox.maxX; ++lvt_8_1_) {
               lvt_6_1_.setPos(lvt_8_1_, 64, lvt_7_1_);
               if (p_202580_2_.isVecInside(lvt_6_1_)) {
                  lvt_4_1_ += p_202580_1_.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, lvt_6_1_).getY();
                  ++lvt_5_1_;
               }
            }
         }

         if (lvt_5_1_ == 0) {
            return false;
         } else {
            this.hPos = lvt_4_1_ / lvt_5_1_;
            this.boundingBox.offset(0, this.hPos - this.boundingBox.minY + p_202580_3_, 0);
            return true;
         }
      }
   }
}
