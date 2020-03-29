package net.minecraftforge.client.model;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.TRSRTransformer;

public final class ItemTextureQuadConverter {
   private ItemTextureQuadConverter() {
   }

   public static List<BakedQuad> convertTexture(TransformationMatrix transform, TextureAtlasSprite template, TextureAtlasSprite sprite, float z, Direction facing, int color, int tint) {
      List<BakedQuad> horizontal = convertTextureHorizontal(transform, template, sprite, z, facing, color, tint);
      List<BakedQuad> vertical = convertTextureVertical(transform, template, sprite, z, facing, color, tint);
      return horizontal.size() <= vertical.size() ? horizontal : vertical;
   }

   public static List<BakedQuad> convertTextureHorizontal(TransformationMatrix transform, TextureAtlasSprite template, TextureAtlasSprite sprite, float z, Direction facing, int color, int tint) {
      int w = template.getWidth();
      int h = template.getHeight();
      float wScale = 16.0F / (float)w;
      float hScale = 16.0F / (float)h;
      List<BakedQuad> quads = Lists.newArrayList();
      int start = -1;

      for(int y = 0; y < h; ++y) {
         for(int x = 0; x < w; ++x) {
            boolean isVisible = !template.isPixelTransparent(0, x, y);
            if (start < 0 && isVisible) {
               start = x;
            }

            if (start >= 0 && !isVisible) {
               int endY = y + 1;
               boolean sameRow = true;

               while(sameRow && endY < h) {
                  for(int i = 0; i < w; ++i) {
                     if (template.isPixelTransparent(0, i, y) != template.isPixelTransparent(0, i, endY)) {
                        sameRow = false;
                        break;
                     }
                  }

                  if (sameRow) {
                     ++endY;
                  }
               }

               quads.add(genQuad(transform, (float)start * wScale, (float)y * hScale, (float)x * wScale, (float)endY * hScale, z, sprite, facing, color, tint));
               if (endY - y > 1) {
                  y = endY - 1;
               }

               start = -1;
            }
         }
      }

      return quads;
   }

   public static List<BakedQuad> convertTextureVertical(TransformationMatrix transform, TextureAtlasSprite template, TextureAtlasSprite sprite, float z, Direction facing, int color, int tint) {
      int w = template.getWidth();
      int h = template.getHeight();
      float wScale = 16.0F / (float)w;
      float hScale = 16.0F / (float)h;
      List<BakedQuad> quads = Lists.newArrayList();
      int start = -1;

      for(int x = 0; x < w; ++x) {
         for(int y = 0; y < h; ++y) {
            boolean isVisible = !template.isPixelTransparent(0, x, y);
            if (start < 0 && isVisible) {
               start = y;
            }

            if (start >= 0 && !isVisible) {
               int endX = x + 1;
               boolean sameColumn = true;

               while(sameColumn && endX < w) {
                  for(int i = 0; i < h; ++i) {
                     if (template.isPixelTransparent(0, x, i) != template.isPixelTransparent(0, endX, i)) {
                        sameColumn = false;
                        break;
                     }
                  }

                  if (sameColumn) {
                     ++endX;
                  }
               }

               quads.add(genQuad(transform, (float)x * wScale, (float)start * hScale, (float)endX * wScale, (float)y * hScale, z, sprite, facing, color, tint));
               if (endX - x > 1) {
                  x = endX - 1;
               }

               start = -1;
            }
         }
      }

      return quads;
   }

   private static boolean isVisible(int color) {
      return (float)(color >> 24 & 255) / 255.0F > 0.1F;
   }

   public static BakedQuad genQuad(TransformationMatrix transform, float x1, float y1, float x2, float y2, float z, TextureAtlasSprite sprite, Direction facing, int color, int tint) {
      float u1 = sprite.getInterpolatedU((double)x1);
      float v1 = sprite.getInterpolatedV((double)y1);
      float u2 = sprite.getInterpolatedU((double)x2);
      float v2 = sprite.getInterpolatedV((double)y2);
      x1 /= 16.0F;
      y1 /= 16.0F;
      x2 /= 16.0F;
      y2 /= 16.0F;
      float tmp = y1;
      y1 = 1.0F - y2;
      y2 = 1.0F - tmp;
      return putQuad(transform, facing, sprite, color, tint, x1, y1, x2, y2, z, u1, v1, u2, v2);
   }

   private static BakedQuad putQuad(TransformationMatrix transform, Direction side, TextureAtlasSprite sprite, int color, int tint, float x1, float y1, float x2, float y2, float z, float u1, float v1, float u2, float v2) {
      BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
      builder.setQuadTint(tint);
      builder.setQuadOrientation(side);
      boolean hasTransform = !transform.isIdentity();
      IVertexConsumer consumer = hasTransform ? new TRSRTransformer(builder, transform) : builder;
      if (side == Direction.SOUTH) {
         putVertex((IVertexConsumer)consumer, side, x1, y1, z, u1, v2, color);
         putVertex((IVertexConsumer)consumer, side, x2, y1, z, u2, v2, color);
         putVertex((IVertexConsumer)consumer, side, x2, y2, z, u2, v1, color);
         putVertex((IVertexConsumer)consumer, side, x1, y2, z, u1, v1, color);
      } else {
         putVertex((IVertexConsumer)consumer, side, x1, y1, z, u1, v2, color);
         putVertex((IVertexConsumer)consumer, side, x1, y2, z, u1, v1, color);
         putVertex((IVertexConsumer)consumer, side, x2, y2, z, u2, v1, color);
         putVertex((IVertexConsumer)consumer, side, x2, y1, z, u2, v2, color);
      }

      return builder.build();
   }

   private static void putVertex(IVertexConsumer consumer, Direction side, float x, float y, float z, float u, float v, int color) {
      VertexFormat format = consumer.getVertexFormat();

      for(int e = 0; e < format.func_227894_c_().size(); ++e) {
         switch(((VertexFormatElement)format.func_227894_c_().get(e)).getUsage()) {
         case POSITION:
            consumer.put(e, x, y, z, 1.0F);
            break;
         case COLOR:
            float r = (float)(color >> 16 & 255) / 255.0F;
            float g = (float)(color >> 8 & 255) / 255.0F;
            float b = (float)(color >> 0 & 255) / 255.0F;
            float a = (float)(color >> 24 & 255) / 255.0F;
            consumer.put(e, r, g, b, a);
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
}
