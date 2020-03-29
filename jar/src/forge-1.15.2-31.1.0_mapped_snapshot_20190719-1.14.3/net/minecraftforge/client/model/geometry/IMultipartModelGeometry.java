package net.minecraftforge.client.model.geometry;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
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

public interface IMultipartModelGeometry<T extends IMultipartModelGeometry<T>> extends ISimpleModelGeometry<T> {
   Collection<? extends IModelGeometryPart> getParts();

   Optional<? extends IModelGeometryPart> getPart(String var1);

   default void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation) {
      this.getParts().stream().filter((part) -> {
         return owner.getPartVisibility(part);
      }).forEach((part) -> {
         part.addQuads(owner, modelBuilder, bakery, spriteGetter, modelTransform, modelLocation);
      });
   }

   default Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
      Set<Material> combined = Sets.newHashSet();
      Iterator var5 = this.getParts().iterator();

      while(var5.hasNext()) {
         IModelGeometryPart part = (IModelGeometryPart)var5.next();
         combined.addAll(part.getTextures(owner, modelGetter, missingTextureErrors));
      }

      return combined;
   }
}
