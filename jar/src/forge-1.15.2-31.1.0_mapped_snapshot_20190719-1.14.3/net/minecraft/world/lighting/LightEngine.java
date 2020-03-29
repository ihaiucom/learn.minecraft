package net.minecraft.world.lighting;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableInt;

public abstract class LightEngine<M extends LightDataMap<M>, S extends SectionLightStorage<M>> extends LevelBasedGraph implements IWorldLightListener {
   private static final Direction[] DIRECTIONS = Direction.values();
   protected final IChunkLightProvider chunkProvider;
   protected final LightType type;
   protected final S storage;
   private boolean field_215629_e;
   protected final BlockPos.Mutable scratchPos = new BlockPos.Mutable();
   private final long[] recentPositions = new long[2];
   private final IBlockReader[] recentChunks = new IBlockReader[2];

   public LightEngine(IChunkLightProvider p_i51296_1_, LightType p_i51296_2_, S p_i51296_3_) {
      super(16, 256, 8192);
      this.chunkProvider = p_i51296_1_;
      this.type = p_i51296_2_;
      this.storage = p_i51296_3_;
      this.invalidateCaches();
   }

   protected void scheduleUpdate(long p_215473_1_) {
      this.storage.processAllLevelUpdates();
      if (this.storage.hasSection(SectionPos.worldToSection(p_215473_1_))) {
         super.scheduleUpdate(p_215473_1_);
      }

   }

   @Nullable
   private IBlockReader getChunkReader(int p_215615_1_, int p_215615_2_) {
      long i = ChunkPos.asLong(p_215615_1_, p_215615_2_);

      for(int j = 0; j < 2; ++j) {
         if (i == this.recentPositions[j]) {
            return this.recentChunks[j];
         }
      }

      IBlockReader iblockreader = this.chunkProvider.getChunkForLight(p_215615_1_, p_215615_2_);

      for(int k = 1; k > 0; --k) {
         this.recentPositions[k] = this.recentPositions[k - 1];
         this.recentChunks[k] = this.recentChunks[k - 1];
      }

      this.recentPositions[0] = i;
      this.recentChunks[0] = iblockreader;
      return iblockreader;
   }

   private void invalidateCaches() {
      Arrays.fill(this.recentPositions, ChunkPos.SENTINEL);
      Arrays.fill(this.recentChunks, (Object)null);
   }

   protected BlockState func_227468_a_(long p_227468_1_, @Nullable MutableInt p_227468_3_) {
      if (p_227468_1_ == Long.MAX_VALUE) {
         if (p_227468_3_ != null) {
            p_227468_3_.setValue(0);
         }

         return Blocks.AIR.getDefaultState();
      } else {
         int i = SectionPos.toChunk(BlockPos.unpackX(p_227468_1_));
         int j = SectionPos.toChunk(BlockPos.unpackZ(p_227468_1_));
         IBlockReader iblockreader = this.getChunkReader(i, j);
         if (iblockreader == null) {
            if (p_227468_3_ != null) {
               p_227468_3_.setValue(16);
            }

            return Blocks.BEDROCK.getDefaultState();
         } else {
            this.scratchPos.setPos(p_227468_1_);
            BlockState blockstate = iblockreader.getBlockState(this.scratchPos);
            boolean flag = blockstate.isSolid() && blockstate.func_215691_g();
            if (p_227468_3_ != null) {
               p_227468_3_.setValue(blockstate.getOpacity(this.chunkProvider.getWorld(), this.scratchPos));
            }

            return flag ? blockstate : Blocks.AIR.getDefaultState();
         }
      }
   }

   protected VoxelShape getVoxelShape(BlockState p_223405_1_, long p_223405_2_, Direction p_223405_4_) {
      return p_223405_1_.isSolid() ? p_223405_1_.func_215702_a(this.chunkProvider.getWorld(), this.scratchPos.setPos(p_223405_2_), p_223405_4_) : VoxelShapes.empty();
   }

   public static int func_215613_a(IBlockReader p_215613_0_, BlockState p_215613_1_, BlockPos p_215613_2_, BlockState p_215613_3_, BlockPos p_215613_4_, Direction p_215613_5_, int p_215613_6_) {
      boolean flag = p_215613_1_.isSolid() && p_215613_1_.func_215691_g();
      boolean flag1 = p_215613_3_.isSolid() && p_215613_3_.func_215691_g();
      if (!flag && !flag1) {
         return p_215613_6_;
      } else {
         VoxelShape voxelshape = flag ? p_215613_1_.getRenderShape(p_215613_0_, p_215613_2_) : VoxelShapes.empty();
         VoxelShape voxelshape1 = flag1 ? p_215613_3_.getRenderShape(p_215613_0_, p_215613_4_) : VoxelShapes.empty();
         return VoxelShapes.doAdjacentCubeSidesFillSquare(voxelshape, voxelshape1, p_215613_5_) ? 16 : p_215613_6_;
      }
   }

   protected boolean isRoot(long p_215485_1_) {
      return p_215485_1_ == Long.MAX_VALUE;
   }

   protected int computeLevel(long p_215477_1_, long p_215477_3_, int p_215477_5_) {
      return 0;
   }

   protected int getLevel(long p_215471_1_) {
      return p_215471_1_ == Long.MAX_VALUE ? 0 : 15 - this.storage.getLight(p_215471_1_);
   }

   protected int getLevelFromArray(NibbleArray p_215622_1_, long p_215622_2_) {
      return 15 - p_215622_1_.get(SectionPos.mask(BlockPos.unpackX(p_215622_2_)), SectionPos.mask(BlockPos.unpackY(p_215622_2_)), SectionPos.mask(BlockPos.unpackZ(p_215622_2_)));
   }

   protected void setLevel(long p_215476_1_, int p_215476_3_) {
      this.storage.setLight(p_215476_1_, Math.min(15, 15 - p_215476_3_));
   }

   protected int getEdgeLevel(long p_215480_1_, long p_215480_3_, int p_215480_5_) {
      return 0;
   }

   public boolean func_215619_a() {
      return this.needsUpdate() || this.storage.needsUpdate() || this.storage.hasSectionsToUpdate();
   }

   public int tick(int p_215616_1_, boolean p_215616_2_, boolean p_215616_3_) {
      if (!this.field_215629_e) {
         if (this.storage.needsUpdate()) {
            p_215616_1_ = this.storage.processUpdates(p_215616_1_);
            if (p_215616_1_ == 0) {
               return p_215616_1_;
            }
         }

         this.storage.updateSections(this, p_215616_2_, p_215616_3_);
      }

      this.field_215629_e = true;
      if (this.needsUpdate()) {
         p_215616_1_ = this.processUpdates(p_215616_1_);
         this.invalidateCaches();
         if (p_215616_1_ == 0) {
            return p_215616_1_;
         }
      }

      this.field_215629_e = false;
      this.storage.updateAndNotify();
      return p_215616_1_;
   }

   protected void setData(long p_215621_1_, @Nullable NibbleArray p_215621_3_) {
      this.storage.setData(p_215621_1_, p_215621_3_);
   }

   @Nullable
   public NibbleArray getData(SectionPos p_215612_1_) {
      return this.storage.getArray(p_215612_1_.asLong());
   }

   public int getLightFor(BlockPos p_215611_1_) {
      return this.storage.getLightOrDefault(p_215611_1_.toLong());
   }

   @OnlyIn(Dist.CLIENT)
   public String getDebugString(long p_215614_1_) {
      return "" + this.storage.getLevel(p_215614_1_);
   }

   public void checkLight(BlockPos p_215617_1_) {
      long i = p_215617_1_.toLong();
      this.scheduleUpdate(i);
      Direction[] var4 = DIRECTIONS;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction direction = var4[var6];
         this.scheduleUpdate(BlockPos.offset(i, direction));
      }

   }

   public void func_215623_a(BlockPos p_215623_1_, int p_215623_2_) {
   }

   public void updateSectionStatus(SectionPos p_215566_1_, boolean p_215566_2_) {
      this.storage.updateSectionStatus(p_215566_1_.asLong(), p_215566_2_);
   }

   public void func_215620_a(ChunkPos p_215620_1_, boolean p_215620_2_) {
      long i = SectionPos.toSectionColumnPos(SectionPos.asLong(p_215620_1_.x, 0, p_215620_1_.z));
      this.storage.func_215526_b(i, p_215620_2_);
   }

   public void retainChunkData(ChunkPos p_223129_1_, boolean p_223129_2_) {
      long i = SectionPos.toSectionColumnPos(SectionPos.asLong(p_223129_1_.x, 0, p_223129_1_.z));
      this.storage.retainChunkData(i, p_223129_2_);
   }

   public abstract int queuedUpdateSize();
}
