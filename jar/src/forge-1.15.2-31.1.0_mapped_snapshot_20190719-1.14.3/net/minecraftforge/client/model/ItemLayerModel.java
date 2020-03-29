package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
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
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.TRSRTransformer;

public final class ItemLayerModel implements IModelGeometry<ItemLayerModel> {
   public static final ItemLayerModel INSTANCE = new ItemLayerModel(ImmutableList.of());
   private static final Direction[] HORIZONTALS;
   private static final Direction[] VERTICALS;
   private ImmutableList<Material> textures;

   public ItemLayerModel(ImmutableList<Material> textures) {
      this.textures = textures;
   }

   public ItemLayerModel() {
      this.textures = null;
   }

   private static ImmutableList<Material> getTextures(IModelConfiguration model) {
      Builder<Material> builder = ImmutableList.builder();

      for(int i = 0; model.isTexturePresent("layer" + i); ++i) {
         builder.add(model.resolveTexture("layer" + i));
      }

      return builder.build();
   }

   public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
      TransformationMatrix transform = modelTransform.func_225615_b_();
      ImmutableList<BakedQuad> quads = getQuadsForSprites(this.textures, transform, spriteGetter);
      TextureAtlasSprite particle = (TextureAtlasSprite)spriteGetter.apply(owner.isTexturePresent("particle") ? owner.resolveTexture("particle") : (Material)this.textures.get(0));
      ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> map = PerspectiveMapWrapper.getTransforms(modelTransform);
      return new BakedItemModel(quads, particle, map, overrides, transform.isIdentity(), owner.isSideLit());
   }

   public static ImmutableList<BakedQuad> getQuadsForSprites(List<Material> textures, TransformationMatrix transform, Function<Material, TextureAtlasSprite> spriteGetter) {
      Builder<BakedQuad> builder = ImmutableList.builder();

      for(int i = 0; i < textures.size(); ++i) {
         TextureAtlasSprite tas = (TextureAtlasSprite)spriteGetter.apply(textures.get(i));
         builder.addAll(getQuadsForSprite(i, tas, transform));
      }

      return builder.build();
   }

   public static ImmutableList<BakedQuad> getQuadsForSprite(int tint, TextureAtlasSprite sprite, TransformationMatrix transform) {
      Builder<BakedQuad> builder = ImmutableList.builder();
      int uMax = sprite.getWidth();
      int vMax = sprite.getHeight();
      ItemLayerModel.FaceData faceData = new ItemLayerModel.FaceData(uMax, vMax);
      boolean translucent = false;

      int u;
      int vStart;
      for(int f = 0; f < sprite.getFrameCount(); ++f) {
         boolean[] ptv = new boolean[uMax];
         Arrays.fill(ptv, true);

         int v;
         for(v = 0; v < vMax; ++v) {
            boolean ptu = true;

            for(u = 0; u < uMax; ++u) {
               vStart = sprite.getPixelRGBA(f, u, v) >> 24 & 255;
               boolean t = (float)vStart / 255.0F <= 0.1F;
               if (!t && vStart < 255) {
                  translucent = true;
               }

               if (ptu && !t) {
                  faceData.set(Direction.WEST, u, v);
               }

               if (!ptu && t) {
                  faceData.set(Direction.EAST, u - 1, v);
               }

               if (ptv[u] && !t) {
                  faceData.set(Direction.UP, u, v);
               }

               if (!ptv[u] && t) {
                  faceData.set(Direction.DOWN, u, v - 1);
               }

               ptu = t;
               ptv[u] = t;
            }

            if (!ptu) {
               faceData.set(Direction.EAST, uMax - 1, v);
            }
         }

         for(v = 0; v < uMax; ++v) {
            if (!ptv[v]) {
               faceData.set(Direction.DOWN, v, vMax - 1);
            }
         }
      }

      Direction[] var19 = HORIZONTALS;
      int var20 = var19.length;

      boolean building;
      int v;
      boolean face;
      int off;
      int var21;
      Direction facing;
      int vEnd;
      for(var21 = 0; var21 < var20; ++var21) {
         facing = var19[var21];

         for(u = 0; u < vMax; ++u) {
            vStart = 0;
            vEnd = uMax;
            building = false;

            for(v = 0; v < uMax; ++v) {
               face = faceData.get(facing, v, u);
               if (!translucent) {
                  if (face) {
                     if (!building) {
                        building = true;
                        vStart = v;
                     }

                     vEnd = v + 1;
                  }
               } else if (building && !face) {
                  off = facing == Direction.DOWN ? 1 : 0;
                  builder.add(buildSideQuad(transform, facing, tint, sprite, vStart, u + off, v - vStart));
                  building = false;
               } else if (!building && face) {
                  building = true;
                  vStart = v;
               }
            }

            if (building) {
               v = facing == Direction.DOWN ? 1 : 0;
               builder.add(buildSideQuad(transform, facing, tint, sprite, vStart, u + v, vEnd - vStart));
            }
         }
      }

      var19 = VERTICALS;
      var20 = var19.length;

      for(var21 = 0; var21 < var20; ++var21) {
         facing = var19[var21];

         for(u = 0; u < uMax; ++u) {
            vStart = 0;
            vEnd = vMax;
            building = false;

            for(v = 0; v < vMax; ++v) {
               face = faceData.get(facing, u, v);
               if (!translucent) {
                  if (face) {
                     if (!building) {
                        building = true;
                        vStart = v;
                     }

                     vEnd = v + 1;
                  }
               } else if (building && !face) {
                  off = facing == Direction.EAST ? 1 : 0;
                  builder.add(buildSideQuad(transform, facing, tint, sprite, u + off, vStart, v - vStart));
                  building = false;
               } else if (!building && face) {
                  building = true;
                  vStart = v;
               }
            }

            if (building) {
               v = facing == Direction.EAST ? 1 : 0;
               builder.add(buildSideQuad(transform, facing, tint, sprite, u + v, vStart, vEnd - vStart));
            }
         }
      }

      builder.add(buildQuad(transform, Direction.NORTH, sprite, tint, 0.0F, 0.0F, 0.46875F, sprite.getMinU(), sprite.getMaxV(), 0.0F, 1.0F, 0.46875F, sprite.getMinU(), sprite.getMinV(), 1.0F, 1.0F, 0.46875F, sprite.getMaxU(), sprite.getMinV(), 1.0F, 0.0F, 0.46875F, sprite.getMaxU(), sprite.getMaxV()));
      builder.add(buildQuad(transform, Direction.SOUTH, sprite, tint, 0.0F, 0.0F, 0.53125F, sprite.getMinU(), sprite.getMaxV(), 1.0F, 0.0F, 0.53125F, sprite.getMaxU(), sprite.getMaxV(), 1.0F, 1.0F, 0.53125F, sprite.getMaxU(), sprite.getMinV(), 0.0F, 1.0F, 0.53125F, sprite.getMinU(), sprite.getMinV()));
      return builder.build();
   }

   public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
      this.textures = getTextures(owner);
      return this.textures;
   }

   private static BakedQuad buildSideQuad(TransformationMatrix transform, Direction side, int tint, TextureAtlasSprite sprite, int u, int v, int size) {
      float eps = 0.01F;
      int width = sprite.getWidth();
      int height = sprite.getHeight();
      float x0 = (float)u / (float)width;
      float y0 = (float)v / (float)height;
      float x1 = x0;
      float y1 = y0;
      float z0 = 0.46875F;
      float z1 = 0.53125F;
      switch(side) {
      case WEST:
         z0 = 0.53125F;
         z1 = 0.46875F;
      case EAST:
         y1 = (float)(v + size) / (float)height;
         break;
      case DOWN:
         z0 = 0.53125F;
         z1 = 0.46875F;
      case UP:
         x1 = (float)(u + size) / (float)width;
         break;
      default:
         throw new IllegalArgumentException("can't handle z-oriented side");
      }

      float dx = (float)side.getDirectionVec().getX() * 0.01F / (float)width;
      float dy = (float)side.getDirectionVec().getY() * 0.01F / (float)height;
      float u0 = 16.0F * (x0 - dx);
      float u1 = 16.0F * (x1 - dx);
      float v0 = 16.0F * (1.0F - y0 - dy);
      float v1 = 16.0F * (1.0F - y1 - dy);
      return buildQuad(transform, remap(side), sprite, tint, x0, y0, z0, sprite.getInterpolatedU((double)u0), sprite.getInterpolatedV((double)v0), x1, y1, z0, sprite.getInterpolatedU((double)u1), sprite.getInterpolatedV((double)v1), x1, y1, z1, sprite.getInterpolatedU((double)u1), sprite.getInterpolatedV((double)v1), x0, y0, z1, sprite.getInterpolatedU((double)u0), sprite.getInterpolatedV((double)v0));
   }

   private static Direction remap(Direction side) {
      return side.getAxis() == Direction.Axis.Y ? side.getOpposite() : side;
   }

   private static BakedQuad buildQuad(TransformationMatrix transform, Direction side, TextureAtlasSprite sprite, int tint, float x0, float y0, float z0, float u0, float v0, float x1, float y1, float z1, float u1, float v1, float x2, float y2, float z2, float u2, float v2, float x3, float y3, float z3, float u3, float v3) {
      BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
      builder.setQuadTint(tint);
      builder.setQuadOrientation(side);
      boolean hasTransform = !transform.isIdentity();
      IVertexConsumer consumer = hasTransform ? new TRSRTransformer(builder, transform) : builder;
      putVertex((IVertexConsumer)consumer, side, x0, y0, z0, u0, v0);
      putVertex((IVertexConsumer)consumer, side, x1, y1, z1, u1, v1);
      putVertex((IVertexConsumer)consumer, side, x2, y2, z2, u2, v2);
      putVertex((IVertexConsumer)consumer, side, x3, y3, z3, u3, v3);
      return builder.build();
   }

   private static void putVertex(IVertexConsumer consumer, Direction side, float x, float y, float z, float u, float v) {
      VertexFormat format = consumer.getVertexFormat();

      for(int e = 0; e < format.func_227894_c_().size(); ++e) {
         switch(((VertexFormatElement)format.func_227894_c_().get(e)).getUsage()) {
         case POSITION:
            consumer.put(e, x, y, z, 1.0F);
            break;
         case COLOR:
            consumer.put(e, 1.0F, 1.0F, 1.0F, 1.0F);
            break;
         case NORMAL:
            float offX = (float)side.getXOffset();
            float offY = (float)side.getYOffset();
            float offZ = (float)side.getZOffset();
            consumer.put(e, offX, offY, offZ, 0.0F);
            break;
         case UV:
            if (((VertexFormatElement)format.func_227894_c_().get(e)).getIndex() == 0) {
               consumer.put(e, u, v, 0.0F, 1.0F);
               break;
            }
         default:
            consumer.put(e);
         }
      }

   }

   static {
      HORIZONTALS = new Direction[]{Direction.UP, Direction.DOWN};
      VERTICALS = new Direction[]{Direction.WEST, Direction.EAST};
   }

   private static class FaceData {
      private final EnumMap<Direction, BitSet> data = new EnumMap(Direction.class);
      private final int vMax;

      FaceData(int uMax, int vMax) {
         this.vMax = vMax;
         this.data.put(Direction.WEST, new BitSet(uMax * vMax));
         this.data.put(Direction.EAST, new BitSet(uMax * vMax));
         this.data.put(Direction.UP, new BitSet(uMax * vMax));
         this.data.put(Direction.DOWN, new BitSet(uMax * vMax));
      }

      public void set(Direction facing, int u, int v) {
         ((BitSet)this.data.get(facing)).set(this.getIndex(u, v));
      }

      public boolean get(Direction facing, int u, int v) {
         return ((BitSet)this.data.get(facing)).get(this.getIndex(u, v));
      }

      private int getIndex(int u, int v) {
         return v * this.vMax + u;
      }
   }
}
