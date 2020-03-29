package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.level.ColorResolver;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IWorldReader extends ILightReader, ICollisionReader, BiomeManager.IBiomeReader {
   @Nullable
   IChunk getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

   /** @deprecated */
   @Deprecated
   boolean chunkExists(int var1, int var2);

   int getHeight(Heightmap.Type var1, int var2, int var3);

   int getSkylightSubtracted();

   BiomeManager func_225523_d_();

   default Biome func_226691_t_(BlockPos p_226691_1_) {
      return this.func_225523_d_().func_226836_a_(p_226691_1_);
   }

   @OnlyIn(Dist.CLIENT)
   default int func_225525_a_(BlockPos p_225525_1_, ColorResolver p_225525_2_) {
      return p_225525_2_.getColor(this.func_226691_t_(p_225525_1_), (double)p_225525_1_.getX(), (double)p_225525_1_.getZ());
   }

   default Biome func_225526_b_(int p_225526_1_, int p_225526_2_, int p_225526_3_) {
      IChunk ichunk = this.getChunk(p_225526_1_ >> 2, p_225526_3_ >> 2, ChunkStatus.BIOMES, false);
      return ichunk != null && ichunk.func_225549_i_() != null ? ichunk.func_225549_i_().func_225526_b_(p_225526_1_, p_225526_2_, p_225526_3_) : this.func_225604_a_(p_225526_1_, p_225526_2_, p_225526_3_);
   }

   Biome func_225604_a_(int var1, int var2, int var3);

   boolean isRemote();

   int getSeaLevel();

   Dimension getDimension();

   default BlockPos getHeight(Heightmap.Type p_205770_1_, BlockPos p_205770_2_) {
      return new BlockPos(p_205770_2_.getX(), this.getHeight(p_205770_1_, p_205770_2_.getX(), p_205770_2_.getZ()), p_205770_2_.getZ());
   }

   default boolean isAirBlock(BlockPos p_175623_1_) {
      return this.getBlockState(p_175623_1_).isAir(this, p_175623_1_);
   }

   default boolean canBlockSeeSky(BlockPos p_175710_1_) {
      if (p_175710_1_.getY() >= this.getSeaLevel()) {
         return this.func_226660_f_(p_175710_1_);
      } else {
         BlockPos blockpos = new BlockPos(p_175710_1_.getX(), this.getSeaLevel(), p_175710_1_.getZ());
         if (!this.func_226660_f_(blockpos)) {
            return false;
         } else {
            for(BlockPos blockpos1 = blockpos.down(); blockpos1.getY() > p_175710_1_.getY(); blockpos1 = blockpos1.down()) {
               BlockState blockstate = this.getBlockState(blockpos1);
               if (blockstate.getOpacity(this, blockpos1) > 0 && !blockstate.getMaterial().isLiquid()) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   /** @deprecated */
   @Deprecated
   default float getBrightness(BlockPos p_205052_1_) {
      return this.getDimension().func_227174_a_(this.getLight(p_205052_1_));
   }

   default int getStrongPower(BlockPos p_175627_1_, Direction p_175627_2_) {
      return this.getBlockState(p_175627_1_).getStrongPower(this, p_175627_1_, p_175627_2_);
   }

   default IChunk getChunk(BlockPos p_217349_1_) {
      return this.getChunk(p_217349_1_.getX() >> 4, p_217349_1_.getZ() >> 4);
   }

   default IChunk getChunk(int p_212866_1_, int p_212866_2_) {
      return this.getChunk(p_212866_1_, p_212866_2_, ChunkStatus.FULL, true);
   }

   default IChunk getChunk(int p_217348_1_, int p_217348_2_, ChunkStatus p_217348_3_) {
      return this.getChunk(p_217348_1_, p_217348_2_, p_217348_3_, true);
   }

   @Nullable
   default IBlockReader func_225522_c_(int p_225522_1_, int p_225522_2_) {
      return this.getChunk(p_225522_1_, p_225522_2_, ChunkStatus.EMPTY, false);
   }

   default boolean hasWater(BlockPos p_201671_1_) {
      return this.getFluidState(p_201671_1_).isTagged(FluidTags.WATER);
   }

   default boolean containsAnyLiquid(AxisAlignedBB p_72953_1_) {
      int i = MathHelper.floor(p_72953_1_.minX);
      int j = MathHelper.ceil(p_72953_1_.maxX);
      int k = MathHelper.floor(p_72953_1_.minY);
      int l = MathHelper.ceil(p_72953_1_.maxY);
      int i1 = MathHelper.floor(p_72953_1_.minZ);
      int j1 = MathHelper.ceil(p_72953_1_.maxZ);
      BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain();
      Throwable var9 = null;

      try {
         for(int k1 = i; k1 < j; ++k1) {
            for(int l1 = k; l1 < l; ++l1) {
               for(int i2 = i1; i2 < j1; ++i2) {
                  BlockState blockstate = this.getBlockState(blockpos$pooledmutable.setPos(k1, l1, i2));
                  if (!blockstate.getFluidState().isEmpty()) {
                     boolean flag = true;
                     boolean var15 = flag;
                     return var15;
                  }
               }
            }
         }

         boolean var27 = false;
         return var27;
      } catch (Throwable var25) {
         var9 = var25;
         throw var25;
      } finally {
         if (blockpos$pooledmutable != null) {
            if (var9 != null) {
               try {
                  blockpos$pooledmutable.close();
               } catch (Throwable var24) {
                  var9.addSuppressed(var24);
               }
            } else {
               blockpos$pooledmutable.close();
            }
         }

      }
   }

   default int getLight(BlockPos p_201696_1_) {
      return this.getNeighborAwareLightSubtracted(p_201696_1_, this.getSkylightSubtracted());
   }

   default int getNeighborAwareLightSubtracted(BlockPos p_205049_1_, int p_205049_2_) {
      return p_205049_1_.getX() >= -30000000 && p_205049_1_.getZ() >= -30000000 && p_205049_1_.getX() < 30000000 && p_205049_1_.getZ() < 30000000 ? this.func_226659_b_(p_205049_1_, p_205049_2_) : 15;
   }

   /** @deprecated */
   @Deprecated
   default boolean isBlockLoaded(BlockPos p_175667_1_) {
      return this.chunkExists(p_175667_1_.getX() >> 4, p_175667_1_.getZ() >> 4);
   }

   default boolean isAreaLoaded(BlockPos p_isAreaLoaded_1_, int p_isAreaLoaded_2_) {
      return this.isAreaLoaded(p_isAreaLoaded_1_.add(-p_isAreaLoaded_2_, -p_isAreaLoaded_2_, -p_isAreaLoaded_2_), p_isAreaLoaded_1_.add(p_isAreaLoaded_2_, p_isAreaLoaded_2_, p_isAreaLoaded_2_));
   }

   /** @deprecated */
   @Deprecated
   default boolean isAreaLoaded(BlockPos p_175707_1_, BlockPos p_175707_2_) {
      return this.isAreaLoaded(p_175707_1_.getX(), p_175707_1_.getY(), p_175707_1_.getZ(), p_175707_2_.getX(), p_175707_2_.getY(), p_175707_2_.getZ());
   }

   /** @deprecated */
   @Deprecated
   default boolean isAreaLoaded(int p_217344_1_, int p_217344_2_, int p_217344_3_, int p_217344_4_, int p_217344_5_, int p_217344_6_) {
      if (p_217344_5_ >= 0 && p_217344_2_ < 256) {
         p_217344_1_ >>= 4;
         p_217344_3_ >>= 4;
         p_217344_4_ >>= 4;
         p_217344_6_ >>= 4;

         for(int i = p_217344_1_; i <= p_217344_4_; ++i) {
            for(int j = p_217344_3_; j <= p_217344_6_; ++j) {
               if (!this.chunkExists(i, j)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
