package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.SlimeGelLayer;
import net.minecraft.client.renderer.entity.model.SlimeModel;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlimeRenderer extends MobRenderer<SlimeEntity, SlimeModel<SlimeEntity>> {
   private static final ResourceLocation SLIME_TEXTURES = new ResourceLocation("textures/entity/slime/slime.png");

   public SlimeRenderer(EntityRendererManager p_i47193_1_) {
      super(p_i47193_1_, new SlimeModel(16), 0.25F);
      this.addLayer(new SlimeGelLayer(this));
   }

   public void func_225623_a_(SlimeEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      this.shadowSize = 0.25F * (float)p_225623_1_.getSlimeSize();
      super.func_225623_a_((MobEntity)p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
   }

   protected void func_225620_a_(SlimeEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      float lvt_4_1_ = 0.999F;
      p_225620_2_.func_227862_a_(0.999F, 0.999F, 0.999F);
      p_225620_2_.func_227861_a_(0.0D, 0.0010000000474974513D, 0.0D);
      float lvt_5_1_ = (float)p_225620_1_.getSlimeSize();
      float lvt_6_1_ = MathHelper.lerp(p_225620_3_, p_225620_1_.prevSquishFactor, p_225620_1_.squishFactor) / (lvt_5_1_ * 0.5F + 1.0F);
      float lvt_7_1_ = 1.0F / (lvt_6_1_ + 1.0F);
      p_225620_2_.func_227862_a_(lvt_7_1_ * lvt_5_1_, 1.0F / lvt_7_1_ * lvt_5_1_, lvt_7_1_ * lvt_5_1_);
   }

   public ResourceLocation getEntityTexture(SlimeEntity p_110775_1_) {
      return SLIME_TEXTURES;
   }
}
