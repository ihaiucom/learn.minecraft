package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.model.TransformationHelper;

public class PerspectiveMapWrapper implements IDynamicBakedModel {
   private final IBakedModel parent;
   private final ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> transforms;
   private final PerspectiveMapWrapper.OverrideListWrapper overrides;

   public PerspectiveMapWrapper(IBakedModel parent, ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> transforms) {
      this.overrides = new PerspectiveMapWrapper.OverrideListWrapper();
      this.parent = parent;
      this.transforms = transforms;
   }

   public PerspectiveMapWrapper(IBakedModel parent, IModelTransform state) {
      this(parent, getTransforms(state));
   }

   public static ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> getTransforms(IModelTransform state) {
      EnumMap<ItemCameraTransforms.TransformType, TransformationMatrix> map = new EnumMap(ItemCameraTransforms.TransformType.class);
      ItemCameraTransforms.TransformType[] var2 = ItemCameraTransforms.TransformType.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ItemCameraTransforms.TransformType type = var2[var4];
         TransformationMatrix tr = state.getPartTransformation(type);
         if (!tr.isIdentity()) {
            map.put(type, tr);
         }
      }

      return ImmutableMap.copyOf(map);
   }

   public static ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> getTransforms(ItemCameraTransforms transforms) {
      EnumMap<ItemCameraTransforms.TransformType, TransformationMatrix> map = new EnumMap(ItemCameraTransforms.TransformType.class);
      ItemCameraTransforms.TransformType[] var2 = ItemCameraTransforms.TransformType.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ItemCameraTransforms.TransformType type = var2[var4];
         if (transforms.hasCustomTransform(type)) {
            map.put(type, TransformationHelper.toTransformation(transforms.getTransform(type)));
         }
      }

      return ImmutableMap.copyOf(map);
   }

   public static ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> getTransformsWithFallback(IModelTransform state, ItemCameraTransforms transforms) {
      EnumMap<ItemCameraTransforms.TransformType, TransformationMatrix> map = new EnumMap(ItemCameraTransforms.TransformType.class);
      ItemCameraTransforms.TransformType[] var3 = ItemCameraTransforms.TransformType.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ItemCameraTransforms.TransformType type = var3[var5];
         TransformationMatrix tr = state.getPartTransformation(type);
         if (!tr.isIdentity()) {
            map.put(type, tr);
         } else if (transforms.hasCustomTransform(type)) {
            map.put(type, TransformationHelper.toTransformation(transforms.getTransform(type)));
         }
      }

      return ImmutableMap.copyOf(map);
   }

   public static IBakedModel handlePerspective(IBakedModel model, ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> transforms, ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
      TransformationMatrix tr = (TransformationMatrix)transforms.getOrDefault(cameraTransformType, TransformationMatrix.func_227983_a_());
      if (!tr.isIdentity()) {
         tr.push(mat);
      }

      return model;
   }

   public static IBakedModel handlePerspective(IBakedModel model, IModelTransform state, ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
      TransformationMatrix tr = state.getPartTransformation(cameraTransformType);
      if (!tr.isIdentity()) {
         tr.push(mat);
      }

      return model;
   }

   public boolean isAmbientOcclusion() {
      return this.parent.isAmbientOcclusion();
   }

   public boolean isAmbientOcclusion(BlockState state) {
      return this.parent.isAmbientOcclusion(state);
   }

   public boolean isGui3d() {
      return this.parent.isGui3d();
   }

   public boolean func_230044_c_() {
      return this.parent.func_230044_c_();
   }

   public boolean isBuiltInRenderer() {
      return this.parent.isBuiltInRenderer();
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.parent.getParticleTexture();
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.parent.getItemCameraTransforms();
   }

   public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData extraData) {
      return this.parent.getQuads(state, side, rand, extraData);
   }

   public ItemOverrideList getOverrides() {
      return this.overrides;
   }

   public boolean doesHandlePerspectives() {
      return true;
   }

   public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
      return handlePerspective(this, (ImmutableMap)this.transforms, cameraTransformType, mat);
   }

   private class OverrideListWrapper extends ItemOverrideList {
      public OverrideListWrapper() {
      }

      @Nullable
      public IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) {
         model = PerspectiveMapWrapper.this.parent.getOverrides().getModelWithOverrides(PerspectiveMapWrapper.this.parent, stack, worldIn, entityIn);
         return new PerspectiveMapWrapper(model, PerspectiveMapWrapper.this.transforms);
      }

      public ImmutableList<ItemOverride> getOverrides() {
         return PerspectiveMapWrapper.this.parent.getOverrides().getOverrides();
      }
   }
}
