package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.vertex.VertexFormat;

public abstract class TransformerConsumer implements IVertexConsumer {
   private IVertexConsumer parent;

   protected TransformerConsumer(IVertexConsumer parent) {
      this.parent = parent;
   }

   public VertexFormat getVertexFormat() {
      return this.parent.getVertexFormat();
   }

   public void put(int element, float... data) {
      float[] newData = this.transform(element, data);
      this.parent.put(element, newData);
   }

   protected abstract float[] transform(int var1, float... var2);
}
