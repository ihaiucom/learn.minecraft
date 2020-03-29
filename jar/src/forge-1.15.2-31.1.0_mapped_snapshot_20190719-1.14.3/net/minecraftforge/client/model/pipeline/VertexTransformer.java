package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;

public class VertexTransformer implements IVertexConsumer {
   protected final IVertexConsumer parent;

   public VertexTransformer(IVertexConsumer parent) {
      this.parent = parent;
   }

   public VertexFormat getVertexFormat() {
      return this.parent.getVertexFormat();
   }

   public void setQuadTint(int tint) {
      this.parent.setQuadTint(tint);
   }

   public void setTexture(TextureAtlasSprite texture) {
      this.parent.setTexture(texture);
   }

   public void setQuadOrientation(Direction orientation) {
      this.parent.setQuadOrientation(orientation);
   }

   public void setApplyDiffuseLighting(boolean diffuse) {
      this.parent.setApplyDiffuseLighting(diffuse);
   }

   public void put(int element, float... data) {
      this.parent.put(element, data);
   }
}
