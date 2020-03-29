package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.PerlinNoiseGenerator;

public class BadlandsSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig> {
   private static final BlockState WHITE_TERRACOTTA;
   private static final BlockState ORANGE_TERRACOTTA;
   private static final BlockState TERRACOTTA;
   private static final BlockState YELLOW_TERRACOTTA;
   private static final BlockState BROWN_TERRACOTTA;
   private static final BlockState RED_TERRACOTTA;
   private static final BlockState LIGHT_GRAY_TERRACOTTA;
   protected BlockState[] field_215432_a;
   protected long field_215433_b;
   protected PerlinNoiseGenerator field_215435_c;
   protected PerlinNoiseGenerator field_215437_d;
   protected PerlinNoiseGenerator field_215439_e;

   public BadlandsSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> p_i51317_1_) {
      super(p_i51317_1_);
   }

   public void buildSurface(Random p_205610_1_, IChunk p_205610_2_, Biome p_205610_3_, int p_205610_4_, int p_205610_5_, int p_205610_6_, double p_205610_7_, BlockState p_205610_9_, BlockState p_205610_10_, int p_205610_11_, long p_205610_12_, SurfaceBuilderConfig p_205610_14_) {
      int lvt_15_1_ = p_205610_4_ & 15;
      int lvt_16_1_ = p_205610_5_ & 15;
      BlockState lvt_17_1_ = WHITE_TERRACOTTA;
      BlockState lvt_18_1_ = p_205610_3_.getSurfaceBuilderConfig().getUnder();
      int lvt_19_1_ = (int)(p_205610_7_ / 3.0D + 3.0D + p_205610_1_.nextDouble() * 0.25D);
      boolean lvt_20_1_ = Math.cos(p_205610_7_ / 3.0D * 3.141592653589793D) > 0.0D;
      int lvt_21_1_ = -1;
      boolean lvt_22_1_ = false;
      int lvt_23_1_ = 0;
      BlockPos.Mutable lvt_24_1_ = new BlockPos.Mutable();

      for(int lvt_25_1_ = p_205610_6_; lvt_25_1_ >= 0; --lvt_25_1_) {
         if (lvt_23_1_ < 15) {
            lvt_24_1_.setPos(lvt_15_1_, lvt_25_1_, lvt_16_1_);
            BlockState lvt_26_1_ = p_205610_2_.getBlockState(lvt_24_1_);
            if (lvt_26_1_.isAir()) {
               lvt_21_1_ = -1;
            } else if (lvt_26_1_.getBlock() == p_205610_9_.getBlock()) {
               if (lvt_21_1_ == -1) {
                  lvt_22_1_ = false;
                  if (lvt_19_1_ <= 0) {
                     lvt_17_1_ = Blocks.AIR.getDefaultState();
                     lvt_18_1_ = p_205610_9_;
                  } else if (lvt_25_1_ >= p_205610_11_ - 4 && lvt_25_1_ <= p_205610_11_ + 1) {
                     lvt_17_1_ = WHITE_TERRACOTTA;
                     lvt_18_1_ = p_205610_3_.getSurfaceBuilderConfig().getUnder();
                  }

                  if (lvt_25_1_ < p_205610_11_ && (lvt_17_1_ == null || lvt_17_1_.isAir())) {
                     lvt_17_1_ = p_205610_10_;
                  }

                  lvt_21_1_ = lvt_19_1_ + Math.max(0, lvt_25_1_ - p_205610_11_);
                  if (lvt_25_1_ >= p_205610_11_ - 1) {
                     if (lvt_25_1_ > p_205610_11_ + 3 + lvt_19_1_) {
                        BlockState lvt_27_3_;
                        if (lvt_25_1_ >= 64 && lvt_25_1_ <= 127) {
                           if (lvt_20_1_) {
                              lvt_27_3_ = TERRACOTTA;
                           } else {
                              lvt_27_3_ = this.func_215431_a(p_205610_4_, lvt_25_1_, p_205610_5_);
                           }
                        } else {
                           lvt_27_3_ = ORANGE_TERRACOTTA;
                        }

                        p_205610_2_.setBlockState(lvt_24_1_, lvt_27_3_, false);
                     } else {
                        p_205610_2_.setBlockState(lvt_24_1_, p_205610_3_.getSurfaceBuilderConfig().getTop(), false);
                        lvt_22_1_ = true;
                     }
                  } else {
                     p_205610_2_.setBlockState(lvt_24_1_, lvt_18_1_, false);
                     Block lvt_27_4_ = lvt_18_1_.getBlock();
                     if (lvt_27_4_ == Blocks.WHITE_TERRACOTTA || lvt_27_4_ == Blocks.ORANGE_TERRACOTTA || lvt_27_4_ == Blocks.MAGENTA_TERRACOTTA || lvt_27_4_ == Blocks.LIGHT_BLUE_TERRACOTTA || lvt_27_4_ == Blocks.YELLOW_TERRACOTTA || lvt_27_4_ == Blocks.LIME_TERRACOTTA || lvt_27_4_ == Blocks.PINK_TERRACOTTA || lvt_27_4_ == Blocks.GRAY_TERRACOTTA || lvt_27_4_ == Blocks.LIGHT_GRAY_TERRACOTTA || lvt_27_4_ == Blocks.CYAN_TERRACOTTA || lvt_27_4_ == Blocks.PURPLE_TERRACOTTA || lvt_27_4_ == Blocks.BLUE_TERRACOTTA || lvt_27_4_ == Blocks.BROWN_TERRACOTTA || lvt_27_4_ == Blocks.GREEN_TERRACOTTA || lvt_27_4_ == Blocks.RED_TERRACOTTA || lvt_27_4_ == Blocks.BLACK_TERRACOTTA) {
                        p_205610_2_.setBlockState(lvt_24_1_, ORANGE_TERRACOTTA, false);
                     }
                  }
               } else if (lvt_21_1_ > 0) {
                  --lvt_21_1_;
                  if (lvt_22_1_) {
                     p_205610_2_.setBlockState(lvt_24_1_, ORANGE_TERRACOTTA, false);
                  } else {
                     p_205610_2_.setBlockState(lvt_24_1_, this.func_215431_a(p_205610_4_, lvt_25_1_, p_205610_5_), false);
                  }
               }

               ++lvt_23_1_;
            }
         }
      }

   }

   public void setSeed(long p_205548_1_) {
      if (this.field_215433_b != p_205548_1_ || this.field_215432_a == null) {
         this.func_215430_b(p_205548_1_);
      }

      if (this.field_215433_b != p_205548_1_ || this.field_215435_c == null || this.field_215437_d == null) {
         SharedSeedRandom lvt_3_1_ = new SharedSeedRandom(p_205548_1_);
         this.field_215435_c = new PerlinNoiseGenerator(lvt_3_1_, 3, 0);
         this.field_215437_d = new PerlinNoiseGenerator(lvt_3_1_, 0, 0);
      }

      this.field_215433_b = p_205548_1_;
   }

   protected void func_215430_b(long p_215430_1_) {
      this.field_215432_a = new BlockState[64];
      Arrays.fill(this.field_215432_a, TERRACOTTA);
      SharedSeedRandom lvt_3_1_ = new SharedSeedRandom(p_215430_1_);
      this.field_215439_e = new PerlinNoiseGenerator(lvt_3_1_, 0, 0);

      int lvt_4_2_;
      for(lvt_4_2_ = 0; lvt_4_2_ < 64; ++lvt_4_2_) {
         lvt_4_2_ += lvt_3_1_.nextInt(5) + 1;
         if (lvt_4_2_ < 64) {
            this.field_215432_a[lvt_4_2_] = ORANGE_TERRACOTTA;
         }
      }

      lvt_4_2_ = lvt_3_1_.nextInt(4) + 2;

      int lvt_5_2_;
      int lvt_6_3_;
      int lvt_7_4_;
      int lvt_8_4_;
      for(lvt_5_2_ = 0; lvt_5_2_ < lvt_4_2_; ++lvt_5_2_) {
         lvt_6_3_ = lvt_3_1_.nextInt(3) + 1;
         lvt_7_4_ = lvt_3_1_.nextInt(64);

         for(lvt_8_4_ = 0; lvt_7_4_ + lvt_8_4_ < 64 && lvt_8_4_ < lvt_6_3_; ++lvt_8_4_) {
            this.field_215432_a[lvt_7_4_ + lvt_8_4_] = YELLOW_TERRACOTTA;
         }
      }

      lvt_5_2_ = lvt_3_1_.nextInt(4) + 2;

      int lvt_9_2_;
      for(lvt_6_3_ = 0; lvt_6_3_ < lvt_5_2_; ++lvt_6_3_) {
         lvt_7_4_ = lvt_3_1_.nextInt(3) + 2;
         lvt_8_4_ = lvt_3_1_.nextInt(64);

         for(lvt_9_2_ = 0; lvt_8_4_ + lvt_9_2_ < 64 && lvt_9_2_ < lvt_7_4_; ++lvt_9_2_) {
            this.field_215432_a[lvt_8_4_ + lvt_9_2_] = BROWN_TERRACOTTA;
         }
      }

      lvt_6_3_ = lvt_3_1_.nextInt(4) + 2;

      for(lvt_7_4_ = 0; lvt_7_4_ < lvt_6_3_; ++lvt_7_4_) {
         lvt_8_4_ = lvt_3_1_.nextInt(3) + 1;
         lvt_9_2_ = lvt_3_1_.nextInt(64);

         for(int lvt_10_1_ = 0; lvt_9_2_ + lvt_10_1_ < 64 && lvt_10_1_ < lvt_8_4_; ++lvt_10_1_) {
            this.field_215432_a[lvt_9_2_ + lvt_10_1_] = RED_TERRACOTTA;
         }
      }

      lvt_7_4_ = lvt_3_1_.nextInt(3) + 3;
      lvt_8_4_ = 0;

      for(lvt_9_2_ = 0; lvt_9_2_ < lvt_7_4_; ++lvt_9_2_) {
         int lvt_10_2_ = true;
         lvt_8_4_ += lvt_3_1_.nextInt(16) + 4;

         for(int lvt_11_1_ = 0; lvt_8_4_ + lvt_11_1_ < 64 && lvt_11_1_ < 1; ++lvt_11_1_) {
            this.field_215432_a[lvt_8_4_ + lvt_11_1_] = WHITE_TERRACOTTA;
            if (lvt_8_4_ + lvt_11_1_ > 1 && lvt_3_1_.nextBoolean()) {
               this.field_215432_a[lvt_8_4_ + lvt_11_1_ - 1] = LIGHT_GRAY_TERRACOTTA;
            }

            if (lvt_8_4_ + lvt_11_1_ < 63 && lvt_3_1_.nextBoolean()) {
               this.field_215432_a[lvt_8_4_ + lvt_11_1_ + 1] = LIGHT_GRAY_TERRACOTTA;
            }
         }
      }

   }

   protected BlockState func_215431_a(int p_215431_1_, int p_215431_2_, int p_215431_3_) {
      int lvt_4_1_ = (int)Math.round(this.field_215439_e.func_215464_a((double)p_215431_1_ / 512.0D, (double)p_215431_3_ / 512.0D, false) * 2.0D);
      return this.field_215432_a[(p_215431_2_ + lvt_4_1_ + 64) % 64];
   }

   static {
      WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
      ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
      TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();
      YELLOW_TERRACOTTA = Blocks.YELLOW_TERRACOTTA.getDefaultState();
      BROWN_TERRACOTTA = Blocks.BROWN_TERRACOTTA.getDefaultState();
      RED_TERRACOTTA = Blocks.RED_TERRACOTTA.getDefaultState();
      LIGHT_GRAY_TERRACOTTA = Blocks.LIGHT_GRAY_TERRACOTTA.getDefaultState();
   }
}
