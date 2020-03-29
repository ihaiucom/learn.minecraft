package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.common.ForgeConfig;

final class FancyMissingModel implements IUnbakedModel {
   private static final ResourceLocation font = new ResourceLocation("minecraft", "textures/font/ascii.png");
   private static final Material font2;
   private static final TransformationMatrix smallTransformation;
   private static final SimpleModelFontRenderer fontRenderer;
   private final IUnbakedModel missingModel;
   private final String message;

   public FancyMissingModel(IUnbakedModel missingModel, String message) {
      this.missingModel = missingModel;
      this.message = message;
   }

   public Collection<Material> func_225614_a_(Function<ResourceLocation, IUnbakedModel> p_225614_1_, Set<Pair<String, String>> p_225614_2_) {
      return ImmutableList.of(font2);
   }

   public Collection<ResourceLocation> getDependencies() {
      return Collections.emptyList();
   }

   @Nullable
   public IBakedModel func_225613_a_(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation) {
      IBakedModel bigMissing = this.missingModel.func_225613_a_(bakery, spriteGetter, modelTransform, modelLocation);
      ModelTransformComposition smallState = new ModelTransformComposition(modelTransform, new SimpleModelTransform(smallTransformation));
      IBakedModel smallMissing = this.missingModel.func_225613_a_(bakery, spriteGetter, smallState, modelLocation);
      return new FancyMissingModel.BakedModel(bigMissing, smallMissing, fontRenderer, this.message, (TextureAtlasSprite)spriteGetter.apply(font2));
   }

   static {
      font2 = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("minecraft", "font/ascii"));
      smallTransformation = (new TransformationMatrix((Vector3f)null, (Quaternion)null, new Vector3f(0.25F, 0.25F, 0.25F), (Quaternion)null)).blockCenterToCorner();
      fontRenderer = (SimpleModelFontRenderer)Util.make(() -> {
         float[] mv = new float[16];
         mv[8] = 0.0078125F;
         mv[1] = mv[6] = -mv[8];
         mv[15] = 1.0F;
         mv[3] = 1.0F;
         mv[3] = 1.0039062F;
         mv[3] = 0.0F;
         Matrix4f m = new Matrix4f(mv);
         return new SimpleModelFontRenderer(Minecraft.getInstance().gameSettings, font, Minecraft.getInstance().getTextureManager(), false, m) {
         };
      });
   }

   static final class BakedModel implements IBakedModel {
      private final SimpleModelFontRenderer fontRenderer;
      private final String message;
      private final TextureAtlasSprite fontTexture;
      private final IBakedModel missingModel;
      private final IBakedModel otherModel;
      private final boolean big;
      private ImmutableList<BakedQuad> quads;

      public BakedModel(IBakedModel bigMissing, IBakedModel smallMissing, SimpleModelFontRenderer fontRenderer, String message, TextureAtlasSprite fontTexture) {
         this.missingModel = bigMissing;
         this.otherModel = new FancyMissingModel.BakedModel(smallMissing, fontRenderer, message, fontTexture, this);
         this.big = true;
         this.fontRenderer = fontRenderer;
         this.message = message;
         this.fontTexture = fontTexture;
      }

      public BakedModel(IBakedModel smallMissing, SimpleModelFontRenderer fontRenderer, String message, TextureAtlasSprite fontTexture, FancyMissingModel.BakedModel big) {
         this.missingModel = smallMissing;
         this.otherModel = big;
         this.big = false;
         this.fontRenderer = fontRenderer;
         this.message = message;
         this.fontTexture = fontTexture;
      }

      public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
         if (side != null) {
            return this.missingModel.getQuads(state, side, rand);
         } else {
            if (this.quads == null) {
               this.fontRenderer.setSprite(this.fontTexture);
               this.fontRenderer.setFillBlanks(true);
               String[] lines = this.message.split("\\r?\\n");
               List<String> splitLines = Lists.newArrayList();

               int y;
               for(y = 0; y < lines.length; ++y) {
                  splitLines.addAll(this.fontRenderer.listFormattedStringToWidth(lines[y], 128));
               }

               for(y = 0; y < splitLines.size(); ++y) {
                  SimpleModelFontRenderer var10000 = this.fontRenderer;
                  String var10001 = (String)splitLines.get(y);
                  float var10003 = (float)y - (float)splitLines.size() / 2.0F;
                  this.fontRenderer.getClass();
                  var10000.drawString(var10001, 0.0F, var10003 * 9.0F + 64.0F, -16711681);
               }

               Builder<BakedQuad> builder = ImmutableList.builder();
               builder.addAll(this.missingModel.getQuads(state, side, rand));
               builder.addAll(this.fontRenderer.build());
               this.quads = builder.build();
            }

            return this.quads;
         }
      }

      public boolean isAmbientOcclusion() {
         return true;
      }

      public boolean isGui3d() {
         return false;
      }

      public boolean func_230044_c_() {
         return false;
      }

      public boolean isBuiltInRenderer() {
         return false;
      }

      public TextureAtlasSprite getParticleTexture() {
         return this.fontTexture;
      }

      public ItemOverrideList getOverrides() {
         return ItemOverrideList.EMPTY;
      }

      public boolean doesHandlePerspectives() {
         return true;
      }

      public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
         TransformationMatrix transform = TransformationMatrix.func_227983_a_();
         boolean big = true;
         switch(cameraTransformType) {
         case THIRD_PERSON_LEFT_HAND:
         case THIRD_PERSON_RIGHT_HAND:
         case HEAD:
         default:
            break;
         case FIRST_PERSON_LEFT_HAND:
            transform = new TransformationMatrix(new Vector3f(-0.62F, 0.5F, -0.5F), new Quaternion(1.0F, -1.0F, -1.0F, 1.0F), (Vector3f)null, (Quaternion)null);
            big = false;
            break;
         case FIRST_PERSON_RIGHT_HAND:
            transform = new TransformationMatrix(new Vector3f(-0.5F, 0.5F, -0.5F), new Quaternion(1.0F, 1.0F, 1.0F, 1.0F), (Vector3f)null, (Quaternion)null);
            big = false;
            break;
         case GUI:
            if ((Boolean)ForgeConfig.CLIENT.zoomInMissingModelTextInGui.get()) {
               transform = new TransformationMatrix((Vector3f)null, new Quaternion(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(4.0F, 4.0F, 4.0F), (Quaternion)null);
               big = false;
            } else {
               transform = new TransformationMatrix((Vector3f)null, new Quaternion(1.0F, 1.0F, 1.0F, 1.0F), (Vector3f)null, (Quaternion)null);
               big = true;
            }
            break;
         case FIXED:
            transform = new TransformationMatrix((Vector3f)null, new Quaternion(-1.0F, -1.0F, 1.0F, 1.0F), (Vector3f)null, (Quaternion)null);
         }

         mat.func_227866_c_().func_227870_a_().func_226595_a_(transform.func_227988_c_());
         return (IBakedModel)(big != this.big ? this.otherModel : this);
      }
   }
}
