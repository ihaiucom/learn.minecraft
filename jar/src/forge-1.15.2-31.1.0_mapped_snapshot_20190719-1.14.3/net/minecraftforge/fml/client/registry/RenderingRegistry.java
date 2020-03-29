package net.minecraftforge.fml.client.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public class RenderingRegistry {
   private static final RenderingRegistry INSTANCE = new RenderingRegistry();
   private final Map<EntityType<? extends Entity>, IRenderFactory<? extends Entity>> entityRenderers = new ConcurrentHashMap();

   public static <T extends Entity> void registerEntityRenderingHandler(EntityType<T> entityClass, IRenderFactory<? super T> renderFactory) {
      INSTANCE.entityRenderers.put(entityClass, renderFactory);
   }

   public static void loadEntityRenderers(EntityRendererManager manager) {
      INSTANCE.entityRenderers.forEach((key, value) -> {
         register(manager, key, value);
      });
      manager.validateRendererExistence();
   }

   private static <T extends Entity> void register(EntityRendererManager manager, EntityType<T> entityType, IRenderFactory<?> renderFactory) {
      manager.func_229087_a_(entityType, renderFactory.createRenderFor(manager));
   }
}
