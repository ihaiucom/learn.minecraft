package net.minecraftforge.client.model;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Material;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;

public interface IModelConfiguration {
   @Nullable
   IUnbakedModel getOwnerModel();

   String getModelName();

   boolean isTexturePresent(String var1);

   Material resolveTexture(String var1);

   boolean isShadedInGui();

   boolean isSideLit();

   boolean useSmoothLighting();

   /** @deprecated */
   @Deprecated
   ItemCameraTransforms getCameraTransforms();

   IModelTransform getCombinedTransform();

   default boolean getPartVisibility(IModelGeometryPart part, boolean fallback) {
      return fallback;
   }

   default boolean getPartVisibility(IModelGeometryPart part) {
      return this.getPartVisibility(part, true);
   }
}
