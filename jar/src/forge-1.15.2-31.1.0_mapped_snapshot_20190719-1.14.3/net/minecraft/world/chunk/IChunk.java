package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IStructureReader;
import net.minecraft.world.ITickList;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import org.apache.logging.log4j.LogManager;

public interface IChunk extends IBlockReader, IStructureReader {
   @Nullable
   BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3);

   void addTileEntity(BlockPos var1, TileEntity var2);

   void addEntity(Entity var1);

   @Nullable
   default ChunkSection getLastExtendedBlockStorage() {
      ChunkSection[] achunksection = this.getSections();

      for(int i = achunksection.length - 1; i >= 0; --i) {
         ChunkSection chunksection = achunksection[i];
         if (!ChunkSection.isEmpty(chunksection)) {
            return chunksection;
         }
      }

      return null;
   }

   default int getTopFilledSegment() {
      ChunkSection chunksection = this.getLastExtendedBlockStorage();
      return chunksection == null ? 0 : chunksection.getYLocation();
   }

   Set<BlockPos> getTileEntitiesPos();

   ChunkSection[] getSections();

   Collection<Entry<Heightmap.Type, Heightmap>> func_217311_f();

   void setHeightmap(Heightmap.Type var1, long[] var2);

   Heightmap func_217303_b(Heightmap.Type var1);

   int getTopBlockY(Heightmap.Type var1, int var2, int var3);

   ChunkPos getPos();

   void setLastSaveTime(long var1);

   Map<String, StructureStart> getStructureStarts();

   void setStructureStarts(Map<String, StructureStart> var1);

   default boolean isEmptyBetween(int p_76606_1_, int p_76606_2_) {
      if (p_76606_1_ < 0) {
         p_76606_1_ = 0;
      }

      if (p_76606_2_ >= 256) {
         p_76606_2_ = 255;
      }

      for(int i = p_76606_1_; i <= p_76606_2_; i += 16) {
         if (!ChunkSection.isEmpty(this.getSections()[i >> 4])) {
            return false;
         }
      }

      return true;
   }

   @Nullable
   BiomeContainer func_225549_i_();

   void setModified(boolean var1);

   boolean isModified();

   ChunkStatus getStatus();

   void removeTileEntity(BlockPos var1);

   default void markBlockForPostprocessing(BlockPos p_201594_1_) {
      LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", p_201594_1_);
   }

   ShortList[] getPackedPositions();

   default void func_201636_b(short p_201636_1_, int p_201636_2_) {
      getList(this.getPackedPositions(), p_201636_2_).add(p_201636_1_);
   }

   default void addTileEntity(CompoundNBT p_201591_1_) {
      LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
   }

   @Nullable
   CompoundNBT getDeferredTileEntity(BlockPos var1);

   @Nullable
   CompoundNBT func_223134_j(BlockPos var1);

   Stream<BlockPos> func_217304_m();

   ITickList<Block> getBlocksToBeTicked();

   ITickList<Fluid> getFluidsToBeTicked();

   default BitSet getCarvingMask(GenerationStage.Carving p_205749_1_) {
      throw (RuntimeException)Util.func_229757_c_(new RuntimeException("Meaningless in this context"));
   }

   UpgradeData getUpgradeData();

   void setInhabitedTime(long var1);

   long getInhabitedTime();

   static ShortList getList(ShortList[] p_217308_0_, int p_217308_1_) {
      if (p_217308_0_[p_217308_1_] == null) {
         p_217308_0_[p_217308_1_] = new ShortArrayList();
      }

      return p_217308_0_[p_217308_1_];
   }

   boolean hasLight();

   void setLight(boolean var1);

   @Nullable
   default IWorld getWorldForge() {
      return null;
   }
}
