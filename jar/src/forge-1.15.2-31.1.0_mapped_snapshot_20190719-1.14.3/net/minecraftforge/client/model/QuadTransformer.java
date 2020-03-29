package net.minecraftforge.client.model;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class QuadTransformer {
   private static final int POSITION;
   private static final int NORMAL;
   private final TransformationMatrix transform;

   public QuadTransformer(TransformationMatrix transform) {
      this.transform = transform;
   }

   private void processVertices(int[] inData, int[] outData) {
      int stride = DefaultVertexFormats.BLOCK.getSize();
      int count = inData.length * 4 / stride;

      int i;
      int offset;
      float x;
      float y;
      for(i = 0; i < count; ++i) {
         offset = POSITION + i * stride;
         float x = Float.intBitsToFloat(getAtByteOffset(inData, offset));
         x = Float.intBitsToFloat(getAtByteOffset(inData, offset + 4));
         y = Float.intBitsToFloat(getAtByteOffset(inData, offset + 8));
         Vector4f pos = new Vector4f(x, x, y, 1.0F);
         this.transform.transformPosition(pos);
         pos.func_229375_f_();
         putAtByteOffset(outData, offset, Float.floatToRawIntBits(pos.getX()));
         putAtByteOffset(outData, offset + 4, Float.floatToRawIntBits(pos.getY()));
         putAtByteOffset(outData, offset + 8, Float.floatToRawIntBits(pos.getZ()));
      }

      for(i = 0; i < count; ++i) {
         offset = NORMAL + i * stride;
         int normalIn = getAtByteOffset(inData, offset);
         if (normalIn != 0) {
            x = (float)((byte)(normalIn >> 24)) / 127.0F;
            y = (float)((byte)(normalIn << 8 >> 24)) / 127.0F;
            float z = (float)((byte)(normalIn << 16 >> 24)) / 127.0F;
            Vector3f pos = new Vector3f(x, y, z);
            this.transform.transformNormal(pos);
            pos.func_229194_d_();
            int normalOut = ((byte)((int)(x / 127.0F)) & 255) << 24 | ((byte)((int)(y / 127.0F)) & 255) << 16 | ((byte)((int)(z / 127.0F)) & 255) << 8 | normalIn & 255;
            putAtByteOffset(outData, offset, normalOut);
         }
      }

   }

   private static int getAtByteOffset(int[] inData, int offset) {
      int index = offset / 4;
      int lsb = inData[index];
      int shift = offset % 4 * 8;
      if (shift == 0) {
         return inData[index];
      } else {
         int msb = inData[index + 1];
         return lsb >>> shift | msb << 32 - shift;
      }
   }

   private static void putAtByteOffset(int[] outData, int offset, int value) {
      int index = offset / 4;
      int shift = offset % 4 * 8;
      if (shift == 0) {
         outData[index] = value;
      } else {
         int lsbMask = -1 >>> 32 - shift;
         int msbMask = -1 << shift;
         outData[index] = outData[index] & lsbMask | value << shift;
         outData[index + 1] = outData[index + 1] & msbMask | value >>> 32 - shift;
      }
   }

   private static int findPositionOffset(VertexFormat fmt) {
      VertexFormatElement element = null;

      int index;
      for(index = 0; index < fmt.func_227894_c_().size(); ++index) {
         VertexFormatElement el = (VertexFormatElement)fmt.func_227894_c_().get(index);
         if (el.getUsage() == VertexFormatElement.Usage.POSITION) {
            element = el;
            break;
         }
      }

      if (index != fmt.func_227894_c_().size() && element != null) {
         if (element.getType() != VertexFormatElement.Type.FLOAT) {
            throw new RuntimeException("Expected POSITION attribute to have data type FLOAT");
         } else if (element.getSize() < 3) {
            throw new RuntimeException("Expected POSITION attribute to have at least 3 dimensions");
         } else {
            return fmt.getOffset(index);
         }
      } else {
         throw new RuntimeException("Expected vertex format to have a POSITION attribute");
      }
   }

   private static int findNormalOffset(VertexFormat fmt) {
      VertexFormatElement element = null;

      int index;
      for(index = 0; index < fmt.func_227894_c_().size(); ++index) {
         VertexFormatElement el = (VertexFormatElement)fmt.func_227894_c_().get(index);
         if (el.getUsage() == VertexFormatElement.Usage.NORMAL) {
            element = el;
            break;
         }
      }

      if (index != fmt.func_227894_c_().size() && element != null) {
         if (element.getType() != VertexFormatElement.Type.BYTE) {
            throw new RuntimeException("Expected NORMAL attribute to have data type BYTE");
         } else if (element.getSize() < 3) {
            throw new RuntimeException("Expected NORMAL attribute to have at least 3 dimensions");
         } else {
            return fmt.getOffset(index);
         }
      } else {
         throw new IllegalStateException("BLOCK format does not have normals?");
      }
   }

   public BakedQuad processOne(BakedQuad input) {
      int[] inData = input.getVertexData();
      int[] outData = Arrays.copyOf(inData, inData.length);
      this.processVertices(inData, outData);
      return new BakedQuad(outData, input.getTintIndex(), input.getFace(), input.getSprite(), input.shouldApplyDiffuseLighting());
   }

   public BakedQuad processOneInPlace(BakedQuad input) {
      int[] data = input.getVertexData();
      this.processVertices(data, data);
      return input;
   }

   public List<BakedQuad> processMany(List<BakedQuad> inputs) {
      if (inputs.size() == 0) {
         return Collections.emptyList();
      } else {
         List<BakedQuad> outputs = Lists.newArrayList();
         Iterator var3 = inputs.iterator();

         while(var3.hasNext()) {
            BakedQuad input = (BakedQuad)var3.next();
            int[] inData = input.getVertexData();
            int[] outData = Arrays.copyOf(inData, inData.length);
            this.processVertices(inData, outData);
            outputs.add(new BakedQuad(outData, input.getTintIndex(), input.getFace(), input.getSprite(), input.shouldApplyDiffuseLighting()));
         }

         return outputs;
      }
   }

   public void processManyInPlace(List<BakedQuad> inputs) {
      if (inputs.size() != 0) {
         Iterator var2 = inputs.iterator();

         while(var2.hasNext()) {
            BakedQuad input = (BakedQuad)var2.next();
            int[] data = input.getVertexData();
            this.processVertices(data, data);
         }

      }
   }

   static {
      POSITION = findPositionOffset(DefaultVertexFormats.BLOCK);
      NORMAL = findNormalOffset(DefaultVertexFormats.BLOCK);
   }
}
