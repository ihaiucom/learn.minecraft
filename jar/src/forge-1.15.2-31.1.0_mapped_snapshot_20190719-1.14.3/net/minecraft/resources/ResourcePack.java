package net.minecraft.resources;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ResourcePack implements IResourcePack {
   private static final Logger LOGGER = LogManager.getLogger();
   public final File file;

   public ResourcePack(File p_i1287_1_) {
      this.file = p_i1287_1_;
   }

   private static String getFullPath(ResourcePackType p_195765_0_, ResourceLocation p_195765_1_) {
      return String.format("%s/%s/%s", p_195765_0_.getDirectoryName(), p_195765_1_.getNamespace(), p_195765_1_.getPath());
   }

   protected static String getRelativeString(File p_195767_0_, File p_195767_1_) {
      return p_195767_0_.toURI().relativize(p_195767_1_.toURI()).getPath();
   }

   public InputStream getResourceStream(ResourcePackType p_195761_1_, ResourceLocation p_195761_2_) throws IOException {
      return this.getInputStream(getFullPath(p_195761_1_, p_195761_2_));
   }

   public boolean resourceExists(ResourcePackType p_195764_1_, ResourceLocation p_195764_2_) {
      return this.resourceExists(getFullPath(p_195764_1_, p_195764_2_));
   }

   protected abstract InputStream getInputStream(String var1) throws IOException;

   @OnlyIn(Dist.CLIENT)
   public InputStream getRootResourceStream(String p_195763_1_) throws IOException {
      if (!p_195763_1_.contains("/") && !p_195763_1_.contains("\\")) {
         return this.getInputStream(p_195763_1_);
      } else {
         throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
      }
   }

   protected abstract boolean resourceExists(String var1);

   protected void onIgnoreNonLowercaseNamespace(String p_195769_1_) {
      LOGGER.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", p_195769_1_, this.file);
   }

   @Nullable
   public <T> T getMetadata(IMetadataSectionSerializer<T> p_195760_1_) throws IOException {
      InputStream lvt_2_1_ = this.getInputStream("pack.mcmeta");
      Throwable var3 = null;

      Object var4;
      try {
         var4 = getResourceMetadata(p_195760_1_, lvt_2_1_);
      } catch (Throwable var13) {
         var3 = var13;
         throw var13;
      } finally {
         if (lvt_2_1_ != null) {
            if (var3 != null) {
               try {
                  lvt_2_1_.close();
               } catch (Throwable var12) {
                  var3.addSuppressed(var12);
               }
            } else {
               lvt_2_1_.close();
            }
         }

      }

      return var4;
   }

   @Nullable
   public static <T> T getResourceMetadata(IMetadataSectionSerializer<T> p_195770_0_, InputStream p_195770_1_) {
      JsonObject lvt_2_3_;
      try {
         BufferedReader lvt_3_1_ = new BufferedReader(new InputStreamReader(p_195770_1_, StandardCharsets.UTF_8));
         Throwable var4 = null;

         try {
            lvt_2_3_ = JSONUtils.fromJson((Reader)lvt_3_1_);
         } catch (Throwable var16) {
            var4 = var16;
            throw var16;
         } finally {
            if (lvt_3_1_ != null) {
               if (var4 != null) {
                  try {
                     lvt_3_1_.close();
                  } catch (Throwable var14) {
                     var4.addSuppressed(var14);
                  }
               } else {
                  lvt_3_1_.close();
               }
            }

         }
      } catch (JsonParseException | IOException var18) {
         LOGGER.error("Couldn't load {} metadata", p_195770_0_.getSectionName(), var18);
         return null;
      }

      if (!lvt_2_3_.has(p_195770_0_.getSectionName())) {
         return null;
      } else {
         try {
            return p_195770_0_.deserialize(JSONUtils.getJsonObject(lvt_2_3_, p_195770_0_.getSectionName()));
         } catch (JsonParseException var15) {
            LOGGER.error("Couldn't load {} metadata", p_195770_0_.getSectionName(), var15);
            return null;
         }
      }
   }

   public String getName() {
      return this.file.getName();
   }
}
