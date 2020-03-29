package net.minecraftforge.client.model.pipeline;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import org.apache.commons.lang3.tuple.Pair;

public class LightUtil {
   private static final ConcurrentMap<Pair<VertexFormat, VertexFormat>, int[]> formatMaps = new ConcurrentHashMap();
   private static final VertexFormat DEFAULT_FROM;
   private static final VertexFormat DEFAULT_TO;
   private static final int[] DEFAULT_MAPPING;
   private static final ThreadLocal<LightUtil.ItemPipeline> itemPipeline;

   public static float diffuseLight(float x, float y, float z) {
      return Math.min(x * x * 0.6F + y * y * ((3.0F + y) / 4.0F) + z * z * 0.8F, 1.0F);
   }

   public static float diffuseLight(Direction side) {
      switch(side) {
      case DOWN:
         return 0.5F;
      case UP:
         return 1.0F;
      case NORTH:
      case SOUTH:
         return 0.8F;
      default:
         return 0.6F;
      }
   }

   public static Direction toSide(float x, float y, float z) {
      if (Math.abs(x) > Math.abs(y)) {
         if (Math.abs(x) > Math.abs(z)) {
            return x < 0.0F ? Direction.WEST : Direction.EAST;
         } else {
            return z < 0.0F ? Direction.NORTH : Direction.SOUTH;
         }
      } else if (Math.abs(y) > Math.abs(z)) {
         return y < 0.0F ? Direction.DOWN : Direction.UP;
      } else {
         return z < 0.0F ? Direction.NORTH : Direction.SOUTH;
      }
   }

   public static void putBakedQuad(IVertexConsumer consumer, BakedQuad quad) {
      consumer.setTexture(quad.getSprite());
      consumer.setQuadOrientation(quad.getFace());
      if (quad.hasTintIndex()) {
         consumer.setQuadTint(quad.getTintIndex());
      }

      consumer.setApplyDiffuseLighting(quad.shouldApplyDiffuseLighting());
      float[] data = new float[4];
      VertexFormat formatFrom = consumer.getVertexFormat();
      VertexFormat formatTo = DefaultVertexFormats.BLOCK;
      int countFrom = formatFrom.func_227894_c_().size();
      int countTo = formatTo.func_227894_c_().size();
      int[] eMap = mapFormats(formatFrom, formatTo);

      for(int v = 0; v < 4; ++v) {
         for(int e = 0; e < countFrom; ++e) {
            if (eMap[e] != countTo) {
               unpack(quad.getVertexData(), data, formatTo, v, eMap[e]);
               consumer.put(e, data);
            } else {
               consumer.put(e);
            }
         }
      }

   }

   public static int[] mapFormats(VertexFormat from, VertexFormat to) {
      return from.equals(DEFAULT_FROM) && to.equals(DEFAULT_TO) ? DEFAULT_MAPPING : (int[])formatMaps.computeIfAbsent(Pair.of(from, to), (pair) -> {
         return generateMapping((VertexFormat)pair.getLeft(), (VertexFormat)pair.getRight());
      });
   }

   private static int[] generateMapping(VertexFormat from, VertexFormat to) {
      int fromCount = from.func_227894_c_().size();
      int toCount = to.func_227894_c_().size();
      int[] eMap = new int[fromCount];

      for(int e = 0; e < fromCount; ++e) {
         VertexFormatElement expected = (VertexFormatElement)from.func_227894_c_().get(e);

         int e2;
         for(e2 = 0; e2 < toCount; ++e2) {
            VertexFormatElement current = (VertexFormatElement)to.func_227894_c_().get(e2);
            if (expected.getUsage() == current.getUsage() && expected.getIndex() == current.getIndex()) {
               break;
            }
         }

         eMap[e] = e2;
      }

      return eMap;
   }

   public static void unpack(int[] from, float[] to, VertexFormat formatFrom, int v, int e) {
      int length = 4 < to.length ? 4 : to.length;
      VertexFormatElement element = (VertexFormatElement)formatFrom.func_227894_c_().get(e);
      int vertexStart = v * formatFrom.getSize() + formatFrom.getOffset(e);
      int count = element.getElementCount();
      VertexFormatElement.Type type = element.getType();
      VertexFormatElement.Usage usage = element.getUsage();
      int size = type.getSize();
      int mask = (256 << 8 * (size - 1)) - 1;

      for(int i = 0; i < length; ++i) {
         if (i < count) {
            int pos = vertexStart + size * i;
            int index = pos >> 2;
            int offset = pos & 3;
            int bits = from[index];
            bits >>>= offset * 8;
            if ((pos + size - 1) / 4 != index) {
               bits |= from[index + 1] << (4 - offset) * 8;
            }

            bits &= mask;
            if (type == VertexFormatElement.Type.FLOAT) {
               to[i] = Float.intBitsToFloat(bits);
            } else if (type != VertexFormatElement.Type.UBYTE && type != VertexFormatElement.Type.USHORT) {
               if (type == VertexFormatElement.Type.UINT) {
                  to[i] = (float)((double)((long)bits & 4294967295L) / 4.294967295E9D);
               } else if (type == VertexFormatElement.Type.BYTE) {
                  to[i] = (float)((byte)bits) / (float)(mask >> 1);
               } else if (type == VertexFormatElement.Type.SHORT) {
                  to[i] = (float)((short)bits) / (float)(mask >> 1);
               } else if (type == VertexFormatElement.Type.INT) {
                  to[i] = (float)((double)((long)bits & 4294967295L) / 2.147483647E9D);
               }
            } else {
               to[i] = (float)bits / (float)mask;
            }
         } else {
            to[i] = i == 3 && usage == VertexFormatElement.Usage.POSITION ? 1.0F : 0.0F;
         }
      }

   }

   public static void pack(float[] from, int[] to, VertexFormat formatTo, int v, int e) {
      VertexFormatElement element = (VertexFormatElement)formatTo.func_227894_c_().get(e);
      int vertexStart = v * formatTo.getSize() + formatTo.getOffset(e);
      int count = element.getElementCount();
      VertexFormatElement.Type type = element.getType();
      int size = type.getSize();
      int mask = (256 << 8 * (size - 1)) - 1;

      for(int i = 0; i < 4; ++i) {
         if (i < count) {
            int pos = vertexStart + size * i;
            int index = pos >> 2;
            int offset = pos & 3;
            int bits = false;
            float f = i < from.length ? from[i] : 0.0F;
            int bits;
            if (type == VertexFormatElement.Type.FLOAT) {
               bits = Float.floatToRawIntBits(f);
            } else if (type != VertexFormatElement.Type.UBYTE && type != VertexFormatElement.Type.USHORT && type != VertexFormatElement.Type.UINT) {
               bits = Math.round(f * (float)(mask >> 1));
            } else {
               bits = Math.round(f * (float)mask);
            }

            to[index] &= ~(mask << offset * 8);
            to[index] |= (bits & mask) << offset * 8;
         }
      }

   }

   public static int getLightOffset(int v) {
      return v * 8 + 6;
   }

   public static void setLightData(BakedQuad q, int light) {
      int[] data = q.getVertexData();

      for(int i = 0; i < 4; ++i) {
         data[getLightOffset(i)] = light;
      }

   }

   static {
      DEFAULT_FROM = VertexLighterFlat.withNormal(DefaultVertexFormats.BLOCK);
      DEFAULT_TO = DefaultVertexFormats.BLOCK;
      DEFAULT_MAPPING = generateMapping(DEFAULT_FROM, DEFAULT_TO);
      itemPipeline = ThreadLocal.withInitial(LightUtil.ItemPipeline::new);
   }

   public static class ItemConsumer extends VertexTransformer {
      private int vertices = 0;
      private float[] auxColor = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
      private float[] buf = new float[4];

      public ItemConsumer(IVertexConsumer parent) {
         super(parent);
      }

      public void setAuxColor(float... auxColor) {
         System.arraycopy(auxColor, 0, this.auxColor, 0, this.auxColor.length);
      }

      public void put(int element, float... data) {
         if (((VertexFormatElement)this.getVertexFormat().func_227894_c_().get(element)).getUsage() == VertexFormatElement.Usage.COLOR) {
            System.arraycopy(this.auxColor, 0, this.buf, 0, this.buf.length);
            int n = Math.min(4, data.length);

            for(int i = 0; i < n; ++i) {
               float[] var10000 = this.buf;
               var10000[i] *= data[i];
            }

            super.put(element, this.buf);
         } else {
            super.put(element, data);
         }

         if (element == this.getVertexFormat().func_227894_c_().size() - 1) {
            ++this.vertices;
            if (this.vertices == 4) {
               this.vertices = 0;
            }
         }

      }
   }

   private static final class ItemPipeline {
      final VertexBufferConsumer bufferConsumer = new VertexBufferConsumer();
      final LightUtil.ItemConsumer itemConsumer;

      ItemPipeline() {
         this.itemConsumer = new LightUtil.ItemConsumer(this.bufferConsumer);
      }
   }
}
