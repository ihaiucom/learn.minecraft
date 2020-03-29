package net.minecraftforge.client.model.obj;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;

public class OBJLoader implements IModelLoader<OBJModel> {
   public static OBJLoader INSTANCE = new OBJLoader();
   private final Map<OBJModel.ModelSettings, OBJModel> modelCache = Maps.newHashMap();
   private final Map<ResourceLocation, MaterialLibrary> materialCache = Maps.newHashMap();
   private IResourceManager manager = Minecraft.getInstance().getResourceManager();

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.modelCache.clear();
      this.materialCache.clear();
      this.manager = resourceManager;
   }

   public OBJModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      if (!modelContents.has("model")) {
         throw new RuntimeException("OBJ Loader requires a 'model' key that points to a valid .OBJ model.");
      } else {
         String modelLocation = modelContents.get("model").getAsString();
         boolean detectCullableFaces = JSONUtils.getBoolean(modelContents, "detectCullableFaces", true);
         boolean diffuseLighting = JSONUtils.getBoolean(modelContents, "diffuseLighting", false);
         boolean flipV = JSONUtils.getBoolean(modelContents, "flip-v", false);
         boolean ambientToFullbright = JSONUtils.getBoolean(modelContents, "ambientToFullbright", true);
         String materialLibraryOverrideLocation = modelContents.has("materialLibraryOverride") ? JSONUtils.getString(modelContents, "materialLibraryOverride") : null;
         return this.loadModel(new OBJModel.ModelSettings(new ResourceLocation(modelLocation), detectCullableFaces, diffuseLighting, flipV, ambientToFullbright, materialLibraryOverrideLocation));
      }
   }

   public OBJModel loadModel(OBJModel.ModelSettings settings) {
      return (OBJModel)this.modelCache.computeIfAbsent(settings, (data) -> {
         IResource resource;
         try {
            resource = this.manager.getResource(settings.modelLocation);
         } catch (IOException var18) {
            throw new RuntimeException("Could not find OBJ model", var18);
         }

         try {
            LineReader rdr = new LineReader(resource);
            Throwable var5 = null;

            OBJModel var6;
            try {
               var6 = new OBJModel(rdr, settings);
            } catch (Throwable var17) {
               var5 = var17;
               throw var17;
            } finally {
               if (rdr != null) {
                  if (var5 != null) {
                     try {
                        rdr.close();
                     } catch (Throwable var16) {
                        var5.addSuppressed(var16);
                     }
                  } else {
                     rdr.close();
                  }
               }

            }

            return var6;
         } catch (Exception var20) {
            throw new RuntimeException("Could not read OBJ model", var20);
         }
      });
   }

   public MaterialLibrary loadMaterialLibrary(ResourceLocation materialLocation) {
      return (MaterialLibrary)this.materialCache.computeIfAbsent(materialLocation, (location) -> {
         IResource resource;
         try {
            resource = this.manager.getResource(location);
         } catch (IOException var17) {
            throw new RuntimeException("Could not find OBJ material library", var17);
         }

         try {
            LineReader rdr = new LineReader(resource);
            Throwable var4 = null;

            MaterialLibrary var5;
            try {
               var5 = new MaterialLibrary(rdr);
            } catch (Throwable var16) {
               var4 = var16;
               throw var16;
            } finally {
               if (rdr != null) {
                  if (var4 != null) {
                     try {
                        rdr.close();
                     } catch (Throwable var15) {
                        var4.addSuppressed(var15);
                     }
                  } else {
                     rdr.close();
                  }
               }

            }

            return var5;
         } catch (Exception var19) {
            throw new RuntimeException("Could not read OBJ material library", var19);
         }
      });
   }
}
