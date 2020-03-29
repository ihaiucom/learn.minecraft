package net.minecraft.client.renderer.model;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.IForgeBakedModel;

@OnlyIn(Dist.CLIENT)
public interface IBakedModel extends IForgeBakedModel {
   /** @deprecated */
   @Deprecated
   List<BakedQuad> getQuads(@Nullable BlockState var1, @Nullable Direction var2, Random var3);

   boolean isAmbientOcclusion();

   boolean isGui3d();

   boolean func_230044_c_();

   boolean isBuiltInRenderer();

   /** @deprecated */
   @Deprecated
   TextureAtlasSprite getParticleTexture();

   /** @deprecated */
   @Deprecated
   default ItemCameraTransforms getItemCameraTransforms() {
      return ItemCameraTransforms.DEFAULT;
   }

   ItemOverrideList getOverrides();
}
