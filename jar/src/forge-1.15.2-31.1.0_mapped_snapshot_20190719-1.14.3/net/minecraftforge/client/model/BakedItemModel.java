package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

public class BakedItemModel implements IBakedModel {
   protected final ImmutableList<BakedQuad> quads;
   protected final TextureAtlasSprite particle;
   protected final ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> transforms;
   protected final ItemOverrideList overrides;
   protected final IBakedModel guiModel;
   protected final boolean isSideLit;

   public BakedItemModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> transforms, ItemOverrideList overrides, boolean untransformed, boolean isSideLit) {
      this.quads = quads;
      this.particle = particle;
      this.transforms = transforms;
      this.overrides = overrides;
      this.isSideLit = isSideLit;
      this.guiModel = untransformed && hasGuiIdentity(transforms) ? new BakedItemModel.BakedGuiItemModel(this) : null;
   }

   private static boolean hasGuiIdentity(ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> transforms) {
      TransformationMatrix guiTransform = (TransformationMatrix)transforms.get(ItemCameraTransforms.TransformType.GUI);
      return guiTransform == null || guiTransform.isIdentity();
   }

   public boolean isAmbientOcclusion() {
      return true;
   }

   public boolean isGui3d() {
      return false;
   }

   public boolean func_230044_c_() {
      return this.isSideLit;
   }

   public boolean isBuiltInRenderer() {
      return false;
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.particle;
   }

   public ItemOverrideList getOverrides() {
      return this.overrides;
   }

   public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
      return side == null ? this.quads : ImmutableList.of();
   }

   public IBakedModel handlePerspective(ItemCameraTransforms.TransformType type, MatrixStack mat) {
      return type == ItemCameraTransforms.TransformType.GUI && this.guiModel != null ? this.guiModel.handlePerspective(type, mat) : PerspectiveMapWrapper.handlePerspective(this, (ImmutableMap)this.transforms, type, mat);
   }

   public static class BakedGuiItemModel<T extends BakedItemModel> extends BakedModelWrapper<T> {
      private final ImmutableList<BakedQuad> quads;

      public BakedGuiItemModel(T originalModel) {
         super(originalModel);
         Builder<BakedQuad> builder = ImmutableList.builder();
         UnmodifiableIterator var3 = originalModel.quads.iterator();

         while(var3.hasNext()) {
            BakedQuad quad = (BakedQuad)var3.next();
            if (quad.getFace() == Direction.SOUTH) {
               builder.add(quad);
            }
         }

         this.quads = builder.build();
      }

      public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
         return side == null ? this.quads : ImmutableList.of();
      }

      public boolean doesHandlePerspectives() {
         return true;
      }

      public IBakedModel handlePerspective(ItemCameraTransforms.TransformType type, MatrixStack mat) {
         return type == ItemCameraTransforms.TransformType.GUI ? PerspectiveMapWrapper.handlePerspective(this, (ImmutableMap)((BakedItemModel)this.originalModel).transforms, type, mat) : ((BakedItemModel)this.originalModel).handlePerspective(type, mat);
      }
   }
}
