package net.minecraftforge.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.data.IModelData;

public abstract class BakedModelWrapper<T extends IBakedModel> implements IBakedModel {
   protected final T originalModel;

   public BakedModelWrapper(T originalModel) {
      this.originalModel = originalModel;
   }

   public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
      return this.originalModel.getQuads(state, side, rand);
   }

   public boolean isAmbientOcclusion() {
      return this.originalModel.isAmbientOcclusion();
   }

   public boolean isAmbientOcclusion(BlockState state) {
      return this.originalModel.isAmbientOcclusion(state);
   }

   public boolean isGui3d() {
      return this.originalModel.isGui3d();
   }

   public boolean func_230044_c_() {
      return this.originalModel.func_230044_c_();
   }

   public boolean isBuiltInRenderer() {
      return this.originalModel.isBuiltInRenderer();
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.originalModel.getParticleTexture();
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.originalModel.getItemCameraTransforms();
   }

   public ItemOverrideList getOverrides() {
      return this.originalModel.getOverrides();
   }

   public boolean doesHandlePerspectives() {
      return this.originalModel.doesHandlePerspectives();
   }

   public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
      return this.originalModel.handlePerspective(cameraTransformType, mat);
   }

   public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
      return this.originalModel.getParticleTexture(data);
   }

   @Nonnull
   public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
      return this.originalModel.getQuads(state, side, rand, extraData);
   }

   @Nonnull
   public IModelData getModelData(@Nonnull ILightReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
      return this.originalModel.getModelData(world, pos, state, tileData);
   }
}
