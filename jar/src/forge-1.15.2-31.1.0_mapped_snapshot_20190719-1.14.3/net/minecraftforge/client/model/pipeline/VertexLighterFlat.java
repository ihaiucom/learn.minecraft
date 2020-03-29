package net.minecraftforge.client.model.pipeline;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;

public class VertexLighterFlat extends QuadGatheringTransformer {
   protected static final VertexFormatElement NORMAL_4F;
   protected final BlockInfo blockInfo;
   private int tint = -1;
   private boolean diffuse = true;
   protected int posIndex = -1;
   protected int normalIndex = -1;
   protected int colorIndex = -1;
   protected int lightmapIndex = -1;
   protected VertexFormat baseFormat;

   public VertexLighterFlat(BlockColors colors) {
      this.blockInfo = new BlockInfo(colors);
   }

   public void setParent(IVertexConsumer parent) {
      super.setParent(parent);
      this.setVertexFormat(parent.getVertexFormat());
   }

   private void updateIndices() {
      for(int i = 0; i < this.getVertexFormat().func_227894_c_().size(); ++i) {
         switch(((VertexFormatElement)this.getVertexFormat().func_227894_c_().get(i)).getUsage()) {
         case POSITION:
            this.posIndex = i;
            break;
         case NORMAL:
            this.normalIndex = i;
            break;
         case COLOR:
            this.colorIndex = i;
            break;
         case UV:
            if (((VertexFormatElement)this.getVertexFormat().func_227894_c_().get(i)).getIndex() == 2) {
               this.lightmapIndex = i;
            }
         }
      }

      if (this.posIndex == -1) {
         throw new IllegalArgumentException("vertex lighter needs format with position");
      } else if (this.lightmapIndex == -1) {
         throw new IllegalArgumentException("vertex lighter needs format with lightmap");
      } else if (this.colorIndex == -1) {
         throw new IllegalArgumentException("vertex lighter needs format with color");
      }
   }

   public void setVertexFormat(VertexFormat format) {
      if (!Objects.equals(format, this.baseFormat)) {
         this.baseFormat = format;
         super.setVertexFormat(withNormal(format));
         this.updateIndices();
      }
   }

   static VertexFormat withNormal(VertexFormat format) {
      return format == DefaultVertexFormats.BLOCK ? DefaultVertexFormats.BLOCK : withNormalUncached(format);
   }

   private static VertexFormat withNormalUncached(VertexFormat format) {
      if (format != null && !format.hasNormal()) {
         List<VertexFormatElement> l = Lists.newArrayList(format.func_227894_c_());
         l.add(NORMAL_4F);
         return new VertexFormat(ImmutableList.copyOf(l));
      } else {
         return format;
      }
   }

   protected void processQuad() {
      float[][] position = this.quadData[this.posIndex];
      float[][] normal = (float[][])null;
      float[][] lightmap = this.quadData[this.lightmapIndex];
      float[][] color = this.quadData[this.colorIndex];
      int v;
      if (this.dataLength[this.normalIndex] < 3 || this.quadData[this.normalIndex][0][0] == -1.0F && this.quadData[this.normalIndex][0][1] == -1.0F && this.quadData[this.normalIndex][0][2] == -1.0F) {
         normal = new float[4][4];
         Vector3f v1 = new Vector3f(position[3]);
         Vector3f t = new Vector3f(position[1]);
         Vector3f v2 = new Vector3f(position[2]);
         v1.sub(t);
         t.set(position[0]);
         v2.sub(t);
         v2.cross(v1);
         v2.func_229194_d_();

         for(v = 0; v < 4; ++v) {
            normal[v][0] = v2.getX();
            normal[v][1] = v2.getY();
            normal[v][2] = v2.getZ();
            normal[v][3] = 0.0F;
         }
      } else {
         normal = this.quadData[this.normalIndex];
      }

      int multiplier = -1;
      if (this.tint != -1) {
         multiplier = this.blockInfo.getColorMultiplier(this.tint);
      }

      VertexFormat format = this.parent.getVertexFormat();
      int count = format.func_227894_c_().size();

      for(v = 0; v < 4; ++v) {
         position[v][0] += this.blockInfo.getShx();
         position[v][1] += this.blockInfo.getShy();
         position[v][2] += this.blockInfo.getShz();
         float x = position[v][0] - 0.5F;
         float y = position[v][1] - 0.5F;
         float z = position[v][2] - 0.5F;
         x += normal[v][0] * 0.5F;
         y += normal[v][1] * 0.5F;
         z += normal[v][2] * 0.5F;
         float blockLight = lightmap[v][0];
         float skyLight = lightmap[v][1];
         this.updateLightmap(normal[v], lightmap[v], x, y, z);
         if (this.dataLength[this.lightmapIndex] > 1) {
            if (blockLight > lightmap[v][0]) {
               lightmap[v][0] = blockLight;
            }

            if (skyLight > lightmap[v][1]) {
               lightmap[v][1] = skyLight;
            }
         }

         this.updateColor(normal[v], color[v], x, y, z, (float)this.tint, multiplier);
         if (this.diffuse) {
            float d = LightUtil.diffuseLight(normal[v][0], normal[v][1], normal[v][2]);

            for(int i = 0; i < 3; ++i) {
               color[v][i] *= d;
            }
         }

         for(int e = 0; e < count; ++e) {
            VertexFormatElement element = (VertexFormatElement)format.func_227894_c_().get(e);
            switch(element.getUsage()) {
            case POSITION:
               this.parent.put(e, position[v]);
               break;
            case NORMAL:
               this.parent.put(e, normal[v]);
               break;
            case COLOR:
               this.parent.put(e, color[v]);
               break;
            case UV:
               if (element.getIndex() == 1) {
                  this.parent.put(e, lightmap[v]);
                  break;
               }
            default:
               this.parent.put(e, this.quadData[e][v]);
            }
         }
      }

      this.tint = -1;
   }

   protected void updateLightmap(float[] normal, float[] lightmap, float x, float y, float z) {
      float e1 = 0.99F;
      float e2 = 0.95F;
      boolean full = this.blockInfo.isFullCube();
      Direction side = null;
      if ((full || y < -0.99F) && normal[1] < -0.95F) {
         side = Direction.DOWN;
      } else if ((full || y > 0.99F) && normal[1] > 0.95F) {
         side = Direction.UP;
      } else if ((full || z < -0.99F) && normal[2] < -0.95F) {
         side = Direction.NORTH;
      } else if ((full || z > 0.99F) && normal[2] > 0.95F) {
         side = Direction.SOUTH;
      } else if ((full || x < -0.99F) && normal[0] < -0.95F) {
         side = Direction.WEST;
      } else if ((full || x > 0.99F) && normal[0] > 0.95F) {
         side = Direction.EAST;
      }

      int i = side == null ? 0 : side.ordinal() + 1;
      int brightness = this.blockInfo.getPackedLight()[i];
      lightmap[0] = (float)(brightness >> 4 & 15) * 32.0F / 65535.0F;
      lightmap[1] = (float)(brightness >> 20 & 15) * 32.0F / 65535.0F;
   }

   protected void updateColor(float[] normal, float[] color, float x, float y, float z, float tint, int multiplier) {
      if (tint != -1.0F) {
         color[0] *= (float)(multiplier >> 16 & 255) / 255.0F;
         color[1] *= (float)(multiplier >> 8 & 255) / 255.0F;
         color[2] *= (float)(multiplier & 255) / 255.0F;
      }

   }

   public void setQuadTint(int tint) {
      this.tint = tint;
   }

   public void setQuadOrientation(Direction orientation) {
   }

   public void setQuadCulled() {
   }

   public void setTexture(TextureAtlasSprite texture) {
   }

   public void setApplyDiffuseLighting(boolean diffuse) {
      this.diffuse = diffuse;
   }

   public void setWorld(ILightReader world) {
      this.blockInfo.setWorld(world);
   }

   public void setState(BlockState state) {
      this.blockInfo.setState(state);
   }

   public void setBlockPos(BlockPos blockPos) {
      this.blockInfo.setBlockPos(blockPos);
   }

   public void resetBlockInfo() {
      this.blockInfo.reset();
   }

   public void updateBlockInfo() {
      this.blockInfo.updateShift();
      this.blockInfo.updateFlatLighting();
   }

   static {
      NORMAL_4F = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.NORMAL, 4);
   }
}
