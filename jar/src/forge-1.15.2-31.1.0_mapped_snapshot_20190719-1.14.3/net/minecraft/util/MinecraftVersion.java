package net.minecraft.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.bridge.game.GameVersion;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftVersion implements GameVersion {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String id;
   private final String name;
   private final boolean stable;
   private final int worldVersion;
   private final int protocolVersion;
   private final int packVersion;
   private final Date buildTime;
   private final String releaseTarget;

   public MinecraftVersion() {
      this.id = UUID.randomUUID().toString().replaceAll("-", "");
      this.name = "1.15.2";
      this.stable = true;
      this.worldVersion = 2230;
      this.protocolVersion = 578;
      this.packVersion = 5;
      this.buildTime = new Date();
      this.releaseTarget = "1.15.2";
   }

   protected MinecraftVersion(JsonObject p_i51407_1_) {
      this.id = JSONUtils.getString(p_i51407_1_, "id");
      this.name = JSONUtils.getString(p_i51407_1_, "name");
      this.releaseTarget = JSONUtils.getString(p_i51407_1_, "release_target");
      this.stable = JSONUtils.getBoolean(p_i51407_1_, "stable");
      this.worldVersion = JSONUtils.getInt(p_i51407_1_, "world_version");
      this.protocolVersion = JSONUtils.getInt(p_i51407_1_, "protocol_version");
      this.packVersion = JSONUtils.getInt(p_i51407_1_, "pack_version");
      this.buildTime = Date.from(ZonedDateTime.parse(JSONUtils.getString(p_i51407_1_, "build_time")).toInstant());
   }

   public static GameVersion load() {
      try {
         InputStream lvt_0_1_ = MinecraftVersion.class.getResourceAsStream("/version.json");
         Throwable var1 = null;

         MinecraftVersion var2;
         try {
            if (lvt_0_1_ != null) {
               InputStreamReader lvt_2_1_ = new InputStreamReader(lvt_0_1_);
               Throwable var3 = null;

               try {
                  Object var4;
                  try {
                     var4 = new MinecraftVersion(JSONUtils.fromJson((Reader)lvt_2_1_));
                     return (GameVersion)var4;
                  } catch (Throwable var30) {
                     var4 = var30;
                     var3 = var30;
                     throw var30;
                  }
               } finally {
                  if (lvt_2_1_ != null) {
                     if (var3 != null) {
                        try {
                           lvt_2_1_.close();
                        } catch (Throwable var29) {
                           var3.addSuppressed(var29);
                        }
                     } else {
                        lvt_2_1_.close();
                     }
                  }

               }
            }

            LOGGER.warn("Missing version information!");
            var2 = new MinecraftVersion();
         } catch (Throwable var32) {
            var1 = var32;
            throw var32;
         } finally {
            if (lvt_0_1_ != null) {
               if (var1 != null) {
                  try {
                     lvt_0_1_.close();
                  } catch (Throwable var28) {
                     var1.addSuppressed(var28);
                  }
               } else {
                  lvt_0_1_.close();
               }
            }

         }

         return var2;
      } catch (JsonParseException | IOException var34) {
         throw new IllegalStateException("Game version information is corrupt", var34);
      }
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getReleaseTarget() {
      return this.releaseTarget;
   }

   public int getWorldVersion() {
      return this.worldVersion;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }

   public int getPackVersion() {
      return this.packVersion;
   }

   public Date getBuildTime() {
      return this.buildTime;
   }

   public boolean isStable() {
      return this.stable;
   }
}
