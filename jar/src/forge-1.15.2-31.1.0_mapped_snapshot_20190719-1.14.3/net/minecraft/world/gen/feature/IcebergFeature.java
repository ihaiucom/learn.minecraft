package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class IcebergFeature extends Feature<BlockStateFeatureConfig> {
   public IcebergFeature(Function<Dynamic<?>, ? extends BlockStateFeatureConfig> p_i51492_1_) {
      super(p_i51492_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, BlockStateFeatureConfig p_212245_5_) {
      p_212245_4_ = new BlockPos(p_212245_4_.getX(), p_212245_1_.getSeaLevel(), p_212245_4_.getZ());
      boolean lvt_6_1_ = p_212245_3_.nextDouble() > 0.7D;
      BlockState lvt_7_1_ = p_212245_5_.field_227270_a_;
      double lvt_8_1_ = p_212245_3_.nextDouble() * 2.0D * 3.141592653589793D;
      int lvt_10_1_ = 11 - p_212245_3_.nextInt(5);
      int lvt_11_1_ = 3 + p_212245_3_.nextInt(3);
      boolean lvt_12_1_ = p_212245_3_.nextDouble() > 0.7D;
      int lvt_13_1_ = true;
      int lvt_14_1_ = lvt_12_1_ ? p_212245_3_.nextInt(6) + 6 : p_212245_3_.nextInt(15) + 3;
      if (!lvt_12_1_ && p_212245_3_.nextDouble() > 0.9D) {
         lvt_14_1_ += p_212245_3_.nextInt(19) + 7;
      }

      int lvt_15_1_ = Math.min(lvt_14_1_ + p_212245_3_.nextInt(11), 18);
      int lvt_16_1_ = Math.min(lvt_14_1_ + p_212245_3_.nextInt(7) - p_212245_3_.nextInt(5), 11);
      int lvt_17_1_ = lvt_12_1_ ? lvt_10_1_ : 11;

      int lvt_18_2_;
      int lvt_19_2_;
      int lvt_20_2_;
      int lvt_21_2_;
      for(lvt_18_2_ = -lvt_17_1_; lvt_18_2_ < lvt_17_1_; ++lvt_18_2_) {
         for(lvt_19_2_ = -lvt_17_1_; lvt_19_2_ < lvt_17_1_; ++lvt_19_2_) {
            for(lvt_20_2_ = 0; lvt_20_2_ < lvt_14_1_; ++lvt_20_2_) {
               lvt_21_2_ = lvt_12_1_ ? this.func_205178_b(lvt_20_2_, lvt_14_1_, lvt_16_1_) : this.func_205183_a(p_212245_3_, lvt_20_2_, lvt_14_1_, lvt_16_1_);
               if (lvt_12_1_ || lvt_18_2_ < lvt_21_2_) {
                  this.func_205181_a(p_212245_1_, p_212245_3_, p_212245_4_, lvt_14_1_, lvt_18_2_, lvt_20_2_, lvt_19_2_, lvt_21_2_, lvt_17_1_, lvt_12_1_, lvt_11_1_, lvt_8_1_, lvt_6_1_, lvt_7_1_);
               }
            }
         }
      }

      this.func_205186_a(p_212245_1_, p_212245_4_, lvt_16_1_, lvt_14_1_, lvt_12_1_, lvt_10_1_);

      for(lvt_18_2_ = -lvt_17_1_; lvt_18_2_ < lvt_17_1_; ++lvt_18_2_) {
         for(lvt_19_2_ = -lvt_17_1_; lvt_19_2_ < lvt_17_1_; ++lvt_19_2_) {
            for(lvt_20_2_ = -1; lvt_20_2_ > -lvt_15_1_; --lvt_20_2_) {
               lvt_21_2_ = lvt_12_1_ ? MathHelper.ceil((float)lvt_17_1_ * (1.0F - (float)Math.pow((double)lvt_20_2_, 2.0D) / ((float)lvt_15_1_ * 8.0F))) : lvt_17_1_;
               int lvt_22_1_ = this.func_205187_b(p_212245_3_, -lvt_20_2_, lvt_15_1_, lvt_16_1_);
               if (lvt_18_2_ < lvt_22_1_) {
                  this.func_205181_a(p_212245_1_, p_212245_3_, p_212245_4_, lvt_15_1_, lvt_18_2_, lvt_20_2_, lvt_19_2_, lvt_22_1_, lvt_21_2_, lvt_12_1_, lvt_11_1_, lvt_8_1_, lvt_6_1_, lvt_7_1_);
               }
            }
         }
      }

      boolean lvt_18_3_ = lvt_12_1_ ? p_212245_3_.nextDouble() > 0.1D : p_212245_3_.nextDouble() > 0.7D;
      if (lvt_18_3_) {
         this.func_205184_a(p_212245_3_, p_212245_1_, lvt_16_1_, lvt_14_1_, p_212245_4_, lvt_12_1_, lvt_10_1_, lvt_8_1_, lvt_11_1_);
      }

      return true;
   }

   private void func_205184_a(Random p_205184_1_, IWorld p_205184_2_, int p_205184_3_, int p_205184_4_, BlockPos p_205184_5_, boolean p_205184_6_, int p_205184_7_, double p_205184_8_, int p_205184_10_) {
      int lvt_11_1_ = p_205184_1_.nextBoolean() ? -1 : 1;
      int lvt_12_1_ = p_205184_1_.nextBoolean() ? -1 : 1;
      int lvt_13_1_ = p_205184_1_.nextInt(Math.max(p_205184_3_ / 2 - 2, 1));
      if (p_205184_1_.nextBoolean()) {
         lvt_13_1_ = p_205184_3_ / 2 + 1 - p_205184_1_.nextInt(Math.max(p_205184_3_ - p_205184_3_ / 2 - 1, 1));
      }

      int lvt_14_1_ = p_205184_1_.nextInt(Math.max(p_205184_3_ / 2 - 2, 1));
      if (p_205184_1_.nextBoolean()) {
         lvt_14_1_ = p_205184_3_ / 2 + 1 - p_205184_1_.nextInt(Math.max(p_205184_3_ - p_205184_3_ / 2 - 1, 1));
      }

      if (p_205184_6_) {
         lvt_13_1_ = lvt_14_1_ = p_205184_1_.nextInt(Math.max(p_205184_7_ - 5, 1));
      }

      BlockPos lvt_15_1_ = new BlockPos(lvt_11_1_ * lvt_13_1_, 0, lvt_12_1_ * lvt_14_1_);
      double lvt_16_1_ = p_205184_6_ ? p_205184_8_ + 1.5707963267948966D : p_205184_1_.nextDouble() * 2.0D * 3.141592653589793D;

      int lvt_18_2_;
      int lvt_19_2_;
      for(lvt_18_2_ = 0; lvt_18_2_ < p_205184_4_ - 3; ++lvt_18_2_) {
         lvt_19_2_ = this.func_205183_a(p_205184_1_, lvt_18_2_, p_205184_4_, p_205184_3_);
         this.func_205174_a(lvt_19_2_, lvt_18_2_, p_205184_5_, p_205184_2_, false, lvt_16_1_, lvt_15_1_, p_205184_7_, p_205184_10_);
      }

      for(lvt_18_2_ = -1; lvt_18_2_ > -p_205184_4_ + p_205184_1_.nextInt(5); --lvt_18_2_) {
         lvt_19_2_ = this.func_205187_b(p_205184_1_, -lvt_18_2_, p_205184_4_, p_205184_3_);
         this.func_205174_a(lvt_19_2_, lvt_18_2_, p_205184_5_, p_205184_2_, true, lvt_16_1_, lvt_15_1_, p_205184_7_, p_205184_10_);
      }

   }

   private void func_205174_a(int p_205174_1_, int p_205174_2_, BlockPos p_205174_3_, IWorld p_205174_4_, boolean p_205174_5_, double p_205174_6_, BlockPos p_205174_8_, int p_205174_9_, int p_205174_10_) {
      int lvt_11_1_ = p_205174_1_ + 1 + p_205174_9_ / 3;
      int lvt_12_1_ = Math.min(p_205174_1_ - 3, 3) + p_205174_10_ / 2 - 1;

      for(int lvt_13_1_ = -lvt_11_1_; lvt_13_1_ < lvt_11_1_; ++lvt_13_1_) {
         for(int lvt_14_1_ = -lvt_11_1_; lvt_14_1_ < lvt_11_1_; ++lvt_14_1_) {
            double lvt_15_1_ = this.func_205180_a(lvt_13_1_, lvt_14_1_, p_205174_8_, lvt_11_1_, lvt_12_1_, p_205174_6_);
            if (lvt_15_1_ < 0.0D) {
               BlockPos lvt_17_1_ = p_205174_3_.add(lvt_13_1_, p_205174_2_, lvt_14_1_);
               Block lvt_18_1_ = p_205174_4_.getBlockState(lvt_17_1_).getBlock();
               if (this.isIce(lvt_18_1_) || lvt_18_1_ == Blocks.SNOW_BLOCK) {
                  if (p_205174_5_) {
                     this.setBlockState(p_205174_4_, lvt_17_1_, Blocks.WATER.getDefaultState());
                  } else {
                     this.setBlockState(p_205174_4_, lvt_17_1_, Blocks.AIR.getDefaultState());
                     this.removeSnowLayer(p_205174_4_, lvt_17_1_);
                  }
               }
            }
         }
      }

   }

   private void removeSnowLayer(IWorld p_205185_1_, BlockPos p_205185_2_) {
      if (p_205185_1_.getBlockState(p_205185_2_.up()).getBlock() == Blocks.SNOW) {
         this.setBlockState(p_205185_1_, p_205185_2_.up(), Blocks.AIR.getDefaultState());
      }

   }

   private void func_205181_a(IWorld p_205181_1_, Random p_205181_2_, BlockPos p_205181_3_, int p_205181_4_, int p_205181_5_, int p_205181_6_, int p_205181_7_, int p_205181_8_, int p_205181_9_, boolean p_205181_10_, int p_205181_11_, double p_205181_12_, boolean p_205181_14_, BlockState p_205181_15_) {
      double lvt_16_1_ = p_205181_10_ ? this.func_205180_a(p_205181_5_, p_205181_7_, BlockPos.ZERO, p_205181_9_, this.func_205176_a(p_205181_6_, p_205181_4_, p_205181_11_), p_205181_12_) : this.func_205177_a(p_205181_5_, p_205181_7_, BlockPos.ZERO, p_205181_8_, p_205181_2_);
      if (lvt_16_1_ < 0.0D) {
         BlockPos lvt_18_1_ = p_205181_3_.add(p_205181_5_, p_205181_6_, p_205181_7_);
         double lvt_19_1_ = p_205181_10_ ? -0.5D : (double)(-6 - p_205181_2_.nextInt(3));
         if (lvt_16_1_ > lvt_19_1_ && p_205181_2_.nextDouble() > 0.9D) {
            return;
         }

         this.func_205175_a(lvt_18_1_, p_205181_1_, p_205181_2_, p_205181_4_ - p_205181_6_, p_205181_4_, p_205181_10_, p_205181_14_, p_205181_15_);
      }

   }

   private void func_205175_a(BlockPos p_205175_1_, IWorld p_205175_2_, Random p_205175_3_, int p_205175_4_, int p_205175_5_, boolean p_205175_6_, boolean p_205175_7_, BlockState p_205175_8_) {
      BlockState lvt_9_1_ = p_205175_2_.getBlockState(p_205175_1_);
      Block lvt_10_1_ = lvt_9_1_.getBlock();
      if (lvt_9_1_.getMaterial() == Material.AIR || lvt_10_1_ == Blocks.SNOW_BLOCK || lvt_10_1_ == Blocks.ICE || lvt_10_1_ == Blocks.WATER) {
         boolean lvt_11_1_ = !p_205175_6_ || p_205175_3_.nextDouble() > 0.05D;
         int lvt_12_1_ = p_205175_6_ ? 3 : 2;
         if (p_205175_7_ && lvt_10_1_ != Blocks.WATER && (double)p_205175_4_ <= (double)p_205175_3_.nextInt(Math.max(1, p_205175_5_ / lvt_12_1_)) + (double)p_205175_5_ * 0.6D && lvt_11_1_) {
            this.setBlockState(p_205175_2_, p_205175_1_, Blocks.SNOW_BLOCK.getDefaultState());
         } else {
            this.setBlockState(p_205175_2_, p_205175_1_, p_205175_8_);
         }
      }

   }

   private int func_205176_a(int p_205176_1_, int p_205176_2_, int p_205176_3_) {
      int lvt_4_1_ = p_205176_3_;
      if (p_205176_1_ > 0 && p_205176_2_ - p_205176_1_ <= 3) {
         lvt_4_1_ = p_205176_3_ - (4 - (p_205176_2_ - p_205176_1_));
      }

      return lvt_4_1_;
   }

   private double func_205177_a(int p_205177_1_, int p_205177_2_, BlockPos p_205177_3_, int p_205177_4_, Random p_205177_5_) {
      float lvt_6_1_ = 10.0F * MathHelper.clamp(p_205177_5_.nextFloat(), 0.2F, 0.8F) / (float)p_205177_4_;
      return (double)lvt_6_1_ + Math.pow((double)(p_205177_1_ - p_205177_3_.getX()), 2.0D) + Math.pow((double)(p_205177_2_ - p_205177_3_.getZ()), 2.0D) - Math.pow((double)p_205177_4_, 2.0D);
   }

   private double func_205180_a(int p_205180_1_, int p_205180_2_, BlockPos p_205180_3_, int p_205180_4_, int p_205180_5_, double p_205180_6_) {
      return Math.pow(((double)(p_205180_1_ - p_205180_3_.getX()) * Math.cos(p_205180_6_) - (double)(p_205180_2_ - p_205180_3_.getZ()) * Math.sin(p_205180_6_)) / (double)p_205180_4_, 2.0D) + Math.pow(((double)(p_205180_1_ - p_205180_3_.getX()) * Math.sin(p_205180_6_) + (double)(p_205180_2_ - p_205180_3_.getZ()) * Math.cos(p_205180_6_)) / (double)p_205180_5_, 2.0D) - 1.0D;
   }

   private int func_205183_a(Random p_205183_1_, int p_205183_2_, int p_205183_3_, int p_205183_4_) {
      float lvt_5_1_ = 3.5F - p_205183_1_.nextFloat();
      float lvt_6_1_ = (1.0F - (float)Math.pow((double)p_205183_2_, 2.0D) / ((float)p_205183_3_ * lvt_5_1_)) * (float)p_205183_4_;
      if (p_205183_3_ > 15 + p_205183_1_.nextInt(5)) {
         int lvt_7_1_ = p_205183_2_ < 3 + p_205183_1_.nextInt(6) ? p_205183_2_ / 2 : p_205183_2_;
         lvt_6_1_ = (1.0F - (float)lvt_7_1_ / ((float)p_205183_3_ * lvt_5_1_ * 0.4F)) * (float)p_205183_4_;
      }

      return MathHelper.ceil(lvt_6_1_ / 2.0F);
   }

   private int func_205178_b(int p_205178_1_, int p_205178_2_, int p_205178_3_) {
      float lvt_4_1_ = 1.0F;
      float lvt_5_1_ = (1.0F - (float)Math.pow((double)p_205178_1_, 2.0D) / ((float)p_205178_2_ * 1.0F)) * (float)p_205178_3_;
      return MathHelper.ceil(lvt_5_1_ / 2.0F);
   }

   private int func_205187_b(Random p_205187_1_, int p_205187_2_, int p_205187_3_, int p_205187_4_) {
      float lvt_5_1_ = 1.0F + p_205187_1_.nextFloat() / 2.0F;
      float lvt_6_1_ = (1.0F - (float)p_205187_2_ / ((float)p_205187_3_ * lvt_5_1_)) * (float)p_205187_4_;
      return MathHelper.ceil(lvt_6_1_ / 2.0F);
   }

   private boolean isIce(Block p_205179_1_) {
      return p_205179_1_ == Blocks.PACKED_ICE || p_205179_1_ == Blocks.SNOW_BLOCK || p_205179_1_ == Blocks.BLUE_ICE;
   }

   private boolean func_205182_b(IBlockReader p_205182_1_, BlockPos p_205182_2_) {
      return p_205182_1_.getBlockState(p_205182_2_.down()).getMaterial() == Material.AIR;
   }

   private void func_205186_a(IWorld p_205186_1_, BlockPos p_205186_2_, int p_205186_3_, int p_205186_4_, boolean p_205186_5_, int p_205186_6_) {
      int lvt_7_1_ = p_205186_5_ ? p_205186_6_ : p_205186_3_ / 2;

      for(int lvt_8_1_ = -lvt_7_1_; lvt_8_1_ <= lvt_7_1_; ++lvt_8_1_) {
         for(int lvt_9_1_ = -lvt_7_1_; lvt_9_1_ <= lvt_7_1_; ++lvt_9_1_) {
            for(int lvt_10_1_ = 0; lvt_10_1_ <= p_205186_4_; ++lvt_10_1_) {
               BlockPos lvt_11_1_ = p_205186_2_.add(lvt_8_1_, lvt_10_1_, lvt_9_1_);
               Block lvt_12_1_ = p_205186_1_.getBlockState(lvt_11_1_).getBlock();
               if (this.isIce(lvt_12_1_) || lvt_12_1_ == Blocks.SNOW) {
                  if (this.func_205182_b(p_205186_1_, lvt_11_1_)) {
                     this.setBlockState(p_205186_1_, lvt_11_1_, Blocks.AIR.getDefaultState());
                     this.setBlockState(p_205186_1_, lvt_11_1_.up(), Blocks.AIR.getDefaultState());
                  } else if (this.isIce(lvt_12_1_)) {
                     Block[] lvt_13_1_ = new Block[]{p_205186_1_.getBlockState(lvt_11_1_.west()).getBlock(), p_205186_1_.getBlockState(lvt_11_1_.east()).getBlock(), p_205186_1_.getBlockState(lvt_11_1_.north()).getBlock(), p_205186_1_.getBlockState(lvt_11_1_.south()).getBlock()};
                     int lvt_14_1_ = 0;
                     Block[] var15 = lvt_13_1_;
                     int var16 = lvt_13_1_.length;

                     for(int var17 = 0; var17 < var16; ++var17) {
                        Block lvt_18_1_ = var15[var17];
                        if (!this.isIce(lvt_18_1_)) {
                           ++lvt_14_1_;
                        }
                     }

                     if (lvt_14_1_ >= 3) {
                        this.setBlockState(p_205186_1_, lvt_11_1_, Blocks.AIR.getDefaultState());
                     }
                  }
               }
            }
         }
      }

   }
}
