package net.minecraftforge.client.model.pipeline;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class VertexBufferConsumer implements IVertexConsumer {
   private static final float[] dummyColor = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
   private IVertexBuilder renderer;
   private int[] quadData;
   private int v = 0;
   private BlockPos offset;

   public VertexBufferConsumer() {
      this.offset = BlockPos.ZERO;
   }

   public VertexBufferConsumer(IVertexBuilder buffer) {
      this.offset = BlockPos.ZERO;
      this.setBuffer(buffer);
   }

   public VertexFormat getVertexFormat() {
      return DefaultVertexFormats.BLOCK;
   }

   public void put(int e, float... data) {
   }

   private void checkVertexFormat() {
      if (this.quadData == null || this.getVertexFormat().getSize() != this.quadData.length) {
         this.quadData = new int[this.getVertexFormat().getSize()];
      }

   }

   public void setBuffer(IVertexBuilder buffer) {
      this.renderer = buffer;
      this.checkVertexFormat();
   }

   public void setOffset(BlockPos offset) {
      this.offset = new BlockPos(offset);
   }

   public void setQuadTint(int tint) {
   }

   public void setQuadOrientation(Direction orientation) {
   }

   public void setApplyDiffuseLighting(boolean diffuse) {
   }

   public void setTexture(TextureAtlasSprite texture) {
   }
}
