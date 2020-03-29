package net.minecraftforge.client.model.geometry;

import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;

public interface IModelGeometry<T extends IModelGeometry<T>> {
   default Collection<? extends IModelGeometryPart> getParts() {
      return Collections.emptyList();
   }

   default Optional<? extends IModelGeometryPart> getPart(String name) {
      return Optional.empty();
   }

   IBakedModel bake(IModelConfiguration var1, ModelBakery var2, Function<Material, TextureAtlasSprite> var3, IModelTransform var4, ItemOverrideList var5, ResourceLocation var6);

   Collection<Material> getTextures(IModelConfiguration var1, Function<ResourceLocation, IUnbakedModel> var2, Set<Pair<String, String>> var3);
}
