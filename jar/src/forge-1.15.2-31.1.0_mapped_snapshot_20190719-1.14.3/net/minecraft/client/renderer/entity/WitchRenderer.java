package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.WitchHeldItemLayer;
import net.minecraft.client.renderer.entity.model.WitchModel;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitchRenderer extends MobRenderer<WitchEntity, WitchModel<WitchEntity>> {
   private static final ResourceLocation WITCH_TEXTURES = new ResourceLocation("textures/entity/witch.png");

   public WitchRenderer(EntityRendererManager p_i46131_1_) {
      super(p_i46131_1_, new WitchModel(0.0F), 0.5F);
      this.addLayer(new WitchHeldItemLayer(this));
   }

   public void func_225623_a_(WitchEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      ((WitchModel)this.entityModel).func_205074_a(!p_225623_1_.getHeldItemMainhand().isEmpty());
      super.func_225623_a_((MobEntity)p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
   }

   public ResourceLocation getEntityTexture(WitchEntity p_110775_1_) {
      return WITCH_TEXTURES;
   }

   protected void func_225620_a_(WitchEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      float lvt_4_1_ = 0.9375F;
      p_225620_2_.func_227862_a_(0.9375F, 0.9375F, 0.9375F);
   }
}
