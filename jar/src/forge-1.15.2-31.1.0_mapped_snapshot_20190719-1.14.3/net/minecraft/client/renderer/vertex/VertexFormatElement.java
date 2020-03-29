package net.minecraft.client.renderer.vertex;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.IntConsumer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class VertexFormatElement {
   private static final Logger LOGGER = LogManager.getLogger();
   private final VertexFormatElement.Type type;
   private final VertexFormatElement.Usage usage;
   private final int index;
   private final int elementCount;
   private final int field_227896_f_;

   public VertexFormatElement(int p_i46096_1_, VertexFormatElement.Type p_i46096_2_, VertexFormatElement.Usage p_i46096_3_, int p_i46096_4_) {
      if (this.isFirstOrUV(p_i46096_1_, p_i46096_3_)) {
         this.usage = p_i46096_3_;
      } else {
         LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
         this.usage = VertexFormatElement.Usage.UV;
      }

      this.type = p_i46096_2_;
      this.index = p_i46096_1_;
      this.elementCount = p_i46096_4_;
      this.field_227896_f_ = p_i46096_2_.getSize() * this.elementCount;
   }

   private boolean isFirstOrUV(int p_177372_1_, VertexFormatElement.Usage p_177372_2_) {
      return p_177372_1_ == 0 || p_177372_2_ == VertexFormatElement.Usage.UV;
   }

   public final VertexFormatElement.Type getType() {
      return this.type;
   }

   public final VertexFormatElement.Usage getUsage() {
      return this.usage;
   }

   public final int getElementCount() {
      return this.elementCount;
   }

   public final int getIndex() {
      return this.index;
   }

   public String toString() {
      return this.elementCount + "," + this.usage.getDisplayName() + "," + this.type.getDisplayName();
   }

   public final int getSize() {
      return this.field_227896_f_;
   }

   public final boolean isPositionElement() {
      return this.usage == VertexFormatElement.Usage.POSITION;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         VertexFormatElement lvt_2_1_ = (VertexFormatElement)p_equals_1_;
         if (this.elementCount != lvt_2_1_.elementCount) {
            return false;
         } else if (this.index != lvt_2_1_.index) {
            return false;
         } else if (this.type != lvt_2_1_.type) {
            return false;
         } else {
            return this.usage == lvt_2_1_.usage;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int lvt_1_1_ = this.type.hashCode();
      lvt_1_1_ = 31 * lvt_1_1_ + this.usage.hashCode();
      lvt_1_1_ = 31 * lvt_1_1_ + this.index;
      lvt_1_1_ = 31 * lvt_1_1_ + this.elementCount;
      return lvt_1_1_;
   }

   public void func_227897_a_(long p_227897_1_, int p_227897_3_) {
      this.usage.func_227902_a_(this.elementCount, this.type.getGlConstant(), p_227897_3_, p_227897_1_, this.index);
   }

   public void func_227898_g_() {
      this.usage.func_227901_a_(this.index);
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      FLOAT(4, "Float", 5126),
      UBYTE(1, "Unsigned Byte", 5121),
      BYTE(1, "Byte", 5120),
      USHORT(2, "Unsigned Short", 5123),
      SHORT(2, "Short", 5122),
      UINT(4, "Unsigned Int", 5125),
      INT(4, "Int", 5124);

      private final int size;
      private final String displayName;
      private final int glConstant;

      private Type(int p_i46095_3_, String p_i46095_4_, int p_i46095_5_) {
         this.size = p_i46095_3_;
         this.displayName = p_i46095_4_;
         this.glConstant = p_i46095_5_;
      }

      public int getSize() {
         return this.size;
      }

      public String getDisplayName() {
         return this.displayName;
      }

      public int getGlConstant() {
         return this.glConstant;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Usage {
      POSITION("Position", (p_227914_0_, p_227914_1_, p_227914_2_, p_227914_3_, p_227914_5_) -> {
         GlStateManager.func_227679_b_(p_227914_0_, p_227914_1_, p_227914_2_, p_227914_3_);
         GlStateManager.func_227770_y_(32884);
      }, (p_227912_0_) -> {
         GlStateManager.func_227772_z_(32884);
      }),
      NORMAL("Normal", (p_227913_0_, p_227913_1_, p_227913_2_, p_227913_3_, p_227913_5_) -> {
         GlStateManager.func_227652_a_(p_227913_1_, p_227913_2_, p_227913_3_);
         GlStateManager.func_227770_y_(32885);
      }, (p_227910_0_) -> {
         GlStateManager.func_227772_z_(32885);
      }),
      COLOR("Vertex Color", (p_227911_0_, p_227911_1_, p_227911_2_, p_227911_3_, p_227911_5_) -> {
         GlStateManager.func_227694_c_(p_227911_0_, p_227911_1_, p_227911_2_, p_227911_3_);
         GlStateManager.func_227770_y_(32886);
      }, (p_227908_0_) -> {
         GlStateManager.func_227772_z_(32886);
         GlStateManager.func_227628_P_();
      }),
      UV("UV", (p_227909_0_, p_227909_1_, p_227909_2_, p_227909_3_, p_227909_5_) -> {
         GlStateManager.func_227747_o_('蓀' + p_227909_5_);
         GlStateManager.func_227650_a_(p_227909_0_, p_227909_1_, p_227909_2_, p_227909_3_);
         GlStateManager.func_227770_y_(32888);
         GlStateManager.func_227747_o_(33984);
      }, (p_227906_0_) -> {
         GlStateManager.func_227747_o_('蓀' + p_227906_0_);
         GlStateManager.func_227772_z_(32888);
         GlStateManager.func_227747_o_(33984);
      }),
      PADDING("Padding", (p_227907_0_, p_227907_1_, p_227907_2_, p_227907_3_, p_227907_5_) -> {
      }, (p_227904_0_) -> {
      }),
      GENERIC("Generic", (p_227905_0_, p_227905_1_, p_227905_2_, p_227905_3_, p_227905_5_) -> {
         GlStateManager.func_227606_A_(p_227905_5_);
         GlStateManager.func_227651_a_(p_227905_5_, p_227905_0_, p_227905_1_, false, p_227905_2_, p_227905_3_);
      }, GlStateManager::func_227608_B_);

      private final String displayName;
      private final VertexFormatElement.Usage.ISetupState field_227899_h_;
      private final IntConsumer field_227900_i_;

      private Usage(String p_i225912_3_, VertexFormatElement.Usage.ISetupState p_i225912_4_, IntConsumer p_i225912_5_) {
         this.displayName = p_i225912_3_;
         this.field_227899_h_ = p_i225912_4_;
         this.field_227900_i_ = p_i225912_5_;
      }

      private void func_227902_a_(int p_227902_1_, int p_227902_2_, int p_227902_3_, long p_227902_4_, int p_227902_6_) {
         this.field_227899_h_.setupBufferState(p_227902_1_, p_227902_2_, p_227902_3_, p_227902_4_, p_227902_6_);
      }

      public void func_227901_a_(int p_227901_1_) {
         this.field_227900_i_.accept(p_227901_1_);
      }

      public String getDisplayName() {
         return this.displayName;
      }

      @OnlyIn(Dist.CLIENT)
      interface ISetupState {
         void setupBufferState(int var1, int var2, int var3, long var4, int var6);
      }
   }
}
