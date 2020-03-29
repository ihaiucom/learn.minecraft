package net.minecraftforge.client.model.pipeline;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;

public class BakedQuadBuilder implements IVertexConsumer {
   private static final int SIZE;
   private final float[][][] unpackedData;
   private int tint;
   private Direction orientation;
   private TextureAtlasSprite texture;
   private boolean applyDiffuseLighting;
   private int vertices;
   private int elements;
   private boolean full;
   private boolean contractUVs;
   private final float eps;

   public BakedQuadBuilder() {
      this.unpackedData = new float[4][SIZE][4];
      this.tint = -1;
      this.applyDiffuseLighting = true;
      this.vertices = 0;
      this.elements = 0;
      this.full = false;
      this.contractUVs = false;
      this.eps = 0.00390625F;
   }

   public BakedQuadBuilder(TextureAtlasSprite texture) {
      this.unpackedData = new float[4][SIZE][4];
      this.tint = -1;
      this.applyDiffuseLighting = true;
      this.vertices = 0;
      this.elements = 0;
      this.full = false;
      this.contractUVs = false;
      this.eps = 0.00390625F;
      this.texture = texture;
   }

   public void setContractUVs(boolean value) {
      this.contractUVs = value;
   }

   public VertexFormat getVertexFormat() {
      return DefaultVertexFormats.BLOCK;
   }

   public void setQuadTint(int tint) {
      this.tint = tint;
   }

   public void setQuadOrientation(Direction orientation) {
      this.orientation = orientation;
   }

   public void setTexture(TextureAtlasSprite texture) {
      this.texture = texture;
   }

   public void setApplyDiffuseLighting(boolean diffuse) {
      this.applyDiffuseLighting = diffuse;
   }

   public void put(int element, float... data) {
      for(int i = 0; i < 4; ++i) {
         if (i < data.length) {
            this.unpackedData[this.vertices][element][i] = data[i];
         } else {
            this.unpackedData[this.vertices][element][i] = 0.0F;
         }
      }

      ++this.elements;
      if (this.elements == SIZE) {
         ++this.vertices;
         this.elements = 0;
      }

      if (this.vertices == 4) {
         this.full = true;
      }

   }

   public BakedQuad build() {
      if (!this.full) {
         throw new IllegalStateException("not enough data");
      } else if (this.texture == null) {
         throw new IllegalStateException("texture not set");
      } else {
         if (this.contractUVs) {
            float tX = (float)this.texture.getWidth() / (this.texture.getMaxU() - this.texture.getMinU());
            float tY = (float)this.texture.getHeight() / (this.texture.getMaxV() - this.texture.getMinV());
            float tS = tX > tY ? tX : tY;
            float ep = 1.0F / (tS * 256.0F);
            int uve = 0;

            ImmutableList elements;
            for(elements = DefaultVertexFormats.BLOCK.func_227894_c_(); uve < elements.size(); ++uve) {
               VertexFormatElement e = (VertexFormatElement)elements.get(uve);
               if (e.getUsage() == VertexFormatElement.Usage.UV && e.getIndex() == 0) {
                  break;
               }
            }

            if (uve == elements.size()) {
               throw new IllegalStateException("Can't contract UVs: format doesn't contain UVs");
            }

            float[] uvc = new float[4];

            int v;
            int i;
            for(v = 0; v < 4; ++v) {
               for(i = 0; i < 4; ++i) {
                  uvc[i] += this.unpackedData[v][uve][i] / 4.0F;
               }
            }

            for(v = 0; v < 4; ++v) {
               for(i = 0; i < 4; ++i) {
                  float uo = this.unpackedData[v][uve][i];
                  float un = uo * 0.99609375F + uvc[i] * 0.00390625F;
                  float ud = uo - un;
                  float aud = ud;
                  if (ud < 0.0F) {
                     aud = -ud;
                  }

                  if (aud < ep) {
                     float udc = uo - uvc[i];
                     if (udc < 0.0F) {
                        udc = -udc;
                     }

                     if (udc < 2.0F * ep) {
                        un = (uo + uvc[i]) / 2.0F;
                     } else {
                        un = uo + (ud < 0.0F ? ep : -ep);
                     }
                  }

                  this.unpackedData[v][uve][i] = un;
               }
            }
         }

         int[] packed = new int[DefaultVertexFormats.BLOCK.getIntegerSize() * 4];

         for(int v = 0; v < 4; ++v) {
            for(int e = 0; e < SIZE; ++e) {
               LightUtil.pack(this.unpackedData[v][e], packed, DefaultVertexFormats.BLOCK, v, e);
            }
         }

         return new BakedQuad(packed, this.tint, this.orientation, this.texture, this.applyDiffuseLighting);
      }
   }

   static {
      SIZE = DefaultVertexFormats.BLOCK.func_227894_c_().size();
   }
}
