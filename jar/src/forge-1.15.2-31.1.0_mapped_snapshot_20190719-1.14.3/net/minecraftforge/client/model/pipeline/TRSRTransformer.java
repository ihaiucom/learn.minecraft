package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class TRSRTransformer extends VertexTransformer {
   private final TransformationMatrix transform;

   public TRSRTransformer(IVertexConsumer parent, TransformationMatrix transform) {
      super(parent);
      this.transform = transform;
   }

   public void put(int element, float... data) {
      switch(((VertexFormatElement)this.getVertexFormat().func_227894_c_().get(element)).getUsage()) {
      case POSITION:
         Vector4f pos = new Vector4f(data[0], data[1], data[2], data[3]);
         this.transform.transformPosition(pos);
         data[0] = pos.getX();
         data[1] = pos.getY();
         data[2] = pos.getZ();
         data[3] = pos.getW();
         break;
      case NORMAL:
         Vector3f normal = new Vector3f(data);
         this.transform.transformNormal(normal);
         data[0] = normal.getX();
         data[1] = normal.getY();
         data[2] = normal.getZ();
      }

      super.put(element, data);
   }
}
