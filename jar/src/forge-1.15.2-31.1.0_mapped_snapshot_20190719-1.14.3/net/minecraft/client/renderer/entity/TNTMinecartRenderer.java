package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TNTMinecartRenderer extends MinecartRenderer<TNTMinecartEntity> {
   public TNTMinecartRenderer(EntityRendererManager p_i46135_1_) {
      super(p_i46135_1_);
   }

   protected void func_225630_a_(TNTMinecartEntity p_225630_1_, float p_225630_2_, BlockState p_225630_3_, MatrixStack p_225630_4_, IRenderTypeBuffer p_225630_5_, int p_225630_6_) {
      int lvt_7_1_ = p_225630_1_.getFuseTicks();
      if (lvt_7_1_ > -1 && (float)lvt_7_1_ - p_225630_2_ + 1.0F < 10.0F) {
         float lvt_8_1_ = 1.0F - ((float)lvt_7_1_ - p_225630_2_ + 1.0F) / 10.0F;
         lvt_8_1_ = MathHelper.clamp(lvt_8_1_, 0.0F, 1.0F);
         lvt_8_1_ *= lvt_8_1_;
         lvt_8_1_ *= lvt_8_1_;
         float lvt_9_1_ = 1.0F + lvt_8_1_ * 0.3F;
         p_225630_4_.func_227862_a_(lvt_9_1_, lvt_9_1_, lvt_9_1_);
      }

      func_229127_a_(p_225630_3_, p_225630_4_, p_225630_5_, p_225630_6_, lvt_7_1_ > -1 && lvt_7_1_ / 5 % 2 == 0);
   }

   public static void func_229127_a_(BlockState p_229127_0_, MatrixStack p_229127_1_, IRenderTypeBuffer p_229127_2_, int p_229127_3_, boolean p_229127_4_) {
      int lvt_5_2_;
      if (p_229127_4_) {
         lvt_5_2_ = OverlayTexture.func_229201_a_(OverlayTexture.func_229199_a_(1.0F), 10);
      } else {
         lvt_5_2_ = OverlayTexture.field_229196_a_;
      }

      Minecraft.getInstance().getBlockRendererDispatcher().func_228791_a_(p_229127_0_, p_229127_1_, p_229127_2_, p_229127_3_, lvt_5_2_);
   }
}
