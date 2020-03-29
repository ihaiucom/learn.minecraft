package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Random;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class StuckInBodyLayer<T extends LivingEntity, M extends PlayerModel<T>> extends LayerRenderer<T, M> {
   public StuckInBodyLayer(LivingRenderer<T, M> p_i226041_1_) {
      super(p_i226041_1_);
   }

   protected abstract int func_225631_a_(T var1);

   protected abstract void func_225632_a_(MatrixStack var1, IRenderTypeBuffer var2, int var3, Entity var4, float var5, float var6, float var7, float var8);

   public void func_225628_a_(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      int lvt_11_1_ = this.func_225631_a_(p_225628_4_);
      Random lvt_12_1_ = new Random((long)p_225628_4_.getEntityId());
      if (lvt_11_1_ > 0) {
         for(int lvt_13_1_ = 0; lvt_13_1_ < lvt_11_1_; ++lvt_13_1_) {
            p_225628_1_.func_227860_a_();
            ModelRenderer lvt_14_1_ = ((PlayerModel)this.getEntityModel()).func_228288_a_(lvt_12_1_);
            ModelRenderer.ModelBox lvt_15_1_ = lvt_14_1_.func_228310_a_(lvt_12_1_);
            lvt_14_1_.func_228307_a_(p_225628_1_);
            float lvt_16_1_ = lvt_12_1_.nextFloat();
            float lvt_17_1_ = lvt_12_1_.nextFloat();
            float lvt_18_1_ = lvt_12_1_.nextFloat();
            float lvt_19_1_ = MathHelper.lerp(lvt_16_1_, lvt_15_1_.posX1, lvt_15_1_.posX2) / 16.0F;
            float lvt_20_1_ = MathHelper.lerp(lvt_17_1_, lvt_15_1_.posY1, lvt_15_1_.posY2) / 16.0F;
            float lvt_21_1_ = MathHelper.lerp(lvt_18_1_, lvt_15_1_.posZ1, lvt_15_1_.posZ2) / 16.0F;
            p_225628_1_.func_227861_a_((double)lvt_19_1_, (double)lvt_20_1_, (double)lvt_21_1_);
            lvt_16_1_ = -1.0F * (lvt_16_1_ * 2.0F - 1.0F);
            lvt_17_1_ = -1.0F * (lvt_17_1_ * 2.0F - 1.0F);
            lvt_18_1_ = -1.0F * (lvt_18_1_ * 2.0F - 1.0F);
            this.func_225632_a_(p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, lvt_16_1_, lvt_17_1_, lvt_18_1_, p_225628_7_);
            p_225628_1_.func_227865_b_();
         }

      }
   }
}
