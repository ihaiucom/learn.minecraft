package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ITickList;
import net.minecraft.world.LightType;
import net.minecraft.world.SerializableTickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerTickList;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.Structures;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkSerializer {
   private static final Logger LOGGER = LogManager.getLogger();

   public static ChunkPrimer read(ServerWorld p_222656_0_, TemplateManager p_222656_1_, PointOfInterestManager p_222656_2_, ChunkPos p_222656_3_, CompoundNBT p_222656_4_) {
      ChunkGenerator<?> chunkgenerator = p_222656_0_.getChunkProvider().getChunkGenerator();
      BiomeProvider biomeprovider = chunkgenerator.getBiomeProvider();
      CompoundNBT compoundnbt = p_222656_4_.getCompound("Level");
      ChunkPos chunkpos = new ChunkPos(compoundnbt.getInt("xPos"), compoundnbt.getInt("zPos"));
      if (!Objects.equals(p_222656_3_, chunkpos)) {
         LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", p_222656_3_, p_222656_3_, chunkpos);
      }

      BiomeContainer biomecontainer = new BiomeContainer(p_222656_3_, biomeprovider, compoundnbt.contains("Biomes", 11) ? compoundnbt.getIntArray("Biomes") : null);
      UpgradeData upgradedata = compoundnbt.contains("UpgradeData", 10) ? new UpgradeData(compoundnbt.getCompound("UpgradeData")) : UpgradeData.EMPTY;
      ChunkPrimerTickList<Block> chunkprimerticklist = new ChunkPrimerTickList((p_lambda$read$0_0_) -> {
         return p_lambda$read$0_0_ == null || p_lambda$read$0_0_.getDefaultState().isAir();
      }, p_222656_3_, compoundnbt.getList("ToBeTicked", 9));
      ChunkPrimerTickList<Fluid> chunkprimerticklist1 = new ChunkPrimerTickList((p_lambda$read$1_0_) -> {
         return p_lambda$read$1_0_ == null || p_lambda$read$1_0_ == Fluids.EMPTY;
      }, p_222656_3_, compoundnbt.getList("LiquidsToBeTicked", 9));
      boolean flag = compoundnbt.getBoolean("isLightOn");
      ListNBT listnbt = compoundnbt.getList("Sections", 10);
      int i = true;
      ChunkSection[] achunksection = new ChunkSection[16];
      boolean flag1 = p_222656_0_.getDimension().hasSkyLight();
      AbstractChunkProvider abstractchunkprovider = p_222656_0_.getChunkProvider();
      WorldLightManager worldlightmanager = abstractchunkprovider.getLightManager();
      if (flag) {
         worldlightmanager.retainData(p_222656_3_, true);
      }

      for(int j = 0; j < listnbt.size(); ++j) {
         CompoundNBT compoundnbt1 = listnbt.getCompound(j);
         int k = compoundnbt1.getByte("Y");
         if (compoundnbt1.contains("Palette", 9) && compoundnbt1.contains("BlockStates", 12)) {
            ChunkSection chunksection = new ChunkSection(k << 4);
            chunksection.getData().readChunkPalette(compoundnbt1.getList("Palette", 10), compoundnbt1.getLongArray("BlockStates"));
            chunksection.recalculateRefCounts();
            if (!chunksection.isEmpty()) {
               achunksection[k] = chunksection;
            }

            p_222656_2_.func_219139_a(p_222656_3_, chunksection);
         }

         if (flag) {
            if (compoundnbt1.contains("BlockLight", 7)) {
               worldlightmanager.setData(LightType.BLOCK, SectionPos.from(p_222656_3_, k), new NibbleArray(compoundnbt1.getByteArray("BlockLight")));
            }

            if (flag1 && compoundnbt1.contains("SkyLight", 7)) {
               worldlightmanager.setData(LightType.SKY, SectionPos.from(p_222656_3_, k), new NibbleArray(compoundnbt1.getByteArray("SkyLight")));
            }
         }
      }

      long k1 = compoundnbt.getLong("InhabitedTime");
      ChunkStatus.Type chunkstatus$type = getChunkStatus(p_222656_4_);
      Object ichunk;
      if (chunkstatus$type == ChunkStatus.Type.LEVELCHUNK) {
         ListNBT var10000;
         Function var10001;
         DefaultedRegistry var10002;
         Object iticklist;
         if (compoundnbt.contains("TileTicks", 9)) {
            var10000 = compoundnbt.getList("TileTicks", 10);
            var10001 = Registry.BLOCK::getKey;
            var10002 = Registry.BLOCK;
            var10002.getClass();
            iticklist = SerializableTickList.func_222984_a(var10000, var10001, var10002::getOrDefault);
         } else {
            iticklist = chunkprimerticklist;
         }

         Object iticklist1;
         if (compoundnbt.contains("LiquidTicks", 9)) {
            var10000 = compoundnbt.getList("LiquidTicks", 10);
            var10001 = Registry.FLUID::getKey;
            var10002 = Registry.FLUID;
            var10002.getClass();
            iticklist1 = SerializableTickList.func_222984_a(var10000, var10001, var10002::getOrDefault);
         } else {
            iticklist1 = chunkprimerticklist1;
         }

         ichunk = new Chunk(p_222656_0_.getWorld(), p_222656_3_, biomecontainer, upgradedata, (ITickList)iticklist, (ITickList)iticklist1, k1, achunksection, (p_lambda$read$2_1_) -> {
            readEntities(compoundnbt, p_lambda$read$2_1_);
         });
         if (compoundnbt.contains("ForgeCaps")) {
            ((Chunk)ichunk).readCapsFromNBT(compoundnbt.getCompound("ForgeCaps"));
         }
      } else {
         ChunkPrimer chunkprimer = new ChunkPrimer(p_222656_3_, upgradedata, achunksection, chunkprimerticklist, chunkprimerticklist1);
         chunkprimer.func_225548_a_(biomecontainer);
         ichunk = chunkprimer;
         chunkprimer.setInhabitedTime(k1);
         chunkprimer.setStatus(ChunkStatus.byName(compoundnbt.getString("Status")));
         if (chunkprimer.getStatus().isAtLeast(ChunkStatus.FEATURES)) {
            chunkprimer.setLightManager(worldlightmanager);
         }

         if (!flag && chunkprimer.getStatus().isAtLeast(ChunkStatus.LIGHT)) {
            Iterator var41 = BlockPos.getAllInBoxMutable(p_222656_3_.getXStart(), 0, p_222656_3_.getZStart(), p_222656_3_.getXEnd(), 255, p_222656_3_.getZEnd()).iterator();

            while(var41.hasNext()) {
               BlockPos blockpos = (BlockPos)var41.next();
               if (((IChunk)ichunk).getBlockState(blockpos).getLightValue((IBlockReader)ichunk, blockpos) != 0) {
                  chunkprimer.addLightPosition(blockpos);
               }
            }
         }
      }

      ((IChunk)ichunk).setLight(flag);
      CompoundNBT compoundnbt3 = compoundnbt.getCompound("Heightmaps");
      EnumSet<Heightmap.Type> enumset = EnumSet.noneOf(Heightmap.Type.class);
      Iterator var43 = ((IChunk)ichunk).getStatus().getHeightMaps().iterator();

      while(var43.hasNext()) {
         Heightmap.Type heightmap$type = (Heightmap.Type)var43.next();
         String s = heightmap$type.getId();
         if (compoundnbt3.contains(s, 12)) {
            ((IChunk)ichunk).setHeightmap(heightmap$type, compoundnbt3.getLongArray(s));
         } else {
            enumset.add(heightmap$type);
         }
      }

      Heightmap.func_222690_a((IChunk)ichunk, enumset);
      CompoundNBT compoundnbt4 = compoundnbt.getCompound("Structures");
      ((IChunk)ichunk).setStructureStarts(func_227076_a_(chunkgenerator, p_222656_1_, compoundnbt4));
      ((IChunk)ichunk).setStructureReferences(func_227075_a_(p_222656_3_, compoundnbt4));
      if (compoundnbt.getBoolean("shouldSave")) {
         ((IChunk)ichunk).setModified(true);
      }

      ListNBT listnbt3 = compoundnbt.getList("PostProcessing", 9);

      ListNBT listnbt4;
      int i2;
      for(int l1 = 0; l1 < listnbt3.size(); ++l1) {
         listnbt4 = listnbt3.getList(l1);

         for(i2 = 0; i2 < listnbt4.size(); ++i2) {
            ((IChunk)ichunk).func_201636_b(listnbt4.getShort(i2), l1);
         }
      }

      if (chunkstatus$type == ChunkStatus.Type.LEVELCHUNK) {
         MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Load((IChunk)ichunk, compoundnbt, chunkstatus$type));
         return new ChunkPrimerWrapper((Chunk)ichunk);
      } else {
         ChunkPrimer chunkprimer1 = (ChunkPrimer)ichunk;
         listnbt4 = compoundnbt.getList("Entities", 10);

         for(i2 = 0; i2 < listnbt4.size(); ++i2) {
            chunkprimer1.addEntity(listnbt4.getCompound(i2));
         }

         ListNBT listnbt5 = compoundnbt.getList("TileEntities", 10);

         CompoundNBT compoundnbt5;
         for(int i1 = 0; i1 < listnbt5.size(); ++i1) {
            compoundnbt5 = listnbt5.getCompound(i1);
            ((IChunk)ichunk).addTileEntity(compoundnbt5);
         }

         ListNBT listnbt6 = compoundnbt.getList("Lights", 9);

         for(int j2 = 0; j2 < listnbt6.size(); ++j2) {
            ListNBT listnbt2 = listnbt6.getList(j2);

            for(int j1 = 0; j1 < listnbt2.size(); ++j1) {
               chunkprimer1.addLightValue(listnbt2.getShort(j1), j2);
            }
         }

         compoundnbt5 = compoundnbt.getCompound("CarvingMasks");
         Iterator var51 = compoundnbt5.keySet().iterator();

         while(var51.hasNext()) {
            String s1 = (String)var51.next();
            GenerationStage.Carving generationstage$carving = GenerationStage.Carving.valueOf(s1);
            chunkprimer1.setCarvingMask(generationstage$carving, BitSet.valueOf(compoundnbt5.getByteArray(s1)));
         }

         MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Load((IChunk)ichunk, compoundnbt, chunkstatus$type));
         return chunkprimer1;
      }
   }

   public static CompoundNBT write(ServerWorld p_222645_0_, IChunk p_222645_1_) {
      ChunkPos chunkpos = p_222645_1_.getPos();
      CompoundNBT compoundnbt = new CompoundNBT();
      CompoundNBT compoundnbt1 = new CompoundNBT();
      compoundnbt.putInt("DataVersion", SharedConstants.getVersion().getWorldVersion());
      compoundnbt.put("Level", compoundnbt1);
      compoundnbt1.putInt("xPos", chunkpos.x);
      compoundnbt1.putInt("zPos", chunkpos.z);
      compoundnbt1.putLong("LastUpdate", p_222645_0_.getGameTime());
      compoundnbt1.putLong("InhabitedTime", p_222645_1_.getInhabitedTime());
      compoundnbt1.putString("Status", p_222645_1_.getStatus().getName());
      UpgradeData upgradedata = p_222645_1_.getUpgradeData();
      if (!upgradedata.isEmpty()) {
         compoundnbt1.put("UpgradeData", upgradedata.write());
      }

      ChunkSection[] achunksection = p_222645_1_.getSections();
      ListNBT listnbt = new ListNBT();
      WorldLightManager worldlightmanager = p_222645_0_.getChunkProvider().getLightManager();
      boolean flag = p_222645_1_.hasLight();

      CompoundNBT compoundnbt6;
      for(int i = -1; i < 17; ++i) {
         ChunkSection chunksection = (ChunkSection)Arrays.stream(achunksection).filter((p_lambda$write$3_1_) -> {
            return p_lambda$write$3_1_ != null && p_lambda$write$3_1_.getYLocation() >> 4 == i;
         }).findFirst().orElse(Chunk.EMPTY_SECTION);
         NibbleArray nibblearray = worldlightmanager.getLightEngine(LightType.BLOCK).getData(SectionPos.from(chunkpos, i));
         NibbleArray nibblearray1 = worldlightmanager.getLightEngine(LightType.SKY).getData(SectionPos.from(chunkpos, i));
         if (chunksection != Chunk.EMPTY_SECTION || nibblearray != null || nibblearray1 != null) {
            compoundnbt6 = new CompoundNBT();
            compoundnbt6.putByte("Y", (byte)(i & 255));
            if (chunksection != Chunk.EMPTY_SECTION) {
               chunksection.getData().writeChunkPalette(compoundnbt6, "Palette", "BlockStates");
            }

            if (nibblearray != null && !nibblearray.isEmpty()) {
               compoundnbt6.putByteArray("BlockLight", nibblearray.getData());
            }

            if (nibblearray1 != null && !nibblearray1.isEmpty()) {
               compoundnbt6.putByteArray("SkyLight", nibblearray1.getData());
            }

            listnbt.add(compoundnbt6);
         }
      }

      compoundnbt1.put("Sections", listnbt);
      if (flag) {
         compoundnbt1.putBoolean("isLightOn", true);
      }

      BiomeContainer biomecontainer = p_222645_1_.func_225549_i_();
      if (biomecontainer != null) {
         compoundnbt1.putIntArray("Biomes", biomecontainer.func_227055_a_());
      }

      ListNBT listnbt1 = new ListNBT();
      Iterator var22 = p_222645_1_.getTileEntitiesPos().iterator();

      CompoundNBT capTag;
      while(var22.hasNext()) {
         BlockPos blockpos = (BlockPos)var22.next();
         capTag = p_222645_1_.func_223134_j(blockpos);
         if (capTag != null) {
            listnbt1.add(capTag);
         }
      }

      compoundnbt1.put("TileEntities", listnbt1);
      ListNBT listnbt2 = new ListNBT();
      if (p_222645_1_.getStatus().getType() == ChunkStatus.Type.LEVELCHUNK) {
         Chunk chunk = (Chunk)p_222645_1_;
         chunk.setHasEntities(false);

         for(int k = 0; k < chunk.getEntityLists().length; ++k) {
            Iterator var30 = chunk.getEntityLists()[k].iterator();

            while(var30.hasNext()) {
               Entity entity = (Entity)var30.next();
               CompoundNBT compoundnbt3 = new CompoundNBT();

               try {
                  if (entity.writeUnlessPassenger(compoundnbt3)) {
                     chunk.setHasEntities(true);
                     listnbt2.add(compoundnbt3);
                  }
               } catch (Exception var20) {
                  LogManager.getLogger().error("An Entity type {} has thrown an exception trying to write state. It will not persist. Report this to the mod author", entity.getType(), var20);
               }
            }
         }

         try {
            capTag = chunk.writeCapsToNBT();
            if (capTag != null) {
               compoundnbt1.put("ForgeCaps", capTag);
            }
         } catch (Exception var19) {
            LogManager.getLogger().error("A capability provider has thrown an exception trying to write state. It will not persist. Report this to the mod author", var19);
         }
      } else {
         ChunkPrimer chunkprimer = (ChunkPrimer)p_222645_1_;
         listnbt2.addAll(chunkprimer.getEntities());
         compoundnbt1.put("Lights", toNbt(chunkprimer.getPackedLightPositions()));
         capTag = new CompoundNBT();
         GenerationStage.Carving[] var31 = GenerationStage.Carving.values();
         int var33 = var31.length;

         for(int var35 = 0; var35 < var33; ++var35) {
            GenerationStage.Carving generationstage$carving = var31[var35];
            capTag.putByteArray(generationstage$carving.toString(), p_222645_1_.getCarvingMask(generationstage$carving).toByteArray());
         }

         compoundnbt1.put("CarvingMasks", capTag);
      }

      compoundnbt1.put("Entities", listnbt2);
      ITickList<Block> iticklist = p_222645_1_.getBlocksToBeTicked();
      if (iticklist instanceof ChunkPrimerTickList) {
         compoundnbt1.put("ToBeTicked", ((ChunkPrimerTickList)iticklist).write());
      } else if (iticklist instanceof SerializableTickList) {
         compoundnbt1.put("TileTicks", ((SerializableTickList)iticklist).func_219498_a(p_222645_0_.getGameTime()));
      } else {
         compoundnbt1.put("TileTicks", p_222645_0_.getPendingBlockTicks().func_219503_a(chunkpos));
      }

      ITickList<Fluid> iticklist1 = p_222645_1_.getFluidsToBeTicked();
      if (iticklist1 instanceof ChunkPrimerTickList) {
         compoundnbt1.put("LiquidsToBeTicked", ((ChunkPrimerTickList)iticklist1).write());
      } else if (iticklist1 instanceof SerializableTickList) {
         compoundnbt1.put("LiquidTicks", ((SerializableTickList)iticklist1).func_219498_a(p_222645_0_.getGameTime()));
      } else {
         compoundnbt1.put("LiquidTicks", p_222645_0_.getPendingFluidTicks().func_219503_a(chunkpos));
      }

      compoundnbt1.put("PostProcessing", toNbt(p_222645_1_.getPackedPositions()));
      compoundnbt6 = new CompoundNBT();
      Iterator var34 = p_222645_1_.func_217311_f().iterator();

      while(var34.hasNext()) {
         Entry<Heightmap.Type, Heightmap> entry = (Entry)var34.next();
         if (p_222645_1_.getStatus().getHeightMaps().contains(entry.getKey())) {
            compoundnbt6.put(((Heightmap.Type)entry.getKey()).getId(), new LongArrayNBT(((Heightmap)entry.getValue()).getDataArray()));
         }
      }

      compoundnbt1.put("Heightmaps", compoundnbt6);
      compoundnbt1.put("Structures", writeStructures(chunkpos, p_222645_1_.getStructureStarts(), p_222645_1_.getStructureReferences()));
      return compoundnbt;
   }

   public static ChunkStatus.Type getChunkStatus(@Nullable CompoundNBT p_222651_0_) {
      if (p_222651_0_ != null) {
         ChunkStatus chunkstatus = ChunkStatus.byName(p_222651_0_.getCompound("Level").getString("Status"));
         if (chunkstatus != null) {
            return chunkstatus.getType();
         }
      }

      return ChunkStatus.Type.PROTOCHUNK;
   }

   private static void readEntities(CompoundNBT p_222650_0_, Chunk p_222650_1_) {
      ListNBT listnbt = p_222650_0_.getList("Entities", 10);
      World world = p_222650_1_.getWorld();

      for(int i = 0; i < listnbt.size(); ++i) {
         CompoundNBT compoundnbt = listnbt.getCompound(i);
         EntityType.func_220335_a(compoundnbt, world, (p_lambda$readEntities$4_1_) -> {
            p_222650_1_.addEntity(p_lambda$readEntities$4_1_);
            return p_lambda$readEntities$4_1_;
         });
         p_222650_1_.setHasEntities(true);
      }

      ListNBT listnbt1 = p_222650_0_.getList("TileEntities", 10);

      for(int j = 0; j < listnbt1.size(); ++j) {
         CompoundNBT compoundnbt1 = listnbt1.getCompound(j);
         boolean flag = compoundnbt1.getBoolean("keepPacked");
         if (flag) {
            p_222650_1_.addTileEntity(compoundnbt1);
         } else {
            TileEntity tileentity = TileEntity.create(compoundnbt1);
            if (tileentity != null) {
               p_222650_1_.addTileEntity(tileentity);
            }
         }
      }

   }

   private static CompoundNBT writeStructures(ChunkPos p_222649_0_, Map<String, StructureStart> p_222649_1_, Map<String, LongSet> p_222649_2_) {
      CompoundNBT compoundnbt = new CompoundNBT();
      CompoundNBT compoundnbt1 = new CompoundNBT();
      Iterator var5 = p_222649_1_.entrySet().iterator();

      while(var5.hasNext()) {
         Entry<String, StructureStart> entry = (Entry)var5.next();
         compoundnbt1.put((String)entry.getKey(), ((StructureStart)entry.getValue()).write(p_222649_0_.x, p_222649_0_.z));
      }

      compoundnbt.put("Starts", compoundnbt1);
      CompoundNBT compoundnbt2 = new CompoundNBT();
      Iterator var9 = p_222649_2_.entrySet().iterator();

      while(var9.hasNext()) {
         Entry<String, LongSet> entry1 = (Entry)var9.next();
         compoundnbt2.put((String)entry1.getKey(), new LongArrayNBT((LongSet)entry1.getValue()));
      }

      compoundnbt.put("References", compoundnbt2);
      return compoundnbt;
   }

   private static Map<String, StructureStart> func_227076_a_(ChunkGenerator<?> p_227076_0_, TemplateManager p_227076_1_, CompoundNBT p_227076_2_) {
      Map<String, StructureStart> map = Maps.newHashMap();
      CompoundNBT compoundnbt = p_227076_2_.getCompound("Starts");
      Iterator var5 = compoundnbt.keySet().iterator();

      while(var5.hasNext()) {
         String s = (String)var5.next();
         map.put(s, Structures.func_227456_a_(p_227076_0_, p_227076_1_, compoundnbt.getCompound(s)));
      }

      return map;
   }

   private static Map<String, LongSet> func_227075_a_(ChunkPos p_227075_0_, CompoundNBT p_227075_1_) {
      Map<String, LongSet> map = Maps.newHashMap();
      CompoundNBT compoundnbt = p_227075_1_.getCompound("References");
      Iterator var4 = compoundnbt.keySet().iterator();

      while(var4.hasNext()) {
         String s = (String)var4.next();
         map.put(s, new LongOpenHashSet(Arrays.stream(compoundnbt.getLongArray(s)).filter((p_lambda$func_227075_a_$5_2_) -> {
            ChunkPos chunkpos = new ChunkPos(p_lambda$func_227075_a_$5_2_);
            if (chunkpos.func_226661_a_(p_227075_0_) > 8) {
               LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", s, chunkpos, p_227075_0_);
               return false;
            } else {
               return true;
            }
         }).toArray()));
      }

      return map;
   }

   public static ListNBT toNbt(ShortList[] p_222647_0_) {
      ListNBT listnbt = new ListNBT();
      ShortList[] var2 = p_222647_0_;
      int var3 = p_222647_0_.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ShortList shortlist = var2[var4];
         ListNBT listnbt1 = new ListNBT();
         if (shortlist != null) {
            ShortListIterator var7 = shortlist.iterator();

            while(var7.hasNext()) {
               Short oshort = (Short)var7.next();
               listnbt1.add(ShortNBT.func_229701_a_(oshort));
            }
         }

         listnbt.add(listnbt1);
      }

      return listnbt;
   }
}
