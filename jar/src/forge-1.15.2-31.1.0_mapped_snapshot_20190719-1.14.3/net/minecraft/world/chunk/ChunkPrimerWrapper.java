package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.ITickList;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.lighting.WorldLightManager;

public class ChunkPrimerWrapper extends ChunkPrimer {
   private final Chunk chunk;

   public ChunkPrimerWrapper(Chunk p_i49948_1_) {
      super(p_i49948_1_.getPos(), UpgradeData.EMPTY);
      this.chunk = p_i49948_1_;
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos p_175625_1_) {
      return this.chunk.getTileEntity(p_175625_1_);
   }

   @Nullable
   public BlockState getBlockState(BlockPos p_180495_1_) {
      return this.chunk.getBlockState(p_180495_1_);
   }

   public IFluidState getFluidState(BlockPos p_204610_1_) {
      return this.chunk.getFluidState(p_204610_1_);
   }

   public int getMaxLightLevel() {
      return this.chunk.getMaxLightLevel();
   }

   @Nullable
   public BlockState setBlockState(BlockPos p_177436_1_, BlockState p_177436_2_, boolean p_177436_3_) {
      return null;
   }

   public void addTileEntity(BlockPos p_177426_1_, TileEntity p_177426_2_) {
   }

   public void addEntity(Entity p_76612_1_) {
   }

   public void setStatus(ChunkStatus p_201574_1_) {
   }

   public ChunkSection[] getSections() {
      return this.chunk.getSections();
   }

   @Nullable
   public WorldLightManager getWorldLightManager() {
      return this.chunk.getWorldLightManager();
   }

   public void setHeightmap(Heightmap.Type p_201607_1_, long[] p_201607_2_) {
   }

   private Heightmap.Type func_209532_c(Heightmap.Type p_209532_1_) {
      if (p_209532_1_ == Heightmap.Type.WORLD_SURFACE_WG) {
         return Heightmap.Type.WORLD_SURFACE;
      } else {
         return p_209532_1_ == Heightmap.Type.OCEAN_FLOOR_WG ? Heightmap.Type.OCEAN_FLOOR : p_209532_1_;
      }
   }

   public int getTopBlockY(Heightmap.Type p_201576_1_, int p_201576_2_, int p_201576_3_) {
      return this.chunk.getTopBlockY(this.func_209532_c(p_201576_1_), p_201576_2_, p_201576_3_);
   }

   public ChunkPos getPos() {
      return this.chunk.getPos();
   }

   public void setLastSaveTime(long p_177432_1_) {
   }

   @Nullable
   public StructureStart getStructureStart(String p_201585_1_) {
      return this.chunk.getStructureStart(p_201585_1_);
   }

   public void putStructureStart(String p_201584_1_, StructureStart p_201584_2_) {
   }

   public Map<String, StructureStart> getStructureStarts() {
      return this.chunk.getStructureStarts();
   }

   public void setStructureStarts(Map<String, StructureStart> p_201612_1_) {
   }

   public LongSet getStructureReferences(String p_201578_1_) {
      return this.chunk.getStructureReferences(p_201578_1_);
   }

   public void addStructureReference(String p_201583_1_, long p_201583_2_) {
   }

   public Map<String, LongSet> getStructureReferences() {
      return this.chunk.getStructureReferences();
   }

   public void setStructureReferences(Map<String, LongSet> p_201606_1_) {
   }

   public BiomeContainer func_225549_i_() {
      return this.chunk.func_225549_i_();
   }

   public void setModified(boolean p_177427_1_) {
   }

   public boolean isModified() {
      return false;
   }

   public ChunkStatus getStatus() {
      return this.chunk.getStatus();
   }

   public void removeTileEntity(BlockPos p_177425_1_) {
   }

   public void markBlockForPostprocessing(BlockPos p_201594_1_) {
   }

   public void addTileEntity(CompoundNBT p_201591_1_) {
   }

   @Nullable
   public CompoundNBT getDeferredTileEntity(BlockPos p_201579_1_) {
      return this.chunk.getDeferredTileEntity(p_201579_1_);
   }

   @Nullable
   public CompoundNBT func_223134_j(BlockPos p_223134_1_) {
      return this.chunk.func_223134_j(p_223134_1_);
   }

   public void func_225548_a_(BiomeContainer p_225548_1_) {
   }

   public Stream<BlockPos> func_217304_m() {
      return this.chunk.func_217304_m();
   }

   public ChunkPrimerTickList<Block> getBlocksToBeTicked() {
      return new ChunkPrimerTickList((p_209219_0_) -> {
         return p_209219_0_.getDefaultState().isAir();
      }, this.getPos());
   }

   public ChunkPrimerTickList<Fluid> getFluidsToBeTicked() {
      return new ChunkPrimerTickList((p_209218_0_) -> {
         return p_209218_0_ == Fluids.EMPTY;
      }, this.getPos());
   }

   public BitSet getCarvingMask(GenerationStage.Carving p_205749_1_) {
      return this.chunk.getCarvingMask(p_205749_1_);
   }

   public Chunk func_217336_u() {
      return this.chunk;
   }

   public boolean hasLight() {
      return this.chunk.hasLight();
   }

   public void setLight(boolean p_217305_1_) {
      this.chunk.setLight(p_217305_1_);
   }

   // $FF: synthetic method
   public ITickList getFluidsToBeTicked() {
      return this.getFluidsToBeTicked();
   }

   // $FF: synthetic method
   public ITickList getBlocksToBeTicked() {
      return this.getBlocksToBeTicked();
   }
}
