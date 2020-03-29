package net.minecraftforge.client.model.geometry;

import com.mojang.datafixers.util.Pair;
import java.util.Collection;
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
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;

public interface ISimpleModelGeometry<T extends ISimpleModelGeometry<T>> extends IModelGeometry<T> {
   default IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
      TextureAtlasSprite particle = (TextureAtlasSprite)spriteGetter.apply(owner.resolveTexture("particle"));
      IModelBuilder<?> builder = IModelBuilder.of(owner, overrides, particle);
      this.addQuads(owner, builder, bakery, spriteGetter, modelTransform, modelLocation);
      return builder.build();
   }

   void addQuads(IModelConfiguration var1, IModelBuilder<?> var2, ModelBakery var3, Function<Material, TextureAtlasSprite> var4, IModelTransform var5, ResourceLocation var6);

   Collection<Material> getTextures(IModelConfiguration var1, Function<ResourceLocation, IUnbakedModel> var2, Set<Pair<String, String>> var3);
}
