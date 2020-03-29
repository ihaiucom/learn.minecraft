package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.WorkingScreen;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.FilePack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.VanillaPack;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.HTTPUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class DownloadingPackFinder implements IPackFinder {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern field_195752_b = Pattern.compile("^[a-fA-F0-9]{40}$");
   private final VanillaPack vanillaPack;
   private final File field_195754_d;
   private final ReentrantLock field_195755_e = new ReentrantLock();
   private final ResourceIndex field_217819_f;
   @Nullable
   private CompletableFuture<?> field_195756_f;
   @Nullable
   private ClientResourcePackInfo field_195757_g;

   public DownloadingPackFinder(File p_i48116_1_, ResourceIndex p_i48116_2_) {
      this.field_195754_d = p_i48116_1_;
      this.field_217819_f = p_i48116_2_;
      this.vanillaPack = new VirtualAssetsPack(p_i48116_2_);
   }

   public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> p_195730_1_, ResourcePackInfo.IFactory<T> p_195730_2_) {
      T lvt_3_1_ = ResourcePackInfo.createResourcePack("vanilla", true, () -> {
         return this.vanillaPack;
      }, p_195730_2_, ResourcePackInfo.Priority.BOTTOM);
      if (lvt_3_1_ != null) {
         p_195730_1_.put("vanilla", lvt_3_1_);
      }

      if (this.field_195757_g != null) {
         p_195730_1_.put("server", this.field_195757_g);
      }

      File lvt_4_1_ = this.field_217819_f.getFile(new ResourceLocation("resourcepacks/programmer_art.zip"));
      if (lvt_4_1_ != null && lvt_4_1_.isFile()) {
         T lvt_5_1_ = ResourcePackInfo.createResourcePack("programer_art", false, () -> {
            return new FilePack(lvt_4_1_) {
               public String getName() {
                  return "Programmer Art";
               }
            };
         }, p_195730_2_, ResourcePackInfo.Priority.TOP);
         if (lvt_5_1_ != null) {
            p_195730_1_.put("programer_art", lvt_5_1_);
         }
      }

   }

   public VanillaPack getVanillaPack() {
      return this.vanillaPack;
   }

   public static Map<String, String> func_195742_b() {
      Map<String, String> lvt_0_1_ = Maps.newHashMap();
      lvt_0_1_.put("X-Minecraft-Username", Minecraft.getInstance().getSession().getUsername());
      lvt_0_1_.put("X-Minecraft-UUID", Minecraft.getInstance().getSession().getPlayerID());
      lvt_0_1_.put("X-Minecraft-Version", SharedConstants.getVersion().getName());
      lvt_0_1_.put("X-Minecraft-Version-ID", SharedConstants.getVersion().getId());
      lvt_0_1_.put("X-Minecraft-Pack-Format", String.valueOf(SharedConstants.getVersion().getPackVersion()));
      lvt_0_1_.put("User-Agent", "Minecraft Java/" + SharedConstants.getVersion().getName());
      return lvt_0_1_;
   }

   public CompletableFuture<?> func_217818_a(String p_217818_1_, String p_217818_2_) {
      String lvt_3_1_ = DigestUtils.sha1Hex(p_217818_1_);
      String lvt_4_1_ = field_195752_b.matcher(p_217818_2_).matches() ? p_217818_2_ : "";
      this.field_195755_e.lock();

      CompletableFuture var13;
      try {
         this.clearResourcePack();
         this.func_195747_e();
         File lvt_5_1_ = new File(this.field_195754_d, lvt_3_1_);
         CompletableFuture lvt_6_2_;
         if (lvt_5_1_.exists()) {
            lvt_6_2_ = CompletableFuture.completedFuture("");
         } else {
            WorkingScreen lvt_7_1_ = new WorkingScreen();
            Map<String, String> lvt_8_1_ = func_195742_b();
            Minecraft lvt_9_1_ = Minecraft.getInstance();
            lvt_9_1_.runImmediately(() -> {
               lvt_9_1_.displayGuiScreen(lvt_7_1_);
            });
            lvt_6_2_ = HTTPUtil.downloadResourcePack(lvt_5_1_, p_217818_1_, lvt_8_1_, 104857600, lvt_7_1_, lvt_9_1_.getProxy());
         }

         this.field_195756_f = lvt_6_2_.thenCompose((p_217812_3_) -> {
            return !this.func_195745_a(lvt_4_1_, lvt_5_1_) ? Util.completedExceptionallyFuture(new RuntimeException("Hash check failure for file " + lvt_5_1_ + ", see log")) : this.func_217816_a(lvt_5_1_);
         }).whenComplete((p_217815_1_, p_217815_2_) -> {
            if (p_217815_2_ != null) {
               LOGGER.warn("Pack application failed: {}, deleting file {}", p_217815_2_.getMessage(), lvt_5_1_);
               func_217811_b(lvt_5_1_);
            }

         });
         var13 = this.field_195756_f;
      } finally {
         this.field_195755_e.unlock();
      }

      return var13;
   }

   private static void func_217811_b(File p_217811_0_) {
      try {
         Files.delete(p_217811_0_.toPath());
      } catch (IOException var2) {
         LOGGER.warn("Failed to delete file {}: {}", p_217811_0_, var2.getMessage());
      }

   }

   public void clearResourcePack() {
      this.field_195755_e.lock();

      try {
         if (this.field_195756_f != null) {
            this.field_195756_f.cancel(true);
         }

         this.field_195756_f = null;
         if (this.field_195757_g != null) {
            this.field_195757_g = null;
            Minecraft.getInstance().func_213245_w();
         }
      } finally {
         this.field_195755_e.unlock();
      }

   }

   private boolean func_195745_a(String p_195745_1_, File p_195745_2_) {
      try {
         FileInputStream lvt_4_1_ = new FileInputStream(p_195745_2_);
         Throwable var5 = null;

         String lvt_3_2_;
         try {
            lvt_3_2_ = DigestUtils.sha1Hex(lvt_4_1_);
         } catch (Throwable var15) {
            var5 = var15;
            throw var15;
         } finally {
            if (lvt_4_1_ != null) {
               if (var5 != null) {
                  try {
                     lvt_4_1_.close();
                  } catch (Throwable var14) {
                     var5.addSuppressed(var14);
                  }
               } else {
                  lvt_4_1_.close();
               }
            }

         }

         if (p_195745_1_.isEmpty()) {
            LOGGER.info("Found file {} without verification hash", p_195745_2_);
            return true;
         }

         if (lvt_3_2_.toLowerCase(java.util.Locale.ROOT).equals(p_195745_1_.toLowerCase(java.util.Locale.ROOT))) {
            LOGGER.info("Found file {} matching requested hash {}", p_195745_2_, p_195745_1_);
            return true;
         }

         LOGGER.warn("File {} had wrong hash (expected {}, found {}).", p_195745_2_, p_195745_1_, lvt_3_2_);
      } catch (IOException var17) {
         LOGGER.warn("File {} couldn't be hashed.", p_195745_2_, var17);
      }

      return false;
   }

   private void func_195747_e() {
      try {
         List<File> lvt_1_1_ = Lists.newArrayList(FileUtils.listFiles(this.field_195754_d, TrueFileFilter.TRUE, (IOFileFilter)null));
         lvt_1_1_.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
         int lvt_2_1_ = 0;
         Iterator var3 = lvt_1_1_.iterator();

         while(var3.hasNext()) {
            File lvt_4_1_ = (File)var3.next();
            if (lvt_2_1_++ >= 10) {
               LOGGER.info("Deleting old server resource pack {}", lvt_4_1_.getName());
               FileUtils.deleteQuietly(lvt_4_1_);
            }
         }
      } catch (IllegalArgumentException var5) {
         LOGGER.error("Error while deleting old server resource pack : {}", var5.getMessage());
      }

   }

   public CompletableFuture<Void> func_217816_a(File p_217816_1_) {
      PackMetadataSection lvt_2_1_ = null;
      NativeImage lvt_3_1_ = null;
      String lvt_4_1_ = null;

      try {
         FilePack lvt_5_1_ = new FilePack(p_217816_1_);
         Throwable var6 = null;

         try {
            lvt_2_1_ = (PackMetadataSection)lvt_5_1_.getMetadata(PackMetadataSection.SERIALIZER);

            try {
               InputStream lvt_7_1_ = lvt_5_1_.getRootResourceStream("pack.png");
               Throwable var8 = null;

               try {
                  lvt_3_1_ = NativeImage.read(lvt_7_1_);
               } catch (Throwable var35) {
                  var8 = var35;
                  throw var35;
               } finally {
                  if (lvt_7_1_ != null) {
                     if (var8 != null) {
                        try {
                           lvt_7_1_.close();
                        } catch (Throwable var34) {
                           var8.addSuppressed(var34);
                        }
                     } else {
                        lvt_7_1_.close();
                     }
                  }

               }
            } catch (IllegalArgumentException | IOException var37) {
               LOGGER.info("Could not read pack.png: {}", var37.getMessage());
            }
         } catch (Throwable var38) {
            var6 = var38;
            throw var38;
         } finally {
            if (lvt_5_1_ != null) {
               if (var6 != null) {
                  try {
                     lvt_5_1_.close();
                  } catch (Throwable var33) {
                     var6.addSuppressed(var33);
                  }
               } else {
                  lvt_5_1_.close();
               }
            }

         }
      } catch (IOException var40) {
         lvt_4_1_ = var40.getMessage();
      }

      if (lvt_4_1_ != null) {
         return Util.completedExceptionallyFuture(new RuntimeException(String.format("Invalid resourcepack at %s: %s", p_217816_1_, lvt_4_1_)));
      } else {
         LOGGER.info("Applying server pack {}", p_217816_1_);
         this.field_195757_g = new ClientResourcePackInfo("server", true, () -> {
            return new FilePack(p_217816_1_);
         }, new TranslationTextComponent("resourcePack.server.name", new Object[0]), lvt_2_1_.getDescription(), PackCompatibility.func_198969_a(lvt_2_1_.getPackFormat()), ResourcePackInfo.Priority.TOP, true, lvt_3_1_);
         return Minecraft.getInstance().func_213245_w();
      }
   }
}
