package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.tileentity.BellTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BellTileEntityRenderer extends TileEntityRenderer<BellTileEntity> {
   public static final Material field_217653_c;
   private final ModelRenderer field_228848_c_ = new ModelRenderer(32, 32, 0, 0);

   public BellTileEntityRenderer(TileEntityRendererDispatcher p_i226005_1_) {
      super(p_i226005_1_);
      this.field_228848_c_.func_228300_a_(-3.0F, -6.0F, -3.0F, 6.0F, 7.0F, 6.0F);
      this.field_228848_c_.setRotationPoint(8.0F, 12.0F, 8.0F);
      ModelRenderer lvt_2_1_ = new ModelRenderer(32, 32, 0, 13);
      lvt_2_1_.func_228300_a_(4.0F, 4.0F, 4.0F, 8.0F, 2.0F, 8.0F);
      lvt_2_1_.setRotationPoint(-8.0F, -12.0F, -8.0F);
      this.field_228848_c_.addChild(lvt_2_1_);
   }

   public void func_225616_a_(BellTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
      float lvt_7_1_ = (float)p_225616_1_.field_213943_a + p_225616_2_;
      float lvt_8_1_ = 0.0F;
      float lvt_9_1_ = 0.0F;
      if (p_225616_1_.field_213944_b) {
         float lvt_10_1_ = MathHelper.sin(lvt_7_1_ / 3.1415927F) / (4.0F + lvt_7_1_ / 3.0F);
         if (p_225616_1_.field_213945_c == Direction.NORTH) {
            lvt_8_1_ = -lvt_10_1_;
         } else if (p_225616_1_.field_213945_c == Direction.SOUTH) {
            lvt_8_1_ = lvt_10_1_;
         } else if (p_225616_1_.field_213945_c == Direction.EAST) {
            lvt_9_1_ = -lvt_10_1_;
         } else if (p_225616_1_.field_213945_c == Direction.WEST) {
            lvt_9_1_ = lvt_10_1_;
         }
      }

      this.field_228848_c_.rotateAngleX = lvt_8_1_;
      this.field_228848_c_.rotateAngleZ = lvt_9_1_;
      IVertexBuilder lvt_10_2_ = field_217653_c.func_229311_a_(p_225616_4_, RenderType::func_228634_a_);
      this.field_228848_c_.func_228308_a_(p_225616_3_, lvt_10_2_, p_225616_5_, p_225616_6_);
   }

   static {
      field_217653_c = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/bell/bell_body"));
   }
}
