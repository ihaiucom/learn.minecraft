package net.minecraftforge.client.event;

import java.util.Map;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.Event;

public class ModelBakeEvent extends Event {
   private final ModelManager modelManager;
   private final Map<ResourceLocation, IBakedModel> modelRegistry;
   private final ModelLoader modelLoader;

   public ModelBakeEvent(ModelManager modelManager, Map<ResourceLocation, IBakedModel> modelRegistry, ModelLoader modelLoader) {
      this.modelManager = modelManager;
      this.modelRegistry = modelRegistry;
      this.modelLoader = modelLoader;
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public Map<ResourceLocation, IBakedModel> getModelRegistry() {
      return this.modelRegistry;
   }

   public ModelLoader getModelLoader() {
      return this.modelLoader;
   }
}
