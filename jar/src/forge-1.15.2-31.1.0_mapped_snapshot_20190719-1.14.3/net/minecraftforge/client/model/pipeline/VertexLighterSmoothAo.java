package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.util.math.MathHelper;

public class VertexLighterSmoothAo extends VertexLighterFlat {
   public VertexLighterSmoothAo(BlockColors colors) {
      super(colors);
   }

   protected void updateLightmap(float[] normal, float[] lightmap, float x, float y, float z) {
      lightmap[0] = this.calcLightmap(this.blockInfo.getBlockLight(), x, y, z);
      lightmap[1] = this.calcLightmap(this.blockInfo.getSkyLight(), x, y, z);
   }

   protected void updateColor(float[] normal, float[] color, float x, float y, float z, float tint, int multiplier) {
      super.updateColor(normal, color, x, y, z, tint, multiplier);
      float a = this.getAo(x, y, z);
      color[0] *= a;
      color[1] *= a;
      color[2] *= a;
   }

   protected float calcLightmap(float[][][][] light, float x, float y, float z) {
      x *= 2.0F;
      y *= 2.0F;
      z *= 2.0F;
      float l2 = x * x + y * y + z * z;
      float ax;
      if (l2 > 5.98F) {
         ax = (float)Math.sqrt((double)(5.98F / l2));
         x *= ax;
         y *= ax;
         z *= ax;
      }

      ax = x > 0.0F ? x : -x;
      float ay = y > 0.0F ? y : -y;
      float az = z > 0.0F ? z : -z;
      float e1 = 1.0001F;
      if (ax > 1.9999F && ay <= e1 && az <= e1) {
         x = x < 0.0F ? -1.9999F : 1.9999F;
      } else if (ay > 1.9999F && az <= e1 && ax <= e1) {
         y = y < 0.0F ? -1.9999F : 1.9999F;
      } else if (az > 1.9999F && ax <= e1 && ay <= e1) {
         z = z < 0.0F ? -1.9999F : 1.9999F;
      }

      ax = x > 0.0F ? x : -x;
      ay = y > 0.0F ? y : -y;
      az = z > 0.0F ? z : -z;
      float l;
      if (ax <= e1 && ay + az > 2.9999F) {
         l = 2.9999F / (ay + az);
         y *= l;
         z *= l;
      } else if (ay <= e1 && az + ax > 2.9999F) {
         l = 2.9999F / (az + ax);
         z *= l;
         x *= l;
      } else if (az <= e1 && ax + ay > 2.9999F) {
         l = 2.9999F / (ax + ay);
         x *= l;
         y *= l;
      } else if (ax + ay + az > 3.9999F) {
         l = 3.9999F / (ax + ay + az);
         x *= l;
         y *= l;
         z *= l;
      }

      l = 0.0F;
      float s = 0.0F;

      for(int ix = 0; ix <= 1; ++ix) {
         for(int iy = 0; iy <= 1; ++iy) {
            for(int iz = 0; iz <= 1; ++iz) {
               float vx = x * (float)(1 - ix * 2);
               float vy = y * (float)(1 - iy * 2);
               float vz = z * (float)(1 - iz * 2);
               float s3 = vx + vy + vz + 4.0F;
               float sx = vy + vz + 3.0F;
               float sy = vz + vx + 3.0F;
               float sz = vx + vy + 3.0F;
               float bx = (2.0F * vx + vy + vz + 6.0F) / (s3 * sy * sz * (vx + 2.0F));
               s += bx;
               l += bx * light[0][ix][iy][iz];
               float by = (2.0F * vy + vz + vx + 6.0F) / (s3 * sz * sx * (vy + 2.0F));
               s += by;
               l += by * light[1][ix][iy][iz];
               float bz = (2.0F * vz + vx + vy + 6.0F) / (s3 * sx * sy * (vz + 2.0F));
               s += bz;
               l += bz * light[2][ix][iy][iz];
            }
         }
      }

      l /= s;
      if (l > 0.0073243305F) {
         l = 0.0073243305F;
      }

      if (l < 0.0F) {
         l = 0.0F;
      }

      return l;
   }

   protected float getAo(float x, float y, float z) {
      int sx = x < 0.0F ? 1 : 2;
      int sy = y < 0.0F ? 1 : 2;
      int sz = z < 0.0F ? 1 : 2;
      if (x < 0.0F) {
         ++x;
      }

      if (y < 0.0F) {
         ++y;
      }

      if (z < 0.0F) {
         ++z;
      }

      float a = 0.0F;
      float[][][] ao = this.blockInfo.getAo();
      a += ao[sx - 1][sy - 1][sz - 1] * (1.0F - x) * (1.0F - y) * (1.0F - z);
      a += ao[sx - 1][sy - 1][sz - 0] * (1.0F - x) * (1.0F - y) * (0.0F + z);
      a += ao[sx - 1][sy - 0][sz - 1] * (1.0F - x) * (0.0F + y) * (1.0F - z);
      a += ao[sx - 1][sy - 0][sz - 0] * (1.0F - x) * (0.0F + y) * (0.0F + z);
      a += ao[sx - 0][sy - 1][sz - 1] * (0.0F + x) * (1.0F - y) * (1.0F - z);
      a += ao[sx - 0][sy - 1][sz - 0] * (0.0F + x) * (1.0F - y) * (0.0F + z);
      a += ao[sx - 0][sy - 0][sz - 1] * (0.0F + x) * (0.0F + y) * (1.0F - z);
      a += ao[sx - 0][sy - 0][sz - 0] * (0.0F + x) * (0.0F + y) * (0.0F + z);
      a = MathHelper.clamp(a, 0.0F, 1.0F);
      return a;
   }

   public void updateBlockInfo() {
      this.blockInfo.updateShift();
      this.blockInfo.updateLightMatrix();
   }
}
