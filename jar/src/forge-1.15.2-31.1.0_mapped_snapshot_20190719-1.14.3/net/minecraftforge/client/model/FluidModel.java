package net.minecraftforge.client.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.TRSRTransformer;
import net.minecraftforge.fluids.FluidAttributes;

public final class FluidModel implements IModelGeometry<FluidModel> {
   public static final FluidModel WATER;
   public static final FluidModel LAVA;
   private final Fluid fluid;

   public FluidModel(Fluid fluid) {
      this.fluid = fluid;
   }

   public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
      return (Collection)ForgeHooksClient.getFluidMaterials(this.fluid).collect(Collectors.toList());
   }

   public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
      FluidAttributes attrs = this.fluid.getAttributes();
      return new FluidModel.CachingBakedFluid(modelTransform.func_225615_b_(), PerspectiveMapWrapper.getTransforms(modelTransform), modelLocation, attrs.getColor(), (TextureAtlasSprite)spriteGetter.apply(ForgeHooksClient.getBlockMaterial(attrs.getStillTexture())), (TextureAtlasSprite)spriteGetter.apply(ForgeHooksClient.getBlockMaterial(attrs.getFlowingTexture())), Optional.ofNullable(attrs.getOverlayTexture()).map(ForgeHooksClient::getBlockMaterial).map(spriteGetter), attrs.isLighterThanAir(), (Optional)null);
   }

   static {
      WATER = new FluidModel(Fluids.WATER);
      LAVA = new FluidModel(Fluids.LAVA);
   }

   private static class BakedFluid implements IBakedModel {
      private static final int[] x = new int[]{0, 0, 1, 1};
      private static final int[] z = new int[]{0, 1, 1, 0};
      private static final float eps = 0.001F;
      protected final TransformationMatrix transformation;
      protected final ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> transforms;
      protected final ResourceLocation modelLocation;
      protected final int color;
      protected final TextureAtlasSprite still;
      protected final TextureAtlasSprite flowing;
      protected final Optional<TextureAtlasSprite> overlay;
      protected final boolean gas;
      protected final ImmutableMap<Direction, ImmutableList<BakedQuad>> faceQuads;

      public BakedFluid(TransformationMatrix transformation, ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> transforms, ResourceLocation modelLocation, int color, TextureAtlasSprite still, TextureAtlasSprite flowing, Optional<TextureAtlasSprite> overlay, boolean gas, boolean statePresent, int[] cornerRound, int flowRound, boolean[] sideOverlays) {
         this.transformation = transformation;
         this.transforms = transforms;
         this.modelLocation = modelLocation;
         this.color = color;
         this.still = still;
         this.flowing = flowing;
         this.overlay = overlay;
         this.gas = gas;
         this.faceQuads = this.buildQuads(statePresent, cornerRound, flowRound, sideOverlays);
      }

      private ImmutableMap<Direction, ImmutableList<BakedQuad>> buildQuads(boolean statePresent, int[] cornerRound, int flowRound, boolean[] sideOverlays) {
         EnumMap<Direction, ImmutableList<BakedQuad>> faceQuads = new EnumMap(Direction.class);
         Direction[] var6 = Direction.values();
         int var7 = var6.length;

         int i;
         for(i = 0; i < var7; ++i) {
            Direction side = var6[i];
            faceQuads.put(side, ImmutableList.of());
         }

         if (statePresent) {
            float[] y = new float[4];
            boolean fullVolume = true;

            float value;
            for(i = 0; i < 4; ++i) {
               value = (float)cornerRound[i] / 864.0F;
               if (value < 1.0F) {
                  fullVolume = false;
               }

               y[i] = this.gas ? 1.0F - value : value;
            }

            boolean isFlowing = flowRound > -1000;
            value = isFlowing ? (float)Math.toRadians((double)flowRound) : 0.0F;
            TextureAtlasSprite topSprite = isFlowing ? this.flowing : this.still;
            float scale = isFlowing ? 4.0F : 8.0F;
            float c = MathHelper.cos(value) * scale;
            float s = MathHelper.sin(value) * scale;
            Direction top = this.gas ? Direction.DOWN : Direction.UP;
            FluidModel.BakedFluid.VertexParameter uv = (ix) -> {
               return c * (float)(x[ix] * 2 - 1) + s * (float)(z[ix] * 2 - 1);
            };
            FluidModel.BakedFluid.VertexParameter topX = (ix) -> {
               return (float)x[ix];
            };
            FluidModel.BakedFluid.VertexParameter topY = (ix) -> {
               return y[ix];
            };
            FluidModel.BakedFluid.VertexParameter topZ = (ix) -> {
               return (float)z[ix];
            };
            FluidModel.BakedFluid.VertexParameter topU = (ix) -> {
               return 8.0F + uv.get(ix);
            };
            FluidModel.BakedFluid.VertexParameter topV = (ix) -> {
               return 8.0F + uv.get((ix + 1) % 4);
            };
            Builder<BakedQuad> builder = ImmutableList.builder();
            builder.add(this.buildQuad(top, topSprite, this.gas, false, topX, topY, topZ, topU, topV));
            if (!fullVolume) {
               builder.add(this.buildQuad(top, topSprite, !this.gas, true, topX, topY, topZ, topU, topV));
            }

            faceQuads.put(top, builder.build());
            Direction bottom = top.getOpposite();
            faceQuads.put(bottom, ImmutableList.of(this.buildQuad(bottom, this.still, this.gas, false, (ix) -> {
               return (float)z[ix];
            }, (ix) -> {
               return this.gas ? 1.0F : 0.0F;
            }, (ix) -> {
               return (float)x[ix];
            }, (ix) -> {
               return (float)(z[ix] * 16);
            }, (ix) -> {
               return (float)(x[ix] * 16);
            })));

            for(int i = 0; i < 4; ++i) {
               Direction side = Direction.byHorizontalIndex((5 - i) % 4);
               boolean useOverlay = this.overlay.isPresent() && sideOverlays[side.getHorizontalIndex()];
               FluidModel.BakedFluid.VertexParameter sideX = (j) -> {
                  return (float)x[(i + x[j]) % 4];
               };
               FluidModel.BakedFluid.VertexParameter sideY = (j) -> {
                  return z[j] == 0 ? (float)(this.gas ? 1 : 0) : y[(i + x[j]) % 4];
               };
               FluidModel.BakedFluid.VertexParameter sideZ = (j) -> {
                  return (float)z[(i + x[j]) % 4];
               };
               FluidModel.BakedFluid.VertexParameter sideU = (j) -> {
                  return (float)(x[j] * 8);
               };
               FluidModel.BakedFluid.VertexParameter sideV = (j) -> {
                  return (this.gas ? sideY.get(j) : 1.0F - sideY.get(j)) * 8.0F;
               };
               Builder<BakedQuad> builder = ImmutableList.builder();
               if (!useOverlay) {
                  builder.add(this.buildQuad(side, this.flowing, this.gas, true, sideX, sideY, sideZ, sideU, sideV));
               }

               builder.add(this.buildQuad(side, useOverlay ? (TextureAtlasSprite)this.overlay.get() : this.flowing, !this.gas, false, sideX, sideY, sideZ, sideU, sideV));
               faceQuads.put(side, builder.build());
            }
         } else {
            faceQuads.put(Direction.SOUTH, ImmutableList.of(this.buildQuad(Direction.UP, this.still, false, false, (ix) -> {
               return (float)z[ix];
            }, (ix) -> {
               return (float)x[ix];
            }, (ix) -> {
               return 0.0F;
            }, (ix) -> {
               return (float)(z[ix] * 16);
            }, (ix) -> {
               return (float)(x[ix] * 16);
            })));
         }

         return ImmutableMap.copyOf(faceQuads);
      }

      private BakedQuad buildQuad(Direction side, TextureAtlasSprite texture, boolean flip, boolean offset, FluidModel.BakedFluid.VertexParameter x, FluidModel.BakedFluid.VertexParameter y, FluidModel.BakedFluid.VertexParameter z, FluidModel.BakedFluid.VertexParameter u, FluidModel.BakedFluid.VertexParameter v) {
         BakedQuadBuilder builder = new BakedQuadBuilder(texture);
         builder.setQuadOrientation(side);
         builder.setQuadTint(0);
         boolean hasTransform = !this.transformation.isIdentity();
         IVertexConsumer consumer = hasTransform ? new TRSRTransformer(builder, this.transformation) : builder;

         for(int i = 0; i < 4; ++i) {
            int vertex = flip ? 3 - i : i;
            this.putVertex((IVertexConsumer)consumer, side, offset, x.get(vertex), y.get(vertex), z.get(vertex), texture.getInterpolatedU((double)u.get(vertex)), texture.getInterpolatedV((double)v.get(vertex)));
         }

         return builder.build();
      }

      private void putVertex(IVertexConsumer consumer, Direction side, boolean offset, float x, float y, float z, float u, float v) {
         VertexFormat format = DefaultVertexFormats.BLOCK;
         ImmutableList<VertexFormatElement> elements = format.func_227894_c_();

         for(int e = 0; e < elements.size(); ++e) {
            switch(((VertexFormatElement)elements.get(e)).getUsage()) {
            case POSITION:
               float dx = offset ? (float)side.getDirectionVec().getX() * 0.001F : 0.0F;
               float dy = offset ? (float)side.getDirectionVec().getY() * 0.001F : 0.0F;
               float dz = offset ? (float)side.getDirectionVec().getZ() * 0.001F : 0.0F;
               consumer.put(e, x - dx, y - dy, z - dz, 1.0F);
               break;
            case COLOR:
               float r = (float)(this.color >> 16 & 255) / 255.0F;
               float g = (float)(this.color >> 8 & 255) / 255.0F;
               float b = (float)(this.color & 255) / 255.0F;
               float a = (float)(this.color >> 24 & 255) / 255.0F;
               consumer.put(e, r, g, b, a);
               break;
            case NORMAL:
               float offX = (float)side.getXOffset();
               float offY = (float)side.getYOffset();
               float offZ = (float)side.getZOffset();
               consumer.put(e, offX, offY, offZ, 0.0F);
               break;
            case UV:
               if (((VertexFormatElement)elements.get(e)).getIndex() == 0) {
                  consumer.put(e, u, v, 0.0F, 1.0F);
                  break;
               }
            default:
               consumer.put(e);
            }
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
         return this.still;
      }

      public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
         return (List)(side == null ? ImmutableList.of() : (List)this.faceQuads.get(side));
      }

      public ItemOverrideList getOverrides() {
         return ItemOverrideList.EMPTY;
      }

      public boolean doesHandlePerspectives() {
         return true;
      }

      public IBakedModel handlePerspective(ItemCameraTransforms.TransformType type, MatrixStack mat) {
         return PerspectiveMapWrapper.handlePerspective(this, (ImmutableMap)this.transforms, type, mat);
      }

      private interface VertexParameter {
         float get(int var1);
      }
   }

   private static final class CachingBakedFluid extends FluidModel.BakedFluid {
      private final LoadingCache<Long, FluidModel.BakedFluid> modelCache = CacheBuilder.newBuilder().maximumSize(200L).build(new CacheLoader<Long, FluidModel.BakedFluid>() {
         public FluidModel.BakedFluid load(Long key) {
            boolean statePresent = (key & 1L) != 0L;
            key = key >>> 1;
            int[] cornerRound = new int[4];

            int flowRound;
            for(flowRound = 0; flowRound < 4; ++flowRound) {
               cornerRound[flowRound] = (int)(key & 1023L);
               key = key >>> 10;
            }

            flowRound = (int)(key & 2047L) - 1024;
            key = key >>> 11;
            boolean[] overlaySides = new boolean[4];

            for(int i = 0; i < 4; ++i) {
               overlaySides[i] = (key & 1L) != 0L;
               key = key >>> 1;
            }

            return new FluidModel.BakedFluid(CachingBakedFluid.this.transformation, CachingBakedFluid.this.transforms, CachingBakedFluid.this.modelLocation, CachingBakedFluid.this.color, CachingBakedFluid.this.still, CachingBakedFluid.this.flowing, CachingBakedFluid.this.overlay, CachingBakedFluid.this.gas, statePresent, cornerRound, flowRound, overlaySides);
         }
      });

      public CachingBakedFluid(TransformationMatrix transformation, ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> transforms, ResourceLocation modelLocation, int color, TextureAtlasSprite still, TextureAtlasSprite flowing, Optional<TextureAtlasSprite> overlay, boolean gas, Optional<IModelData> stateOption) {
         super(transformation, transforms, modelLocation, color, still, flowing, overlay, gas, stateOption.isPresent(), getCorners(stateOption), getFlow(stateOption), getOverlay(stateOption));
      }

      private static int[] getCorners(Optional<IModelData> stateOption) {
         int[] cornerRound = new int[]{0, 0, 0, 0};
         if (stateOption.isPresent()) {
            IModelData state = (IModelData)stateOption.get();

            for(int i = 0; i < 4; ++i) {
               Float level = null;
               cornerRound[i] = Math.round((level == null ? 0.8888889F : level) * 864.0F);
            }
         }

         return cornerRound;
      }

      private static int getFlow(Optional<IModelData> stateOption) {
         Float flow = -1000.0F;
         if (stateOption.isPresent()) {
            flow = null;
            if (flow == null) {
               flow = -1000.0F;
            }
         }

         int flowRound = (int)Math.round(Math.toDegrees((double)flow));
         flowRound = MathHelper.clamp(flowRound, -1000, 1000);
         return flowRound;
      }

      private static boolean[] getOverlay(Optional<IModelData> stateOption) {
         boolean[] overlaySides = new boolean[4];
         if (stateOption.isPresent()) {
            IModelData state = (IModelData)stateOption.get();

            for(int i = 0; i < 4; ++i) {
               Boolean overlay = null;
               if (overlay != null) {
                  overlaySides[i] = overlay;
               }
            }
         }

         return overlaySides;
      }

      public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData modelData) {
         if (side == null) {
            return super.getQuads(state, side, rand);
         } else {
            Optional<IModelData> exState = Optional.of(modelData);
            int[] cornerRound = getCorners(exState);
            int flowRound = getFlow(exState);
            boolean[] overlaySides = getOverlay(exState);
            long key = 0L;

            int i;
            for(i = 3; i >= 0; --i) {
               key <<= 1;
               key |= overlaySides[i] ? 1L : 0L;
            }

            key <<= 11;
            key |= (long)(flowRound + 1024);

            for(i = 3; i >= 0; --i) {
               key <<= 10;
               key |= (long)cornerRound[i];
            }

            key <<= 1;
            key |= 1L;
            return ((FluidModel.BakedFluid)this.modelCache.getUnchecked(key)).getQuads(state, side, rand);
         }
      }
   }
}
