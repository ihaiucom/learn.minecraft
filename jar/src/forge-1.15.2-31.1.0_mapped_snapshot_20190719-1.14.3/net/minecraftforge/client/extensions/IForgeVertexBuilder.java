package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.lwjgl.system.MemoryStack;

public interface IForgeVertexBuilder {
   default IVertexBuilder getVertexBuilder() {
      return (IVertexBuilder)this;
   }

   default void addVertexData(MatrixStack.Entry matrixStack, BakedQuad bakedQuad, float red, float green, float blue, int lightmapCoord, int overlayColor, boolean readExistingColor) {
      this.getVertexBuilder().func_227890_a_(matrixStack, bakedQuad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, red, green, blue, new int[]{lightmapCoord, lightmapCoord, lightmapCoord, lightmapCoord}, overlayColor, readExistingColor);
   }

   default void addVertexData(MatrixStack.Entry matrixEntry, BakedQuad bakedQuad, float red, float green, float blue, float alpha, int lightmapCoord, int overlayColor) {
      this.addVertexData(matrixEntry, bakedQuad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, red, green, blue, alpha, new int[]{lightmapCoord, lightmapCoord, lightmapCoord, lightmapCoord}, overlayColor, false);
   }

   default void addVertexData(MatrixStack.Entry matrixEntry, BakedQuad bakedQuad, float red, float green, float blue, float alpha, int lightmapCoord, int overlayColor, boolean readExistingColor) {
      this.addVertexData(matrixEntry, bakedQuad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, red, green, blue, alpha, new int[]{lightmapCoord, lightmapCoord, lightmapCoord, lightmapCoord}, overlayColor, readExistingColor);
   }

   default void addVertexData(MatrixStack.Entry matrixEntry, BakedQuad bakedQuad, float[] baseBrightness, float red, float green, float blue, float alpha, int[] lightmapCoords, int overlayCoords, boolean readExistingColor) {
      int[] aint = bakedQuad.getVertexData();
      Vec3i faceNormal = bakedQuad.getFace().getDirectionVec();
      Vector3f normal = new Vector3f((float)faceNormal.getX(), (float)faceNormal.getY(), (float)faceNormal.getZ());
      Matrix4f matrix4f = matrixEntry.func_227870_a_();
      normal.func_229188_a_(matrixEntry.func_227872_b_());
      int intSize = DefaultVertexFormats.BLOCK.getIntegerSize();
      int vertexCount = aint.length / intSize;
      MemoryStack memorystack = MemoryStack.stackPush();
      Throwable var18 = null;

      try {
         ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormats.BLOCK.getSize());
         IntBuffer intbuffer = bytebuffer.asIntBuffer();

         for(int v = 0; v < vertexCount; ++v) {
            intbuffer.clear();
            intbuffer.put(aint, v * 8, 8);
            float f = bytebuffer.getFloat(0);
            float f1 = bytebuffer.getFloat(4);
            float f2 = bytebuffer.getFloat(8);
            float cr;
            float cg;
            float cb;
            float ca;
            float f9;
            float f10;
            if (readExistingColor) {
               float r = (float)(bytebuffer.get(12) & 255) / 255.0F;
               f9 = (float)(bytebuffer.get(13) & 255) / 255.0F;
               f10 = (float)(bytebuffer.get(14) & 255) / 255.0F;
               float a = (float)(bytebuffer.get(15) & 255) / 255.0F;
               cr = r * baseBrightness[v] * red;
               cg = f9 * baseBrightness[v] * green;
               cb = f10 * baseBrightness[v] * blue;
               ca = a * alpha;
            } else {
               cr = baseBrightness[v] * red;
               cg = baseBrightness[v] * green;
               cb = baseBrightness[v] * blue;
               ca = alpha;
            }

            int lightmapCoord = this.applyBakedLighting(lightmapCoords[v], bytebuffer);
            f9 = bytebuffer.getFloat(16);
            f10 = bytebuffer.getFloat(20);
            Vector4f pos = new Vector4f(f, f1, f2, 1.0F);
            pos.func_229372_a_(matrix4f);
            this.applyBakedNormals(normal, bytebuffer, matrixEntry.func_227872_b_());
            ((IVertexBuilder)this).func_225588_a_(pos.getX(), pos.getY(), pos.getZ(), cr, cg, cb, ca, f9, f10, overlayCoords, lightmapCoord, normal.getX(), normal.getY(), normal.getZ());
         }
      } catch (Throwable var40) {
         var18 = var40;
         throw var40;
      } finally {
         if (memorystack != null) {
            if (var18 != null) {
               try {
                  memorystack.close();
               } catch (Throwable var39) {
                  var18.addSuppressed(var39);
               }
            } else {
               memorystack.close();
            }
         }

      }

   }

   default int applyBakedLighting(int lightmapCoord, ByteBuffer data) {
      int bl = LightTexture.func_228450_a_(lightmapCoord);
      int sl = LightTexture.func_228454_b_(lightmapCoord);
      int offset = LightUtil.getLightOffset(0) * 4;
      int blBaked = Short.toUnsignedInt(data.getShort(offset)) >> 4;
      int slBaked = Short.toUnsignedInt(data.getShort(offset + 2)) >> 4;
      bl = Math.max(bl, blBaked);
      sl = Math.max(sl, slBaked);
      return LightTexture.func_228451_a_(bl, sl);
   }

   default void applyBakedNormals(Vector3f generated, ByteBuffer data, Matrix3f normalTransform) {
      byte nx = data.get(28);
      byte ny = data.get(29);
      byte nz = data.get(30);
      if (nx != 0 || ny != 0 || nz != 0) {
         generated.set((float)nx / 127.0F, (float)ny / 127.0F, (float)nz / 127.0F);
         generated.func_229188_a_(normalTransform);
      }

   }
}
