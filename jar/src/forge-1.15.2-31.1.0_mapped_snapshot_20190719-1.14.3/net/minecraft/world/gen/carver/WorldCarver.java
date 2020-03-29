package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class WorldCarver<C extends ICarverConfig> extends ForgeRegistryEntry<WorldCarver<?>> {
   public static final WorldCarver<ProbabilityConfig> CAVE = register("cave", new CaveWorldCarver(ProbabilityConfig::deserialize, 256));
   public static final WorldCarver<ProbabilityConfig> HELL_CAVE = register("hell_cave", new NetherCaveWorldCarver(ProbabilityConfig::deserialize));
   public static final WorldCarver<ProbabilityConfig> CANYON = register("canyon", new CanyonWorldCarver(ProbabilityConfig::deserialize));
   public static final WorldCarver<ProbabilityConfig> UNDERWATER_CANYON = register("underwater_canyon", new UnderwaterCanyonWorldCarver(ProbabilityConfig::deserialize));
   public static final WorldCarver<ProbabilityConfig> UNDERWATER_CAVE = register("underwater_cave", new UnderwaterCaveWorldCarver(ProbabilityConfig::deserialize));
   protected static final BlockState AIR;
   protected static final BlockState CAVE_AIR;
   protected static final IFluidState WATER;
   protected static final IFluidState LAVA;
   protected Set<Block> carvableBlocks;
   protected Set<Fluid> carvableFluids;
   private final Function<Dynamic<?>, ? extends C> field_222721_m;
   protected final int maxHeight;

   private static <C extends ICarverConfig, F extends WorldCarver<C>> F register(String p_222699_0_, F p_222699_1_) {
      return (WorldCarver)Registry.register((Registry)Registry.CARVER, (String)p_222699_0_, (Object)p_222699_1_);
   }

   public WorldCarver(Function<Dynamic<?>, ? extends C> p_i49921_1_, int p_i49921_2_) {
      this.carvableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE});
      this.carvableFluids = ImmutableSet.of(Fluids.WATER);
      this.field_222721_m = p_i49921_1_;
      this.maxHeight = p_i49921_2_;
   }

   public int func_222704_c() {
      return 4;
   }

   protected boolean func_227208_a_(IChunk p_227208_1_, Function<BlockPos, Biome> p_227208_2_, long p_227208_3_, int p_227208_5_, int p_227208_6_, int p_227208_7_, double p_227208_8_, double p_227208_10_, double p_227208_12_, double p_227208_14_, double p_227208_16_, BitSet p_227208_18_) {
      Random random = new Random(p_227208_3_ + (long)p_227208_6_ + (long)p_227208_7_);
      double d0 = (double)(p_227208_6_ * 16 + 8);
      double d1 = (double)(p_227208_7_ * 16 + 8);
      if (p_227208_8_ >= d0 - 16.0D - p_227208_14_ * 2.0D && p_227208_12_ >= d1 - 16.0D - p_227208_14_ * 2.0D && p_227208_8_ <= d0 + 16.0D + p_227208_14_ * 2.0D && p_227208_12_ <= d1 + 16.0D + p_227208_14_ * 2.0D) {
         int i = Math.max(MathHelper.floor(p_227208_8_ - p_227208_14_) - p_227208_6_ * 16 - 1, 0);
         int j = Math.min(MathHelper.floor(p_227208_8_ + p_227208_14_) - p_227208_6_ * 16 + 1, 16);
         int k = Math.max(MathHelper.floor(p_227208_10_ - p_227208_16_) - 1, 1);
         int l = Math.min(MathHelper.floor(p_227208_10_ + p_227208_16_) + 1, this.maxHeight - 8);
         int i1 = Math.max(MathHelper.floor(p_227208_12_ - p_227208_14_) - p_227208_7_ * 16 - 1, 0);
         int j1 = Math.min(MathHelper.floor(p_227208_12_ + p_227208_14_) - p_227208_7_ * 16 + 1, 16);
         if (this.func_222700_a(p_227208_1_, p_227208_6_, p_227208_7_, i, j, k, l, i1, j1)) {
            return false;
         } else {
            boolean flag = false;
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
            BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable();
            BlockPos.Mutable blockpos$mutable2 = new BlockPos.Mutable();

            for(int k1 = i; k1 < j; ++k1) {
               int l1 = k1 + p_227208_6_ * 16;
               double d2 = ((double)l1 + 0.5D - p_227208_8_) / p_227208_14_;

               for(int i2 = i1; i2 < j1; ++i2) {
                  int j2 = i2 + p_227208_7_ * 16;
                  double d3 = ((double)j2 + 0.5D - p_227208_12_) / p_227208_14_;
                  if (d2 * d2 + d3 * d3 < 1.0D) {
                     AtomicBoolean atomicboolean = new AtomicBoolean(false);

                     for(int k2 = l; k2 > k; --k2) {
                        double d4 = ((double)k2 - 0.5D - p_227208_10_) / p_227208_16_;
                        if (!this.func_222708_a(d2, d4, d3, k2)) {
                           flag |= this.func_225556_a_(p_227208_1_, p_227208_2_, p_227208_18_, random, blockpos$mutable, blockpos$mutable1, blockpos$mutable2, p_227208_5_, p_227208_6_, p_227208_7_, l1, j2, k1, k2, i2, atomicboolean);
                        }
                     }
                  }
               }
            }

            return flag;
         }
      } else {
         return false;
      }
   }

   protected boolean func_225556_a_(IChunk p_225556_1_, Function<BlockPos, Biome> p_225556_2_, BitSet p_225556_3_, Random p_225556_4_, BlockPos.Mutable p_225556_5_, BlockPos.Mutable p_225556_6_, BlockPos.Mutable p_225556_7_, int p_225556_8_, int p_225556_9_, int p_225556_10_, int p_225556_11_, int p_225556_12_, int p_225556_13_, int p_225556_14_, int p_225556_15_, AtomicBoolean p_225556_16_) {
      int i = p_225556_13_ | p_225556_15_ << 4 | p_225556_14_ << 8;
      if (p_225556_3_.get(i)) {
         return false;
      } else {
         p_225556_3_.set(i);
         p_225556_5_.setPos(p_225556_11_, p_225556_14_, p_225556_12_);
         BlockState blockstate = p_225556_1_.getBlockState(p_225556_5_);
         BlockState blockstate1 = p_225556_1_.getBlockState(p_225556_6_.setPos((Vec3i)p_225556_5_).move(Direction.UP));
         if (blockstate.getBlock() == Blocks.GRASS_BLOCK || blockstate.getBlock() == Blocks.MYCELIUM) {
            p_225556_16_.set(true);
         }

         if (!this.canCarveBlock(blockstate, blockstate1)) {
            return false;
         } else {
            if (p_225556_14_ < 11) {
               p_225556_1_.setBlockState(p_225556_5_, LAVA.getBlockState(), false);
            } else {
               p_225556_1_.setBlockState(p_225556_5_, CAVE_AIR, false);
               if (p_225556_16_.get()) {
                  p_225556_7_.setPos((Vec3i)p_225556_5_).move(Direction.DOWN);
                  if (p_225556_1_.getBlockState(p_225556_7_).getBlock() == Blocks.DIRT) {
                     p_225556_1_.setBlockState(p_225556_7_, ((Biome)p_225556_2_.apply(p_225556_5_)).getSurfaceBuilderConfig().getTop(), false);
                  }
               }
            }

            return true;
         }
      }
   }

   public abstract boolean func_225555_a_(IChunk var1, Function<BlockPos, Biome> var2, Random var3, int var4, int var5, int var6, int var7, int var8, BitSet var9, C var10);

   public abstract boolean shouldCarve(Random var1, int var2, int var3, C var4);

   protected boolean func_222706_a(BlockState p_222706_1_) {
      return this.carvableBlocks.contains(p_222706_1_.getBlock());
   }

   protected boolean canCarveBlock(BlockState p_222707_1_, BlockState p_222707_2_) {
      Block block = p_222707_1_.getBlock();
      return this.func_222706_a(p_222707_1_) || (block == Blocks.SAND || block == Blocks.GRAVEL) && !p_222707_2_.getFluidState().isTagged(FluidTags.WATER);
   }

   protected boolean func_222700_a(IChunk p_222700_1_, int p_222700_2_, int p_222700_3_, int p_222700_4_, int p_222700_5_, int p_222700_6_, int p_222700_7_, int p_222700_8_, int p_222700_9_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int i = p_222700_4_; i < p_222700_5_; ++i) {
         for(int j = p_222700_8_; j < p_222700_9_; ++j) {
            for(int k = p_222700_6_ - 1; k <= p_222700_7_ + 1; ++k) {
               if (this.carvableFluids.contains(p_222700_1_.getFluidState(blockpos$mutable.setPos(i + p_222700_2_ * 16, k, j + p_222700_3_ * 16)).getFluid())) {
                  return true;
               }

               if (k != p_222700_7_ + 1 && !this.isOnEdge(p_222700_4_, p_222700_5_, p_222700_8_, p_222700_9_, i, j)) {
                  k = p_222700_7_;
               }
            }
         }
      }

      return false;
   }

   private boolean isOnEdge(int p_222701_1_, int p_222701_2_, int p_222701_3_, int p_222701_4_, int p_222701_5_, int p_222701_6_) {
      return p_222701_5_ == p_222701_1_ || p_222701_5_ == p_222701_2_ - 1 || p_222701_6_ == p_222701_3_ || p_222701_6_ == p_222701_4_ - 1;
   }

   protected boolean func_222702_a(int p_222702_1_, int p_222702_2_, double p_222702_3_, double p_222702_5_, int p_222702_7_, int p_222702_8_, float p_222702_9_) {
      double d0 = (double)(p_222702_1_ * 16 + 8);
      double d1 = (double)(p_222702_2_ * 16 + 8);
      double d2 = p_222702_3_ - d0;
      double d3 = p_222702_5_ - d1;
      double d4 = (double)(p_222702_8_ - p_222702_7_);
      double d5 = (double)(p_222702_9_ + 2.0F + 16.0F);
      return d2 * d2 + d3 * d3 - d4 * d4 <= d5 * d5;
   }

   protected abstract boolean func_222708_a(double var1, double var3, double var5, int var7);

   static {
      AIR = Blocks.AIR.getDefaultState();
      CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
      WATER = Fluids.WATER.getDefaultState();
      LAVA = Fluids.LAVA.getDefaultState();
   }
}
