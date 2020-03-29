package net.minecraft.client.renderer;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ViewFrustum {
   protected final WorldRenderer renderGlobal;
   protected final World world;
   protected int countChunksY;
   protected int countChunksX;
   protected int countChunksZ;
   public ChunkRenderDispatcher.ChunkRender[] renderChunks;

   public ViewFrustum(ChunkRenderDispatcher p_i226000_1_, World p_i226000_2_, int p_i226000_3_, WorldRenderer p_i226000_4_) {
      this.renderGlobal = p_i226000_4_;
      this.world = p_i226000_2_;
      this.setCountChunksXYZ(p_i226000_3_);
      this.func_228789_a_(p_i226000_1_);
   }

   protected void func_228789_a_(ChunkRenderDispatcher p_228789_1_) {
      int i = this.countChunksX * this.countChunksY * this.countChunksZ;
      this.renderChunks = new ChunkRenderDispatcher.ChunkRender[i];

      for(int j = 0; j < this.countChunksX; ++j) {
         for(int k = 0; k < this.countChunksY; ++k) {
            for(int l = 0; l < this.countChunksZ; ++l) {
               int i1 = this.getIndex(j, k, l);
               this.renderChunks[i1] = p_228789_1_.new ChunkRender();
               this.renderChunks[i1].setPosition(j * 16, k * 16, l * 16);
            }
         }
      }

   }

   public void deleteGlResources() {
      ChunkRenderDispatcher.ChunkRender[] var1 = this.renderChunks;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = var1[var3];
         chunkrenderdispatcher$chunkrender.deleteGlResources();
      }

   }

   private int getIndex(int p_212478_1_, int p_212478_2_, int p_212478_3_) {
      return (p_212478_3_ * this.countChunksY + p_212478_2_) * this.countChunksX + p_212478_1_;
   }

   protected void setCountChunksXYZ(int p_178159_1_) {
      int i = p_178159_1_ * 2 + 1;
      this.countChunksX = i;
      this.countChunksY = 16;
      this.countChunksZ = i;
   }

   public void updateChunkPositions(double p_178163_1_, double p_178163_3_) {
      int i = MathHelper.floor(p_178163_1_);
      int j = MathHelper.floor(p_178163_3_);

      for(int k = 0; k < this.countChunksX; ++k) {
         int l = this.countChunksX * 16;
         int i1 = i - 8 - l / 2;
         int j1 = i1 + Math.floorMod(k * 16 - i1, l);

         for(int k1 = 0; k1 < this.countChunksZ; ++k1) {
            int l1 = this.countChunksZ * 16;
            int i2 = j - 8 - l1 / 2;
            int j2 = i2 + Math.floorMod(k1 * 16 - i2, l1);

            for(int k2 = 0; k2 < this.countChunksY; ++k2) {
               int l2 = k2 * 16;
               ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = this.renderChunks[this.getIndex(k, k2, k1)];
               chunkrenderdispatcher$chunkrender.setPosition(j1, l2, j2);
            }
         }
      }

   }

   public void markForRerender(int p_217628_1_, int p_217628_2_, int p_217628_3_, boolean p_217628_4_) {
      int i = Math.floorMod(p_217628_1_, this.countChunksX);
      int j = Math.floorMod(p_217628_2_, this.countChunksY);
      int k = Math.floorMod(p_217628_3_, this.countChunksZ);
      ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = this.renderChunks[this.getIndex(i, j, k)];
      chunkrenderdispatcher$chunkrender.setNeedsUpdate(p_217628_4_);
   }

   @Nullable
   protected ChunkRenderDispatcher.ChunkRender getRenderChunk(BlockPos p_178161_1_) {
      int i = MathHelper.intFloorDiv(p_178161_1_.getX(), 16);
      int j = MathHelper.intFloorDiv(p_178161_1_.getY(), 16);
      int k = MathHelper.intFloorDiv(p_178161_1_.getZ(), 16);
      if (j >= 0 && j < this.countChunksY) {
         i = MathHelper.normalizeAngle(i, this.countChunksX);
         k = MathHelper.normalizeAngle(k, this.countChunksZ);
         return this.renderChunks[this.getIndex(i, j, k)];
      } else {
         return null;
      }
   }
}
