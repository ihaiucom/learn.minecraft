package net.minecraft.realms;

import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsVertexFormat {
   private VertexFormat v;

   public RealmsVertexFormat(VertexFormat p_i46456_1_) {
      this.v = p_i46456_1_;
   }

   public VertexFormat getVertexFormat() {
      return this.v;
   }

   public List<RealmsVertexFormatElement> getElements() {
      List<RealmsVertexFormatElement> lvt_1_1_ = Lists.newArrayList();
      UnmodifiableIterator var2 = this.v.func_227894_c_().iterator();

      while(var2.hasNext()) {
         VertexFormatElement lvt_3_1_ = (VertexFormatElement)var2.next();
         lvt_1_1_.add(new RealmsVertexFormatElement(lvt_3_1_));
      }

      return lvt_1_1_;
   }

   public boolean equals(Object p_equals_1_) {
      return this.v.equals(p_equals_1_);
   }

   public int hashCode() {
      return this.v.hashCode();
   }

   public String toString() {
      return this.v.toString();
   }
}
