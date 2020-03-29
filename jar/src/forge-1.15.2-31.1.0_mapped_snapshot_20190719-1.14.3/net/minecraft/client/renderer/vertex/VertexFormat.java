package net.minecraft.client.renderer.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VertexFormat {
   private final ImmutableList<VertexFormatElement> elements;
   private final IntList offsets = new IntArrayList();
   private final int vertexSize;

   public VertexFormat(ImmutableList<VertexFormatElement> p_i225911_1_) {
      this.elements = p_i225911_1_;
      int i = 0;

      VertexFormatElement vertexformatelement;
      for(UnmodifiableIterator var3 = p_i225911_1_.iterator(); var3.hasNext(); i += vertexformatelement.getSize()) {
         vertexformatelement = (VertexFormatElement)var3.next();
         this.offsets.add(i);
      }

      this.vertexSize = i;
   }

   public String toString() {
      return "format: " + this.elements.size() + " elements: " + (String)this.elements.stream().map(Object::toString).collect(Collectors.joining(" "));
   }

   public int getIntegerSize() {
      return this.getSize() / 4;
   }

   public int getSize() {
      return this.vertexSize;
   }

   public ImmutableList<VertexFormatElement> func_227894_c_() {
      return this.elements;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         VertexFormat vertexformat = (VertexFormat)p_equals_1_;
         return this.vertexSize != vertexformat.vertexSize ? false : this.elements.equals(vertexformat.elements);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.elements.hashCode();
   }

   public void func_227892_a_(long p_227892_1_) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this.func_227892_a_(p_227892_1_);
         });
      } else {
         int i = this.getSize();
         List<VertexFormatElement> list = this.func_227894_c_();

         for(int j = 0; j < list.size(); ++j) {
            ((VertexFormatElement)list.get(j)).func_227897_a_(p_227892_1_ + (long)this.offsets.getInt(j), i);
         }
      }

   }

   public void func_227895_d_() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(this::func_227895_d_);
      } else {
         UnmodifiableIterator var1 = this.func_227894_c_().iterator();

         while(var1.hasNext()) {
            VertexFormatElement vertexformatelement = (VertexFormatElement)var1.next();
            vertexformatelement.func_227898_g_();
         }
      }

   }

   public int getOffset(int p_getOffset_1_) {
      return this.offsets.getInt(p_getOffset_1_);
   }

   public boolean hasPosition() {
      return this.elements.stream().anyMatch((p_lambda$hasPosition$1_0_) -> {
         return p_lambda$hasPosition$1_0_.isPositionElement();
      });
   }

   public boolean hasNormal() {
      return this.elements.stream().anyMatch((p_lambda$hasNormal$2_0_) -> {
         return p_lambda$hasNormal$2_0_.getUsage() == VertexFormatElement.Usage.NORMAL;
      });
   }

   public boolean hasColor() {
      return this.elements.stream().anyMatch((p_lambda$hasColor$3_0_) -> {
         return p_lambda$hasColor$3_0_.getUsage() == VertexFormatElement.Usage.COLOR;
      });
   }

   public boolean hasUV(int p_hasUV_1_) {
      return this.elements.stream().anyMatch((p_lambda$hasUV$4_1_) -> {
         return p_lambda$hasUV$4_1_.getUsage() == VertexFormatElement.Usage.UV && p_lambda$hasUV$4_1_.getIndex() == p_hasUV_1_;
      });
   }
}
