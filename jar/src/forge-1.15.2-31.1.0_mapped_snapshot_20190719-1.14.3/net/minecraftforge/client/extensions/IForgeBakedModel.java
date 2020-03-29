package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.IModelData;

public interface IForgeBakedModel {
   default IBakedModel getBakedModel() {
      return (IBakedModel)this;
   }

   @Nonnull
   default List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
      return this.getBakedModel().getQuads(state, side, rand);
   }

   default boolean isAmbientOcclusion(BlockState state) {
      return this.getBakedModel().isAmbientOcclusion();
   }

   default boolean doesHandlePerspectives() {
      return false;
   }

   default IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
      return ForgeHooksClient.handlePerspective(this.getBakedModel(), cameraTransformType, mat);
   }

   @Nonnull
   default IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
      return tileData;
   }

   default TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
      return this.getBakedModel().getParticleTexture();
   }
}
