package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;

public abstract class SimpleModelFontRenderer extends FontRenderer {
   private float r;
   private float g;
   private float b;
   private float a;
   private final TransformationMatrix transform;
   private Builder<BakedQuad> builder = ImmutableList.builder();
   private final Vector3f normal = new Vector3f(0.0F, 0.0F, 1.0F);
   private final Direction orientation;
   private boolean fillBlanks = false;
   private TextureAtlasSprite sprite;
   private final Vector4f vec = new Vector4f();

   public SimpleModelFontRenderer(GameSettings settings, ResourceLocation font, TextureManager manager, boolean isUnicode, Matrix4f matrix) {
      super(manager, (Font)null);
      this.transform = new TransformationMatrix(matrix);
      this.transform.transformNormal(this.normal);
      this.orientation = Direction.getFacingFromVector(this.normal.getX(), this.normal.getY(), this.normal.getZ());
   }

   public void setSprite(TextureAtlasSprite sprite) {
      this.sprite = sprite;
   }

   public void setFillBlanks(boolean fillBlanks) {
      this.fillBlanks = fillBlanks;
   }

   private void addVertex(BakedQuadBuilder quadBuilder, float x, float y, float u, float v) {
      ImmutableList<VertexFormatElement> elements = quadBuilder.getVertexFormat().func_227894_c_();

      for(int e = 0; e < elements.size(); ++e) {
         VertexFormatElement element = (VertexFormatElement)elements.get(e);
         switch(element.getUsage()) {
         case POSITION:
            this.vec.set(x, y, 0.0F, 1.0F);
            this.transform.transformPosition(this.vec);
            quadBuilder.put(e, this.vec.getX(), this.vec.getY(), this.vec.getZ(), this.vec.getW());
            break;
         case COLOR:
            quadBuilder.put(e, this.r, this.g, this.b, this.a);
            break;
         case NORMAL:
            quadBuilder.put(e, 0.0F, 0.0F, 1.0F, 1.0F);
            break;
         case UV:
            if (element.getIndex() == 0) {
               quadBuilder.put(e, this.sprite.getInterpolatedU((double)(u * 16.0F)), this.sprite.getInterpolatedV((double)(v * 16.0F)), 0.0F, 1.0F);
               break;
            }
         default:
            quadBuilder.put(e);
         }
      }

   }

   public ImmutableList<BakedQuad> build() {
      ImmutableList<BakedQuad> ret = this.builder.build();
      this.builder = ImmutableList.builder();
      return ret;
   }
}
