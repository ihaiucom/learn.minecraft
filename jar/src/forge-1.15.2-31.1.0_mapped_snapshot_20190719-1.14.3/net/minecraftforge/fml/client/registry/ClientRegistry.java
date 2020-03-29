package net.minecraftforge.fml.client.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;

public class ClientRegistry {
   private static Map<Class<? extends Entity>, ResourceLocation> entityShaderMap = new ConcurrentHashMap();

   public static synchronized <T extends TileEntity> void bindTileEntityRenderer(TileEntityType<T> tileEntityType, Function<? super TileEntityRendererDispatcher, ? extends TileEntityRenderer<? super T>> rendererFactory) {
      TileEntityRendererDispatcher.instance.setSpecialRendererInternal(tileEntityType, (TileEntityRenderer)rendererFactory.apply(TileEntityRendererDispatcher.instance));
   }

   public static synchronized void registerKeyBinding(KeyBinding key) {
      Minecraft.getInstance().gameSettings.keyBindings = (KeyBinding[])ArrayUtils.add(Minecraft.getInstance().gameSettings.keyBindings, key);
   }

   public static void registerEntityShader(Class<? extends Entity> entityClass, ResourceLocation shader) {
      entityShaderMap.put(entityClass, shader);
   }

   public static ResourceLocation getEntityShader(Class<? extends Entity> entityClass) {
      return (ResourceLocation)entityShaderMap.get(entityClass);
   }
}
