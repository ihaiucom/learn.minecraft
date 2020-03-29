package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.vertex.VertexFormat;

public abstract class QuadGatheringTransformer implements IVertexConsumer {
   protected IVertexConsumer parent;
   protected VertexFormat format;
   protected int vertices = 0;
   protected byte[] dataLength = null;
   protected float[][][] quadData = (float[][][])null;

   public void setParent(IVertexConsumer parent) {
      this.parent = parent;
   }

   public void setVertexFormat(VertexFormat format) {
      this.format = format;
      this.dataLength = new byte[format.func_227894_c_().size()];
      this.quadData = new float[format.func_227894_c_().size()][4][4];
   }

   public VertexFormat getVertexFormat() {
      return this.format;
   }

   public void put(int element, float... data) {
      System.arraycopy(data, 0, this.quadData[element][this.vertices], 0, data.length);
      if (this.vertices == 0) {
         this.dataLength[element] = (byte)data.length;
      }

      if (element == this.getVertexFormat().func_227894_c_().size() - 1) {
         ++this.vertices;
      }

      if (this.vertices == 4) {
         this.vertices = 0;
         this.processQuad();
      }

   }

   protected abstract void processQuad();
}
