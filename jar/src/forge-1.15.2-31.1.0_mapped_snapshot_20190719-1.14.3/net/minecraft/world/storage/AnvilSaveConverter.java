package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.chunk.storage.ChunkLoaderUtil;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilSaveConverter {
   private static final Logger LOGGER = LogManager.getLogger();

   static boolean func_215792_a(Path p_215792_0_, DataFixer p_215792_1_, String p_215792_2_, IProgressUpdate p_215792_3_) {
      p_215792_3_.setLoadingProgress(0);
      List<File> lvt_4_1_ = Lists.newArrayList();
      List<File> lvt_5_1_ = Lists.newArrayList();
      List<File> lvt_6_1_ = Lists.newArrayList();
      File lvt_7_1_ = new File(p_215792_0_.toFile(), p_215792_2_);
      File lvt_8_1_ = DimensionType.THE_NETHER.getDirectory(lvt_7_1_);
      File lvt_9_1_ = DimensionType.THE_END.getDirectory(lvt_7_1_);
      LOGGER.info("Scanning folders...");
      func_215789_a(lvt_7_1_, lvt_4_1_);
      if (lvt_8_1_.exists()) {
         func_215789_a(lvt_8_1_, lvt_5_1_);
      }

      if (lvt_9_1_.exists()) {
         func_215789_a(lvt_9_1_, lvt_6_1_);
      }

      int lvt_10_1_ = lvt_4_1_.size() + lvt_5_1_.size() + lvt_6_1_.size();
      LOGGER.info("Total conversion count is {}", lvt_10_1_);
      WorldInfo lvt_11_1_ = SaveFormat.func_215779_a(p_215792_0_, p_215792_1_, p_215792_2_);
      BiomeProviderType<SingleBiomeProviderSettings, SingleBiomeProvider> lvt_13_1_ = BiomeProviderType.FIXED;
      BiomeProviderType<OverworldBiomeProviderSettings, OverworldBiomeProvider> lvt_14_1_ = BiomeProviderType.VANILLA_LAYERED;
      BiomeProvider lvt_12_2_;
      if (lvt_11_1_ != null && lvt_11_1_.getGenerator() == WorldType.FLAT) {
         lvt_12_2_ = lvt_13_1_.create(((SingleBiomeProviderSettings)lvt_13_1_.func_226840_a_(lvt_11_1_)).setBiome(Biomes.PLAINS));
      } else {
         lvt_12_2_ = lvt_14_1_.create(lvt_14_1_.func_226840_a_(lvt_11_1_));
      }

      func_215794_a(new File(lvt_7_1_, "region"), lvt_4_1_, lvt_12_2_, 0, lvt_10_1_, p_215792_3_);
      func_215794_a(new File(lvt_8_1_, "region"), lvt_5_1_, lvt_13_1_.create(((SingleBiomeProviderSettings)lvt_13_1_.func_226840_a_(lvt_11_1_)).setBiome(Biomes.NETHER)), lvt_4_1_.size(), lvt_10_1_, p_215792_3_);
      func_215794_a(new File(lvt_9_1_, "region"), lvt_6_1_, lvt_13_1_.create(((SingleBiomeProviderSettings)lvt_13_1_.func_226840_a_(lvt_11_1_)).setBiome(Biomes.THE_END)), lvt_4_1_.size() + lvt_5_1_.size(), lvt_10_1_, p_215792_3_);
      lvt_11_1_.setSaveVersion(19133);
      if (lvt_11_1_.getGenerator() == WorldType.DEFAULT_1_1) {
         lvt_11_1_.setGenerator(WorldType.DEFAULT);
      }

      func_215790_a(p_215792_0_, p_215792_2_);
      SaveHandler lvt_15_1_ = SaveFormat.func_215783_a(p_215792_0_, p_215792_1_, p_215792_2_, (MinecraftServer)null);
      lvt_15_1_.saveWorldInfo(lvt_11_1_);
      return true;
   }

   private static void func_215790_a(Path p_215790_0_, String p_215790_1_) {
      File lvt_2_1_ = new File(p_215790_0_.toFile(), p_215790_1_);
      if (!lvt_2_1_.exists()) {
         LOGGER.warn("Unable to create level.dat_mcr backup");
      } else {
         File lvt_3_1_ = new File(lvt_2_1_, "level.dat");
         if (!lvt_3_1_.exists()) {
            LOGGER.warn("Unable to create level.dat_mcr backup");
         } else {
            File lvt_4_1_ = new File(lvt_2_1_, "level.dat_mcr");
            if (!lvt_3_1_.renameTo(lvt_4_1_)) {
               LOGGER.warn("Unable to create level.dat_mcr backup");
            }

         }
      }
   }

   private static void func_215794_a(File p_215794_0_, Iterable<File> p_215794_1_, BiomeProvider p_215794_2_, int p_215794_3_, int p_215794_4_, IProgressUpdate p_215794_5_) {
      Iterator var6 = p_215794_1_.iterator();

      while(var6.hasNext()) {
         File lvt_7_1_ = (File)var6.next();
         func_215793_a(p_215794_0_, lvt_7_1_, p_215794_2_, p_215794_3_, p_215794_4_, p_215794_5_);
         ++p_215794_3_;
         int lvt_8_1_ = (int)Math.round(100.0D * (double)p_215794_3_ / (double)p_215794_4_);
         p_215794_5_.setLoadingProgress(lvt_8_1_);
      }

   }

   private static void func_215793_a(File p_215793_0_, File p_215793_1_, BiomeProvider p_215793_2_, int p_215793_3_, int p_215793_4_, IProgressUpdate p_215793_5_) {
      String lvt_6_1_ = p_215793_1_.getName();

      try {
         RegionFile lvt_7_1_ = new RegionFile(p_215793_1_, p_215793_0_);
         Throwable var8 = null;

         try {
            RegionFile lvt_9_1_ = new RegionFile(new File(p_215793_0_, lvt_6_1_.substring(0, lvt_6_1_.length() - ".mcr".length()) + ".mca"), p_215793_0_);
            Throwable var10 = null;

            try {
               for(int lvt_11_1_ = 0; lvt_11_1_ < 32; ++lvt_11_1_) {
                  int lvt_12_2_;
                  for(lvt_12_2_ = 0; lvt_12_2_ < 32; ++lvt_12_2_) {
                     ChunkPos lvt_13_1_ = new ChunkPos(lvt_11_1_, lvt_12_2_);
                     if (lvt_7_1_.contains(lvt_13_1_) && !lvt_9_1_.contains(lvt_13_1_)) {
                        CompoundNBT lvt_14_3_;
                        try {
                           DataInputStream lvt_15_1_ = lvt_7_1_.func_222666_a(lvt_13_1_);
                           Throwable var16 = null;

                           try {
                              if (lvt_15_1_ == null) {
                                 LOGGER.warn("Failed to fetch input stream for chunk {}", lvt_13_1_);
                                 continue;
                              }

                              lvt_14_3_ = CompressedStreamTools.read(lvt_15_1_);
                           } catch (Throwable var104) {
                              var16 = var104;
                              throw var104;
                           } finally {
                              if (lvt_15_1_ != null) {
                                 if (var16 != null) {
                                    try {
                                       lvt_15_1_.close();
                                    } catch (Throwable var101) {
                                       var16.addSuppressed(var101);
                                    }
                                 } else {
                                    lvt_15_1_.close();
                                 }
                              }

                           }
                        } catch (IOException var106) {
                           LOGGER.warn("Failed to read data for chunk {}", lvt_13_1_, var106);
                           continue;
                        }

                        CompoundNBT lvt_15_3_ = lvt_14_3_.getCompound("Level");
                        ChunkLoaderUtil.AnvilConverterData lvt_16_1_ = ChunkLoaderUtil.load(lvt_15_3_);
                        CompoundNBT lvt_17_1_ = new CompoundNBT();
                        CompoundNBT lvt_18_1_ = new CompoundNBT();
                        lvt_17_1_.put("Level", lvt_18_1_);
                        ChunkLoaderUtil.convertToAnvilFormat(lvt_16_1_, lvt_18_1_, p_215793_2_);
                        DataOutputStream lvt_19_1_ = lvt_9_1_.func_222661_c(lvt_13_1_);
                        Throwable var20 = null;

                        try {
                           CompressedStreamTools.write(lvt_17_1_, (DataOutput)lvt_19_1_);
                        } catch (Throwable var102) {
                           var20 = var102;
                           throw var102;
                        } finally {
                           if (lvt_19_1_ != null) {
                              if (var20 != null) {
                                 try {
                                    lvt_19_1_.close();
                                 } catch (Throwable var100) {
                                    var20.addSuppressed(var100);
                                 }
                              } else {
                                 lvt_19_1_.close();
                              }
                           }

                        }
                     }
                  }

                  lvt_12_2_ = (int)Math.round(100.0D * (double)(p_215793_3_ * 1024) / (double)(p_215793_4_ * 1024));
                  int lvt_13_2_ = (int)Math.round(100.0D * (double)((lvt_11_1_ + 1) * 32 + p_215793_3_ * 1024) / (double)(p_215793_4_ * 1024));
                  if (lvt_13_2_ > lvt_12_2_) {
                     p_215793_5_.setLoadingProgress(lvt_13_2_);
                  }
               }
            } catch (Throwable var107) {
               var10 = var107;
               throw var107;
            } finally {
               if (lvt_9_1_ != null) {
                  if (var10 != null) {
                     try {
                        lvt_9_1_.close();
                     } catch (Throwable var99) {
                        var10.addSuppressed(var99);
                     }
                  } else {
                     lvt_9_1_.close();
                  }
               }

            }
         } catch (Throwable var109) {
            var8 = var109;
            throw var109;
         } finally {
            if (lvt_7_1_ != null) {
               if (var8 != null) {
                  try {
                     lvt_7_1_.close();
                  } catch (Throwable var98) {
                     var8.addSuppressed(var98);
                  }
               } else {
                  lvt_7_1_.close();
               }
            }

         }
      } catch (IOException var111) {
         LOGGER.error("Failed to upgrade region file {}", p_215793_1_, var111);
      }

   }

   private static void func_215789_a(File p_215789_0_, Collection<File> p_215789_1_) {
      File lvt_2_1_ = new File(p_215789_0_, "region");
      File[] lvt_3_1_ = lvt_2_1_.listFiles((p_215791_0_, p_215791_1_) -> {
         return p_215791_1_.endsWith(".mcr");
      });
      if (lvt_3_1_ != null) {
         Collections.addAll(p_215789_1_, lvt_3_1_);
      }

   }
}
