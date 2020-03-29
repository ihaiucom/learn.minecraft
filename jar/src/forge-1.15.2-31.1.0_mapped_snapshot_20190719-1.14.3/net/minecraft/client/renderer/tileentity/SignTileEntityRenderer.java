package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.List;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SignTileEntityRenderer extends TileEntityRenderer<SignTileEntity> {
   private final SignTileEntityRenderer.SignModel model = new SignTileEntityRenderer.SignModel();

   public SignTileEntityRenderer(TileEntityRendererDispatcher p_i226014_1_) {
      super(p_i226014_1_);
   }

   public void func_225616_a_(SignTileEntity p_225616_1_, float p_225616_2_, MatrixStack p_225616_3_, IRenderTypeBuffer p_225616_4_, int p_225616_5_, int p_225616_6_) {
      BlockState lvt_7_1_ = p_225616_1_.getBlockState();
      p_225616_3_.func_227860_a_();
      float lvt_8_1_ = 0.6666667F;
      float lvt_9_1_;
      if (lvt_7_1_.getBlock() instanceof StandingSignBlock) {
         p_225616_3_.func_227861_a_(0.5D, 0.5D, 0.5D);
         lvt_9_1_ = -((float)((Integer)lvt_7_1_.get(StandingSignBlock.ROTATION) * 360) / 16.0F);
         p_225616_3_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(lvt_9_1_));
         this.model.field_78165_b.showModel = true;
      } else {
         p_225616_3_.func_227861_a_(0.5D, 0.5D, 0.5D);
         lvt_9_1_ = -((Direction)lvt_7_1_.get(WallSignBlock.FACING)).getHorizontalAngle();
         p_225616_3_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(lvt_9_1_));
         p_225616_3_.func_227861_a_(0.0D, -0.3125D, -0.4375D);
         this.model.field_78165_b.showModel = false;
      }

      p_225616_3_.func_227860_a_();
      p_225616_3_.func_227862_a_(0.6666667F, -0.6666667F, -0.6666667F);
      Material lvt_9_3_ = func_228877_a_(lvt_7_1_.getBlock());
      SignTileEntityRenderer.SignModel var10002 = this.model;
      var10002.getClass();
      IVertexBuilder lvt_10_1_ = lvt_9_3_.func_229311_a_(p_225616_4_, var10002::func_228282_a_);
      this.model.field_78166_a.func_228308_a_(p_225616_3_, lvt_10_1_, p_225616_5_, p_225616_6_);
      this.model.field_78165_b.func_228308_a_(p_225616_3_, lvt_10_1_, p_225616_5_, p_225616_6_);
      p_225616_3_.func_227865_b_();
      FontRenderer lvt_11_1_ = this.field_228858_b_.getFontRenderer();
      float lvt_12_1_ = 0.010416667F;
      p_225616_3_.func_227861_a_(0.0D, 0.3333333432674408D, 0.046666666865348816D);
      p_225616_3_.func_227862_a_(0.010416667F, -0.010416667F, 0.010416667F);
      int lvt_13_1_ = p_225616_1_.getTextColor().func_218388_g();
      double lvt_14_1_ = 0.4D;
      int lvt_16_1_ = (int)((double)NativeImage.func_227791_b_(lvt_13_1_) * 0.4D);
      int lvt_17_1_ = (int)((double)NativeImage.func_227793_c_(lvt_13_1_) * 0.4D);
      int lvt_18_1_ = (int)((double)NativeImage.func_227795_d_(lvt_13_1_) * 0.4D);
      int lvt_19_1_ = NativeImage.func_227787_a_(0, lvt_18_1_, lvt_17_1_, lvt_16_1_);

      for(int lvt_20_1_ = 0; lvt_20_1_ < 4; ++lvt_20_1_) {
         String lvt_21_1_ = p_225616_1_.getRenderText(lvt_20_1_, (p_212491_1_) -> {
            List<ITextComponent> lvt_2_1_ = RenderComponentsUtil.splitText(p_212491_1_, 90, lvt_11_1_, false, true);
            return lvt_2_1_.isEmpty() ? "" : ((ITextComponent)lvt_2_1_.get(0)).getFormattedText();
         });
         if (lvt_21_1_ != null) {
            float lvt_22_1_ = (float)(-lvt_11_1_.getStringWidth(lvt_21_1_) / 2);
            lvt_11_1_.func_228079_a_(lvt_21_1_, lvt_22_1_, (float)(lvt_20_1_ * 10 - p_225616_1_.signText.length * 5), lvt_19_1_, false, p_225616_3_.func_227866_c_().func_227870_a_(), p_225616_4_, false, 0, p_225616_5_);
         }
      }

      p_225616_3_.func_227865_b_();
   }

   public static Material func_228877_a_(Block p_228877_0_) {
      WoodType lvt_1_2_;
      if (p_228877_0_ instanceof AbstractSignBlock) {
         lvt_1_2_ = ((AbstractSignBlock)p_228877_0_).func_226944_c_();
      } else {
         lvt_1_2_ = WoodType.field_227038_a_;
      }

      return Atlases.func_228773_a_(lvt_1_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public static final class SignModel extends Model {
      public final ModelRenderer field_78166_a = new ModelRenderer(64, 32, 0, 0);
      public final ModelRenderer field_78165_b;

      public SignModel() {
         super(RenderType::func_228640_c_);
         this.field_78166_a.func_228301_a_(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F, 0.0F);
         this.field_78165_b = new ModelRenderer(64, 32, 0, 14);
         this.field_78165_b.func_228301_a_(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F, 0.0F);
      }

      public void func_225598_a_(MatrixStack p_225598_1_, IVertexBuilder p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
         this.field_78166_a.func_228309_a_(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
         this.field_78165_b.func_228309_a_(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
      }
   }
}
