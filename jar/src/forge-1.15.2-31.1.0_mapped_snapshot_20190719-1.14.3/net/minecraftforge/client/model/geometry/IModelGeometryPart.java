package net.minecraftforge.client.model.geometry;

import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;

public interface IModelGeometryPart {
   String name();

   void addQuads(IModelConfiguration var1, IModelBuilder<?> var2, ModelBakery var3, Function<Material, TextureAtlasSprite> var4, IModelTransform var5, ResourceLocation var6);

   default Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
      return Collections.emptyList();
   }
}
