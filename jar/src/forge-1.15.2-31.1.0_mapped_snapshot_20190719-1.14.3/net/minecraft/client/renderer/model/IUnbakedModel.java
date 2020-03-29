package net.minecraft.client.renderer.model;

import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.IForgeUnbakedModel;

@OnlyIn(Dist.CLIENT)
public interface IUnbakedModel extends IForgeUnbakedModel {
   Collection<ResourceLocation> getDependencies();

   Collection<Material> func_225614_a_(Function<ResourceLocation, IUnbakedModel> var1, Set<Pair<String, String>> var2);

   @Nullable
   IBakedModel func_225613_a_(ModelBakery var1, Function<Material, TextureAtlasSprite> var2, IModelTransform var3, ResourceLocation var4);
}
