package net.minecraft.client.renderer.vertex;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DefaultVertexFormats {
   public static final VertexFormatElement POSITION_3F;
   public static final VertexFormatElement COLOR_4UB;
   public static final VertexFormatElement TEX_2F;
   public static final VertexFormatElement TEX_2S;
   public static final VertexFormatElement field_227848_e_;
   public static final VertexFormatElement NORMAL_3B;
   public static final VertexFormatElement PADDING_1B;
   public static final VertexFormat BLOCK;
   public static final VertexFormat field_227849_i_;
   @Deprecated
   public static final VertexFormat PARTICLE_POSITION_TEX_COLOR_LMAP;
   public static final VertexFormat POSITION;
   public static final VertexFormat POSITION_COLOR;
   public static final VertexFormat field_227850_m_;
   public static final VertexFormat POSITION_TEX;
   public static final VertexFormat field_227851_o_;
   @Deprecated
   public static final VertexFormat POSITION_TEX_COLOR;
   public static final VertexFormat field_227852_q_;
   @Deprecated
   public static final VertexFormat field_227853_r_;
   @Deprecated
   public static final VertexFormat POSITION_TEX_COLOR_NORMAL;

   static {
      POSITION_3F = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3);
      COLOR_4UB = new VertexFormatElement(0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4);
      TEX_2F = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
      TEX_2S = new VertexFormatElement(1, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
      field_227848_e_ = new VertexFormatElement(2, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
      NORMAL_3B = new VertexFormatElement(0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.NORMAL, 3);
      PADDING_1B = new VertexFormatElement(0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.PADDING, 1);
      BLOCK = new VertexFormat(ImmutableList.builder().add(POSITION_3F).add(COLOR_4UB).add(TEX_2F).add(field_227848_e_).add(NORMAL_3B).add(PADDING_1B).build());
      field_227849_i_ = new VertexFormat(ImmutableList.builder().add(POSITION_3F).add(COLOR_4UB).add(TEX_2F).add(TEX_2S).add(field_227848_e_).add(NORMAL_3B).add(PADDING_1B).build());
      PARTICLE_POSITION_TEX_COLOR_LMAP = new VertexFormat(ImmutableList.builder().add(POSITION_3F).add(TEX_2F).add(COLOR_4UB).add(field_227848_e_).build());
      POSITION = new VertexFormat(ImmutableList.builder().add(POSITION_3F).build());
      POSITION_COLOR = new VertexFormat(ImmutableList.builder().add(POSITION_3F).add(COLOR_4UB).build());
      field_227850_m_ = new VertexFormat(ImmutableList.builder().add(POSITION_3F).add(COLOR_4UB).add(field_227848_e_).build());
      POSITION_TEX = new VertexFormat(ImmutableList.builder().add(POSITION_3F).add(TEX_2F).build());
      field_227851_o_ = new VertexFormat(ImmutableList.builder().add(POSITION_3F).add(COLOR_4UB).add(TEX_2F).build());
      POSITION_TEX_COLOR = new VertexFormat(ImmutableList.builder().add(POSITION_3F).add(TEX_2F).add(COLOR_4UB).build());
      field_227852_q_ = new VertexFormat(ImmutableList.builder().add(POSITION_3F).add(COLOR_4UB).add(TEX_2F).add(field_227848_e_).build());
      field_227853_r_ = new VertexFormat(ImmutableList.builder().add(POSITION_3F).add(TEX_2F).add(field_227848_e_).add(COLOR_4UB).build());
      POSITION_TEX_COLOR_NORMAL = new VertexFormat(ImmutableList.builder().add(POSITION_3F).add(TEX_2F).add(COLOR_4UB).add(NORMAL_3B).add(PADDING_1B).build());
   }
}
