package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;

public interface IVertexConsumer {
   VertexFormat getVertexFormat();

   void setQuadTint(int var1);

   void setQuadOrientation(Direction var1);

   void setApplyDiffuseLighting(boolean var1);

   void setTexture(TextureAtlasSprite var1);

   void put(int var1, float... var2);
}
