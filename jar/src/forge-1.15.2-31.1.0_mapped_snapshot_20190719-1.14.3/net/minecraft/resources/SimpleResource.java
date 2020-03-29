package net.minecraft.resources;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleResource implements IResource {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Executor RESOURCE_IO_EXECUTOR;
   private final String packName;
   private final ResourceLocation location;
   private final InputStream inputStream;
   private final InputStream metadataInputStream;
   @OnlyIn(Dist.CLIENT)
   private boolean wasMetadataRead;
   @OnlyIn(Dist.CLIENT)
   private JsonObject metadataJson;

   public SimpleResource(String p_i47904_1_, ResourceLocation p_i47904_2_, InputStream p_i47904_3_, @Nullable InputStream p_i47904_4_) {
      this.packName = p_i47904_1_;
      this.location = p_i47904_2_;
      this.inputStream = p_i47904_3_;
      this.metadataInputStream = p_i47904_4_;
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getLocation() {
      return this.location;
   }

   public InputStream getInputStream() {
      return this.inputStream;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasMetadata() {
      return this.metadataInputStream != null;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public <T> T getMetadata(IMetadataSectionSerializer<T> p_199028_1_) {
      if (!this.hasMetadata()) {
         return null;
      } else {
         if (this.metadataJson == null && !this.wasMetadataRead) {
            this.wasMetadataRead = true;
            BufferedReader lvt_2_1_ = null;

            try {
               lvt_2_1_ = new BufferedReader(new InputStreamReader(this.metadataInputStream, StandardCharsets.UTF_8));
               this.metadataJson = JSONUtils.fromJson((Reader)lvt_2_1_);
            } finally {
               IOUtils.closeQuietly(lvt_2_1_);
            }
         }

         if (this.metadataJson == null) {
            return null;
         } else {
            String lvt_2_2_ = p_199028_1_.getSectionName();
            return this.metadataJson.has(lvt_2_2_) ? p_199028_1_.deserialize(JSONUtils.getJsonObject(this.metadataJson, lvt_2_2_)) : null;
         }
      }
   }

   public String getPackName() {
      return this.packName;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof SimpleResource)) {
         return false;
      } else {
         SimpleResource lvt_2_1_ = (SimpleResource)p_equals_1_;
         if (this.location != null) {
            if (!this.location.equals(lvt_2_1_.location)) {
               return false;
            }
         } else if (lvt_2_1_.location != null) {
            return false;
         }

         if (this.packName != null) {
            if (!this.packName.equals(lvt_2_1_.packName)) {
               return false;
            }
         } else if (lvt_2_1_.packName != null) {
            return false;
         }

         return true;
      }
   }

   public int hashCode() {
      int lvt_1_1_ = this.packName != null ? this.packName.hashCode() : 0;
      lvt_1_1_ = 31 * lvt_1_1_ + (this.location != null ? this.location.hashCode() : 0);
      return lvt_1_1_;
   }

   public void close() throws IOException {
      this.inputStream.close();
      if (this.metadataInputStream != null) {
         this.metadataInputStream.close();
      }

   }

   static {
      RESOURCE_IO_EXECUTOR = Executors.newSingleThreadExecutor((new ThreadFactoryBuilder()).setDaemon(true).setNameFormat("Resource IO {0}").setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).build());
   }
}
