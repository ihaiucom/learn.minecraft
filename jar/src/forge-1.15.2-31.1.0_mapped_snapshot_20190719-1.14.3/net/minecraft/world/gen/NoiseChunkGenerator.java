package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;

public abstract class NoiseChunkGenerator<T extends GenerationSettings> extends ChunkGenerator<T> {
   private static final float[] field_222561_h = (float[])Util.make(new float[13824], (p_lambda$static$0_0_) -> {
      for(int i = 0; i < 24; ++i) {
         for(int j = 0; j < 24; ++j) {
            for(int k = 0; k < 24; ++k) {
               p_lambda$static$0_0_[i * 24 * 24 + j * 24 + k] = (float)func_222554_b(j - 12, k - 12, i - 12);
            }
         }
      }

   });
   private static final BlockState AIR;
   private final int verticalNoiseGranularity;
   private final int horizontalNoiseGranularity;
   private final int noiseSizeX;
   private final int noiseSizeY;
   private final int noiseSizeZ;
   protected final SharedSeedRandom randomSeed;
   private final OctavesNoiseGenerator field_222568_o;
   private final OctavesNoiseGenerator field_222569_p;
   private final OctavesNoiseGenerator field_222570_q;
   private final INoiseGenerator surfaceDepthNoise;
   protected final BlockState defaultBlock;
   protected final BlockState defaultFluid;

   public NoiseChunkGenerator(IWorld p_i49931_1_, BiomeProvider p_i49931_2_, int p_i49931_3_, int p_i49931_4_, int p_i49931_5_, T p_i49931_6_, boolean p_i49931_7_) {
      super(p_i49931_1_, p_i49931_2_, p_i49931_6_);
      this.verticalNoiseGranularity = p_i49931_4_;
      this.horizontalNoiseGranularity = p_i49931_3_;
      this.defaultBlock = p_i49931_6_.getDefaultBlock();
      this.defaultFluid = p_i49931_6_.getDefaultFluid();
      this.noiseSizeX = 16 / this.horizontalNoiseGranularity;
      this.noiseSizeY = p_i49931_5_ / this.verticalNoiseGranularity;
      this.noiseSizeZ = 16 / this.horizontalNoiseGranularity;
      this.randomSeed = new SharedSeedRandom(this.seed);
      this.field_222568_o = new OctavesNoiseGenerator(this.randomSeed, 15, 0);
      this.field_222569_p = new OctavesNoiseGenerator(this.randomSeed, 15, 0);
      this.field_222570_q = new OctavesNoiseGenerator(this.randomSeed, 7, 0);
      this.surfaceDepthNoise = (INoiseGenerator)(p_i49931_7_ ? new PerlinNoiseGenerator(this.randomSeed, 3, 0) : new OctavesNoiseGenerator(this.randomSeed, 3, 0));
   }

   private double func_222552_a(int p_222552_1_, int p_222552_2_, int p_222552_3_, double p_222552_4_, double p_222552_6_, double p_222552_8_, double p_222552_10_) {
      double d0 = 0.0D;
      double d1 = 0.0D;
      double d2 = 0.0D;
      double d3 = 1.0D;

      for(int i = 0; i < 16; ++i) {
         double d4 = OctavesNoiseGenerator.maintainPrecision((double)p_222552_1_ * p_222552_4_ * d3);
         double d5 = OctavesNoiseGenerator.maintainPrecision((double)p_222552_2_ * p_222552_6_ * d3);
         double d6 = OctavesNoiseGenerator.maintainPrecision((double)p_222552_3_ * p_222552_4_ * d3);
         double d7 = p_222552_6_ * d3;
         ImprovedNoiseGenerator improvednoisegenerator = this.field_222568_o.getOctave(i);
         if (improvednoisegenerator != null) {
            d0 += improvednoisegenerator.func_215456_a(d4, d5, d6, d7, (double)p_222552_2_ * d7) / d3;
         }

         ImprovedNoiseGenerator improvednoisegenerator1 = this.field_222569_p.getOctave(i);
         if (improvednoisegenerator1 != null) {
            d1 += improvednoisegenerator1.func_215456_a(d4, d5, d6, d7, (double)p_222552_2_ * d7) / d3;
         }

         if (i < 8) {
            ImprovedNoiseGenerator improvednoisegenerator2 = this.field_222570_q.getOctave(i);
            if (improvednoisegenerator2 != null) {
               d2 += improvednoisegenerator2.func_215456_a(OctavesNoiseGenerator.maintainPrecision((double)p_222552_1_ * p_222552_8_ * d3), OctavesNoiseGenerator.maintainPrecision((double)p_222552_2_ * p_222552_10_ * d3), OctavesNoiseGenerator.maintainPrecision((double)p_222552_3_ * p_222552_8_ * d3), p_222552_10_ * d3, (double)p_222552_2_ * p_222552_10_ * d3) / d3;
            }
         }

         d3 /= 2.0D;
      }

      return MathHelper.clampedLerp(d0 / 512.0D, d1 / 512.0D, (d2 / 10.0D + 1.0D) / 2.0D);
   }

   protected double[] func_222547_b(int p_222547_1_, int p_222547_2_) {
      double[] adouble = new double[this.noiseSizeY + 1];
      this.func_222548_a(adouble, p_222547_1_, p_222547_2_);
      return adouble;
   }

   protected void func_222546_a(double[] p_222546_1_, int p_222546_2_, int p_222546_3_, double p_222546_4_, double p_222546_6_, double p_222546_8_, double p_222546_10_, int p_222546_12_, int p_222546_13_) {
      double[] adouble = this.func_222549_a(p_222546_2_, p_222546_3_);
      double d0 = adouble[0];
      double d1 = adouble[1];
      double d2 = this.func_222551_g();
      double d3 = this.func_222553_h();

      for(int i = 0; i < this.func_222550_i(); ++i) {
         double d4 = this.func_222552_a(p_222546_2_, i, p_222546_3_, p_222546_4_, p_222546_6_, p_222546_8_, p_222546_10_);
         d4 -= this.func_222545_a(d0, d1, i);
         if ((double)i > d2) {
            d4 = MathHelper.clampedLerp(d4, (double)p_222546_13_, ((double)i - d2) / (double)p_222546_12_);
         } else if ((double)i < d3) {
            d4 = MathHelper.clampedLerp(d4, -30.0D, (d3 - (double)i) / (d3 - 1.0D));
         }

         p_222546_1_[i] = d4;
      }

   }

   protected abstract double[] func_222549_a(int var1, int var2);

   protected abstract double func_222545_a(double var1, double var3, int var5);

   protected double func_222551_g() {
      return (double)(this.func_222550_i() - 4);
   }

   protected double func_222553_h() {
      return 0.0D;
   }

   public int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type p_222529_3_) {
      int i = Math.floorDiv(p_222529_1_, this.horizontalNoiseGranularity);
      int j = Math.floorDiv(p_222529_2_, this.horizontalNoiseGranularity);
      int k = Math.floorMod(p_222529_1_, this.horizontalNoiseGranularity);
      int l = Math.floorMod(p_222529_2_, this.horizontalNoiseGranularity);
      double d0 = (double)k / (double)this.horizontalNoiseGranularity;
      double d1 = (double)l / (double)this.horizontalNoiseGranularity;
      double[][] adouble = new double[][]{this.func_222547_b(i, j), this.func_222547_b(i, j + 1), this.func_222547_b(i + 1, j), this.func_222547_b(i + 1, j + 1)};
      int i1 = this.getSeaLevel();

      for(int j1 = this.noiseSizeY - 1; j1 >= 0; --j1) {
         double d2 = adouble[0][j1];
         double d3 = adouble[1][j1];
         double d4 = adouble[2][j1];
         double d5 = adouble[3][j1];
         double d6 = adouble[0][j1 + 1];
         double d7 = adouble[1][j1 + 1];
         double d8 = adouble[2][j1 + 1];
         double d9 = adouble[3][j1 + 1];

         for(int k1 = this.verticalNoiseGranularity - 1; k1 >= 0; --k1) {
            double d10 = (double)k1 / (double)this.verticalNoiseGranularity;
            double d11 = MathHelper.lerp3(d10, d0, d1, d2, d6, d4, d8, d3, d7, d5, d9);
            int l1 = j1 * this.verticalNoiseGranularity + k1;
            if (d11 > 0.0D || l1 < i1) {
               BlockState blockstate;
               if (d11 > 0.0D) {
                  blockstate = this.defaultBlock;
               } else {
                  blockstate = this.defaultFluid;
               }

               if (p_222529_3_.func_222684_d().test(blockstate)) {
                  return l1 + 1;
               }
            }
         }
      }

      return 0;
   }

   protected abstract void func_222548_a(double[] var1, int var2, int var3);

   public int func_222550_i() {
      return this.noiseSizeY + 1;
   }

   public void func_225551_a_(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {
      ChunkPos chunkpos = p_225551_2_.getPos();
      int i = chunkpos.x;
      int j = chunkpos.z;
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
      sharedseedrandom.setBaseChunkSeed(i, j);
      ChunkPos chunkpos1 = p_225551_2_.getPos();
      int k = chunkpos1.getXStart();
      int l = chunkpos1.getZStart();
      double d0 = 0.0625D;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      for(int i1 = 0; i1 < 16; ++i1) {
         for(int j1 = 0; j1 < 16; ++j1) {
            int k1 = k + i1;
            int l1 = l + j1;
            int i2 = p_225551_2_.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, i1, j1) + 1;
            double d1 = this.surfaceDepthNoise.func_215460_a((double)k1 * 0.0625D, (double)l1 * 0.0625D, 0.0625D, (double)i1 * 0.0625D) * 15.0D;
            p_225551_1_.func_226691_t_(blockpos$mutable.setPos(k + i1, i2, l + j1)).buildSurface(sharedseedrandom, p_225551_2_, k1, l1, i2, d1, this.getSettings().getDefaultBlock(), this.getSettings().getDefaultFluid(), this.getSeaLevel(), this.world.getSeed());
         }
      }

      this.makeBedrock(p_225551_2_, sharedseedrandom);
   }

   protected void makeBedrock(IChunk p_222555_1_, Random p_222555_2_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      int i = p_222555_1_.getPos().getXStart();
      int j = p_222555_1_.getPos().getZStart();
      T t = this.getSettings();
      int k = t.getBedrockFloorHeight();
      int l = t.getBedrockRoofHeight();
      Iterator var9 = BlockPos.getAllInBoxMutable(i, 0, j, i + 15, 0, j + 15).iterator();

      while(true) {
         BlockPos blockpos;
         int j1;
         do {
            if (!var9.hasNext()) {
               return;
            }

            blockpos = (BlockPos)var9.next();
            if (l > 0) {
               for(j1 = l; j1 >= l - 4; --j1) {
                  if (j1 >= l - p_222555_2_.nextInt(5)) {
                     p_222555_1_.setBlockState(blockpos$mutable.setPos(blockpos.getX(), j1, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                  }
               }
            }
         } while(k >= 256);

         for(j1 = k + 4; j1 >= k; --j1) {
            if (j1 <= k + p_222555_2_.nextInt(5)) {
               p_222555_1_.setBlockState(blockpos$mutable.setPos(blockpos.getX(), j1, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
            }
         }
      }
   }

   public void makeBase(IWorld p_222537_1_, IChunk p_222537_2_) {
      int i = this.getSeaLevel();
      ObjectList<AbstractVillagePiece> objectlist = new ObjectArrayList(10);
      ObjectList<JigsawJunction> objectlist1 = new ObjectArrayList(32);
      ChunkPos chunkpos = p_222537_2_.getPos();
      int j = chunkpos.x;
      int k = chunkpos.z;
      int l = j << 4;
      int i1 = k << 4;
      Iterator var11 = Feature.field_214488_aQ.iterator();

      label175:
      while(var11.hasNext()) {
         Structure<?> structure = (Structure)var11.next();
         String s = structure.getStructureName();
         LongIterator longiterator = p_222537_2_.getStructureReferences(s).iterator();

         label173:
         while(true) {
            StructureStart structurestart;
            do {
               do {
                  if (!longiterator.hasNext()) {
                     continue label175;
                  }

                  long j1 = longiterator.nextLong();
                  ChunkPos chunkpos1 = new ChunkPos(j1);
                  IChunk ichunk = p_222537_1_.getChunk(chunkpos1.x, chunkpos1.z);
                  structurestart = ichunk.getStructureStart(s);
               } while(structurestart == null);
            } while(!structurestart.isValid());

            Iterator var20 = structurestart.getComponents().iterator();

            while(true) {
               StructurePiece structurepiece;
               do {
                  do {
                     if (!var20.hasNext()) {
                        continue label173;
                     }

                     structurepiece = (StructurePiece)var20.next();
                  } while(!structurepiece.func_214810_a(chunkpos, 12));
               } while(!(structurepiece instanceof AbstractVillagePiece));

               AbstractVillagePiece abstractvillagepiece = (AbstractVillagePiece)structurepiece;
               JigsawPattern.PlacementBehaviour jigsawpattern$placementbehaviour = abstractvillagepiece.func_214826_b().getPlacementBehaviour();
               if (jigsawpattern$placementbehaviour == JigsawPattern.PlacementBehaviour.RIGID) {
                  objectlist.add(abstractvillagepiece);
               }

               Iterator var24 = abstractvillagepiece.getJunctions().iterator();

               while(var24.hasNext()) {
                  JigsawJunction jigsawjunction = (JigsawJunction)var24.next();
                  int k1 = jigsawjunction.getSourceX();
                  int l1 = jigsawjunction.getSourceZ();
                  if (k1 > l - 12 && l1 > i1 - 12 && k1 < l + 15 + 12 && l1 < i1 + 15 + 12) {
                     objectlist1.add(jigsawjunction);
                  }
               }
            }
         }
      }

      double[][][] adouble = new double[2][this.noiseSizeZ + 1][this.noiseSizeY + 1];

      for(int j5 = 0; j5 < this.noiseSizeZ + 1; ++j5) {
         adouble[0][j5] = new double[this.noiseSizeY + 1];
         this.func_222548_a(adouble[0][j5], j * this.noiseSizeX, k * this.noiseSizeZ + j5);
         adouble[1][j5] = new double[this.noiseSizeY + 1];
      }

      ChunkPrimer chunkprimer = (ChunkPrimer)p_222537_2_;
      Heightmap heightmap = chunkprimer.func_217303_b(Heightmap.Type.OCEAN_FLOOR_WG);
      Heightmap heightmap1 = chunkprimer.func_217303_b(Heightmap.Type.WORLD_SURFACE_WG);
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      ObjectListIterator<AbstractVillagePiece> objectlistiterator = objectlist.iterator();
      ObjectListIterator<JigsawJunction> objectlistiterator1 = objectlist1.iterator();

      for(int k5 = 0; k5 < this.noiseSizeX; ++k5) {
         int i6;
         for(i6 = 0; i6 < this.noiseSizeZ + 1; ++i6) {
            this.func_222548_a(adouble[1][i6], j * this.noiseSizeX + k5 + 1, k * this.noiseSizeZ + i6);
         }

         for(i6 = 0; i6 < this.noiseSizeZ; ++i6) {
            ChunkSection chunksection = chunkprimer.func_217332_a(15);
            chunksection.lock();

            for(int j6 = this.noiseSizeY - 1; j6 >= 0; --j6) {
               double d16 = adouble[0][i6][j6];
               double d17 = adouble[0][i6 + 1][j6];
               double d18 = adouble[1][i6][j6];
               double d0 = adouble[1][i6 + 1][j6];
               double d1 = adouble[0][i6][j6 + 1];
               double d2 = adouble[0][i6 + 1][j6 + 1];
               double d3 = adouble[1][i6][j6 + 1];
               double d4 = adouble[1][i6 + 1][j6 + 1];

               for(int i2 = this.verticalNoiseGranularity - 1; i2 >= 0; --i2) {
                  int j2 = j6 * this.verticalNoiseGranularity + i2;
                  int k2 = j2 & 15;
                  int l2 = j2 >> 4;
                  if (chunksection.getYLocation() >> 4 != l2) {
                     chunksection.unlock();
                     chunksection = chunkprimer.func_217332_a(l2);
                     chunksection.lock();
                  }

                  double d5 = (double)i2 / (double)this.verticalNoiseGranularity;
                  double d6 = MathHelper.lerp(d5, d16, d1);
                  double d7 = MathHelper.lerp(d5, d18, d3);
                  double d8 = MathHelper.lerp(d5, d17, d2);
                  double d9 = MathHelper.lerp(d5, d0, d4);

                  for(int i3 = 0; i3 < this.horizontalNoiseGranularity; ++i3) {
                     int j3 = l + k5 * this.horizontalNoiseGranularity + i3;
                     int k3 = j3 & 15;
                     double d10 = (double)i3 / (double)this.horizontalNoiseGranularity;
                     double d11 = MathHelper.lerp(d10, d6, d7);
                     double d12 = MathHelper.lerp(d10, d8, d9);

                     for(int l3 = 0; l3 < this.horizontalNoiseGranularity; ++l3) {
                        int i4 = i1 + i6 * this.horizontalNoiseGranularity + l3;
                        int j4 = i4 & 15;
                        double d13 = (double)l3 / (double)this.horizontalNoiseGranularity;
                        double d14 = MathHelper.lerp(d13, d11, d12);
                        double d15 = MathHelper.clamp(d14 / 200.0D, -1.0D, 1.0D);

                        int k4;
                        int l4;
                        int i5;
                        for(d15 = d15 / 2.0D - d15 * d15 * d15 / 24.0D; objectlistiterator.hasNext(); d15 += func_222556_a(k4, l4, i5) * 0.8D) {
                           AbstractVillagePiece abstractvillagepiece1 = (AbstractVillagePiece)objectlistiterator.next();
                           MutableBoundingBox mutableboundingbox = abstractvillagepiece1.getBoundingBox();
                           k4 = Math.max(0, Math.max(mutableboundingbox.minX - j3, j3 - mutableboundingbox.maxX));
                           l4 = j2 - (mutableboundingbox.minY + abstractvillagepiece1.getGroundLevelDelta());
                           i5 = Math.max(0, Math.max(mutableboundingbox.minZ - i4, i4 - mutableboundingbox.maxZ));
                        }

                        objectlistiterator.back(objectlist.size());

                        while(objectlistiterator1.hasNext()) {
                           JigsawJunction jigsawjunction1 = (JigsawJunction)objectlistiterator1.next();
                           int k6 = j3 - jigsawjunction1.getSourceX();
                           k4 = j2 - jigsawjunction1.getSourceGroundY();
                           l4 = i4 - jigsawjunction1.getSourceZ();
                           d15 += func_222556_a(k6, k4, l4) * 0.4D;
                        }

                        objectlistiterator1.back(objectlist1.size());
                        BlockState blockstate;
                        if (d15 > 0.0D) {
                           blockstate = this.defaultBlock;
                        } else if (j2 < i) {
                           blockstate = this.defaultFluid;
                        } else {
                           blockstate = AIR;
                        }

                        if (blockstate != AIR) {
                           blockpos$mutable.setPos(j3, j2, i4);
                           if (blockstate.getLightValue(chunkprimer, blockpos$mutable) != 0) {
                              chunkprimer.addLightPosition(blockpos$mutable);
                           }

                           chunksection.setBlockState(k3, k2, j4, blockstate, false);
                           heightmap.update(k3, j2, j4, blockstate);
                           heightmap1.update(k3, j2, j4, blockstate);
                        }
                     }
                  }
               }
            }

            chunksection.unlock();
         }

         double[][] adouble1 = adouble[0];
         adouble[0] = adouble[1];
         adouble[1] = adouble1;
      }

   }

   private static double func_222556_a(int p_222556_0_, int p_222556_1_, int p_222556_2_) {
      int i = p_222556_0_ + 12;
      int j = p_222556_1_ + 12;
      int k = p_222556_2_ + 12;
      if (i >= 0 && i < 24) {
         if (j >= 0 && j < 24) {
            return k >= 0 && k < 24 ? (double)field_222561_h[k * 24 * 24 + i * 24 + j] : 0.0D;
         } else {
            return 0.0D;
         }
      } else {
         return 0.0D;
      }
   }

   private static double func_222554_b(int p_222554_0_, int p_222554_1_, int p_222554_2_) {
      double d0 = (double)(p_222554_0_ * p_222554_0_ + p_222554_2_ * p_222554_2_);
      double d1 = (double)p_222554_1_ + 0.5D;
      double d2 = d1 * d1;
      double d3 = Math.pow(2.718281828459045D, -(d2 / 16.0D + d0 / 16.0D));
      double d4 = -d1 * MathHelper.fastInvSqrt(d2 / 2.0D + d0 / 2.0D) / 2.0D;
      return d4 * d3;
   }

   static {
      AIR = Blocks.AIR.getDefaultState();
   }
}