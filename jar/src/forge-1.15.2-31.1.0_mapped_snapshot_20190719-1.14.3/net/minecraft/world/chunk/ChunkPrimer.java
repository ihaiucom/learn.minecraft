package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.lighting.WorldLightManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkPrimer implements IChunk {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ChunkPos pos;
   private volatile boolean modified;
   @Nullable
   private BiomeContainer biomes;
   @Nullable
   private volatile WorldLightManager field_217334_e;
   private final Map<Heightmap.Type, Heightmap> heightmaps;
   private volatile ChunkStatus status;
   private final Map<BlockPos, TileEntity> tileEntities;
   private final Map<BlockPos, CompoundNBT> deferredTileEntities;
   private final ChunkSection[] sections;
   private final List<CompoundNBT> entities;
   private final List<BlockPos> lightPositions;
   private final ShortList[] packedPositions;
   private final Map<String, StructureStart> structureStartMap;
   private final Map<String, LongSet> structureReferenceMap;
   private final UpgradeData upgradeData;
   private final ChunkPrimerTickList<Block> pendingBlockTicks;
   private final ChunkPrimerTickList<Fluid> pendingFluidTicks;
   private long inhabitedTime;
   private final Map<GenerationStage.Carving, BitSet> carvingMasks;
   private volatile boolean hasLight;

   public ChunkPrimer(ChunkPos p_i48700_1_, UpgradeData p_i48700_2_) {
      this(p_i48700_1_, p_i48700_2_, (ChunkSection[])null, new ChunkPrimerTickList((p_lambda$new$0_0_) -> {
         return p_lambda$new$0_0_ == null || p_lambda$new$0_0_.getDefaultState().isAir();
      }, p_i48700_1_), new ChunkPrimerTickList((p_lambda$new$1_0_) -> {
         return p_lambda$new$1_0_ == null || p_lambda$new$1_0_ == Fluids.EMPTY;
      }, p_i48700_1_));
   }

   public ChunkPrimer(ChunkPos p_i49941_1_, UpgradeData p_i49941_2_, @Nullable ChunkSection[] p_i49941_3_, ChunkPrimerTickList<Block> p_i49941_4_, ChunkPrimerTickList<Fluid> p_i49941_5_) {
      this.heightmaps = Maps.newEnumMap(Heightmap.Type.class);
      this.status = ChunkStatus.EMPTY;
      this.tileEntities = Maps.newHashMap();
      this.deferredTileEntities = Maps.newHashMap();
      this.sections = new ChunkSection[16];
      this.entities = Lists.newArrayList();
      this.lightPositions = Lists.newArrayList();
      this.packedPositions = new ShortList[16];
      this.structureStartMap = Maps.newHashMap();
      this.structureReferenceMap = Maps.newHashMap();
      this.carvingMasks = Maps.newHashMap();
      this.pos = p_i49941_1_;
      this.upgradeData = p_i49941_2_;
      this.pendingBlockTicks = p_i49941_4_;
      this.pendingFluidTicks = p_i49941_5_;
      if (p_i49941_3_ != null) {
         if (this.sections.length == p_i49941_3_.length) {
            System.arraycopy(p_i49941_3_, 0, this.sections, 0, this.sections.length);
         } else {
            LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", p_i49941_3_.length, this.sections.length);
         }
      }

   }

   public BlockState getBlockState(BlockPos p_180495_1_) {
      int i = p_180495_1_.getY();
      if (World.isYOutOfBounds(i)) {
         return Blocks.VOID_AIR.getDefaultState();
      } else {
         ChunkSection chunksection = this.getSections()[i >> 4];
         return ChunkSection.isEmpty(chunksection) ? Blocks.AIR.getDefaultState() : chunksection.getBlockState(p_180495_1_.getX() & 15, i & 15, p_180495_1_.getZ() & 15);
      }
   }

   public IFluidState getFluidState(BlockPos p_204610_1_) {
      int i = p_204610_1_.getY();
      if (World.isYOutOfBounds(i)) {
         return Fluids.EMPTY.getDefaultState();
      } else {
         ChunkSection chunksection = this.getSections()[i >> 4];
         return ChunkSection.isEmpty(chunksection) ? Fluids.EMPTY.getDefaultState() : chunksection.getFluidState(p_204610_1_.getX() & 15, i & 15, p_204610_1_.getZ() & 15);
      }
   }

   public Stream<BlockPos> func_217304_m() {
      return this.lightPositions.stream();
   }

   public ShortList[] getPackedLightPositions() {
      ShortList[] ashortlist = new ShortList[16];
      Iterator var2 = this.lightPositions.iterator();

      while(var2.hasNext()) {
         BlockPos blockpos = (BlockPos)var2.next();
         IChunk.getList(ashortlist, blockpos.getY() >> 4).add(packToLocal(blockpos));
      }

      return ashortlist;
   }

   public void addLightValue(short p_201646_1_, int p_201646_2_) {
      this.addLightPosition(unpackToWorld(p_201646_1_, p_201646_2_, this.pos));
   }

   public void addLightPosition(BlockPos p_201637_1_) {
      this.lightPositions.add(p_201637_1_.toImmutable());
   }

   @Nullable
   public BlockState setBlockState(BlockPos p_177436_1_, BlockState p_177436_2_, boolean p_177436_3_) {
      int i = p_177436_1_.getX();
      int j = p_177436_1_.getY();
      int k = p_177436_1_.getZ();
      if (j >= 0 && j < 256) {
         if (this.sections[j >> 4] == Chunk.EMPTY_SECTION && p_177436_2_.getBlock() == Blocks.AIR) {
            return p_177436_2_;
         } else {
            if (p_177436_2_.getLightValue(this, p_177436_1_) > 0) {
               this.lightPositions.add(new BlockPos((i & 15) + this.getPos().getXStart(), j, (k & 15) + this.getPos().getZStart()));
            }

            ChunkSection chunksection = this.func_217332_a(j >> 4);
            BlockState blockstate = chunksection.setBlockState(i & 15, j & 15, k & 15, p_177436_2_);
            if (this.status.isAtLeast(ChunkStatus.FEATURES) && p_177436_2_ != blockstate && (p_177436_2_.getOpacity(this, p_177436_1_) != blockstate.getOpacity(this, p_177436_1_) || p_177436_2_.getLightValue(this, p_177436_1_) != blockstate.getLightValue(this, p_177436_1_) || p_177436_2_.func_215691_g() || blockstate.func_215691_g())) {
               WorldLightManager worldlightmanager = this.getWorldLightManager();
               worldlightmanager.checkBlock(p_177436_1_);
            }

            EnumSet<Heightmap.Type> enumset1 = this.getStatus().getHeightMaps();
            EnumSet<Heightmap.Type> enumset = null;
            Iterator var11 = enumset1.iterator();

            Heightmap.Type heightmap$type1;
            while(var11.hasNext()) {
               heightmap$type1 = (Heightmap.Type)var11.next();
               Heightmap heightmap = (Heightmap)this.heightmaps.get(heightmap$type1);
               if (heightmap == null) {
                  if (enumset == null) {
                     enumset = EnumSet.noneOf(Heightmap.Type.class);
                  }

                  enumset.add(heightmap$type1);
               }
            }

            if (enumset != null) {
               Heightmap.func_222690_a(this, enumset);
            }

            var11 = enumset1.iterator();

            while(var11.hasNext()) {
               heightmap$type1 = (Heightmap.Type)var11.next();
               ((Heightmap)this.heightmaps.get(heightmap$type1)).update(i & 15, j, k & 15, p_177436_2_);
            }

            return blockstate;
         }
      } else {
         return Blocks.VOID_AIR.getDefaultState();
      }
   }

   public ChunkSection func_217332_a(int p_217332_1_) {
      if (this.sections[p_217332_1_] == Chunk.EMPTY_SECTION) {
         this.sections[p_217332_1_] = new ChunkSection(p_217332_1_ << 4);
      }

      return this.sections[p_217332_1_];
   }

   public void addTileEntity(BlockPos p_177426_1_, TileEntity p_177426_2_) {
      p_177426_2_.setPos(p_177426_1_);
      this.tileEntities.put(p_177426_1_, p_177426_2_);
   }

   public Set<BlockPos> getTileEntitiesPos() {
      Set<BlockPos> set = Sets.newHashSet(this.deferredTileEntities.keySet());
      set.addAll(this.tileEntities.keySet());
      return set;
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos p_175625_1_) {
      return (TileEntity)this.tileEntities.get(p_175625_1_);
   }

   public Map<BlockPos, TileEntity> getTileEntities() {
      return this.tileEntities;
   }

   public void addEntity(CompoundNBT p_201626_1_) {
      this.entities.add(p_201626_1_);
   }

   public void addEntity(Entity p_76612_1_) {
      CompoundNBT compoundnbt = new CompoundNBT();
      p_76612_1_.writeUnlessPassenger(compoundnbt);
      this.addEntity(compoundnbt);
   }

   public List<CompoundNBT> getEntities() {
      return this.entities;
   }

   public void func_225548_a_(BiomeContainer p_225548_1_) {
      this.biomes = p_225548_1_;
   }

   @Nullable
   public BiomeContainer func_225549_i_() {
      return this.biomes;
   }

   public void setModified(boolean p_177427_1_) {
      this.modified = p_177427_1_;
   }

   public boolean isModified() {
      return this.modified;
   }

   public ChunkStatus getStatus() {
      return this.status;
   }

   public void setStatus(ChunkStatus p_201574_1_) {
      this.status = p_201574_1_;
      this.setModified(true);
   }

   public ChunkSection[] getSections() {
      return this.sections;
   }

   @Nullable
   public WorldLightManager getWorldLightManager() {
      return this.field_217334_e;
   }

   public Collection<Entry<Heightmap.Type, Heightmap>> func_217311_f() {
      return Collections.unmodifiableSet(this.heightmaps.entrySet());
   }

   public void setHeightmap(Heightmap.Type p_201607_1_, long[] p_201607_2_) {
      this.func_217303_b(p_201607_1_).setDataArray(p_201607_2_);
   }

   public Heightmap func_217303_b(Heightmap.Type p_217303_1_) {
      return (Heightmap)this.heightmaps.computeIfAbsent(p_217303_1_, (p_lambda$func_217303_b$2_1_) -> {
         return new Heightmap(this, p_lambda$func_217303_b$2_1_);
      });
   }

   public int getTopBlockY(Heightmap.Type p_201576_1_, int p_201576_2_, int p_201576_3_) {
      Heightmap heightmap = (Heightmap)this.heightmaps.get(p_201576_1_);
      if (heightmap == null) {
         Heightmap.func_222690_a(this, EnumSet.of(p_201576_1_));
         heightmap = (Heightmap)this.heightmaps.get(p_201576_1_);
      }

      return heightmap.getHeight(p_201576_2_ & 15, p_201576_3_ & 15) - 1;
   }

   public ChunkPos getPos() {
      return this.pos;
   }

   public void setLastSaveTime(long p_177432_1_) {
   }

   @Nullable
   public StructureStart getStructureStart(String p_201585_1_) {
      return (StructureStart)this.structureStartMap.get(p_201585_1_);
   }

   public void putStructureStart(String p_201584_1_, StructureStart p_201584_2_) {
      this.structureStartMap.put(p_201584_1_, p_201584_2_);
      this.modified = true;
   }

   public Map<String, StructureStart> getStructureStarts() {
      return Collections.unmodifiableMap(this.structureStartMap);
   }

   public void setStructureStarts(Map<String, StructureStart> p_201612_1_) {
      this.structureStartMap.clear();
      this.structureStartMap.putAll(p_201612_1_);
      this.modified = true;
   }

   public LongSet getStructureReferences(String p_201578_1_) {
      return (LongSet)this.structureReferenceMap.computeIfAbsent(p_201578_1_, (p_lambda$getStructureReferences$3_0_) -> {
         return new LongOpenHashSet();
      });
   }

   public void addStructureReference(String p_201583_1_, long p_201583_2_) {
      ((LongSet)this.structureReferenceMap.computeIfAbsent(p_201583_1_, (p_lambda$addStructureReference$4_0_) -> {
         return new LongOpenHashSet();
      })).add(p_201583_2_);
      this.modified = true;
   }

   public Map<String, LongSet> getStructureReferences() {
      return Collections.unmodifiableMap(this.structureReferenceMap);
   }

   public void setStructureReferences(Map<String, LongSet> p_201606_1_) {
      this.structureReferenceMap.clear();
      this.structureReferenceMap.putAll(p_201606_1_);
      this.modified = true;
   }

   public static short packToLocal(BlockPos p_201651_0_) {
      int i = p_201651_0_.getX();
      int j = p_201651_0_.getY();
      int k = p_201651_0_.getZ();
      int l = i & 15;
      int i1 = j & 15;
      int j1 = k & 15;
      return (short)(l | i1 << 4 | j1 << 8);
   }

   public static BlockPos unpackToWorld(short p_201635_0_, int p_201635_1_, ChunkPos p_201635_2_) {
      int i = (p_201635_0_ & 15) + (p_201635_2_.x << 4);
      int j = (p_201635_0_ >>> 4 & 15) + (p_201635_1_ << 4);
      int k = (p_201635_0_ >>> 8 & 15) + (p_201635_2_.z << 4);
      return new BlockPos(i, j, k);
   }

   public void markBlockForPostprocessing(BlockPos p_201594_1_) {
      if (!World.isOutsideBuildHeight(p_201594_1_)) {
         IChunk.getList(this.packedPositions, p_201594_1_.getY() >> 4).add(packToLocal(p_201594_1_));
      }

   }

   public ShortList[] getPackedPositions() {
      return this.packedPositions;
   }

   public void func_201636_b(short p_201636_1_, int p_201636_2_) {
      IChunk.getList(this.packedPositions, p_201636_2_).add(p_201636_1_);
   }

   public ChunkPrimerTickList<Block> getBlocksToBeTicked() {
      return this.pendingBlockTicks;
   }

   public ChunkPrimerTickList<Fluid> getFluidsToBeTicked() {
      return this.pendingFluidTicks;
   }

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public void setInhabitedTime(long p_177415_1_) {
      this.inhabitedTime = p_177415_1_;
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void addTileEntity(CompoundNBT p_201591_1_) {
      this.deferredTileEntities.put(new BlockPos(p_201591_1_.getInt("x"), p_201591_1_.getInt("y"), p_201591_1_.getInt("z")), p_201591_1_);
   }

   public Map<BlockPos, CompoundNBT> getDeferredTileEntities() {
      return Collections.unmodifiableMap(this.deferredTileEntities);
   }

   public CompoundNBT getDeferredTileEntity(BlockPos p_201579_1_) {
      return (CompoundNBT)this.deferredTileEntities.get(p_201579_1_);
   }

   @Nullable
   public CompoundNBT func_223134_j(BlockPos p_223134_1_) {
      TileEntity tileentity = this.getTileEntity(p_223134_1_);
      return tileentity != null ? tileentity.write(new CompoundNBT()) : (CompoundNBT)this.deferredTileEntities.get(p_223134_1_);
   }

   public void removeTileEntity(BlockPos p_177425_1_) {
      this.tileEntities.remove(p_177425_1_);
      this.deferredTileEntities.remove(p_177425_1_);
   }

   public BitSet getCarvingMask(GenerationStage.Carving p_205749_1_) {
      return (BitSet)this.carvingMasks.computeIfAbsent(p_205749_1_, (p_lambda$getCarvingMask$5_0_) -> {
         return new BitSet(65536);
      });
   }

   public void setCarvingMask(GenerationStage.Carving p_205767_1_, BitSet p_205767_2_) {
      this.carvingMasks.put(p_205767_1_, p_205767_2_);
   }

   public void setLightManager(WorldLightManager p_217306_1_) {
      this.field_217334_e = p_217306_1_;
   }

   public boolean hasLight() {
      return this.hasLight;
   }

   public void setLight(boolean p_217305_1_) {
      this.hasLight = p_217305_1_;
      this.setModified(true);
   }
}
