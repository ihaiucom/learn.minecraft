package net.minecraftforge.client.model;

import com.google.common.collect.Maps;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.logging.ModelLoaderErrorMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ModelLoader extends ModelBakery {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ResourceLocation, Exception> loadingExceptions = Maps.newHashMap();
   private IUnbakedModel missingModel = null;
   private boolean isLoading = false;
   private static ModelLoader instance;
   private static Set<ResourceLocation> specialModels = new HashSet();
   private static final Function<ResourceLocation, IUnbakedModel> DEFAULT_MODEL_GETTER = (rl) -> {
      return instance().getModelOrMissing(rl);
   };

   @Nullable
   public static ModelLoader instance() {
      return instance;
   }

   public boolean isLoading() {
      return this.isLoading;
   }

   public ModelLoader(IResourceManager manager, BlockColors colours, IProfiler profiler, int p_i226056_4_) {
      super(manager, colours, false);
      instance = this;
      this.processLoading(profiler, p_i226056_4_);
   }

   public static void addSpecialModel(ResourceLocation rl) {
      specialModels.add(rl);
   }

   public Set<ResourceLocation> getSpecialModels() {
      return specialModels;
   }

   public static ModelResourceLocation getInventoryVariant(String s) {
      return s.contains("#") ? new ModelResourceLocation(s) : new ModelResourceLocation(s, "inventory");
   }

   protected ResourceLocation getModelLocation(ResourceLocation model) {
      return new ResourceLocation(model.getNamespace(), model.getPath() + ".json");
   }

   protected IUnbakedModel getMissingModel() {
      if (this.missingModel == null) {
         try {
            this.missingModel = this.getUnbakedModel(MODEL_MISSING);
         } catch (Exception var2) {
            throw new RuntimeException("Missing the missing model, this should never happen");
         }
      }

      return this.missingModel;
   }

   public IUnbakedModel getModelOrMissing(ResourceLocation location) {
      try {
         return this.getUnbakedModel(location);
      } catch (Exception var3) {
         return this.getMissingModel();
      }
   }

   public IUnbakedModel getModelOrLogError(ResourceLocation location, String error) {
      try {
         return this.getUnbakedModel(location);
      } catch (Exception var4) {
         LOGGER.error(error, var4);
         return this.getMissingModel();
      }
   }

   public void onPostBakeEvent(Map<ResourceLocation, IBakedModel> modelRegistry) {
      IBakedModel missingModel = (IBakedModel)modelRegistry.get(MODEL_MISSING);
      Iterator var3 = this.loadingExceptions.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<ResourceLocation, Exception> entry = (Entry)var3.next();
         if (entry.getKey() instanceof ModelResourceLocation) {
            LOGGER.debug(Logging.MODELLOADING, () -> {
               return new ModelLoaderErrorMessage((ModelResourceLocation)entry.getKey(), (Exception)entry.getValue());
            });
            ModelResourceLocation location = (ModelResourceLocation)entry.getKey();
            IBakedModel model = (IBakedModel)modelRegistry.get(location);
            if (model == null) {
               modelRegistry.put(location, missingModel);
            }
         }
      }

      this.loadingExceptions.clear();
      this.isLoading = false;
   }

   public static Function<Material, TextureAtlasSprite> defaultTextureGetter() {
      return Material::func_229314_c_;
   }

   public static Function<ResourceLocation, IUnbakedModel> defaultModelGetter() {
      return DEFAULT_MODEL_GETTER;
   }

   public static class ItemLoadingException extends ModelLoadingException {
      private final Exception normalException;
      private final Exception blockstateException;

      public ItemLoadingException(String message, Exception normalException, Exception blockstateException) {
         super(message);
         this.normalException = normalException;
         this.blockstateException = blockstateException;
      }
   }

   public static final class White {
      public static final ResourceLocation LOCATION = new ResourceLocation("white");
      private static TextureAtlasSprite instance = null;

      public static final TextureAtlasSprite instance() {
         if (instance == null) {
            instance = (new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, LOCATION)).func_229314_c_();
         }

         return instance;
      }
   }
}
