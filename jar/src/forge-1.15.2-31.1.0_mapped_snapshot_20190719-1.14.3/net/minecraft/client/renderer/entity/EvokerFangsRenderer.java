package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.EvokerFangsModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EvokerFangsRenderer extends EntityRenderer<EvokerFangsEntity> {
   private static final ResourceLocation EVOKER_ILLAGER_FANGS = new ResourceLocation("textures/entity/illager/evoker_fangs.png");
   private final EvokerFangsModel<EvokerFangsEntity> model = new EvokerFangsModel();

   public EvokerFangsRenderer(EntityRendererManager p_i47208_1_) {
      super(p_i47208_1_);
   }

   public void func_225623_a_(EvokerFangsEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      float lvt_7_1_ = p_225623_1_.getAnimationProgress(p_225623_3_);
      if (lvt_7_1_ != 0.0F) {
         float lvt_8_1_ = 2.0F;
         if (lvt_7_1_ > 0.9F) {
            lvt_8_1_ = (float)((double)lvt_8_1_ * ((1.0D - (double)lvt_7_1_) / 0.10000000149011612D));
         }

         p_225623_4_.func_227860_a_();
         p_225623_4_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(90.0F - p_225623_1_.rotationYaw));
         p_225623_4_.func_227862_a_(-lvt_8_1_, -lvt_8_1_, lvt_8_1_);
         float lvt_9_1_ = 0.03125F;
         p_225623_4_.func_227861_a_(0.0D, -0.6259999871253967D, 0.0D);
         p_225623_4_.func_227862_a_(0.5F, 0.5F, 0.5F);
         this.model.func_225597_a_(p_225623_1_, lvt_7_1_, 0.0F, 0.0F, p_225623_1_.rotationYaw, p_225623_1_.rotationPitch);
         IVertexBuilder lvt_10_1_ = p_225623_5_.getBuffer(this.model.func_228282_a_(EVOKER_ILLAGER_FANGS));
         this.model.func_225598_a_(p_225623_4_, lvt_10_1_, p_225623_6_, OverlayTexture.field_229196_a_, 1.0F, 1.0F, 1.0F, 1.0F);
         p_225623_4_.func_227865_b_();
         super.func_225623_a_(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
      }
   }

   public ResourceLocation getEntityTexture(EvokerFangsEntity p_110775_1_) {
      return EVOKER_ILLAGER_FANGS;
   }
}
