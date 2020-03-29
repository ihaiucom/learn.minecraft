package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.crash.ReportedException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.chunk.storage.ChunkLoader;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldOptimizer {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ThreadFactory THREAD_FACTORY = (new ThreadFactoryBuilder()).setDaemon(true).build();
   private final String worldName;
   private final boolean field_219957_d;
   private final SaveHandler worldStorage;
   private final Thread thread;
   private final File folder;
   private volatile boolean active = true;
   private volatile boolean done;
   private volatile float totalProgress;
   private volatile int totalChunks;
   private volatile int converted;
   private volatile int skipped;
   private final Object2FloatMap<DimensionType> progress = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap(Util.identityHashStrategy()));
   private volatile ITextComponent statusText = new TranslationTextComponent("optimizeWorld.stage.counting", new Object[0]);
   private static final Pattern REGION_FILE_PATTERN = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
   private final DimensionSavedDataManager savedDataManager;

   public WorldOptimizer(String p_i50400_1_, SaveFormat p_i50400_2_, WorldInfo p_i50400_3_, boolean p_i50400_4_) {
      this.worldName = p_i50400_3_.getWorldName();
      this.field_219957_d = p_i50400_4_;
      this.worldStorage = p_i50400_2_.getSaveLoader(p_i50400_1_, (MinecraftServer)null);
      this.worldStorage.saveWorldInfo(p_i50400_3_);
      this.savedDataManager = new DimensionSavedDataManager(new File(DimensionType.OVERWORLD.getDirectory(this.worldStorage.getWorldDirectory()), "data"), this.worldStorage.getFixer());
      this.folder = this.worldStorage.getWorldDirectory();
      this.thread = THREAD_FACTORY.newThread(this::optimize);
      this.thread.setUncaughtExceptionHandler((p_219956_1_, p_219956_2_) -> {
         LOGGER.error("Error upgrading world", p_219956_2_);
         this.statusText = new TranslationTextComponent("optimizeWorld.stage.failed", new Object[0]);
         this.done = true;
      });
      this.thread.start();
   }

   public void cancel() {
      this.active = false;

      try {
         this.thread.join();
      } catch (InterruptedException var2) {
      }

   }

   private void optimize() {
      File lvt_1_1_ = this.worldStorage.getWorldDirectory();
      this.totalChunks = 0;
      Builder<DimensionType, ListIterator<ChunkPos>> lvt_2_1_ = ImmutableMap.builder();

      List lvt_5_1_;
      for(Iterator var3 = DimensionType.getAll().iterator(); var3.hasNext(); this.totalChunks += lvt_5_1_.size()) {
         DimensionType lvt_4_1_ = (DimensionType)var3.next();
         lvt_5_1_ = this.func_219953_b(lvt_4_1_);
         lvt_2_1_.put(lvt_4_1_, lvt_5_1_.listIterator());
      }

      if (this.totalChunks == 0) {
         this.done = true;
      } else {
         float lvt_3_1_ = (float)this.totalChunks;
         ImmutableMap<DimensionType, ListIterator<ChunkPos>> lvt_4_2_ = lvt_2_1_.build();
         Builder<DimensionType, ChunkLoader> lvt_5_2_ = ImmutableMap.builder();
         Iterator var6 = DimensionType.getAll().iterator();

         while(var6.hasNext()) {
            DimensionType lvt_7_1_ = (DimensionType)var6.next();
            File lvt_8_1_ = lvt_7_1_.getDirectory(lvt_1_1_);
            lvt_5_2_.put(lvt_7_1_, new ChunkLoader(new File(lvt_8_1_, "region"), this.worldStorage.getFixer()));
         }

         ImmutableMap<DimensionType, ChunkLoader> lvt_6_1_ = lvt_5_2_.build();
         long lvt_7_2_ = Util.milliTime();
         this.statusText = new TranslationTextComponent("optimizeWorld.stage.upgrading", new Object[0]);

         while(this.active) {
            boolean lvt_9_1_ = false;
            float lvt_10_1_ = 0.0F;

            float lvt_15_2_;
            for(Iterator var11 = DimensionType.getAll().iterator(); var11.hasNext(); lvt_10_1_ += lvt_15_2_) {
               DimensionType lvt_12_1_ = (DimensionType)var11.next();
               ListIterator<ChunkPos> lvt_13_1_ = (ListIterator)lvt_4_2_.get(lvt_12_1_);
               ChunkLoader lvt_14_1_ = (ChunkLoader)lvt_6_1_.get(lvt_12_1_);
               if (lvt_13_1_.hasNext()) {
                  ChunkPos lvt_15_1_ = (ChunkPos)lvt_13_1_.next();
                  boolean lvt_16_1_ = false;

                  try {
                     CompoundNBT lvt_17_1_ = lvt_14_1_.func_227078_e_(lvt_15_1_);
                     if (lvt_17_1_ != null) {
                        int lvt_18_1_ = ChunkLoader.getDataVersion(lvt_17_1_);
                        CompoundNBT lvt_19_1_ = lvt_14_1_.updateChunkData(lvt_12_1_, () -> {
                           return this.savedDataManager;
                        }, lvt_17_1_);
                        CompoundNBT lvt_20_1_ = lvt_19_1_.getCompound("Level");
                        ChunkPos lvt_21_1_ = new ChunkPos(lvt_20_1_.getInt("xPos"), lvt_20_1_.getInt("zPos"));
                        if (!lvt_21_1_.equals(lvt_15_1_)) {
                           LOGGER.warn("Chunk {} has invalid position {}", lvt_15_1_, lvt_21_1_);
                        }

                        boolean lvt_22_1_ = lvt_18_1_ < SharedConstants.getVersion().getWorldVersion();
                        if (this.field_219957_d) {
                           lvt_22_1_ = lvt_22_1_ || lvt_20_1_.contains("Heightmaps");
                           lvt_20_1_.remove("Heightmaps");
                           lvt_22_1_ = lvt_22_1_ || lvt_20_1_.contains("isLightOn");
                           lvt_20_1_.remove("isLightOn");
                        }

                        if (lvt_22_1_) {
                           lvt_14_1_.writeChunk(lvt_15_1_, lvt_19_1_);
                           lvt_16_1_ = true;
                        }
                     }
                  } catch (ReportedException var24) {
                     Throwable lvt_18_2_ = var24.getCause();
                     if (!(lvt_18_2_ instanceof IOException)) {
                        throw var24;
                     }

                     LOGGER.error("Error upgrading chunk {}", lvt_15_1_, lvt_18_2_);
                  } catch (IOException var25) {
                     LOGGER.error("Error upgrading chunk {}", lvt_15_1_, var25);
                  }

                  if (lvt_16_1_) {
                     ++this.converted;
                  } else {
                     ++this.skipped;
                  }

                  lvt_9_1_ = true;
               }

               lvt_15_2_ = (float)lvt_13_1_.nextIndex() / lvt_3_1_;
               this.progress.put(lvt_12_1_, lvt_15_2_);
            }

            this.totalProgress = lvt_10_1_;
            if (!lvt_9_1_) {
               this.active = false;
            }
         }

         this.statusText = new TranslationTextComponent("optimizeWorld.stage.finished", new Object[0]);
         UnmodifiableIterator var31 = lvt_6_1_.values().iterator();

         while(var31.hasNext()) {
            ChunkLoader lvt_10_2_ = (ChunkLoader)var31.next();

            try {
               lvt_10_2_.close();
            } catch (IOException var23) {
               LOGGER.error("Error upgrading chunk", var23);
            }
         }

         this.savedDataManager.save();
         lvt_7_2_ = Util.milliTime() - lvt_7_2_;
         LOGGER.info("World optimizaton finished after {} ms", lvt_7_2_);
         this.done = true;
      }
   }

   private List<ChunkPos> func_219953_b(DimensionType p_219953_1_) {
      File lvt_2_1_ = p_219953_1_.getDirectory(this.folder);
      File lvt_3_1_ = new File(lvt_2_1_, "region");
      File[] lvt_4_1_ = lvt_3_1_.listFiles((p_219954_0_, p_219954_1_) -> {
         return p_219954_1_.endsWith(".mca");
      });
      if (lvt_4_1_ == null) {
         return ImmutableList.of();
      } else {
         List<ChunkPos> lvt_5_1_ = Lists.newArrayList();
         File[] var6 = lvt_4_1_;
         int var7 = lvt_4_1_.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            File lvt_9_1_ = var6[var8];
            Matcher lvt_10_1_ = REGION_FILE_PATTERN.matcher(lvt_9_1_.getName());
            if (lvt_10_1_.matches()) {
               int lvt_11_1_ = Integer.parseInt(lvt_10_1_.group(1)) << 5;
               int lvt_12_1_ = Integer.parseInt(lvt_10_1_.group(2)) << 5;

               try {
                  RegionFile lvt_13_1_ = new RegionFile(lvt_9_1_, lvt_3_1_);
                  Throwable var14 = null;

                  try {
                     for(int lvt_15_1_ = 0; lvt_15_1_ < 32; ++lvt_15_1_) {
                        for(int lvt_16_1_ = 0; lvt_16_1_ < 32; ++lvt_16_1_) {
                           ChunkPos lvt_17_1_ = new ChunkPos(lvt_15_1_ + lvt_11_1_, lvt_16_1_ + lvt_12_1_);
                           if (lvt_13_1_.func_222662_b(lvt_17_1_)) {
                              lvt_5_1_.add(lvt_17_1_);
                           }
                        }
                     }
                  } catch (Throwable var26) {
                     var14 = var26;
                     throw var26;
                  } finally {
                     if (lvt_13_1_ != null) {
                        if (var14 != null) {
                           try {
                              lvt_13_1_.close();
                           } catch (Throwable var25) {
                              var14.addSuppressed(var25);
                           }
                        } else {
                           lvt_13_1_.close();
                        }
                     }

                  }
               } catch (Throwable var28) {
               }
            }
         }

         return lvt_5_1_;
      }
   }

   public boolean isFinished() {
      return this.done;
   }

   @OnlyIn(Dist.CLIENT)
   public float getProgress(DimensionType p_212543_1_) {
      return this.progress.getFloat(p_212543_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public float getTotalProgress() {
      return this.totalProgress;
   }

   public int getTotalChunks() {
      return this.totalChunks;
   }

   public int getConverted() {
      return this.converted;
   }

   public int getSkipped() {
      return this.skipped;
   }

   public ITextComponent getStatusText() {
      return this.statusText;
   }
}
