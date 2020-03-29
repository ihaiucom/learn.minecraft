package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class JsonReloadListener extends ReloadListener<Map<ResourceLocation, JsonObject>> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int JSON_EXTENSION_LENGTH = ".json".length();
   private final Gson gson;
   private final String folder;

   public JsonReloadListener(Gson p_i51536_1_, String p_i51536_2_) {
      this.gson = p_i51536_1_;
      this.folder = p_i51536_2_;
   }

   protected Map<ResourceLocation, JsonObject> prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
      Map<ResourceLocation, JsonObject> map = Maps.newHashMap();
      int i = this.folder.length() + 1;
      Iterator var5 = p_212854_1_.getAllResourceLocations(this.folder, (p_lambda$prepare$0_0_) -> {
         return p_lambda$prepare$0_0_.endsWith(".json");
      }).iterator();

      while(var5.hasNext()) {
         ResourceLocation resourcelocation = (ResourceLocation)var5.next();
         String s = resourcelocation.getPath();
         ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(i, s.length() - JSON_EXTENSION_LENGTH));

         try {
            IResource iresource = p_212854_1_.getResource(resourcelocation);
            Throwable var10 = null;

            try {
               InputStream inputstream = iresource.getInputStream();
               Throwable var12 = null;

               try {
                  Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                  Throwable var14 = null;

                  try {
                     JsonObject jsonobject = (JsonObject)JSONUtils.fromJson(this.gson, (Reader)reader, (Class)JsonObject.class);
                     if (jsonobject != null) {
                        JsonObject jsonobject1 = (JsonObject)map.put(resourcelocation1, jsonobject);
                        if (jsonobject1 != null) {
                           throw new IllegalStateException("Duplicate data file ignored with ID " + resourcelocation1);
                        }
                     } else {
                        LOGGER.error("Couldn't load data file {} from {} as it's null or empty", resourcelocation1, resourcelocation);
                     }
                  } catch (Throwable var62) {
                     var14 = var62;
                     throw var62;
                  } finally {
                     if (reader != null) {
                        if (var14 != null) {
                           try {
                              reader.close();
                           } catch (Throwable var61) {
                              var14.addSuppressed(var61);
                           }
                        } else {
                           reader.close();
                        }
                     }

                  }
               } catch (Throwable var64) {
                  var12 = var64;
                  throw var64;
               } finally {
                  if (inputstream != null) {
                     if (var12 != null) {
                        try {
                           inputstream.close();
                        } catch (Throwable var60) {
                           var12.addSuppressed(var60);
                        }
                     } else {
                        inputstream.close();
                     }
                  }

               }
            } catch (Throwable var66) {
               var10 = var66;
               throw var66;
            } finally {
               if (iresource != null) {
                  if (var10 != null) {
                     try {
                        iresource.close();
                     } catch (Throwable var59) {
                        var10.addSuppressed(var59);
                     }
                  } else {
                     iresource.close();
                  }
               }

            }
         } catch (IOException | JsonParseException | IllegalArgumentException var68) {
            LOGGER.error("Couldn't parse data file {} from {}", resourcelocation1, resourcelocation, var68);
         }
      }

      return map;
   }

   protected ResourceLocation getPreparedPath(ResourceLocation p_getPreparedPath_1_) {
      return new ResourceLocation(p_getPreparedPath_1_.getNamespace(), this.folder + "/" + p_getPreparedPath_1_.getPath() + ".json");
   }
}
