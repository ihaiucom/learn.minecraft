package net.minecraft.world.gen.feature.template;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FileUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.datafix.DefaultTypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TemplateManager implements IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ResourceLocation, Template> templates = Maps.newHashMap();
   private final DataFixer fixer;
   private final MinecraftServer minecraftServer;
   private final Path pathGenerated;

   public TemplateManager(MinecraftServer p_i49570_1_, File p_i49570_2_, DataFixer p_i49570_3_) {
      this.minecraftServer = p_i49570_1_;
      this.fixer = p_i49570_3_;
      this.pathGenerated = p_i49570_2_.toPath().resolve("generated").normalize();
      p_i49570_1_.getResourceManager().addReloadListener(this);
   }

   public Template getTemplateDefaulted(ResourceLocation p_200220_1_) {
      Template lvt_2_1_ = this.getTemplate(p_200220_1_);
      if (lvt_2_1_ == null) {
         lvt_2_1_ = new Template();
         this.templates.put(p_200220_1_, lvt_2_1_);
      }

      return lvt_2_1_;
   }

   @Nullable
   public Template getTemplate(ResourceLocation p_200219_1_) {
      return (Template)this.templates.computeIfAbsent(p_200219_1_, (p_209204_1_) -> {
         Template lvt_2_1_ = this.loadTemplateFile(p_209204_1_);
         return lvt_2_1_ != null ? lvt_2_1_ : this.loadTemplateResource(p_209204_1_);
      });
   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      this.templates.clear();
   }

   @Nullable
   private Template loadTemplateResource(ResourceLocation p_209201_1_) {
      ResourceLocation lvt_2_1_ = new ResourceLocation(p_209201_1_.getNamespace(), "structures/" + p_209201_1_.getPath() + ".nbt");

      try {
         IResource lvt_3_1_ = this.minecraftServer.getResourceManager().getResource(lvt_2_1_);
         Throwable var4 = null;

         Template var5;
         try {
            var5 = this.loadTemplate(lvt_3_1_.getInputStream());
         } catch (Throwable var16) {
            var4 = var16;
            throw var16;
         } finally {
            if (lvt_3_1_ != null) {
               if (var4 != null) {
                  try {
                     lvt_3_1_.close();
                  } catch (Throwable var15) {
                     var4.addSuppressed(var15);
                  }
               } else {
                  lvt_3_1_.close();
               }
            }

         }

         return var5;
      } catch (FileNotFoundException var18) {
         return null;
      } catch (Throwable var19) {
         LOGGER.error("Couldn't load structure {}: {}", p_209201_1_, var19.toString());
         return null;
      }
   }

   @Nullable
   private Template loadTemplateFile(ResourceLocation p_195428_1_) {
      if (!this.pathGenerated.toFile().isDirectory()) {
         return null;
      } else {
         Path lvt_2_1_ = this.resolvePath(p_195428_1_, ".nbt");

         try {
            InputStream lvt_3_1_ = new FileInputStream(lvt_2_1_.toFile());
            Throwable var4 = null;

            Template var5;
            try {
               var5 = this.loadTemplate(lvt_3_1_);
            } catch (Throwable var16) {
               var4 = var16;
               throw var16;
            } finally {
               if (lvt_3_1_ != null) {
                  if (var4 != null) {
                     try {
                        lvt_3_1_.close();
                     } catch (Throwable var15) {
                        var4.addSuppressed(var15);
                     }
                  } else {
                     lvt_3_1_.close();
                  }
               }

            }

            return var5;
         } catch (FileNotFoundException var18) {
            return null;
         } catch (IOException var19) {
            LOGGER.error("Couldn't load structure from {}", lvt_2_1_, var19);
            return null;
         }
      }
   }

   private Template loadTemplate(InputStream p_209205_1_) throws IOException {
      CompoundNBT lvt_2_1_ = CompressedStreamTools.readCompressed(p_209205_1_);
      return this.func_227458_a_(lvt_2_1_);
   }

   public Template func_227458_a_(CompoundNBT p_227458_1_) {
      if (!p_227458_1_.contains("DataVersion", 99)) {
         p_227458_1_.putInt("DataVersion", 500);
      }

      Template lvt_2_1_ = new Template();
      lvt_2_1_.read(NBTUtil.update(this.fixer, DefaultTypeReferences.STRUCTURE, p_227458_1_, p_227458_1_.getInt("DataVersion")));
      return lvt_2_1_;
   }

   public boolean writeToFile(ResourceLocation p_195429_1_) {
      Template lvt_2_1_ = (Template)this.templates.get(p_195429_1_);
      if (lvt_2_1_ == null) {
         return false;
      } else {
         Path lvt_3_1_ = this.resolvePath(p_195429_1_, ".nbt");
         Path lvt_4_1_ = lvt_3_1_.getParent();
         if (lvt_4_1_ == null) {
            return false;
         } else {
            try {
               Files.createDirectories(Files.exists(lvt_4_1_, new LinkOption[0]) ? lvt_4_1_.toRealPath() : lvt_4_1_);
            } catch (IOException var19) {
               LOGGER.error("Failed to create parent directory: {}", lvt_4_1_);
               return false;
            }

            CompoundNBT lvt_5_2_ = lvt_2_1_.writeToNBT(new CompoundNBT());

            try {
               OutputStream lvt_6_1_ = new FileOutputStream(lvt_3_1_.toFile());
               Throwable var7 = null;

               try {
                  CompressedStreamTools.writeCompressed(lvt_5_2_, lvt_6_1_);
               } catch (Throwable var18) {
                  var7 = var18;
                  throw var18;
               } finally {
                  if (lvt_6_1_ != null) {
                     if (var7 != null) {
                        try {
                           lvt_6_1_.close();
                        } catch (Throwable var17) {
                           var7.addSuppressed(var17);
                        }
                     } else {
                        lvt_6_1_.close();
                     }
                  }

               }

               return true;
            } catch (Throwable var21) {
               return false;
            }
         }
      }
   }

   public Path resolvePathStructures(ResourceLocation p_209509_1_, String p_209509_2_) {
      try {
         Path lvt_3_1_ = this.pathGenerated.resolve(p_209509_1_.getNamespace());
         Path lvt_4_1_ = lvt_3_1_.resolve("structures");
         return FileUtil.func_214993_b(lvt_4_1_, p_209509_1_.getPath(), p_209509_2_);
      } catch (InvalidPathException var5) {
         throw new ResourceLocationException("Invalid resource path: " + p_209509_1_, var5);
      }
   }

   private Path resolvePath(ResourceLocation p_209510_1_, String p_209510_2_) {
      if (p_209510_1_.getPath().contains("//")) {
         throw new ResourceLocationException("Invalid resource path: " + p_209510_1_);
      } else {
         Path lvt_3_1_ = this.resolvePathStructures(p_209510_1_, p_209510_2_);
         if (lvt_3_1_.startsWith(this.pathGenerated) && FileUtil.func_214995_a(lvt_3_1_) && FileUtil.func_214994_b(lvt_3_1_)) {
            return lvt_3_1_;
         } else {
            throw new ResourceLocationException("Invalid resource path: " + lvt_3_1_);
         }
      }
   }

   public void remove(ResourceLocation p_189941_1_) {
      this.templates.remove(p_189941_1_);
   }
}
