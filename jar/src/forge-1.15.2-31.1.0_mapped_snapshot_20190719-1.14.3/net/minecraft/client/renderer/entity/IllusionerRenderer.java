package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IllusionerRenderer extends IllagerRenderer<IllusionerEntity> {
   private static final ResourceLocation ILLUSIONIST = new ResourceLocation("textures/entity/illager/illusioner.png");

   public IllusionerRenderer(EntityRendererManager p_i47477_1_) {
      super(p_i47477_1_, new IllagerModel(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new HeldItemLayer<IllusionerEntity, IllagerModel<IllusionerEntity>>(this) {
         public void func_225628_a_(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, IllusionerEntity p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
            if (p_225628_4_.isSpellcasting() || p_225628_4_.isAggressive()) {
               super.func_225628_a_(p_225628_1_, p_225628_2_, p_225628_3_, (LivingEntity)p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_, p_225628_8_, p_225628_9_, p_225628_10_);
            }

         }
      });
      ((IllagerModel)this.entityModel).func_205062_a().showModel = true;
   }

   public ResourceLocation getEntityTexture(IllusionerEntity p_110775_1_) {
      return ILLUSIONIST;
   }

   public void func_225623_a_(IllusionerEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      if (p_225623_1_.isInvisible()) {
         Vec3d[] lvt_7_1_ = p_225623_1_.getRenderLocations(p_225623_3_);
         float lvt_8_1_ = this.handleRotationFloat(p_225623_1_, p_225623_3_);

         for(int lvt_9_1_ = 0; lvt_9_1_ < lvt_7_1_.length; ++lvt_9_1_) {
            p_225623_4_.func_227860_a_();
            p_225623_4_.func_227861_a_(lvt_7_1_[lvt_9_1_].x + (double)MathHelper.cos((float)lvt_9_1_ + lvt_8_1_ * 0.5F) * 0.025D, lvt_7_1_[lvt_9_1_].y + (double)MathHelper.cos((float)lvt_9_1_ + lvt_8_1_ * 0.75F) * 0.0125D, lvt_7_1_[lvt_9_1_].z + (double)MathHelper.cos((float)lvt_9_1_ + lvt_8_1_ * 0.7F) * 0.025D);
            super.func_225623_a_(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
            p_225623_4_.func_227865_b_();
         }
      } else {
         super.func_225623_a_(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
      }

   }

   protected boolean func_225622_a_(IllusionerEntity p_225622_1_) {
      return true;
   }

   // $FF: synthetic method
   protected boolean func_225622_a_(LivingEntity p_225622_1_) {
      return this.func_225622_a_((IllusionerEntity)p_225622_1_);
   }
}
